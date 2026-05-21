package javasistema;
// agrupa as classes relacionadas

import javax.swing.*;
// importa componentes graficos
import javax.swing.table.DefaultTableCellRenderer;
// importa o renderizador de celulas da tabela
import javax.swing.table.DefaultTableModel;
// importa o modelo de dados da tabela
import java.awt.*;
// importa ferramentas de desenho
import java.sql.Connection;
// importa a conexão com banco
import java.sql.PreparedStatement;
// importa PreparedStatement para queries seguras
import java.sql.ResultSet;
// importa ResultSet para leitura de resultados

public class FormUsuariosListar extends JFrame
{
    // janela de listagem de usuários

    JTable tabela;
    // tabela que exibe os usuarios
    DefaultTableModel modelo;
    // modelo de dados da tabela

    private final String loginOperador;
    // login do usuario que abriu esta tela (para auditoria futura)

    public FormUsuariosListar(String loginOperador)
    {
        super("Gestão de Usuários");
        // define o titulo da janela
        this.loginOperador = loginOperador;
        // guarda o login para uso em auditoria futura
        setSize(1000, 600);
        // define o tamanho da janela
        setLocationRelativeTo(null);
        // centraliza na tela
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        // fecha apenas esta janela ao fechar
        setLayout(new BorderLayout());
        // define o layout como BorderLayout

        // ===== BARRA DE BOTÕES =====
        JToolBar barra = new JToolBar();
        // cria a barra de ferramentas
        barra.setFloatable(false);
        // impede que a barra seja arrastada

        Dimension tamanhoPadrao = new Dimension(100, 40);
        // define o tamanho padrão dos botões

        JButton btnIncluir = new JButton("INCLUIR");
        // botão para incluir usuário
        JButton btnEditar = new JButton("EDITAR");
        // botão para editar usuário selecionado
        JButton btnExcluir = new JButton("EXCLUIR");
        // botão para excluir usuário selecionado
        JButton btnAtualizar = new JButton("ATUALIZAR");
        // botão para recarregar a listagem do banco
        JButton btnSair = new JButton("SAIR");
        // botão para fechar a janela

        JButton[] botoes = {btnIncluir, btnEditar, btnExcluir, btnAtualizar, btnSair};
        // array com todos os botões

        for (JButton b : botoes)
        {
            // aplica o tamanho padrão em cada botão
            b.setPreferredSize(tamanhoPadrao);
            // define tamanho preferido
            b.setMinimumSize(tamanhoPadrao);
            // define tamanho mínimo
            b.setMaximumSize(tamanhoPadrao);
            // define tamanho máximo
            barra.add(b);
            // adiciona o botão na barra
        }

        // ===== LISTENERS DOS BOTÕES =====

        btnIncluir.addActionListener(e -> FormUsuariosIncluir.abrir(this, loginOperador));
        // listener do botão incluir: abre formulário de inclusão via FormUsuariosIncluir

        btnEditar.addActionListener(e -> abrirFormEdicao());
        // listener do botão editar: abre formulário em modo edição

        btnExcluir.addActionListener(e -> FormUsuariosExcluir.excluir(this, loginOperador));
        // listener do botão excluir: delega exclusão ao FormUsuariosExcluir

        btnAtualizar.addActionListener(e -> carregarUsuarios());
        // listener do botão atualizar: recarrega a lista do banco

        btnSair.addActionListener(e -> dispose());
        // listener do botão sair: fecha a janela

        add(barra, BorderLayout.NORTH);
        // adiciona a barra no topo da janela

        // ===== GRID =====
        String[] colunas = {"ID", "Nome", "Login", "E-mail"};
        // define as colunas da tabela

        modelo = new DefaultTableModel(colunas, 0)
        {
            // cria o modelo da tabela impedindo edição
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

        // Alinhar coluna ID à direita
        DefaultTableCellRenderer direita = new DefaultTableCellRenderer();
        // cria renderizador alinhado à direita
        direita.setHorizontalAlignment(SwingConstants.RIGHT);
        // define alinhamento à direita
        tabela.getColumnModel().getColumn(0).setCellRenderer(direita);
        // aplica o renderizador na coluna ID

        // Larguras das colunas
        tabela.getColumnModel().getColumn(0).setPreferredWidth(50);
        // coluna ID
        tabela.getColumnModel().getColumn(1).setPreferredWidth(300);
        // coluna Nome
        tabela.getColumnModel().getColumn(2).setPreferredWidth(180);
        // coluna Login
        tabela.getColumnModel().getColumn(3).setPreferredWidth(300);
        // coluna Email

        // Duplo clique na linha abre formulário de edição
        tabela.addMouseListener(new java.awt.event.MouseAdapter()
        {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e)
            {
                if (e.getClickCount() == 2)
                {
                    // detecta duplo clique na tabela
                    abrirFormEdicao();
                    // abre o formulário de edição
                }
            }
        });
        // fecha o MouseListener

        JScrollPane scroll = new JScrollPane(tabela);
        // envolve a tabela em um painel com scroll
        add(scroll, BorderLayout.CENTER);
        // adiciona o scroll no centro da janela

        // ===== RODAPÉ =====
        JLabel rodape = new JLabel("  SuperSistema — Gestão de Usuários  |  Duplo clique para editar");
        // label do rodapé com dica de uso
        rodape.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        // margem interna do rodapé
        add(rodape, BorderLayout.SOUTH);
        // adiciona o rodapé no fundo

        carregarUsuarios();
        // carrega os dados do banco ao abrir

        setVisible(true);
        // exibe a janela
    }

    /**
     * Abre o FormUsuarioEditar em modo edição com o usuário selecionado
     */
    private void abrirFormEdicao()
    {
        int linhaSelecionada = tabela.getSelectedRow();
        // obtém o índice da linha selecionada na tabela

        if (linhaSelecionada == -1)
        {
            // nenhuma linha selecionada
            JOptionPane.showMessageDialog(
                this,
                "Selecione um usuário na lista para editar.",
                "Atenção",
                JOptionPane.WARNING_MESSAGE
            );
            // avisa o usuário para selecionar um item
            return;
            // cancela a ação
        }

        int usuarioId = (int) modelo.getValueAt(linhaSelecionada, 0);
        // obtém o ID do usuário da coluna 0 (ID) da linha selecionada

        FormUsuarioEditar form = new FormUsuarioEditar(this, usuarioId);
        // cria o formulário de edição com o ID do usuário
        form.setLoginOperador(loginOperador);
        // passa o login do operador para auditoria
        form.setVisible(true);
        // exibe o formulário (bloqueante por ser modal)

        if (form.isSalvo())
        {
            // se o usuário confirmou e salvou
            carregarUsuarios();
            // recarrega a listagem para refletir as alterações
        }
    }

    /**
     * Busca os usuários no banco de dados e preenche a tabela
     */
    public void carregarUsuarios()
    {
        modelo.setRowCount(0);
        // limpa todas as linhas da tabela antes de recarregar

        String sql = "SELECT usuario_id, nome, login, email FROM tb_usuarios ORDER BY nome";
        // query que busca todos os usuários ordenados por nome

        try
        {
            Connection conn = Conexao.getConnection();
            // abre conexão com o banco
            PreparedStatement stmt = conn.prepareStatement(sql);
            // prepara a query
            ResultSet rs = stmt.executeQuery();
            // executa a query e obtém os resultados

            while (rs.next())
            {
                // percorre cada linha do resultado
                int usuario_id = rs.getInt("usuario_id");
                // ID do usuário
                String nome = rs.getString("nome");
                // nome completo
                String login = rs.getString("login");
                // login
                String email = rs.getString("email");
                // email (pode ser nulo)

                modelo.addRow(new Object[]{
                    usuario_id,
                    nome,
                    login,
                    (email != null ? email : "—")
                });
                // adiciona a linha na tabela
            }

            rs.close();
            // fecha o ResultSet
            stmt.close();
            // fecha o PreparedStatement
            conn.close();
            // fecha a conexão com o banco
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(
                this,
                "Erro ao carregar usuários:\n" + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE
            );
            // exibe mensagem de erro caso a consulta falhe
        }
    }
}
// fecha a classe
