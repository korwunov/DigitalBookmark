package com.FileService.controllers;

import com.BookmarkService.domain.EROLE;
import com.BookmarkService.domain.Teacher;
import com.BookmarkService.domain.User;
import com.FileService.entities.FileEntity;
import com.FileService.repositories.FileRepository;
import com.FileService.repositories.TeacherRepository;
import com.FileService.controller.FileController;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.ByteBuffer;

@SpringBootTest
public class FileControllerTests {
    @Autowired
    public FileController fileController;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private FileRepository fileRepository;

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    public MultipartFile getTestFile() throws IOException {
        final String FILENAME = "test.txt";
        File tmpFile = this.testFolder.newFile(FILENAME);
        InputStream io = new FileInputStream(tmpFile);
        MultipartFile file = new MockMultipartFile(FILENAME, io.readAllBytes());
        return file;
    }

    @Test
    public void uploadFileTest() throws IOException {
        final String FILENAME_WITHOUT_EXTENSION = "test";
        MultipartFile file = this.getTestFile();
        User user = new Teacher();
        user.setName("test");
        user.setRole(EROLE.ROLE_TEACHER);
        this.fileController.uploadFile("token", user, file, FILENAME_WITHOUT_EXTENSION);
        Teacher t = teacherRepository.findByUsername("test").get();
        assert t.getFilesID() != null;
        FileEntity f = fileRepository.findById(t.getFilesID().get(0)).get();
    }
}
