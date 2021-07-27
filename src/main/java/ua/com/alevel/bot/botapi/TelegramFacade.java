package ua.com.alevel.bot.botapi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ua.com.alevel.bot.MyTelegramBot;
import ua.com.alevel.bot.cache.UserDataCache;
import ua.com.alevel.bot.exceptions.SendFileException;
import ua.com.alevel.bot.model.UserProfileData;
import ua.com.alevel.bot.service.MainMenuService;

import java.io.*;

@Component
@Slf4j
public class TelegramFacade {
    private final BotStateContext botStateContext;
    private final UserDataCache userDataCache;
    private final MainMenuService mainMenuService;
    private final MyTelegramBot myWizardBot;

    public TelegramFacade(BotStateContext botStateContext, UserDataCache userDataCache, MainMenuService mainMenuService,
                          @Lazy MyTelegramBot myWizardBot) {
        this.botStateContext = botStateContext;
        this.userDataCache = userDataCache;
        this.mainMenuService = mainMenuService;
        this.myWizardBot = myWizardBot;
    }

    public BotApiMethod<?> handleUpdate(Update update) {
        SendMessage replyMessage = null;

        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            log.info("New callbackQuery from User: {}, userId: {}, with data: {}", update.getCallbackQuery().getFrom().getUserName(),
                    callbackQuery.getFrom().getId(), update.getCallbackQuery().getData());
            return processCallbackQuery(callbackQuery);
        }


        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            log.info("New message from User:{}, userId: {}, chatId: {},  with text: {}",
                    message.getFrom().getUserName(), message.getFrom().getId(), message.getChatId(), message.getText());
            try {
                replyMessage = handleInputMessage(message);
            } catch (SendFileException e) {
                log.error("Error in process:", e);

            }
        }

        return replyMessage;
    }


    private SendMessage handleInputMessage(Message message) throws SendFileException {
        String inputMsg = message.getText();
        int userId = message.getFrom().getId();
        long chatId = message.getChatId();
        BotState botState;
        SendMessage replyMessage;

        switch (inputMsg) {
            case "/start":
                botState = BotState.ASK_DESTINY;
                break;
            case "Заполнить анкету":
                botState = BotState.FILLING_PROFILE;
                break;
            case "Моя анкета":
                botState = BotState.SHOW_USER_PROFILE;
                break;
            case "Скачать анкету":
                try {
                    myWizardBot.sendDocument(chatId, "Ваша анкета", getUsersProfile(userId));
                } catch (IOException e) {
                    log.error("Error:", e);
                }
                botState = BotState.SHOW_USER_PROFILE;
                break;
            case "Погода":
                botState = BotState.SHOW_WEATHER_MENU;
                break;
            default:
                botState = userDataCache.getUsersCurrentBotState(userId);
                break;
        }

        userDataCache.setUsersCurrentBotState(userId, botState);

        replyMessage = botStateContext.processInputMessage(botState, message);

        return replyMessage;
    }


    private BotApiMethod<?> processCallbackQuery(CallbackQuery buttonQuery) {
        final long chatId = buttonQuery.getMessage().getChatId();
        final int userId = buttonQuery.getFrom().getId();
        BotApiMethod<?> callBackAnswer = mainMenuService.getMainMenuMessage(chatId, "Воспользуйтесь главным меню");

        switch (buttonQuery.getData()) {
            case "buttonYes":
                callBackAnswer = new SendMessage(chatId, "Как тебя зовут ?");
                userDataCache.setUsersCurrentBotState(userId, BotState.ASK_AGE);
                break;
            case "buttonNo":
                callBackAnswer = sendAnswerCallbackQuery("Возвращайся, когда будешь готов", false, buttonQuery);
                break;

            case "buttonMan": {
                UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
                userProfileData.setGender("М");
                userDataCache.saveUserProfileData(userId, userProfileData);
                userDataCache.setUsersCurrentBotState(userId, BotState.ASK_COLOR);
                callBackAnswer = new SendMessage(chatId, "Твоя любимая цифра");
                break;
            }
            case "buttonWoman": {
                UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
                userProfileData.setGender("Ж");
                userDataCache.saveUserProfileData(userId, userProfileData);
                userDataCache.setUsersCurrentBotState(userId, BotState.ASK_COLOR);
                callBackAnswer = new SendMessage(chatId, "Твоя любимая цифра");

                break;
            }
            default:
                userDataCache.setUsersCurrentBotState(userId, BotState.SHOW_MAIN_MENU);
                break;
        }

        return callBackAnswer;
    }

    private AnswerCallbackQuery sendAnswerCallbackQuery(String text, boolean alert, CallbackQuery callbackquery) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setCallbackQueryId(callbackquery.getId());
        answerCallbackQuery.setShowAlert(alert);
        answerCallbackQuery.setText(text);
        return answerCallbackQuery;
    }

    public File getUsersProfile(int userId) throws IOException {
        UserProfileData userProfileData = userDataCache.getUserProfileData(userId);
        File profileFile = ResourceUtils.getFile("classpath:static/docs/users_profile.txt");

        try (FileWriter fw = new FileWriter(profileFile.getAbsoluteFile());
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(userProfileData.toString());
        }
        return profileFile;
    }
}
