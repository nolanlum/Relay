package co.fusionx.relay.event.server;

import co.fusionx.relay.base.Server;

public class QuitEvent extends ServerEvent {

    public QuitEvent(final Server server) {
        super(server);
    }
}