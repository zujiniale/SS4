package javasistema;
// agrupa as classes relacionadas

import javax.swing.*;
// importa componentes graficos
import java.sql.Connection;
// importa a conexão com banco
import java.sql.PreparedStatement;
// importa PreparedStatement para queries seguras

/**
 * FormUsuariosExcluir
 *
 * Responsável por excluir o usuário selecionado na tabela.
 * Exibe confirmação antes de executar o DELETE,
 * registra auditoria via AuditoriaUtil e recarrega a listagem.
 *
 * Uso:
 *   FormUsuariosExcluir.excluir(formListar, loginOperador);
 */
public class FormUsuariosExcluir
{
    // classe utilitária — não deve ser instanciada
    private FormUsuariosExcluir() {}

    /**
     * Verifica a seleção, confirma com o operador e executa a exclusão.
     *
     * @param formListar    tela de listagem (fonte da seleção e janela pai dos diálogos)
     * @param loginOperador login do operador logado (para auditoria)
     */
    public static void excluir(FormUsuariosListar formListar, String loginOperador)
    {
        int linhaSelecionada = formListar.tabela.getSelectedRow();
        // obtém o índice da linha selecionada na tabela

        if (linhaSelecionada == -1)
        {
            // nenhuma linha selecionada
            JOptionPane.showMessageDialog(
                formListar,
                "Selecione um usuário na lista para excluir.",
                "Atenção",
                JOptionPane.WARNING_MESSAGE
            );
            // avisa o operador para selecionar um item
            return;
            // cancela a ação
        }

        int usuarioId = (int) formListar.modelo.getValueAt(linhaSelecionada, 0);
        // obtém o ID do usuário da coluna 0
        String nomeUsuario = (String) formListar.modelo.getValueAt(linhaSelecionada, 1);
        // obtém o nome do usuário da coluna 1

        int confirmacao = JOptionPane.showConfirmDialog(
            formListar,
            "Deseja realmente excluir o usuário \"" + nomeUsuario + "\"?\nEsta ação não pode ser desfeita.",
            "Confirmar Exclusão",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        // exibe caixa de confirmação antes de excluir

        if (confirmacao != JOptionPane.YES_OPTION)
        {
            return;
            // operador cancelou: não faz nada
        }

        String sql = "DELETE FROM tb_usuarios WHERE usuario_id = ?";
        // query de exclusão

        try
        {
            Connection conn = Conexao.getConnection();
            // abre conexão com o banco
            PreparedStatement stmt = conn.prepareStatement(sql);
            // prepara a query
            stmt.setInt(1, usuarioId);
            // substitui o ? pelo ID do usuário
            stmt.executeUpdate();
            // executa o DELETE
            stmt.close();
            // fecha o PreparedStatement
            conn.close();
            // fecha a conexão

            JOptionPane.showMessageDialog(
                formListar,
                "Usuário \"" + nomeUsuario + "\" excluído com sucesso.",
                "Exclusão Concluída",
                JOptionPane.INFORMATION_MESSAGE
            );
            // confirma a exclusão ao operador

            AuditoriaUtil.registrar(
                loginOperador,
                "EXCLUIR_USUARIO",
                "Usuário excluído: id=" + usuarioId + ", nome=" + nomeUsuario
            );
            // registra a exclusão no log de auditoria

            formListar.carregarUsuarios();
            // recarrega a listagem sem o registro excluído
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(
                formListar,
                "Erro ao excluir usuário:\n" + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE
            );
            // exibe mensagem de erro caso o DELETE falhe
        }
    }
    // fecha o método
}
// fecha a classe
