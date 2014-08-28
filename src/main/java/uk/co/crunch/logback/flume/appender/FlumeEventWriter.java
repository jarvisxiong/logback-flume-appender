package uk.co.crunch.logback.flume.appender;

import ch.qos.logback.classic.spi.ILoggingEvent;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.FlumeException;
import org.apache.flume.api.RpcClient;
import org.apache.flume.api.RpcClientFactory;
import org.apache.flume.event.EventBuilder;

import java.nio.charset.Charset;
import java.util.concurrent.locks.ReentrantLock;

/**
 * This class performs the actual sending of events to Flume.
 *
 * See http://flume.apache.org/FlumeDeveloperGuide.html#rpc-clients-avro-and-thrift.
 */
public class FlumeEventWriter {

    private FlumeAppender flumeAppender;

    public FlumeEventWriter(FlumeAppender flumeAppender) {
        this.flumeAppender = flumeAppender;
    }

    /**
     * Send the message contained in the logback event to flume.
     * @param event - the logback event
     */
    public void sendFlumeEvent(ILoggingEvent event) {
        RpcClient rpcClient = null;

        try {
            Event flumeEvent = EventBuilder.withBody(flumeAppender.getLayout().doLayout(event), Charset.forName("UTF-8"));
            rpcClient = RpcClientFactory.getDefaultInstance(flumeAppender.getHost(), flumeAppender.getPort());
            rpcClient.append(flumeEvent);
        } catch (EventDeliveryException e) {
            flumeAppender.addError("Error sending event to flume: " + e.getMessage());
        } finally {
            if (rpcClient != null) {
                try {
                    rpcClient.close();
                } catch (FlumeException e) {
                    flumeAppender.addError("Error closing RpcClient: " + e.getMessage());
                }
            }
        }
    }
}
