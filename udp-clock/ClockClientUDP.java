/**
 * Cliente UDP de Relogio Mundial.
 * Envia um fuso horario ao servidor e exibe a data/hora
 * atual retornada. Implementa timeout de 5 segundos.
 * Digite "sair" para encerrar.
 *
 * @author Otavio
 * @version 1.0
 * @since 2026-03-23
 */

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ClockClientUDP {

    private static final int PORTA_SERVIDOR = 9876;
    private static final int TAMANHO_BUFFER = 1024;
    private static final int TIMEOUT_MS = 5000;

    public static void main(String[] args) {
        System.out.println("=== Cliente UDP de Relogio Mundial ===");
        System.out.println("Fusos validos: America/Sao_Paulo, Europe/London, Asia/Tokyo, US/Eastern, UTC");
        System.out.println("Digite 'sair' para encerrar.\n");

        try (DatagramSocket socket = new DatagramSocket();
             Scanner scanner = new Scanner(System.in)) {

            socket.setSoTimeout(TIMEOUT_MS);
            InetAddress enderecoServidor = InetAddress.getByName("localhost");

            while (true) {
                System.out.print("Digite o fuso horario: ");
                String fusoHorario = scanner.nextLine().trim();

                if (fusoHorario.equalsIgnoreCase("sair")) {
                    System.out.println("Cliente encerrado. Ate logo!");
                    break;
                }

                if (fusoHorario.isEmpty()) {
                    System.out.println("Entrada vazia. Tente novamente.\n");
                    continue;
                }

                try {
                    byte[] dadosEnvio = fusoHorario.getBytes(StandardCharsets.UTF_8);
                    DatagramPacket pacoteEnvio = new DatagramPacket(dadosEnvio,
                            dadosEnvio.length, enderecoServidor, PORTA_SERVIDOR);
                    socket.send(pacoteEnvio);

                    byte[] bufferReceber = new byte[TAMANHO_BUFFER];
                    DatagramPacket pacoteRecebido = new DatagramPacket(bufferReceber, bufferReceber.length);
                    socket.receive(pacoteRecebido);

                    String resposta = new String(pacoteRecebido.getData(), 0,
                            pacoteRecebido.getLength(), StandardCharsets.UTF_8);
                    System.out.println("Resposta do servidor: " + resposta + "\n");

                } catch (SocketTimeoutException e) {
                    System.out.println("Servidor ocupado ou offline. Tente novamente.\n");
                } catch (Exception e) {
                    System.err.println("Erro de comunicacao: " + e.getMessage() + "\n");
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao iniciar o cliente: " + e.getMessage());
        }
    }
}
