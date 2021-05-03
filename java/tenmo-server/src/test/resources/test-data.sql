/* Clean out the data first */
--BEGIN TRANSACTION;
DELETE FROM transfers CASCADE;
DELETE FROM accounts CASCADE;
DELETE FROM users CASCADE;

--/* test users */
INSERT INTO users (user_id, username, password_hash)
VALUES (1001, 'johncleese', '$2a$10$QOyqZ0Z9uyCTNxfPCbaskewPRAYZZ87LJFezDVoEIlLdK7I/c1rvy');

INSERT INTO users (user_id, username, password_hash)
VALUES (1002, 'grahamchapman', '$2a$10$oXEeHIkJn74II9hpYfdPTev/QAFbJhGCZjLqtMYqWs9bY8S3VPnJ6');

INSERT INTO users (user_id, username, password_hash)
VALUES (1003, 'michaelpalin', '$2a$10$jBPVw.2w1lcg4fWkBQctJOa6IOSADkzrZdHqS0O8cpTMRYSCcBT4i');


--/* test accounts */
INSERT INTO accounts(account_id, user_id, balance)
VALUES (2001, 1001, 1000.00);

INSERT INTO accounts(account_id, user_id, balance)
VALUES (2002, 1002, 1000.00);

INSERT INTO accounts(account_id, user_id, balance)
VALUES (2003, 1003, 1000.00);


/* test transfers */
INSERT INTO transfers(transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount)
VALUES (3001, 2, 2, 2001, 2002, 100.00);

INSERT INTO transfers(transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount)
VALUES (3002, 2, 2, 2002, 2001, 200.00);

INSERT INTO transfers(transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount)
VALUES (3003, 2, 3, 2001, 2002, 300.00);

INSERT INTO transfers(transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount)
VALUES (3004, 1, 1, 2001, 2002, 400.00);

INSERT INTO transfers(transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount)
VALUES (3005, 2, 3, 2002, 2001, 500.00);

INSERT INTO transfers(transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount)
VALUES (3006, 1, 1, 2002, 2001, 600.00);

--ROLLBACK;
