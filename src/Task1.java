import java.util.concurrent.*;

public class Task1 {
    public static void main(String[] args) {
        double timePerKm = 5.0; // Час бігу на 1 км (у хвилинах)
        double distance = 10.0; // Дистанція бігу (у км)

        CompletableFuture<Double> timeFuture = CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println("Час бігу на 1 км (у хвилинах): " + timePerKm);
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return timePerKm;
        });

        CompletableFuture<Double> distanceFuture = CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println("Дистанція бігу (у км): " + distance);
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            return distance;
        });

        System.out.println("Розрахунок загального часу на дистанцію.");
        CompletableFuture<Double> totalTimeFuture = timeFuture.thenCombine(distanceFuture, (time, dist) -> {
            return time * dist;
        });

        totalTimeFuture.thenAccept(totalTime -> {
            System.out.println("Загальний час для подолання дистанції - " + totalTime + " хвилин");
        });

        // Використання allOf() для виконання додаткових завдань
        CompletableFuture<Void> allTasks = CompletableFuture.allOf(timeFuture, distanceFuture, totalTimeFuture);
        allTasks.thenRun(() -> {
            System.out.println("Усі обчислення завершені!");
        });


        CompletableFuture<Object> anyTask = CompletableFuture.anyOf(timeFuture, distanceFuture);
        anyTask.thenAccept(result -> {
            System.out.println("Перше завершене завдання: " + result);
        });

        // Затримка, щоб дочекатися виконання всіх асинхронних завдань (для демонстрації)
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
