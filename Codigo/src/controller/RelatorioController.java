package controller;

import model.Evento;
import model.RegistroPresenca;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class RelatorioController {
    private SistemaController sistemaController;

    public RelatorioController() {
        this.sistemaController = SistemaController.getInstance();
    }

    public List<RegistroPresenca> filtrarPorEvento(Evento evento) {
        List<RegistroPresenca> registrosFiltrados = new ArrayList<>();
        for (RegistroPresenca reg : sistemaController.getRegistros()) {
            if (reg.getEvento().equals(evento)) {
                registrosFiltrados.add(reg);
            }
        }
        return registrosFiltrados;
    }

    public List<RegistroPresenca> filtrarPorNomeUsuario(String trechoNome) {
        List<RegistroPresenca> registrosFiltrados = new ArrayList<>();
        for (RegistroPresenca reg : sistemaController.getRegistros()) {
            if (reg.getUsuario().getNome().toLowerCase().contains(trechoNome.toLowerCase())) {
                registrosFiltrados.add(reg);
            }
        }
        return registrosFiltrados;
    }

    public List<RegistroPresenca> filtrarPorData(LocalDate data) {
        List<RegistroPresenca> registrosFiltrados = new ArrayList<>();
        for (RegistroPresenca reg : sistemaController.getRegistros()) {
            if (reg.getDataHoraRegistro().toLocalDate().equals(data)) {
                registrosFiltrados.add(reg);
            }
        }
        return registrosFiltrados;
    }

    public String gerarResumo(List<RegistroPresenca> dados) {
        return "Total de Registros Encontrados: " + dados.size();
    }

    public void exportarParaCSV(List<RegistroPresenca> dados, String caminhoArquivo) throws IOException {
        try (FileWriter writer = new FileWriter(caminhoArquivo)) {
            writer.write("Nome;CPF;Evento;DataHora;Status\n");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            for (RegistroPresenca reg : dados) {
                String linha = reg.getUsuario().getNome() + ";" +
                               reg.getUsuario().getCpf() + ";" +
                               reg.getEvento().getTitulo() + ";" +
                               reg.getDataHoraRegistro().format(formatter) + ";" +
                               reg.getStatus() + "\n";
                writer.write(linha);
            }
        }
        LogController.getInstance().registrarLog("Sistema", "EXPORTACAO_DADOS", "Relatório exportado para " + caminhoArquivo + " com " + dados.size() + " registros");
    }
}