package co.fusionx.relay.event.channel;

import co.fusionx.relay.base.Channel;
import co.fusionx.relay.base.ChannelUser;
import co.fusionx.relay.constants.UserLevel;

public class ChannelWorldQuitEvent extends ChannelWorldUserEvent {

    public final String reason;

    public final UserLevel level;

    public ChannelWorldQuitEvent(final Channel channel, final ChannelUser user,
            final UserLevel level, final String reason) {
        super(channel, user);

        this.level = level;
        this.reason = reason;
    }
}