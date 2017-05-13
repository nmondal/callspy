package com.zeroturnaround.callspy;

@SuppressWarnings("unused")
public class Stack {

  static String indent = "";

  public static void push() {
    indent += " ";
  }

  public static void pop() {
    indent = indent.substring(1);
  }

  public static void log(String className, String methodName, Object[] params){
    StringBuilder builder = new StringBuilder(indent);
    builder.append(className).append(":").append(methodName).append("(");
    if(params.length > 0) {
      builder.append(String.valueOf(params[0]));
      for( int i = 1; i < params.length; i++) {
        builder.append(",").append(params[i]);
      }
    }
    builder.append(")");
    System.out.println(builder.toString());
  }
}
