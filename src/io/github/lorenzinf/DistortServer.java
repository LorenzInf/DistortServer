package io.github.lorenzinf;

public class DistortServer extends io.github.lorenzinf.kago_util.Server {

    private final ProgramController pc;

    public DistortServer(int pPort, ProgramController pc) {
        super(pPort);
        this.pc = pc;
        pc.setServer(this);
    }

    @Override
    public void processNewConnection(String pClientIP, int pClientPort) {
        pc.newUser(pClientIP, pClientPort);
    }

    @Override
    public void processMessage(String pClientIP, int pClientPort, String pMessage) {
        var sMessage = pMessage.split("_");
        switch (sMessage[0]) {
            case "SETNAME" -> pc.setName(pClientIP, pClientPort, sMessage[1]);
            case "JOIN" -> pc.join(pClientIP, pClientPort);
            case "MESSAGE" -> pc.sendMessage(pClientIP, pClientPort, sMessage[1]);
            case "LEAVE" -> pc.leave(pClientIP, pClientPort);
            case "WHISPER" -> pc.whisper(pClientIP, pClientPort, sMessage[1], sMessage[2]);
            default -> send(pClientIP,pClientPort, "ERR_INVALID_INPUT");
        }
    }

    @Override
    public void processClosingConnection(String pClientIP, int pClientPort) {
        pc.removeUser(pClientIP, pClientPort);
    }
}
