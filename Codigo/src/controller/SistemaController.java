package controller;

import biometric.ILeitorBiometrico;
import biometric.LeitorBiometricoMock;
import model.*;

import java.io.*;
import java.time.LocalDateTime;
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
        // Validações de datas
        if (dataInicio == null) {
            throw new Exception("Data de início não pode ser nula.");
        }
        if (dataFim.isBefore(dataInicio) || dataFim.isEqual(dataInicio)) {
            throw new Exception("Data de fim deve ser posterior à data de início.");
        }
        // Opcional: não no passado
        if (dataInicio.isBefore(LocalDateTime.now())) {
            throw new Exception("Não é possível criar eventos no passado.");
        }

        // Validação de conflitos
        for (Evento existente : eventos) {
            if (existente.getLocal().equals(local)) {
                LocalDateTime existenteInicio = existente.getDataInicio();
                LocalDateTime existenteFim = existente.getDataFim();
                if (dataInicio.isBefore(existenteFim) && dataFim.isAfter(existenteInicio)) {
                    throw new EventoConflitoException("Conflito de horário detectado para o local " + local + " no horário " + existenteInicio + " - " + existenteFim);
                }
            }
        }

        Evento evento = new Evento(titulo, dataInicio, dataFim, local, status);
        eventos.add(evento);
        salvarTudo();
    }

    public void registrarPresenca(Evento evento) throws Exception {
        String codigo = leitorBiometrico.capturarDigital();
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new Exception("Captura cancelada ou vazia.");
        }
        Usuario usuario = null;
        for (Usuario u : usuarios) {
            if (u.getCodigoBiometrico().equals(codigo)) {
                usuario = u;
                break;
            }
        }
        if (usuario == null) {
            throw new Exception("Usuário não encontrado.");
        }
        LocalDateTime agora = LocalDateTime.now();
        if (agora.isBefore(evento.getDataInicio()) || agora.isAfter(evento.getDataFim())) {
            throw new Exception("Fora do horário.");
        }
        RegistroPresenca registro = new RegistroPresenca(usuario, evento, agora);
        registros.add(registro);
        salvarTudo();
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

    public ILeitorBiometrico getLeitorBiometrico() {
        return leitorBiometrico;
    }
}