package com.bolt.desafio.Controller

import com.bolt.desafio.Entity.Usina
import com.bolt.desafio.Repository.UsinaRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping( "/api/usinas")
class UsinaController (private val repository: UsinaRepository){

    @GetMapping("/maiores")
    fun listarMaiores(): List<Usina> = repository.findTop5ByOrderByPotenciaKwDesc()
}