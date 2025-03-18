package tasks;

import java.util.HashMap;

public class Epic extends Task {
    HashMap<Integer, Subtask> epic;

    public Epic(String name, String description) {
        super(name, description);
        epic = new HashMap<>();
    }

    @Override
    public Status getStatus() {
        if (epic.isEmpty()) {
            status = Status.NEW;
            return status;
        }

        boolean allStatusIsNew = true;
        boolean allStatusIsDone = true;

        for (Subtask subtask : epic.values()) {
            if (!(subtask.getStatus().equals(Status.NEW))) {
                allStatusIsNew = false;
            }
            if (!(subtask.getStatus().equals(Status.DONE))) {
                allStatusIsDone = false;
            }
        }

        if (allStatusIsDone) {
            status = Status.DONE;
            return status;
        } else if (allStatusIsNew) {
            status = Status.NEW;
            return status;
        } else {
            status = Status.IN_PROGRESS;
            return status;
        }
    }

    public HashMap<Integer, Subtask> getEpic() {
        return epic;
    }

    public void addSubtaskInEpic(Subtask subtask) {
        epic.put(subtask.getId(), subtask);
    }
}