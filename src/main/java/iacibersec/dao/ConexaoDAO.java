package iacibersec.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoDAO {

    private static final String URL = "jdbc:mysql://localhost:3306/ia_ciberseguranca_db?useTimezone=true&serverTimezone=UTC";
    private static final String USUARIO = "root"; 
    private static final String SENHA = "Feh2!0406";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); 
            return DriverManager.getConnection(URL, USUARIO, SENHA);
            
        } catch (ClassNotFoundException e) {
            System.err.println("Driver JDBC do MySQL não encontrado.");
            throw new SQLException("Dependência JDBC ausente ou configurada incorretamente.");
        } catch (SQLException e) {
            System.err.println("Erro de conexão ao BD: Verifique o servidor e as credenciais (URL/Usuário/Senha).");
            e.printStackTrace();
            throw e;
        }
    }
}