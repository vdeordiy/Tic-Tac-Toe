import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class GamePanel extends JPanel {
    private ImageIcon X_Image;
    private ImageIcon O_Image;
    private JLabel message;
    private JPanel board;
    private GameStatus gameStatus;
    private JButton[][] tiles;

    public GamePanel(){
        setVisible(true);

        try{
            loadImages();
        }catch(Exception e){
            JOptionPane.showMessageDialog(this, e.getMessage());
        }

        gameStatus = GameStatus.X_TURN;

        message = new JLabel("...", JLabel.CENTER);
        message.setBackground(Color.BLACK);
        message.setForeground(Color.WHITE);
        message.setFont(new Font("Arial", Font.BOLD, 20));
        message.setOpaque(true);

        updateMessage();

        board = new JPanel();
        board.setPreferredSize(new Dimension(300, 300));
        
        setLayout(new BorderLayout());
        add(message, BorderLayout.NORTH);
        add(board, BorderLayout.CENTER);

        tiles = new JButton[3][3];
        board.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        int x, y;
        for(x=0; x < 3; x++){
            for(y=0; y < 3; y++){
                JButton btn = new JButton();
                btn.setOpaque(true);
                btn.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
                btn.setBackground(Color.WHITE);
                btn.setPreferredSize(new Dimension(100, 100));

                gbc.fill = GridBagConstraints.BOTH;
                gbc.gridx = x;
                gbc.gridy = y;
                gbc.weightx = 1;
                gbc.weighty = 1;

                board.add(btn, gbc);

                tiles[x][y] = btn;

                btn.putClientProperty("Owner", Owner.NOONE);
                btn.addActionListener(new TileListener());
            }
        }
    }

    private void loadImages() throws IOException{
        BufferedImage xo = ImageIO.read(new File("src\\xo.png"));

        
        int width = xo.getWidth() / 2;
        int height = xo.getHeight();

        O_Image = new ImageIcon(
            xo.getSubimage(0, 0, width, height).getScaledInstance(100, 100, Image.SCALE_SMOOTH)
        );
        X_Image = new ImageIcon(
            xo.getSubimage(width, 0, width, height).getScaledInstance(100, 100, Image.SCALE_SMOOTH)
        );

    }

    private void updateMessage(){
        switch(gameStatus){
            case X_TURN:
                message.setText("X's turn");
                break;
            case O_TURN:
                message.setText("O's turn");
                break;
            case TIE:
                message.setText("Tie, noone wins");
                break;
            case X_WINS:
                message.setText("X is the winner");
                break;
            case O_WINS:
                message.setText("O is the winner");
                break;
        }
    }

    private void checkForWinner(){
        int[][][] winningConditions = new int[][][]{
            {{0, 0}, {0, 1}, {0, 2}},
            {{1, 0}, {1, 1}, {1, 2}},
            {{2, 0}, {2, 1}, {2, 2}},

            {{0, 0}, {1, 0}, {2, 0}},
            {{0, 1}, {1, 1}, {2, 1}},
            {{0, 2}, {1, 2}, {2, 2}},

            {{0, 0}, {1, 1}, {2, 2}},
            {{2, 0}, {1, 1}, {0, 2}},
        };

        Owner winner = Owner.NOONE;

        for (int[][] winningCondition : winningConditions){
            ArrayList<JButton> tilesToCheck = new ArrayList<JButton>(3);

            for (int[] coordinates : winningCondition){
                int x = coordinates[0]; int y = coordinates[1];
                tilesToCheck.add(tiles[x][y]);
            }

            if(
                tilesToCheck.get(0).getClientProperty("Owner") == Owner.X &&
                tilesToCheck.get(1).getClientProperty("Owner") == Owner.X &&
                tilesToCheck.get(2).getClientProperty("Owner") == Owner.X){
                    winner = Owner.X;
                    break;
            }else if(
                tilesToCheck.get(0).getClientProperty("Owner") == Owner.O &&
                tilesToCheck.get(1).getClientProperty("Owner") == Owner.O &&
                tilesToCheck.get(2).getClientProperty("Owner") == Owner.O){
                    winner = Owner.O;
                    break;
            }
        }

        boolean tie = true;
        for(JButton[] row : tiles){
            for (JButton tile : row){
                if(tile.getClientProperty("Owner") == Owner.NOONE){
                    tie = false;
                }
            }
        }

        if(tie){
            gameStatus = GameStatus.TIE;
        }

        if(winner == Owner.X){
            gameStatus = GameStatus.X_WINS;
        }else if(winner == Owner.O){
            gameStatus = GameStatus.O_WINS;
        }
    }

    class TileListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            JButton tile = (JButton) e.getSource();

            if(tile.getClientProperty("Owner") != Owner.NOONE){
                return;
            }

            if(gameStatus == GameStatus.X_TURN){
                tile.setIcon(X_Image);
                tile.putClientProperty("Owner", Owner.X);

                gameStatus = GameStatus.O_TURN;
            }else if(gameStatus == GameStatus.O_TURN){
                tile.setIcon(O_Image);
                tile.putClientProperty("Owner", Owner.O);

                gameStatus = GameStatus.X_TURN;
            }

            checkForWinner();
            updateMessage();
        }
    }
}
