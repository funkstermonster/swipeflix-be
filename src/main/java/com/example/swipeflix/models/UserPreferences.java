package com.example.swipeflix.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class UserPreferences {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "userPreferences")
    private User user;

    @ManyToMany
    @JoinTable(
            name = "user_liked_genres",
            joinColumns = @JoinColumn(name = "user_preferences_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> likedEGenres = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "user_disliked_genres",
            joinColumns = @JoinColumn(name = "user_preferences_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> dislikedEGenres = new HashSet<>();

    public void addLikedGenres(Set<Genre> genres) {
        likedEGenres.addAll(genres);
    }

    public void addDislikedGenres(Set<Genre> genres) {
        dislikedEGenres.addAll(genres);
    }
}
