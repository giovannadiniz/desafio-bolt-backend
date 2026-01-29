package com.bolt.desafio.Job

import com.bolt.desafio.Entity.Usina
import com.bolt.desafio.Repository.UsinaRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URI

@Component
class CsvAneelJob (
    private val repository: UsinaRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    // Use URI para evitar o 'deprecated'
    private val url =
        URI.create("https://dadosabertos.aneel.gov.br/dataset/57e4b8b5-a5db-40e6-9901-27ca629d0477/resource/4a615df8-4c25-48fa-bbea-873a36a79518/download/ralie-usina.csv")
            .toURL()

    @Scheduled(initialDelay = 5000, fixedDelay = 600000)
    fun processarCargaUsinas() {
        logger.info("Iniciando processamento otimizado...")

        val stream = url.openStream()
        val reader = BufferedReader(InputStreamReader(stream, Charsets.ISO_8859_1))

        // 1. Descobrir os índices (Igual ao outro candidato)
        val header = reader.readLine()?.split(";")?.map { it.trim('"').trim() } ?: return
        logger.info("Cabeçalho detectado: $header")
        val idx = mapOf(
            "ceg" to header.indexOf("CodCEG"),
            "nome" to header.indexOf("NomEmpreendimento"),
            "uf" to header.indexOf("SigUFPrincipal"),
            "comb" to header.indexOf("DscOrigemCombustivel"),
            "pot" to header.indexOf("MdaPotenciaOutorgadaKw"),
            "agente" to header.indexOf("NomEmpresaConexao")
        )

        // Limpamos o banco antes de começar (Requisito 2)
        repository.deleteAllInBatch()

        // 2. O Pulo do Gato: Processar em CHUNKS (Lotes)
        reader.lineSequence()
            .mapNotNull { linha ->
                val colunas = linha.split(";")
                try {
                    Usina(
                        ceg = colunas[idx["ceg"]!!].trim('"'),
                        nome = colunas[idx["nome"]!!].trim('"'),
                        agente = colunas[idx["agente"]!!].trim('"'),
                        combustivel = colunas[idx["comb"]!!].trim('"'),
                        uf = colunas[idx["uf"]!!].trim('"'),
                        potenciaKw = colunas[idx["pot"]!!].trim('"')
                            .replace(".", "").replace(",", ".").toDoubleOrNull() ?: 0.0
                    )
                } catch (e: Exception) {
                    logger.error("Lote de usinas com erro: "+ e )
                    null
                }
            }
            .chunked(1000) // Agrupa de 1000 em 1000
            .forEach { lote ->
                repository.saveAll(lote)
                logger.info("Lote de ${lote.size} usinas salvo...")
            }

        logger.info("Carga concluída com sucesso!")
    }
}