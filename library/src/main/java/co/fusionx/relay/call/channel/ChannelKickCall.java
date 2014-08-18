package co.fusionx.relay.call.channel;

import com.google.common.base.Optional;

import co.fusionx.relay.call.Call;

public class ChannelKickCall implements Call {

    public final static String KICK = "KICK %1$s %2$s";

    public final static String KICK_WITH_REASON = "KICK %1$s %2$s :%3$s";

    private final String mChannelName;

    private final String mUserNick;

    private final Optional<String> mOptReason;

    public ChannelKickCall(final String channelName, final String userNick,
            final Optional<String> reason) {
        mChannelName = channelName;
        mUserNick = userNick;
        mOptReason = reason;
    }

    @Override
    public String getLineToSendServer() {
        return mOptReason.transform(this::kickWithReason)
                .or(String.format(KICK, mUserNick, mChannelName));
    }

    private String kickWithReason(final String reason) {
        return String.format(KICK_WITH_REASON, mChannelName, mUserNick, reason).trim();
    }
}