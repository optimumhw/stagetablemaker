/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oe.stagetablemaker;

import org.apache.commons.math3.util.MathUtils;
import org.junit.*;

import java.util.*;

import static org.apache.commons.math3.util.ArithmeticUtils.binomialCoefficient;
import static org.junit.Assert.*;

/**
 *
 * @author halwilkinson
 */
public class StageTableMakerTest {
    
    public StageTableMakerTest() {

    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void checkHeaderTest() {
        String pathToFile = "/Users/hal/Desktop/forClarkTwo.csv";
        String chillersStr = "6,9,12,13,10,19,5,4,8,7,14";

        ChillerStagingOrder cso = new ChillerStagingOrder(pathToFile, chillersStr);

        String expected = "Stage,Rank,Chiller4,Chiller5,Chiller6,Chiller7,Chiller8,Chiller9,Chiller10,Chiller12,Chiller13,Chiller14,Chiller19";
        String header = cso.getHeaderRow();

        assertTrue("header wrong", header.contentEquals(expected));
    }


    @Test
    public void checkCountOfChillersOnAtEachStageAndRank() {
        String pathToFile = "blahblahblah...";
        String chillersStr = "6,9,12,13,10,19,5,4,8,7,14";


        ChillerStagingOrder cso = new ChillerStagingOrder(pathToFile, chillersStr);
        int[][] rt = cso.getRecommendationTable();

        String[] chillerStrings = chillersStr.split(",");

        int numOfRows = (int)Math.pow(2, chillerStrings.length );
        assertTrue( "array wrong size", rt.length == numOfRows );

        int startingRow = 1;
        for( int stage = 1; stage <= chillerStrings.length; stage++  ) {

            long numOfRanksAtStage = binomialCoefficient( chillerStrings.length, stage );

            for (int rowOffset = startingRow; rowOffset < startingRow + numOfRanksAtStage; rowOffset++) {

                int numOfChillersOn = 0;

                for (int col = 2; col < chillerStrings.length + 2; col++) {
                    numOfChillersOn += rt[rowOffset][col];
                }
                String chillersOnMsg = String.format( "Stage: %d, Rank %d, ChillersOn: %d", stage, rowOffset - startingRow + 1, numOfChillersOn );
                assertTrue(chillersOnMsg, stage == numOfChillersOn );

            }

            startingRow += numOfRanksAtStage;
        }
    }

    @Test
    @Ignore
    public void millionRowStressTest() {
        String pathToFile = "blahblahblah...";
        String chillersStr = "6,5,4,3,2,1,7,8,9,10,11,12,13,24,15,16,17,18,19,20";


        ChillerStagingOrder cso = new ChillerStagingOrder(pathToFile, chillersStr);
        int[][] rt = cso.getRecommendationTable();

        String[] chillerStrings = chillersStr.split(",");

        int numOfRows = (int)Math.pow(2, chillerStrings.length );
        assertTrue( "array wrong size", rt.length == numOfRows );

        int startingRow = 1;
        for( int stage = 1; stage <= chillerStrings.length; stage++  ) {

            long numOfRanksAtStage = binomialCoefficient( chillerStrings.length, stage );

            for (int rowOffset = startingRow; rowOffset < startingRow + numOfRanksAtStage; rowOffset++) {

                int numOfChillersOn = 0;

                for (int col = 2; col < chillerStrings.length + 2; col++) {
                    numOfChillersOn += rt[rowOffset][col];
                }
                String chillersOnMsg = String.format( "Stage: %d, Rank %d, ChillersOn: %d", stage, rowOffset - startingRow + 1, numOfChillersOn );
                assertTrue(chillersOnMsg, stage == numOfChillersOn );

            }

            startingRow += numOfRanksAtStage;
        }
    }
    
}








