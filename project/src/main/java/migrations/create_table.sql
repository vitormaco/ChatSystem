CREATE TABLE messages
(
    id INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    time_sent TIMESTAMP,
    content VARCHAR(10000),
    source_id VARCHAR(30),
    destination_id VARCHAR(30)
)
