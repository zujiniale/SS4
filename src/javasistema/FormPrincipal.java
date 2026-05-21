package javasistema;
// agrupa as classes relacionadas

import javax.swing.*;
// importa componentes graficos
import java.awt.*;
// importa ferramentas de desenho

public class FormPrincipal extends JFrame {
    // cria a janela principal

    private String usuarioLogado = null;
    // guarda o nome do usuário logado
    private String loginUsuarioLogado = null;
    // guarda o login do user logado (usado para auditoria)
    private String empresaLogada = null;
    // guarda a empresa logada

    // menus principais
    private final JMenuBar menuBar;
    // barra que contem todos os menus
    private final JMenu menuSistema;
    // menu "sistema"

    // menu usuários: gestão de usuários — aparece apenas logado
    private JMenu menuUsuarios;
    // menu "usuários"
    private JMenuItem itemGestaoUsuarios;
    // item "gestão de usuários" do menu usuários

    // menu segurança: funcionalidades de segurança — aparece apenas logado
    private JMenu menuSeguranca;
    // menu "segurança"
    private JMenuItem itemLogAuditoria;
    // item "log de auditoria" do menu segurança

    // itens de menu
    private final JMenuItem itemFechar;
    // botão "fechar"
    private final JMenuItem itemLogin;
    // botão "logar/logout"

    public FormPrincipal() {
    // construtor que monta toda a interface
        super("SuperSistema");
        // define o título da janela

        // fundo com logo
        JPanel painelFundo = new JPanel() {
        // cria o painel de fundo
            @Override
            // sobrescreve um metodo da classe pai
            protected void paintComponent(Graphics g) {
            // desenha coisas customizadas no painel
                super.paintComponent(g);
                // desenha o fundo padrão
                try {
                    Image img = new ImageIcon(getClass().getResource("/javasistema/logo.jpg")).getImage();
                    // carrega a imagem do logo
                    g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
                    // desenha a imagem no tamanho da janela
                } catch (Exception e) {
                    System.err.println("Erro ao carregar logo: " + e.getMessage());
                }
            }
        };
        // fecha a classe do painel

        setLayout(new BorderLayout());
        // define o tipo de layout da janela
        add(painelFundo, BorderLayout.CENTER);
        // coloca o painel no centro da janela

        // = = menu = =
        // cria os menus
        menuBar = new JMenuBar();
        // cria a barra de menus

        menuSistema = new JMenu("Sistema");
        // cria o menu "sistema"
        itemLogin = new JMenuItem("Logar");
        // cria o item "logar"
        itemFechar = new JMenuItem("Fechar");
        // cria o item "fechar"

        menuSistema.add(itemLogin);
        // adiciona "logar" ao menu sistema
        menuSistema.addSeparator();
        // adiciona uma linha separadora
        menuSistema.add(itemFechar);
        // adiciona "fechar" ao menu sistema

        menuBar.add(menuSistema);
        // adiciona o menu na barra de menus
        setJMenuBar(menuBar);
        // coloca a barra na janela

        // = = acoes = =
        // configura o que acontece quando clica

        // login / logout
        itemLogin.addActionListener(e -> {
        // executa quando clica em "logar"
            if (usuarioLogado == null) {
            // se não há USER logado
                realizarLogin();
                // abre a tela de login
            } else {
            // se ha user logado
                int op = JOptionPane.showConfirmDialog(this,
                // mostra caixa de confirmação
                    "Deseja sair da sessão de \"" + usuarioLogado + "\"?",
                    // pergunta se quer sair
                    "Logout", JOptionPane.YES_NO_OPTION);
                    // título e opções SIM/NÃO

                if (op == JOptionPane.YES_OPTION) {
                // se clicou em "sim"
                    AuditoriaUtil.registrar(loginUsuarioLogado, "LOGOUT",
                        "Sessão encerrada. Usuário: " + usuarioLogado + ". Empresa: " + empresaLogada);
                    // registra o logout no log de auditoria
                    usuarioLogado = null;
                    // limpa o nome do usuário
                    loginUsuarioLogado = null;
                    // limpa o login do usuário
                    empresaLogada = null;
                    // limpa a empresa
                    aplicarEstadoAutenticacao();
                    // atualiza a interface
                    JOptionPane.showMessageDialog(this, "Sessão encerrada.");
                    // mostra mensagem
                }
            }
        });
        // fecha o listener

        // fechar
        itemFechar.addActionListener(e -> {
        // executa quando clica em "fechar"
            int resposta = JOptionPane.showConfirmDialog(this,
            // mostra caixa de confirmação
                "Deseja realmente sair?",
                // pergunta se quer sair
                "Confirmação",
                // titulo
                JOptionPane.YES_NO_OPTION);
                // opcoes SIM/NAO

            if (resposta == JOptionPane.YES_OPTION) {
            // se clicou em "sim"
                System.exit(0);
                // fecha o programa
            }
        });
        // fecha o listener

        // = = janela = =

        // configura a janela
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        // abre a janela maximizada
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // fechar a janela encerra o programa

        // inicial: sem login, sem menus adicionais
        aplicarEstadoAutenticacao();
        // aplica o estado inicial

        setVisible(true);
        // mostra a janela
    }
    // fecha o construtor

    /**
     * Mostra/esconde os menus Usuários e Segurança conforme o estado de autenticação
     */
    private void aplicarEstadoAutenticacao() {
    // método que atualiza a interface conforme o login
        boolean logado = (usuarioLogado != null);
        // verifica se há usuário logado
        itemLogin.setText(logado ? "Logout" : "Logar");
        // muda o texto do botão de acordo com o login

        if (logado) {
        // se há USER logado

            // ===== MENU USUARIOS =====
            if (menuUsuarios == null) {
            // se o menu usuários ainda não foi criado
                menuUsuarios = new JMenu("Usuários");
                // cria o menu usuários
                itemGestaoUsuarios = new JMenuItem("Gestão de Usuários");
                // cria o item "gestão de usuários"
                itemGestaoUsuarios.addActionListener(e ->
                // executa quando clica em "gestao de usuarios"
                    new FormUsuariosListar(loginUsuarioLogado)
                    // abre a tela de gestão de usuários passando o login para auditoria
                );
                // fecha o listener
                menuUsuarios.add(itemGestaoUsuarios);
                // adiciona o item ao menu usuários
            }
            // fecha o if de criação

            if (!isMenuPresente(menuUsuarios)) {
            // se o menu usuários não está na barra
                menuBar.add(menuUsuarios);
                // adiciona o menu na barra
            }
            // fecha o if

            // ===== MENU SEGURANCA =====
            if (menuSeguranca == null) {
            // se o menu segurança ainda não foi criado
                menuSeguranca = new JMenu("Segurança");
                // cria o menu segurança
                itemLogAuditoria = new JMenuItem("Log de Auditoria");
                // cria o item "log de auditoria"
                itemLogAuditoria.addActionListener(e ->
                // executa quando clica em "log de auditoria"
                    new FormLogAuditoria()
                    // abre a tela de visualização do log de auditoria
                );
                // fecha o listener
                menuSeguranca.add(itemLogAuditoria);
                // adiciona o item ao menu segurança
            }
            // fecha o if de criação

            if (!isMenuPresente(menuSeguranca)) {
            // se o menu segurança não está na barra
                menuBar.add(menuSeguranca);
                // adiciona o menu na barra
            }
            // fecha o if

        } else {
        // se não há USER logado

            if (isMenuPresente(menuUsuarios)) {
            // se o menu usuários está na barra
                menuBar.remove(menuUsuarios);
                // remove o menu
            }
            // fecha o if

            if (isMenuPresente(menuSeguranca)) {
            // se o menu segurança está na barra
                menuBar.remove(menuSeguranca);
                // remove o menu
            }
            // fecha o if
        }
        // fecha o if/else

        menuBar.revalidate();
        // recalcula o layout da barra
        menuBar.repaint();
        // redesenha a barra
    }
    // fecha o metodo

    /**
     * Checa se um menu já está na barra de menus
     */
    private boolean isMenuPresente(JMenu menu) {
    // método que verifica se um menu existe na barra
        if (menu == null) {
        // se o menu é nulo
            return false;
            // retorna falso
        }
        // fecha o if

        for (int i = 0; i < menuBar.getMenuCount(); i++) {
        // percorre todos os menus
            if (menuBar.getMenu(i) == menu) {
            // se encontrou o menu
                return true;
                // retorna verdadeiro
            }
            // fecha o if
        }
        // fecha o loop

        return false;
        // não encontrou, retorna falso
    }
    // fecha o metodo

    /**
     * Abre o diálogo de login e, se autenticado, aplica estado LOGADO e registra auditoria
     */
    private void realizarLogin() {
    // método que abre a tela de login
        LoginDialog dlg = new LoginDialog(this);
        // cria a janela de login
        dlg.setVisible(true);
        // mostra a janela de login

        if (dlg.isAutenticado()) {
        // se o USER se autenticou
            this.usuarioLogado = dlg.getUsuario();
            // guarda o nome completo do usuário
            this.loginUsuarioLogado = dlg.getLoginAutenticado();
            // guarda o login usado (para auditoria e abertura de telas)
            this.empresaLogada = dlg.getEmpresa();
            // guarda a empresa
            aplicarEstadoAutenticacao();
            // atualiza a interface
            AuditoriaUtil.registrar(loginUsuarioLogado, "LOGIN",
                "Acesso ao sistema. Usuário: " + usuarioLogado + ". Empresa: " + empresaLogada);
            // registra o login bem-sucedido no log de auditoria
            JOptionPane.showMessageDialog(this, "Bem-vindo, " + usuarioLogado + "!\nEmpresa: " + empresaLogada);
            // mostra mensagem de boas-vindas com empresa
        }
        // fecha o if
    }
    // fecha o metodo

    public static void main(String[] args) {
    // método que inicia o programa
        SwingUtilities.invokeLater(FormPrincipal::new);
        // cria a janela na thread correta
    }
    // fecha o metodo
}
// fecha a classe
