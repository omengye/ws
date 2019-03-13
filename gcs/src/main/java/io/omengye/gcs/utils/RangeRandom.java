package io.omengye.gcs.utils;

import java.util.Random;

public class RangeRandom {

    private Random random = new Random();

    private RangeRandom() {}


    private static class RangeRandomFactory {
        private static RangeRandom instance = new RangeRandom();
    }

    public static RangeRandom getInstance() {
        return RangeRandomFactory.instance;
    }

    public int rand(int range) {
        return random.nextInt(range);
    }

}
