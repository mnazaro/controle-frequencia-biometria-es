package controller;

import model.RegistroPresenca;

import java.util.Random;

public class MockCloudService {
    private Random random = new Random();

    public boolean enviarRegistro(RegistroPresenca registro) {
        try {
            // Simular latência de rede
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }

        // Simular JSON enviado
        System.out.println("[CLOUD MOCK] Recebido JSON: {\"usuario\":\"" + registro.getUsuario().getNome() +
                           "\", \"evento\":\"" + registro.getEvento().getTitulo() +
                           "\", \"dataHora\":\"" + registro.getDataHoraRegistro() +
                           "\", \"status\":\"" + registro.getStatus() + "\"}");

        // 90% sucesso, 10% falha
        return random.nextInt(100) < 90;
    }
}