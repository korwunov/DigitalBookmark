package com.DigitalBookmark.services;

import com.DigitalBookmark.domain.Subject;
import com.DigitalBookmark.domain.Teacher;
import com.DigitalBookmark.web.dto.SubjectDTO;
import com.DigitalBookmark.repositories.SubjectRepository;
import com.DigitalBookmark.repositories.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private TeacherRepository teacherRepository;

    public Subject addSubject(SubjectDTO s) throws Exception {
        Subject sub = new Subject();
        if (this.subjectRepository.findByName(s.getName()).isPresent()) throw new Exception("subject already exist");

        sub.setName(s.getName());
        List<Teacher> list = new ArrayList<Teacher>();
        for (Long id : s.getTeachersIds()) {
            Teacher teacher = this.teacherRepository.findById(id).get();
            list.add(teacher);
        }
        sub.setSubjectTeachers(list);
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
}
