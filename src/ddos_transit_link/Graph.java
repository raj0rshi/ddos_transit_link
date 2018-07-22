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
import java.util.HashSet;
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
        frame.setSize(1000, 1000);
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
        
        
        boolean[] Visited = new boolean[Collections.max(Nodes.keySet())+1];
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
    
    void saveDetourRT() {
        for (Node n : Nodes.values()) {
            n.D_D = n.D;
            //   System.out.println("n.DD");
            n.NH_D.addAll(n.NH);
            n.NC_PATH_D = n.NC_PATH;
            n.N_PATH = n.NC_PATH_D;
        }
    }
    
    void printRT() {
        System.out.println("************printing routing table*************");
        for (Node n : Nodes.values()) {
            System.out.println("NID:" + n.ID);
            System.out.println(n.D + "" + n.NH + "\t" + n.D_D + "" + n.NH_D);
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
           // System.out.println("U:" + u + "" + path);
            boolean flag = false;
            for (Edge e : path) {
                if (C_Edges.containsKey(e.ID)) {
                    flag = true;
                }
            }
            if (flag) {
                for (Edge e : path) {
                    if ((e.color != Color.green)
                            && (e.A.Type.equals("R"))
                            && (e.B.Type.equals("R"))) {
                        e.color = Color.red;
                        e.congested = true;
                        PC_Edges.put(e.ID, e);
                    }
                }
            } else {
                
                for (Edge e : path) {
                    
                    e.color = Color.green;
                    e.congested = false;
                    PC_Edges.remove(e.ID);
                    
                }
            }
        }
        
    }
    
    ArrayList<Edge> getPath(Node n) {
      //  System.out.println("path to: " + n + n.NH);
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
    void addEdgeCheckDuplicate(Edge e) {
        
        for(Edge ee: Edges.values())
        {
            if((ee.A.ID==e.A.ID)&& (ee.B.ID==e.B.ID))
            {  return ;}
            if((ee.A.ID==e.B.ID)&& (ee.B.ID==e.A.ID))
            {  return ;}
            
        }
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
        
        calculateRT();
        saveDetourRT();
        // printRT();

       // System.out.println(U_N_D.values());
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
   //     System.out.println("detour:" + Detour.values());
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
                e.setDirection(nn);
                if (n.color == Color.black || CF[n.ID]) {
                    
                    e.color = Color.red;
                } else if (e.color != Color.RED) {
                    e.color = Color.green;
                }
            }
            
        }
        
    }
    
    ArrayList<Integer> findNH(Node n, int D) {
        ArrayList<Integer> r = new ArrayList<Integer>();
        
        if (n.ID == V.ID) {
            return r;
        }
        int mind = D;
        for (Node nn : n.Neighbors.values()) {
            if (nn.ID == V.ID) {
                r.add(V.ID);
                return r;
            }
            if ((mind >= nn.D) && (nn.color == Color.orange)) {
                mind = nn.D;
            }
        }
        
        for (Node nn : n.Neighbors.values()) {
            if (nn.D <= mind) {
                r.add(nn.ID);
            }
        }
        return r;
    }
    
    void createDetourFlow(Node u) {
        
        boolean[] CF = new boolean[Nodes.size()];
        boolean[] V = new boolean[Nodes.size()];
        for (int i = 0; i < CF.length; i++) {
            CF[i] = false;
        }
        HashSet<Integer> NH = new HashSet<Integer>();
        
        NH.add(u.ID);
        HashSet<Integer> NH2 = new HashSet<Integer>();
        
        int mind = Integer.MAX_VALUE;
        for (Node nn : u.Neighbors.values()) {
            if ((mind >= nn.D) && (nn.color == Color.orange)) {
                mind = nn.D;
            }
        }
        while (!NH.isEmpty()) {
            for (int n : NH) {
                Node N = Nodes.get(n);
                ArrayList<Integer> nh = findNH(N, mind);
                
                for (int nn : nh) {
                    Node NN = Nodes.get(nn);
                    Edge e = N.getLink(NN);
                    e.setDirection(NN);
                    if (NN.color == Color.black || CF[N.ID]) {
                        CF[NN.ID] = true;
                    }
                    if (N.color == Color.black || CF[N.ID]) {
                        
                        e.color = Color.red;
                    } else if (e.color != Color.red) {
                        e.color = Color.green;
                    }
                    
                    if (!V[NN.ID]) {
                        NH2.add(NN.ID);
                    }
                }
                V[N.ID] = true;
                
            }
            NH.clear();
            NH.addAll(NH2);
            NH2.clear();
            mind--;
        }
    }
    
    Graph TransForm2() {
        Graph G2 = new Graph();
        
        
        for (Edge e : Edges.values()) {
            if (e.color == Color.RED) {
                Node A =G2.Nodes.getOrDefault(e.A.ID, new Node(e.A.ID));
                A.x = e.A.x;
                A.y = e.A.y;
                A.color = e.A.color;
                A.Type = e.A.Type;
                
                Node B = G2.Nodes.getOrDefault(e.B.ID, new Node(e.B.ID));
                B.x = e.B.x;
                B.y = e.B.y;
                B.color = e.B.color;
                B.Type = e.B.Type;
                
                G2.Nodes.put(A.ID, A);
                G2.Nodes.put(B.ID, B);
                
                Edge ee = new Edge(A, B, e.ID);
                ee.color = e.color;
                G2.addEdge(ee);
             //   System.out.println("Edge added:"+ee);
             //   System.out.println("A Edges:"+ A.Edges.keySet());
             //   System.out.println("B Edges:"+ B.Edges.keySet());
                
            }
        }
        G2.V =G2.Nodes.get(V.ID);
        for(Edge e: G2.Edges.values())
        {
           // System.out.println(e+"["+e.A+","+e.B+"]");
        }
        for(Node n: G2.Nodes.values())
        {
           // System.out.println("N:"+n+"="+n.Edges.values());
        }
        
        int EID = Collections.max(G2.Edges.keySet()) + 1;
         int NID = Collections.max(G2.Nodes.keySet()) + 1;
        ArrayList<Node> g2nodes=new ArrayList<Node>(G2.Nodes.values());
        for (Node n : g2nodes) {
           //  System.out.println(n+""+n.Edges.keySet());
            if (n.color != Color.BLACK) {
                if (n.ID != V.ID) {
                    //for all white nodes
                    HashMap<Integer, Node> IN = new HashMap<Integer, Node>();
                    HashMap<Integer, Node> OUT = new HashMap<Integer, Node>();
                    
                  //  System.out.println(n.Edges);
                    for (Edge e : n.Edges.values()) {
                       
                        if (e.B.ID == n.ID) {
                            IN.put(e.A.ID, e.A);
                        } else {
                            OUT.put(e.B.ID, e.B);
                        }
                    }
                 //   System.out.println(n);
                 //   System.out.println("IN"+IN.keySet());
                 //   System.out.println("OUT"+OUT.keySet());
                    
                    for (Node a : IN.values()) {
                        for (Node b : OUT.values()) {
                            Edge e = new Edge(a, b, EID++);
                            e.color = Color.RED;
                            e.congested = true;
                            G2.addEdgeCheckDuplicate(e);
                         //   System.out.println("edge added:"+e);
                        }
                    }
                    
                    G2.Nodes.remove(n.ID);
                    ArrayList<Node> nnn=new ArrayList<Node>(n.Neighbors.values());
                    for(Node nn: nnn)
                    {
                        nn.Neighbors.remove(n.ID);
                    }
                    ArrayList<Edge> eee=new ArrayList<Edge>(n.Edges.values());
                    for(Edge e: eee)
                    {
                        G2.Edges.remove(e.ID);
                        e.A.Edges.remove(e.ID); 
                        e.B.Edges.remove(e.ID);
                    }
                    
                }
            }
        }
        
        Node U=new Node(NID);
        U.Type="U";
        U.color=Color.green;
        U.x=0;
        U.y=0;
        int count=0;
        G2.Nodes.put(U.ID, U);
        for(Node n: G2.Nodes.values())
        {
            if(n.color==Color.black)
            {
                boolean flag=false;
                for(Edge e: Nodes.get(n.ID).Edges.values())
                {
                    if(e.color==Color.green)
                    {
                        flag=true;
                        U.x=Math.max(n.x,U.x);
                        U.y+=e.A.y;
                        count++;
                    }
                }
              if(flag){  
                Edge e=new Edge(U, n, EID++);
                e.color=Color.green;
                G2.addEdge(e);
              }
            }
        }
        U.y=U.y/count;
        U.x+=50;
        return G2;
    }
    
    
    
    void RemoveNode(Node n)
    {
        for(Node nn: n.Neighbors.values())
        {
            Edge e=nn.getLink(n);
            nn.Edges.remove(e.ID);
            Edges.remove(e.ID);
            nn.Neighbors.remove(n.ID);
            Nodes.remove(n.ID);
        }
    }
    void AddNode(Node n)
    {
        for(Node nn: n.Neighbors.values())
        {
            Edge e=n.getLink(nn);
            nn.Edges.put(e.ID,e);
            Edges.put(e.ID, e);
            nn.Neighbors.put(n.ID,n);
              Nodes.put(n.ID,n);
        }
    }
  
    
  int heavy_link()
  {      
      HashMap<Integer, Integer> EC = new HashMap<Integer, Integer>();
      for (Node u : Nodes.values()) {
          if(u.Type.equals("U")){
          ArrayList<Edge> path = getPath(u);
          for (Edge e : path) {

 {
                  int x = EC.getOrDefault(e.ID, 0);
                  x++;
                  EC.put(e.ID, x);
              }
          }}
      }
      int max=-1;
      System.out.println("Weights:"+ EC);
      for (int i : EC.keySet()) {
          
          if(max<EC.get(i))
          {
              max=EC.get(i);
          }
      }
      return max;
  }

}
