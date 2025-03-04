package com.apj.projects.coconut.concurent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apj.projects.coconut.concurent.exceptions.ThreadpoolServiceException;
import com.apj.projects.coconut.utils.PropertyManager;

public class ThreadpoolService {

	private static Logger logger = LoggerFactory.getLogger(ThreadpoolService.class);

	private static ThreadpoolService threadpoolService;
	private static ExecutorService threadPool;

	private ThreadpoolService() {

		if (threadPool == null) {

			ThreadType threadType = PropertyManager.getThreadType();

			if (threadType == ThreadType.CACHED_THREAD_POOL) {
				threadPool = Executors.newCachedThreadPool();
				logger.info("Creating new cached threadpool ..");
			} else if (threadType == ThreadType.VIRTUAL_THREAD) {
				threadPool = Executors.newVirtualThreadPerTaskExecutor();
				logger.info("Creating new Virtual Thread Executor ...");
			} else if (threadType == ThreadType.FIXED_THREAD_POOL) {
				int nThreads = PropertyManager.getNThreads();
				threadPool = Executors.newFixedThreadPool(nThreads);
				logger.info("Creating new FixedThreadPool with " + nThreads + " threads ...");
			} else {
				logger.info(
						"Could not find appropriate thread_type in coconut.properties ===> Switching to default ...");
				threadPool = Executors.newCachedThreadPool();
				logger.info("Creating new cached threadpool ..");
			}
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
