import com.example.Application;
import org.junit.Test;
import static org.junit.Assert.*;

public class AppTest {

    @Test
    public void testApp() {
        Application app = new Application();

        String result = app.getStatus();

        assertEquals("OK", result);
    }
}