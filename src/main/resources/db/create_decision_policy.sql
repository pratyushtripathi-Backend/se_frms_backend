CREATE TABLE IF NOT EXISTS se_frms_decision_policy (
    id SERIAL PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    allow_min_score INTEGER NOT NULL,
    allow_max_score INTEGER NOT NULL,
    review_min_score INTEGER NOT NULL,
    review_max_score INTEGER NOT NULL,
    block_min_score INTEGER NOT NULL,
    block_max_score INTEGER NOT NULL,
    status BOOLEAN NOT NULL DEFAULT TRUE,
    created_by INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_decision_policy_allow_range
        CHECK (allow_min_score >= 0 AND allow_min_score <= allow_max_score),
    CONSTRAINT chk_decision_policy_review_range
        CHECK (review_min_score >= 0 AND review_min_score <= review_max_score),
    CONSTRAINT chk_decision_policy_block_range
        CHECK (block_min_score >= 0 AND block_min_score <= block_max_score),
    CONSTRAINT chk_decision_policy_range_order
        CHECK (
            review_min_score > allow_max_score
            AND block_min_score > review_max_score
        )
);

INSERT INTO se_frms_access_master (access_name, status, created_date, updated_at)
VALUES
    ('DECISION_POLICY_VIEW', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('DECISION_POLICY_CREATE', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('DECISION_POLICY_UPDATE', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('DECISION_POLICY_DELETE', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (access_name) DO NOTHING;
