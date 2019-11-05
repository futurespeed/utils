package org.fs.util.task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 多线程作业管理器
 *
 * @param <T> 作业数据类型
 */
public class MultiTaskManager<T> {
    protected int workerNum;

    protected List<Worker> workers;

    protected List<T> taskQueue;

    protected CountDownLatch countDownLatch;

    protected boolean isTaskRunning = false;

    protected WorkerFactory<T> workerFactory;

    private MultiTaskManager() {
    }

    /**
     * 创建多线程作业管理器
     *
     * @param workerNum     工作者数量（线程数）
     * @param workerFactory 工作者工厂
     */
    public MultiTaskManager(int workerNum, WorkerFactory<T> workerFactory) {
        this.workerNum = workerNum;
        this.workerFactory = workerFactory;
    }

    /**
     * 执行
     *
     * @param taskData 任务数据
     */
    public void execute(List<T> taskData) {
        synchronized (this) {
            if (isTaskRunning) {
                throw new IllegalStateException("task is running !");
            }
            isTaskRunning = true;
        }
        countDownLatch = new CountDownLatch(workerNum);
        taskQueue = taskData;
        workers = new ArrayList<>();
        for (int i = 0; i < workerNum; i++) {
            Worker<T> worker = workerFactory.build();
            worker.setManager(this);
            worker.setName("mtm-" + Thread.currentThread().getId() + "-worker-" + i);
            workers.add(worker);
        }
        for (Worker worker : workers) {
            worker.start();
        }
        while (!isAllTaskFinished()) {
            try {
                countDownLatch.await(60000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        isTaskRunning = false;
    }

    /**
     * 是否所有作业已完成
     *
     * @return 所有作业完成状态
     */
    public boolean isAllTaskFinished() {
        for (Worker worker : workers) {
            if (worker.isRunning()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 通知作业已完成
     */
    public void notifyFinish() {
        countDownLatch.countDown();
    }

    /**
     * 获取任务数据
     *
     * @return 任务数据
     */
    public synchronized T getTask() {
        Iterator<T> ite = taskQueue.iterator();
        T t = null;
        if (ite.hasNext()) {
            t = ite.next();
            ite.remove();
        }
        return t;
    }

    /**
     * 多线程作业工作者
     *
     * @param <T> 作业数据类型
     */
    public abstract static class Worker<T> extends Thread {

        protected MultiTaskManager<T> manager;

        protected boolean isRunning = true;

        /**
         * 设置管理者
         *
         * @param manager 管理者
         */
        public void setManager(MultiTaskManager<T> manager) {
            this.manager = manager;
        }

        @Override
        public void run() {
            T task = null;
            while ((task = manager.getTask()) != null) {
                process(task);
            }
            isRunning = false;
            manager.notifyFinish();
        }

        /**
         * 是否运行中
         *
         * @return 运行状态
         */
        public boolean isRunning() {
            return isRunning;
        }

        /**
         * 执行任务
         *
         * @param t 任务数据
         */
        protected abstract void process(T t);
    }

    /**
     * 多线程作业工作者工厂
     */
    public interface WorkerFactory<T> {
        /**
         * 建造工作者
         *
         * @return 工作者
         */
        Worker<T> build();
    }
}