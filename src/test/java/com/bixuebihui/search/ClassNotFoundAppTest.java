package com.bixuebihui.search;

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
    void testing() throws IOException, ClassNotFoundException {

        String groupId = "org.slf4j,ch.qos.logback";
        String className = "StaticMarkerBinder";
        String methodName = "getSingleton";

        ClassNotFoundApp.main(new String[]{groupId, className, methodName});

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
