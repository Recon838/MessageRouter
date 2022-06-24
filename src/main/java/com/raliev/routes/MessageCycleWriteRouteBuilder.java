package com.raliev.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.io.IOUtils;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class MessageCycleWriteRouteBuilder extends RouteBuilder {
	private static final String MESSAGE;

	static {
		try {
			BundleContext context = FrameworkUtil.getBundle(MessageCycleWriteRouteBuilder.class).getBundleContext();
			URL messageUrl = context.getBundle().getEntry("/data/message.xml");
			try (InputStream is = messageUrl.openStream()) {
				MESSAGE = IOUtils.toString(is, StandardCharsets.UTF_8);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void configure() {
		from("timer://simpleTimer?period=10000") // send the message every 10 sec
				.setBody(simple(MESSAGE))
				.to("jms:queue:DestinationQueue");
	}
}
