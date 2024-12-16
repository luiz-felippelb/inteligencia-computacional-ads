import java.util.Arrays;
import java.util.Random;

public class Madaline {

    // Limiar de ativação
    private final double LIMIAR = 0.5;

    // Alfa
    private final double TAXA_APRENDIZADO = 0.2;

    // Erro tolerado
    private final double ERRO_TOLERADO = 0.01;

    private int[][] entradas;
    private int[][] saidas;
    private double[][] pesos;
    private double[] bias;

    Madaline(int[][] entradas, int[][] saidas) {
        this.entradas = entradas;
        this.saidas = saidas;

        System.out.println("Inicializando pesos sinapticos...");
        // Pesos: v
        this.pesos = this.inicializarPesosSinapticos(this.entradas);

        System.out.println("Inicializando biases sinapticos...");
        // Bias: v0
        int tamanho = this.saidas.length;
        this.bias = this.inicializarBias(tamanho);

        System.out.println("Inicializando treinamento da rede...");
        this.treinarRede(this.entradas, this.saidas, this.pesos, this.bias);

    }

    private double[][] inicializarPesosSinapticos(int[][] entradas) {
        int linhas = entradas.length;
        int colunas = entradas[0].length;

        double[][] pesos = new double[linhas][colunas];

        double min = -0.1;
        double max = 0.1;

        Random r = new Random();

        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                double peso = r.nextDouble() * (max - min) + min;
                pesos[i][j] = Math.round(peso * 100.0) / 100.0;
            }
        }

        return pesos;
    }

    // Definir um bias para cada saída da rede
    private double[] inicializarBias(int tamanho) {
        double[] bias = new double[tamanho];
        double min = -0.1;
        double max = 0.1;

        Random r = new Random();

        for (int i = 0; i < tamanho; i++) {
            double valor = r.nextDouble() * (max - min) + min;
            bias[i] = Math.round(valor * 100.0) / 100.0;
        }

        return bias;
    }

    private double calcularSaidaPura(int[] padrao, double[] pesos, double bias) {
        double saidaPura = bias;

        for (int i = 0; i < padrao.length; i++) {
            saidaPura += padrao[i] * pesos[i];
        }

        return saidaPura;
    }

    private int calcularSaidaLiquida(double saidaPura, double limiar) {
        return saidaPura >= limiar ? 1 : 0;
    }

    private void treinarRede(int[][] entradas, int[][] saidas, double[][] pesos, double[] bias) {
        int ciclos = 0;
        double erro = 1.0;

        while (erro > ERRO_TOLERADO) {
            erro = 0.0;

            // Itera sobre cada padrão de entrada
            for (int p = 0; p < entradas.length; p++) {
                int[] padraoEntrada = entradas[p];
                int[] saidaEsperada = saidas[p];

                // Itera sobre cada saída esperada
                for (int s = 0; s < saidaEsperada.length; s++) {
                    double saidaPura = calcularSaidaPura(padraoEntrada, pesos[s], bias[s]);
                    int saidaLiquida = calcularSaidaLiquida(saidaPura, LIMIAR);

                    // Calcular o erro quadratico acumulado
                    int erroPadrao = saidaEsperada[s] - saidaLiquida;
                    erro += Math.pow(erroPadrao, 2);

                    // Atualização dos pesos e bias
                    if (erroPadrao != 0) {
                        for (int i = 0; i < pesos[s].length; i++) {
                            pesos[s][i] += TAXA_APRENDIZADO * erroPadrao * padraoEntrada[i];
                        }

                        bias[s] += TAXA_APRENDIZADO * erroPadrao;
                    }
                }
            }

            ciclos++;
            System.out.println("Ciclo: " + ciclos + " - " + "Erro: " + erro);
        }

        System.out.println("Treinamento concluído.");
        System.out.println("Ciclos executados: " + ciclos);
    }

    public int[] testarRede(int[] entrada) {
        System.out.println("Testando a rede...");

        int[] saidasCalculadas = new int[this.pesos.length];

        // Iterar sobre cada saída
        for (int s = 0; s < pesos.length; s++) {
            double saidaPura = this.bias[s];

            // Iterar sobre cada padrao da entrada
            for (int p = 0; p < entrada.length; p++) {
                saidaPura += entrada[p] * this.pesos[s][p];
            }

            saidasCalculadas[s] = (saidaPura >= LIMIAR) ? 1 : 0;
        }

        System.out.println("Rede testada com sucesso.");

        return saidasCalculadas;
    }

    public String identificarPadrao(int[] saidasCalculadas, int[][] saidasEsperadas) {
        System.out.println("Identificando padrão...");

        for (int i = 0; i < saidasEsperadas.length; i++) {
            if (Arrays.equals(saidasCalculadas, saidasEsperadas[i])) {
                switch (i) {
                    case 0: return "A";
                    case 1: return "B";
                    case 2: return "C";
                    case 3: return "D";
                    case 4: return "E";
                }
            }
        }

        return "Letra não reconhecida.";
    }
}
