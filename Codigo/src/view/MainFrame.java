package view;

import controller.SincronizacaoController;
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
    private SincronizacaoController syncController;

    public MainFrame() {
        controller = SistemaController.getInstance();
        controller.inicializar();
        syncController = new SincronizacaoController();

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

        // Aba Relatórios
        RelatorioPanel relatorioPanel = new RelatorioPanel();
        tabbedPane.addTab("Relatórios", relatorioPanel);

        // Toolbar superior
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        JButton syncButton = new JButton("Sincronizar com Nuvem");
        syncButton.addActionListener(e -> sincronizarDados(syncButton));
        atualizarBotaoSync(syncButton);
        toolBar.add(syncButton);

        // Aba Logs de Auditoria
        LogsPanel logsPanel = new LogsPanel();
        tabbedPane.addTab("Logs de Auditoria", logsPanel);

        // Layout principal
        setLayout(new BorderLayout());
        add(toolBar, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
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
    }

    private JPanel createTotemPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JButton abrirTotemButton = new JButton("ABRIR TOTEM DE PRESENÇA");
        abrirTotemButton.setFont(new Font("Arial", Font.BOLD, 20));
        abrirTotemButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TotemFrame totem = new TotemFrame();
                totem.setVisible(true);
            }
        });
        panel.add(abrirTotemButton, BorderLayout.CENTER);

        return panel;
    }

    private void sincronizarDados(JButton button) {
        button.setEnabled(false);
        button.setText("Enviando...");
        syncController.sincronizarPendentes(() -> {
            atualizarBotaoSync(button);
        });
    }

    private void atualizarBotaoSync(JButton button) {
        int pendentes = syncController.getPendentesCount();
        if (pendentes > 0) {
            button.setText("Sincronizar com Nuvem (" + pendentes + " pendentes)");
        } else {
            button.setText("Sincronizar com Nuvem");
        }
        button.setEnabled(true);
    }
}