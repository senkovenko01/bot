package ua.com.alevel.bot.botapi.handlers.menu;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ua.com.alevel.bot.botapi.BotState;
import ua.com.alevel.bot.botapi.InputMessageHandler;
import ua.com.alevel.bot.cache.UserDataCache;
import ua.com.alevel.bot.model.UserProfileData;
import ua.com.alevel.bot.service.UsersProfileDataService;

@Component
public class ShowProfileHandler implements InputMessageHandler {
    private final UserDataCache userDataCache;
    private final UsersProfileDataService profileDataService;

    public ShowProfileHandler(UserDataCache userDataCache, UsersProfileDataService profileDataService) {
        this.userDataCache = userDataCache;
        this.profileDataService = profileDataService;
    }

    @Override
    public SendMessage handle(Message message) {
        SendMessage userReply;
        final int userId = message.getFrom().getId();
        final UserProfileData profileData = profileDataService.getUserProfileData(message.getChatId());

        userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_MAIN_MENU);
        if (profileData != null) {
            userReply = new SendMessage(message.getChatId(),
                    String.format("%s%n-------------------%n%s", "Ваша анкета:", profileData.toString()));
        } else {
            userReply = new SendMessage(message.getChatId(), "Такой анкеты в БД не существует !");
        }

        return userReply;
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_USER_PROFILE;
    }
}
