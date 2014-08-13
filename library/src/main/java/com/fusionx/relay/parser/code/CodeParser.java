package com.fusionx.relay.parser.code;

import com.fusionx.relay.RelayServer;
import com.fusionx.relay.RelayUserChannelInterface;
import com.fusionx.relay.bus.ServerEventBus;
import com.fusionx.relay.constants.ServerReplyCodes;

import android.util.SparseArray;

import java.util.List;

import static com.fusionx.relay.constants.ServerReplyCodes.ERR_NICKNAMEINUSE;
import static com.fusionx.relay.constants.ServerReplyCodes.ERR_NOSUCHNICK;
import static com.fusionx.relay.constants.ServerReplyCodes.RPL_ENDOFMOTD;
import static com.fusionx.relay.constants.ServerReplyCodes.RPL_ENDOFNAMES;
import static com.fusionx.relay.constants.ServerReplyCodes.RPL_MOTD;
import static com.fusionx.relay.constants.ServerReplyCodes.RPL_MOTDSTART;
import static com.fusionx.relay.constants.ServerReplyCodes.RPL_NAMREPLY;

public abstract class CodeParser {

    final RelayUserChannelInterface mUserChannelInterface;

    final RelayServer mServer;

    final ServerEventBus mServerEventBus;

    CodeParser(final RelayServer server) {
        mServer = server;
        mUserChannelInterface = server.getUserChannelInterface();
        mServerEventBus = server.getServerEventBus();
    }

    public static SparseArray<CodeParser> getParserMap(final RelayServer server) {
        final SparseArray<CodeParser> parserMap = new SparseArray<>();

        final TopicParser topicParser = new TopicParser(server);
        parserMap.put(ServerReplyCodes.RPL_TOPIC, topicParser);
        parserMap.put(ServerReplyCodes.RPL_TOPICWHOTIME, topicParser);

        final NameParser nameParser = new NameParser(server);
        parserMap.put(RPL_NAMREPLY, nameParser);
        parserMap.put(RPL_ENDOFNAMES, nameParser);

        final MotdParser motdParser = new MotdParser(server);
        parserMap.put(RPL_MOTDSTART, motdParser);
        parserMap.put(RPL_MOTD, motdParser);
        parserMap.put(RPL_ENDOFMOTD, motdParser);

        final ErrorParser errorParser = new ErrorParser(server);
        parserMap.put(ERR_NOSUCHNICK, errorParser);
        parserMap.put(ERR_NICKNAMEINUSE, errorParser);

        return parserMap;
    }

    public abstract void onParseCode(final int code, final List<String> parsedArray);
}