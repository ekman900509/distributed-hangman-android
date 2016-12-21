package hw1;

import java.net.*;
import java.util.*;
import java.io.*;

public class SERVER_THREAD extends Thread{

	private Socket clientSocket;
	private final int MSG_SIZE = 32;
	private String guess_word;
	private String dash_word = "";
	private char[] dash_array;
	private char[] letter_array;
	private int WORD_SIZE = 0;
	private int FAILED_ATTEMPTS = 10;
	private String received_message;

	public SERVER_THREAD(Socket clientSocket, String guess_word){
		this.clientSocket = clientSocket;
		this.guess_word = guess_word;
		WORD_SIZE = guess_word.length();
		dash_array = new char[WORD_SIZE*2];
		letter_array = guess_word.toCharArray();
		create_dash_word();
	}

	private void create_dash_word(){
		for(int i = 0; i < WORD_SIZE*2; i+=2){
			dash_word += "_";
			dash_array[i] = '_';
			dash_array[i+1] = ' ';
			
		}
	}
	
	private boolean checkNewGameMessage(String word){
		return word.equals("new_game");
	}

	private String update_dash_word(String word){
		//System.out.println("Length: "+word.length());
		//GUESS WHOLE WORD
		if(word.length() > 1){
			//RIGHT WORD
			if(word.equals(guess_word)){
				return guess_word;
			}
			else{
				FAILED_ATTEMPTS--;
				return new String(dash_array);
			}
		}
		//GUESS LETTER
		else{
			char c = word.charAt(0);
			boolean validLetter = false;
			//System.out.println("CHAR: "+c);
			for(int i = 0; i < WORD_SIZE*2; i+=2){
				if(c == letter_array[i/2]){
					validLetter = true;
					dash_array[i] = c;
				}
			}
			if(!validLetter) FAILED_ATTEMPTS--;
			return new String(dash_array);
		}
	}

	public void run(){
		BufferedInputStream in = null;
		BufferedOutputStream out = null;

		try{
			in = new BufferedInputStream(clientSocket.getInputStream());
			out = new BufferedOutputStream(clientSocket.getOutputStream());
		} catch (IOException e){
			e.printStackTrace();
		}
		byte[] msg = new byte[MSG_SIZE], toSend;
		byte[] word_msg = null;
		int bytesRead = 0;
		int n;
		dash_word = new String(dash_array);
		while(true){
			try{
				//System.out.println(dash_word);
				toSend = dash_word.getBytes();
				//System.out.println("TS = "+toSend.length);
				out.write(toSend, 0, toSend.length);
				out.write(FAILED_ATTEMPTS);
				out.flush();
				while((n = in.read(msg, bytesRead, MSG_SIZE)) != -1){
					bytesRead += n;
					//System.out.println(in.available());
					if(n == MSG_SIZE){
						break;
					}
					if(in.available() == 0){
						break;
					}
				}
				if(bytesRead == 0 && n == -1)
					break;
				word_msg = new byte[bytesRead];
				for(int i = 0; i < bytesRead; i++){
					word_msg[i] = msg[i];
				}
				bytesRead = 0;
				received_message = new String(word_msg);
				if(!checkNewGameMessage(received_message)){
					dash_word = update_dash_word(received_message);
				}
				//System.out.println("WORD: "+dash_word);

			} catch (IOException e){
				
				System.out.println("SOMETHING HAPPENED AT SERVER I/O");
				e.printStackTrace();
				break;
			}
		}

		
		try{
			in.close();
			out.close();
		} catch (IOException e){
			e.printStackTrace();
		}
		
		System.out.println("SERVER THREAD EXIT");
		
	}
}
