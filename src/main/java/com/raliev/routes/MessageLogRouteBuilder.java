package com.raliev.routes;

import org.apache.camel.LoggingLevel;
import org.apache.camel.Predicate;
import org.apache.camel.builder.PredicateBuilder;
import org.apache.camel.builder.RouteBuilder;

import java.time.LocalDate;

public class MessageLogRouteBuilder extends RouteBuilder {

	@Override
	public void configure() {
		from("jms:queue:DestinationQueue")
				.choice()
				.when(xmlPredicate())
					.setHeader("number").xpath("//*[local-name()='СтраховойНомерПФР']/text()")
					.log(LoggingLevel.INFO, String.format("%s : ${header.number}", LocalDate.now()))
					.to("direct:transform")
				.otherwise()
					.log(LoggingLevel.INFO, "SKIP MESSAGE: ${body}")
				.end();
	}

	private Predicate xmlPredicate() {
		return PredicateBuilder.and(body().startsWith("<?xml "), xpath("//*[local-name()='СтраховойНомерПФР']"));
	}
}
