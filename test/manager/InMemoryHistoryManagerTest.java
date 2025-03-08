package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;
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
        task1 = new Task("Сделать уроки", "решить математику", Status.NEW);
        task2 = new Task("убраться в комнате", "протереть пыль", Status.NEW);
    }

    @Test
    void addTask() {
        manager.createTask(task1);
        historyManager.add(task1);
        List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История просмотров пустая");

        manager.createTask(task2);
        historyManager.add(task2);
        historyManager.getHistory();

        assertEquals(2, history.size(), "task2 не добавлена в историю просмотров");

        historyManager.add(task1);
        historyManager.getHistory();

        assertEquals(2, history.size(), "Количество задач в истории не изменилось");
    }

    @Test
    void getHistory() {
        assertTrue(historyManager.getHistory().isEmpty(), "История не пустая");

        Task task3 = new Task("Сделать уроки", "решить математику", Status.DONE);

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
        Task task3 = new Task("Сделать уроки", "решить математику", Status.DONE);

        manager.createTask(task1);
        historyManager.add(task1);

        manager.createTask(task2);
        historyManager.add(task2);

        manager.createTask(task3);
        historyManager.add(task3);

        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size(), "В истории не 3 задачи");

        historyManager.remove(task2.getId());
        history = historyManager.getHistory();

        assertEquals(2, history.size(), "Задача не удалена");
        assertFalse(history.contains(task2), "Удалена не та задача");
    }
}