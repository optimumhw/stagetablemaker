package com.oe.stagetablemaker;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.logging.Level;

import static org.apache.commons.math3.util.ArithmeticUtils.binomialCoefficient;

/**
 *
 * @author halwilkinson
 */
public class ChillerStagingOrder {

    private String pathToFile;
    private List<Integer> stagingOrder;
    private Map<Integer, Integer> chillerIDToOrderMap;
    private List<Integer> chillerIdsINumericalOrder;

    private final int numOfChillers;
    private int[][] recTable;

    public ChillerStagingOrder( String pathToFile, String chillerStr ){

        this.pathToFile = pathToFile;

        List<Integer> chillerIdsListedInStagingOrder = new ArrayList<>();
        String[] chillersInString = chillerStr.split(",");

        //4,5,8,10,2
        chillerIdsINumericalOrder = new ArrayList<>();
        for( String cs : chillersInString ){
            int chillerId = Integer.parseInt(cs);
            chillerIdsListedInStagingOrder.add(chillerId);
            chillerIdsINumericalOrder.add(chillerId);
        }
        Collections.sort( chillerIdsINumericalOrder ); //2,4,5,8,10

        //create orderToChillerIDMap : 1-2, 2-4, 3-5, 4-8, 5-10
        chillerIDToOrderMap = new HashMap<>();
        int order = 1;
        for( int chillerId : chillerIdsINumericalOrder ){
            chillerIDToOrderMap.put(chillerId, order);
            order++;
        }

        //set staging order : 2,3,4,5,1
        stagingOrder = new ArrayList<>();
        for( int chillerID : chillerIdsListedInStagingOrder){

            stagingOrder.add(chillerIDToOrderMap.get(chillerID));
        }
        numOfChillers = stagingOrder.size();

        //now make the recommendation table
        int width = this.numOfChillers + 2;
        int height = (int) Math.pow(2, this.numOfChillers);
        this.recTable = new int[height][width];

        //create the unsorted staging order table (0,1,10,11,...,111111 ie, 0,1,2,3,...,63 )
        List<Integer> stagingTable = new ArrayList<>();
        for (int i = 0; i < Math.pow(2, this.numOfChillers); i++) {
            stagingTable.add(i);
        }

        //instatiate a comparer that uses the staging order to compare 2 rows in the table
        StageTableComparator orderer = new StageTableComparator(stagingOrder);

        //sort the table by this staging order
        Collections.sort(stagingTable, orderer);
        
        String list = "";
        for( int row : stagingTable){
            list = list + Integer.toString(row);
            list += ",";
        }
        
        System.out.println(list);

        //build the recommendation table by tacking on the stage and rank numbers
        int currentStage = 0;
        int currentRank = 0;
        for (int i = 0; i < Math.pow(2, this.numOfChillers); i++) {

            int row = stagingTable.get(i);
            int stage = orderer.getStage(row);

            if (stage != currentStage) {
                currentRank = 0;
                currentStage = stage;
            }

            currentRank++;

            recTable[i][0] = currentStage;
            recTable[i][1] = currentRank;

            for (int j = 0; j < this.numOfChillers; j++) {
                recTable[i][j + 2] = ((row & 1 << (this.numOfChillers - 1 - j)) > 0) ? 1 : 0;
            }
        }
    }



    public String getHeaderRow() {

        String headerRow = "Stage,Rank,";

        int index = 0;
        for (int chillerId : this.chillerIdsINumericalOrder ) {
            
            String header = String.format("Chiller%d", chillerId );
            if (index < this.numOfChillers - 1) {
                header += ",";
            }
            index++;
            headerRow += header;
        }
        return headerRow;

    }

    public int[][] getRecommendationTable(){
        return this.recTable;
    }

    public void DumpTable() {

        int[][] recTable = this.getRecommendationTable();

        System.out.println(this.getHeaderRow());

        for (int i = 0; i < recTable.length; i++) {
            String rowString = "";
            for (int j = 0; j < this.numOfChillers + 2; j++) {
                rowString += recTable[i][j];
                if (j < this.numOfChillers + 1) {
                    rowString += ",";
                }
            }
            System.out.println(rowString);
        }
    }


    public String getCsv(){

        StringBuffer sb = new StringBuffer();
        sb.append(this.getHeaderRow());

        for (int i = 0; i < this.getRecommendationTable().length; i++) {
            for (int j = 0; j < this.numOfChillers + 2; j++) {

                sb.append(this.getRecommendationTable()[i][j]);

                if (j < this.numOfChillers + 1) {
                    sb.append(",");
                }
            }
            sb.append(System.getProperty("line.separator"));
        }
        return sb.toString();
    }

    public void writeCSVtoDisk(String pathToCSV) {

        Writer writer = null;

        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(pathToCSV), "utf-8"));

            int[][] recTable = this.getRecommendationTable();

            writer.write(this.getHeaderRow());
            writer.write(System.lineSeparator());

            for (int i = 0; i < recTable.length; i++) {
                
                //if( recTable[i][1] > 5 ){
                //   continue;
                //}
                
                String rowString = "";

                for (int j = 0; j < this.numOfChillers + 2; j++) {    
                    rowString += recTable[i][j];
                    if (j < this.numOfChillers + 1) {
                        rowString += ",";
                    }

                }
                writer.write(rowString);
                writer.write(System.lineSeparator());
            }
        } catch (IOException ex) {

            String msg = "Failed to write recommendation table to disk";
            Logger.getLogger(ChillerStagingOrder.class.getName()).log(Level.SEVERE, msg, ex);
        } finally {
            try {
                writer.close();
            } catch (Exception ex) {
                Logger.getLogger(ChillerStagingOrder.class.getName()).log(Level.SEVERE, "not able to close file", ex);
            }
        }

    }

}
