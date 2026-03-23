/**
 * Servidor TCP de Relogio Mundial (Single-Thread).
 * Aceita conexoes na porta 9877 e retorna a data/hora
 * atual no fuso horario solicitado pelo cliente.
 * Limitacao: atende apenas um cliente por vez.
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
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ClockServerTCP {

    private static final int PORTA = 9877;
    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss z");

    public static void main(String[] args) {
        System.out.println("=== Servidor TCP de Relogio Mundial (Single-Thread) ===");
        System.out.println("Porta: " + PORTA);
        System.out.println("Aguardando conexoes...\n");

        try (ServerSocket serverSocket = new ServerSocket(PORTA)) {
            while (true) {
                try (Socket clienteSocket = serverSocket.accept()) {
                    String clienteInfo = clienteSocket.getInetAddress().getHostAddress()
                            + ":" + clienteSocket.getPort();
                    System.out.println("[LOG] Conexao recebida de " + clienteInfo);

                    BufferedReader entrada = new BufferedReader(
                            new InputStreamReader(clienteSocket.getInputStream(), StandardCharsets.UTF_8));
                    PrintWriter saida = new PrintWriter(
                            new OutputStreamWriter(clienteSocket.getOutputStream(), StandardCharsets.UTF_8), true);

                    String fusoHorario = entrada.readLine();
                    String resposta = obterHoraAtual(fusoHorario);
                    saida.println(resposta);

                    System.out.println("[LOG] Fuso solicitado: " + fusoHorario);
                    System.out.println("[LOG] Resposta enviada: " + resposta);
                    System.out.println("[LOG] Conexao com " + clienteInfo + " encerrada.\n");

                } catch (IOException e) {
                    System.err.println("[ERRO] Falha ao atender cliente: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("[ERRO FATAL] Nao foi possivel iniciar o servidor: " + e.getMessage());
        }
    }

    private static String obterHoraAtual(String fusoHorario) {
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
