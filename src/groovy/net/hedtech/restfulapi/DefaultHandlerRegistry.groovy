/* ***************************************************************************
 * Copyright 2014 Ellucian Company L.P. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *****************************************************************************/

package net.hedtech.restfulapi

import java.util.concurrent.atomic.AtomicInteger

class DefaultHandlerRegistry<T, H extends Handler<T>> implements HandlerRegistry<T,H> {
    public static final int DEFAULT_PRIORITY = 0

    private static final AtomicInteger SEQUENCE = new AtomicInteger(0)

    private final SortedSet<Entry> handlers = new TreeSet<Entry>();

    void add(H handler) {
        add(handler, DEFAULT_PRIORITY)
    }

    void add(H handler, int priority) {
        handlers.add(new Entry(handler,priority))
    }

    H getHandler(T t) {
        for (Entry entry : handlers) {
            if (entry.handler.supports(t)) {
                return entry.handler
            }
        }
        return null
    }

    List<H> getOrderedHandlers() {
        List<H> list = new ArrayList<H>()
        for (Entry entry : handlers) {
            list.add(entry.handler);
        }
        list
    }


    class Entry implements Comparable<Entry> {
        protected final H handler
        private final int priority
        private final int seq

        private Entry(H handler, int priority) {
            this.handler = handler
            this.priority = priority
            seq = SEQUENCE.incrementAndGet();
        }

        public int compareTo(Entry entry) {
            //if two handlers have the same priority, the one registered last is used
            //to ensure deterministic behavior
            //this ordering is inverted; higher priority/higher sequences handlers are considered
            //to be less, so that they are ordered first.
            return priority == entry.priority ? entry.seq - seq : entry.priority - priority;
        }
    }

}
