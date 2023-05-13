DELIMITER $$

	CREATE PROCEDURE getMaxStarID()
	BEGIN
		SELECT MAX(id) FROM stars;
	END
	$$


	CREATE PROCEDURE add_movie (IN title VARCHAR(100), IN year INT, IN director VARCHAR(100), IN star VARCHAR(100), IN star_year INT, IN genre VARCHAR(32), 
		OUT status VARCHAR(10), OUT movieID VARCHAR(10), OUT starID VARCHAR(10), OUT genreID INT, OUT newStarEntry bool, OUT newGenreEntry bool)
	BEGIN
		DECLARE movie_id VARCHAR(10);
		DECLARE star_id VARCHAR(10);
		DECLARE genre_id INT;
		IF (SELECT movie_exists(title, year, director) = "N") THEN


			-- insert_into_movies, and returns movieID
			SELECT insert_into_movies(title, year, director) INTO movie_id;	
			-- SET movie_id = insert_into_movies(title, year, director);

			-- determine whether we need to insert a new star
			-- returns starId
			IF (star_exists(star) = "N") THEN
				SELECT insert_into_stars(star, star_year) INTO star_id;
				SELECT 1 INTO newStarEntry;
			ELSE
				SELECT find_star_by_name(star) INTO star_id;
				SELECT 0 INTO newStarEntry;
			END IF;

			SELECT star_id INTO starID;




			-- determine whether we need to insert a new genre
			-- returns genreId
			IF (genre_exists(genre) = "N") THEN
				-- returns genre_id
				SELECT insert_into_genres(genre) INTO genre_id;
				SELECT 1 INTO newGenreEntry;
			ELSE
				SELECT find_genre_by_name(genre) INTO genre_id;
				SELECT 0 INTO newGenreEntry;
			END IF;

			SELECT genre_id INTO genreID;
			
			

			INSERT INTO stars_in_movies VALUES(star_id, movie_id);
			INSERT INTO genres_in_movies VALUES(genre_id, movie_id);

			SELECT "Inserted" INTO status;
			SELECT movie_id INTO movieID;
			
 				
		ELSE	
			SELECT "Exists" INTO status;

		END IF;
	END
	$$


-- HELPER FUNCTIONS

	CREATE FUNCTION genre_exists(genre VARCHAR(32))
	RETURNS VARCHAR(1)
	DETERMINISTIC
	BEGIN
		DECLARE c INT;

		SELECT COUNT(*) INTO c FROM genres g
		WHERE g.name = genre;
		
		IF (c = 0) THEN
			RETURN 'N';
		ELSE
			RETURN 'Y';
		END IF;
		
	END
	$$


	CREATE FUNCTION find_genre_by_name(genre VARCHAR(32))
	RETURNS INT
	DETERMINISTIC
	BEGIN
		
		DECLARE genreId INT;
		SELECT id INTO genreId FROM genres
		WHERE name = genre;

		RETURN genreId;

	END
	$$


	CREATE FUNCTION insert_into_genres(name VARCHAR(32))
	RETURNS INT
	DETERMINISTIC
	BEGIN
		DECLARE this_genre_id INT;

		INSERT INTO genres (name) VALUES(name);
		SELECT MAX(id) INTO this_genre_id FROM genres;
		RETURN this_genre_id;
	END
	$$




	CREATE FUNCTION star_exists(starname VARCHAR(100))
	RETURNS VARCHAR(1)
	DETERMINISTIC
	BEGIN
		DECLARE c INT;

		SELECT COUNT(*) INTO c FROM stars s
		WHERE s.name = starname;
		
		IF (c = 0) THEN
			RETURN 'N';
		ELSE
			RETURN 'Y';
		END IF;
		
	END
	$$


	CREATE FUNCTION find_star_by_name(starname VARCHAR(100))
	RETURNS VARCHAR(10)
	DETERMINISTIC
	BEGIN
		
		DECLARE starId VARCHAR(10);
		SELECT MAX(id) INTO starId FROM stars
		WHERE name = starname;

		RETURN starId;

	END
	$$


	CREATE FUNCTION insert_into_stars(name VARCHAR(100), birthYear INT)
	RETURNS VARCHAR(10)
	DETERMINISTIC
	BEGIN
		DECLARE previous_star_id VARCHAR(10);
		DECLARE previous_star_id_numstr VARCHAR(10);
		DECLARE previous_star_id_num INT;
		DECLARE this_star_id VARCHAR(10);
		DECLARE this_star_id_num INT;

		SELECT MAX(id) INTO previous_star_id FROM stars;

		SELECT SUBSTRING(previous_star_id, 3, LENGTH(previous_star_id)) INTO previous_star_id_numstr;
		SELECT CAST(previous_star_id_numstr AS UNSIGNED) INTO previous_star_id_num;

		SET this_star_id_num = previous_star_id_num + 1;

		SELECT CONCAT("nm",this_star_id_num) INTO this_star_id;

		INSERT INTO stars (id, name, birthYear) VALUES(this_star_id, name, birthYear);

		RETURN this_star_id;
	END
	$$




	CREATE FUNCTION insert_into_movies(title VARCHAR(100), year INT, director VARCHAR(100))
	RETURNS VARCHAR(10)
	DETERMINISTIC
	BEGIN
		DECLARE previous_movie_id VARCHAR(10);
		DECLARE previous_movie_id_numstr VARCHAR(10);
		DECLARE previous_movie_id_num INT;
		DECLARE this_movie_id VARCHAR(10);
		DECLARE this_movie_id_num INT;

		SELECT MAX(id) INTO previous_movie_id FROM movies; 

		SELECT SUBSTRING(previous_movie_id, 3, LENGTH(previous_movie_id)) INTO previous_movie_id_numstr;
		SELECT CAST(previous_movie_id_numstr AS UNSIGNED) INTO previous_movie_id_num;

		-- tt _ 499469

		SET this_movie_id_num = previous_movie_id_num + 1;

		IF (this_movie_id_num < 1000000) THEN
			SELECT CONCAT("tt0",this_movie_id_num) INTO this_movie_id;
		ELSE
			SELECT CONCAT("tt",this_movie_id_num) INTO this_movie_id;
		END IF;

		-- now we have new movieId

		INSERT INTO movies(id, title, year, director) VALUES(this_movie_id, title, year, director);

		RETURN this_movie_id;
		
	END
	$$


	CREATE FUNCTION movie_exists(title VARCHAR(100), year INT, director VARCHAR(100))
	RETURNS CHAR(1)
	DETERMINISTIC
	BEGIN
		DECLARE c INT;

		SELECT COUNT(*) INTO c FROM movies m
		WHERE m.title = title AND m.year = year AND m.director = director;
		
		IF (c = 0) THEN
			RETURN 'N';
		ELSE
			RETURN 'Y';
		END IF;
	END
	$$

DELIMITER ;
