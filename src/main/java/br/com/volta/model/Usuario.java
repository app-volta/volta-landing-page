package br.com.volta.model;

public class Usuario {

    //declaracao de variaveis
    private Integer id;
    private Empresa empresa;
    private Cargo cargo;
    private TipoUsuario tipoUsuario;
    private String nome;
    private String email;
    private String senhaHash;  //guardamos a senha em hash
    public Usuario() {}

    //construtor
    public Usuario(Empresa empresa, Cargo cargo, TipoUsuario tipoUsuario,
                   String nome, String email, String senhaHash) {
        this.empresa = empresa;
        this.cargo = cargo;
        this.tipoUsuario = tipoUsuario;
        this.nome = nome;
        this.email = email;
        this.senhaHash = senhaHash;
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

    public Cargo getCargo() {
        return cargo;
    }

    public void setCargo(Cargo cargo) {
        this.cargo = cargo;
    }

    public TipoUsuario getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(TipoUsuario tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}