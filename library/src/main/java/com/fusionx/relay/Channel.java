package com.fusionx.relay;

import com.google.common.collect.ImmutableList;

import com.fusionx.relay.constants.UserLevel;
import com.fusionx.relay.event.channel.ChannelEvent;
import com.fusionx.relay.event.channel.WorldJoinEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;

public class Channel {

    // Static stuff
    private final static ImmutableList<Character> channelPrefixes = ImmutableList.of('#', '&',
            '+', '!');

    private final String mName;

    private final List<ChannelEvent> mBuffer;

    private final EnumMap<UserLevel, Integer> mNumberOfUsers;

    private final UserChannelInterface mUserChannelInterface;

    Channel(final String channelName, final UserChannelInterface userChannelInterface) {
        mName = channelName;
        mBuffer = new ArrayList<>();
        mNumberOfUsers = new EnumMap<>(UserLevel.class);
        mUserChannelInterface = userChannelInterface;

        for (final UserLevel levelEnum : UserLevel.values()) {
            mNumberOfUsers.put(levelEnum, 0);
        }

        // WorldJoinEvent is used as JoinEvent is a server event
        mBuffer.add(new WorldJoinEvent(this, userChannelInterface.getServer().getUser()));
    }

    /**
     * Returns whether a string is a channel name based on the first character of the string
     *
     * @param firstCharacter the first character of the string that is to be tested
     * @return whether the character can be one at the start of a channel
     */
    public static boolean isChannelPrefix(char firstCharacter) {
        return channelPrefixes.contains(firstCharacter);
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof Channel) {
            final Channel otherChannel = (Channel) o;
            return otherChannel.getName().equals(mName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return mName.hashCode();
    }

    public void onChannelEvent(final ChannelEvent event) {
        /*if ((event.changeType != UserListChangeType.NONE || InterfaceHolders.getPreferences()
                .shouldLogUserListChanges()) && StringUtils.isNotEmpty(event.message)) {
        }*/
        mBuffer.add(event);
    }

    /**
     * Gets the number of people in the channel
     *
     * @return the number of users in the channel
     */
    public int getNumberOfUsers() {
        if (getUsers() != null) {
            return getUsers().size();
        } else {
            return 0;
        }
    }

    /**
     * Increments the type of user in the channel by 1 - for internal use only
     *
     * @param userLevel the type of user
     */
    public void onIncrementUserType(final UserLevel userLevel) {
        if (userLevel != UserLevel.NONE) {
            synchronized (mNumberOfUsers) {
                Integer users = mNumberOfUsers.get(userLevel);
                mNumberOfUsers.put(userLevel, ++users);
            }
        }
    }

    /**
     * Decrements the type of user in the channel by 1 - for internal use only
     *
     * @param userLevel the type of user
     */
    public void onDecrementUserType(final UserLevel userLevel) {
        if (userLevel != UserLevel.NONE) {
            synchronized (mNumberOfUsers) {
                Integer users = mNumberOfUsers.get(userLevel);
                mNumberOfUsers.put(userLevel, --users);
            }
        }
    }

    /**
     * Gets the number of users of a specific level in the channel
     *
     * @param userLevel - the level to get
     * @return the number of users of this level
     */
    public int getNumberOfUsersType(final UserLevel userLevel) {
        synchronized (mNumberOfUsers) {
            if (userLevel != UserLevel.NONE) {
                return mNumberOfUsers.get(userLevel);
            } else {
                int normalUsers = getNumberOfUsers();
                for (UserLevel levelEnum : UserLevel.values()) {
                    normalUsers -= mNumberOfUsers.get(levelEnum);
                }
                return normalUsers;
            }
        }
    }

    /**
     * Overridden method which returns the channel's name
     *
     * @return the channel name
     */
    @Override
    public String toString() {
        return mName;
    }

    // Getters and setters
    public String getName() {
        return mName;
    }

    /**
     * Gets the buffer of the channel - the messages which were received since the start of
     * observation
     *
     * @return a list of the messages
     */
    public List<ChannelEvent> getBuffer() {
        return mBuffer;
    }

    /**
     * Returns a list of all the users currently in the channel
     *
     * @return list of users currently in the channel
     */
    public Collection<WorldUser> getUsers() {
        return mUserChannelInterface.getAllUsersInChannel(this);
    }
}