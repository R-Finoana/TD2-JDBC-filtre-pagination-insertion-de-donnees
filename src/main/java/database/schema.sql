--Create type continent as enum
CREATE TYPE continent AS ENUM ('AFRICA', 'EUROPA', 'ASIA', 'AMERICA');

-- Table: Team
CREATE TABLE Team (
                      id INT PRIMARY KEY NOT NULL,
                      name VARCHAR(255) NOT NULL,
                      continent continent NOT NULL
);

--Create type position as enum
CREATE TYPE "position" AS ENUM ('GK, DEF, MIDF, STR');

-- Table: Player
CREATE TABLE Player (
                        id INT PRIMARY KEY NOT NULL,
                        name VARCHAR(255) NOT NULL,
                        age INT NOT NULL,
                        position "position" NOT NULL,
                        id_team INT,
                        CONSTRAINT Player_id_team_FK FOREIGN KEY (id_team) REFERENCES Team (id)
);

ALTER TABLE Player ADD COLUMN goal_nb int;

UPDATE Player SET goal_nb = 0 WHERE id = 1;
UPDATE Player SET goal_nb = 2 WHERE id = 2;
UPDATE Player SET goal_nb = 5 WHERE id = 3;
UPDATE Player SET goal_nb = NULL WHERE id = 4;
UPDATE Player SET goal_nb = NULL WHERE id = 5;