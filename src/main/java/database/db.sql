--Database creation
CREATE DATABASE mini_football_db;

--User creation
CREATE USER mini_football_db_manager WITH PASSWORD '123456';

--Giving access to the user to connect, create tables and make CRUD inside the database
GRANT CONNECT ON DATABASE mini_football_db TO mini_football_db_manager;

--Authorize CRUD operations on all future tables
ALTER DEFAULT PRIVILEGES IN SCHEMA public
      GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO mini_football_db_manager ;

--Auto-increment usage if needed(serial)
ALTER DEFAULT PRIVILEGES IN SCHEMA public
      GRANT USAGE, SELECT, UPDATE ON SEQUENCES TO mini_football_db_manager ;
