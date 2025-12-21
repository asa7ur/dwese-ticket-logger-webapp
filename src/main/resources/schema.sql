SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS regions;
DROP TABLE IF EXISTS provinces;
DROP TABLE IF EXISTS tickets;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS product_ticket;
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

CREATE TABLE IF NOT EXISTS tickets (
    id INT AUTO_INCREMENT PRIMARY KEY,
    date DATETIME NOT NULL,
    discount DECIMAL(5, 2) NOT NULL
    );

CREATE TABLE IF NOT EXISTS products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL
    );

CREATE TABLE IF NOT EXISTS product_ticket (
    product_id INT NOT NULL,
    ticket_id INT NOT NULL,
    PRIMARY KEY (product_id, ticket_id),
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (ticket_id) REFERENCES tickets(id)
    );
