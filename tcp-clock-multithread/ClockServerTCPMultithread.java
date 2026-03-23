/**
 * Servidor TCP Multithread de Relogio Mundial.
 * Aceita conexoes na porta 9878 e delega o atendimento
 * de cada cliente a uma Thread separada, permitindo
 * atendimento concorrente de multiplos clientes.
 *
 * @author Otavio
 * @version 1.0
 * @since 2026-03-23
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ClockServerTCPMultithread {

    private static final int PORTA = 9878;

    public static void main(String[] args) {
        int contadorClientes = 0;

        System.out.println("=== Servidor TCP de Relogio Mundial (Multithread) ===");
        System.out.println("Porta: " + PORTA);
        System.out.println("Aguardando conexoes...\n");

        try (ServerSocket serverSocket = new ServerSocket(PORTA)) {
            while (true) {
                try {
                    Socket clienteSocket = serverSocket.accept();
                    contadorClientes++;

                    String nomeThread = "Cliente-" + contadorClientes;
                    String clienteInfo = clienteSocket.getInetAddress().getHostAddress()
                            + ":" + clienteSocket.getPort();

                    System.out.println("[LOG] Nova conexao aceita: " + clienteInfo
                            + " -> Thread: " + nomeThread);

                    ClientHandler handler = new ClientHandler(clienteSocket);
                    Thread thread = new Thread(handler, nomeThread);
                    thread.start();

                } catch (IOException e) {
                    System.err.println("[ERRO] Falha ao aceitar conexao: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("[ERRO FATAL] Nao foi possivel iniciar o servidor: " + e.getMessage());
        }
    }
}
