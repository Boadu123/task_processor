import com.example.consumer.Consumer;
import com.example.enums.TaskStatus;
import com.example.producer.Producer;
import org.junit.jupiter.api.*;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ConsumerTest {

    @BeforeEach
    void setUp() {
        Producer.queue.clear();
        Producer.taskStatusMap.clear();
        Consumer.processingTimestamps.clear();
        resetCompletedCount();
        resetSubmittedCount();
    }

    @AfterEach
    void tearDown() {
        Producer.queue.clear();
        Producer.taskStatusMap.clear();
        Consumer.processingTimestamps.clear();
        resetCompletedCount();
        resetSubmittedCount();
    }

    @Test
    void testConsumerProcessesTasksAndHandlesPoisonPill() throws InterruptedException {
        // Arrange
        Producer producer = new Producer("TestProducer", 2, false);
        Thread producerThread = new Thread(producer);

        Consumer consumer = new Consumer();
        Thread consumerThread = new Thread(consumer);

        // Act
        producerThread.start();
        consumerThread.start();

        producerThread.join(); // wait for all tasks to be produced

        consumerThread.join(5000); // wait for processing and poison pill

        // Assert
        assertEquals(2, Consumer.getCompletedCount(), "Expected 2 tasks to be completed");

        for (UUID taskId : Consumer.processingTimestamps.keySet()) {
            assertEquals(TaskStatus.COMPLETED, Producer.taskStatusMap.get(taskId));
        }

        assertTrue(Consumer.processingTimestamps.size() == 2, "Expected 2 tasks to have processing timestamps");
    }

    // Helper methods to reset static counters
    private void resetCompletedCount() {
        try {
            var field = Consumer.class.getDeclaredField("completedCount");
            field.setAccessible(true);
            ((java.util.concurrent.atomic.AtomicInteger) field.get(null)).set(0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void resetSubmittedCount() {
        try {
            var field = Producer.class.getDeclaredField("submittedCount");
            field.setAccessible(true);
            ((java.util.concurrent.atomic.AtomicInteger) field.get(null)).set(0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
