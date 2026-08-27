package br.com.volta.model;

public class TipoResiduo {

    //declaracao de variaveis
    private Integer id;
    private String categoria;
    private String descricao;
    private NivelRisco nivelRiscoPadrao;
    public TipoResiduo() {
    }

    //construtor
    public TipoResiduo(String categoria, String descricao, NivelRisco nivelRiscoPadrao) {
        this.categoria = categoria;
        this.descricao = descricao;
        this.nivelRiscoPadrao = nivelRiscoPadrao;
    }

    //getters e setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public NivelRisco getNivelRiscoPadrao() {
        return nivelRiscoPadrao;
    }

    public void setNivelRiscoPadrao(NivelRisco nivelRiscoPadrao) {
        this.nivelRiscoPadrao = nivelRiscoPadrao;
    }

    @Override
    public String toString() {
        return "TipoResiduo{" +
                "id=" + id +
                ", categoria='" + categoria + '\'' +
                ", nivelRiscoPadrao=" + nivelRiscoPadrao +
                '}';
    }
}