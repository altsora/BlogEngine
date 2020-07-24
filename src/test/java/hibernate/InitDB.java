package hibernate;

import main.model.entity.*;
import main.model.enums.*;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class InitDB {

    private static final int COUNT_USERS = 10;
    private static final int COUNT_POSTS = 100;
    private static final int COUNT_TAGS = 20;
    private static final int COUNT_COMMENTS_NO_PARENT = new Random().nextInt(COUNT_POSTS) + 1;
    private static final int COUNT_COMMENTS_ON_POST = new Random().nextInt(10) + 1;
    //=================================================
    //User
    private static List<String> names;
    private static List<String> emails;
    private static List<String> passwords;
    private static List<String> codesUsers;
    private static List<Long> usersId;

    //Post
    private static List<String> titlesPosts;
    private static List<Long> postsId;

    // Tags
    private static List<String> tags;

    // PostComments
    private static List<String> comments;


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
        tags = new ArrayList<>();
        for (int i = 0; i < COUNT_TAGS; i++) {
            tags.add("#тег" + (i + 1));
        }
        //============================================

        // PostComments
        comments = new ArrayList<>();
        for (int i = 0; i < COUNT_POSTS; i++) {
            comments.add("Комментарий базовый " + (i + 1));
        }
    }

    public static void main(String[] args) {
        Session session = HibernateSessionFactory.getSessionFactory().openSession();
        session.getTransaction().begin();
        randomGenerator(session);
        session.getTransaction().commit();
        session.close();
    }

    //==================================================================================================================

    private static void randomGenerator(Session session) {
        generationUsers(session, 5);
//        createGlobalSettings(session);
//        generationCaptchaCode(session);
//        generationUsers(session);
//        generationPosts(session);
//        generationPostVotes(session);
//        generationTags(session);
//        generationTag2Posts(session);
//        generationPostCommentsNoParent(session);
//        generationPostCommentsChildren(session);
    }

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
        for (long i = 0; i < tags.size(); i++) {
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

    private static void generationUsers(Session session, int count) {
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            User user = new User();
            user.setModerator(random.nextBoolean());
            user.setName("UserName " + (i + 1));
            user.setRegTime(randomLocalDateTimePastNow());
            user.setEmail("email_" + (i + 1) + "@mail.ru");
            user.setPassword("password_" + (i + 1));
            session.saveOrUpdate(user);
        }
    }

    private static void generationUsers(Session session) {
        for (int i = 0; i < COUNT_USERS; i++) {
            Random random = new Random();
            User user = new User();
            user.setModerator(random.nextBoolean());
            user.setName("UserName " + (i + 1));
            user.setRegTime(randomLocalDateTimePastNow());
//            user.setRegTime(LocalDateTime.of(2020, 1, 1, 0, 0));
            user.setEmail("email_" + (i + 1) + "@mail.ru");
            user.setPassword("password_" + (i + 1));
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
        settings1.setValue(SettingsValue.NO);

        GlobalSetting settings2 = new GlobalSetting();
        settings2.setCode(SettingsCode.POST_PREMODERATION);
        settings2.setName("Премодерация постов");
        settings2.setValue(SettingsValue.YES);

        GlobalSetting settings3 = new GlobalSetting();
        settings3.setCode(SettingsCode.STATISTICS_IS_PUBLIC);
        settings3.setName("Показывать всем статистику блога");
        settings3.setValue(SettingsValue.YES);
//        settings3.setValue(SettingsValueType.NO);

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
