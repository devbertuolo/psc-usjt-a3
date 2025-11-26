package iacibersec.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import iacibersec.dao.CategoriaDAO;
import iacibersec.dao.UsuarioDAO;
import iacibersec.models.Categoria;
import iacibersec.models.Usuario;

public class GestaoUsuariosPanel extends JPanel {

    private JTable tabelaUsuarios;
    private DefaultTableModel tableModel;
    
    private JTextField txtNome;
    private JTextField txtIdade;
    private JPasswordField txtSenha;
    private JComboBox<String> cbTipo;
    private JComboBox<Categoria> cbInteresse1;
    private JComboBox<Categoria> cbInteresse2;
    private JButton btnSalvar;
    private JButton btnInativar;
    private JButton btnLimpar;
    
    // Dados
    private List<Categoria> categorias;
    private UsuarioDAO usuarioDAO;
    private Usuario usuarioSelecionado;
    
    public GestaoUsuariosPanel() {
        usuarioDAO = new UsuarioDAO();
        this.categorias = new CategoriaDAO().listarTodas();
        
        setLayout(new BorderLayout(10, 10)); 
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(criarPainelTabela(), BorderLayout.CENTER);

        add(criarPainelFormulario(), BorderLayout.SOUTH);
        
        carregarTabelaUsuarios();
        
        configurarEventos();
    }
    
    private JPanel criarPainelTabela() {
        String[] colunas = {"ID", "Nome", "Idade", "Tipo", "Ativo"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        tabelaUsuarios = new JTable(tableModel);
        
        tabelaUsuarios.getColumnModel().getColumn(0).setMaxWidth(50);
        
        JScrollPane scrollPane = new JScrollPane(tabelaUsuarios);
        
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBorder(BorderFactory.createTitledBorder("Usuários Cadastrados"));
        painel.add(scrollPane, BorderLayout.CENTER);
        return painel;
    }

    private JPanel criarPainelFormulario() {
        JPanel painel = new JPanel(new BorderLayout(5, 5));
        painel.setBorder(BorderFactory.createTitledBorder("Cadastro e Edição"));
        
        JPanel painelCampos = new JPanel(new GridLayout(4, 2, 10, 5)); 
        
        txtNome = new JTextField(20);
        txtIdade = new JTextField(5);
        txtSenha = new JPasswordField(20);
        cbTipo = new JComboBox<>(new String[]{"Comum", "Administrador"});
        
        cbInteresse1 = new JComboBox<>();
        cbInteresse2 = new JComboBox<>();
        Categoria catNula = new Categoria(0, "Nenhum Interesse");
        cbInteresse1.addItem(catNula);
        cbInteresse2.addItem(catNula);
        
        for (Categoria cat : categorias) {
            cbInteresse1.addItem(cat);
            cbInteresse2.addItem(cat);
        }
        
        painelCampos.add(new JLabel("Nome:"));
        painelCampos.add(txtNome);
        
        painelCampos.add(new JLabel("Idade:"));
        painelCampos.add(txtIdade);
        
        painelCampos.add(new JLabel("Senha (Nova):"));
        painelCampos.add(txtSenha);
        
        painelCampos.add(new JLabel("Tipo:"));
        painelCampos.add(cbTipo);
        
        JPanel painelInteresses = new JPanel(new GridLayout(1, 4, 5, 5));
        painelInteresses.add(new JLabel("Interesses (Até 2):"));
        painelInteresses.add(cbInteresse1);
        painelInteresses.add(new JLabel("e"));
        painelInteresses.add(cbInteresse2);

        painel.add(painelCampos, BorderLayout.NORTH);
        painel.add(painelInteresses, BorderLayout.CENTER);
        
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnSalvar = new JButton("Salvar/Atualizar");
        btnInativar = new JButton("Inativar Selecionado");
        btnLimpar = new JButton("Limpar Formulário");
        
        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnInativar);
        painelBotoes.add(btnLimpar);
        painel.add(painelBotoes, BorderLayout.SOUTH);
        
        return painel;
    }
    
    private void carregarTabelaUsuarios() {
        tableModel.setRowCount(0);
        List<Usuario> usuarios = usuarioDAO.listarTodos();
        
        for (Usuario u : usuarios) {
            tableModel.addRow(new Object[]{
                u.getId(),
                u.getNome(),
                u.getIdade(),
                u.getTipo(),
                u.isAtivo() ? "Sim" : "Não"
            });
        }
    }
    
    private void configurarEventos() {
        btnLimpar.addActionListener(e -> limparFormulario());
        btnSalvar.addActionListener(e -> salvarUsuario());
        
        tabelaUsuarios.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabelaUsuarios.getSelectedRow() != -1) {
                carregarUsuarioParaEdicao();
            }
        });
        
        btnInativar.addActionListener(e -> inativarUsuario());
    }

    private void limparFormulario() {
        txtNome.setText("");
        txtIdade.setText("");
        txtSenha.setText("");
        cbTipo.setSelectedItem("Comum");
        cbInteresse1.setSelectedIndex(0);
        cbInteresse2.setSelectedIndex(0);
        usuarioSelecionado = null;
        btnSalvar.setText("Salvar/Atualizar");
        tabelaUsuarios.clearSelection();
    }
    
    private void carregarUsuarioParaEdicao() {
        int linha = tabelaUsuarios.getSelectedRow();
        if (linha != -1) {
            int id = (int) tableModel.getValueAt(linha, 0);
            
            usuarioSelecionado = new Usuario(); 
            usuarioSelecionado.setId(id);
            btnSalvar.setText("Atualizar");
            btnInativar.setEnabled(true);
            
            txtNome.setText((String) tableModel.getValueAt(linha, 1));
            txtIdade.setText(String.valueOf(tableModel.getValueAt(linha, 2)));
            cbTipo.setSelectedItem((String) tableModel.getValueAt(linha, 3));
            
            txtSenha.setText("");
            cbInteresse1.setSelectedIndex(0);
            cbInteresse2.setSelectedIndex(0);
        }
    }
    
    private void salvarUsuario() {
        String nome = txtNome.getText().trim();
        String idadeStr = txtIdade.getText().trim();
        String senha = new String(txtSenha.getPassword());
        String tipo = (String) cbTipo.getSelectedItem();
        
        Categoria cat1 = (Categoria) cbInteresse1.getSelectedItem();
        Categoria cat2 = (Categoria) cbInteresse2.getSelectedItem();
        
        List<Integer> interesses = new ArrayList<>();
        if (cat1 != null && cat1.getId() != 0) interesses.add(cat1.getId());
        if (cat2 != null && cat2.getId() != 0) interesses.add(cat2.getId());
        
        if (nome.isEmpty() || idadeStr.isEmpty() || tipo == null) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos obrigatórios (Nome, Idade, Tipo).", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int idade = Integer.parseInt(idadeStr);
            if (idade < 18) {
                JOptionPane.showMessageDialog(this, "A idade deve ser 18 ou superior.", "Erro de Idade", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Usuario novoUsuario = new Usuario();
            novoUsuario.setNome(nome);
            novoUsuario.setIdade(idade);
            novoUsuario.setTipo(tipo);
            novoUsuario.setInteressesIds(interesses);
            novoUsuario.setAtivo(true); 

            boolean sucesso;

            if (usuarioSelecionado == null) {
                if (senha.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "A senha é obrigatória para novos cadastros.", "Erro de Senha", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                novoUsuario.setSenha(senha);
                sucesso = usuarioDAO.cadastrar(novoUsuario);

            } else {
                novoUsuario.setId(usuarioSelecionado.getId());

                if (!senha.isEmpty()) {
                    novoUsuario.setSenha(senha);
                } else {
                    // Para simplificar, em uma aplicação real buscaríamos a senha antiga.
                    // Aqui, faremos o DAO lidar com a atualização parcial (apenas nome, idade, tipo, interesses).
                }
                sucesso = usuarioDAO.editar(novoUsuario);

            }

            if (sucesso) {
                JOptionPane.showMessageDialog(this, "Usuário salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                limparFormulario();
                carregarTabelaUsuarios();
            } else {
                JOptionPane.showMessageDialog(this, "Falha ao salvar o usuário. Verifique o log.", "Erro de Persistência", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Idade inválida. Digite um número inteiro.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void inativarUsuario() {
        int linha = tabelaUsuarios.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário na tabela para inativar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) tableModel.getValueAt(linha, 0);
        String nomeUsuario = (String) tableModel.getValueAt(linha, 1);
       
        if (id == 1) {
             JOptionPane.showMessageDialog(this, "O Administrador inicial (ID 1) não pode ser inativado.", "Aviso de Segurança", JOptionPane.WARNING_MESSAGE);
             return;
        }

        int confirmacao = JOptionPane.showConfirmDialog(this, 
            "Tem certeza que deseja INATIVAR o usuário: " + nomeUsuario + "?", 
            "Confirmação de Inativação", JOptionPane.YES_NO_OPTION);

        if (confirmacao == JOptionPane.YES_OPTION) {
            boolean sucesso = usuarioDAO.inativar(id);

            if (sucesso) {
                JOptionPane.showMessageDialog(this, "Usuário " + nomeUsuario + " inativado com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                carregarTabelaUsuarios();
                limparFormulario();
            } else {
                JOptionPane.showMessageDialog(this, "Falha ao inativar o usuário.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}