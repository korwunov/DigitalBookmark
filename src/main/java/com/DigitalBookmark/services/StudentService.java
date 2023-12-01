package com.DigitalBookmark.services;

import com.DigitalBookmark.domain.EROLE;
import com.DigitalBookmark.domain.Student;
import com.DigitalBookmark.repositories.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class StudentService {
    @Autowired
    private StudentRepository studentRepository;


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
}
