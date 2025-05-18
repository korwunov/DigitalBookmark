package com.BookmarkService.services;

import com.BookmarkService.domain.*;
import com.BookmarkService.web.dto.request.MarkDTO;
import com.BookmarkService.repositories.StudentRepository;
import com.BookmarkService.repositories.SubjectMarkRepository;
import com.BookmarkService.repositories.SubjectRepository;
import com.BookmarkService.repositories.TeacherRepository;
import com.BookmarkService.web.dto.response.MarkResponseDTO;
import com.BookmarkService.web.httpStatusesExceptions.BadRequestException;
import com.BookmarkService.web.httpStatusesExceptions.ForbiddenException;
import com.BookmarkService.web.httpStatusesExceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    public SubjectMarkRecord addMarkRecord(Teacher markGiver, MarkDTO markDto) {
        if (markDto.getMarkValue() > 5 || markDto.getMarkValue() < 2) {
            throw new BadRequestException("bad mark value");
        }

        Optional<Student> studentRecord = this.studentRepository.findById(markDto.getStudentId());
        if (studentRecord.isEmpty()) throw new NotFoundException("student not found");

        Optional<Teacher> markGiverRecord = this.teacherRepository.findById(markGiver.getId());
        if (markGiverRecord.isEmpty()) throw new NotFoundException("teacher not found");

        Optional<Subject> subjectRecord = this.subjectRepository.findById(markDto.getSubjectId());
        if (subjectRecord.isEmpty()) throw new NotFoundException("subject not found");

        Subject subject = subjectRecord.get();
        Teacher teacher = markGiverRecord.get();
        Student student = studentRecord.get();

        if (!(teacher.getTeacherSubjects().contains(subject))) {
            throw new ForbiddenException("teacher with id " + teacher.getId() + " not allowed to set marks for subject " + subject.getName());
        }

        if (!(student.getStudentSubjects().contains(subject))) {
            throw new ForbiddenException("student with id " + student.getId() + " not allowed to receive marks for subject "  + subject.getName());
        }

        if (student.getGroup() == null) {
            throw new ForbiddenException(String.format("student with id %s is not assigned to any group", student.getId()));
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

    public List<MarkResponseDTO> getStudentMarks(Student student) {
        Optional<List<SubjectMarkRecord>> marksRecords = markRepository.findByMarkOwner(student);
        if (marksRecords.isEmpty()) {
            throw new NotFoundException("Оценки не найдены");
        }
        List<SubjectMarkRecord> marks = marksRecords.get();
        List<MarkResponseDTO> marksResponse = new ArrayList<>();
        for (SubjectMarkRecord mark : marks) {
            marksResponse.add(new MarkResponseDTO(mark.getId(), mark.getMarkSubject().getName(),
                    mark.getMarkGiver().getName(), mark.getMarkOwner().getName(), mark.getMarkOwner().getGroup().getName(),
                    mark.getMarkSetDate(), mark.getMarkValue()));
        }
        return marksResponse;
    }

    public List<MarkResponseDTO> getMarksGivenByTeacher(Teacher teacher) {
        Optional<List<SubjectMarkRecord>> marksRecords;
        if (teacher.getRole() == EROLE.ROLE_ADMIN) {
            marksRecords = Optional.of(markRepository.findAll());
        }
        else {
            marksRecords = markRepository.findByMarkGiver(teacher);
        }
        if (marksRecords.isEmpty()) {
            throw new NotFoundException("Оценки не найдены");
        }
        List<SubjectMarkRecord> marks = marksRecords.get();
        List<MarkResponseDTO> marksResponse = new ArrayList<>();
        for (SubjectMarkRecord mark : marks) {
            marksResponse.add(new MarkResponseDTO(mark.getId(), mark.getMarkSubject().getName(),
                    mark.getMarkGiver().getName(), mark.getMarkOwner().getName(), mark.getMarkOwner().getGroup().getName(),
                    mark.getMarkSetDate(), mark.getMarkValue()));
        }
        return marksResponse;
    }

    public List<SubjectMarkRecord> getMarksBySubjectAndDates(Long subjectId, String dateFrom, String dateTo) {
        Optional<Subject> subRecord = this.subjectRepository.findById(subjectId);
        if (subRecord.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "subject with this id not found");
        Subject sub = subRecord.get();
        Optional<List<SubjectMarkRecord>> marksRecords = this.markRepository.findByMarkSubject(sub);
        if (marksRecords.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no data for this subject");
        List<SubjectMarkRecord> marks = marksRecords.get();
        LocalDate dateFromObj = LocalDate.parse(dateFrom);
        LocalDate dateToObj = LocalDate.parse(dateTo);
        List<SubjectMarkRecord> finalList = new ArrayList<SubjectMarkRecord>();
        for (SubjectMarkRecord m : marks) {
            if (m.getMarkSetDate().isAfter(dateFromObj) && m.getMarkSetDate().isBefore(dateToObj)) {
                finalList.add(m);
            }
        }
        if (finalList.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no data for this dates");
        return finalList;
    }
}
