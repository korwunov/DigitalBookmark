package com.FileService.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "files")
public class FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "file_generator")
    @SequenceGenerator(name = "file_generator", sequenceName = "files_seq", allocationSize = 1)
    private Long id;

    private String fileName;

    private Long fileSize;

    private Long fileOwner;

    @JsonIgnore
    private List<Long> allowedUsers;

    @Lob
    @JsonIgnore
    private byte[] fileContent;

}
