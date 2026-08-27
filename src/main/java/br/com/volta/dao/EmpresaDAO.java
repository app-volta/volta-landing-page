package br.com.volta.dao;

import br.com.volta.model.Empresa;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class EmpresaDAO {

    //insere uma empresa nova no banco e devolve o mesmo objeto
    public Empresa inserir(Empresa empresa) {
        String sql = "INSERT INTO empresa (nome, cnpj, endereco) VALUES (?, ?, ?)";

        //faz a conexao com o banco
        try (Connection conexao = ConexaoDB.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, empresa.getNome());
            stmt.setString(2, empresa.getCnpj());
            stmt.setString(3, empresa.getEndereco());
            stmt.executeUpdate();

            //pega o id que o banco gerou e cria uma tabela temporaria
            try (ResultSet chavesGeradas = stmt.getGeneratedKeys()) {

                //verifica se tem uma linha para ler
                if (chavesGeradas.next()) {
                    empresa.setId(chavesGeradas.getInt(1));
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao inserir empresa: " + e.getMessage());
        }

        //devolve o objeto empresa
        return empresa;
    }

    //procura uma empresa pelo id
    public Empresa buscarPorId(Integer id) {
        String sql = "SELECT * FROM empresa WHERE id = ?";
        Empresa empresa = null; //vira um objeto se encontrarmos

        try (Connection conexao = ConexaoDB.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);

            //lendo dados da tabela
            try (ResultSet resultado = stmt.executeQuery()) {
                if (resultado.next()) {
                    //monta o objeto Empresa pelos dados da tabela
                    empresa = montarEmpresa(resultado);
                }
            }

        } catch (SQLException e) {
            System.out.println("Erro ao buscar empresa por id: " + e.getMessage());
        }

        return empresa;
    }

    //devolve todas as empresas cadastradas no banco
    public List<Empresa> listarTodos() {
        String sql = "SELECT * FROM empresa";
        List<Empresa> empresas = new ArrayList<>();

        try (Connection conexao = ConexaoDB.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet resultado = stmt.executeQuery()) {

            while (resultado.next()) {
                empresas.add(montarEmpresa(resultado));
            }

        } catch (SQLException e) {
            System.out.println("Erro ao listar empresas: " + e.getMessage());
        }

        return empresas;
    }

    //atualiza os dados de uma empresa que ja existe
    public boolean atualizar(Empresa empresa) {
        String sql = "UPDATE empresa SET nome = ?, cnpj = ?, endereco = ? WHERE id = ?";

        try (Connection conexao = ConexaoDB.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, empresa.getNome());
            stmt.setString(2, empresa.getCnpj());
            stmt.setString(3, empresa.getEndereco());
            stmt.setInt(4, empresa.getId());


            //mostra quantas linhas foram alteradas
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar empresa: " + e.getMessage());
            return false;
        }
    }

    //apaga uma empresa do banco pelo id
    public boolean deletar(Integer id) {
        String sql = "DELETE FROM empresa WHERE id = ?";

        try (Connection conexao = ConexaoDB.conectar();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);
            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao deletar empresa: " + e.getMessage());
            return false;
        }
    }

    //transforma empresa em um objeto
    private Empresa montarEmpresa(ResultSet resultado) throws SQLException {
        Empresa empresa = new Empresa();
        empresa.setId(resultado.getInt("id"));
        empresa.setNome(resultado.getString("nome"));
        empresa.setCnpj(resultado.getString("cnpj"));
        empresa.setEndereco(resultado.getString("endereco"));
        return empresa;
    }
}