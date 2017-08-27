/* 
 * Copyright (C) 2017 V12 Technology Limited (greg.higgins@v12technology.com)
 *
 * This file is part of Fluxtion.
 *
 * Fluxtion is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.fluxtion.runtime.plugin.profiler;

import org.HdrHistogram.Histogram;
import org.HdrHistogram.HistogramLogWriter;
import org.junit.Test;
import org.junit.Ignore;

/**
 *
 * @author Greg Higgins (greg.higgins@V12technology.com)
 */
public class HdrHistogramTest {

    
    @Test
    @Ignore
    public void histoTest(){
        Histogram histogram = new Histogram(5);
        for (int i = 1; i <= 10 * 1000; i++) {
            final long value = i;//(long) (1000* Math.random());
//            System.out.println(value);
            histogram.recordValue(value);
        }
        HistogramLogWriter writer = new HistogramLogWriter(System.out);
        writer.outputIntervalHistogram(histogram);
        
        histogram.outputPercentileDistribution(System.out, 1.0);
        
    }
}
