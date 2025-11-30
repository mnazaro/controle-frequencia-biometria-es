package model;

import java.io.Serializable;

public class Usuario implements Serializable {
    private String nome;
    private String cpf;
    private String email;
    private String senha;
    private String codigoBiometrico;
    private TipoUsuario tipo;

    public Usuario(String nome, String cpf, String email, String senha, String codigoBiometrico, TipoUsuario tipo) {
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.senha = senha;
        this.codigoBiometrico = codigoBiometrico;
        this.tipo = tipo;
    }

    // Getters and Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getCodigoBiometrico() { return codigoBiometrico; }
    public void setCodigoBiometrico(String codigoBiometrico) { this.codigoBiometrico = codigoBiometrico; }

    public TipoUsuario getTipo() { return tipo; }
    public void setTipo(TipoUsuario tipo) { this.tipo = tipo; }

    @Override
    public String toString() {
        return nome + " (" + cpf + ")";
    }
}