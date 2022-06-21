# MST / SMRL Library
This repository contains the source of the MST Library (previously called SMRL library). 
It includes all the algorithms necessary to execute metamorphic relations.

See LICENSE.txt for license information.

For more information https://sntsvv.github.io/SMRL/

##COMPILE

To use the MST / SMRL library (i.e., to execute metamorphic relations) in a MRL project, you need to create a jar file containing the compiled library and all its dependencies. To this end it is sufficient to rely on maven with the following goals: "clean compile package assembly:single".

The jar is created in the directory "target" as MST-1.0.0-jar-with-dependencies.jar
