CREATE TABLE messages
(
    id INT PRIMARY KEY NOT NULL,
    time_sent DATE,
    message_type VARCHAR(20),
    content VARCHAR(10000)
)
