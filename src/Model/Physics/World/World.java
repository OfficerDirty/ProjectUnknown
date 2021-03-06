package Model.Physics.World;

import Control.ProjectUnknownProperties;
import Model.Abstraction.IDrawableObject;
import Model.Notification;
import Model.Parser.WorldExtensionParser;
import Model.Physics.Block.*;
import Model.Physics.Entity.Mobs.Enemy;
import Model.Physics.Entity.Player;
import Model.Planet;
import Model.UI.Overlay.GraphicalUserInterface;
import View.DrawingPanel;
import View.StaticDrawingPanel;
import com.Physics2D.PhysicsObject;
import com.Physics2D.event.MovementEvent;
import com.SideScroller.SideScrollingPhysicsWorld;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created and edited by Oussama and Patrick on 03.01.2017.
 */
public class World extends SideScrollingPhysicsWorld {

    public static final int PIXEL_TO_METER = 50;

    private Player player;
    private Point spawnPoint;

    private List<AbstractBlock> identifiableBlocks;

    private GraphicalUserInterface gui;

    private LevelRenderer renderer;

    private ProjectUnknownProperties properties;

    /**
     * constructs a new world object dependent on planet and the world file
     *
     * @param path       world file
     * @param properties
     * @param p          selected Planet
     */
    public World(@NotNull Path path, @NotNull ProjectUnknownProperties properties, @NotNull Planet p) {
        super(p.getGravity() * PIXEL_TO_METER);

        this.properties = properties;
        this.renderer = new LevelRenderer(properties);
        this.player = new Player(properties);
        this.gui = new GraphicalUserInterface(player, properties);
        this.identifiableBlocks = new ArrayList<>();

        try {
            createWorld(Files.readAllLines(path));
        } catch (IOException e) {
            ProjectUnknownProperties.raiseException(e);
        }

        player.setPosition(spawnPoint.getX(), spawnPoint.getY());

        focusWithoutScrolling(player);
        addObject(player);

        properties.getFrame().setForegroundPanel(gui);

    }

    public void showNotification(Notification notification) {
        properties.getNotificationArea().addNotification(notification);
    }

    @NotNull
    public AbstractBlock getBlockById(String id) {
        return identifiableBlocks.stream()
                .filter((block) -> block.getId().equals(id))
                .findFirst()
                .get();
    }

    @Override
    public void addObject(PhysicsObject o) {
        super.addObject(o);
        if (o instanceof IDrawableObject) {
            IDrawableObject drawableObject = (IDrawableObject) o;
            renderer.scheduleAddObject(drawableObject);
        }
        if (o instanceof AbstractBlock) {
            if (((AbstractBlock) o).getId() != null) {
                identifiableBlocks.add((AbstractBlock) o);
            }
        }
        o.addMovementListener(this);
    }

    @Override
    public void removeObject(PhysicsObject o) {
        if (o instanceof IDrawableObject) {
            IDrawableObject drawableObject = (IDrawableObject) o;
            renderer.scheduleRemoveObject(drawableObject);
        }
        if (o instanceof AbstractBlock) {
            if (((AbstractBlock) o).getId() != null) {
                identifiableBlocks.remove(o);
            }
        }
        o.removeMovementListener(this);
        SwingUtilities.invokeLater(() -> super.removeObject(o));
    }

    @Override
    public void onMovement(MovementEvent event) {
        super.onMovement(event);
        renderer.forceRepaint();
    }

    /**
     * interpret world file and creates the world
     *
     * @param lines
     */
    private void createWorld(@NotNull List<String> lines) {
        Teleporter tempTeleporter = null;
        for (String line : lines) {
            if (line.equals("stardust .world extension")) {
                //Lets break out of this code and skip into the dedicated parser
                WorldExtensionParser.parse(lines.subList(lines.indexOf(line) + 1, lines.size()), this);
                break;
            } else {
                String[] values = line.split(" ");
                if (tempTeleporter == null) {
                    switch (values[0]) {
                        case "BLOCK":
                            BlockType blockType = BlockType.valueOf(values[1]);
                            int x = Integer.parseInt(values[2]);
                            int y = Integer.parseInt(values[3]);
                            addObject(new InconsistentStateBlock(x, y, blockType, ""));
                            break;
                        case "PLAYER":
                            x = Integer.parseInt(values[1]);
                            y = Integer.parseInt(values[2]);
                            spawnPoint = new Point(x, y);
                            break;
                        case "TP1":
                            x = Integer.parseInt(values[2]);
                            y = Integer.parseInt(values[3]);
                            tempTeleporter = new Teleporter(x, y, "");
                            addObject(tempTeleporter);
                            break;
                        case "SPAWN":
                            Enemy.Type enemyType = Enemy.Type.valueOf(values[1]);
                            blockType = BlockType.valueOf(values[2]);
                            x = Integer.parseInt(values[3]);
                            y = Integer.parseInt(values[4]);
                            addObject(new SpawnBlock(x, y, blockType, enemyType));
                            break;
                    }
                } else {
                    if (values[0].equals("TP2")) {
                        int x = Integer.parseInt(values[2]);
                        int y = Integer.parseInt(values[3]);
                        Teleporter temp = new Teleporter(x, y, "");
                        temp.link(tempTeleporter);
                        tempTeleporter.link(temp);
                        addObject(temp);
                        tempTeleporter = null;
                    }
                }
            }
        }
    }

    public Player getPlayer() {
        return player;
    }

    public LevelRenderer getRenderer() {
        return renderer;
    }

    private class LevelRenderer extends StaticDrawingPanel {

        public LevelRenderer(ProjectUnknownProperties properties) {
            super(properties);

            setFocusYOffset(screenHeight / 2 - 50);
            setFocusXOffset(screenWidth / 2 - 10);
        }

        @NotNull
        @Override
        protected Point getRenderingOffset() {
            return new Point(-getRendererXOffset(), -getRendererYOffset());
        }

        @Override
        public void keyPressed(@NotNull KeyEvent event) {
            super.keyPressed(event);
            if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
                properties.getFrame().setContentPanel(properties.getFrame().getLevelSelect());
                properties.getFrame().setForegroundPanel(new DrawingPanel(properties));
            }
        }
    }

}
