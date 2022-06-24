package com.raliev.routes;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.osgi.framework.FrameworkUtil;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;

public class TransformationRouteBuilder extends RouteBuilder {

	private static final URL TRANSFORM_SCHEME = FrameworkUtil.getBundle(MessageCycleWriteRouteBuilder.class)
			.getBundleContext().getBundle().getEntry("/data/transform-scheme.xml");

	@Override
	public void configure() {
		from("direct:transform")
				.log("TransformationRouteBuilder")
				.process(exchange -> exchange.getMessage().setBody(transformXmlToJson(exchange), String.class))
				.to("jms:queue:DestinationQueue");
	}

	private String transformXmlToJson(Exchange exchange) {
		TransformerFactory factory = TransformerFactory.newInstance();
		String body = exchange.getMessage().getBody(String.class);
		StringWriter writer = new StringWriter();

		try (InputStream is = TRANSFORM_SCHEME.openStream()) {
			Transformer transformer = factory.newTransformer(new StreamSource(is));
			transformer.transform(new StreamSource(new StringReader(body)), new StreamResult(writer));

		} catch (IOException | TransformerException e) {
			throw new RuntimeException("Failed to transform the message", e);
		}

		return writer.toString();
	}
}
