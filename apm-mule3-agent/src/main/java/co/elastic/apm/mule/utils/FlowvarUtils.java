package co.elastic.apm.mule.utils;

import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.mule.api.MuleEvent;
import org.mule.context.notification.MessageProcessorNotification;

public class FlowvarUtils {

	public static final String ELASTIC_APM_MULE_CAPTURE_FLOWVAR_PREFIX = "elastic.apm.mule.capture_at_";
	public static final String ELASTIC_APM_MULE_CAPTURE_FLOWVARS = "elastic.apm.mule.capture_flowvars";

	public static boolean isCaptureFlowvarsEnabled() {
		return Boolean.getBoolean(ELASTIC_APM_MULE_CAPTURE_FLOWVARS);
	}

	public static Stream<ImmutablePair<String, Object>> getFlowvars(MessageProcessorNotification notification) {
		MuleEvent source = notification.getSource();
		return source.getFlowVariableNames().stream()
				.map(x -> new ImmutablePair<String, Object>(x, source.getFlowVariable(x)));
	}

	public static boolean isFlowvarConfigured(ImmutablePair<String, Object> pair, String processorName) {
		String varName = pair.getLeft();

		String regex = System.getProperty(ELASTIC_APM_MULE_CAPTURE_FLOWVAR_PREFIX + processorName);
		if (regex == null)
			return false;

		return varName.matches(regex);
	}

}
