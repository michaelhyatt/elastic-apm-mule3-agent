package co.elastic.apm.mule.functional;

import org.junit.Test;
import org.mockito.Mockito;
import org.mule.tck.junit4.FunctionalTestCase;

import co.elastic.apm.bci.ElasticApmAgent;
import co.elastic.apm.impl.ElasticApmTracerBuilder;
import co.elastic.apm.impl.transaction.Transaction;
import co.elastic.apm.report.Reporter;
import net.bytebuddy.agent.ByteBuddyAgent;

public class SimpleFunctionalTest1 extends FunctionalTestCase {

	@Test
	public void simpleFlowSendsOneTransaction() throws Exception {

		Reporter reporter = Mockito.mock(Reporter.class);

		ElasticApmAgent.initInstrumentation(new ElasticApmTracerBuilder().reporter(reporter).build(),
				ByteBuddyAgent.install());

		runFlow("test1Flow");

		Mockito.verify(reporter, Mockito.times(1)).report(Mockito.any(Transaction.class));
	}

	@Override
	protected String getConfigResources() {
		return "test_tracer.xml, test1.xml";
	}

}
