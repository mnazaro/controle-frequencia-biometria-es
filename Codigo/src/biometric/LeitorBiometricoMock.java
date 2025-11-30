package biometric;

import javax.swing.JOptionPane;

public class LeitorBiometricoMock implements ILeitorBiometrico {
    @Override
    public String capturarDigital() {
        return JOptionPane.showInputDialog(null, "Simulação: Digite o ID do Usuário para simular a digital", "Captura Biométrica", JOptionPane.QUESTION_MESSAGE);
    }

    @Override
    public boolean isDisponivel() {
        return true; // Simulando hardware ok
    }
}