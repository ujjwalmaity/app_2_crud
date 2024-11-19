--liquibase formatted sql

--changeset ujjwal:1
--comment: create table employee
CREATE TABLE employee (
    id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    phone_number VARCHAR(15) NOT NULL
);

--changeset ujjwal:2
--comment: alter table employee add unique phone_number
ALTER TABLE employee
ADD UNIQUE (phone_number);
