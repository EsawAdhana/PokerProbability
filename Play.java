import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class Play {

    // Below fields (scanner to potSize) make freq. used data easier to access
    public static Scanner scanner = new Scanner(System.in);
    private Hand hand1;
    private Hand hand2;
    private int playerOneFunds;
    private int playerTwoFunds;
    public String playerOneName;
    public String playerTwoName;

    public int potSize = 0;


    // These two variables change the game length
    public final int BUY_IN_SIZE = 100;
    public final int BLIND_SIZE = 5;

    public Play() throws IOException {
        welcome(); // Welcomes users, starts bets, informs of buy-in/blind
        while (playerOneFunds >= BLIND_SIZE && playerTwoFunds >= BLIND_SIZE) {
            System.out.println("Let's play!");
            runRound(); // Runs one full round of betting
            System.out.print("Press enter to start the next round.");
            System.in.read();
            clear(); // Clears the console for clarity
        }
        System.out.println("Good game! Gamble responsibly...");
    }

    private void welcome() {
        System.out.println("Welcome to poker night!");
        System.out.print("Player 1, what would you like your name to be?: ");
        playerOneName = scanner.nextLine();
        System.out.print("And now, player 2?: ");
        playerTwoName = scanner.nextLine();
        System.out.println("Great! Welcome, " + playerOneName + " and " + playerTwoName + "!");
        playerOneFunds = BUY_IN_SIZE;
        playerTwoFunds = BUY_IN_SIZE;
        System.out.println("Both players will start with $" + BUY_IN_SIZE + ", and $" + BLIND_SIZE + " will be taken as the blind each round.");
    }

    public void runRound() throws IOException {
        Deck deck = new Deck(); // Makes a new deck (which is shuffled)
        hand1 = new Hand(deck); // Gets player one's hole cards
        playerOneFunds -= BLIND_SIZE;
        playerTwoFunds -= BLIND_SIZE;
        System.out.println(playerOneName + "'s balance: $" + playerOneFunds);
        System.out.println(playerTwoName + "'s balance: $" + playerTwoFunds);
        potSize = 2 * BLIND_SIZE;
        System.out.println("Pot size: $" + potSize);
        System.out.print(playerOneName + "! Press enter to see your hole cards.");
        System.in.read(); // Easiest way of waiting for user to be ready, used freq. throughout program
        System.out.println(playerOneName + "'s hole cards: " + hand1.hand);
        System.out.print("Okay, now again, press enter to hide them.");
        System.in.read();
        clear();
        hand2 = new Hand(deck);
        System.out.print(playerTwoName + "! Press enter to see your hole cards.");
        System.in.read();
        System.out.println(playerTwoName + "'s hole cards: " + hand2.hand);
        System.out.print("Okay, now again, press enter to hide them.");
        System.in.read();
        clear();
        ArrayList<Card> communityCards = hand1.getCommunityCards(deck);
        hand1.hand.addAll(communityCards);
        hand2.hand.addAll(communityCards);
        System.out.println("Okay, now both players! Here are your community cards!");
        System.out.println(communityCards);
        System.out.print(playerOneName + ", press enter to see your best possible hand.");
        System.in.read();
        System.out.println(Hand.convertToHandRanking(Hand.evaluateHand(hand1.determineHand())) + " with " + hand1.determineHand());
        calculateOdds(hand1);
        System.out.print("Press enter to hide them.");
        System.in.read();
        clear();
        System.out.print(playerTwoName + ", press enter to see your best possible hand.");
        System.in.read();
        System.out.println(Hand.convertToHandRanking(Hand.evaluateHand(hand2.determineHand())) + " with " + hand2.determineHand());
        calculateOdds(hand2);
        System.out.print("Press enter to hide them.");
        System.in.read();
        clear();
        runBets();
        System.out.println(playerOneName + "'s balance: $" + playerOneFunds);
        System.out.println(playerTwoName + "'s balance: $" + playerTwoFunds);
    }

    public void calculateOdds(Hand hand) {
        // This method runs 10,000 theoretical random hands to estimate the player's chances of winning
        int wins = 0;
        int losses = 0;
        int ties = 0;
        int current;
        for (int i = 0; i < 10000; i++) {
            Deck deck = new Deck();
            Hand otherHand = new Hand(deck); // Contains two (possibly repeated) hole cards
            otherHand.hand.remove(hand.holeCards.get(0)); // Ensures no repeats
            otherHand.hand.remove(hand.holeCards.get(1)); // Ensures no repeats
            otherHand.hand.addAll(hand1.communityCards);
            ArrayList<Card> topCard = new ArrayList<>();
            while (otherHand.hand.size() < 7) {
                topCard.add(deck.deckOfCards.get(deck.deckOfCards.size() - 1));
                if (Collections.disjoint(otherHand.hand, topCard)) {
                    otherHand.hand.add(deck.deckOfCards.remove(deck.deckOfCards.size() - 1));
                }
                topCard.clear();
            }
            // By this point, the hand has seven total cards, no repeats

            current = Play.determineBetter(hand, otherHand);
            switch (current) {
                case 0 -> ties++;
                case 1 -> wins++;
                case 2 -> losses++;
            }
        }
        System.out.println("Win rate: " + wins / 100.0 + "%");
        System.out.println("Lose rate: " + losses / 100.0 + "%");
        System.out.println("Tie rate: " + ties / 100.0 + "%");
    }

    private void runBets() {
        System.out.println(playerOneName + "! You get to bet first.");
        System.out.println("Type $0 to check or any number below/equal to your balance to bet.");
        int bet = takeInput(false, 0);
        if (bet == 0) { // Player one limps
            System.out.println(playerOneName + " checks!");
            System.out.println(playerTwoName + ", now you have an opportunity to check back or bet.");
            bet = takeInput(true, 0);
            if (bet == 0) { // Player two limps back
                System.out.println(playerTwoName + " checks back!");
                playerOneFunds += potSize / 2;
                playerTwoFunds += potSize / 2;
            } else { // Player two raises the limp
                if (bet > playerOneFunds) {
                    bet = playerOneFunds;
                    System.out.println("Your bet has been capped at " + playerOneName + "'s balance.");
                }
                playerTwoFunds -= bet;
                potSize += bet;
                System.out.println("Pot size: $" + potSize);
                System.out.println(playerTwoName + " raises to $" + bet + "!");
                System.out.println(playerOneName + ", you must match to stay in the hand.");
                String callOrFold = "";
                while (!callOrFold.equalsIgnoreCase("call") && !callOrFold.equals("fold")) {
                    System.out.print("Type \"call\" to call or \"fold\" to fold: ");
                    callOrFold = scanner.next();
                }
                if (callOrFold.equals("call")) { // Player one calls the raise
                    if (bet > playerTwoFunds) {
                        bet = playerTwoFunds;
                        System.out.println("Your bet has been capped at " + playerTwoFunds + "'s balance.");
                    }
                    System.out.println(playerOneName + " calls!");
                    playerOneFunds -= bet;
                    potSize += bet;
                    System.out.println("Pot size: $" + potSize);
                } else { // Player one folds
                    System.out.println(playerOneName + " FOLDS!");
                    playerTwoFunds += potSize;
                    return;
                }
            }
        } else { // Player one raises
            if (bet > playerTwoFunds) {
                bet = playerTwoFunds;
                System.out.println("Your bet has been capped at " + playerTwoName + "'s balance.");
            }
            playerOneFunds -= bet;
            potSize += bet;
            System.out.println("Pot size: $" + potSize);
            System.out.println(playerTwoName + ", the current bet is $" + bet);
            System.out.print("Would you like to fold? Type \"yes\" for yes and \"no\" for no: ");
            String fold = "";
            while (!fold.equalsIgnoreCase("yes") && !fold.equalsIgnoreCase("no")) {
                fold = scanner.next();
            }
            if (fold.equals("yes")) { // Player two folds
                System.out.println(playerTwoName + " FOLDS!");
                playerOneFunds += potSize;
                return;
            }
            System.out.println("No fold! Okay, now, would you like to call their bet or raise? Type your bet.");
            int redoneBet = takeInput(true, bet);
            if (redoneBet == bet) { // Player two calls the bet
                System.out.println(playerTwoName + " calls!");
                playerTwoFunds -= redoneBet;
                potSize += redoneBet;
                System.out.println("Pot size: $" + potSize);
            } else { // Player two re-raises
                if (redoneBet - bet > playerOneFunds) {
                    redoneBet = playerOneFunds;
                    System.out.println("Your bet has been capped at " + playerOneName + "'s balance.");
                }
                playerTwoFunds -= redoneBet;
                potSize += redoneBet;
                System.out.println("Pot size: $" + potSize);
                System.out.println(playerTwoName + " raises to $" + redoneBet + "!");
                System.out.println(playerOneName + ", you must match with $" + (redoneBet - bet) + " to stay in the hand.");
                String callOrFold = "";
                while (!callOrFold.equalsIgnoreCase("call") && !callOrFold.equals("fold")) {
                    System.out.print("Type \"call\" to call or \"fold\" to fold: ");
                    callOrFold = scanner.next();
                }
                if (callOrFold.equals("call")) { // Player one calls the re-raise
                    System.out.println(playerOneName + " calls!");
                    playerOneFunds -= redoneBet;
                    playerOneFunds += bet;
                    potSize += redoneBet - bet;
                    System.out.println("Pot size: $" + potSize);
                } else { // Player one folds
                    System.out.println(playerOneName + " FOLDS!");
                    playerTwoFunds += potSize;
                    return;
                }
            }
        }
        int betterHand = determineBetter(hand1, hand2);
        if (betterHand == 1) { // Player one wins
            System.out.println(playerOneName + " wins with " + hand1.determineHand() + " (a " + Hand.convertToHandRanking(Hand.evaluateHand(hand1.determineHand())) + ")");
            playerOneFunds += potSize;
        } else if (betterHand == 2) { // Player two wins
            playerTwoFunds += potSize;
            System.out.println(playerTwoName + " wins with " + hand2.determineHand() + " (a " + Hand.convertToHandRanking(Hand.evaluateHand(hand2.determineHand())) + ")");
        } else { // Tied hand
            playerOneFunds += potSize / 2;
            playerTwoFunds += potSize / 2;
            System.out.println("It's a tie with " + hand1 + " (a " + Hand.convertToHandRanking(Hand.evaluateHand(hand1.determineHand())));
        }
    }

    private int takeInput(boolean isPlayer2, int min) {
        // This method prevents repetition in the bets
        int funds = playerOneFunds;
        if (isPlayer2) {
            funds = playerTwoFunds;
        }
        int bet = -1;
        while (bet == -1) {
            System.out.print("$");
            try {
                bet = scanner.nextInt();
                if (bet < min || bet > funds) {
                    System.out.println("Your bet must be between $" + min + " and your balance ($" + funds + ").");
                    bet = -1;
                }
            } catch (Exception e) {
                System.out.println("Sorry, that was an invalid input.");
                scanner.next();
            }
        }
        return bet;
    }

    private void clear() {
        // Ensures players do not get confused (or cheat) by seeing opponent's hand/previous rounds
        for (int i = 0; i < 50; i++) System.out.println();
    }

    public static int determineBetter(Hand hand1, Hand hand2) {
        // 0 returned = a tie, 1 returned = hand1 better, 2 returned = hand2 better
        int storedHand1Score = Hand.evaluateHand(hand1.determineHand());
        int storedHand2Score = Hand.evaluateHand(hand2.determineHand());

        int betterHandNum = 0;
        if (storedHand1Score < storedHand2Score) {
            betterHandNum = 1;
        } else if (storedHand1Score > storedHand2Score) {
            betterHandNum = 2;
        } else {
            for (int j = 4; j >= 0; j--) {
                if (hand1.hand.get(j).getRankNum() > hand2.hand.get(j).getRankNum()) {
                    betterHandNum = 1;
                    break;
                } else if (hand1.hand.get(j).getRankNum() < hand2.hand.get(j).getRankNum()) {
                    betterHandNum = 2;
                    break;
                }
            }
        }
        return betterHandNum;
    }
}
