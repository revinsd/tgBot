package org.example;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TruthOrDare {
    @Getter
    private final List<String> players = new ArrayList<>();
    @Getter
    private static final String rules = """
            Правила очень просты:
                 Если игрок отвечает: «Правда», то он должен будет правдиво ответить на вопрос, который ему будет задан.
                 Если он выбирает действие, то должен будет выполнить задание (совершить задаваемое ему действие).
                 После того как игрок ответил на вопрос или выполнил задание, то он нажимает кнопку «Готово»""";
    private final List<String> QUESTIONS = List.of(
            "Если бы у вас была машина времени, в какой период вы бы поехали?",
            "Вы когда-нибудь были загипнотизированы?",
            "За кого вы голосовали? Или за кого бы вы проголосовали?",
            "Если бы вам пришлось выбирать ум или красоту, что бы вы выбрали?",
            "Какой самый худший подарок вы когда-либо получали?",
            "Если бы вашу жизнь сняли в кино, кто бы вас сыграл?",
            "Сколько раз вы тайком убегали из дома?",
            "Можете ли вы прикоснуться языком к носу?",
            "Если бы вы могли жить где угодно в мире, где бы вы жили?",
            "Какой день в вашей жизни был лучшим?",
            "Вы занимаетесь каким-нибудь спортом?",
            "Вы ведете дневник?",
            "Какой самый мерзкий розыгрыш вы над кем-то разыграли?",
            "Какой у вас был любимый мультфильм в детстве?",
            "Вас когда-нибудь рвало в чьей-то машине?",
            "Вы бы предпочли быть умным или счастливым, и почему?",
            "Самая твоя большая ложь?",
            "Основные причины того, что ты злишься?",
            "Какой супергерой больше всего похож на тебя?",
            "Какие твоя слабости используют другие люди?",
            "Какой настораживающий факт можешь рассказать о себе?",
            "Самый странный сон, случившийся с тобой?",
            "Что больше всего мешает в жизни?",
            "Чем хочешь заняться на пенсии?",
            "Когда планируешь создать семью и завести детей?",
            "Тебя когда-нибудь унижали?",
            "О чем ты надеешься, что родители не узнают?",
            "Что самое нелепое искал (искала) в телефоне за последний месяц?",
            "От чего тебе удалось с трудом избавиться?",
            "Любимый цвет волос противоположного пола?",
            "Какие песни поешь под душем?",
            "Самая обидная вещь, что слышал про себя?"
    );
    private static final List<String> ACTIONS = List.of(
            "Съешь целиком сырую луковицу.",
            "Выпей литр воды.",
            "Потанцуй под музыку, которую выбрали другие.",
            "Спой любовную серенаду человеку слева.",
            "Спародируй одного из присутствующих.",
            "Отожмись или присядь 10 раз.",
            "Стой на одной ноге минуту.",
            "Встань на стул и прочитай стихотворение.",
            "Продемонстрируй походку Майкла Джексона или Конора Макгрегора.",
            "Познакомься с кем-то на улице.",
            "Укуси сам себя за ногу или кого-то другого.",
            "Напиши сообщение «я тебя люблю» и отправь 5 случайным контактам",
            "Ходи по улице маршем и пой гимн.",
            "Погавкай в окно на прохожих.",
            "Спой песню",
            "Выпей ложку подсолнечного масла или съешь кусок сливочного масла.",
            "Изображай лягушку, кенгуру или шимпанзе 5 минут.",
            "Нарисуй себе на лице усы.",
            "Скажи комплимент и гадость каждому присутствующему.",
            "Покажи позу, в которой обычно спишь.",
            "Придумай всем клички, характеризующие их.",
            "Изобрази известную личность, чтобы другие игроки угадали.",
            "Откуси или лизни мыло.",
            "Изображай цыганку и предлагай всем погадать.",
            "Расскажи смешной анекдот или историю, произошедшую с тобой.",
            "Устрой случайному человеку «прожарку». Высмеивай его недостатки 2 минуты.",
            "Покажи фокус."
    );
    private List<String> remainingQuestions;
    private List<String> remainingActions;
    private final Random random = new Random();
    @Getter
    private boolean started = false;
    private int counter = 0;

    public TruthOrDare startNew() {
        remainingActions = new ArrayList<>(ACTIONS);
        remainingQuestions = new ArrayList<>(QUESTIONS);
        started = true;
        return this;
    }

    public void stop() {
        started = false;
    }

    public String getNewQuestion() {
        if (remainingQuestions.size() == 0)
            return "Список вопросов пуст";
        var question = remainingQuestions.get(
                random.nextInt(remainingQuestions.size())
        );
        remainingQuestions.remove(question);
        return question;
    }

    public String getNewAction() {
        if (remainingActions.size() == 0)
            return "Список действий пуст";
        var action = remainingActions.get(
                random.nextInt(remainingActions.size())
        );
        remainingActions.remove(action);
        return action;
    }

    public String getNextPlayer() {
        if (counter == players.size())
            counter = 0;
        return players.get(counter++);
    }

    public String getPlayersList(){
        var playersList = new StringBuilder();
        players.forEach(player->{
            playersList.append(player)
                    .append("\n");
        });
        return playersList.toString();
    }
}