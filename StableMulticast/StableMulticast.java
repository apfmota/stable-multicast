package StableMulticast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Iterator;

public class StableMulticast {
    private final String MULTICAST_IP = "230.0.0.1";
    private final int MULTICAST_PORT = 4446;
    private final int UDP_BUFFER_SIZE = 1024;
    private final int DISCOVERY_DELAY = 2000;

    private IStableMulticast client;
    private String id;
    private int localPort;

    private ArrayList<Message> messageBuffer = new ArrayList<Message>();
    private MatrixClock matrix = new MatrixClock();
    private Set<String> groupMembers = ConcurrentHashMap.newKeySet();
    

    public StableMulticast(String ip, Integer port, IStableMulticast client) {
        this.client = client;
        this.id = ip + ":" + port.toString(); // talvez seja melhor pega o IP do localhost
        this.localPort = port;

        groupMembers.add(id);
        matrix.addProcess(id);

        // Thread de descoberta
        new Thread(() -> escutarMulticast(ip, port)).start();

        // Thread de recepção de mensagens
        new Thread(() -> escutarMensagensUnicast()).start();

        // Thread para enviar DISCOVERY periodicamente
        new Thread(() -> enviarPresencaPeriodicamente(ip, port)).start();
    }

    public void msend(String msg, IStableMulticast client) {
        Map<String,Integer> vec = matrix.getVector(this.id);
        Message m = new Message(msg, this.id, vec);

        List<String> targets = new ArrayList<String>(groupMembers);

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enviar mensagem para todos? (s/n)");
        String userInput = scanner.nextLine();

        if(userInput.equalsIgnoreCase("s")) {
            for (String target : targets ) {
                sendMessage(target, m);
            }
        } else {
            for (String target : targets) {
                System.out.println("Enviar para " + target + "? (s/n/q)");
                String r = scanner.nextLine();
                if (r.equalsIgnoreCase("s")) {
                    sendMessage(target, m);
                } else if (r.equalsIgnoreCase("q")) {
                    break;
                }
            }
        }
        matrix.tick(this.id);
    }

    private void sendMessage(String dest, Message m) {
        try {
            String[] part = dest.split(":");
            InetAddress addr = InetAddress.getByName(part[0]);
            int port = Integer.parseInt(part[1]);

            DatagramSocket socket = new DatagramSocket();
            byte[] data = m.serialize();
            DatagramPacket packet = new DatagramPacket(data, data.length, addr, port);
            socket.send(packet);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erro ao enviar mensagem para " + dest + ": ");
        }
    }

    private void escutarMulticast(String ip, int port) {
        // cria um socket multicast e entra no grupo para ouvir
         try (MulticastSocket socket = new MulticastSocket(MULTICAST_PORT)) {
            InetAddress group = InetAddress.getByName(MULTICAST_IP);
            socket.joinGroup(group);
            // inicia um buffer de mensagens
            byte[] buf = new byte[UDP_BUFFER_SIZE];

            while (true) { // fica ouvindo
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String msg = new String(packet.getData(), 0, packet.getLength());

                if (msg.startsWith("DISCOVERY:")) { // se a mensagem que recebeu é um DISCOVERY
                    String senderId = msg.substring("DISCOVERY:".length());
                    if (!groupMembers.contains(senderId)){
                        groupMembers.add(senderId); // adiciona o novo membro no grupo
                        matrix.addProcess(senderId); // adiciona o novo membro na matriz
                        System.out.println("encontrado" + senderId);
                    }
                    
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erro ao receber mensagens no multicast :");
        }
    }

    private void escutarMensagensUnicast() {
        // DatagramSocket.bind(portaAnunciada) + receive mensagens
        try (DatagramSocket socket = new DatagramSocket(localPort)) {
            byte[] buf = new byte[1024];
            while (true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                Message m = Message.deserialize(packet.getData());
                messageBuffer.add(m);
                String senderId = m.getSenderId();
                Map<String, Integer> receivedVC = m.getVectorClock();

                matrix.update(senderId, receivedVC);

                if(!senderId.equals(id)) {
                    matrix.tickAt(id, senderId);
                    // matrix.update(id, receivedVC); 
                }

                deliverMessages(); //Tenta enviar as mensagens prontas para o client
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void enviarPresencaPeriodicamente(String ip, int port) {
        // socket.send("DISCOVERY:<ip>:<porta>")
        try {
            DatagramSocket socket = new DatagramSocket();
            InetAddress groupAddrs = InetAddress.getByName(MULTICAST_IP);
            byte[] buf = ("DISCOVERY:" + id).getBytes();
            while (true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length, groupAddrs, MULTICAST_PORT);
                socket.send(packet);
                Thread.sleep(DISCOVERY_DELAY);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erro ao enviar DISCOVERY ao grupo");
        }
    }

    private void deliverMessages() {
        System.out.println("matriz atual");
        System.out.println(this.matrix.prettyPrint()); 
        Iterator<Message> it = messageBuffer.iterator();
        while (it.hasNext()) {
            Message m = it.next();
            if(matrix.isReadyToDeliver(this.id, m)){
                client.deliver(m.getContent());
                it.remove();
            }else{ //TODO remover esta linha
                System.out.println("nao está pronta ainda");
            }
        }
    }
}
