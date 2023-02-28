package sorts.intercalation.substitute;

public class SubstitutionHeapNode<T extends Comparable<T>> implements Comparable<SubstitutionHeapNode<T>> {

    // Atributes

    private T item;
    private int weight;


    // Constructor

    public SubstitutionHeapNode(T item, int weight) {
        this.item = item;
        this.weight = weight;
    }


    /**
     * Compares two SubstitutionHeapNodes.
     * @param tSubstitutionHeapNode the other SubstitutionHeapNode
     * @return the difference between the weights of the two nodes if they are not equal. Otherwise, the difference between the items of the two nodes.
     */

    @Override
    public int compareTo(SubstitutionHeapNode<T> tSubstitutionHeapNode) {
        if (tSubstitutionHeapNode.weight != this.weight)
            return this.weight - tSubstitutionHeapNode.weight;
        return this.item.compareTo(tSubstitutionHeapNode.item);
    }

    // Getters and Setters

    public T getItem() {
        return item;
    }

    public void setItem(T item) {
        this.item = item;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
