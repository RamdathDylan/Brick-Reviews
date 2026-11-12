
CREATE TABLE users (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(20)  NOT NULL DEFAULT 'STUDENT',
    verified      BOOLEAN      NOT NULL DEFAULT FALSE,
    status        VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT users_email_lowercase CHECK (email = LOWER(email)),
    CONSTRAINT users_role_valid      CHECK (role IN ('STUDENT', 'ADMIN')),
    CONSTRAINT users_status_valid    CHECK (status IN ('ACTIVE', 'BANNED'))
);

CREATE TABLE courses (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code       VARCHAR(20)  NOT NULL UNIQUE,
    title      VARCHAR(200) NOT NULL,
    department VARCHAR(100) NOT NULL,
    retired    BOOLEAN      NOT NULL DEFAULT FALSE,
    CONSTRAINT courses_code_format CHECK (code ~ '^[A-Z]{4}-[0-9]{3}$')
);

CREATE TABLE professors (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name       VARCHAR(150) NOT NULL,
    department VARCHAR(100) NOT NULL,
    retired    BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE TABLE reviews (
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    author_id    BIGINT        NOT NULL REFERENCES users(id)      ON DELETE CASCADE,
    course_id    BIGINT        NOT NULL REFERENCES courses(id)    ON DELETE RESTRICT,
    professor_id BIGINT        NOT NULL REFERENCES professors(id) ON DELETE RESTRICT,
    score        SMALLINT      NOT NULL,
    body         VARCHAR(2000) NOT NULL,
    created_at   TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    CONSTRAINT reviews_one_per_course UNIQUE (author_id, course_id),         
    CONSTRAINT reviews_score_range    CHECK (score BETWEEN 1 AND 5),
    CONSTRAINT reviews_body_not_blank CHECK (LENGTH(TRIM(body)) > 0)
);

CREATE INDEX idx_reviews_course    ON reviews (course_id, created_at DESC);
CREATE INDEX idx_reviews_professor ON reviews (professor_id, created_at DESC);

CREATE TABLE reports (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    reporter_id BIGINT      NOT NULL REFERENCES users(id)   ON DELETE CASCADE,
    review_id   BIGINT      NOT NULL REFERENCES reviews(id) ON DELETE CASCADE,
    reason      VARCHAR(30) NOT NULL,
    status      VARCHAR(10) NOT NULL DEFAULT 'OPEN',
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT reports_one_per_user UNIQUE (reporter_id, review_id),          
    CONSTRAINT reports_reason_valid CHECK (reason IN ('HARASSMENT', 'PERSONAL_ATTACK', 'SPAM', 'NOT_ABOUT_COURSE')),
    CONSTRAINT reports_status_valid CHECK (status IN ('OPEN', 'CLOSED'))
);

CREATE INDEX idx_reports_status ON reports (status);
CREATE INDEX idx_reports_review ON reports (review_id);

CREATE TABLE catalog_requests (
    id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    requester_id   BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type           VARCHAR(10)  NOT NULL,
    course_code    VARCHAR(20),
    title          VARCHAR(200),
    professor_name VARCHAR(150),
    department     VARCHAR(100) NOT NULL,
    status         VARCHAR(10)  NOT NULL DEFAULT 'PENDING',
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT catalog_requests_type_valid   CHECK (type IN ('COURSE', 'PROFESSOR')),
    CONSTRAINT catalog_requests_status_valid CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED')),
    CONSTRAINT catalog_requests_fields_match_type CHECK (
        (type = 'COURSE'    AND course_code IS NOT NULL AND title IS NOT NULL) OR
        (type = 'PROFESSOR' AND professor_name IS NOT NULL)
    )
);

CREATE TABLE verification_tokens (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id    BIGINT      NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash VARCHAR(64) NOT NULL UNIQUE,                                   
    expires_at TIMESTAMPTZ NOT NULL,
    used       BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_verification_tokens_user ON verification_tokens (user_id);

CREATE TABLE admin_action_logs (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    admin_id    BIGINT       NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    action      VARCHAR(30)  NOT NULL,
    target_type VARCHAR(20)  NOT NULL,
    target_id   BIGINT       NOT NULL,
    reason      VARCHAR(500),
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE FUNCTION prevent_admin_log_changes() RETURNS TRIGGER AS $$
BEGIN
    RAISE EXCEPTION 'admin_action_logs is append-only';
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER admin_action_logs_append_only
    BEFORE UPDATE OR DELETE ON admin_action_logs
    FOR EACH ROW EXECUTE FUNCTION prevent_admin_log_changes();