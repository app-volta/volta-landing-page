package br.com.volta.dao;

import br.com.volta.model.Area;
import br.com.volta.model.Empresa;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class AreaDAO {

    //insere uma area nova no banco e devolve o mesmo objeto
    public Area inserir(Area area) {
        String sql = "INSERT INTO area (empresa_id, nome_setor, descricao_localizacao) VALUES (?, ?, ?)";

        //faz a conexao com o banco
        try (Connection conexao = ConexaoDB.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            //pega só o id da empresa
            stmt.setInt(1, area.getEmpresa().getId());
            stmt.setString(2, area.getNomeSetor());
            stmt.setString(3, area.getDescricaoLocalizacao());
            stmt.executeUpdate();

            //pega o id que o banco gerou e cria uma tabela temporaria
            try (ResultSet chavesGeradas = stmt.getGeneratedKeys()) {

                //verifica se tem uma linha para ler
                if (chavesGeradas.next()) {
                    area.setId(chavesGeradas.getInt(1));
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao inserir area: " + e.getMessage());
        }

        //devolve o objeto area
        return area;
    }

    //procura uma area pelo id
    public Area buscarPorId(Integer id) {
        String sql = "SELECT * FROM area WHERE id = ?";
        Area area = null; //vira um objeto se encontrarmos

        try (Connection conexao = ConexaoDB.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);

            //lendo dados da tabela
            try (ResultSet resultado = stmt.executeQuery()) {
                if (resultado.next()) {
                    //monta o objeto Area pelos dados da tabela
                    area = montarArea(resultado);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar area por id: " + e.getMessage());
        }

        return area;
    }

    //devolve todas as areas cadastradas no banco
    public List<Area> listarTodos() {
        String sql = "SELECT * FROM area";
        List<Area> areas = new ArrayList<>();

        try (Connection conexao = ConexaoDB.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet resultado = stmt.executeQuery()) {

            while (resultado.next()) {
                areas.add(montarArea(resultado));
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar areas: " + e.getMessage());
        }

        return areas;
    }

    //devolve so as areas de uma empresa especifica
    public List<Area> listarPorEmpresa(Integer empresaId) {
        String sql = "SELECT * FROM area WHERE empresa_id = ?";
        List<Area> areas = new ArrayList<>();

        try (Connection conexao = ConexaoDB.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, empresaId);

            try (ResultSet resultado = stmt.executeQuery()) {
                while (resultado.next()) {
                    areas.add(montarArea(resultado));
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar areas por empresa: " + e.getMessage());
        }

        return areas;
    }

    //atualiza os dados de uma area que ja existe
    public boolean atualizar(Area area) {
        String sql = "UPDATE area SET nome_setor = ?, descricao_localizacao = ? WHERE id = ?";

        try (Connection conexao = ConexaoDB.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, area.getNomeSetor());
            stmt.setString(2, area.getDescricaoLocalizacao());
            stmt.setInt(3, area.getId());

            //mostra quantas linhas foram alteradas
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar area: " + e.getMessage());
            return false;
        }
    }

    //apaga uma area do banco pelo id
    public boolean deletar(Integer id) {
        String sql = "DELETE FROM area WHERE id = ?";

        try (Connection conexao = ConexaoDB.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao deletar area: " + e.getMessage());
            return false;
        }
    }

    //transforma area em um objeto
    private Area montarArea(ResultSet resultado) throws SQLException {
        Area area = new Area();
        area.setId(resultado.getInt("id"));
        area.setNomeSetor(resultado.getString("nome_setor"));
        area.setDescricaoLocalizacao(resultado.getString("descricao_localizacao"));

        //monta um objeto Empresa só com o idx
        Empresa empresa = new Empresa();
        empresa.setId(resultado.getInt("empresa_id"));
        area.setEmpresa(empresa);

        return area;
    }
}