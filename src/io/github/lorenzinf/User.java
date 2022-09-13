package io.github.lorenzinf;

public class User {

    private String ip;
    private int port;
    private String name;
    private boolean isJoined;

    public User(String ip, int port, String name) {
        this.ip = ip;
        this.port = port;
        this.name = name;
        isJoined = false;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isJoined() {
        return isJoined;
    }

    public void setJoined(boolean joined) {
        isJoined = joined;
    }
}
