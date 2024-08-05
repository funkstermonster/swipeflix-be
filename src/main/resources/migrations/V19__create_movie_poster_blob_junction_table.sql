-- Create the poster_blob table if it doesn't exist
CREATE TABLE IF NOT EXISTS poster_blob (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    img_data TEXT
);

-- Create the movie_poster_blob junction table
CREATE TABLE IF NOT EXISTS movie_poster (
    movie_id BIGINT NOT NULL,
    poster_id BIGINT NOT NULL,
    PRIMARY KEY (movie_id, poster_id),
    FOREIGN KEY (movie_id) REFERENCES movies(id),
    FOREIGN KEY (poster_id) REFERENCES poster_blob(id)
);