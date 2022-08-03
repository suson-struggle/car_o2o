package cn.wolfcode.car;

import org.junit.Test;

public class ClassLoaderTest {
    @Test
    public void getClassLoader() throws ClassNotFoundException {
        // 1、通过类的字节码文件获取加载该类的类记载器
        ClassLoader classLoader = Class.forName("java.lang.String").getClassLoader();
        System.out.println(classLoader);    // null

        // 2、通过当前线程获取类的加载器（得到的是加载当前类的类加载器：应用类加载器，也叫系统类记载器）
        ClassLoader classLoader1 = Thread.currentThread().getContextClassLoader();
        System.out.println(classLoader1);   // jdk.internal.loader.ClassLoaders$AppClassLoader@2437c6dc

        // 3、通过 ClassLoader 的方法获取系统类加载器
        ClassLoader classLoader2 = ClassLoader.getSystemClassLoader();
        System.out.println(classLoader2);
        // 通过系统类记载器获取扩展类加载器
        ClassLoader extClassLoader = classLoader2.getParent();
        System.out.println(extClassLoader);
        // 通过扩展类加载器获取启动类加载器
        System.out.println(extClassLoader.getParent());
    }
}
