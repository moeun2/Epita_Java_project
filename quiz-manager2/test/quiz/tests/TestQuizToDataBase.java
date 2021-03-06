package quiz.tests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import quiz.datamodel.Question;
import quiz.services.QuestionJDBCDAO;

public class TestQuizToDataBase {

	public static void main(String[] args) throws SQLException {
		// TODO Auto-generated method stub
		QuestionJDBCDAO dao = new QuestionJDBCDAO();
		List<String> topics = Arrays.asList("dlfjsofjs");
		dao.create(new Question("dlfjs", topics,1),1);
		System.out.println(dao.search(new Question(1,1,"dlfjs")));
	}
	
	private static void testInsertIntoDatabase() throws SQLException {
		Connection connection = DriverManager.getConnection("jdbc:h2:~/test", "sa", "");
		String sqlCommand = "INSERT INTO QUESTION (QUESTION, DIFFICULTY) VALUES ('what', '1')";
		PreparedStatement insertStatement = connection.prepareStatement(sqlCommand);
		insertStatement.execute();
		
		connection.close();
		insertStatement.close();
		
		testSearchQeustionDifficultyFromDB();
	}
	
	
	private static void testSearchQeustionDifficultyFromDB() throws SQLException {
		Connection connection = DriverManager.getConnection("jdbc:h2:~/test", "sa", "");
		System.out.println(connection.getSchema());
		String selectQuery = "select DIFFICULTY from QUESTION";

		// PreparedStatement is preferred to prevent SQL injections
		PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
		ResultSet results = preparedStatement.executeQuery();

		while (results.next()) {
			int column1 = results.getInt("DIFFICULTY");
			System.out.println(column1);
		}
		preparedStatement.close();
		results.close();
		connection.close();
	}

}
