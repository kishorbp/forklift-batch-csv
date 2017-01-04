DROP TABLE vexss;

CREATE TABLE vexss  (
    kbo_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    product VARCHAR(60),
    object_type VARCHAR(60),
    query_topic_variant VARCHAR(200),
    full_solution VARCHAR(200),
    web_resource VARCHAR(500)
);
