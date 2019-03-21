package mazes.generators.maze;

import java.util.Random;

import datastructures.concrete.Graph;
import datastructures.interfaces.ISet;
import mazes.entities.Maze;
import mazes.entities.Room;
import mazes.entities.Wall;

/**
 * Carves out a maze based on Kruskal's algorithm.
 *
 * See the spec for more details.
 */
public class KruskalMazeCarver implements MazeCarver {
    @Override
    public ISet<Wall> returnWallsToRemove(Maze maze) {
        // Note: make sure that the input maze remains unmodified after this method is over.
        //
        // In particular, if you call 'wall.setDistance()' at any point, make sure to
        // call 'wall.resetDistanceToOriginal()' on the same wall before returning.

        Random rand = new Random();

        for (Wall wall : maze.getWalls()) {
            wall.setDistance(rand.nextDouble());
        }

        Graph<Room, Wall> graph = new Graph<>(maze.getRooms(), maze.getWalls());
        ISet<Wall> toRemove = graph.findMinimumSpanningTree();

        for (Wall wall : maze.getWalls()) {
            wall.resetDistanceToOriginal();
        }

        return toRemove;
    }
}
