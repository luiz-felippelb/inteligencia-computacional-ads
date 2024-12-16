import java.util.Arrays;

public class Main {

    static final double TAXA_DE_APRENDIZADO = 0.01;
    static final int NEURONIOS = 2;
    static final double ERRO_TOLERADO = 0.5; // 0.1 | 0.05 | 0.02

    static double[][] pesos_sinapticos_ij;
    static double[][] bias_j;

    static double[][] pesos_sinapticos_jk;
    static double[][] bias_k;

    // Multilayer Perceptron
    public static void main(String[] args) {

        // Alvo
        int[][] targets;

        // entradas[amostras][parametros]
        double[][] entradas;

        // saidas[amostras][parametros]
        //double[][] saida;

        int amostras;
        int parametros;

//        double[][] pesos_sinapticos_ij;
//        double[][] bias_j;
//
//        double[][] pesos_sinapticos_jk;
//        double[][] bias_k;

        entradas = new double[][] {{1, 0.5, -1}, {0, 0.5, 1}, {1, -0.5, -1}};
        targets = new int[][] {{1, -1, -1}, {-1, 1, -1}, {-1, -1, 1}};

        amostras = entradas.length;
        parametros = entradas[0].length;

        // pesos_sinapticos[parametros][NEURONIOS] (entrada -> intermediaria)
        pesos_sinapticos_ij = new double[][] {{0.12, -0.3}, {-0.04, 0.15}, {0.31, -0.41}};

        // pesos_sinapticos[NEURONIOS][parametros] (intermediaria -> saida)
        pesos_sinapticos_jk = new double[][] {{-0.05, -0.34, 0.21}, {0.19, -0.09, 0.26}};

        // bias[1][NEURONIOS] (intermediaria)
        bias_j = new double[][] {{-0.09, 0.18}};

        // bias[1][parametros] (saida)
        bias_k = new double[][] {{0.18, -0.27, -0.12}};

        // Variáveis do algoritmo de treinamento

        // Forward
        double[][] zinj; // Valores que chegam a camada intermediária
        double[][] xi;   // Valores de entrada
        double[][] vij;  // Pesos sinápticos entre a camada de entrada e a camada intermediária
        double[][] v0j;  // Bias da camada intermediaria
        double[][] zj;   // Valores que saem da camada intermediaria (Após aplicado a função de ativação)
        double[][] yink; // Valores que chegam a camada de saída
        double[][] wjk;  // Pesos sinápticos entre a camada intermediária e a camada de saída
        double[][] w0k;  // Bias da camada de saída
        double[][] yk;   // Valores que saem da camada de saída (Após aplicado a função de ativação)
        int[][] tk;   // Valores Alvo
        double erro = 1;     // Erro para utilização do método Gradiente descendente

        // Backward
        double[][] deltinha_k;
        double[][] delta_wjk;
        double[][] delta_w0k;
        double[][] deltinha_j;
        double[][] delta_vij;
        double[][] delta_v0j;
        // Pesos atualizados
        double[][] vij_novo;
        double[][] v0j_novo;
        double[][] wjk_novo;
        double[][] w0k_novo;

        // Inicialização das variáveis
        zinj = new double[amostras][NEURONIOS];
        xi = entradas;
        vij = pesos_sinapticos_ij;
        v0j = bias_j;
        zj = new double[amostras][NEURONIOS];
        yink = new double[amostras][parametros];
        wjk = pesos_sinapticos_jk;
        w0k = bias_k;
        yk = new double[amostras][parametros];
        tk = targets;

        deltinha_k = new double[parametros][1];
        delta_wjk = new double[parametros][NEURONIOS];
        delta_w0k = new double[1][parametros];
        deltinha_j = new double[1][NEURONIOS];
        delta_vij = new double[NEURONIOS][parametros];
        delta_v0j = new double[1][NEURONIOS];

        vij_novo = new double[parametros][NEURONIOS];
        v0j_novo = new double[1][NEURONIOS];
        wjk_novo = new double[NEURONIOS][parametros];
        w0k_novo = new double[1][parametros];

        // Cálculo do erro (forward)
        int ciclos = 0;

        System.out.println("Início do treinamento da rede.");

        while (erro > ERRO_TOLERADO) {
            erro = 0.0;

            for (int i = 0; i < amostras; i++) {

//                delta_wjk = zerarValores(delta_wjk);
//                delta_w0k = zerarValores(delta_w0k);
//                delta_vij = zerarValores(delta_vij);
//                delta_v0j = zerarValores(delta_v0j);

                // ========================================== Forward

                // (Entrada(3) -> Intermediaria(2))
                Arrays.fill(zinj[i], 0.0);
                for (int j = 0; j < NEURONIOS; j++) {
                    for (int k = 0; k < parametros; k++) {
                        zinj[i][j] += xi[i][k] * vij[k][j];
                    }
                    zinj[i][j] += v0j[0][j];
                    zj[i][j] = tanh(zinj[i][j]);
                }

                // (Intermediaria(2) -> Saida(3))
                Arrays.fill(yink[i], 0.0);
                for (int k = 0; k < parametros; k++) {
                    for (int j = 0; j < NEURONIOS; j++) {
                        yink[i][k] += zj[i][j] * wjk[j][k];
                    }
                    yink[i][k] += w0k[0][k];
                    yk[i][k] = tanh(yink[i][k]);
                }
                // Softmax deve ser aplicado ao vetor inteiro de saída
                //yk[i] = softmax(yink[i]);

                // Cálculo do erro quadrático
                for (int k = 0; k < parametros; k++) {
                    erro += 0.5 * Math.pow(tk[i][k] - yk[i][k], 2);
                }

                // ========================================== Backpropagation

                // Correção dos pesos sinápticos

                // deltinha_k
                for (int k = 0; k < parametros; k++) {
                    deltinha_k[k][0] = (tk[i][k] - yk[i][k]) * derivadaTanh(yk[i][k]);
                }

                // delta_wjk
                for (int j = 0; j < NEURONIOS; j++) {
                    for (int k = 0; k < parametros; k++) {
                        delta_wjk[k][j] += TAXA_DE_APRENDIZADO * deltinha_k[k][0] * zj[i][j];
                    }
                }

                // delta_w0k
                for (int k = 0; k < parametros; k++) {
                    delta_w0k[0][k] += TAXA_DE_APRENDIZADO * deltinha_k[k][0];
                }

                // deltinha_j
                for (int j = 0; j < NEURONIOS; j++) {
                    deltinha_j[0][j] = 0;
                    for (int k = 0; k < parametros; k++) {
                        deltinha_j[0][j] += deltinha_k[k][0] * wjk[j][k];
                    }
                    deltinha_j[0][j] *= derivadaTanh(zj[i][j]);
                }

                // delta_vij
                double[][] deltinha_j_transposta = transposta(deltinha_j);
                for (int j = 0; j < NEURONIOS; j++) {
                    for (int k = 0; k < parametros; k++) {
                        delta_vij[j][k] += TAXA_DE_APRENDIZADO * deltinha_j_transposta[j][0] * xi[i][k];
                    }
                }

                // delta_v0j
                for (int j = 0; j < NEURONIOS; j++) {
                    delta_v0j[0][j] += TAXA_DE_APRENDIZADO * deltinha_j[0][j];
                }

                // wjk_novo
                double[][] delta_wjk_transposta = transposta(delta_wjk);
                for (int j = 0; j < NEURONIOS; j++) {
                    for (int k = 0; k < parametros; k++) {
                        wjk_novo[j][k] = wjk[j][k] + delta_wjk_transposta[j][k];
                    }
                }

                // w0k_novo
                for (int k = 0; k < parametros; k++) {
                    w0k_novo[0][k] = w0k[0][k] + delta_w0k[0][k];
                }

                // vij_novo
                double[][] delta_vij_transposta = transposta(delta_vij);
                for (int k = 0; k < parametros; k++) {
                    for (int j = 0; j < NEURONIOS; j++) {
                        vij_novo[k][j] = vij[k][j] + delta_vij_transposta[k][j];
                    }
                }

                // v0j_novo
                for (int j = 0; j < NEURONIOS; j++) {
                    v0j_novo[0][j] = v0j[0][j] + delta_v0j[0][j];
                }

                // Atualização dos pesos
                vij = vij_novo;
                v0j = v0j_novo;
                wjk = wjk_novo;
                w0k = w0k_novo;

            }

            ciclos++;
            erro /= amostras; // Cálculo do erro médio
            System.out.println("Ciclo: " + ciclos + " | " + "Erro: " + erro);

        }

        pesos_sinapticos_ij = vij;
        bias_j = v0j;
        pesos_sinapticos_jk = wjk;
        bias_k = w0k;

        System.out.println("Rede Treinada.");

        System.out.println("Testando a rede...");
        testarRede(entradas, targets);


    }

    // Funções de ativação
    double sigmoid(double x) {
        return 1 / (1 + Math.exp(-x));
    }
    static double tanh(double x) {
        return Math.tanh(x);
    }

    static double[] softmax(double[] z) {
        // Calcular exponenciais
        double[] expZ = new double[z.length];
        double sumExpZ = 0.0;
        for (int i = 0; i < z.length; i++) {
            expZ[i] = Math.exp(z[i]);
            sumExpZ += expZ[i];
        }

        // Calcular probabilidades
        double[] probabilities = new double[z.length];
        for (int i = 0; i < z.length; i++) {
            probabilities[i] = expZ[i] / sumExpZ;
        }

        return probabilities;
    }

    static double derivadaTanh(double x) {
        double tanhX = Math.tanh(x);
        return 1 - tanhX * tanhX;
    }

    public static double[][] transposta(double[][] matriz) {
        int linhas = matriz.length;       // Linhas
        int colunas = matriz[0].length;   // Colunas

        double[][] transposta = new double[colunas][linhas];

        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                transposta[j][i] = matriz[i][j];
            }
        }

        return transposta;
    }

    public static double[][] zerarValores(double[][] matriz) {
        int linhas = matriz.length;
        int colunas = matriz[0].length;

        double[][] zero = new double[colunas][linhas];

        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                zero[j][i] = 0.0;
            }
        }

        return zero;
    }

    public static void testarRede(double[][] entradas, int[][] targets) {
        double[][] ykTeste = new double[entradas.length][targets[0].length];

        for (int i = 0; i < entradas.length; i++) {
            // (Entrada -> Intermediária)
            double[] zinjLocal = new double[NEURONIOS];
            for (int j = 0; j < NEURONIOS; j++) {
                for (int k = 0; k < entradas[0].length; k++) {
                    zinjLocal[j] += entradas[i][k] * pesos_sinapticos_ij[k][j];
                }
                zinjLocal[j] += bias_j[0][j];
            }

            // Aplicar ativação na camada intermediária
            double[] zjLocal = new double[NEURONIOS];
            for (int j = 0; j < NEURONIOS; j++) {
                zjLocal[j] = tanh(zinjLocal[j]);
            }

            // (Intermediária -> Saída)
            double[] yinkLocal = new double[targets[0].length];
            for (int k = 0; k < targets[0].length; k++) {
                for (int j = 0; j < NEURONIOS; j++) {
                    yinkLocal[k] += zjLocal[j] * pesos_sinapticos_jk[j][k];
                }
                yinkLocal[k] += bias_k[0][k];
            }

            // Aplicar tanh na saída
            for (int k = 0; k < targets[0].length; k++) {
                ykTeste[i][k] = tanh(yinkLocal[k]);
            }
        }

        // Resultados
        for (int i = 0; i < ykTeste.length; i++) {
            System.out.println("Entrada: " + Arrays.toString(entradas[i]));
            System.out.println("Saída: " + Arrays.toString(ykTeste[i]));
            System.out.println("Saída esperada: " + Arrays.toString(targets[i]));
            System.out.println();
        }

    }

}