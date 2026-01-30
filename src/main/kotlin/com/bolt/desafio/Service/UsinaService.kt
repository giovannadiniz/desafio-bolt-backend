package com.bolt.desafio.Service

import com.bolt.desafio.Repository.UsinaRepository
import com.bolt.desafio.dto.UsinaDTO
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class UsinaService (private val repository: UsinaRepository) {

    companion object {
        private val log = LoggerFactory.getLogger(UsinaService::class.java)
    }

    fun buscarTop5Maiores(): List<UsinaDTO> {
        return try {
            repository.findTop5ByOrderByPotenciaKwDesc()
        } catch (e: Exception) {
            log.error("Erro ao buscar usinas: ${e.message}")
            emptyList()
        }
    }

}