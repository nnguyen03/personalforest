import processing.core.PImage;

import java.util.List;

public class Factory {
    private static final double SAPLING_ACTION_ANIMATION_PERIOD = 1.000; // have to be in sync since grows and gains health at same time
    private static final int SAPLING_HEALTH_LIMIT = 5;


    public static Entity createGod(String id, Point position, double actionPeriod, double animationPeriod, List<PImage> images){
        return new God(id, position, images, actionPeriod, animationPeriod);
    }

    public static Entity createUnDude(String id, Point position, double actionPeriod, double animationPeriod, List<PImage> images){
        return new UnDude(id, position, images, actionPeriod, animationPeriod);
    }

    public static Entity createGrim(String id, Point position, double actionPeriod, double animationPeriod, List<PImage> images){
        return new Grim(id, position, images, actionPeriod, animationPeriod);
    }

    public static Entity createHouse(String id, Point position, List<PImage> images) {
        return new House(id, position, images);
    }

    public static Entity createObstacle(String id, Point position, double animationPeriod, List<PImage> images) {
        return new Obstacle(id, position, images, animationPeriod);
    }

    public static Entity createTree(String id, Point position, double actionPeriod, double animationPeriod, int health, List<PImage> images) {
        return new Tree(id, position, images, actionPeriod, animationPeriod, health);
    }

    public static Entity createStump(String id, Point position, List<PImage> images) {
        return new Stump(id, position, images);
    }

    // health starts at 0 and builds up until ready to convert to Tree
    public static Entity createSapling(String id, Point position, List<PImage> images, int health) {
        return new Sapling(id, position, images, SAPLING_ACTION_ANIMATION_PERIOD, SAPLING_ACTION_ANIMATION_PERIOD, 0, SAPLING_HEALTH_LIMIT);
    }

    public static Entity createFairy(String id, Point position, double actionPeriod, double animationPeriod, List<PImage> images) {
        return new Fairy(id, position, images, actionPeriod, animationPeriod);
    }

    // need resource count, though it always starts at 0
    public static Entity createDudeNotFull(String id, Point position, double actionPeriod, double animationPeriod, int resourceLimit, List<PImage> images) {
        return new DudeNotFull(id, position, images, resourceLimit, 0, actionPeriod, animationPeriod);
    }

    // don't technically need resource count ... full
    public static Entity createDudeFull(String id, Point position, double actionPeriod, double animationPeriod, int resourceLimit, List<PImage> images) {
        return new DudeFull(id, position, images, resourceLimit, actionPeriod, animationPeriod);
    }

    public static Action createAnimationAction(Animated entity, int repeatCount) {
        return new Animation(entity, repeatCount);
    }

    public static Action createActivityAction(Active entity, WorldModel world, ImageStore imageStore) {
        return new Activity(entity, world, imageStore);
    }
}
