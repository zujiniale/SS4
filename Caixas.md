O que é um diagrama de classes?
É o mapa do projeto. Mostra quem existe, o que cada um faz e como se conectam. É o documento mais importante de um sistema Java porque te diz a estrutura inteira sem precisar ler o código.

As 11 caixas — quem é cada um
FormPrincipal é a janela que abre quando o programa inicia. É o centro de tudo — ela decide o que aparece no menu dependendo se o usuário está logado ou não. O main() fica aqui.

LoginDialog é a janela de login. Ela é modal (bloqueia tudo até o usuário logar ou cancelar). Guarda se o login deu certo (autenticado), o nome do usuário e a empresa escolhida.

Conexao é a ponte com o banco MySQL. Toda vez que qualquer classe precisa falar com o banco, passa por aqui. Os métodos são static (o $ no diagrama), então não precisam criar um objeto — chamam direto.

AuditoriaUtil é o "diário" do sistema. Toda ação importante (login, logout, criar usuário, editar, excluir) gera uma linha no arquivo auditoria.log. Também static.

FormUsuariosListar é a tela de gestão de usuários — uma tabela com todos os usuários do banco. Os botões INCLUIR, EDITAR, EXCLUIR ficam aqui.

FormUsuarioEditar é o formulário de cadastro. Serve para duas coisas: incluir (quando usuarioId = 0) e editar (quando usuarioId > 0). Um único form para os dois casos.

FormUsuariosIncluir e FormUsuariosExcluir são classes utilitárias que só têm um método cada. Servem para isolar a lógica de incluir e excluir, deixando o FormUsuariosListar mais limpo.

FormLogAuditoria é a tela que lê o arquivo auditoria.log e mostra numa tabela. O operador acessa pelo menu Segurança.

tb_usuarios é a tabela no banco MySQL. Não é código Java — é a estrutura de dados persistida.

auditoria_log é o arquivo de texto auditoria.log no disco. Também não é código Java — é o arquivo gerado pelo AuditoriaUtil.

