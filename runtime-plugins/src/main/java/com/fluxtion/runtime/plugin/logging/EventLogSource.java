package com.fluxtion.runtime.plugin.logging;

/**
 * EventLogSource is registered with a CalculationLogManager. The
 * CalculationLogManager provides a configured EventLogger to this instance via
 * the setLogger method.
 *
 * @author Greg Higgins (greg.higgins@v12technology.com)
 */
public interface EventLogSource {

    void setLogger(EventLogger log);

//    default String id() {
//        return null;
//    }

}
