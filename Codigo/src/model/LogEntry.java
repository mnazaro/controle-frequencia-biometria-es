package model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class LogEntry implements Serializable {
    private LocalDateTime dataHora;
    private String usuarioResponsavel;
    private String acao;
    private String detalhes;

    public LogEntry(LocalDateTime dataHora, String usuarioResponsavel, String acao, String detalhes) {
        this.dataHora = dataHora;
        this.usuarioResponsavel = usuarioResponsavel;
        this.acao = acao;
        this.detalhes = detalhes;
    }

    // Getters
    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public String getUsuarioResponsavel() {
        return usuarioResponsavel;
    }

    public String getAcao() {
        return acao;
    }

    public String getDetalhes() {
        return detalhes;
    }

    @Override
    public String toString() {
        return "LogEntry{" +
                "dataHora=" + dataHora +
                ", usuarioResponsavel='" + usuarioResponsavel + '\'' +
                ", acao='" + acao + '\'' +
                ", detalhes='" + detalhes + '\'' +
                '}';
    }
}