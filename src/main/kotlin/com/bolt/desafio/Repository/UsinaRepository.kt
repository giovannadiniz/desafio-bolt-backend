package com.bolt.desafio.Repository

import com.bolt.desafio.Entity.Usina
import com.bolt.desafio.dto.UsinaDTO
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UsinaRepository : JpaRepository<Usina, String> {

    fun findTop5ByOrderByPotenciaKwDesc(): List<UsinaDTO>
}