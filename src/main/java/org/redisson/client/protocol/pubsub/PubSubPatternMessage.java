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
package org.redisson.client.protocol.pubsub;

public class PubSubPatternMessage implements Message {

    private final String pattern;
    private final String channel;
    private final Object value;

    public PubSubPatternMessage(String pattern, String channel, Object value) {
        super();
        this.pattern = pattern;
        this.channel = channel;
        this.value = value;
    }

    public String getPattern() {
        return pattern;
    }

    public String getChannel() {
        return channel;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "PubSubPatternMessage [pattern=" + pattern + ", channel=" + channel + ", value=" + value + "]";
    }

}
