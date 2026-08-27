package br.com.volta.model;

public class Cargo {

    //declaracao de variaveis
    private Integer id;
    private String nome;
    private String descricao;
    public Cargo() {}

    //construtor
    public Cargo(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }

    //getters e setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return "Cargo{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                '}';
    }
}