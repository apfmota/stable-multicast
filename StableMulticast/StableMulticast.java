package StableMulticast;

import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.List;

public class StableMulticast {
    private IStableMulticast client;
    private List<Integer> grupo = new ArrayList();

    public StableMulticast(String multicastIP, int multicastPort, IStableMulticast client) {
        this.client = client;

        // Thread de descoberta
        new Thread(() -> escutarMulticast(multicastIP, multicastPort)).start();

        // Thread de recepção de mensagens
        new Thread(() -> escutarMensagensUnicast()).start();

        // Thread para enviar DISCOVERY periodicamente
        new Thread(() -> enviarPresencaPeriodicamente(multicastIP, multicastPort)).start();
    }

    public void msend(String msg) {
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
