/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.runtime.plugin.container.server;

/**
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class DashboardSummary {
    
    private String sepName;
    
    private String className;

    public DashboardSummary(String sepName) {
        this.sepName = sepName;
    }

    public DashboardSummary() {
    }
    /**
     * Get the value of className
     *
     * @return the value of className
     */
    public String getClassName() {
        return className;
    }
    /**
     * Set the value of className
     *
     * @param className new value of className
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Get the value of sepName
     *
     * @return the value of sepName
     */
    public String getSepName() {
        return sepName;
    }

    /**
     * Set the value of sepName
     *
     * @param sepName new value of sepName
     */
    public void setSepName(String sepName) {
        this.sepName = sepName;
    }

}
