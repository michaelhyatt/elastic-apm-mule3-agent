package co.elastic.apm.mule.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.mule.api.MuleMessage;
import org.mule.api.transformer.DataType;
import org.mule.api.transport.PropertyScope;

import co.elastic.apm.agent.impl.error.ErrorCapture;
import co.elastic.apm.agent.impl.transaction.Span;
import co.elastic.apm.agent.impl.transaction.Transaction;
import co.elastic.apm.mule.utils.PropertyUtils;

@RunWith(MockitoJUnitRunner.class)
public class FlowFunctionalTests extends AbstractApmFunctionalTestCase {

	@Test
	public void simpleFlowSendsOneTransaction() throws Exception {

		runFlow("test1Flow");

		Mockito.verify(reporter, Mockito.times(1)).report(Mockito.any(Span.class));
		Mockito.verify(reporter, Mockito.times(1)).report(Mockito.any(Transaction.class));
		Mockito.verify(reporter, Mockito.times(0)).report(Mockito.any(ErrorCapture.class));

		assertEquals("test1Flow", tx.getNameAsString());
		assertEquals("Logger", spans.get(0).getNameAsString());
	}

	@Test
	public void parallelFlowSendsOneTransaction() throws Exception {

		runFlow("parallelFlow");

		Mockito.verify(reporter, Mockito.times(7)).report(Mockito.any(Span.class));
		Mockito.verify(reporter, Mockito.times(1)).report(Mockito.any(Transaction.class));
		Mockito.verify(reporter, Mockito.times(0)).report(Mockito.any(ErrorCapture.class));

		assertEquals("parallelFlow", tx.getNameAsString());
		assertEquals("Logger1", spans.get(0).getNameAsString());
		assertEquals("Logger", spans.get(1).getNameAsString());
		assertEquals("Logger", spans.get(2).getNameAsString());
		assertEquals("Logger", spans.get(3).getNameAsString());
		assertEquals("Logger", spans.get(4).getNameAsString());
		assertEquals("Scatter-Gather", spans.get(5).getNameAsString());
		assertEquals("Logger4", spans.get(6).getNameAsString());

	}

	@Test
	public void simpleFlowSendsOneTransactionWithProperty() throws Exception {

		MuleMessage msg = this.getTestMuleMessage();

		msg.setProperty("testProp", "testValue", PropertyScope.INBOUND, DataType.STRING_DATA_TYPE);
		msg.setProperty("not_testProp", "testValue", PropertyScope.INBOUND, DataType.STRING_DATA_TYPE);
		msg.setPayload("TEST", DataType.STRING_DATA_TYPE);
		System.setProperty(PropertyUtils.ELASTIC_APM_MULE_CAPTURE_INPUT_PROPERTIES, "true");
		System.setProperty(PropertyUtils.ELASTIC_APM_MULE_CAPTURE_INPUT_PROPERTIES_REGEX, "testPro(.*)");

		runFlow("test1Flow", msg);

		Mockito.verify(reporter, Mockito.times(1)).report(Mockito.any(Span.class));
		Mockito.verify(reporter, Mockito.times(1)).report(Mockito.any(Transaction.class));
		Mockito.verify(reporter, Mockito.times(0)).report(Mockito.any(ErrorCapture.class));

		assertEquals("test1Flow", tx.getNameAsString());

		// Logged property
		assertEquals("testValue", tx.getContext().getLabel("in:testProp"));

		// Filtered out property
		assertNull(tx.getContext().getLabel("in:not_testProp"));

		assertEquals("Logger", spans.get(0).getNameAsString());
	}

	@Test
	public void testFlowWith4steps() throws Exception {

		System.setProperty(PropertyUtils.ELASTIC_APM_MULE_CAPTURE_OUTPUT_PROPERTIES, "true");
		System.setProperty(PropertyUtils.ELASTIC_APM_MULE_CAPTURE_OUTPUT_PROPERTIES_REGEX, "http(.*)");

		runFlow("test2Flow");

		Mockito.verify(reporter, Mockito.times(4)).report(Mockito.any(Span.class));
		Mockito.verify(reporter, Mockito.times(1)).report(Mockito.any(Transaction.class));
		Mockito.verify(reporter, Mockito.times(0)).report(Mockito.any(ErrorCapture.class));

		assertEquals("test2Flow", tx.getNameAsString());

		assertEquals("Logger", spans.get(0).getNameAsString());
		assertEquals("logger", spans.get(0).getType().toString());
		assertEquals("Groovy", spans.get(1).getNameAsString());
		assertEquals("scripting:component", spans.get(1).getType().toString());
		assertEquals("VM", spans.get(2).getNameAsString());
		assertEquals("vm:outbound-endpoint", spans.get(2).getType().toString());
		assertEquals("Property", spans.get(3).getNameAsString());
		assertEquals("set-property", spans.get(3).getType().toString());

		assertEquals("201", tx.getContext().getLabel("out:http.response"));
	}

	@Override
	protected String getConfigResources() {
		return "test_tracer.xml, test1.xml, test2.xml, parallel_flow.xml";
	}

}
