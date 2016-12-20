package Model.Physics;

import Control.ProjectUnknownProperties;
import Model.Abstraction.IDrawableObject;
import View.DrawingPanel;
import View.StaticDrawingPanel;
import com.Physics2D.PhysicsObject;
import com.SideScroller.SideScrollingPhysicsWorld;

import java.awt.*;

public class Level extends SideScrollingPhysicsWorld {

    private LevelRenderer renderer;

    public Level(double gravitationalConstant, ProjectUnknownProperties properties) {
        super(gravitationalConstant);
        renderer = new LevelRenderer(properties);
    }

    @Override
    public void addObject(PhysicsObject o) {
        super.addObject(o);
        if(o instanceof IDrawableObject){
            IDrawableObject drawableObject = (IDrawableObject)o;
            renderer.addObject(drawableObject);
        }
        o.addMovementListener((event) -> renderer.forceRepaint());
    }

    public LevelRenderer getRenderer() {
        return renderer;
    }

    private class LevelRenderer extends StaticDrawingPanel {

        @Override
        protected Point getRenderingOffset(){
            return new Point(-getRendererXOffset(), -getRendererYOffset());
        }

        public LevelRenderer(ProjectUnknownProperties properties) {
            super(properties);
        }
    }
}
