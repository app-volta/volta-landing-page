package br.com.volta.model;
import java.time.LocalTime;

public class Cooperativa {

    //declaracao de variaveis
    private Integer id;
    private String nome;
    private String cnpj;
    private Double latitude;
    private Double longitude;
    private Double notaMedia;     // média das avaliações recebidas
    private String especialidades;// campo texto solto
    private LocalTime horarioFuncionamento;
    private Integer capacidadeMensal;
    public Cooperativa() {
    }

    //construtor
    public Cooperativa(String nome, String cnpj, Double latitude, Double longitude, String especialidades, LocalTime horarioFuncionamento, Integer capacidadeMensal) {
        this.nome = nome;
        this.cnpj = cnpj;
        this.latitude = latitude;
        this.longitude = longitude;
        this.especialidades = especialidades;
        this.horarioFuncionamento = horarioFuncionamento;
        this.capacidadeMensal = capacidadeMensal;
        this.notaMedia = 0.0; // toda cooperativa nova começa sem avaliação
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

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getNotaMedia() {
        return notaMedia;
    }

    public void setNotaMedia(Double notaMedia) {
        this.notaMedia = notaMedia;
    }

    public String getEspecialidades() {
        return especialidades;
    }

    public void setEspecialidades(String especialidades) {
        this.especialidades = especialidades;
    }

    public LocalTime getHorarioFuncionamento() {
        return horarioFuncionamento;
    }

    public void setHorarioFuncionamento(LocalTime horarioFuncionamento) {
        this.horarioFuncionamento = horarioFuncionamento;
    }

    public Integer getCapacidadeMensal() {
        return capacidadeMensal;
    }

    public void setCapacidadeMensal(Integer capacidadeMensal) {
        this.capacidadeMensal = capacidadeMensal;
    }


    @Override
    public String toString() {
        return "Cooperativa{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", cnpj='" + cnpj + '\'' +
                ", notaMedia=" + notaMedia +
                ", capacidadeMensal=" + capacidadeMensal +
                '}';
    }
}