package br.com.volta.dao;

import br.com.volta.model.Cargo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class CargoDAO {

    //procura um cargo pelo id
    public Cargo buscarPorId(Integer id) {
        String sql = "SELECT * FROM cargo WHERE id = ?";
        Cargo cargo = null; //vira um objeto se encontrarmos

        try (Connection conexao = ConexaoDB.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);

            //lendo dados da tabela
            try (ResultSet resultado = stmt.executeQuery()) {
                if (resultado.next()) {
                    cargo = montarCargo(resultado);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar cargo por id: " + e.getMessage());
        }

        return cargo;
    }

    //procura um cargo pelo nome, usado quando o formulario manda o nome escolhido
    public Cargo buscarPorNome(String nome) {
        String sql = "SELECT * FROM cargo WHERE nome = ?";
        Cargo cargo = null;

        try (Connection conexao = ConexaoDB.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, nome);

            try (ResultSet resultado = stmt.executeQuery()) {
                if (resultado.next()) {
                    cargo = montarCargo(resultado);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar cargo por nome: " + e.getMessage());
        }

        return cargo;
    }

    //devolve todos os cargos cadastrados, usado pra preencher dropdown no formulario
    public List<Cargo> listarTodos() {
        String sql = "SELECT * FROM cargo";
        List<Cargo> cargos = new ArrayList<>();

        try (Connection conexao = ConexaoDB.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet resultado = stmt.executeQuery()) {

            while (resultado.next()) {
                cargos.add(montarCargo(resultado));
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar cargos: " + e.getMessage());
        }

        return cargos;
    }

    //transforma cargo em um objeto
    private Cargo montarCargo(ResultSet resultado) throws SQLException {
        Cargo cargo = new Cargo();
        cargo.setId(resultado.getInt("id"));
        cargo.setNome(resultado.getString("nome"));
        cargo.setDescricao(resultado.getString("descricao"));
        return cargo;
    }
}