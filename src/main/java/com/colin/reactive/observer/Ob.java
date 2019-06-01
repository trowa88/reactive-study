package com.colin.reactive.observer;

import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

public class Ob {
    //    public static void main(String[] args) {
//        Iterable<Integer> iter = () -> new Iterator<>() {
//            int i = 0;
//            final static int MAX = 10;
//
//            @Override
//            public boolean hasNext() {
//                return i < MAX;
//            }
//
//            @Override
//            public Integer next() {
//                return i++;
//            }
//        };
//
//        for (Integer i : iter) {
//            System.out.println(i);
//        }
//    }

    static class IntObservable extends Observable implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i <= 10; i++) {
                setChanged();
                notifyObservers(i);
            }
        }
    }

    public static void main(String[] args) {
        Observer observer = new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                System.out.println(arg);
            }
        };

        IntObservable intObservable = new IntObservable();
        intObservable.addObserver(observer);

        intObservable.run();
    }
}
