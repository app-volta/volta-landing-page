package br.com.volta.dao;

import br.com.volta.model.Cargo;
import br.com.volta.model.Empresa;
import br.com.volta.model.TipoUsuario;
import br.com.volta.model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class UsuarioDAO {

    //insere um usuario novo no banco e devolve o mesmo objeto
    public Usuario inserir(Usuario usuario) {
        String sql = "INSERT INTO usuario (empresa_id, cargo_id, tipo_usuario_id, nome, email, senha_hash) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        //faz a conexao com o banco
        try (Connection conexao = ConexaoDB.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            //pega só o id de cada relacionamento
            stmt.setInt(1, usuario.getEmpresa().getId());
            stmt.setInt(2, usuario.getCargo().getId());
            stmt.setInt(3, usuario.getTipoUsuario().getId());
            stmt.setString(4, usuario.getNome());
            stmt.setString(5, usuario.getEmail());
            stmt.setString(6, usuario.getSenhaHash());
            stmt.executeUpdate();

            //pega o id que o banco gerou e cria uma tabela temporaria
            try (ResultSet chavesGeradas = stmt.getGeneratedKeys()) {

                //verifica se tem uma linha para ler
                if (chavesGeradas.next()) {
                    usuario.setId(chavesGeradas.getInt(1));
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao inserir usuario: " + e.getMessage());
        }

        //devolve o objeto usuario
        return usuario;
    }

    //procura um usuario pelo id
    public Usuario buscarPorId(Integer id) {
        String sql = "SELECT * FROM usuario WHERE id = ?";
        Usuario usuario = null; //vira um objeto se encontrarmos

        try (Connection conexao = ConexaoDB.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);

            //lendo dados da tabela
            try (ResultSet resultado = stmt.executeQuery()) {
                if (resultado.next()) {
                    //monta o objeto Usuario pelos dados da tabela
                    usuario = montarUsuario(resultado);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar usuario por id: " + e.getMessage());
        }

        return usuario;
    }

    //procura um usuario pelo email
    public Usuario buscarPorEmail(String email) {
        String sql = "SELECT * FROM usuario WHERE email = ?";
        Usuario usuario = null;

        try (Connection conexao = ConexaoDB.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet resultado = stmt.executeQuery()) {
                if (resultado.next()) {
                    usuario = montarUsuario(resultado);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar usuario por email: " + e.getMessage());
        }

        return usuario;
    }

    //devolve todos os usuarios cadastrados no banco
    public List<Usuario> listarTodos() {
        String sql = "SELECT * FROM usuario";
        List<Usuario> usuarios = new ArrayList<>();

        try (Connection conexao = ConexaoDB.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet resultado = stmt.executeQuery()) {

            while (resultado.next()) {
                usuarios.add(montarUsuario(resultado));
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar usuarios: " + e.getMessage());
        }

        return usuarios;
    }

    //atualiza os dados de um usuario que ja existe
    public boolean atualizar(Usuario usuario) {
        String sql = "UPDATE usuario SET cargo_id = ?, tipo_usuario_id = ?, nome = ?, email = ? WHERE id = ?";

        try (Connection conexao = ConexaoDB.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, usuario.getCargo().getId());
            stmt.setInt(2, usuario.getTipoUsuario().getId());
            stmt.setString(3, usuario.getNome());
            stmt.setString(4, usuario.getEmail());
            stmt.setInt(5, usuario.getId());

            //mostra quantas linhas foram alteradas
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar usuario: " + e.getMessage());
            return false;
        }
    }

    //apaga um usuario do banco pelo id
    public boolean deletar(Integer id) {
        String sql = "DELETE FROM usuario WHERE id = ?";

        try (Connection conexao = ConexaoDB.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao deletar usuario: " + e.getMessage());
            return false;
        }
    }

    //transforma usuario em um objeto
    private Usuario montarUsuario(ResultSet resultado) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(resultado.getInt("id"));
        usuario.setNome(resultado.getString("nome"));
        usuario.setEmail(resultado.getString("email"));
        usuario.setSenhaHash(resultado.getString("senha_hash"));

        //monta os relacionamentos so com o id
        Empresa empresa = new Empresa();
        empresa.setId(resultado.getInt("empresa_id"));
        usuario.setEmpresa(empresa);

        Cargo cargo = new Cargo();
        cargo.setId(resultado.getInt("cargo_id"));
        usuario.setCargo(cargo);

        TipoUsuario tipoUsuario = new TipoUsuario();
        tipoUsuario.setId(resultado.getInt("tipo_usuario_id"));
        usuario.setTipoUsuario(tipoUsuario);

        return usuario;
    }
}