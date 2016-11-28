package math;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EquidistributionPhi {


	private List<Double> sixIntervals = new ArrayList<>();
	private List<Double> eigthIntervals = new ArrayList<>();
	private List<Double> tenIntervals = new ArrayList<>();
	private List<Double> twelveIntervals = new ArrayList<>();
	private List<Double> fourteenIntervals = new ArrayList<>();
	private List<Double> sixteenIntervals = new ArrayList<>();
	private List<Double> eightteenIntervals = new ArrayList<>();
	private List<Double> twentyIntervals = new ArrayList<>();
	private List<Double> twentyTwoIntervals = new ArrayList<>();
	private List<Double> twentyFourIntervals = new ArrayList<>();
	private List<Double> twentySixIntervals = new ArrayList<>();
	private List<Double> twentyEightntervals = new ArrayList<>();

	private Map<Integer, List<Double>> mapCountOfIntervalsAndPointValues;


	public EquidistributionPhi(){

		// Aprroximation with this code (100 steps * 0.01) 12 oct. 2013
		sixIntervals.add(0.0);
		sixIntervals.add(0.8429940287132611);
		sixIntervals.add(1.2304571226560022);
		sixIntervals.add(1.5707963267948966);
		sixIntervals.add(1.9111355309337907);
		sixIntervals.add(2.298598624876532);
		sixIntervals.add(3.141592653589793);


		eigthIntervals.add(0.0);
		eigthIntervals.add(0.7225663103256523);
		eigthIntervals.add(1.0485065481355935);
		eigthIntervals.add(1.3194689145077132);
		eigthIntervals.add(1.5707963267948966);
		eigthIntervals.add(1.82212373908208);
		eigthIntervals.add(2.0930861054541996);
		eigthIntervals.add(2.419026343264141);
		eigthIntervals.add(3.141592653589793);


		tenIntervals.add(0.0);
		tenIntervals.add(0.6283185307179586);
		tenIntervals.add(0.9173450548482196);
		tenIntervals.add(1.152964503867454);
		tenIntervals.add(1.36659280431156);
		tenIntervals.add(1.5707963267948966);
		tenIntervals.add(1.774999849278233);
		tenIntervals.add(1.988628149722339);
		tenIntervals.add(2.2242475987415737);
		tenIntervals.add(2.5132741228718345);
		tenIntervals.add(3.141592653589793);


		twelveIntervals.add(0.0);
		twelveIntervals.add(0.5235987755982988);
		twelveIntervals.add(0.7853981633974483);
		twelveIntervals.add(1.0105456369047168);
		twelveIntervals.add(1.2095131716320702);
		twelveIntervals.add(1.392772743091475);
		twelveIntervals.add(1.5707963267948966);
		twelveIntervals.add(1.7488199104983182);
		twelveIntervals.add(1.9320794819577225);
		twelveIntervals.add(2.131047016685076);
		twelveIntervals.add(2.356194490192345);
		twelveIntervals.add(2.617993877991494);
		twelveIntervals.add(3.141592653589793);

		// Aprroximation with this code (20 steps * 0.05) 12 oct. 2013

		fourteenIntervals.add(0.0);
		fourteenIntervals.add(0.4487989505128276);
		fourteenIntervals.add(0.6731984257692414);
		fourteenIntervals.add(0.8975979010256552);
		fourteenIntervals.add(1.088337454993607 );
		fourteenIntervals.add(1.2566370614359172);
		fourteenIntervals.add(1.413716694115407);
		fourteenIntervals.add(1.5707963267948966);
		fourteenIntervals.add(1.7278759594743862);
		fourteenIntervals.add(1.8849555921538759);
		fourteenIntervals.add(2.053255198596186);
		fourteenIntervals.add(2.2439947525641375);
		fourteenIntervals.add(2.4683942278205517);
		fourteenIntervals.add(2.692793703076966);
		fourteenIntervals.add(3.141592653589793);

		sixteenIntervals.add(0.0);
		sixteenIntervals.add(0.39269908169872414);
		sixteenIntervals.add(0.5890486225480862);
		sixteenIntervals.add(0.7853981633974483);
		sixteenIntervals.add(0.9719302272043422);
		sixteenIntervals.add(1.1388273369263);
		sixteenIntervals.add(1.2860894925633215);
		sixteenIntervals.add(1.4333516482003432);
		sixteenIntervals.add(1.5707963267948966);
		sixteenIntervals.add(1.70824100538945);
		sixteenIntervals.add(1.8555031610264716);
		sixteenIntervals.add(2.002765316663493);
		sixteenIntervals.add(2.1696624263854507);
		sixteenIntervals.add(2.356194490192345);
		sixteenIntervals.add(2.552544031041707);
		sixteenIntervals.add(2.7488935718910685);
		sixteenIntervals.add(3.141592653589793);


		eightteenIntervals.add(0.0); 
		eightteenIntervals.add(0.33161255787892263);
		eightteenIntervals.add(0.5235987755982988);
		eightteenIntervals.add(0.6981317007977318);
		eightteenIntervals.add(0.8726646259971648);
		eightteenIntervals.add(1.0297442586766543);
		eightteenIntervals.add(1.1693705988362006);
		eightteenIntervals.add(1.308996938995747);
		eightteenIntervals.add(1.4486232791552935);
		eightteenIntervals.add(1.5707963267948966);
		eightteenIntervals.add(1.6929693744344996);
		eightteenIntervals.add(1.832595714594046);
		eightteenIntervals.add(1.9722220547535922);
		eightteenIntervals.add(2.111848394913139);
		eightteenIntervals.add(2.2689280275926285);
		eightteenIntervals.add(2.443460952792061);
		eightteenIntervals.add(2.6179938779914944);
		eightteenIntervals.add(2.8099800957108707);
		eightteenIntervals.add(3.141592653589793);

		twentyIntervals.add(0.0);
		twentyIntervals.add(0.3141592653589793);
		twentyIntervals.add(0.47123889803846897);
		twentyIntervals.add(0.6283185307179586);
		twentyIntervals.add(0.7853981633974483);
		twentyIntervals.add(0.9424777960769379);
		twentyIntervals.add(1.0838494654884787);
		twentyIntervals.add(1.2095131716320704);
		twentyIntervals.add(1.335176877775662);
		twentyIntervals.add(1.4608405839192538);
		twentyIntervals.add(1.5707963267948966);
		twentyIntervals.add(1.6807520696705391);
		twentyIntervals.add(1.806415775814131);
		twentyIntervals.add(1.9320794819577227);
		twentyIntervals.add(2.0577431881013144);
		twentyIntervals.add(2.199114857512855);
		twentyIntervals.add(2.356194490192345);
		twentyIntervals.add(2.5132741228718345);
		twentyIntervals.add(2.670353755551324);
		twentyIntervals.add(2.827433388230814);
		twentyIntervals.add(3.141592653589793);

		twentyTwoIntervals.add(0.0);
		twentyTwoIntervals.add(0.28559933214452665);
		twentyTwoIntervals.add(0.42839899821678995); 
		twentyTwoIntervals.add(0.5711986642890533);
		twentyTwoIntervals.add(0.7139983303613167);
		twentyTwoIntervals.add(0.85679799643358);
		twentyTwoIntervals.add(0.985317695898617);
		twentyTwoIntervals.add(1.113837395363654);
		twentyTwoIntervals.add(1.2280771282214646);
		twentyTwoIntervals.add(1.3423168610792753);
		twentyTwoIntervals.add(1.456556593937086);
		twentyTwoIntervals.add(1.5707963267948963);
		twentyTwoIntervals.add(1.6850360596527072);
		twentyTwoIntervals.add(1.7992757925105178);
		twentyTwoIntervals.add(1.9135155253683285);
		twentyTwoIntervals.add(2.0277552582261387);
		twentyTwoIntervals.add(2.1562749576911764);
		twentyTwoIntervals.add(2.284794657156213);
		twentyTwoIntervals.add(2.4275943232284765);
		twentyTwoIntervals.add(2.5703939893007397);
		twentyTwoIntervals.add(2.7131936553730034);
		twentyTwoIntervals.add(2.8559933214452666);
		twentyTwoIntervals.add(3.141592653589793);

		// Aprroximation with this code (5 steps * 0.2) 19 jan. 2014

		twentyFourIntervals.add(0.0);
		twentyFourIntervals.add(0.2617993877991494);
		twentyFourIntervals.add(0.39269908169872414);
		twentyFourIntervals.add(0.5235987755982988);
		twentyFourIntervals.add(0.6544984694978735);
		twentyFourIntervals.add(0.7853981633974483); 
		twentyFourIntervals.add(0.916297857297023); 
		twentyFourIntervals.add(1.0471975511965976); 
		twentyFourIntervals.add(1.1519173063162573); 
		twentyFourIntervals.add(1.2566370614359172); 
		twentyFourIntervals.add(1.3613568165555772); 
		twentyFourIntervals.add(1.4660765716752366); 
		twentyFourIntervals.add(1.5707963267948966); 
		twentyFourIntervals.add(1.6755160819145565); 
		twentyFourIntervals.add(1.780235837034216); 
		twentyFourIntervals.add(1.8849555921538756); 
		twentyFourIntervals.add(1.9896753472735356); 
		twentyFourIntervals.add(2.0943951023931957); 
		twentyFourIntervals.add(2.2252947962927703); 
		twentyFourIntervals.add(2.356194490192345); 
		twentyFourIntervals.add(2.48709418409192); 
		twentyFourIntervals.add(2.6179938779914944); 
		twentyFourIntervals.add(2.748893571891069); 
		twentyFourIntervals.add(2.8797932657906435); 
		twentyFourIntervals.add(3.141592653589793); 

		twentySixIntervals.add(0.0);
		twentySixIntervals.add(0.241660973353061);
		twentySixIntervals.add(0.3624914600295915);
		twentySixIntervals.add(0.48332194670612205);
		twentySixIntervals.add(0.6041524333826525);
		twentySixIntervals.add(0.7249829200591831); 
		twentySixIntervals.add(0.8458134067357136); 
		twentySixIntervals.add(0.966643893412244);
		twentySixIntervals.add(1.0874743800887745); 
		twentySixIntervals.add(1.184138769429999); 
		twentySixIntervals.add(1.2808031587712234); 
		twentySixIntervals.add(1.3774675481124474);
		twentySixIntervals.add(1.4741319374536723); 
		twentySixIntervals.add(1.5707963267948968);
		twentySixIntervals.add(1.6674607161361208); 
		twentySixIntervals.add(1.7641251054773452); 
		twentySixIntervals.add(1.8607894948185697); 
		twentySixIntervals.add(1.9574538841597942); 
		twentySixIntervals.add(2.0541182735010186); 
		twentySixIntervals.add(2.174948760177549); 
		twentySixIntervals.add(2.29577924685408); 
		twentySixIntervals.add(2.4166097335306103);
		twentySixIntervals.add(2.53744022020714);
		twentySixIntervals.add(2.658270706883671); 
		twentySixIntervals.add(2.779101193560202);
		twentySixIntervals.add(2.8999316802367323);
		twentySixIntervals.add(3.141592653589793); 

		// maybe not finished the 28 .... should be redone		
		twentyEightntervals.add(0.0); 
		twentyEightntervals.add(0.20195952773077241);
		twentyEightntervals.add(0.3365992128846207); 
		twentyEightntervals.add(0.4487989505128276);
		twentyEightntervals.add(0.5609986881410345);
		twentyEightntervals.add(0.6731984257692414); 
		twentyEightntervals.add(0.7853981633974483); 
		twentyEightntervals.add(0.8975979010256552); 
		twentyEightntervals.add(1.009797638653862); 
		twentyEightntervals.add(1.121997376282069);
		twentyEightntervals.add(1.2117571663846345); 
		twentyEightntervals.add(1.3015169564871998);
		twentyEightntervals.add(1.3912767465897655); 
		twentyEightntervals.add(1.4810365366923313); 
		twentyEightntervals.add(1.5707963267948966); 
		twentyEightntervals.add(1.6605561168974619); 
		twentyEightntervals.add(1.7503159070000276); 
		twentyEightntervals.add(1.8400756971025933); 
		twentyEightntervals.add(1.9298354872051586); 
		twentyEightntervals.add(2.019595277307724); 
		twentyEightntervals.add(2.131795014935931); 
		twentyEightntervals.add(2.243994752564138); 
		twentyEightntervals.add(2.3561944901923444); 
		twentyEightntervals.add(2.4683942278205517); 
		twentyEightntervals.add(2.5805939654487586); 
		twentyEightntervals.add(2.6927937030769655); 
		twentyEightntervals.add(2.804993440705173); 
		twentyEightntervals.add(2.9396331258590207); 
		twentyEightntervals.add(3.141592653589793); 


		mapCountOfIntervalsAndPointValues = new HashMap<>();

		mapCountOfIntervalsAndPointValues.put(6, sixIntervals);
		mapCountOfIntervalsAndPointValues.put(8, eigthIntervals);
		mapCountOfIntervalsAndPointValues.put(10, tenIntervals);
		mapCountOfIntervalsAndPointValues.put(10, tenIntervals);
		mapCountOfIntervalsAndPointValues.put(12, twelveIntervals);
		mapCountOfIntervalsAndPointValues.put(14, fourteenIntervals);
		mapCountOfIntervalsAndPointValues.put(16, sixteenIntervals);
		mapCountOfIntervalsAndPointValues.put(18, eightteenIntervals);
		mapCountOfIntervalsAndPointValues.put(20, twentyIntervals);
		mapCountOfIntervalsAndPointValues.put(22, twentyTwoIntervals);
		mapCountOfIntervalsAndPointValues.put(24, twentyFourIntervals);
		mapCountOfIntervalsAndPointValues.put(26, twentySixIntervals);
		mapCountOfIntervalsAndPointValues.put(28, twentySixIntervals);
	}


	// Purpose of this class is to get preComputed well distributed angle values (latitude OR Phi according to Apache libs.)  accoring to constant solid angle
	public Map<Integer, List<Double>> getMapCountOfIntervalsAndPointValues() {
		return mapCountOfIntervalsAndPointValues;
	}



	public static void main(String[] args) {

		// find empirically a distribution of n intervals
		// n must be even
		int n=28;

		List<Double> startingValues = defineStartingValues(n);  // n+1 values
		List<Double> startingDeltaValues = defineStartingDeltaValues(n); // n/2 -1 values   e.g for 6 it is 2 ; for 8 it is 3 ...

		List<Double> optimizedValues = optimizeDeltasN28(startingValues, startingDeltaValues, n);

	}



	public static List<Double> defineStartingDeltaValues(Integer n){

		int countOfDeltaNeeded = (n / 2) - 1;
		List<Double> deltaValues = new ArrayList<>();

		for (int i=0; i<countOfDeltaNeeded; i++){
			double deltaValue = 0.0;
			deltaValues.add(deltaValue);
		}

		return deltaValues;
	}



	public static List<Double> defineStartingValues(Integer n){

		List<Double> positionPoint = new ArrayList<>();

		for (int i=0; i< n ; i++){
			double value = i * Math.PI / (1.0 * n);
			positionPoint.add(value);
		}
		positionPoint.add(Math.PI);

		return positionPoint;
	}



	public static List<Double> optimizeDeltasN6(List<Double> startingValues, List<Double> startingDeltaValues, Integer n){

		if (n != 6){
			System.out.println("routine only OK for n=6");
			System.exit(0);
		}

		List<Double> optimizeDeltas = new ArrayList<>();

		// Systematic optimization

		double minRmsd = Double.MAX_VALUE;
		List<Double> valuesAtMin;

		for (int incr1=0; incr1<101; incr1++){
			for (int incr2=0; incr2<101; incr2++){

				List<Double> coeff = new ArrayList<>();
				coeff.add(incr1 * 0.01);
				coeff.add(incr2 * 0.01);

				// Fill deltaValuesToTry
				List<Double> currentDeltaValues = new ArrayList<>();
				for (int i=0; i<startingDeltaValues.size(); i++){

					double startingValue = startingDeltaValues.get(i);
					double newValue = startingValue + coeff.get(i) * Math.PI / (1.0 * n);
					currentDeltaValues.add(newValue);
				}
				// Fill as a consequence the values
				List<Double> currentValues = new ArrayList<>();
				for (Double value: startingValues){
					currentValues.add(value);
				}

				for (int i=0; i<startingDeltaValues.size(); i++){
					int rankLeft = i+1;
					int rankRight = n-rankLeft;
					//System.out.println("changing values at index " + rankLeft + "  " +  rankRight);
					double startingValueLeft = startingValues.get(rankLeft);
					double startingValueRight = startingValues.get(rankRight);
					double newValueLeft = startingValueLeft + currentDeltaValues.get(i);
					double newValueRight = startingValueRight - currentDeltaValues.get(i);

					currentValues.set(rankLeft, newValueLeft);
					currentValues.set(rankRight, newValueRight);
				}

				//System.out.println("there values should be set");
				double cost = computeCostToMinimize(currentValues);
				if (cost < minRmsd){
					minRmsd = cost;
					valuesAtMin = currentValues;
					System.out.println("new min " + minRmsd);
					printValues(valuesAtMin);
				}
				//System.out.println("cost = " + cost);
			}
		}
		return optimizeDeltas;
	}


	public static List<Double> optimizeDeltasN8(List<Double> startingValues, List<Double> startingDeltaValues, Integer n){

		if (n != 8){
			System.out.println("routine only OK for n=8");
			System.exit(0);
		}

		List<Double> optimizeDeltas = new ArrayList<>();

		// Systematic optimization

		double minRmsd = Double.MAX_VALUE;
		List<Double> valuesAtMin;

		for (int incr1=0; incr1<101; incr1++){
			for (int incr2=0; incr2<101; incr2++){
				for (int incr3=0; incr3<101; incr3++){

					List<Double> coeff = new ArrayList<>();
					coeff.add(incr1 * 0.01);
					coeff.add(incr2 * 0.01);
					coeff.add(incr3 * 0.01);

					// Fill deltaValuesToTry
					List<Double> currentDeltaValues = new ArrayList<>();
					for (int i=0; i<startingDeltaValues.size(); i++){

						double startingValue = startingDeltaValues.get(i);
						double newValue = startingValue + coeff.get(i) * Math.PI / (1.0 * n);
						currentDeltaValues.add(newValue);
					}
					// Fill as a consequence the values
					List<Double> currentValues = new ArrayList<>();
					for (Double value: startingValues){
						currentValues.add(value);
					}

					for (int i=0; i<startingDeltaValues.size(); i++){
						int rankLeft = i+1;
						int rankRight = n-rankLeft;
						//System.out.println("changing values at index " + rankLeft + "  " +  rankRight);
						double startingValueLeft = startingValues.get(rankLeft);
						double startingValueRight = startingValues.get(rankRight);
						double newValueLeft = startingValueLeft + currentDeltaValues.get(i);
						double newValueRight = startingValueRight - currentDeltaValues.get(i);

						currentValues.set(rankLeft, newValueLeft);
						currentValues.set(rankRight, newValueRight);
					}

					//System.out.println("there values should be set");
					double cost = computeCostToMinimize(currentValues);
					if (cost < minRmsd){
						minRmsd = cost;
						valuesAtMin = currentValues;
						System.out.println("new min " + minRmsd);
						printValues(valuesAtMin);
					}
					//System.out.println("cost = " + cost);
				}
			}
		}
		return optimizeDeltas;
	}



	public static List<Double> optimizeDeltasN10(List<Double> startingValues, List<Double> startingDeltaValues, Integer n){

		if (n != 10){
			System.out.println("routine only OK for n=10");
			System.exit(0);
		}

		List<Double> optimizeDeltas = new ArrayList<>();

		// Systematic optimization

		double minRmsd = Double.MAX_VALUE;
		List<Double> valuesAtMin;

		for (int incr1=0; incr1<101; incr1++){
			for (int incr2=0; incr2<101; incr2++){
				for (int incr3=0; incr3<101; incr3++){
					for (int incr4=0; incr4<101; incr4++){
						List<Double> coeff = new ArrayList<>();
						coeff.add(incr1 * 0.01);
						coeff.add(incr2 * 0.01);
						coeff.add(incr3 * 0.01);
						coeff.add(incr4 * 0.01);

						// Fill deltaValuesToTry
						List<Double> currentDeltaValues = new ArrayList<>();
						for (int i=0; i<startingDeltaValues.size(); i++){

							double startingValue = startingDeltaValues.get(i);
							double newValue = startingValue + coeff.get(i) * Math.PI / (1.0 * n);
							currentDeltaValues.add(newValue);
						}
						// Fill as a consequence the values
						List<Double> currentValues = new ArrayList<>();
						for (Double value: startingValues){
							currentValues.add(value);
						}

						for (int i=0; i<startingDeltaValues.size(); i++){
							int rankLeft = i+1;
							int rankRight = n-rankLeft;
							//System.out.println("changing values at index " + rankLeft + "  " +  rankRight);
							double startingValueLeft = startingValues.get(rankLeft);
							double startingValueRight = startingValues.get(rankRight);
							double newValueLeft = startingValueLeft + currentDeltaValues.get(i);
							double newValueRight = startingValueRight - currentDeltaValues.get(i);

							currentValues.set(rankLeft, newValueLeft);
							currentValues.set(rankRight, newValueRight);
						}

						//System.out.println("there values should be set");
						double cost = computeCostToMinimize(currentValues);
						if (cost < minRmsd){
							minRmsd = cost;
							valuesAtMin = currentValues;
							System.out.println("new min " + minRmsd);
							printValues(valuesAtMin);
						}
						//System.out.println("cost = " + cost);
					}
				}
			}
		}
		return optimizeDeltas;
	}


	public static List<Double> optimizeDeltasN12(List<Double> startingValues, List<Double> startingDeltaValues, Integer n){

		if (n != 12){
			System.out.println("routine only OK for n=12");
			System.exit(0);
		}

		List<Double> optimizeDeltas = new ArrayList<>();

		// Systematic optimization

		double minRmsd = Double.MAX_VALUE;
		List<Double> valuesAtMin;

		for (int incr1=0; incr1<101; incr1++){
			System.out.println(incr1);
			for (int incr2=0; incr2<101; incr2++){
				for (int incr3=0; incr3<101; incr3++){
					for (int incr4=0; incr4<101; incr4++){
						for (int incr5=0; incr5<101; incr5++){
							List<Double> coeff = new ArrayList<>();
							coeff.add(incr1 * 0.01);
							coeff.add(incr2 * 0.01);
							coeff.add(incr3 * 0.01);
							coeff.add(incr4 * 0.01);
							coeff.add(incr5 * 0.01);
							// Fill deltaValuesToTry
							List<Double> currentDeltaValues = new ArrayList<>();
							for (int i=0; i<startingDeltaValues.size(); i++){

								double startingValue = startingDeltaValues.get(i);
								double newValue = startingValue + coeff.get(i) * Math.PI / (1.0 * n);
								currentDeltaValues.add(newValue);
							}
							// Fill as a consequence the values
							List<Double> currentValues = new ArrayList<>();
							for (Double value: startingValues){
								currentValues.add(value);
							}

							for (int i=0; i<startingDeltaValues.size(); i++){
								int rankLeft = i+1;
								int rankRight = n-rankLeft;
								//System.out.println("changing values at index " + rankLeft + "  " +  rankRight);
								double startingValueLeft = startingValues.get(rankLeft);
								double startingValueRight = startingValues.get(rankRight);
								double newValueLeft = startingValueLeft + currentDeltaValues.get(i);
								double newValueRight = startingValueRight - currentDeltaValues.get(i);

								currentValues.set(rankLeft, newValueLeft);
								currentValues.set(rankRight, newValueRight);
							}

							//System.out.println("there values should be set");
							double cost = computeCostToMinimize(currentValues);
							if (cost < minRmsd){
								minRmsd = cost;
								valuesAtMin = currentValues;
								System.out.println("new min " + minRmsd);
								printValues(valuesAtMin);
							}
							//System.out.println("cost = " + cost);
						}
					}
				}
			}
		}
		return optimizeDeltas;
	}



	public static List<Double> optimizeDeltasN14(List<Double> startingValues, List<Double> startingDeltaValues, Integer n){

		if (n != 14){
			System.out.println("routine only OK for n=14");
			System.exit(0);
		}

		List<Double> optimizeDeltas = new ArrayList<>();

		// Systematic optimization

		double minRmsd = Double.MAX_VALUE;
		List<Double> valuesAtMin;

		for (int incr1=0; incr1<21; incr1++){
			System.out.println(incr1);
			for (int incr2=0; incr2<21; incr2++){
				for (int incr3=0; incr3<21; incr3++){
					for (int incr4=0; incr4<21; incr4++){
						for (int incr5=0; incr5<21; incr5++){
							for (int incr6=0; incr6<21; incr6++){
								List<Double> coeff = new ArrayList<>();
								coeff.add(incr1 * 0.05);
								coeff.add(incr2 * 0.05);
								coeff.add(incr3 * 0.05);
								coeff.add(incr4 * 0.05);
								coeff.add(incr5 * 0.05);
								coeff.add(incr6 * 0.05);
								// Fill deltaValuesToTry
								List<Double> currentDeltaValues = new ArrayList<>();
								for (int i=0; i<startingDeltaValues.size(); i++){

									double startingValue = startingDeltaValues.get(i);
									double newValue = startingValue + coeff.get(i) * Math.PI / (1.0 * n);
									currentDeltaValues.add(newValue);
								}
								// Fill as a consequence the values
								List<Double> currentValues = new ArrayList<>();
								for (Double value: startingValues){
									currentValues.add(value);
								}

								for (int i=0; i<startingDeltaValues.size(); i++){
									int rankLeft = i+1;
									int rankRight = n-rankLeft;
									//System.out.println("changing values at index " + rankLeft + "  " +  rankRight);
									double startingValueLeft = startingValues.get(rankLeft);
									double startingValueRight = startingValues.get(rankRight);
									double newValueLeft = startingValueLeft + currentDeltaValues.get(i);
									double newValueRight = startingValueRight - currentDeltaValues.get(i);

									currentValues.set(rankLeft, newValueLeft);
									currentValues.set(rankRight, newValueRight);
								}

								//System.out.println("there values should be set");
								double cost = computeCostToMinimize(currentValues);
								if (cost < minRmsd){
									minRmsd = cost;
									valuesAtMin = currentValues;
									System.out.println("new min " + minRmsd);
									printValues(valuesAtMin);
								}
								//System.out.println("cost = " + cost);
							}
						}
					}
				}
			}
		}
		return optimizeDeltas;
	}



	public static List<Double> optimizeDeltasN16(List<Double> startingValues, List<Double> startingDeltaValues, Integer n){

		if (n != 16){
			System.out.println("routine only OK for n=16");
			System.exit(0);
		}

		List<Double> optimizeDeltas = new ArrayList<>();

		// Systematic optimization

		double minRmsd = Double.MAX_VALUE;
		List<Double> valuesAtMin;

		for (int incr1=0; incr1<21; incr1++){
			System.out.println(incr1);
			for (int incr2=0; incr2<21; incr2++){
				for (int incr3=0; incr3<21; incr3++){
					for (int incr4=0; incr4<21; incr4++){
						for (int incr5=0; incr5<21; incr5++){
							for (int incr6=0; incr6<21; incr6++){
								for (int incr7=0; incr7<21; incr7++){
									List<Double> coeff = new ArrayList<>();
									coeff.add(incr1 * 0.05);
									coeff.add(incr2 * 0.05);
									coeff.add(incr3 * 0.05);
									coeff.add(incr4 * 0.05);
									coeff.add(incr5 * 0.05);
									coeff.add(incr6 * 0.05);
									coeff.add(incr7 * 0.05);
									// Fill deltaValuesToTry
									List<Double> currentDeltaValues = new ArrayList<>();
									for (int i=0; i<startingDeltaValues.size(); i++){

										double startingValue = startingDeltaValues.get(i);
										double newValue = startingValue + coeff.get(i) * Math.PI / (1.0 * n);
										currentDeltaValues.add(newValue);
									}
									// Fill as a consequence the values
									List<Double> currentValues = new ArrayList<>();
									for (Double value: startingValues){
										currentValues.add(value);
									}

									for (int i=0; i<startingDeltaValues.size(); i++){
										int rankLeft = i+1;
										int rankRight = n-rankLeft;
										//System.out.println("changing values at index " + rankLeft + "  " +  rankRight);
										double startingValueLeft = startingValues.get(rankLeft);
										double startingValueRight = startingValues.get(rankRight);
										double newValueLeft = startingValueLeft + currentDeltaValues.get(i);
										double newValueRight = startingValueRight - currentDeltaValues.get(i);

										currentValues.set(rankLeft, newValueLeft);
										currentValues.set(rankRight, newValueRight);
									}

									//System.out.println("there values should be set");
									double cost = computeCostToMinimize(currentValues);
									if (cost < minRmsd){
										minRmsd = cost;
										valuesAtMin = currentValues;
										System.out.println("new min " + minRmsd);
										printValues(valuesAtMin);
									}
									//System.out.println("cost = " + cost);
								}
							}
						}
					}
				}
			}
		}
		return optimizeDeltas;
	}



	public static List<Double> optimizeDeltasN18(List<Double> startingValues, List<Double> startingDeltaValues, Integer n){

		if (n != 18){
			System.out.println("routine only OK for n=18");
			System.exit(0);
		}

		List<Double> optimizeDeltas = new ArrayList<>();

		// Systematic optimization

		double minRmsd = Double.MAX_VALUE;
		List<Double> valuesAtMin;

		for (int incr1=0; incr1<11; incr1++){
			System.out.println(incr1 + "  /  10 is done ");
			for (int incr2=0; incr2<11; incr2++){
				for (int incr3=0; incr3<11; incr3++){
					for (int incr4=0; incr4<11; incr4++){
						for (int incr5=0; incr5<11; incr5++){
							for (int incr6=0; incr6<11; incr6++){
								for (int incr7=0; incr7<11; incr7++){
									for (int incr8=0; incr8<11; incr8++){
										List<Double> coeff = new ArrayList<>();
										coeff.add(incr1 * 0.1);
										coeff.add(incr2 * 0.1);
										coeff.add(incr3 * 0.1);
										coeff.add(incr4 * 0.1);
										coeff.add(incr5 * 0.1);
										coeff.add(incr6 * 0.1);
										coeff.add(incr7 * 0.1);
										coeff.add(incr8 * 0.1);
										// Fill deltaValuesToTry
										List<Double> currentDeltaValues = new ArrayList<>();
										for (int i=0; i<startingDeltaValues.size(); i++){

											double startingValue = startingDeltaValues.get(i);
											double newValue = startingValue + coeff.get(i) * Math.PI / (1.0 * n);
											currentDeltaValues.add(newValue);
										}
										// Fill as a consequence the values
										List<Double> currentValues = new ArrayList<>();
										for (Double value: startingValues){
											currentValues.add(value);
										}

										for (int i=0; i<startingDeltaValues.size(); i++){
											int rankLeft = i+1;
											int rankRight = n-rankLeft;
											//System.out.println("changing values at index " + rankLeft + "  " +  rankRight);
											double startingValueLeft = startingValues.get(rankLeft);
											double startingValueRight = startingValues.get(rankRight);
											double newValueLeft = startingValueLeft + currentDeltaValues.get(i);
											double newValueRight = startingValueRight - currentDeltaValues.get(i);

											currentValues.set(rankLeft, newValueLeft);
											currentValues.set(rankRight, newValueRight);
										}

										//System.out.println("there values should be set");
										double cost = computeCostToMinimize(currentValues);
										if (cost < minRmsd){
											minRmsd = cost;
											valuesAtMin = currentValues;
											System.out.println("new min " + minRmsd);
											printValues(valuesAtMin);
										}
										//System.out.println("cost = " + cost);
									}
								}
							}
						}
					}
				}
			}
		}
		return optimizeDeltas;
	}



	public static List<Double> optimizeDeltasN20(List<Double> startingValues, List<Double> startingDeltaValues, Integer n){

		if (n != 20){
			System.out.println("routine only OK for n=20");
			System.exit(0);
		}

		List<Double> optimizeDeltas = new ArrayList<>();

		// Systematic optimization

		double minRmsd = Double.MAX_VALUE;
		List<Double> valuesAtMin;

		for (int incr1=0; incr1<11; incr1++){
			System.out.println(incr1);
			for (int incr2=0; incr2<11; incr2++){
				for (int incr3=0; incr3<11; incr3++){
					for (int incr4=0; incr4<11; incr4++){
						for (int incr5=0; incr5<11; incr5++){
							for (int incr6=0; incr6<11; incr6++){
								for (int incr7=0; incr7<11; incr7++){
									for (int incr8=0; incr8<11; incr8++){
										for (int incr9=0; incr9<11; incr9++){
											List<Double> coeff = new ArrayList<>();
											coeff.add(incr1 * 0.1);
											coeff.add(incr2 * 0.1);
											coeff.add(incr3 * 0.1);
											coeff.add(incr4 * 0.1);
											coeff.add(incr5 * 0.1);
											coeff.add(incr6 * 0.1);
											coeff.add(incr7 * 0.1);
											coeff.add(incr8 * 0.1);
											coeff.add(incr9 * 0.1);
											// Fill deltaValuesToTry
											List<Double> currentDeltaValues = new ArrayList<>();
											for (int i=0; i<startingDeltaValues.size(); i++){

												double startingValue = startingDeltaValues.get(i);
												double newValue = startingValue + coeff.get(i) * Math.PI / (1.0 * n);
												currentDeltaValues.add(newValue);
											}
											// Fill as a consequence the values
											List<Double> currentValues = new ArrayList<>();
											for (Double value: startingValues){
												currentValues.add(value);
											}

											for (int i=0; i<startingDeltaValues.size(); i++){
												int rankLeft = i+1;
												int rankRight = n-rankLeft;
												//System.out.println("changing values at index " + rankLeft + "  " +  rankRight);
												double startingValueLeft = startingValues.get(rankLeft);
												double startingValueRight = startingValues.get(rankRight);
												double newValueLeft = startingValueLeft + currentDeltaValues.get(i);
												double newValueRight = startingValueRight - currentDeltaValues.get(i);

												currentValues.set(rankLeft, newValueLeft);
												currentValues.set(rankRight, newValueRight);
											}

											//System.out.println("there values should be set");
											double cost = computeCostToMinimize(currentValues);
											if (cost < minRmsd){
												minRmsd = cost;
												valuesAtMin = currentValues;
												System.out.println("new min " + minRmsd);
												printValues(valuesAtMin);
											}
											//System.out.println("cost = " + cost);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return optimizeDeltas;
	}



	public static List<Double> optimizeDeltasN22(List<Double> startingValues, List<Double> startingDeltaValues, Integer n){

		if (n != 22){
			System.out.println("routine only OK for n=22");
			System.exit(0);
		}

		List<Double> optimizeDeltas = new ArrayList<>();

		// Systematic optimization

		double minRmsd = Double.MAX_VALUE;
		List<Double> valuesAtMin;

		for (int incr1=0; incr1<11; incr1++){
			System.out.println(incr1);
			for (int incr2=0; incr2<11; incr2++){
				for (int incr3=0; incr3<11; incr3++){
					for (int incr4=0; incr4<11; incr4++){
						for (int incr5=0; incr5<11; incr5++){
							for (int incr6=0; incr6<11; incr6++){
								for (int incr7=0; incr7<11; incr7++){
									for (int incr8=0; incr8<11; incr8++){
										for (int incr9=0; incr9<11; incr9++){
											for (int incr10=0; incr10<11; incr10++){
												List<Double> coeff = new ArrayList<>();
												coeff.add(incr1 * 0.1);
												coeff.add(incr2 * 0.1);
												coeff.add(incr3 * 0.1);
												coeff.add(incr4 * 0.1);
												coeff.add(incr5 * 0.1);
												coeff.add(incr6 * 0.1);
												coeff.add(incr7 * 0.1);
												coeff.add(incr8 * 0.1);
												coeff.add(incr9 * 0.1);
												coeff.add(incr10 * 0.1);
												// Fill deltaValuesToTry
												List<Double> currentDeltaValues = new ArrayList<>();
												for (int i=0; i<startingDeltaValues.size(); i++){

													double startingValue = startingDeltaValues.get(i);
													double newValue = startingValue + coeff.get(i) * Math.PI / (1.0 * n);
													currentDeltaValues.add(newValue);
												}
												// Fill as a consequence the values
												List<Double> currentValues = new ArrayList<>();
												for (Double value: startingValues){
													currentValues.add(value);
												}

												for (int i=0; i<startingDeltaValues.size(); i++){
													int rankLeft = i+1;
													int rankRight = n-rankLeft;
													//System.out.println("changing values at index " + rankLeft + "  " +  rankRight);
													double startingValueLeft = startingValues.get(rankLeft);
													double startingValueRight = startingValues.get(rankRight);
													double newValueLeft = startingValueLeft + currentDeltaValues.get(i);
													double newValueRight = startingValueRight - currentDeltaValues.get(i);

													currentValues.set(rankLeft, newValueLeft);
													currentValues.set(rankRight, newValueRight);
												}

												//System.out.println("there values should be set");
												double cost = computeCostToMinimize(currentValues);
												if (cost < minRmsd){
													minRmsd = cost;
													valuesAtMin = currentValues;
													System.out.println("new min " + minRmsd);
													printValues(valuesAtMin);
												}
												//System.out.println("cost = " + cost);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return optimizeDeltas;
	}



	public static List<Double> optimizeDeltasN24(List<Double> startingValues, List<Double> startingDeltaValues, Integer n){

		if (n != 24){
			System.out.println("routine only OK for n=24");
			System.exit(0);
		}

		List<Double> optimizeDeltas = new ArrayList<>();

		// Systematic optimization

		double minRmsd = Double.MAX_VALUE;
		List<Double> valuesAtMin;

		for (int incr1=0; incr1<6; incr1++){
			System.out.println(incr1);
			for (int incr2=0; incr2<6; incr2++){
				for (int incr3=0; incr3<6; incr3++){
					for (int incr4=0; incr4<6; incr4++){
						for (int incr5=0; incr5<6; incr5++){
							for (int incr6=0; incr6<6; incr6++){
								for (int incr7=0; incr7<6; incr7++){
									for (int incr8=0; incr8<6; incr8++){
										for (int incr9=0; incr9<6; incr9++){
											for (int incr10=0; incr10<6; incr10++){
												for (int incr11=0; incr11<6; incr11++){
													List<Double> coeff = new ArrayList<>();
													coeff.add(incr1 * 0.2);
													coeff.add(incr2 * 0.2);
													coeff.add(incr3 * 0.2);
													coeff.add(incr4 * 0.2);
													coeff.add(incr5 * 0.2);
													coeff.add(incr6 * 0.2);
													coeff.add(incr7 * 0.2);
													coeff.add(incr8 * 0.2);
													coeff.add(incr9 * 0.2);												
													coeff.add(incr10 * 0.2);
													coeff.add(incr11 * 0.2);
													// Fill deltaValuesToTry
													List<Double> currentDeltaValues = new ArrayList<>();
													for (int i=0; i<startingDeltaValues.size(); i++){

														double startingValue = startingDeltaValues.get(i);
														double newValue = startingValue + coeff.get(i) * Math.PI / (1.0 * n);
														currentDeltaValues.add(newValue);
													}
													// Fill as a consequence the values
													List<Double> currentValues = new ArrayList<>();
													for (Double value: startingValues){
														currentValues.add(value);
													}

													for (int i=0; i<startingDeltaValues.size(); i++){
														int rankLeft = i+1;
														int rankRight = n-rankLeft;
														//System.out.println("changing values at index " + rankLeft + "  " +  rankRight);
														double startingValueLeft = startingValues.get(rankLeft);
														double startingValueRight = startingValues.get(rankRight);
														double newValueLeft = startingValueLeft + currentDeltaValues.get(i);
														double newValueRight = startingValueRight - currentDeltaValues.get(i);

														currentValues.set(rankLeft, newValueLeft);
														currentValues.set(rankRight, newValueRight);
													}

													//System.out.println("there values should be set");
													double cost = computeCostToMinimize(currentValues);
													if (cost < minRmsd){
														minRmsd = cost;
														valuesAtMin = currentValues;
														System.out.println("new min " + minRmsd);
														printValues(valuesAtMin);
													}
													//System.out.println("cost = " + cost);
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return optimizeDeltas;
	}


	public static List<Double> optimizeDeltasN26(List<Double> startingValues, List<Double> startingDeltaValues, Integer n){

		if (n != 26){
			System.out.println("routine only OK for n=26");
			System.exit(0);
		}

		List<Double> optimizeDeltas = new ArrayList<>();

		// Systematic optimization

		double minRmsd = Double.MAX_VALUE;
		List<Double> valuesAtMin;

		for (int incr1=0; incr1<6; incr1++){
			System.out.println(incr1);
			for (int incr2=0; incr2<6; incr2++){
				for (int incr3=0; incr3<6; incr3++){
					for (int incr4=0; incr4<6; incr4++){
						for (int incr5=0; incr5<6; incr5++){
							for (int incr6=0; incr6<6; incr6++){
								for (int incr7=0; incr7<6; incr7++){
									for (int incr8=0; incr8<6; incr8++){
										for (int incr9=0; incr9<6; incr9++){
											for (int incr10=0; incr10<6; incr10++){
												for (int incr11=0; incr11<6; incr11++){
													for (int incr12=0; incr12<6; incr12++){
														List<Double> coeff = new ArrayList<>();
														coeff.add(incr1 * 0.2);
														coeff.add(incr2 * 0.2);
														coeff.add(incr3 * 0.2);
														coeff.add(incr4 * 0.2);
														coeff.add(incr5 * 0.2);
														coeff.add(incr6 * 0.2);
														coeff.add(incr7 * 0.2);
														coeff.add(incr8 * 0.2);
														coeff.add(incr9 * 0.2);												
														coeff.add(incr10 * 0.2);
														coeff.add(incr11 * 0.2);
														coeff.add(incr12 * 0.2);
														// Fill deltaValuesToTry
														List<Double> currentDeltaValues = new ArrayList<>();
														for (int i=0; i<startingDeltaValues.size(); i++){

															double startingValue = startingDeltaValues.get(i);
															double newValue = startingValue + coeff.get(i) * Math.PI / (1.0 * n);
															currentDeltaValues.add(newValue);
														}
														// Fill as a consequence the values
														List<Double> currentValues = new ArrayList<>();
														for (Double value: startingValues){
															currentValues.add(value);
														}

														for (int i=0; i<startingDeltaValues.size(); i++){
															int rankLeft = i+1;
															int rankRight = n-rankLeft;
															//System.out.println("changing values at index " + rankLeft + "  " +  rankRight);
															double startingValueLeft = startingValues.get(rankLeft);
															double startingValueRight = startingValues.get(rankRight);
															double newValueLeft = startingValueLeft + currentDeltaValues.get(i);
															double newValueRight = startingValueRight - currentDeltaValues.get(i);

															currentValues.set(rankLeft, newValueLeft);
															currentValues.set(rankRight, newValueRight);
														}

														//System.out.println("there values should be set");
														double cost = computeCostToMinimize(currentValues);
														if (cost < minRmsd){
															minRmsd = cost;
															valuesAtMin = currentValues;
															System.out.println("new min " + minRmsd);
															printValues(valuesAtMin);
														}
														//System.out.println("cost = " + cost);
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return optimizeDeltas;
	}



	public static List<Double> optimizeDeltasN28(List<Double> startingValues, List<Double> startingDeltaValues, Integer n){

		if (n != 28){
			System.out.println("routine only OK for n=28");
			System.exit(0);
		}

		List<Double> optimizeDeltas = new ArrayList<>();

		// Systematic optimization

		double minRmsd = Double.MAX_VALUE;
		List<Double> valuesAtMin;

		for (int incr1=0; incr1<6; incr1++){
			System.out.println(incr1);
			for (int incr2=0; incr2<6; incr2++){
				for (int incr3=0; incr3<6; incr3++){
					for (int incr4=0; incr4<6; incr4++){
						for (int incr5=0; incr5<6; incr5++){
							for (int incr6=0; incr6<6; incr6++){
								for (int incr7=0; incr7<6; incr7++){
									for (int incr8=0; incr8<6; incr8++){
										for (int incr9=0; incr9<6; incr9++){
											for (int incr10=0; incr10<6; incr10++){
												for (int incr11=0; incr11<6; incr11++){
													for (int incr12=0; incr12<6; incr12++){
														for (int incr13=0; incr13<6; incr13++){
															List<Double> coeff = new ArrayList<>();
															coeff.add(incr1 * 0.2);
															coeff.add(incr2 * 0.2);
															coeff.add(incr3 * 0.2);
															coeff.add(incr4 * 0.2);
															coeff.add(incr5 * 0.2);
															coeff.add(incr6 * 0.2);
															coeff.add(incr7 * 0.2);
															coeff.add(incr8 * 0.2);
															coeff.add(incr9 * 0.2);												
															coeff.add(incr10 * 0.2);
															coeff.add(incr11 * 0.2);
															coeff.add(incr12 * 0.2);
															coeff.add(incr13 * 0.2);
															// Fill deltaValuesToTry
															List<Double> currentDeltaValues = new ArrayList<>();
															for (int i=0; i<startingDeltaValues.size(); i++){

																double startingValue = startingDeltaValues.get(i);
																double newValue = startingValue + coeff.get(i) * Math.PI / (1.0 * n);
																currentDeltaValues.add(newValue);
															}
															// Fill as a consequence the values
															List<Double> currentValues = new ArrayList<>();
															for (Double value: startingValues){
																currentValues.add(value);
															}

															for (int i=0; i<startingDeltaValues.size(); i++){
																int rankLeft = i+1;
																int rankRight = n-rankLeft;
																//System.out.println("changing values at index " + rankLeft + "  " +  rankRight);
																double startingValueLeft = startingValues.get(rankLeft);
																double startingValueRight = startingValues.get(rankRight);
																double newValueLeft = startingValueLeft + currentDeltaValues.get(i);
																double newValueRight = startingValueRight - currentDeltaValues.get(i);

																currentValues.set(rankLeft, newValueLeft);
																currentValues.set(rankRight, newValueRight);
															}

															//System.out.println("there values should be set");
															double cost = computeCostToMinimize(currentValues);
															if (cost < minRmsd){
																minRmsd = cost;
																valuesAtMin = currentValues;
																System.out.println("new min " + minRmsd);
																printValues(valuesAtMin);
															}
															//System.out.println("cost = " + cost);
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return optimizeDeltas;
	}



	private static void printValues(List<Double> valuesToPrint){
		for (Double value: valuesToPrint){
			System.out.println(value + " ");
		}
	}


	private static double computeCostToMinimize(List<Double> values){

		int n = values.size();


		double sumSectorLenght = 0.0;

		double[] sectorLengthArray = new double[n-1];

		for (int i=0; i<n-1; i++){
			double sectorLength = computeSectorLength(values.get(i), values.get(i+1) - values.get(i));
			sectorLengthArray[i] = sectorLength;
			//System.out.println(sectorLength);
			sumSectorLenght += sectorLength;
		}

		double averageSectorLenght = sumSectorLenght / (n-1);

		double rmsd = 0.0;
		for (int i=0; i<n-1; i++){
			rmsd += (sectorLengthArray[i] - averageSectorLenght) * (sectorLengthArray[i] - averageSectorLenght);
		}
		rmsd = Math.sqrt(rmsd) / n; 

		//System.out.println("sumSectorLenght = " + sumSectorLenght);
		//System.out.println("averageSectorLenght = " + averageSectorLenght);
		// System.out.println("rmsd = " + rmsd);
		return rmsd;

	}



	private static double computeRmsdSectorLength(double[] deltaTheta){

		int n = deltaTheta.length;

		double sumSectorLenght = 0.0;

		for (int i=0; i<n-1; i++){
			double sectorLength = computeSectorLength(deltaTheta[i], deltaTheta[i+1] - deltaTheta[i]);
			System.out.println(sectorLength);
			sumSectorLenght += sectorLength;
		}

		System.out.println("sumSectorLenght = " + sumSectorLenght);
		return sumSectorLenght;
	}



	private static double computeSectorLength(double theta, double deltaTheta){

		return Math.cos(theta)*(1.0 - Math.cos(deltaTheta)) + Math.sin(theta)*Math.sin(deltaTheta);
	}
}
