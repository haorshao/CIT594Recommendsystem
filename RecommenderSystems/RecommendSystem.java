package RecommenderSystems;
/**
 * This class is for the interaction between user and the recommender system.
 * by Haoran Shao 11/18/2016
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class RecommendSystem {
	Analyse ana;
	HashMap<Integer, String> moviesMap;
	/**
	 * The RecommendSystem constructor
	 * @param thresh-The number of neighbor when making prediction
	 * @param userID
	 */
	public RecommendSystem(int thresh, int userID){
		ana = new Analyse(thresh, userID);
		moviesMap = ana.read.getMoviesMap();
	}
	/**
	 * Get the predict score for a specific movieID
	 * @param movieID
	 * @return
	 */
	public double predictKNN(String itemID){
		return ana.prediction(itemID);
	}
	/**
	 * Get the predict score by Baseline predictor
	 * @param itemID
	 * @return
	 */
	public double predictBaseline(String itemID){
		return ana.predictionBaseline(itemID);
	}
	/**
	 * Get a arraylist of the recommendations from the system.
	 * @param number
	 * @return
	 */
	public ArrayList<Map.Entry<String, Double>> recommendSystem(int number){
		return ana.recommendations(number);
	}
	/**
	 * Print out the info of the recommendations.
	 * @param res
	 */
	public void printInfo(ArrayList<Map.Entry<String, Double>> res){
		System.out.println("The recommendations are as follows");
		for(int i = 0; i < res.size(); i++){
			System.out.println((i+1) + ". " + res.get(i).getKey() + " Score:" + res.get(i).getValue());
		}
	}
	public static void main(String[] args) {
		//Excute this main method when you start the Recommender System.
		Scanner scanner = new Scanner(System.in);
		System.out.println("Please input your user id:");
		String user = scanner.nextLine();
		RecommendSystem manager = new RecommendSystem(20, Integer.parseInt(user));//set default neighbor thresh is 20
		System.out.println("Please input the itemID which you want to predict: ");
		String item = scanner.nextLine();
		System.out.println("The score of item " + item + " predicted by KNN is: " + manager.predictKNN(item));
		System.out.println("The score of item " + item + " predicted by Baseline is: " + manager.predictBaseline(item));
		System.out.println("How many movie recommendations do you want to get: ");
		String recNum = scanner.nextLine();
		System.out.println("Getting recommendations for you...(Be patient. It needs about 10 minutes)");
		ArrayList<Map.Entry<String, Double>> res = manager.recommendSystem(Integer.parseInt(recNum));
		manager.printInfo(res);
		scanner.close();
		System.out.println("Thank you for using this system...");
	}
}
