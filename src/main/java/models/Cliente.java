package models;

import java.io.Serializable;

public class Cliente implements Serializable, Comparable<Cliente> {
    // Serial Version UID para controle de versão da serialização
    private static final long serialVersionUID = 1L;

    // Campos da classe
    private String nome;
    private String sobrenome;
    private String endereco;
    private String telefone;
    private int creditScore; // Valor entre 0 e 100

    // Construtor da classe
    public Cliente(String nome, String sobrenome, String endereco, String telefone, int creditScore) {
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.endereco = endereco;
        this.telefone = telefone;
        setCreditScore(creditScore); // Validação do credit score
    }

    // Getters e Setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public int getCreditScore() {
        return creditScore;
    }

    public void setCreditScore(int creditScore) {
        if (creditScore < 0 || creditScore > 100) {
            throw new IllegalArgumentException("Credit score deve ser entre 0 e 100.");
        }
        this.creditScore = creditScore;
    }

    // Metodo para exibir as informações do cliente
    @Override
    public String toString() {
        return "models.Cliente{" +
                "nome='" + nome + '\'' +
                ", sobrenome='" + sobrenome + '\'' +
                ", endereco='" + endereco + '\'' +
                ", telefone='" + telefone + '\'' +
                ", creditScore=" + creditScore +
                '}';
    }

    @Override
    public int compareTo(Cliente other) {
        String nomeCompleto = this.nome + " " + this.sobrenome;
        String otherNomeCompleto = other.nome + " " + other.sobrenome;
        return nomeCompleto.compareTo(otherNomeCompleto);
    }
}
