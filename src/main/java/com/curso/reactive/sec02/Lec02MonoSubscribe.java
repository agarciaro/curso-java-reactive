package com.curso.reactive.sec02;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class Lec02MonoSubscribe {
	public static void main(String[] args) {
		var mono = Mono.just(10000);
		
		mono.subscribe(
			i -> log.info("Valor recibido: {}", i),
			err -> log.error("Error recibido: {}", err.getMessage()),
			() -> log.info("Completado sin errores"),
			subscription -> subscription.request(1) //No tiene sentido, pero se puede hacer
		);
	
	}
}
