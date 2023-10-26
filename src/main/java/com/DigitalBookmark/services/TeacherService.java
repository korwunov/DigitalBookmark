package com.DigitalBookmark.services;

import com.DigitalBookmark.repositories.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeacherService {

    @Autowired
    public TeacherRepository teacherRepository;
}
