import java.util.ArrayList;

public class HandHierarchy {
    public static ArrayList<Integer> countRepeats(ArrayList<Card> hand) {
        // Very helpful method used to check for pairs, sets, full houses, etc.
        ArrayList<Integer> savedRepeats = new ArrayList<>();
        ArrayList<Card> saveRemove = new ArrayList<>(hand);
        for (int i = 0; i < saveRemove.size(); i++) {
            int currentCount = 1;
            for (int j = i + 1; j < saveRemove.size(); j++) {
                if (saveRemove.get(i).getRankNum() == saveRemove.get(j).getRankNum()) {
                    currentCount++;
                    saveRemove.remove(j);
                    j--;
                }
            }
            savedRepeats.add(currentCount);
        }
        return savedRepeats;
    }
    public static void sortByNum(ArrayList<Card> hand) {
        // Sorts the cards from low to high
        int i;
        int j;
        for (i = 1; i < 5; i++) {
            Card tmp = hand.get(i);
            j = i;
            while ((j > 0) && (hand.get(j - 1).getRankNum() > tmp.getRankNum())) {
                hand.set(j, hand.get(j - 1));
                j--;
            }
            hand.set(j, tmp);
        }
    }

    public static boolean isRoyalFlush(ArrayList<Card> hand) {
        // Checks for a straight flush beginning with a 10
        if (hand.get(0).getRankNum() == 10) {
            return isStraightFlush(hand);
        }
        return false;
    }

    public static boolean isStraightFlush(ArrayList<Card> hand) {
        // Checks for straight and flush
        return isStraight(hand) && isFlush(hand);
    }

    public static boolean isFourOfAKind(ArrayList<Integer> repeats) {
        // Checks for 4-of-a-kind (kicker does not matter here)
        return repeats.contains(4);
    }

    public static boolean isFullHouse(ArrayList<Integer> repeats) {
        // Checks for 3-of-a-kind and pair
        return repeats.contains(3) && repeats.contains(2);
    }

    public static boolean isFlush(ArrayList<Card> hand) {
        // Checks if every card has same suit (i.e. a flush)
        return hand.get(0).getSuit().equals(hand.get(1).getSuit()) && hand.get(0).getSuit().equals(hand.get(2).getSuit())
                && hand.get(0).getSuit().equals(hand.get(3).getSuit()) && hand.get(0).getSuit().equals(hand.get(4).getSuit());
    }

    public static boolean isStraight(ArrayList<Card> hand) {
        // Checks for incrementing number (i.e. a straight)
        boolean isHighStraight = true;
        if (hand.get(4).getRankNum() == 14 && hand.get(0).getRankNum() == 2) { // Potential ace low straight check
            boolean isLowStraight = true;
            ArrayList<Card> aceLow = new ArrayList<>(hand);
            aceLow.set(4, new Card(1, hand.get(0).getSuit()));
            HandHierarchy.sortByNum(aceLow);
            for (int i = 1; i < aceLow.size(); i++) {
                if (aceLow.get(i - 1).getRankNum() + 1 != aceLow.get(i).getRankNum()) {
                    isLowStraight = false;
                    break;
                }
            }
            return isLowStraight;
        }
        for (int i = 1; i < hand.size(); i++) { // Regular straight check
            if (hand.get(i - 1).getRankNum() + 1 != hand.get(i).getRankNum()) {
                isHighStraight = false;
                break;
            }
        }
        return isHighStraight;
    }


    public static boolean isThreeOfAKind(ArrayList<Integer> repeats) {
        // Checks for a 3-of-a-kind
        return repeats.contains(3);
    }

    public static boolean isTwoPair(ArrayList<Integer> repeats) {
        // Checks for a pair and then "removes" that pair to check again
        ArrayList<Integer> saveRemove = new ArrayList<>(repeats);
        if (isPair(repeats)) {
            saveRemove.remove((Integer) 2);
            return isPair(saveRemove);
        }
        return false;
    }

    public static boolean isPair(ArrayList<Integer> repeats) {
        // Checks for a pair
        return repeats.contains(2);
    }
}

