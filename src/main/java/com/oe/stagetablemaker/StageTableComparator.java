
package com.oe.stagetablemaker;

import java.util.Comparator;
import java.util.List;

public class StageTableComparator implements Comparator<Integer> {

    int[] stagingOrder;

   
    public StageTableComparator( List<Integer> list) {
        stagingOrder = new int[ list.size() ];

        int i=0;
        for( int order : list ) {
            stagingOrder[i++] = order;
        }

    }

    //count the number of chillers that are ON; this is the stage.
    public int getStage(int A) {

        int countOfChillersOn = 0;
        for (int i = 1; i <= Math.pow(2, this.stagingOrder.length); i = i << 1) {
            countOfChillersOn += (A & i) > 0 ? 1 : 0;
        }
        return countOfChillersOn;
    }

    
    @Override
    public int compare(Integer A, Integer B) {
        if( getStage(A) < getStage(B) ) return -1; //A should be higher in the list since it has smaller stage
        if( getStage(A) > getStage(B) ) return 1;  //B wins
        
        //when both are of the same stage, look at which chillers are on.
        //the guy with the first more efficient chiller should be higher in the table.
        for( int i=0; i< this.stagingOrder.length; i++ ){
            
            int nextBestChiller = 1 << (this.stagingOrder.length - stagingOrder[i]);
            
            if( (A & B & nextBestChiller) > 0 ) continue; //they both have this chiller
            
            if( (A & nextBestChiller) > 0  ) return -1; //A wins
            if( (B & nextBestChiller) > 0  ) return 1; //B wins
            
            //continue - neither have this chiller turned on
            
        }
       
        return 0;
    }

}
