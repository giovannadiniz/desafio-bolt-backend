package com.bolt.desafio.Job

import com.bolt.desafio.Entity.Job
import com.bolt.desafio.Entity.Usina
import com.bolt.desafio.Repository.JobRepository
import com.bolt.desafio.Repository.UsinaRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URI
import java.time.LocalDateTime

@Component
class CsvAneelJob(
    private val repository: UsinaRepository,
    private val jobRepository: JobRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)


    private val url =
        URI.create("https://dadosabertos.aneel.gov.br/dataset/57e4b8b5-a5db-40e6-9901-27ca629d0477/resource/4a615df8-4c25-48fa-bbea-873a36a79518/download/ralie-usina.csv")
            .toURL()

    @Scheduled(initialDelay = 5000, fixedDelay = 300000)
    fun processarCargaUsinas() {
        val JOB_NAME = "JOB_CSV_IMPORT"

        // Busca o progresso ou cria um novo objeto caso seja a primeira execução da história
        val progress = jobRepository.findById(JOB_NAME)
            .orElseGet {
                logger.info("Primeira execução detectada. Criando registro de controle...")
                jobRepository.save(Job(JOB_NAME, 0, LocalDateTime.now()))
            }

        var currentLine = progress.ultimaLinhaProcessada
        logger.info("Retomando da linha $currentLine. Última execução em: ${progress.dataUltimaExecucao}")

        val stream = url.openStream()
        val reader = BufferedReader(InputStreamReader(stream, Charsets.ISO_8859_1))

        val header = reader.readLine() ?: return
        val idx = mapHeadings(header)

        // O segredo está aqui: drop(currentLine) pula o que já foi salvo
        reader.lineSequence()
            .drop(currentLine.toInt())
            .filter { it.isNotBlank() }
            .mapIndexed { _, linha -> parseUsina(linha, idx) }
            .filterNotNull()
            .chunked(1000)
            .forEach { lote ->
                try {
                    // 1. Salva os dados das usinas
                    repository.saveAll(lote)

                    // 2. Atualiza o ponteiro de progresso
                    currentLine += lote.size
                    progress.ultimaLinhaProcessada = currentLine
                    progress.dataUltimaExecucao = LocalDateTime.now()

                    // 3. Persiste o checkpoint no banco energia.job
                    jobRepository.save(progress)

                    logger.info("Checkpoint: $currentLine linhas processadas com sucesso.")
                } catch (e: Exception) {
                    logger.error("Erro ao salvar lote próximo à linha $currentLine: ${e.message}")
                    throw e // Para a execução para não perder o tracking do erro
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

            // Função auxiliar segura para buscar o valor pela chave do mapa
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
                // Tratamento de número: remove ponto de milhar e troca vírgula por ponto
                potenciaKw = getVal("pot")
                    .replace(".", "")
                    .replace(",", ".")
                    .toDoubleOrNull() ?: 0.0
            )
        } catch (e: Exception) {
            logger.error("Erro ao fazer parse da linha: $linha | Erro: ${e.message}")
            null // Retorna null para o mapNotNull ignorar essa linha e seguir o processamento
        }
    }
}