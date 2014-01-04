package com.fusionx.relay.parser;

import com.fusionx.relay.Server;
import com.fusionx.relay.connection.BaseConnection;
import com.fusionx.relay.constants.ServerCommands;
import com.fusionx.relay.event.Event;
import com.fusionx.relay.event.server.ErrorEvent;
import com.fusionx.relay.event.server.GenericServerEvent;
import com.fusionx.relay.event.server.QuitEvent;
import com.fusionx.relay.event.server.ServerEvent;
import com.fusionx.relay.event.server.WhoisEvent;
import com.fusionx.relay.misc.CoreListener;
import com.fusionx.relay.parser.code.CodeParser;
import com.fusionx.relay.parser.command.CommandParser;
import com.fusionx.relay.parser.command.QuitParser;
import com.fusionx.relay.util.IRCUtils;
import com.fusionx.relay.writers.ServerWriter;

import org.apache.commons.lang3.StringUtils;

import android.util.Log;
import android.util.SparseArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.fusionx.relay.constants.ServerReplyCodes.doNothingCodes;
import static com.fusionx.relay.constants.ServerReplyCodes.genericCodes;
import static com.fusionx.relay.constants.ServerReplyCodes.whoisCodes;

public class ServerLineParser {

    private final Server mServer;

    private final BaseConnection mBaseConnection;

    private final Map<String, CommandParser> mCommandParserMap;

    private final SparseArray<CodeParser> mCodeParser;

    private ServerWriter mWriter;

    public ServerLineParser(final Server server, final BaseConnection connection) {
        mServer = server;
        mBaseConnection = connection;
        mCodeParser = CodeParser.getParserMap(server);
        mCommandParserMap = CommandParser.getParserMap(server);
    }

    /**
     * A loop which reads each line from the server as it is received and passes it on to parse
     *
     * @param reader - the reader associated with the server stream
     */
    public void parseMain(final BufferedReader reader, final ServerWriter writer) throws
            IOException {
        mWriter = writer;

        String line;
        while ((line = reader.readLine()) != null && !mBaseConnection.isUserDisconnected()) {
            final Event quit = parseLine(line);
            if (quit instanceof QuitEvent || quit instanceof ErrorEvent) {
                return;
            }
        }
    }

    /**
     * Parses a line from the server
     *
     * @param rawLine the raw line from the server
     * @return returns a boolean which indicates whether the server has disconnected
     */
    Event parseLine(final String rawLine) {
        final ArrayList<String> parsedArray = IRCUtils.splitRawLine(rawLine, true);
        // For stupid servers that send blank lines - like seriously - why??
        if (!parsedArray.isEmpty()) {
            String command = parsedArray.get(0);
            switch (command) {
                case ServerCommands.PING:
                    // Immediately respond & return
                    final String source = parsedArray.get(1);
                    CoreListener.respondToPing(mWriter, source);
                    break;
                case ServerCommands.ERROR:
                    // We are finished - the server has kicked us
                    // out for some reason
                    return new ErrorEvent(rawLine);
                default:
                    // Check if the second thing is a code or a command
                    if (StringUtils.isNumeric(parsedArray.get(1))) {
                        onParseServerCode(rawLine, parsedArray);
                        //mCodeParser.onParseCode(parsedArray);
                    } else if (!onParseServerCommand(parsedArray)) {
                        return new QuitEvent();
                    }
            }
        }
        return new Event();
    }

    // The server is sending a command to us - parse what it is
    private boolean onParseServerCommand(final List<String> parsedArray) {
        final String rawSource = parsedArray.get(0);
        final String command = parsedArray.get(1).toUpperCase();

        // Parse the command
        final CommandParser parser = mCommandParserMap.get(command);
        parser.onParseCommand(parsedArray, rawSource);

        if (parser instanceof QuitParser) {
            final QuitParser quitParser = (QuitParser) parser;
            if (quitParser.isUserQuit()) {
                return false;
            }
        }
        return true;
    }

    private void onParseServerCode(final String rawLine, final List<String> parsedArray) {
        final int code = Integer.parseInt(parsedArray.get(1));

        // Pretty common across all the codes
        IRCUtils.removeFirstElementFromList(parsedArray, 3);
        final String message = parsedArray.get(0);

        if (genericCodes.contains(code)) {
            final ServerEvent event = new GenericServerEvent(message);
            mServer.getServerEventBus().postAndStoreEvent(event, mServer);
        } else if (whoisCodes.contains(code)) {
            final String response = IRCUtils.concatenateStringList(parsedArray);
            final WhoisEvent event = new WhoisEvent(response);
            mServer.getServerEventBus().postAndStoreEvent(event, mServer);
        } else if (doNothingCodes.contains(code)) {
            // Do nothing
        } else {
            final CodeParser parser = mCodeParser.get(code);
            if (parser != null) {
                parser.onParseCode(code, parsedArray);
            } else {
                Log.e("HoloIRC", rawLine);
            }
        }
    }

    public Server getServer() {
        return mServer;
    }
}