package quiz.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import quiz.datamodel.MCQQuestion;

/**
 * For MCQQuestion Table
 * @author LeeJuyeon
 * @author SonMoeun
 *
 */
public class MCQQuestionJDBCDAO {

	/*
	 * DELETE FROM QUESTION WHERE ID = 3;
	 *
	 * select * from question;
	 */

	private static final String INSERT_STATEMENT = "INSERT INTO MCQQUESTION (CHOICE1, CHOICE2, CHOICE3, CHOICE4, ANSWER) VALUES (?, ?, ?, ?, ?)";
	private static final String SEARCH_STATEMENT_BY_ID = "SELECT * FROM MCQQUESTION WHERE MCQID=?";
	private static final String UPDATE_STATEMENT = "UPDATE MCQQUESTION SET CHOICE1=?, CHOICE2=?, CHOICE3=?, CHOICE4=?, ANSWER=? WHERE MCQID=?";
	private static final String DELETE_STATEMENT = "DELETE FROM MCQQUESTION WHERE MCQID=?";
	private static final String SELECT_STATEMENT = "SELECT MCQID from MCQQUESTION WHERE CHOICE1=? AND CHOICE2=? AND CHOICE3=? AND CHOICE4=? AND ANSWER=?";

	private static final Logger LOG = Logger.getGlobal();

	/**
	 * @see quiz.services.Configuration#getInstance()
	 * @see quiz.services.Configuration#getConfigurationValue(String)
	 * 
	 * @return Connection
	 * @throws SQLException
	 */
	private Connection getConnection() throws SQLException {
		Configuration conf = Configuration.getInstance();
		String jdbcUrl = conf.getConfigurationValue("jdbc.url");
		String user = conf.getConfigurationValue("jdbc.user");
		String password = conf.getConfigurationValue("jdbc.password");
		Connection connection = DriverManager.getConnection(jdbcUrl, user, password);
		return connection;
	}
	
	/**
	 * This method will perform 'insert' command by using parameters for MCQQuestion table.
	 * First, it will insert a row and then find them again to know the MCQid.
	 * It will make log "insert success" and "done getting MCQId : " + mcqId.
	 * 
	 * @param mcqInfo - List of String : choice1~4, answer
	 * @return int - 0: error / other number : the MCQid of created MCQ information
	 * @author LeeJuyeon
	 */
	public int create(List<String> mcqInfo) {
		
		try (Connection connection = getConnection();
			 PreparedStatement insertStatement = connection.prepareStatement(INSERT_STATEMENT);) {

            for (int i = 0; i < 5; i++) {
                insertStatement.setString(i+1, mcqInfo.get(i));
            }
			insertStatement.execute();
			LOG.info("insert success");
		} catch (SQLException e) {
			e.printStackTrace();
		}

		try (Connection connection = getConnection();
			 PreparedStatement selectStatement = connection.prepareStatement(SELECT_STATEMENT);) {

            for (int i = 0; i < 5; i++) {
                selectStatement.setString(i+1, mcqInfo.get(i));
            }
			ResultSet results = selectStatement.executeQuery();
			int mcqId=0;
			if(results.next()){
				mcqId = results.getInt("MCQID");
			}
			LOG.info("done getting MCQId : " + mcqId);
			results.close();
			return mcqId;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;// 0 : error
	}

	/**
	 * This method will perform 'update' command by using parameters for MCQQuestion table.
	 * It will make log "A MCQquestion is updated."
	 * 
	 * @param list - ArrayList of String : choice1~4, answer, mcqid
	 * @author LeeJuyeon
	 */
    public void update(ArrayList<String> list) {

        try (Connection connection = getConnection();
             PreparedStatement updateStatement = connection.prepareStatement(UPDATE_STATEMENT)){

            for (int i = 0; i < 6; i++) {
                updateStatement.setString(i+1, list.get(i));
            }
            updateStatement.execute();
            LOG.info("A MCQquestion is updated.");
        }catch (SQLException e) {
            e.printStackTrace();
        }

    }

	/**
	 * This method will perform 'delete' command by using mcqid for MCQQuestion table.
	 * It will make log "A MCQquestion is deleted."
	 * 
	 * @param mcqid - int
	 * @author LeeJuyeon
	 */
    public void delete(int mcqid) {

        try (Connection connection = getConnection();
             PreparedStatement deleteStatement = connection.prepareStatement(DELETE_STATEMENT)){
            deleteStatement.setInt(1, mcqid);
            deleteStatement.execute();
            LOG.info("A MCQquestion is deleted.");
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method return if answer is correct answer or not.
     * @deprecated
     * @param mcqid - int
     * @param answer - String
     * @return boolean : 1 : correct / 0 : incorrect
     * @author SonMoeun
     */
	public boolean correctAnswer(int mcqid,String answer )
	{
		MCQQuestion rAnswer = showWhereMcqid(mcqid);

		return rAnswer.getAnswer().equals(answer);
	}

	/**
	 * This method will be called with mcqid.
	 * It will find information by mcqid and send a MCQQuestion with MCQId, choice1~4, and answer.
	 * 
	 * @param mcqid - int
	 * @return MCQQuestion
	 * @author SonMoeun
	 */
	public MCQQuestion showWhereMcqid(int mcqid) {
		MCQQuestion currentQuestion = new MCQQuestion();
		try (Connection connection = getConnection();
			 PreparedStatement preparedStatement = connection.prepareStatement(SEARCH_STATEMENT_BY_ID);)
		{
			preparedStatement.setInt(1, mcqid);
			ResultSet results = preparedStatement.executeQuery();
			while (results.next()) {
				int MCQID = results.getInt("MCQID");
				String choice1 = results.getString("CHOICE1");
				String choice2 = results.getString("CHOICE2");
				String choice3 = results.getString("CHOICE3");
				String choice4 = results.getString("CHOICE4");
				String answer = results.getString("Answer");
				currentQuestion = new MCQQuestion(MCQID, choice1, choice2, choice3, choice4, answer);
			}
			results.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return currentQuestion;
	}

	/**
	 * This method will print choice1, choice2, choice3, choice4, and answer by mcqid.
	 * @param mcqid - int
	 * @author LeeJuyeon
	 */
	public void print(int mcqid){
	    try(Connection connection = getConnection();
            PreparedStatement printMCQStatement = connection.prepareStatement(SEARCH_STATEMENT_BY_ID);
        ){
	    	printMCQStatement.setInt(1,mcqid);
	        ResultSet result_mcq = printMCQStatement.executeQuery();
	        while(result_mcq.next()) {
	        	for (int i = 1; i < 5; i++) {
	                System.out.println("choice"+i+" : "+ result_mcq.getString("choice"+i));
	            }
	            System.out.println("result : " + result_mcq.getString("Answer"));
	        }
	        
        } catch (Exception e){
	        e.printStackTrace();
        }
    }

}
