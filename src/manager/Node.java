package manager;

import tasks.Task;

public class Node {
    public Task task;
    public Node next;
    public Node prev;

    public Node(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return task;
    }
}
