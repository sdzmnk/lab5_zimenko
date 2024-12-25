import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;
import java.util.List;

public class Task2 {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // Назви програмних забезпечень
        String software1 = "Програма A";
        String software2 = "Програма B";
        String software3 = "Програма C";

        // Отримання даних для кожного програмного забезпечення асинхронно
        CompletableFuture<int[]> software1Future = getSoftwareDataAsync(software1);
        CompletableFuture<int[]> software2Future = getSoftwareDataAsync(software2);
        CompletableFuture<int[]> software3Future = getSoftwareDataAsync(software3);

        // Обчислення загального балу асинхронно та визначення найкращого програмного забезпечення
        CompletableFuture<Integer> totalScore1Future = software1Future.thenCompose(Task2::calculateScoreAsync);
        CompletableFuture<Integer> totalScore2Future = software2Future.thenCompose(Task2::calculateScoreAsync);
        CompletableFuture<Integer> totalScore3Future = software3Future.thenCompose(Task2::calculateScoreAsync);

        // Використання allOf для очікування завершення всіх завдань
        CompletableFuture<Void> allOfFuture = CompletableFuture.allOf(totalScore1Future, totalScore2Future, totalScore3Future);
        allOfFuture.get(); // чекаємо на завершення всіх задач

        // Обчислення результатів асинхронно та визначення найкращого програмного забезпечення
        int totalScore1 = totalScore1Future.get();
        int totalScore2 = totalScore2Future.get();
        int totalScore3 = totalScore3Future.get();

        System.out.println(software1 + ": Загальний бал: " + totalScore1);
        System.out.println(software2 + ": Загальний бал: " + totalScore2);
        System.out.println(software3 + ": Загальний бал: " + totalScore3);

        // Визначення найкращого програмного забезпечення
        List<String> bestSoftware = determineBestSoftware(totalScore1, totalScore2, totalScore3, software1, software2, software3);
        System.out.println("Найкраще програмне забезпечення: " + bestSoftware);

        // Використання anyOf для отримання першого завершеного результату
        CompletableFuture<Integer> anyScore = CompletableFuture.anyOf(totalScore1Future, totalScore2Future, totalScore3Future)
                .thenApply(score -> (Integer) score);

        System.out.println("Перший завершений результат: " + anyScore.get());
    }

    private static CompletableFuture<int[]> getSoftwareDataAsync(String software) {
        // Паралельне отримання даних асинхронно
        CompletableFuture<Integer> priceFuture = CompletableFuture.supplyAsync(() -> getRandomValue("Ціна", software, 100, 500));
        CompletableFuture<Integer> functionalityFuture = CompletableFuture.supplyAsync(() -> getRandomValue("Функціональність", software, 1, 10));
        CompletableFuture<Integer> supportFuture = CompletableFuture.supplyAsync(() -> getRandomValue("Підтримка", software, 1, 10));

        // Завдання, які повинні бути виконані паралельно
        return CompletableFuture.allOf(priceFuture, functionalityFuture, supportFuture)
                .thenApply(v -> new int[]{priceFuture.join(), functionalityFuture.join(), supportFuture.join()});
    }

    private static int getRandomValue(String criteria, String software, int min, int max) {
        delayRandom();
        int value = ThreadLocalRandom.current().nextInt(min, max + 1); // Генеруємо значення в заданому діапазоні
        System.out.println(software + ": " + criteria + " отримано: " + value);
        return value;
    }

    private static CompletableFuture<Integer> calculateScoreAsync(int[] scores) {
        return CompletableFuture.supplyAsync(() -> {
            int price = scores[0];
            int functionality = scores[1];
            int support = scores[2];
            return (10 - price / 50) + functionality + support;
        });
    }

    private static List<String> determineBestSoftware(int score1, int score2, int score3, String software1, String software2, String software3) {

        List<String> bestSoftwares = new ArrayList<>();

        if (score1 > score2 && score1 > score3) {
            return Collections.singletonList(software1);
        } else if (score2 > score1 && score2 > score3) {
            return Collections.singletonList(software2);
        } else if (score3 > score1 && score3 > score2) {
            return Collections.singletonList(software3);
        } else {
            if (score1 == score2) {
                bestSoftwares.add(software1);
                bestSoftwares.add(software2);
            } else if (score1 == score3) {
                bestSoftwares.add(software1);
                bestSoftwares.add(software3);
            } else {
                bestSoftwares.add(software2);
                bestSoftwares.add(software3);
            }
            return bestSoftwares;
        }
    }

    private static void delayRandom() {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(500, 2000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}