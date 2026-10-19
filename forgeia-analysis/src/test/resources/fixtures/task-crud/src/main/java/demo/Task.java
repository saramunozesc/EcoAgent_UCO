package demo;

@Entity
public final class Task {
    private final String title;
    private boolean completed;
    @ManyToOne
    private final User owner;

    public Task(String title, User owner) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("title is required");
        }
        this.title = title;
        this.owner = owner;
    }

    public String getTitle() { return title; }
    public boolean isCompleted() { return completed; }
    public User getOwner() { return owner; }
    public void complete() { completed = true; }
}
