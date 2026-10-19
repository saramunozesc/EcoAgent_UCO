package demo;

public final class Task {
    private final String title;
    private final Priority priority;

    public Task(String title, Priority priority) {
        this.title = title;
        this.priority = priority;
    }

    public String getTitle() { return title; }
    public Priority getPriority() { return priority; }
}
