USE zenomdb;

CREATE TABLE customers (
                           id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           name VARCHAR(100) NOT NULL,
                           old_balance DECIMAL(20,2) NOT NULL,
                           new_balance DECIMAL(20,2) NOT NULL
);

CREATE TABLE transactions (
                              id BIGINT PRIMARY KEY AUTO_INCREMENT,
                              step BIGINT NOT NULL,
                              type ENUM('CASH_IN', 'CASH_OUT', 'DEBIT', 'PAYMENT', 'TRANSFER') NOT NULL,
                              amount DECIMAL(20,2) NOT NULL,
                              origin_id BIGINT NOT NULL,
                              recipient_id BIGINT NOT NULL,
                              is_fraud BOOLEAN,
                              is_flagged_fraud BOOLEAN,
                              FOREIGN KEY (origin_id) REFERENCES customers(id),
                              FOREIGN KEY (recipient_id) REFERENCES customers(id)
);
