package handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {

    public TasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_TASKS:
                handleGetTasks(exchange);
                break;
            case POST_TASKS:
                handleCreateTasks(exchange);
                break;
            case POST_TASK_ID:
                handleUpdateTask(exchange);
                break;
            case GET_TASK_ID:
                handleGetTaskByID(exchange);
                break;
            case DELETE_TASK:
                handleDeleteTask(exchange);
            default:
                sendNotFound(exchange, "Некорректный эндпоинт");
        }

    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        Map<Integer, Task> tasks = taskManager.getTasks();
        if (tasks.isEmpty()) {
            sendText(exchange, "Список задач пуст");
            return;
        }

        String responseJson = gson.toJson(tasks);
        sendText(exchange, responseJson);
    }

    private void handleGetTaskByID(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");
        if (pathParts.length < 3) {
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(400, 0);
            exchange.getResponseBody().write("Неверный формат URI. Ожидается /tasks/{id}"
                    .getBytes(StandardCharsets.UTF_8));
            return;
        }
        try {
            int taskId = Integer.parseInt(pathParts[2]);

            Task task = taskManager.getTaskOfId(taskId);
            if (task == null) {
                sendNotFound(exchange, String.format("Задача с ID %d не найдена", taskId));
                return;
            }

            String responseJson = gson.toJson(task);
            sendText(exchange, responseJson);
        } catch (NumberFormatException e) {
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(400, 0);
            exchange.getResponseBody().write("Неверный формат задачи. Ожидается целое число"
                    .getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
            sendInternalServerError(exchange, "Ошибка при получении задачи: " + e.getMessage());
        } finally {
            exchange.close();
        }
    }

    private void handleCreateTasks(HttpExchange exchange) throws IOException {
        try (InputStream inputStream = exchange.getRequestBody()) {
            String taskJson = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            if (taskJson.isEmpty()) {
                sendNotFound(exchange, "Тело запроса пустое");
                return;
            }

            Task task = gson.fromJson(taskJson, Task.class);

            taskManager.createTask(task);
            if (task.getId() == 0) {
                sendHasInteractions(exchange, "Задача пересекается с существующей");
                return;
            }

            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(201, 0);

            String responseJson = gson.toJson(task);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseJson.getBytes(StandardCharsets.UTF_8));
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            sendNotFound(exchange, "Неверный формат JSON: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            sendInternalServerError(exchange, "Ошибка при создании задачи: " + e.getMessage());
        } finally {
            exchange.close();
        }
    }

    private void handleUpdateTask(HttpExchange exchange) throws IOException {
        try (InputStream inputStream = exchange.getRequestBody()) {
            String taskJson = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            if (taskJson.isEmpty()) {
                sendNotFound(exchange, "Тело запроса пустое");
                return;
            }

            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");
            if (pathParts.length < 3) {
                exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
                exchange.sendResponseHeaders(400, 0);
                exchange.getResponseBody().write("Неверный формат URI. Ожидается /tasks/{id}"
                        .getBytes(StandardCharsets.UTF_8));
                return;
            }

            int taskId = Integer.parseInt(pathParts[2]);

            Task task = taskManager.getTaskOfId(taskId);
            if (task == null) {
                sendNotFound(exchange, String.format("Задача с ID %d не найдена", taskId));
                return;
            }

            Task taskUpdate = gson.fromJson(taskJson, Task.class);
            taskUpdate.setId(task.getId());

            taskManager.updateTask(taskUpdate);
            if (task.getId() == 0) {
                sendHasInteractions(exchange, "Задача пересекается с существующей");
                return;
            }

            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(201, 0);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(("Задача с ID " + taskId + " обновлена").getBytes(StandardCharsets.UTF_8));
            }
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            sendNotFound(exchange, "Неверный формат JSON: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            sendInternalServerError(exchange, "Ошибка при создании задачи: " + e.getMessage());
        } finally {
            exchange.close();
        }
    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");
        if (pathParts.length < 3) {
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(400, 0);
            exchange.getResponseBody().write("Неверный формат URI. Ожидается /tasks/{id}"
                    .getBytes(StandardCharsets.UTF_8));
            return;
        }

        int taskId = Integer.parseInt(pathParts[2]);

        Task task = taskManager.getTaskOfId(taskId);
        if (task == null) {
            sendNotFound(exchange, String.format("Задача с ID %d не найдена", taskId));
            return;
        }

        taskManager.deleteTask(taskId);
        sendText(exchange, String.format("Задача с ID %d удалена", taskId));
    }
}