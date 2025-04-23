CREATE TABLE IF NOT EXISTS index_info (
    id BIGINT NOT NULL PRIMARY KEY  GENERATED ALWAYS AS IDENTITY,
    index_classification VARCHAR(255) NOT NULL,
    index_name VARCHAR(255) NOT NULL,
    employed_items_count DOUBLE PRECISION NOT NULL,
    base_point_in_time VARCHAR(255) NOT NULL,
    base_index DOUBLE PRECISION NOT NULL,
    favorite BOOLEAN NOT NULL,
    source_type VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS index_data (
    id BIGINT PRIMARY KEY  GENERATED ALWAYS AS IDENTITY,
    index_info_id BIGINT NOT NULL,
    trade_date DATE NOT NULL,
    source_type VARCHAR(10) NOT NULL,
    open_price NUMERIC(18,2) NOT NULL,
    close_price NUMERIC(18,2) NOT NULL,
    high_price NUMERIC(18,2) NOT NULL,
    low_price NUMERIC(18,2) NOT NULL,
    diff NUMERIC(18,2) NOT NULL,
    change_rate NUMERIC(8,3) NOT NULL,
    volume BIGINT NOT NULL,
    trading_value NUMERIC(24,0) NOT NULL,
    market_cap NUMERIC(24,0) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NULL,
    CONSTRAINT uq_index_trade UNIQUE (index_info_id, trade_date),
    CONSTRAINT fk_index_info FOREIGN KEY (index_info_id) REFERENCES index_info(id)
);


CREATE TABLE IF NOT EXISTS sync_jobs (
    id BIGINT PRIMARY KEY  GENERATED ALWAYS AS IDENTITY,
    job_type VARCHAR(10) NOT NULL,
    target_date DATE NOT NULL,
    worker VARCHAR(50) NOT NULL,
    job_time TIMESTAMP NOT NULL,
    result VARCHAR(10) NOT NULL,
    index_info_id BIGINT,
    CONSTRAINT fk_indexinfo FOREIGN KEY (index_info_id) REFERENCES index_info(id)
);

CREATE TABLE IF NOT EXISTS auto_sync_config (
    id BIGINT PRIMARY KEY  GENERATED ALWAYS AS IDENTITY,
    index_info_id BIGINT NOT NULL UNIQUE,
    enabled BOOLEAN NOT NULL,
    CONSTRAINT fk_auto_sync_config_index_classification FOREIGN KEY (index_info_id) REFERENCES index_info(id)
);