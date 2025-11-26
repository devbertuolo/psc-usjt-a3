package iacibersec.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout; 
import java.awt.Insets;
import java.beans.PropertyVetoException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import iacibersec.dao.RecursoDAO;
import iacibersec.models.Recurso;
import iacibersec.models.Usuario;

public class MainFrame extends JFrame {
    
    private Usuario usuarioLogado; 
    private JDesktopPane desktopPane;
    private JPanel dashboardPanel;

    public MainFrame(Usuario usuarioLogado) {
        super("Sistema de Curadoria | Perfil: " + usuarioLogado.getTipo() + " | Usuário: " + usuarioLogado.getNome());
        this.usuarioLogado = usuarioLogado;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        setLayout(new BorderLayout());

        add(criarHeaderPanel(), BorderLayout.NORTH);

        desktopPane = new JDesktopPane();
        desktopPane.setBackground(Color.decode("#F0F0F0"));
        add(desktopPane, BorderLayout.CENTER);

        setupMenuBar();
        
        mostrarDashboard();
    }
    
    private JPanel criarHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        headerPanel.setBackground(Color.decode("#E5E5E5"));

        JLabel lblInfo = new JLabel("Logado como: " + usuarioLogado.getNome() + " (" + usuarioLogado.getTipo() + ")");
        lblInfo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        headerPanel.add(lblInfo, BorderLayout.WEST);

        JButton btnLogout = new JButton("Logout");
        btnLogout.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            this.dispose();
        });
        
        JPanel panelLogout = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelLogout.setBackground(Color.decode("#E5E5E5"));
        panelLogout.add(btnLogout);
        headerPanel.add(panelLogout, BorderLayout.EAST);

        return headerPanel;
    }
    
    private void mostrarDashboard() {
        desktopPane.removeAll();
        desktopPane.repaint();

        dashboardPanel = criarDashboardCentral(); 
        dashboardPanel.setBounds(0, 0, desktopPane.getWidth(), desktopPane.getHeight());
        
        desktopPane.add(dashboardPanel, JLayeredPane.DEFAULT_LAYER);
        desktopPane.revalidate();
    }

    private JPanel criarDashboardCentral() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        JLabel welcomeLabel = new JLabel("SELECIONE UMA OPÇÃO ABAIXO", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        mainPanel.add(welcomeLabel, BorderLayout.NORTH);

        JPanel botoesPanel = new JPanel(new GridBagLayout());
        
        if ("Administrador".equals(usuarioLogado.getTipo())) {
            adicionarBotoesAdmin(botoesPanel);
        } else if ("Comum".equals(usuarioLogado.getTipo())) {
            adicionarBotoesComum(botoesPanel);
        }

        mainPanel.add(botoesPanel, BorderLayout.CENTER);
        return mainPanel;
    }
    
    private void adicionarBotoesAdmin(JPanel painel) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 50;
        gbc.ipady = 30;
        
        JButton btnUsuarios = new JButton("Gerenciar Usuários");
        btnUsuarios.addActionListener(e -> abrirInternalFrame("Gestão de Usuários", new GestaoUsuariosPanel(), 750, 600));
        gbc.gridx = 0; gbc.gridy = 0; painel.add(btnUsuarios, gbc);

        JButton btnVerificacao = new JButton("Verificar Recursos");

        btnVerificacao.addActionListener(e -> abrirInternalFrame("Verificação de Recursos", new VerificacaoPanel(), 850, 600)); 
        gbc.gridx = 1; gbc.gridy = 0; painel.add(btnVerificacao, gbc);
        
        JButton btnListagem = new JButton("Visualizar Recursos");
        btnListagem.addActionListener(e -> abrirInternalFrame("Listagem de Recursos", new ListagemRecursosPanel(usuarioLogado), 900, 550));
        gbc.gridx = 2; gbc.gridy = 0; painel.add(btnListagem, gbc);
    }

    private void adicionarBotoesComum(JPanel painel) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 50;
        gbc.ipady = 30;
        
        JButton btnCadastro = new JButton("Cadastrar Novo Recurso");
        btnCadastro.addActionListener(e -> abrirInternalFrame("Cadastrar Recurso", new CadastroRecursoPanel(usuarioLogado), 500, 350));
        gbc.gridx = 0; gbc.gridy = 0; painel.add(btnCadastro, gbc);
        
        JButton btnListagem = new JButton("Visualizar Recursos");
        btnListagem.addActionListener(e -> abrirInternalFrame("Listagem de Recursos", new ListagemRecursosPanel(usuarioLogado), 900, 550));
        gbc.gridx = 1; gbc.gridy = 0; painel.add(btnListagem, gbc);
    }

    private JPanel criarListaTopRecursos(RecursoDAO dao, int idCategoria, String titulo) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(titulo));
        
        List<Recurso> topRecursos = dao.listarTopRecursosPorCategoria(idCategoria, 5);
        
        JList<String> lista = new JList<>();
        DefaultListModel<String> listModel = new DefaultListModel<>();

        if (topRecursos.isEmpty()) {
            listModel.addElement("Nenhum recurso avaliado nesta área.");
        } else {
            for (Recurso r : topRecursos) {
                String notaStr = String.format("%.1f", r.getNotaMedia());
                listModel.addElement("⭐ " + notaStr + " - " + r.getTitulo());
            }
        }
        
        lista.setModel(listModel);
        
        panel.add(new JScrollPane(lista), BorderLayout.CENTER);
        return panel;
    }
    
    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu menuGeral = new JMenu("Geral");
        JMenuItem itemDashboard = new JMenuItem("Dashboard");
        itemDashboard.addActionListener(e -> mostrarDashboard());
        menuGeral.add(itemDashboard);
        menuBar.add(menuGeral);

        if ("Administrador".equals(usuarioLogado.getTipo())) {
            JMenu menuAdmin = new JMenu("Administração");
            JMenuItem itemGestaoUsuarios = new JMenuItem("Gestão de Usuários");
            
            itemGestaoUsuarios.addActionListener(e -> {
                abrirInternalFrame("Gestão de Usuários", new GestaoUsuariosPanel(), 750, 600);
            });

            menuAdmin.add(itemGestaoUsuarios);
            menuBar.add(menuAdmin);
        
        } else if ("Comum".equals(usuarioLogado.getTipo())) {
            JMenu menuRecursos = new JMenu("Recursos");
            JMenuItem itemCadastro = new JMenuItem("Cadastrar Novo Recurso");
            JMenuItem itemListagem = new JMenuItem("Visualizar Recursos");
            
            itemCadastro.addActionListener(e -> {
                abrirInternalFrame("Cadastrar Recurso", new CadastroRecursoPanel(usuarioLogado), 500, 350);
            });
            
            itemListagem.addActionListener(e -> {
                abrirInternalFrame("Listagem de Recursos", new ListagemRecursosPanel(usuarioLogado), 900, 550);
            });

            menuRecursos.add(itemCadastro);
            menuRecursos.add(itemListagem);
            menuBar.add(menuRecursos);
        }
    }

    private void abrirInternalFrame(String titulo, JPanel painel, int width, int height) {
        JInternalFrame internalFrame = new JInternalFrame(titulo, true, true, true, true);
        internalFrame.setContentPane(painel);
        internalFrame.setSize(width, height);
        internalFrame.setVisible(true);
        internalFrame.setLocation((desktopPane.getWidth() - width) / 2, (desktopPane.getHeight() - height) / 2);
        
        desktopPane.add(internalFrame, JLayeredPane.MODAL_LAYER);
        try { internalFrame.setSelected(true); } catch (PropertyVetoException pve) { /* ignore */ }
    }
}