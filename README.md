# tracereconstruction
This application reads a log file from a micro services environment, and tries to reconstruct the traces. This is written to standard output and to file. 

### Installation
Clone the repository, build it using mvn package. This will generate a target folder containing the runnable jar (trace-reconstruction-0.1.jar) as well as a resources folder containing required input files. 

### Running
When running from the root directory of the repository immediately after installation, the following command can be used:
java -jar target/trace-reconstruction-0.1.jar -c src/main/resources/config.json -i src/main/resources/log.txt -o log/generated_trace.json

When not all arguments are provided an error messages will be generated together with help on how to specify the arguments. The h flag can also be used to get this help.

### Configuration
The c flag specifies the json configuration file, where some application variables can be defined. For now available:
- traceFinishedTimeoutSec: The number of seconds after a trace event was received for a trace to consider the trace to be finished
- jsonPrettyPrintOutput: When true the json is printed in nested style, otherwise in compact form

### Output
As mentioned, the trace output will be generated to standard output. The trace will also be generated to an output file that can be specified using the o flag. 
In the log folder under the root some more output can be found: a statistics file containing a limited number of statistics for now and the log file containing other information such as memory usage and errors.
