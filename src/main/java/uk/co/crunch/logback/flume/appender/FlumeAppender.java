package uk.co.crunch.logback.flume.appender;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;

/**
 * Simple logback appender that appends to apache Flume.
 *
 * It assumes that the flume agent running on the specified host + port is configured with an
 * Avro Source (http://flume.apache.org/FlumeUserGuide.html#avro-source).
 *
 * Example configuration in logback.xml:
 * <pre>
 * <configuration>
 *     <appender name="FLUME" class="uk.co.crunch.logback.flume.appender.FlumeAppender">
 *         <layout>
 *             <pattern>%msg%n</pattern>
 *         </layout>
 *         <host>127.0.0.1</host>
 *         <port>1234</port>
 *     </appender>
 *
 *     <logger name="foo.bar.MyClass" level="ALL">
 *         <appender-ref ref="FLUME" />
 *     </logger>
 * </configuration>
 * </pre>
 *
 * Limitations:
 * <ul>
 *     <li>currently doesn't support failover, fairly trivial to implement (http://flume.apache.org/FlumeDeveloperGuide.html#failover-client)</li>
 *     <li>doesn't batch events, each one is sent immediately</li>
 * </ul>
 */
public class FlumeAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    private FlumeEventWriter eventWriter = new FlumeEventWriter(this);
    private PatternLayout layout;
    private String host;
    private int port;

    public void setEventWriter(FlumeEventWriter eventWriter) {
        this.eventWriter =  eventWriter;
    }

    public PatternLayout getLayout() {
        return layout;
    }

    public void setLayout(PatternLayout layout) {
        this.layout = layout;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        if (!isStarted()) {
            return;
        }

        eventWriter.sendFlumeEvent(eventObject);
    }
}
