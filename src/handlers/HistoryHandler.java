package handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_HISTORY:
                handleGetHistory(exchange);
                break;
            default:
                sendNotFound(exchange, "Такого эндпоинта не существует");
        }
    }

    private void handleGetHistory(HttpExchange exchange) throws IOException {
        List<Task> history = taskManager.getHistoryManager().getHistory();
        if (history.isEmpty()) {
            sendText(exchange, "История пустая");
            return;
        }

        String responseJson = gson.toJson(history);
        sendText(exchange, responseJson);
    }
}
