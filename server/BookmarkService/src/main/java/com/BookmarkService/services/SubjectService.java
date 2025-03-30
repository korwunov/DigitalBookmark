package com.BookmarkService.services;

import com.BookmarkService.domain.Student;
import com.BookmarkService.domain.Subject;
import com.BookmarkService.domain.Teacher;
import com.BookmarkService.repositories.StudentRepository;
import com.BookmarkService.web.dto.SubjectDTO;
import com.BookmarkService.repositories.SubjectRepository;
import com.BookmarkService.repositories.TeacherRepository;
import com.BookmarkService.web.dto.SubjectsToAddDTO;
import com.BookmarkService.web.httpStatusesExceptions.BadRequestException;
import com.BookmarkService.web.httpStatusesExceptions.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private StudentRepository studentRepository;

    public Subject addSubject(SubjectDTO s) throws Exception {
        Subject sub = new Subject();
        if (this.subjectRepository.findByName(s.getName()).isPresent()) throw new BadRequestException("subject already exist");

        sub.setName(s.getName());
//        List<Teacher> list = new ArrayList<Teacher>();
//        for (Long id : s.getTeachersIds()) {
//            Teacher teacher = this.teacherRepository.findById(id).get();
//            list.add(teacher);
//        }
//        sub.setSubjectTeachers(list);
        this.subjectRepository.save(sub);
        return sub;
    }

    public List<Subject> getAllSubjects() {
        return this.subjectRepository.findAll();
    }

    public Subject getSubjectById(Long id) throws Exception {
        Optional<Subject> sRecord = this.subjectRepository.findById(id);
        if (sRecord.isEmpty()) throw new Exception("subject with id " + id + " not found");
        return sRecord.get();
    }

    public Subject deleteSubject(Long id) throws Exception {
        if (id == null) throw new Exception("no id in request");
        Optional<Subject> sRecord = this.subjectRepository.findById(id);
        if (sRecord.isEmpty()) throw new Exception("subject with id " + id + " not found");
        this.subjectRepository.deleteById(id);
        return sRecord.get();
    }

    //Метод для добавления учителю нового предмета
    @Transactional
    public Teacher addSubjectsToTeacher(SubjectsToAddDTO subjects) {
        //Проверка на наличие учителя с нужным ID в БД
        Optional<Teacher> teacherRecord = this.teacherRepository.findById(subjects.getUserId());
        if (teacherRecord.isEmpty()) throw new NotFoundException("teacher not found");
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

    @Transactional
    public Student addSubjectToStudent(SubjectsToAddDTO subjects) {
        Optional<Student> studentRecord = this.studentRepository.findById(subjects.getUserId());
        if (studentRecord.isEmpty()) throw new NotFoundException("student not found");
        Student student = studentRecord.get();

        List<Subject> subs = student.getStudentSubjects();
        if (subs == null) {
            subs = new ArrayList<Subject>();
        }

        for (Long id : subjects.getSubjectIds()) {
            Optional<Subject> subjectRecord = this.subjectRepository.findById(id);
            if (subjectRecord.isEmpty()) throw new NotFoundException("subject with id " + id + " not found");
            Subject subject = subjectRecord.get();
            if (!(subs.contains(subject))) {
                subs.add(subject);
                List<Student> subjectStudents = subject.getSubjectStudents();
                subjectStudents.add(student);
                subject.setSubjectStudents(subjectStudents);
                this.subjectRepository.save(subject);
            }
        }
        if (!(Objects.equals(student.getStudentSubjects(), subs))) {
            student.setStudentSubjects(subs);
            this.studentRepository.save(student);
        }
        //Возврат объекта студента
        return student;
    }

    public Teacher unassignSubjectToTeacher(SubjectsToAddDTO subjects) {
        Optional<Teacher> teacherRecord = this.teacherRepository.findById(subjects.getUserId());
        if (teacherRecord.isEmpty()) throw new NotFoundException("teacher not found");
        Teacher teacher = teacherRecord.get();
        List<Subject> teacherSubjects = teacher.getTeacherSubjects();
        if (teacherSubjects == null) throw new BadRequestException("There are no subjects assigned to teacher");

        for (Long subjectToDeleteId : subjects.getSubjectIds()) {
            Optional<Subject> subjectRecord = this.subjectRepository.findById(subjectToDeleteId);
            if (subjectRecord.isEmpty()) throw new NotFoundException("subject with id " + subjectToDeleteId + " not found");
            Subject subject = subjectRecord.get();
            teacherSubjects.remove(subject);
            subject.getSubjectTeachers().remove(teacher);
        }

        return teacher;
    }

    public Student unassignSubjectToStudent(SubjectsToAddDTO subjects) {
        Optional<Student> studentRecord = this.studentRepository.findById(subjects.getUserId());
        if (studentRecord.isEmpty()) throw new NotFoundException("Student not found");
        Student student = studentRecord.get();
        List<Subject> studentSubjects = student.getStudentSubjects();
        if (studentSubjects == null) throw new BadRequestException("There are no subjects assigned to student");

        for (Long subjectToDeleteId : subjects.getSubjectIds()) {
            Optional<Subject> subjectRecord = this.subjectRepository.findById(subjectToDeleteId);
            if (subjectRecord.isEmpty())
                throw new NotFoundException("Subject with id " + subjectToDeleteId + " not found");
            studentSubjects.remove(subjectRecord.get());
        }

        if (!(Objects.equals(student.getStudentSubjects(), studentSubjects))) {
            student.setStudentSubjects(studentSubjects);
            this.studentRepository.save(student);
        }
        return student;
    }
}
