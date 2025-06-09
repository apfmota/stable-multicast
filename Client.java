import StableMulticast.IStableMulticast;
import StableMulticast.StableMulticast;

import java.util.ArrayList;

import java.util.Scanner;

public class Client implements IStableMulticast {
    private StableMulticast multicast;
    private ArrayList<String> receivedMessages = new ArrayList<String>();

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Uso: java Client <ip> <porta>");
            return;
        }

        String ip = args[0];
        int porta = Integer.parseInt(args[1]);

        Client client = new Client();
        client.multicast = new StableMulticast(ip, porta, client); // referência circular intencional
        client.lerTerminal(); // iniciar leitura manual do terminal
    }

    @Override
    public void deliver(String mensagem) {
        receivedMessages.add(mensagem);

        System.out.println("==================================");

        for (String m : receivedMessages){
            System.out.println(m);
        }
    }

    private void lerTerminal() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String linha = scanner.nextLine();
            if (!linha.isBlank()) {
                multicast.msend(linha, this);
            }
        }
    }
}
