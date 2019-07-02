CREATE TABLE user_roles
(
  user_id BIGINT NOT NULL,
  role    VARCHAR,
  FOREIGN KEY (user_id) REFERENCES users (id)
);
