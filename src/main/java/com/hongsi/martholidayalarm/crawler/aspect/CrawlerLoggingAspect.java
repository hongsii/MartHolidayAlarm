package com.hongsi.martholidayalarm.crawler.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
@Slf4j
public class CrawlerLoggingAspect {

	private StopWatch stopWatch;

	@Before("execution(* com.hongsi.martholidayalarm.crawler.MartCrawlerScheduler.crawlMart())")
	public void beforeRunningCrawler() {
		log.info("============ Start Mart crawling ============");
		stopWatch = new StopWatch("MartCrawler");
	}

	@After("execution(* com.hongsi.martholidayalarm.crawler.MartCrawlerScheduler.crawlMart())")
	public void afterRunningCrawler() {
		if (stopWatch.isRunning()) {
			stopWatch.stop();
		}

		log.info(stopWatch.prettyPrint());
		log.info("============    End  crawling    ============");
	}

	@Before("execution(* com.hongsi.martholidayalarm.crawler.domain.MartCrawler+.crawl())")
	public void beforeCrawling(JoinPoint joinPoint) {
		log.info("============ Start {} ============", joinPoint.toShortString());
		stopWatch.start(joinPoint.toShortString());
	}

	@After("execution(* com.hongsi.martholidayalarm.crawler.domain.MartCrawler+.crawl())")
	public void afterCrawling(JoinPoint joinPoint) {
		stopWatch.stop();
		log.info("============ End {} ============", joinPoint.toShortString());
	}
}