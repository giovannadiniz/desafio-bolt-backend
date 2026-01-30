DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'usinas') THEN
CREATE TABLE energia.usina (
                        id BIGSERIAL PRIMARY KEY,
                        ceg VARCHAR(50),
                        nome_usina VARCHAR(255),
                        agente VARCHAR(255),
                        combustivel VARCHAR(100),
                        estado_uf VARCHAR(2),
                        potencia_kw DOUBLE PRECISION
);
END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_potencia') THEN
CREATE INDEX idx_potencia ON energia.usina (potencia_kw DESC);
END IF;

END $$;