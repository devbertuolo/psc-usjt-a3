package iacibersec.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import iacibersec.models.Recurso;

public class RecursoCard extends JPanel {

    private Recurso recurso;

    public RecursoCard(Recurso recurso) {
        this.recurso = recurso;
        setLayout(new BorderLayout(5, 5));
        
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        setPreferredSize(new Dimension(300, 180));
        setMaximumSize(new Dimension(300, 180));

        // --- 1. Título e Categoria (NORTH) ---
        JLabel lblTitulo = new JLabel("<html><b>" + recurso.getTitulo() + "</b></html>");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        add(lblTitulo, BorderLayout.NORTH);

        // --- 2. Conteúdo Principal e Detalhes (CENTER) ---
        JPanel detalhesPanel = new JPanel(new GridLayout(4, 1));
        detalhesPanel.setBackground(Color.WHITE); 
        
        JLabel lblTipo = new JLabel("Tipo: " + recurso.getTipoRecurso());
        detalhesPanel.add(lblTipo);
        
        JLabel lblAutor = new JLabel("Fonte: " + recurso.getAutor());
        detalhesPanel.add(lblAutor);

        JLabel lblCategoria = new JLabel("Área: " + recurso.getCategoria().getNome());
        detalhesPanel.add(lblCategoria);
        
        // --- Link Clicável e Checagem de Nulo ---
        String conteudoBruto = recurso.getConteudo();
        String linkDisplay;

        if (conteudoBruto == null || conteudoBruto.isEmpty()) {
            linkDisplay = "N/D (Não informado)";
        } else {
            linkDisplay = conteudoBruto.length() > 30 ? conteudoBruto.substring(0, 30) + "..." : conteudoBruto;
        }

        JLabel lblLink = new JLabel("<html>Link: <u>" + linkDisplay + "</u></html>");
        lblLink.setForeground(Color.BLUE);
        lblLink.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (conteudoBruto != null && !conteudoBruto.isEmpty()) {
            lblLink.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent evt) {
                    try {
                        URI uri = new URI(conteudoBruto);
                        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                            Desktop.getDesktop().browse(uri);
                        } else {
                            JOptionPane.showMessageDialog(RecursoCard.this, "Não foi possível abrir o navegador. Copie o link: " + conteudoBruto);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(RecursoCard.this, "Link inválido ou inacessível: " + ex.getMessage(), "Erro de Link", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                }
            });
        }
        
        detalhesPanel.add(lblLink);
        add(detalhesPanel, BorderLayout.CENTER);

        // --- 3. Avaliação e Status (SOUTH) ---
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(Color.decode("#F5F5F5"));
        
        JLabel lblAvaliacao = new JLabel(getEstrelas(recurso.getNotaMedia()));
        lblAvaliacao.setFont(new Font("Segoe UI", Font.BOLD, 16));
        footerPanel.add(lblAvaliacao, BorderLayout.WEST);

        JLabel lblStatus = new JLabel(recurso.getStatus(), SwingConstants.RIGHT);
        if ("Verificado".equals(recurso.getStatus())) {
            lblStatus.setForeground(Color.decode("#008000"));
        } else if ("Pendente".equals(recurso.getStatus())) {
            lblStatus.setForeground(Color.decode("#FF8C00"));
        } else if ("Rejeitado".equals(recurso.getStatus())) {
            lblStatus.setForeground(Color.RED);
        }
        footerPanel.add(lblStatus, BorderLayout.EAST);
        
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    private String getEstrelas(double nota) {
        int estrelasCheias = (int) Math.round(nota);
        StringBuilder sb = new StringBuilder();
        
        sb.append(String.format("Nota: %.1f ", nota));
        
        for (int i = 0; i < 5; i++) {
            if (i < estrelasCheias) {
                sb.append("★");
            } else {
                sb.append("☆");
            }
        }
        return "<html>" + sb.toString() + "</html>";
    }
}