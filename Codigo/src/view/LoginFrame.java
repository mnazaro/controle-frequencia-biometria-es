package view;

import controller.DataSeeder;
import controller.LogController;
import controller.SistemaController;
import model.TipoUsuario;
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
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Email:"));
        emailField = new JTextField();
        panel.add(emailField);

        panel.add(new JLabel("Senha:"));
        senhaField = new JPasswordField();
        panel.add(senhaField);

        loginButton = new JButton("Entrar");
        loginButton.addActionListener(e -> autenticar());
        panel.add(loginButton);

        registrarButton = new JButton("Registar-se");
        registrarButton.addActionListener(e -> abrirCadastro());
        panel.add(registrarButton);

        // Botão de reset para desenvolvimento
        JButton resetButton = new JButton("Reset DB");
        resetButton.setFont(new Font("Arial", Font.PLAIN, 10));
        resetButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Isso vai apagar todos os dados e gerar dados de demonstração. Continuar?", "Reset DB", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                DataSeeder.resetarDados();
                DataSeeder.gerarCargaInicial();
                JOptionPane.showMessageDialog(this, "Banco de dados resetado e semeado com dados de demonstração.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        panel.add(resetButton);

        panel.add(new JLabel()); // Espaço vazio

        add(panel);
    }

    private void autenticar() {
        String email = emailField.getText();
        String senha = new String(senhaField.getPassword());
        Usuario usuario = controller.autenticar(email, senha);
        if (usuario != null) {
            if (usuario.getTipo() == TipoUsuario.ADMIN) {
                LogController.getInstance().registrarLog(usuario.getNome(), "LOGIN_SUCESSO", "Login realizado com sucesso");
                // Abrir MainFrame
                MainFrame mainFrame = new MainFrame();
                mainFrame.setVisible(true);
                dispose(); // Fechar login
            } else {
                // Acesso negado para alunos
                JOptionPane.showMessageDialog(this, "Acesso Negado: Esta aplicação é restrita a Administradores. Alunos devem utilizar apenas o registro biométrico nos totens.", "Acesso Negado", JOptionPane.WARNING_MESSAGE);
                LogController.getInstance().registrarLog(usuario.getNome(), "TENTATIVA_ACESSO_RESTRITO", "Tentativa de acesso à aplicação administrativa por usuário do tipo ALUNO");
            }
        } else {
            LogController.getInstance().registrarLog(email, "LOGIN_FALHA", "Tentativa de login com email: " + email);
            JOptionPane.showMessageDialog(this, "Credenciais Inválidas", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirCadastro() {
        CadastroFrame cadastro = new CadastroFrame(this, false);
        cadastro.setVisible(true);
    }
}