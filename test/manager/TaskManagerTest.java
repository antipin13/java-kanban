package manager;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    private TaskManager manager = Managers.getDefault();

    @Test
    void createTasks() {
        Task task1 = new Task("Задача 1", "описание 1", Status.NEW, LocalDateTime.of(2025
                , Month.JANUARY, 1,0,0), Duration.ofMinutes(60));
        manager.createTask(task1);

        assertNotNull(manager.getTasks(), "Задача 1 не добавлена");

        Task task2 = new Task("Задача 2", "описание 2", Status.NEW, LocalDateTime.of(2025
                , Month.JUNE, 16,15,0), Duration.ofMinutes(320));
        manager.createTask(task2);

        assertEquals(2, manager.getTasks().size(), "Задача 2 не добавлена");

        Subtask subtask1 = new Subtask("Подзадача 1", "описание подзадачи 1", Status.NEW
                , LocalDateTime.of(2025, Month.FEBRUARY, 13,23,5)
                , Duration.ofMinutes(50));
        manager.createSubtask(subtask1);

        assertNotNull(manager.getSubtasks(), "Подзадача 1 не добавлена");

        Subtask subtask2 = new Subtask("Подзадача 2", "описание подзадачи 2", Status.NEW
                , LocalDateTime.of(2025, Month.FEBRUARY, 14,0,0), Duration.ofMinutes(40));
        manager.createSubtask(subtask2);

        assertEquals(2, manager.getSubtasks().size(), "Подзадача 2 не добавлена");

        Epic epic1 = new Epic("Эпик 1", "описание эпика 1");
        manager.createEpic(epic1);

        assertNotNull(manager.getEpics(), "Эпик 1 не добавлен");
    }

    @Test
    void searchTasksById() {
        Task task1 = new Task("Задача 1", "описание 1", Status.NEW, LocalDateTime.of(2025
                , Month.JANUARY, 1,0,0), Duration.ofMinutes(60));
        manager.createTask(task1);

        assertEquals(task1, manager.getTaskOfId(task1.getId()), "Задача 1 не найдена");

        Subtask subtask1 = new Subtask("Подзадача 1", "описание подзадачи 1", Status.NEW
                , LocalDateTime.of(2025, Month.FEBRUARY, 13,23,5)
                , Duration.ofMinutes(50));
        manager.createSubtask(subtask1);

        assertEquals(subtask1, manager.getSubtaskOfId(subtask1.getId()), "Подзадача 1 не найдена");

        Epic epic1 = new Epic("Эпик 1", "описание эпика 1");
        manager.createEpic(epic1);

        assertEquals(epic1, manager.getEpicOfId(epic1.getId()), "Эпик 1 не найден");
    }

    @Test
    void immutabilityOfTask() {
        Task task1 = new Task("Задача 1", "описание 1", Status.NEW, LocalDateTime.of(2025
                , Month.JANUARY, 1,0,0), Duration.ofMinutes(60));
        manager.createTask(task1);

        Task task2 = manager.getTaskOfId(task1.getId());
        assertEquals(task1, task2, "Задачи не совпадают");

        Subtask subtask1 = new Subtask("Подзадача 1", "описание подзадачи 1", Status.NEW
                , LocalDateTime.of(2025, Month.FEBRUARY, 13,23,5)
                , Duration.ofMinutes(50));
        manager.createSubtask(subtask1);

        Subtask subtask2 = manager.getSubtaskOfId(subtask1.getId());
        assertEquals(subtask1, subtask2, "Подзадачи не совпадают");

        Epic epic1 = new Epic("Эпик 1", "описание эпика 1");
        manager.createEpic(epic1);

        Epic epic2 = manager.getEpicOfId(epic1.getId());
        assertEquals(epic1, epic2, "Эпики не совпадают");
    }

    @Test
    void updateTasks() {
        Task task1 = new Task("Задача 1", "описание 1", Status.NEW, LocalDateTime.of(2025
                , Month.JANUARY, 1,0,0), Duration.ofMinutes(60));
        manager.createTask(task1);

        Task task2 = new Task("Задача 1", "описание 1", Status.DONE
                , LocalDateTime.of(2025, Month.JANUARY, 1,0,0), Duration.ofMinutes(60));
        task2.setId(task1.getId());
        manager.updateTask(task2);

        assertEquals(Status.DONE, manager.getTaskOfId(task1.getId()).getStatus(), "Задача 1 не обновилась");

        Subtask subtask1 = new Subtask("Подзадача 1", "описание подзадачи 1", Status.NEW
                , LocalDateTime.of(2025, Month.FEBRUARY, 13,23,5)
                , Duration.ofMinutes(50));
        manager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("Подзадача 1", "описание подзадачи 1", Status.IN_PROGRESS
                , LocalDateTime.of(2025, Month.FEBRUARY, 13,23,5)
                , Duration.ofMinutes(50));
        subtask2.setId(subtask1.getId());
        manager.updateSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, manager.getSubtaskOfId(subtask1.getId()).getStatus()
                ,"Подзадача не обновилась");

        Epic epic1 = new Epic("Эпик 1", "описание эпика 1");
        manager.createEpic(epic1);

        Epic epic2 = new Epic("Эпик 1", "измененное описание эпика 1");
        epic2.setId(epic1.getId());
        manager.updateEpic(epic2);

        assertEquals("измененное описание эпика 1", manager.getEpicOfId(epic1.getId()).getDescription()
                , "Эпик не обновился");
    }

    @Test
    void deleteTask() {
        Task task1 = new Task("Задача 1", "описание 1", Status.NEW, LocalDateTime.of(2025
                , Month.JANUARY, 1,0,0), Duration.ofMinutes(60));
        manager.createTask(task1);

        assertNotNull(manager.getTasks());

        manager.deleteTask(task1.getId());
        assertEquals(0, manager.getTasks().size());

        Subtask subtask1 = new Subtask("Подзадача 1", "описание подзадачи 1", Status.NEW
                , LocalDateTime.of(2025, Month.FEBRUARY, 13,23,5)
                , Duration.ofMinutes(50));
        manager.createSubtask(subtask1);

        assertNotNull(manager.getSubtasks());

        manager.deleteSubtask(subtask1.getId());
        assertEquals(0, manager.getSubtasks().size());

        Epic epic1 = new Epic("Эпик 1", "описание эпика 1");
        manager.createEpic(epic1);

        assertNotNull(manager.getEpics());

        manager.deleteEpic(epic1.getId());
        assertEquals(0, manager.getEpics().size());
    }

    @Test
    void deleteAllSubtaskInEpic() {
        Epic epic1 = new Epic("Эпик 1", "описание эпика 1");
        manager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача 1", "описание подзадачи 1", Status.NEW
                , LocalDateTime.of(2025, Month.FEBRUARY, 13,23,5)
                , Duration.ofMinutes(50));
        manager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("Подзадача 2", "описание подзадачи 2", Status.NEW
                , LocalDateTime.of(2025, Month.FEBRUARY, 14,0,0), Duration.ofMinutes(40));
        manager.createSubtask(subtask2);

        epic1.addSubtaskInEpic(epic1, subtask1);
        epic1.addSubtaskInEpic(epic1, subtask2);

        assertEquals(2, epic1.getEpic().size(), "Подзадачи не добавлены в эпик");

        manager.deleteEpic(epic1.getId());

        assertNull(manager.getSubtaskOfId(subtask1.getId()), "Подзадача не удалилалсь вместе с эпиком");
    }

    @Test
    void getEndTimeTasks() {
        Task task1 = new Task("Задача 1", "описание 1", Status.NEW, LocalDateTime.of(2025
                , Month.JANUARY, 1,0,0), Duration.ofMinutes(60));
        manager.createTask(task1);

        assertEquals(LocalDateTime.of(2025
                        , Month.JANUARY, 1,1,0), task1.getEndTime()
                , "Неккоректное время для задачи 1");

        Subtask subtask1 = new Subtask("Подзадача 1", "описание подзадачи 1", Status.NEW
                , LocalDateTime.of(2025, Month.FEBRUARY, 13,23,5)
                , Duration.ofMinutes(50));
        manager.createSubtask(subtask1);

        assertEquals(LocalDateTime.of(2025, Month.FEBRUARY, 13,23,55)
                , subtask1.getEndTime(), "Неккоректное время для задачи 1");
    }

    @Test
    void calculationStartTimeInEpic() {
        Epic epic1 = new Epic("Эпик 1", "описание эпика 1");
        manager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача 1", "описание подзадачи 1", Status.NEW
                , LocalDateTime.of(2025, Month.FEBRUARY, 13,23,5)
                , Duration.ofMinutes(50));
        manager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("Подзадача 2", "описание подзадачи 2", Status.NEW
                , LocalDateTime.of(2025, Month.FEBRUARY, 14,0,0), Duration.ofMinutes(40));
        manager.createSubtask(subtask2);

        epic1.addSubtaskInEpic(epic1, subtask1);
        epic1.addSubtaskInEpic(epic1, subtask2);

        assertEquals(LocalDateTime.of(2025, Month.FEBRUARY, 13,23,5), epic1.getStartTime()
                , "Неверное время начала Эпика 1");
    }

    @Test
    void calculationDurationInEpic() {
        Epic epic1 = new Epic("Эпик 1", "описание эпика 1");
        manager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача 1", "описание подзадачи 1", Status.NEW
                , LocalDateTime.of(2025, Month.FEBRUARY, 13,23,5)
                , Duration.ofMinutes(50));
        manager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("Подзадача 2", "описание подзадачи 2", Status.NEW
                , LocalDateTime.of(2025, Month.FEBRUARY, 14,0,0), Duration.ofMinutes(40));
        manager.createSubtask(subtask2);

        epic1.addSubtaskInEpic(epic1, subtask1);
        epic1.addSubtaskInEpic(epic1, subtask2);

        assertEquals(Duration.ofMinutes(90), epic1.getDuration(), "Неверное время длительности Эпика 1");
    }
}