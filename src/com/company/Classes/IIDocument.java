package com.company.Classes;

/**
 * Created by lns16 on 7/13/2017.
 */
public class IIDocument {

    private String text;
    private int docID;
    private String[] tokens;

    public IIDocument(String text, int docID) {
        this.text = text;
        this.docID = docID;
    }

    public String getText() {
        return text;
    }

    public int getDocID() {
        return docID;
    }

    public String[] getTokens(){
        return tokens = text.split(" ");
    }
}
