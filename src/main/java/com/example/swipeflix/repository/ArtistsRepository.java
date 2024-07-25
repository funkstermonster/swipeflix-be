package com.example.swipeflix.repository;

import com.example.swipeflix.models.Artists;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistsRepository extends JpaRepository<Artists, Long> {
}
