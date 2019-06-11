ALTER TABLE books
  ALTER COLUMN year TYPE VARCHAR;

ALTER TABLE books
  DROP COLUMN pages; -- When applying this migration, there should not be data in db yet.

ALTER TABLE books
  ADD COLUMN pages INTEGER;
