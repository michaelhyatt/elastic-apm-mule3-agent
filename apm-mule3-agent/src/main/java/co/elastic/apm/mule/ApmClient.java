package co.elastic.apm.mule;

import co.elastic.apm.bci.ElasticApmAgent;
import co.elastic.apm.impl.ElasticApmTracerBuilder;
import net.bytebuddy.agent.ByteBuddyAgent;

public class ApmClient {

	public void initElasticApmInstrumentation() {
		ElasticApmAgent.initInstrumentation(new ElasticApmTracerBuilder().build(), ByteBuddyAgent.install());
	}
}