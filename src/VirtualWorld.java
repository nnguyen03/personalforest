import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

import processing.core.*;

public final class VirtualWorld extends PApplet {
    private static String[] ARGS;
    private static final Random rand = new Random();
    public static final int VIEW_WIDTH = 640;
    public static final int VIEW_HEIGHT = 480;
    public static final int TILE_WIDTH = 32;
    public static final int TILE_HEIGHT = 32;

    public static final int VIEW_COLS = VIEW_WIDTH / TILE_WIDTH;
    public static final int VIEW_ROWS = VIEW_HEIGHT / TILE_HEIGHT;
    public static final String IMAGE_LIST_FILE_NAME = "imagelist";
    public static final String DEFAULT_IMAGE_NAME = "background_default";
    public static final int DEFAULT_IMAGE_COLOR = 0x808080;
    public static final String FAST_FLAG = "-fast";
    public static final String FASTER_FLAG = "-faster";
    public static final String FASTEST_FLAG = "-fastest";
    public static final double FAST_SCALE = 0.5;
    public static final double FASTER_SCALE = 0.25;
    public static final double FASTEST_SCALE = 0.10;

    public String loadFile = "world.sav";
    public long startTimeMillis = 0;
    public double timeScale = 1.0;

    public ImageStore imageStore;
    public WorldModel world;
    public WorldView view;
    public EventScheduler scheduler;

    private static boolean firstEventBool = false;
    private static boolean secondEventBool = false;

    public void settings() {
        size(VIEW_WIDTH, VIEW_HEIGHT);
    }
    /*
       Processing entry point for "sketch" setup.
    */
    public void setup() {
        parseCommandLine(ARGS);
        loadImages(IMAGE_LIST_FILE_NAME);
        loadWorld(loadFile, this.imageStore);

        this.view = new WorldView(VIEW_ROWS, VIEW_COLS, this, world, TILE_WIDTH, TILE_HEIGHT);
        this.scheduler = new EventScheduler();
        this.startTimeMillis = System.currentTimeMillis();
        this.scheduleActions(world, scheduler, imageStore);
    }

    public void draw() {
        double appTime = (System.currentTimeMillis() - startTimeMillis) * 0.001;
        double frameTime = (appTime - scheduler.getCurrentTime())/timeScale;
        this.update(frameTime);
        view.drawViewport();
    }

    public void update(double frameTime){
        scheduler.updateOnTime(frameTime);
    }

    // Just for debugging and for P5
    // Be sure to refactor this method as appropriate
    public void mousePressed() {
        //TODO! Figure out how we will trigger the two events...simultaneously or separately?

        // get location of mouse click
        Point pressed = mouseToPoint();


        if (!firstEventBool && !secondEventBool) {
            zombiesAndSurpriseAlsoGod(pressed);
        }
        else if (firstEventBool && !secondEventBool){
            grimEvent(pressed);
            secondEventBool = true;
        }

        firstEventBool = true;
    }

    private void grimEvent(Point pressed){
        // draw castles on screen in proximity to mouse click
        castleVisualization(pressed);
        Optional<Entity> tgtHouse = world.findNearest(pressed, new ArrayList<>(List.of(House.class)));

        if (tgtHouse.isPresent()) {
            Entity grimReaper = Factory.createGrim(FileParser.GRIM_KEY, pressed, 0.8, 0.180, imageStore.getImageList(FileParser.GRIM_KEY));
            world.addEntity(grimReaper);
            ((Active) grimReaper).scheduleActions(scheduler, world, imageStore);
        }

    }

    private void zombiesAndSurpriseAlsoGod (Point pressed){

        // draw tombstones on screen in proximity to mouse click
        tombstoneVisualization(pressed);
        // get list of dudes within some range of mouse click
        List<Entity> dudesNearby = world.inProximity(pressed, new ArrayList<>(Arrays.asList(DudeNotFull.class, DudeFull.class)), 10);
        // transform dudes in range into UnDudes
        theWalkingDude(dudesNearby);

        // spawn God
        Entity god = Factory.createGod(FileParser.GOD_KEY, pressed, 0.4, 0.180, imageStore.getImageList(FileParser.GOD_KEY));
        world.addEntity(god);
        ((Active) god).scheduleActions(scheduler, world, imageStore);
    }

    private void theWalkingDude(List<Entity> dudesNearby){
        if(dudesNearby.size() > 0) {
            for (Entity dude: dudesNearby){
                Point replaceDude = dude.getPosition();
                world.removeEntity(scheduler, dude);
                scheduler.unscheduleAllEvents(dude);
                Entity newUnDude = Factory.createUnDude(FileParser.UNDUDE_KEY, replaceDude, 0.8, 0.180, imageStore.getImageList(FileParser.UNDUDE_KEY));
                world.tryAddEntity(newUnDude);
                ((Active)newUnDude).scheduleActions(scheduler, world, imageStore);
            }
        }
    }

    private void tombstoneVisualization(Point pressed){
        int rand_tombsAndMausoleums = rand.nextInt(7, 14);
        int loop_counter = 20;
        while(rand_tombsAndMausoleums > 0){
            if(loop_counter == 0){
                break;
            }
            Background tombstone = new Background("tombstone", imageStore.getImageList("tombstone"));
            Point destPoint = new Point(pressed.x + rand.nextInt(4), pressed.y + rand.nextInt(4));
            Optional<PImage> image = this.world.getBackgroundImage(destPoint);

            // if background cell is already tombstone then rerun loop until tombstone can be placed elsewhere
            if(image.equals(imageStore.getImageList("tombstone"))){
                loop_counter--;
                continue;
            }

            world.setBackgroundCell(destPoint, tombstone);
            rand_tombsAndMausoleums--;
        }
    }
    private void castleVisualization(Point pressed){
        int randCastle = rand.nextInt(7, 14);
        int loop_counter = 20;
        while(randCastle > 0){
            if(loop_counter == 0){
                break;
            }
            Background castle = new Background("castle", imageStore.getImageList("castle"));
            Point destPoint = new Point(pressed.x + rand.nextInt(4), pressed.y + rand.nextInt(4));
            Optional<PImage> image = this.world.getBackgroundImage(destPoint);

            // if background cell is already tombstone then rerun loop until tombstone can be placed elsewhere
            if(image.equals(imageStore.getImageList("castle"))){
                loop_counter--;
                continue;
            }

            world.setBackgroundCell(destPoint, castle);
            randCastle--;
        }
    }

    public void scheduleActions(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        for (Entity entity : world.getEntities()) {
            if (entity instanceof Animated){
                ((Animated)entity).scheduleActions(scheduler, world, imageStore);
            }
        }
    }

    private Point mouseToPoint() {
        return view.getViewport().viewportToWorld(mouseX / TILE_WIDTH, mouseY / TILE_HEIGHT);
    }

    public void keyPressed() {
        if (key == CODED) {
            int dx = 0;
            int dy = 0;

            switch (keyCode) {
                case UP -> dy -= 1;
                case DOWN -> dy += 1;
                case LEFT -> dx -= 1;
                case RIGHT -> dx += 1;
            }
            view.shiftView(dx, dy);
        }
    }

    public static Background createDefaultBackground(ImageStore imageStore) {
        return new Background(DEFAULT_IMAGE_NAME, imageStore.getImageList(DEFAULT_IMAGE_NAME));
    }

    public static PImage createImageColored(int width, int height, int color) {
        PImage img = new PImage(width, height, RGB);
        img.loadPixels();
        Arrays.fill(img.pixels, color);
        img.updatePixels();
        return img;
    }

    public void loadImages(String filename) {
        this.imageStore = new ImageStore(createImageColored(TILE_WIDTH, TILE_HEIGHT, DEFAULT_IMAGE_COLOR));
        try {
            Scanner in = new Scanner(new File(filename));
            ImageLoader.loadImages(in, imageStore,this);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    public void loadWorld(String file, ImageStore imageStore) {
        this.world = new WorldModel();
        try {
            Scanner in = new Scanner(new File(file));
            world.load(in, imageStore, createDefaultBackground(imageStore));
        } catch (FileNotFoundException e) {
            Scanner in = new Scanner(file);
            world.load(in, imageStore, createDefaultBackground(imageStore));
        }
    }

    public void parseCommandLine(String[] args) {
        for (String arg : args) {
            switch (arg) {
                case FAST_FLAG -> timeScale = Math.min(FAST_SCALE, timeScale);
                case FASTER_FLAG -> timeScale = Math.min(FASTER_SCALE, timeScale);
                case FASTEST_FLAG -> timeScale = Math.min(FASTEST_SCALE, timeScale);
                default -> loadFile = arg;
            }
        }
    }

    public static void main(String[] args) {
        VirtualWorld.ARGS = args;
        PApplet.main(VirtualWorld.class);
    }

    public static List<String> headlessMain(String[] args, double lifetime){
        VirtualWorld.ARGS = args;

        VirtualWorld virtualWorld = new VirtualWorld();
        virtualWorld.setup();
        virtualWorld.update(lifetime);

        return virtualWorld.world.log();
    }
}
