package ua.com.alevel.bot.botapi.handlers.menu;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ua.com.alevel.bot.botapi.BotState;
import ua.com.alevel.bot.botapi.InputMessageHandler;
import ua.com.alevel.bot.service.MainMenuService;
import ua.com.alevel.bot.service.parcers.OpenWeatherMapJsonParserService;


@Component
public class WeatherMenuHandler implements InputMessageHandler {
    private final MainMenuService mainMenuService;
    private final OpenWeatherMapJsonParserService weatherParser;

    public WeatherMenuHandler(MainMenuService mainMenuService, OpenWeatherMapJsonParserService weatherParser) {
        this.mainMenuService = mainMenuService;
        this.weatherParser = weatherParser;
    }

    @Override
    public SendMessage handle(Message message) {
        return mainMenuService.getMainMenuMessage(message.getChatId(),
                weatherParser.getReadyForecast());
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_WEATHER_MENU;
    }
}
