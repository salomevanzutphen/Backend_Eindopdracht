-- password voor beide
INSERT INTO users (username, password, name, email, birthday) VALUES
    ('test', '$2a$12$jXRzGpSccm7Tv6pa5TgrHOGMQBnJJD4FXnht9O2RVApT1Kbq7xxN.', 'Test User', 'testuser@example.com', '1990-01-01');


INSERT INTO authorities (username, authority) VALUES
    ('test', 'ROLE_USER');



INSERT INTO users (username, password, name, email, birthday) VALUES
    ('test2', '$2a$12$jXRzGpSccm7Tv6pa5TgrHOGMQBnJJD4FXnht9O2RVApT1Kbq7xxN.', 'Test User', 'test2user@example.com', '1990-01-01');


INSERT INTO authorities (username, authority) VALUES
    ('test2', 'ROLE_USER');

INSERT INTO users (username, password, name, email, birthday) VALUES
    ('admin', '$2a$12$jXRzGpSccm7Tv6pa5TgrHOGMQBnJJD4FXnht9O2RVApT1Kbq7xxN.', 'Salomé', 'livinginsync@example.com', '1990-01-01');

INSERT INTO authorities (username, authority) VALUES
    ('admin', 'ROLE_ADMIN');