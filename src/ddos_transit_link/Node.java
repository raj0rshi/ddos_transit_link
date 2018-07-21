/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ddos_transit_link;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author rajor
 */
public class Node {

    int ID;
    int x;
    int y;
    String Type = "R";
    Color color = Color.GRAY;
    HashMap<Integer, Node> Neighbors;
    HashMap<Integer, Edge> Edges;

    int N_PATH = 0;
    int NC_PATH = 0;

    ArrayList<Integer> RT;
    ArrayList<Integer> NH;
    int D = -1;

    public Node(int ID) {
        this.ID = ID;
        Neighbors = new HashMap<Integer, Node>();
        Edges = new HashMap<Integer, Edge>();
        RT = new ArrayList<Integer>();
        NH = new ArrayList<Integer>();
    }

    Edge getLink(int ID) {
        Edge er = null;

        for (Edge e : Edges.values()) {
            if ((e.A.ID == this.ID) && (e.B.ID == ID)) {
                er = e;
            }
            if ((e.B.ID == this.ID) && (e.A.ID == ID)) {
                er = e;
            }
        }
        return er;
    }

    Edge getLink(Node n) {
        int ID = n.ID;
        Edge er = null;

        for (Edge e : Edges.values()) {
            if ((e.A.ID == this.ID) && (e.B.ID == ID)) {
                er = e;
            }
            if ((e.B.ID == this.ID) && (e.A.ID == ID)) {
                er = e;
            }
        }
        return er;
    }

    @Override
    public String toString() {
        return ID + "";
    }
}
