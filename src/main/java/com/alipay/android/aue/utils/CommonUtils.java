package com.alipay.android.aue.utils;

import com.alipay.android.aue.bean.config.Configuration;
import com.alipay.android.aue.bean.UiNode;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bingo on 2016/10/1.
 */
public class CommonUtils {
    /**
     * To verify if string is empty or not
     * @param content
     * @return
     */
    public final static boolean isEmpty(String content){
        if (content == null || content.isEmpty()){
            return true;
        }
        return false;
    }

    /**
     * Get leaf node from UiNode
     * @param uiNode
     * @return
     */
    public final static List<UiNode> getLeafNode(UiNode uiNode){
        List<UiNode> nodeList = new ArrayList<UiNode>();
        for (UiNode node : uiNode.getChildrenNode()){
            if (node.hasChild() == false){
                nodeList.add(node);
            }else{
                nodeList.addAll(getLeafNode(node));
            }
        }
        return nodeList;
    }

    /**
     * Save json content to file
     * @param file
     * @param json
     */
    public void saveRecordPathToFile(File file, String json){
        File dirFile = file.getParentFile();
        if (dirFile.exists() == false){
            dirFile.mkdirs();
        }
        try {
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file, false);
            fileWriter.write(json);
            fileWriter.flush();
            fileWriter.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static Iterable<Object> loadMap(InputStream fileStream){
        Yaml yaml = new Yaml();
        return yaml.loadAll(fileStream);
    }

    public static void main(String[] args){
//        Iterable<Object> objects = loadMap(CommonUtils.class.getResourceAsStream("/configuration.yml"));
//        Iterator iterator = objects.iterator();
//        while (iterator.hasNext()){
//            Object object = iterator.next();
//
//        }

        Yaml yaml = new Yaml();
        Configuration ignore = yaml.loadAs(CommonUtils.class.getResourceAsStream("/configuration.yml"), Configuration.class);
        System.out.println("abdkaj");
    }
}
