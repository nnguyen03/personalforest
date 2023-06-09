import processing.core.PImage;

import java.util.List;

public final class Stump implements Entity {
    private final String id;
    private Point position;
    private final List<PImage> images;
    private final int imageIndex;

    public Stump(String id, Point position, List<PImage> images){
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
    }

    public Point getPosition(){
        return position;
    }

    public String getId(){
        return id;
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
