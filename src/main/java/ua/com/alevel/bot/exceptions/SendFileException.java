package ua.com.alevel.bot.exceptions;

public class SendFileException extends Exception {
    public SendFileException(String message) {
        super(message);
    }

    public SendFileException() {
        super();
    }
}
