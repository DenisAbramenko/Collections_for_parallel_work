package org.example;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    public static final BlockingQueue<String> queueA = new ArrayBlockingQueue<>(100);
    public static final BlockingQueue<String> queueB = new ArrayBlockingQueue<>(100);
    public static final BlockingQueue<String> queueC = new ArrayBlockingQueue<>(100);
    public static Thread fillingQueues;

    public static void main(String[] args) throws InterruptedException {
        fillingQueues = new Thread(() -> {
            for (int i = 0; i < 10000; i++) {
                String text = generateText("abc", 100000);
                try {
                    queueA.put(text);
                    queueB.put(text);
                    queueC.put(text);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        fillingQueues.start();
        Thread a = generateThread(queueA, 'a');
        a.start();
        Thread b = generateThread(queueB, 'b');
        b.start();
        Thread c = generateThread(queueC, 'c');
        c.start();
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static Thread generateThread(BlockingQueue<String> queue, char symbol) {
        return new Thread(() -> {
            System.out.println("Max count char: '" + symbol + "' = " + sortingQueue(queue, symbol));
        });
    }

    public static int sortingQueue(BlockingQueue<String> queue, char symbol) {
        int count = 0;
        int max = 0;
        String text;
        while (fillingQueues.isAlive()) {
            try {
                text = queue.take();
                for (char symbolQueue : text.toCharArray()) {
                    if (symbolQueue == symbol) {
                        count++;
                    }
                }
                if (count > max) {
                    max = count;
                }
                count = 0;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return max;
    }
}
