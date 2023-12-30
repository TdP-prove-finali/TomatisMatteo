package javacode.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
	
	double obsKm2 = 24000; //total surface observable (km^2)
	Matrix variablesCoefficients; //matrix containing the coefficient compute with the multiple linear regression method
	double burned_area; //km2 of burned area for patches
	double vegDensity; //density of vegetation
	
	public Model() {
		dao = new WildfiresDAO();
		regr = new LinearRegression();
	}
	

	public void setVegDensity(double density) {
		this.vegDensity = density;
	}

	/*
	 * Compute the burned area for patches using the coefficients obtained thanks to the multiple linear regression
	 * 
	 * @param a list containing all the user input
	 */
	public void setBurnedArea(double[] input) {
		setVariablesCoefficients();

		Matrix data = new Matrix(input, input.length); 
        
        this.burned_area = this.variablesCoefficients.transpose().times(data).get(0, 0);
	}
	
	/*
	 * Compute the coefficients for the variables using the data contained in the db
	 * 
	 */
	private void setVariablesCoefficients() {
		
		double[][] xMatrix =  dao.variablesData();
		double[] listOfResultsM = dao.resultsData();
		double[] numPatches = dao.getPatches();
		
		double[] yMatrix = new double[listOfResultsM.length];
		
		for(int i=0; i<listOfResultsM.length; i++) {
			
			double d = listOfResultsM[i];
			double d_2 = d/1000000; //from m2 to km2
			double d_3 = d_2/numPatches[i]; //burned area for a single patche
			
			yMatrix[i] = d_3;
		}
		
		this.variablesCoefficients = regr.findCoefficients(xMatrix, yMatrix);
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


	public WritableImage spreadFire(ImageView mapImg) {
		Double pixelArea = 4.0 / (600 * 600);

		// Ottieni l'immagine dall'ImageView
		Image image = mapImg.getImage();
	
		// Verifica se l'immagine nell'ImageView è un'istanza di WritableImage
		if (image instanceof WritableImage) {
			WritableImage writableImage = (WritableImage) image;
			PixelWriter pixelWriter = writableImage.getPixelWriter();
	

			// Trova la componente connessa più grande nel grafo
			List<Pixel> largestConnectedComponent = findLargestConnectedComponent();

			// Seleziona un pixel casuale dalla componente connessa più grande
			if (!largestConnectedComponent.isEmpty()) {
				int randomIndex = (int) (Math.random() * largestConnectedComponent.size());
				Pixel startingPixel = largestConnectedComponent.get(randomIndex);
		
				// Colora il pixel e i suoi adiacenti fino a raggiungere l'area bruciata desiderata
				double totalBurnedArea = 0;
				List<Pixel> visitedPixels = new ArrayList<>();
				visitedPixels.add(startingPixel);
				totalBurnedArea += pixelArea;
		
				while (totalBurnedArea < burned_area && !visitedPixels.isEmpty()) {
					Pixel currentPixel = visitedPixels.remove(0); // Prendi il primo pixel dalla lista
		
					// Colora il quadrato associato di nero
					int x = currentPixel.getX() * 4;
					int y = currentPixel.getY() * 4;
					Color colore = Math.random()>0.2 ? Color.BLACK : Color.DARKORANGE;
					disegnaQuadrato(pixelWriter, x, y, 4, colore);
		
					// Aggiungi i pixel adiacenti alla lista dei visitati
					List<Pixel> adjacentPixels = getAdjacentPixels(currentPixel);
					for (Pixel adjacentPixel : adjacentPixels) {
						if (!visitedPixels.contains(adjacentPixel) && adjacentPixel.getStatus() && (visitedPixels.size() < 3 || Math.random() > 0.5) ) {
							visitedPixels.add(adjacentPixel);
							totalBurnedArea += pixelArea;
						}
					}
				}
			}

			return writableImage;
		}

		return null;
	}
	
	private List<Pixel> getAdjacentPixels(Pixel pixel) {
		List<Pixel> adjacentPixels = new ArrayList<>();

		for (DefaultEdge edge : graph.edgesOf(pixel)) {
			Pixel neighbor = graph.getEdgeTarget(edge);
			if (neighbor.equals(pixel)) {
				neighbor = graph.getEdgeSource(edge);
			}
	
			if (neighbor.getStatus()) {
				adjacentPixels.add(neighbor);
			}
		}
	
		return adjacentPixels;
	}
	
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
		
		return lPixels;
	}


}


