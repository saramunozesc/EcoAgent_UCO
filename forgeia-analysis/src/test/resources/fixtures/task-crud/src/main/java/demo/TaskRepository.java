package demo;

import java.util.ArrayList;
import java.util.List;

public final class TaskRepository {
    private final List<Task> tasks = new ArrayList<Task>();

    public Task save(Task task) { tasks.add(task); return task; }
    public List<Task> findAll() { return new ArrayList<Task>(tasks); }
}
