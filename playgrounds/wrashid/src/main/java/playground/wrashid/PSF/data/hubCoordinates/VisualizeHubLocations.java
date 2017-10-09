package playground.wrashid.PSF.data.hubCoordinates;

import org.matsim.api.core.v01.Coord;
import org.matsim.contrib.parking.parkingchoice.lib.GeneralLib;
import org.matsim.contrib.parking.parkingchoice.lib.obj.Matrix;

import playground.wrashid.lib.tools.kml.BasicPointVisualizer;
import playground.wrashid.lib.tools.kml.Color;

public class VisualizeHubLocations {

	public static void main(String[] args) {
		Matrix matrix=GeneralLib.readStringMatrix("A:/data/ewz daten/GIS_coordinates_of_managers.txt");
		BasicPointVisualizer visualizer=new BasicPointVisualizer();
		
		for (int i=0;i<matrix.getNumberOfRows();i++){
			Coord coord= new Coord(matrix.getDouble(i, 1), matrix.getDouble(i, 2));
			visualizer.addPointCoordinate(coord, Long.toString(matrix.convertDoubleToInteger(i, 0)), Color.RED);
		}
		
		visualizer.write("A:/data/ewz daten/GIS_coordinates_of_managers.kml");
	}
	
}
