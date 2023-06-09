import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

class AStarPathingStrategy
        implements PathingStrategy
{


    public List<Point> computePath(Point start, Point end,
                                   Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors)
    {
        // returned path
        List<Point> path = new LinkedList<Point>();

        // open queue - prioritize visiting neighbors with better f values
        Queue<Node> openQueue = new PriorityQueue<>(Comparator.comparingInt(Node::getF));

        // closed list (HashMap)
        Map<Point, Node> closedList = new HashMap<Point, Node>();

        // open list
        Map<Point, Node> openList = new HashMap<Point, Node>();

        // make start node current
        Node current = new Node(0, heuristic(start, end), null, start);

        // now add current to openQueue
        openQueue.add(current);

        int g_val;

        while(!openQueue.isEmpty()){
            // pop off node with smallest f
            current = openQueue.remove();

            // check if within reach and if so then add points to path
            if (withinReach.test(current.getPosition(), end)){
                while(current.getPrior() != null){
                    path.add(0, current.getPosition());
                    current = current.getPrior();
                }
                return path;
            }

            // check against if can pass through
            // if on closed list
            // if start node
            // if end node
            // then turn into list
            List<Point> validList = potentialNeighbors.apply(current.getPosition())
                    .filter(canPassThrough)
                    .filter(p -> !closedList.containsKey(p))
                    .filter(p -> !p.equals(start))
                    .filter(p -> !p.equals(end))
                    .toList();

            // for each valid point in list
            for (Point neighbor: validList){

                g_val = current.getG() + 1;

                // if adjacent node already on openList
                if (openList.containsKey(neighbor)){
                    // if g val is better, update it in the queue and map
                    if (g_val < openList.get(neighbor).getG()){
                        Node newNeighbor = new Node(g_val, heuristic(neighbor, end) + g_val, current, neighbor);
                        // remove from queue and add new node with better g value back into queue
                        // replace current node in map with new node with better g value
                        openQueue.remove(openList.get(neighbor));
                        openList.replace(neighbor, newNeighbor);
                        openQueue.add(newNeighbor);

                    }
                }
                else {
                    // add valid neighbor to open queue and map
                    // since neighbor node is valid but not on open list
                    // make new node for it and add it to open queue and list
                    Node neighborNode = new Node(g_val, heuristic(neighbor, end) + g_val, current, neighbor);
                    openQueue.add(neighborNode);
                    openList.put(neighbor, neighborNode);
                }

            }
            // move current to closed list
            closedList.put(current.getPosition(), current);
        }

        System.out.println("No path");

        return path;
    }

    private int heuristic(Point cur, Point goal){
        return Math.abs(cur.x - goal.x) + Math.abs(cur.y - goal.y);
    }

    class Node {
        private int g;
        private int f;
        private Node prior;
        private Point position;

        public Node (int g, int f, Node prior, Point position){
            this.g = g;
            this.f = f ;
            this.prior = prior;
            this.position = position;
        }

        private int getF(){return f;}

        private int getG(){return g;}

        private Point getPosition(){return position;}

        private Node getPrior(){return prior;}

    }
}
