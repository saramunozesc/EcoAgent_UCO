package demo;

import java.util.List;

public final class TaskService {
    private final TaskRepository repository;

    public TaskService(TaskRepository repository) { this.repository = repository; }
    public Task create(String title, User owner) { return repository.save(new Task(title, owner)); }
    public List<Task> list() { return repository.findAll(); }
    public Task complete(Task task) { task.complete(); return task; }
}
