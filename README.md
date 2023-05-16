Video URL: https://youtu.be/oSPpXsYjSSI

Contributions:

Lisa Chen: Dashboard CSS + HTML, XML Dom parser, DNS setup

Richard Sima: Dashboard Servlet, SQL Stored Procedure, XML Dom parser, Custom Domain Purchase


\
Substring Matching:

- Browsing by character was matched by "WHERE title LIKE [CHAR]%"

- Browsing by * was matched by WHERE m.title REGEXP '^[^a-zA-Z0-9]')

- Search by title was matched by "WHERE TITLE LIKE title%"

- Search by star was matched by "WHERE s.name LIKE name%"

- Search by director was matched by WHERE "m.director LIKE name%"


Stored Procedure implemented for queries where it contains text that the user inputs. Such as Login, Search, and Checkout


XML Parsing Assumptions:

If movie has same name, director and year it is counted as a duplicate and is not inserted\
If a parsed movie has no name, it's not inserted\
If a parsed movie has no genre, it's not inserted\
If it has a year value that contains non-integer characters, it's an invalid year and not inserted\

If an actor or movie was not found in the corresponding database, the star_in_movie connection is not inserted.

\

XML Parsing and insertion optimizations:

Writing to memory: While parsing the data from the XML file, write all the raw data into memory for quicker access later instead of needing to re fetch from the database. Cut time down from 15 min to 5 mins.\
Execution of queries in batches: Minimize the amount of connections to the server that need to be made and broken by grouping stored procedures into batches and then executing batches of insert/select all at once. Cut time down from 5 mins to 1:35.\
All times noted are running time on local machine. AWS time is approx. double.

XML Genre Matching was done with the following:\
codesToGenres.put("Susp", "Thriller");\
codesToGenres.put("CnR", "Crime");\
codesToGenres.put("Dram", "Drama");\
codesToGenres.put("West", "Western");\
codesToGenres.put("Myst", "Mystery");\
codesToGenres.put("S.F.", "Sci-Fi");\
codesToGenres.put("Advt", "Adventure");\
codesToGenres.put("Horr", "Horror");\
codesToGenres.put("Romt", "Romance");\
codesToGenres.put("Comd", "Comedy");\
codesToGenres.put("Musc", "Musical");\
codesToGenres.put("Docu", "Documentary");\
codesToGenres.put("Porn", "Adult");\
codesToGenres.put("Noir", "Black");\
codesToGenres.put("BioP", "Biography");\
codesToGenres.put("TV", "TV Show");\
codesToGenres.put("TVs", "TV series");\
codesToGenres.put("TVm", "TV miniseries");\
codesToGenres.put("Ctxx", "Uncategorized");\
codesToGenres.put("Actn", "Action");\
codesToGenres.put("Camp", "Camp");\
codesToGenres.put("Disa", "Disaster");\
codesToGenres.put("Epic", "Epic");\
codesToGenres.put("Cart", "Animation");\
codesToGenres.put("Faml", "Family");\
codesToGenres.put("Surl", "Surreal");\
codesToGenres.put("AvGa", "Avant Garde");\
codesToGenres.put("Hist", "History");

Any others genres were added with its code as the name

XML Report:\
Inserted 8711 movies\
3324 movies had No Genres\
17 movies had Invalid Year Value\
8 movies had Unnamed Movie\
Added 88 genres\
Inserted 6839 actors\
28699 actors added in 6434 movies\
Actor not provided for movie: 20772\
Movies not provided for an actor: 6960\
No actors for a role: 3