package com.luv2code.web.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

public class StudentDbUtil {

	private DataSource dataSource;
	
	public StudentDbUtil(DataSource theDataSource){
		dataSource = theDataSource;
	}
	
	public List<Student> getStudents() throws Exception{
		
		List<Student> students = new ArrayList<>();
		
		Connection myConn = null;
		Statement myStmt = null;
		ResultSet myRs = null;
		
		try{
				
			// get a connection
			myConn = dataSource.getConnection();
			
			// create sql statement
			String sql = "select * from Student order by last_name";
			myStmt = myConn.createStatement();
			
			// execute query
			myRs = myStmt.executeQuery(sql);
			
			// process the result set
			while(myRs.next()){
				
				// retrieve data from result set row
				int id = myRs.getInt("id");
				String firstName = myRs.getString("first_Name");
				String lastName = myRs.getString("last_Name");
				String email = myRs.getString("email");
				
				// create new student object
				Student tempStudent = new Student(id, firstName, lastName, email);
				
				// add it to the list of students
				students.add(tempStudent);
			}
			
			return students;
		}
		finally{
			
			// close JDBC objects
			close(myConn, myStmt, myRs);
		}
	}

	private void close(Connection myConn, Statement myStmt, ResultSet myRs) {
		// TODO Auto-generated method stub
		try{
			if(myRs !=null){
				myRs.close();
			}
			if(myStmt !=null){
				myStmt.close();
			}
			if(myConn !=null){
				myConn.close(); // doesn't really close, just puts back in the connection pool and make it available for someone else to use.
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public void addStudent(Student theStudent) throws Exception{
		
		// create sql to insert
		Connection myConn =null;
		PreparedStatement myStmt = null;
		try{
			
			// set the param values for the student
			myConn = dataSource.getConnection();
			
			// execute sql insert
			String sql = "insert into student" 
							+"(first_name, last_name, email) "
							+ "values (?, ?, ?)";
			myStmt = myConn.prepareStatement(sql);
			
			//set the param values for the student
			myStmt.setString(1, theStudent.getFirstName());
			myStmt.setString(2, theStudent.getLastName());
			myStmt.setString(3, theStudent.getEmail());
			
			myStmt.execute();
		}
		finally{
			// clean up JDBC Objects
			close(myConn, myStmt, null);
		}
		
		
	}

	public Student getStudent(String theStudentId) throws Exception{
		
		Student theStudent = null;
		
		Connection myConn = null;
		PreparedStatement myStmt = null;
		ResultSet myRs = null;
		int studentId;
		
		try{
			
			// convert student id to int
			studentId = Integer.parseInt(theStudentId);
			
			// get connection to database
			myConn = dataSource.getConnection();
			
			// create sql to get selected student
			String sql = "select * from student where id=?";
			
			// prepared statement
			myStmt = myConn.prepareStatement(sql);
			
			// set params
			myStmt.setInt(1, studentId);
			
			// execute statement
			myRs = myStmt.executeQuery();
			
			// retrieve data from result set row
			if(myRs.next()){
				String firstName = myRs.getString("first_Name");
				String lastName = myRs.getString("last_Name");
				String email = myRs.getString("email");
				
				// use the studentId during construction
				theStudent = new Student(studentId, firstName, lastName, email);
			}
			else{
				throw new Exception("Could not find student id:" + studentId);
			}
			
			return theStudent;
		}
		finally{
			
			// clean up jdbc objects
			close(myConn, myStmt, myRs);
		}
		
	}

	public void updateStudent(Student student) throws Exception{
		
		
		Connection myConn = null;
		PreparedStatement myStmt =null;
		String sql = null;
		
		try{
			
			myConn = dataSource.getConnection();
			sql = "update student "+ "set first_Name=?, last_Name=?, email=? "+ "where id=?";
			
			myStmt = myConn.prepareStatement(sql);
			
			myStmt.setString(1, student.getFirstName());
			myStmt.setString(2, student.getLastName());
			myStmt.setString(3, student.getEmail());
			myStmt.setInt(4, student.getId());
			
			myStmt.execute();
		}
		finally{
			
			close(myConn, myStmt, null);
		}
	}

	public void deleteStudent(int theStudentId) throws Exception {
		
		Connection myConn = null;
		PreparedStatement myStmt = null;
		String sql = null;
		
		try{
			myConn = dataSource.getConnection();
			sql= "Delete from student "+"where id=?";
			myStmt = myConn.prepareStatement(sql);
			
			myStmt.setInt(1, theStudentId);
			myStmt.execute();
		}
		finally{
			close(myConn, myStmt, null);
		}
	}
	
}
