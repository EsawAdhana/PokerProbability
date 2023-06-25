import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    public final ArrayList<Card> deckOfCards = new ArrayList<>();

    public Deck() {
        setDeck();
        shuffle(); // Uses Collections shuffle
    }
    public void setDeck() {
        // Adds all the cards, not shuffled
        deckOfCards.add(new Card(14, "♠"));
        for (int i = 2; i < 14; i++) {
            deckOfCards.add(new Card(i, "♠"));
        }

        deckOfCards.add(new Card(14, "♥"));
        for (int i = 2; i < 14; i++) {
            deckOfCards.add(new Card(i, "♥"));
        }

        deckOfCards.add(new Card(14, "♦"));
        for (int i = 2; i < 14; i++) {
            deckOfCards.add(new Card(i, "♦"));
        }

        deckOfCards.add(new Card(14, "♣"));
        for (int i = 2; i < 14; i++) {
            deckOfCards.add(new Card(i, "♣"));
        }
    }

    public void shuffle() { Collections.shuffle(deckOfCards); }
}
