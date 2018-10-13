# elastic-apm-mule3-agent
## Intro
This addon allows application performance monitoring of Mule 3.x components using Elastic APM. It provides a non-intrusive way to measure and benchmark individual flows and steps in Mule, adding the application performance monitoring of Mule components to reside in Elasticsearch alongside logs, metrics and other data.

## How it works
The agent is converting the top level flow into APM transaction, all the flow steps and flow references into APM spans linked to the top level transaction, as well as all the flow references that invoke the flow step as child spans. The implementation also allows capturing input and output properties in Mule flow, turning them into Transaction tags. From that point on, all the features of Elastic APM can be applied, such as standard and custom visualisations in Kibana, machine learning, as well as other features of the Elastic stack. 

## Setup
### Getting and building the code
Download the code by `git clone https://github.com/michaelhyatt/elastic-apm-mule3-agent`. Install the code in your 

### Mule flow

### Mule property configuration

## Things to know and consider
* Requires Maven 3.x to build jar file.
* Works with both, Mule 3.x CE and EE. Built with Mule CE 3.9.0.
* Only supports Mule 3.x at this stage.
* Only captures input and output properties, no flowVars at this stage.
* Compatible with Elastic APM 6.4.x and uses APM Java client v0.7.
* Elastic APM - https://www.elastic.co/solutions/apm
* Elastic APM Java client - https://github.com/elastic/apm-agent-java
