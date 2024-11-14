package com.apj.projects.coconut.concurent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.apj.projects.coconut.exceptions.ThreadpoolServiceException;

public class ThreadpoolService {

	private static ThreadpoolService threadpoolService;
	private static ExecutorService threadPool;

	private ThreadpoolService() {

		if (threadPool == null) {
			System.out.println("[INFO]: Creating new cached threadpool ..");
			threadPool = Executors.newCachedThreadPool();
		}
	}

	public static ThreadpoolService getThreadPoolService() {
		if (threadpoolService == null) {
			threadpoolService = new ThreadpoolService();
		}

		return threadpoolService;
	}

	public void execute(Runnable runnable) throws ThreadpoolServiceException {

		if (!threadPool.isShutdown())
			threadPool.execute(runnable);
		else {
			throw new ThreadpoolServiceException("ThreadPool has stopped");
		}
	}

	public static void shutdown() {
		if (threadPool != null && !threadPool.isShutdown()) {
			try {
				threadPool.awaitTermination(5, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			threadPool.shutdown();
		}
		System.out.println("Shutting down ..");

	}

}
