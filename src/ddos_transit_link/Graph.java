/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ddos_transit_link;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 *
 * @author rajor
 */
public class Graph {

    HashMap<Integer, Node> Nodes;
    HashMap<Integer, Edge> Edges;
    HashMap<Integer, Edge> C_Edges;
    HashMap<Integer, Edge> PC_Edges;
    Node V;
    HashMap<Integer, Node> Users;
    HashMap<Integer, Node> Bots;
    HashMap<Integer, Node> Detour;
    HashMap<Integer, Edge> Detour_edges;
    HashMap<Integer, Node> U_N_D;

    Graph() {
        Nodes = new HashMap<Integer, Node>();
        Users = new HashMap<Integer, Node>();
        U_N_D = new HashMap<Integer, Node>();
        Detour = new HashMap<Integer, Node>();
        Bots = new HashMap<Integer, Node>();
        Edges = new HashMap<Integer, Edge>();
        C_Edges = new HashMap<Integer, Edge>();
        PC_Edges = new HashMap<Integer, Edge>();
        Detour_edges = new HashMap<Integer, Edge>();
    }

    void ReadFile() throws FileNotFoundException {
        Scanner scn = new Scanner(new File(Constants.TOPOLOGY_FILE_EDGES));
        int eID = 0;
        while (scn.hasNext()) {
            String line = scn.nextLine();
            //      System.out.println(line);
            StringTokenizer strtok = new StringTokenizer(line, " ");
            int v1 = Integer.parseInt(strtok.nextToken().trim());
            int v2 = Integer.parseInt(strtok.nextToken().trim());
            //     System.out.println(v1+"-"+v2);
            //  if(v1>133){System.out.println("wrong v1");}
            //if(v2>133){System.out.println("wrong v2");}

            Node n1 = null;
            if (!Nodes.containsKey(v1)) {
                n1 = new Node(v1);
                Nodes.put(v1, n1);
            } else {
                n1 = Nodes.get(v1);

            }
            Node n2 = null;
            if (!Nodes.containsKey(v2)) {
                n2 = new Node(v2);
                Nodes.put(v2, n2);
            } else {
                n2 = Nodes.get(v2);
            }

            // System.out.println(v1+"-"+v2);
            n1.Neighbors.put(v2, n2);
            n2.Neighbors.put(v1, n1);
            Edge e = new Edge(n1, n2, eID);
            Edges.put(eID++, e);
            n1.Edges.put(e.ID, e);
            n2.Edges.put(e.ID, e);

            if (C_Edges.containsKey(e.ID)) {
                e.color = Color.RED;
                e.congested = true;
                C_Edges.put(e.ID, e);
            }
        }

        scn = new Scanner(new File(Constants.TOPOLOGY_FILE_NODES));
        // System.out.println(Nodes.keySet());
        while (scn.hasNext()) {
            String line = scn.nextLine();
            StringTokenizer strtok = new StringTokenizer(line);
            int ID = Integer.parseInt(strtok.nextToken());
            int x = Integer.parseInt(strtok.nextToken());
            int y = Integer.parseInt(strtok.nextToken());
            String Type = strtok.nextToken();

            //    System.out.println("ID:" + ID);
            Nodes.get(ID).x = x;
            Nodes.get(ID).y = y;
            Nodes.get(ID).Type = Type;
            if (Type.equals("A")) {
                Nodes.get(ID).color = Color.red;
                Bots.put(ID, Nodes.get(ID));
            }
            if (Type.equals("U")) {
                Nodes.get(ID).color = Color.green;
                Users.put(ID, Nodes.get(ID));
            }
            if (Type.equals("V")) {
                Nodes.get(ID).color = Color.BLUE;
                V = Nodes.get(ID);
            }
        }

    }

    public void markCOngested(int eid) {
        if (Edges.containsKey(eid)) {
            C_Edges.put(eid, Edges.get(eid));
            Edges.get(eid).congested = true;
            Edges.get(eid).color = Color.RED;
        } else {
            C_Edges.put(eid, null);
        }

    }

    public void Draw() {
        GraphDraw frame = new GraphDraw(new ArrayList<>(Nodes.values()), new ArrayList<>(Edges.values()));
        // System.out.println("N" + Nodes.size());
        //System.out.println("E" + Edges.size());
        frame.setSize(1200, 800);
        //frame.setAlwaysOnTop(true);
        frame.setVisible(true);
    }

    Graph Transform() {
        Graph G = new Graph();
        for (Edge e : Edges.values()) {
            Node n = new Node(e.ID);
            n.x = (e.A.x + e.B.x) / 2;
            n.y = (e.A.y + e.B.y) / 2;
            if (e.A.Type.equals("A") || e.B.Type.equals("A")) {
                n.Type = "A";
                n.color = Color.RED;
                G.Bots.put(n.ID, n);
            }
            if (e.A.Type.equals("V") || e.B.Type.equals("V")) {
                n.Type = "V";
                n.color = Color.BLUE;
                G.V = n;
            }
            if (e.A.Type.equals("U") || e.B.Type.equals("U")) {
                n.Type = "U";
                n.color = Color.GREEN;
                G.Users.put(n.ID, n);
            }
            if (e.congested) {
                n.color = Color.black;
            }
            if (e.color == Color.ORANGE) {
                n.color = Color.ORANGE;
            }
            G.Nodes.put(n.ID, n);
        }
        //  System.out.println(G.Nodes.keySet());

        int EID = 0;
        for (Node n : Nodes.values()) {
            for (int i = 0; i < n.Edges.size(); i++) {
                for (int j = i + 1; j < n.Edges.size(); j++) {
                    //   System.out.println("node id: "+ n.ID);
                    ArrayList<Edge> eee = new ArrayList<Edge>(n.Edges.values());
//                    System.out.println("links connected:"+ n.Edges.keySet());
//                    System.out.println("i:"+i+"-" + eee.get(i).ID);
//                    System.out.println("j:"+j+"-" + eee.get(j).ID);
                    Node I = G.Nodes.get(eee.get(i).ID);
                    Node J = G.Nodes.get(eee.get(j).ID);

                    Edge e = new Edge(I, J, EID++);
                    G.Edges.put(e.ID, e);
                    I.Edges.put(e.ID, e);
                    J.Edges.put(e.ID, e);
                    I.Neighbors.put(J.ID, J);
                    J.Neighbors.put(I.ID, I);
                }
            }
        }
        return G;
    }

    void calculateRT() {
        boolean[] Visited = new boolean[Nodes.size()];
        for (int i = 0; i < Visited.length; i++) {
            Visited[i] = false;
        }
        for (Node n : Nodes.values()) {
            n.NH.clear();
            n.N_PATH = 0;
            n.NC_PATH = 0;
        }
        HashMap<Integer, Node> Q = new HashMap<Integer, Node>();
        Q.put(V.ID, V);
        Visited[V.ID] = true;
        HashMap<Integer, Node> Q2 = new HashMap<Integer, Node>();

        int d = 0;
        while (!Q.isEmpty()) {
            for (Node n : Q.values()) {
                //  System.out.println("calc for:" + n.ID + n.Neighbors.keySet());
                for (Node n2 : n.Neighbors.values()) {
                    if (!Visited[n2.ID]) {
                        Q2.put(n2.ID, n2);
                        n2.NH.add(n.ID);
                    }
                }

                if (n.ID == V.ID) {
                    n.NC_PATH = 0;
                    n.N_PATH = 1;
                    n.D = 0;
                } else {
                    for (int n1 : n.NH) {
                        n.D = Nodes.get(n1).D + 1;
                        n.N_PATH += Nodes.get(n1).N_PATH;
                        //    System.out.println(n.Edges.values());
                        Edge e = n.getLink(n1);
                        //    System.out.println(n.ID + "-" + n1);
                        //     System.out.println(e);
                        if ((e.congested) || (Nodes.get(n1).color == Color.black)) {
                            n.NC_PATH += Nodes.get(n1).N_PATH;
                        } else {
                            n.NC_PATH += Nodes.get(n1).NC_PATH;
                        }
                    }

                }
            }
            for (Node n : Q2.values()) {
                Visited[n.ID] = true;
            }
            // System.out.println("Q2:" + Q2.keySet());
//            for (int i = 0; i < Visited.length; i++) {
//                System.out.print(i+"["+Visited[i]+"]");
//            }
//            System.out.println("");

            Q.clear();
            Q.putAll(Q2);
            Q2.clear();
        }
    }

    void printRT() {
        System.out.println("************printing routing table*************");
        for (Node n : Nodes.values()) {
            System.out.println("NID:" + n.ID);
            System.out.println(n.D + "" + n.NH);
            System.out.println("P:" + n.N_PATH + "\t CP:" + n.NC_PATH);
        }
    }

    void calculateDetourNeedingNodes() {
        for (Node n : Nodes.values()) {
            if ((n.NC_PATH == n.N_PATH) && (n.Type.equals("U"))) {
                U_N_D.put(n.ID, n);
            }
        }
    }

    void calculatePossibleCongestedNodes() {

        for (Node u : Users.values()) {
            ArrayList<Edge> path = getPath(u);
            boolean flag = false;
            for (int i = path.size() - 1; i > 0; i--) {
                Edge e = path.get(i);
                if (C_Edges.containsKey(e.ID)) {
                    flag = true;
                }
                if (flag) {
                    e.color = Color.red;
                    e.congested = true;
                    PC_Edges.put(e.ID, e);
                }
            }
        }

    }

    ArrayList<Edge> getPath(Node n) {
        ArrayList<Edge> p = new ArrayList<Edge>();
        Node nh = null;
        if (n.ID != V.ID) {
            nh = Nodes.get(n.NH.get(n.NH.size() - 1));

            Edge e = n.getLink(nh);
            //    System.out.println(e.ID);
            p.add(e);
            p.addAll(getPath(nh));
        }
        return p;
    }

    void removeEdge(Edge e) {
        Edges.remove(e.ID);
//        System.out.println("removing edge:" + e);
//        System.out.println("neighbor:" + e.A + ":" + e.A.Neighbors.values());
//        System.out.println("neighbor:" + e.B + ":" + e.B.Neighbors.values());
        e.A.Neighbors.remove(e.B.ID);
        e.B.Neighbors.remove(e.A.ID);
        e.A.Edges.remove(e.ID);
        e.B.Edges.remove(e.ID);

//        System.out.println("neighbor:" + e.A + ":" + e.A.Neighbors.values());
//        System.out.println("neighbor:" + e.B + ":" + e.B.Neighbors.values());
    }

    void addEdge(Edge e) {
        Edges.put(e.ID, e);
        e.A.Neighbors.put(e.B.ID, e.B);
        e.B.Neighbors.put(e.A.ID, e.A);
        e.A.Edges.put(e.ID, e);
        e.B.Edges.put(e.ID, e);
    }

    void calculateNodesAndEdgesOnDetour() {

        for (Edge e : PC_Edges.values()) {
            removeEdge(e);
        }

        for (Node n : Nodes.values()) {
            //  System.out.println(n.ID + ":" + n.Neighbors.values());
        }
        calculateRT();
        //printRT();

        System.out.println(U_N_D.values());
        for (Node u : U_N_D.values()) {
            ArrayList<Edge> path = getPath(u);
            for (Edge e : path) {
                Detour_edges.put(e.ID, e);
                Detour.put(e.A.ID, e.A);
                Detour.put(e.B.ID, e.B);
            }
        }

        ArrayList<Node> dns = new ArrayList<Node>(Detour.values());
        for (Node n : dns) {
            if (!n.Type.equals("R")) {
                Detour.remove(n.ID);
                if (n.Type.equals("V")) {
                    for (Node nn : n.Neighbors.values()) {
                        Detour.remove(nn.ID);
                    }
                }
            }
        }
        ArrayList<Edge> degs = new ArrayList<Edge>(Detour_edges.values());
        for (Edge e : degs) {
            if (e.A.Type.equals("R") && e.B.Type.equals("R")) {
            } else {
                Detour_edges.remove(e.ID);
            }
        }

        for (Node n : Detour.values()) {
            n.color = Color.ORANGE;
        }
        for (Edge e : Detour_edges.values()) {
            e.color = Color.ORANGE;
        }
        System.out.println("detour:" + Detour.values());
        for (Edge e : PC_Edges.values()) {
            addEdge(e);
        }
        calculateRT();
        //printRT();
    }

    void addVirtualUser() {

        int N = Collections.max(Nodes.keySet()) + 1;

        int EID = Collections.max(Edges.keySet()) + 1;
        for (Node d : Detour.values()) {

            boolean flag = false;
            for (Node nei : d.Neighbors.values()) {
                if (nei.Type.equals("U")) {
                    flag = true;
                }
            }
            if (!flag) {
                Node u = new Node(N++);
                Nodes.put(u.ID, u);
                u.x = d.x + 40;
                u.y = d.y + 40;
                u.Type = "U";

                Edge e = new Edge(u, d, EID++);
                addEdge(e);
                u.color = Color.ORANGE;
            }
        }
    }

    void createFlow(Node u) {
        boolean[] CF = new boolean[Nodes.size()];
        for (int i = 0; i < CF.length; i++) {
            CF[i] = false;
        }
        ArrayList<Integer> NH = new ArrayList<Integer>();
        ArrayList<Integer> NH2 = new ArrayList<Integer>();
        NH.add(u.ID);
        while (!NH.isEmpty()) {
            Node n = Nodes.get(NH.remove(0));
            NH.addAll(n.NH);
            for (int i : n.NH) {
                Node nn = Nodes.get(i);
                if (nn.color == Color.black || CF[n.ID]) {
                    CF[i] = true;
                }
                Edge e = n.getLink(i);

                if (n.color == Color.black || CF[n.ID]) {

                    e.color = Color.red;
                } else {
                    e.color = Color.green;
                }
            }

        }

    }

    void createDetourFlow(Node u) {
        boolean[] CF = new boolean[Nodes.size()];
        for (int i = 0; i < CF.length; i++) {
            CF[i] = false;
        }
        ArrayList<Integer> NH = new ArrayList<Integer>();
        NH.add(u.ID);
        while (!NH.isEmpty()) {
            Node n = Nodes.get(NH.remove(0));

            int Min_Det_D = Integer.MAX_VALUE;

            //find min detour distance
            for (Node neigh : n.Neighbors.values()) {
                if (neigh.color == Color.orange) {
                    if (Min_Det_D > neigh.D) {
                        Min_Det_D = neigh.D;
                    }
                }
            }
            for (Node neigh : n.Neighbors.values()) {
                if (Min_Det_D >= neigh.D) {
                    NH.add(neigh.ID);
                }
            }

            for (int i : n.NH) {
                Node nn = Nodes.get(i);
                if (nn.color == Color.black || CF[n.ID]) {
                    CF[i] = true;
                }
                Edge e = n.getLink(i);

                if (n.color == Color.black || CF[n.ID]) {

                    e.color = Color.red;
                } else {
                    e.color = Color.green;
                }
            }

        }

    }
}
