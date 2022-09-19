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
}
