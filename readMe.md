# Task Processor 🚀

![Java](https://img.shields.io/badge/Java-21-blue.svg)

A multithreaded Java application demonstrating core concurrency concepts including task production & consumption, thread monitoring, deadlock scenarios, and automated JSON export of task statuses.

# 📘 **Overview**

### ConcurQueueLab showcases:

🧵 Producer–Consumer pattern using PriorityBlockingQueue

🔁 Thread pool management & real-time monitoring

📊 Task lifecycle tracking with concurrent data structures

⚠️ Deadlock creation and prevention strategies

📤 Scheduled export of task status data to JSON

🧪 Custom metrics and thread pool analytics

## ✨ Features

✅ Concurrent Task Processing — Producers create tasks; Consumers process them in parallel

📌 Priority Task Queue — Tasks are ordered using PriorityBlockingQueue

📡 Thread Pool Monitoring — Real-time metrics via a dedicated logger thread

📈 Task State Tracking — Tracks lifecycle: SUBMITTED, PROCESSING, COMPLETED, FAILED

🗃️ Periodic JSON Export — Exports all task statuses every 1 minute

🔒 Deadlock Simulation & Resolution — Demonstrates how deadlocks occur and how to fix them

flowchart TD
Start([Start])
Init[Initialize Shared Task Queue]

    subgraph Producers
        P1[Start Producer 1]
        P2[Start Producer 2]
        Pn[...] 
        Gen1[Generate Task 1]
        Gen2[Generate Task 2]
        GenN[...]

        P1 --> Gen1 --> Add1[Add Task to Queue (BlockingQueue)]
        P2 --> Gen2 --> Add2[Add Task to Queue (BlockingQueue)]
        Pn --> GenN --> AddN[Add Task to Queue (BlockingQueue)]
    end

    Queue[(Shared Blocking Queue)]

    subgraph Consumers
        CT1[Take Task from Queue]
        Mark1[Mark Task as PROCESSING]
        Proc1[Process Task]
        Done1[Mark Task as COMPLETED]

        CT2[Take Task from Queue]
        Mark2[Mark Task as PROCESSING]
        Proc2[Process Task]
        Done2[Mark Task as COMPLETED]

        CTN[...] 

        CT1 --> Mark1 --> Proc1 --> Done1
        CT2 --> Mark2 --> Proc2 --> Done2
        CTN --> [...]

        Done1 --> PoisonCheck1{{POISON Task?}}
        PoisonCheck1 -->|Yes| Exit1([Break loop & Exit])
        PoisonCheck1 -->|No| CT1

        Done2 --> PoisonCheck2{{POISON Task?}}
        PoisonCheck2 -->|Yes| Exit2([Break loop & Exit])
        PoisonCheck2 -->|No| CT2
    end

    Export[Export Results to JSON (JsonExporter)]
    Shutdown[Stop Monitor + Shutdown Thread Pools]
    End([End])

    Start --> Init --> P1
    Init --> P2
    Init --> Pn
    Add1 --> Queue
    Add2 --> Queue
    AddN --> Queue

    Queue --> CT1
    Queue --> CT2
    Queue --> CTN

    Exit1 --> Export
    Exit2 --> Export
    Export --> Shutdown --> End


## 🛠️ Prerequisites

Java JDK 24 or newer

Maven (for building and running)

IDE like IntelliJ IDEA (recommended)

## 📦 Dependencies
SLF4J API v2.0.7 - used for logging messages in a uniform way.

Logback Classic v1.4.7 — SLF4J implementation

Gson v2.10.1 - used for converting Java objects to JSON and vice versa.



## Project Structure 🗂️

```
src/
└── main/
    ├── java/
    │   └── com/
    │       └── example/
    │           └── taskprocessor/
    │               ├── Main.java
    │
    │               ├── core/
    │               │   └── Task.java
    │
    │               ├── consumer/
    │               │   └── Consumer.java
    │
    │               ├── producer/
    │               │   └── Producer.java
    │
    │               ├── utils/
    │               │   └── JsonExporter.java
    │
    │               ├── enums/
    │               │   ├── TaskStatus
    │
    │               └── monitor/
    │                   └── Monitor.java
    │
    └── resources/
        └── (application.properties)

```


#  **Activity Diagram**

![activityDiagram.png](activityDiagram.png)

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/Boadu123/task_processor
   cd task_processor
   ```

2. Build the project:
   ```bash
   mvn clean package
   ```

3. Start-app Locally
    ```bash
   mvn exec:java -Dexec.mainClass="com.example.Main"
   ```
   
## ✅ Option 2: Using IntelliJ IDEA
1. **Open the project in IntelliJ**
2. **Ensure your Project SDK is set to Java 21+**
3. **Locate and run the main() method in ConcurQueueLab.java**

## ⚙️ Configuration

### Key configuration parameters in ConcurQueueLab.java:

numberOfTasks: Number of tasks per producer iteration

Producer threads: 3 threads

Consumer thread pool: 3 threads

Export interval: 1 minute

## Output

##### The application generates:

Console output: Real-time logging and final summary statistics

JSON files: Task status exports in the jsonExports/ directory

Thread monitoring logs: Performance metrics and thread pool status