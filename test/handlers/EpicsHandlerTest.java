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
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import java.net.http.HttpResponse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EpicsHandlerTest {
    HistoryManager historyManager = new InMemoryHistoryManager();
    TaskManager manager = new InMemoryTaskManager(historyManager);
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();
    Epic epic1;

    EpicsHandlerTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.clearTasks();
        manager.clearSubtasks();
        manager.clearEpics();
        taskServer.start();
        epic1 = new Epic("Эпик 1", "описание эпика 1");
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        String taskJson = gson.toJson(epic1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        Map<Integer, Epic> tasksFromManager = manager.getEpics();

        assertNotNull(tasksFromManager, "Эпики не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество эпиков");
    }

    @Test
    public void testGetEpics() throws IOException, InterruptedException {
        manager.createEpic(epic1);

        Epic epic2 = new Epic("Эпик 2", "описание эпика 2");

        manager.createEpic(epic2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Map<Integer, Epic> tasksFromManager = manager.getEpics();

        assertNotNull(tasksFromManager, "Эпики не возвращаются");
        assertEquals(2, tasksFromManager.size(), "Некорректное количество эпиков");
    }

    @Test
    public void testGetEpicById() throws IOException, InterruptedException {
        manager.createEpic(epic1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic1.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        HttpClient client1 = HttpClient.newHttpClient();
        URI url1 = URI.create("http://localhost:8080/epics/2");
        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(url1)
                .GET()
                .build();

        HttpResponse<String> response1 = client1.send(request1, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response1.statusCode(), "Некорректный код ошибки");
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        manager.createEpic(epic1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic1.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Map<Integer, Epic> tasksFromManager = manager.getEpics();

        assertEquals(0, tasksFromManager.size(), "Эпик не был удален");
    }

    @Test
    public void testAddSubtaskInEpic() throws IOException, InterruptedException {
        manager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Подзадача 1", "описание подзадачи 1", Status.NEW,
                LocalDateTime.of(2025, Month.FEBRUARY, 13,23,5), Duration.ofMinutes(50));

        manager.createSubtask(subtask1);

        String taskJson = String.format("{\"epicId\":%d,\"subtaskId\":%d}", epic1.getId(), subtask1.getId());

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(String.format("http://localhost:8080/epics/%d/%d", epic1.getId(),subtask1.getId()));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        assertNotNull(subtask1.getEpicId(), "Подзадача не привязалась к эпику");
    }
}