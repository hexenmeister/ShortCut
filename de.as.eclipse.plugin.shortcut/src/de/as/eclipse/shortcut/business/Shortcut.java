package de.as.eclipse.shortcut.business;

import java.io.File;

public abstract class Shortcut {

    private Integer id;

    private String priority;

    private String name;

    private String group;

    private String category1;

    private String category2;

    private String location;

    private String workingDir;

    private String moreCommands;

    private boolean grabOutput;

    private String rgb;

    /**
     * Erstellt eine neue Instanz.
     */
    public Shortcut() {
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCategory1() {
        return this.category1;
    }

    public void setCategory1(String category) {
        this.category1 = category;
    }

    public String getCategory2() {
        return this.category2;
    }

    public void setCategory2(String category) {
        this.category2 = category;
    }

    public String getPriority() {
        return this.priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return this.group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWorkingDir() {
        return this.workingDir;
    }

    public void setWorkingDir(String workingDir) {
        this.workingDir = workingDir;
    }

    public String getMoreCommands() {
        return this.moreCommands;
    }

    public void setMoreCommands(String moreCommands) {
        this.moreCommands = moreCommands;
    }

    public boolean isGrabOutput() {
        return this.grabOutput;
    }

    public void setGrabOutput(boolean grabOutput) {
        this.grabOutput = grabOutput;
    }

    public String getRgb() {
        return this.rgb;
    }

    public void setRgb(String rgb) {
        this.rgb = rgb;
    }

    @Override
    public boolean equals(Object arg0) {
        Shortcut sc = (Shortcut) arg0;

        return (sc.getName().equals(this.name)) && (sc.getLocation().equals(this.location));
    }

    public long getSize() {
        File f = new File(this.location);
        return f.exists() ? f.length() : -1;
    }

    public long getLastModified() {
        File f = new File(this.location);
        return f.exists() ? f.lastModified() : -1;
    }

}
