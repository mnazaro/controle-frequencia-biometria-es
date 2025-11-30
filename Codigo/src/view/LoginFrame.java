package view;

import controller.SistemaController;
import model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField senhaField;
    private JButton loginButton;
    private JButton registrarButton;
    private SistemaController controller;

    public LoginFrame() {
        controller = SistemaController.getInstance();
        controller.inicializar();

        setTitle("Login - Sistema de Controle de Frequência");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Email:"));
        emailField = new JTextField();
        panel.add(emailField);

        panel.add(new JLabel("Senha:"));
        senhaField = new JPasswordField();
        panel.add(senhaField);

        loginButton = new JButton("Entrar");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                autenticar();
            }
        });
        panel.add(loginButton);

        registrarButton = new JButton("Registar-se");
        registrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirCadastro();
            }
        });
        panel.add(registrarButton);

        add(panel);
    }

    private void autenticar() {
        String email = emailField.getText();
        String senha = new String(senhaField.getPassword());
        Usuario usuario = controller.autenticar(email, senha);
        if (usuario != null) {
            // Abrir MainFrame
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
            dispose(); // Fechar login
        } else {
            JOptionPane.showMessageDialog(this, "Credenciais Inválidas", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirCadastro() {
        CadastroFrame cadastro = new CadastroFrame(this, false);
        cadastro.setVisible(true);
    }
}