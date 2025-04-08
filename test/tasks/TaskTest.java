package tasks;

import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    private TaskManager manager = Managers.getDefault();

    @Test
    void intersectionOfTwoTasks() {
        Task task1 = new Task("Задача 1", "описание 1", Status.NEW, LocalDateTime.of(2025,
                Month.JANUARY, 1,0,0), Duration.ofMinutes(60));
        manager.createTask(task1);

        Task task2 = new Task("Задача 2", "описание 2", Status.NEW, LocalDateTime.of(2024,
                Month.DECEMBER, 31,23,0), Duration.ofMinutes(120));
        manager.createTask(task2);

        assertTrue(task1.intersectionOfTwoTasks(task2), "Задачи не пересекаются по времени");
    }
}