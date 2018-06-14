package com.nerd.redisson.task;

import org.redisson.api.RedissonClient;
import org.redisson.api.annotation.RInject;

/**
 * @author nerd
 * @date 2018-06-14 10:21
 */
public abstract class AbstractTask {

    @RInject
    protected transient RedissonClient redissonClient;

    protected String queue;

    protected long defaultDelay = Long.parseLong(System.getProperty("redisson.task.default-delay", "3"));
    protected int maxTaskRetries = Integer.parseInt(System.getProperty("redisson.task.max-retries", "3"));

    protected int attempts = 1;

    public void setRedissonClient(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * 任务重试次数
     */
    public int attempts() {
        return this.attempts;
    }

    /**
     * 队列名
     */
    public abstract String getQueue();

    public abstract void logFailedTask();

}
