package model;

import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import Jama.Matrix;
import db.WildfiresDAO;

public class Model {
	
	WildfiresDAO dao;
	LinearRegression regr;
	
	private Graph<Pixel, DefaultEdge> graph = new SimpleGraph(DefaultEdge.class);
	private List<List<Pixel>> pixelMap;
	
	double obsKm2 = 24000; //total surface observable (km^2)
	Matrix variablesCoefficients; //matrix containing the coefficient compute with the multiple linear regression method
	double burned_area; //km2 of burned area
	
	public Model() {
		dao = new WildfiresDAO();
		regr = new LinearRegression();
	}
	
	/*
	 * Compute the burned area for patches using the coefficients obtained thanks to the multiple linear regression
	 * 
	 * @param a list containing all the user input
	 */
	public void setBurnedArea(List<Double> input) {
		
		this.burned_area=0.0;
		multipleLinearRegression();
		
        int numRows = variablesCoefficients.getRowDimension();
        int numCols = variablesCoefficients.getColumnDimension();
        
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < numCols; j++) {
            	double var = input.get(i);
            	double coeff = variablesCoefficients.get(i, j);
                this.burned_area += var*coeff;
            }
        }
	}
	
	/*
	 * Compute the coefficients for the coefficients using the data contained in the db
	 * 
	 */
	private void multipleLinearRegression() {
		
		double[][] xMatrix =  dao.variablesData();
		double[] listOfResultsM = dao.resultsData();
		double[] numPatches = dao.getPatches();
		
		double[] yMatrix = new double[listOfResultsM.length];
		
		for(int i=0; i<listOfResultsM.length; i++) {
			
			double d = listOfResultsM[i];
			double d_2 = d*(10^-3); //form m2 to km2
			double d_3 = d_2/numPatches[i]; //burned area for a single patche
			
			yMatrix[i] = d_3;
		}
		
		variablesCoefficients = regr.findCoefficients(xMatrix, yMatrix);
	}
	
	/*
	 * Build a complete graph
	 * 
	 */
	public void buildGraph() {
		//add all vertices
		for(int row = 0; row < this.pixelMap.size(); row ++) {
			List<Pixel> rowPixel = this.pixelMap.get(row);
			for(int col = 0; col < rowPixel.size(); col++) {
				this.graph.addVertex(rowPixel.get(col));
			}
		}
		
		//add all edges
		for(Pixel p1 : this.graph.vertexSet()) {
			for(Pixel p2 : this.graph.vertexSet()) {
				this.graph.addEdge(p2, p1);
			}
		}
	}

	public WritableImage getMap() {
		int larghezzaImmagine = 1000;
        int altezzaImmagine = 1000;
        int dimensioneQuadrato = 4;
        int numeroRighe = altezzaImmagine / dimensioneQuadrato;
        int numeroColonne = larghezzaImmagine / dimensioneQuadrato;

        WritableImage writableImage = new WritableImage(larghezzaImmagine, altezzaImmagine);
        PixelWriter pixelWriter = writableImage.getPixelWriter();
        
        this.pixelMap = new ArrayList<List<Pixel>>();

        for (int riga = 0; riga < numeroRighe; riga++) {
        	List<Pixel> pixelList = new ArrayList<>();
            for (int colonna = 0; colonna < numeroColonne; colonna++) {
            	pixelList.add(new Pixel(riga, colonna));
                Color colore = (riga + colonna) % 2 == 0 ? Color.GREEN : Color.DARKGREEN;
                disegnaQuadrato(pixelWriter, colonna * dimensioneQuadrato, riga * dimensioneQuadrato, dimensioneQuadrato, colore);
            }
        }
		return writableImage;
	}
	
	private void disegnaQuadrato(PixelWriter pixelWriter, int x, int y, int dimensione, Color colore) {
        for (int i = 0; i < dimensione; i++) {
            for (int j = 0; j < dimensione; j++) {
                pixelWriter.setColor(x + i, y + j, colore);
            }
        }
    }
	
}
