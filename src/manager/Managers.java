package manager;

import java.util.HashMap;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager(new InMemoryHistoryManager());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
