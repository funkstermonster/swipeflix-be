package com.example.swipeflix.services;

import com.example.swipeflix.models.Movie;
import com.example.swipeflix.models.User;
import com.example.swipeflix.payload.response.MessageResponse;
import com.example.swipeflix.repository.MovieRepository;
import com.example.swipeflix.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
public class FavoriteService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MovieRepository movieRepository;


    public MessageResponse addFavorite(Long userId, Long movieId) {
        Optional<User> userOptional = userRepository.findById(userId);
        Optional<Movie> movieOptional = movieRepository.findById(movieId);

        if (userOptional.isPresent() && movieOptional.isPresent()) {
            User user = userOptional.get();
            Movie movie = movieOptional.get();

            if (user.getFavoriteMovies().contains(movie)) {
                return new MessageResponse("Movie is already added to your favorites!");
            }
            user.getFavoriteMovies().add(movie);
            userRepository.save(user);
            return new MessageResponse("Movie successfully added to favorites!");
        }
        return new MessageResponse("User or movie not found!");
    }

    public MessageResponse removeFavorite(Long userId, Long movieId) {
        return userRepository.findById(userId)
                .map(user -> {
                    boolean removed = user.getFavoriteMovies()
                            .removeIf(movie -> Objects.equals(movie.getId(), movieId));

                    if (removed) {
                        userRepository.save(user);
                        return new MessageResponse("Successfully removed from favorites!");
                    } else {
                        return new MessageResponse("Movie not found in favorites!");
                    }
                })
                .orElseGet(() -> new MessageResponse("User not found!"));
    }

    public Set<Movie> getUserFavorites(Long userId) {
        return userRepository.findById(userId)
                .map(User::getFavoriteMovies)
                .orElse(Set.of());
    }
}
