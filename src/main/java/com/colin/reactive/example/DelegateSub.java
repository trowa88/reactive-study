package com.colin.reactive.example;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class DelegateSub implements Subscriber<Integer> {
    Subscriber<? super Integer> sub;

    public DelegateSub(Subscriber<? super Integer> sub) {
        this.sub = sub;
    }

    @Override
    public void onSubscribe(Subscription s) {
         sub.onSubscribe(s);
    }

    public void onNext(Integer i) {
        sub.onNext(i);
    }

    @Override
    public void onError(Throwable t) {
        sub.onError(t);
    }

    @Override
    public void onComplete() {
        sub.onComplete();
    }
}
