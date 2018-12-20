package co.elastic.apm.mule;

import co.elastic.apm.agent.bci.ElasticApmAgent;
import co.elastic.apm.agent.impl.ElasticApmTracerBuilder;
import net.bytebuddy.agent.ByteBuddyAgent;

/**
 * @author michaelhyatt
 * 
 *         Initialise Elastic Apm instrumentaton on startup
 *
 */
public class ApmClient {

	public void initElasticApmInstrumentation() {
		ElasticApmAgent.initInstrumentation(new ElasticApmTracerBuilder().build(), ByteBuddyAgent.install());
	}
}