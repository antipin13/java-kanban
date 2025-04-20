package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;

public class Epic extends Task {
    HashMap<Integer, Subtask> epic;

    public Epic(String name, String description) {
        super(name, description);
    }

    @Override
    public Status getStatus() {
        if (epic == null || epic.isEmpty()) {
            return Status.NEW;
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
        if (epic == null) {
            epic = new HashMap<>();
        }
        return epic;
    }

    public LocalDateTime getStartTime() {
        if (epic == null) {
            return null;
        }
        startTime = epic.values().stream()
                .map(Subtask::getStartTime)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        return startTime;
    }

    public Duration getDuration() {
        if (epic == null || epic.isEmpty()) {
            return Duration.ZERO;
        }

        this.duration = epic.values().stream()
                .map(Subtask::getDuration)
                .reduce(Duration.ZERO, Duration::plus);

        return this.duration;
    }

    @Override
    public LocalDateTime getEndTime() {
        if (startTime == null) {
            return null;
        }
        return startTime.plus(duration);
    }
}