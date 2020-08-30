package Pathfinder;

import javax.swing.JPanel;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.Math;
import javax.swing.JOptionPane;

public class Grid extends JPanel {

    private int[] contents;
    private int[] pathfrom;
    private long[][] path;
    private int size;
    private int width;
    private int height;
    private int lastDrag;
    private int lastDragVal;
    private boolean draggingSpecial;
    private int prevloc;
    private int startloc;
    private int endloc;
    private int order;
    private boolean pathfinding;

    public Grid(int w, int h) {
        initGrid(w, h);
        MouseAdapter ma = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point clck = e.getPoint();
                if (clck.x > 10 && clck.x < (width * 20 + 20) - 10 && clck.y > 10 && clck.y < (height * 20 + 20) - 10) {
                    int loc = ((clck.x - 10) / 20) + width * ((clck.y - 10) / 20);
                    if (contents[loc] < 2) {
                        contents[loc] = 1;
                    } else if (contents[loc] == 4) {
                        contents[loc] = 1;
                        findPath();
                    } else {
                        for (int j = 0; j < size; j++) {
                            if (contents[j] == 4) {
                                contents[j] = 0;
                            }
                        }
                        draggingSpecial = true;
                        if (contents[loc] == 2) {
                            startloc = loc;
                        } else {
                            endloc = loc;
                        }
                        lastDrag = loc;
                        prevloc = loc;
                        lastDragVal = contents[loc];
                    }
                    repaint();
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                Point drag = e.getPoint();
                if (drag.x > 10 && drag.x < (width * 20 + 10) && drag.y > 10 && drag.y < (height * 20 + 10)) {
                    int loc = ((drag.x - 10) / 20) + width * ((drag.y - 10) / 20);
                    if (contents[loc] < 2 && !draggingSpecial) {
                        contents[loc] = 1;
                        repaint();
                    } else if (contents[loc] == 4 && !draggingSpecial) {
                        contents[loc] = 1;
                        repaint();
                        findPath();
                    } else {
                        lastDrag = loc;
                        if (lastDrag != prevloc) {
                            contents[prevloc] = 0;
                            prevloc = lastDrag;
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

            @Override
            public void mouseReleased(MouseEvent e) {
                draggingSpecial = false;
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

    public void clearContents() {
        contents = new int[size];
        contents[0] = 2;
        startloc = 0;
        endloc = size - 1;
        contents[size - 1] = 3;
        pathfinding = false;
        repaint();
    }
    public void findPath(){
        pathfinding = true;
        order = 1;
        path = new long[size][4];
        pathfrom = new int[size];
        for (int j = 0; j < size; j++) {
            if (contents[j] == 4) {
                contents[j] = 0;
            }
        }
        PriorityQueue<Integer> locs = new PriorityQueue<>(size, new scoreComparator());
        path[startloc][3] = order;
        order += 1;
        locs.add(startloc);
        breaker(locs);

    }

    private void breaker(PriorityQueue<Integer> locs) {
        while (!locs.isEmpty()) {
            Integer node = locs.poll();
            ArrayList<Integer> neighbors =  new ArrayList<>();
            if ((node + 1) % width != 0 && contents[node + 1] != 1) {
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
            for (int i = 0; i < neighbors.size(); i++) {
                Integer succ = neighbors.get(i);
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
        }
        JOptionPane.showMessageDialog(null, "No path found!", "Error", JOptionPane.INFORMATION_MESSAGE);
    }

    private void retracePath(Integer node) {
        while (node != 0) {
            contents[node] = 4;
            node = pathfrom[node];
        }
        repaint();
    }

    private int heuristic(Integer a) {
        return Math.abs((endloc % width) - (a % width)) + Math.abs((endloc / width) - (a / width));
    }

    class scoreComparator implements Comparator<Integer> {
        @Override
        public int compare(Integer o1, Integer o2) {
            if (path[o1][0] == path[o2][0]) {
                if (path[o2][3] - path[o1][3] >= 0) {
                    return 1;
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


