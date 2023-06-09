public interface Plant extends Active {
    void setHealth(int health);

    int getHealth();

    default void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler){
        _plantActivity(this);
        if (!this.transformPlant(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, Factory.createActivityAction(this, world, imageStore), this.getActionPeriod());
        }
    }

    void _plantActivity(Plant plant);

    default boolean transformPlant(WorldModel world, EventScheduler scheduler, ImageStore imageStore){
        if (this.getHealth() <= 0) {
            Entity stump = Factory.createStump(FileParser.STUMP_KEY + "_" + this.getId(), this.getPosition(), imageStore.getImageList(FileParser.STUMP_KEY));

            world.removeEntity(scheduler, this);

            world.addEntity(stump);

            return true;
        }
        _transformPlant(world, scheduler, imageStore);
        return false;
    }

    boolean _transformPlant(WorldModel world, EventScheduler scheduler, ImageStore imageStore);
}
