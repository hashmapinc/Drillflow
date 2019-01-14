/**
 * Copyright © 2018-2018 Hashmap, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hashmapinc.tempus.witsml.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication()
@EnableAsync(proxyTargetClass=true)
public class WitsmlServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(WitsmlServerApplication.class, args);
    }

    /**
     * Configure custom Async task executor to provide async functionality for
     * this application
     *
     * @return custom task executor async in nature
     */
    @Bean("asyncCustomTaskExecutor")
    public TaskExecutor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(AppConstants.CORE_POOL_SIZE);
        executor.setMaxPoolSize(AppConstants.MAX_POOL_SIZE);
        executor.setQueueCapacity(AppConstants.QUEUE_CAPACITY);
        executor.setKeepAliveSeconds(AppConstants.KEEP_ALIVE_TIME_IN_SEC);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setThreadNamePrefix(AppConstants.ASYNC_THREAD_NAME);
        executor.initialize();
        return executor;
    }
}
