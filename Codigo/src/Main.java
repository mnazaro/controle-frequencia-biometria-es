import controller.SistemaService;
import view.LoginFrame;
import view.SplashScreen;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
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
                    splash.hideSplash();
                    // Abrir LoginFrame
                    LoginFrame loginFrame = new LoginFrame();
                    loginFrame.setVisible(true);
                }
            };

            worker.execute();
        });
    }
}