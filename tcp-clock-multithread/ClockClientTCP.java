/**
 * Cliente TCP para consulta de Relogio Mundial.
 * Conecta ao servidor multithread na porta 9878 e solicita
 * a hora atual em um fuso horario informado pelo usuario.
 * Digite "sair" para encerrar.
 *
 * @author Otavio
 * @version 1.0
 * @since 2026-03-23
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ClockClientTCP {

    private static final String SERVIDOR = "localhost";
    private static final int PORTA = 9878;

    public static void main(String[] args) {
        System.out.println("=== Cliente de Relogio Mundial (TCP Multithread) ===");
        System.out.println("Fusos validos: America/Sao_Paulo, Europe/London, Asia/Tokyo, US/Eastern, UTC");
        System.out.println("Digite 'sair' para encerrar.\n");

        Scanner scanner = new Scanner(System.in);

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

            try (Socket socket = new Socket(SERVIDOR, PORTA)) {
                PrintWriter saida = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
                BufferedReader entrada = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

                saida.println(fusoHorario);
                String resposta = entrada.readLine();

                if (resposta != null) {
                    System.out.println("Resposta do servidor: " + resposta + "\n");
                } else {
                    System.out.println("Servidor nao retornou resposta.\n");
                }

            } catch (ConnectException e) {
                System.err.println("Erro: nao foi possivel conectar ao servidor. Verifique se esta em execucao.\n");
            } catch (IOException e) {
                System.err.println("Erro de comunicacao: " + e.getMessage() + "\n");
            }
        }

        scanner.close();
    }
}
