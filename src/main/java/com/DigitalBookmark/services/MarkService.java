package com.DigitalBookmark.services;

import com.DigitalBookmark.domain.Student;
import com.DigitalBookmark.domain.Subject;
import com.DigitalBookmark.domain.SubjectMarkRecord;
import com.DigitalBookmark.domain.Teacher;
import com.DigitalBookmark.domain.dto.MarkDTO;
import com.DigitalBookmark.repositories.StudentRepository;
import com.DigitalBookmark.repositories.SubjectMarkRepository;
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
public class MarkService {

    @Autowired
    private SubjectMarkRepository markRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private TeacherRepository teacherRepository;

    public SubjectMarkRecord addMarkRecord(Long id, MarkDTO markDto) throws Exception {
        Optional<Student> studentRecord = this.studentRepository.findById(id);
        if (studentRecord.isEmpty()) throw new Exception("student not found");
        SubjectMarkRecord mark = new SubjectMarkRecord();

        if (markDto.getMarkValue() > 5 || markDto.getMarkValue() < 2) {
            throw new Exception("bad mark value");
        }
        mark.setMarkValue(markDto.getMarkValue());
        mark.setMarkOwner(studentRecord.get());

        Optional<Teacher> markGiverRecord = this.teacherRepository.findById(markDto.getMarkGiverId());
        if (markGiverRecord.isEmpty()) throw new Exception("teacher not found");
        mark.setMarkGiver(markGiverRecord.get());

        Optional<Subject> subjectRecord = this.subjectRepository.findById(markDto.getSubjectId());
        if (subjectRecord.isEmpty()) throw new Exception("subject no found");
        Subject subject = subjectRecord.get();
        mark.setMarkSubject(subject);
        Student student = studentRecord.get();
        List<SubjectMarkRecord> list = student.getMarksList();
        if (list == null) {
            list = new ArrayList<SubjectMarkRecord>();
        }
        list.add(mark);
        student.setMarksList(list);

        list = subject.getSubjectMarks();
        if (list == null) {
            list = new ArrayList<SubjectMarkRecord>();
        }
        list.add(mark);
        subject.setSubjectMarks(list);

        this.subjectRepository.save(subject);
        this.studentRepository.save(student);
        this.markRepository.save(mark);
        return mark;
    }
}
