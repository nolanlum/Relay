package com.fusionx.relay.parser.command;

import com.fusionx.relay.RelayServer;

import java.util.List;

public class PongParser extends CommandParser {

    public PongParser(final RelayServer server) {
        super(server);
    }

    @Override
    public void onParseCommand(List<String> parsedArray, String rawSource) {
        // TODO - what should be done here?
    }
}