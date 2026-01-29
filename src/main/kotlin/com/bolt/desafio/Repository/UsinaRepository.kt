package com.bolt.desafio.Repository

import com.bolt.desafio.Entity.Usina
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UsinaRepository : JpaRepository<Usina, String> {

    fun findTop5ByOrderByPotenciaKwDesc(): List<Usina>
}