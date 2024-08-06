ALTER TABLE order_detail
ADD COLUMN in_progress int DEFAULT 0;

ALTER TABLE order_detail
ADD COLUMN serving int DEFAULT 0;