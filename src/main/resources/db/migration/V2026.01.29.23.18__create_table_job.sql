DO
$$
    BEGIN
        CREATE TABLE IF NOT EXISTS energia.job
        (
            job_name                VARCHAR(100) PRIMARY KEY,
            ultima_linha_processada INT       NOT NULL,
            data_ultima_execucao    TIMESTAMP NOT NULL
        );
    END
$$;