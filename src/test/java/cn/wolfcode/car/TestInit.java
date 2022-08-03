package cn.wolfcode.car;

public class TestInit {

    public static void main(String[] args) {
        // 获取系统类加载器
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        System.out.println(systemClassLoader);  // jdk.internal.loader.ClassLoaders$AppClassLoader@2437c6dc

        // 通过获取系统类加载器的上层来获取扩展类加载器
        ClassLoader extClassloader = systemClassLoader.getParent();
        System.out.println(extClassloader); // jdk.internal.loader.ClassLoaders$PlatformClassLoader@589838eb

        // 通过获取扩展类加载器的上层来获取引导类加载器
        // 发现无法获取到对象，因为引导类加载器是通过 C 和 C ++ 进行编写。
        ClassLoader classLoader = extClassloader.getParent();
        System.out.println(classLoader);    // null

        // 对于用户自定义的类：使用的都是系统类加载器进行加载
        ClassLoader loader = TestInit.class.getClassLoader();
        System.out.println(loader);     // jdk.internal.loader.ClassLoaders$AppClassLoader@2437c6dc

        // 对于 Java 中的核心类库，都是使用引导类加载器进行加载
        ClassLoader classLoader1 = String.class.getClassLoader();
        System.out.println(classLoader1);   // null

        /**
         * 总结：Java 类加载器可以分为两大类：启动类加载器和自定义类加载器
         *      1、BootstrapClassLoader 是启动类加载器，由 C 和 C ++ 语言实现
         *      2、其他直接或间接继承自 ClassLoader 类的类加载器都归类为自定义类加载器
         *          自定义类加载器细分来说又可以分为：
         *              系统类加载器
         *              扩展类加载器
         *              其他
         *
         */
    }
}
