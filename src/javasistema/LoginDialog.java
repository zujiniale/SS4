package javasistema;
// agrupa as classes relacionadas

import javax.swing.*;
// importa componentes graficos
import java.awt.*;
// importa ferramentas de desenho
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginDialog extends JDialog {
    // cria a janela de dialogo de login
    
    private boolean autenticado = false;
    // armazena se o user foi autenticado
    private String usuario = null;
    // armazena o nome completo do usuario autenticado
    private String loginDigitado = null;
    // armazena o login (username) digitado — usado para auditoria
    // armazena o nome do usuario
    
    // componentes da interface
    private final JComboBox<String> comboEmpresa;
    // SELECT para escolher empresa/filial
    private final JTextField campoUsuario;
    // campo de entrada de usuario
    private final JPasswordField campoSenha;
    // campo de entrada de senha
    private final JButton botaoOk;
    // botão de confirmar
    private final JButton botaoCancelar;
    // botão de cancelar
    private String empresa = null;
    // armazena a empresa selecionada
    
    public LoginDialog(JFrame janelaPai) {
    // construtor que cria a janela de login
        super(janelaPai, "SuperSistema - Login", true);
        // define o titulo e torna modal
        
        // painel principal
        JPanel painel = new JPanel();
        // cria o painel
        painel.setLayout(new GridBagLayout());
        // usa layout GridBag para melhor posicionamento
        painel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        // adiciona margem
        
        GridBagConstraints gbc = new GridBagConstraints();
        // objeto para posicionar componentes
        gbc.insets = new Insets(5, 5, 5, 5);
        // espaçamento entre componentes
        gbc.fill = GridBagConstraints.HORIZONTAL;
        // preenche horizontalmente
        
        // === SELECT DE EMPRESA ===
        gbc.gridx = 0;
        // coluna 0
        gbc.gridy = 0;
        // linha 0
        gbc.weightx = 0;
        // sem expansao horizontal
        painel.add(new JLabel("Empresa/Filial:"), gbc);
        // adiciona label de empresa
        
        // campo SELECT de empresa
        comboEmpresa = new JComboBox<>(
        // cria o SELECT com opcoes
            new String[]{
            // array de opcoes
                "Selecione uma empresa...",
                // opcao padrao
                "Matriz - São Paulo",
                // opcao 1
                "Filial - Rio de Janeiro",
                // opcao 2
                "Filial - Belo Horizonte",
                // opcao 3
                "Filial - Curitiba",
                // opcao 4
                "Filial - Salvador"
                // opcao 5
            }
        );
        // opcoes disponiveis
        comboEmpresa.setSelectedIndex(0);
        // seleciona a primeira opcao por padrao
        gbc.gridx = 1;
        // coluna 1
        gbc.weightx = 1;
        // expande horizontalmente
        painel.add(comboEmpresa, gbc);
        // adiciona o SELECT
        
        // label usuario
        gbc.gridx = 0;
        // coluna 0
        gbc.gridy = 1;
        // linha 1
        gbc.weightx = 0;
        // sem expansao horizontal
        painel.add(new JLabel("Usuário:"), gbc);
        // adiciona label
        
        // campo usuario
        campoUsuario = new JTextField(15);
        // cria campo com 15 colunas
        gbc.gridx = 1;
        // coluna 1
        gbc.weightx = 1;
        // expande horizontalmente
        painel.add(campoUsuario, gbc);
        // adiciona campo
        
        // label senha
        gbc.gridx = 0;
        // coluna 0
        gbc.gridy = 2;
        // linha 2
        gbc.weightx = 0;
        // sem expansao horizontal
        painel.add(new JLabel("Senha:"), gbc);
        // adiciona label
        
        // campo senha
        campoSenha = new JPasswordField(15);
        // cria campo de senha com 15 colunas
        gbc.gridx = 1;
        // coluna 1
        gbc.weightx = 1;
        // expande horizontalmente
        painel.add(campoSenha, gbc);
        // adiciona campo
        
        // painel de botões
        JPanel painelBotoes = new JPanel();
        // cria painel para botões
        painelBotoes.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        // layout em linha, alinhado -> direita
        
        botaoOk = new JButton("Entrar");
        // cria botao OK
        botaoCancelar = new JButton("Cancelar");
        // cria botão cancelar
        
        painelBotoes.add(botaoOk);
        // adiciona botao OK
        painelBotoes.add(botaoCancelar);
        // adiciona botao cancelar
        
        // adiciona painel de botões ao painel principal
        gbc.gridx = 0;
        // coluna 0
        gbc.gridy = 3;
        // linha 3
        gbc.gridwidth = 2;
        // ocupa 2 colunas
        gbc.weightx = 1;
        // expande horizontalmente
        gbc.insets = new Insets(15, 5, 5, 5);
        // margem maior no topo
        painel.add(painelBotoes, gbc);
        // adiciona painel de botoes
        
        // adiciona painel à janela
        add(painel);
        // adiciona painel ao diálogo
        
        // = =  acoes = =
        // configura o que acontece quando clica
        
        botaoOk.addActionListener(e -> {
        // executa quando clica em "entrar"
            String empresaSel = (String) comboEmpresa.getSelectedItem();
            // pega a empresa selecionada
            String usr = campoUsuario.getText().trim();
            // pega o texto do campo usuário
            String sen = new String(campoSenha.getPassword()).trim();
            // pega a senha
            
            if (empresaSel.equals("Selecione uma empresa...")) {
            // se nao selecionou empresa
                JOptionPane.showMessageDialog(this,
                // mostra caixa de mensagem
                    "Por favor, selecione uma empresa/filial.",
                    // mensagem
                    "Atenção",
                    // titulo
                    JOptionPane.WARNING_MESSAGE);
                    // tipo de mensagem
                comboEmpresa.requestFocus();
                // foca no combo
                return;
                // cancela
            }
            // fecha o if
            
            if (usr.isEmpty()) {
            // se o usuario está vazio
                JOptionPane.showMessageDialog(this,
                // mostra caixa de mensagem
                    "Por favor, informe um usuário.",
                    // mensagem
                    "Atenção",
                    // titulo
                    JOptionPane.WARNING_MESSAGE);
                    // tipo de mensagem
                campoUsuario.requestFocus();
                return;
                // cancela
            }
            // fecha o if
            
            if (sen.isEmpty()) {
            // se a senha esta vazia
                JOptionPane.showMessageDialog(this,
                // mostra caixa de mensagem
                    "Por favor, informe uma senha.",
                    // mensagem
                    "Atenção",
                    // título
                    JOptionPane.WARNING_MESSAGE);
                    // tipo de mensagem
                campoSenha.requestFocus();
                return;
                // cancela
            }
            // fecha o if
            
            // ========== AUTENTICAÇÃO NO BANCO DE DADOS ==========
            validarLogin(usr, sen, empresaSel);
            // Chama o método de validação no banco
        });
        // fecha o listener
        
        botaoCancelar.addActionListener(e -> {
        // executa quando clica em "cancelar"
            autenticado = false;
            // marca como não autenticado
            dispose();
            // fecha a janela
        });
        // fecha o listener
        
        // = = janela = =
        // configura a janela
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        // fechar a janela a descarrega
        setResizable(false);
        // nao permite redimensionar
        pack();
        // ajusta o tamanho automaticamente
        setLocationRelativeTo(janelaPai);
        // centraliza em relaçao a janela pai
    }
    // fecha o construtor
    
    /**
     * Valida as credenciais do usuário no banco de dados
     */
    private void validarLogin(String usuarioLogin, String senhaLogin, String empresaSelecionada)
    {
        // Primeiro, testa a conexão com o banco
        if (!Conexao.testarConexao())
        {
            JOptionPane.showMessageDialog(
                this,
                "Não foi possível conectar ao banco de dados.\nVerifique sua conexão com a internet e tente novamente.",
                "Erro de Conexão",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // SQL para buscar o usuário no banco
        String sql = "SELECT nome FROM tb_usuarios WHERE login = ? AND senha = ? LIMIT 1";

        try (
            Connection conn = Conexao.getConnection();
            PreparedStatement pst = conn.prepareStatement(sql)
        )
        {
            // Define os parâmetros da consulta
            pst.setString(1, usuarioLogin);    // Substitui o primeiro ? pelo usuário
            pst.setString(2, senhaLogin);      // Substitui o segundo ? pela senha

            // Executa a consulta
            ResultSet rs = pst.executeQuery();

            // Verifica se encontrou um usuário com essas credenciais
            if (rs.next())
            {
                // Usuário encontrado!
                autenticado = true;
                usuario = rs.getString("nome");
                loginDigitado = usuarioLogin;
                // guarda o login digitado para uso na auditoria
                empresa = empresaSelecionada;

                JOptionPane.showMessageDialog(
                    this,
                    "Login realizado com sucesso!\nBem-vindo, " + usuario + "!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE
                );

                dispose(); // Fecha a janela de login
            }
            else
            {
                // Usuário ou senha incorretos
                JOptionPane.showMessageDialog(
                    this,
                    "Usuário ou senha inválidos!\nVerifique os dados e tente novamente.",
                    "Erro de Autenticação",
                    JOptionPane.ERROR_MESSAGE
                );
                campoSenha.setText(""); // Limpa o campo de senha
                campoUsuario.requestFocus(); // Foca novamente no campo de usuário
            }
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(
                this,
                "Erro ao validar login:\n" + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE
            );
            System.err.println("Erro ao validar login:");
            e.printStackTrace(); // Exibe o erro no console para debug
        }
    }

    /**
     * retorna se o usuario foi autenticado
     */
    public boolean isAutenticado() {
    // método que verifica se foi autenticado
        return autenticado;
        // retorna o status de autenticaçao
    }
    // fecha o metodo
    
    /**
     * retorna o nome do usuario autenticado
     */
    public String getUsuario() {
    // metodo que retorna o usuário
        return usuario;
        // retorna o usuario
    }
    // fecha o metodo
    
    /**
     * retorna a empresa selecionada
     */
    public String getEmpresa() {
    // metodo que retorna a empresa
        return empresa;
        // retorna a empresa
    }
    // fecha o metodo

    /**
     * retorna o login digitado pelo usuario que se autenticou (usado para auditoria)
     */
    public String getLoginAutenticado() {
    // metodo que retorna o login digitado
        return loginDigitado;
        // retorna o login
    }
    // fecha o metodo
}
// fecha a classe
