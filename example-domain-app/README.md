When running the app, add the following VM args with correct values.
```
-Delastic.apm.log_level=INFO
-Delastic.apm.instrument=true
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
