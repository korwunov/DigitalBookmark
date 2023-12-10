package com.DigitalBookmark.services;

import com.DigitalBookmark.domain.EROLE;
import com.DigitalBookmark.domain.Subject;
import com.DigitalBookmark.domain.Teacher;
import com.DigitalBookmark.repositories.SubjectRepository;
import com.DigitalBookmark.repositories.TeacherRepository;
import com.DigitalBookmark.web.dto.SubjectsToAddDTO;
import com.DigitalBookmark.web.httpStatusesExceptions.BadRequestException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.rmi.server.ExportException;
import java.util.*;

@Service        //Обозначение класса сервиса для Spring
@RequiredArgsConstructor        //Автоинициализация конструктора без параметров
public class TeacherService {

    @Autowired  //Автопоиск и подставление Bean класса TeacherRepository
    //Репозиторий БД с данными об учтелях
    private TeacherRepository teacherRepository;
    @Autowired
    //Репозиторий БД с данными о предметах
    private SubjectRepository subjectRepository;

    //Метод добавления нового учителя
    public void addTeacher(Teacher t) throws Exception {
        //Если учитель с таким email уже существует, то необходимо вернуть ошибку
        if (this.teacherRepository.findByUsername(t.getUsername()).isPresent()) throw new BadRequestException("email is already registred");
        //Добавление роли учитель в набор ролей класса
        Set<EROLE> eroleSet = new HashSet<EROLE>();
        eroleSet.add(EROLE.ROLE_TEACHER);
        t.setRoles(eroleSet);
        this.teacherRepository.save(t);    //Сохранение учителя в репозитории
    }

    //Получение всех учителей
    public List<Teacher> getAllTeachers() {
        return this.teacherRepository.findAll();
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

    //Метод для добавления учителю нового предмета
    @Transactional
    public Teacher addSubjectsToTeacher(SubjectsToAddDTO subjects) throws Exception {
        //Проверка на наличие учителя с нужным ID в БД
        Optional<Teacher> teacherRecord = this.teacherRepository.findById(subjects.getUserId());
        if (teacherRecord.isEmpty()) throw new Exception("teacher not found");
        Teacher teacher = teacherRecord.get();
        //Получение всех предметов учителя
        List<Subject> subs = teacher.getTeacherSubjects();
        if (subs == null) {     //Если список предметов не определен, то создаем новый
            subs = new ArrayList<Subject>();
        }

        for (Long id : subjects.getSubjectIds()) {     //Перебор ID из запроса
            //Поиск предмета по ID
            Subject subject = this.subjectRepository.findById(id).get();
            //Если список предметов преподавателя не содержит предмета из массива
            if (!(subs.contains(subject))) {
                subs.add(subject);    //Добавляем предмет в массив предметов преподавателя
                //Получаем список учителей для предмета
                List<Teacher> teachers = subject.getSubjectTeachers();
                //Добавляем учителя к предмету
                teachers.add(teacher);
                subject.setSubjectTeachers(teachers);
                this.subjectRepository.save(subject);
            }
        }
        //Если списки предметов до и после обработки массива ID не совпадают,
        //то записываем новый список к учителю в массив
        if (!(Objects.equals(teacher.getTeacherSubjects(), subs))) {
            teacher.setTeacherSubjects(subs);
            this.teacherRepository.save(teacher);
        }
        //Возврат объекта учителя
        return teacher;
    }
}
