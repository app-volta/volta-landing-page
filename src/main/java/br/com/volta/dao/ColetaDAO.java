package br.com.volta.dao;

import br.com.volta.model.Coleta;
import br.com.volta.model.Cooperativa;
import br.com.volta.model.Ocorrencia;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


public class ColetaDAO {

    //insere uma coleta nova no banco e devolve o mesmo objeto
    public Coleta inserir(Coleta coleta) {
        String sql = "INSERT INTO coleta (ocorrencia_id, cooperativa_id, data_solicitacao, " +
                "data_agendada, status_atual, tipo_coleta, urgente, tempo_estimado) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        //faz a conexao com o banco
        try (Connection conexao = ConexaoDB.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, coleta.getOcorrencia().getId());
            stmt.setInt(2, coleta.getCooperativa().getId());

            //transforma LocalDateTime do model em Timestamp do java.sql
            stmt.setTimestamp(3, Timestamp.valueOf(coleta.getDataSolicitacao()));
            stmt.setTimestamp(4, Timestamp.valueOf(coleta.getDataAgendada()));

            stmt.setString(5, coleta.getStatusAtual());
            stmt.setString(6, coleta.getTipoColeta());
            stmt.setBoolean(7, coleta.isUrgente());

            //transforma LocalTime do model em Time do java.sql
            stmt.setTime(8, Time.valueOf(coleta.getTempoEstimado()));

            stmt.executeUpdate();

            //pega o id que o banco gerou e cria uma tabela temporaria
            try (ResultSet chavesGeradas = stmt.getGeneratedKeys()) {

                //verifica se tem uma linha para ler
                if (chavesGeradas.next()) {
                    coleta.setId(chavesGeradas.getInt(1));
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao inserir coleta: " + e.getMessage());
        }

        //devolve o objeto coleta
        return coleta;
    }

    //procura uma coleta pelo id
    public Coleta buscarPorId(Integer id) {
        String sql = "SELECT * FROM coleta WHERE id = ?";
        Coleta coleta = null; //vira um objeto se encontrarmos

        try (Connection conexao = ConexaoDB.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);

            //lendo dados da tabela
            try (ResultSet resultado = stmt.executeQuery()) {
                if (resultado.next()) {
                    //monta o objeto Coleta pelos dados da tabela
                    coleta = montarColeta(resultado);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar coleta por id: " + e.getMessage());
        }

        return coleta;
    }

    //devolve todas as coletas cadastradas no banco
    public List<Coleta> listarTodos() {
        String sql = "SELECT * FROM coleta";
        List<Coleta> coletas = new ArrayList<>();

        try (Connection conexao = ConexaoDB.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet resultado = stmt.executeQuery()) {

            while (resultado.next()) {
                coletas.add(montarColeta(resultado));
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar coletas: " + e.getMessage());
        }

        return coletas;
    }

    //devolve so as coletas de uma cooperativa especifica
    public List<Coleta> listarPorCooperativa(Integer cooperativaId) {
        String sql = "SELECT * FROM coleta WHERE cooperativa_id = ?";
        List<Coleta> coletas = new ArrayList<>();

        try (Connection conexao = ConexaoDB.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, cooperativaId);

            try (ResultSet resultado = stmt.executeQuery()) {
                while (resultado.next()) {
                    coletas.add(montarColeta(resultado));
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar coletas por cooperativa: " + e.getMessage());
        }

        return coletas;
    }

    //atualiza so o status atual de uma coleta
    public boolean atualizarStatus(Integer id, String novoStatus) {
        String sql = "UPDATE coleta SET status_atual = ? WHERE id = ?";

        try (Connection conexao = ConexaoDB.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, novoStatus);
            stmt.setInt(2, id);

            //mostra quantas linhas foram alteradas
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar status da coleta: " + e.getMessage());
            return false;
        }
    }

    //apaga uma coleta do banco pelo id
    public boolean deletar(Integer id) {
        String sql = "DELETE FROM coleta WHERE id = ?";

        try (Connection conexao = ConexaoDB.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao deletar coleta: " + e.getMessage());
            return false;
        }
    }

    //transforma coleta em um objeto
    private Coleta montarColeta(ResultSet resultado) throws SQLException {
        Coleta coleta = new Coleta();
        coleta.setId(resultado.getInt("id"));
        coleta.setStatusAtual(resultado.getString("status_atual"));
        coleta.setTipoColeta(resultado.getString("tipo_coleta"));
        coleta.setUrgente(resultado.getBoolean("urgente"));

        //transforma o Timestamp do java.sql em LocalDateTime de volta
        Timestamp dataSolicitacao = resultado.getTimestamp("data_solicitacao");
        if (dataSolicitacao != null) {
            coleta.setDataSolicitacao(dataSolicitacao.toLocalDateTime());
        }

        Timestamp dataAgendada = resultado.getTimestamp("data_agendada");
        if (dataAgendada != null) {
            coleta.setDataAgendada(dataAgendada.toLocalDateTime());
        }

        //transforma o Time do java.sql em LocalTime de volta
        Time tempoEstimado = resultado.getTime("tempo_estimado");
        if (tempoEstimado != null) {
            coleta.setTempoEstimado(tempoEstimado.toLocalTime());
        }

        //monta os relacionamentos so com o id
        Ocorrencia ocorrencia = new Ocorrencia();
        ocorrencia.setId(resultado.getInt("ocorrencia_id"));
        coleta.setOcorrencia(ocorrencia);

        Cooperativa cooperativa = new Cooperativa();
        cooperativa.setId(resultado.getInt("cooperativa_id"));
        coleta.setCooperativa(cooperativa);

        return coleta;
    }
}