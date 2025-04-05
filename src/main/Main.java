package main;

import manager.*;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        HistoryManager historyManager = Managers.getDefaultHistory();

        File file = new File("C:\\Users\\mihail\\IdeaProjects\\java-kanban\\src\\Tasksfile.csv");
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(historyManager, file);

        Task task1 = new Task("Сделать уроки", "решить математику", Status.NEW);
        fileBackedTaskManager.createTask(task1);

        Task task2 = new Task("Сделать уроки", "решить русский язык", Status.NEW);
        fileBackedTaskManager.createTask(task2);

        Epic epic1 = new Epic("эпик 1", "нужна машина");
        fileBackedTaskManager.createEpic(epic1);

        Subtask subtask1 = new Subtask("подзадача 1", "информация", Status.NEW);
        epic1.addSubtaskInEpic(epic1, subtask1);
        fileBackedTaskManager.createSubtask(subtask1);
    }
}
