package br.com.volta.dao;

import br.com.volta.model.Cooperativa;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;


public class CooperativaDAO {

    //insere uma cooperativa nova no banco e devolve o mesmo objeto
    public Cooperativa inserir(Cooperativa cooperativa) {
        String sql = "INSERT INTO cooperativa (nome, cnpj, latitude, longitude, especialidades, horario_funcionamento, capacidade_mensal) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        //faz a conexao com o banco
        try (Connection conexao = ConexaoDB.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, cooperativa.getNome());
            stmt.setString(2, cooperativa.getCnpj());
            stmt.setDouble(3, cooperativa.getLatitude());
            stmt.setDouble(4, cooperativa.getLongitude());
            stmt.setString(5, cooperativa.getEspecialidades());

            //LocalTime do model precisa virar Time do java.sql para o banco aceitar
            stmt.setTime(6, Time.valueOf(cooperativa.getHorarioFuncionamento()));
            stmt.setInt(7, cooperativa.getCapacidadeMensal());
            stmt.executeUpdate();

            //pega o id que o banco gerou e cria uma tabela temporaria
            try (ResultSet chavesGeradas = stmt.getGeneratedKeys()) {

                //verifica se tem uma linha para ler
                if (chavesGeradas.next()) {
                    cooperativa.setId(chavesGeradas.getInt(1));
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao inserir cooperativa: " + e.getMessage());
        }

        //devolve o objeto cooperativa
        return cooperativa;
    }

    //procura uma cooperativa pelo id
    public Cooperativa buscarPorId(Integer id) {
        String sql = "SELECT * FROM cooperativa WHERE id = ?";
        Cooperativa cooperativa = null; //vira um objeto se encontrarmos

        try (Connection conexao = ConexaoDB.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);

            //lendo dados da tabela
            try (ResultSet resultado = stmt.executeQuery()) {
                if (resultado.next()) {
                    //monta o objeto Cooperativa pelos dados da tabela
                    cooperativa = montarCooperativa(resultado);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar cooperativa por id: " + e.getMessage());
        }

        return cooperativa;
    }

    //devolve todas as cooperativas cadastradas no banco
    public List<Cooperativa> listarTodos() {
        String sql = "SELECT * FROM cooperativa";
        List<Cooperativa> cooperativas = new ArrayList<>();

        try (Connection conexao = ConexaoDB.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet resultado = stmt.executeQuery()) {

            while (resultado.next()) {
                cooperativas.add(montarCooperativa(resultado));
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar cooperativas: " + e.getMessage());
        }

        return cooperativas;
    }

    //atualiza os dados de uma cooperativa que ja existe
    public boolean atualizar(Cooperativa cooperativa) {
        String sql = "UPDATE cooperativa SET nome = ?, cnpj = ?, latitude = ?, longitude = ?, " +
                "especialidades = ?, horario_funcionamento = ?, capacidade_mensal = ? WHERE id = ?";

        try (Connection conexao = ConexaoDB.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, cooperativa.getNome());
            stmt.setString(2, cooperativa.getCnpj());
            stmt.setDouble(3, cooperativa.getLatitude());
            stmt.setDouble(4, cooperativa.getLongitude());
            stmt.setString(5, cooperativa.getEspecialidades());
            stmt.setTime(6, Time.valueOf(cooperativa.getHorarioFuncionamento()));
            stmt.setInt(7, cooperativa.getCapacidadeMensal());
            stmt.setInt(8, cooperativa.getId());

            //mostra quantas linhas foram alteradas
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar cooperativa: " + e.getMessage());
            return false;
        }
    }

    //apaga uma cooperativa do banco pelo id
    public boolean deletar(Integer id) {
        String sql = "DELETE FROM cooperativa WHERE id = ?";

        try (Connection conexao = ConexaoDB.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao deletar cooperativa: " + e.getMessage());
            return false;
        }
    }

    //transforma cooperativa em um objeto
    private Cooperativa montarCooperativa(ResultSet resultado) throws SQLException {
        Cooperativa cooperativa = new Cooperativa();
        cooperativa.setId(resultado.getInt("id"));
        cooperativa.setNome(resultado.getString("nome"));
        cooperativa.setCnpj(resultado.getString("cnpj"));
        cooperativa.setLatitude(resultado.getDouble("latitude"));
        cooperativa.setLongitude(resultado.getDouble("longitude"));
        cooperativa.setNotaMedia(resultado.getDouble("nota_media"));
        cooperativa.setEspecialidades(resultado.getString("especialidades"));

        //transforma o Time do java.sql precisa virar LocalTime de volta
        Time horario = resultado.getTime("horario_funcionamento");
        if (horario != null) {
            cooperativa.setHorarioFuncionamento(horario.toLocalTime());
        }

        cooperativa.setCapacidadeMensal(resultado.getInt("capacidade_mensal"));
        return cooperativa;
    }
}