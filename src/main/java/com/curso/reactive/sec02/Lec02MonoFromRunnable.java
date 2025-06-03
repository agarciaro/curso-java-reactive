package com.curso.reactive.sec02;

import com.curso.reactive.common.Util;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class Lec02MonoFromRunnable {
	public static void main(String[] args) {
		
		getById(2);//.subscribe(Util.createSubscriber("usuario-1"));
		// Quiero estar aquí lo antes posible
		
//		String nombre = Mono.just("Pepe").block(); //KAKA
//		log.info("Nombre del usuario: {}", nombre);
		
		Mono.just("Pepe").subscribe(nombre -> log.info("Nombre del usuario: {}", nombre));
	
	}
	
	private static Mono<String> getById(int userId) {
		if(userId == 1) {
			return Mono.fromSupplier(() -> Util.faker().name().fullName());
		}
		return Mono.fromRunnable(() -> notifyBusiness(userId));
	}
	
	private static void notifyBusiness(int userId){
//		Util.sleepSeconds(3);
        log.info("notifying business on unavailable User {}", userId);
    }

}
