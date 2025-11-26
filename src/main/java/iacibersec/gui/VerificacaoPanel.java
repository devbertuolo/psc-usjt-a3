package iacibersec.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import iacibersec.dao.RecursoDAO;
import iacibersec.models.Recurso;

public class VerificacaoPanel extends JPanel {

    private JTable tabelaPendentes;
    private DefaultTableModel tableModel;
    private RecursoDAO recursoDAO;
    private JButton btnAprovar;
    private JButton btnRejeitar;
    private JButton btnVerConteudo;

    public VerificacaoPanel() {
        this.recursoDAO = new RecursoDAO();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Recursos Pendentes de Verificação"));

        String[] colunas = {"ID", "Título", "Tipo", "Autor", "Categoria", "Conteúdo (Link)", "Cadastrado Por ID"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaPendentes = new JTable(tableModel);
        
        tabelaPendentes.getColumnModel().getColumn(0).setMaxWidth(50);
        tabelaPendentes.getColumnModel().getColumn(6).setMaxWidth(120);
        
        add(new JScrollPane(tabelaPendentes), BorderLayout.CENTER);
        add(criarPainelAcoes(), BorderLayout.SOUTH);
        
        carregarTabelaPendentes();
        configurarEventos();
    }
    
    private JPanel criarPainelAcoes() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnAprovar = new JButton("Aprovar (Verificado)");
        btnRejeitar = new JButton("Rejeitar");
        btnVerConteudo = new JButton("Ver Conteúdo (Link)");
        
        painel.add(btnAprovar);
        painel.add(btnRejeitar);
        painel.add(btnVerConteudo);
        return painel;
    }
    
    private void carregarTabelaPendentes() {
        tableModel.setRowCount(0);
        List<Recurso> pendentes = recursoDAO.listarRecursosPorStatus("Pendente"); 
        
        for (Recurso r : pendentes) {
            tableModel.addRow(new Object[]{
                r.getId(),
                r.getTitulo(),
                r.getTipoRecurso(),
                r.getAutor(),
                r.getCategoria().getNome(),
                r.getConteudo(),
                r.getIdUsuarioCadastro()
            });
        }
    }
    
    private void configurarEventos() {
        btnAprovar.addActionListener(e -> atualizarStatusRecurso("Verificado"));
        btnRejeitar.addActionListener(e -> atualizarStatusRecurso("Rejeitado"));
        btnVerConteudo.addActionListener(e -> visualizarConteudo());
    }
    
    private void atualizarStatusRecurso(String status) {
        int linha = tabelaPendentes.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um recurso na tabela.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idRecurso = (int) tableModel.getValueAt(linha, 0);
        
        if (recursoDAO.atualizarStatus(idRecurso, status)) {
            JOptionPane.showMessageDialog(this, "Recurso marcado como: " + status, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            carregarTabelaPendentes();
        } else {
            JOptionPane.showMessageDialog(this, "Falha ao atualizar o status.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void visualizarConteudo() {
        int linha = tabelaPendentes.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um recurso para ver o conteúdo.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String conteudo = (String) tableModel.getValueAt(linha, 5);
        
        JOptionPane.showMessageDialog(this, 
            "Conteúdo/Link:\n" + conteudo, 
            "Visualizar Conteúdo", 
            JOptionPane.PLAIN_MESSAGE);
        
    }
}