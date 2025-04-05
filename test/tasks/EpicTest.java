package tasks;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    private TaskManager manager = Managers.getDefault();

    @Test
    void addSubtaskInEpic() {
        Subtask subtask1 = new Subtask("выбор машины", "просмотр харатекристик", Status.NEW);
        manager.createSubtask(subtask1);

        Epic epic1 = new Epic("покупка машины", "нужна машина");
        manager.createEpic(epic1);
        epic1.addSubtaskInEpic(epic1, subtask1);

        assertEquals(1, epic1.getEpic().size(), "Подзадача не добавлена");
    }

    @Test
    void recalculatingStatus() {
        Subtask subtask1 = new Subtask("выбор машины", "просмотр харатекристик", Status.NEW);
        manager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("выбор автосалона", "просмотр акций", Status.NEW);
        manager.createSubtask(subtask2);

        Epic epic1 = new Epic("покупка машины", "нужна машина");
        manager.createEpic(epic1);

        epic1.addSubtaskInEpic(epic1, subtask1);
        epic1.addSubtaskInEpic(epic1, subtask2);

        assertEquals(Status.NEW, epic1.getStatus(), "Статус рассчитан неверно");

        Subtask subtask3 = new Subtask("выбор автосалона", "просмотр акций", Status.DONE);
        subtask3.setId(2);
        manager.updateSubtask(subtask3);

        assertEquals(Status.IN_PROGRESS, epic1.getStatus(), "Статус рассчитан неверно");
    }
}