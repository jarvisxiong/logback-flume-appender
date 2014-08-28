package uk.co.crunch.logback.flume.appender;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.core.status.Status;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertNotEquals;

public class FlumeAppenderTest {

    private FlumeAppender flumeAppender;

    @Mock
    private FlumeEventWriter eventWriter;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        flumeAppender = new FlumeAppender();
        flumeAppender.setEventWriter(eventWriter);
    }

    @Test
    public void debug() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        PatternLayout layout = new PatternLayout();
        layout.setPattern("%msg%n");
        layout.setContext(loggerContext);
        layout.start();

        flumeAppender.setName("FLUME");
        flumeAppender.setHost("54.76.122.255");
        flumeAppender.setPort(41414);
        flumeAppender.setLayout(layout);
        flumeAppender.setContext(loggerContext);
        flumeAppender.start();

        Logger logger = (Logger) LoggerFactory.getLogger(FlumeAppenderTest.class);
        logger.addAppender(flumeAppender);
        logger.setLevel(Level.DEBUG);
        logger.setAdditive(false);
        logger.debug("This is a test.");

        List<Status> statusList = loggerContext.getStatusManager().getCopyOfStatusList();
        for (Status status : statusList) {
            System.out.println(status.getMessage());
            assertNotEquals(status.getLevel(), Level.ERROR_INT);
        }
    }
}
