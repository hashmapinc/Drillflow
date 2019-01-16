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

/**
 * App Constants
 *
 */
public class AsyncAppConstants {

    /**
     * CorePoolSize is the initial number of threads that will be running tasks
     * in the pool
     */
    public static final int CORE_POOL_SIZE = 20;

    /**
     * The max pool size is the maximum number of workers that can be in the
     * pool. If the max pool size is greater than the core pool size, it means
     * that the pool can grow in size, i.e. more workers can be added to the
     * pool. Workers are added to the pool when a task is submitted but the work
     * queue is full. Every time this happens, a new worker is added until the
     * max pool size is reached. If the max pool size has already been reached
     * and the work queue is full, then the next task will be rejected.
     */
    public static final int MAX_POOL_SIZE = 200;

    /**
     * QueueCapacity is the number of tasks that will be “waiting” in the queue
     * while all the threads are in use.
     */
    public static final int QUEUE_CAPACITY = 50;

    /**
     * If the pool currently has more than corePoolSize threads, excess threads
     * will be terminated if they have been idle for more than the keepAliveTime
     */
    public static final int KEEP_ALIVE_TIME_IN_SEC = 60;

    /**
     * Async thread name
     */
    public static final String ASYNC_THREAD_NAME = "Async-Witsml-";
}
