/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.runtime.plugin.events;

/**
 * A generic configuration event, extends {@link GenericEvent}. To receive
 * type filtered events, the event handler annotates a method similar to that
 * below:
 *
 *
 * <pre>
 * 
 * 
 *{@literal @}EventHandler(propagate = false)
 * public void configUpdate(ConfigurationEvent&lt;MyConfig&gt; configUpdate) {
 * ...
 * }
 * </pre>
 *
 * Using propagate=false will swallow the event at this node.
 *<br>
 * The generated SEP provide all filtering logic within the generated dispatch.
 * 
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class ConfigurationEvent<T> extends GenericEvent<T> {

    public ConfigurationEvent(T configuration) {
        super(configuration);
    }
    
    public <V extends T> ConfigurationEvent(T configuration, V value) {
        super((Class<T>)configuration.getClass(), value);
    }

    public T configuration() {
        return value;
    }

}
