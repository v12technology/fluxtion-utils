/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.runtime.plugin.container;

/**
 *
 * @author greg
 */
public class TraceEvent {

    private String nodeName;
    private String fieldName;
    private boolean recordTrace;

    public TraceEvent() {
    }

    public TraceEvent(String name, boolean records) {
        this.nodeName = name;
        this.recordTrace = records;
    }

    public TraceEvent(String nodeName, String fieldName, boolean recordTrace) {
        this.nodeName = nodeName;
        this.recordTrace = recordTrace;
        this.fieldName = fieldName;
    }

    /**
     * Get the value of fieldName
     *
     * @return the value of fieldName
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Set the value of fieldName
     *
     * @param fieldName new value of fieldName
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * Get the value of records
     *
     * @return the value of records
     */
    public boolean isRecords() {
        return recordTrace;
    }

    /**
     * Set the value of records
     *
     * @param records new value of records
     */
    public void setRecords(boolean records) {
        this.recordTrace = records;
    }

    /**
     * Get the value of name
     *
     * @return the value of name
     */
    public String getName() {
        return nodeName;
    }

    /**
     * Set the value of name
     *
     * @param name new value of name
     */
    public void setName(String name) {
        this.nodeName = name;
    }

}
