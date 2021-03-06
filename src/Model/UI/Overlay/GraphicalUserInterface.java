package Model.UI.Overlay;

import Control.ProjectUnknownProperties;
import Model.Physics.Entity.Player;
import View.DrawingPanel;
import org.jetbrains.annotations.NotNull;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Created by Oussama on 08.01.2017.
 */
public class GraphicalUserInterface extends DrawingPanel {

    //ATTRIBUTE
    private static final double WIDTH = screenWidth * 0.7;
    private static final double HEIGHT = screenHeight * 0.15;
    private static final double X_POS = screenWidth / 2 - WIDTH / 2;
    private static final double Y_POS = screenHeight * 0.8;
    private static final Color BACKGROUND_BAR = new Color(63, 17, 33);
    private static final Color MANA_COLOR = new Color(71, 102, 227);
    private static final Color HEALTH_COLOR = new Color(68, 153, 30);
    private static final Color BACKGROUND_COLOR = new Color(31, 11, 15);
    private static final Color EXPERIENCE_COLOR = new Color(255, 127, 36);
    private Player player;
    private RoundRectangle2D.Double mainBoard;

    public GraphicalUserInterface(Player player, ProjectUnknownProperties properties) {
        super(properties);
        this.player = player;
        mainBoard = new RoundRectangle2D.Double(X_POS, Y_POS, WIDTH, HEIGHT + screenHeight * 0.03, HEIGHT / 2, HEIGHT / 2);
    }

    @Override
    public void paintComponent(@NotNull Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        //MAIN BOARD
        double halfWidth = screenWidth / 2;
        double widthCircle = screenHeight * 0.1;
        double xOval = halfWidth - widthCircle / 2;
        double yOval = Y_POS - widthCircle * 0.7;
        g.setColor(BACKGROUND_COLOR);
        g2d.fillOval((int) xOval, (int) yOval, (int) widthCircle, (int) widthCircle);
        g.setColor(BACKGROUND_COLOR);
        g2d.fill(mainBoard);

        //LEVEL NUMBER
        g.setColor(Color.WHITE);
        String level = String.valueOf(player.getLevel());
        g2d.setFont(properties.getGameFont().deriveFont(30f));
        FontMetrics metrics = g2d.getFontMetrics();
        int stringWidthLevel = metrics.stringWidth(level) / 2;
        int stringHeightLevel = metrics.getHeight() / 2;
        g2d.drawString(level, (int) (halfWidth - stringWidthLevel), (int) (yOval + widthCircle / 2 - stringHeightLevel));

        //MANA BACKGROUND
        g.setColor(BACKGROUND_BAR);
        double widthMana = WIDTH * 0.8;
        double heightMana = HEIGHT * 0.3;
        double xMana = halfWidth - widthMana / 2;
        double yMana = Y_POS + HEIGHT * 0.6;
        RoundRectangle2D rect = new RoundRectangle2D.Double(xMana, yMana, widthMana, heightMana, heightMana, heightMana);
        g2d.fill(rect);

        //MANA
        g.setColor(MANA_COLOR);
        widthMana = widthMana * player.getManaInPercent();
        xMana = halfWidth - widthMana / 2;
        rect = new RoundRectangle2D.Double(xMana, yMana, widthMana, heightMana, heightMana, heightMana);
        g2d.fill(rect);

        //MANA TEXT
        g.setColor(Color.WHITE);
        String mana = "MANA";
        g2d.setFont(properties.getGameFont().deriveFont(20f));
        metrics = g2d.getFontMetrics();
        int stringWidthMana = metrics.stringWidth(mana) / 2;
        int stringHeightMana = metrics.getHeight() / 2;
        g2d.drawString(mana, (int) (halfWidth - stringWidthMana), (int) (yMana + heightMana - stringHeightMana));

        //HEALTH BACKGROUND
        g.setColor(BACKGROUND_BAR);
        double widthHealth = WIDTH * 0.8;
        double heightHealth = HEIGHT * 0.4;
        double xHealth = halfWidth - widthHealth / 2;
        double yHealth = Y_POS + HEIGHT * 0.1;
        rect = new RoundRectangle2D.Double(xHealth, yHealth, widthHealth, heightHealth, heightHealth, heightHealth);
        g2d.fill(rect);

        //HEALTH
        g.setColor(HEALTH_COLOR);
        widthHealth = widthHealth * player.getHealthInPercent();
        xHealth = halfWidth - widthHealth / 2;
        rect = new RoundRectangle2D.Double(xHealth, yHealth, widthHealth, heightHealth, heightHealth, heightHealth);
        g2d.fill(rect);

        //HEALTH TEXT
        g.setColor(Color.WHITE);
        String health = "HEALTH";
        g2d.setFont(properties.getGameFont().deriveFont(20f));
        metrics = g2d.getFontMetrics();
        int stringWidthHealth = metrics.stringWidth(health) / 2;
        int stringHeightHealth = metrics.getHeight() / 2;
        g2d.drawString(health, (int) (halfWidth - stringWidthHealth), (int) (yHealth + heightHealth / 2 + stringHeightHealth));

        //EXPERIENCE BACKGROUND
        g.setColor(BACKGROUND_BAR);
        double widthExperience = WIDTH * 0.6;
        double heightExperience = HEIGHT * 0.2;
        double xExperience = halfWidth - widthExperience / 2;
        double yExperience = Y_POS + HEIGHT * 0.95;
        rect = new RoundRectangle2D.Double(xExperience, yExperience, widthExperience, heightExperience, heightExperience, heightExperience);
        g2d.fill(rect);

        //EXPERIENCE
        g.setColor(EXPERIENCE_COLOR);
        widthExperience = widthHealth * player.getExperienceInPercent();
        xExperience = halfWidth - widthExperience / 2;
        rect = new RoundRectangle2D.Double(xExperience, yExperience, widthExperience, heightExperience, heightExperience, heightExperience);
        g2d.fill(rect);

        //EXPERIENCE TEXT
        g.setColor(Color.WHITE);
        String experience = "EXP";
        g2d.setFont(properties.getGameFont().deriveFont(20f));
        metrics = g2d.getFontMetrics();
        int stringWidthExperience = metrics.stringWidth(experience) / 2;
        int stringHeightExperience = metrics.getHeight() / 2;
        g2d.drawString(experience, (int) (halfWidth - stringWidthExperience), (int) (yExperience + heightExperience / 2 + stringHeightExperience));

        //CURRENT MANA CAST
        g2d.drawImage(player.getCurrentCast().getImage(), (int) (X_POS + WIDTH - HEIGHT / 2 - 10), (int) (Y_POS + HEIGHT / 2 - 10), null);
    }
}