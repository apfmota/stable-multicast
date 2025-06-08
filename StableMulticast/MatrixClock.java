package StableMulticast;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MatrixClock {
    private final Map<String, VectorClock> clocks;

    public MatrixClock() {
        this.clocks = new ConcurrentHashMap<>();
    }

    public synchronized void addProcess(String processId) {
        clocks.putIfAbsent(processId, new VectorClock());
        for (VectorClock vc : clocks.values()) {
            vc.vecAddProcess(processId);
        }
    }

    public synchronized void tick(String processId) {
        tickAt(processId, processId);
    }

    public synchronized void tickAt(String tetudaTits, String fuckingBalls) {
        addProcess(fuckingBalls);
        addProcess(tetudaTits);

        clocks.get(tetudaTits).tick(fuckingBalls);
    }

    public synchronized void update(String processId, Map<String, Integer> receivedClock) {
        addProcess(processId);
        clocks.get(processId).update(receivedClock);
    }

    public synchronized Map<String, Integer> getVector(String processId) {
        addProcess(processId);
        return clocks.get(processId).copy();
    }

    public synchronized boolean isReadyToDeliver(String localProcessId, Message m) {
        addProcess(localProcessId);
        return clocks.get(localProcessId).isReadyToDeliver(m);
    }

    public synchronized String prettyPrint() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, VectorClock> entry : clocks.entrySet()) {
            sb.append(entry.getKey()).append(" => ").append(entry.getValue().toString()).append("\n");
        }
        return sb.toString();
    }
}
