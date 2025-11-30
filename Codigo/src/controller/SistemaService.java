package controller;

import biometric.ILeitorBiometrico;
import biometric.LeitorBiometricoMock;

import java.io.File;

public class SistemaService {
    private ILeitorBiometrico leitorBiometrico;

    public SistemaService() {
        this.leitorBiometrico = new LeitorBiometricoMock();
    }

    public boolean verificarIntegridadeArquivos() {
        String[] arquivos = {"usuarios.dat", "eventos.dat", "registros.dat"};
        boolean todosExistem = true;
        for (String arquivo : arquivos) {
            File file = new File(arquivo);
            if (!file.exists()) {
                todosExistem = false;
                try {
                    file.createNewFile();
                    // Ignorar retorno
                } catch (Exception e) {
                    return false;
                }
            }
        }
        if (!todosExistem) {
            System.out.println("Banco de dados local inicializado.");
            // Exibir login e senha do admin
            System.out.println("Usuário admin cadastrado: login=admin, senha=admin");
        }
        return true;
    }

    public boolean verificarLeitor() {
        return leitorBiometrico.isDisponivel();
    }

    public boolean verificarConexao() {
        // Simular verificação de internet, sempre true por enquanto
        return true;
    }

    public boolean executarVerificacoes() {
        if (!verificarIntegridadeArquivos()) {
            System.err.println("Erro: Falha na integridade dos arquivos.");
            return false;
        }
        if (!verificarLeitor()) {
            System.err.println("Erro: Leitor biométrico não disponível.");
            return false;
        }
        if (!verificarConexao()) {
            System.err.println("Erro: Sem conexão com a internet.");
            return false;
        }
        return true;
    }
}