DROP TABLE IF EXISTS users;
CREATE TABLE users (id integer PRIMARY KEY, name varchar(255));

-- SQL COMMENTS
DELETE FROM users WHERE id = 1 ;
INSERT INTO users (id,name) VALUES (1, 'eddie') ;