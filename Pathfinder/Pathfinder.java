package Pathfinder;

import javax.swing.JFrame;
import java.awt.*;

public class Pathfinder extends JFrame {

    public Pathfinder() {
        initPF();
    }

    private void initPF() {
        setTitle("Pathfinder");
        add(new Board());
        setSize(700,500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> { Pathfinder pf = new Pathfinder(); pf.setVisible(true); });
    }
}