-- Create the artists table with the correct structure
CREATE TABLE artists (
    id BIGINT AUTO_INCREMENT PRIMARY KEY
);

-- Create the artists_actors table to store the actors collection with the correct structure
CREATE TABLE artists_actors (
    artist_id BIGINT NOT NULL,
    character_name VARCHAR(255) NOT NULL,
    actor_name VARCHAR(255) NOT NULL,
    FOREIGN KEY (artist_id) REFERENCES artists(id)
);

-- Add the artist_id column to the movies table with the correct structure
ALTER TABLE movies
ADD COLUMN artist_id BIGINT,
ADD CONSTRAINT fk_artist_id FOREIGN KEY (artist_id) REFERENCES artists(id);