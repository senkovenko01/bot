package ua.com.alevel.bot.cache;

import ua.com.alevel.bot.botapi.BotState;
import ua.com.alevel.bot.model.UserProfileData;


public interface DataCache {
    void setUsersCurrentBotState(int userId, BotState botState);

    BotState getUsersCurrentBotState(int userId);

    UserProfileData getUserProfileData(int userId);

    void saveUserProfileData(int userId, UserProfileData userProfileData);
}
