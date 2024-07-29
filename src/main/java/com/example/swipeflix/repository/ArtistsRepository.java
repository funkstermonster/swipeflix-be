package com.example.swipeflix.repository;

import com.example.swipeflix.models.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface ArtistsRepository extends JpaRepository<Artist, Long> {

     Optional<Artist> findByName(String name);
}
