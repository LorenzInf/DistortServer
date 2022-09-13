package io.github.lorenzinf;

import io.github.lorenzinf.kago_util.List;
import io.github.lorenzinf.kago_util.Server;

public class ProgramController {

    private Server server;
    private List<User> allUsers = new List<>();

    public List<User> getAllUsers() {
        return allUsers;
    }

    public void setServer(DistortServer distortServer) {
        server = distortServer;
    }

    public void newUser(String pClientIP, int pClientPort) {
        allUsers.append(new User(pClientIP, pClientPort,"Anon"));
    }

    public void sendToJoined(String message) {
        allUsers.toFirst();
        while(allUsers.hasAccess()) {
            if(allUsers.getContent().isJoined()) {
                server.send(allUsers.getContent().getIp(), allUsers.getContent().getPort(), message);
            }
            allUsers.next();
        }
    }

    public void removeUser(String pClientIP, int pClientPort) {
        allUsers.toFirst();
        while(allUsers.hasAccess() && !isUser(pClientIP, pClientPort)) {
            allUsers.next();
        }
        allUsers.remove();
    }

    private User findUser(String pClientIP, int pClientPort) {
        allUsers.toFirst();
        while(allUsers.hasAccess() && !isUser(pClientIP, pClientPort))
            allUsers.next();
        return allUsers.getContent();
    }

    private List<User> findUser(String name) {
        List<User> users = new List<>();
        allUsers.toFirst();
        while(allUsers.hasAccess()) {
            if(allUsers.getContent().getName().equals(name)) {
                users.append(allUsers.getContent());
            }
            allUsers.next();
        }
        return users;
    }

    private boolean isUser(String pClientIP, int pClientPort) {
        return allUsers.getContent().getIp().equals(pClientIP) && allUsers.getContent().getPort() == pClientPort;
    }

    public void setName(String pClientIP, int pClientPort, String name) {
        var user = findUser(pClientIP, pClientPort);
        var ogName = user.getName();
        user.setName(name);
        if(user.isJoined()) {
            sendToJoined("CHANGED-NAME_" + ogName + "_" + user.getName());
        }
    }


    public void join(String pClientIP, int pClientPort) {
        var user = findUser(pClientIP, pClientPort);
        if(user.isJoined())
            server.send(pClientIP, pClientPort, "ERR_ALREADY-JOINED");
        else {
            sendToJoined("JOINED_" + user.getName());
            user.setJoined(true);
        }
    }

    public void sendMessage(String pClientIP, int pClientPort, String message) {
        var user = findUser(pClientIP, pClientPort);
        if(!user.isJoined())
            server.send(pClientIP, pClientPort, "ERR_NOT-CONNECTED");
        else {
            sendToJoined("MESSAGE_" + user.getName() + "_" + message);
        }
    }

    public void leave(String pClientIP, int pClientPort) {
        var user = findUser(pClientIP, pClientPort);
        if(!user.isJoined()) {
            server.send(pClientIP, pClientPort, "ERR_NOT-IN-ROOM");
        } else {
            user.setJoined(false);
            sendToJoined("LEFT_" + user.getName());
        }
    }

    public void whisper(String pClientIP, int pClientPort, String recipient, String message) {
        var user = findUser(pClientIP,pClientPort);
        var recipients = findUser(recipient);
        recipients.toFirst();
        if(!recipients.hasAccess()) {
            server.send(pClientIP, pClientPort, "ERR_USER-NOT-FOUND");
        } else {
            recipients.next();
            if(recipients.hasAccess()) {
                server.send(pClientIP, pClientPort, "ERR_DUPLICATE-NAME");
            } else {
                recipients.toFirst();
                server.send(pClientIP, pClientPort, "DM_SENT");
                server.send(recipients.getContent().getIp(), recipients.getContent().getPort(), "DM_RECEIVED_" + user.getName() + "_" + message);
            }
        }
    }


}
