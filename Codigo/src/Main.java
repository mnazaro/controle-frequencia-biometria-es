import controller.SistemaService;
import view.LoginFrame;
import view.SplashScreen;

import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    private static LoginFrame loginFrame; // Referência para manter viva

    public static void main(String[] args) {
        // Global Exception Handler para capturar UncaughtExceptions
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            System.err.println("Uncaught Exception in thread " + t.getName() + ":");
            e.printStackTrace(System.err);
            JOptionPane.showMessageDialog(null, "Erro Crítico: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            System.exit(1); // Encerra a aplicação após mostrar o erro
        });

        SwingUtilities.invokeLater(() -> {
            SplashScreen splash = new SplashScreen();
            splash.showSplash();

            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    SistemaService service = new SistemaService();

                    splash.setProgress(10, "A verificar integridade...");
                    boolean integridade = service.verificarIntegridadeArquivos();
                    Thread.sleep(1000);

                    if (!integridade) {
                        JOptionPane.showMessageDialog(null, "Erro crítico: Falha na inicialização.", "Erro", JOptionPane.ERROR_MESSAGE);
                        System.exit(1);
                    }

                    splash.setProgress(50, "A testar driver biométrico...");
                    boolean leitor = service.verificarLeitor();
                    Thread.sleep(1000);

                    if (!leitor) {
                        JOptionPane.showMessageDialog(null, "Erro crítico: Leitor biométrico indisponível.", "Erro", JOptionPane.ERROR_MESSAGE);
                        System.exit(1);
                    }

                    splash.setProgress(90, "A conectar...");
                    boolean conexao = service.verificarConexao();
                    Thread.sleep(1000);

                    if (!conexao) {
                        JOptionPane.showMessageDialog(null, "Erro crítico: Sem conexão.", "Erro", JOptionPane.ERROR_MESSAGE);
                        System.exit(1);
                    }

                    splash.setProgress(100, "Inicialização completa.");
                    Thread.sleep(500);

                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get(); // Verificar se houve exceção no doInBackground
                        splash.hideSplash();
                        // Abrir LoginFrame
                        loginFrame = new LoginFrame();
                        loginFrame.setVisible(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Erro na inicialização: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                        System.exit(1);
                    }
                }
            };

            worker.execute();
        });
    }
}