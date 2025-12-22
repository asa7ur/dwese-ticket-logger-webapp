SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS regions;
DROP TABLE IF EXISTS provinces;
DROP TABLE IF EXISTS tickets;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS product_ticket;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS user_roles;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE regions
(
    id   INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(10)  NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    image VARCHAR(255) NULL
    );

CREATE TABLE provinces
(
    id        INT PRIMARY KEY AUTO_INCREMENT,
    code      VARCHAR(10)  NOT NULL UNIQUE,
    name      VARCHAR(100) NOT NULL,
    region_id INT          NOT NULL,
    FOREIGN KEY (region_id) REFERENCES regions (id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
    );

CREATE TABLE tickets (
    id INT AUTO_INCREMENT PRIMARY KEY,
    date DATETIME NOT NULL,
    discount DECIMAL(5, 2) NOT NULL
    );

CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL
    );

CREATE TABLE product_ticket (
    product_id INT NOT NULL,
    ticket_id INT NOT NULL,
    PRIMARY KEY (product_id, ticket_id),
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (ticket_id) REFERENCES tickets(id)
    );

CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    enabled BOOLEAN NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    image VARCHAR(255),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE
    CURRENT_TIMESTAMP,
    last_password_change_date TIMESTAMP
    );

CREATE TABLE roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) UNIQUE NOT NULL
    );

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
    );