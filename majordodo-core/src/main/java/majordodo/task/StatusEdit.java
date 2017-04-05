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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An action for the log
 *
 * @author enrico.olivelli
 */
public final class StatusEdit {

    public static final short TYPE_ADD_TASK = 1;
    public static final short TYPE_WORKER_CONNECTED = 2;
    public static final short TYPE_ASSIGN_TASK_TO_WORKER = 3;
    public static final short TYPE_TASK_STATUS_CHANGE = 4;
    public static final short TYPE_WORKER_DISCONNECTED = 5;
    public static final short TYPE_WORKER_DIED = 6;
    public static final short TYPE_BEGIN_TRANSACTION = 7;
    public static final short TYPE_COMMIT_TRANSACTION = 8;
    public static final short TYPE_ROLLBACK_TRANSACTION = 9;
    public static final short TYPE_PREPARE_ADD_TASK = 10;
    public static final short TYPE_NOOP = 11;
    public static final short TYPE_DELETECODEPOOL = 13;
    public static final short TYPE_CREATECODEPOOL = 14;

    public static String typeToString(short type) {
        switch (type) {
            case TYPE_NOOP:
                return "TYPE_NOOP";
            case TYPE_ADD_TASK:
                return "ADD_TASK";
            case TYPE_PREPARE_ADD_TASK:
                return "TYPE_PREPARE_ADD_TASK";
            case TYPE_WORKER_CONNECTED:
                return "WORKER_CONNECTED";
            case TYPE_ASSIGN_TASK_TO_WORKER:
                return "ASSIGN_TASK_TO_WORKER";
            case TYPE_TASK_STATUS_CHANGE:
                return "TASK_STATUS_CHANGE";
            case TYPE_WORKER_DISCONNECTED:
                return "TYPE_WORKER_DISCONNECTED";
            case TYPE_WORKER_DIED:
                return "TYPE_WORKER_DIED";
            case TYPE_BEGIN_TRANSACTION:
                return "TYPE_BEGIN_TRANSACTION";
            case TYPE_COMMIT_TRANSACTION:
                return "TYPE_COMMIT_TRANSACTION";
            case TYPE_ROLLBACK_TRANSACTION:
                return "TYPE_ROLLBACK_TRANSACTION";
            case TYPE_DELETECODEPOOL:
                return "TYPE_DELETECODEPOOL";
            case TYPE_CREATECODEPOOL:
                return "TYPE_CREATECODEPOOL";
            default:
                return "?" + type;
        }
    }

    public short editType;
    public String taskType;
    public long taskId;
    public int taskStatus;
    public int attempt;
    public int maxattempts;
    public long timestamp;
    public long transactionId;
    public long executionDeadline;
    public String parameter;
    public String userid;
    public String workerId;
    public String resources;
    public String workerLocation;
    public String workerProcessId;
    public String result;
    public String slot;
    public String codepool;
    public String mode;
    public Set<Long> actualRunningTasks;
    public byte[] payload;

    @Override
    public String toString() {
        return "StatusEdit{" + "editType=" + editType + " " + typeToString(editType) + ", taskType=" + taskType + ", taskId=" + taskId + ", taskStatus=" + taskStatus + " " + Task.statusToString(taskStatus) +", attempt=" + attempt + ", maxattempts=" + maxattempts + ", timestamp=" + timestamp + ", transactionId=" + transactionId + ", executionDeadline=" + executionDeadline + ", parameter=" + parameter + ", userid=" + userid + ", workerId=" + workerId + ", workerLocation=" + workerLocation + ", workerProcessId=" + workerProcessId + ", result=" + result + ", slot=" + slot + ", actualRunningTasks=" + actualRunningTasks + ", resources=" + resources + '}';
    }

    private static void append(StringBuilder v, String key, Object value) {
        if (value != null && !value.toString().isEmpty()) {
            v.append("," + key + "=" + value);
        }
    }

    public String toFormattedString(SimpleDateFormat tsFormatter) {
        StringBuilder res = new StringBuilder(typeToString(editType));
        append(res, "timestamp", tsFormatter.format(new java.util.Date(timestamp)));
        append(res, "taskId", taskId);
        if (transactionId > 0) {
            append(res, "tx", transactionId);
        }
        if (userid != null && !userid.isEmpty()) {
            append(res, "userid", userid);
        }
        if (taskId > 0) {
            append(res, "taskType", taskType);
            append(res, "taskStatus", Task.statusToString(taskStatus));
        }
        if (slot != null && !slot.isEmpty()) {
            append(res, "slot", slot);
        }
        if (workerId != null && !workerId.isEmpty()) {
            append(res, "workerId", workerId);
        }
        if (resources != null && !resources.isEmpty()) {
            append(res, "resources", resources);
        }
        if (attempt > 0) {
            append(res, "attempt", attempt);
        }
        if (maxattempts > 0) {
            append(res, "maxattempts", maxattempts);
        }
        if (executionDeadline > 0) {
            append(res, "executionDeadline", tsFormatter.format(new java.util.Date(executionDeadline)));
        }
        if (workerLocation != null && !workerLocation.isEmpty()) {
            append(res, "workerLocation", workerLocation);
        }
        if (workerProcessId != null && !workerProcessId.isEmpty()) {
            append(res, "workerProcessId", workerProcessId);
        }
        if (actualRunningTasks != null && !actualRunningTasks.isEmpty()) {
            append(res, "actualRunningTasks", actualRunningTasks);
        }
        if (parameter != null && !parameter.isEmpty()) {
            append(res, "data", parameter);
        }
        if (result != null && !result.isEmpty()) {
            append(res, "result", result);
        }
        return res.toString();
    }

    public static final StatusEdit NOOP() {
        StatusEdit action = new StatusEdit();
        action.editType = TYPE_NOOP;
        return action;
    }

    public static final StatusEdit DELETE_CODEPOOL(String codePoolId) {
        StatusEdit action = new StatusEdit();
        action.editType = TYPE_DELETECODEPOOL;
        action.codepool = codePoolId;
        return action;
    }

    public static final StatusEdit CREATE_CODEPOOL(String codePoolId, long timestamp, byte[] payload, long ttl) {
        StatusEdit action = new StatusEdit();
        action.editType = TYPE_CREATECODEPOOL;
        action.codepool = codePoolId;
        action.timestamp = timestamp;
        action.payload = payload;
        action.executionDeadline = ttl;// a bit weird
        return action;
    }

    public static final StatusEdit BEGIN_TRANSACTION(long transactionId, long timestamp) {
        StatusEdit action = new StatusEdit();
        action.editType = TYPE_BEGIN_TRANSACTION;
        action.transactionId = transactionId;
        action.timestamp = timestamp;
        return action;
    }

    public static final StatusEdit ROLLBACK_TRANSACTION(long transactionId) {
        StatusEdit action = new StatusEdit();
        action.editType = TYPE_ROLLBACK_TRANSACTION;
        action.transactionId = transactionId;
        return action;
    }

    public static final StatusEdit COMMIT_TRANSACTION(long transactionId) {
        StatusEdit action = new StatusEdit();
        action.editType = TYPE_COMMIT_TRANSACTION;
        action.transactionId = transactionId;
        return action;
    }

    public static final StatusEdit ASSIGN_TASK_TO_WORKER(long taskId, String nodeId, int attempt, String resources) {
        StatusEdit action = new StatusEdit();
        action.editType = TYPE_ASSIGN_TASK_TO_WORKER;
        action.workerId = nodeId;
        action.taskId = taskId;
        action.attempt = attempt;
        action.resources = resources;
        return action;
    }

    public static final StatusEdit TASK_STATUS_CHANGE(long taskId, String workerId, int finalStatus, String result) {
        StatusEdit action = new StatusEdit();
        action.editType = TYPE_TASK_STATUS_CHANGE;
        action.workerId = workerId;
        action.taskId = taskId;
        action.taskStatus = finalStatus;
        action.result = result;
        return action;
    }

    public static final StatusEdit ADD_TASK(long taskId, String taskType, String taskParameter, String userid, int maxattempts, long executionDeadline, String slot, int attempt, String codePool, String mode) {
        StatusEdit action = new StatusEdit();
        action.editType = TYPE_ADD_TASK;
        action.attempt = attempt;
        action.slot = slot;
        action.parameter = taskParameter;
        action.taskType = taskType;
        action.taskId = taskId;
        action.userid = userid;
        action.maxattempts = maxattempts;
        action.executionDeadline = executionDeadline;
        action.codepool = codePool;
        action.mode = mode;
        return action;
    }

    public static final StatusEdit PREPARE_ADD_TASK(long transactionId, long taskId, String taskType, String taskParameter, String userid, int maxattempts, long executionDeadline, String slot, int attempts, String codePool, String mode) {
        StatusEdit action = new StatusEdit();
        action.editType = TYPE_PREPARE_ADD_TASK;
        action.attempt = attempts;
        action.transactionId = transactionId;
        action.slot = slot;
        action.parameter = taskParameter;
        action.taskType = taskType;
        action.taskId = taskId;
        action.userid = userid;
        action.maxattempts = maxattempts;
        action.executionDeadline = executionDeadline;
        action.codepool = codePool;
        action.mode = mode;
        return action;
    }

    public static final StatusEdit WORKER_CONNECTED(String workerId, String processid, String nodeLocation, Set<Long> actualRunningTasks, long timestamp) {
        StatusEdit action = new StatusEdit();
        action.editType = TYPE_WORKER_CONNECTED;
        action.timestamp = timestamp;
        action.workerId = workerId;
        action.workerLocation = nodeLocation;
        action.workerProcessId = processid;
        action.actualRunningTasks = actualRunningTasks;
        return action;
    }

    public static final StatusEdit WORKER_DISCONNECTED(String workerId, long timestamp) {
        StatusEdit action = new StatusEdit();
        action.editType = TYPE_WORKER_DISCONNECTED;
        action.timestamp = timestamp;
        action.workerId = workerId;
        return action;
    }

    public static final StatusEdit WORKER_DIED(String workerId, long timestamp) {
        StatusEdit action = new StatusEdit();
        action.editType = TYPE_WORKER_DIED;
        action.timestamp = timestamp;
        action.workerId = workerId;
        return action;
    }

    public byte[] serialize() {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            DataOutputStream doo = new DataOutputStream(out);
            doo.writeShort(this.editType);
            switch (this.editType) {
                case TYPE_BEGIN_TRANSACTION:
                    doo.writeLong(transactionId);
                    doo.writeLong(timestamp);
                    break;
                case TYPE_COMMIT_TRANSACTION:
                    doo.writeLong(transactionId);
                    break;
                case TYPE_ROLLBACK_TRANSACTION:
                    doo.writeLong(transactionId);
                    break;
                case TYPE_ADD_TASK:
                    doo.writeLong(taskId);
                    doo.writeUTF(userid);
                    doo.writeInt(taskStatus);
                    doo.writeUTF(taskType);
                    doo.writeInt(maxattempts);
                    doo.writeInt(attempt);
                    doo.writeLong(executionDeadline);
                    if (parameter != null) {
                        doo.writeUTF(parameter);
                    } else {
                        doo.writeUTF("");
                    }
                    if (slot != null) {
                        doo.writeUTF(slot);
                    } else {
                        doo.writeUTF("");
                    }
                    if (codepool != null) {
                        doo.writeUTF(codepool);
                    } else {
                        doo.writeUTF("");
                    }
                    if (mode != null) {
                        doo.writeUTF(mode);
                    } else {
                        doo.writeUTF("");
                    }
                    break;
                case TYPE_PREPARE_ADD_TASK:
                    doo.writeLong(transactionId);
                    doo.writeLong(taskId);
                    doo.writeUTF(userid);
                    doo.writeInt(taskStatus);
                    doo.writeUTF(taskType);
                    doo.writeInt(maxattempts);
                    doo.writeInt(attempt);
                    doo.writeLong(executionDeadline);
                    if (parameter != null) {
                        doo.writeUTF(parameter);
                    } else {
                        doo.writeUTF("");
                    }
                    if (slot != null) {
                        doo.writeUTF(slot);
                    } else {
                        doo.writeUTF("");
                    }
                    if (codepool != null) {
                        doo.writeUTF(codepool);
                    } else {
                        doo.writeUTF("");
                    }
                    if (mode != null) {
                        doo.writeUTF(mode);
                    } else {
                        doo.writeUTF("");
                    }
                    break;
                case TYPE_WORKER_CONNECTED:
                    doo.writeUTF(workerId);
                    doo.writeUTF(workerLocation);
                    doo.writeUTF(workerProcessId);
                    doo.writeLong(timestamp);
                    doo.writeUTF(actualRunningTasks.stream().map(l -> l.toString()).collect(Collectors.joining(",")));
                    break;
                case TYPE_WORKER_DIED:
                case TYPE_WORKER_DISCONNECTED:
                    doo.writeUTF(workerId);
                    doo.writeLong(timestamp);
                    break;

                case TYPE_ASSIGN_TASK_TO_WORKER:
                    doo.writeUTF(workerId);
                    doo.writeLong(taskId);
                    doo.writeInt(attempt);
                    if (resources != null) {
                        doo.writeUTF(resources);
                    } else {
                        doo.writeUTF("");
                    }
                    break;
                case TYPE_TASK_STATUS_CHANGE:
                    doo.writeLong(taskId);
                    doo.writeInt(taskStatus);
                    if (workerId != null) {
                        doo.writeUTF(workerId);
                    } else {
                        doo.writeUTF("");
                    }
                    if (result != null) {
                        doo.writeUTF(result);
                    } else {
                        doo.writeUTF("");
                    }
                    break;
                case TYPE_NOOP:
                    break;
                case TYPE_DELETECODEPOOL:
                    doo.writeUTF(codepool);
                    break;
                case TYPE_CREATECODEPOOL:
                    doo.writeUTF(codepool);
                    doo.writeLong(timestamp);
                    doo.writeLong(executionDeadline);
                    doo.writeInt(payload.length);
                    doo.write(payload);
                    break;
                default:
                    throw new UnsupportedOperationException();

            }
            doo.close();
            return out.toByteArray();
        } catch (IOException err) {
            throw new RuntimeException(err);
        }
    }

    public static StatusEdit read(byte[] data) throws IOException {
        StatusEdit res = new StatusEdit();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        DataInputStream doo = new DataInputStream(in);
        res.editType = doo.readShort();
        switch (res.editType) {
            case TYPE_ADD_TASK: {
                res.taskId = doo.readLong();
                res.userid = doo.readUTF();
                res.taskStatus = doo.readInt();
                res.taskType = doo.readUTF();
                res.maxattempts = doo.readInt();
                res.attempt = doo.readInt();
                res.executionDeadline = doo.readLong();
                res.parameter = doo.readUTF();
                String slot = doo.readUTF();
                if (!slot.isEmpty()) {
                    res.slot = slot;
                }
                try {
                    String codepool = doo.readUTF();
                    if (!codepool.isEmpty()) {
                        res.codepool = codepool;
                    }
                    String mode = doo.readUTF();
                    if (!mode.isEmpty()) {
                        res.mode = mode;
                    }
                } catch (EOFException legacy) {
                }
                break;
            }
            case TYPE_PREPARE_ADD_TASK: {
                res.transactionId = doo.readLong();
                res.taskId = doo.readLong();
                res.userid = doo.readUTF();
                res.taskStatus = doo.readInt();
                res.taskType = doo.readUTF();
                res.maxattempts = doo.readInt();
                res.attempt = doo.readInt();
                res.executionDeadline = doo.readLong();
                res.parameter = doo.readUTF();
                String slot = doo.readUTF();
                if (!slot.isEmpty()) {
                    res.slot = slot;
                }
                try {
                    String codepool = doo.readUTF();
                    if (!codepool.isEmpty()) {
                        res.codepool = codepool;
                    }
                    String mode = doo.readUTF();
                    if (!mode.isEmpty()) {
                        res.mode = mode;
                    }
                } catch (EOFException legacy) {
                }
                break;
            }
            case TYPE_WORKER_DIED:
            case TYPE_WORKER_DISCONNECTED:
                res.workerId = doo.readUTF();
                res.timestamp = doo.readLong();
                break;
            case TYPE_WORKER_CONNECTED:
                res.workerId = doo.readUTF();
                res.workerLocation = doo.readUTF();
                res.workerProcessId = doo.readUTF();
                res.timestamp = doo.readLong();
                res.actualRunningTasks = new HashSet<>();
                String rt = doo.readUTF();

                Stream.of(rt.split(",")).filter(s -> !s.isEmpty()).map(s -> Long.parseLong(s)).forEach(res.actualRunningTasks::add);

                break;
            case TYPE_ASSIGN_TASK_TO_WORKER:
                res.workerId = doo.readUTF();
                res.taskId = doo.readLong();
                res.attempt = doo.readInt();
                try {
                    res.resources = doo.readUTF();
                } catch (EOFException legacy) {
                }
                break;
            case TYPE_TASK_STATUS_CHANGE:
                res.taskId = doo.readLong();
                res.taskStatus = doo.readInt();
                res.workerId = doo.readUTF();
                res.result = doo.readUTF();
                break;
            case TYPE_BEGIN_TRANSACTION:
                res.transactionId = doo.readLong();
                res.timestamp = doo.readLong();
                break;
            case TYPE_COMMIT_TRANSACTION:
                res.transactionId = doo.readLong();
                break;
            case TYPE_ROLLBACK_TRANSACTION:
                res.transactionId = doo.readLong();
                break;
            case TYPE_NOOP:
                break;
            case TYPE_DELETECODEPOOL:
                res.codepool = doo.readUTF();
                break;
            case TYPE_CREATECODEPOOL:
                res.codepool = doo.readUTF();
                res.timestamp = doo.readLong();
                res.executionDeadline = doo.readLong();
                res.payload = new byte[doo.readInt()];
                int countRead = doo.read(res.payload, 0, res.payload.length);
                if (countRead != res.payload.length) {
                    throw new IOException("short read " + countRead + " <> " + res.payload.length);
                }
                break;
            default:
                throw new UnsupportedOperationException("editType=" + res.editType);
        }
        return res;

    }

}
