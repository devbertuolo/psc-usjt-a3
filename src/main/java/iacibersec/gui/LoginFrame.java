package iacibersec.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import iacibersec.dao.UsuarioDAO;
import iacibersec.models.Usuario;

public class LoginFrame extends JFrame {

    private final JTextField txtNome;
    private final JPasswordField txtSenha;
    private final JButton btnLogin;
    private final JButton btnNovoCadastro;

    public LoginFrame() {
        super("Sistema de Curadoria - Conectar-se");
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350, 220);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel painelForm = new JPanel(new GridLayout(2, 2, 10, 10));
        painelForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        txtNome = new JTextField(20);
        txtSenha = new JPasswordField(20);
        
        painelForm.add(new JLabel("Nome de Usuário:"));
        painelForm.add(txtNome);
        painelForm.add(new JLabel("Senha:"));
        painelForm.add(txtSenha);
        
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnLogin = new JButton("Login");
        btnNovoCadastro = new JButton("Novo Cadastro");
        
        btnLogin.setPreferredSize(new Dimension(100, 30));
        btnNovoCadastro.setPreferredSize(new Dimension(120, 30));
        
        painelBotoes.add(btnLogin);
        painelBotoes.add(btnNovoCadastro);
        
        add(painelForm, BorderLayout.CENTER);
        add(painelBotoes, BorderLayout.SOUTH);

        btnLogin.addActionListener(e -> tentarLogin());
        btnNovoCadastro.addActionListener(e -> abrirCadastroInicial());
    }
    
    private void tentarLogin() {
        String nome = txtNome.getText().trim();
        String senha = new String(txtSenha.getPassword()); 
        
        if (nome.isEmpty() || senha.isEmpty()) {
             JOptionPane.showMessageDialog(this, "Preencha o nome de usuário e a senha.", "Campos Vazios", JOptionPane.WARNING_MESSAGE);
             return;
        }

        Usuario usuarioTentativa = new Usuario(nome, senha);
        UsuarioDAO dao = new UsuarioDAO();
        
        try {
            Usuario usuarioLogado = dao.login(usuarioTentativa);

            if (usuarioLogado != null) {
                JOptionPane.showMessageDialog(this, "Login bem-sucedido! Bem-vindo(a), " + usuarioLogado.getNome());
                new MainFrame(usuarioLogado).setVisible(true);
                this.dispose(); 
                
            } else {
                JOptionPane.showMessageDialog(this, "Nome de usuário ou senha inválidos, ou conta inativa.", 
                                              "Erro de Login", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao tentar conectar ao banco de dados: " + ex.getMessage(), 
                                          "Erro Crítico", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void abrirCadastroInicial() {
        CadastroInicialFrame cadastroFrame = new CadastroInicialFrame();
        cadastroFrame.setVisible(true);
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Nimbus Look and Feel não disponível. Usando padrão.");
        }
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}