import processing.core.PImage;

/**
 * An entity that exists in the world. See EntityKind for the
 * different kinds of entities that exist.
 */
public interface Entity {

    // how am i supposed to put create and parse here

    Point getPosition();

    String getId();

    void setPosition(Point position);

    int getImageIndex();

    PImage getCurrentImage();

    default String log(){
        return this.getId().isEmpty() ? null :
                String.format("%s %d %d %d", this.getId(), this.getPosition().getX(), this.getPosition().getY(), this.getImageIndex());
    }


}
