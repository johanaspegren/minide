package com.aspegrenide.minide.data;

import java.util.ArrayList;

public class Aspect {

    public String title;
    public String uid;
    public String type;
    public ArrayList<String> linkedChallenges;
    public ArrayList<String> linkedProblems;
    public ArrayList<String> linkedIdeas;
    public ArrayList<String> linkedConcepts;

    public Aspect() {
    }


    @Override
    public String toString() {
        return "Aspect{" +
                "title='" + title + '\'' +
                ", uid='" + uid + '\'' +
                ", type='" + type + '\'' +
                ", linkedChallenges=" + linkedChallenges +
                ", linkedProblems=" + linkedProblems +
                ", linkedIdeas=" + linkedIdeas +
                ", linkedConcepts=" + linkedConcepts +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<String> getLinkedProblems() {
        return linkedProblems;
    }

    public void setLinkedProblems(ArrayList<String> linkedProblems) {
        this.linkedProblems = linkedProblems;
    }

    public ArrayList<String> getLinkedIdeas() {
        return linkedIdeas;
    }

    public void setLinkedIdeas(ArrayList<String> linkedIdeas) {
        this.linkedIdeas = linkedIdeas;
    }

    public ArrayList<String> getLinkedChallenges() {
        return linkedChallenges;
    }

    public void setLinkedChallenges(ArrayList<String> linkedChallenges) {
        this.linkedChallenges = linkedChallenges;
    }

    public ArrayList<String> getLinkedConcepts() {
        return linkedConcepts;
    }

    public void setLinkedConcepts(ArrayList<String> linkedConcepts) {
        this.linkedConcepts = linkedConcepts;
    }
}
