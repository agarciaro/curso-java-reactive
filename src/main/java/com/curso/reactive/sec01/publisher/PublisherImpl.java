package com.curso.reactive.sec01.publisher;

import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;

public class PublisherImpl implements Publisher<String> {

	@Override
	public void subscribe(Subscriber<? super String> subscriber) {
		var subscription = new SubscriptionImpl(subscriber);
		subscriber.onSubscribe(subscription);
	}

}
