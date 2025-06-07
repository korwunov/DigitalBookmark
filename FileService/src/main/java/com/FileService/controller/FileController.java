package com.FileService.controller;

import com.BookmarkService.domain.Student;
import com.BookmarkService.domain.Teacher;
import com.BookmarkService.domain.User;
import com.FileService.repositories.StudentRepository;
import com.FileService.repositories.TeacherRepository;
import com.FileService.entities.FileEntity;
import com.FileService.middleware.Authentication;
import com.FileService.repositories.FileRepository;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@ResponseBody
@RequestMapping("/api/files")
@Log4j
public class FileController {

    @Autowired
    public FileRepository fileRepository;

    @Autowired
    public TeacherRepository teacherRepository;

    @Autowired
    public StudentRepository studentRepository;

    private static FileEntity getFileEntity(MultipartFile file, Long userId) throws IOException {
        FileEntity fileEntity = new FileEntity();
        fileEntity.setFileName(file.getOriginalFilename());
        fileEntity.setFileContent(file.getBytes());
        fileEntity.setFileSize(file.getSize());
        fileEntity.setFileOwner(userId);
        return fileEntity;
    }

    /**
     * Используется Object user, так как User user - абстрактный класс,
     * а спринг пытается инициализировать все параметры метода перед выполнением (абстрактные классы нельзя инициализировать)
    **/
    @Authentication
    @PostMapping("/upload")
    public ResponseEntity<Object> uploadFile(@RequestHeader("Authorization") String token, Object user, @RequestParam("file") MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                final var fileEntity = getFileEntity(file, ((User) user).getId());
                FileEntity f = fileRepository.save(fileEntity);

                List<Long> userFiles = ((User) user).getFilesID();
                if (userFiles == null) {
                    userFiles = new ArrayList<>();
                }
                userFiles.add(f.getId());

                if (user instanceof Teacher) {
                    Teacher t = teacherRepository.findById(((Teacher) user).getId()).get();
                    t.setFilesID(userFiles);
                    teacherRepository.save(t);
                }
                if (user instanceof Student) {
                    Student s = studentRepository.findById(((Student) user).getId()).get();
                    s.setFilesID(userFiles);
                    studentRepository.save((Student) user);
                }
                log.info("File created, id: " + f.getId() + ", owner id: " + f.getFileOwner() + ", file name: " + 302L);
                return new ResponseEntity<>(HttpStatus.OK);

            } catch (Exception e) {
                log.error(e.getMessage());
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Unable to save file " + e.getMessage());
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is empty");
    }

    @Authentication
    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> getFileById(@RequestHeader("Authorization") String token, Object user, @PathVariable Long id) {
        User u = (User) user;
        List<Long> usersFiles = u.getFilesID() == null ? List.of() : u.getFilesID();
        List<Long> allowedFilesForUser = u.getSharedFilesIds() == null ? List.of() : u.getSharedFilesIds();
        List<Long> accessableFileIds = Stream.concat(usersFiles.stream(), allowedFilesForUser.stream()).toList();
        if (!accessableFileIds.contains(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Файл с id = " + id + " не принадлежит или не предоставлен пользователю " + u.getUsername());
        }
        Optional<FileEntity> fileRecord = fileRepository.findById(id);
        if (fileRecord.isPresent()) {
            FileEntity file = fileRecord.get();
            HttpHeaders headers = new HttpHeaders();
            headers.set(
                    HttpHeaders.CONTENT_DISPOSITION, "attachement;filename=\"" + file.getFileName() + "\""
            );
            return new ResponseEntity<>(file.getFileContent(), headers, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Authentication
    @GetMapping
    public List<FileEntity> getAllUserFiles(@RequestHeader("Authorization") String token, Object user) {
        User u = (User) user;
        if (Objects.nonNull(u.getFilesID())) {
            return fileRepository.findAllById(u.getFilesID());
        }
        return List.of();
    }

    @Authentication
    @DeleteMapping("/delete/{id}")
    @Transactional
    public ResponseEntity<Object> deleteFileById(@RequestHeader("Authorization") String token, Object user, @PathVariable Long id) {
        if (((User) user).getFilesID() == null || !(((User) user).getFilesID().contains(id))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "File with id = " + id + " doesn't belong to user " + ((User) user).getUsername());
        }
        FileEntity file = fileRepository.findById(id).get();
        if (user instanceof Teacher) {
            Teacher t = teacherRepository.findById(((Teacher) user).getId()).get();
            List<Long> usersFilesIds = t.getFilesID();
            usersFilesIds.remove(id);
            t.setFilesID(usersFilesIds);
            teacherRepository.save(t);
        }
        else if (user instanceof Student) {
            Student s = studentRepository.findById(((Student) user).getId()).get();
            List<Long> usersFilesIds = s.getFilesID();
            usersFilesIds.remove(id);
            s.setFilesID(usersFilesIds);
            studentRepository.save(s);
        }
        if (Objects.nonNull(file.getAllowedUsers()) && !file.getAllowedUsers().isEmpty()) {
            for (Long userId : file.getAllowedUsers()) {
                Optional<Teacher> tRecord = teacherRepository.findById(userId);
                Optional<Student> sRecord = studentRepository.findById(userId);
                if (tRecord.isPresent() && sRecord.isEmpty()) {
                    Teacher t = tRecord.get();
                    List<Long> allowedFilesList = t.getSharedFilesIds();
                    allowedFilesList.remove(id);
                    t.setSharedFilesIds(allowedFilesList);
                    teacherRepository.save(t);
                } else if (sRecord.isPresent() && tRecord.isEmpty()) {
                    Student s = sRecord.get();
                    List<Long> allowedFilesList = s.getSharedFilesIds();
                    allowedFilesList.remove(id);
                    s.setSharedFilesIds(allowedFilesList);
                    studentRepository.save(s);
                }
            }
        }
        fileRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
