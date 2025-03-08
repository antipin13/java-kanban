package manager;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private TaskManager manager = Managers.getDefault();

    @Test
    void createTasks() {
        Task task1 = new Task("Сделать уроки", "решить математику", Status.NEW);
        manager.createTask(task1);

        assertNotNull(manager.getTasks(), "Задача не добавлена");

        Task task3 = new Task("убраться в комнате", "протереть пыль", Status.NEW);
        manager.createTask(task3);

        assertEquals(2, manager.getTasks().size(), "Задача не добавлена");

        Subtask subtask1 = new Subtask("выбор машины", "просмотр харатекристик", Status.NEW);
        manager.createSubtask(subtask1);

        assertNotNull(manager.getSubtasks(), "Задача не добавлена");

        Subtask subtask3 = new Subtask("выбор автосалона", "просмотр акций", Status.NEW);
        manager.createSubtask(subtask3);

        assertEquals(2, manager.getSubtasks().size(), "Задача не добавлена");

        Epic epic1 = new Epic("покупка машины", "нужна машина");
        manager.createEpic(epic1);

        assertNotNull(manager.getEpics(), "Задача не добавлена");
    }

    @Test
    void searchTasksById() {
        Task task1 = new Task("Сделать уроки", "решить математику", Status.NEW);
        manager.createTask(task1);

        assertEquals(task1, manager.getTaskOfId(task1.getId()), "Задача не найдена");

        Subtask subtask1 = new Subtask("выбор машины", "просмотр харатекристик", Status.NEW);
        manager.createSubtask(subtask1);

        assertEquals(subtask1, manager.getSubtaskOfId(subtask1.getId()), "Задача не найдена");

        Epic epic1 = new Epic("покупка машины", "нужна машина");
        manager.createEpic(epic1);

        assertEquals(epic1, manager.getEpicOfId(epic1.getId()), "Задача не найдена");
    }

    @Test
    void immutabilityOfTask() {
        Task task1 = new Task("Сделать уроки", "решить математику", Status.NEW);
        manager.createTask(task1);

        Task task2 = manager.getTaskOfId(task1.getId());
        assertEquals(task1, task2, "Задачи не совпадают");

        Subtask subtask1 = new Subtask("выбор машины", "просмотр харатекристик", Status.NEW);
        manager.createSubtask(subtask1);

        Subtask subtask2 = manager.getSubtaskOfId(subtask1.getId());
        assertEquals(subtask1, subtask2, "Задачи не совпадают");

        Epic epic1 = new Epic("покупка машины", "нужна машина");
        manager.createEpic(epic1);

        Epic epic2 = manager.getEpicOfId(epic1.getId());
        assertEquals(epic1, epic2, "Задачи не совпадают");
    }

    @Test
    void updateTasks() {
        Task task1 = new Task("Сделать уроки", "решить математику", Status.NEW);
        manager.createTask(task1);

        Task task2 = new Task("Сделать уроки", "решить математику", Status.DONE);
        task2.setId(task1.getId());
        manager.updateTask(task2);

        assertEquals(Status.DONE, manager.getTaskOfId(task1.getId()).getStatus(), "Задача не обновилась");

        Subtask subtask1 = new Subtask("выбор машины", "просмотр харатекристик", Status.NEW);
        manager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("выбор машины", "просмотр харатекристик", Status.IN_PROGRESS);
        subtask2.setId(subtask1.getId());
        manager.updateSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, manager.getSubtaskOfId(subtask1.getId()).getStatus(), "Подзадача не обновилась");

        Epic epic1 = new Epic("покупка машины", "нужна машина");
        manager.createEpic(epic1);

        Epic epic2 = new Epic("покупка квартиры", "нужна квартира");
        epic2.setId(epic1.getId());
        manager.updateEpic(epic2);

        assertEquals("покупка квартиры", manager.getEpicOfId(epic1.getId()).getName(), "Эпик не обновился");
    }

    @Test
    void deleteTask() {
        Task task1 = new Task("Сделать уроки", "решить математику", Status.NEW);
        manager.createTask(task1);

        assertNotNull(manager.getTasks());

        manager.deleteTask(task1.getId());
        assertEquals(0, manager.getTasks().size());

        Subtask subtask1 = new Subtask("выбор машины", "просмотр харатекристик", Status.NEW);
        manager.createSubtask(subtask1);

        assertNotNull(manager.getSubtasks());

        manager.deleteSubtask(subtask1.getId());
        assertEquals(0, manager.getSubtasks().size());

        Epic epic1 = new Epic("покупка машины", "нужна машина");
        manager.createEpic(epic1);

        assertNotNull(manager.getEpics());

        manager.deleteEpic(epic1.getId());
        assertEquals(0, manager.getEpics().size());
    }

    @Test
    void deleteAllSubtaskInEpic() {
        Epic epic1 = new Epic("покупка машины", "нужна машина");
        manager.createEpic(epic1);

        Subtask subtask1 = new Subtask("выбор машины", "просмотр харатекристик", Status.NEW);
        manager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("выбор машины", "просмотр харатекристик", Status.IN_PROGRESS);
        manager.createSubtask(subtask2);

        epic1.addSubtaskInEpic(subtask1);
        epic1.addSubtaskInEpic(subtask2);

        assertEquals(2, epic1.getEpic().size(), "Подзадачи не добавлены в эпик");

        manager.deleteEpic(epic1.getId());

        assertNull(manager.getSubtaskOfId(subtask1.getId()), "Подзадача не удалилалсь вместе с эпиком");
    }
}