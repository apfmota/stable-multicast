package StableMulticast;

import java.util.ArrayList;
import java.util.List;
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
        List<String> processos = new ArrayList<>(clocks.keySet());
        processos.sort(String::compareTo); // ordena para manter consistência

        // Cabeçalho
        sb.append(String.format("%20s", ""));
        for (String col : processos) {
            sb.append(String.format("%15s", col));
        }
        sb.append("\n");

        // Linhas
        for (String row : processos) {
            sb.append(String.format("%20s", row));
            for (String col : processos) {
                int valor = clocks.getOrDefault(row, new VectorClock()).getVectorClock().getOrDefault(col, 0);
                sb.append(String.format("%15d", valor));
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}
