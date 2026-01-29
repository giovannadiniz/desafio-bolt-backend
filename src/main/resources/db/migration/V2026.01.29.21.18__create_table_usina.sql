DO $$
BEGIN
    -- 1. Criar a tabela apenas se ela não existir
    IF NOT EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'usinas') THEN
CREATE TABLE energia.usinas (
                        ceg VARCHAR(50) PRIMARY KEY,
                        nome_usina VARCHAR(255) NOT NULL,
                        agente VARCHAR(255),
                        combustivel VARCHAR(100),
                        estado_uf VARCHAR(2),
                        potencia_kw DOUBLE PRECISION NOT NULL
);
END IF;

    -- 2. Criar o índice apenas se ele não existir
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_potencia') THEN
CREATE INDEX idx_potencia ON energia.usinas (potencia_kw DESC);
END IF;

END $$;