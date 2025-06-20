import com.example.producer.Producer;

import com.example.core.Task;
import com.example.enums.TaskStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class ProducerTest {

    @BeforeEach
    void setUp() {
        Producer.queue.clear();
        Producer.taskStatusMap.clear();
    }

    @AfterEach
    void tearDown() {
        while (Producer.getSubmittedCount() > 0) {
            Producer.submittedCount.decrementAndGet();
        }
    }

    @Test
    void testProducerSubmitsTasksToQueue() throws InterruptedException {
        // Arrange
        Producer producer = new Producer("TestProducer", 2, true);
        Thread producerThread = new Thread(producer);

        // Act
        producerThread.start();
        producerThread.join(5000); // wait max 5s for task generation

        // Assert
        BlockingQueue<Task> queue = Producer.queue;

        assertEquals(2, queue.size(), "Expected 2 tasks in the queue");

        for (Task task : queue) {
            assertTrue(task.getName().startsWith("TestProducer-Task-"));
            assertEquals(TaskStatus.SUBMITTED, Producer.taskStatusMap.get(task.getId()));
        }

        assertEquals(2, Producer.getSubmittedCount(), "Expected submittedCount to be 2");
    }

    @Test
    void testProducerHandlesInterruptionGracefully() throws InterruptedException {
        // Arrange
        Producer producer = new Producer("InterruptTest", 10, false);
        Thread thread = new Thread(producer);

        // Act
        thread.start();
        Thread.sleep(100); // Let it start
        thread.interrupt(); // Simulate interruption
        thread.join(2000); // Wait for cleanup

        // Assert: Either no tasks or fewer than expected (since it was interrupted)
        assertTrue(Producer.getSubmittedCount() < 10, "Expected fewer tasks due to interruption");
    }
}
