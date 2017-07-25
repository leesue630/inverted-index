package com.company;

import com.company.Classes.IIDocument;
import com.company.Classes.Tuple;
import com.company.Classes.TupleList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import static java.util.Collections.sort;

/**
 * Created by lns16 on 7/10/2017.
 */
public class InvertedIndex {

    private List<String> tokens = new ArrayList<>();
    private List<IIDocument> documents = new ArrayList<>();
    private int docCount = 5;
    private int termCount = 0;
    private List<String> stopWords = Arrays.asList("and", "the", "is", "to");

    public InvertedIndex() {
    }

//    private void countTerms(List<IIDocument> documents) {
//        for (IIDocument document : documents) {
//            for (String token : normalize(document.getTokens())) {
//                if (!tokens.contains(token) && !stopWords.contains(token)) {
//                    tokens.add(token);
//                }
//            }
//        }
//        sort(tokens);
//        termCount = tokens.size();
//    }

    private List<String> normalize(String[] tokens) {
        List<String> tokenList = new ArrayList<>();
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i].toLowerCase().replaceAll("\\.|,", "");
            tokenList.add(token);
        }
        return tokenList;
    }

//    private int[][] generateMatrix() {
//        countTerms(documents);
//        int[][] tokenMatrix = new int[docCount][termCount];
//        for (IIDocument document : documents) {
//            for (String term : normalize(document.getTokens())) {
//                if (tokens.contains(term)) {
//                    tokenMatrix[document.getDocID() - 1][tokens.indexOf(term)] += 1;
//                }
//            }
//        }
//        return tokenMatrix;
//    }
//
//    private int[] getVectorByTerm(String term, int[][] tokenMatrix) {
//        int[] termVector = new int[docCount];
//        if (tokens.contains(term)) {
//            int termIndex = tokens.indexOf(term);
//            for (int k = 0; k < tokenMatrix.length; k++) {
//                termVector[k] = tokenMatrix[k][termIndex];
//            }
//            return termVector;
//        }
//        return null;
//    }

    private HashMap<String, TupleList> generateTuples() {
        HashMap<String, TupleList> tupleMap = new HashMap<>();
        for (IIDocument document : documents) {
            for (String term : normalize(document.getTokens())) {
                if (!stopWords.contains(term)) {
                    if (!tupleMap.containsKey(term)) {
                        tokens.add(term);
                        tupleMap.put(term, new TupleList());
                        tupleMap.get(term).addTuple(new Tuple(document.getDocID(), 1));
                    } else {
                        boolean hasDocID = tupleMap.get(term).increaseTupleOccurNumByDocID(document.getDocID());
                        if (hasDocID == false) {
                            tupleMap.get(term).addTuple(new Tuple(document.getDocID(), 1));
                        }
                    }
                }
            }

        }
        return tupleMap;
    }

    private List<String> stringToQueryList(String string) {
        List<String> tokenList = new ArrayList<>();
        String[] strings = string.split(" ");
        for (int i = 0; i < strings.length; i++) {
            String token = strings[i].toLowerCase().replaceAll("\\.|,", "");
            if(!tokenList.contains(token)) {
                tokenList.add(token);
            }
        }
        return tokenList;
    }

    private HashMap<Integer, Integer> processLargeQuery(List<String> queryList, HashMap<String, TupleList> tupleMap) {
        HashMap<Integer, Integer> docScores;
        if (queryList.size() > 2) {
            List<String> querySample = new ArrayList<>();
            querySample.add(queryList.get(0));
            querySample.add(queryList.get(1));
            queryList.remove(0);
            queryList.remove(0);
            docScores = processSmallQuery(querySample, tupleMap);
            docScores = combineDocScores(docScores, processLargeQuery(queryList, tupleMap));
        } else {
            docScores = processSmallQuery(queryList, tupleMap);
        }
        return docScores;
    }

    private HashMap<Integer, Integer> combineDocScores(HashMap<Integer, Integer> docScores1, HashMap<Integer, Integer> docScores2) {
        for (Map.Entry<Integer, Integer> entry : docScores2.entrySet()) {
            if (docScores1.containsKey(entry.getKey())) {
                docScores1.replace(entry.getKey(), docScores1.get(entry.getKey()) + entry.getValue());
            } else {
                docScores1.put(entry.getKey(), entry.getValue());
            }
        }
        return docScores1;
    }

    private HashMap<Integer, Integer> processSmallQuery(List<String> queryList, HashMap<String, TupleList> tupleMap) {
        List<TupleList> tupleListList = new ArrayList<>();
        HashMap<Integer, Integer> docScores = new HashMap<>();
        for (int i = 0; i < queryList.size(); i++){
            if (!tokens.contains(queryList.get(i))){
                queryList.remove(i);
                i--;
            }
        }
        if (queryList.size() == 1) {
            for (Map.Entry<String, TupleList> tupleListEntry : tupleMap.entrySet()) {
                if (tupleListEntry.getKey().equals(queryList.get(0))) {
                    for (Tuple tuple : tupleListEntry.getValue().getTupleList())
                        docScores.put(tuple.getDocID(), tuple.getOccurNum());
                }
            }
        } else if (queryList.size() == 2) {
            for (int i = 0; i < queryList.size(); i++) {
                tupleListList.add(tupleMap.get(queryList.get(i)));
            }
            int i = 0;
            int j = 0;
            while (i < tupleListList.get(0).getTupleList().size() || j < tupleListList.get(1).getTupleList().size()) {
                Tuple tuple1 = tupleListList.get(0).getTupleList().get(i);
                Tuple tuple2 = tupleListList.get(1).getTupleList().get(j);
                if (tuple1.getDocID() == tuple2.getDocID()) {
                    int[] processedScore = processScore(score(tuple1, tuple2, "=="));
                    docScores.put(processedScore[0], processedScore[1]);
                    i++;
                    j++;
                } else if (tuple1.getDocID() < tuple2.getDocID()) {
                    int[] processedScore = processScore(score(tuple1, tuple2, "<"));
                    docScores.put(processedScore[0], processedScore[1]);
                    i++;
                } else if (tuple1.getDocID() > tuple2.getDocID()) {
                    int[] processedScore = processScore(score(tuple1, tuple2, ">"));
                    docScores.put(processedScore[0], processedScore[1]);
                    j++;
                }
            }
        }
        return docScores;
    }

    private int[] score(Tuple tuple1, Tuple tuple2, String docIDCompare) {
        int[] score;
        switch (docIDCompare) {
            case "==":
                score = new int[]{tuple1.getDocID(), tuple1.getOccurNum(), tuple2.getOccurNum()};
                return score;
            case ">":
                score = new int[]{tuple2.getDocID(), 0, tuple2.getOccurNum()};
                return score;
            case "<":
                score = new int[]{tuple1.getDocID(), tuple1.getOccurNum(), 0};
                return score;
            default:
                return null;
        }
    }

    private int[] processScore(int[] score) {
        return new int[]{score[0], score[1] + score[2]};
    }

    private void printDocScores(HashMap<Integer, Integer> docScores){
        for (Map.Entry<Integer, Integer> entry : docScores.entrySet()) {
            System.out.println("Document " + entry.getKey() + ": " + entry.getValue() + " pts.");
        }
        if (docScores.size() == 0){
            System.out.println("No Results");
        }
    }

    public static void main(String[] args) throws IOException{
        IIDocument doc1 = new IIDocument("He likes to wink, he likes to drink.", 1);
        IIDocument doc2 = new IIDocument("He likes to drink, and drink, and drink.", 2);
        IIDocument doc3 = new IIDocument("The thing he likes to drink is ink.", 3);
        IIDocument doc4 = new IIDocument("The ink he likes to drink is pink.", 4);
        IIDocument doc5 = new IIDocument("He likes to wink and drink pink ink.", 5);

        InvertedIndex invertedIndex = new InvertedIndex();

        invertedIndex.documents.add(doc1);
        invertedIndex.documents.add(doc2);
        invertedIndex.documents.add(doc3);
        invertedIndex.documents.add(doc4);
        invertedIndex.documents.add(doc5);

//        int[][] tokenMatrix = invertedIndex.generateMatrix();
//        System.out.println(invertedIndex.tokens);
//        for (int[] vector : tokenMatrix) {
//            System.out.println(Arrays.toString(vector));
//        }
//        System.out.println(Arrays.toString(invertedIndex.getVectorByTerm("drink", tokenMatrix)));

        HashMap<String, TupleList> tupleMap = invertedIndex.generateTuples();
        for (Map.Entry<String, TupleList> entry : tupleMap.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue().tupleListToList());
        }

        String query;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Enter search query: ");
        query = reader.readLine();
        while (!query.equals("")) {
            HashMap<Integer, Integer> docScores = invertedIndex.processLargeQuery(invertedIndex.stringToQueryList(query), tupleMap);
            invertedIndex.printDocScores(docScores);
            System.out.print("Enter search query: ");
            query = reader.readLine();
        }
    }
}
