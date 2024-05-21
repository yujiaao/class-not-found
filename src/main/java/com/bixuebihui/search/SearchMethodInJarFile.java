package com.bixuebihui.search;

import io.micrometer.common.util.StringUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class SearchMethodInJarFile {

    private static final String CLASS_SUFFIX = ".class";

//    public static void main(String[] args) throws IOException,
//            SecurityException, ClassNotFoundException {
//
//        /** target method name to be searched */
//        String targetMethodClass = "removeCauseMethodName";
//
//        /**
//         * Specify a target method name as 'removeCauseMethodName'. Find class
//         * name that includes the target method name in Jar File.
//         */
//        new SearchMethodInJarFile().searchMethodName(new JarFile(
//                "D:\\Develop\\workspace\\Test\\commons-lang-2.4.jar"),
//                targetMethodClass);
//
//    }

    /**
     * list all jar files in the directory
     */
    public static void listJarFileInDir(String dir, Consumer<JarFile> jarFileConsumer )
            throws IOException, SecurityException {
        //recursive list all jar files in the directory
        Files.walkFileTree(Paths.get(dir), new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                if (file.toString().endsWith(".jar")) {
                    JarFile jarFile = new JarFile(file.toFile());
                    jarFileConsumer.accept(jarFile);
                }
                return FileVisitResult.CONTINUE;
            }
        });


    }



    /**
     * Search target method name in one Jar file.
     *
     * @return
     */
    public boolean searchMethodName(JarFile jarFile, String className, String targetMethodName) {
        try {

            Enumeration<JarEntry> entryEnum = jarFile.entries();
            String classFilePath = (className!=null && !className.isEmpty())? className.replace(".", "/") + ".class": null;

            while (entryEnum.hasMoreElements()) {
                JarEntry el = entryEnum.nextElement();
                if(el.isDirectory()) continue;

                String fileName = el.getName();

                if(!fileName.endsWith(".class")) continue;

                if (classFilePath!=null) {
                    if(fileName.equals(classFilePath) || fileName.endsWith("/"+classFilePath)){
                        if(StringUtils.isBlank(targetMethodName)){
                            return true;
                        }
                        return visit(jarFile.getInputStream(el), targetMethodName);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    private boolean visit(InputStream inputStream, String targetMethodName) {
        try {
            ClassReader reader = new ClassReader(inputStream);
            ClassNode cn = new ClassNode();

            int parsingOptions = ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES;
            reader.accept(cn, parsingOptions);

            //（3）找到某个具体的方法
            List<MethodNode> methods = cn.methods;
            for (MethodNode mn : methods) {
                if (targetMethodName.equals(mn.name)) {
                    //（4）打印输出
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Check the name of JarEntry, if its name ends with '.class'. Then do the
     * following 3 steps: 1. Populate Class name. 2. Get the methods by
     * reflection. 3. Compare the target method name with the names. If the
     * methood name is equal to target method name. Then print the method name
     * and class name in console.
     */
    private boolean doSearchMethodName(String name, String targetMethodName)
            throws SecurityException, ClassNotFoundException {
        //String name = entry.getName();
        if (name.endsWith(CLASS_SUFFIX)) {
            /**
             * Populate the class name
             */
            name = name.replaceAll("/", ".")
                    .substring(0, name.lastIndexOf("."));

            /**
             * Retrieve the methods via reflection.
             */
            Method[] methods = Class.forName(name).getDeclaredMethods();
            for (Method m : methods) {
                /**
                 * Print the message in console if the method name is expected.
                 */
                if (targetMethodName.equals(m.getName())) {
                    System.out.printf(
                            "Method [%s] is included in Class [%s]%n",
                            targetMethodName, name);
                    return true;
                }
            }

        }
        return false;
    }
}
