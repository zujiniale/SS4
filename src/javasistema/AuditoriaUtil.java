package javasistema;
// agrupa as classes relacionadas

import java.io.FileWriter;
// importa o escritor de arquivo
import java.io.PrintWriter;
// importa o impressor de texto
import java.io.IOException;
// importa excecao de I/O
import java.time.LocalDateTime;
// importa data e hora atual
import java.time.format.DateTimeFormatter;
// importa o formatador de data

public class AuditoriaUtil
{
    // utilitário estático para registro de log de auditoria

    private static final String ARQUIVO_LOG = "auditoria.log";
    // nome do arquivo de log gerado na raiz do projeto

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    // formato de data e hora para o log

    /**
     * Registra um evento de auditoria no arquivo de log
     * @param login    login do usuário que realizou a ação
     * @param evento   tipo de evento (ex: LOGIN, LOGOUT, INCLUIR_USUARIO)
     * @param detalhe  descrição adicional do evento
     */
    public static void registrar(String login, String evento, String detalhe)
    {
        // monta a linha de log com data, login, evento e detalhe
        String dataHora = LocalDateTime.now().format(FORMATO);
        // obtém a data e hora atual formatada

        String linha = dataHora + " | " + login + " | " + evento + " | " + detalhe;
        // monta a linha completa do log

        try
        {
            FileWriter fw = new FileWriter(ARQUIVO_LOG, true);
            // abre o arquivo em modo de adição (true = append)
            PrintWriter pw = new PrintWriter(fw);
            // cria o escritor de texto
            pw.println(linha);
            // escreve a linha no arquivo
            pw.close();
            // fecha o PrintWriter (também fecha o FileWriter)
        }
        catch (IOException e)
        {
            System.err.println("Erro ao gravar auditoria: " + e.getMessage());
            // imprime no console de erro caso falhe — não interrompe a aplicação
        }
    }
    // fecha o metodo

}
// fecha a classe
