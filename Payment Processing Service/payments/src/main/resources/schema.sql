CREATE TABLE IF NOT EXISTS Payment_Method (
    id INT NOT NULL,
    name VARCHAR(50) NOT NULL,
    status TINYINT DEFAULT 1,
    creationDate TIMESTAMP(2) NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS Payment_Type (
    id INT NOT NULL,
    type VARCHAR(50) NOT NULL,
    status TINYINT DEFAULT 1,
    creationDate TIMESTAMP(2) NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS Provider (
    id INT NOT NULL AUTO_INCREMENT,
    providerName VARCHAR(50) NOT NULL,
    status TINYINT DEFAULT 1,
    creationDate TIMESTAMP(2) NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS Transaction_Status (
    id INT NOT NULL,
    name VARCHAR(50) NOT NULL,
    status TINYINT DEFAULT 1,
    creationDate TIMESTAMP(2) NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS `Transaction` (
    id INT NOT NULL AUTO_INCREMENT,
    userId INT NOT NULL,

    paymentMethodId INT NOT NULL,
    providerId INT NOT NULL,
    paymentTypeId INT NOT NULL,
    txnStatusId INT NOT NULL,

    amount DECIMAL(19,2) DEFAULT 0.00,
    currency VARCHAR(3) NOT NULL,

    merchantTransactionReference VARCHAR(50) NOT NULL,
    txnReference VARCHAR(50) NOT NULL,
    providerReference VARCHAR(100),

    errorCode VARCHAR(500),
    errorMessage VARCHAR(1000),

    creationDate TIMESTAMP(2) NOT NULL DEFAULT CURRENT_TIMESTAMP(2),
    retryCount INT DEFAULT 0,

    PRIMARY KEY (id),

    UNIQUE KEY transaction_txnReference (txnReference),

    CONSTRAINT transaction_paymentMethodId
        FOREIGN KEY (paymentMethodId)
        REFERENCES Payment_Method(id),

    CONSTRAINT transaction_providerId
        FOREIGN KEY (providerId)
        REFERENCES Provider(id),

    CONSTRAINT transaction_txnStatusId
        FOREIGN KEY (txnStatusId)
        REFERENCES Transaction_Status(id),

    CONSTRAINT transaction_paymentTypeId
        FOREIGN KEY (paymentTypeId)
        REFERENCES Payment_Type(id)
);

CREATE TABLE IF NOT EXISTS Transaction_Log (
    id INT NOT NULL AUTO_INCREMENT,
    transactionId INT NOT NULL,
    txnFromStatus VARCHAR(50) DEFAULT '-1',
    txnToStatus VARCHAR(50) DEFAULT '-1',
    creationDate TIMESTAMP(2) NOT NULL DEFAULT CURRENT_TIMESTAMP(2),

    PRIMARY KEY (id),

    CONSTRAINT transaction_log_transactionId
        FOREIGN KEY (transactionId)
        REFERENCES `Transaction`(id)
);