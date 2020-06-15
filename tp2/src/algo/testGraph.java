package algo;

public class testGraph {
    public static void main(String[] args) {
        GraphWeighted graphWeighted = new GraphWeighted(true);

        NodeWeighted A = new NodeWeighted(1, "A");
        NodeWeighted B = new NodeWeighted(2, "B");
        NodeWeighted C = new NodeWeighted(3, "C");
        NodeWeighted D = new NodeWeighted(4, "D");
        NodeWeighted E = new NodeWeighted(5, "E");
        NodeWeighted F = new NodeWeighted(6, "F");
        NodeWeighted END = new NodeWeighted(6, "END");


        graphWeighted.addEdge(A, B, 5);
        graphWeighted.addEdge(A, D, 45);
        graphWeighted.addEdge(B, C, 70);
        graphWeighted.addEdge(B, E, 3);
        graphWeighted.addEdge(B, E, 3);
        graphWeighted.addEdge(D, C, 50);
        graphWeighted.addEdge(D, E, 8);
        graphWeighted.addEdge(C, F, 78);
        graphWeighted.addEdge(E, F, 7);
        graphWeighted.addEdge(F, END, 0);

        graphWeighted.DijkstraShortestPath(A, END);
    }
}