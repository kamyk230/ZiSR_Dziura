package com.fss;

public class GaussianFuzzySet {
    double center = 0.0;
    double width = 1.0;
    String name = "";

    public GaussianFuzzySet(){
    }

    public GaussianFuzzySet(double center, double width){
        this.center = center;
        this.width = width;
    }

    public void setCenter(double center) {
        this.center = center;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public double getMembership(double value){
        double w = 1.0/width;
        double tmp = value-center;
        return Math.exp( (-tmp*tmp)/2*w*w );
    }

    @Override
    public String toString() {
        return name + " {" +
                "center=" + center +
                ", width=" + width +
                '}';
    }
}
