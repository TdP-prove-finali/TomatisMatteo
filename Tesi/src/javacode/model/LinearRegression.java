package javacode.model;


import Jama.Matrix;
import Jama.QRDecomposition;
import javacode.db.WildfiresDAO;

public class LinearRegression {
    private static Matrix coefficientMatrix;  // regression coefficients
	
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

	 /**
     * Test of the linear regression.
     *
     */
    public static void main(String[] args) {
        WildfiresDAO dao = new WildfiresDAO();

        LinearRegression regression = new LinearRegression();
		
		double[][] x = dao.variablesData();
		double[] y = dao.resultsData("burned_area");

		regression.findCoefficients(x, y);

		

        System.out.print("Temperatura: " + coefficientMatrix.get(0,0) + "Pioggia: " + coefficientMatrix.get(1, 0) + "Vento: " + coefficientMatrix.get(2, 0));

		double burned_area = coefficientMatrix.get(0,0) * 35 + 20 * coefficientMatrix.get(1,0) + 1 * coefficientMatrix.get(2,0);
		System.out.println("\n" + burned_area);
    }

}
