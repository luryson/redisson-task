package com.nerd.redisson.task;

import org.redisson.Redisson;
import org.redisson.RedissonNode;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.RedissonNodeConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import lombok.extern.slf4j.Slf4j;

/**
 * @author nerd
 * @date 2018-06-14 13:13
 */
@Slf4j
public class TestCallable {
    public static void main(String... args) throws ExecutionException, InterruptedException {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");


        RedissonNodeConfig nodeConfig = new RedissonNodeConfig(config);
        Map<String, Integer> workers = new HashMap<>();
        workers.put("test", 1);
        nodeConfig.setExecutorServiceWorkers(workers);
        RedissonNode node = RedissonNode.create(nodeConfig);
        node.start();


        RedissonClient client = Redisson.create(config);
        CallableTask<Long> task = new MyCallableTask();
        task.setRedissonClient(client);
        Future<Long> future = task.release(5);
        System.out.println(future.get());

    }

    public static class MyCallableTask extends CallableTask<Long> {

        public String queue = "test";

        @Override
        public Long handle() throws Exception {
            return 10L;
        }

        @Override
        public String getQueue() {
            return this.queue;
        }

        @Override
        public void logFailedTask() {
            log.info("task failed");
        }
    }
}
