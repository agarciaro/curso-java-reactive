package com.curso.reactive.sec04;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import com.curso.reactive.common.Util;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Slf4j
public class Lec04FluxThreadSafetyExamples {

    public static void main(String[] args) {
        log.info("=== Ejemplo 1: Un productor, múltiples suscriptores ===");
        demo1UnProductorMultiplesSuscriptores();
        
        Util.sleepSeconds(2);
        
        //log.info("\n=== Ejemplo 2: Múltiples productores, un suscriptor ===");
        //demo2MultipleProductoresUnSuscriptor();
        
        //Util.sleepSeconds(2);
        
        //log.info("\n=== Ejemplo 3: Múltiples productores, múltiples suscriptores ===");
        //demo3MultipleProductoresMultiplesSuscriptores();
    }

    /**
     * Ejemplo 1: Un productor que emite 1000 elementos, múltiples suscriptores (3 threads)
     * Cada suscriptor recibe todos los elementos
     */
    private static void demo1UnProductorMultiplesSuscriptores() {
        // Sink thread-safe para un solo productor
        Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();
        Flux<String> flux = sink.asFlux();
        
        // Listas thread-safe para cada suscriptor
        List<String> lista1 = new CopyOnWriteArrayList<>();
        List<String> lista2 = new CopyOnWriteArrayList<>();
        List<String> lista3 = new CopyOnWriteArrayList<>();
        
        CountDownLatch latch = new CountDownLatch(3);
        
        // Suscriptor 1
        flux.subscribe(
            item -> {
                lista1.add("Subs1-" + item);
                log.info("Suscriptor 1 recibió: {}", item);
            },
            error -> log.error("Error en suscriptor 1: {}", error.getMessage()),
            () -> {
                log.info("Suscriptor 1 completado. Total elementos: {}", lista1.size());
                latch.countDown();
            }
        );
        
        // Suscriptor 2
        flux.subscribe(
            item -> {
                lista2.add("Subs2-" + item);
                log.info("Suscriptor 2 recibió: {}", item);
            },
            error -> log.error("Error en suscriptor 2: {}", error.getMessage()),
            () -> {
                log.info("Suscriptor 2 completado. Total elementos: {}", lista2.size());
                latch.countDown();
            }
        );
        
        // Suscriptor 3
        flux.subscribe(
            item -> {
                lista3.add("Subs3-" + item);
                log.info("Suscriptor 3 recibió: {}", item);
            },
            error -> log.error("Error en suscriptor 3: {}", error.getMessage()),
            () -> {
                log.info("Suscriptor 3 completado. Total elementos: {}", lista3.size());
                latch.countDown();
            }
        );
        
        // Un solo productor emitiendo 1000 elementos
        Thread productor = new Thread(() -> {
            log.info("Productor iniciado");
            for (int i = 1; i <= 1000; i++) {
                sink.tryEmitNext("Item-" + i);
                if (i % 100 == 0) {
                    log.info("Productor emitió {} elementos", i);
                }
            }
            sink.tryEmitComplete();
            log.info("Productor completado");
        });
        
        productor.start();
        
        try {
            latch.await();
            log.info("=== RESULTADO EJEMPLO 1 ===");
            log.info("Lista 1 tamaño: {}", lista1.size());
            log.info("Lista 2 tamaño: {}", lista2.size());
            log.info("Lista 3 tamaño: {}", lista3.size());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Ejemplo 2: Múltiples productores (5 threads) emitiendo elementos, un suscriptor
     * Total de 5000 elementos (1000 por cada productor)
     */
    private static void demo2MultipleProductoresUnSuscriptor() {
        // Sink thread-safe para múltiples productores
        Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();
        Flux<String> flux = sink.asFlux();
        
        // Lista thread-safe para el único suscriptor
        List<String> listaSuscriptor = Collections.synchronizedList(new java.util.ArrayList<>());
        
        AtomicInteger productoresCompletados = new AtomicInteger(0);
        int totalProductores = 5;
        CountDownLatch latch = new CountDownLatch(1);
        
        // Un solo suscriptor
        flux.subscribe(
            item -> {
                listaSuscriptor.add(item);
                if (listaSuscriptor.size() % 500 == 0) {
                    log.info("Suscriptor ha recibido {} elementos", listaSuscriptor.size());
                }
            },
            error -> log.error("Error en suscriptor: {}", error.getMessage()),
            () -> {
                log.info("Suscriptor completado. Total elementos recibidos: {}", listaSuscriptor.size());
                latch.countDown();
            }
        );
        
        // Crear múltiples productores
        for (int producerIndex = 1; producerIndex <= totalProductores; producerIndex++) {
            final int producerId = producerIndex;
            Thread productor = new Thread(() -> {
                log.info("Productor {} iniciado", producerId);
                for (int i = 1; i <= 1000; i++) {
                    sink.tryEmitNext("Prod" + producerId + "-Item" + i);
                }
                
                int completados = productoresCompletados.incrementAndGet();
                log.info("Productor {} completado. Total completados: {}", producerId, completados);
                
                if (completados == totalProductores) {
                    sink.tryEmitComplete();
                    log.info("Todos los productores completados, sink cerrado");
                }
            });
            productor.start();
        }
        
        try {
            latch.await();
            log.info("=== RESULTADO EJEMPLO 2 ===");
            log.info("Lista suscriptor tamaño: {}", listaSuscriptor.size());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Ejemplo 3: Múltiples productores (3 threads) y múltiples suscriptores (4 threads)
     * Total de 3000 elementos emitidos, distribuidos entre 4 suscriptores
     */
    private static void demo3MultipleProductoresMultiplesSuscriptores() {
        // Sink thread-safe
        Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();
        Flux<String> flux = sink.asFlux();
        
        // Listas thread-safe para cada suscriptor
        List<String> listaSubs1 = new CopyOnWriteArrayList<>();
        List<String> listaSubs2 = new CopyOnWriteArrayList<>();
        List<String> listaSubs3 = new CopyOnWriteArrayList<>();
        List<String> listaSubs4 = new CopyOnWriteArrayList<>();
        
        AtomicInteger productoresCompletados = new AtomicInteger(0);
        int totalProductores = 3;
        CountDownLatch latch = new CountDownLatch(4); // 4 suscriptores
        
        // Suscriptor 1
        flux.subscribe(
            item -> {
                listaSubs1.add("S1-" + item);
                if (listaSubs1.size() % 200 == 0) {
                    log.info("Suscriptor 1: {} elementos", listaSubs1.size());
                }
            },
            error -> log.error("Error en suscriptor 1: {}", error.getMessage()),
            () -> {
                log.info("Suscriptor 1 completado. Total: {}", listaSubs1.size());
                latch.countDown();
            }
        );
        
        // Suscriptor 2
        flux.subscribe(
            item -> {
                listaSubs2.add("S2-" + item);
                if (listaSubs2.size() % 200 == 0) {
                    log.info("Suscriptor 2: {} elementos", listaSubs2.size());
                }
            },
            error -> log.error("Error en suscriptor 2: {}", error.getMessage()),
            () -> {
                log.info("Suscriptor 2 completado. Total: {}", listaSubs2.size());
                latch.countDown();
            }
        );
        
        // Suscriptor 3
        flux.subscribe(
            item -> {
                listaSubs3.add("S3-" + item);
                if (listaSubs3.size() % 200 == 0) {
                    log.info("Suscriptor 3: {} elementos", listaSubs3.size());
                }
            },
            error -> log.error("Error en suscriptor 3: {}", error.getMessage()),
            () -> {
                log.info("Suscriptor 3 completado. Total: {}", listaSubs3.size());
                latch.countDown();
            }
        );
        
        // Suscriptor 4
        flux.subscribe(
            item -> {
                listaSubs4.add("S4-" + item);
                if (listaSubs4.size() % 200 == 0) {
                    log.info("Suscriptor 4: {} elementos", listaSubs4.size());
                }
            },
            error -> log.error("Error en suscriptor 4: {}", error.getMessage()),
            () -> {
                log.info("Suscriptor 4 completado. Total: {}", listaSubs4.size());
                latch.countDown();
            }
        );
        
        // Crear múltiples productores
        for (int producerIndex = 1; producerIndex <= totalProductores; producerIndex++) {
            final int producerId = producerIndex;
            Thread productor = new Thread(() -> {
                log.info("Productor {} iniciado", producerId);
                for (int i = 1; i <= 1000; i++) {
                    sink.tryEmitNext("P" + producerId + "-Item" + i);
                }
                
                int completados = productoresCompletados.incrementAndGet();
                log.info("Productor {} completado. Total completados: {}", producerId, completados);
                
                if (completados == totalProductores) {
                    sink.tryEmitComplete();
                    log.info("Todos los productores completados, sink cerrado");
                }
            });
            productor.start();
        }
        
        try {
            latch.await();
            log.info("=== RESULTADO EJEMPLO 3 ===");
            log.info("Suscriptor 1 tamaño: {}", listaSubs1.size());
            log.info("Suscriptor 2 tamaño: {}", listaSubs2.size());
            log.info("Suscriptor 3 tamaño: {}", listaSubs3.size());
            log.info("Suscriptor 4 tamaño: {}", listaSubs4.size());
            int totalRecibido = listaSubs1.size() + listaSubs2.size() + 
                              listaSubs3.size() + listaSubs4.size();
            log.info("Total elementos recibidos por todos los suscriptores: {}", totalRecibido);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
} 