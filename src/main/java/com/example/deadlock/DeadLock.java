package com.example.deadlock;

import java.util.logging.Logger;

public class DeadLock {
    private static final Logger logger = Logger.getLogger(DeadLock.class.getName());
    private static final Object resourceOne = new Object();
    private static final Object resourceTwo = new Object();


    public static void main(String[] args) {
        Thread threadOne = new Thread(() -> {
            synchronized (resourceOne){
                logger.info("Thread One locks resource 1");

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                synchronized (resourceTwo){
                    logger.info("Thread One accesses resource 2");
                }
            }
        });

        Thread threadTwo = new Thread(() -> {
            synchronized (resourceTwo){
                logger.info("Thread Two locks resource 2");

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                synchronized (resourceOne){
                    logger.info("Thread Two accesses resource 1");
                }
            }
        });

        threadOne.start();
        threadTwo.start();

        try{
            threadOne.join();
            threadTwo.join();
        }catch(InterruptedException e){
            throw new RuntimeException(e);
        }
    }
}