package brightspot.example;

import com.psddev.dari.db.Modification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloWorldModification extends Modification<SaysHelloWorld> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HelloWorldModification.class);

    @Override
    public void afterSave() {
        LOGGER.info("Hello, world!");
    }
}
