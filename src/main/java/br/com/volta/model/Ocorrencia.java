package br.com.volta.model;
import java.time.LocalDateTime;

public class Ocorrencia {

    //declaracao de variaveis
    private Integer id;
    private Empresa empresa;
    private Usuario usuario;
    private Area area;
    private TipoResiduo tipoResiduo;
    private Prioridade prioridade;
    private StatusOcorrencia status;
    private NivelContaminacao nivelContaminacao;
    private String fotoUrl;
    private String descricaoFuncionario;
    private Integer quantidadeEstimada;
    private LocalDateTime dataRegistro;
    private Double pesoEstimado;
    public Ocorrencia() {}

    //construtor
    public Ocorrencia(Empresa empresa, Usuario usuario, Area area, TipoResiduo tipoResiduo, Prioridade prioridade, NivelContaminacao nivelContaminacao, String fotoUrl, String descricaoFuncionario, Integer quantidadeEstimada, Double pesoEstimado) {
        this.empresa = empresa;
        this.usuario = usuario;
        this.area = area;
        this.tipoResiduo = tipoResiduo;
        this.prioridade = prioridade;
        this.nivelContaminacao = nivelContaminacao;
        this.fotoUrl = fotoUrl;
        this.descricaoFuncionario = descricaoFuncionario;
        this.quantidadeEstimada = quantidadeEstimada;
        this.pesoEstimado = pesoEstimado;
        this.status = StatusOcorrencia.PENDENTE; // toda ocorrencia nova começa pendente
        this.dataRegistro = LocalDateTime.now();
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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public TipoResiduo getTipoResiduo() {
        return tipoResiduo;
    }

    public void setTipoResiduo(TipoResiduo tipoResiduo) {
        this.tipoResiduo = tipoResiduo;
    }

    public Prioridade getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(Prioridade prioridade) {
        this.prioridade = prioridade;
    }

    public StatusOcorrencia getStatus() {
        return status;
    }

    public void setStatus(StatusOcorrencia status) {
        this.status = status;
    }

    public NivelContaminacao getNivelContaminacao() {
        return nivelContaminacao;
    }

    public void setNivelContaminacao(NivelContaminacao nivelContaminacao) {
        this.nivelContaminacao = nivelContaminacao;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public String getDescricaoFuncionario() {
        return descricaoFuncionario;
    }

    public void setDescricaoFuncionario(String descricaoFuncionario) {
        this.descricaoFuncionario = descricaoFuncionario;
    }

    public Integer getQuantidadeEstimada() {
        return quantidadeEstimada;
    }

    public void setQuantidadeEstimada(Integer quantidadeEstimada) {
        this.quantidadeEstimada = quantidadeEstimada;
    }

    public LocalDateTime getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(LocalDateTime dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public Double getPesoEstimado() {
        return pesoEstimado;
    }

    public void setPesoEstimado(Double pesoEstimado) {
        this.pesoEstimado = pesoEstimado;
    }

    @Override
    public String toString() {
        return "Ocorrencia{" +
                "id=" + id +
                ", status=" + status +
                ", prioridade=" + prioridade +
                ", dataRegistro=" + dataRegistro +
                '}';
    }
}