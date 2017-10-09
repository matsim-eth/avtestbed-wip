package playground.gregor.misanthrope.simulation.physics;

import be.humphreys.simplevoronoi.GraphEdge;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.apache.log4j.Logger;
import org.matsim.core.api.experimental.events.EventsManager;
import playground.gregor.misanthrope.run.CTRunner;
import playground.gregor.misanthrope.simulation.CTEvent;
import playground.gregor.sim2d_v4.cgal.LineSegment;
import playground.gregor.sim2d_v4.events.debug.LineEvent;
import playground.gregor.sim2d_v4.events.debug.PolygonEvent;

import java.util.*;

public abstract class CTCell {


    public static final double MAX_CELL_WIDTH = 10;
    public static final double RHO_M = 4.97;
    public static final double V_0 = 1.14;
    public static final double GAMMA = 0.55;
    public static final double P0 = 0.5;
    protected static final Logger log = Logger.getLogger(CTCell.class);
    public static double MIN_CELL_WIDTH;
    private static int ID = 0;
    private static double Q;

    static {
        MIN_CELL_WIDTH = 2 * Math.sqrt((V_0 / GAMMA + 1) / (1.5 * Math.sqrt(3) * RHO_M));
    }

    static {
        Q = (V_0 * RHO_M) / (V_0 / GAMMA + 1);
    }

    protected final CTNetwork net;
    //	protected final EventsManager em;
    protected final double width;
    protected final CTNetworkEntity parent;
    final int id;
    private final List<CTCellFace> faces = new ArrayList<>();
    private final List<GraphEdge> ge = new ArrayList<>();
    //	private final HashSet<CTPed> peds = new HashSet<>();
//	private final Map<Double,LinkedList<CTPed>> pop = new ArrayMap<>();//TODO is this faster than HashMap?
    private final Set<CTCell> neighbors = new HashSet<>();
    protected CTPed next = null;
    protected double nextCellJumpTime;
    protected CTEvent currentEvent = null;
    protected int n = 0; //nr peds
    int r = 0;
    int g = 0;
    int b = 0;
    private double alpha; //area
    private double rho; //current density
    private double x;
    private double y;
    private int N; //max number of peds

    public CTCell(double x, double y, CTNetwork net, CTNetworkEntity parent, double width, double area) {
        this.x = x;
        this.y = y;
        this.net = net;
        this.parent = parent;
        this.width = width;
        this.setArea(area);
        this.id = ID++;
//		pop.put(Math.PI/6., new LinkedList<CTPed>());
//		pop.put(Math.PI/2., new LinkedList<CTPed>());
//		pop.put(5*Math.PI/6., new LinkedList<CTPed>());
//		pop.put(-5*Math.PI/6., new LinkedList<CTPed>());
//		pop.put(-Math.PI/2., new LinkedList<CTPed>());
//		pop.put(-Math.PI/6., new LinkedList<CTPed>());
    }

    public CTNetwork getNet() {
        return net;
    }

    public void setArea(double a) {
        this.alpha = a;
        this.N = (int) (RHO_M * this.getAlpha() + 0.5);
    }

    public double getAlpha() {
        return this.alpha;
    }

    public synchronized void addFace(CTCellFace face) {
        faces.add(face);
        getNeighbors().add(face.nb);
        face.nb.addNeighbor(this);
    }

    public Set<CTCell> getNeighbors() {
        return neighbors;
    }

    public synchronized void addNeighbor(CTCell nb) {
        this.getNeighbors().add(nb);
    }

    public void debug(EventsManager em) {
        if (!CTRunner.DEBUG) {
            return;
        }
        for (CTCellFace f : faces) {
            debug(f, em);
        }
    }

    public void debugFill(EventsManager em, int r, int g, int b, int a) {
        GeometryFactory fac = new GeometryFactory();
        Optional<Geometry> mp = ge.parallelStream().map(f -> (Geometry) fac.createMultiPoint(new Coordinate[]{new Coordinate(f.x1, f.y1), new Coordinate(f.x2, f.y2)})).reduce(Geometry::union);
        if (mp.isPresent()) {
            Geometry hull = mp.get().convexHull().buffer(-0.1);
            Coordinate[] coords = hull.getCoordinates();
            PolygonEvent p = new PolygonEvent(0, coords, r, g, b, a, true);
            em.processEvent(p);
        }
    }


    private void debug(CTCellFace f, EventsManager em) {

        if (!CTRunner.DEBUG) {
            return;
        }


        {
            LineSegment s = new LineSegment();
            s.x0 = f.x0;
            s.y0 = f.y0;
            s.x1 = f.x1;
            s.y1 = f.y1;
            LineEvent le = new LineEvent(0, s, true, r, g, b, 255, 0);
            em.processEvent(le);
        }
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public int getN() {
        return this.N;
    }

    public CTNetworkEntity getParent() {
        return this.parent;
    }


    public void jumpAndUpdateNeighbors(double now) {

        if (next == null) {
            log.error("next is null");
        }
        CTCell nb = next.getNextCellAndJump(now);
        next = null;
        this.nextCellJumpTime = Double.NaN;


        Set<CTCell> affectedCells = new HashSet<>();
        for (CTCell ctCell : this.getNeighbors()) {
            affectedCells.add(ctCell);
        }
        for (CTCell ctCell : nb.getNeighbors()) {
            affectedCells.add(ctCell);
        }
        for (CTCell cell : affectedCells) {
            cell.updateIntendedCellJumpTimeAndChooseNextJumper(now);
        }
    }

    public abstract void updateIntendedCellJumpTimeAndChooseNextJumper(double now);

    protected double chooseNextCellAndReturnJ(CTPed ped) {


        CTCell bestNB = null;
        double maxFJ = 0;
        double maxJ = Double.NaN;
        for (CTCellFace face : this.getFaces()) {

            double fDir = face.h_i;
            double f = getFHHi(ped, face);
            double j = this.getJ(face.nb, ped);
            double fJ = f * j;
            if (fJ > maxFJ) {
                maxJ = j;
                maxFJ = fJ;
                bestNB = face.nb;

            }
        }
        if (bestNB == null) {
            return Double.NaN;
        }
        ped.setTentativeNextCell(bestNB);

        return maxJ;
    }

    abstract double getFHHi(CTPed ped, CTCellFace face);

    public double getJ(CTCell n_i, CTPed ped) { //flow to cell n_i
        double demand = getDelta(ped);
        double supply = n_i.getSigma(ped);
        return width * Math.min(demand, supply) * 1.5;
    }

    private double getDelta(CTPed ped) { //demand function
        double coeff = getDirCoeff(ped);
        return Math.min(coeff * Q, V_0 * this.getRho());
    }

    public double getRho() { //current density
        return this.rho;
    }

    public void setRho(double rho) {
        this.rho = rho;
    }

    private double getSigma(CTPed ped) { //supply function

        double coeff = getDirCoeff(ped);
        return Math.min(coeff * Q, GAMMA * (RHO_M - this.getRho()));
    }

    private double getDirCoeff(CTPed ped) {
        double ph = getDirectionalProportion(ped);
        return P0 + (1. - P0) * ph;
    }

    public List<CTCellFace> getFaces() {
        return faces;
    }

    abstract void jumpOffPed(CTPed ctPed, double time);

    public abstract boolean jumpOnPed(CTPed ctPed, double time);

    abstract HashSet<CTPed> getPeds();

    abstract double getDirectionalProportion(CTPed ped);

    public void addGe(GraphEdge e) {
        this.ge.add(e);
    }

    public double getWidth() {
        return width;
    }
}
