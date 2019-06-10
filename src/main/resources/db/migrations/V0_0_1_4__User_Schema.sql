CREATE TABLE users
(
  id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
  username   VARCHAR,
  name       VARCHAR,
  birth_date DATE
);

CREATE TABLE user_books
(
  book_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  FOREIGN KEY (book_id) REFERENCES books (id),
  FOREIGN KEY (user_id) REFERENCES users (id)
);
