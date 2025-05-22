import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import java.util.Random;

class BlockGame2 {
    static class MyFrame extends JFrame {

        // constant
        static int BALL_WIDTH = 15; // 값 변경
        static int BALL_HEIGHT = 15; // 값 변경
        static int BLOCK_ROWS = 5;
        static int BLOCK_COLUMNS = 10;
        static int BLOCK_WIDTH = 40;
        static int BLOCK_HEIGHT = 20;
        static int BLOCK_GAP = 3;
        static int BAR_WIDTH = 80;
        static int BAR_HEIGHT = 20;
        static int CANVAS_WIDTH = 400 + (BLOCK_GAP * BLOCK_COLUMNS) - BLOCK_GAP;
        static int CANVAS_HEIGHT = 600;

        // variable
        static MyPanel myPanel = null;
        static int score = 0;
        static Timer timer = null;
        static Block[][] blocks = new Block[BLOCK_ROWS][BLOCK_COLUMNS];
        static Bar bar = new Bar();
        static Ball ball = new Ball();
        static int barXTarget = bar.x;
        static int dir = new Random().nextInt(4);
        static boolean isGameFinish = false; // 김민서

        static class Ball {
            int x = CANVAS_WIDTH / 2 - BALL_WIDTH / 2;
            int y = CANVAS_HEIGHT / 2 - BALL_HEIGHT / 2;
            int width = BALL_WIDTH;
            int height = BALL_HEIGHT;
            int ballSpeedx = 5;
            int ballSpeedy = -5;
        }

        static class Bar {
            int x = CANVAS_WIDTH / 2 - BAR_WIDTH / 2;
            int y = CANVAS_HEIGHT - 100;
            int width = BAR_WIDTH;
            int height = BAR_HEIGHT;
        }

        static class Block {
            int x = 0;
            int y = 0;
            int width = BLOCK_WIDTH;
            int height = BLOCK_HEIGHT;
            int color = 0;// 0:white 1:yellow 2:blue 3:mazanta 4:red
            boolean isHidden = false;// after collision, block will be hidden

        }

        static class MyPanel extends JPanel { // CANAVAS for Draw!    
            public MyPanel() {
                this.setSize(CANVAS_WIDTH, CANVAS_HEIGHT);
                this.setBackground(Color.BLACK);
            }

            @Override
            public void paint(Graphics g) {
                super.paint(g);
                Graphics2D g2d = (Graphics2D) g;
                drawUI(g2d);
            }

            private void drawUI(Graphics2D g2d) {
                // draw Blocks
                for (int i = 0; i < BLOCK_ROWS; i++) {
                    for (int j = 0; j < BLOCK_COLUMNS; j++) {
                        if (blocks[i][j].isHidden) {
                            continue;
                        }
                        switch (blocks[i][j].color) {
                            case 0 -> g2d.setColor(Color.WHITE);
                            case 1 -> g2d.setColor(Color.YELLOW);
                            case 2 -> g2d.setColor(Color.BLUE);
                            case 3 -> g2d.setColor(Color.MAGENTA);
                            case 4 -> g2d.setColor(Color.RED);
                        }
                        g2d.fillRect(blocks[i][j].x, blocks[i][j].y, blocks[i][j].width, blocks[i][j].height);
                    }
                }
                // draw score
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("TimesRoman", Font.BOLD, 20));
                g2d.drawString("score: " + score, CANVAS_WIDTH / 2 - 30, 20);

                // draw Ball
                g2d.setColor(Color.WHITE);
                g2d.fillOval(ball.x, ball.y, BALL_WIDTH, BALL_HEIGHT);

                // draw Bar
                g2d.setColor(Color.WHITE);
                g2d.fillRect(bar.x, bar.y, bar.width, bar.height);
                if( isGameFinish ) {
						g2d.setColor(Color.RED);
						g2d.drawString("Game Finished!", CANVAS_WIDTH/2 - 55, 50);
					}//김민서
            }
        }

        public MyFrame(String title) {
            super(title);
            this.setSize(CANVAS_WIDTH, CANVAS_HEIGHT);
            this.setLocation(400, 300);
            this.setLayout(new BorderLayout());
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setResizable(false);

            initData();

            myPanel = new MyPanel();
            myPanel.setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
            this.add(myPanel, BorderLayout.CENTER);

            this.pack();
            this.setLocation(400, 300);
            
            setKeyListener();
            startTimer();

            this.setVisible(true);
        }

        public void initData() {
            for (int i = 0; i < BLOCK_ROWS; i++) {
                for (int j = 0; j < BLOCK_COLUMNS; j++) {
                    blocks[i][j] = new Block();
                    blocks[i][j].x = BLOCK_WIDTH * j + BLOCK_GAP * j;
                    blocks[i][j].y = 100 + BLOCK_HEIGHT * i + BLOCK_GAP * i;
                    blocks[i][j].width = BLOCK_WIDTH;
                    blocks[i][j].height = BLOCK_HEIGHT;
                    blocks[i][j].color = 4 - i;// 0:white 1:yellow 2:blue 3:mazanta 4:red
                    blocks[i][j].isHidden = false;
                }
            }
        }

        public void setKeyListener() {

            myPanel.setFocusable(true);
            myPanel.requestFocusInWindow();
            myPanel.addKeyListener(new KeyAdapter() {

                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_LEFT) {

                        System.out.println("pressed Left Key");

                    if (bar.x - 20 >= 0) {

                        barXTarget = bar.x - 20;

                    }

                        
                    } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {

                        System.out.println("pressed Right Key");

                        if (bar.x + BAR_WIDTH + 20 <= CANVAS_WIDTH) {

                            barXTarget = bar.x + 20;
                            
                        }
                    }
                }
            });
        }

        public void startTimer() {
            timer = new Timer(20, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    movement();
                    checkCollision(); // Wall, Bar
                    checkCollisionBlock(); // Blocks 50
                    myPanel.repaint(); // Redraw
                }
            });
            timer.start();
        }

        void movement() {
            if (bar.x < barXTarget) {
                bar.x += 5;
            } else if (bar.x > barXTarget) {
                bar.x -= 5;
            }
            ball.x += ball.ballSpeedx;
            ball.y -= ball.ballSpeedy;

        }

        void checkCollision() {
            // 위/아래 벽 충돌 → y축 반전
            if (ball.y <= 0 || ball.y >= CANVAS_HEIGHT - 3*BALL_HEIGHT) { //3 곱함
                ball.ballSpeedy *= -1;
            }

            // 좌우 벽 충돌 → x축 반전
            if (ball.x <= 0 || ball.x >= CANVAS_WIDTH - BALL_WIDTH) {
                ball.ballSpeedx *= -1;
            }
        }

        public void checkCollisionBlock() {

        }
    }

    public static void main(String[] args) {
        System.out.println("main 시작됨");
        new MyFrame("BlockGame");
    }
}
