package controller;

import model.*;

import java.io.File;
import java.time.LocalDateTime;

public class DataSeeder {
    private static final String AUDITORIO_1 = "Auditório 1";
    private static final String LAB_2 = "Laboratório 2";

    private DataSeeder() {
        // Construtor privado
    }

    public static void resetarDados() {
        // Apagar arquivos .dat
        String[] arquivos = {"usuarios.dat", "eventos.dat", "registros.dat", "logs.dat"};
        for (String arquivo : arquivos) {
            File file = new File(arquivo);
            if (file.exists()) {
                boolean deleted = file.delete();
                if (!deleted) {
                    System.err.println("Falha ao deletar " + arquivo);
                }
            }
        }

        // Limpar listas em memória
        SistemaController sistemaController = SistemaController.getInstance();
        sistemaController.getUsuarios().clear();
        sistemaController.getEventos().clear();
        sistemaController.getRegistros().clear();

        // Limpar logs (reinicializar)
        LogController.getInstance();
    }

    public static void gerarCargaInicial() {
        SistemaController sistemaController = SistemaController.getInstance();

        try {
            // Criar Admin
            sistemaController.cadastrarUsuario("Coordenador Rogério", "12345678901", "admin@unesp.br", "admin", "BIO_ADMIN", TipoUsuario.ADMIN);
        } catch (Exception e) {
            System.err.println("Erro ao criar admin: " + e.getMessage());
        }

        try {
            // Criar Alunos
            sistemaController.cadastrarUsuario("Gustavo", "11111111111", "gustavo@unesp.br", "123", "BIO_01", TipoUsuario.ALUNO);
            sistemaController.cadastrarUsuario("Matheus", "22222222222", "matheus@unesp.br", "123", "BIO_02", TipoUsuario.ALUNO);
            sistemaController.cadastrarUsuario("Isabella", "33333333333", "isabella@unesp.br", "123", "BIO_03", TipoUsuario.ALUNO);
            sistemaController.cadastrarUsuario("Carolina", "44444444444", "carolina@unesp.br", "123", "BIO_04", TipoUsuario.ALUNO);
            sistemaController.cadastrarUsuario("Lucas", "55555555555", "lucas@unesp.br", "123", "BIO_05", TipoUsuario.ALUNO);
        } catch (Exception e) {
            System.err.println("Erro ao criar alunos: " + e.getMessage());
        }

        try {
            // Criar Eventos
            LocalDateTime ontem = LocalDateTime.now().minusDays(1);
            Evento eventoPassado = new Evento("Aula Inaugural", ontem, ontem.plusHours(2), AUDITORIO_1, StatusEvento.FECHADO);
            sistemaController.criarEvento(eventoPassado, true);

            LocalDateTime agoraMenos1h = LocalDateTime.now().minusHours(1);
            LocalDateTime agoraMais3h = LocalDateTime.now().plusHours(3);
            Evento eventoAtivo = new Evento("Defesa de TCC", agoraMenos1h, agoraMais3h, AUDITORIO_1, StatusEvento.ABERTO);
            sistemaController.criarEvento(eventoAtivo, false);

            LocalDateTime amanha = LocalDateTime.now().plusDays(1);
            Evento eventoFuturo = new Evento("Workshop Java", amanha, amanha.plusHours(4), LAB_2, StatusEvento.ABERTO);
            sistemaController.criarEvento(eventoFuturo, false);

            // Tentar criar evento com conflito (vai falhar e logar)
            try {
                Evento eventoConflitante = new Evento("Evento Conflitante", agoraMenos1h, agoraMais3h, AUDITORIO_1, StatusEvento.ABERTO);
                sistemaController.criarEvento(eventoConflitante, false);
            } catch (Exception e) {
                // Esperado, conflito
            }
        } catch (Exception e) {
            System.err.println("Erro ao criar eventos: " + e.getMessage());
        }

        try {
            // Registrar presenças
            Evento eventoPassado = sistemaController.getEventos().stream().filter(e -> e.getTitulo().equals("Aula Inaugural")).findFirst().orElse(null);
            Evento eventoAtivo = sistemaController.getEventos().stream().filter(e -> e.getTitulo().equals("Defesa de TCC")).findFirst().orElse(null);

            if (eventoPassado != null) {
                sistemaController.registrarPresenca("BIO_01", eventoPassado); // Gustavo
                sistemaController.registrarPresenca("BIO_02", eventoPassado); // Matheus
                sistemaController.registrarPresenca("BIO_03", eventoPassado); // Isabella
            }

            if (eventoAtivo != null) {
                sistemaController.registrarPresenca("BIO_04", eventoAtivo); // Carolina
                // Deixar Lucas pendente (não registrar)
            }
        } catch (Exception e) {
            System.err.println("Erro ao registrar presenças: " + e.getMessage());
        }
    }
}