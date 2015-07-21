/**
 * Copyright 2014 Nikita Koksharov, Nickolay Borbit
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.redisson.connection;

import java.util.NavigableMap;
import java.util.concurrent.TimeUnit;

import org.redisson.MasterSlaveServersConfig;
import org.redisson.client.RedisClient;
import org.redisson.client.RedisConnection;
import org.redisson.client.RedisPubSubListener;
import org.redisson.client.protocol.Codec;
import org.redisson.client.protocol.pubsub.PubSubStatusMessage;

import io.netty.channel.EventLoopGroup;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.Promise;

/**
 *
 * @author Nikita Koksharov
 *
 */
//TODO ping support
public interface ConnectionManager {

    int calcSlot(String key);

    HashedWheelTimer getTimer();

    MasterSlaveServersConfig getConfig();

    Codec getCodec();

    NavigableMap<Integer, MasterSlaveEntry> getEntries();

    <R> Promise<R> newPromise();

    void releaseRead(int slot, RedisConnection connection);

    void releaseWrite(int slot, RedisConnection connection);

    RedisConnection connectionReadOp(int slot);

    RedisConnection connectionWriteOp(int slot);

    <T> FutureListener<T> createReleaseReadListener(int slot,
            RedisConnection conn, Timeout timeout);

    <T> FutureListener<T> createReleaseWriteListener(int slot,
            RedisConnection conn, Timeout timeout);

    RedisClient createClient(String host, int port, int timeout);

    RedisClient createClient(String host, int port);

    PubSubConnectionEntry getEntry(String channelName);

    PubSubConnectionEntry subscribe(String channelName);

    PubSubConnectionEntry psubscribe(String pattern);

    <V> Future<PubSubStatusMessage> subscribe(RedisPubSubListener<V> listener, String channelName);

    Future unsubscribe(String channelName);

    Future punsubscribe(String channelName);

    void shutdown();

    EventLoopGroup getGroup();

    Timeout newTimeout(TimerTask task, long delay, TimeUnit unit);
}
