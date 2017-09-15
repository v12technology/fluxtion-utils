/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.runtime.plugin.runner;

import com.fluxtion.learning.utils.monitoring.cooling.generated.RackCoolingSystem;

/**
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class RunnerTest {
    
    
    public static void main(String[] args) {
        SEPRunner runner = new SEPRunner(new RackCoolingSystem(), "rack", 8080);
    }
}
