package br.com.volta.model;

public class TipoUsuario {

    //declaracao de variaveis
    private Integer id;
    private String nome;
    public TipoUsuario() {
    }

    //construtor
    public TipoUsuario(String nome) {
        this.nome = nome;
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

    @Override
    public String toString() {
        return "TipoUsuario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                '}';
    }
}