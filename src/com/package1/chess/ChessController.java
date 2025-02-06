package com.package1.chess;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChessController implements ChessDelegate, ActionListener{
    private String SOCKET_SERVER_ADDR = "localhost";
    private int PORT = 50000;

    private ChessModel chessModel = new ChessModel();

    private JFrame frame;
    private ChessView chessBoardPanel;
    private JButton resetBtn;
    private JButton serverBtn;
    private JButton clientBtn;

    private ServerSocket listener;
    private Socket socket;
    private PrintWriter printWriter;

    ChessController(){
        chessModel.reset();


        frame = new JFrame("Chess Game");
        frame.setSize(600,600);
        frame.setLocation(0,1300);
        frame.setLayout(new BorderLayout());
        chessBoardPanel = new ChessView(this);
        frame.add(chessBoardPanel, BorderLayout.CENTER);

        var buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        resetBtn = new JButton("Reset");
        resetBtn.addActionListener(this);
        buttonsPanel.add(resetBtn);

        serverBtn = new JButton("Listen");
        buttonsPanel.add(serverBtn);
        serverBtn.addActionListener(this);

        clientBtn = new JButton("Connect");
        buttonsPanel.add(clientBtn);
        clientBtn.addActionListener(this);

        frame.add(buttonsPanel,BorderLayout.PAGE_END);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
               printWriter.close();
                try {
                    socket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

    }
    public static void main(String[] args) {

        new ChessController();
    }

    @Override
    public ChessPiece pieceAt(int col, int row) {

        return chessModel.pieceAt(col, row);
    }

    public void movePiece(int fromCol, int fromRow, int toCol, int toRow){
     chessModel.movePiece(fromCol,fromRow,toCol,toRow);
     chessBoardPanel.repaint();
     if (printWriter !=null){
         printWriter.println(fromCol+","+fromRow+","+toCol+","+toRow);
     }

    }
    private void receiveMove(Scanner scanner){
        while (scanner.hasNextLine()){
            var moveStr = scanner.nextLine(); //"0,1,0,2"
            System.out.println("Chess move received:"+ moveStr);

            var moveStrArr = moveStr.split(",") ;//["0","1","0","2"]
            var fromCol = Integer.parseInt(moveStrArr[0]) ;
            var fromRow = Integer.parseInt(moveStrArr[1]);
            var toCol = Integer.parseInt(moveStrArr[2]);
            var toRow = Integer.parseInt(moveStrArr[3]);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    chessModel.movePiece(fromCol,fromRow,toCol,toRow);
                    chessBoardPanel.repaint();
                }
            });
        }
    }

    private void runSocketServer(){
        Executors.newFixedThreadPool(1).execute(new Runnable() {
            @Override
            public void run() {
                try{
                    listener = new ServerSocket(PORT);
                    System.out.println("server is listening on port"+PORT);

                    socket = listener.accept();
                    printWriter = new PrintWriter(socket.getOutputStream(),true);
                    var scanner = new Scanner(socket.getInputStream());

                    receiveMove(scanner);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    private void runSocketClient(){
        try {
            socket = new Socket(SOCKET_SERVER_ADDR,PORT);
            System.out.println("client connected to port"+PORT);
            var scanner = new Scanner(socket.getInputStream());
            printWriter = new PrintWriter(socket.getOutputStream(),true);

            Executors.newFixedThreadPool(1).execute(new Runnable() {
                @Override
                public void run() {
                    receiveMove(scanner);
                }
            });

        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
       // System.out.println(e.getSource());
        if(e.getSource() == resetBtn){
            chessModel.reset();
            chessBoardPanel.repaint();
            try {
                if (listener !=null){
                    listener.close();
                }
                if(socket != null){
                socket.close();
                }
                serverBtn.setEnabled(true);
                clientBtn.setEnabled(true);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } else if(e.getSource()== serverBtn){
            serverBtn.setEnabled(false);
            clientBtn.setEnabled(false);
            frame.setTitle("Chess Server");
            runSocketServer();
            JOptionPane.showMessageDialog(frame,"listening on port "+PORT);
        }
        else if(e.getSource()== clientBtn){
            serverBtn.setEnabled(false);
            clientBtn.setEnabled(false);
            frame.setTitle("Chess Client");
            runSocketClient();
            JOptionPane.showMessageDialog(frame,"connected to port "+PORT);
        }

    }

}
