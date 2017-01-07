package com.example.hyh.snowfall;

import java.util.Random;

/**
 * Created by HYH on 2017/1/6.
 */

public class Randomizer {

    private static Random sRandom = new Random();

    public static double randomDouble(int max){
        return sRandom.nextDouble() * (max + 1);
    }

    public static double randomDouble(int min, int max){
        return sRandom.nextDouble() * (max - min) + min;
    }

    public static int randomInt(int min, int max, boolean gaussian){
        return randomInt(max - min, gaussian) + min;
    }

    public static int  randomInt(int max, boolean gaussian) {
        if (gaussian) {
            return (int) (Math.abs(randomGaussian()) * (max + 1));
        } else {
            return sRandom.nextInt(max + 1);
        }
    }

    public static double randomGaussian() {
        double gaussian = sRandom.nextGaussian() / 3; // more 99% of instances in range (-1, 1)
        if(gaussian > -1 && gaussian < 1) {
            return gaussian;
        } else {
            return randomGaussian();
        }
    }

    public static int  randomSignum() {
        if (sRandom.nextBoolean()) {
            return 1;
        }
        return -1;
    }

}
