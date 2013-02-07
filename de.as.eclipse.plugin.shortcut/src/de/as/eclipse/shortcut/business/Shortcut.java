package de.as.eclipse.shortcut.business;

import java.io.File;

/**
 * Diese Struktur nimmt die Daten zu einem Shortcut auf.
 *
 * @author Alexander Schulz
 */
public abstract class Shortcut {

    private Integer id;

    private String priority;

    private String name;

    private String group;

    private String category1;

    private String category2;

    private String payload;

    private String workingDir;

    private String description;

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

    public String getPayload() {
        return this.payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getWorkingDir() {
        return this.workingDir;
    }

    public void setWorkingDir(String workingDir) {
        this.workingDir = workingDir;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
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

        return (sc.getName().equals(this.name)) && (sc.getPayload().equals(this.payload));
    }

    public long getSize() {
        File f = new File(this.payload);
        return f.exists() ? f.length() : -1;
    }

    public long getLastModified() {
        File f = new File(this.payload);
        return f.exists() ? f.lastModified() : -1;
    }

    public Shortcut copyFrom(Shortcut another) {
        this.setName(another.getName());
        this.setPriority(another.getPriority());
        this.setGroup(another.getGroup());
        this.setCategory1(another.getCategory1());
        this.setCategory2(another.getCategory2());
        this.setPayload(another.getPayload());
        this.setWorkingDir(another.getWorkingDir());
        this.setGrabOutput(another.isGrabOutput());
        this.setRgb(another.getRgb());
        return this;
    }

}
