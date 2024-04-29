package src.Quartets;

import java.util.ArrayList;

import src.Taxon.RealTaxon;

public class QuartestsList {

    
    // convert to a singleton
    // private static QuartestsList instance = null;
    
    // public ArrayList<Quartet> quartets;


    // private QuartestsList() {
    //     quartets = new ArrayList<Quartet>();
    // }

    // public static QuartestsList getInstance() {
    //     if (instance == null) {
    //         instance = new QuartestsList();
    //     }
    //     return instance;
    // }

    int[][][][] quartets;

    public QuartestsList(int n) {
        quartets = new int[n][n][n][n];
        // quartets = new ArrayList<Quartet>();
    }


    public void addQuartet(int a, int b, int c, int d){
        Quartet q = new Quartet(a, b, c, d);
        quartets[q.a][q.b][q.c][q.d] += 1;
        // quartets[a][b][c][d] += 1;
        // quartets.add(new Quartet(a, b, c, d));
    }

    public void addQuartetsFromArr(int[][][][] arr){
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr.length; j++) {
                for (int k = 0; k < arr.length; k++) {
                    for (int l = 0; l < arr.length; l++) {
                        quartets[i][j][k][l] += arr[i][j][k][l];
                    }
                }
            }
        }
    }

    public void printQuartets(RealTaxon[] taxons){
        int totalQuartets = 0;
        for (int i = 0; i < taxons.length; i++) {
            for (int j = 0; j < taxons.length; j++) {
                for (int k = 0; k < taxons.length; k++) {
                    for (int l = 0; l < taxons.length; l++) {
                        if(quartets[i][j][k][l] > 0){
                            // print in the format of ((a,b),(c,d)); count
                            System.out.println("((" + taxons[i].label + "," + taxons[j].label + "),(" + taxons[k].label + "," + taxons[l].label + ")); " + quartets[i][j][k][l]);
                            totalQuartets += quartets[i][j][k][l];
                            // System.out.println(taxons[i].label + taxons[j].label + "|" + taxons[k].label + taxons[l].label + " : " + quartets[i][j][k][l]);
                        }
                    }
                }
            }
        }
        // System.out.println("Total Quartets: " + totalQuartets);

    }

    
    
}
