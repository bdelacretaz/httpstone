-----------------
HttpStone project
-----------------

WHAT?
-----
Software written by 
  Bertrand Delacretaz, bdelacretaz@codeconsult.ch, www.codeconsult.ch.
  with the help of Thomas Rimmele (swissinfo.ch)  for design and testing.
  
See http://code.google.com/p/httpstone/ for more info and source code.

Httpstone is a simple tool to create stress test sequences for HTTP servers.

Licensed under the Apache Software License V2.0, http://apache.org/licenses/LICENSE-2.0.

HOW?
----
Small Java classes (Workers) can be written to implement various testing cycles. 
The tool runs many such workers simultaneously, showing their status (working, 
waiting, error) in real-time on a colored grid.

To compile and test, run "ant" in this directory for explanations.

Tested on macosx with JDK 1.5 and ant 1.6.5, should run on any 
similar platform.

MORE
----
Ant is available at http://ant.apache.org/
