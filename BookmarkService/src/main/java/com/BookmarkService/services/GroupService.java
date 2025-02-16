package com.BookmarkService.services;

import com.BookmarkService.domain.Group;
import com.BookmarkService.domain.Student;
import com.BookmarkService.repositories.GroupRepository;
import com.BookmarkService.repositories.StudentRepository;
import com.BookmarkService.web.httpStatusesExceptions.BadRequestException;
import com.BookmarkService.web.httpStatusesExceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GroupService {
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private StudentRepository studentRepository;

    public Group createGroup(String name) {
        if (groupRepository.findByName(name).isPresent()) throw new BadRequestException("Group is already exists");
        return groupRepository.save(new Group(name));
    }

    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    public Group getGroupByName(String name) {
        Optional<Group> groupRecord = groupRepository.findByName(name);
        if (groupRecord.isEmpty()) throw new NotFoundException("Group with name " + name + " is not found");
        return groupRecord.get();
    }

    public void deleteGroup(Long id) {
        Optional<Group> groupRecord = groupRepository.findById(id);
        if (groupRecord.isEmpty()) throw new NotFoundException("Group with id " + id + " is not found");
        groupRepository.deleteById(id);
    }

    public void assignGroupToStudent(Long studentId, Long groupId) {
        Optional<Group> groupRecord = groupRepository.findById(groupId);
        Optional<Student> studentRecord = studentRepository.findById(studentId);
        if (groupRecord.isEmpty()) throw new NotFoundException("Group with id " + groupId + " is not found");
        if (studentRecord.isEmpty()) throw new NotFoundException("Student with id " + studentId + " is not found");

        Student student = studentRecord.get();
        if (student.getGroup() != null) throw new BadRequestException(String.format("Student with id %s already has an assigned group %s", studentId, student.getGroup()));
        student.setGroup(groupRecord.get());
        studentRepository.save(student);
    }

    public void unassignGroupToStudent(Long studentId) {
        Optional<Student> studentRecord = studentRepository.findById(studentId);
        if (studentRecord.isEmpty()) throw new NotFoundException("Student with id " + studentId + " is not found");
        Student student = studentRecord.get();
        if (student.getGroup() == null) throw new BadRequestException(String.format("Student with id %s doesn't have an assigned group", studentId));
        student.setGroup(null);
        studentRepository.save(student);
    }

}
