package com.zeroturnaround.callspy;

import java.lang.instrument.Instrumentation;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * http://stackoverflow.com/questions/23287228/how-do-i-pass-arguments-to-a-java-instrumentation-agent
 * java -javaagent:/path/to/agent.jar=argumentstring -cp jar-under-test.jar Foo.Main
 */
public class Agent {

  public static void premain(String args, Instrumentation instrumentation){
    Set<String> set = Collections.emptySet();
    if ( args != null ) {
        set = new HashSet<>(Arrays.asList( args.split("#")));
    }
    CallSpy transformer = new CallSpy(set);
    instrumentation.addTransformer(transformer);
  }
}
