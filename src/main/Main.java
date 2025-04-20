package main;

import manager.*;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

public class Main {

    public static void main(String[] args) throws IOException {

        HistoryManager historyManager = Managers.getDefaultHistory();

        File file = new File("C:\\Users\\mihail\\IdeaProjects\\java-kanban\\src\\Tasksfile.csv");
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(historyManager, file);

        Task task1 = new Task("Задача 1", "описание 1", Status.NEW, LocalDateTime.of(2025,
                Month.JANUARY, 1, 0, 0), Duration.ofMinutes(60));
        fileBackedTaskManager.createTask(task1);

        Task task2 = new Task("Задача 1", "описание 1", Status.NEW, LocalDateTime.of(2025,
                Month.JUNE, 16, 15, 0), Duration.ofMinutes(320));
        fileBackedTaskManager.createTask(task2);

        Epic epic1 = new Epic("Эпик 1", "описание эпика 1");
        fileBackedTaskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача 1", "описание подзадачи 1", Status.NEW,
                LocalDateTime.of(2025, Month.FEBRUARY, 13, 23, 5),
                Duration.ofMinutes(50));
        epic1.addSubtaskInEpic(epic1, subtask1);
        fileBackedTaskManager.createSubtask(subtask1);
    }
}
