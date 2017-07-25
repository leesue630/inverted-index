package com.company.Classes;

/**
 * Created by lns16 on 7/13/2017.
 */
public class Tuple {
    private int docID;
    private int occurNum;

    public Tuple(int docID, int occurNum) {
        this.docID = docID;
        this.occurNum = occurNum;
    }

    public int getDocID() {
        return docID;
    }

    public int getOccurNum() {
        return occurNum;
    }

    public void increaseOccurNum(int increaseValue){
        occurNum+=increaseValue;
    }

    public String tupleToString(){
        return docID + ":" + occurNum;
    }
}
