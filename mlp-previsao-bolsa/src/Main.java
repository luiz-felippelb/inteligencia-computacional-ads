import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    static final double TAXA_DE_APRENDIZADO = 0.01;
    static final int NEURONIOS = 4; // Número de neurônios na camada oculta
    static final double ERRO_TOLERADO = 0.01; // Erro tolerado
    static final double PORCENTAGEM_TREINO = 0.7; // 70% para treino

    static double[][] pesos_sinapticos_ij;
    static double[][] bias_j;
    static double[][] pesos_sinapticos_jk;
    static double[][] bias_k;

    static double[] maxColuna; // Armazena o máximo de cada coluna
    static double[] minColuna; // Armazena o mínimo de cada coluna

    public static void main(String[] args) {
        String caminhoCSV = "src/dados_acoes_itub3.csv";
        double[][] dados = carregarDadosCSV(caminhoCSV);

        // Normalizar os dados
        double[][] dadosNormalizados = normalizar(dados);

        // Dividir dados em entradas e targets
        int totalDados = dadosNormalizados.length - 1; // Deixa de usar o último elemento


        double[][] entradas = new double[totalDados][4]; // 4: open, high, low, volume
        double[][] targets = new double[totalDados][1]; // 1: close

        //int totalDados = dadosNormalizados.length - 1;

        int treinoSize = (int) (totalDados * PORCENTAGEM_TREINO);
        for (int i = 0; i < totalDados; i++) {
            entradas[i] = Arrays.copyOfRange(dadosNormalizados[i], 0, 4); // open, high, low, volume
            if (i + 1 < dadosNormalizados.length) {
                targets[i][0] = dadosNormalizados[i + 1][4]; // o "close" do próximo dia
            }
        }


        // Divisão treino e teste
        double[][] entradasTreino = Arrays.copyOfRange(entradas, 0, treinoSize);
        double[][] targetsTreino = Arrays.copyOfRange(targets, 0, treinoSize);
        double[][] entradasTeste = Arrays.copyOfRange(entradas, treinoSize, totalDados);
        double[][] targetsTeste = Arrays.copyOfRange(targets, treinoSize, totalDados);

        // Inicializar pesos e bias
        inicializarPesosEBias(entradas[0].length, targets[0].length);

        // Treinar o modelo
        treinarRede(entradasTreino, targetsTreino);

        // Testar a rede
        testarRede(entradasTeste, targetsTeste);
    }

    public static double[][] normalizar(double[][] dados) {
        maxColuna = new double[dados[0].length];
        minColuna = new double[dados[0].length];
        double[][] dadosNormalizados = new double[dados.length][dados[0].length];

        for (int j = 0; j < dados[0].length; j++) {
            int finalJ1 = j;
            maxColuna[j] = Arrays.stream(dados).mapToDouble(d -> d[finalJ1]).max().orElse(1);
            int finalJ = j;
            minColuna[j] = Arrays.stream(dados).mapToDouble(d -> d[finalJ]).min().orElse(0);

            for (int i = 0; i < dados.length; i++) {
                dadosNormalizados[i][j] = (dados[i][j] - minColuna[j]) / (maxColuna[j] - minColuna[j]);
            }
        }
        return dadosNormalizados;
    }

    public static double[] desnormalizar(double[] valor, int coluna) {
        double min = minColuna[coluna];
        double max = maxColuna[coluna];
        double[] valoresDesnormalizados = new double[valor.length];
        for (int i = 0; i < valor.length; i++) {
            valoresDesnormalizados[i] = (valor[i] * (max - min)) + min;
        }
        return valoresDesnormalizados;
    }

    public static void treinarRede(double[][] entradas, double[][] targets) {
        int amostras = entradas.length;
        double erro = 1;
        int ciclos = 0;

        System.out.println("Início do treinamento...");

        while (erro > ERRO_TOLERADO && ciclos < 10000) {
            erro = 0.0;
            for (int i = 0; i < amostras; i++) {
                // Forward
                double[] zinj = calcularZinj(entradas[i]);
                double[] zj = ativacaoReLU(zinj);
                double[] yk = calcularYk(zj);

                // Cálculo do erro
                double erroAmostra = 0.5 * Math.pow(targets[i][0] - yk[0], 2);
                erro += erroAmostra;

                // Backward (atualizar pesos)
                backpropagation(entradas[i], targets[i], yk, zj, zinj);
            }

            erro /= amostras; // Erro médio
            ciclos++;
            System.out.println("Ciclo: " + ciclos + " | Erro: " + erro);
        }

        System.out.println("Rede treinada.");
    }

    public static void testarRede(double[][] entradas, double[][] targets) {
        for (int i = 0; i < entradas.length; i++) {
            double[] zinj = calcularZinj(entradas[i]);
            double[] zj = ativacaoReLU(zinj);
            double[] yk = calcularYk(zj);

            double[] saidaPrevistaReal = desnormalizar(yk, 4);
            double[] valorReal = desnormalizar(targets[i], 4);

            System.out.println("Saída prevista real: " + Arrays.toString(saidaPrevistaReal));
            System.out.println("Valor real: " + Arrays.toString(valorReal));
        }
    }

    public static double[][] carregarDadosCSV(String caminho) {
        ArrayList<double[]> dadosList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(caminho))) {
            String linha;
            br.readLine(); // Pula o cabeçalho
            while ((linha = br.readLine()) != null) {
                String[] valores = linha.split(",");
                double[] linhaNumerica = new double[5]; // 5 colunas: open, high, low, volume, close
                try {
                    for (int j = 1; j <= 5; j++) {
                        String valor = valores[j].trim().replace("\"", "").replace(",", ".");
                        if (!valor.isEmpty()) {
                            linhaNumerica[j - 1] = Double.parseDouble(valor);
                        } else {
                            linhaNumerica[j - 1] = 0.0; // Valor padrão para campos vazios
                        }
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Erro ao converter valor na linha: " + linha);
                    e.printStackTrace();
                }
                dadosList.add(linhaNumerica);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        double[][] dados = new double[dadosList.size()][5];
        for (int i = 0; i < dadosList.size(); i++) {
            dados[i] = dadosList.get(i);
        }
        return dados;
    }

    public static void inicializarPesosEBias(int entradas, int saidas) {
        pesos_sinapticos_ij = new double[entradas][NEURONIOS];
        pesos_sinapticos_jk = new double[NEURONIOS][saidas];
        bias_j = new double[1][NEURONIOS];
        bias_k = new double[1][saidas];

        for (int i = 0; i < entradas; i++) {
            for (int j = 0; j < NEURONIOS; j++) {
                pesos_sinapticos_ij[i][j] = Math.random() - 0.5;
            }
        }

        for (int j = 0; j < NEURONIOS; j++) {
            for (int k = 0; k < saidas; k++) {
                pesos_sinapticos_jk[j][k] = Math.random() - 0.5;
            }
            bias_j[0][j] = Math.random() - 0.5;
        }

        for (int k = 0; k < saidas; k++) {
            bias_k[0][k] = Math.random() - 0.5;
        }
    }

    public static double[] calcularZinj(double[] entrada) {
        double[] zinj = new double[NEURONIOS];
        for (int j = 0; j < NEURONIOS; j++) {
            zinj[j] = 0.0;
            for (int i = 0; i < entrada.length; i++) {
                zinj[j] += entrada[i] * pesos_sinapticos_ij[i][j];
            }
            zinj[j] += bias_j[0][j];
        }
        return zinj;
    }

    public static double[] ativacaoReLU(double[] z) {
        double[] ativacao = new double[z.length];
        for (int i = 0; i < z.length; i++) {
            ativacao[i] = Math.max(0, z[i]); // Função ReLU
        }
        return ativacao;
    }

    public static double[] calcularYk(double[] zj) {
        double[] yk = new double[1];
        yk[0] = 0.0;
        for (int j = 0; j < NEURONIOS; j++) {
            yk[0] += zj[j] * pesos_sinapticos_jk[j][0];
        }
        yk[0] += bias_k[0][0];
        return yk;
    }

    public static void backpropagation(double[] entrada, double[] target, double[] yk, double[] zj, double[] zinj) {
        double erro = target[0] - yk[0];

        // Atualização dos pesos j -> k
        for (int j = 0; j < NEURONIOS; j++) {
            pesos_sinapticos_jk[j][0] += TAXA_DE_APRENDIZADO * erro * zj[j];
        }

        // Atualização dos pesos i -> j
        for (int i = 0; i < entrada.length; i++) {
            for (int j = 0; j < NEURONIOS; j++) {
                double gradiente = erro * pesos_sinapticos_jk[j][0] * (zinj[j] > 0 ? 1 : 0); // Derivada ReLU
                pesos_sinapticos_ij[i][j] += TAXA_DE_APRENDIZADO * gradiente * entrada[i];
            }
        }
    }
}