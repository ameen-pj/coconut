package com.apj.projects.coconut.concurent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apj.projects.coconut.concurent.exceptions.ThreadpoolServiceException;

public class ThreadpoolService {

	private static Logger logger = LoggerFactory.getLogger(ThreadpoolService.class);

	private static ThreadpoolService threadpoolService;
	private static ExecutorService threadPool;

	private ThreadpoolService() {

		if (threadPool == null) {
			logger.info("Creating new cached threadpool ..");
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
		logger.info("Shutting down ...");

	}

}
