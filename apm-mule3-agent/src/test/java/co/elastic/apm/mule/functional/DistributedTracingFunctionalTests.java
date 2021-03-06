package co.elastic.apm.mule.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.api.transport.PropertyScope;

import co.elastic.apm.agent.impl.error.ErrorCapture;
import co.elastic.apm.agent.impl.transaction.Span;
import co.elastic.apm.agent.impl.transaction.Transaction;

@RunWith(MockitoJUnitRunner.class)
public class DistributedTracingFunctionalTests extends AbstractApmFunctionalTestCase {

	@Test
	public void testDistributedTracingHeaderPropagation() throws Exception {

		// Injecting elastic-apm-traceparent header and validating propagation
		MuleMessage message = getTestMuleMessage();

		String value = "00-8bcf1a675fec3e77a6159b5595b74509-da91f142a15ba4fc-01";
		message.setProperty("elastic-apm-traceparent", value, PropertyScope.INBOUND);

		MuleEvent response = runFlow("dt1_senderFlow", message);
		
		Mockito.verify(reporter, Mockito.times(10)).report(Mockito.any(Span.class));
		Mockito.verify(reporter, Mockito.times(1)).report(Mockito.any(Transaction.class));
		Mockito.verify(reporter, Mockito.times(0)).report(Mockito.any(ErrorCapture.class));

		MuleMessage responseMessage = response.getMessage();

		assertEquals(value.split("-")[0], responseMessage.getInboundProperty("result").toString().split("-")[0]);
		assertEquals(value.split("-")[1], responseMessage.getInboundProperty("result").toString().split("-")[1]);
		assertNotEquals(value.split("-")[2], responseMessage.getInboundProperty("result").toString().split("-")[2]);
	}

	@Override
	protected String getConfigResources() {
		return "test_tracer.xml, test_dt1.xml";
	}

}
