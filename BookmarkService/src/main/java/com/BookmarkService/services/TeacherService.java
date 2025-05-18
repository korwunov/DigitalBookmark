package com.BookmarkService.services;

import com.BookmarkService.domain.EROLE;
import com.BookmarkService.domain.Teacher;
import com.BookmarkService.repositories.SubjectRepository;
import com.BookmarkService.repositories.TeacherRepository;
import com.BookmarkService.web.dto.response.TeacherResponseDTO;
import com.BookmarkService.web.httpStatusesExceptions.BadRequestException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service        //Обозначение класса сервиса для Spring
@RequiredArgsConstructor        //Автоинициализация конструктора без параметров
public class TeacherService {

    @Autowired  //Автопоиск и подставление Bean класса TeacherRepository
    //Репозиторий БД с данными об учителях
    private TeacherRepository teacherRepository;
    @Autowired
    //Репозиторий БД с данными о предметах
    private SubjectRepository subjectRepository;

    //Метод добавления нового учителя
    public void addTeacher(Teacher t) throws Exception {
        //Если учитель с таким email уже существует, то необходимо вернуть ошибку
        if (this.teacherRepository.findByUsername(t.getUsername()).isPresent()) throw new BadRequestException("email is already registred");
        //Добавление роли учитель в набор ролей класса
        t.setRole(EROLE.ROLE_TEACHER);
        this.teacherRepository.save(t);    //Сохранение учителя в репозитории
    }

    //Получение всех учителей
    public List<TeacherResponseDTO> getAllTeachers() {
        return this.teacherRepository.findAll().stream().map(t -> new TeacherResponseDTO(t.getId(), t.getName())).toList();
    }

    //Получение учителя по ID
    public Teacher getTeacherById(Long id) {
        return this.teacherRepository.findById(id).get();
    }

    @Transactional  //Аннотация обозначает, что данный метод должен использовать транзакции при работе с репозиториями
    //Удаление учителя по ID
    public Teacher deleteTeacherById(Long id) throws Exception {
        //Проверка на наличие ID в запросе
        if (id == null) throw new Exception("no id in request");
        Optional<Teacher> t = this.teacherRepository.findById(id);
        //Проверка на наличие учителя с нужным ID в БД
        if (t.isEmpty()) throw new Exception("teacher with id " + id + " not found");
        //Удаление учителя, если он был найден
        this.teacherRepository.deleteById(id);
        //Возвращаем объект учителя
        return t.get();
    }

}
