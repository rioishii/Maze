package datastructures.concrete;

import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IEdge;
import datastructures.interfaces.IList;
import datastructures.interfaces.ISet;
import datastructures.interfaces.IDisjointSet;
import datastructures.interfaces.IPriorityQueue;
import misc.Sorter;
import misc.exceptions.NoPathExistsException;

/**
 * Represents an undirected, weighted graph, possibly containing self-loops, parallel edges,
 * and unconnected components.
 *
 * Note: This class is not meant to be a full-featured way of representing a graph.
 * We stick with supporting just a few, core set of operations needed for the
 * remainder of the project.
 */
public class Graph<V, E extends IEdge<V> & Comparable<E>> {
    // NOTE 1:
    //
    // Feel free to add as many fields, private helper methods, and private
    // inner classes as you want.
    //
    // And of course, as always, you may also use any of the data structures
    // and algorithms we've implemented so far.
    //
    // Note: If you plan on adding a new class, please be sure to make it a private
    // static inner class contained within this file. Our testing infrastructure
    // works by copying specific files from your project to ours, and if you
    // add new files, they won't be copied and your code will not compile.
    //
    //
    // NOTE 2:
    //
    // You may notice that the generic types of Graph are a little bit more
    // complicated than usual.
    //
    // This class uses two generic parameters: V and E.
    //
    // - 'V' is the type of the vertices in the graph. The vertices can be
    //   any type the client wants -- there are no restrictions.
    //
    // - 'E' is the type of the edges in the graph. We've constrained Graph
    //   so that E *must* always be an instance of IEdge<V> AND Comparable<E>.
    //
    //   What this means is that if you have an object of type E, you can use
    //   any of the methods from both the IEdge interface and from the Comparable
    //   interface
    //
    // If you have any additional questions about generics, or run into issues while
    // working with them, please ask ASAP either on Piazza or during office hours.
    //
    // Working with generics is really not the focus of this class, so if you
    // get stuck, let us know we'll try and help you get unstuck as best as we can.

    /**
     * Constructs a new graph based on the given vertices and edges.
     *
     * @throws IllegalArgumentException if any of the edges have a negative weight
     * @throws IllegalArgumentException if one of the edges connects to a vertex not
     *                                  present in the 'vertices' list
     * @throws IllegalArgumentException if vertices or edges are null or contain null
     */

    private IList<V> vertices;
    private IList<E> edges;
    private IDictionary<V, IList<E>> adjacencyList;

    public Graph(IList<V> vertices, IList<E> edges) {
        this.vertices = vertices;
        this.edges = edges;
        this.adjacencyList = new ChainedHashDictionary<>();

        for (V vertex : vertices) {
            if (vertex == null) {
                throw new IllegalArgumentException();
            }
            this.adjacencyList.put(vertex, new DoubleLinkedList<>());
        }

        for (E edge : edges) {
            if (edge == null) {
                throw new IllegalArgumentException();
            }
            if (edge.getWeight() < 0) {
                throw new IllegalArgumentException();
            }
            if (!vertices.contains(edge.getVertex1()) || !vertices.contains(edge.getVertex2())) {
                throw new IllegalArgumentException();
            }
            this.adjacencyList.get(edge.getVertex1()).add(edge);
            this.adjacencyList.get(edge.getVertex2()).add(edge);
        }
    }

    /**
     * Sometimes, we store vertices and edges as sets instead of lists, so we
     * provide this extra constructor to make converting between the two more
     * convenient.
     *
     * @throws IllegalArgumentException if any of the edges have a negative weight
     * @throws IllegalArgumentException if one of the edges connects to a vertex not
     *                                  present in the 'vertices' list
     * @throws IllegalArgumentException if vertices or edges are null or contain null
     */
    public Graph(ISet<V> vertices, ISet<E> edges) {
        // You do not need to modify this method.
        this(setToList(vertices), setToList(edges));
    }

    // You shouldn't need to call this helper method -- it only needs to be used
    // in the constructor above.
    private static <T> IList<T> setToList(ISet<T> set) {
        if (set == null) {
            throw new IllegalArgumentException();
        }
        IList<T> output = new DoubleLinkedList<>();
        for (T item : set) {
            output.add(item);
        }
        return output;
    }

    /**
     * Returns the number of vertices contained within this graph.
     */
    public int numVertices() {
        return this.vertices.size();
    }

    /**
     * Returns the number of edges contained within this graph.
     */
    public int numEdges() {
        return this.edges.size();
    }

    /**
     * Returns the set of all edges that make up the minimum spanning tree of
     * this graph.
     *
     * If there exists multiple valid MSTs, return any one of them.
     *
     * Precondition: the graph does not contain any unconnected components.
     */
    public ISet<E> findMinimumSpanningTree() {
        ISet<E> result = new ChainedHashSet<>();
        IDisjointSet<V> disjointSet = new ArrayDisjointSet<>();
        this.edges = Sorter.topKSort(edges.size(), edges);
        for (V vertex : this.vertices) { // making components for all vertices
            disjointSet.makeSet(vertex);
        }

        for (E edge : this.edges) {
            V vertexU = edge.getVertex1();
            V vertexV = edge.getVertex2();
            if (disjointSet.findSet(vertexU) != disjointSet.findSet(vertexV)) {
                result.add(edge);
                disjointSet.union(vertexV, vertexU);
            }
        }

        return result;
    }

    /**
     * Returns the edges that make up the shortest path from the start
     * to the end.
     *
     * The first edge in the output list should be the edge leading out
     * of the starting node; the last edge in the output list should be
     * the edge connecting to the end node.
     *
     * Return an empty list if the start and end vertices are the same.
     *
     * @throws NoPathExistsException  if there does not exist a path from the start to the end
     * @throws IllegalArgumentException if start or end is null
     */
    public IList<E> findShortestPathBetween(V start, V end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException();
        }

        // return empty list of start and end are the same
        if (start == end) {
            return new DoubleLinkedList<>();
        }

        IList<E> result = new DoubleLinkedList<>();
        IDictionary<V, Vertex> allVertices = new ChainedHashDictionary<>();
        ISet<V> unprocessed = new ChainedHashSet<>();

        // make all vertices weight equal to infinity
        for (V vertex : this.vertices) {
            Vertex comparableVertex = new Vertex(vertex, Double.POSITIVE_INFINITY);
            allVertices.put(vertex, comparableVertex);
            unprocessed.add(vertex);
        }

        Vertex sourceVertex = new Vertex(start, 0.0);
        allVertices.put(start, sourceVertex);

        // initialize MPQ with source as the first thing inside of it
        IPriorityQueue<Vertex> minPriorityQueue = new ArrayHeap<>();
        minPriorityQueue.insert(allVertices.get(start));
        boolean foundPath = false;
        Vertex v = null; // last vertex we put a predecessor on

        while (!minPriorityQueue.isEmpty() && !foundPath) {
            Vertex u = minPriorityQueue.removeMin();

            if (unprocessed.contains(u.getValue())) {
                if (u.getValue() == end) {
                    if (v.getValue() != start || v.getValue() != end) {
                        v = u;
                    }

                    foundPath = true;
                    break;
                }

                // reached the end of the stuff we want in our stack
                if (u.getDist() == Double.POSITIVE_INFINITY) {
                    while (!minPriorityQueue.isEmpty()) {
                        minPriorityQueue.removeMin();
                    }
                }

                for (E edge : adjacencyList.get(u.getValue())) {
                    V otherVertex = edge.getOtherVertex(u.getValue());
                    if (unprocessed.contains(otherVertex)) {
                        v = allVertices.get(otherVertex);
                        double oldDist = v.getDist();
                        double newDist = u.getDist() + edge.getWeight();

                        if (newDist < oldDist) {
                            v.setDist(newDist);
                            v.setPredecessor(u);
                            minPriorityQueue.insert(v);
                        }
                    }
                }

                unprocessed.remove(u.getValue());
            }
        }

        if (!foundPath) {
            throw new NoPathExistsException();
        }

        while (v.getPredecessor() != null) {
            for (E edge : adjacencyList.get(v.getValue())) {
                V otherVertex = edge.getOtherVertex(v.getValue());
                if (otherVertex == v.getPredecessor().getValue()) {
                    result.insert(0, edge);
                    v = v.getPredecessor();
                    break;
                }
            }
        }

        return result;
    }

    // Custom class to keep track of weights in vertex
    private class Vertex implements Comparable<Vertex> {
        private V value;
        private double dist;
        private Vertex predecessor;

        Vertex(V value, double dist) {
            this.value = value;
            this.dist = dist;
        }

        V getValue() {
            return this.value;
        }

        double getDist() {
            return this.dist;
        }

        void setDist(double dist) {
            this.dist = dist;
        }

        void setPredecessor(Vertex predecessor) {
            this.predecessor = predecessor;
        }

        Vertex getPredecessor() {
            return this.predecessor;
        }

        @Override
        public int compareTo(Vertex other) {
            return (int) this.getDist() - (int) other.getDist();
        }
    }
}
