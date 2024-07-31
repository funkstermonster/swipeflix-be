package com.example.swipeflix.models;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "genres")
@Data
public class Genre {
    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    private EGenre name;

}
