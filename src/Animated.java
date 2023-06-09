public interface Animated extends Entity{
    default void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore){
        scheduler.scheduleEvent(this, Factory.createAnimationAction(this, 0), this.getAnimationPeriod());
    }
    double getAnimationPeriod();

    void nextImage();
}
