package program;

import java.util.Scanner;
import fuzzlib.FuzzySet;
import fuzzlib.norms.TNMin;
import fuzzlib.norms.SNMax;

public class KlimatyzatorFuzzlib {

    public static void main(String[] args) {

        // temperatura
        FuzzySet t_vCold = new FuzzySet();
        FuzzySet t_Cold  = new FuzzySet();
        FuzzySet t_Comf  = new FuzzySet();
        FuzzySet t_Warm  = new FuzzySet();
        FuzzySet t_Hot   = new FuzzySet();
        FuzzySet t_vHot  = new FuzzySet();

        t_vCold.addPoint(0, 1); t_vCold.addPoint(10, 1); t_vCold.addPoint(16, 0);
        t_Cold.addPoint(12, 0);  t_Cold.addPoint(17, 1);  t_Cold.addPoint(21, 0);
        t_Comf.addPoint(18, 0);  t_Comf.addPoint(22, 1);  t_Comf.addPoint(26, 0);
        t_Warm.addPoint(23, 0);  t_Warm.addPoint(27, 1);  t_Warm.addPoint(32, 0);
        t_Hot.addPoint(29, 0);   t_Hot.addPoint(33, 1);   t_Hot.addPoint(38, 0);
        t_vHot.addPoint(35, 0);  t_vHot.addPoint(40, 1);  t_vHot.addPoint(50, 1);

        // wilgotnosc
        FuzzySet h_vDry  = new FuzzySet();
        FuzzySet h_Dry   = new FuzzySet();
        FuzzySet h_Norm  = new FuzzySet();
        FuzzySet h_Wet   = new FuzzySet();
        FuzzySet h_vWet  = new FuzzySet();

        h_vDry.addPoint(0, 1);   h_vDry.addPoint(20, 1);  h_vDry.addPoint(35, 0);
        h_Dry.addPoint(20, 0);   h_Dry.addPoint(35, 1);   h_Dry.addPoint(50, 0);
        h_Norm.addPoint(40, 0);  h_Norm.addPoint(55, 1);  h_Norm.addPoint(65, 0);
        h_Wet.addPoint(60, 0);   h_Wet.addPoint(72, 1);   h_Wet.addPoint(85, 0);
        h_vWet.addPoint(78, 0);  h_vWet.addPoint(90, 1);  h_vWet.addPoint(100, 1);

        // chlodzenie
        FuzzySet outC0 = new FuzzySet();
        FuzzySet outC1 = new FuzzySet();
        FuzzySet outC2 = new FuzzySet();
        FuzzySet outC3 = new FuzzySet();
        FuzzySet outC4 = new FuzzySet();
        FuzzySet outC5 = new FuzzySet();

        outC0.addPoint(0, 1);  outC0.addPoint(5, 1);  outC0.addPoint(15, 0);
        outC1.addPoint(10, 0); outC1.addPoint(20, 1); outC1.addPoint(32, 0);
        outC2.addPoint(28, 0); outC2.addPoint(40, 1); outC2.addPoint(52, 0);
        outC3.addPoint(48, 0); outC3.addPoint(60, 1); outC3.addPoint(72, 0);
        outC4.addPoint(68, 0); outC4.addPoint(80, 1); outC4.addPoint(90, 0);
        outC5.addPoint(85, 0); outC5.addPoint(93, 1); outC5.addPoint(100, 1);

        // wentylator
        FuzzySet outF0 = new FuzzySet();
        FuzzySet outF1 = new FuzzySet();
        FuzzySet outF2 = new FuzzySet();
        FuzzySet outF3 = new FuzzySet();
        FuzzySet outF4 = new FuzzySet();
        FuzzySet outF5 = new FuzzySet();

        outF0.addPoint(0, 1);  outF0.addPoint(5, 1);  outF0.addPoint(15, 0);
        outF1.addPoint(10, 0); outF1.addPoint(20, 1); outF1.addPoint(32, 0);
        outF2.addPoint(28, 0); outF2.addPoint(40, 1); outF2.addPoint(52, 0);
        outF3.addPoint(48, 0); outF3.addPoint(60, 1); outF3.addPoint(72, 0);
        outF4.addPoint(68, 0); outF4.addPoint(80, 1); outF4.addPoint(90, 0);
        outF5.addPoint(85, 0); outF5.addPoint(93, 1); outF5.addPoint(100, 1);

        Scanner reader = new Scanner(System.in);
        System.out.print("Input Temperature [C]: ");
        double valT = reader.nextDouble();
        System.out.print("Input Humidity [%]:    ");
        double valH = reader.nextDouble();
        reader.close();

        // fuzzyfy
        double degT_vCold = t_vCold.getMembership(valT);
        double degT_Cold  = t_Cold.getMembership(valT);
        double degT_Comf  = t_Comf.getMembership(valT);
        double degT_Warm  = t_Warm.getMembership(valT);
        double degT_Hot   = t_Hot.getMembership(valT);
        double degT_vHot  = t_vHot.getMembership(valT);

        double degH_vDry  = h_vDry.getMembership(valH);
        double degH_Dry   = h_Dry.getMembership(valH);
        double degH_Norm  = h_Norm.getMembership(valH);
        double degH_Wet   = h_Wet.getMembership(valH);
        double degH_vWet  = h_vWet.getMembership(valH);

        TNMin t_norm = new TNMin();
        SNMax s_norm = new SNMax();

        // inference
        FuzzySet rc1 = clipSet(outC0, degT_vCold, t_norm);
        FuzzySet rc2 = clipSet(outC0, degT_Cold,  t_norm);
        FuzzySet rc3 = clipSet(outC1, degT_Comf,  t_norm);
        FuzzySet rc4 = clipSet(outC2, degT_Warm,  t_norm);
        FuzzySet rc5 = clipSet(outC3, degT_Hot,   t_norm);
        FuzzySet rc6 = clipSet(outC5, degT_vHot,  t_norm);
        FuzzySet rc7 = clipSet(outC0, degH_vDry,  t_norm);
        FuzzySet rc8 = clipSet(outC0, degH_Dry,   t_norm);
        FuzzySet rc9 = clipSet(outC1, degH_Norm,  t_norm);
        FuzzySet rc10= clipSet(outC2, degH_Wet,   t_norm);
        FuzzySet rc11= clipSet(outC4, degH_vWet,  t_norm);

        FuzzySet ac1 = new FuzzySet(); FuzzySet.processSetsWithNorm(ac1, rc1, rc2, s_norm);
        FuzzySet ac2 = new FuzzySet(); FuzzySet.processSetsWithNorm(ac2, ac1, rc3, s_norm);
        FuzzySet ac3 = new FuzzySet(); FuzzySet.processSetsWithNorm(ac3, ac2, rc4, s_norm);
        FuzzySet ac4 = new FuzzySet(); FuzzySet.processSetsWithNorm(ac4, ac3, rc5, s_norm);
        FuzzySet ac5 = new FuzzySet(); FuzzySet.processSetsWithNorm(ac5, ac4, rc6, s_norm);
        FuzzySet ac6 = new FuzzySet(); FuzzySet.processSetsWithNorm(ac6, ac5, rc7, s_norm);
        FuzzySet ac7 = new FuzzySet(); FuzzySet.processSetsWithNorm(ac7, ac6, rc8, s_norm);
        FuzzySet ac8 = new FuzzySet(); FuzzySet.processSetsWithNorm(ac8, ac7, rc9, s_norm);
        FuzzySet ac9 = new FuzzySet(); FuzzySet.processSetsWithNorm(ac9, ac8, rc10, s_norm);
        FuzzySet ac10= new FuzzySet(); FuzzySet.processSetsWithNorm(ac10, ac9, rc11, s_norm);

        ac10.PackFlatSections();
        double finalCooling = ac10.DeFuzzyfy();

        FuzzySet rf1 = clipSet(outF0, degT_vCold, t_norm);
        FuzzySet rf2 = clipSet(outF1, degT_Cold,  t_norm);
        FuzzySet rf3 = clipSet(outF1, degT_Comf,  t_norm);
        FuzzySet rf4 = clipSet(outF2, degT_Warm,  t_norm);
        FuzzySet rf5 = clipSet(outF3, degT_Hot,   t_norm);
        FuzzySet rf6 = clipSet(outF5, degT_vHot,  t_norm);
        FuzzySet rf7 = clipSet(outF1, degH_vDry,  t_norm);
        FuzzySet rf8 = clipSet(outF1, degH_Dry,   t_norm);
        FuzzySet rf9 = clipSet(outF2, degH_Norm,  t_norm);
        FuzzySet rf10= clipSet(outF3, degH_Wet,   t_norm);
        FuzzySet rf11= clipSet(outF5, degH_vWet,  t_norm);

        FuzzySet af1 = new FuzzySet(); FuzzySet.processSetsWithNorm(af1, rf1, rf2, s_norm);
        FuzzySet af2 = new FuzzySet(); FuzzySet.processSetsWithNorm(af2, af1, rf3, s_norm);
        FuzzySet af3 = new FuzzySet(); FuzzySet.processSetsWithNorm(af3, af2, rf4, s_norm);
        FuzzySet af4 = new FuzzySet(); FuzzySet.processSetsWithNorm(af4, af3, rf5, s_norm);
        FuzzySet af5 = new FuzzySet(); FuzzySet.processSetsWithNorm(af5, af4, rf6, s_norm);
        FuzzySet af6 = new FuzzySet(); FuzzySet.processSetsWithNorm(af6, af5, rf7, s_norm);
        FuzzySet af7 = new FuzzySet(); FuzzySet.processSetsWithNorm(af7, af6, rf8, s_norm);
        FuzzySet af8 = new FuzzySet(); FuzzySet.processSetsWithNorm(af8, af7, rf9, s_norm);
        FuzzySet af9 = new FuzzySet(); FuzzySet.processSetsWithNorm(af9, af8, rf10, s_norm);
        FuzzySet af10= new FuzzySet(); FuzzySet.processSetsWithNorm(af10, af9, rf11, s_norm);

        af10.PackFlatSections();
        double finalAirflow = af10.DeFuzzyfy();

        System.out.println("----------------------------");
        System.out.printf("Cooling output: %5.1f%%\n", finalCooling);
        System.out.printf("Airflow output: %5.1f%%\n", finalAirflow);
    }

    static FuzzySet clipSet(FuzzySet source, double weight, TNMin norm) {
        FuzzySet weightSet = new FuzzySet();
        weightSet.addPoint(0.0, weight);
        weightSet.addPoint(100.0, weight);

        FuzzySet clippedSet = new FuzzySet();
        FuzzySet.processSetsWithNorm(clippedSet, weightSet, source, norm);
        return clippedSet;
    }
}