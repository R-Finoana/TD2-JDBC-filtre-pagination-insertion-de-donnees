--Team insertion
INSERT INTO Team (id, name, continent)
    VALUES (1, 'Real Madrid CF', 'EUROPA'),
           (2, 'FC Barcelona', 'EUROPA'),
           (3, 'Altético de Madrid', 'EUROPA'),
           (4, 'Al Ahly SC', 'AFRICA'),
           (5, 'Inter Miami CF', 'AMERICA');

--Player insertion
INSERT INTO Player (id, name, age, position, id_team)
    VALUES (1, 'Thibaut Courtois', 32, 'GK', 1),
           (2, 'Dani Carvajal', 33, 'DEF', 1),
           (3, 'Jude Bellingham', 21, 'MIDF', 1),
           (4, 'Robert Lewandowski', 36, 'STR', 2),
           (5, 'Antoine Griezmann', 33, 'STR', 3);