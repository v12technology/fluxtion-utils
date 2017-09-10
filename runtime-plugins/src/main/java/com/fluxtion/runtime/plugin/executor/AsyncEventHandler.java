/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.runtime.plugin.executor;

import com.fluxtion.runtime.lifecycle.EventHandler;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public interface AsyncEventHandler extends EventHandler {

    EventHandler delegate();

    <T> Future<T> submit(Callable<T> task);
    
}
