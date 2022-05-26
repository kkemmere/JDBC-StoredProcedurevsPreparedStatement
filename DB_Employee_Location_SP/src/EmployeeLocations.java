
// Created by Kevin Kemmerer CS485 Advanced Database Systems

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class EmployeeLocations {
	
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost:3306/classicmodels?serverTimezone=UTC";

	public static void main(String[] args) {
		
		// The name of the MySQL account
		String dbUser = args[0];
		// Password for MySQL account
		String passWord = args[1];
		System.out.println(dbUser + ", " + passWord);
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rsGetCat = null;
		ResultSet rsdbmd = null;
		ResultSet rs = null;
		ResultSet rs2 = null;

		try {
			
			// Open a connection to the MySQL Database Manager 
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL, dbUser, passWord);
			System.out.println("Connection is valid: " + conn.isValid(2));
			System.out.println();
			
			
			String idEmployeeString;
			int normalID;
			String employeeName;
			String employeeAddress;
			
			// Static Query
			String sqlLocation = "select CONCAT('ID:',' ',e.employeeNumber) as ID, e.employeeNumber as normalID, CONCAT('NAME:',' ',e.firstName,' ',e.lastName) as Name, CONCAT('LOCATION:',' ',o.city,' ',COALESCE(o.state, ''),' ',o.country) as Address\n" + 
					"from offices as o\n" + 
					"join employees as e on e.officeCode = o.officeCode;";
			PreparedStatement psLocation = conn.prepareStatement(sqlLocation, rs.TYPE_SCROLL_SENSITIVE, rs.CONCUR_UPDATABLE);
			
			// Stored Procedure Query
			String EmployeeLocation = "CALL classicmodels.EmployeeLocation()";
			CallableStatement csLocation = conn.prepareCall(EmployeeLocation);
					
			rs = psLocation.executeQuery();
			
			// Static Query output
			System.out.println("Static Query Output (Prepared Statement):");
			System.out.println();
			while (rs.next())
			{
				// Get employee info
				idEmployeeString = rs.getString("ID");
				employeeName = rs.getString("Name");
				employeeAddress = rs.getString("Address");
				
				System.out.println(idEmployeeString + " | " + employeeName + " | " + employeeAddress);
			}
			
			// Reset first result set before use again	
			rs.beforeFirst();
			System.out.println();
			System.out.println("Stored Procedure Output:");
			System.out.println();
			
			// Second loop to get ID for procedure (Still using the same static query ResultSet)
			while (rs.next())
			{
				// Stored Procedure input was removed for basic use. Can be used such that one ID is taken from static query at sudo random for input
				// Get ID from first result set
				//normalID = rs.getInt("normalID");
				
				// Call procedure using ID from first result set
                // csLocation.setInt(1, normalID);
				rs2 = csLocation.executeQuery();
				
				// Only goes through loop once for each time the procedure is called
				if(rs2.next())
				{
					// Prints out first result set from stored procedure only if stored procedure has a return function
					//System.out.println(rs2.getString(1)); 
					
					// Get employee info
					idEmployeeString = rs.getString("ID");
					employeeName = rs.getString("Name");
					employeeAddress = rs.getString("Address");
					
					System.out.println(idEmployeeString + " | " + employeeName + " | " + employeeAddress);
				}
				
			}
			
			
				
			}
		catch (SQLException se) { 
			
			// Handle errors for Database
			// See https://docs.oracle.com/javase/tutorial/jdbc/basics/sqlexception.html
			// 8.	There, print out the SQL Exception, the SQL State Code, and any Error Code if an exception were to occur.
			System.out.println("SQL Exception: " + se.getMessage());
			System.out.println("SQLState Code: " + se.getSQLState());
			System.out.println("Error Code: " + se.getErrorCode());
		} finally {
				try {
					// 6.	Upon completion, ensure that all Connections, Statements, and ResultSets are closed.
					if (rs != null) rs.close();
					if (rs2 != null) rs2.close();
					if (stmt != null) stmt.close();
					if (conn != null) conn.close();
					System.out.println("All connections are closed:" + conn.isClosed());
				} catch (SQLException se2) {}
				// End of finally
			}
		
	}

}
