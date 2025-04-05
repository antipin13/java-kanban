package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Task;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
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
        task1 = new Task("Сделать уроки", "решить математику", Status.NEW);
        Epic epic1 = new Epic("эпик 1", "нужна машина");

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
        task1 = new Task("Сделать уроки", "решить математику", Status.NEW);
        fileBackedTaskManager.createTask(task1);

        Task updateTask1 = new Task("Сделать уроки", "решить математику", Status.DONE);
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