package com.colin.reactive.example;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

public class PubSub {
    public static void main(String[] args) {
        Iterable<Integer> itr = List.of(1, 2, 3, 4, 5);
        ExecutorService es = Executors.newSingleThreadExecutor();

        Publisher<Integer> p = subscriber -> {
            Iterator<Integer> it = itr.iterator();

            subscriber.onSubscribe(new Subscription() {
                @Override
                public void request(long n) {
                    es.execute(() -> {
                        int i = 0;
                        try {
                            while (i++ < n) {
                                if (it.hasNext()) {
                                    subscriber.onNext(it.next());
                                } else {
                                    subscriber.onComplete();
                                    break;
                                }
                            }
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    });
                }

                @Override
                public void cancel() {

                }
            });
        };

        Subscriber<Integer> s = new Subscriber<>() {
            Subscription subscription;
            @Override
            public void onSubscribe(Subscription subscription) {
                System.out.println("onSubscribe");
                this.subscription = subscription;
                this.subscription.request(1);
            }

            @Override
            public void onNext(Integer item) {
                System.out.println("onNext: " + item);
                this.subscription.request(1);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("onError: " + throwable.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
                es.shutdown();
            }
        };

        p.subscribe(s);
    }
}
