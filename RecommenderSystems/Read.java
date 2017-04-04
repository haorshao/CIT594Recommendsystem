package RecommenderSystems;
/**
 * This class is for read and store the rating and movie data into system.
 * by Haoran Shao 11/18/2016
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Read {
//	private double[][] matrix;//change the type to double. Just realize the rate can be half.
//	ArrayList<String> items;//Store the movie IDs
//	private ArrayList<String> ISBN;//Store the books' ISBN
//	private ArrayList<Integer> users;//Store the user IDs
	String fileName;//rating file name
	String movieFileName;//movie file name
	ArrayList<String> dataString;
	int maxScore;//Added for milestone3 for storing the different max rated score for different rating system
	private HashMap<Integer, String> moviesMap;//map the movie ID with its names
	public HashMap<Integer, ArrayList<Map.Entry<String, Double>>> rateMap;//Added for milestone3 for substituting the overlarge matrix
	/**
	 * Read constructor, with input as rating file name and movie file name.
	 * @param fileName
	 * @param movieFileName
	 */
	public Read(String fileName, String movieFileName){
		this.fileName = fileName;
		this.movieFileName = movieFileName;
//		items = new ArrayList<String>();
//		users = new ArrayList<Integer>();
		dataString = new ArrayList<String>();
		moviesMap = new HashMap<Integer, String>();
		rateMap = new HashMap<Integer, ArrayList<Map.Entry<String, Double>>>();
		if(this.fileName.contains("Book")){
			this.maxScore = 10;
		}else{
			this.maxScore = 5;
		}
	}

	/**
	 * Overload the constructor, for read the single CSV data
	 */
	public Read(String fileName){
		this.fileName = fileName;
//		ISBN = new ArrayList<String>();
//		users = new ArrayList<Integer>();
//		items = new ArrayList<String>();
		dataString = new ArrayList<String>();
		movieFileName = null;
		rateMap = new HashMap<Integer, ArrayList<Map.Entry<String, Double>>>();
	}
//	/**
//	 * Store the rating data into a matrix
//	 * @return-the rating data matrix
//	 */
//	public double[][] getMatrix() {
//		return matrix;
//	}
//	/**
//	 * Get the arraylist of movie IDs
//	 * @return-the arraylist of movie IDs
//	 */
//	public ArrayList<Integer> getItems() {
//		return items;
//	}
//	/**
//	 * Get the arraylist of user IDs
//	 * @return-the arraylist of user IDs
//	 */
//	public ArrayList<Integer> getUsers() {
//		return users;
//	}
	/**
	 * Get the map of movie ID and movie names
	 * @return
	 */
	public HashMap<Integer, String> getMoviesMap() {
		return moviesMap;
	}
	/**
	 * Read and store the data in rating file and movie file
	 */
	public void readData(){
		System.out.println("Start reading data...");
		File file = new File(fileName);
		if(!file.exists() || file.isDirectory()){
			System.out.println("ERROR: FILE DOES NOT EXIST");
			return;
		}
		try{
			BufferedReader br = new BufferedReader(new FileReader(file));
			String temp = null;
			temp = br.readLine();
			while(temp != null){
				String newEle = new String(temp);
				dataString.add(newEle);
				temp = br.readLine();
			}
			br.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		if(movieFileName != null){
			File movieFile = new File(movieFileName);
			if(!movieFile.exists() || movieFile.isDirectory()){
				System.out.println("MOVIE FILE DOES NOT EXIST!");
				return;
			}
			ArrayList<String> movieString = new ArrayList<>();
			try{
				BufferedReader br = new BufferedReader(new FileReader(movieFile));
				String temp = null;
				temp = br.readLine();
				while(temp != null){
					String newEle = new String(temp);
					movieString.add(newEle);
					temp = br.readLine();
				}
				br.close();
			}catch(IOException e){
				e.printStackTrace();
			}
			for(int i = 0; i < movieString.size(); i++){
				String[] temp = movieString.get(i).split("::");
				moviesMap.put(Integer.parseInt(temp[0]), temp[1]);
			}
		}
		for(int i = 0; i < dataString.size(); i++){
			String newest = dataString.get(i);
			int userid;
			String itemid;
			double rate;
			Map.Entry<String, Double> entry;
			//Deal with different files with different string split methods
			if(fileName.contains("ratings.csv")){
				if(i == 0){
					continue;
				}
				String[] strArray = newest.split(",");
				userid = Integer.parseInt(strArray[0]);
				itemid = strArray[1];
				rate = Double.parseDouble(strArray[2]);  
				entry = new AbstractMap.SimpleEntry<String, Double>(itemid, rate);
			}else if(fileName.contains("Book")){
				if(i == 0){
					continue;
				}
				String[] strArray = newest.split(";");
				userid = Integer.parseInt(strArray[0].substring(1, strArray[0].length() - 1));
				itemid = strArray[1].substring(1, strArray[1].length() - 1);
				rate = Double.parseDouble(strArray[2].substring(1,  strArray[2].length() - 1));  
				entry = new AbstractMap.SimpleEntry<String, Double>(itemid, rate);
			}else{
				String[] strArray = newest.split("::");
				userid = Integer.parseInt(strArray[0]);
				itemid = strArray[1];
				rate = Double.parseDouble(strArray[2]);
				entry = new AbstractMap.SimpleEntry<String, Double>(itemid, rate);
			}
			if(!rateMap.containsKey(userid)){
				ArrayList<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>();
				list.add(entry);
				rateMap.put(userid, list);
			}else{
				rateMap.get(userid).add(entry);
			}
		}
		System.out.println("Reading data finished...");
	}
	
	public static void main(String[] args){
		//codes in this main method are just for testing Read class purpose.
//		Read read = new Read("ratings.dat", "movies.dat");
		Read read = new Read("BX-Book-Ratings.csv");
		long start = System.currentTimeMillis();
		read.readData();
		long end = System.currentTimeMillis();
		System.out.println(end - start);
		System.out.println(read.dataString.size());
		System.out.println(read.dataString.get(0));
		System.out.println(read.rateMap.size());
		System.out.println(read.rateMap.get(276725).get(0).getKey());
//		System.out.println(read.rateMap.get(1).get(0).getKey());
//		System.out.println(read.rateMap.get(1).get(0).getValue());
//		System.out.println(read.matrix.length);
//		System.out.println(read.matrix[0].length);
//		System.out.println(read.users.get(0));
//		System.out.println(read.items.get(0));
//		System.out.println(read.matrix[0][0]);
//		System.out.println(read.users.get(1));
//		System.out.println(read.items.get(0));
//		System.out.println(read.matrix[1][0]);
//		System.out.println(read.moviesMap.get(1));
	}
}
