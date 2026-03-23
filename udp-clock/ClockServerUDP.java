/**
 * Servidor UDP de Relogio Mundial.
 * Recebe um fuso horario via datagrama UDP e responde
 * com a data/hora atual formatada naquele fuso.
 *
 * @author Otavio
 * @version 1.0
 * @since 2026-03-23
 */

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class ClockServerUDP {

    private static final int PORTA = 9876;
    private static final int TAMANHO_BUFFER = 1024;
    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss z");

    public static void main(String[] args) {
        System.out.println("=== Servidor UDP de Relogio Mundial ===");
        System.out.println("Porta: " + PORTA);
        System.out.println("Aguardando requisicoes...\n");

        try (DatagramSocket socket = new DatagramSocket(PORTA)) {
            while (true) {
                try {
                    byte[] bufferReceber = new byte[TAMANHO_BUFFER];
                    DatagramPacket pacoteRecebido = new DatagramPacket(bufferReceber, bufferReceber.length);
                    socket.receive(pacoteRecebido);

                    String fusoHorario = new String(pacoteRecebido.getData(), 0,
                            pacoteRecebido.getLength(), StandardCharsets.UTF_8).trim();
                    InetAddress enderecoCliente = pacoteRecebido.getAddress();
                    int portaCliente = pacoteRecebido.getPort();

                    System.out.println("[LOG] Requisicao de " + enderecoCliente.getHostAddress()
                            + ":" + portaCliente + " - Fuso: " + fusoHorario);

                    String resposta = obterHoraAtual(fusoHorario);

                    byte[] dadosResposta = resposta.getBytes(StandardCharsets.UTF_8);
                    DatagramPacket pacoteResposta = new DatagramPacket(dadosResposta,
                            dadosResposta.length, enderecoCliente, portaCliente);
                    socket.send(pacoteResposta);

                    System.out.println("[LOG] Resposta enviada: " + resposta + "\n");
                } catch (Exception e) {
                    System.err.println("[ERRO] Falha ao processar requisicao: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("[ERRO FATAL] Nao foi possivel iniciar o servidor: " + e.getMessage());
        }
    }

    private static String obterHoraAtual(String fusoHorario) {
        if (fusoHorario == null || fusoHorario.isBlank()) {
            return "Erro: nenhum fuso horario recebido.";
        }
        try {
            ZoneId zona = ZoneId.of(fusoHorario);
            ZonedDateTime agora = ZonedDateTime.now(zona);
            return agora.format(FORMATO_DATA);
        } catch (Exception e) {
            return "Erro: fuso horario invalido '" + fusoHorario
                    + "'. Use formatos como: America/Sao_Paulo, Europe/London, Asia/Tokyo";
        }
    }
}
