package manager;

import exceptions.ManagerSaveException;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private File taskFile;

    public FileBackedTaskManager(HistoryManager historyManager, File taskFile) throws IOException {
        super(historyManager);
        this.taskFile = taskFile;
    }

    public void save() {
        try (FileWriter fileWriter = new FileWriter(taskFile, StandardCharsets.UTF_8, false)) {
            fileWriter.write("id,type,name,status,description,epic, startTime, duration\n");
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении задач в менеджер");
        }

        try (FileWriter fileWriter = new FileWriter(taskFile, StandardCharsets.UTF_8, true)) {
            for (Task task : getTasks().values()) {
                fileWriter.write(toString(task));
            }

            for (Subtask subtask : getSubtasks().values()) {
                fileWriter.write(toString(subtask));
            }

            for (Epic epic : getEpics().values()) {
                fileWriter.write(toString(epic));
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении задач в менеджер");
        }
    }

    static FileBackedTaskManager loadFromFile(File file) throws IOException {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(new InMemoryHistoryManager(), file);

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            bufferedReader.readLine();

            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                fileBackedTaskManager.fromString(line);
            }
        } catch (FileNotFoundException e) {
            e.getMessage();
        }
        return fileBackedTaskManager;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    public String toString(Task task) {
        if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            String template = String.format("%d,%s,%s,%s,%s,%s,%tF %tT,%02d\n", subtask.getId(), KindOfTask.SUBTASK
                    , subtask.getName(), subtask.getStatus(), subtask.getDescription(), subtask.getEpicId()
                    , subtask.getStartTime(), subtask.getStartTime(), subtask.getDuration().toMinutes());
            return template;
        } else if (task instanceof Epic) {
            Epic epic = (Epic) task;
            String template = String.format("%d,%s,%s,%s,%s,%tF %tT,%02d\n", epic.getId(), KindOfTask.EPIC
                    , epic.getName(), epic.getStatus(), epic.getDescription(), epic.getStartTime(), epic.getStartTime()
                    , epic.getDuration().toMinutes());
            return template;
        } else {
            String template = String.format("%d,%s,%s,%s,%s,%tF %tT,%02d\n", task.getId(), KindOfTask.TASK
                    , task.getName(), task.getStatus(), task.getDescription(), task.getStartTime(), task.getStartTime()
                    , task.getDuration().toMinutes());
            return template;
        }

    }

    public Task fromString(String value) {
        String[] split = value.split(",");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        if (KindOfTask.TASK.name().equals(split[1])) {
            Task task = new Task(split[2], split[4], Status.valueOf(split[3]), LocalDateTime.parse(split[5], formatter)
                    , Duration.parse("PT" + split[6] + "M"));
            task.setId(Integer.parseInt(split[0]));
            tasks.put(task.getId(), task);

            return task;
        } else if (KindOfTask.SUBTASK.name().equals(split[1])) {
            Subtask subtask = new Subtask(split[2], split[4], Status.valueOf(split[3])
                    , LocalDateTime.parse(split[6], formatter), Duration.parse("PT" + split[7] + "M"));
            subtask.setId(Integer.parseInt(split[0]));
            subtasks.put(subtask.getId(), subtask);

            return subtask;
        } else {
            Epic epic = new Epic(split[2], split[4]);
            epic.setId(Integer.parseInt(split[0]));
            epics.put(epic.getId(), epic);

            return epic;
        }
    }

    @Override
    public HashMap<Integer, Task> clearTasks() {
        super.clearTasks();
        save();
        return super.clearTasks();
    }

    @Override
    public HashMap<Integer, Subtask> clearSubtasks() {
        super.clearSubtasks();
        save();
        return super.clearSubtasks();
    }

    @Override
    public HashMap<Integer, Epic> clearEpics() {
        super.clearEpics();
        save();
        return super.clearEpics();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteTask(int idOfTask) {
        super.deleteTask(idOfTask);
        save();
    }

    @Override
    public void deleteSubtask(int idOfSubtask) {
        super.deleteSubtask(idOfSubtask);
        save();
    }

    @Override
    public void deleteEpic(int idOfEpic) {
        super.deleteEpic(idOfEpic);
        save();
    }

    public static void main(String[] args) throws IOException {
        File file = new File("C:\\Users\\mihail\\IdeaProjects\\java-kanban\\src\\Tasksfile.csv");
        FileBackedTaskManager newFileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);

        Task task = newFileBackedTaskManager.tasks.get(1);
        System.out.println(task.getStartTime());
        System.out.println(task.getDuration());
        System.out.println(task.getEndTime());
    }
}
