package io.omengye.ws.config;

import org.springframework.boot.context.embedded.undertow.UndertowEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.undertow.UndertowOptions;

@Configuration
public class UndertowConfigure {
	@Bean
	UndertowEmbeddedServletContainerFactory embeddedServletContainerFactory() {
	    UndertowEmbeddedServletContainerFactory factory = new UndertowEmbeddedServletContainerFactory();
	    factory.addBuilderCustomizers(builder -> builder.setServerOption(UndertowOptions.ENABLE_HTTP2, true));
	    return factory;
	}

}
