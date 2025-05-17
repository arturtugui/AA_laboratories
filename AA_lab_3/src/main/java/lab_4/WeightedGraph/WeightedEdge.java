package lab_4.WeightedGraph;

public class WeightedEdge<V> {
    public final V target;
    public final double weight;

    public WeightedEdge(V target, double weight) {
        this.target = target;
        this.weight = weight;
    }

    @Override
    public String toString() {
        return target + " (" + weight + ")";
    }
}

