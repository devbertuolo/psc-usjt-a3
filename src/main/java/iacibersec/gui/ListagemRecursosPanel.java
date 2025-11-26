package iacibersec.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import iacibersec.dao.CategoriaDAO;
import iacibersec.dao.RecursoDAO;
import iacibersec.models.Categoria;
import iacibersec.models.Recurso;
import iacibersec.models.Usuario;

public class ListagemRecursosPanel extends JPanel {

    private JTable tabelaRecursos;
    private DefaultTableModel tableModel;
    private RecursoDAO recursoDAO;
    private Usuario usuarioLogado;
    
    private JTextField txtBusca;
    private JComboBox<Categoria> cbFiltroCategoria;
    private JButton btnBuscar;
    
    private List<Categoria> categorias;
    
    public ListagemRecursosPanel(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
        this.recursoDAO = new RecursoDAO();
        this.categorias = new CategoriaDAO().listarTodas();
        
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        add(criarPainelFiltros(), BorderLayout.NORTH);
        add(criarPainelTabela(), BorderLayout.CENTER);
        
        carregarTabelaRecursos(null, null);
        adicionarAcaoAvaliacao();
    }
    
    private JPanel criarPainelFiltros() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        painel.setBorder(BorderFactory.createTitledBorder("Filtros e Busca"));
        
        txtBusca = new JTextField(25);
        cbFiltroCategoria = new JComboBox<>();
        btnBuscar = new JButton("Buscar/Filtrar");
        
        cbFiltroCategoria.addItem(new Categoria(0, "Todas as Categorias")); 
        for (Categoria cat : categorias) {
            cbFiltroCategoria.addItem(cat);
        }

        painel.add(new JLabel("Buscar (Título/Autor):"));
        painel.add(txtBusca);
        painel.add(new JLabel("Categoria:"));
        painel.add(cbFiltroCategoria);
        painel.add(btnBuscar);
        
        btnBuscar.addActionListener(e -> aplicarFiltros());
        txtBusca.addActionListener(e -> aplicarFiltros());
        
        return painel;
    }
    
    private void aplicarFiltros() {
        String textoBusca = txtBusca.getText();
        Categoria categoriaSelecionada = (Categoria) cbFiltroCategoria.getSelectedItem();
        Integer idCategoria = categoriaSelecionada != null ? categoriaSelecionada.getId() : 0;
        
        carregarTabelaRecursos(textoBusca, idCategoria);
    }
    
    private JScrollPane criarPainelTabela() {
        String[] colunas = {"ID", "Título", "Autor/Fonte", "Categoria", "Nota Média", "Cadastrado Por ID"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaRecursos = new JTable(tableModel);
        
        tabelaRecursos.getColumnModel().getColumn(0).setMaxWidth(50); 
        tabelaRecursos.getColumnModel().getColumn(5).setMaxWidth(120); 

        JScrollPane scrollPane = new JScrollPane(tabelaRecursos);
        return scrollPane;
    }
    
    private void carregarTabelaRecursos(String textoBusca, Integer idCategoria) {
        tableModel.setRowCount(0); 
        
        List<Recurso> recursos = recursoDAO.listarComFiltros(textoBusca, idCategoria);
        
        for (Recurso r : recursos) {
            tableModel.addRow(new Object[]{
                r.getId(),
                r.getTitulo(),
                r.getAutor(),
                r.getCategoria().getNome(),
                String.format("%.1f", r.getNotaMedia()),
                r.getIdUsuarioCadastro()
            });
        }
    }
    
    private void adicionarAcaoAvaliacao() {
        tabelaRecursos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int linha = tabelaRecursos.getSelectedRow();
                    if (linha != -1) {
                        avaliarRecurso(linha);
                    }
                }
            }
        });
    }

    private void avaliarRecurso(int linha) {
        int idRecurso = (int) tableModel.getValueAt(linha, 0);
        String titulo = (String) tableModel.getValueAt(linha, 1);
        
        JComboBox<Integer> notas = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        notas.setSelectedItem(5);

        int resultado = JOptionPane.showConfirmDialog(this, notas, 
            "Avaliar Recurso: " + titulo + " (1 a 5)", JOptionPane.OK_CANCEL_OPTION);

        if (resultado == JOptionPane.OK_OPTION) {
            int nota = (int) notas.getSelectedItem();
            
            if (recursoDAO.registrarAvaliacao(idRecurso, usuarioLogado.getId(), nota)) {
                JOptionPane.showMessageDialog(this, "Avaliação registrada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarTabelaRecursos(null, null);
            } else {
                JOptionPane.showMessageDialog(this, "Falha ao registrar avaliação.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}