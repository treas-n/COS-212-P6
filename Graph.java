import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Graph {
    private String[] vertices;
    private Integer[][] adjacencyMatrix;
    private int numVertices;
    private int numEdges;
    // Helper members below
    private boolean[] visited; // store whether a certain vertex has been visited or not
    private Integer[] distanceFromStart, ids, low;
    private String[] queue;
    private int globalCounter = 1, stackSize;

    public Graph(String fileName) {
        if (fileName.equals("")) {
            // initialise empty graph
            initEmpty();
        } else {
            // use file name to initialise the graph
            initFromFile(fileName);
        }
    }

    public void insertVertex(String name) {
        name = name.toUpperCase();

        // Add vertext to vertices array
        vertices = expandVerticesArray();
        numVertices++;
        vertices[numVertices - 1] = name;

        // add vertex to adjacency matrix
        // numVertices++;
        adjacencyMatrix = expandMatrix(numVertices);
    }

    public void insertEdge(String start, String end, int weight) {
        start = start.toUpperCase();
        end = end.toUpperCase();

        // if one of start or end vertices are not in the graph or weight = 0, terminate
        if (!(isInVerticesArray(start) || isInVerticesArray(end)) || weight == 0)
            return;

        int origin = positionInVerticesArray(start);
        int destination = positionInVerticesArray(end);

        adjacencyMatrix[origin][destination] = weight;
        numEdges++;
    }

    public void removeVertex(String name) {
        name = name.toUpperCase();

        if (!isInVerticesArray(name)) {
            return;
        }

        int vertexPosition = positionInVerticesArray(name);

        // remove from the adjacency matrix
        shrinkMatrix(vertexPosition);

        // remove from the vertex array
        shiftVerticesLeft(vertexPosition);
        numVertices--;
        

    }

    public void removeEdge(String start, String end) {
        start = start.toUpperCase();
        end = end.toUpperCase();

        if (isInVerticesArray(start) && isInVerticesArray(end)) {
            int origin = positionInVerticesArray(start);
            int destination = positionInVerticesArray(end);

            adjacencyMatrix[origin][destination] = 0;
            numEdges--;
        }
    }

    @Override
    public String toString() {
        if (numVertices == 0) {
            return "(0,0)";
        }

        String return_ = "";

        /* is there a space before and after the tab or not */
        return_ += "(" + numVertices + "," + numEdges + ")\t";

        // return_ += "\n";

        for (String vertex : vertices) {
            return_ += vertex + "\t"; // + vertex;
        }

        return_ += "\n";

        for (int i = 0; i < numVertices; i++) {
            return_ += vertices[i];
            for (int j = 0; j < numVertices; j++) {
                return_ += "\t" + adjacencyMatrix[i][j];
            }
            return_ += "\n";
        }

        return_ = return_.substring(0, return_.length() - 1);
        return return_;
    }

    public String printMatrix(Double[][] matrix) {
        if (numVertices == 0) {
            return "(0,0)";
        }

        String return_ = "";

        /* is there a space before and after the tab or not */
        return_ += "(" + numVertices + "," + numEdges + ")\t";

        // return_ += "\n";

        for (String vertex : vertices) {
            return_ += vertex + "\t"; // + vertex;
        }

        return_ += "\n";

        for (int i = 0; i < numVertices; i++) {
            return_ += vertices[i];

            for (int j = 0; j < numVertices; j++) {
                if(matrix[i][j] == (int) Double.POSITIVE_INFINITY) return_ += "\t" + "∞";
                
                else return_ += "\t" + matrix[i][j];
                
            }
            return_ += "\n";
        }

        return_ = return_.substring(0, return_.length() - 1);
        return return_;
    }

    public String depthFirstTraversal() {
        if (numVertices == 0)
            return "";

        resetHelpers();
        String return_ = "";

        for (int i = 0; i < vertices.length; i++) {
            if (!visited[i]) {
                return_ += depthFirstTraversal(vertices[i]);
            }
        }

        return return_;
    }

    private String depthFirstTraversal(String current) {
        String return_ = "[" + current + "]",
                neighbors[] = getNeighbours(current);
        int position = positionInVerticesArray(current);

        visited[position] = true;

        for (String vertex : neighbors) {
            int nextVertex = positionInVerticesArray(vertex);
            boolean isVisited = visited[nextVertex];

            if (!isVisited)
                return_ += depthFirstTraversal(vertex);
        }

        return return_;
    }

    public String breadthFirstTraversal() {
        if (numVertices == 0)
            return "";

        resetHelpers();

        queue = new String[numVertices];
        String neighbors[],
                return_ = "",
                current;
        int queueSize = 0;

        // Push the first value onto the queue
        for (int i = 0; i < numVertices; i++) {
            if (!visited[i])
                queue = push(queue, vertices[i], queueSize++);

            while (queueSize > 0) {
                // Get the vertex at the front of the queue and remove it from the queue
                current = queue[0];
                queue = pop(queue);
                queueSize--;
                int position = positionInVerticesArray(current);

                visited[position] = true;

                // add the current vertext to the return string
                return_ += "[" + current + "]";

                neighbors = getNeighbours(current);

                // push all current's neighbors to the queue
                for (String neighbor : neighbors) {
                    int nextVertex = positionInVerticesArray(neighbor);
                    boolean isVisited = visited[nextVertex];

                    if (!isVisited) {
                        queue = push(queue, neighbor, queueSize);
                        queueSize++;
                    }
                }
            }
        }
        return return_;
    }

    public Double[][] shortestPaths() {
        Double[][] resultantMatrix = new Double[numVertices][numVertices];

        for (int i = 0; i < numVertices; i++) {
            for (int j = 0; j < numVertices; j++) {
                String start = vertices[i], end = vertices[j];
                resultantMatrix[i][j] = shortestPath(start, end);
            }
        }
        return resultantMatrix;
    }

    public Double shortestPath(String start, String end) {
        start = start.toUpperCase();
        end = end.toUpperCase();

        if (numVertices == 0 || start.equals(end))
            return 0.0;

        if (!isInVerticesArray(start) || !isInVerticesArray(end))
            return null;

        resetHelpers();

        boolean shortestPathTree[] = new boolean[numVertices];
        // int sPTSize = 0;
        for (int i = 0; i < shortestPathTree.length; i++) {
            shortestPathTree[i] = false;
        }

        int minDist = positionInVerticesArray(start);
        distanceFromStart[minDist] = 0;

        while (includesAllVertices(shortestPathTree)) {
            minDist = pickMinimumDistanceValue(shortestPathTree);

            if (minDist < 0) {
                int endIndex = positionInVerticesArray(end);
                return distanceFromStart[endIndex] * 1.0;
            }

            shortestPathTree[minDist] = true;
            // sPTSize++;

            String[] neighbors = getNeighbours(vertices[minDist]);

            // update the distance value of the neighbors
            for (int i = 0; i < neighbors.length; i++) {
                int neighborIndex = positionInVerticesArray(neighbors[i]);
                int sum = distanceFromStart[minDist] + adjacencyMatrix[minDist][neighborIndex];

                if (sum < distanceFromStart[neighborIndex]) {
                    distanceFromStart[neighborIndex] = sum;
                }
            }
            neighbors = null;
        }

        int endIndex = positionInVerticesArray(end);
        return distanceFromStart[endIndex] * 1.0;
    }

    public boolean cycleDetection() {
        if (numVertices == 0 || numEdges == 0)
            return false;

        boolean[] recursiveStack = new boolean[numVertices];

        for (int j = 0; j < recursiveStack.length; j++) {
            resetHelpers();
            if (detectCycle(j, recursiveStack))
                return true;
        }

        return false;
    }

    public String stronglyConnectedComponents() {
        if (numVertices == 0 || numEdges == 0) {
            return "";
        }

        resetHelpers();

        int stack[] = new int[numVertices];
        boolean onStack[] = new boolean[numVertices];

        for (int i = 0; i < onStack.length; i++) {
            onStack[i] = false;
        }

        String return_ = "";

        for (int i = 0; i < visited.length; i++) {
            if (!visited[i]) {
                return_ += stronglyConnectedComponents(i, stack, onStack);
            }
        }

        return_ = return_.substring(1, return_.length()) + "\n";
        return return_;

        /*
         * for (int i = 0; i < low.length && low[i] != 0; i++) {
         * return_ +=
         * }
         * return low;
         */
    }

    /*
     * 
     *
     * HELPER FUNCTIONS BELOW
     * 
     * 
     */

    private String stronglyConnectedComponents(int currentIndex, int[] stack, boolean[] onStack) {
        stack = push(stack, currentIndex);
        onStack[currentIndex] = true;
        ids[currentIndex] = low[currentIndex] = globalCounter++;
        visited[currentIndex] = true;
        String return_ = "";

        String[] neighbors = getNeighbours(vertices[currentIndex]);

        for (String neighbor : neighbors) {
            int neighborIndex = positionInVerticesArray(neighbor);

            if (!visited[neighborIndex]) {
                return_ += stronglyConnectedComponents(neighborIndex, stack, onStack);
            }
            if (onStack[neighborIndex]) {
                low[currentIndex] = Integer.min(low[currentIndex], low[neighborIndex]);
            }
        }

        if (ids[currentIndex] == low[currentIndex]) {
            int i = 0;
            for (int node = pop(stack);; node = pop(stack)) {
                if (i == 0) {
                    return_ += "\n";
                    i++;
                }
                onStack[node] = false;
                low[node] = ids[currentIndex];

                return_ += "[" + vertices[node] + "]";

                if (node == currentIndex)
                    break;
            }
        }

        return return_;
    }

    /* Recursively searches for a cycle */
    private boolean detectCycle(int currentIndex, boolean[] recursiveStack) {
        if (recursiveStack[currentIndex] || visited[currentIndex])
            return true;

        visited[currentIndex] = true;
        recursiveStack[currentIndex] = true;

        String[] neighbors = getNeighbours(vertices[currentIndex]);

        for (int i = 0; i < neighbors.length; i++) {
            int neighborIndex = positionInVerticesArray(neighbors[i]);

            if (detectCycle(neighborIndex, recursiveStack)) {
                return true;
            }
        }

        recursiveStack[currentIndex] = false;
        return false;
    }

    /* Checks whether all vertices have been added to the shortest path tree
     * If at least one value in @param array is false, that means not all vertices have
     * been included
     */
    private boolean includesAllVertices(boolean array[]) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == false) {
                return true;
            }
        }

        return false;
    }

    // Returns the index of the smallest distance value that has not yet been added
    // to the shortest distance tree (array)
    private int pickMinimumDistanceValue(boolean array[]) {
        int min = (int) Double.POSITIVE_INFINITY,
                minIndex = -1;

        for (int i = 0; i < distanceFromStart.length; i++) {

            boolean isLessThanMin = (distanceFromStart[i] < min) && array[i] == false;

            if (isLessThanMin) {
                min = distanceFromStart[i];
                minIndex = i;
            }
        }
        return minIndex;
    }

    private void resetHelpers() {
        visited = new boolean[numVertices];
        distanceFromStart = new Integer[numVertices];
        // predecessor = new int[numVertices];
        queue = new String[numVertices];
        // stack = new int[numVertices];
        globalCounter = 1;
        stackSize = 0;
        ids = new Integer[numVertices];
        low = new Integer[numVertices];

        for (int i = 0; i < visited.length; i++) {
            visited[i] = false;
            distanceFromStart[i] = Integer.MAX_VALUE;
            // predecessor[i] = -1;
            // stack[i] = -1;
            queue[i] = null;
            // num[i] = 0;
            ids[i] = 0;
            low[i] = 0;
        }
    }

    /*
     * Initialises an empty graph
     */
    private void initEmpty() {
        vertices = new String[0];
        adjacencyMatrix = new Integer[0][0];

        numEdges = numVertices = 0;
        resetHelpers();
    }

    /*
     * Initialises a graph from a file
     */
    private void initFromFile(String fileName) {
        try {
            File file = new File(fileName);

            Scanner scanner = new Scanner(file);

            // init num vertices
            numVertices = Integer.parseInt(scanner.nextLine());
            adjacencyMatrix = new Integer[numVertices][numVertices];

            // init vertices array
            vertices = new String[numVertices];
            String line = scanner.nextLine();
            Scanner scanVertices = new Scanner(line);
            scanVertices.useDelimiter(" ");
            int i = 0;

            for (String vertex : vertices) {
                vertices[i++] = scanVertices.next();
            }

            scanVertices.close();

            // read in the individual rows
            for (int rows = 0; rows < numVertices; rows++) {
                line = scanner.nextLine();
                Scanner scanLine = new Scanner(line);

                // read in each value in the row
                for (int cols = 0; cols < numVertices; cols++) {
                    adjacencyMatrix[rows][cols] = scanLine.nextInt();
                }

                scanLine.close();
            }
            scanner.close();
            countEdges();
            resetHelpers();
        } catch (FileNotFoundException f) {
            System.out.println("Error reading file\n");
            f.printStackTrace();
            System.exit(0);
        }
    }

    private void countEdges() {
        if (numVertices == 0) {
            return;
        }

        int counter = 0;
        for (int i = 0; i < numVertices; i++) {
            for (int j = 0; j < numVertices; j++) {
                if (!adjacencyMatrix[i][j].equals(0)) {
                    counter++;
                }
            }
        }

        numEdges = counter;
    }

    /*
     * Adds one extra space in the vertices array
     */
    private String[] expandVerticesArray() {
        String[] newVertices = new String[numVertices + 1],
                newQueue = new String[numVertices + 1];

        Integer[] newDistFromStart = new Integer[numVertices + 1],
                newIds = new Integer[numVertices + 1],
                newLow = new Integer[numVertices + 1];
        boolean[] newVisited = new boolean[numVertices + 1];

        for (int i = 0; i < numVertices; i++) {
            newVertices[i] = vertices[i];
            newDistFromStart[i] = distanceFromStart[i];
            newQueue[i] = queue[i];
            newIds[i] = ids[i];
            newLow[i] = low[i];
            newVisited[i] = visited[i];
        }

        vertices = newVertices;
        queue = newQueue;
        distanceFromStart = newDistFromStart;
        ids = newIds;
        low = newLow;
        visited = newVisited;

        return newVertices;
    }

    /*
     * Adds one extra row and column to the adjacency matrix
     */
    private Integer[][] expandMatrix(int newSize) {
        if(newSize < 0 || newSize < numVertices) return null;

        // create new array
        Integer[][] newArray = new Integer[newSize][newSize];

        // populate new array with old array
        for (int i = 0; i < newSize - 1; i++) {
            for (int j = 0; j < newSize - 1; j++) {
                newArray[i][j] = adjacencyMatrix[i][j];
            }
            newArray[i][newSize - 1] = 0;
        }

        // set all the values of the "new"/last row to 0
        for (int i = 0; i < newSize; i++) {
            newArray[newSize - 1][i] = 0;
        }

        return newArray;
    }

    /*
     * Search the vertex array for the value passed in the parameter
     */
    private boolean isInVerticesArray(String searchValue) {
        if(searchValue == null) return false;
        
        boolean found = false;

        for (String vertex : vertices) {
            if (vertex.equals(searchValue)) {
                found = true;
                break;
            }
        }

        return found;
    }

    /*
     * Loops over the vertices array until the parameter is found and returned
     */
    private int positionInVerticesArray(String vertex) {
        if(vertex == null) return -1;
        int return_ = 0;
        for (String index : vertices) {
            if (index.equalsIgnoreCase(vertex)) {
                break;
            }
            return_++;
        }

        return return_;
    }

    /*
     * Shifts all the elements in an array to the left starting from the
     * position in the parameter
     */

    private boolean shiftVerticesLeft(int index) {
        if (index < 0 || index > numVertices) {
            return false;
        }

        for (int i = index; i < numVertices - 1; i++) {
            vertices[i] = vertices[i + 1];
        }

        String[] newArray = new String[numVertices - 1];

        for (int i = 0; i < newArray.length; i++) {
            newArray[i] = vertices[i];
        }

        vertices = newArray;
        return true;
    }

    private void shrinkMatrix(int index) {
        if(index > numVertices || index < 0) return;
        
        for (int i = 0; i < numVertices - 1; i++) {
            for(int j = index; j < numVertices - 1; j++) 
                adjacencyMatrix[i][j] = adjacencyMatrix[i][j + 1];

            if(i >= index)
                adjacencyMatrix[i] = adjacencyMatrix[i + 1];
        }

        Integer[][] newMatrix = new Integer[numVertices - 1][numVertices - 1];

        for (int i = 0; i < newMatrix.length; i++) {
            for (int j = 0; j < newMatrix.length; j++)
                newMatrix[i][j] = adjacencyMatrix[i][j];
        }

        adjacencyMatrix = newMatrix;
    }

    private String[] getNeighbours(String vertex) {
        if (isInVerticesArray(vertex)) {
            String[] temp = new String[numVertices];
            int index = positionInVerticesArray(vertex),
                    tempSize = 0;

            for (int i = 0; i < numVertices; i++) {
                if (adjacencyMatrix[index][i] != 0) {
                    temp[tempSize++] = vertices[i];
                }
            }

            String[] return_ = new String[tempSize];

            for (int i = 0; i < return_.length; i++) {
                return_[i] = temp[i];
            }

            return return_;
        }
        return null;
    }

    /*
     * This function adds a value to the queue
     */
    private String[] push(String[] queue, String value, Integer queueSize) {
        if (queueSize < numVertices)
            queue[queueSize] = value;

        return queue;

    }

    /*
     * This function takes a queue array, removes the front element, BUT DOES NOT
     * RETURN IT
     * It returns the queue without the original first element
     */
    private String[] pop(String[] queue) {
        String[] newQ = new String[queue.length];

        for (int i = 0; i < newQ.length - 1; i++) {
            newQ[i] = queue[i + 1];
        }

        return newQ;
    }

    private int[] push(int[] stack, int index) {
        if (stackSize < numVertices)
            stack[stackSize++] = index;
        return stack;
    }

    private int pop(int[] stack) {
        if (stackSize > 0)
            return stack[--stackSize];
        return -1;
    }

    
}