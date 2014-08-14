package com.fusionx.relay.call.channel;

import com.fusionx.relay.call.Call;
import com.fusionx.relay.writers.WriterCommands;

import android.text.TextUtils;

public class ChannelPartCall extends Call {

    private final String channelName;

    private final String reason;

    public ChannelPartCall(final String channelName, final String reason) {
        this.channelName = channelName;
        this.reason = reason;
    }

    @Override
    public String getLineToSendServer() {
        return TextUtils.isEmpty(channelName) ? String.format(WriterCommands.PART, channelName)
                : String.format(WriterCommands.PART_WITH_REASON, channelName, reason).trim();
    }
}