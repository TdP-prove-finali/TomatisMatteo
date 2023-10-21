package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WildfiresDAO {
	
	/**
	 * Extract all the data about the climate and natural variables that could influence the propagation of a wildfire
	 * 
	 * @return a map containing a list for each record of the db
	 */
	public double[][] variablesData() {
		
		String query = "SELECT t2m, tp, u10, v10 FROM wildfires WHERE burned_area>0" ;
		
        List<double[]> data = new ArrayList<>();

		try {
			Connection conn = DBConnection.getConnection() ;
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
	public double[] resultsData() {
		
		List<Double> list = new ArrayList<>();
		
		String query = "SELECT burned_area FROM wildfires WHERE burned_area>0" ;
		
		try {
			Connection conn = DBConnection.getConnection() ;
			PreparedStatement st = conn.prepareStatement(query) ;
			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				list.add(res.getDouble("burned_area"));
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
	
	/**
	 * Extract the data about the total burned area for each db record
	 * 
	 * @return a list containing the data (m^2)
	 */
	public double[] getPatches() {
		List<Double> list = new ArrayList<>();
		
		String query = "SELECT number_of_patches FROM wildfires WHERE burned_area>0" ;
		
		try {
			Connection conn = DBConnection.getConnection() ;
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

