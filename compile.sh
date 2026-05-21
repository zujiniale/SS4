#!/bin/bash

# Script de compilação para SuperSistema (corrigido)

echo "================================"
echo "Compilando SuperSistema..."
echo "================================"

# Verifica se está no diretório correto

if [ ! -d "src/javasistema" ]; then
echo "✗ Erro: Execute este script a partir do diretório raiz do projeto"
exit 1
fi

# Caminho fixo do driver (já descoberto)

MYSQL_JAR="/usr/share/java/mysql-connector-j-9.7.0.jar"

# Verifica se o driver existe

if [ ! -f "$MYSQL_JAR" ]; then
echo "✗ Erro: Driver MySQL não encontrado em $MYSQL_JAR"
echo "Instale corretamente o mysql-connector-j"
exit 1
fi

echo "✓ Usando driver:"
echo "  $MYSQL_JAR"
echo ""

# Navega para src

cd src

# Limpa classes antigas (opcional, mas saudável)

find . -name "*.class" -type f -delete

# Compila todos os arquivos Java

javac -cp ".:$MYSQL_JAR" javasistema/*.java

if [ $? -eq 0 ]; then
echo ""
echo "✓ Compilação concluída com sucesso!"
echo ""
echo "Para executar:"
echo "  ./run.sh"
echo ""
else
echo ""
echo "✗ Erro na compilação!"
echo ""
echo "Verifique:"
echo "- Código Java com erros"
echo "- Versão do Java (recomendado 8+)"
echo ""
fi

cd ..

