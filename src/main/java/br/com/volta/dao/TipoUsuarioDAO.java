package br.com.volta.dao;

import br.com.volta.model.TipoUsuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class TipoUsuarioDAO {

    //procura um tipo de usuario pelo id
    public TipoUsuario buscarPorId(Integer id) {
        String sql = "SELECT * FROM tipo_usuario WHERE id = ?";
        TipoUsuario tipoUsuario = null; //vira um objeto se encontrarmos

        try (Connection conexao = ConexaoDB.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);

            //lendo dados da tabela
            try (ResultSet resultado = stmt.executeQuery()) {
                if (resultado.next()) {
                    tipoUsuario = montarTipoUsuario(resultado);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar tipo de usuario por id: " + e.getMessage());
        }

        return tipoUsuario;
    }

    //procura um tipo de usuario pelo nome, usado quando o formulario manda o nome escolhido
    public TipoUsuario buscarPorNome(String nome) {
        String sql = "SELECT * FROM tipo_usuario WHERE nome = ?";
        TipoUsuario tipoUsuario = null;

        try (Connection conexao = ConexaoDB.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, nome);

            try (ResultSet resultado = stmt.executeQuery()) {
                if (resultado.next()) {
                    tipoUsuario = montarTipoUsuario(resultado);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar tipo de usuario por nome: " + e.getMessage());
        }

        return tipoUsuario;
    }

    //devolve todos os tipos de usuario cadastrados
    public List<TipoUsuario> listarTodos() {
        String sql = "SELECT * FROM tipo_usuario";
        List<TipoUsuario> tiposUsuario = new ArrayList<>();

        try (Connection conexao = ConexaoDB.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet resultado = stmt.executeQuery()) {

            while (resultado.next()) {
                tiposUsuario.add(montarTipoUsuario(resultado));
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar tipos de usuario: " + e.getMessage());
        }

        return tiposUsuario;
    }

    //transforma tipo_usuario em um objeto
    private TipoUsuario montarTipoUsuario(ResultSet resultado) throws SQLException {
        TipoUsuario tipoUsuario = new TipoUsuario();
        tipoUsuario.setId(resultado.getInt("id"));
        tipoUsuario.setNome(resultado.getString("nome"));
        return tipoUsuario;
    }
}