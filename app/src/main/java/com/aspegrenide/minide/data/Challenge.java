package com.aspegrenide.minide.data;

public class Challenge {

    private String title;

    public Challenge() {
    }

    @Override
    public String toString() {
        return "Idea{" +
                "title='" + title + '\'' +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
