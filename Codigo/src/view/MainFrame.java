package view;

import controller.SistemaController;
import model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MainFrame extends JFrame {
    private SistemaController controller;
    private JTabbedPane tabbedPane;
    private JTable usuariosTable;
    private JTable eventosTable;
    private JComboBox<Evento> eventoComboBox;
    private JLabel statusLabel;

    public MainFrame() {
        controller = SistemaController.getInstance();
        controller.inicializar();

        setTitle("Sistema de Controle de Frequência Biométrica");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Tentar FlatLaf
        try {
            // UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatDarkLaf());
            // Como não temos dependência, usar padrão
        } catch (Exception e) {
            // Usar padrão
        }

        tabbedPane = new JTabbedPane();

        // Aba Gerenciar Usuários
        JPanel usuariosPanel = createUsuariosPanel();
        tabbedPane.addTab("Gerenciar Usuários", usuariosPanel);

        // Aba Gerenciar Eventos
        JPanel eventosPanel = createEventosPanel();
        tabbedPane.addTab("Gerenciar Eventos", eventosPanel);

        // Aba Totem de Presença
        JPanel totemPanel = createTotemPanel();
        tabbedPane.addTab("Totem de Presença", totemPanel);

        add(tabbedPane);
    }

    private JPanel createUsuariosPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Tabela
        String[] columnNames = {"Nome", "CPF", "Email", "Código Biométrico", "Tipo"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        usuariosTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(usuariosTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Botão Cadastrar
        JButton cadastrarButton = new JButton("Cadastrar Usuário");
        cadastrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cadastrarUsuario();
            }
        });
        panel.add(cadastrarButton, BorderLayout.SOUTH);

        atualizarUsuariosTable();

        return panel;
    }

    private void atualizarUsuariosTable() {
        DefaultTableModel model = (DefaultTableModel) usuariosTable.getModel();
        model.setRowCount(0);
        for (Usuario u : controller.getUsuarios()) {
            model.addRow(new Object[]{u.getNome(), u.getCpf(), u.getEmail(), u.getCodigoBiometrico(), u.getTipo()});
        }
    }

    private void cadastrarUsuario() {
        CadastroFrame cadastro = new CadastroFrame(this, true);
        cadastro.setVisible(true);
        atualizarUsuariosTable();
    }

    private JPanel createEventosPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Tabela
        String[] columnNames = {"Título", "Data Início", "Data Fim", "Local", "Status"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        eventosTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(eventosTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Botão Criar
        JButton criarButton = new JButton("Criar Evento");
        criarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                criarEvento();
            }
        });
        panel.add(criarButton, BorderLayout.SOUTH);

        atualizarEventosTable();

        return panel;
    }

    private void atualizarEventosTable() {
        DefaultTableModel model = (DefaultTableModel) eventosTable.getModel();
        model.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime agora = LocalDateTime.now();
        for (Evento ev : controller.getEventos()) {
            String statusDisplay = calcularStatus(ev, agora);
            model.addRow(new Object[]{ev.getTitulo(), ev.getDataInicio().format(formatter), ev.getDataFim().format(formatter), ev.getLocal(), statusDisplay});
        }
    }

    private String calcularStatus(Evento ev, LocalDateTime agora) {
        if (agora.isBefore(ev.getDataInicio())) {
            return "AGENDADO";
        } else if (agora.isAfter(ev.getDataFim())) {
            return "ENCERRADO";
        } else {
            return "EM ANDAMENTO";
        }
    }

    private void criarEvento() {
        EventoFrame eventoFrame = new EventoFrame(this);
        eventoFrame.setVisible(true);
        atualizarEventosTable();
        atualizarEventoComboBox();
    }

    private JPanel createTotemPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // ComboBox para eventos
        eventoComboBox = new JComboBox<>();
        atualizarEventoComboBox();
        panel.add(eventoComboBox, BorderLayout.NORTH);

        // Botão gigante
        JButton registrarButton = new JButton("REGISTRAR PRESENÇA");
        registrarButton.setFont(new Font("Arial", Font.BOLD, 24));
        registrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registrarPresenca();
            }
        });
        panel.add(registrarButton, BorderLayout.CENTER);

        // Label para status
        statusLabel = new JLabel("Pronto", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        panel.add(statusLabel, BorderLayout.SOUTH);

        return panel;
    }

    private void atualizarEventoComboBox() {
        eventoComboBox.removeAllItems();
        for (Evento ev : controller.getEventos()) {
            if (ev.getStatus() == StatusEvento.ABERTO) {
                eventoComboBox.addItem(ev);
            }
        }
    }

    private void registrarPresenca() {
        Evento evento = (Evento) eventoComboBox.getSelectedItem();
        if (evento == null) {
            statusLabel.setText("Nenhum evento selecionado");
            statusLabel.setForeground(Color.RED);
            return;
        }
        try {
            controller.registrarPresenca(evento);
            statusLabel.setText("Presença registrada com sucesso!");
            statusLabel.setForeground(Color.GREEN);
        } catch (Exception ex) {
            statusLabel.setText("Erro: " + ex.getMessage());
            statusLabel.setForeground(Color.RED);
        }
    }
}