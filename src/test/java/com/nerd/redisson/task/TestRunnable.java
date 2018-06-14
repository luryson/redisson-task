package com.nerd.redisson.task;

import org.redisson.Redisson;
import org.redisson.RedissonNode;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.RedissonNodeConfig;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * @author nerd
 * @date 2018-06-14 12:48
 */
@Slf4j
public class TestRunnable {
    public static void main(String... args) {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");

        RedissonClient client = Redisson.create(config);
        RunnableTask task = new MyTask();

        task.setRedissonClient(client);
        task.release(5);

        RedissonNodeConfig nodeConfig = new RedissonNodeConfig(config);
        Map<String, Integer> workers = new HashMap<>();
        workers.put("test", 1);
        nodeConfig.setExecutorServiceWorkers(workers);
        RedissonNode node = RedissonNode.create(nodeConfig);
        node.start();
    }

    @Slf4j
    public static class MyTask extends RunnableTask {

        public String queue = "test";

        @Override
        public void handle() throws Exception {
            log.info("task handled.");
        }

        @Override
        public String getQueue() {
            return this.queue;
        }

        @Override
        public void logFailedTask() {
            log.error("task failed.");
        }
    }
}
