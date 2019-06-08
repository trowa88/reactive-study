package com.colin.reactive.example;

import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class ReactiveStreamsPubSub {
    public static void main(String[] args) {
        Publisher<Integer> pub = iterPub(Stream.iterate(1, a -> a + 1)
                .limit(10)
                .collect(Collectors.toList()));
//        Publisher<Integer> mapPub = mapPub(pub, s -> s * 10);
//        Publisher<Integer> sumPub = sumPub(pub);
        Publisher<Integer> reducePub = reducePub(pub, 0, Integer::sum);
        reducePub.subscribe(logSub());
    }

    private static Publisher<Integer> reducePub(Publisher<Integer> pub,
                                                int init,
                                                BiFunction<Integer, Integer, Integer> biFunction) {
        return sub -> pub.subscribe(new DelegateSub(sub) {
            int result = init;

            @Override
            public void onNext(Integer i) {
                result = biFunction.apply(result, i);
            }

            @Override
            public void onComplete() {
                sub.onNext(result);
            }
        });
    }

    private static Publisher<Integer> sumPub(Publisher<Integer> pub) {
        return sub -> pub.subscribe(new DelegateSub(sub) {
            int sum = 0;

            @Override
            public void onNext(Integer i) {
                sum += i;
            }

            @Override
            public void onComplete() {
                sub.onNext(sum);
            }
        });
    }

    private static Publisher<Integer> mapPub(Publisher<Integer> pub, Function<Integer, Integer> f) {
        return sub -> pub.subscribe(new DelegateSub(sub) {
            @Override
            public void onNext(Integer i) {
                super.onNext(f.apply(i));
            }
        });
    }

    private static Subscriber<Integer> logSub() {
        return new Subscriber<>() {
            @Override
            public void onSubscribe(Subscription s) {
                log.debug("onSubscribe");
                s.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer integer) {
                log.debug("onNext: {}", integer);
            }

            @Override
            public void onError(Throwable t) {
                log.debug("onError: ", t);
            }

            @Override
            public void onComplete() {
                log.debug("onComplete");
            }
        };
    }

    private static Publisher<Integer> iterPub(Iterable<Integer> iter) {
        return sub -> sub.onSubscribe(new Subscription() {
            @Override
            public void request(long n) {
                try {
                    iter.forEach(sub::onNext);
                    sub.onComplete();
                } catch (Exception e) {
                    sub.onError(e);
                }
            }

            @Override
            public void cancel() {

            }
        });
    }
}
