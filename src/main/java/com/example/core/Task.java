package com.example.core;

import java.time.Instant;
import java.util.UUID;

public class Task implements Comparable<Task> {
    private final UUID id;
    private final String name;
    private final int priority;
    private final Instant createdTimestamp;
    private final String payload;

    private Task(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID();
        this.name = builder.name;
        this.priority = builder.priority;
        this.createdTimestamp = builder.createdTimestamp != null ? builder.createdTimestamp : Instant.now();
        this.payload = builder.payload;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public int getPriority() { return priority; }
    public Instant getCreatedTimestamp() { return createdTimestamp; }
    public String getPayload() { return payload; }

    public boolean isPoisonPill() {
        return this.name != null && this.name.equals("POISON");
    }

    @Override
    public int compareTo(Task other) {
        return Integer.compare(this.priority, other.priority);
    }

    @Override
    public String toString() {
        return String.format("Task{id=%s, name=%s, priority=%d, time=%s}",
                id, name, priority, createdTimestamp);
    }

    public static class Builder {
        private UUID id;
        private String name;
        private int priority;
        private Instant createdTimestamp;
        private String payload;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder priority(int priority) {
            this.priority = priority;
            return this;
        }

        public Builder payload(String payload) {
            this.payload = payload;
            return this;
        }

        public Builder createdTimestamp(Instant createdTimestamp) {
            this.createdTimestamp = createdTimestamp;
            return this;
        }

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Task build() {
            return new Task(this);
        }
    }
}

