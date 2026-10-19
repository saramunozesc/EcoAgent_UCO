package demo;

import java.util.List;

@RestController
public final class TaskController {
    private final TaskService service;

    public TaskController(TaskService service) { this.service = service; }
    @GetMapping("/tasks")
    public List<Task> list() { return service.list(); }
    @PostMapping("/tasks")
    public Task create(String title, User owner) { return service.create(title, owner); }
}
