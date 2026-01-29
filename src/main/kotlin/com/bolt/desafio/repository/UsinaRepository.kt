package com.bolt.desafio.repository

import com.bolt.desafio.entity.Usina
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UsinaRepository : JpaRepository<Usina, String> {

    // Esta é a query que resolve o Requisito 3
    fun findTop5ByOrderByPotenciaKwDesc(): List<Usina>
}