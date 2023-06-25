public class Card {
    private final String rank;
    private final int rankNum;
    private final String suit;
    public Card(int rank, String suit) {
        this.rankNum = rank;
        this.rank = getRank(rank);
        this.suit = suit;
    }
    private String getRank(int rank) {
        String toReturn;
         if (rank == 11) toReturn = "Jack";
         else if (rank == 12) toReturn = "Queen";
         else if (rank == 13) toReturn = "King";
         else if (rank == 1 || rank == 14) toReturn = "Ace"; // Allows for low or high ace
         else toReturn = String.valueOf(rank);
         return toReturn;
    }
    @Override
    public String toString() { return rank + " of " + suit;}
    public int getRankNum() {
        return rankNum;
    }
    public String getSuit() {
        return suit;
    }



}
