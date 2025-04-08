package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private TaskManager manager;
    private HistoryManager historyManager;
    private Task task1;
    private Task task2;

    @BeforeEach
    void beforeEach() {
        manager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
        task1 = new Task("Задача 1", "описание 1", Status.NEW, LocalDateTime.of(2025
                , Month.JANUARY, 1, 0, 0), Duration.ofMinutes(60));
        task2 = new Task("Задача 2", "описание 2", Status.NEW, LocalDateTime.of(2025
                , Month.JUNE, 16, 15, 0), Duration.ofMinutes(320));
    }

    @Test
    void addTask() {
        manager.createTask(task1);
        historyManager.add(task1);
        List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История просмотров пустая");

        manager.createTask(task2);
        historyManager.add(task2);
        history = historyManager.getHistory();

        assertEquals(2, history.size(), "task2 не добавлена в историю просмотров");

        historyManager.add(task1);
        historyManager.getHistory();

        assertEquals(2, history.size(), "Количество задач в истории не изменилось");
    }

    @Test
    void getHistory() {
        assertTrue(historyManager.getHistory().isEmpty(), "История не пустая");

        Task task3 = new Task("Задача 3", "описание 3", Status.NEW, LocalDateTime.of(2025
                , Month.JUNE, 13, 15, 0), Duration.ofMinutes(360));

        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        List<Task> history = historyManager.getHistory();

        assertEquals(task3, history.get(2), "Третья задача не в конце");
    }

    @Test
    void remove() {
        Task task3 = new Task("Задача 3", "описание 3", Status.NEW, LocalDateTime.of(2025
                , Month.JUNE, 13, 15, 0), Duration.ofMinutes(360));

        manager.createTask(task1);
        historyManager.add(task1);

        manager.createTask(task2);
        historyManager.add(task2);

        manager.createTask(task3);
        historyManager.add(task3);
        historyManager.add(task3);

        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size(), "Задача 3 не переписалась в истории");

        historyManager.remove(task2.getId());
        history = historyManager.getHistory();

        assertEquals(2, history.size(), "Задача с ID 2 не удалена из середины");
        assertFalse(history.contains(task2), "Удалена задача с ID не равным 2");

        historyManager.remove(history.get(history.size() - 1).getId());
        history = historyManager.getHistory();

        assertEquals(1, history.size(), "Задача не удалена с конца");
    }
}
