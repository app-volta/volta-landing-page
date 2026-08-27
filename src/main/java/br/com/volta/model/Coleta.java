package br.com.volta.model;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Coleta {

    //declaracao de variaveis
    private Integer id;
    private Ocorrencia ocorrencia;
    private Cooperativa cooperativa;
    private LocalDateTime dataSolicitacao;
    private LocalDateTime dataAgendada;
    private String statusAtual;
    private String tipoColeta;
    private boolean urgente;
    private LocalTime tempoEstimado;
    public Coleta() {
    }

    //construtor
    public Coleta(Ocorrencia ocorrencia, Cooperativa cooperativa, LocalDateTime dataAgendada, String tipoColeta, boolean urgente, LocalTime tempoEstimado) {
        this.ocorrencia = ocorrencia;
        this.cooperativa = cooperativa;
        this.dataAgendada = dataAgendada;
        this.tipoColeta = tipoColeta;
        this.urgente = urgente;
        this.tempoEstimado = tempoEstimado;
        this.statusAtual = "SOLICITADA"; // toda coleta nova começa como solicitada
        this.dataSolicitacao = LocalDateTime.now();
    }

    //getters e setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Ocorrencia getOcorrencia() {
        return ocorrencia;
    }

    public void setOcorrencia(Ocorrencia ocorrencia) {
        this.ocorrencia = ocorrencia;
    }

    public Cooperativa getCooperativa() {
        return cooperativa;
    }

    public void setCooperativa(Cooperativa cooperativa) {
        this.cooperativa = cooperativa;
    }

    public LocalDateTime getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(LocalDateTime dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }

    public LocalDateTime getDataAgendada() {
        return dataAgendada;
    }

    public void setDataAgendada(LocalDateTime dataAgendada) {
        this.dataAgendada = dataAgendada;
    }

    public String getStatusAtual() {
        return statusAtual;
    }

    public void setStatusAtual(String statusAtual) {
        this.statusAtual = statusAtual;
    }

    public String getTipoColeta() {
        return tipoColeta;
    }

    public void setTipoColeta(String tipoColeta) {
        this.tipoColeta = tipoColeta;
    }

    public boolean isUrgente() {
        return urgente;
    }

    public void setUrgente(boolean urgente) {
        this.urgente = urgente;
    }

    public LocalTime getTempoEstimado() {
        return tempoEstimado;
    }

    public void setTempoEstimado(LocalTime tempoEstimado) {
        this.tempoEstimado = tempoEstimado;
    }

    @Override
    public String toString() {
        return "Coleta{" +
                "id=" + id +
                ", statusAtual='" + statusAtual + '\'' +
                ", tipoColeta='" + tipoColeta + '\'' +
                ", urgente=" + urgente +
                '}';
    }
}