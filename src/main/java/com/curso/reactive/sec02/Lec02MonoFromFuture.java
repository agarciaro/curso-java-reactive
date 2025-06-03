package com.curso.reactive.sec02;

import java.util.concurrent.CompletableFuture;

import com.curso.reactive.common.Util;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class Lec02MonoFromFuture {
	public static void main(String[] args) {
		
		Mono.fromFuture(Lec02MonoFromFuture::getName)
		    .subscribe(Util.createSubscriber());
		log.info("Continuando con el flujo principal");
		
		Util.sleepSeconds(3); // Esperar a que se complete el futuro antes de terminar el programa
	}
	
	private static CompletableFuture<String> getName() {
		return CompletableFuture.supplyAsync(
			() -> { 
				Util.sleepSeconds(2); // Simular una operación que toma tiempo
				log.info("Calculando nombre del usuario");
				return Util.faker().name().firstName();
			}
		);
	}

}
