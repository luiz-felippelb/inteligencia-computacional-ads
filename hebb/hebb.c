#include <stdio.h>

#include "hebb.h"

#define C 100

float hebb(float entrada[2][C], float nova_entrada[C]) {
    float y[] = {1,-1};//O 1 ser o A e -1 ser o B.
    float deltaW[C], deltaB, w[C], b, deltaTeste;
    int cont1, cont2;

    for(cont2=0; cont2<C; cont2++) {
        w[cont2] = 0;
    }

    b=0;

    //Aplica o da regra.
    for(cont1 = 0; cont1<2; cont1++) {
        for(cont2 = 0; cont2<C; cont2++) { //Os valores de entrada (A e B) serão multiplicados pela saída correspondente.
            deltaW[cont2] = entrada[cont1][cont2] * y[cont1];
        }
        deltaB = y[cont1];
        for(cont2 = 0; cont2<C;cont2++) { //Cria o dos pesos.
            w[cont2] = w[cont2] + (entrada[cont1][cont2] * y[cont1]);
        }
        b = b + deltaB;
    }

    // Teste da nova sequência usando os pesos já treinados
    deltaTeste = 0;
    for (cont2 = 0; cont2 < C; cont2++) {
        deltaTeste += w[cont2] * nova_entrada[cont2];
    }
    deltaTeste += b;

    return deltaTeste;
}