package com.bolt.desafio

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class DesafioApplication

fun main(args: Array<String>) {
	runApplication<DesafioApplication>(*args)

	println("Aplicação iniciada com sucesso!")
}
