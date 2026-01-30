package com.bolt.desafio.Controller

import com.bolt.desafio.Service.UsinaService
import com.bolt.desafio.dto.UsinaDTO
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping( "/api/usinas")
class UsinaController (private val service: UsinaService){

    @GetMapping("/maiores")
    fun listarMaiores(): List<UsinaDTO> = service.buscarTop5Maiores()
}