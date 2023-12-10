package com.DigitalBookmark.services;

import com.DigitalBookmark.domain.User;
import com.DigitalBookmark.repositories.UserRepository;
import com.DigitalBookmark.web.dto.FileActionInfoDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.lang.Long.valueOf;

@Service
public class FileService {

    @Autowired
    private UserRepository userRepository;

    @RabbitListener(queues = "digitalbookmark_file_queue")
    public void handleFileAction(String msg) {
        List<String> list = List.of(msg.split(";"));
        String action = list.get(0);
        Long userId = valueOf(list.get(1));
        String fileId = list.get(2);

        if (Objects.equals(action, "add")) {
            User u = userRepository.findById(userId).get();
            List<String> ids;
            if (u.getFilesID() == null) {
                ids = new ArrayList<String>();
            }
            else {
                ids = u.getFilesID();
            }
            ids.add(fileId);
            u.setFilesID(ids);
            userRepository.save(u);
        } else if (Objects.equals(action, "delete")) {
            User u = userRepository.findById(userId).get();
            List<String> ids = u.getFilesID();
            if (ids != null) {
                ids.remove(fileId);
                u.setFilesID(ids);
                userRepository.save(u);
            }
        }
        else {
            System.out.println(action + " "
                    + userId + " "
                    + fileId + " wrong action");
        }
    }
    //Аннотация показывает, что данный метод является
    //обработчиком сообщения из очереди digitalbookmark_file_permission_queue
    @RabbitListener(queues = "digitalbookmark_file_permission_queue")
    public String handleFilePermission(String msg) {
        //Парсинг строки с информацией и файле и пользователе
        List<String> list = List.of(msg.split(";"));
        String fileId = list.get(0);
        Long userId = valueOf(list.get(1));
        //Поиск пользователя в БД
        Optional<User> uRecord = userRepository.findById(userId);
        //Если пользователь не найден вернуть false
        if (uRecord.isEmpty()) return "false";
        User u = uRecord.get();
        List<String> ids = u.getFilesID();
        //Если списко файлов пуст вернуть false
        if (ids == null) return "false";
        //Если список файлов пользователя содержит ID файла из сообщения вернуть true
        if (ids.contains(fileId)) { return "true"; }
        //Если список файлов не содержит ID файла из сообщения вернуть false
        else { return "false"; }
    }
}
