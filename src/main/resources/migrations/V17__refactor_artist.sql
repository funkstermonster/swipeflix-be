-- Create the join table for movie-artist relationships
CREATE TABLE IF NOT EXISTS movie_artist (
    movie_id BIGINT NOT NULL,
    artist_id BIGINT NOT NULL,
    PRIMARY KEY (movie_id, artist_id),
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE,
    FOREIGN KEY (artist_id) REFERENCES artists(id) ON DELETE CASCADE
);

-- Ensure the artists table exists and has the necessary column
ALTER TABLE artists
ADD COLUMN name VARCHAR(255) NOT NULL UNIQUE;
