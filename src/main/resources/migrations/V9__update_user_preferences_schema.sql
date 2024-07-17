-- Drop old liked and disliked genres tables if they exist
DROP TABLE IF EXISTS user_liked_genres;
DROP TABLE IF EXISTS user_disliked_genres;

-- Create new join table for liked movies
CREATE TABLE IF NOT EXISTS user_liked_movies (
    user_preferences_id BIGINT NOT NULL,
    movie_id BIGINT NOT NULL,
    PRIMARY KEY (user_preferences_id, movie_id),
    FOREIGN KEY (user_preferences_id) REFERENCES user_preferences(id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE
);

-- Create new join table for disliked movies
CREATE TABLE IF NOT EXISTS user_disliked_movies (
    user_preferences_id BIGINT NOT NULL,
    movie_id BIGINT NOT NULL,
    PRIMARY KEY (user_preferences_id, movie_id),
    FOREIGN KEY (user_preferences_id) REFERENCES user_preferences(id) ON DELETE CASCADE,
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE
);
