package javasistema;
// agrupa as classes relacionadas

import javax.swing.*;
// importa componentes graficos

/**
 * FormUsuariosIncluir
 *
 * Responsável por abrir o formulário de inclusão de usuário.
 * Delega a lógica de interface ao FormUsuarioEditar (modo inclusão: id = 0)
 * e registra auditoria via AuditoriaUtil.
 *
 * Uso:
 *   FormUsuariosIncluir.abrir(formListar, loginOperador);
 */
public class FormUsuariosIncluir
{
    // classe utilitária — não deve ser instanciada
    private FormUsuariosIncluir() {}

    /**
     * Abre o formulário de inclusão de usuário de forma modal.
     * Ao confirmar, atualiza automaticamente a listagem e registra auditoria.
     *
     * @param formListar    tela de listagem (janela pai e referência para recarregar)
     * @param loginOperador login do operador logado (para auditoria)
     */
    public static void abrir(FormUsuariosListar formListar, String loginOperador)
    {
        FormUsuarioEditar form = new FormUsuarioEditar(formListar, 0);
        // cria o formulário em modo inclusão (id = 0)
        form.setLoginOperador(loginOperador);
        // passa o login do operador para auditoria
        form.setVisible(true);
        // exibe o formulário de forma modal (bloqueia até fechar)

        if (form.isSalvo())
        {
            // se o usuário foi incluído com sucesso
            formListar.carregarUsuarios();
            // recarrega a listagem para exibir o novo registro
        }
        // fecha o if
    }
    // fecha o método
}
// fecha a classe
