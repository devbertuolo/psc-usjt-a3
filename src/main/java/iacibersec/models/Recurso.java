package iacibersec.models;

import java.util.Date;

public class Recurso {
    private int id;
    private String titulo;
    private String autor;
    private int idCategoria;
    private Categoria categoria;
    private double notaMedia;
    private int idUsuarioCadastro;
    private Date dataCadastro;
    private String status;
    private String tipoRecurso;
    private String conteudo;
    
    public Recurso() {}
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
    
    public int getIdCategoria() { return idCategoria; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }
    
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public double getNotaMedia() { return notaMedia; }
    public void setNotaMedia(double notaMedia) { this.notaMedia = notaMedia; }
    
    public int getIdUsuarioCadastro() { return idUsuarioCadastro; }
    public void setIdUsuarioCadastro(int idUsuarioCadastro) { this.idUsuarioCadastro = idUsuarioCadastro; }
    
    public Date getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(Date dataCadastro) { this.dataCadastro = dataCadastro; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTipoRecurso() { return tipoRecurso; }
    public void setTipoRecurso(String tipoRecurso) { this.tipoRecurso = tipoRecurso; }

    public String getConteudo() { return conteudo; }
    public void setConteudo(String conteudo) { this.conteudo = conteudo; }
}