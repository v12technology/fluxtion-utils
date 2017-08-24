/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.runtime.plugin.engine;

import com.fluxtion.runtime.event.Event;

/**
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public interface EngineControl {
    
    void processEvent(Event event);
    
//    void addTraceListener
    
}
