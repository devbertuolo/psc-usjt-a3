package iacibersec.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import iacibersec.dao.CategoriaDAO;
import iacibersec.dao.RecursoDAO;
import iacibersec.models.Categoria;
import iacibersec.models.Recurso;
import iacibersec.models.Usuario;

public class CadastroRecursoPanel extends JPanel {

    private JTextField txtTitulo;
    private JTextField txtAutor;
    private JComboBox<Categoria> cbCategoria;
    private JComboBox<String> cbTipoRecurso; 
    private JTextField txtConteudo; 
    private JButton btnSalvar;
    
    private Usuario usuarioLogado;
    private RecursoDAO recursoDAO;
    
    private final String[] TIPOS_RECURSO = {"Selecione", "Vídeo", "Podcast", "Artigo", "Blog", "Site", "Imagem", "Revista"};
    
    public CadastroRecursoPanel(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
        this.recursoDAO = new RecursoDAO();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Cadastrar Novo Recurso"));
        
        JPanel painelForm = new JPanel(new GridLayout(5, 2, 10, 10)); 
        painelForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        txtTitulo = new JTextField(30);
        txtAutor = new JTextField(30);
        cbCategoria = new JComboBox<>();
        cbTipoRecurso = new JComboBox<>(TIPOS_RECURSO); 
        txtConteudo = new JTextField(30); 

        carregarCategorias(); 
        
        painelForm.add(new JLabel("Título do Recurso:"));
        painelForm.add(txtTitulo);
        
        painelForm.add(new JLabel("Autor/Fonte:"));
        painelForm.add(txtAutor);
        
        painelForm.add(new JLabel("Tipo (Vídeo, Artigo, etc.):")); 
        painelForm.add(cbTipoRecurso);
        
        painelForm.add(new JLabel("Categoria:"));
        painelForm.add(cbCategoria);
        
        painelForm.add(new JLabel("Link / Conteúdo:")); 
        painelForm.add(txtConteudo);
        
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnSalvar = new JButton("Salvar Recurso");
        painelBotoes.add(btnSalvar);
        
        add(painelForm, BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.SOUTH);
        
        btnSalvar.addActionListener(e -> salvarRecurso());
    }
    
    private void carregarCategorias() {
        List<Categoria> categorias = new CategoriaDAO().listarTodas();
        cbCategoria.addItem(new Categoria(0, "Selecione uma Categoria")); 
        for (Categoria cat : categorias) {
            cbCategoria.addItem(cat);
        }
    }
    
    private void salvarRecurso() {
        String titulo = txtTitulo.getText().trim();
        String autor = txtAutor.getText().trim();
        String tipoRecurso = (String) cbTipoRecurso.getSelectedItem();
        String conteudo = txtConteudo.getText().trim();
        Categoria categoriaSelecionada = (Categoria) cbCategoria.getSelectedItem();
        
        if (titulo.isEmpty() || autor.isEmpty() || categoriaSelecionada == null || categoriaSelecionada.getId() == 0 || tipoRecurso.equals("Selecione") || conteudo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha Título, Autor, Tipo, Conteúdo e selecione a Categoria.", 
                                          "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Recurso novoRecurso = new Recurso();
        novoRecurso.setTitulo(titulo);
        novoRecurso.setAutor(autor);
        novoRecurso.setTipoRecurso(tipoRecurso); 
        novoRecurso.setConteudo(conteudo);
        novoRecurso.setIdCategoria(categoriaSelecionada.getId());
        novoRecurso.setIdUsuarioCadastro(usuarioLogado.getId()); 
        
        if (recursoDAO.cadastrar(novoRecurso)) {
            JOptionPane.showMessageDialog(this, "Recurso cadastrado com sucesso! Aguardando verificação.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            txtTitulo.setText("");
            txtAutor.setText("");
            txtConteudo.setText("");
            cbTipoRecurso.setSelectedIndex(0);
            cbCategoria.setSelectedIndex(0);
        } else {
            JOptionPane.showMessageDialog(this, "Falha ao cadastrar o recurso.", "Erro de Persistência", JOptionPane.ERROR_MESSAGE);
        }
    }
}