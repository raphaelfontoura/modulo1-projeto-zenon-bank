USE zenomdb;

CREATE TABLE transactions_flat (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    step BIGINT NOT NULL,
    type ENUM('CASH_IN', 'CASH_OUT', 'DEBIT', 'PAYMENT', 'TRANSFER') NOT NULL,
    amount DECIMAL(20,2) NOT NULL,
    origin_name VARCHAR(100) NOT NULL,
    origin_old_balance DECIMAL(20,2) NOT NULL,
    origin_new_balance DECIMAL(20,2) NOT NULL,
    recipient_name VARCHAR(100) NOT NULL,
    recipient_old_balance DECIMAL(20,2) NOT NULL,
    recipient_new_balance DECIMAL(20,2) NOT NULL,
    is_fraud BOOLEAN,
    is_flagged_fraud BOOLEAN
);
