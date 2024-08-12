The template engine project is a light-weight java framework for creating custom template engines in the style of java server pages. You will not utilize "request", "response" scriptlet variables as you would in your JSP, but in your custom template engine, you would use whatever scriptlet variables you deem necessary. The only pre-defined scriptlet variables are "out", "ctx", and "name". All other scriptlet variable names will be defined by your template engine that is built using this framework.  The only nuance item related to escaping is that when NOT in scriptlet mode, "<<" will be translated as "<".
  
I have included a classdiag.png diagram to help make sense of the class structure. But, otherwise, the framework is meant to be built upon, and it is only 8 classes where only 2 classes have any type of real code base. So, I assume you will just review the code yourself directly and get familiar since you will be building your template engine upon it anyways. NOTE: This framework is expected to be used by java developers. The framework does not create a good separation between the template engineers and the developers, the template engineers can start to put real business logic in the template (you would have to build something into this framework if you wanted to create that separation greater).  
  
The intent is that you create a custom implementation of this framework and incorporate into your own application. I would suggest you create classes/interfaces with "getter" only methods, create javadocs of those classes/interfaces, and then incorporate those classes as pre-defined scriptlet variables into your template engine. You would provide all this information along with an example to those downstream template creators who use your application.  
  
  
To help get your started in terms of this framework and its base reference implementation, try these commands and see output.  
  
build.bat             (compiles the code, see classes directory after running)  
  
run.bat compile       (compiles the sample template found in config/templates/test/sample.txt)  
                      (you should evaluate com\munskimii\tengine\gen for generated code and classes directory for compiled code)  
  
run.bat run test.dat  (you should evaluate config/documents created from sample.txt template & data/test.dat data records)  
  
And that is all, good luck!
