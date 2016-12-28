/*
 * This is copyrighted code.  All rights reserved.
 * Please see the file license.txt for details.
 */
package net.stamfest.randomtests.nist;

/**
 * A Result to hold a chi-square based result.
 *
 * @author Peter Stamfest
 */
public class Chi2Result extends Result {
    private double chi2;
    private int degrees;

    /**
     *
     * @param p_value the value of p_value
     * @param chi2 the value of chi2
     * @param degrees the value of degrees
     * @param description the value of description
     */
    public Chi2Result(double p_value, double chi2, int degrees, String description) {
        super(p_value, description);
        this.chi2 = chi2;
        this.degrees = degrees;
    }

    
    /**
     * Get the number of degrees of freedom for the chi^2 statistic covering
     * this result.
     *
     * @return The number of degrees of freedom.
     */
    public int getDegrees() {
        return degrees;
    }

    /**
     * Get the value of chi^2.
     *
     * @return the value of chi^2.
     */
    public double getChi2() {
        return chi2;
    }

}
