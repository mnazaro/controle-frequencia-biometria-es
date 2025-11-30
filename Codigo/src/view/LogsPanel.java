package view;

import controller.LogController;
import model.LogEntry;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class LogsPanel extends JPanel {
    private transient LogController logController;
    private JTable logsTable;
    private DefaultTableModel tableModel;
    private JTextField filtroField;
    private transient TableRowSorter<DefaultTableModel> sorter;

    public LogsPanel() {
        logController = LogController.getInstance();

        setLayout(new BorderLayout());

        // Painel superior com filtro
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("Filtrar por Usuário ou Ação:"));
        filtroField = new JTextField(20);
        filtroField.addActionListener(e -> filtrarLogs());
        topPanel.add(filtroField);

        JButton atualizarButton = new JButton("Atualizar");
        atualizarButton.addActionListener(e -> carregarLogs());
        topPanel.add(atualizarButton);

        add(topPanel, BorderLayout.NORTH);

        // Tabela
        String[] columnNames = {"Data/Hora", "Usuário", "Ação", "Detalhes"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Não editável
            }
        };
        logsTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        logsTable.setRowSorter(sorter);
        // Ordenar por data decrescente (coluna 0)
        sorter.setSortKeys(java.util.List.of(new RowSorter.SortKey(0, SortOrder.DESCENDING)));

        JScrollPane scrollPane = new JScrollPane(logsTable);
        add(scrollPane, BorderLayout.CENTER);

        carregarLogs();
    }

    private void carregarLogs() {
        tableModel.setRowCount(0);
        List<LogEntry> logs = logController.getLogs();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        for (LogEntry log : logs) {
            tableModel.addRow(new Object[]{
                log.getDataHora().format(formatter),
                log.getUsuarioResponsavel(),
                log.getAcao(),
                log.getDetalhes()
            });
        }
    }

    private void filtrarLogs() {
        String filtro = filtroField.getText().toLowerCase();
        if (filtro.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + filtro, 1, 2)); // Filtrar colunas 1 (Usuário) e 2 (Ação)
        }
    }
}