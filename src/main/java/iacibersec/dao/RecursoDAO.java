package iacibersec.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import iacibersec.models.Categoria;
import iacibersec.models.Recurso;

public class RecursoDAO {

    public boolean cadastrar(Recurso recurso) {
        String sql = "INSERT INTO Recursos (titulo, autor, id_categoria, id_usuario_cadastro, tipo_recurso, conteudo_link_ou_base64, status) VALUES (?, ?, ?, ?, ?, ?, 'Pendente')";
        
        try (Connection conn = ConexaoDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, recurso.getTitulo());
            stmt.setString(2, recurso.getAutor());
            stmt.setInt(3, recurso.getIdCategoria());
            stmt.setInt(4, recurso.getIdUsuarioCadastro());
            stmt.setString(5, recurso.getTipoRecurso());
            stmt.setString(6, recurso.getConteudo());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar recurso: " + e.getMessage());
            return false;
        }
    }

    public List<Recurso> listarComFiltros(String textoBusca, Integer idCategoria) {
        List<Recurso> recursos = new ArrayList<>();
        
        String sql = "SELECT r.id, r.titulo, r.autor, c.nome as nome_categoria, r.id_categoria, r.id_usuario_cadastro, " +
                     "COALESCE(AVG(a.nota), 0) AS nota_media " +
                     "FROM Recursos r " +
                     "JOIN Categorias c ON r.id_categoria = c.id " +
                     "LEFT JOIN Avaliacoes a ON r.id = a.id_recurso ";
        
        List<Object> parametros = new ArrayList<>();
        boolean temWhere = false;

        if (idCategoria != null && idCategoria > 0) {
            sql += "WHERE r.id_categoria = ? ";
            parametros.add(idCategoria);
            temWhere = true;
        }

        if (textoBusca != null && !textoBusca.trim().isEmpty()) {
            sql += (temWhere ? "AND " : "WHERE ") + "(r.titulo LIKE ? OR r.autor LIKE ?) ";
            parametros.add("%" + textoBusca + "%"); 
            parametros.add("%" + textoBusca + "%");
            temWhere = true;
        }

        sql += "GROUP BY r.id, r.titulo, r.autor, c.nome, r.id_categoria, r.id_usuario_cadastro ";
        sql += "ORDER BY r.titulo ASC";
        
        try (Connection conn = ConexaoDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            for (int i = 0; i < parametros.size(); i++) {
                if (parametros.get(i) instanceof Integer) {
                    stmt.setInt(i + 1, (Integer) parametros.get(i));
                } else {
                    stmt.setString(i + 1, (String) parametros.get(i));
                }
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Recurso r = new Recurso();
                    r.setId(rs.getInt("id"));
                    r.setTitulo(rs.getString("titulo"));
                    r.setAutor(rs.getString("autor"));
                    r.setIdCategoria(rs.getInt("id_categoria"));
                    r.setIdUsuarioCadastro(rs.getInt("id_usuario_cadastro"));
                    r.setNotaMedia(rs.getDouble("nota_media"));
                    
                    Categoria categoria = new Categoria();
                    categoria.setId(rs.getInt("id_categoria"));
                    categoria.setNome(rs.getString("nome_categoria"));
                    r.setCategoria(categoria);
                    
                    recursos.add(r);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar recursos com filtros: " + e.getMessage());
        }
        return recursos;
    }
    
    public List<Recurso> listarTopRecursosPorCategoria(int idCategoria, int limite) {
        List<Recurso> recursos = new ArrayList<>();
        
        String sql = "SELECT r.id, r.titulo, r.autor, c.nome as nome_categoria, " +
                     "COALESCE(AVG(a.nota), 0) AS nota_media " +
                     "FROM Recursos r " +
                     "JOIN Categorias c ON r.id_categoria = c.id " +
                     "LEFT JOIN Avaliacoes a ON r.id = a.id_recurso " +
                     "WHERE r.id_categoria = ? " +
                     "GROUP BY r.id, r.titulo, r.autor, c.nome " +
                     "ORDER BY nota_media DESC, r.titulo ASC " +
                     "LIMIT ?";

        try (Connection conn = ConexaoDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idCategoria);
            stmt.setInt(2, limite);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Recurso r = new Recurso();
                    r.setId(rs.getInt("id"));
                    r.setTitulo(rs.getString("titulo"));
                    r.setAutor(rs.getString("autor"));
                    r.setNotaMedia(rs.getDouble("nota_media"));
                    
                    Categoria categoria = new Categoria();
                    categoria.setNome(rs.getString("nome_categoria"));
                    r.setCategoria(categoria);
                    
                    recursos.add(r);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar Top Recursos: " + e.getMessage());
        }
        return recursos;
    }

    public boolean registrarAvaliacao(int idRecurso, int idUsuario, int nota) {
        
        String sql = "REPLACE INTO Avaliacoes (id_recurso, id_usuario, nota) VALUES (?, ?, ?)";

        try (Connection conn = ConexaoDAO.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idRecurso);
            stmt.setInt(2, idUsuario);
            stmt.setInt(3, nota);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Erro ao registrar avaliação: " + e.getMessage());
            return false;
        }
    }

    public boolean atualizarStatus(int idRecurso, String novoStatus) {
        String sql = "UPDATE Recursos SET status = ? WHERE id = ?";
        
        try (Connection conn = ConexaoDAO.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, novoStatus); // Ex: "Verificado" ou "Rejeitado"
            stmt.setInt(2, idRecurso);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar status do recurso: " + e.getMessage());
            return false;
        }
    }

    public List<Recurso> listarRecursosPorStatus(String status) {
        List<Recurso> recursos = new ArrayList<>();
        
        String sql = "SELECT r.id, r.titulo, r.autor, c.nome as nome_categoria, r.id_categoria, r.id_usuario_cadastro, r.tipo_recurso, r.conteudo_link_ou_base64 " +
                    "FROM Recursos r JOIN Categorias c ON r.id_categoria = c.id " +
                    "WHERE r.status = ? " +
                    "ORDER BY r.id ASC";

        try (Connection conn = ConexaoDAO.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status); // Ex: 'Pendente'

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Recurso r = new Recurso();
                    r.setId(rs.getInt("id"));
                    r.setTitulo(rs.getString("titulo"));
                    r.setAutor(rs.getString("autor"));
                    r.setIdUsuarioCadastro(rs.getInt("id_usuario_cadastro"));
                    r.setTipoRecurso(rs.getString("tipo_recurso")); // NOVO
                    r.setConteudo(rs.getString("conteudo_link_ou_base64")); // NOVO
                    
                    Categoria categoria = new Categoria();
                    categoria.setId(rs.getInt("id_categoria"));
                    categoria.setNome(rs.getString("nome_categoria"));
                    r.setCategoria(categoria);
                    
                    recursos.add(r);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar recursos por status: " + e.getMessage());
        }
        return recursos;
    }

}