/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.mischedule.service;

import com.alipay.sofa.jraft.Iterator;
import com.alipay.sofa.jraft.Status;
import com.alipay.sofa.jraft.core.StateMachineAdapter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class ElectionOnlyStateMachine extends StateMachineAdapter {

    private final AtomicLong                leaderTerm = new AtomicLong(-1L);
    private final List<LeaderStateListener> listeners  = new CopyOnWriteArrayList<>();

    @Override
    public void onApply(final Iterator it) {
        // election only, do nothing
        while (it.hasNext()) {
            log.info("On apply with term: {} and index: {}. ", it.getTerm(), it.getIndex());
            it.next();
        }
    }

    @Override
    public void onLeaderStart(final long term) {
        super.onLeaderStart(term);
        this.leaderTerm.set(term);
        for (final LeaderStateListener listener : this.listeners) { // iterator the snapshot
            listener.onLeaderStart(term);
        }
    }

    @Override
    public void onLeaderStop(final Status status) {
        super.onLeaderStop(status);
        final long oldTerm = leaderTerm.get();
        this.leaderTerm.set(-1L);
        for (final LeaderStateListener listener : this.listeners) { // iterator the snapshot
            listener.onLeaderStop(oldTerm);
        }
    }

    public boolean isLeader() {
        return this.leaderTerm.get() > 0;
    }

    public void addLeaderStateListener(final LeaderStateListener listener) {
        this.listeners.add(listener);
    }
}
