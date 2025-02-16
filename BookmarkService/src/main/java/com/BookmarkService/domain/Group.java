package com.BookmarkService.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "groups")
@RequiredArgsConstructor
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "group_generator")
    @SequenceGenerator(name = "group_generator", sequenceName = "groups_seq", allocationSize = 1)
    private Long id;

    @Column
    @NonNull
    private String name;

    @Override
    public String toString() {
        return String.format("(id = %s, name = %s)", this.getId(), this.getName());
    }
}
