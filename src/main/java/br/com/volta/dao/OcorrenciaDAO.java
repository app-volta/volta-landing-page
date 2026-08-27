package br.com.volta.dao;

import br.com.volta.model.Area;
import br.com.volta.model.Empresa;
import br.com.volta.model.NivelContaminacao;
import br.com.volta.model.Ocorrencia;
import br.com.volta.model.Prioridade;
import br.com.volta.model.StatusOcorrencia;
import br.com.volta.model.TipoResiduo;
import br.com.volta.model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


public class OcorrenciaDAO {

    //insere uma ocorrencia nova no banco e devolve o mesmo objeto
    public Ocorrencia inserir(Ocorrencia ocorrencia) {
        String sql = "INSERT INTO ocorrencia (empresa_id, usuario_id, area_id, tipo_residuo_id, " +
                "prioridade_id, status_ocorrencia_id, nivel_contaminacao_id, foto_url, " +
                "descricao_funcionario, quantidade_estimada, data_registro, peso_estimado) " +
                "VALUES ((SELECT id FROM prioridade WHERE nome = ?), " +
                "?, ?, ?, ?, (SELECT id FROM status_ocorrencia WHERE nome = ?), " +
                "(SELECT id FROM nivel_contaminacao WHERE nome = ?), ?, ?, ?, ?, ?)";

        //faz a conexao com o banco
        try (Connection conexao = ConexaoDB.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, ocorrencia.getEmpresa().getId());
            stmt.setInt(2, ocorrencia.getUsuario().getId());
            stmt.setInt(3, ocorrencia.getArea().getId());
            stmt.setInt(4, ocorrencia.getTipoResiduo().getId());

            //transforma o valor do enum em texto
            stmt.setString(5, ocorrencia.getPrioridade().name());
            stmt.setString(6, ocorrencia.getStatus().name());
            stmt.setString(7, ocorrencia.getNivelContaminacao().name());

            stmt.setString(8, ocorrencia.getFotoUrl());
            stmt.setString(9, ocorrencia.getDescricaoFuncionario());
            stmt.setInt(10, ocorrencia.getQuantidadeEstimada());

            //transforma LocalDateTime do model em Timestamp do java.sql para o banco aceitar
            stmt.setTimestamp(11, Timestamp.valueOf(ocorrencia.getDataRegistro()));
            stmt.setDouble(12, ocorrencia.getPesoEstimado());

            stmt.executeUpdate();

            //pega o id que o banco gerou e cria uma tabela temporaria
            try (ResultSet chavesGeradas = stmt.getGeneratedKeys()) {

                //verifica se tem uma linha para ler
                if (chavesGeradas.next()) {
                    ocorrencia.setId(chavesGeradas.getInt(1));
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao inserir ocorrencia: " + e.getMessage());
        }

        //devolve o objeto ocorrencia
        return ocorrencia;
    }

    //procura uma ocorrencia pelo id
    public Ocorrencia buscarPorId(Integer id) {
        String sql = "SELECT o.*, p.nome AS prioridade_nome, s.nome AS status_nome, n.nome AS nivel_nome " +
                "FROM ocorrencia o " +
                "JOIN prioridade p ON o.prioridade_id = p.id " +
                "JOIN status_ocorrencia s ON o.status_ocorrencia_id = s.id " +
                "JOIN nivel_contaminacao n ON o.nivel_contaminacao_id = n.id " +
                "WHERE o.id = ?";
        Ocorrencia ocorrencia = null; //vira um objeto se encontrarmos

        try (Connection conexao = ConexaoDB.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);

            //lendo dados da tabela
            try (ResultSet resultado = stmt.executeQuery()) {
                if (resultado.next()) {
                    //monta o objeto Ocorrencia pelos dados da tabela
                    ocorrencia = montarOcorrencia(resultado);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar ocorrencia por id: " + e.getMessage());
        }

        return ocorrencia;
    }

    //devolve todas as ocorrencias cadastradas no banco
    public List<Ocorrencia> listarTodos() {
        String sql = "SELECT o.*, p.nome AS prioridade_nome, s.nome AS status_nome, n.nome AS nivel_nome " +
                "FROM ocorrencia o " +
                "JOIN prioridade p ON o.prioridade_id = p.id " +
                "JOIN status_ocorrencia s ON o.status_ocorrencia_id = s.id " +
                "JOIN nivel_contaminacao n ON o.nivel_contaminacao_id = n.id";
        List<Ocorrencia> ocorrencias = new ArrayList<>();

        try (Connection conexao = ConexaoDB.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet resultado = stmt.executeQuery()) {

            while (resultado.next()) {
                ocorrencias.add(montarOcorrencia(resultado));
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar ocorrencias: " + e.getMessage());
        }

        return ocorrencias;
    }

    //atualiza so o status de uma ocorrencia
    public boolean atualizarStatus(Integer id, StatusOcorrencia novoStatus) {
        String sql = "UPDATE ocorrencia SET status_ocorrencia_id = " +
                "(SELECT id FROM status_ocorrencia WHERE nome = ?) WHERE id = ?";

        try (Connection conexao = ConexaoDB.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, novoStatus.name());
            stmt.setInt(2, id);

            //mostra quantas linhas foram alteradas
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar status da ocorrencia: " + e.getMessage());
            return false;
        }
    }

    //apaga uma ocorrencia do banco pelo id
    public boolean deletar(Integer id) {
        String sql = "DELETE FROM ocorrencia WHERE id = ?";

        try (Connection conexao = ConexaoDB.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao deletar ocorrencia: " + e.getMessage());
            return false;
        }
    }

    //transforma ocorrencia em um objeto
    private Ocorrencia montarOcorrencia(ResultSet resultado) throws SQLException {
        Ocorrencia ocorrencia = new Ocorrencia();
        ocorrencia.setId(resultado.getInt("id"));
        ocorrencia.setFotoUrl(resultado.getString("foto_url"));
        ocorrencia.setDescricaoFuncionario(resultado.getString("descricao_funcionario"));
        ocorrencia.setQuantidadeEstimada(resultado.getInt("quantidade_estimada"));
        ocorrencia.setPesoEstimado(resultado.getDouble("peso_estimado"));

        //transforma o Timestamp do java.sql em LocalDateTime de volta
        Timestamp dataRegistro = resultado.getTimestamp("data_registro");
        if (dataRegistro != null) {
            ocorrencia.setDataRegistro(dataRegistro.toLocalDateTime());
        }

        //transforma o texto do banco de volta em enum
        ocorrencia.setPrioridade(Prioridade.valueOf(resultado.getString("prioridade_nome")));
        ocorrencia.setStatus(StatusOcorrencia.valueOf(resultado.getString("status_nome")));
        ocorrencia.setNivelContaminacao(NivelContaminacao.valueOf(resultado.getString("nivel_nome")));

        //monta os relacionamentos so com o id
        Empresa empresa = new Empresa();
        empresa.setId(resultado.getInt("empresa_id"));
        ocorrencia.setEmpresa(empresa);

        Usuario usuario = new Usuario();
        usuario.setId(resultado.getInt("usuario_id"));
        ocorrencia.setUsuario(usuario);

        Area area = new Area();
        area.setId(resultado.getInt("area_id"));
        ocorrencia.setArea(area);

        TipoResiduo tipoResiduo = new TipoResiduo();
        tipoResiduo.setId(resultado.getInt("tipo_residuo_id"));
        ocorrencia.setTipoResiduo(tipoResiduo);

        return ocorrencia;
    }
}