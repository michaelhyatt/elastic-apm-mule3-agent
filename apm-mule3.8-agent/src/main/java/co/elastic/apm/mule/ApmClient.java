package co.elastic.apm.mule;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import co.elastic.apm.attach.ElasticApmAttacher;

/**
 * @author michaelhyatt
 * 
 *         Initialise Elastic Apm instrumentaton on startup
 *
 */
@Component
public class ApmClient {

	private static final String ELASTIC_APM = "elastic.apm.";
	private static Logger logger = LogManager.getLogger(ApmClient.class);
	private static volatile boolean initialised = false;

	private ApmClient() {

		initialiseElasticApm();

	}

	public static void initialiseElasticApm() {
		logger.debug("Elastic APM system properties set:");

		Supplier<Stream<Entry<Object, Object>>> apmPropertiesFilter = () -> System.getProperties().entrySet().stream()
				.filter(x -> x.getKey().toString().startsWith(ELASTIC_APM));

		apmPropertiesFilter.get().map(x -> " " + x.getKey() + "=" + x.getValue()).forEach(logger::debug);

		logger.debug("Attaching ElasticApmAttacher");

		Map<String, String> configMap = apmPropertiesFilter.get().collect(
				Collectors.toMap(k -> k.getKey().toString().replace(ELASTIC_APM, ""), v -> v.getValue().toString()));

		ElasticApmAttacher.attach(configMap);
		
		setInitialised();
	}

	public static boolean isInitialised() {
		return initialised;
	}

	public static void setInitialised() {
		ApmClient.initialised = true;
	}
}