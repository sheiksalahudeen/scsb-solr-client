package org.recap.camel.activemq;

import org.apache.activemq.broker.jmx.DestinationViewMBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;

/**
 * Created by peris on 11/4/16.
 */
@Component
public class JmxHelper {

    @Value("${activemq.jmx.service.url}")
    private String serviceUrl;
    private MBeanServerConnection connection;

    public DestinationViewMBean getBeanForQueueName(String queueName) {
        try {
            ObjectName nameConsumers = new ObjectName("org.apache.activemq:type=Broker,brokerName=localhost,destinationType=Queue,destinationName="+ queueName);
            DestinationViewMBean mbView = MBeanServerInvocationHandler.newProxyInstance(getConnection(), nameConsumers, DestinationViewMBean.class, true);
            return mbView;
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        }
        return null;
    }

    public MBeanServerConnection getConnection() {
        if (null == connection) {
            JMXConnector connector = null;
            try {
                connector = JMXConnectorFactory.connect(new JMXServiceURL(serviceUrl));
                connection = connector.getMBeanServerConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }
}
