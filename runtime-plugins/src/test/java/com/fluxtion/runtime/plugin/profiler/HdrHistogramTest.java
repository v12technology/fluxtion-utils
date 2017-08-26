/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fluxtion.runtime.plugin.profiler;

import java.util.Random;
import org.HdrHistogram.Histogram;
import org.HdrHistogram.HistogramLogWriter;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class HdrHistogramTest {

    
    @Test
    public void histoTest(){
        Histogram histogram = new Histogram(5);
        for (int i = 0; i < 10 * 1000; i++) {
            final long value = i;//(long) (1000* Math.random());
//            System.out.println(value);
            histogram.recordValue(value);
        }
        HistogramLogWriter writer = new HistogramLogWriter(System.out);
        writer.outputIntervalHistogram(histogram);
        
        histogram.outputPercentileDistribution(System.out, 1.0);
        
    }
}
