-- Drop the existing foreign key constraint and artist_id column from movies table
ALTER TABLE movies DROP FOREIGN KEY fk_artist_id;
ALTER TABLE movies DROP COLUMN artist_id;

-- Drop the existing artists_actors table if it exists
DROP TABLE IF EXISTS artists_actors;

-- Drop the existing artists table if it exists
DROP TABLE IF EXISTS artists;