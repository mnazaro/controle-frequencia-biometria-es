package model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class RegistroPresenca implements Serializable {
    private Usuario usuario;
    private Evento evento;
    private LocalDateTime dataHoraRegistro;
    private StatusSincronizacao status;

    public RegistroPresenca(Usuario usuario, Evento evento, LocalDateTime dataHoraRegistro) {
        this.usuario = usuario;
        this.evento = evento;
        this.dataHoraRegistro = dataHoraRegistro;
        this.status = StatusSincronizacao.PENDENTE; // Padrão
    }

    // Getters and Setters
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Evento getEvento() { return evento; }
    public void setEvento(Evento evento) { this.evento = evento; }

    public LocalDateTime getDataHoraRegistro() { return dataHoraRegistro; }
    public void setDataHoraRegistro(LocalDateTime dataHoraRegistro) { this.dataHoraRegistro = dataHoraRegistro; }

    public StatusSincronizacao getStatus() { return status; }
    public void setStatus(StatusSincronizacao status) { this.status = status; }

    @Override
    public String toString() {
        return usuario.getNome() + " - " + evento.getTitulo() + " - " + dataHoraRegistro + " - " + status;
    }
}