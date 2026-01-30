package com.bolt.desafio.Entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(schema = "energia", name = "job")
class Job (
    @Id
    @Column(name = "job_name")
    val jobName: String,

    @Column(name = "ultima_linha_processada")
    var ultimaLinhaProcessada: Long = 0,

    @Column(name = "data_ultima_execucao")
    var dataUltimaExecucao: LocalDateTime = LocalDateTime.now()
) {
    constructor() : this("", 0, LocalDateTime.now())
}