package frc.spectrumLib.lasercan;

import au.grapplerobotics.ConfigurationFailedException;
import au.grapplerobotics.LaserCan;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Subsystem;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.spectrumLib.CachedDouble;

public class LaserCanUtil implements Subsystem {
    private LaserCan lasercan; // TODO: Waiting on GrappleLib 2025
    private int id;

    private final CachedDouble cachedDistance;

    // default constructor
    public LaserCanUtil(int id) {
        this(id, true, 8, 8, 4, 4, LaserCan.TimingBudget.TIMING_BUDGET_50MS);
    }

    public LaserCanUtil(
            int id,
            boolean shortRange,
            int x,
            int y,
            int w,
            int h,
            LaserCan.TimingBudget timingBudget) {
        lasercan = new LaserCan(id);
        if (shortRange == true) {
            setShortRange();
        } else {
            setLongRange();
        }
        setRegionOfInterest(x, y, w, h); // max region
        setTimingBudget(timingBudget); // Can only set ms to 20, 33, 50, and 100
        cachedDistance = new CachedDouble(this::updateDistance);
        this.register();
    }

    @Override
    public void periodic() {}

    /* Internal Lasercan methods */

    public double getDistance() {
        return cachedDistance.getAsDouble();
    }

    public boolean validDistance() {
        return cachedDistance.getAsDouble() >= 0;
    }

    /* Helper methods for constructors */
    public LaserCanUtil setShortRange() {
        try {
            lasercan.setRangingMode(LaserCan.RangingMode.SHORT);
        } catch (ConfigurationFailedException e) {
            logError();
        }

        return this;
    }

    public LaserCanUtil setLongRange() {
        try {
            lasercan.setRangingMode(LaserCan.RangingMode.LONG);
        } catch (ConfigurationFailedException e) {
            logError();
        }

        return this;
    }

    public LaserCanUtil setRegionOfInterest(int x, int y, int w, int h) {
        try {
            lasercan.setRegionOfInterest(new LaserCan.RegionOfInterest(x, y, w, h));
        } catch (ConfigurationFailedException e) {
            logError();
        }

        return this;
    }

    public LaserCanUtil setTimingBudget(LaserCan.TimingBudget timingBudget) {
        try {
            lasercan.setTimingBudget(timingBudget);
        } catch (ConfigurationFailedException e) {
            logError();
        }

        return this;
    }

    /* Other methods */
    private static void logError() {
        DriverStation.reportWarning("LaserCan: failed to complete operation", false);
    }

    public Trigger greaterThan(int distance) {
        return new Trigger(() -> getDistance() > distance);
    }

    public Trigger lessThan(int distance) {
        return new Trigger(() -> getDistance() < distance);
    }

    public Trigger withinRange(int min, int max) {
        return new Trigger(() -> getDistance() > min && getDistance() < max);
    }

    private double updateDistance() {
        LaserCan.Measurement measurement = lasercan.getMeasurement();
        if (measurement != null) {
            if (measurement.status == 0) {
                return measurement.distance_mm;
            } else {
                if (measurement.status != 2) {
                    DriverStation.reportWarning(
                            "LaserCan #" + id + " status went bad: " + measurement.status, false);
                }
                return measurement.distance_mm;
            }
        } else {
            return -1000;
        }
    }
}
