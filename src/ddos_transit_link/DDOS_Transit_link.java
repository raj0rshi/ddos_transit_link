/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ddos_transit_link;

import java.awt.Color;
import java.io.FileNotFoundException;

/**
 *
 * @author rajor
 */
public class DDOS_Transit_link {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException {
        // TODO code application logic here

        Graph G = new Graph();
        G.markCOngested(1);
        G.ReadFile();
        G.calculateRT();
        // G.printRT();
        G.calculatePossibleCongestedNodes();
        G.calculateDetourNeedingNodes();
        G.calculateNodesAndEdgesOnDetour();
        G.addVirtualUser();
       //  G.Draw();
        Graph G1 = G.Transform();

        System.out.println(G1.V.ID);
        G1.calculateRT();
        G1.printRT();
         G1.Draw();
        System.out.println(G1.U_N_D);
        for (Node u : G1.U_N_D.values()) {
            if (u.color==Color.green) {
                System.out.println("U:" + u);
                G1.createFlow(G1.Nodes.get(16));
                //break;
            }
        }

    }
}
