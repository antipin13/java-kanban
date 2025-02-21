package manager;

import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private TaskManager manager = Managers.getDefault();
    private HistoryManager historyManager = Managers.getDefaultHistory();

    @Test
    void addTaskInHistory() {
        Task task1 = new Task("Сделать уроки", "решить математику", Status.NEW);
        manager.createTask(task1);
        historyManager.add(task1);
        List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История просмотров пустая");
    }

    @Test
    void historyManagerStoresOnly10Tasks() {
        Task task1 = new Task("Сделать уроки", "решить математику", Status.NEW);
        manager.createTask(task1);
        for (int i = 0; i < 12; i++) {
            historyManager.add(task1);
        }

        List<Task> history = historyManager.getHistory();
        assertEquals(10, history.size(), "Перебор задач");
    }

    @Test
    void historyManagerDeleteFirstTask() {
        Task task1 = new Task("Сделать уроки", "решить математику", Status.NEW);
        manager.createTask(task1);

        Task task2 = new Task("убраться в комнате", "протереть пыль", Status.NEW);
        manager.createTask(task2);

        historyManager.add(task1);
        for (int i = 0; i < 10; i++) {
            historyManager.add(task2);
        }

        List<Task> history = historyManager.getHistory();
        assertEquals(task2, history.get(0), "Задачи удаляются не с первой добавленной");
    }

    @Test
    void historyManagerStorePreviosVersionOfTask() {
        Task task1 = new Task("Сделать уроки", "решить математику", Status.NEW);
        manager.createTask(task1);
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();

        Task task2 = new Task("убраться в комнате", "протереть пыль", Status.NEW);
        task2.setId(1);
        manager.updateTask(task2);

        assertTrue(task1.equals(history.get(0)), "Задача изменилась");
    }

}