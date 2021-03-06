/*
 Licensed to Diennea S.r.l. under one
 or more contributor license agreements. See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership. Diennea S.r.l. licenses this file
 to you under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance
 with the License.  You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License.

 */
package majordodo.task;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Schedules purges of finished tasks
 *
 * @author enrico.olivelli
 */
public class FinishedTaskCollectorScheduler {

    private final BrokerConfiguration configuration;
    private final ScheduledExecutorService timer;
    private final Broker broker;

    public FinishedTaskCollectorScheduler(BrokerConfiguration configuration, Broker broker) {
        this.configuration = configuration;
        this.broker = broker;
        if (this.configuration.getFinishedTasksPurgeSchedulerPeriod() > 0) {
            this.timer = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r, "dodo-broker-finishedtasks-purge-thread");
                    t.setDaemon(true);
                    return t;
                }
            });
        } else {
            this.timer = null;
        }
    }

    private class Purger implements Runnable {

        @Override
        public void run() {
            broker.purgeTasks();
        }

    }

    public void start() {
        if (this.timer != null) {
            this.timer.scheduleAtFixedRate(new Purger(), 1000, configuration.getFinishedTasksPurgeSchedulerPeriod(), TimeUnit.MILLISECONDS);
        }
    }

    public void stop() {
        if (this.timer != null) {
            this.timer.shutdown();
        }
    }

}
