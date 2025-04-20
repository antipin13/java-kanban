package handlers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {


    public EpicsHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_EPICS:
                handleGetEpics(exchange);
                break;
            case POST_EPICS:
                handleCreateEpics(exchange);
                break;
            case GET_EPIC_ID:
                handleGetEpicByID(exchange);
                break;
            case DELETE_EPIC:
                handleDeleteEpic(exchange);
            case POST_EPIC_SUBTASK:
                handleAddSubtaskInEpic(exchange);
                break;
            default:
                sendNotFound(exchange, "Такого эндпоинта не существует");
        }
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        Map<Integer, Epic> epics = taskManager.getEpics();
        if (epics.isEmpty()) {
            sendText(exchange, "Список эпиков пуст");
            return;
        }

        String responseJson = gson.toJson(epics);
        sendText(exchange, responseJson);
    }

    private void handleGetEpicByID(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");
        if (pathParts.length < 3) {
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(400, 0);
            exchange.getResponseBody().write("Неверный формат URI. Ожидается /epics/{id}"
                    .getBytes(StandardCharsets.UTF_8));
            return;
        }
        try {
            int epicId = Integer.parseInt(pathParts[2]);

            Epic epic = taskManager.getEpicOfId(epicId);
            if (epic == null) {
                sendNotFound(exchange, "Эпик с ID " + epicId + " не найден");
                return;
            }

            String responseJson = gson.toJson(epic);
            sendText(exchange, responseJson);
        } catch (NumberFormatException e) {
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(400, 0);
            exchange.getResponseBody().write("Неверный формат эпика. Ожидается целое число"
                    .getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
            sendInternalServerError(exchange, "Ошибка при получении эпика: " + e.getMessage());
        } finally {
            exchange.close();
        }
    }

    private void handleCreateEpics(HttpExchange exchange) throws IOException {
        try (InputStream inputStream = exchange.getRequestBody()) {
            String epicJson = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            if (epicJson.isEmpty()) {
                sendNotFound(exchange, "Тело запроса пустое");
                return;
            }

            Epic epic = gson.fromJson(epicJson, Epic.class);

            taskManager.createEpic(epic);

            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(201, 0);
            String responseJson = gson.toJson(epic);
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

    private void handleAddSubtaskInEpic(HttpExchange exchange) throws IOException {
        try (InputStream inputStream = exchange.getRequestBody()) {
            String epicJson = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            if (epicJson.isEmpty()) {
                sendNotFound(exchange, "Тело запроса пустое");
                return;
            }

            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");
            if (pathParts.length < 4) {
                exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
                exchange.sendResponseHeaders(400, 0);
                exchange.getResponseBody().write("Неверный формат URI. Ожидается /epics/{id}/{id subtask}"
                        .getBytes(StandardCharsets.UTF_8));
                return;
            }

            JsonObject json = JsonParser.parseString(epicJson).getAsJsonObject();
            int epicId = json.get("epicId").getAsInt();
            int subtaskId = json.get("subtaskId").getAsInt();

            Epic epic = taskManager.getEpicOfId(epicId);
            Subtask subtask = taskManager.getSubtaskOfId(subtaskId);

            if (epic == null || subtask == null) {
                sendNotFound(exchange, "Эпика или подзадачи не существует");
                return;
            }

            taskManager.addSubtaskInEpic(epic, subtask);

            sendText(exchange, "Подзадача " + subtaskId + " добавлена в эпик " + epicId);
        } catch (Exception e) {
            sendInternalServerError(exchange, "Error adding subtask to epic: " + e.getMessage());
        }
    }

    private void handleDeleteEpic(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");
        if (pathParts.length < 3) {
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(400, 0);
            exchange.getResponseBody().write("Неверный формат URI. Ожидается /epics/{id}"
                    .getBytes(StandardCharsets.UTF_8));
            return;
        }

        int epicId = Integer.parseInt(pathParts[2]);

        Epic epic = taskManager.getEpicOfId(epicId);
        if (epic == null) {
            sendNotFound(exchange, "Эпик с ID " + epicId + " не найден");
            return;
        }

        taskManager.deleteEpic(epicId);
        sendText(exchange, "Эпик с ID " + epicId + " удален");
    }
}