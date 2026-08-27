package br.com.volta.dao;

import br.com.volta.model.NivelRisco;
import br.com.volta.model.TipoResiduo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class TipoResiduoDAO {

    //procura um tipo de residuo pelo id
    public TipoResiduo buscarPorId(Integer id) {
        String sql = "SELECT * FROM tipo_residuo WHERE id = ?";
        TipoResiduo tipoResiduo = null; //vira um objeto se encontrarmos

        try (Connection conexao = ConexaoDB.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);

            //lendo dados da tabela
            try (ResultSet resultado = stmt.executeQuery()) {
                if (resultado.next()) {
                    tipoResiduo = montarTipoResiduo(resultado);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar tipo de residuo por id: " + e.getMessage());
        }

        return tipoResiduo;
    }

    //procura um tipo de residuo pela categoria, usado quando o formulario manda o nome escolhido
    public TipoResiduo buscarPorCategoria(String categoria) {
        String sql = "SELECT * FROM tipo_residuo WHERE categoria = ?";
        TipoResiduo tipoResiduo = null;

        try (Connection conexao = ConexaoDB.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, categoria);

            try (ResultSet resultado = stmt.executeQuery()) {
                if (resultado.next()) {
                    tipoResiduo = montarTipoResiduo(resultado);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar tipo de residuo por categoria: " + e.getMessage());
        }

        return tipoResiduo;
    }

    //devolve todos os tipos de residuo cadastrados, usado pra preencher dropdown no formulario
    public List<TipoResiduo> listarTodos() {
        String sql = "SELECT * FROM tipo_residuo";
        List<TipoResiduo> tiposResiduo = new ArrayList<>();

        try (Connection conexao = ConexaoDB.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet resultado = stmt.executeQuery()) {

            while (resultado.next()) {
                tiposResiduo.add(montarTipoResiduo(resultado));
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar tipos de residuo: " + e.getMessage());
        }

        return tiposResiduo;
    }

    //transforma tipo_residuo em um objeto
    private TipoResiduo montarTipoResiduo(ResultSet resultado) throws SQLException {
        TipoResiduo tipoResiduo = new TipoResiduo();
        tipoResiduo.setId(resultado.getInt("id"));
        tipoResiduo.setCategoria(resultado.getString("categoria"));
        tipoResiduo.setDescricao(resultado.getString("descricao"));

        //transforma o texto do banco de volta em enum
        tipoResiduo.setNivelRiscoPadrao(NivelRisco.valueOf(resultado.getString("nivel_risco_padrao")));

        return tipoResiduo;
    }
}