INSERT INTO Payment_Method(id, name, status)
VALUES (1, 'APM', 1)
ON DUPLICATE KEY UPDATE
name = VALUES(name);

INSERT INTO Payment_Type(id, type, status)
VALUES (1, 'SALE', 1)
ON DUPLICATE KEY UPDATE
type = VALUES(type);

INSERT INTO Provider(id, providerName, status)
VALUES (1, 'PAYPAL', 1)
ON DUPLICATE KEY UPDATE
providerName = VALUES(providerName);

INSERT INTO Transaction_Status(id, name, status)
VALUES
(1, 'CREATED', 1),
(2, 'INITIATED', 1),
(3, 'PENDING', 1),
(4, 'APPROVED', 1),
(5, 'SUCCESS', 1),
(6, 'FAILED', 1)
ON DUPLICATE KEY UPDATE
name = VALUES(name);
