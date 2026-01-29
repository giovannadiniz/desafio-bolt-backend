package com.bolt.desafio.job

import com.bolt.desafio.entity.Usina
import com.bolt.desafio.repository.UsinaRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.net.URL

@Component
class CsvAneelJob (
    private val repository: UsinaRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val csvUrl = "https://dadosabertos.aneel.gov.br/dataset/ralie-relatorio-de-acompanhamento-da-expansao-da-oferta-de-geracao-de-energia-eletrica/resource/a3c58ecb-e936-4dc1-884b-9941f7079a73/download/ralie-usina.csv"

    @Scheduled(fixedRate = 86400000)
    fun processarCargaUsinas() {
        logger.info("Iniciando Job de extração da ANEEL...")

        try {
            val connection = URL(csvUrl).openConnection()
            connection.setRequestProperty("User-Agent", "Mozilla/5.0")

            val usinas = URL(csvUrl).openConnection().getInputStream()
                .bufferedReader(Charsets.ISO_8859_1).useLines { lines ->
                    lines.drop(1)
                        .filter { it.isNotBlank() }
                        .map { linha ->
                            val colunas = linha.split(";")
                            Usina(
                                ceg = colunas[3].limpar(),
                                nome = colunas[8].limpar(),
                                agente = colunas[1].limpar(),
                                combustivel = colunas[7].limpar(),
                                uf = colunas[5].limpar(),
                                potenciaKw = formatarPotencia(colunas[10])
                            )
                        }.toList()
                }

            logger.info("Limpando banco e salvando ${usinas.size} registros...")
            repository.deleteAllInBatch()
            repository.saveAll(usinas)

            logger.info("Carga finalizada com sucesso! ${usinas.size} usinas importadas.")

        } catch (e: Exception) {
            logger.error("Falha ao processar o Job: ${e.message}")
        }
    }

    private fun formatarPotencia(valor: String): Double {
        return valor.replace(".", "")
            .replace(",", ".")
            .toDoubleOrNull() ?: 0.0
    }

    private fun String.limpar() = this.removeSurrounding("\"").trim()
}