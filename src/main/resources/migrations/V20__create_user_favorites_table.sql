CREATE TABLE user_favorites (
        user_id BIGINT NOT NULL,
        movie_id BIGINT NOT NULL,
        PRIMARY KEY (user_id, movie_id),
        CONSTRAINT fk_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,
        CONSTRAINT fk_movie
        FOREIGN KEY (movie_id)
        REFERENCES movies(id)
        ON DELETE CASCADE
);