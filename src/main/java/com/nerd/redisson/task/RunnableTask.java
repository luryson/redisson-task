package com.nerd.redisson.task;

import org.redisson.api.RScheduledExecutorService;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

/**
 * @author nerd
 * @date 2018-06-14 10:17
 */
@Slf4j
public abstract class RunnableTask extends AbstractTask implements Runnable, Serializable {

    @Override
    public void run() {
        try {
            this.handle();
        } catch (Exception e) {
            log.error("", e);
            this.release(defaultDelay);
        }
    }

    public abstract void handle() throws Exception;

    public void release() {
        this.release(0L);
    }

    public void release(long delay, TimeUnit timeUnit) {
        this.release(TimeUnit.SECONDS.convert(delay, timeUnit));
    }

    public void release(long delaySeconds) {
        if (this.attempts() > maxTaskRetries) {
            this.logFailedTask();
            return;
        }
        RScheduledExecutorService service = this.redissonClient.getExecutorService(this.getQueue());
        ++attempts;
        if (delaySeconds <= 0) {
            service.submitAsync(this);
        } else {
            service.scheduleAsync(this, delaySeconds, TimeUnit.SECONDS);
        }
    }

}
