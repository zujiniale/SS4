package javasistema;

import java.sql.Connection;
import java.sql.DriverManager;

public class Conexao {
    // Configuração do banco de dados
    private static final String URL = "jdbc:mysql://bd_savir.mysql.dbaas.com.br:3306/bd_savir?useSSL=false&serverTimezone=America/Fortaleza";
    private static final String USUARIO = "bd_savir";
    private static final String SENHA = "B@nc0D@d0s";

    static {
        // Carrega o driver MySQL na inicialização
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver MySQL não encontrado!");
            e.printStackTrace();
        }
    }

    /**
     * Retorna uma conexão ativa com o banco de dados
     */
    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }

    /**
     * Testa se a conexão com o banco está disponível
     */
    public static boolean testarConexao() {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("✓ Teste de conexão realizado com sucesso!");
                return true;
            }
        } catch (Exception e) {
            System.err.println("✗ Erro ao conectar ao banco: " + e.getMessage());
        }
        return false;
    }
}
