import processing.core.PImage;

import java.util.List;

public class Obstacle implements Animated {
    private final String id;
    private Point position;
    private final List<PImage> images;
    private final double animationPeriod;
    private int imageIndex;

    public Obstacle(String id, Point position, List<PImage> images, double animationPeriod){
        this.id = id;
        this.position = position;
        this.images = images;
        this.animationPeriod = animationPeriod;
        this.imageIndex = 0;
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
