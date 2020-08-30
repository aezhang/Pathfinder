package Pathfinder;

import javax.swing.JFrame;
import java.awt.EventQueue;

public class Pathfinder extends JFrame {

    public Pathfinder(int w, int h) {
        initPF(w, h);
    }

    private void initPF(int w, int h) {
        setTitle("Pathfinder");
        add(new Board(w, h));
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        try {
            int w = Integer.parseInt(args[0]);
            int h = Integer.parseInt(args[1]);
            EventQueue.invokeLater(() -> { Pathfinder pf = new Pathfinder(w,h); pf.setVisible(true); });
        } catch (NumberFormatException e) {
            System.out.println("Arguments must be numbers!");
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Invalid number of arguments!");
        }
    }
}