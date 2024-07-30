package com.bixuebihui.search;

import org.dom4j.DocumentException;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;


public class ClassNotFoundApp {

    public static void main(String[] args) throws IOException, DocumentException {
        //SpringApplication.run(Demo1Application.class, args);
        String mavenRepo = getMavenRepoDir();
        if (mavenRepo == null) return;

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
            SearchMethodInJarFile.listJarFileInDirAndSubDir(dirWithGroupId, jarFile -> {
                if(o.searchMethodName(jarFile, className, methodName)){
                    System.out.println("Found in " + jarFile.getName());
                }

            }, 10);
        }


    }

    private static String getMavenRepoDir() {
        //find maven repository dir by env var
        String mavenRepo = System.getenv("MAVEN_REPO");
        String fileSeparator = FileSystems.getDefault().getSeparator();

        if(mavenRepo == null|| !Files.isDirectory(Path.of(mavenRepo))){
            // from .m2/settings.xml find localRepository
            String settingsXml = System.getProperty("user.home") + fileSeparator+ ".m2"+fileSeparator+"settings.xml";
            try {
                mavenRepo = SearchMethodInJarFile.getMavenRepoFromSettingsXml(settingsXml);

                //if is windows, replace \ with /
                if(System.getProperty("os.name").toLowerCase().contains("win")){
                    mavenRepo = mavenRepo.replace("\\", "/");
                }
            }catch (Exception e){
                System.out.println("Failed to read maven settings.xml: " + e.getMessage());
            }

            //try default location
            if (mavenRepo == null || !Files.isDirectory(Path.of(mavenRepo))) {
                mavenRepo = System.getProperty("user.home") + fileSeparator+ ".m2"+fileSeparator+"repository";
            }

            if(!Files.isDirectory(Path.of(mavenRepo))) {
                System.out.println("Please set MAVEN_REPO env var or use -DMAVEN_REPO=xxx to set maven repository dir");
                return null;
            }
        }

        System.out.println("Maven repository: " + mavenRepo);
        return mavenRepo;
    }

}
