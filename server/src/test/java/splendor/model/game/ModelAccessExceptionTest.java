package splendor.model.game;
import splendor.model.ModelAccessException;
import org.junit.jupiter.api.*;

public class ModelAccessExceptionTest {
    @Test
    public void createNewException() {
        String exceptionText = "exception";
        ModelAccessException exception = new ModelAccessException(exceptionText);
        Assertions.assertEquals(exceptionText, exception.getMessage());
    }
}
