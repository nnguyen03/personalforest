import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public interface Moving extends Active{

    Point nextPosition(WorldModel world, Point destPos);

    default boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        if (this.getPosition().adjacent(target.getPosition())) {
            _moveToHelper(world, target, scheduler);
            return true;
        } else {
            Point nextPos = this.nextPosition(world, target.getPosition());

            if (!this.getPosition().equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }

    void _moveToHelper(WorldModel world, Entity target, EventScheduler scheduler);


}
