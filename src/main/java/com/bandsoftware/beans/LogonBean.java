package com.bandsoftware.beans;

/**
 * Created by Tyler on 4/11/14.
 */
public class LogonBean {

    private String username;
    private String password;

    public LogonBean(String adminUser, String adminPW) {
        this.username = adminUser;
        this.password = adminPW;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
