-- Create MyTest table

CREATE TABLE MyTest (
     id CHAR(50) PRIMARY KEY NOT NULL COMMENT 'Unique identifier for the record, stored as a UUID',
     name VARCHAR(255) NOT NULL COMMENT 'Name of the individual or entity',
     city VARCHAR(255) NOT NULL COMMENT 'City associated with the individual or entity',
     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Timestamp when the record was created',
     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Timestamp when the record was last updated'
) COMMENT='MyTest table to store file information.';