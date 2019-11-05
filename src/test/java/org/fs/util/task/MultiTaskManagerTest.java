package org.fs.util.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MultiTaskManagerTest {
    public static void main(String[] args) {
        MultiTaskManager<String> manager = new MultiTaskManager<>(10, () -> new MultiTaskManager.Worker<String>() {
            @Override
            protected void process(String t) {
                System.out.println(Thread.currentThread().getName() + ":" + t);
                try {
                    Thread.sleep(ThreadLocalRandom.current().nextLong(1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(String.valueOf(i));
        }
        manager.execute(list);
    }
}
