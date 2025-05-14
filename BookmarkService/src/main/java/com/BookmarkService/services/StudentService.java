package com.BookmarkService.services;

import com.BookmarkService.domain.EROLE;
import com.BookmarkService.domain.Group;
import com.BookmarkService.domain.Student;
import com.BookmarkService.domain.Subject;
import com.BookmarkService.repositories.GroupRepository;
import com.BookmarkService.repositories.StudentRepository;
import com.BookmarkService.repositories.SubjectRepository;
import com.BookmarkService.web.dto.SubjectsToAddDTO;
import com.BookmarkService.web.dto.request.StudentBySubjectAndGroup;
import com.BookmarkService.web.dto.response.StudentResponseDTO;
import com.BookmarkService.web.httpStatusesExceptions.NotFoundException;
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
    private GroupRepository groupRepository;

    @Autowired
    private SubjectRepository subjectRepository;


    public void addStudent(Student u) throws Exception {
        if (studentRepository.findByUsername(u.getUsername()).isPresent()) throw new Exception("email already registered");
        u.setRole(EROLE.ROLE_STUDENT);
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

    public List<StudentResponseDTO> getStudentsBySubjectAndGroupIds(Long groupId, Long subjectId) {
        Optional<Group> groupRecord = this.groupRepository.findById(groupId);
        if (groupRecord.isEmpty()) throw new NotFoundException("Группа с ID " + groupId + " не найдена");
        Optional<Subject> subjectRecord = this.subjectRepository.findById(subjectId);
        if (subjectRecord.isEmpty()) throw new NotFoundException("Предмет с ID " + subjectId + " не найден");
        Group group = groupRecord.get();
        Subject subject = subjectRecord.get();
        Optional<List<Student>> studentRecords = this.studentRepository.findByGroupAndStudentSubjects(group, subject);
        if (studentRecords.isEmpty()) throw new NotFoundException("Студенты из группы " + group.getName() + " с предметом " + subject.getName() + " не найдены");
        List<StudentResponseDTO> response = new ArrayList<>();
        for (Student student : studentRecords.get()) {
            response.add(new StudentResponseDTO(student.getId(), student.getName(), student.getGroup()));
        }
        return response;
    }
}
