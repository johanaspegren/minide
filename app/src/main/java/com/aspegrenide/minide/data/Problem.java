package com.aspegrenide.minide.data;

public class Problem extends Aspect{

    private String what;
    private String who;
    private String when;
    private String where;
    private String contact;
    private boolean open;

    public Problem() {

    }

    @Override
    public String toString() {
        return "Problem{" +
                "title='" + title + '\'' +
                ", uid='" + uid + '\'' +
                ", what='" + what + '\'' +
                ", who='" + who + '\'' +
                ", when='" + when + '\'' +
                ", where='" + where + '\'' +
                ", contact='" + contact + '\'' +
                ", open=" + open +
                ", linkedIdeas=" + linkedIdeas +
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

    public String getWhat() {
        return what;
    }

    public void setWhat(String what) {
        this.what = what;
    }

    public String getWho() {
        return who;
    }

    public void setWho(String who) {
        this.who = who;
    }

    public String getWhen() {
        return when;
    }

    public void setWhen(String when) {
        this.when = when;
    }

    public String getWhere() {
        return where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

}
