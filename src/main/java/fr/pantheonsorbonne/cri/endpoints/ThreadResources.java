package fr.pantheonsorbonne.cri.endpoints;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class ThreadResources {

	static ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("MSProducerThread-%d").build();
	static ExecutorService executor = Executors.newFixedThreadPool(12, namedThreadFactory);

}
