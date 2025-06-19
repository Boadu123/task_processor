package com.example;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.models.Consumer;
import com.example.models.Producer;
import com.example.models.Task;

import java.util.concurrent.PriorityBlockingQueue;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        Producer producer = new Producer("Washer", 2, true);
        Thread t1 = new Thread(producer);

        Producer producer1 = new Producer("Machine", 2, false);
        Thread t2 = new Thread(producer1);

        ExecutorService consumerPool = Executors.newFixedThreadPool(2);

        consumerPool.submit(new Consumer());
        consumerPool.submit(new Consumer());

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        consumerPool.shutdown();
    }
}

