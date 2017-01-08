package Model.Physics.World;

import Control.ProjectUnknownProperties;
import Model.Physics.Block.BlockType;
import Model.Physics.Block.SolidTerrainBlock;
import Model.Physics.Entity.Player;
import View.DrawingPanel;
import View.MainFrame;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by Amasso on 02.01.2017.
 */
public class World extends AbstractWorld{

    private Player player;
    private Point spawnPoint;
    private GraphicalUserInterface gui;

    public World(Path path, Player player, ProjectUnknownProperties projectUnknownProperties){
        super(20.00,projectUnknownProperties);
        try {
            List<String> lines = Files.readAllLines(path);
            createWorld(lines);
        } catch (IOException e) {
            System.out.println("Error: World doesn't exist...");
        }
        this.player = player;
        player.setX((int) spawnPoint.getX());
        player.setY((int) spawnPoint.getY());
        addObject(player);
        setFocusYOffset((int)(ProjectUnknownProperties.getScreenDimension().getHeight()/2)-50);
        setFocusXOffset((int)(ProjectUnknownProperties.getScreenDimension().getWidth()/2)-10);
        focusWithoutScrolling(player);
        gui = new GraphicalUserInterface(player, projectUnknownProperties);
        projectUnknownProperties.getFrame().setForegroundPanel(gui);
    }

    private void createWorld(List<String> lines) {
        for(String line: lines){
            String[] values = line.split(" ");
            switch (values[0]){
                case "BLOCK":
                    BlockType blockType = BlockType.valueOf(values[1]);
                    int x = Integer.parseInt(values[2]);
                    int y = Integer.parseInt(values[3]);
                    addObject(new SolidTerrainBlock(x,y,blockType));
                    break;
                case "PLAYER":
                    x = Integer.parseInt(values[1]);
                    y = Integer.parseInt(values[2]);
                    spawnPoint = new Point(x,y);
            }

        }
    }


}
