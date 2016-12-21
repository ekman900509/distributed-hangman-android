package hw1;

import java.io.*;
import java.net.*;
import java.util.*;

public class SERVER_MAIN {

	private LinkedList<String> words = new LinkedList<String>();
	private ServerSocket serverSocket = null;
	private Random rnd = new Random();
	
	
	public static void main(String[] args) throws IOException{
		new SERVER_MAIN();
	}
	
	public SERVER_MAIN() throws IOException{
		loadWords();
		setUpConnection();
		listenForClients();
	}
	
	private void loadWords() throws IOException{
		BufferedReader br;
		FileReader fr;
		try{
			fr = new FileReader("words.txt");
			br = new BufferedReader(fr);
			System.out.print("Loading wordlist...");
			String word;
			while((word = br.readLine()) != null){
				if(word.matches("[a-zA-Z]{5,}")){
					words.addLast(word.toLowerCase());
				}
			}
			System.out.println("Done!");
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	
	private void setUpConnection() throws IOException{
		
		try{
			serverSocket = new ServerSocket(444);
		} catch (IOException e){
			e.printStackTrace();
		}
		
	}
	
	private void listenForClients() throws IOException{
		while(true){
			Socket clientSocket = serverSocket.accept();
			String guess_word = words.get(rnd.nextInt(words.size()));
			(new SERVER_THREAD(clientSocket, guess_word)).start();
		}
	}

}
