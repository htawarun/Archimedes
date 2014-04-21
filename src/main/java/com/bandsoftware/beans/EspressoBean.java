package com.bandsoftware.beans;

/**
 * Created by Tyler on 4/19/14.
 */
public class EspressoBean {
    // protected String metadata;
    protected String entity_name;
    protected String attribute_name;
    protected int ruletype_ident;
    protected int project_ident;
    protected boolean active = false;

    public EspressoBean() {

    }

    public EspressoBean(String entity_name, String attribute_name, int ruletype_ident, int project_ident) {
        this.entity_name = entity_name;
        this.attribute_name = attribute_name;
        this.ruletype_ident = ruletype_ident;
        this.project_ident = project_ident;
    }


    public String getEntity_name() {
        return entity_name;
    }

    public void setEntity_name(String entity_name) {
        this.entity_name = entity_name;
    }

    public String getAttribute_name() {
        return attribute_name;
    }

    public void setAttribute_name(String attribute_name) {
        this.attribute_name = attribute_name;
    }

    public int getRuletype_ident() {
        return ruletype_ident;
    }

    public void setRuletype_ident(int ruletype_ident) {
        this.ruletype_ident = ruletype_ident;
    }


    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getProject_ident() {
        return project_ident;
    }

    public void setProject_ident(int project_ident) {
        this.project_ident = project_ident;
    }
}
