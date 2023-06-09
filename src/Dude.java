import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public interface Dude extends Moving {

    default void transformDude(Entity dude, WorldModel world, EventScheduler scheduler, ImageStore imageStore){
        world.removeEntity(scheduler, this);
        world.addEntity(dude);
        ((Active)dude).scheduleActions(scheduler, world, imageStore);
    }

    //next position is inherited
    default Point nextPosition(WorldModel world, Point destPos) {
        Predicate<Point> canPassThrough = (p) -> world.withinBounds(p) && (!world.isOccupied(p) || (world.getOccupancyCell(p) instanceof Stump));
        //can pass through is lambda
        BiPredicate<Point, Point> withinReach = (p1, p2) -> p1.adjacent(p2);

        // PathingStrategy strat = new SingleStepPathingStrategy();
        PathingStrategy strat = new AStarPathingStrategy();
        List<Point> path = strat.computePath(this.getPosition(), destPos, canPassThrough, withinReach,PathingStrategy.CARDINAL_NEIGHBORS);

        Point nextPos = this.getPosition();
        if (path.size() > 0){
            nextPos = path.get(0);
        }
        return nextPos;
    }

}
