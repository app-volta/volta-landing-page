package br.com.volta.model;

public class Area {

    //declaracao de variavei
    private Integer id;
    private Empresa empresa;
    private String nomeSetor;
    private String descricaoLocalizacao;
    public Area() {
    }

    //construtor
    public Area(Empresa empresa, String nomeSetor, String descricaoLocalizacao) {
        this.empresa = empresa;
        this.nomeSetor = nomeSetor;
        this.descricaoLocalizacao = descricaoLocalizacao;
    }

    //getters e setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public String getNomeSetor() {
        return nomeSetor;
    }

    public void setNomeSetor(String nomeSetor) {
        this.nomeSetor = nomeSetor;
    }

    public String getDescricaoLocalizacao() {
        return descricaoLocalizacao;
    }

    public void setDescricaoLocalizacao(String descricaoLocalizacao) {
        this.descricaoLocalizacao = descricaoLocalizacao;
    }

    @Override
    public String toString() {
        return "Area{" +
                "id=" + id +
                ", nomeSetor='" + nomeSetor + '\'' +
                '}';
    }
}