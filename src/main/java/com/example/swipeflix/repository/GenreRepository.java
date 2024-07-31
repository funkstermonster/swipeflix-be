package com.example.swipeflix.repository;

import com.example.swipeflix.models.EGenre;
import com.example.swipeflix.models.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {

    Optional<Genre> findByName(EGenre name);
}
