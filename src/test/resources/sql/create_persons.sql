DROP TABLE IF EXISTS persons;
CREATE TABLE persons (id integer PRIMARY KEY GENERATED ALWAYS AS IDENTITY, name varchar(255));
INSERT INTO persons (name) VALUES (':name');