package com.annalisetarhan.kacaserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class KaçaOlurServerApplication

fun main(args: Array<String>) {
	runApplication<KaçaOlurServerApplication>(*args)
}

@RestController
class MainController{
	@GetMapping
	fun index()= "Hello World"
}