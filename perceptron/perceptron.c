#include <stdio.h>
// #include <conio.h>

int main()
{
    int opc = 0;
    int contCiclo = 0;
    int lin, col;
    int condErro = 1;
    float teste;
    float entrada[2][4];
    float limiar = 0;
    float w[4];      // Um peso para cada entrada.
    float target[2]; // Duas possíveis saídas.
    float b;
    float alfa = 0.01; // Taxa de aprendizagem 0<X<=1
    float yLiq = 0;
    float y;
    float yTeste;

    target[0] = -1; // Reconhecimento de A
    target[1] = 1;  // Reconhecimento de B

    w[0] = 0.5; // O mais comum na literatura é [-0.5, +0.5]
    w[1] = -0.1;
    w[2] = 0.4;
    w[3] = -0.27;

    b = 0.3256;

    entrada[0][0] = -1; // Equivalente ao A.
    entrada[0][1] = -1;
    entrada[0][2] = 1;
    entrada[0][3] = 1;

    entrada[1][0] = 1; // Equivalente ao B.
    entrada[1][1] = -1;
    entrada[1][2] = 1;
    entrada[1][3] = -1;

    while (opc != 3)
    {
        printf("\n\n *********** Programa Perceptron ***********");
        printf("\n\n Digite 1 para treinar a rede");
        printf("\n Digite 2 para operar");
        printf("\n Digite 3 para Sair\n ->");
        scanf("%i", &opc);

        if (opc == 1)
        {

            while (condErro == 1)
            {
                condErro = 0;
                lin = 0;

                while (lin < 2)
                {
                    yLiq = 0;
                    col = 0;

                    while (col < 4)
                    {
                        yLiq = yLiq + (entrada[lin][col] * w[col]);
                        col++;
                    }
                    yLiq = yLiq + b;
                    if (yLiq >= limiar) // função de ativação
                    {
                        y = 1;
                    }
                    else
                    {
                        y = -1;
                    }
                    printf("\n y: %.2f - target: %.2f", y, target[lin]);

                    // O segredo do perceptron está na linha abaixo
                    if (y != target[lin])
                    {
                        condErro = 1;
                        col = 0;

                        while (col < 4) // Algoritmo para correção dos pesos e bias
                        {
                            w[col] = w[col] + (alfa * target[lin] * entrada[lin][col]);
                            col++;
                        }
                        b = b + (alfa * target[lin]);
                    }
                    lin++;
                }
                printf("\n Ciclo: %i \n", contCiclo);
                contCiclo++;
            }

            printf("\n Rede treinada!");
            col = 0;
            while (col < 4)
            {
                printf("\n Peso[%i]: %.2f", col, w[col]);
                col++;
            }
            printf("\n Bias: %.2f", b);
        }
        if (opc == 2)
        {
            printf("\n\t ---> Testando a rede treinada");
            printf("\n\n Teste com as entradas do treinamento");
            // Mostrando as entradas
            lin = 0;
            while (lin < 2)
            {
                col = 0;
                while (col < 4)
                {
                    printf("\n Entrada[%i][%i]: %.2f", lin, col, entrada[lin][col]);
                    col++;
                }
                lin++;
                printf("\n ---------------\n");
            }
            for (lin = 0; lin < 2; lin++)
            {
                teste = 0;
                for (col = 0; col < 4; col++)
                { // Somatório dos pesos
                    teste = teste + (entrada[lin][col] * w[col]);
                }
                teste = teste + b;

                if (teste >= limiar)
                {
                    yTeste = 1;
                }
                else
                {
                    yTeste = -1;
                }
                printf("\n Saida da rede[%i]: %.2f", lin, yTeste);
            }
        }
    }
    return 0;
}
