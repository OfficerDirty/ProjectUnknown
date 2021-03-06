package Model.UI.Screen;

import Control.ProjectUnknownProperties;
import Model.Physics.Block.*;
import Model.Physics.Entity.Mobs.Enemy;
import View.DrawingPanel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Oussama on 03.01.2017.
 */
public class WorldEditor extends DrawingPanel implements KeyListener, MouseListener {
    private int camX;
    private int camY;
    private int realX;
    private int realY;
    private int indexOfBlockType;

    private Point spawnPoint;
    @Nullable
    private Point pos1;
    @Nullable
    private Point pos2;

    private List<Block> blocks;
    private List<String> extensionLines;

    /**
     * constructs a drawing panel which contains a grid. You can load, save and edit every .world-data.
     *
     * @param properties
     */
    public WorldEditor(ProjectUnknownProperties properties) {
        super(properties);
        camX = 0;
        camY = 0;
        realX = 0;
        realY = 0;
        indexOfBlockType = 0;
        spawnPoint = new Point(0, 0);
        blocks = new ArrayList<>();
        extensionLines = new ArrayList<>();

        this.addMouseListener(this);
    }

    /**
     * work in process... because the code to load the world in WorldEditor and World is pretty similar
     *
     * @param lines
     * @return
     */
    @NotNull
    public static ArrayList<Block> getBlocksFromLines(@NotNull List<String> lines) {

        ArrayList<Block> blocks = new ArrayList<>();
        Teleporter currentTpBlock = null;
        for (String line : lines) {
            String[] values = line.split(" ");
            switch (values[0]) {
                case "BLOCK":
                    BlockType blockType = BlockType.valueOf(values[1]);
                    int x = Integer.parseInt(values[2]);
                    int y = Integer.parseInt(values[3]);
                    Block temp = new Block(x, y, blockType, "");
                    blocks.add(temp);
                    break;
                case "TP1":
                    blockType = BlockType.valueOf(values[1]);
                    x = Integer.parseInt(values[2]);
                    y = Integer.parseInt(values[3]);
                    temp = new Teleporter(x, y, "");
                    blocks.add(temp);
                    currentTpBlock = (Teleporter) temp;
                    break;

                case "TP2":
                    blockType = BlockType.valueOf(values[1]);
                    x = Integer.parseInt(values[2]);
                    y = Integer.parseInt(values[3]);
                    Teleporter tempTp = new Teleporter(x, y, "");
                    tempTp.link(currentTpBlock);
                    currentTpBlock.link(tempTp);
                    blocks.add(tempTp);
                    currentTpBlock = null;
                    break;
            }
        }
        return blocks;
    }

    /**
     * draws grid and current world on it
     *
     * @param g
     */
    @Override
    public void paintComponent(@NotNull Graphics g) {
        g.setColor(Color.white);
        int x = (((realX + MouseInfo.getPointerInfo().getLocation().x) / 50)) * 50;
        int y = (((realY + MouseInfo.getPointerInfo().getLocation().y) / 50)) * 50;
        g.drawString(x + "/" + y, (int) (screenWidth * 0.9), (int) (screenHeight * 0.05));

        for (int i = 0; i < screenHeight / 50 + 1; i++) {
            //g.drawLine(realX , realY+i*50 , realX+screenWidth, realY+i*50);
            g.drawLine(0, i * 50, screenWidth, i * 50);
        }
        for (int i = 0; i < screenWidth / 50 + 1; i++) {
            //g.drawLine(realX+i*50 , realY , realX+i*50 ,realY+screenHeight);
            g.drawLine(i * 50, 0, i * 50, screenHeight);
        }
        //Everything before this should be drawn at absolute positions, everything after should be translated
        super.paintComponent(g);
        g.drawString("P", (int) spawnPoint.getX() + 25, (int) spawnPoint.getY() + 25);

        if (pos1 != null) {
            g.setColor(Color.BLUE);
            g.drawRect((int) pos1.getX(), (int) pos1.getY(), 50, 50);
        }
        if (pos2 != null) {
            g.setColor(Color.BLUE);
            g.drawRect((int) pos2.getX(), (int) pos2.getY(), 50, 50);
        }
    }

    @NotNull
    @Override
    public Point getRenderingOffset() {
        return new Point(-camX, -camY);
    }

    /**
     * with arrows you can move free through the current world
     * with space you can switch your current block
     * with 'd' you can remove a block on the current mouse position
     * with 's' you can save your world in a .world document and give it a name
     * with 'a' you can place a block on the current mouse position
     * with 'l' you can load a world from the Worlds folder (type in the name)
     * with 'p' you can set the spawn point of your player
     * with '1'&'2' you can mark two points which are getting filled with the current block by pressing 'f'
     * with 't' you can place a tp block, but you have to place two ot them
     *
     * @param e
     */
    @Override
    public void keyPressed(@NotNull KeyEvent e) {
        int x = (((realX + MouseInfo.getPointerInfo().getLocation().x) / 50)) * 50;
        int y = (((realY + MouseInfo.getPointerInfo().getLocation().y) / 50)) * 50;
        int k = e.getKeyCode();
        if (k == KeyEvent.VK_LEFT) {
            camX = camX - 50;
            realX = realX - 50;
        }
        if (k == KeyEvent.VK_RIGHT) {
            camX = camX + 50;
            realX = realX + 50;
        }
        if (k == KeyEvent.VK_UP) {
            camY = camY - 50;
            realY = realY - 50;
        }
        if (k == KeyEvent.VK_DOWN) {
            camY = camY + 50;
            realY = realY + 50;
        }

        if (k == KeyEvent.VK_SPACE) {
            indexOfBlockType++;
            if (indexOfBlockType >= BlockType.values().length) {
                indexOfBlockType = 0;
            }
        }
        if (e.getKeyChar() == 'o'){

            createSpawnBlock(x,y);
        }

        if (e.getKeyChar() == 'd') {
            removeBlock(x, y);
        }
        if (e.getKeyChar() == 's') {
            String file = JOptionPane.showInputDialog(properties.getFrame(), "How do you want to name your world?");
            if (file != null) {
                saveWorld(file);
            }
        }
        if (e.getKeyChar() == 'a') {
            createBlock(x, y);
        }
        if (e.getKeyChar() == 'l') {
            String file = JOptionPane.showInputDialog(properties.getFrame(), "Which world do you want to load?");
            if (file != null) {
                loadWorld(file);
            }
        }
        if (e.getKeyChar() == 'p') {
            spawnPoint.move(x, y);
        }
        if (e.getKeyChar() == '1') {
            pos1 = new Point(x, y);
        }
        if (e.getKeyChar() == '2') {
            pos2 = new Point(x, y);
        }
        if (e.getKeyChar() == 'f') {
            fillSpace();
        }
        if (e.getKeyChar() == 't') {
            createTeleporter(x, y);
        }

    }

    private void createSpawnBlock(int x, int y) {
        System.out.println("bbbb");
        String details = JOptionPane.showInputDialog(properties.getFrame(), "Which entity should be spawned");
        Enemy.Type type = Enemy.Type.valueOf(details);
        SpawnBlock temp = new SpawnBlock(x,y, BlockType.values()[indexOfBlockType], type);
        blocks.add(temp);
        super.addObject(temp);
    }

    @Override
    public void mousePressed(@NotNull MouseEvent e) {
        int x = (((realX + MouseInfo.getPointerInfo().getLocation().x) / 50)) * 50;
        int y = (((realY + MouseInfo.getPointerInfo().getLocation().y) / 50)) * 50;

        if (e.getButton() == MouseEvent.BUTTON1) {

            createBlock(x, y);
        }
        if (e.getButton() == MouseEvent.BUTTON2) {
            indexOfBlockType++;
            if (indexOfBlockType >= BlockType.values().length) {
                indexOfBlockType = 0;
            }
        }
        if (e.getButton() == MouseEvent.BUTTON3) {
            removeBlock(x, y);
        }
    }

    /**
     * loads the world based on the given file name
     *
     * @param file - world name
     */
    private void loadWorld(String file) {
        try {
            List<String> lines = Files.readAllLines(Paths.get("Worlds/" + file + ".world"));
            for (Block block : blocks) {
                super.removeObject(block);
            }
            blocks = new ArrayList<>();
            Teleporter currentTpBlock = null;
            for (String line : lines) {
                if (line.equals("stardust .world extension")) {
                    extensionLines = lines.subList(lines.indexOf(line), lines.size());
                    break;
                }
                String[] values = line.split(" ");
                switch (values[0]) {
                    case "BLOCK":
                        BlockType blockType = BlockType.valueOf(values[1]);
                        int x = Integer.parseInt(values[2]);
                        int y = Integer.parseInt(values[3]);
                        Block temp = new Block(x, y, blockType, "");
                        blocks.add(temp);
                        super.addObject(temp);
                        break;

                    case "PLAYER":
                        int xS = Integer.parseInt(values[1]);
                        int yS = Integer.parseInt(values[2]);
                        spawnPoint.move(xS, yS);
                        break;

                    case "TP1":
                        blockType = BlockType.valueOf(values[1]);
                        x = Integer.parseInt(values[2]);
                        y = Integer.parseInt(values[3]);
                        temp = new Teleporter(x, y, "");
                        blocks.add(temp);
                        super.addObject(temp);
                        currentTpBlock = (Teleporter) temp;
                        break;

                    case "TP2":
                        blockType = BlockType.valueOf(values[1]);
                        x = Integer.parseInt(values[2]);
                        y = Integer.parseInt(values[3]);
                        Teleporter tempTp = new Teleporter(x, y, "");
                        tempTp.link(currentTpBlock);
                        currentTpBlock.link(tempTp);
                        blocks.add(tempTp);
                        super.addObject(tempTp);
                        currentTpBlock = null;
                        break;
                    case "SPAWN":
                        Enemy.Type enemyType = Enemy.Type.valueOf(values[1]);
                        blockType = BlockType.valueOf(values[2]);
                        x = Integer.parseInt(values[3]);
                        y = Integer.parseInt(values[4]);
                        SpawnBlock tempSpawnBlock = new SpawnBlock(x, y, blockType, enemyType);
                        blocks.add(tempSpawnBlock);
                        super.addObject(tempSpawnBlock);
                        break;


                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * creates a teleport block on the position and checks for existing block on this position
     *
     * @param x - x position
     * @param y - y position
     */
    private void createTeleporter(int x, int y) {
        boolean noBlock = true;
        for (Block block : blocks) {
            if (block.getX() == x && block.getY() == y) {
                noBlock = false;
            }
        }
        if (noBlock) {
            Teleporter teleporter = new Teleporter(x, y, "");
            if (blocks.get(blocks.size() - 1) instanceof Teleporter) {
                Teleporter temp = (Teleporter) blocks.get(blocks.size() - 1);
                teleporter.link(temp);
                temp.link(teleporter);
            }
            blocks.add(teleporter);
            super.addObject(teleporter);
        }

    }

    /**
     * creates a block with the current block type on the position and checks for existing block on this position
     *
     * @param x - x position
     * @param y - y position
     */
    private void createBlock(int x, int y) {
        if (blockDetected(x,y)) {
            Block temp = new Block(x, y, BlockType.values()[indexOfBlockType], "");
            blocks.add(temp);
            super.addObject(temp);
        }
    }

    /**
     * removes  block if existing
     *
     * @param x - x position
     * @param y - y position
     */
    private void removeBlock(int x, int y) {
        Block temp;
        for (Block block : blocks) {
            if (block.getX() == x && block.getY() == y) {
                temp = block;
                blocks.remove(temp);
                super.removeObject(temp);
                break;
            }
        }
    }

    /**
     * translates the current world in a world document and saves it with the given String
     *
     * @param text - name of world
     */
    private void saveWorld(String text) {
        try {
            PrintWriter writer = new PrintWriter(new File("Worlds/" + text + ".world"));
            for (int i = 0; i < blocks.size(); i++) {
                Block block = blocks.get(i);
                int x = (int) block.getX();
                int y = (int) block.getY();
                if (block instanceof Teleporter) {
                    writer.println("TP1 " + block.getBlockType().toString() + " " + x + " " + y);
                    i++;
                    block = blocks.get(i);
                    x = (int) block.getX();
                    y = (int) block.getY();
                    writer.println("TP2 " + block.getBlockType().toString() + " " + x + " " + y);
                } else if(block instanceof SpawnBlock) {
                    SpawnBlock spawnBlock = (SpawnBlock) block;
                    writer.println("SPAWN "+ spawnBlock.getEnemyType().toString()+" "+spawnBlock.getBlockType().toString()+" "+x+" "+y);
                }else{
                    writer.println("BLOCK " + block.getBlockType().toString() + " " + x + " " + y);
                }
            }
            writer.println("PLAYER " + (int) spawnPoint.getX() + " " + (int) spawnPoint.getY());
            for (String line : extensionLines) {
                writer.println(line);
            }
            writer.close();
            properties.getFrame().setContentPanel(properties.getFrame().getStart());
        } catch (IOException e) {

        }
    }

    /**
     * fill space between blue-marked position with current block type
     */
    private void fillSpace() {
        if (pos1 != null && pos2 != null) {
            int width = (int) (pos2.getX() - pos1.getX());
            int height = (int) (pos2.getY() - pos1.getY());

            for (int i = 0; i < 1 + (height / 50); i++) {
                for (int j = 0; j < 1 + (width / 50); j++) {
                    createBlock((int) pos1.getX() + j * 50, (int) pos1.getY() + i * 50);
                }
            }
            pos1 = null;
            pos2 = null;
        }
    }


    /**
     * checks if a block is on the location
     * @param x - x location
     * @param y - y location
     * @return returns true when there is no block
     */
    private boolean blockDetected(int x,int y){
        for (AbstractBlock block : blocks) {
            if (block.getX() == x && block.getY() == y) {
                return false;
            }
        }
        return true;
    }
}
