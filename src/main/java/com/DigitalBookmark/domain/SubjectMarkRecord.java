package com.DigitalBookmark.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Entity     //Обозначение сущности для Spring
@Data       //Автоинициализация гетеров и сетеров для полей класса
@AllArgsConstructor     //Автоинициализация конструктора со всеми параметрами
@NoArgsConstructor      //Автоинициализация конструктора без параметров
@Table(name = "marks")      //Имя таблицы в БД
public class SubjectMarkRecord {
    @Id     //Обозначение для поля с ID
    @GeneratedValue(strategy = GenerationType.AUTO)     //Стратегия генерации ID
    private Long id;    //ID оценки

    //Обозначение отдельной колонки в таблице,
    // параметр nullable устанавливает возможность отсутствия значения
    @Column(nullable = false)
    private int markValue;      //Значение оценки

    @Column
    private LocalDate markSetDate;  //Дата выставления оценки

    @JsonIgnore     //Игнорирование поля при конвертации в JSON
    @ManyToOne(targetEntity = Subject.class)    //Обозначение типа связи
    @OnDelete(action = OnDeleteAction.CASCADE)  //Ссылочная целостность
    private Subject markSubject;    //Предмет

    @JsonIgnore
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Student markOwner;      //Студент

    @ManyToOne(targetEntity = Teacher.class)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Teacher markGiver;      //Преподаватель
}
