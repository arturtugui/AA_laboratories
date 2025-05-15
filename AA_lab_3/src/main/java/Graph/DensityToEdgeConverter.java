package Graph;

public class DensityToEdgeConverter {
    public static int UndirectedConverter(int v, int density) {
        return (density * v * (v-1)) /2;
    }

    public static int DirectedConverter(int v, int density) {
        return density * v * (v-1);
    }

    public static int BipartiteConverter(int n, int u, int density) {
        return density * u * (n-u);
    }
}
