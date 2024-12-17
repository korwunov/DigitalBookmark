package com.FileService.controller;

import com.BookmarkService.domain.Student;
import com.BookmarkService.domain.Teacher;
import com.BookmarkService.domain.User;
import com.FileService.repositories.StudentRepository;
import com.FileService.repositories.TeacherRepository;
import com.FileService.entities.FileEntity;
import com.FileService.middleware.Authentication;
import com.FileService.repositories.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@ResponseBody
@RequestMapping("/files")
public class FileController {

    @Autowired
    public FileRepository fileRepository;

    @Autowired
    public TeacherRepository teacherRepository;

    @Autowired
    public StudentRepository studentRepository;

//    @Authentication
//    @PostMapping("/test")
//    public String test(@RequestHeader("Authorization") String token, Object user) {
//        System.out.println(token);
//        System.out.println(user);
//        return token;
//    }

    private static FileEntity getFileEntity(MultipartFile file, String fileNameWithoutExtension, Long userId) throws IOException {
        String originFileName = file.getOriginalFilename();
        String extensionName = originFileName.substring(originFileName.lastIndexOf((".")));
        String newFileName = fileNameWithoutExtension + extensionName;
        FileEntity fileEntity = new FileEntity();
        fileEntity.setFileName(newFileName);
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
    public ResponseEntity<Object> uploadFile(@RequestHeader("Authorization") String token, Object user, @RequestParam("file") MultipartFile file, @RequestParam("name") String fileNameWithoutExtension) {
        if (!file.isEmpty()) {
            try {
                final var fileEntity = getFileEntity(file, fileNameWithoutExtension, ((User) user).getId());
                FileEntity f = fileRepository.save(fileEntity);

                List<Long> userFiles = ((User) user).getFilesID();
                if (userFiles == null) {
                    userFiles = new ArrayList<>();
                }
                userFiles.add(f.getId());
                ((User) user).setFilesID(userFiles);
                if (user instanceof Teacher) { teacherRepository.save((Teacher) user); }
                if (user instanceof Student) { studentRepository.save((Student) user); }
                return new ResponseEntity<>(HttpStatus.OK);

            } catch (Exception e) {
                System.out.println(e.getMessage());
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Unable to save file " + e.getMessage());
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is empty");
    }

    @Authentication
    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> getFileById(@RequestHeader("Authorization") String token, Object user, @PathVariable Long id) {
        User u = (User) user;
        if (u.getFilesID() == null || !(u.getFilesID().contains(id))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "File with id = " + id + " doesn't belong to user " + u.getUsername());
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
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteFileById(@RequestHeader("Authorization") String token, Object user, @PathVariable Long id) {
        if (((User) user).getFilesID() == null || !(((User) user).getFilesID().contains(id))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "File with id = " + id + " doesn't belong to user " + ((User) user).getUsername());
        }
        fileRepository.deleteById(id);
        List<Long> userFiles = ((User) user).getFilesID();
        userFiles.remove(id);
        ((User) user).setFilesID(userFiles);
        if (user instanceof Teacher) { teacherRepository.save((Teacher) user); }
        if (user instanceof Student) { studentRepository.save((Student) user); }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
