package manager;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    private TaskManager manager = Managers.getDefault();

    @Test
    void createTasks() {
        Task task1 = new Task("Задача 1", "описание 1", Status.NEW, LocalDateTime.of(2025,
                Month.JANUARY, 1,0,0), Duration.ofMinutes(60));
        manager.createTask(task1);

        assertNotNull(manager.getTasks(), "Задача 1 не добавлена");

        Task task2 = new Task("Задача 2", "описание 2", Status.NEW, LocalDateTime.of(2025,
                Month.JANUARY, 1,0,30), Duration.ofMinutes(60));
        manager.createTask(task2);

        assertEquals(1, manager.getTasks().size(), "Задача 2 добавлена с пересекающимся временем");

        Subtask subtask1 = new Subtask("Подзадача 1", "описание подзадачи 1", Status.NEW,
                LocalDateTime.of(2025, Month.FEBRUARY, 13,23,5), Duration.ofMinutes(50));
        manager.createSubtask(subtask1);

        assertNotNull(manager.getSubtasks(), "Подзадача 1 не добавлена");

        Subtask subtask2 = new Subtask("Подзадача 2", "описание подзадачи 2", Status.NEW,
                LocalDateTime.of(2025, Month.FEBRUARY, 14,0,0), Duration.ofMinutes(40));
        manager.createSubtask(subtask2);

        assertEquals(2, manager.getSubtasks().size(), "Подзадача 2 не добавлена");

        Epic epic1 = new Epic("Эпик 1", "описание эпика 1");
        manager.createEpic(epic1);

        assertNotNull(manager.getEpics(), "Эпик 1 не добавлен");
    }

    @Test
    void getPrioritizedTasks() {
        Task task1 = new Task("Задача 1", "описание 1", Status.NEW, LocalDateTime.of(2025,
                Month.JANUARY, 1,0,0), Duration.ofMinutes(60));
        manager.createTask(task1);

        assertNotNull(manager.getTasks(), "Задача 1 не добавлена");

        Task task2 = new Task("Задача 2", "описание 2", Status.NEW, LocalDateTime.of(2025,
                Month.JUNE, 16,15,0), Duration.ofMinutes(320));
        manager.createTask(task2);

        Subtask subtask1 = new Subtask("Подзадача 1", "описание подзадачи 1", Status.NEW,
                LocalDateTime.of(2025, Month.FEBRUARY, 13,23,5), Duration.ofMinutes(50));
        manager.createSubtask(subtask1);

        TreeSet<Task> prioritizedTasks = manager.getPrioritizedTasks();

        assertEquals(task1, prioritizedTasks.getFirst(), "Неверная сортировка задач");
    }

    @Test
    void intersectionAnyTasks() {
        Task task1 = new Task("Задача 1", "описание 1", Status.NEW, LocalDateTime.of(2025,
                Month.JANUARY, 1,0,0), Duration.ofMinutes(60));
        manager.createTask(task1);

        assertNotNull(manager.getTasks(), "Задача 1 не добавлена");

        Task task2 = new Task("Задача 2", "описание 2", Status.NEW, LocalDateTime.of(2025,
                Month.JUNE, 16,15,0), Duration.ofMinutes(320));
        manager.createTask(task2);

        Subtask subtask1 = new Subtask("Подзадача 1", "описание подзадачи 1", Status.NEW,
                LocalDateTime.of(2025, Month.FEBRUARY, 13,23,5), Duration.ofMinutes(50));
        manager.createSubtask(subtask1);

        assertFalse(manager.intersectionAnyTasks(task2), "Задача 2 не пересекается по времени с другими");
    }

}