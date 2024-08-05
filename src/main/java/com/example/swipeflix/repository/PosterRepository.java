package com.example.swipeflix.repository;

import com.example.swipeflix.models.PosterBlob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PosterRepository extends JpaRepository<PosterBlob, Long> {
}
