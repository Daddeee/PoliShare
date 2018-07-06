package it.polimi.polishare.peer.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {
    private static ThreadPool ourInstance = new ThreadPool();

    public static ThreadPool getInstance() {
        return ourInstance;
    }

    private ExecutorService cachedThreadPool;

    private ThreadPool() {
        cachedThreadPool = Executors.newCachedThreadPool();
    }

    public void execute(Runnable runnable) {
        cachedThreadPool.execute(runnable);
    }
}
