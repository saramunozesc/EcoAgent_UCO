package demo;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public final class TaskTest {
    @Test
    public void preservesPriority() {
        Task task = new Task("Preparar demo", Priority.HIGH);
        assertEquals(Priority.HIGH, task.getPriority());
    }
}
