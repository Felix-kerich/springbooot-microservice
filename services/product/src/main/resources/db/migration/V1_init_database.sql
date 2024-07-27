-- src/main/resources/db/migration/V1_init_database.sql

-- Create the category table
CREATE TABLE IF NOT EXISTS category (
    id INTEGER NOT NULL DEFAULT nextval('category_seq') PRIMARY KEY,
    description VARCHAR(255),
    name VARCHAR(255)
);

-- Create the product table
CREATE TABLE IF NOT EXISTS product (
    id INTEGER NOT NULL DEFAULT nextval('product_seq') PRIMARY KEY,
    description VARCHAR(255),
    name VARCHAR(255),
    available_quantity DOUBLE PRECISION NOT NULL,
    price NUMERIC(38, 2),
    category_id INTEGER,
    CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES category (id)
);

-- Create sequences
CREATE SEQUENCE IF NOT EXISTS category_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE IF NOT EXISTS product_seq START WITH 1 INCREMENT BY 50;
