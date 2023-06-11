import processing.core.PImage;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class God implements Moving {
    private final String id;
    private Point position;
    private final List<PImage> images;
    private int imageIndex;
    private final double actionPeriod;
    private final double animationPeriod;

    public God(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
    }

    @Override
    public double getActionPeriod() {
        return actionPeriod;
    }

    @Override
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {

    }

    @Override
    public double getAnimationPeriod() {
        return animationPeriod;
    }

    @Override
    public void nextImage() {
        this.imageIndex = this.imageIndex + 1;
    }

    @Override
    public Point getPosition() {
        return position;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setPosition(Point position) {
        this.position = position;
    }

    @Override
    public int getImageIndex() {
        return imageIndex;
    }

    @Override
    public PImage getCurrentImage() {
        return this.images.get(imageIndex % images.size());
    }

    @Override
    public Point nextPosition(WorldModel world, Point destPos) {
        Predicate<Point> canPassThrough = (p) -> world.withinBounds(p) &&
                (!world.isOccupied(p)
                        || (world.getOccupancyCell(p) instanceof Obstacle));
        //can pass through is lambda
        BiPredicate<Point, Point> withinReach = (p1, p2) -> p1.adjacent(p2);

        PathingStrategy strat = new AStarPathingStrategy();
        List<Point> path = strat.computePath(this.getPosition(), destPos, canPassThrough, withinReach,PathingStrategy.CARDINAL_NEIGHBORS);

        Point nextPos = this.getPosition();
        if (path.size() > 0){
            nextPos = path.get(0);
        }
        return nextPos;
    }

    @Override
    public void _moveToHelper(WorldModel world, Entity target, EventScheduler scheduler) {

    }
}
