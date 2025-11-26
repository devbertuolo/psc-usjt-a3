package iacibersec.models;

import java.util.List;

public class Usuario {
    
    private int id;
    private String nome;
    private int idade;
    private String senha;
    private String tipo;
    private boolean ativo;
    private List<Integer> interessesIds;
    
    public Usuario() {}

    public Usuario(String nome, String senha) {
        this.nome = nome;
        this.senha = senha;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public int getIdade() { return idade; }
    public void setIdade(int idade) { this.idade = idade; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    
    public List<Integer> getInteressesIds() { return interessesIds; }
    public void setInteressesIds(List<Integer> interessesIds) { this.interessesIds = interessesIds; }
    
}