package com.zeroturnaround.callspy;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 */
public final class Interceptor {

    public static final class LoggerHook {

        public static Object _intercept(String methodName, Object[] args){
            System.out.printf("%s->(%s)\n", methodName, Arrays.asList(args) );
            return NONE; // always return basic
        }
    }

    public static final Object NONE = new Object();

    private Interceptor(){}

    public static final String METHOD_NAME = "_intercept" ;

    public static final Map<String, Object> registeredInterceptors = new HashMap<>();

    static {
        // just for testing now
        registeredInterceptors.put("Test", LoggerHook.class.getName());
    }

    public static Object hook( String className, String methodName,  Object[] args) {
        if ( !registeredInterceptors.containsKey(className) ) {
            return NONE;
        }
        Object interceptor = registeredInterceptors.get(className);
        try {
            return safeCall(interceptor,className,methodName,args);
        } catch (Throwable t){
            throw new RuntimeException(t);
        }
    }

    private static Object invoke(Method m, String methodName, Object[] methodArgs) throws Exception {
       Object[] args = new Object[ ] { methodName, methodArgs };
       return m.invoke(null, args);
    }

    private static Object safeCall(Object interceptor, String className, String methodName, Object[] args) throws Exception {
        if ( interceptor instanceof Method ){
            // handle it
            return invoke((Method)interceptor, methodName, args);
        }
        Class c = Class.forName(String.valueOf(interceptor));
        Method[] methods = c.getDeclaredMethods();
        for ( int i = 0 ; i < methods.length; i++ ){
            if ( METHOD_NAME.equals(methods[i].getName()) ){
                registeredInterceptors.put(className, methods[i]);
                return invoke(methods[i], methodName, args);
            }
        }
        throw new RuntimeException( c.getName() +  " : does not define method : " + METHOD_NAME );
    }
}
