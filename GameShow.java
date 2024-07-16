/* Game Show assignment - Wheel of Fortune game
 * Challenge features: multiplayer & interrupt user input with timer
 * Author: Andy Sun
 * Date: October 22, 2023
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.swing.*;

public class GameShow {

 // global variables/objects
 static BufferedReader b = new BufferedReader(new InputStreamReader(System.in));
 static ArrayList<String> revealed = new ArrayList<String>();
 static Scanner in = new Scanner(System.in);
 static String phrase;
 static ArrayList<String> wheel = new ArrayList<>(Arrays.asList("$2500", "$600", "$700", "$600", "$650", "$500", "$700", "$600", "$550", "$500", "$600", "$650", "$700", "$800", "$500", "$650", "$500", "$900",
   "Bankrupt", "Bankrupt", "Bankrupt", "Turn End"));
 
 // main
 public static void main(String[] args) throws Exception {

  // declare variables
  int[] turnResult = new int[2];
  int p1Bank, p2Bank;
  String line, choice, name, name2;
  Boolean won = false, p2Won = false;
  ArrayList<String> listOfPhrases = new ArrayList<String>(); // all phrases are in caps
  Scanner fileReader;
  File words;
  
  // read file with list of phrases into listOfPhrases
  try {
   words = new File("Phrases.txt");
   fileReader = new Scanner(words);
   while (fileReader.hasNextLine()) { // add phrases from file line by line
    line = fileReader.nextLine();
    listOfPhrases.add(line);
   }
   fileReader.close();
  } catch (FileNotFoundException e) {
   System.out.println("An error occured. File not found.");
   e.printStackTrace();
   System.exit(0);
  }

  // print title
  slowPrint("Welcome to the...", 60);
  sleep(1000);
  System.out.println("\n\r\n" + "WHEEL OF FORTUNE");
  sleep(1000);
  

  do { // main program that can repeat
   // reset values
   won = false;
   p2Won = false;
   p1Bank = 0;
   p2Bank = 0;
   choice = "";
   revealed.clear();
   
   while (true) { // run until user chooses to exit
    // display main menu and prompt user for choice
    System.out.println("\nPlease enter an integer between 1 - 4 to select one of the options below:");
    System.out.println("\tMain Menu:\n\t[1]\tPlay against a CPU\n\t[2]\tPlay against a friend\n\t[3]\tRules\n\t[4]\tExit");
    choice = in.nextLine();
    if (!choice.equals("1") && !choice.equals("2") && !choice.equals("3") && !choice.equals("4")) // ensure user input is valid
     System.out.println("Incorrect. Enter an integer from 1-4!");
    else break;
   }

   
   if (choice.equals("1")) { // play single player game against cpu
    //get user name
    System.out.println("What's your name?");
    name = in.nextLine();
    
    // get random phrase
    phrase = listOfPhrases.get((int) (Math.random() * listOfPhrases.size()));

    while (!won && !p2Won) { // continue until the player or cpu (in place of p2 for single player) has won
     turnResult = playerTurn(name, p1Bank); // plays turn using the playerTurn() method
     p1Bank = turnResult[0];
     
     // prints result of turn
     if (turnResult[1] == -1) {
      System.out.println("\nYou took too long and lost your turn!");
      sleep(500);
     } else if (turnResult[1] == -2) {
      System.out.println("\nYou guessed wrong and lost your turn!");
      sleep(500);
     } else if (turnResult[1] == 1) {
      won = true; 
      break;
     }
     
     turnResult = cpuTurn(p2Bank); // simulates CPU turn
     p2Bank = turnResult[0]; // update CPU bank
     if (turnResult[1] == 1) { // cpu won
      p2Won = true;
     }
     sleep(2000);
    } // end of game
    
    // display results of the game
    displayBoard(); 
    System.out.println("Your bank: $" + p1Bank);
    System.out.println("CPU bank: $" + p2Bank);
    if (won)
     p1Bank += 1000;
    else if (p2Won)
     p2Bank += 1000;
    if (p1Bank > p2Bank) System.out.println("Congratulations, " + name + "! You won!");
    else System.out.println("Unforunately, you lost to the CPU! Try again next time!");
    sleep(1000);
   }

   
   else if (choice.equals("2")) { // multiplayer; play against a friend on the same device
    //get players
    System.out.print("Name of Player 1: ");
    name = in.nextLine();
    System.out.print("Name of Player 2: ");
    name2 = in.nextLine();
    
    // get random phrase
    phrase = listOfPhrases.get((int) (Math.random() * listOfPhrases.size()));

    while (!won && !p2Won) { // continue until the player or cpu (in place of p2 for single player) has won
     // player 1's turn
     turnResult = playerTurn(name, p1Bank); 
     p1Bank = turnResult[0];
     // prints result of turn
     if (turnResult[1] == -1) {
      System.out.println("\nYou took too long and lost your turn!");
      sleep(500);
     } else if (turnResult[1] == -2) {
      System.out.println("\nYou guessed wrong and lost your turn!");
      sleep(500);
     } else if (turnResult[1] == 1) {
      System.out.println("That's correct!");
      sleep(500);
      won = true; 
      break;
     }
     
     // player 2's turn
     turnResult = playerTurn(name2, p2Bank); // plays turn using the playerTurn() method
     p2Bank = turnResult[0];
     // prints result of turn
     if (turnResult[1] == -1) {
      System.out.println("\nYou took too long and lost your turn!");
      sleep(500);
     } else if (turnResult[1] == -2) {
      System.out.println("\nYou guessed wrong and lost your turn!");
      sleep(500);
     } else if (turnResult[1] == 1) {
      System.out.println("That's correct!");
      sleep(500);
      p2Won = true; 
      break;
     }

    } // end of game
    
    displayBoard(); 
    // add $1000 to the winner
    if (won)
     p1Bank += 1000;
    else if (p2Won)
     p2Bank += 1000;
    // display results of the game
    System.out.println(name + "'s bank: $" + p1Bank);
    System.out.println(name2 + "'s bank: $" + p2Bank);
    if (p1Bank > p2Bank) System.out.println("\nGame over. " + name + " is the winner!");
    else if (p1Bank == p2Bank) System.out.println("The game ended in a tie!");
    else System.out.println("Game over. " + name2 + " is the winner!");
    
    
   }
   
   else if (choice.equals("3")) { // print rules
    System.out.println("");
    System.out.println("Rules to Play: (Press enter to continue throughout rules)");
    in.nextLine();
    System.out.println(
      "The object of the game is to earn the most money before someone guesses the phrase displayed on the board.");
    System.out.println(
      "You can choose to play singleplayer (against a CPU), or multiplayer with another person on the same device.");
    System.out.println(
      "Each turn, a player has the option of spinning the wheel, buying a vowel, or trying to solve the puzzle. However, make sure to not take too long making your decision, or you'll lose your turn!");
    in.nextLine();
    System.out.println("If the player chooses to spin, the wheel spins until it lands on a wedge.");
    System.out.println(
      "If the wheel lands on the end turn wedge, the turn ends (wow). If the wheel lands on the the bankrupt wedge, the player's balance is also set to zero.");
    System.out.println(
      "Otherwise, the player can then guess a consonant. If the guess is correct, the letter is displayed on the board, and the player's balance increases by the wedge value multiplied by the number of times the letter occurs in the phrase.");
    System.out.println("If the guess is incorrect, or if the player takes too long to answer, the player's turn ends.");
    in.nextLine();
    System.out.println(
      "If the player chooses to buy a vowel, the player's balance decreases by the cost to buy a vowel ($250), and the player can then pick a vowel to guess.");
    System.out.println(
      "If the player guesses correctly, the letter is displayed on the board. There is no reward for guessing vowels.");
    System.out.println(
      "If the player guesses incorrectly or takes too long, the player's turn ends.");
    in.nextLine();
    System.out.println(
      "If the player chooses to solve the puzzle, the player must type the phrase they believe is hidden on the board.");
    System.out.println("If the player guesses incorrectly or takes too long, the turn ends.");
    System.out.println(
      "If the player guesses correctly, the player earns the puzzle prize ($1000) and the game ends.");
    System.out.println("Whoever has the most money at the end of the game wins!");
    in.nextLine();
    System.out.println("IMPORTANT INSTRUCTIONS:");
    System.out.println("Due to this program using threads, make sure to terminate the program before rerunning it (similar to closing an app).");
    System.out.println("That's the end of the rules. If you have any other questions, contact customer support at andy.sun2@student.tdsb.on.ca :) Good luck and have fun!");
    in.nextLine();
   }
  } while (!choice.equals("4")); // escape

  System.out.println("Wheel of Fortune over. Thanks for playing, and see you soon!");
  in.close(); // close scanner
  try {
   b.close();
  } catch (IOException e) {
   e.printStackTrace();
  }
 } // end of main method

 
 
 static int spin() throws Exception {
  int width = 750, height = 750;
  String selection;
  JFrame frame = new JFrame();
  SelectionWheel wheelGUI;
  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  
  wheelGUI = new SelectionWheel(wheel);
  wheelGUI.hasBorders(true);
  wheelGUI.setBounds(10, 10, 700, 700);
  frame.add(wheelGUI);
  frame.setSize(width, height);
  frame.setLayout(null);
  frame.setVisible(true);
  
  while (true) {
   // wait for action
   while(true)
   {
    try {
     Thread.sleep(10);
    } catch (InterruptedException e) {
     e.printStackTrace();
    }
    if(wheelGUI.isSpinning())
     break;
   }
   // while spinning
   while(wheelGUI.isSpinning())
   {
    try {
     Thread.sleep(10);
    } catch (InterruptedException e) {
     e.printStackTrace();
    }
   }
   // show selection
   selection = wheelGUI.getSelectedString();
   JOptionPane.showMessageDialog(frame, "You spun: " + selection);
   frame.setVisible(false);
   if (selection.equals("Bankrupt")) return -1;
   else if (selection.equals("Turn End")) return -2;
   else return Integer.parseInt(selection.substring(1)); // trim "$" character
  }
 }
 
 
 
 // pre: the necessary global variables (wheel, revealed etc.) and methods (displayBoard(), slowPrint(), getInputWithTimeout(), etc.) must exist 
 //      bank must be an integer
 // post: returns an array with 2 integers: the first is the player's bank after the turn, 
 //  the second is the turn result (-1 if timed out, -2 if wrong guess, 1 if won game, 0 for other turn end conditions). Updates global variable 'revealed' accordingly
 // simulates a player's turn
 static int[] playerTurn(String name, int bank) throws Exception {
  int spin, occurences; // variables
  String consonants = "qwrtpsdfghjklmnbvcxzQWRTPSDFGHJKLMNBVCXZ", vowels = "aeiouyAEIOUY", vowel, choice, guess; 
  int[] result = new int[2];
  result[0] = bank;
  
  System.out.println("\n```````````````````````````````````````````````");
  System.out.println(name + "'s Turn");
  sleep(2000);
  
  // repeat turn until player times out, guesses wrong, or wins game
  while (true) {
   displayBoard(); // display board at start of turn

   // choose what to do on turn
   do {
    System.out.println("\n" + name + "'s Bank: $" + result[0]);
    choice = getInputWithTimeout(
      "What would you like to do?\n[1]\tSpin wheel\n[2]\tBuy a vowel\n[3]\tSolve puzzle\n", 20);
    if (choice.equals("$$TIMEOUT$$")) { // if user timed out
     result[1] = -1;
     return result;
    }
    if (!choice.equals("1") && !choice.equals("2") && !choice.equals("3")) System.out.println("Enter a valid integer!"); 
   } while (!choice.equals("1") && !choice.equals("2") && !choice.equals("3")); // ensure valid input

   
   // spin wheel (then guess consonant)
   if (choice.equals("1")) {
    // spin wheel
    System.out.println("Spin the wheel! Navigate to the popup GUI!");
    spin = spin();
    System.out.println("Please close the popup now.");
    // bankrupt & end turn squares
    if (spin == -1) {
     System.out.println("Oops, you landed on a bankrupt square! Better luck next turn.");
     sleep(500);
     result[0] = 0;
     result[1] = 0;
     return result;
    } else if (spin == -2) {
     System.out.println("Dang, you landed on an 'end turn' square! Your turn is now over.");
     sleep(500);
     result[1] = 0;
     return result;
    }
    System.out.println("You spun $" + spin + "!\n");

    // if spin successful, start calling consonants
    guess = getInputWithTimeout("Call your consonant now: ", 10).toUpperCase();
    if (guess.equals("$$TIMEOUT$$")) {
     result[1] = -1;
     return result;
    }

    // make sure user guesses actual consonant
    while (guess.equals(" ") || guess.length() != 1 || !consonants.contains(guess)) {
     System.out.println("Incorrect input! Guess a consonant!");
     guess = getInputWithTimeout("Guess: ", 5).toUpperCase();
     if (guess.equals("$$TIMEOUT$$")) {
      result[1] = -1;
      return result;
     }
    }

    // if user guessed a correct consonant, reveal it on the board and award them the value spun times the number of occurences
    if (phrase.contains(guess) && !revealed.contains(guess)) {
     revealed.add(guess);
     occurences = phrase.length() - phrase.replace(guess, "").length(); // get the number of times the consonant appears in the phrase
     result[0] += occurences * spin;
     System.out.println("\'" + guess + "\' occured " + occurences + " times in the phrase!");
     sleep(500);
    } else if (revealed.contains(guess)) { // guessed letter that was already revealed
     System.out.println("Letter already revealed!");
     sleep(500);
     result[1] = -2;
     return result;
    } else { // incorrect guess
     result[1] = -2;
     return result;
    }
   }

   
   // buy vowel
   else if (choice.equals("2")) {
    if (result[0] < 250) { // not enough funds
     System.out.println("You don't have enough money buy a vowel!\nSelect another option!");
     sleep(1000);
    } else {
     vowel = getInputWithTimeout("Which vowel would you like to buy? ", 10).toUpperCase(); // get vowel
     if (vowel.equals("$$TIMEOUT$$")) {
      result[1] = -1;
      return result;
     }
     // make sure user chooses actual vowel
     while (vowel.equals(" ") || vowel.length() != 1 || !vowels.contains(vowel)) {
      vowel = getInputWithTimeout("Enter an actual vowel (AEIOUY)! ", 10).toUpperCase();
      if (vowel.equals("$$TIMEOUT$$")) {
       result[1] = -1;
       return result;
      }
     }
     
     result[0] -= 250; // subtract the cost of a vowel
     // display results
     if (revealed.contains(vowel)) { 
      System.out.println("Vowel already revealed!");
      result[1] = -2;
      return result;
     } else if (!phrase.contains(vowel)) {
      revealed.add(vowel);
      System.out.println("Vowel not found in phrase!");
      result[1] = -2;
      return result;
     } else {
      revealed.add(vowel);
      System.out.println("\'" + vowel + "\' occured "
       + (phrase.length() - phrase.replace(vowel, "").length()) + " times in the phrase!");
      sleep(500);
     }
    }
   }

   
   // guess phrase
   else if (choice.equals("3")) {
    guess = getInputWithTimeout("Guess the Phrase:\n", 15).toUpperCase(); // get guess
    if (guess.equals("$$TIMEOUT$$")) {
     result[1] = -1;
     return result;
    } else if (guess.equalsIgnoreCase(phrase)) { // guessed phrase correctly
     for (int i = 0; i < guess.length(); i++) { // reveal all characters
      if (!revealed.contains(guess.substring(i, i + 1))) {
       revealed.add(guess.substring(i, i + 1));
      }
     }
     result[1] = 1;
     return result;
    } else { // guessed wrong
     result[1] = -2;
     return result;
    }
   }
  } // end of repeated segment
 } // end of playerTurn

 
 
 // pre: the necessary global variables (wheel, revealed etc.) and methods (displayBoard(), slowPrint(), etc.) must exist, and bank must be an integer
 // post: Returns an array with 2 integers: the first is the CPU's bank after the turn, the second is the turn result (1 if CPU won, 0 otherwise). Updates global variables accordingly
 // simulates a CPU turn
 static int[] cpuTurn(int cpuBank) {
  String consonants = "QWRTPSDFGHJKLMNBVCXZ", vowels = "AEIOUY", vowel, guess, tempSpin;
  int[] result = new int[2];
  result[0] = cpuBank;
  int choice, rand, spin, occurences;
  Boolean cpuWon;

  System.out.println("\n```````````````````````````````````````````````");
  System.out.println("CPU'S TURN");
  sleep(2000);
  
  // repeat turn until cpu guesses wrong or wins game
  while (true) {
   System.out.println("\nCPU Bank: $" + cpuBank);

   // random generate choice
   choice = (int) (Math.random() * 3 + 1);
   while (choice == 2 && cpuBank < 250) choice = (int) (Math.random() * 3 + 1); // regenerate

   // spin wheel (then guess consonant)
   if (choice == 1) {
    System.out.println("CPU chose to spin the wheel.");
    // spin wheel
    System.out.print("Spinning wheel");
    slowPrint("...............", 100);
    tempSpin = wheel.get((int) (Math.random() * wheel.size()));
    if (tempSpin.equals("Bankrupt")) {
      System.out.println("CPU landed on a bankrupt square!");
     result[0] = 0; // reset bank
     result[1] = 0;
     return result;
    } else if (tempSpin.equals("Turn End")) {
      System.out.println("CPU landed on an 'end turn' square!");
     result[1] = 0;
     return result;
    }
    else {
      spin = Integer.parseInt(tempSpin.substring(1));
    }
    
    System.out.println("CPU spun $" + spin + "!\n");
    sleep(500);

    // call consonants
    System.out.println("CPU calling consonants");
    slowPrint("...............", 100);
    rand = (int) (Math.random() * consonants.length()); // randomly generate a consonant
    guess = consonants.substring(rand, rand + 1);

    // display results
    System.out.println("CPU guessed " + guess);
    sleep(500);
    if (phrase.contains(guess) && !revealed.contains(guess)) {
     revealed.add(guess);
     occurences = phrase.length() - phrase.replace(guess, "").length(); // get the number of times guess is in the phrase
     result[0] += occurences * spin; // add money to bank
     System.out.println("\'" + guess + "\' occured " + occurences + " times in the phrase!");
     sleep(1500);
     displayBoard();
    } else {
     System.out.println("CPU guessed wrong!");
     result[1] = 0;
     return result;
    }
   }

   
   // buy vowel
   else if (choice == 2) {
    System.out.println("CPU chose to buy a vowel!");
    rand = (int) (Math.random() * vowels.length()); // generate random vowel
    vowel = vowels.substring(rand, rand + 1);
    while (revealed.contains(vowel)) { // don't rebuy same vowel
     rand = (int) (Math.random() * vowels.length());
     vowel = vowels.substring(rand, rand + 1);
    }
    
    result[0] -= 250; // subtract the cost
    System.out.println("CPU bought " + vowel);
    sleep(1000);
    
    // display results
    if (revealed.contains(vowel)) {
     System.out.println("Vowel already revealed!");
     result[1] = 0;
     return result;
    } else if (!phrase.contains(vowel)) {
     System.out.println("Vowel not found in phrase!");
     revealed.add(vowel);
     result[1] = 0;
     return result;
    } else {
     revealed.add(vowel);
     System.out.println(vowel + " occured "
       + (phrase.length() - phrase.replace(vowel, "").length()) + " times.\n");
    }
    sleep(1000);
    displayBoard();
   }

   
   // guess phrase
   else if (choice == 3) {
    System.out.println("CPU chose to guess phrase.");
    slowPrint("................", 80);
    cpuWon = true; 
    for (int i = 0; i < phrase.length(); i++) { // this CPU is pretty dumb; it'll only "guess" the phrase correctly if all the letters are already revealed
     if (phrase.substring(i, i + 1) != " " && !revealed.contains(phrase.substring(i, i + 1))) { // if there are any unrevealed letters, CPU canot guess right
      cpuWon = false;
      break;
     }
    }
    if (cpuWon) {
     System.out.println("CPU guessed the phrase correctly!");
     result[1] = 1;
     return result;
    } else {
     System.out.println("CPU guessed wrong!");
     result[1] = 0;
     return result;
    }
   }
  }

 }

 
 
 // pre: global String phrase and ArrayList revealed exist
 // post: n/a
 // outputs the 'board', adjusting based on the length of the phrase
 static void displayBoard() { 
  // variables
  String output = "\n\t", emptyLine = "\t$";
  int lineLength = phrase.length() * 3;
  
  // comments won't be provided for every line, but essentially all of this is just to format the board to look neat, based on how long the phrase is!
  for (int i = 0; i < lineLength - 2; i++) {
   emptyLine += " ";
  }
  emptyLine += "$\n";

  for (int i = 0; i < lineLength; i++) {
   output += "$";
  }
  output += "\n\t$";
  for (int i = 0; i < (lineLength - 7) / 2; i++) {
   output += " ";
  }
  output += "BOARD";
  for (int i = 0; i < (lineLength - 7) / 2; i++) {
   output += " ";
  }
  if (lineLength % 2 == 0)
   output += " ";
  output += "$\n" + emptyLine + "\t$";
  for (int i = 0; i < (lineLength - 2 - phrase.length() * 2) / 2; i++) {
   output += " ";
  }

  // the part for the actual phrase, with underscores for unrevealed letters, hangman style
  for (int i = 0; i < phrase.length(); i++) {
   if (revealed.contains(phrase.substring(i, i + 1))) { // revealed characters
    output += phrase.charAt(i) + " ";
   } else if (phrase.charAt(i) != ' ') { // not revealed characters (not space)
    output += "_ ";
   } else
    output += "  "; // space characters in the phrase
  }

  for (int i = 0; i < (lineLength - 2 - phrase.length() * 2) / 2; i++) {
   output += " ";
  }
  if (lineLength % 2 == 1)
   output += " ";
  output += "$\n" + emptyLine + emptyLine + "\t";
  for (int i = 0; i < lineLength; i++) {
   output += "$";
  }

  System.out.println(output);
 }

 // reads in one line only if it's available
 static String next() throws Exception {
  if(b.ready()) return b.readLine();
  return "";
 }
 
 
 // pre: sec is a positive integer, start is a positive number
 // post: Returns "" if timed out, and the input as a string otherwise
 // uses libraries from java.util.concurrent to get user input, timing out if they take too long. 
 static String timedOut(int sec, long start) {
  // variables/object declarations
  Callable<String> k = () -> next();
  String input = "";
  boolean valid = true;
  ExecutorService es = Executors.newFixedThreadPool(1);
  Future<String> f;
  f = es.submit(k); // submits task to future
  // continue running while less than sec seconds has elapsed
  done: while (System.currentTimeMillis() < start + sec * 1000) {
   do {
    valid = true;
    if (f.isDone()) { // checks if task is done
     try { // try to get input
      input = f.get();
      break done;
     } catch (Exception e) { // in case input is invalid in some way
      System.out.println("Something went wrong! Try again:");
      f = es.submit(k);
      valid = false;
     }
    }
   } while (!valid); // loop again if input was invalid
  }

  f.cancel(true); // task is done
  return input; 
 }

 
 // pre: sec is a positive integer
 // post: Returns "$$TIMEOUT$$" if timed out, and the input as a string otherwise
 public static String getInputWithTimeout(String msg, int sec) {
  System.out.print(msg);
  long start = System.currentTimeMillis(); // starting time
  String answer = "";
  //continues looping until the time is out
  while (System.currentTimeMillis() < start + sec*1000) {
   //reads the answer 
   answer = timedOut(sec, start);
   if(answer.length() >= 1) return answer; // answer successful; wasn't ""
   
  }
  //user timed out
  if(answer.equals("")) {
   answer = "$$TIMEOUT$$";
  }
  return answer;
 }
 
 // pre: input (m) is a positive integer
 // post: n/a
 // program pauses for m milliseconds
 static void sleep(int m) {
  try {
   Thread.sleep(m);
  } catch (InterruptedException e) {
   e.printStackTrace();
  }
 }

 
 
 // pre: the second parameter, delay, must be a positive integer
 // post: n/a
 // outputs first parameter with a delay of the second parameter between each character
 static void slowPrint(String output, int delay) {
  char c;
  for (int i = 0; i < output.length(); i++) {
   c = output.charAt(i);
   System.out.print(c);
   try {
    Thread.sleep(delay);
   } catch (Exception e) {
   }
  }
 }
} // end of GameShow class

 