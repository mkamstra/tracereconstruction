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

In the source code under the src/main/resources the log4j2.xml file is used to configure the log output (where to write what and how). This can be modified, but it requires a rebuild of the software for the changes to take effect.

### Output
As mentioned, the trace output will be generated to standard output. The trace will also be generated to an output file that can be specified using the o flag. 
In the log folder under the root some more output can be found: a statistics file containing a limited number of statistics for now and the log file containing other information such as memory usage and errors.

### Design
Some provisions have been made for a concurrent application, but so far only a few threads are used. The TraceBuilder containing the entry point of the application (the main method) creates a thread pool (currently with only one thread) using an ExecutorService for the LogParser where most of the application logic is happening.  It also creates a scheduled thread pool (currently also with one thread) for application statistics reporting. This thread will run every 10 seconds and write some statistics to an output file (notice that for now only a very limited number of statistics is reported just to show how it could be done). Another approach not used here for now (as it would require quite a bit of extra work), would be to use a publish / subscribe mechanism and a message broker (like RabbitMQ, ZeroMQ, or Kafka).

The LogParser reads from the micro services log input file using a BufferedReader and will continue processing one line at a time until the end is reached. Once the end of that file is reached it will pause 1 second and check for a new line in an infinite loop. When data is appended to the log file the LogParser will detect this and process new lines as they become available. By reading the file line by line the log file (which potentially can be very large) will never need to be loaded entirely into memory, thereby limiting the memory consumption of the application. Obviously this is a place where a concurrent implementation could significantly speed up the processing (not so much the fetching of lines by the BufferedReader, but the conversion to traces). This has not been done yet. 

The lines get converted into TraceElement objects which together form Trace objects. Each Trace object consists of a number of TraceElement objects (normally only one, since there usually only is one root, but there is a provisioning for potentially multiple roots in a trace). Each TraceElement object has a number of children TraceElement objects, together representing the entire Trace. A Trace object is considered to be completed once the traceFinishedTimeoutSec timeout has been reached, i.e. whenever a line is processed its end time is used to compare with the last event time of a Trace object and if this exceeds the traceFinishedTimeoutSec value, then the Trace object is considered to be finished. If a finished Trace object has a root (i.e. received a line with receivedSpanId null), then the state of the Trace object has already been change to at least 2 (3 represents the situation of multiple roots as mentioned before). Such a trace is considered complete. A complete Trace object is removed from the map of Traces and written to output (and statistics). Some Trace objects may not have received a root object, i.e. they are orphaned Trace objects. These will also be removed from the map of Traces, but will not be written to output (statistics on them are collected though). Note that the event time of incoming lines is used and not the system time as an old log might be processed.   

### Testing
One test class has been created to illustrate how to do this. Ideally there should be a lot more tests, but those have been omitted due to time limitations.
