package controller;

import biometric.ILeitorBiometrico;
import biometric.LeitorBiometricoMock;
import model.*;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SistemaController {
    private static SistemaController instance;
    private List<Usuario> usuarios;
    private List<Evento> eventos;
    private List<RegistroPresenca> registros;
    private ILeitorBiometrico leitorBiometrico;

    private SistemaController() {
        usuarios = new ArrayList<>();
        eventos = new ArrayList<>();
        registros = new ArrayList<>();
        leitorBiometrico = new LeitorBiometricoMock();
    }

    public static SistemaController getInstance() {
        if (instance == null) {
            instance = new SistemaController();
        }
        return instance;
    }

    public void inicializar() {
        carregarUsuarios();
        carregarEventos();
        carregarRegistros();
        if (usuarios.isEmpty()) {
            // Criar admin padrão
            Usuario admin = new Usuario("Admin", "00000000000", "admin@admin.com", "admin", "admin", TipoUsuario.ADMIN);
            usuarios.add(admin);
            salvarTudo();
        }
    }

    public void salvarTudo() {
        salvarUsuarios();
        salvarEventos();
        salvarRegistros();
    }

    private void salvarUsuarios() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("usuarios.dat"))) {
            oos.writeObject(usuarios);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void salvarEventos() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("eventos.dat"))) {
            oos.writeObject(eventos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void salvarRegistros() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("registros.dat"))) {
            oos.writeObject(registros);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void carregarUsuarios() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("usuarios.dat"))) {
            usuarios = (List<Usuario>) ois.readObject();
        } catch (FileNotFoundException e) {
            // Arquivo não existe, ok
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void carregarEventos() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("eventos.dat"))) {
            eventos = (List<Evento>) ois.readObject();
        } catch (FileNotFoundException e) {
            // Arquivo não existe, ok
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private void carregarRegistros() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("registros.dat"))) {
            registros = (List<RegistroPresenca>) ois.readObject();
        } catch (FileNotFoundException e) {
            // Arquivo não existe, ok
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void cadastrarUsuario(String nome, String cpf, String email, String senha, String codigoBiometrico, TipoUsuario tipo) throws Exception {
        // Validações
        for (Usuario u : usuarios) {
            if (u.getEmail().equals(email)) {
                throw new Exception("Email já cadastrado.");
            }
            if (u.getCpf().equals(cpf)) {
                throw new Exception("CPF já cadastrado.");
            }
        }
        Usuario usuario = new Usuario(nome, cpf, email, senha, codigoBiometrico, tipo);
        usuarios.add(usuario);
        salvarTudo();
    }

    public void criarEvento(String titulo, LocalDateTime dataInicio, LocalDateTime dataFim, String local, StatusEvento status) throws Exception {
        Evento evento = new Evento(titulo, dataInicio, dataFim, local, status);
        criarEvento(evento, false);
    }

    public void criarEvento(Evento evento, boolean ignorarValidacaoData) throws Exception {
        validarECriarEvento(evento, ignorarValidacaoData);
    }

    private void validarECriarEvento(Evento evento, boolean ignorarValidacaoData) throws Exception {
        // Validações de datas
        if (evento.getDataInicio() == null) {
            throw new Exception("Data de início não pode ser nula.");
        }
        if (evento.getDataFim().isBefore(evento.getDataInicio()) || evento.getDataFim().isEqual(evento.getDataInicio())) {
            throw new Exception("Data de fim deve ser posterior à data de início.");
        }
        // Opcional: não no passado, a menos que ignorado
        if (!ignorarValidacaoData && evento.getDataInicio().isBefore(LocalDateTime.now())) {
            throw new Exception("Não é possível criar eventos no passado.");
        }

        // Validação de conflitos
        for (Evento existente : eventos) {
            if (existente.getLocal().equals(evento.getLocal())) {
                LocalDateTime existenteInicio = existente.getDataInicio();
                LocalDateTime existenteFim = existente.getDataFim();
                if (evento.getDataInicio().isBefore(existenteFim) && evento.getDataFim().isAfter(existenteInicio)) {
                    LogController.getInstance().registrarLog("Sistema", "CONFLITO_AGENDA", "Conflito detectado para local " + evento.getLocal() + " com evento existente");
                    throw new EventoConflitoException("Conflito de horário detectado para o local " + evento.getLocal() + " no horário " + existenteInicio + " - " + existenteFim);
                }
            }
        }

        eventos.add(evento);
        salvarTudo();
        LogController.getInstance().registrarLog("Sistema", "CRIAR_EVENTO", "Evento criado: " + evento.getTitulo());
    }

    public String registrarPresenca(String codigoBiometrico, Evento eventoAtual) throws Exception {
        // Busca usuário
        Usuario usuario = null;
        for (Usuario u : usuarios) {
            if (u.getCodigoBiometrico().equals(codigoBiometrico)) {
                usuario = u;
                break;
            }
        }
        if (usuario == null) {
            throw new Exception("Usuário não encontrado.");
        }

        // Verifica duplicidade
        for (RegistroPresenca reg : registros) {
            if (reg.getUsuario().equals(usuario) && reg.getEvento().equals(eventoAtual)) {
                throw new Exception("Presença já registrada!");
            }
        }

        // Verifica janela de tempo
        LocalDateTime agora = LocalDateTime.now();
        if (agora.isBefore(eventoAtual.getDataInicio()) || agora.isAfter(eventoAtual.getDataFim())) {
            throw new Exception("Evento não está ocorrendo agora.");
        }

        // Sucesso
        RegistroPresenca registro = new RegistroPresenca(usuario, eventoAtual, agora);
        registros.add(registro);
        salvarTudo();

        return usuario.getNome() + " - " + agora.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public Usuario autenticar(String email, String senha) {
        for (Usuario u : usuarios) {
            if (u.getEmail().equals(email) && u.getSenha().equals(senha)) {
                return u;
            }
        }
        return null;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public List<Evento> getEventos() {
        return eventos;
    }

    public List<RegistroPresenca> getRegistros() {
        return registros;
    }

    public ILeitorBiometrico getLeitorBiometrico() {
        return leitorBiometrico;
    }
}