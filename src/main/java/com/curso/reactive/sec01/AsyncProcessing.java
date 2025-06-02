 package com.curso.reactive.sec01;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncProcessing {
	
	public static void main(String[] args) {
		ExecutorService executor = Executors.newFixedThreadPool(10);
		for (int i = 0; i < 10000; i++) {
			final int messageId = i;
			CompletableFuture.runAsync(() -> processMessage(messageId), executor);
		}
		System.out.println("All messages submitted for processing.");
	}

	private static void processMessage(int messageId) {
		try {
            Thread.sleep(10);
            System.out.println("Processed message: " + messageId);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
	}
}
