package Pathfinder;

import javax.swing.JFrame;
import java.awt.EventQueue;


/**
 * This is the main class of my pathfinding program. Calling main simply
 * creates a new instance of this class in the Event Dispatch Thread.
 *
 * The arguments to main should be the width and height of the board that walls and
 * goals are placed in.
 *
 * The basic structure for this project is roughly based on the game tutorial basics by J
 * an Bodnar at zetcode.com/javagames/basics/, and code for drawing on JPanels was learned
 * from code provided by MadProgrammer on StackOverflow.
 * @author Andrew Zhang
 */
public class Pathfinder extends JFrame {

    /**
     * The constructor for the Pathfinder class.
     * Simply calls the init function.
     *
     * @param w The width of the Board.
     * @param h The height of the Board.
     */
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