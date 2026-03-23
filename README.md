# Sistema Distribuido de Relogio Mundial (TCP/UDP)

## Resumo do Projeto

Atividade pratica de **Sistemas Distribuidos** que explora a comunicacao entre processos utilizando **Sockets em Java**. O projeto implementa tres versoes de um sistema cliente-servidor de relogio mundial, demonstrando as diferencas fundamentais entre os protocolos **UDP** e **TCP**, alem do modelo de servidor concorrente com **Multithread**.

O cliente envia o identificador de um fuso horario (ex: `America/Sao_Paulo`) e o servidor responde com a data/hora atual naquela regiao, utilizando a API `java.time.ZonedDateTime`.

---

## Tecnologias

- **Java 17+** (JDK)
- `java.net` (DatagramSocket, ServerSocket, Socket)
- `java.time` (ZoneId, ZonedDateTime, DateTimeFormatter)
- `java.io` (BufferedReader, PrintWriter, InputStreamReader, OutputStreamWriter)

---

## Estrutura do Repositorio

```
├── udp-clock/                        # Versao 1: UDP
│   ├── ClockServerUDP.java
│   └── ClockClientUDP.java
├── tcp-clock-simple/                 # Versao 2: TCP Single-Thread
│   ├── ClockServerTCP.java
│   └── ClockClientTCP.java
├── tcp-clock-multithread/            # Versao 3: TCP Multithread
│   ├── ClockServerTCPMultithread.java
│   ├── ClientHandler.java
│   └── ClockClientTCP.java
└── README.md
```

---

## Instrucoes de Execucao

### Pre-requisitos

- Java Development Kit (JDK) 17 ou superior instalado
- Terminal/Prompt de Comando

### Versao 1: UDP (`udp-clock/`)

```bash
cd udp-clock/
javac -encoding UTF-8 *.java

# Terminal 1 - Servidor:
java ClockServerUDP

# Terminal 2 - Cliente:
java ClockClientUDP
```

### Versao 2: TCP Single-Thread (`tcp-clock-simple/`)

```bash
cd tcp-clock-simple/
javac -encoding UTF-8 *.java

# Terminal 1 - Servidor:
java ClockServerTCP

# Terminal 2 - Cliente:
java ClockClientTCP
```

### Versao 3: TCP Multithread (`tcp-clock-multithread/`)

```bash
cd tcp-clock-multithread/
javac -encoding UTF-8 *.java

# Terminal 1 - Servidor:
java ClockServerTCPMultithread

# Terminal 2, 3, 4... - Clientes (podem conectar simultaneamente):
java ClockClientTCP
```

### Exemplos de Fusos Horarios Validos

| Fuso Horario         | Regiao                  |
|----------------------|-------------------------|
| `America/Sao_Paulo`  | Brasil (Brasilia)       |
| `Europe/London`      | Reino Unido             |
| `Asia/Tokyo`         | Japao                   |
| `US/Eastern`         | EUA (Costa Leste)       |
| `Europe/Paris`       | Franca                  |
| `Australia/Sydney`   | Australia               |
| `UTC`                | Tempo Universal         |

---

## Analise Tecnica: TCP Single-Thread vs. TCP Multithread

### Versao 2 - TCP Single-Thread (Sequencial)

```
Cliente A ──connect──> Servidor ──process──> Resposta A
                         |
Cliente B ──connect──> [FILA DO SO - aguarda]
                         |
              (Somente apos Cliente A ser atendido)
                         |
Cliente B ────────────> Servidor ──process──> Resposta B
```

**Comportamento:** O servidor utiliza apenas a thread principal. Ao receber uma conexao via `accept()`, ele le a requisicao, processa e responde **antes** de voltar a aceitar novas conexoes. Enquanto um cliente e atendido, todos os outros ficam na **fila de espera do Sistema Operacional** (backlog, padrao ~50 conexoes).

**Limitacao:** Se o processamento de um cliente for demorado (ex: operacao de I/O lenta), **todos os demais clientes ficam bloqueados**, mesmo que suas requisicoes sejam simples e rapidas.

### Versao 3 - TCP Multithread (Concorrente)

```
Cliente A ──connect──> Servidor ──accept()──> Thread "Cliente-1" ──process──> Resposta A
                         |
Cliente B ──connect──> Servidor ──accept()──> Thread "Cliente-2" ──process──> Resposta B
                         |
Cliente C ──connect──> Servidor ──accept()──> Thread "Cliente-3" ──process──> Resposta C
                         |
              (Main thread NUNCA processa, apenas delega)
```

**Comportamento:** Ao receber um `accept()`, o servidor **imediatamente** cria uma nova Thread (`ClientHandler`) para processar aquela requisicao e retorna ao loop de `accept()`. A thread principal **nunca bloqueia** no processamento — ela apenas aceita conexoes e delega.

**Vantagem:** Multiplos clientes sao atendidos **simultaneamente**. Um cliente lento nao afeta os demais, pois cada um executa em sua propria thread.

### Comparacao Direta

| Aspecto              | Single-Thread (V2)            | Multithread (V3)                    |
|----------------------|-------------------------------|-------------------------------------|
| **Concorrencia**     | 1 cliente por vez             | N clientes simultaneos              |
| **Thread principal** | Aceita, processa, responde    | Apenas aceita e delega              |
| **Bloqueio**         | Cliente lento bloqueia todos  | Cliente lento bloqueia so sua thread|
| **Escalabilidade**   | Baixa                         | Alta (limitada por threads do SO)   |
| **Complexidade**     | Simples (1 arquivo)           | Moderada (classe Runnable separada) |
| **Isolamento**       | Erro pode afetar o loop       | Erro isolado na thread do cliente   |

### Conclusao

A versao **Multithread** e estritamente superior em cenarios com multiplos clientes simultaneos. Mesmo para operacoes rapidas como consulta de horario, a arquitetura concorrente elimina a **serializacao artificial** imposta pelo modelo single-thread, resultando em **menor latencia** e **maior throughput** sob carga.

---

## Autor

- **Otavio**
- Disciplina: Sistemas Distribuidos
- Data: Março de 2026
