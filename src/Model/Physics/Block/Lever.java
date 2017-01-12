package Model.Physics.Block;

import Model.Abstraction.IDrawableObject;
import Model.Abstraction.IPlayerInteractable;
import com.Physics2D.Entity;

import java.awt.*;
import java.util.function.Consumer;

/**
 * Created by jardu on 1/12/2017.
 */
public class Lever extends DrawablePhysicsObject implements IPlayerInteractable, IDrawableObject{

    private Consumer<Boolean> onToggle;

    private boolean on;

    /**
     * Creates a new physics object with the given position and dimensions
     *
     * @param x      The x-position of the physics object
     * @param y      The y-position of the physics object
     * @param width  the width of the physics object
     * @param height the height of the physics object
     */
    public Lever(double x, double y, double width, double height, Consumer<Boolean> onToggle) {
        super(x, y, width, height);
        this.onToggle = onToggle;
    }
    @Override
    public void draw() {
        Graphics2D g = canvas.getPencil();
        g.setColor(Color.ORANGE);
        g.fill(getBounds());
    }

    @Override
    public void update(double dt) {

    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public double getFrictionConstant() {
        return 0;
    }

    @Override
    public void onInteractWith(Entity actor) {
        on = !on;
        onToggle.accept(on);
    }
}
