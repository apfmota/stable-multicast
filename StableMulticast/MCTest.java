package StableMulticast;

import java.util.Map;

public class MCTest {
    public static void main(String[] args) {
        MatrixClock matrix = new MatrixClock();

        // Adiciona os processos
        matrix.addProcess("P1");
        matrix.addProcess("P2");
        matrix.addProcess("P3");

        System.out.println("Estado inicial:");
        System.out.println("Após P1.tick:");
        System.out.println(matrix.prettyPrint());

        // Evento local em P1
        matrix.tick("P1");
        System.out.println("Após P1.tick:");
        System.out.println(matrix.prettyPrint());

        // Evento local em P2
        matrix.tick("P2");
        matrix.tick("P2");  // 2 eventos em P2
        System.out.println("Após P2.tick x2:");
        System.out.println(matrix.prettyPrint());

        // Simula envio de mensagem de P2 → P1
        // P1 atualiza seu estado com o vetor de P2
        Map<String, Integer> messageVector = matrix.getVector("P2");
        matrix.update("P1", messageVector);
        System.out.println(matrix.prettyPrint());

        // Evento local em P3
        matrix.tick("P3");
        System.out.println("Após P3.tick:");
        System.out.println(matrix.prettyPrint());

        // Simula envio de mensagem de P1 → P3
        messageVector = matrix.getVector("P1");
        matrix.update("P3", messageVector); 
        System.out.println(matrix.prettyPrint());

        // Simula envio de mensagem de P3 → P1
        messageVector = matrix.getVector("P3");
        matrix.update("P1", messageVector); 
        System.out.println(matrix.prettyPrint());

        // Simula envio de mensagem de P3 → P2
        matrix.tick("P3");
        messageVector = matrix.getVector("P3");
        matrix.update("P2", messageVector); 
        System.out.println(matrix.prettyPrint());

                // Simula envio de mensagem de P2 → P1
        messageVector = matrix.getVector("P2");
        matrix.update("P1", messageVector); 
        System.out.println(matrix.prettyPrint());

        
    }
}
