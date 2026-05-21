#!/bin/bash

# Script de execução para SuperSistema (corrigido)

echo "================================"
echo "Iniciando SuperSistema..."
echo "================================"
echo ""

# Verifica se está no diretório correto

if [ ! -d "src/javasistema" ]; then
echo "✗ Erro: Execute este script a partir do diretório raiz do projeto"
echo "  Diretório esperado: javasistema_final/"
exit 1
fi

# Verifica se os arquivos .class existem

if [ ! -f "src/javasistema/FormPrincipal.class" ]; then
echo "⚠️  Arquivos compilados não encontrados!"
echo "Compilando primeiro..."
bash compile.sh
echo ""
fi

# Caminho fixo do driver (já sabemos onde está)

MYSQL_JAR="/usr/share/java/mysql-connector-j-9.7.0.jar"

# Verifica se o driver existe

if [ ! -f "$MYSQL_JAR" ]; then
echo "✗ Erro: Driver MySQL não encontrado em $MYSQL_JAR"
echo "Verifique se o pacote mysql-connector-j está instalado"
exit 1
fi

echo "✓ Driver encontrado:"
echo "  $MYSQL_JAR"
echo ""

# Navega para o diretório src

cd src

# Executa a aplicação com classpath correto

java -cp ".:$MYSQL_JAR" javasistema.FormPrincipal

cd ..

