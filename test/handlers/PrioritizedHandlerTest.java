package handlers;

import adapters.DurationAdapter;
import adapters.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import httpserver.HttpTaskServer;
import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.net.http.HttpResponse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

class PrioritizedHandlerTest {
    HistoryManager historyManager = new InMemoryHistoryManager();
    TaskManager manager = new InMemoryTaskManager(historyManager);
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    PrioritizedHandlerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.clearTasks();
        manager.clearSubtasks();
        manager.clearEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testGetPrioritized() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        TreeSet<Task> prioritizedTasks = manager.getPrioritizedTasks();

        assertNull(prioritizedTasks, "Список проинициализирован без единой задачи");

        Task task1 = new Task("Задача 1", "описание 1", Status.NEW, LocalDateTime.of(2025,
                Month.JANUARY, 1, 0, 0), Duration.ofMinutes(60));

        Subtask subtask1 = new Subtask("Подзадача 1", "описание подзадачи 1", Status.NEW,
                LocalDateTime.of(2025, Month.FEBRUARY, 13, 23, 5), Duration.ofMinutes(50));

        Task task2 = new Task("Задача 2", "описание 2", Status.NEW, LocalDateTime.of(2025,
                Month.JUNE, 1, 0, 0), Duration.ofMinutes(60));

        manager.createTask(task1);
        manager.createSubtask(subtask1);
        manager.createTask(task2);

        HttpClient client1 = HttpClient.newHttpClient();
        URI url1 = URI.create("http://localhost:8080/prioritized");
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(url1)
                .GET()
                .build();

        HttpResponse<String> response1 = client1.send(request1, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response1.statusCode());

        prioritizedTasks = manager.getPrioritizedTasks();

        assertEquals(task1, prioritizedTasks.first(), "Задача 1 не самая приоритетная");
        assertEquals(task2, prioritizedTasks.last(), "Задача 2 не последняя в приоритном списке ");
    }
}