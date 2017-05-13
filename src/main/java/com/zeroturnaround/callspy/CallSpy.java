package com.zeroturnaround.callspy;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class CallSpy implements ClassFileTransformer {

  private final Set<Pattern> filters;

  CallSpy(Set<String> strings) {
     filters = new HashSet<>();
     for( String p : strings) {
       Pattern pattern = Pattern.compile(p,Pattern.DOTALL);
       filters.add(pattern);
     }
  }

  private boolean matches(String className){
      // never do java.* ever
      if ( className.startsWith("java/") ||
              className.startsWith("javax/") ||
              className.startsWith("sun/") ){
          return false;
      }

      // do all
      if (filters.isEmpty()) {
          return true;
      }
      // if not, then do some
      for (Pattern p : filters) {
          if (p.matcher(className).matches()) {
              return true;
          }
      }
      // nothing, so return
      return false;
  }

    private void hookTransform(CtMethod method, String className) throws Exception {
      boolean isVoid = ( method.getReturnType() == CtClass.voidType );
      String body = (isVoid) ? "" : "return " ;
      body += Interceptor.class.getName() + ".hook(\"" + className + "\", \"" + method.getName() + "\", $args );" ;
      body += (isVoid) ? "return;" : "" ;
      method.insertBefore(body);
      method.insertAfter("",true);
    }

  @Override
  public byte[] transform(//region other parameters
                          ClassLoader loader,
                          String className,
                          Class<?> classBeingRedefined,
                          ProtectionDomain protectionDomain,
                          //endregion
                          byte[] classfileBuffer) throws IllegalClassFormatException {

    ClassPool cp = ClassPool.getDefault();
    cp.importPackage("com.zeroturnaround.callspy");

    //region filter agent classes
    // we do not want to profile ourselves
    if (className.startsWith("com/zeroturnaround/callspy")) {
      return classfileBuffer;
    }
    //endregion

    //region filter out non-application classes
    // Application filter. Can be externalized into a property file.
    // For instance, profilers use blacklist/whitelist to configure this kind of filters
    if (!matches(className)) {
      return classfileBuffer;
    }
    //endregion
    try {
      CtClass ct = cp.makeClass(new ByteArrayInputStream(classfileBuffer));

      CtMethod[] declaredMethods = ct.getDeclaredMethods();
      for (CtMethod method : declaredMethods) {
        //region instrument method
        hookTransform(method,className);
        //endregion
      }

      return ct.toBytecode();
    } catch (Throwable e) {
      e.printStackTrace();
    }

    return classfileBuffer;
  }
}