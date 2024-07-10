-- Create the user_preferences table
CREATE TABLE user_preferences (
    user_id BIGINT PRIMARY KEY,
    CONSTRAINT fk_user_preferences_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create the user_liked_genres join table
CREATE TABLE user_liked_genres (
    user_id BIGINT NOT NULL,
    genre_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, genre_id),
    FOREIGN KEY (user_id) REFERENCES user_preferences(user_id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genres(id) ON DELETE CASCADE
);

-- Create the user_disliked_genres join table
CREATE TABLE user_disliked_genres (
    user_id BIGINT NOT NULL,
    genre_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, genre_id),
    FOREIGN KEY (user_id) REFERENCES user_preferences(user_id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genres(id) ON DELETE CASCADE
);