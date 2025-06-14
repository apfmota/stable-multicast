package StableMulticast;


import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class VectorClock {
    private final Map<String, Integer> clock;

    public VectorClock() {
        this.clock = new HashMap<>();
    }

    public VectorClock(Map<String,Integer> s) {
        this.clock = new HashMap<>(s);
    }

    public synchronized void tick(String processId) {
        clock.put(processId, clock.getOrDefault(processId, -1) + 1);
    }

    public synchronized void update(Map<String, Integer> receivedClock) {
        for (Map.Entry<String, Integer> entry : receivedClock.entrySet()) {
            String pId = entry.getKey();
            Integer receivedTime = entry.getValue();
            Integer currentTime = clock.getOrDefault(pId, -1);
            clock.put(pId, Math.max(currentTime, receivedTime));
        }
    }

    public synchronized boolean isReadyToDeliver(Message m) {
        Map<String, Integer> msgClock = m.getVectorClock();
        String senderId = m.getSenderId();

        for (Map.Entry<String, Integer> entry : msgClock.entrySet()) {
            String pId = entry.getKey();
            Integer receivedTime = entry.getValue();
            Integer localTime = clock.getOrDefault(pId, -1);

            if (receivedTime == localTime + 1){
                return false;
            }
        }
        return true;
    }

    public synchronized Map<String, Integer> copy() {
        return new HashMap<>(clock);
    }

    public synchronized void vecAddProcess(String processId) {
        clock.putIfAbsent(processId, -1);
    }

    public synchronized Map<String, Integer> getVectorClock(){
        return clock;
    }

    @Override
    public synchronized String toString() {
        return clock.toString();
    }
}