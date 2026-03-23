/**
 * Handler de cliente executado em thread separada.
 * Recebe o fuso horario do cliente via socket TCP, consulta
 * o horario atual e retorna a resposta formatada.
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
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ClientHandler implements Runnable {

    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss z");

    private final Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        long threadId = Thread.currentThread().threadId();
        String threadNome = Thread.currentThread().getName();
        String clienteInfo = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();

        System.out.println("[Thread-ID: " + threadId + " | Nome: " + threadNome
                + "] Atendendo cliente " + clienteInfo);

        try (
            BufferedReader entrada = new BufferedReader(
                    new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            PrintWriter saida = new PrintWriter(
                    new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)
        ) {
            String fusoHorario = entrada.readLine();
            String resposta = obterHoraAtual(fusoHorario);
            saida.println(resposta);

            System.out.println("[Thread-ID: " + threadId + " | Nome: " + threadNome
                    + "] Fuso: " + fusoHorario + " -> Resposta: " + resposta);

        } catch (IOException e) {
            System.err.println("[Thread-ID: " + threadId + " | Nome: " + threadNome
                    + "] Erro ao atender cliente " + clienteInfo + ": " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("[Thread-ID: " + threadId + " | Nome: " + threadNome
                        + "] Erro ao fechar socket: " + e.getMessage());
            }
            System.out.println("[Thread-ID: " + threadId + " | Nome: " + threadNome
                    + "] Cliente " + clienteInfo + " finalizado.\n");
        }
    }

    private String obterHoraAtual(String fusoHorario) {
        if (fusoHorario == null || fusoHorario.isBlank()) {
            return "Erro: nenhum fuso horario recebido.";
        }
        try {
            ZoneId zona = ZoneId.of(fusoHorario.trim());
            ZonedDateTime agora = ZonedDateTime.now(zona);
            return agora.format(FORMATO_DATA);
        } catch (Exception e) {
            return "Erro: fuso horario invalido '" + fusoHorario
                    + "'. Use formatos como: America/Sao_Paulo, Europe/London, Asia/Tokyo";
        }
    }
}
