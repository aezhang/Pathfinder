package Pathfinder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Point;
import java.lang.Math;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * This class is the main functionality of the Pathfinder.
 * It contains the code behind editing the board and the actual A* algorithm.
 *
 * @author Andrew Zhang
 */
public class Grid extends JPanel {

    /**
     * This boolean keeps track of if the user is dragging the start or the goal.
     */
    private boolean draggingSpecial;

    /**
     * This boolean keeps track of whether the Find Button has been pressed
     * since last clear. After this is set to true, the A* algorithm will be run after
     * every repainting of this JPanel.
     */
    private boolean pathfinding;

    /**
     * This is the width of our board, provided when an instance
     * of this class is created.
     */
    private int width;

    /**
     * This is the height of our board, provided when an instance
     * of this class is created.
     */
    private int height;

    /**
     * This is the number of squares in our grid, equal to
     * width * height.
     */
    private int size;

    /**
     * This is a variable to distinguish if we are moving the goal
     * or starting square, and to make sure there is only one start and goal.
     * It is 2 if we are dragging the start, and 3
     * when dragging the goal.
     */
    private int lastDrag;

    /**
     * This variable is used to make sure we cannot erase the goal or start.
     * It is 2 if we are dragging the start, and 3
     * when dragging the goal.
     */
    private int lastDragVal;

    /**
     * This variable makes sure that when we drag the goal or start tile,
     * duplicates are erased after we drag the tile past a location.
     * This value holds the last known value the lastDragVal had.
     */
    private int prevloc;

    /**
     * The index of the start in contents.
     */
    private int startloc;

    /**
     * The index of the goal in contents.
     */
    private int endloc;

    /**
     * This variable is used to keep track of when a square was
     * added to the PriorityQueue used in this implementation of
     * A* search, since Java breaks ties in priority arbitrarily,
     * and we want LIFO behavior. The first square added (the start)
     * has order 1, the second has order 2, etc.
     */
    private int order;

    /**
     * The representation of the Grid in memory. An index of 0
     * indicates empty space, 1 indicates a wall, 2 indicates a start,
     * 3 indicates a goal, 4 indicates that this index is part of the final shortest path,
     * and 5 indicates this square was inlcuded in the search, but is not in the final
     * shortest path.
     *
     * Index zero is the top left square, and the array is a row-major representation
     * of the Grid.
     */
    private int[] contents;

    /**
     * This array holds the final path that is the shortest found.
     * Because a node may be searched but not included in the final path,
     * the path cannot be built in sequential order. Instead, the completed
     * path is retraced from the goal to the start after a path is found.
     */
    private int[] pathfrom;

    /**
     * This array holds the values necessary for A* to work.
     * path[location] is the array holding the values particular to a specific square.
     * path[location][0] is the fscore of the square.
     * path[location][1] is the gscore of the square.
     * path[location][2] is the hscore of the square, or heuristically estimated
     * distance from >location< to the goal.
     * path[location][3] is the order in which this square was processed.
     */
    private long[][] path;

    /**
     * The constructor for this class. It calls initGrid and adds
     * MouseAdapters to listen for mouse presses.
     *
     * @param w The width of the Grid.
     * @param h The width of the Grid.
     */
    public Grid(int w, int h) {
        initGrid(w, h);
        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point clck = e.getPoint();
                if (clck.x > 10 && clck.x < (width * 20 + 20) - 10 && clck.y > 10
                        && clck.y < (height * 20 + 20) - 10) {                      //check click is in grid

                    int loc = ((clck.x - 10) / 20) + width * ((clck.y - 10) / 20);  //calculate which square was clicked

                    if (contents[loc] < 2) {    //check wall or empty space
                        contents[loc] = 1;
                        repaint();
                    } else if (contents[loc] == 4 || contents[loc] == 5) {
                        contents[loc] = 1;      //check path or searched
                        findPath();
                    } else {
                        draggingSpecial = true;         //dragging goal or start
                        if (contents[loc] == 2) {
                            startloc = loc;
                        } else {
                            endloc = loc;
                        }
                        lastDrag = loc;                 //keep track of what and where we are dragging
                        prevloc = loc;
                        lastDragVal = contents[loc];
                        if (pathfinding) {
                            findPath();
                        }
                    }
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                Point drag = e.getPoint();
                if (drag.x > 10 && drag.x < (width * 20 + 10) && drag.y > 10
                        && drag.y < (height * 20 + 10)) {

                    int loc = ((drag.x - 10) / 20) + width * ((drag.y - 10) / 20);

                    if (contents[loc] < 2 && !draggingSpecial) {
                        contents[loc] = 1;
                        repaint();
                    } else if ((contents[loc] == 4 || contents[loc] == 5) && !draggingSpecial) {
                        contents[loc] = 1;
                        findPath();
                    } else {
                        if (draggingSpecial) {
                            if ((contents[loc] != 3 && lastDragVal == 2) || (lastDragVal == 3 &&
                                    contents[loc] != 2)) {              //check to make sure goal/start isn't erased
                                lastDrag = loc;
                                if (lastDrag != prevloc) {
                                    contents[prevloc] = 0;              //if we dragged goal/start to new square,
                                    prevloc = lastDrag;                 //clear old goal/start and update last location
                                }
                                contents[loc] = lastDragVal;
                                if (lastDragVal == 2) {
                                    startloc = loc;
                                } else {
                                    endloc = loc;
                                }
                                if (pathfinding) {
                                    findPath();
                                } else {
                                    repaint();
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                draggingSpecial = false;            //not draggin goal/start anymore
            }
        };
        addMouseListener(ma);
        addMouseMotionListener(ma);
    }

    private void initGrid(int w, int h) {
        width = w;
        height = h;
        size = w * h;
        lastDrag = -1;
        clearContents();
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(w * 20 + 20, h * 20 + 20));
        repaint();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        if (lastDrag >= 0) {
            contents[lastDrag] = lastDragVal;
            lastDrag = -1;
        }
        for (int k = 0; k < contents.length; k++) {
            if (contents[k] == 1) {
                g2d.fillRect((k % width) * 20 + 10,(k / width) * 20 + 10,20, 20);
            } else if (contents[k] == 2) {
                g2d.setColor(Color.GREEN);
                g2d.fillRect((k % width) * 20 + 10,(k / width) * 20 + 10,20, 20);
            } else if (contents[k] == 3) {
                g2d.setColor(Color.RED);
                g2d.fillRect((k % width) * 20 + 10,(k / width) * 20 + 10,20, 20);
            } else if (contents[k] == 4) {
                g2d.setColor(Color.YELLOW);
                g2d.fillRect((k % width) * 20 + 10,(k / width) * 20 + 10,20, 20);
            } else if (contents[k] == 5) {
                g2d.setColor(Color.PINK);
                g2d.fillRect((k % width) * 20 + 10,(k / width) * 20 + 10,20, 20);
            }
            g2d.setColor(Color.BLACK);
        }
        for (int i = 10; i <= width * 20 + 20; i += 20) {
            g2d.drawLine(i,10, i,(height * 20) + 10);
        }
        for (int j = 10; j <= height * 20 + 20; j += 20) {
            g2d.drawLine(10,j,(width * 20) + 10, j);
        }
        g2d.dispose();
    }

    /**
     * Resets board to initial state with no walls
     * and start and goal in opposite corners.
     *
     * Clears pathfinding and previous path.
     */
    public void clearContents() {
        contents = new int[size];
        contents[0] = 2;
        startloc = 0;
        endloc = size - 1;
        contents[size - 1] = 3;
        pathfinding = false;
        repaint();
    }

    /**
     * The function that implements A* search.
     */
    public void findPath(){
        pathfinding = true;
        order = 1;
        path = new long[size][4];           //clear path
        pathfrom = new int[size];           //clear pathfrom

        for (int j = 0; j < size; j++) {
            if (contents[j] == 4 || contents[j] == 5) {
                contents[j] = 0;            //clear paths, but not walls
            }
        }

        PriorityQueue<Integer> locs = new PriorityQueue<>(size, new scoreComparator());

        path[startloc][3] = order;
        order += 1;
        locs.add(startloc);

        breaker(locs);
    }

    /**
     * This is the main logic of the A* algorithm.
     * It is in another function to allow for a return statement
     * to break a nested loop, hence breaker.
     *
     * @param locs The PriorityQueue with the start added.
     */
    private void breaker(PriorityQueue<Integer> locs) {
        while (!locs.isEmpty()) {

            Integer node = locs.poll();
            ArrayList<Integer> neighbors =  new ArrayList<>();              //dynamically sized list in case neighbor is a wall or out of bounds

            if ((node + 1) % width != 0 && contents[node + 1] != 1) {       //these check if direct neighbors are off the grid
                neighbors.add(node + 1);
            }
            if (node % width != 0 && contents[node - 1] != 1) {
                neighbors.add(node - 1);
            }
            if ((node - width) >= 0 && contents[node - width] != 1) {
                neighbors.add(node - width);
            }
            if ((node + width) < size && contents[node + width] != 1) {
                neighbors.add(node + width);
            }

            for (Integer succ : neighbors) {
                if (endloc == succ) {
                    retracePath(node);
                    return;
                }

                long gscore = path[node][1] + 1;
                long hscore = heuristic(succ);

                if (path[succ][1] == 0 || gscore < path[succ][1]) {
                    pathfrom[succ] = node;
                    path[succ][1] = gscore;
                    path[succ][2] = hscore;
                    path[succ][0] = path[succ][1] + path[succ][2];
                    path[succ][3] = order;
                    order += 1;
                    locs.add(succ);
                }
            }

            if (node != startloc) {
                contents[node] = 5;                                 //mark this square as searched
            }

        }
        repaint();
        JOptionPane.showMessageDialog(null, "No path found!", "Error", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * This functions rebuilds the path starting from the goal and going backwards
     * @param node The node that found the goal
     */
    private void retracePath(Integer node) {
        while (node != startloc) {
            contents[node] = 4;
            node = pathfrom[node];
        }
        repaint();
    }

    /**
     * This function returns the Manhattan Distance from >a< to the goal.
     * Manhattan distance is used because diagonal travel is not allowed,
     * as that would not completely fill one square wide paths around corners and
     * ignore diagonal walls.
     *
     * @param a The index of the square in question.
     * @return The Manhattan Distance from >a< to the goal.
     */
    private int heuristic(Integer a) {
        return Math.abs((endloc % width) - (a % width)) + Math.abs((endloc / width) - (a / width));
    }

    /**
     * This class compares numbers by their fscore, which is
     * the third index value of path[num]. If two numbers
     * have the same fscore, ties are broken using the order they were
     * added to path.
     */
    class scoreComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer o1, Integer o2) {
            if (path[o1][0] == path[o2][0]) {
                if (path[o2][3] - path[o1][3] > 0) {
                    return 1;
                } else if (path[o2][3] == path[o1][3]){
                    return 0;
                } else {
                    return -1;
                }
            } else {
                if (path[o1][0] - path[o2][0] >= 0) {
                    return 1;
                } else {
                    return -1;
                }
            }
        }
    }
}


