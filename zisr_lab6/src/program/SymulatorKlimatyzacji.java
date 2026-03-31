package program;

import fuzzlib.*;
import fuzzlib.creators.OperationCreator;
import fuzzlib.norms.*;
import fuzzlib.reasoning.ReasoningSystem;
import fuzzlib.reasoning.SystemConfig;

public class SymulatorKlimatyzacji {

    private static final long serialVersionUID = 1L;

    public static void main(String[] args) {
        System.out.println("Rozmyty System Wnioskujący - Symulator Klimatyzacji (Temp + Wilgotność)");
        System.out.println("=========================================================================");

        ReasoningSystem rs1;

        // wejścia

        // temp
        FuzzySet temp_zimno = new FuzzySet("zimno", "");
        FuzzySet temp_optymalna = new FuzzySet("optymalna", "");
        FuzzySet temp_goraco = new FuzzySet("goraco", "");

        temp_optymalna.addPoint(18, 0);
        temp_optymalna.addPoint(24, 1);
        temp_optymalna.addPoint(30, 0);

        temp_zimno.assign(temp_goraco.assign(temp_optymalna));
        temp_zimno.fuzzyfy(-6);
        temp_goraco.fuzzyfy(6);

        // wilgotnosc
        FuzzySet wilg_sucho = new FuzzySet("sucho", "");
        FuzzySet wilg_optymalna = new FuzzySet("optymalna", "");
        FuzzySet wilg_wilgotno = new FuzzySet("wilgotno", "");

        wilg_optymalna.addPoint(30, 0);
        wilg_optymalna.addPoint(50, 1);
        wilg_optymalna.addPoint(70, 0);

        wilg_sucho.assign(wilg_wilgotno.assign(wilg_optymalna));
        wilg_sucho.fuzzyfy(-20);
        wilg_wilgotno.fuzzyfy(30);

        // wyjście

        // moc klimatyzatora
        FuzzySet moc_wylaczona = new FuzzySet("wylaczona", "");
        FuzzySet moc_niska = new FuzzySet("niska", "");
        FuzzySet moc_srednia = new FuzzySet("srednia", "");
        FuzzySet moc_wysoka = new FuzzySet("wysoka", "");
        FuzzySet moc_max = new FuzzySet("maksymalna", "");

        moc_srednia.addPoint(25, 0);
        moc_srednia.addPoint(50, 1);
        moc_srednia.addPoint(75, 0);
        moc_srednia.IncreaseYPrecision(0.05, 0.005);

        moc_wylaczona.assign(moc_niska.assign(moc_wysoka.assign(moc_max.assign(moc_srednia))));
        moc_wylaczona.fuzzyfy(-50);
        moc_niska.fuzzyfy(-25);
        moc_wysoka.fuzzyfy(25);
        moc_max.fuzzyfy(50);

        // konfiguracja

        SystemConfig config = new SystemConfig();
        config.setInputWidth(2);
        config.setOutputWidth(1);
        config.setNumberOfPremiseSets(6);
        config.setNumberOfConclusionSets(5);

        config.setIsOperationType(TNorm.TN_PRODUCT);
        config.setAndOperationType(TNorm.TN_MINIMUM);
        config.setOrOperationType(SNorm.SN_PROBABSUM);
        config.setImplicationType(TNorm.TN_MINIMUM);
        config.setConclusionAgregationType(SNorm.SN_PROBABSUM);
        config.setTruthCompositionType(TNorm.TN_MINIMUM);
        config.setAutoDefuzzyfication(false);
        config.setDefuzzyfication(DefuzMethod.DF_COG);
        config.setAutoAlpha(true);
        config.setTruthPrecision(0.001, 0.0001);

        rs1 = new ReasoningSystem(config);
        rs1.getInputVar(0).id = "temperatura";
        rs1.getInputVar(1).id = "wilgotnosc";
        rs1.getOutputVar(0).id = "moc_klimatyzatora";

        rs1.addPremiseSet(temp_zimno);
        rs1.addPremiseSet(temp_optymalna);
        rs1.addPremiseSet(temp_goraco);
        rs1.addPremiseSet(wilg_sucho);
        rs1.addPremiseSet(wilg_optymalna);
        rs1.addPremiseSet(wilg_wilgotno);

        rs1.addConclusionSet(moc_wylaczona);
        rs1.addConclusionSet(moc_niska);
        rs1.addConclusionSet(moc_srednia);
        rs1.addConclusionSet(moc_wysoka);
        rs1.addConclusionSet(moc_max);
        // baza regul
        try {
            rs1.addRule(1, 1); rs1.addRuleItem("temperatura", "zimno", "AND", "wilgotnosc", "sucho"); rs1.addRuleConclusion("moc_klimatyzatora", "wylaczona");
            rs1.addRule(1, 1); rs1.addRuleItem("temperatura", "zimno", "AND", "wilgotnosc", "optymalna"); rs1.addRuleConclusion("moc_klimatyzatora", "wylaczona");
            rs1.addRule(1, 1); rs1.addRuleItem("temperatura", "zimno", "AND", "wilgotnosc", "wilgotno"); rs1.addRuleConclusion("moc_klimatyzatora", "niska");
            rs1.addRule(1, 1); rs1.addRuleItem("temperatura", "optymalna", "AND", "wilgotnosc", "sucho"); rs1.addRuleConclusion("moc_klimatyzatora", "wylaczona");
            rs1.addRule(1, 1); rs1.addRuleItem("temperatura", "optymalna", "AND", "wilgotnosc", "optymalna"); rs1.addRuleConclusion("moc_klimatyzatora", "niska");
            rs1.addRule(1, 1); rs1.addRuleItem("temperatura", "optymalna", "AND", "wilgotnosc", "wilgotno"); rs1.addRuleConclusion("moc_klimatyzatora", "srednia");
            rs1.addRule(1, 1); rs1.addRuleItem("temperatura", "goraco", "AND", "wilgotnosc", "sucho"); rs1.addRuleConclusion("moc_klimatyzatora", "srednia");
            rs1.addRule(1, 1); rs1.addRuleItem("temperatura", "goraco", "AND", "wilgotnosc", "optymalna"); rs1.addRuleConclusion("moc_klimatyzatora", "wysoka");
            rs1.addRule(1, 1); rs1.addRuleItem("temperatura", "goraco", "AND", "wilgotnosc", "wilgotno"); rs1.addRuleConclusion("moc_klimatyzatora", "maksymalna");
        } catch (Exception e) {
            System.err.println("Błąd podczas ładowania bazy reguł: " + e.getMessage());
        }

        // test

        double obecnaTemp = 27.5; // °C
        double obecnaWilg = 50.0; // %

        System.out.println("Odczyty z czujników:");
        System.out.println(" > Temperatura: " + obecnaTemp + " °C");
        System.out.println(" > Wilgotność: " + obecnaWilg + " %");

        rs1.setInput(0, obecnaTemp);
        rs1.setInput(1, obecnaWilg);
        rs1.Process();

        System.out.println("\nWynik wnioskowania:");
        double wynikDefuzzyfikacji = rs1.getOutputVar(0).outset.DeFuzzyfy();
        System.out.println(" > Wysterowanie sprężarki klimatyzatora: " + String.format("%.2f", wynikDefuzzyfikacji) + " %");
    }
}