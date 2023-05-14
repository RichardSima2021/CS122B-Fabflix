Video URL: https://youtu.be/N1XPKXiBOb4

Contributions:

Lisa Chen: CSS design and implementation, Shopping Cart, Checkout, Browse frontend, CartItem, User, Submit Payment

Richard Sima: Shopping Cart Servlet, Browse Servlet, XmlParser.Movie List js and servlet, login, Submit Payment


\
Substring Matching:

- Browsing by character was matched by "WHERE title LIKE [CHAR]%"

- Browsing by * was matched by WHERE m.title REGEXP '^[^a-zA-Z0-9]')

- Search by title was matched by "WHERE TITLE LIKE %title%"

- Search by star was matched by "WHERE s.name LIKE %name%"

- Search by director was matched by WHERE "m.director LIKE %name%"



XML Parsing Assumptions:

Genre matching was done with the following:\
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
codesToGenres.put("Hist", "History");\

If movie has same name, director and year it is counted as a duplicate and is not inserted\
If a parsed movie has no name, it's not inserted\
If a parsed movie has no genre, it's not inserted\
If it has a year value that contains non-integer characters, it's an invalid year and not inserted\

If an actor with the same name and birth year already exists, it's not inserted\

If an actor or movie was not found in the corresponding database, the star_in_movie connection is not inserted.
