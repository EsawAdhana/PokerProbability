import java.util.ArrayList;

public class Hand {
    public ArrayList<Card> hand = new ArrayList<>();
    public ArrayList<Card> holeCards;
    public ArrayList<Card> communityCards;
    public Hand(Deck deck) {
        holeCards = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            holeCards.add(deck.deckOfCards.remove(deck.deckOfCards.size() - 1));
        }
        hand.addAll(holeCards);
    }

    @Override
    public String toString() {
        return String.valueOf(hand);
    }

    public ArrayList<Card> getCommunityCards(Deck deck) {
        // No real need to burn, but it's a nice touch.
        // Unlike real poker, all cards are dealt at once.
        communityCards = new ArrayList<>();
        communityCards.add(deck.deckOfCards.remove(deck.deckOfCards.size() - 1)); // Flop card #1
        communityCards.add(deck.deckOfCards.remove(deck.deckOfCards.size() - 1)); // Flop card #2
        communityCards.add(deck.deckOfCards.remove(deck.deckOfCards.size() - 1)); // Flop card #3
        deck.deckOfCards.remove(deck.deckOfCards.size() - 1); // Burned card
        communityCards.add(deck.deckOfCards.remove(deck.deckOfCards.size() - 1)); // The turn
        deck.deckOfCards.remove(deck.deckOfCards.size() - 1); // Burned card
        communityCards.add(deck.deckOfCards.remove(deck.deckOfCards.size() - 1)); // The river
        return communityCards;
    }

    public ArrayList<Card> determineHand() {
        // nCr(7, 5) dictates that there are 21 possible hands that can be made
        // Random hands are made until all 21 have been added to perms
        ArrayList<ArrayList<Card>> perms = new ArrayList<>();
        while (perms.size() < 21) {
            perms.add(generateHand());
            for (int i = 0; i < perms.size() - 1; i++) {
                if (perms.get(i).containsAll(perms.get(perms.size() - 1))) {
                    perms.remove(perms.size() - 1);
                }
            }
        }
        ArrayList<Card> bestHand = new ArrayList<>(perms.get(0));
        ArrayList<Card> currentHand;
        int bestHandRank = Integer.MAX_VALUE;
        for (int i = 1; i < perms.size(); i++) {
            currentHand = perms.get(i);
            if (evaluateHand(currentHand) < bestHandRank) { // Best hand is potentially changed
                bestHand.clear();
                bestHand.addAll(currentHand);
                bestHandRank = evaluateHand(currentHand);
            } else if (evaluateHand(currentHand) == bestHandRank) {
                for (int j = 4; j >= 0; j--) {
                    if (currentHand.get(j).getRankNum() > bestHand.get(j).getRankNum()) {
                        bestHand.clear();
                        bestHand.addAll(currentHand);
                        bestHandRank = evaluateHand(currentHand);
                    }
                }
            }
        }
        return bestHand; // Out of all possible hands, this is the best one (and highest kicker, if applicable)
    }

    private ArrayList<Card> generateHand() {
        // Randomly generated hands, 5 cards from 7
        ArrayList<Card> returnedHand = new ArrayList<>();
        ArrayList<Card> copiedWhole = new ArrayList<>(hand);
        for (int i = 0; i < 5; i++) {
            int randomIndex = (int) (Math.random() * (7 - i));
            returnedHand.add(copiedWhole.remove(randomIndex));
        }
        return returnedHand;
    }

    public static int evaluateHand(ArrayList<Card> hand) {
        // Checks for each condition but returns early intentionally (full house will not accidentally be a 3 of a kind or pair)
        HandHierarchy.sortByNum(hand);
        ArrayList<Integer> repeats = HandHierarchy.countRepeats(hand);
        if (HandHierarchy.isRoyalFlush(hand)) {
            return 1;
        } else if (HandHierarchy.isStraightFlush(hand)) {
            return 2;
        } else if (HandHierarchy.isFourOfAKind(repeats)) {
            return 3;
        } else if (HandHierarchy.isFullHouse(repeats)) {
            return 4;
        } else if (HandHierarchy.isFlush(hand)) {
            return 5;
        } else if (HandHierarchy.isStraight(hand)) {
            return 6;
        } else if (HandHierarchy.isThreeOfAKind(repeats)) {
            return 7;
        } else if (HandHierarchy.isTwoPair(repeats)) {
            return 8;
        } else if (HandHierarchy.isPair(repeats)) {
            return 9;
        }
        return 10;
    }
    public static String convertToHandRanking(int num) {
        // Easier to handle values
        switch (num) {
            case 1 -> { return "Royal Flush"; }
            case 2 -> { return "Straight Flush"; }
            case 3 -> { return "Four of a Kind"; }
            case 4 -> { return "Full House"; }
            case 5 -> { return "Flush"; }
            case 6 -> { return "Straight"; }
            case 7 -> { return "Three of a Kind"; }
            case 8 -> { return "Two Pair"; }
            case 9 -> { return "Pair"; }
            default -> { return "High Card"; }
        }
    }
}
