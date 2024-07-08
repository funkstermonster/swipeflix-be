package com.example.swipeflix.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@Table(name = "movies")
@NoArgsConstructor
@AllArgsConstructor
public class Movie {
    @Id
    private Long id;
    private List<String> genres;
    private String imdbId;
    @Column(columnDefinition = "TEXT")
    private String overview;
    private String posterPath;
    private LocalDate releaseDate;
    private Integer runtime;
    private String title;
    private Double rating;

}
