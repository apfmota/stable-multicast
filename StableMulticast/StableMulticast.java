package StableMulticast;

import java.lang.reflect.Member;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public class StableMulticast {
    private final String multicastGroupIP = "10.0.0.1";
    private final int multicastPort = 4446;

    private final IStableMulticast client;
    private final String id;

    private final ArrayList<Message> messageBuffer = new ArrayList<Message>();
    private final MatrixClock matrix = new MatrixClock();
    private final Set<Member> group = ConcurrentHashMap.newKeySet();
    

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

        //TODO
            //Checar se o usuário quer enviar para todos os membros
                //Se sim, multicast
                //Senão, abrir diálogo para seleção de targets
        
        // Envia via unicast para todos da lista grupo
    }

    private void escutarMulticast(String ip, int port) {
        // joinGroup + receive DISCOVERY + atualizar grupo
    }

    private void escutarMensagensUnicast() {
        // DatagramSocket.bind(portaAnunciada) + receive mensagens
    }

    private void enviarPresencaPeriodicamente(String ip, int port) {
        // socket.send("DISCOVERY:<ip>:<porta>")
    }

    private void deliverMessages() {
        for (Message m : messageBuffer) {
            if(matrix.isReadyToDeliver(this.id, m)){
                client.deliver(m.getContent());
            }
        }
    }
}
