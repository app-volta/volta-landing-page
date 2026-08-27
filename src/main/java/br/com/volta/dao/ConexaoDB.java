package br.com.volta.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexaoDB {

    //abre uma conexao nova com o banco
    public static Connection conectar() throws SQLException {
        Properties propriedades = carregarPropriedades();

        String url = propriedades.getProperty("db.url");
        String usuario = propriedades.getProperty("db.usuario");
        String senha = propriedades.getProperty("db.senha");

        return DriverManager.getConnection(url, usuario, senha);
    }

    //le o arquivo db.properties e devolve os dados
    private static Properties carregarPropriedades() throws SQLException {
        Properties propriedades = new Properties();

        try (InputStream input = ConexaoDB.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new SQLException("Arquivo db.properties não encontrado. " +
                        "Copie db.properties.example, renomeie para db.properties e preencha com suas credenciais.");
            }
            propriedades.load(input);
        } catch (IOException e) {
            throw new SQLException("Erro ao ler db.properties: " + e.getMessage());
        }

        return propriedades;
    }
}