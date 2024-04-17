CREATE TABLE transactions (
  id UUID PRIMARY KEY,
  state INT NOT NULL,
  created_on TIMESTAMP NOT NULL,
  last_modified_on TIMESTAMP NOT NULL
);