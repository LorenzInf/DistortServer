package io.github.lorenzinf;

import io.github.lorenzinf.kago_util.List;

public class DistortServer extends io.github.lorenzinf.kago_util.Server {

    private final ProgramController pc;

    public DistortServer(int pPort, ProgramController pc) {
        super(pPort);
        this.pc = pc;
        pc.setServer(this);
        System.out.println("[INFO] Server started");
    }

    @Override
    public void processNewConnection(String pClientIP, int pClientPort) {
        newUser(pClientIP, pClientPort);
        System.out.println("[INFO] User connected via " + pClientIP + " " + pClientPort);
    }

    @Override
    public void processMessage(String pClientIP, int pClientPort, String pMessage) {
        var sMessage = pMessage.split("_");
        switch (sMessage[0]) {
            case "SETNAME" -> setName(pClientIP, pClientPort, sMessage[1]);
            case "JOIN" -> join(pClientIP, pClientPort);
            case "MESSAGE" -> {
                if(sMessage.length == 2)
                    sendMessage(pClientIP, pClientPort, sMessage[1]);
                else
                    send(pClientIP,pClientPort, "ERR_INVALID_INPUT");
            }
            case "LEAVE" -> leave(pClientIP, pClientPort);
            case "WHISPER" -> whisper(pClientIP, pClientPort, sMessage[1], sMessage[2]);
            default -> send(pClientIP,pClientPort, "ERR_INVALID_INPUT");
        }
    }

    @Override
    public void processClosingConnection(String pClientIP, int pClientPort) {
        removeUser(pClientIP, pClientPort);
        System.out.println("[INFO] User disconnected (" + pClientIP + " " + pClientPort + ")");
    }

    public void newUser(String pClientIP, int pClientPort) {
        pc.getAllUsers().append(new User(pClientIP, pClientPort,"Anon"));
    }

    public void sendToJoined(String message) {
        pc.getAllUsers().toFirst();
        while(pc.getAllUsers().hasAccess()) {
            if(pc.getAllUsers().getContent().isJoined()) {
                send(pc.getAllUsers().getContent().getIp(), pc.getAllUsers().getContent().getPort(), message);
            }
            pc.getAllUsers().next();
        }
    }

    public void removeUser(String pClientIP, int pClientPort) {
        pc.getAllUsers().toFirst();
        while(pc.getAllUsers().hasAccess() && !isUser(pClientIP, pClientPort)) {
            pc.getAllUsers().next();
        }
        pc.getAllUsers().remove();
    }

    private User findUser(String pClientIP, int pClientPort) {
        pc.getAllUsers().toFirst();
        while(pc.getAllUsers().hasAccess() && !isUser(pClientIP, pClientPort))
            pc.getAllUsers().next();
        return pc.getAllUsers().getContent();
    }

    private List<User> findUser(String name) {
        List<User> users = new List<>();
        pc.getAllUsers().toFirst();
        while(pc.getAllUsers().hasAccess()) {
            if(pc.getAllUsers().getContent().getName().equals(name)) {
                users.append(pc.getAllUsers().getContent());
            }
            pc.getAllUsers().next();
        }
        return users;
    }

    private boolean isUser(String pClientIP, int pClientPort) {
        return pc.getAllUsers().getContent().getIp().equals(pClientIP) && pc.getAllUsers().getContent().getPort() == pClientPort;
    }

    public void setName(String pClientIP, int pClientPort, String name) {
        var user = findUser(pClientIP, pClientPort);
        var ogName = user.getName();
        user.setName(name);
        if(user.isJoined()) {
            sendToJoined("CHANGED-NAME_" + ogName + "_" + user.getName());
        }
        System.out.println("[INFO] " + ogName + " changed their name to " + user.getName());
    }


    public void join(String pClientIP, int pClientPort) {
        var user = findUser(pClientIP, pClientPort);
        if(user.isJoined())
            send(pClientIP, pClientPort, "ERR_ALREADY-JOINED");
        else {
            sendToJoined("JOINED_" + user.getName());
            user.setJoined(true);
        }
        System.out.println("[INFO] " + user.getName() + " joined the chat");
    }

    public void sendMessage(String pClientIP, int pClientPort, String message) {
        var user = findUser(pClientIP, pClientPort);
        if(!user.isJoined())
            send(pClientIP, pClientPort, "ERR_NOT-CONNECTED");
        else {
            sendToJoined("MESSAGE_" + user.getName() + "_" + message);
            System.out.println("[INFO] A message was sent");
        }
    }

    public void leave(String pClientIP, int pClientPort) {
        var user = findUser(pClientIP, pClientPort);
        if(!user.isJoined()) {
            send(pClientIP, pClientPort, "ERR_NOT-IN-ROOM");
        } else {
            user.setJoined(false);
            sendToJoined("LEFT_" + user.getName());
        }
        System.out.println("[INFO] " + user.getName() + " left the chat");
    }

    public void whisper(String pClientIP, int pClientPort, String recipient, String message) {
        var user = findUser(pClientIP,pClientPort);
        var recipients = findUser(recipient);
        recipients.toFirst();
        if(!recipients.hasAccess()) {
            send(pClientIP, pClientPort, "ERR_USER-NOT-FOUND");
        } else {
            recipients.next();
            if(recipients.hasAccess()) {
                send(pClientIP, pClientPort, "ERR_DUPLICATE-NAME");
            } else {
                recipients.toFirst();
                send(pClientIP, pClientPort, "DM_SENT");
                send(recipients.getContent().getIp(), recipients.getContent().getPort(), "DM_RECEIVED_" + user.getName() + "_" + message);
            }
        }
        System.out.println("[INFO] Someone tried sending a dm");
    }
}
