package ch.codeconsult.httpstone.util;

/** Compute and store min/max/avg times
 *  @author bdelacretaz@codeconsult.ch
 *  $Id$
 */
 
public class TimeStats {
    private long min = Long.MAX_VALUE;
    private long max;
    private long avg;
    private long total;
    private long count;

    public void addDataPoint(long value) {
        total += value;
        count++;
        avg = total / count;
        min = Math.min(min,value);
        max = Math.max(max,value);
    }

    /** format min/max/avg display */
    public String toString() {
        return min + "/" + max + "/" + avg;
    }

    public long getMin() {
        return min;
    }

    public long getMax() {
        return max;
    }

    public long getAvg() {
        return avg;
    }

    public long getTotal() {
        return total;
    }

    public long getCount() {
        return count;
    }
}
