package com.bixuebihui.search;

import org.dom4j.DocumentException;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

class ClassNotFoundAppTest {

    private static void printList(List<?> list) {
        PrintWriter writer = new PrintWriter(System.out);
        printList(writer, list);
        writer.flush();
    }

    private static void printList(final PrintWriter printWriter, final List<?> list) {
        for (Object o : list) {
            if (o instanceof List) {
                printList(printWriter, (List<?>) o);
            } else {
                printWriter.print(o);
            }
        }
    }

    @Test
    void testing() throws IOException, DocumentException {

        String groupId = "org.slf4j,ch.qos.logback";
        String className = "StaticMarkerBinder";
        String methodName = "getSingleton";

        ClassNotFoundApp.main(new String[]{"app", className, methodName, groupId});

    }

    @Test
    void testingMavenHome() throws IOException, DocumentException {

        String groupId = "";
        String className = "org.apache.log4j.Level";
        String methodName = "";

        ClassNotFoundApp.main(new String[]{"app", className, methodName, groupId});

    }

    @Test
    void testing1() throws IOException, DocumentException {

       //  String groupId = "org.springframework.boot.context.properties";
        String groupId = "org.springframework.boot";
        String className = "ConfigurationBeanFactoryMetadata";
        String methodName = "-";

        ClassNotFoundApp.main(new String[]{"app", className, methodName, groupId});
    }

    @Test
    void searchByFileName() throws DocumentException, IOException {
        //String ex = "Caused by: java.io.FileNotFoundException: class path resource [springfox/documentation/spring/web/SpringfoxWebConfiguration.class] cannot be opened because it does not exist";
        //String ex ="[springfox/documentation/swagger2/configuration/Swagger2DocumentationConfiguration.class] ";
        //String ex = "[springfox/documentation/spi/service/ResponseBuilderPlugin.class]";
        //String ex = "[springfox/documentation/spi/service/ResourceGroupingStrategy.class]";
        String ex = "[springfox/documentation/spi/service/ResponseBuilderPlugin.class]";


        String[] parts = ex.split("\\[");
        String className = parts[1].split("\\.")[0];
        System.out.println(className);

        String groupId = "";

        String methodName = "-";

        ClassNotFoundApp.main(new String[]{"app", className, methodName, groupId});
    }

    @Test
    void searchByClassName() throws DocumentException, IOException {
        String className = "springfox.documentation.spring.web.paths.RelativePathProvider";
        String groupId = "";
        String methodName = "-";

        ClassNotFoundApp.main(new String[]{"app", className, methodName, groupId});
    }

    @Test
    void searchByShortClassName() throws DocumentException, IOException {
        String className = "DataTypeHint";
        String groupId = "org.apache.flink";
        String methodName = "-";

        ClassNotFoundApp.main(new String[]{"app", className, methodName, groupId});
    }



    @Test
    void testAsm() throws IOException {
        ClassReader reader = new ClassReader("java.lang.StringBuilder");

        ClassNode cn = new ClassNode();

        int parsingOptions = ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES;
        reader.accept(cn, parsingOptions);

        List<MethodNode> methods = cn.methods;
        MethodNode mn = methods.get(1);

        Textifier printer = new Textifier();
        TraceMethodVisitor tmv = new TraceMethodVisitor(printer);

        InsnList instructions = mn.instructions;
        for (AbstractInsnNode node : instructions) {
            node.accept(tmv);
        }
        List<Object> list = printer.text;
        printList(list);

    }
}
