package RecommenderSystems;
import java.awt.HeadlessException;
import java.lang.annotation.Annotation;
import java.util.AbstractMap;
/**
 * The Analyse class is for making prediction with a specific movie and making recommendations for the users
 * by Haoran Shao 11/18/2016
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;

public class Analyse {
	Read read;
	int thresh;//The threshold number of neighbors
	Map<Integer, Double> similarity;//The map for the similarity number between other users and this user
	int userID;
	double userAverageRate;//This user's average rate
	ArrayList<String> ratedID;//The arraylist of the movieID which has been rated by this user.
//	double[][] matrix;//The rating matrix from Read class
	HashMap<Integer, Double> average;//The array of average rates of each of the users in the system.
	boolean fuckUser;//Check if the user rates all the movies with the same rates.
	ArrayList<Map.Entry<Integer, Double>> heap;//Store the similarity pairs in an descending order.
	double overAllAverage;
	/**
	 * Analyse Constructor //Added for milestone3-switch different data files by switching the read initialization within the constructor.
	 * Choose the similarity calculation methods here
	 * @param thresh-number of neighbors for every prediction
	 * @param userID
	 */
	public Analyse(int thresh, int userID){
		this.thresh = thresh;
		//Switch the data files here
//		read = new Read("ratings.dat", "movies.dat");
		read = new Read("BX-Book-Ratings.csv");
//		read = new Read("ratings.csv");
//		read = new Read("fullratings.csv");
		read.readData();
		this.userID = userID;
		similarity = new HashMap<Integer, Double>();
		System.out.println("userID:" + this.userID);
		this.fuckUser = false;//Set false by default.
		this.ratedID = new ArrayList<String>();
		for(int i = 0; i < read.rateMap.get(this.userID).size(); i++){
			ratedID.add(read.rateMap.get(userID).get(i).getKey());
		}
		average = new HashMap<Integer, Double>();
		for(Integer i : read.rateMap.keySet()){
			ArrayList<Map.Entry<String, Double>> list = read.rateMap.get(i);
			double sum = 0;
			for(int j = 0; j < list.size(); j++){
				sum += list.get(j).getValue(); 
			}
			double aver = sum / read.rateMap.get(i).size();
			average.put(i, aver);
		}
		userAverageRate = average.get(this.userID);
		//Calculate the average rate for this user.
		//Choose the similarity calculation methods here
		this.getSimilarity();
//		this.getCosineSimilarity();
	}
	/**
	 * Calculate the similarity of every user with the current user and update the map.
	 */
	public void getSimilarity(){
		for(Integer i : read.rateMap.keySet()){
			if(i != this.userID){
				//Calculate the average rate.
				double iAverage = average.get(i);
				double a = 0.0;
				double b = 0.0;
				double c = 0.0;
				//Set a flag by default meaning the user does not have common rate with this user.
				boolean intersection = false;
				ArrayList<Map.Entry<String, Double>> list = read.rateMap.get(i);
				for(int j = 0; j < list.size(); j++){
					double userRate = 0;
					if(ratedID.contains(list.get(j).getKey())){
						//The user has common rated movies with this user
						intersection = true;
						for(int k = 0; k < read.rateMap.get(userID).size(); k++){
							if(read.rateMap.get(userID).get(k).getKey().equals(list.get(j).getKey())){
								userRate = read.rateMap.get(userID).get(k).getValue();
							}
						}
						a += (list.get(j).getValue() - iAverage) * (userRate- this.userAverageRate);
						b += (list.get(j).getValue() - iAverage) * (list.get(j).getValue() - iAverage);
						c += (userRate - this.userAverageRate) * (userRate - this.userAverageRate);
					}
				}
				//Check if this user rated all the movies with the same rate.
				if(intersection && c == 0){
					fuckUser = true;//really bad user.
					return;
				} 
				//Check if current user rated all the movies with the same rate. If so, just set similarity as 0.
				if(intersection && b != 0){
					double s = a / (Math.sqrt(b) * Math.sqrt(c));
					similarity.put(i, s);
				}else{
					double s = 0;
					similarity.put(i, s);
				}
			}
		}
		//Create a heap storing the similarity pairs in a descending order.
		this.heap = new ArrayList<Map.Entry<Integer, Double>>();
		Iterator<Map.Entry<Integer, Double>> it = similarity.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<Integer, Double> newest = it.next();
			heap.add(newest);
		}
		Collections.sort(this.heap, new Comparator<Map.Entry<Integer, Double>>() {
			@Override
			public int compare(Map.Entry<Integer, Double> a, Map.Entry<Integer, Double> b){
				return -1 * (a.getValue().compareTo(b.getValue()));
			}
		});
	}
	/**
	 * Calculate the the cosine similarity
	 */
	public void getCosineSimilarity(){
		for(Integer i : read.rateMap.keySet()){
			if(i != this.userID){
				//Calculate the average rate.
//				double iAverage = average.get(i);
				double a = 0.0;
				double b = 0.0;
				double c = 0.0;
				//Set a flag by default meaning the user does not have common rate with this user.
				boolean intersection = false;
				ArrayList<Map.Entry<String, Double>> list = read.rateMap.get(i);
				for(int j = 0; j < list.size(); j++){
					double userRate = 0;
					if(ratedID.contains(list.get(j).getKey())){
						//The user has common rated movies with this user
						intersection = true;
						for(int k = 0; k < read.rateMap.get(userID).size(); k++){
							if(read.rateMap.get(userID).get(k).getKey().equals(list.get(j).getKey())){
								userRate = read.rateMap.get(userID).get(k).getValue();
							}
						}
						a += userRate * list.get(j).getValue();
						b += userRate * userRate;
						c += list.get(j).getValue() * list.get(j).getValue();
					}
				}
				//Check if this user rated all the movies with the same rate.
				if(intersection && b == 0){
					fuckUser = true;//really bad user.
					return;
				} 
				//Check if current user rated all the movies with the same rate. If so, just set similarity as 0.
				if(intersection && c != 0){
					double s = a / (Math.sqrt(b) * Math.sqrt(c));
					similarity.put(i, s);
				}else{
					double s = 0;
					similarity.put(i, s);
				}
			}
		}
		this.heap = new ArrayList<Map.Entry<Integer, Double>>();
		Iterator<Map.Entry<Integer, Double>> it = similarity.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<Integer, Double> newest = it.next();
			heap.add(newest);
		}
		Collections.sort(this.heap, new Comparator<Map.Entry<Integer, Double>>() {
			@Override
			public int compare(Map.Entry<Integer, Double> a, Map.Entry<Integer, Double> b){
				return -1 * (a.getValue().compareTo(b.getValue()));
			}
		});
	}
	/**
	 * Get the top thresh number similarity pairs whose user has rated the movie.
	 * @param movieID - change the input type to String for milestone3.
	 * @return
	 */
	public ArrayList<Map.Entry<Integer, Double>> getNeighbor(String itemID){
		ArrayList<Map.Entry<Integer, Double>> res = new ArrayList<Map.Entry<Integer, Double>>();
		int neighborCount = 0;
//		System.out.println(heap == null);
		for(int i = 0; i < this.heap.size();i++){
			Map.Entry<Integer, Double> newest = this.heap.get(i);
			int current = newest.getKey();
			boolean exist = false;
			for(int j = 0; j < read.rateMap.get(current).size(); j++){
				if(read.rateMap.get(current).get(j).getKey().equals(itemID)){
					exist = true;
				}
			}
			if(exist && newest.getValue() != 0){
				res.add(newest);
				neighborCount++;
				//Check if the number of elements in the heap is more than the thresh number.
				if(neighborCount >= this.thresh){
					return res;
				}
			}
		}
		return res;
	}
	/**
	 * predict rate by baseline predictor.
	 * @param itemID
	 * @return
	 */
	public double predictionBaseline(String itemID){
		if(fuckUser){
			return this.userAverageRate;
		}
		double sum = 0;
		int count = 0;
		for(Integer i : read.rateMap.keySet()){
			for(int j = 0; j < read.rateMap.get(i).size(); j++){
				sum += read.rateMap.get(i).get(j).getValue();
				count++;
			}
		}
		this.overAllAverage = sum / count;
		int I = read.rateMap.get(this.userID).size();
		int U = 0;
		ArrayList<Double> rates = new ArrayList<>();
		for(Integer i : read.rateMap.keySet()){
			for(int j = 0; j < read.rateMap.get(i).size(); j++){
				if(read.rateMap.get(i).get(j).getKey().equals(itemID)){
					U++;
					rates.add(read.rateMap.get(i).get(j).getValue());
				}
			}
		}
		double bu = 0;
		double bi = 0;
		for(int i = 0; i < read.rateMap.get(userID).size(); i++){
			bu += read.rateMap.get(userID).get(i).getValue() - this.overAllAverage;
		}
		bu = bu / I;
		for(int i = 0; i < rates.size(); i++){
			bi += rates.get(i) - bu - this.overAllAverage;
		}
		bi = bi / U;
		double res = this.overAllAverage + bu + bi;
		return res;
	}
	/**
	 * Make prediction for this user in terms of the input movieID
	 * @param movieID
	 * @return
	 */
	public double prediction(String itemID){
		if(fuckUser){
			return this.userAverageRate;
		}
		ArrayList<Map.Entry<Integer, Double>> neighbor = this.getNeighbor(itemID);
		//If this user is the bad user, just give the prediction with the same rate as the others which the user has been rated.
		if(neighbor.size() == 0){
			return this.userAverageRate;
		}
		double a = 0.0;
		double b = 0.0;
		for(int i = 0; i < neighbor.size(); i++){
			double neighborRate = 0;
			int neighborID = neighbor.get(i).getKey();
			double neighborAver = average.get(neighbor.get(i).getKey());
			for(int j = 0; j < read.rateMap.get(neighborID).size(); j++){
				if(read.rateMap.get(neighborID).get(j).getKey().equals(itemID)){
					neighborRate = read.rateMap.get(neighborID).get(j).getValue();
				}
			}
			a += neighbor.get(i).getValue() * (neighborRate - neighborAver);
			b += Math.abs(neighbor.get(i).getValue());
		}
		double res = this.userAverageRate + a / b;
		return res;
	}
	/**
	 * Get the TOP number of recommendations for this user.//Optimized for milestone 3 to increase the recommendation speed
	 * @param number
	 * @return
	 */
	public ArrayList<Map.Entry<String, Double>> recommendations(int number){
		ArrayList<Map.Entry<String, Double>> res = new ArrayList<Map.Entry<String, Double>>();
		//Create a heap for storing the movies and sorting them by the predict rates.
		PriorityQueue<Map.Entry<String, Double>> heap = new PriorityQueue<Map.Entry<String, Double>>(read.rateMap.get(userID).size(), new Comparator<Map.Entry<String, Double>>() {
			public int compare(Map.Entry<String, Double> a, Map.Entry<String, Double> b){
				return -1 * (a.getValue().compareTo(b.getValue()));
			}
		});
		for(int i = 0; i < read.rateMap.get(userID).size(); i++){
			heap.add(read.rateMap.get(userID).get(i));
		}
		for(Integer i : read.rateMap.keySet()){
			for(int j = 0; j < read.rateMap.get(i).size(); j++){
				if(!ratedID.contains(read.rateMap.get(i).get(j).getKey())){
					double predict = this.prediction(read.rateMap.get(i).get(j).getKey());
					Map.Entry<String, Double> newest = new AbstractMap.SimpleEntry(read.rateMap.get(i).get(j).getKey(), predict);
					//Optimization starts here....
					if(fuckUser){
						res.add(newest);
						if(res.size() == number){
							return res;
						}
					}else{
						if(predict > read.maxScore - 1){
							heap.add(newest);
							if(heap.size() >= (number + read.rateMap.get(userID).size())){
								for(int k = 0; k < number; k++){
									res.add(heap.poll());
								}
								return res;
							}
						}
					}
				}
			}
		}
		return res;
	}
	public static void main(String[] args) {
		// Codes in this main method are just for testing purpose.
		Analyse ana = new Analyse(20, 5);
//		ArrayList<Map.Entry<Integer, Double>> current = ana.getNeighbor("")
//		for(int i = 0; i < ana.average.length; i++){
//			System.out.println(ana.average[i]);
//		}
//		for (int i = 0; i < 5; i++) {
//			System.out.println(ana.heap.get(i).getValue());
//		}
		System.out.println(ana.similarity.size());
//		ArrayList<Map.Entry<Integer, Double>> record = ana.getNeighbor("0155061224");
//		System.out.println(ana.heap.size());
//		System.out.println(record.size());
//		for(int i = 0; i < record.size(); i++){
//			System.out.println(record.get(i).getKey());
//			System.out.println(record.get(i).getValue());
////			System.out.println(ana.matrix[record.get(i).getKey()][ana.read.getItems().indexOf(3149)]);
//		}
		long start = System.currentTimeMillis();
		System.out.println("score" + ana.predictionBaseline("100"));
		long end = System.currentTimeMillis();
		System.out.println(end - start);
//		System.out.println(ana.predictionBaseline("0155061224"));
	}

}
