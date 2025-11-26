package iacibersec.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
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
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import iacibersec.dao.CategoriaDAO;
import iacibersec.dao.RecursoDAO;
import iacibersec.models.Categoria;
import iacibersec.models.Recurso;
import iacibersec.models.Usuario;

public class ListagemRecursosPanel extends JPanel {

    private RecursoDAO recursoDAO;
    private Usuario usuarioLogado;
    
    private JPanel cardsContainer; 
    
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
        
        // FIX: Inicializa o cardsContainer antes de usá-lo no JScrollPane
        cardsContainer = new JPanel();
        
        JScrollPane scrollPane = new JScrollPane(cardsContainer);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        add(scrollPane, BorderLayout.CENTER);
        
        carregarCardsRecursos(null, null);
        
        // Adiciona eventos de filtro
        btnBuscar.addActionListener(e -> aplicarFiltros());
        txtBusca.addActionListener(e -> aplicarFiltros());
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
        
        return painel;
    }
    
    private void aplicarFiltros() {
        String textoBusca = txtBusca.getText();
        Categoria categoriaSelecionada = (Categoria) cbFiltroCategoria.getSelectedItem();
        Integer idCategoria = categoriaSelecionada != null ? categoriaSelecionada.getId() : 0;
        
        carregarCardsRecursos(textoBusca, idCategoria);
    }
    
    private void carregarCardsRecursos(String textoBusca, Integer idCategoria) {
        cardsContainer.removeAll();
        
        List<Recurso> recursos = recursoDAO.listarRecursosVerificadosComFiltros(textoBusca, idCategoria); 
        
        if (recursos.isEmpty()) {
            cardsContainer.setLayout(new BorderLayout());
            cardsContainer.add(new JLabel("Nenhum recurso encontrado ou verificado que corresponda aos filtros.", SwingConstants.CENTER), BorderLayout.CENTER);
        } else {
            int totalCards = recursos.size();
            int numColunas = 3;
            int numLinhas = (int) Math.ceil((double) totalCards / numColunas); 
            
            // Define o GridPanel que irá forçar 3 colunas com quebra de linha
            JPanel gridPanel = new JPanel(new GridLayout(numLinhas, numColunas, 15, 15)); 

            for (Recurso r : recursos) {
                RecursoCard card = new RecursoCard(r);
                gridPanel.add(card);
                
                card.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent evt) {
                        if (evt.getClickCount() == 2) {
                            avaliarRecurso(r.getId(), r.getTitulo());
                        }
                    }
                });
            }
            
            // Adiciona o gridPanel ao cardsContainer
            cardsContainer.setLayout(new BorderLayout()); 
            cardsContainer.add(gridPanel, BorderLayout.NORTH);
        }
        
        cardsContainer.revalidate();
        cardsContainer.repaint();
    }
    
    private void avaliarRecurso(int idRecurso, String titulo) {
        JComboBox<Integer> notas = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        notas.setSelectedItem(5);

        int resultado = JOptionPane.showConfirmDialog(this, notas, 
            "Avaliar Recurso: " + titulo + " (1 a 5)", JOptionPane.OK_CANCEL_OPTION);

        if (resultado == JOptionPane.OK_OPTION) {
            int nota = (int) notas.getSelectedItem();
            
            if (recursoDAO.registrarAvaliacao(idRecurso, usuarioLogado.getId(), nota)) {
                JOptionPane.showMessageDialog(this, "Avaliação registrada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                // Recarrega a listagem com os filtros atuais (mantém o contexto)
                aplicarFiltros(); 
            } else {
                JOptionPane.showMessageDialog(this, "Falha ao registrar avaliação.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}