package Pathfinder;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Board extends JPanel {
    public Board(int w, int h) {
        initBoard(w, h);
    }

    private void initBoard(int w, int h) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel headingPanel = new JPanel();
        headingPanel.setBorder(BorderFactory.createLineBorder(Color.black));

        JLabel headingLabel = new JLabel("A* Search");

        headingPanel.add(headingLabel);

        Grid subPanel = new Grid(w, h);

        JButton clr = new JButton("Clear");

        clr.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                subPanel.clearContents();
            }
        });

        JButton fP = new JButton("Find Path");

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
