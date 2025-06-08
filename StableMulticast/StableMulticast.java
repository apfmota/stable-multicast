package StableMulticast;

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
    private final String multicastGroupIP = "10.0.0.1";
    private final int multicastPort = 4446;

    private final IStableMulticast client;
    private final String id;

    private final ArrayList<Message> messageBuffer = new ArrayList<Message>();
    private final MatrixClock matrix = new MatrixClock();
    private final Set<String> group = ConcurrentHashMap.newKeySet();
    

    public StableMulticast(String multicastIP, Integer multicastPort, IStableMulticast client) {
        this.client = client;
        this.id = multicastIP + ":" + multicastPort.toString(); // talvez seja melhor pega o IP do localhost

        // Thread de descoberta
        new Thread(() -> escutarMulticast(multicastIP, multicastPort)).start();

        // Thread de recepção de mensagens
        new Thread(() -> escutarMensagensUnicast()).start();

        // Thread para enviar DISCOVERY periodicamente
        new Thread(() -> enviarPresencaPeriodicamente(multicastIP, multicastPort)).start();
    }

    public void msend(String msg, IStableMulticast client) {
        Map<String,Integer> vec = matrix.getVector(this.id);
        Message m = new Message(msg, this.id, vec);

        List<String> targets = new ArrayList<String>(group);

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
        
        messageBuffer.add(m);
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
            System.err.println("Erro ao enviar mensagem para " + dest + ": " + e);
        }
    }

    private void escutarMulticast(String ip, int port) {
        // joinGroup + receive DISCOVERY + atualizar grupo
    }

    private void escutarMensagensUnicast() {
        // DatagramSocket.bind(portaAnunciada) + receive mensagens

        deliverMessages(); //Tenta enviar as mensagens prontas para o client
    }

    private void enviarPresencaPeriodicamente(String ip, int port) {
        // socket.send("DISCOVERY:<ip>:<porta>")
    }

    private void deliverMessages() {
        Iterator<Message> it = messageBuffer.iterator();
        while (it.hasNext()) {
            Message m = it.next();
            if(matrix.isReadyToDeliver(this.id, m)){
                client.deliver(m.getContent());
                it.remove();
            }
        }
    }
}
