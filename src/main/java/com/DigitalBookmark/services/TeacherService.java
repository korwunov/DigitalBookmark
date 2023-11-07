package com.DigitalBookmark.services;

import com.DigitalBookmark.domain.Subject;
import com.DigitalBookmark.domain.Teacher;
import com.DigitalBookmark.repositories.SubjectRepository;
import com.DigitalBookmark.repositories.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeacherService {

    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private SubjectRepository subjectRepository;

    public void addTeacher(Teacher t) throws Exception {
        if (this.teacherRepository.findByEmail(t.getEmail()).isPresent()) throw new Exception("email is already registred");
        this.teacherRepository.save(t);
    }

    public List<Teacher> getAllTeachers() {
        return this.teacherRepository.findAll();
    }

    public Teacher getTeacherById(Long id) {
        return this.teacherRepository.findById(id).get();
    }

    public Teacher addSubjectsToTeacher(Long teacherId, List<Long> inputSubjectIds) throws Exception {
        Optional<Teacher> teacherRecord = this.teacherRepository.findById(teacherId);
        if (teacherRecord.isEmpty()) throw new Exception("teacher not found");
        Teacher teacher = teacherRecord.get();
        List<Subject> subs = teacher.getTeacherSubjects();
        if (subs == null) {
            subs = new ArrayList<Subject>();
        }
        for (Long id : inputSubjectIds) {
            Subject s = this.subjectRepository.findById(id).get();
            if (!(subs.contains(s))) {
                subs.add(s);
                Subject subject = this.subjectRepository.findById(s.getId()).get();
                List<Teacher> teachers = subject.getSubjectTeachers();
                teachers.add(teacher);
                subject.setSubjectTeachers(teachers);
                this.subjectRepository.save(subject);
            }
        }
        if (!(Objects.equals(teacher.getTeacherSubjects(), subs))) {
            teacher.setTeacherSubjects(subs);
            this.teacherRepository.save(teacher);
        }

        return teacher;
    }
}
