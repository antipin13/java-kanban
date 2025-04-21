package handlers;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import tasks.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {

    public SubtasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_SUBTASKS:
                handleGetSubtasks(exchange);
                break;
            case POST_SUBTASKS:
                handleCreateSubtasks(exchange);
                break;
            case POST_SUBTASKS_ID:
                handleUpdateSubtask(exchange);
                break;
            case GET_SUBTASK_ID:
                handleGetSubtaskByID(exchange);
                break;
            case DELETE_SUBTASK:
                handleDeleteSubtask(exchange);
            default:
                sendNotFound(exchange, "Некорректный эндпоинт");
        }
    }

    private void handleGetSubtasks(HttpExchange exchange) throws IOException {
        Map<Integer, Subtask> subtasks = taskManager.getSubtasks();
        if (subtasks.isEmpty()) {
            sendText(exchange, "Список подзадач пуст");
            return;
        }

        String responseJson = gson.toJson(subtasks);
        sendText(exchange, responseJson);
    }

    private void handleGetSubtaskByID(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");
        if (pathParts.length < 3) {
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(400, 0);
            exchange.getResponseBody().write("Неверный формат URI. Ожидается /subtasks/{id}"
                    .getBytes(StandardCharsets.UTF_8));
            return;
        }
        try {
            int subtaskId = Integer.parseInt(pathParts[2]);

            Subtask subtask = taskManager.getSubtaskOfId(subtaskId);
            if (subtask == null) {
                sendNotFound(exchange, String.format("Подзадача с ID %d не найдена", subtaskId));
                return;
            }

            String responseJson = gson.toJson(subtask);
            sendText(exchange, responseJson);
        } catch (NumberFormatException e) {
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(400, 0);
            exchange.getResponseBody().write("Неверный формат подзадачи. Ожидается целое число"
                    .getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
            sendInternalServerError(exchange, "Ошибка при получении подзадачи: " + e.getMessage());
        } finally {
            exchange.close();
        }
    }

    private void handleCreateSubtasks(HttpExchange exchange) throws IOException {
        try (InputStream inputStream = exchange.getRequestBody()) {
            String subtaskJson = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            if (subtaskJson.isEmpty()) {
                sendNotFound(exchange, "Тело запроса пустое");
                return;
            }

            Subtask subtask = gson.fromJson(subtaskJson, Subtask.class);

            taskManager.createSubtask(subtask);
            if (subtask.getId() == 0) {
                sendHasInteractions(exchange, "Подзадача пересекается с существующей");
                return;
            }

            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(201, 0);

            String responseJson = gson.toJson(subtask);
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

    private void handleUpdateSubtask(HttpExchange exchange) throws IOException {
        try (InputStream inputStream = exchange.getRequestBody()) {
            String subtaskJson = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            if (subtaskJson.isEmpty()) {
                sendNotFound(exchange, "Тело запроса пустое");
                return;
            }

            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");
            if (pathParts.length < 3) {
                exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
                exchange.sendResponseHeaders(400, 0);
                exchange.getResponseBody().write("Неверный формат URI. Ожидается /subtasks/{id}"
                        .getBytes(StandardCharsets.UTF_8));
                return;
            }

            int subtaskId = Integer.parseInt(pathParts[2]);

            Subtask subtask = taskManager.getSubtaskOfId(subtaskId);
            if (subtask == null) {
                sendNotFound(exchange, String.format("Подзадача с ID %d не найдена", subtaskId));
                return;
            }

            Subtask subtaskUpdate = gson.fromJson(subtaskJson, Subtask.class);
            subtaskUpdate.setId(subtask.getId());

            taskManager.updateSubtask(subtaskUpdate);
            if (subtask.getId() == 0) {
                sendHasInteractions(exchange, "Подзадача пересекается с существующей");
                return;
            }

            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(201, 0);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(("Задача с ID " + subtaskId + " обновлена").getBytes(StandardCharsets.UTF_8));
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

    private void handleDeleteSubtask(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");
        if (pathParts.length < 3) {
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(400, 0);
            exchange.getResponseBody().write("Неверный формат URI. Ожидается /tasks/{id}"
                    .getBytes(StandardCharsets.UTF_8));
            return;
        }

        int subtaskId = Integer.parseInt(pathParts[2]);

        Subtask subtask = taskManager.getSubtaskOfId(subtaskId);
        if (subtask == null) {
            sendNotFound(exchange, String.format("Подзадача с ID %d не найдена", subtaskId));
            return;
        }

        taskManager.deleteSubtask(subtaskId);
        sendText(exchange, String.format("Подадача с ID %d удалена", subtaskId));
    }
}