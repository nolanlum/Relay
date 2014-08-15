package co.fusionx.relay.event.server;

import com.google.common.base.Optional;

import co.fusionx.relay.Channel;
import co.fusionx.relay.ChannelUser;
import co.fusionx.relay.Nick;
import co.fusionx.relay.RelayChannelUser;

public class KickEvent extends ServerEvent {

    public final String channelName;

    public final Nick kickingNick;

    public final String kickingNickString;

    public final String reason;

    public KickEvent(final Channel channel, final Optional<RelayChannelUser> optKickingUser,
            final String kickingNickString, final String reason) {
        this.channelName = channel.getName();
        this.kickingNick = optKickingUser.transform(ChannelUser::getNick).orNull();
        this.kickingNickString = kickingNickString;
        this.reason = reason;
    }
}