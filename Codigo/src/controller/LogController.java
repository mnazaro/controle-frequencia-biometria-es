package controller;

import model.LogEntry;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LogController {
    private static LogController instance;
    private List<LogEntry> logs;

    private LogController() {
        System.out.println("Iniciando Logs...");
        logs = new ArrayList<>();
        try {
            carregarLogs();
            System.out.println("Logs Carregados.");
        } catch (Exception e) {
            System.err.println("Erro ao carregar logs, iniciando vazio: " + e.getMessage());
            e.printStackTrace();
            // Inicia vazio e continua
        }
    }

    public static synchronized LogController getInstance() {
        if (instance == null) {
            instance = new LogController();
        }
        return instance;
    }

    public void registrarLog(String usuario, String acao, String detalhes) {
        try {
            LogEntry entry = new LogEntry(LocalDateTime.now(), usuario, acao, detalhes);
            logs.add(entry);
            salvarLogs();
        } catch (Exception e) {
            System.err.println("Erro ao registrar log: " + e.getMessage());
            e.printStackTrace();
            // Não lança, apenas loga o erro
        }
    }

    public List<LogEntry> getLogs() {
        return new ArrayList<>(logs); // Retorna cópia para evitar modificações externas
    }

    @SuppressWarnings("unchecked")
    private void carregarLogs() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("logs.dat"))) {
            logs = (List<LogEntry>) ois.readObject();
        } catch (FileNotFoundException e) {
            // Arquivo não existe, ok - lista vazia
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Erro ao carregar logs", e); // Lança para ser capturado no construtor
        }
    }

    private void salvarLogs() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("logs.dat"))) {
            oos.writeObject(logs);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar logs", e); // Lança para ser capturado em registrarLog
        }
    }
}