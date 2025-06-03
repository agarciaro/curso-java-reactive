package com.curso.reactive.sec02;

import com.curso.reactive.sec01.subscriber.SubscriberImpl;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class Lec02MonoJust {
	public static void main(String[] args) {
		var mono = Mono.just("Pepe");
		var subscriber = new SubscriberImpl();
		mono.subscribe(subscriber);
		
		subscriber.getSubscription().request(10);
		subscriber.getSubscription().request(10);
		subscriber.getSubscription().cancel();
		subscriber.getSubscription().request(10);
	
	}
}
