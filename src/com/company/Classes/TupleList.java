package com.company.Classes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lns16 on 7/13/2017.
 */
public class TupleList {

    private List<Tuple> tupleList = new ArrayList<>();

    public TupleList(List<Tuple> tupleList) {
        this.tupleList = tupleList;
    }

    public TupleList() {
    }

    public void addTuple(Tuple tuple){
        tupleList.add(tuple);
    }

    public List<Tuple> getTupleList() {
        return tupleList;
    }

    public Tuple getTupleByDocID(int docID){
        for (Tuple tuple : tupleList){
            if (tuple.getDocID() == docID){
                return tuple;
            }
        }
        return null;
    }

    public boolean increaseTupleOccurNumByDocID(int docID){
        for (Tuple tuple : tupleList){
            if (tuple.getDocID() == docID){
                tuple.increaseOccurNum(1);
                return true;
            }
        }
        return false;
    }

    public List<String> tupleListToList(){
        List<String> stringList = new ArrayList<>();
        for (Tuple tuple : tupleList){
            stringList.add(tuple.tupleToString());
        }
        return stringList;
    }
}
