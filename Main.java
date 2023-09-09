
public class Main {
    public static void main(String[] args) {
        test1();
    }

    public static void test1() {
        
        System.out.println("\n------------1-INITIAL-----------");
    
        Graph g = new Graph("graph.txt");
        System.out.println(g);

        System.out.println("\n\n=======TRAVERSALS=======");
        System.out.println("\n------------2------------");
        
        System.out.println("DFS:" + g.depthFirstTraversal());
        System.out.println("BFS:" + g.breadthFirstTraversal());
        System.out.println("Strongly Connected Components: \n" + g.stronglyConnectedComponents());
        
        System.out.println("\n\n=======ADDITION-AND-REMOVAL-OF-VERTICES-AND-EDGES=======");
        System.out.println("\n------------3-ADD(X)-----------");
        g.insertVertex("X");
        System.out.println(g);

        System.out.println("\n\n------------4-ADD(JX-2)-&-ADD(XA-6)-&-ADD(MJ)----------");
        g.insertEdge("j", "x", 2);
        g.insertEdge("x", "a", 6);
        g.insertEdge("M", "J", 5);
        System.out.println(g);

        System.out.println("Strongly Connected Components: \n" + g.stronglyConnectedComponents());

        System.out.println("\n\n------------5-REMOVE(PL)------------");
        g.removeEdge("p", "l");
        System.out.println(g);


        System.out.println("\n\n------------6-REMOVE(L)-----------");
        g.removeVertex("l");
        System.out.println(g);

        System.out.println("\n\n=======SHORTEST-PATHS=======");
        System.out.println("\n------------7------------");

        Double shortestPath = g.shortestPath("a", "r");
        System.out.println("Shortest Path from A to R: " + shortestPath);
        
        System.out.println("\n------------8------------");

        Double[][] shortestPaths = g.shortestPaths();
        
        System.out.println("All-to-All Shortest Paths:");
        System.out.println(g.printMatrix(shortestPaths));

        System.out.println("\n\n=======CYCLE-DETECTION=======");
        System.out.println("\n------------9------------");

        if (g.cycleDetection()) 
            System.out.println("Graph has a cycle"); 
        else 
            System.out.println("Graph does not have a cycle");

        System.out.println("\n------------10------------");

        System.out.println("Strongly Connected Components: \n" + g.stronglyConnectedComponents());
    }

}
