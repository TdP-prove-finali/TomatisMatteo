package javacode.db;

import java.sql.SQLException;

import java.util.List;

import java.util.ArrayList;

import java.sql.ResultSet;

import java.sql.Connection;

import java.sql.PreparedStatement;

public class WildfiresDAO {

	/**
	 * Extract the mean density of vegetation
	 * 
	 * @return a % of density (double) 
	 */
	public double getVegDensity() {
		
		String query = "SELECT AVG(fraction_of_burnable_area) AS vegDen FROM wildfires" ;
		
		try {
			Connection conn = DBconnection.getConnection() ;
			PreparedStatement st = conn.prepareStatement(query) ;
			
			ResultSet res = st.executeQuery() ;
			
			double result = res.getDouble("vegDen");
			
			res.close();
			conn.close();
			
			return result;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0.0;
		}

	}
    
    /**
	 * Extract the data about the climate variables for each db record
	 * 
	 * @return a list containing the data (m^2)
	 */
	public double[][] variablesData() {
		
		String query = "SELECT t2m, tp, u10, v10 FROM wildfires WHERE burned_area>0" ;
		
        List<double[]> data = new ArrayList<>();

		try {
			Connection conn = DBconnection.getConnection() ;
			PreparedStatement st = conn.prepareStatement(query) ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				double temp = res.getDouble("t2m");
	            double prec = res.getDouble("tp");
	            double wind = Math.sqrt(Math.pow(res.getDouble("u10"), 2)+Math.pow(res.getDouble("v10"), 2));
	            double[] record = {temp, prec, wind};
	            data.add(record);
			}
			
			double[][] matrix = new double[data.size()][3];
            for (int i = 0; i < data.size(); i++) {
                matrix[i] = data.get(i);
            }
			
			res.close();
			conn.close();
			
			return matrix;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Extract the data about the total burned area for each db record
	 * 
	 * @return a list containing the data (m^2)
	 */
	public double[] resultsData(String columnName) {
		List<Double> list = new ArrayList<>();

		String query = "";
		
		// select the rigth type of query based on param
		if (columnName.equals("burned_area")) {
			query = "SELECT burned_area FROM wildfires WHERE burned_area > 0";
		} else if (columnName.equals("standard_error")) {
			query = "SELECT standard_error FROM wildfires WHERE standard_error > 0";
		} else {
			System.out.println("Error: coloumn not found in db");
			return null;
		}
		
		try {
			Connection conn = DBconnection.getConnection();
			PreparedStatement st = conn.prepareStatement(query);
			// Non è necessario inserire un valore di input per il nome della colonna
			
			ResultSet res = st.executeQuery();
			
			while (res.next()) {
				list.add(res.getDouble(columnName));
			}
			
			double[] matrix = new double[list.size()];
			for (int i = 0; i < list.size(); i++) {
				matrix[i] = list.get(i);
			}
			
			res.close();
			conn.close();
			
			return matrix;
			
		} catch (SQLException e) {
			// Gestione dell'eccezione
			e.printStackTrace();
			return null;
		}
	}	
	
	/**
	 * Extract the data about the total burned area for each db record
	 * 
	 * @return a list containing the data (m^2)
	 */
	public double[] getPatches() {
		List<Double> list = new ArrayList<>();
		
		String query = "SELECT number_of_patches FROM wildfires WHERE burned_area>0" ;
		
		try {
			Connection conn = DBconnection.getConnection() ;
			PreparedStatement st = conn.prepareStatement(query) ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				list.add(res.getDouble("number_of_patches"));
			}
			
			double[] matrix = new double[list.size()];
			for(int i = 0; i < list.size(); i++) {
				matrix[i] = list.get(i);
			}
			
			res.close();
			conn.close();
			
			return matrix;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}
}