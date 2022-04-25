-- ATTENTION: DROP ALL TABLES
-- drop table coordinates, locations, persons, users;

-- CREATE ENUMS TYPES
-- create type color as ENUM ('RED', 'BLUE', 'YELLOW', 'ORANGE', 'WHITE');
-- create type movie_genre as ENUM ('ADVENTURE', 'THRILLER', 'HORROR');
-- create type mpaa_rating as ENUM ('G', 'PG', 'PG_13', 'NC_17');

-- CREATE TABLES
create table locations
(
    id   serial
        constraint location_pk
            primary key,
    x    double precision not null,
    y    double precision not null,
    name varchar(255)
);

create table persons
(
    id serial
        constraint person_pk
            primary key,
    name varchar(255) not null,
    weight double precision not null,
    hair_color color,
    location int not null
        constraint person_location_id_fk
            references locations (id)
            on update cascade on delete cascade
);

create table coordinates
(
    id serial
        constraint coordinates_pk
            primary key,
    x float not null,
    y int not null
);

create table users
(
    id serial
        constraint users_pk
            primary key,
    username varchar(255) unique,
    password varchar(255),
    salt varchar(127)
);

create table movies
(
    id serial
        constraint movies_pk
            primary key,
    user_id int not null constraint movies_users_id_fk
        references users (id)
        on update cascade on delete cascade,
    name varchar(255) not null,
    coordinates int not null
        constraint movies_coordinates_id_fk
            references coordinates
            on update cascade on delete cascade,
    creation_date date default now(),
    oscars_count int default 0,
    movie_genre movie_genre not null,
    mpaa_rating mpaa_rating,
    director int
        constraint movies_persons_id_fk
            references persons (id)
);