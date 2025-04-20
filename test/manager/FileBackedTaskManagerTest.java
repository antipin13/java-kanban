package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Task;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private FileBackedTaskManager fileBackedTaskManager;
    private File tmpFile;
    private Task task1;

    @BeforeEach
    void beforeEach() throws IOException {
        tmpFile = File.createTempFile("Testfile", ".csv");
        fileBackedTaskManager = new FileBackedTaskManager(new InMemoryHistoryManager(), tmpFile);
    }

    @Test
    void createEmptyFile() {
        assertEquals(0, tmpFile.length(), "Файл tmpFile не пустой");
    }

    @Test
    void loadEmptyFile() throws IOException {
        FileBackedTaskManager fileBackedTaskManager1 = FileBackedTaskManager.loadFromFile(tmpFile);
        assertNotNull(fileBackedTaskManager1, "Менеджер не загрузился из файла tmpFile");
    }

    @Test
    void addNewTasksInFile() throws IOException {
        task1 = new Task("Задача 1", "описание 1", Status.NEW, LocalDateTime.of(2025,
                Month.JANUARY, 1, 0, 0), Duration.ofMinutes(60));
        Epic epic1 = new Epic("Эпик 1", "описание эпика 1");

        fileBackedTaskManager.createTask(task1);
        fileBackedTaskManager.createEpic(epic1);

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(tmpFile))) {
            bufferedReader.readLine();

            String line = bufferedReader.readLine();
            Task taskInFile1 = fileBackedTaskManager.fromString(line);

            assertEquals(taskInFile1.getId(), task1.getId(), "Задача task1 не записалась в файл tmpFile");

            line = bufferedReader.readLine();
            Task epicInFile1 = fileBackedTaskManager.fromString(line);
            assertEquals(epicInFile1.getId(), epic1.getId(), "Эпик epic1 не записался в файл tmpFile");
        } catch (FileNotFoundException e) {
            e.getMessage();
        }
    }

    @Test
    void updateTaskInFile() {
        task1 = new Task("Задача 1", "описание 1", Status.NEW, LocalDateTime.of(2025,
                Month.JANUARY, 1, 0, 0), Duration.ofMinutes(60));
        fileBackedTaskManager.createTask(task1);

        Task updateTask1 = new Task("Задача 1", "описание 1", Status.DONE, LocalDateTime.of(2025,
                Month.JANUARY, 1, 0, 0), Duration.ofMinutes(60));
        updateTask1.setId(task1.getId());

        fileBackedTaskManager.updateTask(updateTask1);

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(tmpFile))) {
            bufferedReader.readLine();

            String line = bufferedReader.readLine();
            Task taskInFile1 = fileBackedTaskManager.fromString(line);
            assertEquals(Status.DONE, taskInFile1.getStatus(), "Задача task1 не обновилась в файле tmpFile");
        } catch (IOException e) {
            e.getMessage();
        }
    }
}
