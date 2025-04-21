package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class InMemoryTaskManager implements TaskManager {
    private HistoryManager historyManager;
    public HashMap<Integer, Task> tasks;
    public HashMap<Integer, Subtask> subtasks;
    public HashMap<Integer, Epic> epics;
    private static int id;

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
    }

    @Override
    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    @Override
    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    @Override
    public HashMap<Integer, Task> clearTasks() {
        tasks.clear();
        return tasks;
    }

    @Override
    public HashMap<Integer, Subtask> clearSubtasks() {
        subtasks.clear();
        return subtasks;
    }

    @Override
    public HashMap<Integer, Epic> clearEpics() {
        for (Epic epic : epics.values()) {
            deleteEpic(epic.getId());
        }
        return epics;
    }

    @Override
    public Task getTaskOfId(int idOfTask) {
        for (Integer id : tasks.keySet()) {
            if (id == idOfTask) {
                System.out.println(tasks.get(idOfTask).getName());
            }
        }
        historyManager.add(tasks.get(idOfTask));
        return tasks.get(idOfTask);
    }

    @Override
    public Subtask getSubtaskOfId(int idOfSubtask) {
        for (Integer id : subtasks.keySet()) {
            if (id == idOfSubtask) {
                System.out.println(subtasks.get(idOfSubtask).getName());
            }
        }
        historyManager.add(subtasks.get(idOfSubtask));
        return subtasks.get(idOfSubtask);
    }

    @Override
    public Epic getEpicOfId(int idOfEpic) {
        for (Integer id : epics.keySet()) {
            if (id == idOfEpic) {
                System.out.println(epics.get(idOfEpic).getName());
            }
        }
        historyManager.add(epics.get(idOfEpic));
        return epics.get(idOfEpic);
    }

    Predicate<Task> isIntersectionTask = task -> {
        boolean intersects = intersectionAnyTasks(task);
        if (intersects) {
            System.out.println("Задача пересекается по времени с существующей");
        }
        return intersects;
    };

    @Override
    public void createTask(Task task) {
        if (isIntersectionTask.test(task)) {
            return;
        }
        id = id + 1;
        task.setId(id);
        tasks.put(task.getId(), task);
        historyManager.add(task);
        System.out.printf("Создана задача - %s с идентификатором: %d%n", task.getName(), id);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        if (isIntersectionTask.test(subtask)) {
            return;
        }
        id = id + 1;
        subtask.setId(id);
        subtasks.put(subtask.getId(), subtask);
        historyManager.add(subtask);
        System.out.printf("Создана задача - %s с идентификатором: %d%n", subtask.getName(), id);
    }

    @Override
    public void createEpic(Epic epic) {
        id = id + 1;
        epic.setId(id);
        epics.put(epic.getId(), epic);
        updateEpic(epic);
        historyManager.add(epic);
        System.out.printf("Создана задача - %s с идентификатором: %d%n", epic.getName(), id);
    }

    @Override
    public void updateTask(Task task) {
        if (isIntersectionTask.test(task)) {
            return;
        }
        tasks.put(task.getId(), task);
        historyManager.add(task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (isIntersectionTask.test(subtask)) {
            return;
        }
        Subtask existingSubtask = subtasks.get(subtask.getId());
        if (existingSubtask != null) {
            existingSubtask.setName(subtask.getName());
            existingSubtask.setDescription(subtask.getDescription());
            existingSubtask.setStatus(subtask.getStatus());
        }
        try {
            updateEpic(epics.get(subtask.getEpicId()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        historyManager.add(subtask);
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.get(epic.getId()).getStatus();
        epics.get(epic.getId()).getStartTime();
        epics.get(epic.getId()).getDuration();
        epics.get(epic.getId()).getEndTime();
        epics.put(epic.getId(), epic);
    }

    @Override
    public void deleteTask(int idOfTask) {
        int idForDelete = 0;
        for (Integer id : tasks.keySet()) {
            if (id == idOfTask) {
                idForDelete = idOfTask;
                break;
            }
        }
        tasks.remove(idForDelete);
        historyManager.remove(idForDelete);
    }

    @Override
    public void deleteSubtask(int idOfSubtask) {
        int idForDelete = 0;
        for (Integer id : subtasks.keySet()) {
            if (id == idOfSubtask) {
                idForDelete = idOfSubtask;
                break;
            }
        }
        subtasks.remove(idForDelete);
        historyManager.remove(idForDelete);
        try {
            updateEpic(epics.get(subtasks.get(idOfSubtask).getEpicId()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void deleteEpic(int idOfEpic) {
        int idForDelete = 0;
        for (Integer id : epics.keySet()) {
            if (id == idOfEpic) {
                idForDelete = idOfEpic;
                break;
            }
        }

        for (Subtask subtask : epics.get(idForDelete).getEpic().values()) {
            deleteSubtask(subtask.getId());
        }
        epics.remove(idForDelete);
        historyManager.remove(idForDelete);
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        if (tasks.isEmpty() && subtasks.isEmpty()) {
            return null;
        }
        TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

        Stream.concat(tasks.values().stream().filter(task -> task.getStartTime() != null),
                        subtasks.values().stream().filter(subtask -> subtask.getStartTime() != null))
                .forEach(prioritizedTasks::add);

        return prioritizedTasks;
    }

    @Override
    public boolean intersectionAnyTasks(Task task) {
        if (getPrioritizedTasks() == null) {
            return false;
        }
        return getPrioritizedTasks().stream()
                .filter(task1 -> !task.equals(task1))
                .anyMatch(task1 -> task.intersectionOfTwoTasks(task1));
    }

    @Override
    public void addSubtaskInEpic(Epic epic, Subtask subtask) {
        epics.get(epic.getId()).getEpic().put(subtask.getId(), subtask);
        subtask.setEpicId(epic.getId());
        updateEpic(epic);
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }
}


