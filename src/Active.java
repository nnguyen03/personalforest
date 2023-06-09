public interface Active extends Animated{

    double getActionPeriod();

    void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler);

    default void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore){
        scheduler.scheduleEvent(this, Factory.createActivityAction(this, world, imageStore), this.getActionPeriod());
        Animated.super.scheduleActions(scheduler, world, imageStore);
    }


}
