-- Create the artists table
CREATE TABLE artists (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    movie_id BIGINT NOT NULL,
    FOREIGN KEY (movie_id) REFERENCES movies(id)
);

-- Create the artists_actors table to store the actors collection
CREATE TABLE artists_actors (
    artist_id BIGINT NOT NULL,
    actor VARCHAR(255) NOT NULL,
    PRIMARY KEY (artist_id, actor),
    FOREIGN KEY (artist_id) REFERENCES artists(id)
);

-- Add the artist_id column to the movies table
ALTER TABLE movies
ADD COLUMN artist_id BIGINT,
ADD CONSTRAINT fk_artist_id FOREIGN KEY (artist_id) REFERENCES artists(id);
