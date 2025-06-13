-- Drop existing tables if they exist
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS addresses CASCADE;
DROP TABLE IF EXISTS companies CASCADE;

-- Create companies table
CREATE TABLE companies (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    catch_phrase VARCHAR(255),
    bs VARCHAR(255)
);

-- Create addresses table
CREATE TABLE addresses (
    id BIGSERIAL PRIMARY KEY,
    street VARCHAR(255) NOT NULL,
    suite VARCHAR(255),
    city VARCHAR(255) NOT NULL,
    zipcode VARCHAR(255),
    lat VARCHAR(255),
    lng VARCHAR(255)
);

-- Create users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(255),
    website VARCHAR(255),
    password VARCHAR(255),
    address_id BIGINT REFERENCES addresses(id),
    company_id BIGINT REFERENCES companies(id)
);

-- Create indexes
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_address_id ON users(address_id);
CREATE INDEX idx_users_company_id ON users(company_id); 