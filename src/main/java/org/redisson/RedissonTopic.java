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
package org.redisson;

import org.redisson.client.protocol.RedisCommands;
import org.redisson.connection.PubSubConnectionEntry;
import org.redisson.core.MessageListener;
import org.redisson.core.RTopic;

import io.netty.util.concurrent.Future;

/**
 * Distributed topic implementation. Messages are delivered to all message listeners across Redis cluster.
 *
 * @author Nikita Koksharov
 *
 * @param <M> message
 */
public class RedissonTopic<M> extends RedissonObject implements RTopic<M> {

    protected RedissonTopic(CommandExecutor commandExecutor, String name) {
        super(commandExecutor, name);
    }

    @Override
    public long publish(M message) {
        return get(publishAsync(message));
    }

    @Override
    public Future<Long> publishAsync(M message) {
        return commandExecutor.writeAsync(getName(), RedisCommands.PUBLISH, getName(), message);
    }

    @Override
    public int addListener(MessageListener<M> listener) {
        RedisPubSubTopicListenerWrapper<M> pubSubListener = new RedisPubSubTopicListenerWrapper<M>(listener, getName());
        return addListener(pubSubListener);
    }

    private int addListener(RedisPubSubTopicListenerWrapper<M> pubSubListener) {
        PubSubConnectionEntry entry = commandExecutor.getConnectionManager().subscribe(getName());
        synchronized (entry) {
            if (entry.isActive()) {
                entry.addListener(getName(), pubSubListener);
                return pubSubListener.hashCode();
            }
        }
        // entry is inactive trying add again
        return addListener(pubSubListener);
    }

    @Override
    public void removeListener(int listenerId) {
        PubSubConnectionEntry entry = commandExecutor.getConnectionManager().getEntry(getName());
        if (entry == null) {
            return;
        }
        synchronized (entry) {
            if (entry.isActive()) {
                entry.removeListener(getName(), listenerId);
                if (!entry.hasListeners(getName())) {
                    commandExecutor.getConnectionManager().unsubscribe(getName());
                }
                return;
            }
        }

        // entry is inactive trying add again
        removeListener(listenerId);
    }

    @Override
    public boolean delete() {
        throw new UnsupportedOperationException();
    }

    @Override
    public io.netty.util.concurrent.Future<Boolean> deleteAsync() {
        throw new UnsupportedOperationException();
    };

}
