package com.example.swipeflix.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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
            name = "user_liked_movies",
            joinColumns = @JoinColumn(name = "user_preferences_id"),
            inverseJoinColumns = @JoinColumn(name = "movie_id")
    )
    private Set<Movie> likedMovies = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "user_disliked_movies",
            joinColumns = @JoinColumn(name = "user_preferences_id"),
            inverseJoinColumns = @JoinColumn(name = "movie_id")
    )
    private Set<Movie> dislikedMovies = new HashSet<>();

    public void addLikedMovie(Movie movie) {
        likedMovies.add(movie);
    }

    public void addDislikedMovie(Movie movie) {
        dislikedMovies.add(movie);
    }

    public void removeLikedMovie(Movie movie) {
        likedMovies.remove(movie);
    }

    public void removeDislikedMovie(Movie movie) {
        dislikedMovies.remove(movie);
    }

    public Set<Genre> getLikedGenres() {
        return likedMovies.stream().flatMap(movie -> movie.getGenres().stream()).collect(Collectors.toSet());
    }

    public Set<Genre> getDislikedGenres() {
        return dislikedMovies.stream().flatMap(movie -> movie.getGenres().stream()).collect(Collectors.toSet());
    }
}
