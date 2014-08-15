package co.fusionx.relay.parser.code;

import co.fusionx.relay.RelayServer;
import co.fusionx.relay.RelayUserChannelInterface;
import co.fusionx.relay.bus.ServerEventBus;
import co.fusionx.relay.constants.ServerReplyCodes;

import android.util.SparseArray;

import java.util.List;

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
        parserMap.put(ServerReplyCodes.RPL_NAMREPLY, nameParser);
        parserMap.put(ServerReplyCodes.RPL_ENDOFNAMES, nameParser);

        final MotdParser motdParser = new MotdParser(server);
        parserMap.put(ServerReplyCodes.RPL_MOTDSTART, motdParser);
        parserMap.put(ServerReplyCodes.RPL_MOTD, motdParser);
        parserMap.put(ServerReplyCodes.RPL_ENDOFMOTD, motdParser);

        final ErrorParser errorParser = new ErrorParser(server);
        parserMap.put(ServerReplyCodes.ERR_NOSUCHNICK, errorParser);
        parserMap.put(ServerReplyCodes.ERR_NICKNAMEINUSE, errorParser);

        return parserMap;
    }

    public abstract void onParseCode(final int code, final List<String> parsedArray);
}