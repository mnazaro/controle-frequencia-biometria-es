package view;

import controller.EventoConflitoException;
import controller.SistemaController;
import model.StatusEvento;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class EventoFrame extends JDialog {
    private JTextField tituloField;
    private JTextField dataInicioField;
    private JTextField dataFimField;
    private JTextField localField;
    private JComboBox<StatusEvento> statusCombo;
    private JButton salvarButton;
    private SistemaController controller;

    public EventoFrame(JFrame parent) {
        super(parent, "Criar Evento", true);
        controller = SistemaController.getInstance();

        setSize(400, 250);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(6, 2, 10, 10));

        add(new JLabel("Título:"));
        tituloField = new JTextField();
        add(tituloField);

        add(new JLabel("Data Início (dd/MM/yyyy HH:mm):"));
        dataInicioField = new JTextField();
        add(dataInicioField);

        add(new JLabel("Data Fim (dd/MM/yyyy HH:mm):"));
        dataFimField = new JTextField();
        add(dataFimField);

        add(new JLabel("Local:"));
        localField = new JTextField();
        add(localField);

        add(new JLabel("Status:"));
        statusCombo = new JComboBox<>(StatusEvento.values());
        add(statusCombo);

        salvarButton = new JButton("Salvar");
        salvarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                salvarEvento();
            }
        });
        add(new JLabel()); // Espaço vazio
        add(salvarButton);
    }

    private void salvarEvento() {
        String titulo = tituloField.getText();
        String dataInicioStr = dataInicioField.getText();
        String dataFimStr = dataFimField.getText();
        String local = localField.getText();
        StatusEvento status = (StatusEvento) statusCombo.getSelectedItem();

        if (titulo.isEmpty() || dataInicioStr.isEmpty() || dataFimStr.isEmpty() || local.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos.");
            return;
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            LocalDateTime dataInicio = LocalDateTime.parse(dataInicioStr, formatter);
            LocalDateTime dataFim = LocalDateTime.parse(dataFimStr, formatter);

            controller.criarEvento(titulo, dataInicio, dataFim, local, status);
            JOptionPane.showMessageDialog(this, "Evento criado com sucesso!");
            dispose();
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Formato de data inválido. Use dd/MM/yyyy HH:mm");
        } catch (EventoConflitoException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Conflito de Horário", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }
}