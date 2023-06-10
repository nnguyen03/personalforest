import processing.core.PImage;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class Grim implements Moving {
    private final String id;
    private Point position;
    private final List<PImage> images;
    private int imageIndex;
    private final double actionPeriod;
    private final double animationPeriod;

    public Grim(String id, Point position, List<PImage> images, double actionPeriod, double animationPeriod) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
    }
//TODO: Path Reaper to Unvisited House. Potential issue: findNearest requires List<Class>
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> target = world.findNearest(this.position, new ArrayList<>(List.of(House.class)));
        if (target.isPresent()) {
            House house = (House) target.get();
            if (!house.hasUndude()) {
                Point tgtPos = house.getPosition();
                if (this.moveTo(world, target.get(), scheduler)) {
                    Entity unDude = Factory.createUnDude(FileParser.UNDUDE_KEY, tgtPos, 0.8, 0.180, imageStore.getImageList(FileParser.UNDUDE_KEY));
                    world.addEntity(unDude);
                    ((Active)unDude).scheduleActions(scheduler, world, imageStore);
                    house.setUndude(true);
                }
            }
        }
        scheduler.scheduleEvent(this, Factory.createActivityAction(this, world, imageStore), this.actionPeriod);
    }

    public Point nextPosition(WorldModel world, Point destPos) {
        Predicate<Point> canPassThrough = (p) -> world.withinBounds(p) &&
                (!world.isOccupied(p)
                        || (world.getOccupancyCell(p) instanceof Stump)
                        || (world.getOccupancyCell(p) instanceof Tree)
                        || (world.getOccupancyCell(p) instanceof Sapling));
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

    public void _moveToHelper(WorldModel world, Entity target, EventScheduler scheduler){
        world.removeEntity(scheduler, target);
        scheduler.unscheduleAllEvents(target);
    }
    public Point getPosition(){
        return position;
    }

    public String getId(){
        return id;
    }

    public double getAnimationPeriod(){
        return animationPeriod;
    }

    public double getActionPeriod(){
        return actionPeriod;
    }

    public void nextImage() {
        this.imageIndex = this.imageIndex + 1;
    }

    public void setPosition(Point position){
        this.position = position;
    }

    public int getImageIndex(){
        return imageIndex;
    }

    public PImage getCurrentImage() {
        return this.images.get(imageIndex % images.size());
    }
}

