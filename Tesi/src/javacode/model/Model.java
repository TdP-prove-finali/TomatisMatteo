package javacode.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import Jama.Matrix;
import javacode.db.WildfiresDAO;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class Model {
    
	WildfiresDAO dao;
	LinearRegression regr;
	
    Graph<Pixel, DefaultEdge> graph;
	private List<List<Pixel>> pixelMap;
	
	Matrix variablesCoefficients4Mean; //matrix containing the coefficient compute with the multiple linear regression method
	Matrix variableCoefficients4Error; //matrix containing the coefficient for the standard error
	double totalBurnedArea = 0; //km2 of burned area for patches
	double standardError = 0;
	double vegDensity; //density of vegetation
	NormalDistribution distribution;
	
	double burned_area;

	PriorityQueue<Pixel> queue;
	Map<Double, Pixel> distances;

	int numberOfCC = 0;
	float maxCCsize = 0;
	
	public Model() {
		dao = new WildfiresDAO();
		regr = new LinearRegression();

		this.vegDensity = dao.getVegDensity();
	}

	/*
	 * Draw the map of the forest & the graph
	 * 
	 */
	public WritableImage getMap() {
		int larghezzaImmagine = 600;
        int altezzaImmagine = 600;
        int dimensioneQuadrato = 4;
        int numeroRighe = altezzaImmagine / dimensioneQuadrato;
        int numeroColonne = larghezzaImmagine / dimensioneQuadrato;

        WritableImage writableImage = new WritableImage(larghezzaImmagine, altezzaImmagine);
        PixelWriter pixelWriter = writableImage.getPixelWriter();
        
        this.pixelMap = new ArrayList<List<Pixel>>();

        for (int riga = 1; riga < numeroRighe-1; riga++) {
        	List<Pixel> pixelList = new ArrayList<>();
            for (int colonna = 1; colonna < numeroColonne-1; colonna++) {
				double prob = Math.random();
				if(prob>this.vegDensity) {
					pixelList.add(new Pixel(riga, colonna, false));
                	Color colore = Color.WHITE;
                	disegnaQuadrato(pixelWriter, riga * dimensioneQuadrato, colonna * dimensioneQuadrato, dimensioneQuadrato, colore);
				} else{
					pixelList.add(new Pixel(riga, colonna, true));
                	Color colore = (riga + colonna) % 2 == 0 ? Color.GREEN : Color.DARKGREEN;
                	disegnaQuadrato(pixelWriter, riga * dimensioneQuadrato, colonna * dimensioneQuadrato, dimensioneQuadrato, colore);
				}
            }
			this.pixelMap.add(pixelList);
        }

		buildGraph();

		return writableImage;
	}
	
	/*
	 * Build the graph
	 * 
	 */
	public void buildGraph() {
		
		this.graph = new DefaultDirectedGraph<>(DefaultEdge.class);

		// Add intersection vertices
        for (List<Pixel> row : this.pixelMap) {
			for(Pixel col : row) {
				if(col.getStatus() == true) {
					this.graph.addVertex(col);
				}
			}
        }

        // Add edges between intersection vertices
		for(int row = 0; row < this.pixelMap.size()-1; row++) {
			List<Pixel> row1 = this.pixelMap.get(row);
			List<Pixel> row2 = this.pixelMap.get(row+1);

			for(int col = 0; col < row1.size()-1; col++) {
				Pixel p = row1.get(col);

				if(p.getStatus()==true){
					Pixel succ = row1.get(col+1);
					Pixel lower = row2.get(col);

					if(succ.getStatus()==true) {
						this.graph.addEdge(succ, p);
					}
					if(lower.getStatus()==true) {
						this.graph.addEdge(p, lower);
					}
				}
			}
		}

		int lastIndex = this.pixelMap.size()-1;
		List<Pixel> lastRow = this.pixelMap.get(lastIndex);
		for(int col = 0; col < lastRow.size()-1; col++) {
			Pixel p = lastRow.get(col);

			if(p.getStatus()==true){
				Pixel succ = lastRow.get(col+1);
				if(succ.getStatus()==true){
					this.graph.addEdge(p, succ);
				}
			}
		}
	}
	
	
	/*
	 * Find the coordinates of a square, given the relative pixel (i.e. vertex of the graph)
	 * 
	 */	
	private void disegnaQuadrato(PixelWriter pixelWriter, int x, int y, int dimensione, Color colore) {
        for (int i = 0; i < dimensione; i++) {
            for (int j = 0; j < dimensione; j++) {
                pixelWriter.setColor(x + i, y + j, colore);
            }
        }
    }

	/*
	 * Set the variables to start a fire
	 * 
	 */	
	public void spreadFire() {
		
		this.queue = new PriorityQueue<>();
		this.burned_area = 0;

		//set the normal distribution
		this.distribution = new NormalDistribution(this.totalBurnedArea/this.vegDensity, this.standardError/this.vegDensity);

		//seleziona un pixel random
		Random random = new Random();
        int randomIndex = random.nextInt(this.graph.vertexSet().size());
		Iterator<Pixel> iterator = this.graph.vertexSet().iterator();
		Pixel startingPixel = null;
        for (int i = 0; i <= randomIndex; i++) {
            startingPixel = iterator.next();
        }

		//compute the distances form any node and insert it in the queue
		this.queue = new PriorityQueue<>();
		for(Pixel p : this.graph.vertexSet()) {
			double euclidianDistance = Math.sqrt(Math.pow(p.getX() - startingPixel.getX(), 2) + Math.pow(p.getY() - startingPixel.getY(), 2));
			double kmDistance = euclidianDistance*2.5/148;
			p.setDistance(kmDistance);
			this.queue.add(p);
		}
	}

	/*
	 * To modify an image pixel by pixel until return null
	 * 
	 */	
	public WritableImage yield(ImageView mapImg) {
		Double pixelArea = 2.5 * 2.5 / (148 * 148);

		Image image = mapImg.getImage();
		WritableImage writableImage = (WritableImage) image;
		PixelWriter pixelWriter = writableImage.getPixelWriter();

		if(!this.queue.isEmpty()) {
			Pixel currentPixel = this.queue.poll();


			double pseudoArea = Math.PI*Math.pow(currentPixel.getDistance(), 2);	
			double prob = 1 - this.distribution.cumulativeProbability(pseudoArea);

			//improve speed by eliminating very unlikely burnable pixel
			if(prob<Math.pow(10, -5)) {
				return null;
			}

			if(Math.random()<=prob) {
				this.burned_area += pixelArea;
				int x = currentPixel.getX() * 4;
				int y = currentPixel.getY() * 4;
				Color colore = Math.random()>0.05 ? Color.BLACK : Color.DARKORANGE;
				disegnaQuadrato(pixelWriter, x, y, 4, colore);
			}
			return writableImage;
		} else {
			return null;
		}
		
	}

	/*
	 * To modify an image of the graph, showing all the connected components
	 * 
	 */	
	public WritableImage showConnectedComponents(ImageView img) {
		Image image = img.getImage();
		WritableImage writableImage = (WritableImage) image;
		PixelWriter pixelWriter = writableImage.getPixelWriter();
		Random random = new Random();

		List<List<Pixel>> connecLists = findAllConnectedComponents();

		for(List<Pixel> connList : connecLists) {
			Color colore = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
			for(Pixel p : connList) {
				// Colora il quadrato associato di nero
				int x = p.getX() * 4;
				int y = p.getY() * 4;
				disegnaQuadrato(pixelWriter, x, y, 4, colore);
			}
		}

		return writableImage;
	}
	
	/*
	 * Find the pixel contained in the largest connected component of the graph
	 * 
	 */	
	public List<Pixel> findLargestConnectedComponent() {
		ConnectivityInspector<Pixel, DefaultEdge> inspector = new  ConnectivityInspector<Pixel, DefaultEdge>(this.graph);

		Set<Pixel> verSet = new HashSet<>(this.graph.vertexSet());

		int dimMax = 0;
		List<Pixel> lPixels = new ArrayList<>();

		for(Pixel ver : this.graph.vertexSet()) {
			if (verSet.contains(ver)) {
				Set<Pixel> componenete = inspector.connectedSetOf(ver);
				verSet.removeAll(componenete); //non analizzare di nuovo questa componente
				
				if(dimMax < componenete.size()) {
					dimMax = componenete.size();
					lPixels = new ArrayList<Pixel>(componenete);
				}
			}
		}

		this.maxCCsize = lPixels.size();
		return lPixels;
	}

	/*
	 * Find all the connected components of the graph
	 * 
	 */	
	public List<List<Pixel>> findAllConnectedComponents() {
        ConnectivityInspector<Pixel, DefaultEdge> inspector = new ConnectivityInspector<>(this.graph);

        Set<Pixel> remainingVertices = new HashSet<>(this.graph.vertexSet());
        List<List<Pixel>> connectedComponents = new ArrayList<>();

        while (!remainingVertices.isEmpty()) {
            Pixel startVertex = remainingVertices.iterator().next();
            Set<Pixel> component = inspector.connectedSetOf(startVertex);

            remainingVertices.removeAll(component); // Non analizzare di nuovo questa componente
            connectedComponents.add(new ArrayList<>(component));
        }

		this.numberOfCC = connectedComponents.size();
		findLargestConnectedComponent();
        return connectedComponents;
    }

	public int getNumberOfCC() {
		return this.numberOfCC;
	}

    public double getBurnedArea() {
        return this.burned_area;
    }

	public float getMaxCCsixe() {
		return this.maxCCsize;
	}

	public String getTotalBurnedArea() {
		return String.format("%.2f", this.totalBurnedArea);
	}

    public String getVegExtension() {
		Double pixelArea = 2.5 * 2.5 / (148 * 148);
        return String.format("%.2f", this.graph.vertexSet().size() * pixelArea);
    }

	/*
	 * Compute the burned area for patches using the coefficients obtained thanks to the multiple linear regression
	 * 
	 * @param a list containing all the user input
	 */
	public void setBurnedArea(double[] input) {
		setVariablesCoefficients();

		Matrix data = new Matrix(input, input.length); 
        
        this.totalBurnedArea = this.variablesCoefficients4Mean.transpose().times(data).get(0, 0);
		this.standardError = this.variableCoefficients4Error.transpose().times(data).get(0, 0);
	}
	
	/*
	 * Compute the coefficients for the variables using the data contained in the db
	 * 
	 */
	private void setVariablesCoefficients() {
		
		double[][] xMatrix =  dao.variablesData();
		double[] listOfMeans = dao.resultsData("burned_area");
		double[] listOfError = dao.resultsData("standard_error");
		double[] numPatches = dao.getPatches();
		
		double[] meanMatrix = new double[listOfMeans.length];
		double[] errorMatrix = new double[listOfError.length];
		
		for(int i=0; i<listOfMeans.length; i++) {
			
			double m = listOfMeans[i];
			double e = listOfError[i];
			double m2 = m/1000000; //from m2 to km2
			double e2 = e/1000000; 
			double m3 = m2/numPatches[i]; //burned area for a single patche
			double e3 = e2/numPatches[i];
			
			meanMatrix[i] = m3;
			errorMatrix[i] = e3;
		}
		
		this.variablesCoefficients4Mean = regr.findCoefficients(xMatrix, meanMatrix);
		this.variableCoefficients4Error = regr.findCoefficients(xMatrix, errorMatrix);
	}

}


