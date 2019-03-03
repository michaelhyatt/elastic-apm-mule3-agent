# Elastic APM Mule3 Agent
## Intro
This addon allows application performance monitoring of Mule 3.x components using Elastic APM. It provides a non-intrusive way to measure and benchmark individual flows and steps in Mule, adding the application performance monitoring of Mule components to reside in Elasticsearch alongside logs, metrics and other data. Mule APM agent supports distributed tracing allowing propagation of trace context using transport protocol meta-data, such as HTTP headers, and can present a unified view of the same trace spanning multiple components, built in Mule and other supported technologies. For more information about Elastic APM see [this link](https://www.elastic.co/solutions/apm). Also, feel free to check out the [sample app](example-app).

## How it works
The agent is converting the top level flow into APM trace and transactions, all the flow steps and flow references into APM spans linked to the top level transaction, as well as all the flow references that invoke the flow step as child spans. The implementation also allows capturing input and output properties in Mule flow, turning them into Transaction tags. From that point on, all the features of Elastic APM can be applied, such as standard and custom visualisations in Kibana, machine learning, as well as other features of the Elastic stack. The two images below illustrate how a given Mule flow is translated into APM transaction and underlying hierarchical spans. See below example Mule flow and how this flow will be represented in Elastic APM:
![Mule flow](./apm-mule3-agent.png)

APM transaction view:
![APM Transaction and Spans](./apm.png)

Example Canvas visualisation (built for `example-app`, see `kibana-objs` folder):
![Canvas1](./canvas1.png)

## Setup
### Getting and building the code
Download the code by `git clone https://github.com/michaelhyatt/elastic-apm-mule3-agent`. Install the jar in your Maven repo by `mvn install -Pjar`. Add the following dependency to your Mule project POM file:
```xml
<dependency>
    <groupId>co.elastic.apm</groupId>
    <artifactId>apm-mule3-agent</artifactId>
    <version>1.4.3</version>
</dependency>
```

### Enabling debugging within Mule flow
This is a good way to enable debugging in standalone Mule workers. Add the tracer to the main Mule flow. This import will ensure all the relevant modules for tracing will get loaded and connection will be made to your Elastic APM server:
```xml
<spring:beans>
    <spring:import resource="classpath:co/elastic/apm/mule/tracer.xml"/>
</spring:beans>
```

### Enabling debugging in Mule applications deployed into the same domain
Add the following to `mule-domain-config.xml` file within your domain. It will inject the APM client into all the projects deployed for this domain.
```xml
<spring:beans>
    <spring:import resource="classpath:co/elastic/apm/mule/domain-tracer.xml" />
</spring:beans>
```

### Mule property configuration
Make sure to add the following configuration options to your `mule-app.properties` file configuring your Mule application. More information about the available configuration properties for APM module can be found here - https://www.elastic.co/guide/en/apm/agent/java/current/config-core.html
```properties
# Elastic Apm Java client properties
elastic.apm.log_level=INFO
elastic.apm.instrument=false #leave it as false to disable autoinstrumentation
elastic.apm.active=true
elastic.apm.server_urls=http://localhost:8200
elastic.apm.application_packages=
elastic.apm.service_name=my-cool-service
elastic.apm.service_version=v1.0.0
elastic.apm.stack_trace_limit=3
elastic.apm.span_frames_min_duration=0ms

# Mule apm specific properties
elastic.apm.mule.capture_input_properties=true
elastic.apm.mule.capture_input_properties_regex=http_(.*)

elastic.apm.mule.capture_output_properties=true
elastic.apm.mule.capture_output_properties_regex=(.*)
```
Alternatively, it is possible to configure the APM agent using command line properties passed using the command line switch `-D`:
```properties
-Delastic.apm.log_level=INFO
-Delastic.apm.instrument=false
-Delastic.apm.active=true
-Delastic.apm.server_urls=http://localhost:8200
-Delastic.apm.application_packages=
-Delastic.apm.service_name=path-component
-Delastic.apm.service_version=v1.0.0
-Delastic.apm.stack_trace_limit=30
-Delastic.apm.span_frames_min_duration=0ms
-Delastic.apm.mule.capture_input_properties=true
-Delastic.apm.mule.capture_input_properties_regex=http_(.*)
-Delastic.apm.mule.capture_output_properties=true
-Delastic.apm.mule.capture_output_properties_regex=(.*)
```
Also, environment variables are supported, as per [Elastic APM Java agent configuration guide](https://www.elastic.co/guide/en/apm/agent/java/current/config-core.html).

### Capturing flowVar values as Span tags
To capture value of flowVars, it is possible to configure a regular expression matching the flowVar name and flow step name that will be used to capture the value of the flowVar as a Span tag:
```properties
# Enable flowVar capture into Span tags
elastic.apm.mule.capture_flowvars=true

# Capture all flowVars with names matching 'var*' at a flow step named "HTTP"
elastic.apm.mule.capture_at_HTTP=var.*
```

## Support for distributed tracing
The agent supports distributed tracing by propagating the trace context in property `elastic-apm-traceparent`. Protocols, such as HTTP, that can convert outbound properties into relevant protocol meta-data, i.e. HTTP headers, don't need to take any special precautions. Other protocols that ignore outbound properties, will need to explicitly map the above property into appropriate protocol related header or property.

## Things to know and consider
* Requires Maven 3.x to build jar file.
* Works with both, Mule 3.x CE and EE. Built with Mule CE 3.9.0.
* Only supports Mule 3.x at this stage.
* Only captures input and output properties, no flowVars at this stage.
* Compatible with Elastic APM 6.5 and 6.6 and uses APM Java client v1.4.0.
* Elastic APM - https://www.elastic.co/solutions/apm
* For the rest of configuration parameters, see Elastic APM Java client documentation - https://github.com/elastic/apm-agent-java
