package com.bixuebihui.search;

import java.io.IOException;
import java.util.Arrays;


public class ClassNotFoundApp {

    public static void main(String[] args) throws IOException {
        //SpringApplication.run(Demo1Application.class, args);
        //find maven repository dir by env var
        String mavenRepo = System.getenv("MAVEN_REPO");
        if (mavenRepo == null) {
            mavenRepo = System.getProperty("user.home") + "/.m2/repository";
        }
        System.out.println("Maven repository: " + mavenRepo);

        //use first arg as an artifact group id and second arg as class name and third arg as method name
        if(args.length < 1){
            System.out.println("Usage: class-not-found <className> [methodName] [groupId1,groupId2...]");
            return;
        }

        String[] groupIds = args.length>3? args[3].split(","): new String[]{""};
        String className = args[1];
        String methodName;
        if(args.length > 2 && args[2].equals("-")){
            methodName = "";
        } else if(args.length > 2){
            methodName = args[2] ;
        }else{
            methodName = "";
        }
        System.out.println("groupId: " + Arrays.toString(groupIds));
        System.out.println("className: " + className);
        System.out.println("methodName: " + methodName);

        //search method name in jar file
        SearchMethodInJarFile o = new SearchMethodInJarFile();

        for(String groupId : groupIds){
            String dirWithGroupId = mavenRepo + "/" + groupId.replace(".", "/");
            SearchMethodInJarFile.listJarFileInDir(dirWithGroupId, jarFile -> {
                if(o.searchMethodName(jarFile, className, methodName)){
                    System.out.println("Found in " + jarFile.getName());
                }

            });
        }


    }

}
