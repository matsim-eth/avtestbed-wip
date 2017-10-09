package playground.gleich.av_bus;

public class FilePaths {
	
	public final static String PATH_BASE_DIRECTORY = "../../../../../shared-svn/studies/gleich/av-bus_berlinNW/"; // 3 Levels to Workspace -> runs-svn (partly checked-out -> looks up local copy)
//	public final static String PATH_BASE_DIRECTORY = "C:/Users/gleich/av_bus berlin/";
//	public final static String PATH_BASE_DIRECTORY = "dat/Uni/av_bus berlin/";
	
	/** 10pct Scenario Unmodified */
	public final static String PATH_NETWORK_BERLIN__10PCT = PATH_BASE_DIRECTORY + "data/input/Berlin10pct/network.final10pct.xml.gz";
	public final static String PATH_POPULATION_BERLIN__10PCT_UNFILTERED = PATH_BASE_DIRECTORY + 
			"data/input/Berlin10pct/population.10pct.unfiltered.base.xml.gz";
	public final static String PATH_TRANSIT_SCHEDULE_BERLIN__10PCT = PATH_BASE_DIRECTORY + "data/input/Berlin10pct/transitSchedule.xml.gz";
	public final static String PATH_TRANSIT_VEHICLES_BERLIN__10PCT = PATH_BASE_DIRECTORY + "data/input/Berlin10pct/transitVehicles.final.xml";
	/** 10pct Scenario Modified */
	public final static String PATH_POPULATION_BERLIN__10PCT_FILTERED_TEST = PATH_BASE_DIRECTORY + 
			"data/input/Berlin10pct/mod/population.10pct.filtered.test.xml.gz";
	
	/** 100pct Scenario Unmodified */
	public final static String PATH_NETWORK_BERLIN_100PCT = PATH_BASE_DIRECTORY + "data/input/Berlin100pct/network.final.xml.gz";
	public final static String PATH_POPULATION_BERLIN_100PCT_UNFILTERED = PATH_BASE_DIRECTORY + 
			"data/input/Berlin100pct/population.100pct.unfiltered.base.xml.gz";
	public final static String PATH_TRANSIT_SCHEDULE_BERLIN_100PCT = PATH_BASE_DIRECTORY + "data/input/Berlin100pct/transitSchedule.xml.gz";
	public final static String PATH_TRANSIT_VEHICLES_BERLIN_100PCT = PATH_BASE_DIRECTORY + "data/input/Berlin100pct/transitVehicles.final.xml.gz";
	/** 100pct Scenario Modified */
	public final static String PATH_POPULATION_BERLIN_100PCT_FILTERED = PATH_BASE_DIRECTORY + 
			"data/input/Berlin100pct/mod/population.100pct.filtered.xml.gz";
	public final static String PATH_NETWORK_BERLIN_100PCT_ENCLOSED_IN_AREA = PATH_BASE_DIRECTORY + 
			"data/input/Berlin100pct/mod/network.enclosedInStudyArea.xml.gz";
	public final static String PATH_BERLIN_100PCT_LINKS_ENCLOSED_IN_AREA = PATH_BASE_DIRECTORY + 
			"data/input/Berlin100pct/mod/linksInArea.csv";
	public final static String PATH_BERLIN_100PCT_SHP_LINKS_ENCLOSED_IN_AREA = PATH_BASE_DIRECTORY + 
			"data/input/Berlin100pct/mod/linksInArea.shp";
	public final static String PATH_TRANSIT_SCHEDULE_BERLIN_100PCT_WITHOUT_BUSES_IN_STUDY_AREA = PATH_BASE_DIRECTORY + "data/input/Berlin100pct/mod/transitSchedule.xml";
	public final static String PATH_TRANSIT_VEHICLES_BERLIN_100PCT_45MPS = PATH_BASE_DIRECTORY + "data/input/Berlin100pct/mod/transitVehicles.45mps.xml";
	/** 100pct Scenario Output */
	public final static String PATH_OUTPUT_BERLIN_100PCT_MODIFIED_TRANSIT_SCHEDULE_TEST = PATH_BASE_DIRECTORY + "data/output/test/modified_transitSchedule";
	
	/** Study area */
	public final static String PATH_STUDY_AREA_SHP = PATH_BASE_DIRECTORY + "data/input/Untersuchungsraum shp/study_area.shp";
	public final static String STUDY_AREA_SHP_KEY = "id"; // name of the key column
	public final static String STUDY_AREA_SHP_ELEMENT = "1"; // key for the element containing the study area

}
