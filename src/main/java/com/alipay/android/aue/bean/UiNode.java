package com.alipay.android.aue.bean;

import org.dom4j.Element;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by bingo on 2016/9/30.
 */
public class UiNode {
    private Element node;
    private List<UiNode> childrenNode;

    public UiNode(Element node) {
        this.node = node;

        childrenNode = new ArrayList<UiNode>();
        init();
    }

    private void init(){
        for (Iterator iterator = node.elementIterator(); iterator.hasNext(); ){
            Element element = (Element) iterator.next();
            childrenNode.add(new UiNode(element));
        }
    }

    public Element getNode() {
        return node;
    }

    public List<UiNode> getChildrenNode() {
        return childrenNode;
    }

    public boolean hasChild(){
        return childrenNode.size() == 0 ? false : true;
    }
}
