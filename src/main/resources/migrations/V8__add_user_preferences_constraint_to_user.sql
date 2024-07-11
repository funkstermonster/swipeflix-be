-- Create the user_preferences table
CREATE TABLE user_preferences (
    id BIGINT AUTO_INCREMENT PRIMARY KEY
);

-- Add the user_preferences_id column to users table and set up the foreign key relationship
ALTER TABLE users
ADD COLUMN user_preferences_id BIGINT,
ADD CONSTRAINT fk_user_preferences
    FOREIGN KEY (user_preferences_id)
    REFERENCES user_preferences(id);

-- Create the user_liked_genres table
CREATE TABLE user_liked_genres (
    user_preferences_id BIGINT NOT NULL,
    genre_id BIGINT NOT NULL,
    PRIMARY KEY (user_preferences_id, genre_id),
    FOREIGN KEY (user_preferences_id) REFERENCES user_preferences(id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genres(id) ON DELETE CASCADE
);

-- Create the user_disliked_genres table
CREATE TABLE user_disliked_genres (
    user_preferences_id BIGINT NOT NULL,
    genre_id BIGINT NOT NULL,
    PRIMARY KEY (user_preferences_id, genre_id),
    FOREIGN KEY (user_preferences_id) REFERENCES user_preferences(id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genres(id) ON DELETE CASCADE
);
