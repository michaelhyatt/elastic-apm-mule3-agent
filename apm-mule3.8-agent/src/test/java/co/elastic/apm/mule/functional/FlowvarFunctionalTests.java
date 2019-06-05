package co.elastic.apm.mule.functional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.mule.api.MuleMessage;

import co.elastic.apm.agent.impl.error.ErrorCapture;
import co.elastic.apm.agent.impl.transaction.Span;
import co.elastic.apm.agent.impl.transaction.Transaction;
import co.elastic.apm.mule.utils.FlowvarUtils;

@RunWith(MockitoJUnitRunner.class)
public class FlowvarFunctionalTests extends AbstractApmFunctionalTestCase {

	@Test
	public void simpleFlowTestsOneFlowvar() throws Exception {

		MuleMessage msg = this.getTestMuleMessage();

		System.setProperty(FlowvarUtils.ELASTIC_APM_MULE_CAPTURE_FLOWVARS, "true");
		System.setProperty(FlowvarUtils.ELASTIC_APM_MULE_CAPTURE_FLOWVAR_PREFIX + "Logger", "var.*");

		runFlow("testflowvar1Flow", msg);

		Mockito.verify(reporter, Mockito.times(4)).report(Mockito.any(Span.class));
		Mockito.verify(reporter, Mockito.times(1)).report(Mockito.any(Transaction.class));
		Mockito.verify(reporter, Mockito.times(0)).report(Mockito.any(ErrorCapture.class));

		assertEquals("testflowvar1Flow", tx.getName().toString());

		assertEquals("Variable", spans.get(0).getName().toString());
		assertEquals("Variable", spans.get(1).getName().toString());
		assertEquals("Variable", spans.get(2).getName().toString());
		assertEquals("Logger", spans.get(3).getName().toString());

		assertEquals("123", spans.get(3).getContext().getLabel("flowVar:var1"));
		assertEquals("456", spans.get(3).getContext().getLabel("flowVar:var2"));
		assertNull(spans.get(3).getContext().getLabel("flowVar:abc"));
		assertNull(spans.get(0).getContext().getLabel("flowVar:var1"));
		assertNull(spans.get(1).getContext().getLabel("flowVar:var2"));
		assertNull(spans.get(2).getContext().getLabel("flowVar:var1"));

	}

	@Override
	protected String getConfigResources() {
		return "test_tracer.xml, testflowvar1.xml";
	}

}
