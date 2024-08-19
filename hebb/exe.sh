#!/bin/bash

# Nome do executável
EXECUTAVEL="./main"

# Comando para abrir um novo terminal e definir o tamanho
gnome-terminal --geometry=80x30 -- bash -c "$EXECUTAVEL; exec bash"
