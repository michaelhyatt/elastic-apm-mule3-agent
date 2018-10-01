package co.elastic.apm.mule;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.elastic.apm.impl.ElasticApmTracer;
import co.elastic.apm.impl.ElasticApmTracerBuilder;

@Configuration
public class ApmClient {

	private ElasticApmTracer tracer;

	public ApmClient() {
		ElasticApmTracerBuilder builder = new ElasticApmTracerBuilder();
		tracer = builder.build();
	}

	@Bean
	public ElasticApmTracer getTracer() {
		return tracer;
	}
	
}
