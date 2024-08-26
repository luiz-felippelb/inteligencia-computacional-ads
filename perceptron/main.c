#include <ncurses.h>
#include "perceptron.h"

#define C 100

typedef struct PARAMETROS_NOVA_JANELA_struct
{
    int startx, starty;
    int height, width;
} NOVA_JANELA;

typedef struct BORDAS_MATRIZ_struct
{
    char canto_superior_direito,
        canto_superior_esquerdo,
        canto_inferior_esquerdo,
        canto_inferior_direito,
        t_para_direita,
        t_para_esquerda,
        t_para_cima,
        t_para_baixo,
        linha_horizontal,
        linha_vertical,
        cruz;
} BORDAS_MATRIZ;

typedef struct MATRIZ_struct
{
    int startx, starty;
    int height, width;
    BORDAS_MATRIZ borda;
} MATRIZ;

void parametros_iniciais_matriz(MATRIZ *p_matriz);
void construir_matriz(MATRIZ *p_matriz);

int main()
{
    float entradaTreinamento[2][C];
    float entradaTeste[C];
    float saida;

    MATRIZ matriz;
    int ch;
    int x, y;
    int yy, xx;

    int m[10][10];
    float entrada[2][C];
    float nova_entrada[C];
    int qtd_entrada = 0;
    int pos_i;
    int pos_j;
    int k;

    initscr();
    cbreak();             /* Buffer de linha desativado */
    keypad(stdscr, TRUE); /* Habilita a captura do teclado */
    noecho();

    /* Inicializa os parâmetros da matriz */
    parametros_iniciais_matriz(&matriz);

    mvprintw(0, 0, "Utilize as setas para percorrer a matriz.\n");
    mvprintw(1, 0, "Pressione 'Enter' para marcar um campo.\n");
    mvprintw(2, 0, "Desenhe um padrão a ser processado.\n");
    mvprintw(3, 0, "Pressione 'C' para confirmar a entrada.");

    mvprintw(LINES - 1, 0, "Developed by Luiz Felippe and Nathan Rodrigues");

    mvprintw(9, 0, "Informe a primeira entrada.");

    construir_matriz(&matriz);

    x = matriz.startx + 1;
    y = matriz.starty + 1;

    move(y, x);
    refresh();

    for (int i = 0; i < 10; i++)
    {
        for (int j = 0; j < 10; j++)
        {
            m[i][j] = -1;
        }
    }

    while ((ch = getch()) != 'q')
    {
        switch (ch)
        {
        case KEY_LEFT:
            if (x != 31)
            {
                x = x - 2;
            }
            break;
        case KEY_RIGHT:
            if (x != 49)
            {
                x = x + 2;
            }
            break;
        case KEY_UP:
            if (y != 6)
            {
                y = y - 2;
            }
            break;
        case KEY_DOWN:
            if (y != 24)
            {
                y = y + 2;
            }
            break;
        case '\n':
            getyx(stdscr, yy, xx);                // Captura a posição atual do cursor
            chtype current_char = mvinch(yy, xx); // Captura o caractere na posição atual

            pos_i = (yy - ((LINES - 20) / 2)) / 2;
            pos_j = (xx - ((COLS - 20) / 2)) / 2;

            if (current_char == '*')
            {
                mvprintw(yy, xx, " "); // Apaga o asterisco se ele já estiver lá
                m[pos_i][pos_j] = -1;
            }
            else
            {
                mvprintw(yy, xx, "*"); // Coloca o asterisco se a posição estiver vazia
                m[pos_i][pos_j] = 1;
            }

            mvprintw(((LINES - 20) / 2) + 1, 0, "(y,x): (%d,%d)", yy, xx);
            if (m[pos_i][pos_j] < 0)
            {
                mvprintw(((LINES - 20) / 2) + 2, 0, "(%d,%d): %d", pos_i, pos_j, m[pos_i][pos_j]);
            }
            else
            {
                mvprintw(((LINES - 20) / 2) + 2, 0, "(%d,%d): +%d", pos_i, pos_j, m[pos_i][pos_j]);
            }

            break;
        case 'c':
            k = 0;
            if (qtd_entrada == 0)
            {
                for (int i = 0; i < 10; i++)
                {
                    for (int j = 0; j < 10; j++)
                    {
                        entrada[qtd_entrada][k++] = m[i][j];
                    }
                }
                mvprintw(9, 0, "Primeira entrada registrada.");
                qtd_entrada++;
                mvprintw(10, 0, "Informe a segunda entrada.");
            }
            else if (qtd_entrada == 1)
            {
                for (int i = 0; i < 10; i++)
                {
                    for (int j = 0; j < 10; j++)
                    {
                        entrada[qtd_entrada][k++] = m[i][j];
                    }
                }
                mvprintw(10, 0, "Segunda entrada registrada.");
                qtd_entrada++;
                mvprintw(11, 0, "Informe a terceira entrada.");
            }
            else
            {
                mvprintw(11, 0, "Terceira entrada registrada.");
                mvprintw(13, 0, "Pressione 'q' para sair.");
                for (int i = 0; i < 10; i++)
                {
                    for (int j = 0; j < 10; j++)
                    {
                        nova_entrada[k++] = m[i][j];
                    }
                }
                // move(LINES - 4, 0);

                saida = perceptron(entradaTreinamento, entradaTeste);

                if (saida > 0)
                {
                    mvprintw(LINES - 4, 0, "A nova sequencia é mais proxima da primeira entrada..");
                }
                else
                {
                    mvprintw(LINES - 4, 0, "A nova sequencia é mais proxima da segunda entrada..");
                }

                // refresh();
            }

            // limpa os valores da matriz
            for (int i = 0; i < 10; i++)
            {
                for (int j = 0; j < 10; j++)
                {
                    m[i][j] = -1;
                }
            }

            construir_matriz(&matriz);
            x = matriz.startx + 1;
            y = matriz.starty + 1;
            break;
        }

        move(y, x);
        refresh();
    }

    endwin();
    return 0;
}

void parametros_iniciais_matriz(MATRIZ *p_matriz)
{
    int h, w;

    p_matriz->height = 20;
    p_matriz->width = 20;

    h = p_matriz->height;
    w = p_matriz->width;

    p_matriz->starty = (LINES - h) / 2;
    p_matriz->startx = (COLS - w) / 2;

    p_matriz->borda.canto_superior_direito = ACS_URCORNER;
    p_matriz->borda.canto_superior_esquerdo = ACS_ULCORNER;
    p_matriz->borda.canto_inferior_esquerdo = ACS_LLCORNER;
    p_matriz->borda.canto_inferior_direito = ACS_LRCORNER;
    p_matriz->borda.t_para_baixo = ACS_TTEE;
    p_matriz->borda.t_para_cima = ACS_BTEE;
    p_matriz->borda.t_para_direita = ACS_LTEE;
    p_matriz->borda.t_para_esquerda = ACS_RTEE;
    p_matriz->borda.linha_horizontal = ACS_HLINE;
    p_matriz->borda.linha_vertical = ACS_VLINE;
    p_matriz->borda.cruz = ACS_PLUS;
}

void construir_matriz(MATRIZ *p_matriz)
{
    int i, j;
    int x, y;

    x = p_matriz->startx;
    y = p_matriz->starty;

    // Desenha a primeira linha da matriz
    mvaddch(y, x++, p_matriz->borda.canto_superior_esquerdo);
    for (i = 0; i < 9; i++)
    {
        mvaddch(y, x++, p_matriz->borda.linha_horizontal);
        mvaddch(y, x++, p_matriz->borda.t_para_baixo);
    }
    mvaddch(y, x++, p_matriz->borda.linha_horizontal);
    mvaddch(y, x, p_matriz->borda.canto_superior_direito);

    for (j = 0; j < 19; j++)
    {
        x = p_matriz->startx;
        if (j % 2 == 0)
        {
            // Desenha as linhas verticais
            mvaddch(++y, x, p_matriz->borda.linha_vertical);
            for (i = 0; i < 10; i++)
            {
                mvaddch(y, x + 1, ' ');
                mvaddch(y, x = x + 2, p_matriz->borda.linha_vertical);
            }
        }
        else
        {
            // Desenha as linhas horizontais
            mvaddch(++y, x, p_matriz->borda.t_para_direita);
            for (i = 0; i < 19; i++)
            {
                if (i % 2 == 0)
                {
                    mvaddch(y, ++x, p_matriz->borda.linha_horizontal);
                }
                else
                {
                    mvaddch(y, ++x, p_matriz->borda.cruz);
                }
            }
            mvaddch(y, ++x, p_matriz->borda.t_para_esquerda);
        }
    }

    x = p_matriz->startx;

    // Desenha a ultima linha da matriz
    mvaddch(++y, x++, p_matriz->borda.canto_inferior_esquerdo);
    for (i = 0; i < 9; i++)
    {
        mvaddch(y, x++, p_matriz->borda.linha_horizontal);
        mvaddch(y, x++, p_matriz->borda.t_para_cima);
    }
    mvaddch(y, x++, p_matriz->borda.linha_horizontal);
    mvaddch(y, x, p_matriz->borda.canto_inferior_direito);

    refresh();
}