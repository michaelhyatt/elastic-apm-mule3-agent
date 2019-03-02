package co.elastic.apm.mule;

import co.elastic.apm.attach.ElasticApmAttacher;

/**
 * @author michaelhyatt
 * 
 *         Initialise Elastic Apm instrumentaton on startup
 *
 */
public class ApmClient {

	public void initElasticApmInstrumentation() {
//		ElasticApmAgent.initInstrumentation(new ElasticApmTracerBuilder().build(), ByteBuddyAgent.install());
		
		ElasticApmAttacher.attach();
	}
}