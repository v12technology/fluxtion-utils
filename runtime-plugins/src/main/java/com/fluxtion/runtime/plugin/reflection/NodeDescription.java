/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.runtime.plugin.reflection;

import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.reflect.FieldUtils;

/**
 *
 * @author greg
 */
public class NodeDescription {

    private String name;
    private String classType;
    private List<String> fields;

    public NodeDescription() {
    }

    public NodeDescription(String name, String classType, List<String> fields) {
        this.name = name;
        this.classType = classType;
        this.fields = fields;
    }

    public static NodeDescription buildDescription(String name, Object node) {

        List<String> fields = FieldUtils.getAllFieldsList(node.getClass()).stream()
                .map(f -> f.getName()).distinct()
                .collect(Collectors.toList());
        return new NodeDescription(name, node.getClass().getCanonicalName(), fields);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }

    @Override
    public String toString() {
        return "NodeDescription{" + "name=" + name + ", classType=" + classType + ", fields=" + fields + '}';
    }

}
