package frc.spectrumLib;

import edu.wpi.first.wpilibj.event.EventLoop;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import java.util.HashMap;
import java.util.function.BooleanSupplier;

public class SpectrumState extends Trigger {

    private static final HashMap<String, Boolean> stateConditions = new HashMap<>();
    private String name;
    private boolean value = false;

    /**
     * Create a new EventTrigger. This will run on the EventScheduler's event loop, which will be
     * polled any time a path following command is running.
     *
     * @param name The name of the event. This will be the name of the event marker in the GUI
     */
    public SpectrumState(String name) {
        super(pollCondition(name));
        this.name = name;
    }

    /**
     * Create a new EventTrigger that gets polled by the given event loop instead of the
     * EventScheduler
     *
     * @param eventLoop The event loop to poll this trigger
     * @param name The name of the event. This will be the name of the event marker in the GUI
     */
    public SpectrumState(EventLoop eventLoop, String name) {
        super(eventLoop, pollCondition(name));
    }

    public Command set(boolean value) {
        return Commands.runOnce(
                () -> {
                    this.value = value;
                    setCondition(name, value);
                });
    }

    public Command setTrue() {
        return set(true);
    }

    public Command setFalse() {
        return set(false);
    }

    public Command toggle() {
        return Commands.runOnce(
                () -> {
                    this.value = !this.value;
                    setCondition(name, value);
                });
    }

    /**
     * Create a boolean supplier that will poll(check) a condition.
     *
     * @param name The name of the event
     * @return A boolean supplier to poll the event's condition
     */
    private static BooleanSupplier pollCondition(String name) {
        // Ensure there is a condition in the map for this name
        if (!stateConditions.containsKey(name)) {
            stateConditions.put(name, false);
        }

        return () -> stateConditions.get(name);
    }

    /**
     * Set the value of an event condition
     *
     * @param name The name of the condition
     * @param value The value of the condition
     */
    protected static void setCondition(String name, boolean value) {
        stateConditions.put(name, value);
    }
}
