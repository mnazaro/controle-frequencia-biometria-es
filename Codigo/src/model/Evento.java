package model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Evento implements Serializable {
    private String titulo;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private String local;
    private StatusEvento status;

    public Evento(String titulo, LocalDateTime dataInicio, LocalDateTime dataFim, String local, StatusEvento status) {
        this.titulo = titulo;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.local = local;
        this.status = status;
    }

    // Getters and Setters
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public LocalDateTime getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }

    public LocalDateTime getDataFim() { return dataFim; }
    public void setDataFim(LocalDateTime dataFim) { this.dataFim = dataFim; }

    public String getLocal() { return local; }
    public void setLocal(String local) { this.local = local; }

    public StatusEvento getStatus() { return status; }
    public void setStatus(StatusEvento status) { this.status = status; }

    @Override
    public String toString() {
        return titulo + " - " + local;
    }
}