package follower.concept.data;

import follower.concept.geometry.Direction;

public class CircleEntity {
    public float x, y, radius, rotation, speed;
    public Direction direction;

    public CircleEntity(float x, float y, float radius, float rotation, float speed) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.rotation = rotation;
        this.speed = speed;
        this.direction = Direction.E;
    }
}
