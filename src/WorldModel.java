import processing.core.PImage;

import java.util.*;
import java.util.function.Predicate;

/**
 * Represents the 2D World in which this simulation is running.
 * Keeps track of the size of the world, the background image for each
 * location in the world, and the entities that populate the world.
 */
public final class WorldModel {
    private int numRows;
    private int numCols;
    private Background[][] background;
    private Entity[][] occupancy;
    private Set<Entity> entities;

    public WorldModel() {

    }

    private static Optional<Entity> nearestEntity(List<Entity> entities, Point pos) {
        if (entities.isEmpty()) {
            return Optional.empty();
        } else {
            Entity nearest = entities.get(0);
            int nearestDistance = nearest.getPosition().distanceSquared(pos);

            for (Entity other : entities) {
                int otherDistance = other.getPosition().distanceSquared(pos);

                if (otherDistance < nearestDistance) {
                    nearest = other;
                    nearestDistance = otherDistance;
                }
            }

            return Optional.of(nearest);
        }
    }

    public void parseBackgroundRow(String line, int row, ImageStore imageStore) {
        String[] cells = line.split(" ");
        if(row < this.numRows){
            int rows = Math.min(cells.length, this.numCols);
            for (int col = 0; col < rows; col++){
                this.background[row][col] = new Background(cells[col], imageStore.getImageList(cells[col]));
            }
        }
    }

    /*
           Assumes that there is no entity currently occupying the
           intended destination cell.
        */
    public void addEntity(Entity entity) {
        if (withinBounds(entity.getPosition())) {
            setOccupancyCell(entity.getPosition(), entity);
            this.entities.add(entity);
        }
    }

    public void tryAddEntity(Entity entity) {
        try{
            if (isOccupied(entity.getPosition())) {
                // arguably the wrong type of exception, but we are not
                // defining our own exceptions yet
                throw new IllegalArgumentException("position occupied");
            }
            this.addEntity(entity);
        }
        catch (IllegalArgumentException e){
            System.out.println("position occupied");
        }
    }

    public boolean isOccupied(Point pos) {
        return withinBounds(pos) && getOccupancyCell(pos) != null;
    }

    public Optional<Entity> findNearest(Point pos, List<Class> kinds) {
        List<Entity> ofType = new LinkedList<>();
        for (Class kind : kinds) {
            for (Entity entity : this.entities) {
                if (entity.getClass() == kind) {
                    ofType.add(entity);
                }
            }
        }

        return nearestEntity(ofType, pos);
    }

    public Optional<Entity> findNearestTrishaGuha(Point pos, Predicate<Entity> include) {
        List<Entity> ofType = new LinkedList<>();
        for (Entity entity : this.entities) {
            if (include.test(entity)) {
                ofType.add(entity);
            }
        }
        return nearestEntity(ofType, pos);
    }

    public List<Entity> inProximity(Point pos, List<Class> kinds, int range){
        List<Entity> entitiesInRange = new LinkedList<>();
        for (Class kind : kinds) {
            for (Entity entity : this.entities) {
                if (entity.getClass() == kind && (pos.distanceManhattan(entity.getPosition()) < range)) {
                    entitiesInRange.add(entity);
                }
            }
        }
        return entitiesInRange;
    }


    private void removeEntityAt(Point pos) {
        if (withinBounds(pos) && getOccupancyCell(pos) != null) {
            Entity entity = getOccupancyCell(pos);

            /* This moves the entity just outside of the grid for
             * debugging purposes. */
            entity.setPosition(new Point(-1, -1));
            this.entities.remove(entity);
            setOccupancyCell(pos, null);
        }
    }

    public void removeEntity(EventScheduler scheduler, Entity entity) {
        scheduler.unscheduleAllEvents(entity);
        this.removeEntityAt(entity.getPosition());
    }

    private Background getBackgroundCell(Point pos) {
        return this.background[pos.getY()][pos.getX()];
    }

    public Optional<PImage> getBackgroundImage(Point pos) {
        if (withinBounds(pos)) {
            return Optional.of(this.getBackgroundCell(pos).getCurrentImage());
        } else {
            return Optional.empty();
        }
    }

    public void setBackgroundCell(Point pos, Background background) {
        this.background[pos.getY()][pos.getX()] = background;
    }

    public Optional<Entity> getOccupant(Point pos) {
        if (this.isOccupied(pos)) {
            return Optional.of(getOccupancyCell(pos));
        } else {
            return Optional.empty();
        }
    }

    public Entity getOccupancyCell(Point pos) {
        return this.occupancy[pos.getY()][pos.getX()];
    }

    private void setOccupancyCell(Point pos, Entity entity) {
        this.occupancy[pos.getY()][pos.getX()] = entity;
    }

    public void moveEntity(EventScheduler scheduler, Entity entity, Point pos) {
        Point oldPos = entity.getPosition();
        if (withinBounds(pos) && !pos.equals(oldPos)) {
            this.setOccupancyCell(oldPos, null);
            Optional<Entity> occupant = this.getOccupant(pos);
            occupant.ifPresent(target -> this.removeEntity(scheduler, target));
            this.setOccupancyCell(pos, entity);
            entity.setPosition(pos);
        }
    }

    public boolean withinBounds(Point pos) {
        return pos.getY() >= 0 && pos.getY() < numRows && pos.getX() >= 0 && pos.getX() < numCols;
    }

    public void load(Scanner saveFile, ImageStore imageStore, Background defaultBackground){
        FileParser.parseSaveFile(this, saveFile, imageStore, defaultBackground);
        if(background == null){
            background = new Background[numRows][numCols];
            for (Background[] row : background)
                Arrays.fill(row, defaultBackground);
        }
        if(occupancy == null){
            occupancy = new Entity[numRows][numCols];
            entities = new HashSet<>();
        }
    }

    /**
     * Helper method for testing. Don't move or modify this method.
     */
    public List<String> log(){
        List<String> list = new ArrayList<>();
        for (Entity entity : entities) {
            String log = entity.log();
            if(log != null) list.add(log);
        }
        return list;
    }

    public int getNumRows() {
        return numRows;
    }

    public void setNumRows(int numRows) {
        this.numRows = numRows;
    }

    public int getNumCols() {
        return numCols;
    }

    public void setNumCols(int numCols) {
        this.numCols = numCols;
    }

    public Background[][] getBackground() {
        return background;
    }

    public void setBackground(Background[][] background) {
        this.background = background;
    }

    public Entity[][] getOccupancy() {
        return occupancy;
    }

    public void setOccupancy(Entity[][] occupancy) {
        this.occupancy = occupancy;
    }

    public Set<Entity> getEntities() {
        return entities;
    }

    public void setEntities(Set<Entity> entities) {
        this.entities = entities;
    }
}
