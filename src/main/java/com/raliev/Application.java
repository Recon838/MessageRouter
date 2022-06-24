package com.raliev;

import com.raliev.routes.MessageLogRouteBuilder;
import com.raliev.routes.MessageCycleWriteRouteBuilder;
import com.raliev.routes.TransformationRouteBuilder;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.log4j.PropertyConfigurator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import javax.jms.ConnectionFactory;

public class Application implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		PropertyConfigurator.configure(context.getBundle().getEntry("/log4j.properties"));

		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("admin", "admin", "tcp://localhost:61616");

		CamelContext camelContext = new DefaultCamelContext();
		camelContext.addComponent("jms", JmsComponent.jmsComponentAutoAcknowledge(connectionFactory));
		camelContext.addRoutes(new MessageLogRouteBuilder());
		camelContext.addRoutes(new TransformationRouteBuilder());
		camelContext.addRoutes(new MessageCycleWriteRouteBuilder());

		camelContext.start();

		Thread.sleep(5 * 60 * 1000);
		camelContext.stop();
	}

	@Override
	public void stop(BundleContext bundleContext) {
		// do nothing
	}
}
