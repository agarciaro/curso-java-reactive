package com.curso.reactive.sec01;

import java.util.stream.Stream;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LazyStream {
	public static void main(String[] args) {
		Stream.of(10).peek(i -> log.info("Recibido: {}", i)).toList();
	}
}
