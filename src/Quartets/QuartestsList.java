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

    double[][][][] quartets;

    public QuartestsList(int n) {
        quartets = new double[n][n][n][n];
        // quartets = new ArrayList<Quartet>();
    }


    public void addQuartet(int a, int b, int c, int d){
        Quartet q = new Quartet(a, b, c, d);
        quartets[q.a][q.b][q.c][q.d] += 1;
        // quartets[a][b][c][d] += 1;
        // quartets.add(new Quartet(a, b, c, d));
    }

    public void addQuartetsFromArr(double[][][][] arr){
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

    public void normalize(){
        for(int i = 0; i < quartets.length; i++){
            for(int j = 0; j < quartets.length; j++){
                for(int k = 0; k < quartets.length; k++){
                    for(int l = 0; l < quartets.length; l++){
                        Quartet q1 = new Quartet(i, j, k, l);
                        Quartet q2 = new Quartet(i, k, j, l);
                        Quartet q3 = new Quartet(i, l, j, k);

                        double sum = quartets[q1.a][q1.b][q1.c][q1.d] + quartets[q2.a][q2.b][q2.c][q2.d] + quartets[q3.a][q3.b][q3.c][q3.d];
                        if(sum > 0){
                            quartets[q1.a][q1.b][q1.c][q1.d] = quartets[q1.a][q1.b][q1.c][q1.d] / sum;
                            quartets[q2.a][q2.b][q2.c][q2.d] = quartets[q2.a][q2.b][q2.c][q2.d] / sum;
                            quartets[q3.a][q3.b][q3.c][q3.d] = quartets[q3.a][q3.b][q3.c][q3.d] / sum;
                        }
                    }
                }
            }
        }
    }

    
    
}
