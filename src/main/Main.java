package main;

import manager.TaskManager;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("Сделать уроки", "решить математику", Status.NEW);
        Task task2 = new Task("убраться в комнате", "протереть пыль", Status.NEW);

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        Subtask subtask1 = new Subtask("выбор машины", "просмотр харатекристик", Status.NEW);
        Subtask subtask2 = new Subtask("выбор автосалона", "просмотр акций", Status.NEW);
        Subtask subtask3 = new Subtask("выбор застройщика", "просмотр цен", Status.NEW);

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        Epic epic1 = new Epic("покупка машины", "нужна машина");
        Epic epic2 = new Epic("покупка квартиры", "нужна квартира");

        taskManager.createEpic(epic1);
        epic1.addSubtaskInEpic(subtask1);
        epic1.addSubtaskInEpic(subtask2);


        taskManager.createEpic(epic2);
        epic2.addSubtaskInEpic(subtask3);

        System.out.println(taskManager.tasks);
        System.out.println(taskManager.epics);
        System.out.println(epic1.getStatus());
        System.out.println(epic2.getStatus());
        System.out.println(taskManager.subtasks);

        System.out.println();

        Subtask subtask4 = new Subtask("выбор машины", "просмотр харатекристик", Status.DONE);
        subtask4.setId(3);
        taskManager.updateSubtask(subtask4);
        System.out.println(epic1.getStatus());

        System.out.println();

        taskManager.deleteTask(1);
        taskManager.getTasks();

        System.out.println();

        taskManager.deleteSubtask(3);
        taskManager.getSubtasks();

        System.out.println();

        taskManager.deleteEpic(7);
        taskManager.getEpics();

    }
}
