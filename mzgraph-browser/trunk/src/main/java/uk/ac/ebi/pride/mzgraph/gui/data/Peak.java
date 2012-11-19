package uk.ac.ebi.pride.mzgraph.gui.data;

/**
 * Peak including m/z and intensity double value. Currently, we implement Comparable interface,
 * user can put Peak object into {@link java.util.SortedSet}, which based on m/z ascent order.
 *
 * Creator: Qingwei-XU
 * Date: 02/11/12
 */
public class Peak implements Comparable<Peak> {
    private double intensity;
    private double mz;

    public Peak(double mz, double intensity) {
        if (mz < 0d) {
            throw new IllegalArgumentException("mz value should not less than 0");
        }

        if (intensity < 0d) {
            throw new IllegalArgumentException("intensity value should not less than 0");
        }

        this.intensity = intensity;
        this.mz = mz;
    }

    public double getIntensity() {
        return intensity;
    }

    public double getMz() {
        return mz;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Peak peak = (Peak) o;

        if (Double.compare(peak.intensity, intensity) != 0) return false;
        if (Double.compare(peak.mz, mz) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = intensity != +0.0d ? Double.doubleToLongBits(intensity) : 0L;
        result = (int) (temp ^ (temp >>> 32));
        temp = mz != +0.0d ? Double.doubleToLongBits(mz) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    /**
     * Based on m/z ascent order.
     * @see PeakSet
     * @param peak
     * @return
     */
    @Override
    public int compareTo(Peak peak) {
        return Double.compare(this.getMz(), peak.getMz());
    }

    @Override
    public String toString() {
        return "Peak{" +
                "intensity=" + intensity +
                ", mz=" + mz +
                '}';
    }
}