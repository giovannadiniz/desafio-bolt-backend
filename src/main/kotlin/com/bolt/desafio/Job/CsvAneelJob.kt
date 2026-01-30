package com.bolt.desafio.Job

import com.bolt.desafio.Entity.Job
import com.bolt.desafio.Entity.Usina
import com.bolt.desafio.Repository.JobRepository
import com.bolt.desafio.Repository.UsinaRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.net.URI
import java.time.LocalDateTime

@Component
class CsvAneelJob(
    private val repository: UsinaRepository,
    private val jobRepository: JobRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    private val url = URI.create(
        "https://dadosabertos.aneel.gov.br/dataset/57e4b8b5-a5db-40e6-9901-27ca629d0477/resource/4a615df8-4c25-48fa-bbea-873a36a79518/download/ralie-usina.csv"
    ).toURL()

    @Scheduled(initialDelay = 5000, fixedDelay = 120000)
    fun processarCargaUsinas() {
        val JOB_NAME = "JOB_CSV_IMPORT"
        val LOTE_SIZE = 500

        val progress = jobRepository.findById(JOB_NAME)
            .orElseGet {
                jobRepository.save(Job(JOB_NAME, 0, LocalDateTime.now()))
            }

        var currentLine = progress.ultimaLinhaProcessada
        logger.info("Iniciando lote a partir da linha $currentLine")

        url.openStream().bufferedReader(Charsets.ISO_8859_1).use { reader ->
            val header = reader.readLine() ?: return
            val idx = mapHeadings(header)

            val lote = reader.lineSequence()
                .drop(currentLine.toInt())
                .filter { it.isNotBlank() }
                .take(LOTE_SIZE)
                .map { linha -> parseUsina(linha, idx) }
                .filterNotNull()
                .toList()

            if (lote.isNotEmpty()) {
                try {
                    repository.saveAll(lote)

                    currentLine += lote.size
                    progress.ultimaLinhaProcessada = currentLine
                    progress.dataUltimaExecucao = LocalDateTime.now()
                    jobRepository.save(progress)

                    logger.info("Lote de ${lote.size} processado. Novo checkpoint: $currentLine")
                } catch (e: Exception) {
                    logger.error("Erro ao salvar lote: ${e.message}")
                }
            } else {
                logger.info("Fim do arquivo alcançado ou nenhuma nova linha para processar.")
            }
        }
    }

    private fun mapHeadings(header: String): Map<String, Int> {
        val cols = header.split(";").map { it.trim('"').trim() }
        return mapOf(
            "id" to cols.indexOf("_id"),
            "ceg" to cols.indexOf("CodCEG"),
            "nome" to cols.indexOf("NomEmpreendimento"),
            "uf" to cols.indexOf("SigUFPrincipal"),
            "comb" to cols.indexOf("DscOrigemCombustivel"),
            "pot" to cols.indexOf("MdaPotenciaOutorgadaKw"),
            "agente" to cols.indexOf("NomEmpresaConexao")
        )
    }

    private fun parseUsina(linha: String, idx: Map<String, Int>): Usina? {
        return try {
            val colunas = linha.split(";")

            fun getVal(key: String): String {
                val i = idx[key] ?: -1
                return if (i >= 0 && i < colunas.size) {
                    colunas[i].trim('"').trim()
                } else ""
            }

            Usina(
                ceg = getVal("ceg"),
                nome = getVal("nome"),
                agente = getVal("agente"),
                combustivel = getVal("comb"),
                uf = getVal("uf"),
                potenciaKw = getVal("pot")
                    .replace(".", "")
                    .replace(",", ".")
                    .toDoubleOrNull() ?: 0.0
            )
        } catch (e: Exception) {
            logger.error("Erro ao fazer parse da linha: $linha | Erro: ${e.message}")
            null
        }
    }
}