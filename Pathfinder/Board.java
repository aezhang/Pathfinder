package Pathfinder;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class serves as the main board that headings, borders,
 * and other window setting are applied to, to avoid drawing directly on
 * a JFrame.
 *
 * @author Andrew Zhang
 */
public class Board extends JPanel {

    public Board(int w, int h) {
        initBoard(w, h);
    }

    private void initBoard(int w, int h) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel headingPanel = new JPanel();         //creating heading panel
        headingPanel.setBorder(BorderFactory.createLineBorder(Color.black));

        JLabel headingLabel = new JLabel("A* Search");
        headingPanel.add(headingLabel);

        Grid subPanel = new Grid(w, h);

        JButton clr = new JButton("Clear");     //creating Clear JButton
        clr.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                subPanel.clearContents();
            }
        });

        JButton fP = new JButton("Find Path");  //creating Find Path JButton
        fP.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                subPanel.findPath();
            }
        });

        headingPanel.add(clr);
        headingPanel.add(fP);

        add(headingPanel);
        add(subPanel);
    }
}
