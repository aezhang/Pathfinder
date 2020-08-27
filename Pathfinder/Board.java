package Pathfinder;

import javax.swing.*;
import java.awt.*;

public class Board extends JPanel {
    public Board() {
        initBoard();
    }

    private void initBoard() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel headingPanel = new JPanel();
        JLabel headingLabel = new JLabel("This is the heading panel for our demo course");
        headingPanel.add(headingLabel);

        JPanel subPanel = new JPanel();
        subPanel.setLayout(new GridBagLayout());
        subPanel.setBorder(BorderFactory.createLineBorder(Color.red));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.anchor = GridBagConstraints.NORTH;

        constraints.gridx = 0;
        constraints.gridy = 0;

        JLabel userNameLabel = new JLabel("Enter your name :");
        JLabel pwdLabel = new JLabel("Enter your password :");
        JLabel emailLabel = new JLabel("Enter email :");
        subPanel.add(userNameLabel, constraints);
        constraints.gridx = 0;
        constraints.gridy = 1;
        subPanel.add(pwdLabel, constraints);
        constraints.gridx = 0;
        constraints.gridy = 2;
        subPanel.add(emailLabel, constraints);
        constraints.gridx = 0;
        constraints.gridy = 3;

        add(headingPanel);
        add(subPanel);
    }
}
