package com.DigitalBookmark.services;

import com.DigitalBookmark.domain.Student;
import com.DigitalBookmark.repositories.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentService {
    @Autowired
    private StudentRepository studentRepository;


    public void addStudent(Student u) throws Exception {
        if (studentRepository.findByEmail(u.getEmail()).isPresent()) throw new Exception("email already registered");
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
