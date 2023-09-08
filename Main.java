import java.lang.reflect.GenericSignatureFormatError;

public class Main {
    public static void main(String[] args) {
        test1();
    }

    public static void test1() {
        System.out.println("\n\n------------1------------");
    
        Graph g = new Graph("graph.txt");
        System.out.println(g);

        System.out.println("\n\n------------2------------");
        g.insertVertex("X");
        System.out.println(g);

        System.out.println("DFS:" + g.depthFirstTraversal());
        System.out.println("BFS:" + g.breadthFirstTraversal());
        System.out.println("Strongly Connected:\n" + g.stronglyConnectedComponents());

        System.out.println("\n\n------------3------------");
        g.insertEdge("l", "x", 2);
        g.insertEdge("x", "n", 6);
        System.out.println(g);

        System.out.println("DFS:" + g.depthFirstTraversal());
        System.out.println("BFS:" + g.breadthFirstTraversal());
        System.out.println("Strongly Connected:\n" + g.stronglyConnectedComponents());

        System.out.println("\n\n------------4------------");
        g.removeEdge("r", "p");
        g.removeEdge("n", "r");
        System.out.println(g);

        System.out.println("DFS:" + g.depthFirstTraversal());
        System.out.println("BFS:" + g.breadthFirstTraversal());
        System.out.println("Strongly Connected:\n" + g.stronglyConnectedComponents());

        System.out.println("\n\n------------5------------");
        g.removeVertex("x");
        System.out.println(g);

        System.out.println("DFS:" + g.depthFirstTraversal());
        System.out.println("BFS:" + g.breadthFirstTraversal());
        System.out.println("Strongly Connected:\n" + g.stronglyConnectedComponents());

        System.out.println("\n\n------------6------------");
    }

}