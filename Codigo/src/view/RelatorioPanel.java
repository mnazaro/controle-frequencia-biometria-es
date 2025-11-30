package view;

import controller.RelatorioController;
import controller.SistemaController;
import model.Evento;
import model.RegistroPresenca;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class RelatorioPanel extends JPanel {
    private static final String FILTRO_EVENTO = "Por Evento";
    private static final String FILTRO_NOME = "Por Nome do Usuário";
    private static final String FILTRO_DATA = "Por Data";

    private transient SistemaController sistemaController;
    private transient RelatorioController relatorioController;
    private JComboBox<String> criterioComboBox;
    private JPanel filtroPanel;
    private JComboBox<Evento> eventoComboBox;
    private JTextField nomeField;
    private JTextField dataField;
    private JButton gerarButton;
    private JTable relatorioTable;
    private DefaultTableModel tableModel;
    private JLabel resumoLabel;
    private JButton exportarButton;

    public RelatorioPanel() {
        sistemaController = SistemaController.getInstance();
        relatorioController = new RelatorioController();

        setLayout(new BorderLayout());

        // Painel superior
        JPanel topPanel = new JPanel(new FlowLayout());
        criterioComboBox = new JComboBox<>(new String[]{FILTRO_EVENTO, FILTRO_NOME, FILTRO_DATA});
        criterioComboBox.addActionListener(e -> atualizarFiltroPanel());
        topPanel.add(new JLabel("Critério de Filtro:"));
        topPanel.add(criterioComboBox);

        filtroPanel = new JPanel(new FlowLayout());
        topPanel.add(filtroPanel);

        gerarButton = new JButton("Gerar Relatório");
        gerarButton.addActionListener(e -> gerarRelatorio());
        topPanel.add(gerarButton);

        add(topPanel, BorderLayout.NORTH);

        // Tabela
        String[] columnNames = {"Nome do Aluno", "CPF", "Evento", "Data/Hora", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0);
        relatorioTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(relatorioTable);
        add(scrollPane, BorderLayout.CENTER);

        // Painel inferior
        JPanel bottomPanel = new JPanel(new BorderLayout());
        resumoLabel = new JLabel("Selecione um critério e gere o relatório.", SwingConstants.CENTER);
        bottomPanel.add(resumoLabel, BorderLayout.CENTER);

        exportarButton = new JButton("Exportar CSV");
        exportarButton.addActionListener(e -> exportarCSV());
        bottomPanel.add(exportarButton, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);

        atualizarFiltroPanel();
    }

    private void atualizarFiltroPanel() {
        filtroPanel.removeAll();
        String criterio = (String) criterioComboBox.getSelectedItem();
        if (FILTRO_EVENTO.equals(criterio)) {
            eventoComboBox = new JComboBox<>();
            atualizarEventos();
            filtroPanel.add(new JLabel("Selecionar Evento:"));
            filtroPanel.add(eventoComboBox);
        } else if (FILTRO_NOME.equals(criterio)) {
            nomeField = new JTextField(15);
            filtroPanel.add(new JLabel("Nome do Usuário:"));
            filtroPanel.add(nomeField);
        } else if (FILTRO_DATA.equals(criterio)) {
            dataField = new JTextField(10);
            filtroPanel.add(new JLabel("Data (dd/MM/yyyy):"));
            filtroPanel.add(dataField);
        }
        filtroPanel.revalidate();
        filtroPanel.repaint();
    }

    private void atualizarEventos() {
        if (eventoComboBox != null) {
            eventoComboBox.removeAllItems();
            for (Evento ev : sistemaController.getEventos()) {
                eventoComboBox.addItem(ev);
            }
        }
    }

    private void gerarRelatorio() {
        String criterio = (String) criterioComboBox.getSelectedItem();
        List<RegistroPresenca> registros = null;

        if (FILTRO_EVENTO.equals(criterio)) {
            Evento evento = (Evento) eventoComboBox.getSelectedItem();
            if (evento == null) {
                JOptionPane.showMessageDialog(this, "Selecione um evento.");
                return;
            }
            registros = relatorioController.filtrarPorEvento(evento);
        } else if (FILTRO_NOME.equals(criterio)) {
            String nome = nomeField.getText().trim();
            if (nome.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Digite um nome de usuário.");
                return;
            }
            registros = relatorioController.filtrarPorNomeUsuario(nome);
        } else if (FILTRO_DATA.equals(criterio)) {
            String dataStr = dataField.getText().trim();
            if (dataStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Digite uma data.");
                return;
            }
            try {
                LocalDate data = LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                registros = relatorioController.filtrarPorData(data);
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "Formato de data inválido. Use dd/MM/yyyy.");
                return;
            }
        }

        if (registros == null || registros.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum registro encontrado.");
            tableModel.setRowCount(0);
            resumoLabel.setText("Total de Registros: 0");
            return;
        }

        tableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        for (RegistroPresenca reg : registros) {
            tableModel.addRow(new Object[]{
                reg.getUsuario().getNome(),
                reg.getUsuario().getCpf(),
                reg.getEvento().getTitulo(),
                reg.getDataHoraRegistro().format(formatter),
                reg.getStatus().toString()
            });
        }

        String resumo = relatorioController.gerarResumo(registros);
        resumoLabel.setText(resumo);
    }

    private void exportarCSV() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Gere um relatório primeiro.");
            return;
        }

        // Como é complexo reconstruir a lista, vamos exportar os dados da tabela
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File("relatorio.csv"));
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String caminho = fileChooser.getSelectedFile().getAbsolutePath();
            try {
                try (java.io.FileWriter writer = new java.io.FileWriter(caminho)) {
                    writer.write("Nome;CPF;Evento;DataHora;Status\n");
                    for (int i = 0; i < tableModel.getRowCount(); i++) {
                        String linha = tableModel.getValueAt(i, 0) + ";" +
                                       tableModel.getValueAt(i, 1) + ";" +
                                       tableModel.getValueAt(i, 2) + ";" +
                                       tableModel.getValueAt(i, 3) + ";" +
                                       tableModel.getValueAt(i, 4) + "\n";
                        writer.write(linha);
                    }
                }
                JOptionPane.showMessageDialog(this, "Relatório exportado com sucesso para " + caminho);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Erro ao exportar: " + ex.getMessage());
            }
        }
    }
}