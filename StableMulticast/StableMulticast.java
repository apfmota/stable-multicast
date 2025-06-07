package StableMulticast;

import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class StableMulticast {
    private IStableMulticast client;
    private String id;
    private List<Integer> grupo = new ArrayList(); // pra que serve isso?
    
    private MatrixClock matrix;
    private ArrayList<Message> messageBuffer;

    public StableMulticast(String multicastIP, Integer multicastPort, IStableMulticast client) {
        this.client = client;
        this.id = multicastIP + ":" + multicastPort.toString(); // talvez seja melhor pega o IP do localhost

        this.matrix = new MatrixClock();

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
}
