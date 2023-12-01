package com.DigitalBookmark.services;

import com.DigitalBookmark.domain.EROLE;
import com.DigitalBookmark.domain.Subject;
import com.DigitalBookmark.domain.Teacher;
import com.DigitalBookmark.repositories.SubjectRepository;
import com.DigitalBookmark.repositories.TeacherRepository;
import com.DigitalBookmark.web.dto.SubjectsToAddDTO;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.rmi.server.ExportException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TeacherService {

    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private SubjectRepository subjectRepository;

    public void addTeacher(Teacher t) throws Exception {
        if (this.teacherRepository.findByUsername(t.getUsername()).isPresent()) throw new Exception("email is already registred");
        Set<EROLE> eroleSet = new HashSet<EROLE>();
        eroleSet.add(EROLE.ROLE_TEACHER);
        t.setRoles(eroleSet);
        this.teacherRepository.save(t);
    }

    public List<Teacher> getAllTeachers() {
        return this.teacherRepository.findAll();
    }

    public Teacher getTeacherById(Long id) {
        return this.teacherRepository.findById(id).get();
    }

    @Transactional
    public Teacher deleteTeacherById(Long id) throws Exception {
        if (id == null) throw new Exception("no id in request");
        Optional<Teacher> t = this.teacherRepository.findById(id);
        if (t.isEmpty()) throw new Exception("teacher with id " + id + " not found");
        this.teacherRepository.deleteById(id);
        return t.get();
    }

    public Teacher addSubjectsToTeacher(SubjectsToAddDTO subjects) throws Exception {
        Optional<Teacher> teacherRecord = this.teacherRepository.findById(subjects.getTeacherId());
        if (teacherRecord.isEmpty()) throw new Exception("teacher not found");
        Teacher teacher = teacherRecord.get();
        List<Subject> subs = teacher.getTeacherSubjects();
        if (subs == null) {
            subs = new ArrayList<Subject>();
        }
        for (Long id : subjects.getIds()) {
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
