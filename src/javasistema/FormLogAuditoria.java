package javasistema;
// agrupa as classes relacionadas

import javax.swing.*;
// importa componentes graficos
import javax.swing.table.DefaultTableCellRenderer;
// importa o renderizador de celulas
import javax.swing.table.DefaultTableModel;
// importa o modelo de dados da tabela
import java.awt.*;
// importa ferramentas de desenho
import java.io.BufferedReader;
// importa o leitor com buffer
import java.io.FileNotFoundException;
// importa excecao de arquivo nao encontrado
import java.io.FileReader;
// importa o leitor de arquivo
import java.io.IOException;
// importa excecao de I/O

public class FormLogAuditoria extends JFrame
{
    // janela de visualização do log de auditoria

    private JTable tabela;
    // tabela que exibe os registros do log
    private DefaultTableModel modelo;
    // modelo de dados da tabela

    private static final String ARQUIVO_LOG = "auditoria.log";
    // nome do arquivo de log — deve coincidir com AuditoriaUtil

    /**
     * Construtor da tela de log de auditoria
     */
    public FormLogAuditoria()
    {
        super("Log de Auditoria");
        // define o titulo da janela
        setSize(1000, 600);
        // define o tamanho da janela
        setLocationRelativeTo(null);
        // centraliza na tela
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // fecha apenas esta janela ao fechar
        setLayout(new BorderLayout());
        // define o layout

        // ===== BARRA DE BOTÕES =====
        JToolBar barra = new JToolBar();
        // cria a barra de ferramentas
        barra.setFloatable(false);
        // impede que a barra seja arrastada

        JButton btnAtualizar = new JButton("ATUALIZAR");
        // botão para recarregar o log
        JButton btnSair = new JButton("SAIR");
        // botão para fechar a janela

        Dimension tamanhoPadrao = new Dimension(100, 40);
        // tamanho padrão dos botões
        btnAtualizar.setPreferredSize(tamanhoPadrao);
        // define tamanho preferido do botão atualizar
        btnAtualizar.setMinimumSize(tamanhoPadrao);
        // define tamanho mínimo do botão atualizar
        btnAtualizar.setMaximumSize(tamanhoPadrao);
        // define tamanho máximo do botão atualizar
        btnSair.setPreferredSize(tamanhoPadrao);
        // define tamanho preferido do botão sair
        btnSair.setMinimumSize(tamanhoPadrao);
        // define tamanho mínimo do botão sair
        btnSair.setMaximumSize(tamanhoPadrao);
        // define tamanho máximo do botão sair

        barra.add(btnAtualizar);
        // adiciona o botão atualizar na barra
        barra.add(btnSair);
        // adiciona o botão sair na barra

        btnAtualizar.addActionListener(e -> carregarLog());
        // listener do botão atualizar: recarrega o log

        btnSair.addActionListener(e -> dispose());
        // listener do botão sair: fecha a janela

        add(barra, BorderLayout.NORTH);
        // adiciona a barra no topo da janela

        // ===== GRID =====
        String[] colunas = {"Data/Hora", "Login", "Evento", "Detalhe"};
        // define as colunas da tabela

        modelo = new DefaultTableModel(colunas, 0)
        {
            // cria o modelo impedindo edição
            @Override
            public boolean isCellEditable(int row, int column)
            {
                return false;
                // todas as células são somente leitura
            }
        };

        tabela = new JTable(modelo);
        // cria a tabela com o modelo
        tabela.setRowHeight(25);
        // define a altura das linhas
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // permite selecionar apenas uma linha por vez

        // Larguras das colunas
        tabela.getColumnModel().getColumn(0).setPreferredWidth(150);
        // coluna Data/Hora
        tabela.getColumnModel().getColumn(1).setPreferredWidth(120);
        // coluna Login
        tabela.getColumnModel().getColumn(2).setPreferredWidth(150);
        // coluna Evento
        tabela.getColumnModel().getColumn(3).setPreferredWidth(550);
        // coluna Detalhe

        // Alinhar Data/Hora à esquerda com fonte mono para legibilidade
        DefaultTableCellRenderer mono = new DefaultTableCellRenderer();
        // cria renderizador personalizado
        mono.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        // define fonte monoespaçada para datas
        tabela.getColumnModel().getColumn(0).setCellRenderer(mono);
        // aplica na coluna Data/Hora

        JScrollPane scroll = new JScrollPane(tabela);
        // envolve a tabela em painel com scroll
        add(scroll, BorderLayout.CENTER);
        // adiciona o scroll no centro da janela

        // ===== RODAPÉ =====
        JLabel rodape = new JLabel("  SuperSistema — Log de Auditoria  |  Leitura do arquivo: " + ARQUIVO_LOG);
        // label do rodapé com informação do arquivo
        rodape.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        // margem interna do rodapé
        add(rodape, BorderLayout.SOUTH);
        // adiciona o rodapé no fundo

        carregarLog();
        // carrega os dados ao abrir

        setVisible(true);
        // exibe a janela
    }
    // fecha o construtor

    /**
     * Lê o arquivo de log e preenche a tabela
     * Cada linha do arquivo tem o formato: data | login | evento | detalhe
     */
    private void carregarLog()
    {
        modelo.setRowCount(0);
        // limpa todas as linhas antes de recarregar

        try
        {
            BufferedReader br = new BufferedReader(new FileReader(ARQUIVO_LOG));
            // abre o arquivo de log para leitura
            String linha;
            // variável para cada linha lida

            while ((linha = br.readLine()) != null)
            {
                // percorre cada linha do arquivo
                String[] partes = linha.split("\\|");
                // divide a linha pelo separador |

                if (partes.length == 4)
                {
                    // linha válida com 4 campos
                    modelo.addRow(new Object[]{
                        partes[0].trim(),
                        // campo Data/Hora
                        partes[1].trim(),
                        // campo Login
                        partes[2].trim(),
                        // campo Evento
                        partes[3].trim()
                        // campo Detalhe
                    });
                    // adiciona a linha na tabela
                }
                // fecha o if
            }
            // fecha o while

            br.close();
            // fecha o leitor de arquivo

            // rolar para a última linha (registro mais recente aparece no final)
            if (modelo.getRowCount() > 0)
            {
                // se há linhas na tabela
                tabela.scrollRectToVisible(
                    tabela.getCellRect(modelo.getRowCount() - 1, 0, true)
                );
                // rola para a última linha do log
            }
            // fecha o if
        }
        catch (FileNotFoundException e)
        {
            // arquivo ainda não foi criado — situação esperada antes do primeiro login
            JOptionPane.showMessageDialog(
                this,
                "Nenhum registro de auditoria encontrado ainda.\n"
                + "O arquivo será criado automaticamente ao realizar o primeiro login.",
                "Log Vazio",
                JOptionPane.INFORMATION_MESSAGE
            );
            // informa que o arquivo não existe ainda
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(
                this,
                "Erro ao carregar log de auditoria:\n" + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE
            );
            // exibe mensagem de erro genérico de leitura
        }
    }
    // fecha o metodo

}
// fecha a classe
