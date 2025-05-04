import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * A *tiny* self‑contained “creatures in a park” demo.
 * Compile:  javac MiniWorld.java
 * Run:      java MiniWorld
 */
public class MiniWorld extends JPanel implements ActionListener, KeyListener {
    /* ===== Constants ===== */
    private static final int TILE_SIZE = 16;      // pixels per tile
    private static final int WORLD_WIDTH = 60;      // tiles
    private static final int WORLD_HEIGHT = 50;      // tiles
    private static final int FPS = 4;      // simulation steps/second
    private int currentBiome = 0;
    /**
     * 0 = GRASS, 1 == SAND, 2 == WATER
     **/
    private final Random random = new Random();


    /* ===== World state ===== */
    private final Tile[][] tiles = new Tile[WORLD_WIDTH][WORLD_HEIGHT];
    private final List<Creature> creatures = new ArrayList<>();
    private final Timer timer;


    public MiniWorld() {
        /* --- basic window set‑up --- */
        setPreferredSize(new Dimension(WORLD_WIDTH * TILE_SIZE,
                WORLD_HEIGHT * TILE_SIZE));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        /* generating a world with a certain Biome */
        generateVariableGrass();
        //generateWorldWithScarceSprites();

        /* --- drop one creature in the center --- */
        creatures.add(new Creature(WORLD_WIDTH / 2, WORLD_HEIGHT / 2));

        /* --- kick‑off the game loop --- */
        timer = new Timer(1000 / FPS, this);
        timer.start();
    }

    /* ====================  GAME LOOP  ==================== */
    @Override
    public void actionPerformed(ActionEvent e) {
        updateSimulation();
        repaint();
    }

    private void updateSimulation() {
        for (Creature c : creatures) c.update();
    }

    /* ====================  RENDERING  ==================== */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawWorld(g);
    }

    private void drawWorld(Graphics g) {
        /* draw background tiles */
        for (int x = 0; x < WORLD_WIDTH; x++)
            for (int y = 0; y < WORLD_HEIGHT; y++)
                tiles[x][y].draw(g, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE);

        /* draw every creature */
        for (Creature c : creatures) c.draw(g);
    }

    private void fillWorldWith(Tile t) {
        for (int x = 0; x < WORLD_WIDTH; x++) {
            for (int y = 0; y < WORLD_HEIGHT; y++) {
                tiles[x][y] = t;
            }
        }
    }
    private void generateVariableGrass() {
        Tile[] grassVariants = {Tile.GRASS_LIGHT, Tile.GRASS, Tile.GRASS_DARK};
        for (int x = 0; x < WORLD_WIDTH; x++) {
            for (int y = 0; y < WORLD_HEIGHT; y++) {
                int index = random.nextInt(grassVariants.length);
                tiles[x][y] = grassVariants[index];
            }
        }
    }

    /* testing our oak-tree, house, and chciken sprites*/
    private void generateWorldWithScarceSprites() {
        for (int x = 0; x < WORLD_WIDTH; x++) {
            for (int y = 0; y < WORLD_HEIGHT; y++) {
                double roll = random.nextDouble(); // [0.0,1.0)
                if (roll < 0.005) {
                    // 0.5% chance to be an oak tree
                    tiles[x][y] = Tile.OAK_TREE;
                } else if (roll < 0.008) {
                    // next 0.3% chance for a house
                    tiles[x][y] = Tile.HOUSE;
                } else if (roll < 0.010) {
                    // next 0.2% chance for a chicken
                    tiles[x][y] = Tile.CHICKEN;
                } else {
                    // otherwise, pick your normal biome tile
                    tiles[x][y] = pickDefaultBiomeTile();
                }
            }
        }
    }
    private Tile pickDefaultBiomeTile() {
        // e.g., just grass for now:
        return Tile.GRASS;
    }


    /* ====================  INPUT  ==================== */
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            // spawn another roaming creature at a random spot
            int rx = (int) (Math.random() * WORLD_WIDTH);
            int ry = (int) (Math.random() * WORLD_HEIGHT);
            creatures.add(new Creature(rx, ry));
        }
        switch (e.getKeyCode()) {
            case KeyEvent.VK_SPACE:
                // … existing spawn logic …
                break;
            case KeyEvent.VK_1:
                currentBiome = 0;
                fillWorldWith(Tile.GRASS);
                break;
            case KeyEvent.VK_2:
                currentBiome = 1;
                fillWorldWith(Tile.SAND);
                break;
            case KeyEvent.VK_3:
                currentBiome = 2;
                fillWorldWith(Tile.WATER);
                break;
        }


    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    /* ====================  ENTRY‑POINT  ==================== */
    public static void main(String[] args) {
        /** SwingUtilities.invokeLater(() -> {
         JFrame f = new JFrame("Mini World");
         f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
         f.setResizable(false);
         f.add(new MiniWorld());
         f.pack();
         f.setLocationRelativeTo(null);
         f.setVisible(true);
         });
         **/
        System.out.println("Hello World, I'm activating MiniWorld!");
    }

    /* ====================================================== */
    /* ====================  HELPER TYPES  ================== */

    /**
     * Very small, solid‑colour floor tiles.
     */
    private enum Tile {
        GRASS(new Color(34, 139, 34)),
        SAND(new Color(194, 178, 128)),
        WATER(new Color(28, 107, 160)),
        GRASS_LIGHT(new Color(50, 180, 50)),
        GRASS_DARK(new Color(20, 100, 20)),

        OAK_TREE("assets/tiles/Oak-Tree.png"),
        HOUSE("assets/tiles/House.png"),
        CHICKEN("assets/tiles/Chicken.png");

        private BufferedImage img = new BufferedImage(TILE_SIZE, TILE_SIZE, BufferedImage.TYPE_INT_ARGB);;

        Tile(String path) {
            // load the image once, when the enum constant is created
            BufferedImage tmp;
            try {
                tmp = ImageIO.read(new File(path));
            } catch (IOException e) {
                e.printStackTrace();
                // fall back to a 1×1 red pixel if load fails
                tmp = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
                tmp.setRGB(0, 0, Color.RED.getRGB());
            }
            img = tmp;
        }

        private Color c = new Color(255, 255, 255);

        Tile(Color c) {
            this.c = c;
        }


        void draw(Graphics g, int px, int py, int size) {
            g.setColor(c);
            g.fillRect(px, py, size, size);
        }
    }

    /**
     * A super‑simple randomly generated wandering creature.
     */
    private class Creature {
        int x, y;                    // tile coordinates
        final Color color = Color.WHITE;

        Creature(int x, int y) {
            this.x = x;
            this.y = y;
        }

        void update() {
            int dir = (int) (Math.random() * 4);
            switch (dir) {
                case 0:
                    if (x > 0) x--;
                    break; // left
                case 1:
                    if (x < WORLD_WIDTH - 1) x++;
                    break; // right
                case 2:
                    if (y > 0) y--;
                    break; // up
                case 3:
                    if (y < WORLD_HEIGHT - 1) y++;
                    break; // down
            }
        }

        void draw(Graphics g) {
            g.setColor(color);
            g.fillOval(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

        }
    }
}

