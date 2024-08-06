INSERT INTO role (role_name)
VALUES ('admin');
INSERT INTO user (user_name, full_name, password)
VALUES (
        'admin',
        'admin',
        '$2a$10$fT24blmx9Tm5PE/XXm4OyOOIkLSKx9OWQemQBtEeNzX6wusYQFmKG'
    );
INSERT INTO user_role (user_id, role_id)
VALUES (1, 1);