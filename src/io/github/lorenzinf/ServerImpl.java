package io.github.lorenzinf;

import io.github.lorenzinf.kago_util.Server;


import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ServerImpl extends Server {

	private final Map<String, Consumer<String>> commands;

	public ServerImpl (int pPort) {
		super(pPort);
		commands = new HashMap<>();
	}

	public void loadCommands () {
		var cls = Listeners.class;
		for (Method declaredMethod : cls.getDeclaredMethods()) {
			var annotation = declaredMethod.getAnnotation(OnCommand.class);
			if (annotation != null) {
				commands.put(annotation.command(), null /* todo */);
			}
		}
	}

	public void invoke () {
		// ehrgtg (todo)
	}


	@Override
	public void processNewConnection (String pClientIP, int pClientPort) {
		System.out.printf("%s/%d joined", pClientIP, pClientPort);
	}

	@Override
	public void processClosingConnection (String pClientIP, int pClientPort) {
		System.out.printf("%s/%d left", pClientIP, pClientPort);
		//Falls User gerade im Chatraum ist wird den anderen Bescheid gesagt
	}

	@Override
	public void processMessage (String pClientIP, int pClientPort, String pMessage) {
		pMessage.split(" ");
		// todo vdspojlkfsmhigeeee
	}
}
