public class Main {
    public static void main(String[] args) {

        System.out.println("Algoritmo Madaline\n");

        System.out.println("Carregando as entradas...");

        // [5][50]
        int[][] entradas = {
                // A
                {0, 0, 1, 1, 1, 0, 0, 0, 0, 0,
                        0, 1, 0, 0, 0, 1, 0, 0, 0, 0,
                        1, 0, 0, 0, 0, 0, 1, 0, 0, 0,
                        1, 1, 1, 1, 1, 1, 1, 0, 0, 0,
                        1, 0, 0, 0, 0, 0, 1, 0, 0, 0 },

                // B
                {1, 1, 1, 1, 1, 0, 0, 0, 0, 0,
                        1, 0, 0, 0, 0, 1, 0, 0, 0, 0,
                        1, 1, 1, 1, 1, 0, 0, 0, 0, 0,
                        1, 0, 0, 0, 0, 1, 0, 0, 0, 0,
                        1, 1, 1, 1, 1, 0, 0, 0, 0, 0 },

                // C
                {0, 1, 1, 1, 1, 1, 0, 0, 0, 0,
                        1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        0, 1, 1, 1, 1, 1, 0, 0, 0, 0 },

                // D
                {1, 1, 1, 1, 1, 0, 0, 0, 0, 0,
                        1, 0, 0, 0, 0, 1, 0, 0, 0, 0,
                        1, 0, 0, 0, 0, 1, 0, 0, 0, 0,
                        1, 0, 0, 0, 0, 1, 0, 0, 0, 0,
                        1, 1, 1, 1, 1, 0, 0, 0, 0, 0 },

                // E
                {1, 1, 1, 1, 1, 0, 0, 0, 0, 0,
                        1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        1, 1, 1, 1, 1, 0, 0, 0, 0, 0,
                        1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                        1, 1, 1, 1, 1, 0, 0, 0, 0, 0 }
        };

        System.out.println("Carregando as saídas esperadas...");

        // [5][5]
        int[][] saidas = {
                {1, 0, 0, 0, 0}, // A
                {0, 1, 0, 0, 0}, // B
                {0, 0, 1, 0, 0}, // C
                {0, 0, 0, 1, 0}, // D
                {0, 0, 0, 0, 1}  // E
        };

        System.out.println("Inicializando o algoritmo...");

        Madaline madaline = new Madaline(entradas, saidas);

        int[] teste = { 1, 1, 0, 1, 1, 0, 0, 0, 0, 0,
                1, 0, 0, 0, 0, 1, 0, 0, 0, 0,
                0, 1, 0, 1, 1, 0, 0, 0, 0, 0,
                1, 0, 0, 0, 0, 1, 0, 0, 0, 0,
                1, 1, 1, 1, 1, 0, 0, 0, 0, 0 };

        int[] saidasCalculadas = madaline.testarRede(teste);

        String letraIdentificada = madaline.identificarPadrao(saidasCalculadas, saidas);

        System.out.println("A letra identificada pela rede é: " + letraIdentificada);

    }
}