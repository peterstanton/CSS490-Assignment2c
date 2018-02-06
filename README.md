CSS-490 Weather Application, console version
======
This program is a RESTful API based weather checking application.
 
#### Does not work on
* Invalid, gibberish locations.
* Locations not recognized as places by Google's locating API.
* Places that don't report weather data, like the middle of nowhere, or North Korea.
 
## How-to use this code
* Ensure you have a Java compiler, preferably Java 1.8.0_162, installed.
* Navigate to the source code directory which contains WeatherApp.java
* Compile the program with "javac WeatherApp.java"
* Run the program with "java WeatherApp"

* You can optionally compile your own executable jar by running this code in Eclipse, and going to File -> Export ->
Export -> Runnable JAR File -> Launch Configuration: WeatherApp -> Library Handling: Package required libraries into generated JAR -> 
select your desired output directory -> Finish.

* You can then run the .jar by running "java -jar Program2c-java8.jar" OR by double clicking the included executable.bat. Note that executable.bat
must be updated if you name the jar anything but Program2c-java8.jar.

## Known bugs
* Due to the fact that this is a console application, double-clicking the executable jar has no effect. The application must be run
from the command-line, or double clicking the executable .bat in the same directory as the .jar.

* JSON responses can very by location leading to all sorts of different responses that need handling, 
some areas do not report weather data. This program cannot be responsible for omitted data in such cases.

* I am limited in my API calls. Dark Sky is limited to 1000 API calls daily. Google is limited to 2500 calls
daily. If those limits are reached, no further usage is possible as I have not given Dark Sky my billing 
information and I am enforcing free usage quotas on my Google API usage. Please do not test excessively.

* Alerts often are ugly, but there isn't a lot I can do as these are raw dumps from a government alert service.

* Alerts also appear and disappear without my control.
 
## Dependencies
* Java 1.8.0_162

## Sources
* I got help from https://stackoverflow.com/a/7467629/4864069 on learning how to read JSON responses
from HTTP connections.

* CSS 490 grader, Gousiya Farheen, helped me understand what was going wrong with building executable .jar files.
