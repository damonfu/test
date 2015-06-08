
/*
 * FPS Counter on-screen Log
 * Log will be drawn on screen whenever onDraw is triggered.
 * 
 * author : zhaorui@baidu.com
 */

package yi.support.v1.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;


public class Reflection {

    public static Object getFieldValue(Object object, String name) {
        try {
            final Field field = getField(object.getClass(), name);
            if (field != null) {
                field.setAccessible(true);
                return field.get(object);
            }
        } catch (Exception e) {
            Logger.w("Reflection", "getFieldValue " + name + " " + e.toString());
        }
        return null;
    }

    public static <T> T getFieldValue(Object object, String name, Class<T> clazz) {
        Object value = getFieldValue(object, name);
        if (value != null && clazz.isAssignableFrom(value.getClass())) {
            @SuppressWarnings("unchecked")
            T t = (T) value;
            return t;
        } else {
            return null;
        }
    }

    public static boolean setFieldValue(Object object, String name, Object value) {
        try {
            final Field field = getField(object.getClass(), name);
            if (field != null) {
                field.setAccessible(true);
                field.set(object, value);
                return true;
            }
        } catch (Exception e) {
            Logger.w("Reflection", "setFieldValue " + name + " " + e.toString());
        }
        return false;
    }

    /**
     * Create instance by triggering appropriate constructor of the specified class.
     */
    public static Object newInstance(String className, Object... args) {
        try {
            Class<?> clazz = Class.forName(className);
            Constructor<?>[] constructors = clazz.getDeclaredConstructors();
            for (Constructor<?> constructor : constructors) {
                Class<?>[] types = constructor.getParameterTypes();
                if (types.length == args.length) {
                    int matched = 0;
                    for (; matched<types.length; matched++) {
                        if (!types[matched].isAssignableFrom(args[matched].getClass())) {
                            break;
                        }
                    }
                    if (matched == args.length) {
                        constructor.setAccessible(true);
                        return constructor.newInstance(args);
                    }
                }
            }
        } catch (Exception e) {
            Logger.w("Reflection", "newInstance " + className + " " + e.toString());
        }
        return null;
    }

    /**
     * Invoke appropriate function by checking function name and arguments.
     */
    public static Object invokeMethod(Object receiver, String name, Object... args) {
        try {
            Class<?> clazz = receiver.getClass();
            /*Class<?>[] parameterTypes = new Class<?>[args.length];
            for (int i=0; i<args.length; i++) {
                parameterTypes[i] = args[i].getClass();
            }*/
            
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().equals(name)) {
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    if (parameterTypes.length == args.length) {
                        int matched = 0;
                        for (; matched<parameterTypes.length; matched++) {
                            if (!isAcceptableParameter(parameterTypes[matched], args[matched])) {
                                break;
                            }
                        }
                        if (matched == parameterTypes.length) {
                            method.setAccessible(true);
                            return method.invoke(receiver, args);
                        }
                    }
                }
            }

            /*Method method = clazz.getDeclaredMethod(name, parameterTypes);
            if (method != null) {
                method.setAccessible(true);
                return method.invoke(receiver, args);
            }*/
        } catch (Exception e) {
            Logger.w("Reflection", "invokeMethod " + name + " " + e.toString());
        }
        return null;
    }
    
    private static boolean isAcceptableParameter(Class<?> clazz, Object obj) {
        if (clazz.isPrimitive()) {
            Class<?> c = getWrapperClass(obj.getClass());
            return (obj!=null && clazz==c);
        } else {
            return (obj==null || clazz.isAssignableFrom(obj.getClass()));
        }
    }
    
    private static Class<?> getWrapperClass(Class<?> clazz) {
        if (Character.class == clazz) return Character.TYPE;
        if (Byte.class == clazz) return Byte.TYPE;
        if (Short.class == clazz) return Short.TYPE;
        if (Integer.class == clazz) return Integer.TYPE;
        if (Long.class == clazz) return Long.TYPE;
        if (Float.class == clazz) return Float.TYPE;
        if (Double.class == clazz) return Double.TYPE;
        if (Boolean.class == clazz) return Boolean.TYPE;
        return clazz;
    }

    /**
     * Get field from class or super class.
     */
    public static Field getField(Class<?> clazz, String name) {
        if (clazz != null) {
            Field field = null;
            try {
                field = clazz.getDeclaredField(name);
            } catch (Exception e) {}

            if (field == null) {
                clazz = clazz.getSuperclass();
                if (Object.class != clazz) {
                    return getField(clazz, name);
                }
            }
            return field;
        } else {
            return null;
        }
    }

}

