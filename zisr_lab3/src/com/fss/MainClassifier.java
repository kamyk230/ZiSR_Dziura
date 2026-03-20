package com.fss;

import neuralnetwork.data.DataPackage;
import neuralnetwork.data.DataVector;

public class MainClassifier {

	public static void main(String[] args) {
		
		int lSize = 5; //number of learning samples for each class

		// 1. Load data
		DataPackage all = new DataPackage();
		all.setFieldSeparator(","); // default ,
		all.setDecimalSeparator('.'); // default .
		all.loadTextFile("wines.txt");

		// divide classes
		DataPackage class1 = all.removeVectorsByColumnValue(0,1);
		DataPackage class2 = all.removeVectorsByColumnValue(0,2);
		DataPackage class3 = all.removeVectorsByColumnValue(0,3);
		// create test and learning packages


		DataPackage testPkg = new DataPackage();
		// learning package
		DataPackage learnPkg = class1.removeRandomVectors(lSize);
		learnPkg.add(class2.removeRandomVectors(lSize));
		learnPkg.add(class3.removeRandomVectors(lSize));
		// test package
		testPkg.add(class1);
		testPkg.add(class2);
		testPkg.add(class3);


		//System.out.println(learnPkg);
		//System.out.println(testPkg);

		// 2. Create fuzzy rules

		// in simple cases we do not have to build anything
		// - each value of the data row represents the "middle" of gaussian function
		// - value range of each data column (feature range) represents the "width" of
		// gaussian function

		// 3. Classify (find rule - class - with maximum compliance)

		// get one vector to be classified
		DataPackage oneRandom = testPkg.removeRandomVectors(1);
		DataVector testVector = oneRandom.get(0); //get first row

		// prepare
		double maxResult = 0.0;
		double resultClass = 0.0;
		DataVector resultVector = null;


		// for each learning data row
		for (DataVector vector : learnPkg.getList()) {

			double temp = 1.0;
//for each each column (omit first column - no 0)
			for (int i = 1; i < 14; i++) {
				temp *= getGaussian(testVector.get(i),
						learnPkg.getColumnRange(i),
						vector.get(i));
			}
			if (temp > maxResult) { //store max result and class
				maxResult = temp;
				resultClass = vector.get(0); //first col stores the class no
				resultVector = vector;


			}}

		//show results
		System.out.println("Result class: " +
				resultClass);
		System.out.println(resultVector);
		System.out.println("Real class: " +
				testVector.get(0));
		System.out.println(testVector);


	}

	public static double getGaussian(double value, double width, double center) {
		double w = 1.0 / width;
		double tmp = value - center;
		return Math.exp((-tmp * tmp) / 2 * w * w);
	}

}
