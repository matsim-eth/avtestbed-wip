package playground.dhosse.gap;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.matsim.api.core.v01.TransportMode;
import org.matsim.core.utils.collections.Tuple;
import org.matsim.matrices.Matrix;

import playground.wisinee.IPF.Ipf;

public class GAPMatrices {
	
	private static Matrix distances = new Matrix("dij", "distance matrix");

	public static Map<String, Matrix> run(){
		
		Map<String, Matrix> map = new TreeMap<>();
		
		Map<String, Tuple<double[], double[]>> actType2ODs = new HashMap<>();
		
		double[] cWA = new double[]{293,592,345,203,869,5706,833,340,453,1756,2853,1172,679,753,260,393,127,538,187,689,326,313};
		double[] rWA = new double[]{162,275,274,275,544,8154,571,160,490,1191,4173,1188,492,390,60,283,52,265,66,294,153,169};
		actType2ODs.put("WA", new Tuple<double[], double[]>(cWA, rWA));
		
		double[] cWB = new double[]{105,277,167,64,394,2321,311,174,175,606,1242,552,327,370,128,194,69,301,94,361,182,164};
		double[] rWB = new double[]{40,130,69,499,161,3748,120,120,0,202,2093,409,273,158,0,70,0,0,0,276,82,129};
		actType2ODs.put("WB", new Tuple<double[], double[]>(cWB, rWB));
		
		double[] cWE = new double[]{187,404,248,127,590,4152,574,229,310,1205,1952,833,480,520,188,266,98,400,120,471,234,223};
		double[] rWE = new double[]{113,193,193,193,381,5723,401,112,344,835,2928,834,345,274,43,199,36,186,46,206,107,118};
		actType2ODs.put("WE", new Tuple<double[], double[]>(cWE, rWE));
		
		double[] cWF = new double[]{398,859,527,269,1254,8824,1220,486,659,2560,4147,1769,1020,1105,399,566,207,850,254,1000,497,473};
		double[] rWF = new double[]{242,409,409,410,810,12158,851,239,731,1775,6221,1772,734,582,91,422,78,396,99,438,229,252};
		actType2ODs.put("WF", new Tuple<double[], double[]>(cWF, rWF));
		
		double[] cWS = new double[]{304,657,403,206,959,6748,933,372,504,1958,3171,1353,780,845,305,433,159,650,194,765,380,362};
		double[] rWS = new double[]{245,486,358,260,791,8013,793,278,531,1660,3958,1354,672,647,188,378,109,478,136,551,278,278};
		actType2ODs.put("WS", new Tuple<double[], double[]>(cWS, rWS));
		
		double[] rAS = new double[]{819,1768,1085,554,2582,18167,2512,1001,1356,5271,8539,3643,2100,2275,821,1165,427,1751,523,2059,1023,974};
		double[] cAS = new double[]{658,1305,963,700,2125,21598,2132,746,1431,4463,10672,3645,1806,1737,504,1016,294,1282,364,1481,747,745};
		actType2ODs.put("AS", new Tuple<double[], double[]>(cAS, rAS));
		
		double[] rBW = new double[]{42,112,67,26,159,936,125,70,71,245,501,223,132,149,52,78,28,122,38,146,74,66};
		double[] cBW = new double[]{16,53,28,201,65,1512,49,49,0,82,844,165,111,64,0,28,0,0,0,112,33,52};
		actType2ODs.put("BW", new Tuple<double[], double[]>(cBW, rBW));
		
		double[] rEW = new double[]{117,253,155,79,369,2595,359,143,194,753,1220,520,300,325,117,166,61,250,75,294,146,139};
		double[] cEW = new double[]{72,120,120,120,238,3576,250,71,215,522,1829,521,216,171,27,124,23,116,29,129,68,73};
		actType2ODs.put("EW", new Tuple<double[], double[]>(cEW, rEW));
		
		double[] rFW = new double[]{164,354,217,111,516,3633,502,200,271,1054,1708,729,420,455,164,233,85,350,105,412,205,195};
		double[] cFW = new double[]{100,168,168,168,334,5007,350,99,301,731,2562,729,302,239,37,174,32,163,41,181,94,104};
		actType2ODs.put("FW", new Tuple<double[], double[]>(cFW, rFW));
		
		double[] rSW = new double[]{222,480,295,150,701,4931,682,272,368,1431,2318,989,570,618,223,316,116,475,142,559,278,264};
		double[] cSW = new double[]{179,355,262,190,578,5855,580,203,388,1213,2892,990,491,473,138,276,80,349,99,403,203,203};
		actType2ODs.put("SW", new Tuple<double[], double[]>(cSW, rSW));
		
		double[] rSA = new double[]{105,227,140,71,332,2336,323,129,174,678,1098,468,270,293,106,150,55,225,67,265,131,125};
		double[] cSA = new double[]{64,108,108,108,215,3218,225,64,194,470,1648,469,194,154,24,112,21,105,26,117,60,67};
		actType2ODs.put("SA", new Tuple<double[], double[]>(cSA, rSA));
		
		double[] cSS = new double[]{889,1761,1301,1486,2869,37146,2879,1007,1970,6026,19214,4923,2439,2344,680,1372,396,1731,490,1998,1008,1006};
		double[] rSS = new double[]{1274,2798,1600,945,3954,29180,3775,1569,1932,7934,14420,4993,3066,3570,1396,1758,701,2839,881,3238,1624,1488};
		actType2ODs.put("SS", new Tuple<double[], double[]>(cSS, rSS));
		
		//distance matrix
		double[][] dij = new double[][]{
						{5300,6600,28600,19300,28900,33900,42500,28800,49600,50400,19700,15600,24900,27000,23000,5300,32100,19900,24000,24900,11200,51400},
						{6600,2400,22100,16400,25900,31000,39600,22300,46600,47400,13200,12600,22000,20600,16500,2400,27600,13500,17500,17600,8200,48400},
						{28600,22100,6000,11800,10200,15200,23800,14300,30900,31700,10100,16400,6000,6600,13900,25700,6300,12100,15600,16200,20000,32700},
						{19300,16400,11800,4700,9600,14700,23300,25000,30300,31100,21300,4700,5600,17700,25100,14100,12800,23200,26800,27300,8300,32100},
						{28900,25900,10200,9600,4700,4800,13000,23400,20400,21200,19600,14200,4700,16100,23500,23600,12100,21600,25100,25700,17800,22200},
						{33900,31000,15200,14700,4800,4800,9600,28900,17100,17900,25100,19700,10100,21600,28900,29000,17600,27100,30600,31200,23300,18900},
						{42500,39600,23800,23300,13000,9600,9600,37200,26100,26900,33400,28000,18400,29900,37200,37300,25900,35400,38900,39500,31600,27900},
						{28800,22300,14300,25000,23400,28900,37200,8000,31600,45600,9400,30300,19900,8000,13200,24600,19700,12000,20500,16200,33900,29800},
						{49600,46600,30900,30300,20400,17100,26100,31600,2000,8500,40000,34500,25000,36400,43800,43900,32400,41900,45500,46100,38200,2000},
						{50400,47400,31700,31100,21200,17900,26900,45600,8500,8500,41200,35800,26300,37700,45000,45200,33700,43200,46700,47300,39400,10000},
						{19700,13200,10100,21300,19600,25100,33400,9400,40000,41200,2800,26200,15800,7500,3900,15700,15700,2800,5500,6900,21600,42500},
						{15600,12600,16400,4700,14200,19700,28000,30300,34500,35800,26200,4600,10300,22400,29700,10300,17500,25900,31400,30100,4600,36800},
						{24900,22000,6000,5600,4700,10100,18400,19900,25000,26300,15800,10300,4700,12000,19400,19600,7700,17500,21100,21700,13900,27200},
						{27000,20600,6600,17700,16100,21600,29900,8000,36400,37700,7500,22400,12000,6600,11400,22700,12000,10200,13900,14300,26000,38700},
						{23000,16500,13900,25100,23500,28900,37200,13200,43800,45000,3900,29700,19400,11400,3900,18700,19900,5800,6400,12600,24600,46700},
						{5300,2400,25700,14100,23600,29000,37300,24600,43900,45200,15700,10300,19600,22700,18700,2400,26800,15700,19700,19800,5900,46100},
						{32100,27600,6300,12800,12100,17600,25900,19700,32400,33700,15700,17500,7700,12000,19900,26800,6300,17400,21000,21600,21100,34900},
						{19900,13500,12100,23200,21600,27100,35400,12000,41900,43200,2800,25900,17500,10200,5800,15700,17400,2800,5000,4700,21500,44200},
						{24000,17500,15600,26800,25100,30600,38900,20500,45500,46700,5500,31400,21100,13900,6400,19700,21000,5000,5000,5700,25600,47700},
						{24900,17600,16200,27300,25700,31200,39500,16200,46100,47300,6900,30100,21700,14300,12600,19800,21600,4700,5700,4700,25600,48300},
						{11200,8200,20000,8300,17800,23300,31600,33900,38200,39400,21600,4600,13900,26000,24600,5900,21100,21500,25600,25600,4600,40800},
						{51400,48400,32700,32100,22200,18900,27900,29800,2000,10000,42500,36800,27200,38700,46700,46100,34900,44200,47700,48300,40800,2000}
		};
		
		for(int i = 0; i < dij.length; i++){
			
			for(int j = 0; j < dij.length; j++){
				
				distances.createEntry(getId(i), getId(j), dij[i][j]);
				
			}
			
		}
		
		for(Entry<String, Tuple<double[],double[]>> entry : actType2ODs.entrySet()){
			
			System.out.println(entry.getKey());
			
			double[] o = entry.getValue().getFirst();
			double[] d = entry.getValue().getSecond();
			
			double[][] rij = new double[o.length][d.length];
			double[][] rijWalk = new double[o.length][d.length];
			
			for(int i = 0; i < rij.length; i++){
				
				for(int j = 0; j < rij.length; j++){
					
					double factor = i == j ? 0.3 : 1.0;
					
					rij[i][j] = o[i] * d[j] / (dij[i][j] * factor);
					rijWalk[i][j] = o[i] * d[j] / (dij[i][j] * factor);
					
				}
				
			}
			
			Ipf ipf = new Ipf();
			ipf.setFixColumn(cWB, 0);
			ipf.setFixRow(rWB, 0);
			ipf.setInitialMatrix(rij, 1, 0);
			
			double[][] result = ipf.ipfcal(o.length, d.length, 1, 100);
			
			Matrix m = createMatrixFromResultArray(entry.getKey(), result);
			map.put(m.getId(), m);
			
			Ipf ipfWalk = new Ipf();
			ipfWalk.setFixColumn(cWB, 0);
			ipfWalk.setFixRow(rWB, 0);
			ipfWalk.setInitialMatrix(rijWalk, 1, 0);
			
			double[][] resultWalk = ipfWalk.ipfcal(o.length, d.length, 1, 100);
			
			Matrix mWalk = createMatrixFromResultArray(entry.getKey() + "_" + TransportMode.walk, resultWalk);
			map.put(mWalk.getId(), mWalk);
			
		}
		
		return map;
						
	}

	private static Matrix createMatrixFromResultArray(String key, double[][] result) {
		
		Matrix m = new Matrix(key, "");
		
		for(int i = 0; i < result.length; i++){
			
			String fromId = getId(i);
			
			for(int j = 0; j < result.length; j++){
				
				String toId = getId(j);
				
				double v = result[i][j];
				m.createEntry(fromId, toId, v);
				
			}
			
		}
		
		return m;
		
	}
	
	private static String getId(int i){
		
		switch(i){
		case 0: return Global.idBadBayersoien;
		case 1: return Global.idBadKohlgrub;
		case 2: return Global.idEschenlohe;
		case 3: return Global.idEttal;
		case 4: return Global.idFarchant;
		case 5: return Global.idGarmischPartenkirchen;
		case 6: return Global.idGrainau;
		case 7: return Global.idGroßweil;
		case 8: return Global.idKrün;
		case 9: return Global.idMittenwald;
		case 10: return Global.idMurnau;
		case 11: return Global.idOberammergau;
		case 12: return Global.idOberau;
		case 13: return Global.idOhlstadt;
		case 14: return Global.idRiegsee;
		case 15: return Global.idSaulgrub;
		case 16: return Global.idSchwaigen;
		case 17: return Global.idSeehausen;
		case 18: return Global.idSpatzenhausen;
		case 19: return Global.idUffing;
		case 20: return Global.idUnterammergau;
		case 21: return Global.idWallgau;
		default: return null;
		}
		
	}

	public static Matrix getDistances() {
		return distances;
	}
	
}
