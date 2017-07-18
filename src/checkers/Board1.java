
package checkers;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class Board1 implements ActionListener {

    private final JPanel gui = new JPanel(new BorderLayout(1, 1));
    private JButton newButton, resignButton;
    private final JButton[][] checkersBoardSquares = new JButton[8][8];
    private final JPanel checkersBoard = new JPanel(new GridLayout(0, 8));
    private final JLabel message = new JLabel("Checkers ready!");
    private final BufferedImage emptyPieceImage = new BufferedImage(80, 80, BufferedImage.TYPE_INT_ARGB);
    private BufferedImage blackPieceFile, blackKingPieceFile, redPieceFile, redKingPieceFile;
    private ImageIcon blackPieceImage, blackKingPieceImage, redPieceImage, redKingPieceImage;
    private final int[][] piece = new int[8][8];
    private final int[][] pieceScore = {{0, 4, 0, 4, 0, 4, 0, 4}, {4, 0, 3, 0, 3, 0, 3, 0}, {0, 3, 0, 2, 0, 2, 0, 4}, {4, 0, 2, 0, 1, 0, 3, 0}, {0, 3, 0, 1, 0, 2, 0, 4}, {4, 0, 2, 0, 2, 0, 3, 0}, {0, 3, 0, 3, 0, 3, 0, 4}, {4, 0, 4, 0, 4, 0, 4, 0}};
    private final int player1 = 1, player2 = 3, blackPiece = 1, blackKingPiece = 2, redPiece = 3, redKingPiece = 4, emptyPiece = 0, player = 1;
    private int currentPlayer, selectedRow, selectedCol, countPiecePlayer1, countPiecePlayer2;
    private boolean statusGame;
    private PieceData[] legalMoves;

    public Board1() {
        try {
            blackPieceFile = ImageIO.read(new File(System.getProperty("user.dir") + "/src/checkers/red_piece.png"));
            blackKingPieceFile = ImageIO.read(new File(System.getProperty("user.dir") + "/src/checkers/red_king.png"));
            redPieceFile = ImageIO.read(new File(System.getProperty("user.dir") + "/src/checkers/white_piece.png"));
            redKingPieceFile = ImageIO.read(new File(System.getProperty("user.dir") + "/src/checkers/white_king.png"));
            blackPieceImage = new ImageIcon(blackPieceFile.getSubimage(0, 0, 80, 80));
            blackKingPieceImage = new ImageIcon(blackKingPieceFile.getSubimage(0, 0, 80, 80));
            redPieceImage = new ImageIcon(redPieceFile.getSubimage(0, 0, 80, 80));
            redKingPieceImage = new ImageIcon(redKingPieceFile.getSubimage(0, 0, 80, 80));
        } catch (Exception e) {
            System.exit(1);
        }
        initializeGUI();
        setUpGame();
    }
    
    public final void initializeGUI() {
        int col;
        gui.setBorder(new EmptyBorder(5, 5, 5, 5));
        JToolBar tools = new JToolBar();
        tools.setFloatable(false);
        gui.add(tools, BorderLayout.PAGE_START);
        newButton = new JButton("New");
        newButton.addActionListener(this);
        resignButton = new JButton("Resign");
        resignButton.addActionListener(this);
        tools.add(newButton);
        tools.addSeparator();
        tools.add(resignButton);
        tools.addSeparator();
        tools.add(message);      
        checkersBoard.setBorder(new LineBorder(Color.BLACK));
        gui.add(checkersBoard);       
        Insets buttonMargin = new Insets(0,0,0,0);
        for (JButton[] checkersBoardSquare : checkersBoardSquares) {
            for (col = 0; col < checkersBoardSquare.length; col++) {
                JButton b = new JButton();
                b.setMargin(buttonMargin);
                ImageIcon icon = new ImageIcon(emptyPieceImage);
                b.setIcon(icon);
                checkersBoardSquare[col] = b;
                checkersBoardSquare[col].addActionListener(this);
            }
        }
    }
    
    public final void setUpGame() {
        currentPlayer = player1;
        selectedRow = -1;
        countPiecePlayer1 = 12;
        countPiecePlayer2 = 12;
        initializeBoard();
        if(currentPlayer != player) {
            statusGame = false;
            message.setText("Please wait, computer is thinking for move.");
            comTurn();
            currentPlayer = player2;
            message.setText("White : Click the piece you want to move!");
        } else {
            message.setText("Red : Click the piece you want to move!");
        }
        statusGame = true;
        legalMoves = getLegalMoves(currentPlayer, piece);
        refreshBoard();
    }

    public final void initializeBoard() {
        int row, col;
        for (row = 0; row < checkersBoardSquares.length; row++) {
            for (col = 0; col < checkersBoardSquares[row].length; col++) {
                piece[row][col] = emptyPiece;
            }
        }
        for (col = 0; col < 8; col+=2) {
            piece[0][col+1] = blackPiece;
            piece[1][col] = blackPiece;
            piece[2][col+1] = blackPiece;
            piece[5][col] = redPiece;
            piece[6][col+1] = redPiece;
            piece[7][col] = redPiece;
        }
        for (row = 0; row < 8; row++) {
            for (col = 0; col < 8; col++) {
                checkersBoard.add(checkersBoardSquares[row][col]);
            }
        }
    }
    
    public final PieceData[] getLegalMoves(int tempPlayer, int[][] tempPiece) {
        int row, col;
        ArrayList<PieceData> moves = new ArrayList<>();
        for (row = 0; row < 8; row++) {
            for (col = 0; col < 8; col++) {
                if (tempPiece[row][col] == tempPlayer) {
                    if (canJump(tempPlayer, row, col, row+1, col+1, row+2, col+2)) {
                        if (row+2 == 7) {
                            moves.add(new PieceData(row, col, row+2, col+2, (pieceScore[row+2][col+2] - pieceScore[row][col]), 100, 0, 50));
                        } else {
                            moves.add(new PieceData(row, col, row+2, col+2, (pieceScore[row+2][col+2] - pieceScore[row][col]), 100, 0, 0));
                        }
                    }
                    if (canJump(tempPlayer, row, col, row-1, col+1, row-2, col+2)) {
                        if (row-2 == 0) {
                            moves.add(new PieceData(row, col, row-2, col+2, (pieceScore[row-2][col+2] - pieceScore[row][col]), 100, 0, 50));
                        } else {
                            moves.add(new PieceData(row, col, row-2, col+2, (pieceScore[row-2][col+2] - pieceScore[row][col]), 100, 0, 0));
                        }
                    }
                    if (canJump(tempPlayer, row, col, row+1, col-1, row+2, col-2)) {
                        if (row+2 == 7) {
                            moves.add(new PieceData(row, col, row+2, col-2, (pieceScore[row+2][col-2] - pieceScore[row][col]), 100, 0, 50));
                        } else {
                            moves.add(new PieceData(row, col, row+2, col-2, (pieceScore[row+2][col-2] - pieceScore[row][col]), 100, 0, 0));
                        }
                    }
                    if (canJump(tempPlayer, row, col, row-1, col-1, row-2, col-2)) {
                        if (row-2 == 0) {
                            moves.add(new PieceData(row, col, row-2, col-2, (pieceScore[row-2][col-2] - pieceScore[row][col]), 100, 0, 50));
                        } else {
                            moves.add(new PieceData(row, col, row-2, col-2, (pieceScore[row-2][col-2] - pieceScore[row][col]), 100, 0, 0));
                        }
                    }
                } else if (tempPiece[row][col] == (tempPlayer + 1)) {
                    if (canJump(tempPlayer, row, col, row+1, col+1, row+2, col+2)) {
                        moves.add(new PieceData(row, col, row+2, col+2, (pieceScore[row+2][col+2] - pieceScore[row][col]), 100, 5, 0));
                    }
                    if (canJump(tempPlayer, row, col, row-1, col+1, row-2, col+2)) {
                        moves.add(new PieceData(row, col, row-2, col+2, (pieceScore[row-2][col+2] - pieceScore[row][col]), 100, 5, 0));
                    }
                    if (canJump(tempPlayer, row, col, row+1, col-1, row+2, col-2)) {
                        moves.add(new PieceData(row, col, row+2, col-2, (pieceScore[row+2][col-2] - pieceScore[row][col]), 100, 5, 0));
                    }
                    if (canJump(tempPlayer, row, col, row-1, col-1, row-2, col-2)) {
                        moves.add(new PieceData(row, col, row-2, col-2, (pieceScore[row-2][col-2] - pieceScore[row][col]), 100, 5, 0));
                    }
                }
            }
        }
        if (moves.isEmpty()) {
            for (row = 0; row < 8; row++) {
                for (col = 0; col < 8; col++) {
                    if (piece[row][col] == tempPlayer) {
                        if (canMove(tempPlayer, row, col, row+1, col+1)) {
                            if (row+1 == 7) {
                                moves.add(new PieceData(row, col, row+1, col+1, (pieceScore[row+1][col+1] - pieceScore[row][col]), 0, 0, 50));
                            } else {
                                moves.add(new PieceData(row, col, row+1, col+1, (pieceScore[row+1][col+1] - pieceScore[row][col]), 0, 0, 0));
                            }
                        }
                        if (canMove(tempPlayer, row, col, row-1, col+1)) {
                            if (row-1 == 0) {
                                moves.add(new PieceData(row, col, row-1, col+1, (pieceScore[row-1][col+1] - pieceScore[row][col]), 0, 0, 50));
                            } else {
                                moves.add(new PieceData(row, col, row-1, col+1, (pieceScore[row-1][col+1] - pieceScore[row][col]), 0, 0, 0));
                            }
                        }
                        if (canMove(tempPlayer, row, col, row+1, col-1)) {
                            if (row+1 == 7) {
                                moves.add(new PieceData(row, col, row+1, col-1, (pieceScore[row+1][col-1] - pieceScore[row][col]), 0, 0, 50));
                            } else {
                                moves.add(new PieceData(row, col, row+1, col-1, (pieceScore[row+1][col-1] - pieceScore[row][col]), 0, 0, 0));
                            }
                        }
                        if (canMove(tempPlayer, row, col, row-1, col-1)) {
                            if (row-1 == 0) {
                                moves.add(new PieceData(row, col, row-1, col-1, (pieceScore[row-1][col-1] - pieceScore[row][col]), 0, 0, 50));
                            } else {
                                moves.add(new PieceData(row, col, row-1, col-1, (pieceScore[row-1][col-1] - pieceScore[row][col]), 0, 0, 0));
                            }
                        }
                    } else if (piece[row][col] == (tempPlayer + 1)) {
                        if (canMove(tempPlayer, row, col, row+1, col+1)) {
                            moves.add(new PieceData(row, col, row+1, col+1, (pieceScore[row+1][col+1] - pieceScore[row][col]), 0, 5, 0));
                        }
                        if (canMove(tempPlayer, row, col, row-1, col+1)) {
                            moves.add(new PieceData(row, col, row-1, col+1, (pieceScore[row-1][col+1] - pieceScore[row][col]), 0, 5, 0));
                        }
                        if (canMove(tempPlayer, row, col, row+1, col-1)) {
                            moves.add(new PieceData(row, col, row+1, col-1, (pieceScore[row+1][col-1] - pieceScore[row][col]), 0, 5, 0));
                        }
                        if (canMove(tempPlayer, row, col, row-1, col-1)) {
                            moves.add(new PieceData(row, col, row-1, col-1, (pieceScore[row-1][col-1] - pieceScore[row][col]), 0, 5, 0));
                        }
                    }
                }
            }
        }
        if (moves.isEmpty()) {
            return null;
        } else {
            PieceData[] moveArray = new PieceData[moves.size()];
            for (row = 0; row < moves.size(); row++) {
                moveArray[row] = moves.get(row);
            }
            return moveArray;
        }
    }
    
    public final boolean canMove(int tempPlayer, int fromRow, int fromCol, int toRow, int toCol) {
        if (toRow < 0 || toRow >= 8 || toCol < 0 || toCol >= 8) {
            return false;
        }
        if (piece[toRow][toCol] != emptyPiece) {
            return false;
        }
        if (tempPlayer == player1) {
            return !(piece[fromRow][fromCol] == player1 && toRow < fromRow);
        }
        else {
            return !(piece[fromRow][fromCol] == player2 && toRow > fromRow);
        }
    }
    
    public final boolean canJump(int tempPlayer, int fromRow, int fromCol, int row, int col, int toRow, int toCol) {     
        if (toRow < 0 || toRow >= 8 || toCol < 0 || toCol >= 8) {
            return false;
        }
        if (piece[toRow][toCol] != emptyPiece) {
            return false;
        }
        if (tempPlayer == player1) {
            if (piece[fromRow][fromCol] == player1 && toRow < fromRow) {
                return false;
            }
            return !(piece[row][col] != player2 && piece[row][col] != redKingPiece);
        }
        else {
            if (piece[fromRow][fromCol] == player2 && toRow > fromRow) {
                return false;
            }
            return !(piece[row][col] != player1 && piece[row][col] != blackKingPiece);
        }
    }
    
    public final void drawLegalMoveOnBoard() {
        int row;
        for (row = 0; row < legalMoves.length; row++) {
            checkersBoardSquares[legalMoves[row].getX2()][legalMoves[row].getY2()].setBackground(Color.GRAY);
        }
    }
    
    public final void doAction(int x, int y) {
        int row;
        for (row = 0; row < legalMoves.length; row++) {
            if (legalMoves[row].getX1() == x && legalMoves[row].getY1() == y) {
                selectedRow = x;
                selectedCol = y;
                if (currentPlayer == player1 && currentPlayer == player) {
                    message.setText("Red : Click the square you want to move to!");
                } else if (currentPlayer == player2 && currentPlayer == player) {
                    message.setText("White : Click the square you want to move to!");
                }
                return;
            }
        }
        if (selectedRow < 0) {
            if (currentPlayer == player1 && currentPlayer == player) {
                message.setText("Red : Click the piece you want to move!");
            } else if (currentPlayer == player2 && currentPlayer == player) {
                message.setText("White : Click the piece you want to move!");
            }
            return;
        }
        for (row = 0; row < legalMoves.length; row++) {
            if (legalMoves[row].getX1() == selectedRow && legalMoves[row].getY1() == selectedCol && legalMoves[row].getX2() == x && legalMoves[row].getY2() == y) {
                doMakeMove(legalMoves[row]);
                return;
            }
        }
    }
    
    public final void doMakeMove(PieceData piece) {
        makeMove(piece.getX1(), piece.getY1(), piece.getX2(), piece.getY2());
        if (piece.isJump()) {
            legalMoves = getLegalJumpFrom(currentPlayer, piece.getX2(), piece.getY2(), this.piece);
            if (legalMoves != null) {
                if (currentPlayer == player1 && currentPlayer == player) {
                    message.setText("Red : You must continue jumping!");
                } else if (currentPlayer == player2 && currentPlayer == player) {
                    message.setText("White : You must continue jumping!");
                }
                selectedRow = piece.getX2();
                selectedCol = piece.getY2();
                return;
            }
        }
        if (currentPlayer == player1 && currentPlayer == player) {
            currentPlayer = player2;
            legalMoves = getLegalMoves(currentPlayer, this.piece);
            if (legalMoves == null) {
                if (countPiecePlayer2 > 0) {
                    gameOver("Computer has no moves. White wins.");
                } else {
                    gameOver("Computer has no piece. White wins.");
                }
                return;
            }
        } else if (currentPlayer == player2 && currentPlayer == player) {
            currentPlayer = player1;
            legalMoves = getLegalMoves(currentPlayer, this.piece);
            if (legalMoves == null) {
                if (countPiecePlayer1 > 0) {
                    gameOver("Computer has no moves. Red wins.");
                } else {
                    gameOver("Computer has no piece. Red wins.");
                }
                return;
            }
        }
        selectedRow = -1;
        refreshBoard();
        comTurn();
    }
    
    public final PieceData[] getLegalJumpFrom(int tempPlayer, int fromRow, int fromCol, int[][] tempPiece) {
        int row;
        ArrayList<PieceData> moves = new ArrayList<>();
        if (tempPiece[fromRow][fromCol] == tempPlayer) {
            if (canJump(tempPlayer, fromRow, fromCol, fromRow+1, fromCol+1, fromRow+2, fromCol+2)) {
                if (fromRow+2 == 7) {
                    moves.add(new PieceData(fromRow, fromCol, fromRow+2, fromCol+2, (pieceScore[fromRow+2][fromCol+2] - pieceScore[fromRow][fromCol]), 100, 0, 50));
                } else {
                    moves.add(new PieceData(fromRow, fromCol, fromRow+2, fromCol+2, (pieceScore[fromRow+2][fromCol+2] - pieceScore[fromRow][fromCol]), 100, 0, 0));
                }
            }
            if (canJump(tempPlayer, fromRow, fromCol, fromRow-1, fromCol+1, fromRow-2, fromCol+2)) {
                if (fromRow-2 == 0) {
                    moves.add(new PieceData(fromRow, fromCol, fromRow-2, fromCol+2, (pieceScore[fromRow-2][fromCol+2] - pieceScore[fromRow][fromCol]), 100, 0, 50));
                } else {
                    moves.add(new PieceData(fromRow, fromCol, fromRow-2, fromCol+2, (pieceScore[fromRow-2][fromCol+2] - pieceScore[fromRow][fromCol]), 100, 0, 0));
                }
            }
            if (canJump(tempPlayer, fromRow, fromCol, fromRow+1, fromCol-1, fromRow+2, fromCol-2)) {
                if (fromRow+2 == 7) {
                    moves.add(new PieceData(fromRow, fromCol, fromRow+2, fromCol-2, (pieceScore[fromRow+2][fromCol-2] - pieceScore[fromRow][fromCol]), 100, 0, 50));
                } else {
                    moves.add(new PieceData(fromRow, fromCol, fromRow+2, fromCol-2, (pieceScore[fromRow+2][fromCol-2] - pieceScore[fromRow][fromCol]), 100, 0, 0));
                }
            }
            if (canJump(tempPlayer, fromRow, fromCol, fromRow-1, fromCol-1, fromRow-2, fromCol-2)) {
                if (fromRow-2 == 0) {
                    moves.add(new PieceData(fromRow, fromCol, fromRow-2, fromCol-2, (pieceScore[fromRow-2][fromCol-2] - pieceScore[fromRow][fromCol]), 100, 0, 50));
                } else {
                    moves.add(new PieceData(fromRow, fromCol, fromRow-2, fromCol-2, (pieceScore[fromRow-2][fromCol-2] - pieceScore[fromRow][fromCol]), 100, 0, 0));
                }
            }
        } else if (tempPiece[fromRow][fromCol] == (tempPlayer + 1)) {
            if (canJump(tempPlayer, fromRow, fromCol, fromRow+1, fromCol+1, fromRow+2, fromCol+2)) {
                moves.add(new PieceData(fromRow, fromCol, fromRow+2, fromCol+2, (pieceScore[fromRow+2][fromCol+2] - pieceScore[fromRow][fromCol]), 100, 5, 0));
            }
            if (canJump(tempPlayer, fromRow, fromCol, fromRow-1, fromCol+1, fromRow-2, fromCol+2)) {
                moves.add(new PieceData(fromRow, fromCol, fromRow-2, fromCol+2, (pieceScore[fromRow-2][fromCol+2] - pieceScore[fromRow][fromCol]), 100, 5, 0));
            }
            if (canJump(tempPlayer, fromRow, fromCol, fromRow+1, fromCol-1, fromRow+2, fromCol-2)) {
                moves.add(new PieceData(fromRow, fromCol, fromRow+2, fromCol-2, (pieceScore[fromRow+2][fromCol-2] - pieceScore[fromRow][fromCol]), 100, 5, 0));
            }
            if (canJump(tempPlayer, fromRow, fromCol, fromRow-1, fromCol-1, fromRow-2, fromCol-2)) {
                moves.add(new PieceData(fromRow, fromCol, fromRow-2, fromCol-2, (pieceScore[fromRow-2][fromCol-2] - pieceScore[fromRow][fromCol]), 100, 5, 0));
            }
        }
        if (moves.isEmpty()) {
            return null;
        } else {
            PieceData[] moveArray = new PieceData[moves.size()];
            for (row = 0; row < moves.size(); row++) {
                moveArray[row] = moves.get(row);
            }
            return moveArray;
        }
    }
    
    public final void makeMove(int fromRow, int fromCol, int toRow, int toCol) {
        piece[toRow][toCol] = piece[fromRow][fromCol];
        piece[fromRow][fromCol] = emptyPiece;
        if (fromRow - toRow == 2 || fromRow - toRow == -2) {
            int jumpRow = (fromRow + toRow) / 2;
            int jumpCol = (fromCol + toCol) / 2;
            if (piece[jumpRow][jumpCol] == redPiece || piece[jumpRow][jumpCol] == redKingPiece) {
                countPiecePlayer2--;
            } else {
                countPiecePlayer1--;
            }
            piece[jumpRow][jumpCol] = emptyPiece;
        }
        if (toRow == 0 && piece[toRow][toCol] == redPiece) {
            piece[toRow][toCol] = redKingPiece;
        }
        if (toRow == 7 && piece[toRow][toCol] == blackPiece) {
            piece[toRow][toCol] = blackKingPiece;
        }
    }
    
    /*public final void comTurn() {
        int[][] tempPiece = new int[8][8];
        int x, y, z;
        legalMoves = getLegalMoves(currentPlayer, piece);
        for (x = 0; x < 8; x++) {
            for (y = 0; y < 8; y++) {
                tempPiece[x][y] = piece[x][y];
            }
        }
        if (legalMoves.length != 1) {
            z = -negascout(tempPiece, -999, 999, 1, currentPlayer);
            System.out.println(legalMoves.length);
            System.out.println(z);
            makeMove(legalMoves[z].getX1(), legalMoves[z].getY1(), legalMoves[z].getX2(), legalMoves[z].getY2());
        } else {
            makeMove(legalMoves[0].getX1(), legalMoves[0].getY1(), legalMoves[0].getX2(), legalMoves[0].getY2());
        }
        currentPlayer = player;
        legalMoves = getLegalMoves(currentPlayer, piece);
        selectedRow = -1;
        refreshBoard();
        statusGame = true;
    }
    
    public final int negascout(int[][] tempPiece1, int alpha, int beta, int depth, int player) {
        int i, t, m, n, x, y;
        int[][] tempPiece2 = new int[8][8];
        if (depth == 4) {
            return evaluate(tempPiece1, player);
        } else {
            m = -999;
            n = beta;
            PieceData[] tempLegalMove = getLegalMoves(player, tempPiece1);
            for (i = 0; i < tempLegalMove.length; i++) {
                //System.out.println(tempLegalMove[i].getX1() + " " + tempLegalMove[i].getY1() + "-" + tempLegalMove[i].getX2() + " " + tempLegalMove[i].getY2());
                for (x = 0; x < 8; x++) {
                    for (y = 0; y < 8; y++) {
                        tempPiece2[x][y] = tempPiece1[x][y];
                    }
                }
                if (tempLegalMove[i].isJump()) {
                    tempPiece2[(tempLegalMove[i].getX1() + tempLegalMove[i].getX2()) / 2][(tempLegalMove[i].getY1() + tempLegalMove[i].getY2()) / 2] = 0;
                }
                tempPiece2[tempLegalMove[i].getX2()][tempLegalMove[i].getY2()] = tempPiece1[tempLegalMove[i].getX1()][tempLegalMove[i].getY1()];
                tempPiece2[tempLegalMove[i].getX1()][tempLegalMove[i].getY1()] = 0;
                if (alpha >= n) {
                    t = -negascout(tempPiece2, -n, -alpha, depth + 1, player);
                } else {
                    t = -negascout(tempPiece2, -n, -m, depth + 1, player);
                }
                if (t > m) {
                    if (n == beta || depth >= 2-2) {
                        m = t;
                    } else {
                        m = -negascout(tempPiece2, -beta, -t, depth + 1, player);
                    }
                }
                if (m >= beta) {
                    return m;
                }
                if (alpha >= m) {
                    n = alpha + 1;
                } else {
                    n = m + 1;
                }
            }
            return m;
        }
    }*/
    
    public final void comTurn() {
        int[][] tempPiece = new int[8][8];
        int[] tempScore = new int[50];
        int x, y, z = -999, temp = -999;
        legalMoves = getLegalMoves(currentPlayer, piece);
        for (x = 0; x < 8; x++) {
            for (y = 0; y < 8; y++) {
                tempPiece[x][y] = piece[x][y];
            }
        }
        for (x = 0; x < legalMoves.length; x++) {
            tempScore[x] = negascout(tempPiece, legalMoves[x], -999, 999, 1, currentPlayer);
        }
        for (x = 0; x < legalMoves.length; x++) {
            if (tempScore[x] > z) {
                z = tempScore[x];
                temp = x;
            } else if (tempScore[x] == z) {
                if (legalMoves[x].getPieceScore() > legalMoves[temp].getPieceScore()) {
                    z = tempScore[x];
                    temp = x;
                }
            }
        }
        makeMove(legalMoves[temp].getX1(), legalMoves[temp].getY1(), legalMoves[temp].getX2(), legalMoves[temp].getY2());
        currentPlayer = player;
        legalMoves = getLegalMoves(currentPlayer, piece);
        selectedRow = -1;
        refreshBoard();
        statusGame = true;
    }
    
    public final int negascout(int[][] tempPiece1, PieceData tempMove1, int alpha, int beta, int depth, int player) {
        int i, t, m, n, x, y;
        int[][] tempPiece2 = new int[8][8];
        PieceData tempMove2 = new PieceData(tempMove1.getX1(), tempMove1.getY1(), tempMove1.getX2(), tempMove1.getY2(), tempMove1.getPieceScore(), tempMove1.getJumpScore(), tempMove1.getKingScore(), tempMove1.getBeKing());
        if (tempMove2.isJump()) {
            tempPiece2[(tempMove2.getX1() + tempMove2.getX2()) / 2][(tempMove2.getY1() + tempMove2.getY2()) / 2] = 0;
        }
        tempPiece2[tempMove2.getX2()][tempMove2.getY2()] = tempPiece1[tempMove2.getX1()][tempMove2.getY1()];
        tempPiece2[tempMove2.getX1()][tempMove2.getY1()] = 0;
        if (depth == 1) {
            return evaluate(tempPiece1, player);
        } else {
            m = alpha;
            n = beta;
            PieceData[] tempLegalMove = getLegalMoves(player, tempPiece1);
            for (i = 0; i < tempLegalMove.length; i++) {
                for (x = 0; x < 8; x++) {
                    for (y = 0; y < 8; y++) {
                        tempPiece2[x][y] = tempPiece1[x][y];
                    }
                }
                if (alpha >= n) {
                    t = -negascout(tempPiece2, tempLegalMove[i], -n, -alpha, depth + 1, player);
                } else {
                    t = -negascout(tempPiece2, tempLegalMove[i], -n, -m, depth + 1, player);
                }
                if (t > m) {
                    if (n == beta || depth >= 2-2) {
                        m = t;
                    } else {
                        m = -negascout(tempPiece2, tempLegalMove[i], -beta, -t, depth + 1, player);
                    }
                }
                if (m >= beta) {
                    return m;
                }
                if (alpha >= m) {
                    n = alpha + 1;
                } else {
                    n = m + 1;
                }
            }
            return m;
        }
    }
    
    public final int evaluate(int[][] tempPiece, int player) {
        PieceData[] tempLegalMove = getLegalMoves(player, tempPiece);
        int x, y, temp, score;
        x = 0;
        y = 0;
        temp = tempLegalMove[x].getBeKing() + tempLegalMove[x].getJumpScore() + tempLegalMove[x].getKingScore() + tempLegalMove[x].getPieceScore();
        while (x < tempLegalMove.length) {
            score = tempLegalMove[x].getBeKing() + tempLegalMove[x].getJumpScore() + tempLegalMove[x].getKingScore() + tempLegalMove[x].getPieceScore();
            if (score > temp) {
                temp = score;
                y = x;
            }
            x++;
        }
        return temp;
    }
    
    public final void refreshBoard() {
        int row, col;
        for (row = 0; row < checkersBoardSquares.length; row++) {
            for (col = 0; col < checkersBoardSquares[row].length; col++) {
                checkersBoardSquares[row][col].setIcon(null);
                if ((row % 2 == 1 && col % 2 == 1) || (row % 2 == 0 && col % 2 == 0)) {
                    checkersBoardSquares[row][col].setBackground(Color.WHITE);
                } else {
                    checkersBoardSquares[row][col].setBackground(Color.BLACK);
                }
                switch(piece[row][col]) {
                    case blackPiece : {
                        checkersBoardSquares[row][col].setIcon(blackPieceImage);
                        break;
                    }
                    case blackKingPiece : {
                        checkersBoardSquares[row][col].setIcon(blackKingPieceImage);
                        break;
                    }
                    case redPiece : {
                        checkersBoardSquares[row][col].setIcon(redPieceImage);
                        break;
                    }
                    case redKingPiece : {
                        checkersBoardSquares[row][col].setIcon(redKingPieceImage);
                        break;
                    }
                }
            }
        }
        drawLegalMoveOnBoard();
    }
    
    public final void gameOver(String text) {
        JOptionPane.showMessageDialog(null, text, "Game Over", JOptionPane.PLAIN_MESSAGE);
        statusGame = false;
        message.setText(text);
    }

    public static void main(String[] args) {
        Board1 board =  new Board1();
        JFrame frame = new JFrame("Checkers");
        frame.add(board.getGui());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationByPlatform(true);
        frame.pack();
        frame.setMinimumSize(frame.getSize());
        frame.setResizable(false);
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int row, col;
        Object src = e.getSource();
        if (src == newButton) {
            setUpGame();
        } else if (src == resignButton) {
            if (currentPlayer == player1) {
                gameOver("Red has resign.  Computer wins.");
            } else {
                gameOver("White has resign.  Computer wins.");
            }
        }
        if (statusGame) {
            for (row = 0; row < 8; row++) {
                for (col = 0; col < 8; col++) {
                    if (src == checkersBoardSquares[row][col]) {
                        doAction(row, col);
                        break;
                    }
                }
            }
        }
    }

    public final JComponent getGui() {
        return gui;
    }
}