DROP TABLE IF EXISTS persons;
CREATE TABLE persons (id integer PRIMARY KEY, name varchar(255));

-- SQL COMMENTS
DELETE FROM persons WHERE id = 1 ;
INSERT INTO persons (id,name) VALUES (1,'eddie') ;