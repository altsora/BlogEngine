import main.model.entities.*;
import main.model.enums.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;

import java.time.LocalDateTime;
import java.util.*;

public class Test {
    private static final int COUNT_USERS = 10;
    private static final int COUNT_POSTS = 100;
    private static final int COUNT_TAGS = 20;
    private static final int COUNT_COMMENTS_NO_PARENT = new Random().nextInt(COUNT_POSTS) + 1;
    private static final int COUNT_COMMENTS_ON_POST = new Random().nextInt(10) + 1;
    //=================================================
    private static List<String> names;
    private static List<String> emails;
    private static List<String> passwords;
    private static List<String> codesUsers;
    private static List<Long> usersId;

    //Post
    private static List<String> titlesPosts;
    private static List<Long> postsId;

    // Tags
    private static List<String> tagsTest;

    // PostComments
    private static List<String> commentsTest;


    static {
        // User
        names = new ArrayList<>();
        names.add("Дуайт Фэйрфилд");
        names.add("Мэг Томас");
        names.add("Клодетт Морель");
        names.add("Джейк Парк");
        names.add("Нея Карлссон");
        names.add("Лори Строуд");
        names.add("Эйс Висконти");
        names.add("Уильям \"Билл\" Овербек");
        names.add("Фенг Мин");
        names.add("Дэвид Кинг");
        names.add("Квентин Смит");
        names.add("Детектив Тэпп");
        names.add("Кейт Денсон");
        names.add("Адам Фрэнсис");
        names.add("Джефф Йохансен");
        names.add("Джейн Ромеро");
        names.add("Эшли Джей Уильямс");
        names.add("Стив Харрингтон");
        names.add("Нэнси Уиллер");
        names.add("Юи Кимура");
        names.add("Зарина Кассир");

        emails = new ArrayList<>();
        passwords = new ArrayList<>();
        codesUsers = new ArrayList<>();
        usersId = new ArrayList<>();
        for (long i = 0; i < COUNT_USERS; i++) {
            emails.add("email" + (i + 1) + "@mail.ru");
            passwords.add("password " + (i + 1));
            codesUsers.add("code" + (i + 1));
            usersId.add(i + 1);
        }

        Collections.shuffle(usersId);
        //============================================

        // Post
        titlesPosts = new ArrayList<>();
        postsId = new ArrayList<>();
        for (long i = 0; i < COUNT_POSTS; i++) {
            titlesPosts.add("Заголовок " + (i + 1));
            postsId.add(i + 1);
        }

        Collections.shuffle(postsId);
        //============================================

        // Tag
        tagsTest = new ArrayList<>();
        for (int i = 0; i < COUNT_TAGS; i++) {
            tagsTest.add("#тег" + (i + 1));
        }
        //============================================

        // PostComments
        commentsTest = new ArrayList<>();
        for (int i = 0; i < COUNT_POSTS; i++) {
            commentsTest.add("Комментарий базовый " + (i + 1));
        }
    }

    public static void main(String[] args) {
        Session session = HibernateSessionFactory.getSessionFactory().openSession();
        session.getTransaction().begin();
//        randomGenerator(session);
        workDatabase(session);
        session.getTransaction().commit();
        session.close();
    }

    //==================================================================================================================

    private static void randomGenerator(Session session) {
        createGlobalSettings(session);
//        generationCaptchaCode(session);
        generationUsers(session);
        generationPosts(session);
        generationPostVotes(session);
        generationTags(session);
        generationTag2Posts(session);
        generationPostCommentsNoParent(session);
        generationPostCommentsChildren(session);
    }

    //==================================================================================================================

    private static List<User> users = new ArrayList<>();
    private static List<Post> posts = new ArrayList<>();
    private static List<PostVote> postVotes = new ArrayList<>();
    private static List<Tag> tags = new ArrayList<>();
    private static List<PostComment> comments = new ArrayList<>();

    private static void workDatabase(Session session) {
        createGlobalSettings(session);
        createUsers(session);
        createPosts(session);
        createPostVotes(session);
        createTags(session);
        createTag2Posts(session);
        createComments(session);
    }

    private static void createComments(Session session) {
        saveComment(users.get(0), posts.get(0), "Замечательное чтиво! Автор: Ричард Докинз.", session);
        saveComment(users.get(0), posts.get(1), "Важно понимать принцип работы...", session);
        saveComment(users.get(1), posts.get(1), "Э...А как это работает???", session);
        users.forEach(u -> saveComment(u, posts.get(2), "Zzzzzzz...", session));
        saveComment(users.get(7), posts.get(3), "Дружище, ты серьёзно? Ну не здесь же!", session);
        saveComment(users.get(0), posts.get(4), "Тащемта,секретов нет))", session);
        saveComment(users.get(1), posts.get(4), "Школу бы закончить сперва...", session);
        saveComment(users.get(2), posts.get(4), "Поступил и сплю, ни о чём не жалею", session);
        saveComment(users.get(3), posts.get(4), "Я ваш будущий преподаватель, будем знакомы.", session);
        saveComment(users.get(4), posts.get(4), "((((((((((((", session);
        saveComment(users.get(5), posts.get(4), "Мы справимся!!!", session);
        saveComment(users.get(6), posts.get(4), "XD", session);
        saveComment(users.get(7), posts.get(4), "Вот бы в швейное училище...", session);
    }

    private static void createTag2Posts(Session session) {
        saveTag2Post(tags.get(1), posts.get(0), session);
        saveTag2Post(tags.get(0), posts.get(1), session);
        saveTag2Post(tags.get(2), posts.get(1), session);
        saveTag2Post(tags.get(8), posts.get(1), session);
        saveTag2Post(tags.get(4), posts.get(3), session);
        saveTag2Post(tags.get(3), posts.get(4), session);
        saveTag2Post(tags.get(5), posts.get(4), session);
        saveTag2Post(tags.get(6), posts.get(4), session);
        saveTag2Post(tags.get(7), posts.get(4), session);
        saveTag2Post(tags.get(8), posts.get(4), session);
    }

    //9
    private static void createTags(Session session) {
        saveTag("Java", session);
        saveTag("Генетика", session);
        saveTag("Skillbox", session);
        saveTag("Институт", session);
        saveTag("Экономика", session);
        saveTag("Поступление", session);
        saveTag("Абитуриенты", session);
        saveTag("ЕГЭ", session);
        saveTag("Полезное", session);
    }

    //27
    private static void createPostVotes(Session session) {
        for (User user : users) {
            PostVote postVote = new PostVote();
            postVote.setUser(user);
            postVote.setPost(posts.get(0));
            postVote.setValue(Rating.LIKE);
            postVote.setTime(LocalDateTime.now());
            postVotes.add(postVote);
            session.save(postVote);
        }

        for (int i = 3; i < 5; i++) {
            PostVote postVote = new PostVote();
            postVote.setUser(users.get(i));
            postVote.setPost(posts.get(2));
            postVote.setValue(Rating.LIKE);
            postVote.setTime(LocalDateTime.now());
            postVotes.add(postVote);
            session.save(postVote);
        }

        for (int i = 0; i < 3; i++) {
            PostVote postVote = new PostVote();
            postVote.setUser(users.get(i));
            postVote.setPost(posts.get(2));
            postVote.setValue(Rating.DISLIKE);
            postVote.setTime(LocalDateTime.now());
            postVotes.add(postVote);
            session.save(postVote);
        }

        for (int i = 0; i < 2; i++) {
            PostVote postVote = new PostVote();
            postVote.setUser(users.get(i));
            postVote.setPost(posts.get(1));
            postVote.setValue(Rating.LIKE);
            postVote.setTime(LocalDateTime.now());
            postVotes.add(postVote);
            session.save(postVote);
        }

        for (User user : users) {
            PostVote postVote = new PostVote();
            postVote.setUser(user);
            postVote.setPost(posts.get(3));
            postVote.setValue(Rating.DISLIKE);
            postVote.setTime(LocalDateTime.now());
            postVotes.add(postVote);
            session.save(postVote);
        }

        for (int i = 0; i < 4; i++) {
            PostVote postVote = new PostVote();
            postVote.setUser(users.get(i));
            postVote.setPost(posts.get(4));
            postVote.setValue(Rating.LIKE);
            postVote.setTime(LocalDateTime.now());
            postVotes.add(postVote);
            session.save(postVote);
        }

    }

    //5
    private static void createPosts(Session session) {
        Post post0 = new Post();
        post0.setActivityStatus(ActivityStatus.ACTIVE);
        post0.setModerationStatus(ModerationStatus.ACCEPTED);
        User user0 = users.get(0);
        post0.setUser(user0);
        post0.setTime(getPostTime(user0));
        post0.setTitle("Эгоистичный ген");
        post0.setText("После смерти от нас остаются две вещи: наши гены и наши мемы.");
        post0.setViewCount(25);
        session.save(post0);
        posts.add(post0);

        Post post1 = new Post();
        post1.setActivityStatus(ActivityStatus.ACTIVE);
        post1.setModerationStatus(ModerationStatus.ACCEPTED);
        user0 = users.get(0);
        post1.setUser(user0);
        post1.setTime(getPostTime(user0));
        post1.setTitle("Топ 5 библиотек машинного обучения для Java");
        post1.setText("Машинное обучение — подход, при котором искусственный интеллект изначально не знает, как решать конкретную задачу, но обучается этому процессу с помощью решения сходных задач. " +
                "Для построения взаимосвязей используются разные математические методы. ");
        post1.setViewCount(10);
        session.save(post1);
        posts.add(post1);

        Post post2 = new Post();
        post2.setActivityStatus(ActivityStatus.ACTIVE);
        post2.setModerationStatus(ModerationStatus.ACCEPTED);
        post2.setModerator(user0);
        User user2 = users.get(2);
        post2.setUser(user2);
        post2.setTime(getPostTime(user2));
        post2.setTitle("СОН");
        post2.setText("Ребята, давайте дружно немного поспим...");
        post2.setViewCount(4);
        session.save(post2);
        posts.add(post2);

        Post post3 = new Post();
        post3.setActivityStatus(ActivityStatus.ACTIVE);
        post3.setModerationStatus(ModerationStatus.ACCEPTED);
        post3.setModerator(user0);
        User user3 = users.get(3);
        post3.setUser(user3);
        post3.setTime(getPostTime(user3));
        post3.setTitle("Современная экономика");
        post3.setText("Сегодня мы обсудим плановую экономику. Предлагайте свои варианты.");
        post3.setViewCount(33);
        session.save(post3);
        posts.add(post3);

        Post post4 = new Post();
        post4.setActivityStatus(ActivityStatus.ACTIVE);
        post4.setModerationStatus(ModerationStatus.ACCEPTED);
        post4.setModerator(user0);
        User user4 = users.get(4);
        post4.setUser(user4);
        post4.setTime(getPostTime(user4));
        post4.setTitle("Высшее учебное заведение");
        post4.setText("Надеюсь, мне удастся поступить...");
        post3.setViewCount(14);
        session.save(post4);
        posts.add(post4);
    }

    //8
    private static void createUsers(Session session) {
        User user0 = new User();
        user0.setName("Александр Вергун");
        user0.setModerator(true);
        user0.setEmail("vergun@mail.ru");
        user0.setRegTime(randomLocalDateTimePastNow());
        user0.setPassword("vergun1235");
        session.save(user0);
        users.add(user0);

        User user1 = new User();
        user1.setName("Арсений Романов");
        user1.setModerator(false);
        user1.setEmail("romanov@mail.ru");
        user1.setRegTime(randomLocalDateTimePastNow());
        user1.setPassword("romanov1235");
        session.save(user1);
        users.add(user1);

        User user2 = new User();
        user2.setName("Рустам Манафов");
        user2.setModerator(false);
        user2.setEmail("manafov@mail.ru");
        user2.setRegTime(randomLocalDateTimePastNow());
        user2.setPassword("manafov1235");
        session.save(user2);
        users.add(user2);

        User user3 = new User();
        user3.setName("Пётр Санктумов");
        user3.setModerator(false);
        user3.setEmail("vova@mail.ru");
        user3.setRegTime(randomLocalDateTimePastNow());
        user3.setPassword("vova1235");
        session.save(user3);
        users.add(user3);

        User user4 = new User();
        user4.setName("Святополк Афинский");
        user4.setModerator(false);
        user4.setEmail("miska@mail.ru");
        user4.setRegTime(randomLocalDateTimePastNow());
        user4.setPassword("miska1235");
        session.save(user4);
        users.add(user4);

        User user5 = new User();
        user5.setName("Анастасия Шестакова");
        user5.setModerator(false);
        user5.setEmail("shestakova@mail.ru");
        user5.setRegTime(randomLocalDateTimePastNow());
        user5.setPassword("shestakova1235");
        session.save(user5);
        users.add(user5);

        User user6 = new User();
        user6.setName("Макс Антипин");
        user6.setModerator(false);
        user6.setEmail("antipin@mail.ru");
        user6.setRegTime(randomLocalDateTimePastNow());
        user6.setPassword("antipin1235");
        session.save(user6);
        users.add(user6);

        User user7 = new User();
        user7.setName("Василий Круглов");
        user7.setModerator(false);
        user7.setEmail("kruglov@mail.ru");
        user7.setRegTime(randomLocalDateTimePastNow());
        user7.setPassword("kruglov1235");
        session.save(user7);
        users.add(user7);
    }

    private static LocalDateTime getCommentTime(Post post) {
        LocalDateTime postTime = post.getTime();
        LocalDateTime commentTime = randomLocalDateTimePastNow();
        while(commentTime.isBefore(postTime)) {
            commentTime = randomLocalDateTimePastNow();
        }
        return commentTime;
    }

    private static LocalDateTime getPostTime(User user) {
        LocalDateTime userRegTime = user.getRegTime();
        LocalDateTime postTime = randomLocalDateTimePastNow();
        while (postTime.isBefore(userRegTime)) {
            postTime = randomLocalDateTimePastNow();
        }
        return postTime;
    }

    private static void saveComment(User user, Post post, String text, Session session) {
        PostComment comment = new PostComment();
        comment.setUser(user);
        comment.setPost(post);
        comment.setTime(getCommentTime(post));
        comment.setText(text);
        comments.add(comment);
        session.save(comment);
    }
    private static void saveTag(String tagName, Session session) {
        Tag tag = new Tag();
        tag.setName(tagName);
        tags.add(tag);
        session.save(tag);
    }

    private static void saveTag2Post(Tag tag, Post post, Session session) {
        Tag2Post tag2Post = new Tag2Post();
        tag2Post.setTag(tag);
        tag2Post.setPost(post);
        session.save(tag2Post);
    }
    //==================================================================================================================

    private static void generationPostCommentsChildren(Session session) {
        String hql = "Select s from " + PostComment.class.getSimpleName() + " s";
        Query<PostComment> query = session.createQuery(hql);
        int countSourcesComment = query.getResultList().size();
        for (long i = 0; i < countSourcesComment; i++) {
            PostComment parentComment = session.get(PostComment.class, (i + 1));
            Post post = parentComment.getPost();
            int countCommentsOnParent = new Random().nextInt(10) + 1;
            for (int j = 0; j < countCommentsOnParent; j++) {
                PostComment childComment = new PostComment();
                long userId = new Random().nextInt(COUNT_USERS) + 1;
                User user = session.get(User.class, userId);
                LocalDateTime parentCommentTime = parentComment.getTime();
                LocalDateTime childCommentTime = randomLocalDateTimePastNow();
                while (childCommentTime.isBefore(parentCommentTime)) {
                    childCommentTime = randomLocalDateTimePastNow();
                }

                childComment.setUser(user);
                childComment.setParent(parentComment);
                childComment.setPost(post);
                childComment.setTime(childCommentTime);
                childComment.setText("Производный комментарий от " + user.getName());

                session.saveOrUpdate(childComment);
            }
        }
    }

    private static void generationPostCommentsNoParent(Session session) {
        for (long i = 3; i <= COUNT_POSTS; i++) {
            Post post = session.get(Post.class, i);
            int countCommentsOnPost = new Random().nextInt(10) + 1;
            for (int j = 0; j < countCommentsOnPost; j++) {
                long userId = new Random().nextInt(COUNT_USERS) + 1;
                User user = session.get(User.class, userId);
                LocalDateTime postTime = post.getTime();
                LocalDateTime userRegTime = user.getRegTime();
                LocalDateTime commentTime = randomLocalDateTimePastNow();
                while (commentTime.isBefore(postTime) && commentTime.isBefore(userRegTime)) {
                    commentTime = randomLocalDateTimePastNow();
                }
                String comment = "Комментарий " + (j + 1) + " от " + user.getName();

                PostComment postComment = new PostComment();
                postComment.setPost(post);
                postComment.setUser(user);
                postComment.setTime(commentTime);
                postComment.setText(comment);

                session.saveOrUpdate(postComment);
            }
        }
    }

    private static void generationTag2Posts(Session session) {
        for (long i = 0; i < tagsTest.size(); i++) {
            Tag tag = session.get(Tag.class, (i + 1));
            int countPost = new Random().nextInt(COUNT_POSTS) + 1;
            List<Long> indexPosts = new ArrayList<>();
            for (long j = 1; j <= COUNT_POSTS; j++) {
                indexPosts.add(j);
            }
            Collections.shuffle(indexPosts);
            for (int j = 0; j < countPost; j++) {
                Post post = session.get(Post.class, indexPosts.get(j));

                Tag2Post tag2Post = new Tag2Post();
                tag2Post.setTag(tag);
                tag2Post.setPost(post);

                session.saveOrUpdate(tag2Post);
            }
        }
    }

    private static void generationTags(Session session) {
        for (int i = 0; i < COUNT_TAGS; i++) {
            Tag tag = new Tag();
            tag.setName("tag" + (i + 1));
            session.saveOrUpdate(tag);
        }
    }

    private static void generationPostVotes(Session session) {
        int usersCount = (int) (Math.random() * COUNT_USERS) + 1;
        for (int i = 0; i < usersCount; i++) {
            int postCount = (int) (Math.random() * COUNT_POSTS) + 1;
            Collections.shuffle(postsId);
            for (int j = 0; j < postCount; j++) {
                long userId = usersId.get(i);
                long postId = postsId.get(j);
                User user = session.get(User.class, userId);
                Post post = session.get(Post.class, postId);

                PostVote postVote = new PostVote();
                postVote.setUser(user);
                postVote.setPost(post);
//                postVote.setValue((byte) (new Random().nextBoolean() ? 1 : -1));
                postVote.setValue(new Random().nextBoolean() ? Rating.LIKE : Rating.DISLIKE);

                LocalDateTime regTime = user.getRegTime();
                LocalDateTime likeTime = randomLocalDateTimePastNow();
                while (likeTime.isBefore(regTime)) {
                    likeTime = randomLocalDateTimePastNow();
                }
                postVote.setTime(likeTime);

                session.saveOrUpdate(postVote);
            }
        }
    }

    private static void generationPosts(Session session) {
        for (int i = 0; i < COUNT_POSTS; i++) {
            Post post = new Post();
            Random random = new Random();
            ModerationStatus[] types = ModerationStatus.values();
//            post.setModerationStatus(types[(int) (Math.random() * types.length)]);
            post.setModerationStatus(ModerationStatus.ACCEPTED);

            long userId = (int) (Math.random() * COUNT_USERS) + 1;
            User user = session.get(User.class, userId);
            post.setUser(user);
            post.setTitle("Title " + (i + 1));
            LocalDateTime userRegTime = user.getRegTime();
//            LocalDateTime postTime = randomLocalDateTimePastFuture();
            LocalDateTime postTime = randomLocalDateTimePastNow();
//            LocalDateTime postTime = LocalDateTime.of(2020, 2, 1, 0, 0);
            while (postTime.isBefore(userRegTime)) {
//                postTime = randomLocalDateTimePastFuture();
                postTime = randomLocalDateTimePastNow();
            }
            post.setTime(postTime);
//            post.setIsActive((byte) (random.nextBoolean() ? 1 : 0));
//            post.setIsActive((byte)1);
            post.setActivityStatus(ActivityStatus.ACTIVE);
            post.setText("Text " + (i + 1));
            post.setViewCount(new Random().nextInt(COUNT_USERS) + 1);

            if (new Random().nextBoolean()) {
                long moderatorId = (int) (Math.random() * COUNT_USERS) + 1;
                User moderator = session.get(User.class, moderatorId);
                post.setModerator(moderator);
            }

            session.saveOrUpdate(post);
        }
    }

    private static void generationUsers(Session session) {
        for (int i = 0; i < COUNT_USERS; i++) {
            Random random = new Random();
            User user = new User();
//            user.setIsModerator((byte) (random.nextBoolean() ? 1 : 0));
            user.setModerator(random.nextBoolean());
            user.setName("UserName " + (i + 1));
            user.setRegTime(randomLocalDateTimePastNow());
//            user.setRegTime(LocalDateTime.of(2020, 1, 1, 0, 0));
            user.setEmail("email_" + (i + 1) + "@mail.ru");
            user.setPassword("password_" + (i + 1));
//            user.setPhoto("");
//            user.setCode(null);
            session.saveOrUpdate(user);
        }
    }

    private static void generationCaptchaCode(Session session) {
        CaptchaCode captchaCode = new CaptchaCode();
        captchaCode.setTime(LocalDateTime.now());
        captchaCode.setSecretCode("SecretCode1");
        captchaCode.setCode("Code1");

        session.save(captchaCode);
    }

    private static void createGlobalSettings(Session session) {
        GlobalSetting settings1 = new GlobalSetting();
        settings1.setCode(SettingsCode.MULTIUSER_MODE);
        settings1.setName("Многопользовательский режим");
        settings1.setValue(SettingsValue.YES);

        GlobalSetting settings2 = new GlobalSetting();
        settings2.setCode(SettingsCode.POST_PREMODERATION);
        settings2.setName("Премодерация постов");
        settings2.setValue(SettingsValue.YES);

        GlobalSetting settings3 = new GlobalSetting();
        settings3.setCode(SettingsCode.STATISTICS_IS_PUBLIC);
        settings3.setName("Показывать всем статистику блога");
        settings3.setValue(SettingsValue.YES);

        session.saveOrUpdate(settings1);
        session.saveOrUpdate(settings2);
        session.saveOrUpdate(settings3);
    }

    private static <T> void showTable(Class<T> t, Session session) {
        String hql = "SELECT S FROM " + t.getSimpleName() + " S";
        List<T> list = session.createQuery(hql).getResultList();

        for (T t1 : list) {
            System.out.println("\n" + t1 + "\n");
        }
    }

    private static String randomString(int lengthString) {
        String symbols = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder builder = new StringBuilder();
        int length = 1 + (int) Math.round(lengthString * Math.random());
        int symbolsCount = symbols.length();
        for (int i = 0; i < length; i++) {
            builder.append(symbols.charAt((int) (symbolsCount * Math.random())));
        }
        return builder.toString();
    }

    private static String randomString(int minLength, int lengthString) {
        String symbols = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder builder = new StringBuilder();
        int length = minLength + (int) Math.round(lengthString * Math.random());
        int symbolsCount = symbols.length();
        for (int i = 0; i < length; i++) {
            builder.append(symbols.charAt((int) (symbolsCount * Math.random())));
        }
        return builder.toString();
    }

    private static LocalDateTime randomLocalDateTimePastNow() {
        Random random = new Random();
        LocalDateTime now = LocalDateTime.now();
        long years = random.nextInt(5);
        long days = random.nextInt(28);
        long month = random.nextInt(12);
        long hours = random.nextInt(24);
        long minutes = random.nextInt(60);
        now = now.minusYears(years);
        now = now.minusDays(days);
        now = now.minusMonths(month);
        now = now.minusHours(hours);
        now = now.minusMinutes(minutes);

        return now;
    }

    private static LocalDateTime randomLocalDateTimePastNow2() {
        Random random = new Random();
        LocalDateTime now = LocalDateTime.now();
        long years = random.nextInt(2);
        long days = random.nextInt(2);
        long month = random.nextInt(2);
        long hours = random.nextInt(2);
        long minutes = random.nextInt(2);
        now = now.minusYears(years);
        now = now.minusDays(days);
        now = now.minusMonths(month);
        now = now.minusHours(hours);
        now = now.minusMinutes(minutes);

        return now;
    }

    private static LocalDateTime randomLocalDateTimePastFuture() {
        Random random = new Random();
        LocalDateTime now = LocalDateTime.now();
        long years = random.nextInt(5);
        long days = random.nextInt(28);
        long month = random.nextInt(12);
        long hours = random.nextInt(24);
        long minutes = random.nextInt(60);

        if (random.nextBoolean()) {
            now = now.plusYears(years);
            now = now.plusDays(days);
            now = now.plusMonths(month);
            now = now.plusHours(hours);
            now = now.plusMinutes(minutes);
        } else {
            long yearsFuture = random.nextInt(1);
            long daysFuture = random.nextInt(7);
            long monthFuture = random.nextInt(1);
            long hoursFuture = random.nextInt(1);
            long minutesFuture = random.nextInt(1);
            now = now.minusYears(yearsFuture);
            now = now.minusDays(daysFuture);
            now = now.minusMonths(monthFuture);
            now = now.minusHours(hoursFuture);
            now = now.minusMinutes(minutesFuture);
        }

        return now;
    }
}

class HibernateSessionFactory {
    private static SessionFactory sessionFactory = buildSessionFactory();

    protected static SessionFactory buildSessionFactory() {
        // A SessionFactory is set up once for an application!
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure("hibernate.cfg.xml") // configures settings from hibernate.cfg.xml
                .build();
        try {
            sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        } catch (Exception e) {
            // The registry would be destroyed by the SessionFactory, but we had trouble building the SessionFactory
            // so destroy it manually.
            StandardServiceRegistryBuilder.destroy(registry);

            throw new ExceptionInInitializerError("Initial SessionFactory failed" + e);
        }
        return sessionFactory;
    }


    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        // Close caches and connection pools
        getSessionFactory().close();
    }
}
