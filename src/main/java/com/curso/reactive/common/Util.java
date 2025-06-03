package com.curso.reactive.common;

import org.reactivestreams.Subscriber;

import com.github.javafaker.Faker;

public class Util {
	
	private static final Faker faker = Faker.instance();
	
	public static <T> Subscriber<T> createSubscriber(){
        return new DefaultSubscriber<>("Default");
    }

    public static <T> Subscriber<T> createSubscriber(String name){
        return new DefaultSubscriber<>(name);
    }

    public static Faker faker(){
        return faker;
    }
    
    public static void sleepSeconds(int seconds){
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
