package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.HashMap;

public interface TaskManager {
    HashMap<Integer, Task> getTasks();

    HashMap<Integer, Subtask> getSubtasks();

    HashMap<Integer, Epic> getEpics();

    HashMap<Integer, Task> clearTasks();

    HashMap<Integer, Subtask> clearSubtasks();

    HashMap<Integer, Epic> clearEpics();

    Task getTaskOfId(int idOfTask);

    Subtask getSubtaskOfId(int idOfSubtask);

    Epic getEpicOfId(int idOfEpic);

    void createTask(Task task);

    void createSubtask(Subtask subtask);

    void createEpic(Epic epic);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    void deleteTask(int idOfTask);

    void deleteSubtask(int idOfSubtask);

    void deleteEpic(int idOfEpic);
}
