package com.nerd.redisson.task;

import org.redisson.api.RScheduledExecutorService;

import java.io.Serializable;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

/**
 * @author nerd
 * @date 2018-06-14 10:17
 */
@Slf4j
public abstract class CallableTask<T> extends AbstractTask implements Callable<T>, Serializable {

    @Override
    public T call() throws Exception {
        try {
            return this.handle();
        } catch (Exception e) {
            log.error("", e);
            return this.release(defaultDelay).get();
        }
    }

    public abstract T handle() throws Exception;

    public Future<T> release() {
        return this.release(0);
    }

    public Future<T> release(long delay, TimeUnit timeUnit) {
        return this.release(TimeUnit.SECONDS.convert(delay, timeUnit));
    }

    public Future<T> release(long delaySeconds) {
        if (this.attempts() > maxTaskRetries) {
            this.logFailedTask();
            return null;
        }
        RScheduledExecutorService service = this.redissonClient.getExecutorService(this.getQueue());
        ++attempts;
        if (delaySeconds <= 0) {
            return service.submitAsync(this);
        } else {
            return service.scheduleAsync(this, delaySeconds, TimeUnit.SECONDS);
        }
    }
}
