package io.omengye.wscloud;

import io.omengye.gcs.GcsApplication;
import io.omengye.gcs.configure.GSearchItems;
import io.omengye.gcs.entity.GCEntity;
import io.omengye.gcs.entity.GSearchItem;
import io.omengye.gcs.service.ChooseItemService;
import io.omengye.gcs.service.WebClientService;
import io.omengye.gcs.utils.RangeRandom;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GcsApplication.class)
public class EurekaApplicationTests {

	@Autowired
	private WebClientService webClientService;

	@Test
	public void contextLoads() {
	}

	@Autowired
	ChooseItemService chooseItemService;
	int threadNum = 200;
	RangeRandom rangeRandom = RangeRandom.getInstance();
	CountDownLatch countDownLatch = new CountDownLatch(threadNum);

	class RequestRunnable implements Runnable {
		@Override
		public void run() {
			GSearchItem item = chooseItemService.getItem();
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			int i = rangeRandom.rand(20);
			if (i==1) {
				item = chooseItemService.removeAndGetItem(item);

				chooseItemService.loopErrorItems();
			}
			countDownLatch.countDown();
		}
	}

	public class ErrorThread implements Runnable {

		@Override
		public void run() {
			chooseItemService.loopErrorItems();
		}
	}

	@Test
	public void testChoose() throws InterruptedException {
		ExecutorService executor = Executors.newFixedThreadPool(threadNum);
//		ExecutorService errorExecutor = Executors.newSingleThreadExecutor();


		for (int i=0; i<threadNum; ++i) {
			RequestRunnable requestRunnable = new RequestRunnable();
			executor.execute(requestRunnable);
		}
//		Thread.sleep(10);
//		for (int i=0; i<5; ++i) {
//			ErrorThread errorThread = new ErrorThread();
//			errorExecutor.execute(errorThread);
//		}

		countDownLatch.await();
		executor.shutdown();
		chooseItemService.print();
	}

}

