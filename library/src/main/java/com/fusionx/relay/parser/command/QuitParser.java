package com.fusionx.relay.parser.command;

import com.google.common.base.Optional;

import com.fusionx.relay.RelayChannel;
import com.fusionx.relay.RelayChannelUser;
import com.fusionx.relay.RelayQueryUser;
import com.fusionx.relay.RelayServer;
import com.fusionx.relay.event.channel.ChannelWorldQuitEvent;
import com.fusionx.relay.event.query.QueryQuitWorldEvent;
import com.fusionx.relay.util.IRCUtils;
import com.fusionx.relay.util.Optionals;

import java.util.Collection;
import java.util.List;

public class QuitParser extends CommandParser {

    private boolean mIsUserQuit;

    public QuitParser(final RelayServer server) {
        super(server);
    }

    @Override
    public void onParseCommand(final List<String> parsedArray, final String rawSource) {
        final String nick = IRCUtils.getNickFromRaw(rawSource);
        if (mServer.getUser().isNickEqual(nick)) {
            onQuit();
        } else {
            onUserQuit(parsedArray, nick);
        }
    }

    public boolean isUserQuit() {
        return mIsUserQuit;
    }

    private void onUserQuit(final List<String> parsed, final String userNick) {
        final Optional<RelayChannelUser> optUser = mUserChannelInterface.getUser(userNick);
        Optionals.ifPresent(optUser, user -> {
            final Collection<RelayChannel> channels = mUserChannelInterface.removeUser(user);
            final String reason = parsed.size() == 3 ? parsed.get(2).replace("\"", "") : "";
            for (final RelayChannel channel : channels) {
                mUserChannelInterface.removeUserFromChannel(channel, user);

                final ChannelWorldQuitEvent event = new ChannelWorldQuitEvent(channel, user,
                        reason);
                mServerEventBus.postAndStoreEvent(event, channel);
            }
        });

        final Optional<RelayQueryUser> optQuery = mUserChannelInterface.getQueryUser(userNick);
        Optionals.ifPresent(optQuery, queryUser -> {
            final QueryQuitWorldEvent event = new QueryQuitWorldEvent(queryUser);
            mServerEventBus.postAndStoreEvent(event, queryUser);
        });
    }

    private void onQuit() {
        // TODO - improve this
        mIsUserQuit = true;
    }
}