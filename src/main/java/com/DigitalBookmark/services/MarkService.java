package com.DigitalBookmark.services;

import com.DigitalBookmark.domain.Student;
import com.DigitalBookmark.domain.Subject;
import com.DigitalBookmark.domain.SubjectMarkRecord;
import com.DigitalBookmark.domain.Teacher;
import com.DigitalBookmark.web.dto.MarkDTO;
import com.DigitalBookmark.repositories.StudentRepository;
import com.DigitalBookmark.repositories.SubjectMarkRepository;
import com.DigitalBookmark.repositories.SubjectRepository;
import com.DigitalBookmark.repositories.TeacherRepository;
import com.DigitalBookmark.web.httpStatusesExceptions.ForbiddenException;
import com.DigitalBookmark.web.httpStatusesExceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    public SubjectMarkRecord addMarkRecord(MarkDTO markDto) throws Exception {
        if (markDto.getMarkValue() > 5 || markDto.getMarkValue() < 2) {
            throw new Exception("bad mark value");
        }

        Optional<Student> studentRecord = this.studentRepository.findById(markDto.getStudentId());
        if (studentRecord.isEmpty()) throw new Exception("student not found");

        Optional<Teacher> markGiverRecord = this.teacherRepository.findById(markDto.getMarkGiverId());
        if (markGiverRecord.isEmpty()) throw new Exception("teacher not found");

        Optional<Subject> subjectRecord = this.subjectRepository.findById(markDto.getSubjectId());
        if (subjectRecord.isEmpty()) throw new Exception("subject not found");

        Subject subject = subjectRecord.get();
        Teacher teacher = markGiverRecord.get();
        Student student = studentRecord.get();

        if (!(teacher.getTeacherSubjects().contains(subject))) {
            throw new ForbiddenException("teacher with id " + teacher.getId() + " not allowed to set marks for subject " + subject.getName());
        }

        if (!(student.getStudentSubjects().contains(subject))) {
            throw new ForbiddenException("student with id " + student.getId() + " not allowed to receive marks for subject "  + subject.getName());
        }

        SubjectMarkRecord mark = new SubjectMarkRecord();
        mark.setMarkValue(markDto.getMarkValue());
        mark.setMarkOwner(studentRecord.get());
        mark.setMarkSubject(subject);
        mark.setMarkGiver(teacher);

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

        mark.setMarkSetDate(LocalDate.now());

        this.subjectRepository.save(subject);
        this.studentRepository.save(student);
        this.markRepository.save(mark);
        return mark;
    }

    public List<SubjectMarkRecord> getMarksBySubjectAndDates(Long subjectId, String dateFrom, String dateTo) {
        Optional<Subject> subRecord = this.subjectRepository.findById(subjectId);
        if (subRecord.isEmpty()) throw new NotFoundException("subject with this id not found");
        Subject sub = subRecord.get();
        Optional<List<SubjectMarkRecord>> marksRecords = this.markRepository.findByMarkSubject(sub);
        if (marksRecords.isEmpty()) throw new NotFoundException("no data for this subject");
        List<SubjectMarkRecord> marks = marksRecords.get();
        LocalDate dateFromObj = LocalDate.parse(dateFrom);
        LocalDate dateToObj = LocalDate.parse(dateTo);
        List<SubjectMarkRecord> finalList = new ArrayList<SubjectMarkRecord>();
        for (SubjectMarkRecord m : marks) {
            if (m.getMarkSetDate().isAfter(dateFromObj) && m.getMarkSetDate().isBefore(dateToObj)) {
                finalList.add(m);
            }
        }
        if (finalList.size() == 0) throw new NotFoundException("no data for this dates");
        return finalList;
    }
}
