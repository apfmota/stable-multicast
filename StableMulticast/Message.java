package StableMulticast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

public class Message implements Serializable {
    private final String content;
    private final String senderId;
    private final Map<String, Integer> vectorClock;
    
    public Message(String content, String sender, Map<String, Integer> vectorClock) {
        this.content = Objects.requireNonNull(content);
        this.senderId = Objects.requireNonNull(sender);
        this.vectorClock = Map.copyOf(vectorClock);
    }

    // public byte[] serialize() {
    //     try {
    //         ByteArrayOutputStream baos = new ByteArrayOutputStream();
    //         ObjectOutputStream oos = new ObjectOutputStream(baos);
    //         oos.writeObject(this);
    //         oos.close();
    //         return baos.toByteArray();
    //     } catch (IOException e) {
    //         throw new RuntimeException("Falha na serialização", e);
    //     }
    // }
    
    // public static Message deserialize(byte[] data) {
    //     try {
    //         ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
    //         return (Message) ois.readObject();
    //     } catch (IOException | ClassNotFoundException e) {
    //         throw new RuntimeException("Falha na desserialização", e);
    //     }
    // }
    
    public String getContent() { return content; }
    public String getSenderId() { return senderId; }
    public Map<String, Integer> getVectorClock() { return vectorClock; }
}