package main;

import manager.HistoryManager;
import manager.InMemoryTaskManager;
import manager.Managers;
import manager.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {

        HistoryManager historyManager = Managers.getDefaultHistory();
        TaskManager manager = new InMemoryTaskManager(historyManager);


        Task task1 = new Task("Сделать уроки", "решить математику", Status.NEW);
        Task task2 = new Task("Сделать уроки", "решить русский язык", Status.DONE);
        Task task3 = new Task("убраться в комнате", "протереть пыль", Status.NEW);
        manager.createTask(task1);
        manager.createTask(task2);
        manager.createTask(task3);

        Epic epic1 = new Epic("эпик 1", "нужна машина");
        manager.createEpic(epic1);

        Subtask subtask1 = new Subtask("подзадача 1", "информация", Status.NEW);
        Subtask subtask2 = new Subtask("подзадача 2", "информация", Status.NEW);
        Subtask subtask3 = new Subtask("подзадача 3", "информация", Status.NEW);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);

        epic1.addSubtaskInEpic(subtask1);
        epic1.addSubtaskInEpic(subtask2);
        epic1.addSubtaskInEpic(subtask3);

        Epic epic2 = new Epic("эпик 2", "пустой");
        manager.createEpic(epic2);

        historyManager.add(task1);
        System.out.println(historyManager.getHistory());

        historyManager.add(task2);
        historyManager.add(epic2);
        System.out.println(historyManager.getHistory());

        historyManager.add(task1);
        System.out.println(historyManager.getHistory());

        historyManager.add(epic1);
        historyManager.add(subtask1);
        historyManager.add(subtask3);
        System.out.println(historyManager.getHistory());

        manager.deleteTask(task1.getId());
        System.out.println(historyManager.getHistory());

        manager.deleteEpic(epic1.getId());
        System.out.println(historyManager.getHistory());
    }
}
