package view;

import controller.SistemaController;
import model.TipoUsuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CadastroFrame extends JDialog {
    private JTextField nomeField;
    private JTextField emailField;
    private JTextField cpfField;
    private JPasswordField senhaField;
    private JComboBox<TipoUsuario> tipoCombo;
    private JButton capturarButton;
    private JButton salvarButton;
    private SistemaController controller;
    private String codigoBiometrico;
    private boolean isModoAdmin;

    public CadastroFrame(JFrame parent, boolean isModoAdmin) {
        super(parent, "Cadastro de Usuário", true);
        this.isModoAdmin = isModoAdmin;
        controller = SistemaController.getInstance();

        setSize(400, isModoAdmin ? 300 : 250);
        setLocationRelativeTo(parent);
        int rows = isModoAdmin ? 7 : 6;
        setLayout(new GridLayout(rows, 2, 10, 10));

        add(new JLabel("Nome:"));
        nomeField = new JTextField();
        add(nomeField);

        add(new JLabel("Email:"));
        emailField = new JTextField();
        add(emailField);

        add(new JLabel("CPF:"));
        cpfField = new JTextField();
        add(cpfField);

        add(new JLabel("Senha:"));
        senhaField = new JPasswordField();
        add(senhaField);

        if (isModoAdmin) {
            add(new JLabel("Tipo:"));
            tipoCombo = new JComboBox<>(TipoUsuario.values());
            add(tipoCombo);
        } else {
            // Forçar ALUNO
            tipoCombo = new JComboBox<>();
            tipoCombo.setSelectedItem(TipoUsuario.ALUNO);
            tipoCombo.setEnabled(false);
        }

        capturarButton = new JButton("Registar Digital");
        capturarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                capturarDigital();
            }
        });
        add(capturarButton);

        salvarButton = new JButton("Salvar");
        salvarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                salvarUsuario();
            }
        });
        add(salvarButton);
    }

    private void capturarDigital() {
        try {
            codigoBiometrico = controller.getLeitorBiometrico().capturarDigital();
            if (codigoBiometrico != null && !codigoBiometrico.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Digital capturada com sucesso!");
            } else {
                JOptionPane.showMessageDialog(this, "Captura cancelada.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro na captura: " + ex.getMessage());
        }
    }

    private void salvarUsuario() {
        String nome = nomeField.getText();
        String email = emailField.getText();
        String cpf = cpfField.getText();
        String senha = new String(senhaField.getPassword());
        TipoUsuario tipo = isModoAdmin ? (TipoUsuario) tipoCombo.getSelectedItem() : TipoUsuario.ALUNO;

        if (nome.isEmpty() || email.isEmpty() || cpf.isEmpty() || senha.isEmpty() || codigoBiometrico == null) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos e capture a digital.");
            return;
        }

        try {
            controller.cadastrarUsuario(nome, cpf, email, senha, codigoBiometrico, tipo);
            JOptionPane.showMessageDialog(this, "Usuário cadastrado com sucesso!");
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
        }
    }
}