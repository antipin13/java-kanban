package manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    @Test
    void getDefaultTaskManager() {
        TaskManager taskManager = Managers.getDefault();

        assertNotNull(taskManager, "TaskManager не проинициализирован.");
    }

    @Test
    void getDefaultHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertNotNull(historyManager, "HistoryManager не проинициализирован.");
    }
}