CREATE TABLE cases (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255),
    description VARCHAR(255),
    status VARCHAR(255),
    created_at TIMESTAMP
);

CREATE TABLE comments (
    id BIGSERIAL PRIMARY KEY,
    text VARCHAR(255),
    created_at TIMESTAMP,
    case_id BIGINT NOT NULL,

    CONSTRAINT fk_comments_case
        FOREIGN KEY (case_id)
        REFERENCES cases(id)
);