package tasks;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private TaskManager manager = Managers.getDefault();

    @Test
    void addSubtaskInEpic() {
        Subtask subtask1 = new Subtask("Подзадача 1", "описание подзадачи 1", Status.NEW,
                LocalDateTime.of(2025, Month.FEBRUARY, 13, 23, 5),
                Duration.ofMinutes(50));
        manager.createSubtask(subtask1);

        Epic epic1 = new Epic("Эпик 1", "описание эпика 1");
        manager.createEpic(epic1);
        manager.addSubtaskInEpic(epic1, subtask1);

        assertEquals(1, epic1.getEpic().size(), "Подзадача не добавлена");
    }

    @Test
    void recalculatingStatus() {
        Subtask subtask1 = new Subtask("Подзадача 1", "описание подзадачи 1", Status.NEW,
                LocalDateTime.of(2025, Month.FEBRUARY, 13, 23, 5),
                Duration.ofMinutes(50));
        manager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("Подзадача 2", "описание подзадачи 2", Status.NEW,
                LocalDateTime.of(2025, Month.FEBRUARY, 14, 0, 0), Duration.ofMinutes(40));
        manager.createSubtask(subtask2);

        Epic epic1 = new Epic("Эпик 1", "описание эпика 1");
        manager.createEpic(epic1);

        manager.addSubtaskInEpic(epic1, subtask1);
        manager.addSubtaskInEpic(epic1, subtask2);

        assertEquals(Status.NEW, epic1.getStatus(), "Статус должен быть NEW");

        Subtask subtask3 = new Subtask("Подзадача 1", "описание подзадачи 1", Status.DONE,
                LocalDateTime.of(2025, Month.FEBRUARY, 13, 23, 5),
                Duration.ofMinutes(50));
        subtask3.setId(subtask1.getId());
        manager.updateSubtask(subtask3);

        assertEquals(Status.IN_PROGRESS, epic1.getStatus(), "Статус должен быть IN_PROGRESS");

        Subtask subtask4 = new Subtask("Подзадача 2", "описание подзадачи 2", Status.DONE,
                LocalDateTime.of(2025, Month.FEBRUARY, 14, 0, 0), Duration.ofMinutes(40));
        subtask4.setId(subtask2.getId());
        manager.updateSubtask(subtask4);

        assertEquals(Status.DONE, epic1.getStatus(), "Статус должен быть DONE");

        Subtask subtask5 = new Subtask("Подзадача 1", "описание подзадачи 1", Status.IN_PROGRESS,
                LocalDateTime.of(2025, Month.FEBRUARY, 13, 23, 5),
                Duration.ofMinutes(50));
        subtask5.setId(subtask1.getId());
        manager.updateSubtask(subtask5);

        Subtask subtask6 = new Subtask("Подзадача 2", "описание подзадачи 2", Status.DONE,
                LocalDateTime.of(2025, Month.FEBRUARY, 14, 0, 0), Duration.ofMinutes(40));
        subtask6.setId(subtask2.getId());
        manager.updateSubtask(subtask6);

        assertEquals(Status.IN_PROGRESS, epic1.getStatus(), "Статус должен быть IN_PROGRESS");
    }
}
