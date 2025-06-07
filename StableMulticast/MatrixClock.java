package StableMulticast;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class MatrixClock {
    private final ConcurrentMap<String, ConcurrentMap<String, Integer>> matrix;

    public MatrixClock() {
        this.matrix = new ConcurrentHashMap<>();
    }

    public synchronized void update(String nodeId, Map<String, Integer> vectorClock) {
        matrix.putIfAbsent(nodeId, new ConcurrentHashMap<>());
        
        vectorClock.forEach((senderId, clockValue) -> {
            matrix.get(nodeId).put(senderId, clockValue);
        });
    }

    public synchronized int getMinForNode(String nodeId) {
        return matrix.values().stream()
            .mapToInt(nodeClock -> nodeClock.getOrDefault(nodeId, 0))
            .min()
            .orElse(0);
    }

    public synchronized void increment(String nodeId, String senderId) {
        matrix.putIfAbsent(nodeId, new ConcurrentHashMap<>());
        matrix.get(nodeId).merge(senderId, 1, Integer::sum);
    }

    public synchronized Map<String, Map<String, Integer>> getMatrix() {
        Map<String, Map<String, Integer>> copy = new ConcurrentHashMap<>();
        matrix.forEach((nodeId, nodeClock) -> {
            copy.put(nodeId, new ConcurrentHashMap<>(nodeClock));
        });
        return copy;
    }

    public synchronized Map<String, Integer> getVector(String nodeId) {
        return matrix.get(nodeId);
    }

    public synchronized void addNode(String nodeId) {
        matrix.putIfAbsent(nodeId, new ConcurrentHashMap<>());
    }

    public synchronized void removeNode(String nodeId) {
        matrix.remove(nodeId);
        matrix.values().forEach(nodeClock -> nodeClock.remove(nodeId));
    }

    public void Main() {
        
    }
}