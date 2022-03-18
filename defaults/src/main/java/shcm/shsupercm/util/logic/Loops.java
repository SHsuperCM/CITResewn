package shcm.shsupercm.util.logic;

import java.util.*;

/**
 * This class(or class portion) is a part of SHCM's utilities. Feel free to use without credit.
 */
public class Loops {
    /**
     * Creates a loop of T with linked intensities allowing for fading between the elements.
     * @param items list of items and pause durations(in time units) ordered as they are in the loop
     * @param fade time in units to fade between each item
     * @param ticks positive raising counter
     * @param tpu the amount of ticks per time unit
     * @param <T> element type
     * @return map of elements and their respective intensities(between 0.0f and 1.0f)
     */
    public static <T> Map<T, Float> statelessFadingLoop(List<Map.Entry<T, Float>> items, float fade, int ticks, int tpu) {
        Map<T, Float> itemValues = new HashMap<>();

        if (items == null || items.size() == 0)
            return itemValues;

        if (items.size() == 1) {
            itemValues.put(items.get(0).getKey(), 1f);
            return itemValues;
        }

        float totalUnitsInLoop = 0f;
        for (Map.Entry<T, Float> item : items) {
            itemValues.put(item.getKey(), 0f);
            totalUnitsInLoop += item.getValue() + fade;
        }

        float unitInLoop = (ticks % (tpu * totalUnitsInLoop)) / tpu;

        for (int i = 0; i < items.size(); i++) {
            Map.Entry<T, Float> item = items.get(i);
            if (unitInLoop < item.getValue()) {
                itemValues.put(item.getKey(), 1f);
                break;
            } else
                unitInLoop -= item.getValue();

            if (unitInLoop < fade) {
                Map.Entry<T, Float> nextItem = items.get(i + 1 >= items.size() ? 0 : i + 1);

                unitInLoop /= fade;

                itemValues.put(item.getKey(), 1f - unitInLoop);
                itemValues.put(nextItem.getKey(), unitInLoop);

                break;
            } else
                unitInLoop -= fade;
        }

        return itemValues;
    }
}