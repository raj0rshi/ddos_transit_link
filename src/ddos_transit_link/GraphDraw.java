package ddos_transit_link;

import java.util.*;
import java.awt.*;
import javax.swing.*;

public class GraphDraw extends JFrame {

    int width;
    int height;

    public static ArrayList<Node> nodes;
    public static ArrayList<Edge> edges;

    public GraphDraw(ArrayList nodes, ArrayList edges) { //Construct with label
        this.setTitle("Graph Draw");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBackground(Color.WHITE);
        this.nodes = nodes;
        this.edges = edges;
        width = 25;
        height = 25;

    }

    synchronized public void paint(Graphics g) { // draw the nodes and edges
        // System.out.println("drawing");
        FontMetrics f = g.getFontMetrics();
        int nodeHeight = height;
        Graphics2D g2 = (Graphics2D) g;
        g2.clearRect(this.getX(), this.getY(), this.getWidth(),this.getHeight());
        for (int pp = 0; pp < edges.size(); pp++) {
            Edge e = edges.get(pp);
            g.setColor(e.color);
            Node xx = e.A;
            Node yy = e.B;

            g2.setStroke(new BasicStroke(2));
            if (e.color.getRGB() != Color.white.getRGB()) {
                g.drawLine(xx.x, xx.y, yy.x, yy.y);
            }
            g2.setStroke(new BasicStroke(1));

            g.drawString(e.ID + "", (e.A.x + e.B.x) / 2, (e.A.y + e.B.y) / 2);

        }
          g.setColor(Color.black);
        for (Node n : nodes) {

            int nodeWidth = width;
            g.setColor(n.color);
            g.fillOval(n.x - nodeWidth / 2, n.y - nodeHeight / 2, nodeWidth, nodeHeight);
            if (n.color.getRGB() != Color.black.getRGB()) {
                g.setColor(Color.black);
                g2.setStroke(new BasicStroke(3));
                g.drawOval(n.x - nodeWidth / 2 - 2, n.y - nodeHeight / 2 - 2, nodeWidth + 4, nodeHeight + 4);
            }
            g.setColor(Color.white);
            g.drawOval(n.x - nodeWidth / 2, n.y - nodeHeight / 2, nodeWidth, nodeHeight);
            g.drawString(n.ID + "", n.x - f.stringWidth(n.ID + "") / 2, n.y + f.getHeight() / 2);

        }
    }

    synchronized public void EdgeColor(Edge e, Color c) {

        if (e == null) {
            return;
        }
        e.color = c;
        //this.repaint();

    }

}
