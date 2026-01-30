package com.bolt.desafio.Entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(schema = "energia", name = "usina")
data class Usina(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "ceg")
    val ceg: String,

    @Column(name = "nome_usina")
    val nome: String,

    @Column(name = "agente")
    val agente: String,

    @Column(name = "combustivel")
    val combustivel: String,

    @Column(name = "estado_uf")
    val uf: String,

    @Column(name = "potencia_kw")
    val potenciaKw: Double
) {
    constructor() : this( 0L, "", "", "", "", "", 0.0)
}