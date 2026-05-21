package javasistema;
// agrupa as classes relacionadas

import javax.swing.*;
// importa componentes graficos
import java.awt.*;
// importa ferramentas de desenho
import java.sql.Connection;
// importa a conexão com banco
import java.sql.PreparedStatement;
// importa PreparedStatement para queries seguras
import java.sql.ResultSet;
// importa ResultSet para leitura de resultados

public class FormUsuarioEditar extends JDialog
{
    // diálogo modal para incluir ou editar um usuário

    private boolean salvo = false;
    // indica se o usuário confirmou e salvou com sucesso

    private final int usuarioId;
    // ID do usuário em edição (0 quando for inclusão)

    // ===== CAMPOS DO FORMULÁRIO =====
    private final JTextField campoNome;
    // campo para o nome completo
    private final JTextField campoLogin;
    // campo para o login
    private final JPasswordField campoSenha;
    // campo para a senha
    private final JTextField campoEmail;
    // campo para o e-mail

    private final JButton btnSalvar;
    // botão de salvar
    private final JButton btnCancelar;
    // botão de cancelar

    private String loginOperador = "sistema";
    // login do operador logado (para auditoria)

    /**
     * Construtor — recebe o ID do usuário a editar, ou 0 para incluir novo
     */
    public FormUsuarioEditar(JFrame janelaPai, int usuarioId)
    {
        super(janelaPai, usuarioId == 0 ? "Incluir Usuário" : "Editar Usuário", true);
        // define o título conforme o modo e torna modal

        this.usuarioId = usuarioId;
        // guarda o ID para uso nos métodos de salvar

        // ===== PAINEL PRINCIPAL =====
        JPanel painel = new JPanel();
        // cria o painel
        painel.setLayout(new GridBagLayout());
        // usa GridBagLayout para posicionamento preciso
        painel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        // margem interna do painel

        GridBagConstraints gbc = new GridBagConstraints();
        // objeto de restrições de layout
        gbc.insets = new Insets(6, 6, 6, 6);
        // espaçamento entre componentes
        gbc.fill = GridBagConstraints.HORIZONTAL;
        // preenche horizontalmente

        // ----- Nome -----
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        painel.add(new JLabel("Nome completo:"), gbc);
        // label nome

        campoNome = new JTextField(25);
        // campo de texto para o nome
        gbc.gridx = 1;
        gbc.weightx = 1;
        painel.add(campoNome, gbc);
        // adiciona campo nome

        // ----- Login -----
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        painel.add(new JLabel("Login:"), gbc);
        // label login

        campoLogin = new JTextField(25);
        // campo de texto para o login
        gbc.gridx = 1;
        gbc.weightx = 1;
        painel.add(campoLogin, gbc);
        // adiciona campo login

        // ----- Senha -----
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        painel.add(new JLabel("Senha:"), gbc);
        // label senha

        campoSenha = new JPasswordField(25);
        // campo de senha (caracteres ocultos)
        gbc.gridx = 1;
        gbc.weightx = 1;
        painel.add(campoSenha, gbc);
        // adiciona campo senha

        // ----- E-mail -----
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        painel.add(new JLabel("E-mail (opcional):"), gbc);
        // label email

        campoEmail = new JTextField(25);
        // campo de texto para o e-mail
        gbc.gridx = 1;
        gbc.weightx = 1;
        painel.add(campoEmail, gbc);
        // adiciona campo email

        // ===== PAINEL DE BOTÕES =====
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        // painel alinhado à direita

        btnSalvar = new JButton("SALVAR");
        // botão de confirmação
        btnCancelar = new JButton("CANCELAR");
        // botão de cancelamento

        Dimension tamBotao = new Dimension(100, 32);
        // tamanho padrão dos botões
        btnSalvar.setPreferredSize(tamBotao);
        // aplica tamanho ao botão salvar
        btnCancelar.setPreferredSize(tamBotao);
        // aplica tamanho ao botão cancelar

        painelBotoes.add(btnSalvar);
        // adiciona botão salvar
        painelBotoes.add(btnCancelar);
        // adiciona botão cancelar

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.insets = new Insets(14, 6, 6, 6);
        painel.add(painelBotoes, gbc);
        // adiciona painel de botões ao formulário

        add(painel);
        // adiciona painel à janela

        // ===== AÇÕES =====
        btnSalvar.addActionListener(e -> salvar());
        // listener do botão salvar

        btnCancelar.addActionListener(e -> dispose());
        // listener do botão cancelar: fecha sem salvar

        // ===== PRÉ-CARREGA DADOS SE FOR EDIÇÃO =====
        if (usuarioId > 0)
        {
            carregarDados();
            // busca os dados do usuário no banco e preenche os campos
        }

        // ===== CONFIGURAÇÕES DA JANELA =====
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        // fechar a janela a descarta
        setResizable(false);
        // não permite redimensionar
        pack();
        // ajusta tamanho ao conteúdo
        setLocationRelativeTo(janelaPai);
        // centraliza em relação à janela pai
    }

    /**
     * Busca os dados do usuário no banco e preenche os campos (modo edição)
     */
    private void carregarDados()
    {
        String sql = "SELECT nome, login, senha, email FROM tb_usuarios WHERE usuario_id = ?";
        // query que busca o usuário pelo ID

        try
        {
            Connection conn = Conexao.getConnection();
            // abre conexão com o banco
            PreparedStatement stmt = conn.prepareStatement(sql);
            // prepara a query
            stmt.setInt(1, usuarioId);
            // substitui o ? pelo ID do usuário
            ResultSet rs = stmt.executeQuery();
            // executa a query

            if (rs.next())
            {
                // se encontrou o usuário
                campoNome.setText(rs.getString("nome"));
                // preenche o campo nome
                campoLogin.setText(rs.getString("login"));
                // preenche o campo login
                campoSenha.setText(rs.getString("senha"));
                // preenche o campo senha
                String email = rs.getString("email");
                // lê o email
                campoEmail.setText(email != null ? email : "");
                // preenche o campo email (vazio se nulo)
            }

            rs.close();
            // fecha o ResultSet
            stmt.close();
            // fecha o PreparedStatement
            conn.close();
            // fecha a conexão
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(
                this,
                "Erro ao carregar dados do usuário:\n" + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE
            );
            // exibe mensagem de erro
        }
    }

    /**
     * Valida os campos e persiste o usuário no banco (INSERT ou UPDATE)
     */
    private void salvar()
    {
        String nome = campoNome.getText().trim();
        // lê e remove espaços do campo nome
        String login = campoLogin.getText().trim();
        // lê e remove espaços do campo login
        String senha = new String(campoSenha.getPassword()).trim();
        // lê e remove espaços do campo senha
        String email = campoEmail.getText().trim();
        // lê e remove espaços do campo email

        // ===== VALIDAÇÕES =====
        if (nome.isEmpty())
        {
            JOptionPane.showMessageDialog(this, "O campo Nome é obrigatório.", "Atenção", JOptionPane.WARNING_MESSAGE);
            // avisa que o nome é obrigatório
            campoNome.requestFocus();
            // foca no campo nome
            return;
            // cancela o salvamento
        }

        if (login.isEmpty())
        {
            JOptionPane.showMessageDialog(this, "O campo Login é obrigatório.", "Atenção", JOptionPane.WARNING_MESSAGE);
            // avisa que o login é obrigatório
            campoLogin.requestFocus();
            // foca no campo login
            return;
            // cancela o salvamento
        }

        if (senha.isEmpty())
        {
            JOptionPane.showMessageDialog(this, "O campo Senha é obrigatório.", "Atenção", JOptionPane.WARNING_MESSAGE);
            // avisa que a senha é obrigatória
            campoSenha.requestFocus();
            // foca no campo senha
            return;
            // cancela o salvamento
        }

        // ===== PERSISTÊNCIA NO BANCO =====
        try
        {
            Connection conn = Conexao.getConnection();
            // abre conexão com o banco
            PreparedStatement stmt;
            // declara o PreparedStatement

            if (usuarioId == 0)
            {
                // modo INCLUSÃO: INSERT
                String sql = "INSERT INTO tb_usuarios (nome, login, senha, email) VALUES (?, ?, ?, ?)";
                // query de inclusão
                stmt = conn.prepareStatement(sql);
                // prepara a query
                stmt.setString(1, nome);
                // substitui o 1º ? pelo nome
                stmt.setString(2, login);
                // substitui o 2º ? pelo login
                stmt.setString(3, senha);
                // substitui o 3º ? pela senha
                stmt.setString(4, email.isEmpty() ? null : email);
                // substitui o 4º ? pelo email (null se vazio)
            }
            else
            {
                // modo EDIÇÃO: UPDATE
                String sql = "UPDATE tb_usuarios SET nome = ?, login = ?, senha = ?, email = ? WHERE usuario_id = ?";
                // query de atualização
                stmt = conn.prepareStatement(sql);
                // prepara a query
                stmt.setString(1, nome);
                // substitui o 1º ? pelo nome
                stmt.setString(2, login);
                // substitui o 2º ? pelo login
                stmt.setString(3, senha);
                // substitui o 3º ? pela senha
                stmt.setString(4, email.isEmpty() ? null : email);
                // substitui o 4º ? pelo email (null se vazio)
                stmt.setInt(5, usuarioId);
                // substitui o 5º ? pelo ID do usuário
            }

            stmt.executeUpdate();
            // executa o INSERT ou UPDATE
            stmt.close();
            // fecha o PreparedStatement
            conn.close();
            // fecha a conexão

            salvo = true;
            // marca como salvo com sucesso

            // registra auditoria
            if (usuarioId == 0)
                AuditoriaUtil.registrar(loginOperador, "INCLUIR_USUARIO", "Usuário incluído: login=" + login + ", nome=" + nome);
            else
                AuditoriaUtil.registrar(loginOperador, "EDITAR_USUARIO", "Usuário editado: id=" + usuarioId + ", login=" + login + ", nome=" + nome);

            JOptionPane.showMessageDialog(
                this,
                usuarioId == 0 ? "Usuário incluído com sucesso!" : "Usuário atualizado com sucesso!",
                "Sucesso",
                JOptionPane.INFORMATION_MESSAGE
            );
            // exibe mensagem de confirmação

            dispose();
            // fecha o diálogo após salvar
        }
        catch (Exception e)
        {
            String mensagem = e.getMessage();
            // mensagem de erro do banco
            if (mensagem != null && mensagem.contains("Duplicate entry"))
            {
                JOptionPane.showMessageDialog(
                    this,
                    "Este login já está em uso. Escolha um login diferente.",
                    "Login Duplicado",
                    JOptionPane.ERROR_MESSAGE
                );
                // avisa especificamente sobre login duplicado
                campoLogin.requestFocus();
                // foca no campo login
            }
            else
            {
                JOptionPane.showMessageDialog(
                    this,
                    "Erro ao salvar usuário:\n" + mensagem,
                    "Erro",
                    JOptionPane.ERROR_MESSAGE
                );
                // exibe mensagem de erro genérica
            }
        }
    }

    /**
     * Define o login do operador para auditoria
     */
    public void setLoginOperador(String loginOperador)
    {
        this.loginOperador = loginOperador;
        // guarda o login do operador para registrar na auditoria
    }

    /**
     * Retorna se o usuário foi salvo com sucesso
     */
    public boolean isSalvo()
    {
        return salvo;
        // retorna o status de salvamento
    }
}
// fecha a classe
