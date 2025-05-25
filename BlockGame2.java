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
        static int TOTAL_BLOCKS = BLOCK_ROWS * BLOCK_COLUMNS;
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
        static int destroyedBlockCount = 0;
        static int clearStack = 0; //클리어 스택 --> 클리어마다 1씩 증가, 제약 중첩
        static boolean isGameFinish = false; // 김민서
        static boolean isClear = false; //클리어 판정용 변수
        static boolean isLeftPressed = false;
        static boolean isRightPressed = false;

        static class Ball {
            int x = CANVAS_WIDTH / 2 - BALL_WIDTH / 2;
            int y = CANVAS_HEIGHT / 2 - BALL_HEIGHT / 2;
            int width = BALL_WIDTH;
            int height = BALL_HEIGHT;
            int ballSpeedx = 6;
            int ballSpeedy = -6;

            Point getCenter() {
                return new Point(x + (BALL_WIDTH / 2), y + (BALL_HEIGHT / 2));
            }// 김민서

            Point getBottomCenter() {
                return new Point(x + (BALL_WIDTH / 2), y + (BALL_HEIGHT));
            }// 김민서

            Point getTopCenter() {
                return new Point(x + (BALL_WIDTH / 2), y);
            }// 김민서

            Point getLeftCenter() {
                return new Point(x, y + (BALL_HEIGHT / 2));
            }// 김민서

            Point getRightCenter() {
                return new Point(x + (BALL_WIDTH), y + (BALL_HEIGHT / 2));
            }// 김민서
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

            private void drawMidText(Graphics2D g2d, String text, int center, int y) { //텍스트 중앙 정렬
                FontMetrics metrics = g2d.getFontMetrics(g2d.getFont());
                int textWidth = metrics.stringWidth(text);
                g2d.drawString(text, center - textWidth / 2, y);
}

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
                drawMidText(g2d, "score :" +String.valueOf(score), CANVAS_WIDTH / 2, 20);
                // draw Ball
                g2d.setColor(Color.WHITE);
                g2d.fillOval(ball.x, ball.y, BALL_WIDTH, BALL_HEIGHT);

                // draw Bar
                g2d.setColor(Color.WHITE);
                g2d.fillRect(bar.x, bar.y, bar.width, bar.height);
                if (isGameFinish) {
                    g2d.setColor(Color.RED);
                    if (isClear) {
                        drawMidText(g2d, "Clear!", CANVAS_WIDTH / 2, 50);
                        drawMidText(g2d, "Press SPACE to continue", CANVAS_WIDTH / 2, 80);
                    } else {
                        drawMidText(g2d, "Game Over!", CANVAS_WIDTH / 2, 50);
                        drawMidText(g2d, "Press SPACE to restart", CANVAS_WIDTH / 2, 80);
                    }
                } // 김민서
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
            TOTAL_BLOCKS = BLOCK_COLUMNS * BLOCK_ROWS;
            destroyedBlockCount = 0;
        }

        public void setKeyListener() {

            myPanel.setFocusable(true);
            myPanel.requestFocusInWindow();
            myPanel.addKeyListener(new KeyAdapter() {

                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                        //키 버벅임 현상 방지
                        isLeftPressed = true;
                        isRightPressed = false;

                        System.out.println("pressed Left Key");

                        if (bar.x - 30 >= 0) {

                            barXTarget = bar.x - 30;

                        }

                    } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                        // 키 버벅임 현상 방지
                        isRightPressed = true;
                        isLeftPressed = false;

                        System.out.println("pressed Right Key");

                        if (bar.x + BAR_WIDTH + 30 <= CANVAS_WIDTH) {

                            barXTarget = bar.x + 30;

                        }
                    }
                }

                // 키 헤제시 막대기 멈춤
                @Override
                public void keyReleased(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                        isLeftPressed = false;
                        System.out.println("released Left Key");
                    } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                        isRightPressed = false;
                        System.out.println("released Right Key");
                    } else if (e.getKeyCode() == KeyEvent.VK_SPACE) { //키보드 조작 게임이므로 재시작 조건을 스페이스로 변경함
                        if (isGameFinish) {
                            restartGame();
                        }
                    }
                }
            });

            // 마우스 클릭시 재시작
            // myPanel.addMouseListener(new MouseAdapter() {
            //     @Override
            //     public void mousePressed(MouseEvent e) {
            //         if (isGameFinish && SwingUtilities.isLeftMouseButton(e)) {
            //             restartGame();
            //         }
            //     }
            // });
        }
        public void restartGame() {
            isGameFinish = false;
            if (isClear == false) { //클리어가 아니라면 점수 초기화
                score = 0;
                clearStack = 0;
            }
            isClear = false;
            initData(); // 블록 초기화

            // 막대기 초기화
            bar.x = CANVAS_WIDTH / 2 - BAR_WIDTH / 2;
            bar.y = CANVAS_HEIGHT - 100;
            barXTarget = bar.x;
            // 공 초기화
            ball.x = CANVAS_WIDTH / 2 - BALL_WIDTH / 2;
            ball.y = CANVAS_HEIGHT / 3 - BALL_HEIGHT / 2;
            ball.ballSpeedx = 6;
            ball.ballSpeedy = -6;

            int speedX = 6;
            int speedY = -6;
            int barWidth = BAR_WIDTH;

            if (clearStack >= 1) {
                speedX = 8;
                speedY = -8;
            }
            if (clearStack >= 2) {
                barWidth = BAR_WIDTH - 20;
            }


            if (barWidth < 30) barWidth = 30;

            ball.ballSpeedx = speedX;
            ball.ballSpeedy = speedY;
            bar.width = barWidth;

            timer.start();
        }

        public static void checkClear() {

            if (destroyedBlockCount == TOTAL_BLOCKS) { //파괴한 블록수와 총 블록수가 같아지면
                isGameFinish = true;
                isClear = true;
                clearStack += 1;
                timer.stop();
            }

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
            if (isLeftPressed && bar.x > 0) {
                bar.x -= 9;
            } else if (isRightPressed && bar.x + bar.width < CANVAS_WIDTH) {
                bar.x += 9;
            }
            ball.x += ball.ballSpeedx;
            ball.y -= ball.ballSpeedy;

        }

        public boolean duplRect(Rectangle rect1, Rectangle rect2) {
            return rect1.intersects(rect2); // check two Rect is Duplicated!
        }// 김민서

        void checkCollision() {

            Random rand = new Random();
            int tweak = rand.nextInt(3) - 1; // -1, 0, 1 중 랜덤

            // 위/아래 벽 충돌 → y축 반전
            if (ball.y <= 0) {
                ball.ballSpeedy *= -1;
                ball.ballSpeedy += tweak; //미세한 각도 변화
                ball.y = 1; // 위쪽 보정
            }
            // 좌우 벽 충돌 → x축 반전
            if (ball.x <= 0) {
                ball.ballSpeedx *= -1;
                ball.ballSpeedx += tweak;
                ball.x = 1; // 왼쪽 벽에서 튕겼을 때 위치 보정
            } else if (ball.x >= CANVAS_WIDTH - BALL_WIDTH) {
                ball.ballSpeedx *= -1;
                ball.ballSpeedx += tweak;
                ball.x = CANVAS_WIDTH - BALL_WIDTH - 1; // 오른쪽 벽에서 튕겼을 때 위치 보정

            }
            // 김민서 -> 막대기 충돌버젼에서 (김태현 수정) 오류사유 dir 사용( 현재 필요없는 부분)
            if (ball.getBottomCenter().y >= bar.y) {
                if (duplRect(new Rectangle(ball.x, ball.y, ball.width, ball.height),
                        new Rectangle(bar.x, bar.y, bar.width, bar.height))) {
                    ball.ballSpeedy *= -1; // 변경전 dir = 2;
                    ball.y = bar.y - ball.height - 1; //끼임 방지를 위한 위치 보정
                }
            }
            ball.ballSpeedx = Math.max(-10, Math.min(ball.ballSpeedx, 10)); //공이 너무 빨라지지 않도록 속도 보정
            ball.ballSpeedy = Math.max(-10, Math.min(ball.ballSpeedy, 10));

            if (ball.y >= CANVAS_HEIGHT - BALL_HEIGHT) {
                isGameFinish =true;
                timer.stop();
            }

        }

        public void checkCollisionBlock() {
            for (int i = 0; i < BLOCK_ROWS; i++) {
                for (int j = 0; j < BLOCK_COLUMNS; j++) {
                    Block block = blocks[i][j];

                    if (block.isHidden) continue;

                    Rectangle ballRect = new Rectangle(ball.x, ball.y, ball.width, ball.height);
                    Rectangle blockRect = new Rectangle(block.x, block.y, block.width, block.height);

                    if (duplRect(ballRect, blockRect)) {

                        block.isHidden =true;
                        destroyedBlockCount += 1;
                        checkClear();
                        // 점수 시스템
                        // 점수는 블록의 행에 따라 다르게 부여(위쪽 블록일수록 점수가 높음)
                        int rowScore = (BLOCK_ROWS - i) * 10;
                        score += rowScore;

                        // 공 충돌 방향 계산
                        Rectangle intersection = ballRect.intersection(blockRect);
                        if (intersection.width > intersection.height) {
                            ball.ballSpeedy *= -1;
                        } else{
                            ball.ballSpeedx *= -1;
                        }

                        return;
                        }
                    }
                }
            }

        }







    public static void main(String[] args) {
        System.out.println("main 시작됨");
        new MyFrame("BlockGame");
    }
}
