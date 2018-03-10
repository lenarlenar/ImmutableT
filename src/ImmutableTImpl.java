import sun.misc.Unsafe;

import java.io.*;
import java.lang.reflect.Field;

interface ImmutableT<T>{
    T getT(getMethodType type);

    enum getMethodType {
        Reflection,
        Serialization
    }
}

public final class ImmutableTImpl<T> implements ImmutableT<T> {
    private final T originalT;

    public ImmutableTImpl(T originalT) {
        this.originalT = originalT;
    }

    public T getT(getMethodType type){

        T result = null;
        switch (type){
            case Reflection:{
                result = cloneByReflection();
                break;
            }
            case Serialization:
            default:
                result = cloneBySerialization();

        }

        return result;
    }

    private static Unsafe getUnsafe() {
        try {
            Field singleoneInstanceField = Unsafe.class.getDeclaredField("theUnsafe");
            singleoneInstanceField.setAccessible(true);
            return (Unsafe) singleoneInstanceField.get(null);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private T cloneByReflection() {
        System.out.println(originalT.getClass());
        Object cloneObj = null;

        try {
            cloneObj = getUnsafe().allocateInstance(originalT.getClass());
        } catch (InstantiationException e) {
            e.printStackTrace();
            return null;
        }

        for (Class<? extends Object> objClass = originalT.getClass(); !objClass.equals(Object.class); objClass = objClass.getSuperclass()) {
            Field[] fields = objClass.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                try {
                    Object object = fields[i].get(originalT);
                    fields[i].set(cloneObj, object);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return (T)cloneObj;
    }

    private  T cloneBySerialization() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(originalT);

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (T) ois.readObject();
        } catch (IOException e) {
            return null;
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

}
