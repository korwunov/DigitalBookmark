package com.FileService.controller;

import com.BookmarkService.domain.Student;
import com.BookmarkService.domain.Teacher;
import com.FileService.controller.dto.request.FileShareRequest;
import com.FileService.entities.FileEntity;
import com.FileService.middleware.Authentication;
import com.FileService.repositories.FileRepository;
import com.FileService.repositories.StudentRepository;
import com.FileService.repositories.TeacherRepository;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@RequestMapping("/api/files/shared")
@ResponseBody
@Log4j
public class FileShareController {
    @Autowired
    public FileRepository fileRepository;

    @Autowired
    public TeacherRepository teacherRepository;

    @Autowired
    public StudentRepository studentRepository;

    @Authentication
    @GetMapping
    public List<FileEntity> getFilesSharedToUser(@RequestHeader("Authorization") String token, Object user) {
        if (user instanceof Teacher) {
            Teacher t = teacherRepository.findById(((Teacher) user).getId()).get();
            List<Long> sharedFilesIds = t.getSharedFilesIds();
            if (sharedFilesIds != null && !sharedFilesIds.isEmpty()){
                return fileRepository.findAllById(sharedFilesIds);
            }
            return List.of();
        }
        else if (user instanceof Student) {
            Student s = studentRepository.findById(((Student) user).getId()).get();
            List<Long> sharedFilesIds = s.getSharedFilesIds();
            if (sharedFilesIds != null){
                return fileRepository.findAllById(sharedFilesIds);
            }
            return List.of();
        }
        return List.of();
    }

    @Authentication
    @PostMapping
    public void shareFileWithUser(@RequestHeader("Authorization") String token, Object user, @RequestBody FileShareRequest request) {
        if (fileRepository.findById(request.fileId).isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Файл с ID " + request.fileId + " не найден");

        if (user instanceof Teacher) {
            Teacher t = teacherRepository.findById(((Teacher) user).getId()).get();
            if (!t.getFilesID().contains(request.fileId)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Файл с ID " + request.fileId + " не принадлежит текущему пользователю");
            manageFileSharing(request.fileId, request.userId, "share");
        }
        else if (user instanceof Student) {
            Student s = studentRepository.findById(((Student) user).getId()).get();
            if (!s.getFilesID().contains(request.fileId)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Файл с ID " + request.fileId + " не принадлежит текущему пользователю");
            manageFileSharing(request.fileId, request.userId, "share");
        }
    }

    @Authentication
    @DeleteMapping
    public void unshareFilesWithUser(@RequestHeader("Authorization") String token, Object user, @RequestBody FileShareRequest request) {
        if (fileRepository.findById(request.fileId).isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Файл с ID " + request.fileId + " не найден");

        if (user instanceof Teacher) {
            Teacher t = teacherRepository.findById(((Teacher) user).getId()).get();
            if (!t.getFilesID().contains(request.fileId)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Файл с ID " + request.fileId + " не принадлежит текущему пользователю");
            manageFileSharing(request.fileId, request.userId, "unshare");
        }
        else if (user instanceof Student) {
            Student s = studentRepository.findById(((Student) user).getId()).get();
            if (!s.getFilesID().contains(request.fileId)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Файл с ID " + request.fileId + " не принадлежит текущему пользователю");
            manageFileSharing(request.fileId, request.userId, "unshare");
        }
    }

    private void manageFileSharing(Long fileId, Long userIdToManageAccess, String action) {
        FileEntity f = fileRepository.findById(fileId).get();
        Optional<Teacher> tRecord = teacherRepository.findById(userIdToManageAccess);
        Optional<Student> sRecord = studentRepository.findById(userIdToManageAccess);
        if (sRecord.isEmpty() && tRecord.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с ID " + userIdToManageAccess + " не найден");
        else if (sRecord.isEmpty()) {
            Teacher t = tRecord.get();
            List<Long> sharedFilesIds = t.getSharedFilesIds();
            List<Long> fileAllowedUsers = f.getAllowedUsers();
            if (sharedFilesIds != null && Objects.equals(action, "unshare")) {
                if (!sharedFilesIds.contains(fileId)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Доступ к файлу с ID " + fileId + " уже отсутствует у пользователя " + userIdToManageAccess);
                sharedFilesIds.remove(fileId);
                if (Objects.isNull(fileAllowedUsers)) fileAllowedUsers = new ArrayList<>();
                fileAllowedUsers.remove(userIdToManageAccess);
            }
            else if (Objects.equals(action, "share")) {
                if (sharedFilesIds == null) {
                    sharedFilesIds = new ArrayList<>();
                }
                if (sharedFilesIds.contains(fileId)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Доступ к файлу с ID " + fileId + " уже предоставлен пользователю " + userIdToManageAccess);
                sharedFilesIds.add(fileId);
                if (Objects.isNull(fileAllowedUsers)) fileAllowedUsers = new ArrayList<>();
                fileAllowedUsers.add(userIdToManageAccess);
            }
            t.setSharedFilesIds(sharedFilesIds);
            teacherRepository.save(t);
            f.setAllowedUsers(fileAllowedUsers);
            fileRepository.save(f);
        }
        else if (tRecord.isEmpty()) {
            Student s = sRecord.get();
            List<Long> sharedFilesIds = s.getSharedFilesIds();
            List<Long> fileAllowedUsers = f.getAllowedUsers();
            if (sharedFilesIds != null && Objects.equals(action, "unshare")) {
                if (!sharedFilesIds.contains(fileId)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Доступ к файлу с ID " + fileId + " уже отсутствует у пользователя " + userIdToManageAccess);
                sharedFilesIds.remove(fileId);
                if (Objects.isNull(fileAllowedUsers)) fileAllowedUsers = new ArrayList<>();
                fileAllowedUsers.remove(userIdToManageAccess);
            }
            else if (Objects.equals(action, "share")) {
                if (sharedFilesIds == null) {
                    sharedFilesIds = new ArrayList<>();
                }
                if (sharedFilesIds.contains(fileId)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Доступ к файлу с ID " + fileId + " уже предоставлен пользователю " + userIdToManageAccess);
                sharedFilesIds.add(fileId);
                if (Objects.isNull(fileAllowedUsers)) fileAllowedUsers = new ArrayList<>();
                fileAllowedUsers.add(userIdToManageAccess);
            }
            s.setSharedFilesIds(sharedFilesIds);
            studentRepository.save(s);
            f.setAllowedUsers(fileAllowedUsers);
            fileRepository.save(f);
        }
    }
}
