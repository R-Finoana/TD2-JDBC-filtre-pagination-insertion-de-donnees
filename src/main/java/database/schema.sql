--Create type continent as enum
CREATE TYPE continent AS ENUM ('GK, DEF, MIDF, STR');

-- Table: Team
CREATE TABLE Team (
                      id INT PRIMARY KEY NOT NULL,
                      name VARCHAR(255) NOT NULL,
                      continent continent NOT NULL
);

--Create type position as enum
CREATE TYPE "position" AS ENUM ('AFRICA', 'EUROPA', 'ASIA', 'AMERICA');

-- Table: Player
CREATE TABLE Player (
                        id INT PRIMARY KEY NOT NULL,
                        name VARCHAR(255) NOT NULL,
                        age INT NOT NULL,
                        position "position" NOT NULL,
                        id_team INT,
                        CONSTRAINT Player_id_team_FK FOREIGN KEY (id_team) REFERENCES Team (id)
);