/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.runtime.plugin.logging;

/**
 * No operation logger, has no side effects on any function call, ie no logging
 *
 * @author gregp
 */
public final class NullEventLogger extends EventLogger {

    public static final NullEventLogger INSTANCE = new NullEventLogger();

    private NullEventLogger() {
        super(null, null);
    }

    @Override
    public void log(String key, boolean value, EventLogConfig.LogLevel logLevel) {
    }

    @Override
    public void log(String key, CharSequence value, EventLogConfig.LogLevel logLevel) {
    }

    @Override
    public void log(String key, double value, EventLogConfig.LogLevel logLevel) {
    }

    @Override
    public void trace(String key, double value) {
    }

    @Override
    public void debug(String key, double value) {
    }

    @Override
    public void info(String key, double value) {
    }

    @Override
    public void warn(String key, double value) {
    }

    @Override
    public void error(String key, double value) {
    }

    @Override
    public void trace(String key, boolean value) {
    }

    @Override
    public void debug(String key, boolean value) {
    }

    @Override
    public void info(String key, boolean value) {
    }

    @Override
    public void warn(String key, boolean value) {
    }

    @Override
    public void error(String key, boolean value) {
    }

    @Override
    public void trace(String key, String value) {
    }

    @Override
    public void debug(String key, String value) {
    }

    @Override
    public void info(String key, String value) {
    }

    @Override
    public void warn(String key, String value) {
    }

    @Override
    public void error(String key, String value) {
    }

    @Override
    public void setLevel(EventLogConfig.LogLevel level) {
    }

}
