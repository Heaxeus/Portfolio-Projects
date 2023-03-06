import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


public class Main {


    //Global scanner prompt
    public static Scanner prompt = new Scanner(System.in);

    //Global reference to 'words.txt' file
    public static File words = new File("words.txt");

    //Global String of letters already guessed
    public static String lettersGuessed = "";

    //Global reference of base 'gallows' layout
    public static String[][] gallows = {
            {" ", " ", "_", "_", "_", "_", "_", " ", " "},
            {" ", " ", "|", " ", " ", " ", "|", " ", " "},
            {" ", " ", "|", " ", " ", " ", " ", " ", " "},
            {" ", " ", "|", " ", " ", " ", " ", " ", " "},
            {" ", " ", "|", " ", " ", " ", " ", " ", " "},
            {" ", " ", "|", " ", " ", " ", " ", " ", " "},
            {"_", "_", "_", "_", "_", "_", "_", "_", "_"}
    };


    public static void main(String[] args) {
        println("Welcome to Hangman!");
        start();
    }


    /**
     * This method runs the start, or 'main menu' of the game
     */
    public static void start() {

        //Resets gallows and lettersGuessed variables
        gallows = new String[][]{
                {" ", " ", "_", "_", "_", "_", "_", " ", " "},
                {" ", " ", "|", " ", " ", " ", "|", " ", " "},
                {" ", " ", "|", " ", " ", " ", " ", " ", " "},
                {" ", " ", "|", " ", " ", " ", " ", " ", " "},
                {" ", " ", "|", " ", " ", " ", " ", " ", " "},
                {" ", " ", "|", " ", " ", " ", " ", " ", " "},
                {"_", "_", "_", "_", "_", "_", "_", "_", "_"}
        };

        lettersGuessed = "";


        println("To respond to prompts, please type what is in quotes(''), including the hash symbol(#)");
        println("Would you like to '#play' the game, or '#add' a word to the word list?");


        String response = prompt.nextLine();
        if (response.equals("#play")) {
            game();
        } else if (response.equals("#add")) {
            //Process to add a word to 'words.txt'
            try {
                BufferedWriter writeCall = new BufferedWriter(new FileWriter(words, true));
                do {
                    println("Please enter the word you would like to add. Go back with '#back'.");
                    response = prompt.nextLine();

                    if (response.equals("#back")) {
                        break;
                    } else {
                        writeCall.write(response);
                        writeCall.newLine();
                        println("Word Added!");
                    }
                } while (true);
                writeCall.close();
                start();
            } catch (IOException e) {
                checkWordFile();
                start();
            }
        }
    }

    /**
     * This method is the main Hangman game
     */
    public static void game() {
        println("Please type in the word to guess, or say '#random' to pull a random word from the word list.");

        int numOfWords = 0;
        String word = "";
        int incorrectGuesses = 0;




        String response = prompt.nextLine();
        if (response.equals("#random")) {
            //Pulls a random word from 'words.txt', then starts the game.
            if (checkWordFile()) {
                try {
                    Path file = Paths.get("words.txt");
                    numOfWords = (int) Files.lines(file).count();

                    int lineChoice = (int) (Math.random() * numOfWords);
                    word = Files.readAllLines(file).get(lineChoice);
                } catch (IOException e) {
                    println("Word could not be chosen.");
                    game();
                }
            } else {
                println("Word file must exist for random word to be chosen!");
                game();
            }
        } else {
            word = response;
        }

        String[] guessArea = new String[word.length()];
        Arrays.fill(guessArea, "_");


        //Main game loop
        do {
            printGallows(incorrectGuesses);
            printGuessArea(guessArea);

            println("You have " + ((incorrectGuesses - 6) * -1) + " guess(es) remaining.");

            print("Guess a letter: ");
            response = prompt.nextLine();
            char guess = response.toCharArray()[0];
            ArrayList index = letterInWordIndex(word, guess);
            if (!index.contains(-1)) {
                for (Object intIndex : index) {
                    guessArea[Integer.parseInt(intIndex.toString())] = guess + " ";
                }
            } else {
                incorrectGuesses++;
                lettersGuessed += guess;
            }
            if (!Arrays.asList(guessArea).contains("_")) {
                break;
            }
        } while (incorrectGuesses != 6);

        //Win/Lose conditions
        if (incorrectGuesses == 6) {
            println("Better luck next time! The word was: " + word);
            println("");
            game();
        } else {
            println("Congratulations! The word was '" + word + "'.");
            println("");
            game();
        }
    }


    /*
    **************************
    HELPER METHODS
    **************************
     */

    /**
     * This methods checks to see if the word file exists, and if it doesn't, prompts the user to create it.
     */
    public static boolean checkWordFile() {
        if(words.exists()) return true;

        println("'words.txt' can not be found. Would you like to create it? '#yes' or '#back'?");
        String response = prompt.nextLine();
        if (response.equals("#yes")) {
            try {
                words.createNewFile();
                println("File Created!");
                println("", 5);
                return true;
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        } else {
            println("", 50);
            return false;
        }

    }



    /**
     * This method checks to see if the guess String is in the word
     *
     * @param guess The player's guess
     * @param word  The word to guess
     * @return int[] Array of indexes of letters
     */
    public static ArrayList letterInWordIndex(String word, char guess) {
        ArrayList indexes = new ArrayList();
        for (int i = 0; i < word.length(); i++) {
            if (word.substring(i, i + 1).equals(Character.toString(guess))) {
                indexes.add(i);
            }
        }
        if (indexes.isEmpty()) indexes.add(-1);
        return indexes;
    }


    /**
     * This method prints out the guess area below the gallows
     *
     * @param guessArea takes in the guessArea global variable
     */
    public static void printGuessArea(String[] guessArea) {
        for (String guess : guessArea) {
            print(guess + " ");
        }
        println("");
        println("Incorrect letters guessed: " + lettersGuessed);
    }


    /**
     * This method prints the 'gallows' from Hangman, with the various body parts
     *
     * @param incorrectGuesses the number of incorrect guesses.
     */
    public static void printGallows(int incorrectGuesses) {
        switch (incorrectGuesses) {
            case 1:
                gallows[2][6] = "O";
                break;
            case 2:
                gallows[3][6] = "|";
                break;
            case 3:
                gallows[3][7] = "\\";
                break;
            case 4:
                gallows[3][5] = "/";
                break;
            case 5:
                gallows[4][7] = "\\";
                break;
            case 6:
                gallows[4][5] = "/";
                break;
        }
        for (int i = 0; i < gallows.length; i++) {
            for (int j = 0; j < gallows[i].length; j++) {
                print(gallows[i][j] + " ");
            }
            println("");
        }
        println("");
    }

    /**
     * This method is a print helper method
     *
     * @param string What to print
     */
    public static void print(String string) {
        System.out.print(string);
    }

    /**
     * This method is a println helper method
     *
     * @param string What to print
     */
    public static void println(String string) {
        System.out.println(string);
    }

    /**
     * This method is a print helper method that can print the message with a variable number of lines
     *
     * @param string        What to print
     * @param numberOfLines Number of lines to print
     */
    public static void print(String string, int numberOfLines) {
        for (int i = 0; i < numberOfLines; i++) {
            System.out.print(string);
        }
    }

    /**
     * This method is a println helper method that can print the message with a variable number of lines
     *
     * @param string        What to print
     * @param numberOfLines Number of lines to print
     */
    public static void println(String string, int numberOfLines) {
        for (int i = 0; i < numberOfLines; i++) {
            System.out.println(string);
        }
    }

    /*
    **************************
    HELPER METHODS - END
    **************************
     */

}