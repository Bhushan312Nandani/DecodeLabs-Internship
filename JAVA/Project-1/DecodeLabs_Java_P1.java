import java.util.Scanner;
import java.util.Random;
import java.util.InputMismatchException;

public class DecodeLabs_Java_P1 {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Random random = new Random();
        boolean playAgain = true;

        System.out.println("WELCOME TO THE TEAM! Phase 1: The Number Game");

        while (playAgain) {
            int targetNumber = random.nextInt(100) + 1;
            int attempts = 0;
            boolean hasGuessedCorrectly = false;

            System.out.println("\nI have generated a number between 1 and 100. Can you guess it?");

            while (!hasGuessedCorrectly) {
                System.out.print("Enter your guess: ");
                
                try {
                    int userGuess = sc.nextInt();
                    attempts++;
                    sc.nextLine(); 
                    if (userGuess == targetNumber) {
                        System.out.println("Correct! You found it in " + attempts + " attempts.");
                        hasGuessedCorrectly = true;
                    } else if (userGuess > targetNumber) {
                        System.out.println("Too High! Try again.");
                    } else {
                        System.out.println("Too Low! Try again.");
                    }

                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a whole number.");
                    sc.nextLine();
                }
            }
            System.out.print("Do you want to play another round? (Y/N): ");
            String response = sc.nextLine().trim().toLowerCase();
            if (!response.equals("y")) {
                playAgain = false;
                System.out.println("Thank you for playing! Mission Complete.");
            }
        }
        sc.close();
    }
}