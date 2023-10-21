package model;

import Jama.Matrix;
import Jama.QRDecomposition;

public class LinearRegression {
	
	private Matrix coefficientMatrix;  // regression coefficients
	
	/*
	 * Constructor
	 */
	public LinearRegression() {
		
	}
	
	/*
	 * Resolve the Multiple Linear Regression
	 * 
	 * @param a map and a list containing the variables (x) and the results (y)
	 * @return the matrix of the coefficients
	 */
	public Matrix findCoefficients(double[][] variables, double[] results) {
		
		Matrix variablesMatrix = buildVariables(variables);
		Matrix resultsMatrix = buildResults(results);
		
        // find least squares solution
        QRDecomposition qr = new QRDecomposition(variablesMatrix);
        coefficientMatrix = qr.solve(resultsMatrix);
        
        return coefficientMatrix;
	}
	
	/*
	 * Construct a matrix from a double[][]
	 * 
	 * @param a map of witch values are list containing the variables
	 */
	private Matrix buildVariables(double[][] data) {
		
		Matrix matrixData = new Matrix(data);
		
		return matrixData;
	}
	
	/*
	 * Construct a matrix from a double[]
	 * 
	 * @param a list containing the results
	 */
	private Matrix buildResults(double[] data) {
		
		int c = data.length;
		
		Matrix matrixData = new Matrix(data, c);
		
		return matrixData;
	}


}
