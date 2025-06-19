package com.example.models;

import static com.example.models.Producer.queue;

public class Consumer implements Runnable{
    @Override
    public void run() {
        try {
            Task task = queue.take();
            System.out.println(Thread.currentThread().getName() + " started processing: " + task);

            Thread.sleep(1000);

            System.out.println(Thread.currentThread().getName() + " completed task: " + task.getName() + " at " + java.time.Instant.now());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println(Thread.currentThread().getName() + " was interrupted.");
        }
    }
}
