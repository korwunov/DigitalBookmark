package com.DigitalBookmark.services;

import com.DigitalBookmark.domain.EROLE;
import com.DigitalBookmark.domain.Student;
import com.DigitalBookmark.domain.Subject;
import com.DigitalBookmark.repositories.StudentRepository;
import com.DigitalBookmark.repositories.SubjectRepository;
import com.DigitalBookmark.web.dto.SubjectsToAddDTO;
import com.DigitalBookmark.web.httpStatusesExceptions.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class StudentService {
    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private SubjectRepository subjectRepository;


    public void addStudent(Student u) throws Exception {
        if (studentRepository.findByUsername(u.getUsername()).isPresent()) throw new Exception("email already registered");
        Set<EROLE> eroleSet = new HashSet<EROLE>();
        eroleSet.add(EROLE.ROLE_STUDENT);
        u.setRoles(eroleSet);
        studentRepository.save(u);
    }


    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }


    public Optional<Student> getStudentByID(Long ID) throws Exception{
        Optional<Student> result = studentRepository.findById(ID);
        if (result.isEmpty()) throw new Exception("user not found");
        return result;
    }


    public void deleteStudent(Long ID) throws Exception {
        try {
            studentRepository.deleteById(ID);
        }
        catch (Exception e) {
            throw new Exception("user not found");
        }
    }

    @Transactional
    public Student addSubjectToStudent(SubjectsToAddDTO subjects) throws Exception {
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
}
