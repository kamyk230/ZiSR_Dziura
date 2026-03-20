package com.fss;

//1. download install JDK
//2. download and extract Eclipse (Darkest Dark theme) || download and install IntelliJ Idea (Free)

public class MainSimple {

    public static void main(String[] args) {

        GaussianFuzzySet fs_warm = new GaussianFuzzySet(0,8.0);

        fs_warm.setCenter(30); //fuzification
        fs_warm.setName("warm water");

        GaussianFuzzySet fs_hot = new GaussianFuzzySet(40,8.0);
        fs_hot.setName("hot water");

        GaussianFuzzySet fs_cold = new GaussianFuzzySet(15,8.0);
        fs_cold.setName("cold water");

        System.out.println(fs_cold);
        System.out.println(fs_warm);
        System.out.println(fs_hot);

        double temperature = 5.0;

        System.out.printf("Cold: %.3f \n",fs_cold.getMembership(temperature));
        System.out.printf("Warm: %.3f \n",fs_warm.getMembership(temperature));
        System.out.printf("Hot: %.3f \n",fs_hot.getMembership(temperature));

    }

}
