package view;

import controller.SistemaController;
import model.Evento;
import model.StatusEvento;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;

public class TotemFrame extends JFrame {
    private SistemaController controller;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JPanel panelConfiguracao;
    private JPanel panelSessaoAtiva;
    private JComboBox<Evento> eventoComboBox;
    private JButton iniciarButton;
    private JLabel eventoLabel;
    private JLabel statusLabel;
    private JButton simularButton;
    private Timer resetTimer;
    private Evento eventoAtual;

    public TotemFrame() {
        controller = SistemaController.getInstance();

        setTitle("Totem de Presença");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Timer para reset
        resetTimer = new Timer(3000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetTotem();
            }
        });
        resetTimer.setRepeats(false);

        // CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Painel de Configuração
        panelConfiguracao = new JPanel(new FlowLayout());
        eventoComboBox = new JComboBox<>();
        atualizarEventos();
        iniciarButton = new JButton("INICIAR SESSÃO DE PRESENÇA");
        iniciarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                iniciarSessao();
            }
        });
        panelConfiguracao.add(new JLabel("Selecionar Evento:"));
        panelConfiguracao.add(eventoComboBox);
        panelConfiguracao.add(iniciarButton);

        // Painel de Sessão Ativa
        panelSessaoAtiva = new JPanel(new BorderLayout());
        panelSessaoAtiva.setOpaque(true);
        panelSessaoAtiva.setBackground(Color.LIGHT_GRAY);

        eventoLabel = new JLabel("", SwingConstants.CENTER);
        eventoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panelSessaoAtiva.add(eventoLabel, BorderLayout.NORTH);

        statusLabel = new JLabel("PRONTO PARA LEITURA... ENCOSTE O DEDO", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        statusLabel.setOpaque(true);
        statusLabel.setBackground(Color.CYAN);
        panelSessaoAtiva.add(statusLabel, BorderLayout.CENTER);

        simularButton = new JButton("SIMULAR LEITURA BIOMÉTRICA");
        simularButton.setFont(new Font("Arial", Font.BOLD, 20));
        simularButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                simularLeitura();
            }
        });
        panelSessaoAtiva.add(simularButton, BorderLayout.SOUTH);

        // Adicionar cards
        mainPanel.add(panelConfiguracao, "CONFIG");
        mainPanel.add(panelSessaoAtiva, "SESSAO_ATIVA");

        add(mainPanel);
        cardLayout.show(mainPanel, "CONFIG");
    }

    private void atualizarEventos() {
        eventoComboBox.removeAllItems();
        LocalDateTime hoje = LocalDateTime.now();
        for (Evento ev : controller.getEventos()) {
            if (ev.getStatus() == StatusEvento.ABERTO && ev.getDataInicio().toLocalDate().equals(hoje.toLocalDate())) {
                eventoComboBox.addItem(ev);
            }
        }
    }

    private void iniciarSessao() {
        Evento evento = (Evento) eventoComboBox.getSelectedItem();
        if (evento == null) {
            JOptionPane.showMessageDialog(this, "Selecione um evento.");
            return;
        }
        eventoAtual = evento;
        eventoLabel.setText("EVENTO: " + evento.getTitulo());
        resetTotem();
        cardLayout.show(mainPanel, "SESSAO_ATIVA");
        revalidate();
        repaint();
    }

    private void simularLeitura() {
        if (eventoAtual == null) return;

        try {
            String codigo = controller.getLeitorBiometrico().capturarDigital();
            if (codigo == null || codigo.trim().isEmpty()) {
                throw new Exception("Captura cancelada.");
            }
            String resultado = controller.registrarPresenca(codigo, eventoAtual);
            statusLabel.setText("PRESENÇA REGISTRADA: " + resultado);
            statusLabel.setBackground(Color.GREEN);
            // Beep opcional
            Toolkit.getDefaultToolkit().beep();
        } catch (Exception ex) {
            statusLabel.setText("ERRO: " + ex.getMessage());
            statusLabel.setBackground(Color.RED);
        }
        resetTimer.restart();
    }

    private void resetTotem() {
        statusLabel.setText("PRONTO PARA LEITURA... ENCOSTE O DEDO");
        statusLabel.setBackground(Color.CYAN);
    }
}