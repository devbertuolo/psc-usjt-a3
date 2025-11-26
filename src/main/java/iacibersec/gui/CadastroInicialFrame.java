package iacibersec.gui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import iacibersec.dao.CategoriaDAO;
import iacibersec.dao.UsuarioDAO;
import iacibersec.models.Categoria;
import iacibersec.models.Usuario;

public class CadastroInicialFrame extends JFrame {

    private JTextField txtNome;
    private JTextField txtIdade;
    private JTextField txtLogin;
    private JPasswordField txtSenha;
    private JComboBox<Categoria> cbInteresse1;
    private JComboBox<Categoria> cbInteresse2;
    private JButton btnCadastrar;
    
    private List<Categoria> categorias;

    public CadastroInicialFrame() {
        super("Auto-Registro de Usuário Comum");
        
        this.categorias = new CategoriaDAO().listarTodas();
        
        setSize(500, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel lblTitulo = new JLabel("Crie sua Conta de Usuário Comum", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        txtNome = new JTextField();
        txtIdade = new JTextField();
        txtLogin = new JTextField();
        txtSenha = new JPasswordField();
        
        cbInteresse1 = new JComboBox<>();
        cbInteresse2 = new JComboBox<>();
        
        carregarComboBoxInteresses();

        formPanel.add(new JLabel("Nome Completo:"));
        formPanel.add(txtNome);
        formPanel.add(new JLabel("Idade:"));
        formPanel.add(txtIdade);
        formPanel.add(new JLabel("Login (Nome de Usuário):"));
        formPanel.add(txtLogin);
        formPanel.add(new JLabel("Senha:"));
        formPanel.add(txtSenha);
        formPanel.add(new JLabel("Interesse Principal (1):"));
        formPanel.add(cbInteresse1);
        formPanel.add(new JLabel("Segundo Interesse (2):"));
        formPanel.add(cbInteresse2);

        add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        btnCadastrar = new JButton("Cadastrar");
        btnPanel.add(btnCadastrar);
        add(btnPanel, BorderLayout.SOUTH);

        btnCadastrar.addActionListener(e -> cadastrarNovoUsuario());
    }
    
    private void carregarComboBoxInteresses() {
        Categoria catNula = new Categoria(0, "Nenhum Interesse Selecionado");
        cbInteresse1.addItem(catNula);
        cbInteresse2.addItem(catNula);
        
        for (Categoria cat : categorias) {
            cbInteresse1.addItem(cat);
            cbInteresse2.addItem(cat);
        }
    }

    private void cadastrarNovoUsuario() {
        String nome = txtNome.getText().trim();
        String idadeStr = txtIdade.getText().trim();
        String login = txtLogin.getText().trim();
        String senha = new String(txtSenha.getPassword());
        
        Categoria cat1 = (Categoria) cbInteresse1.getSelectedItem();
        Categoria cat2 = (Categoria) cbInteresse2.getSelectedItem();
        
        if (nome.isEmpty() || idadeStr.isEmpty() || login.isEmpty() || senha.isEmpty() || cat1 == null || cat1.getId() == 0) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos e selecione pelo menos um interesse principal.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int idade;
        try {
            idade = Integer.parseInt(idadeStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "A idade deve ser um número válido.", "Erro de Validação", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        List<Integer> interesses = new ArrayList<>();
        interesses.add(cat1.getId());
        
        if (cat2 != null && cat2.getId() != 0 && cat2.getId() != cat1.getId()) {
             interesses.add(cat2.getId());
        }

        Usuario novoUsuario = new Usuario();
        novoUsuario.setNome(nome);
        novoUsuario.setIdade(idade);
        novoUsuario.setSenha(senha);
        novoUsuario.setTipo("Comum");
        novoUsuario.setAtivo(true);
        novoUsuario.setInteressesIds(interesses);

        UsuarioDAO dao = new UsuarioDAO();
        boolean sucesso = dao.cadastrar(novoUsuario);

        if (sucesso) {
            JOptionPane.showMessageDialog(this, "Cadastro realizado com sucesso! Use seu login e senha.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            this.dispose(); 
        } else {
            JOptionPane.showMessageDialog(this, "Falha ao cadastrar. O login pode já estar em uso.", "Erro de Cadastro", JOptionPane.ERROR_MESSAGE);
        }
    }
}