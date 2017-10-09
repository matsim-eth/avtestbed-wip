package playground.dziemke.cemdapMatsimCadyts;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Population;
import org.matsim.testcases.MatsimTestUtils;
import playground.dziemke.accessibility.OTPMatrix.CSVReader;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

/**
 * @author GabrielT on 15.11.2016.
 */
public class DemandGeneratorCensusTest {

	@Rule
	public MatsimTestUtils utils = new MatsimTestUtils();

	@Test
	public void TestGenerateDemand() {

		// Input and output files
		String commuterFileOutgoingTest = utils.getInputDirectory() + "Teil1BR2009Ga_Test_kurz.txt";
		String censusFile = utils.getInputDirectory() + "Zensus11_Datensatz_Bevoelkerung_BE_BB.csv";
		String shapeFileLors = utils.getInputDirectory() + "Bezirksregion_EPSG_25833.shp";

		String[] commuterFilesOutgoing = {commuterFileOutgoingTest};

		// Parameters
		int numberOfPlansPerPerson = 1;
		String planningAreaId = "11000000"; // "Amtliche Gemeindeschlüssel (AGS)" of Berlin is "11000000"
		double defaultAdultsToEmployeesRatio = 1.23;  // Calibrated based on sum value from Zensus 2011.
		double defaultEmployeesToCommutersRatio = 2.5;  // This is an assumption, oriented on observed values, deliberately chosen slightly too high.
		boolean writeMatsimPlanFiles = false;
		boolean includeChildren = false;

		DemandGeneratorCensus demandGeneratorCensus = new DemandGeneratorCensus(commuterFilesOutgoing, censusFile,
				shapeFileLors, utils.getOutputDirectory(), 	numberOfPlansPerPerson, planningAreaId,
				defaultAdultsToEmployeesRatio, defaultEmployeesToCommutersRatio, writeMatsimPlanFiles, includeChildren);

		String municipal = "Breydin";
		ArrayList<String> possibleLocationsOfWork = readPossibleLocationsOfWork(commuterFileOutgoingTest, municipal);

		String[] municipalLine = getCensusDataLine(censusFile, municipal);

		int male18_24Ref = parseInt(municipalLine[85]);
		int female18_24Ref = parseInt(municipalLine[86]);
		int male25_29Ref = parseInt(municipalLine[88]);
		int female25_29Ref = parseInt(municipalLine[89]);
		int male30_39Ref = parseInt(municipalLine[91]);
		int female30_39Ref = parseInt(municipalLine[92]);
		int male40_49Ref = parseInt(municipalLine[94]);
		int female40_49Ref = parseInt(municipalLine[95]);
		int male50_64Ref = parseInt(municipalLine[97]);
		int female50_64Ref = parseInt(municipalLine[98]);
		int male65_74Ref = parseInt(municipalLine[100]);
		int female65_74Ref = parseInt(municipalLine[101]);
		int male75PlusRef = parseInt(municipalLine[103]);
		int female75PlusRef = parseInt(municipalLine[104]);

		int male18_24 = 0;
		int female18_24 = 0;
		int male25_29 = 0;
		int female25_29 = 0;
		int male30_39 = 0;
		int female30_39 = 0;
		int male40_49 = 0;
		int female40_49 = 0;
		int male50_64 = 0;
		int female50_64 = 0;
		int male65_74 = 0;
		int female65_74 = 0;
		int male75Plus = 0;
		int female75Plus = 0;

		Population pop = demandGeneratorCensus.getPopulation();
		for (Person person : pop.getPersons().values()) {
			//collect data
			String locationOfWork = (String) person.getAttributes().getAttribute("locationOfWork");
			boolean employed = (boolean) person.getAttributes().getAttribute("employed");
			int age = (Integer) person.getAttributes().getAttribute("age");
			int female = (Integer) person.getAttributes().getAttribute("gender"); // assumes that female = 1

			//assert
//			Assert.assertEquals("Wrong municipality", "12060034", householdId.toString().substring(0,8));
			if (!employed) {
				Assert.assertEquals("Wrong locationOfWork", "-99", locationOfWork);
			} else if (locationOfWork.length() != 6) {
				Assert.assertTrue("Wrong locationOfWork", possibleLocationsOfWork.contains(locationOfWork));
			}
			if (female == 0) {
				if (isBetween(age, 18, 24)) male18_24++;
				if (isBetween(age, 25, 29)) male25_29++;
				if (isBetween(age, 30, 39)) male30_39++;
				if (isBetween(age, 40, 49)) male40_49++;
				if (isBetween(age, 50, 64)) male50_64++;
				if (isBetween(age, 65, 74)) male65_74++;
				if (age > 74) male75Plus++;
			} else if (female == 1){
				if (isBetween(age, 18, 24)) female18_24++;
				if (isBetween(age, 25, 29)) female25_29++;
				if (isBetween(age, 30, 39)) female30_39++;
				if (isBetween(age, 40, 49)) female40_49++;
				if (isBetween(age, 50, 64)) female50_64++;
				if (isBetween(age, 65, 74)) female65_74++;
				if (age > 74) female75Plus++;
			} else Assert.fail("Wrong gender");
		}

		//System.out.println("Persons size: " + pop.getPersons().values().size());

		Assert.assertEquals("Wrong male18_24 count", male18_24Ref, male18_24);
		Assert.assertEquals("Wrong male25_29 count", male25_29Ref, male25_29);
		Assert.assertEquals("Wrong male30_39 count", male30_39Ref, male30_39);
		Assert.assertEquals("Wrong male40_49 count", male40_49Ref, male40_49);
		Assert.assertEquals("Wrong male50_64 count", male50_64Ref, male50_64);
		Assert.assertEquals("Wrong male75Plus count", male75PlusRef, male75Plus);
		Assert.assertEquals("Wrong female18_24 count", female18_24Ref, female18_24);
		Assert.assertEquals("Wrong female25_29 count", female25_29Ref, female25_29);
		Assert.assertEquals("Wrong female30_39 count", female30_39Ref, female30_39);
		Assert.assertEquals("Wrong female40_49 count", female40_49Ref, female40_49);
		Assert.assertEquals("Wrong female50_64 count", female50_64Ref, female50_64);
		Assert.assertEquals("Wrong female65_74 count", female65_74Ref, female65_74);
		Assert.assertEquals("Wrong female75Plus count", female75PlusRef, female75Plus);
		Assert.assertEquals("Wrong male65_74 count", male65_74Ref, male65_74);

		Assert.assertTrue("", new File(utils.getOutputDirectory() + "persons.dat").exists());

	}

	private boolean isBetween(int x, int lower, int upper) {
		return lower <= x && x <= upper;
	}

	private ArrayList<String> readPossibleLocationsOfWork(String commuterFileOutgoingTest, String municipal) {
		ArrayList<String> result = new ArrayList<>();

		CSVReader reader = new CSVReader(commuterFileOutgoingTest, "\t");
		String[] line = reader.readLine();
		while (line.length < 2 || !Objects.equals(line[1], municipal)) line = reader.readLine();
		line = reader.readLine();
		while (Objects.equals(line[0], "")) {
			if (line[2].length() == 8) {
				result.add(line[2]);
			}
			line = reader.readLine();
		}
		return result;
	}

	private int parseInt(String value) {
		if (value.startsWith("(")) {
			value = value.substring(1, value.length()-1);
		}
		return Integer.parseInt(value);
	}

	private String[] getCensusDataLine(String censusFile, String municipal) {
		CSVReader reader = new CSVReader(censusFile, ";");
		String[] line = reader.readLine();
		while (!Objects.equals(line[6], municipal)) {
			line = reader.readLine();
		}
		return line;
	}

}
