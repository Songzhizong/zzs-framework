package com.zzs.framework.core.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author 宋志宗 on 2022/8/26
 */
@SuppressWarnings("AlibabaServiceOrDaoClassShouldEndWithImpl")
public final class CommonPool implements ExecutorService {
  private static final Log log = LogFactory.getLog(CommonPool.class);
  private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
    0, 512,
    60, TimeUnit.SECONDS, new SynchronousQueue<>(),
    BasicThreadFactory.builder().namingPattern("common-pool").build(),
    (r, executor) -> {
      log.warn("common-pool线程池资源不足");
      r.run();
    });

  public static final CommonPool INSTANCE = new CommonPool();

  private CommonPool() {
  }

  @Override
  public void shutdown() {
    EXECUTOR.shutdown();
  }

  @Nonnull
  @Override
  public List<Runnable> shutdownNow() {
    return EXECUTOR.shutdownNow();
  }

  @Override
  public boolean isShutdown() {
    return EXECUTOR.isShutdown();
  }

  @Override
  public boolean isTerminated() {
    return EXECUTOR.isTerminated();
  }

  @Override
  public boolean awaitTermination(long timeout,
                                  @Nonnull TimeUnit unit) throws InterruptedException {
    return EXECUTOR.awaitTermination(timeout, unit);
  }

  @Nonnull
  @Override
  public <T> Future<T> submit(@Nonnull Callable<T> task) {
    return EXECUTOR.submit(task);
  }

  @Nonnull
  @Override
  public <T> Future<T> submit(@Nonnull Runnable task, T result) {
    return EXECUTOR.submit(task, result);
  }

  @Nonnull
  @Override
  public Future<?> submit(@Nonnull Runnable task) {
    return EXECUTOR.submit(task);
  }

  @Nonnull
  @Override
  public <T> List<Future<T>> invokeAll(@Nonnull Collection<? extends Callable<T>> tasks)
    throws InterruptedException {
    return EXECUTOR.invokeAll(tasks);
  }

  @Nonnull
  @Override
  public <T> List<Future<T>> invokeAll(@Nonnull Collection<? extends Callable<T>> tasks,
                                       long timeout,
                                       @Nonnull TimeUnit unit) throws InterruptedException {
    return EXECUTOR.invokeAll(tasks, timeout, unit);
  }

  @Nonnull
  @Override
  public <T> T invokeAny(@Nonnull Collection<? extends Callable<T>> tasks)
    throws InterruptedException, ExecutionException {
    return EXECUTOR.invokeAny(tasks);
  }

  @Override
  public <T> T invokeAny(@Nonnull Collection<? extends Callable<T>> tasks,
                         long timeout, @Nonnull TimeUnit unit)
    throws InterruptedException, ExecutionException, TimeoutException {
    return EXECUTOR.invokeAny(tasks, timeout, unit);
  }

  @Override
  public void execute(@Nonnull Runnable command) {
    EXECUTOR.execute(command);
  }
}
