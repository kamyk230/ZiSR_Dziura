/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package program;

import fuzzlib.FuzzySet;
import fuzzlib.creators.OperationCreator;
import fuzzlib.norms.Norm;
import fuzzlib.norms.TNorm;

/**
 *
 * @author Student
 */
public class MainFuzzlibTest {

    public static void main(String[] args) {
        double valSL = 5.0; // Sepal Length
        double valSW = 3.6; // Sepal Width
        double valPL = 1.4; // Petal Length
        double valPW = 0.2; // Petal Width

        // Definicja T-normy
        Norm tNorma = OperationCreator.newTNorm(TNorm.TN_MINIMUM);

        // --- MODEL DLA IRIS SETOSA ---
        FuzzySet setSL = new FuzzySet(); setSL.newGaussian(5.0, 0.35);
        FuzzySet setSW = new FuzzySet(); setSW.newGaussian(3.4, 0.38);
        FuzzySet setPL = new FuzzySet(); setPL.newGaussian(1.5, 0.17);
        FuzzySet setPW = new FuzzySet(); setPW.newGaussian(0.2, 0.1);

        double scoreSetosa = tNorma.calc(setSL.getMembership(valSL),
                tNorma.calc(setSW.getMembership(valSW),
                        tNorma.calc(setPL.getMembership(valPL), setPW.getMembership(valPW))));

        // --- MODEL DLA IRIS VERSICOLOR ---
        FuzzySet verSL = new FuzzySet(); verSL.newGaussian(5.9, 0.51);
        FuzzySet verSW = new FuzzySet(); verSW.addPoint(2.0, 0.0); // Przykładowa zmiana stylu na addPoint jeśli chcesz
        verSW.newGaussian(2.8, 0.31);
        FuzzySet verPL = new FuzzySet(); verPL.newGaussian(4.35, 0.47);
        FuzzySet verPW = new FuzzySet(); verPW.newGaussian(1.3, 0.20);

        double scoreVersicolor = tNorma.calc(verSL.getMembership(valSL),
                tNorma.calc(verSW.getMembership(valSW),
                        tNorma.calc(verPL.getMembership(valPL), verPW.getMembership(valPW))));

        // --- MODEL DLA IRIS VIRGINICA ---
        FuzzySet virSL = new FuzzySet(); virSL.newGaussian(6.5, 0.63);
        FuzzySet virSW = new FuzzySet(); virSW.newGaussian(3.0, 0.32);
        FuzzySet virPL = new FuzzySet(); virPL.newGaussian(5.55, 0.55);
        FuzzySet virPW = new FuzzySet(); virPW.newGaussian(2.0, 0.27);

        double scoreVirginica = tNorma.calc(virSL.getMembership(valSL),
                tNorma.calc(virSW.getMembership(valSW),
                        tNorma.calc(virPL.getMembership(valPL), virPW.getMembership(valPW))));

        System.out.println("Analiza przynależności:");
        System.out.println("Setosa: " + scoreSetosa);
        System.out.println("Versicolor: " + scoreVersicolor);
        System.out.println("Virginica: " + scoreVirginica);
        System.out.println("-----------------------------------");

        if (scoreSetosa >= scoreVersicolor && scoreSetosa >= scoreVirginica) {
            System.out.println("Klasyfikacja: Iris-setosa");
        } else if (scoreVersicolor >= scoreVirginica) {
            System.out.println("Klasyfikacja: Iris-versicolor");
        } else {
            System.out.println("Klasyfikacja: Iris-virginica");
        }
    }
}
