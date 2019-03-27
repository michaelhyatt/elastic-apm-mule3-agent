package co.elastic.apm.mule;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import co.elastic.apm.attach.ElasticApmAttacher;

/**
 * @author michaelhyatt
 * 
 *         Initialise Elastic Apm instrumentaton on startup
 *
 */
public class ApmClient {

	private Logger logger = LogManager.getLogger(ApmClient.class);

	public void initElasticApmInstrumentation() {

		logger.debug("Elastic APM system properties set:");

		if (logger.isDebugEnabled())
			System.getProperties().keySet().stream().filter(x -> x.toString().startsWith("elastic.apm."))
					.forEach(x -> logger.debug(" " + x + "=" + System.getProperty((String) x).toString()));

		logger.debug("Attaching ElasticApmAttacher");

		ElasticApmAttacher.attach();
	}
}