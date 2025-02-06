package com.package1.chess;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ChessView extends JPanel implements MouseListener, MouseMotionListener {

   private ChessDelegate chessDelegate;



    private final Color lightWoodColor = new Color(222, 184, 135); // Light brown
    private final Color darkWoodColor = new Color(139, 69, 19);    // Dark brown

    int panelWidth = getWidth();
    int panelHeight = getHeight();

    // Calculate cell size dynamically based on panel size
    int boardSize = Math.min(panelWidth, panelHeight) - 20; // Padding of 10px on each side
    private int cellSize = boardSize / 8; // Chessboard has 8 rows and 8 columns

    // Center the board within the panel
    private int  originX = (panelWidth - boardSize) / 2;
    private int originY = (panelHeight - boardSize) / 2;

    Map<String, Image> keyNameValueImage = new HashMap<>();
    private int fromCol = -1;
    private int fromRow = -1;
    private ChessPiece movingPiece;
    private Point movingPiecePoint;
    ChessView(ChessDelegate chessDelegate) {
        this.chessDelegate = chessDelegate;
        String[] imageNames = {
                ChessConstants.bBishop,// "Bishop-black"
                ChessConstants.wBishop, // "Bishop-white"
                ChessConstants.bKing,   // "King-black"
                ChessConstants.wKing,   // "King-white"
                ChessConstants.bKnight, // "Knight-black"
                ChessConstants.wKnight, // "Knight-white"
                ChessConstants.bPawn,   // "Pawn-black"
                ChessConstants.wPawn,   // "Pawn-white"
                ChessConstants.bQueen,  // "Queen-black"
                ChessConstants.wQueen,  // "Queen-white"
                ChessConstants.bRook,   // "Rook-black"
                ChessConstants.wRook    // "Rook-white"
        };

        try {
            for (String imgName : imageNames) {
                Image img = loadImage(imgName + ".png");
                keyNameValueImage.put(imgName, img);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        addMouseListener(this);
        addMouseMotionListener(this);
    }



    @Override
    protected void paintChildren(Graphics g) {
        super.paintChildren(g); // Clears the panel before drawing

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        // Recalculate cell size dynamically based on updated panel size
        int boardSize = Math.min(panelWidth, panelHeight) - 20; // Padding of 10px on each side
        cellSize = boardSize / 8;
        originX = (panelWidth - boardSize) / 2;
        originY = (panelHeight - boardSize) / 2;

        // Use Graphics2D for better control
        Graphics2D g2 = (Graphics2D) g;
        drawBoard(g2, cellSize);
        drawPieces(g2);


    } //image rendering
    public void drawPieces(Graphics2D g2){
        for (int row = 0; row <8 ; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPiece p = chessDelegate.pieceAt(col,row);
                if (p != null && p != movingPiece ){
                    drawImage(g2,col, row,p.imgName);

                }
            }

        }
if (movingPiece != null && movingPiecePoint != null){
    Image img = keyNameValueImage.get(movingPiece.imgName);
    g2.drawImage(img, movingPiecePoint.x-cellSize/2,movingPiecePoint.y-cellSize/2,cellSize,cellSize,null);
}


    }

   

    private void drawImage(Graphics2D g2,int col,int row,String imgName){
        int panelWidth = getWidth();
        int panelHeight = getHeight();

        // Recalculate cell size dynamically based on updated panel size
        int boardSize = Math.min(panelWidth, panelHeight) - 20; // Padding of 10px on each side
        int cellSize = boardSize / 8;

        Image img = keyNameValueImage.get(imgName);
        // Draw the image at a specific position (adjusting to board position)
        g2.drawImage(img, originX + col*cellSize, originY + (7-row)*cellSize, cellSize, cellSize, null);
    }

    private Image loadImage(String imgFileName) throws URISyntaxException, IOException {
        var classLoader = getClass().getClassLoader();
        URL resURL = classLoader.getResource("img/"+imgFileName);

        if (resURL == null) {
            return null;
        } else {
            System.out.println("correct");
            var imgFile = new File(resURL.toURI());
            return ImageIO.read(imgFile);
        }
    }

    private void drawBoard(Graphics2D g2, int cellSize) {
        // Draw the 8x8 chessboard
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                boolean light = (row + col) % 2 == 0; // Alternate colors
                drawSquare(g2, col, row, cellSize, light);
            }
        }
    }

    private void drawSquare(Graphics2D g2, int col, int row, int cellSize, boolean light) {
        g2.setColor(light ? lightWoodColor : darkWoodColor);
        g2.fillRect(originX + col * cellSize, originY + row * cellSize, cellSize, cellSize);
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        fromCol = (e.getPoint().x - originX)/cellSize;
        fromRow = 7-(e.getPoint().y - originY)/cellSize;
        movingPiece = chessDelegate.pieceAt(fromCol,fromRow);

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int col = (e.getPoint().x - originX)/cellSize;
        int row = 7-(e.getPoint().y - originY)/cellSize;
        if(fromCol!=col || fromRow!=row){
            chessDelegate.movePiece(fromCol, fromRow, col, row);
           
        }

        movingPiece = null;
        movingPiecePoint = null;
        repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {
    movingPiecePoint = e.getPoint();
    repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {}
}