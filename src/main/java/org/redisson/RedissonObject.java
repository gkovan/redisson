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
import org.redisson.connection.ConnectionManager;
import org.redisson.core.RObject;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;

/**
 * Base Redisson object
 *
 * @author Nikita Koksharov
 *
 */
abstract class RedissonObject implements RObject {

    final CommandExecutor commandExecutor;
    private final String name;

    public RedissonObject(CommandExecutor commandExecutor, String name) {
        this.commandExecutor = commandExecutor;
        this.name = name;
    }

    protected <V> V get(Future<V> future) {
        return commandExecutor.get(future);
    }

    protected <V> Promise<V> newPromise() {
        return commandExecutor.getConnectionManager().getGroup().next().<V>newPromise();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean rename(String newName) {
        return commandExecutor.get(renameAsync(newName));
    }

    @Override
    public Future<Boolean> renameAsync(String newName) {
        return commandExecutor.writeAsync(getName(), RedisCommands.RENAME, getName(), newName);
    }

    @Override
    public boolean renamenx(String newName) {
        return commandExecutor.get(renamenxAsync(newName));
    }

    @Override
    public Future<Boolean> renamenxAsync(String newName) {
        return commandExecutor.writeAsync(getName(), RedisCommands.RENAMENX, getName(), newName);
    }

    @Override
    public boolean delete() {
        return commandExecutor.get(deleteAsync());
    }

    @Override
    public Future<Boolean> deleteAsync() {
        return commandExecutor.writeAsync(getName(), RedisCommands.DEL_SINGLE, getName());
    }

}
