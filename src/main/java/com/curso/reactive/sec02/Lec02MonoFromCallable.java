package com.curso.reactive.sec02;

import java.util.List;

import com.curso.reactive.common.Util;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class Lec02MonoFromCallable {
	public static void main(String[] args) {
		
		var list = List.of(1, 2, 3, 4, 5);
		Mono.fromCallable(() -> sum(list))
		    .subscribe(Util.createSubscriber("sumador"));
	
	}

	private static int sum(List<Integer> list) throws Exception {
		log.info("Calculando suma de {}", list);
		if (list.isEmpty()) {
			throw new Exception("La lista no puede estar vacía");
		}
        return list.stream().mapToInt(Integer::intValue).sum();
	}

}
