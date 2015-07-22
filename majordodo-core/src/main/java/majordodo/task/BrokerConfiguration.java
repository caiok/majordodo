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

/**
 * Broker configuration
 *
 * @author enrico.olivelli
 */
public class BrokerConfiguration {

    private int maxWorkerIdleTime = 30000;

    /**
     * Maximum time to wait for a worker to demonstrate its liveness. defaults
     * to 30 seconds
     *
     * @return
     */
    public int getMaxWorkerIdleTime() {
        return maxWorkerIdleTime;
    }

    public void setMaxWorkerIdleTime(int maxWorkerIdleTime) {
        this.maxWorkerIdleTime = maxWorkerIdleTime;
    }

    private int checkpointTime = 1000 * 60 * 60;

    /**
     * Maximum time to wait for a checkpoint. Defaults to 1 hour
     *
     * @return
     */
    public int getCheckpointTime() {
        return checkpointTime;
    }

    public void setCheckpointTime(int checkpointTime) {
        this.checkpointTime = checkpointTime;
    }

    private int finishedTasksRetention = 1000 * 60 * 60;

    /**
     * Retention for finished tasks. Defaults to one hour
     *
     * @return
     */
    public int getFinishedTasksRetention() {
        return finishedTasksRetention;
    }

    public void setFinishedTasksRetention(int finishedTasksRetention) {
        this.finishedTasksRetention = finishedTasksRetention;
    }

    private int recomputeGroupsPeriod = 0;

    /**
     * Period for recomputation of task to group mapping. 0 means 'never', that
     * is that groups are assigned at task submission time. Defaults to 0.
     *
     * @return
     */
    public int getRecomputeGroupsPeriod() {
        return recomputeGroupsPeriod;
    }

    public void setRecomputeGroupsPeriod(int recomputeGroupsPeriod) {
        this.recomputeGroupsPeriod = recomputeGroupsPeriod;
    }

    private int finishedTasksPurgeSchedulerPeriod = 1000 * 60 * 15;

    /**
     * Period for finished task purge scheduler. Defaults to 15 minutes
     *
     * @return
     */
    public int getFinishedTasksPurgeSchedulerPeriod() {
        return finishedTasksPurgeSchedulerPeriod;
    }

    public void setFinishedTasksPurgeSchedulerPeriod(int finishedTasksPurgeSchedulerPeriod) {
        this.finishedTasksPurgeSchedulerPeriod = finishedTasksPurgeSchedulerPeriod;
    }

    private int maxExpiredTasksPerCycle = 1000;

    /**
     * Maximum number of tasks to expire per period
     *
     * @return
     * @see #finishedTasksPurgeSchedulerPeriod
     */
    public int getMaxExpiredTasksPerCycle() {
        return maxExpiredTasksPerCycle;
    }

    public void setMaxExpiredTasksPerCycle(int maxExpiredTasksPerCycle) {
        this.maxExpiredTasksPerCycle = maxExpiredTasksPerCycle;
    }

    private long transactionsTtl = 1000 * 60 * 5;

    /**
     * Maximum time to live for an active transaction
     *
     * @return
     */
    public long getTransactionsTtl() {
        return transactionsTtl;
    }

    public void setTransactionsTtl(long transactionsTtl) {
        this.transactionsTtl = transactionsTtl;
    }

    /**
     * Maximum size of the tasksheap
     */
    private int tasksHeapSize = 2000000;

    public int getTasksHeapSize() {
        return tasksHeapSize;
    }

    public void setTasksHeapSize(int tasksHeapSize) {
        this.tasksHeapSize = tasksHeapSize;
    }

}