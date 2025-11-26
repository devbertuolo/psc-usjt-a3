package iacibersec.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import iacibersec.models.Usuario;

public class UsuarioDAO {

    public Usuario login(Usuario usuario) {
        String sql = "SELECT id, nome, idade, tipo, ativo FROM Usuarios WHERE nome = ? AND senha = ? AND ativo = 1";
        
        try (Connection conn = ConexaoDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNome()); 
            stmt.setString(2, usuario.getSenha()); 

            try (ResultSet rs = stmt.executeQuery()) {
                
                if (rs.next()) {
                    Usuario usuarioLogado = new Usuario();
                    usuarioLogado.setId(rs.getInt("id"));
                    usuarioLogado.setNome(rs.getString("nome"));
                    usuarioLogado.setIdade(rs.getInt("idade"));
                    usuarioLogado.setTipo(rs.getString("tipo"));
                    usuarioLogado.setAtivo(rs.getBoolean("ativo"));
                    
                    return usuarioLogado;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro no login: " + e.getMessage());
        }
        return null; 
    }

    public boolean cadastrar(Usuario usuario) {
        String sqlUsuario = "INSERT INTO Usuarios (nome, idade, senha, tipo, ativo) VALUES (?, ?, ?, ?, ?)";
        String sqlInteresses = "INSERT INTO Usuario_Interesses (id_usuario, id_categoria) VALUES (?, ?)";
        
        Connection conn = null;
        PreparedStatement stmtUsuario = null;
        PreparedStatement stmtInteresses = null;
        
        try {
            conn = ConexaoDAO.getConnection();
            conn.setAutoCommit(false); 
            
            stmtUsuario = conn.prepareStatement(sqlUsuario, PreparedStatement.RETURN_GENERATED_KEYS);
            stmtUsuario.setString(1, usuario.getNome());
            stmtUsuario.setInt(2, usuario.getIdade());
            stmtUsuario.setString(3, usuario.getSenha()); 
            stmtUsuario.setString(4, usuario.getTipo());
            stmtUsuario.setBoolean(5, true); 

            stmtUsuario.executeUpdate();
            
            try (ResultSet rs = stmtUsuario.getGeneratedKeys()) {
                if (rs.next()) {
                    usuario.setId(rs.getInt(1)); 
                }
            }
            
            if (usuario.getInteressesIds() != null && !usuario.getInteressesIds().isEmpty()) {
                stmtInteresses = conn.prepareStatement(sqlInteresses);
                for (Integer idCategoria : usuario.getInteressesIds()) {
                    stmtInteresses.setInt(1, usuario.getId());
                    stmtInteresses.setInt(2, idCategoria);
                    stmtInteresses.addBatch(); 
                }
                stmtInteresses.executeBatch(); 
            }

            conn.commit(); 
            return true;
            
        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar usuário/interesses: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback(); 
                } catch (SQLException ex) {
                    System.err.println("Erro ao desfazer transação: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            try { if (stmtUsuario != null) stmtUsuario.close(); } catch (SQLException e) { /* ignore */ }
            try { if (stmtInteresses != null) stmtInteresses.close(); } catch (SQLException e) { /* ignore */ }
            try { if (conn != null) conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { /* ignore */ }
        }
    }

    public List<Usuario> listarTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT id, nome, idade, tipo, ativo FROM Usuarios";

        try (Connection conn = ConexaoDAO.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNome(rs.getString("nome"));
                usuario.setIdade(rs.getInt("idade"));
                usuario.setTipo(rs.getString("tipo"));
                usuario.setAtivo(rs.getBoolean("ativo"));
                
                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar todos os usuários: " + e.getMessage());
        }
        return usuarios;
    }

public boolean inativar(int id) {
    String sql = "UPDATE Usuarios SET ativo = FALSE WHERE id = ?";
    
    try (Connection conn = ConexaoDAO.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setInt(1, id);
        return stmt.executeUpdate() > 0;
        
    } catch (SQLException e) {
        System.err.println("Erro ao inativar usuário: " + e.getMessage());
        return false;
    }
}

public boolean editar(Usuario usuario) {
    String sqlUpdateUsuario = "UPDATE Usuarios SET nome=?, idade=?, tipo=?, ativo=? " + 
                              (usuario.getSenha() != null && !usuario.getSenha().isEmpty() ? ", senha=?" : "") +
                              " WHERE id=?";
    String sqlDeleteInteresses = "DELETE FROM Usuario_Interesses WHERE id_usuario=?";
    String sqlInsertInteresses = "INSERT INTO Usuario_Interesses (id_usuario, id_categoria) VALUES (?, ?)";

    Connection conn = null;
    try {
        conn = ConexaoDAO.getConnection();
        conn.setAutoCommit(false); 

        PreparedStatement stmtUsuario = conn.prepareStatement(sqlUpdateUsuario);
        int paramIndex = 1;
        stmtUsuario.setString(paramIndex++, usuario.getNome());
        stmtUsuario.setInt(paramIndex++, usuario.getIdade());
        stmtUsuario.setString(paramIndex++, usuario.getTipo());
        stmtUsuario.setBoolean(paramIndex++, usuario.isAtivo());
        
        if (usuario.getSenha() != null && !usuario.getSenha().isEmpty()) {
            stmtUsuario.setString(paramIndex++, usuario.getSenha());
        }
        stmtUsuario.setInt(paramIndex, usuario.getId());
        stmtUsuario.executeUpdate();
        
        PreparedStatement stmtDelete = conn.prepareStatement(sqlDeleteInteresses);
        stmtDelete.setInt(1, usuario.getId());
        stmtDelete.executeUpdate();
        
        if (usuario.getInteressesIds() != null && !usuario.getInteressesIds().isEmpty()) {
            PreparedStatement stmtInsert = conn.prepareStatement(sqlInsertInteresses);
            for (Integer idCategoria : usuario.getInteressesIds()) {
                stmtInsert.setInt(1, usuario.getId());
                stmtInsert.setInt(2, idCategoria);
                stmtInsert.addBatch();
            }
            stmtInsert.executeBatch();
        }

        conn.commit(); 
        return true;

    } catch (SQLException e) {
        System.err.println("Erro ao editar usuário: " + e.getMessage());
        if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { /* ignore */ } }
        return false;
    } finally {
        try { if (conn != null) conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { /* ignore */ }
    }
}
}