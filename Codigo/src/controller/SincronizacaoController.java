package controller;

import model.RegistroPresenca;
import model.StatusSincronizacao;

import javax.swing.*;
import java.util.List;

public class SincronizacaoController {
    private SistemaController sistemaController;
    private MockCloudService cloudService;

    public SincronizacaoController() {
        this.sistemaController = SistemaController.getInstance();
        this.cloudService = new MockCloudService();
    }

    public void sincronizarPendentes(Runnable onComplete) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private int sincronizados = 0;
            private int falhas = 0;

            @Override
            protected Void doInBackground() throws Exception {
                LogController.getInstance().registrarLog("Sistema", "SYNC_INICIO", "Iniciando sincronização de registros pendentes");

                List<RegistroPresenca> registros = sistemaController.getRegistros();
                for (RegistroPresenca reg : registros) {
                    if (reg.getStatus() == StatusSincronizacao.PENDENTE) {
                        boolean sucesso = cloudService.enviarRegistro(reg);
                        if (sucesso) {
                            reg.setStatus(StatusSincronizacao.SINCRONIZADO);
                            sincronizados++;
                        } else {
                            falhas++;
                            LogController.getInstance().registrarLog("Sistema", "SYNC_FALHA", "Falha ao sincronizar registro de " + reg.getUsuario().getNome() + " no evento " + reg.getEvento().getTitulo());
                        }
                    }
                }

                // Salvar mudanças
                sistemaController.salvarTudo();

                LogController.getInstance().registrarLog("Sistema", "SYNC_FIM", sincronizados + " registros sincronizados, " + falhas + " falhas");

                return null;
            }

            @Override
            protected void done() {
                JOptionPane.showMessageDialog(null, sincronizados + " registros sincronizados com sucesso. Falhas: " + falhas, "Sincronização Completa", JOptionPane.INFORMATION_MESSAGE);
                if (onComplete != null) {
                    onComplete.run();
                }
            }
        };
        worker.execute();
    }

    public int getPendentesCount() {
        int count = 0;
        for (RegistroPresenca reg : sistemaController.getRegistros()) {
            if (reg.getStatus() == StatusSincronizacao.PENDENTE) {
                count++;
            }
        }
        return count;
    }
}