package org.example;

import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Objects.isNull;

public class Bot extends TelegramLongPollingBot {

    private static final String GAME_IS_STARTING = "Игра начинается!";
    private static final String READY = "Готово!";
    private static final String GAME_ALLREADY_EXISTS_ERROR = "Для данного чата уже создана игра";
    private static final String NEW_GAME_CREATED = "Игра успешно создана, все желающие могут присоединиться";
    private static final String GAME_ALLREADY_STARTED_ERROR = "Игра уже запущена, остановить игру можно коммандой /stopgame";
    private static final String JOINING_ACTIVE_GAME_ERROR = "Невозможно подключиться к активной игре, дождитесь завершения";
    private static final String BOT_TOKEN = "5552914198:AAEtfC6HqahObIsiQa-wKRmsaoJp92f2ElM";
    private static final String BOT_USERNAME = "@truthOoorDareBot";
    private static final String SUCCESSFULLY_JOINED_GAME_TEMPLATE = "%s присоединился к игре!";
    private static final String PLAYER_ALREADY_JOINED_ERROR_TEMPLATE = "Игрок %s уже есть в игре";
    private static final String PLAYERS_LIST_TEMPLATE = "Список игроков:\n %s";
    private static final String SUCCESSFULLY_LEFT_GAME_TEMPLATE = "%s покинул игру";
    private static final List<List<InlineKeyboardButton>> TRUTH_OR_DARE_TEXT = List.of(List.of(
            InlineKeyboardButton.builder()
                    .text("Правда")
                    .callbackData("Question")
                    .build(),
            InlineKeyboardButton.builder()
                    .text("Действие")
                    .callbackData("Action")
                    .build()
    ));
    private final Map<String, TruthOrDare> games = new HashMap<>();


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            handleCallback(update.getCallbackQuery());
        } else if (update.hasMessage()) {
            handleMessage(update.getMessage());
        }

    }

    private void handleCallback(CallbackQuery callbackQuery) {
        var message = callbackQuery.getMessage();
        var chatId = message.getChatId().toString();
        var game = games.get(chatId);
        var username = "@" + callbackQuery.getFrom().getUserName();
        switch (callbackQuery.getData()) {
            case "Question" -> printWithButton(game.getNewQuestion(), READY, "Ready", chatId);
            case "Action" -> printWithButton(game.getNewAction(), READY, "Ready", chatId);
            case "Ready" -> nextPlayer(chatId);
            case "JoinGame" -> joinGame(chatId, username);
            case "StartGame" -> startGame(chatId);
        }
    }

    private void handleMessage(Message message) {
        var chatId = message.getChatId().toString();
        var username = "@" + message.getFrom().getUserName();
        if (message.hasText() && message.hasEntities()) {
            String command = getCommand(message);
            switch (command) {
                case "/creategame" -> createGame(chatId);
                case "/stopgame" -> stopGame(chatId);
                case "/leavegame" -> leaveGame(chatId, username);
            }
        }
    }


    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }


    public String getBotToken() {
        return BOT_TOKEN;
    }

    private String getCommand(Message message) {
        var commandEntity = message.getEntities()
                .stream()
                .filter(e -> "bot_command".equals(e.getType()))
                .findFirst();
        return commandEntity.map(messageEntity ->
                        message.getText().substring(messageEntity.getOffset(), messageEntity.getLength()))
                .orElse(null);
    }


    private void startGame(String chatId) {
        if (isGameExists(chatId)) {
            var game = games.get(chatId);
            if (game.isStarted())
                print(GAME_ALLREADY_STARTED_ERROR, chatId);
            else {
                game.startNew();
                print(format(PLAYERS_LIST_TEMPLATE, game.getPlayersList()), chatId);
                print(GAME_IS_STARTING, chatId);
                print(TruthOrDare.getRules(), chatId);
                nextPlayer(chatId);
            }
        }
    }

    private boolean isGameExists(String chatId) {
        var game = games.get(chatId);
        return game != null;
    }

    private void stopGame(String chatId) {
        if (isGameExists(chatId)) {
            var game = games.get(chatId);
            if (game.isStarted())
                game.stop();
        }

    }

    private void joinGame(String chatId, String username) {
        if (isGameExists(chatId)) {
            var game = games.get(chatId);
            if (game.getPlayers().contains(username)) {
                print(format(PLAYER_ALREADY_JOINED_ERROR_TEMPLATE, username), chatId);
                return;
            }
            if (!game.isStarted()) {
                game.getPlayers()
                        .add(username);
                print(format(SUCCESSFULLY_JOINED_GAME_TEMPLATE, username), chatId);
            } else {
                print(JOINING_ACTIVE_GAME_ERROR, chatId);
            }
        }
    }

    private void createGame(String chatId) {
        var game = games.get(chatId);
        if (isNull(game)) {
            games.put(chatId, new TruthOrDare());
            var buttons = List.of(
                    InlineKeyboardButton.builder()
                            .text("Присоединиться")
                            .callbackData("JoinGame")
                            .build(),
                    InlineKeyboardButton.builder()
                            .text("Начать игру")
                            .callbackData("StartGame")
                            .build()
            );
            printButtons(NEW_GAME_CREATED, buttons, chatId);
        } else
            print(GAME_ALLREADY_EXISTS_ERROR, chatId);
    }

    private void leaveGame(String chatId, String username) {
        if (isGameExists(chatId)) {
            var game = games.get(chatId);
            var players = game.getPlayers();
            if (players.contains(username)) {
                game.getPlayers().remove(username);
                print(format(SUCCESSFULLY_LEFT_GAME_TEMPLATE, username), chatId);
            }
        }
    }

    @SneakyThrows(value = TelegramApiException.class)
    private void print(String botMessage, String chatId) {
        execute(SendMessage.builder()
                .text(botMessage)
                .chatId(chatId)
                .build()
        );
    }

    //@SneakyThrows(value = TelegramApiException.class)
    private void printButtons(String text, List<InlineKeyboardButton> buttons, String chatId) {
        try {
            execute(SendMessage.builder()
                    .text(text)
                    .chatId(chatId)
                    .replyMarkup(InlineKeyboardMarkup.builder()
                            .keyboard(List.of(buttons))
                            .build()
                    ).build()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows(value = TelegramApiException.class)
    private void printWithButton(String text, String buttonText, String callback, String chatId) {
        execute(SendMessage.builder()
                .text(text)
                .chatId(chatId)
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboard(List.of(List.of(
                                InlineKeyboardButton.builder()
                                        .text(buttonText)
                                        .callbackData(callback)
                                        .build()
                        )))
                        .build()
                ).build()
        );
    }

    @SneakyThrows(value = TelegramApiException.class)
    private void nextPlayer(String chatId) {
        var game = games.get(chatId);
        execute(SendMessage.builder()
                .text(format("%s, твой выбор", game.getNextPlayer()))
                .chatId(chatId)
                .replyMarkup(
                        InlineKeyboardMarkup.builder()
                                .keyboard(TRUTH_OR_DARE_TEXT)
                                .build()
                )
                .build()
        );
    }

    @SneakyThrows(value = TelegramApiException.class)
    public static void main(String[] args) {
        var bot = new Bot();
        var telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(bot);
    }
}
