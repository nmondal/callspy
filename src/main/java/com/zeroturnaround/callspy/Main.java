package com.zeroturnaround.callspy;

public class Main {

  private static void usage(){
    System.out.println("Hello from CallSpy!");
    System.out.println("Usage: ");
    System.out.println("   java -javaagent:/path/to/callspy.jar your.main.Clazz");
    System.out.println("\nEnjoy! :-)");
  }

  public static void main(String[] args) {
    usage();
  }
}
