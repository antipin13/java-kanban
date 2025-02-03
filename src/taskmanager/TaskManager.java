package taskmanager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.HashMap;

public class TaskManager {
    public HashMap<Integer, Task> tasks;
    public HashMap<Integer, Subtask> subtasks;
    public HashMap<Integer, Epic> epics;
    private static int id;

    public TaskManager() {
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
    }

    public void getTasks() {
        for (Task task : tasks.values()) {
            System.out.println(task.getId() + " - " + task.getName());
        }
    }

    public void getSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            System.out.println(subtask.getId() + " - " + subtask.getName());
        }
    }

    public void getEpics() {
        for (Epic epic : epics.values()) {
            System.out.println(epic.getId() + " - " + epic.getName());
        }
    }

    public HashMap<Integer, Task> clearTasks() {
        tasks.clear();
        return tasks;
    }

    public HashMap<Integer, Subtask> clearSubtasks() {
        subtasks.clear();
        return subtasks;
    }

    public HashMap<Integer, Epic> clearEpics() {
        epics.clear();
        return epics;
    }

    public void getTaskOfId(int idOfTask) {
        for (Integer id : tasks.keySet()) {
            if (id == idOfTask) {
                System.out.println(tasks.get(idOfTask).getName());
            }
        }
    }

    public void getSubtaskOfId(int idOfSubtask) {
        for (Integer id : subtasks.keySet()) {
            if (id == idOfSubtask) {
                System.out.println(subtasks.get(idOfSubtask).getName());
            }
        }
    }

    public void getEpicOfId(int idOfEpic) {
        for (Integer id : epics.keySet()) {
            if (id == idOfEpic) {
                System.out.println(epics.get(idOfEpic).getName());
            }
        }
    }

    public void createTask (Task task) {
        id = id + 1;
        task.setId(id);
        tasks.put(task.getId(), task);
        System.out.println("Создана задача " + task.getName() + " с идентификатором " + id);
    }

    public void createSubtask (Subtask subtask) {
        id = id + 1;
        subtask.setId(id);
        subtasks.put(subtask.getId(), subtask);
        System.out.println("Создана подзадача " + subtask.getName() + " с идентификатором " + id);
    }

    public void createEpic (Epic epic) {
        id = id + 1;
        epic.setId(id);
        epics.put(epic.getId(), epic);
        System.out.println("Создан эпик " + epic.getName() + " с идентификатором " + id);
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateSubtask(Subtask subtask) {
        Subtask existingSubtask = subtasks.get(subtask.getId());
        if (existingSubtask != null) {
            existingSubtask.setName(subtask.getName());
            existingSubtask.setDescription(subtask.getDescription());
            existingSubtask.setStatus(subtask.getStatus());
        }
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void deleteTask(int idOfTask) {
        int idForDelete = 0;
        for (Integer id : tasks.keySet()) {
            if (id == idOfTask) {
                idForDelete = idOfTask;
            }
        }
        tasks.remove(idForDelete);
    }

    public void deleteSubtask(int idOfSubtask) {
        int idForDelete = 0;
        for (Integer id : subtasks.keySet()) {
            if (id == idOfSubtask) {
                idForDelete = idOfSubtask;
            }
        }
        subtasks.remove(idForDelete);
    }

    public void deleteEpic(int idOfEpic) {
        int idForDelete = 0;
        for (Integer id : epics.keySet()) {
            if (id == idOfEpic) {
                idForDelete = idOfEpic;
            }
        }
        epics.remove(idForDelete);
    }

}


