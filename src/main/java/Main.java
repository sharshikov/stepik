import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static final String AUSTIN_POWERS = "Austin Powers";
    public static final String WEAPONS = "weapons";
    public static final String BANNED_SUBSTANCE = "banned substance";
    public static void main(String[] args) {

        Logger logger = Logger.getLogger(Main.class.getName());
        Inspector inspector = new Inspector();
        Spy spy = new Spy(logger);
        Thief thief = new Thief(10000);
        MailService variousWorkers[] = new MailService[]{spy, thief, inspector};
        UntrustworthyMailWorker worker = new UntrustworthyMailWorker(variousWorkers);

        AbstractSendable correspondence[] = {
                new MailMessage("Oxxxymiron", "Гнойный", "Я здесь чисто по фану, поглумиться над слабым\n" +
                        "Ты же вылез из мамы под мой дисс на Бабана...."),
                new MailMessage("Гнойный", "Oxxxymiron", "....Что? Так болел за Россию, что на нервах терял ганглии.\n" +
                        "Но когда тут проходили митинги, где ты сидел? В Англии!...."),
                new MailMessage("Жриновский", AUSTIN_POWERS, "Бери пацанов, и несите меня к воде."),
                new MailMessage(AUSTIN_POWERS, "Пацаны", "Го, потаскаем Вольфовича как Клеопатру"),
                new MailPackage("берег", "море", new Package("ВВЖ", 32)),
                new MailMessage("NASA", AUSTIN_POWERS, "Найди в России ракетные двигатели и лунные stones"),
                new MailPackage(AUSTIN_POWERS, "NASA", new Package("рпакетный двигатель ", 2500000)),
                new MailPackage(AUSTIN_POWERS, "NASA", new Package("stones", 1000)),
                new MailPackage("Китай", "КНДР", new Package("banned substance", 99)),
                new MailPackage(AUSTIN_POWERS, "ИГИЛ (запрещенная группировка", new Package("tiny bomb", 9000)),
                new MailMessage(AUSTIN_POWERS, "Психиатр", "Помогите"),
        };
        Arrays.stream(correspondence).forEach(parcell -> {
            try {
                worker.processMail(parcell);
            } catch (StolenPackageException e) {
                logger.log(Level.WARNING, "Inspector found stolen package: " + e);
            } catch (IllegalPackageException e) {
                logger.log(Level.WARNING, "Inspector found illegal package: " + e);
            }
        });
    }

    public static abstract class AbstractSendable implements Sendable {

        protected final String from;
        protected final String to;

        public AbstractSendable(String from, String to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public String getFrom() {
            return from;
        }

        @Override
        public String getTo() {
            return to;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            AbstractSendable that = (AbstractSendable) o;

            if (!from.equals(that.from)) return false;
            if (!to.equals(that.to)) return false;

            return true;
        }

    }

    public static class IllegalPackageException extends RuntimeException {
        public IllegalPackageException() {
            super();
        }
    }

    public static class Inspector implements MailService {

        @Override
        public Sendable processMail(Sendable mail) {
            if (mail instanceof MailPackage) {
                if (((MailPackage) mail).getContent().getContent().contains("weapons") || ((MailPackage) mail).getContent().getContent().contains("banned substance"))
                    throw new IllegalPackageException();
                else if (((MailPackage) mail).getContent().getContent().contains("stones") && ((MailPackage) mail).getContent().getPrice()==0)
                    throw new StolenPackageException();
            }

            return mail;
        }
    }

    public static class MailMessage extends AbstractSendable {

        private final String message;

        public MailMessage(String from, String to, String message) {
            super(from, to);
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            MailMessage that = (MailMessage) o;

            if (message != null ? !message.equals(that.message) : that.message != null) return false;

            return true;
        }

    }

    public static class MailPackage extends AbstractSendable {
        private final Package content;

        public MailPackage(String from, String to, Package content) {
            super(from, to);
            this.content = content;
        }

        public Package getContent() {
            return content;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            MailPackage that = (MailPackage) o;

            if (!content.equals(that.content)) return false;

            return true;
        }

    }

    public static interface MailService {
        Sendable processMail(Sendable mail);
    }

    public static class Package {
        private final String content;
        private final int price;

        public Package(String content, int price) {
            this.content = content;
            this.price = price;
        }

        public String getContent() {
            return content;
        }

        public int getPrice() {
            return price;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Package aPackage = (Package) o;

            if (price != aPackage.price) return false;
            if (!content.equals(aPackage.content)) return false;

            return true;
        }
    }

    public static class RealMailService implements MailService {

        @Override
        public Sendable processMail(Sendable mail) {
            // Здесь описан код настоящей системы отправки почты.
            return mail;
        }
    }

    public static interface Sendable {
        String getFrom();

        String getTo();
    }

    public static class Spy implements MailService {

        Logger spyLogger;

        public Spy(Logger log) {
            this.spyLogger = log;
        }

        @Override
        public Sendable processMail(Sendable mail) {
            if (mail instanceof MailMessage)
                if (mail.getFrom().equals("Austin Powers") || mail.getTo().equals("Austin Powers")) {
                    spyLogger.log(Level.WARNING, "Detected target mail correspondence: from {0} to {1} \"{2}\"",
                            new Object[]{mail.getFrom(), mail.getTo(), ((MailMessage) mail).getMessage()});
                } else
                    spyLogger.log(Level.FINE, "Usual correspondence: from {0} to {1}", new Object[]{mail.getFrom(), mail.getTo()});
            return mail;
        }

    }

    public static class StolenPackageException extends RuntimeException {
        public StolenPackageException() {
            super();
        }
    }

    public static class Thief implements MailService {

        private int minPrice;
        private static int stolenValue = 0;

        public Thief(int minPrice) {
            this.minPrice = minPrice;
        }

        @Override
        public Sendable processMail(Sendable mail) {
            if (mail instanceof MailPackage)
                if (((MailPackage) mail).getContent().getPrice() >= minPrice) {
                    Package fakePackage = new Package("stones instead of " + ((MailPackage) mail).getContent().getContent(), 0);
                    mail = new MailPackage(mail.getFrom(), mail.getTo(), fakePackage);
                }
            return mail;
        }

        public static int getStolenValue() {
            return stolenValue;
        }

    }

    public static class UntrustworthyMailWorker implements MailService {

        private MailService[] mailarr;
        RealMailService realMailService = new RealMailService();

        public UntrustworthyMailWorker(MailService[] mailarr) {
            this.mailarr = mailarr;

        }

        public RealMailService getRealMailService() {
            return realMailService;
        }

        @Override
        public Sendable processMail(Sendable mail) {
            Sendable mail2 = mail;
            for (int i = 0; i < mailarr.length - 1; i++) {
                mail2 = mailarr[i + 1].processMail(mailarr[i].processMail(mail));
            }
            return realMailService.processMail(mail2);
        }
    }
}
