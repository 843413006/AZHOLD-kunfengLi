//kunfeng Li
import java.util.*;

public class AZHoldem {

    private Deck deck;
    private List<Player> players;
    private List<Card> communityCards;
    private Scanner scanner;

    public AZHoldem() {
        deck = new Deck();
        players = new ArrayList<>();
        communityCards = new ArrayList<>();
        scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.print("How many players? ");
        int numberOfPlayers = scanner.nextInt();
        initializePlayers(numberOfPlayers);
        while (true) {
            deck.shuffle();
            dealInitialCards();
            dealCommunityCards();
            showGameState();
            determineWinner();
            if (!askForNextGame()) {
                break;
            }
            resetGame();
        }
        scanner.close();
    }

    private void initializePlayers(int numberOfPlayers) {
        players.clear();
        for (int i = 0; i < numberOfPlayers; i++) {
            players.add(new Player(100.00));
        }
    }

    private void dealInitialCards() {
        for (Player player : players) {
            player.addCard(deck.deal());
            player.addCard(deck.deal());
        }
    }

    private void dealCommunityCards() {
        communityCards.clear();
        for (int i = 0; i < 5; i++) {
            communityCards.add(deck.deal());
        }
    }

    private void showGameState() {
        System.out.println("Community Cards: " + communityCards);
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            System.out.println("Player " + (i + 1) + ": $" + player.getBalance() + " - " + player.getHand());
            Hand bestHand = player.determineBestHand(communityCards);
            System.out.println("    Best hand: " + bestHand.getCards() + "    " + bestHand.getRank());
        }
    }

    private void determineWinner() {
        List<Player> winners = new ArrayList<>();
        Hand bestHand = null;

        for (Player player : players) {
            Hand currentHand = player.determineBestHand(communityCards);
            if (bestHand == null || currentHand.compareTo(bestHand) > 0) {
                bestHand = currentHand;
                winners.clear();
                winners.add(player);
            } else if (currentHand.compareTo(bestHand) == 0) {
                winners.add(player);
            }
        }

        if (winners.size() == 1) {
            Player winner = winners.get(0);
            System.out.println("Winner: Player " + (players.indexOf(winner) + 1) + " " + winner.getBalance());
            System.out.println(bestHand.getCards() + "    " + bestHand.getRank());
        } else {
            System.out.println("Winning hands (tie): ");
            for (Player winner : winners) {
                System.out.println("Player " + (players.indexOf(winner) + 1) + " " + winner.getBalance());
                System.out.println(bestHand.getCards() + "    " + bestHand.getRank());
            }
        }
    }

    private boolean askForNextGame() {
        System.out.print("Play another game? <y or n> ");
        String answer = scanner.next();
        return answer.equalsIgnoreCase("y");
    }

    private void resetGame() {
        deck = new Deck();
        communityCards.clear();
        for (Player player : players) {
            player.clearHand();
        }
    }

    public static void main(String[] args) {
        AZHoldem azHoldem = new AZHoldem();
        azHoldem.start();
    }
}

class Card implements Comparable<Card> {
    private String suit;
    private String rank;

    private static final Map<String, Integer> RANK_VALUE = new HashMap<>();

    static {
        RANK_VALUE.put("2", 2);
        RANK_VALUE.put("3", 3);
        RANK_VALUE.put("J", 11);
        RANK_VALUE.put("Q", 12);
        RANK_VALUE.put("K", 13);
        RANK_VALUE.put("A", 14);
    }

    public Card(String rank, String suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public String getSuit() {
        return suit;
    }

    public String getRank() {
        return rank;
    }

    @Override
    public int compareTo(Card other) {
        return Integer.compare(RANK_VALUE.get(this.rank), RANK_VALUE.get(other.rank));
    }

    @Override
    public String toString() {
        return rank + " of " + suit;
    }
}

class Deck {
    private List<Card> cards;

    public Deck() {
        cards = new ArrayList<>();
        String[] suits = {"Hearts", "Clubs", "Diamonds", "Spades"};
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};

        for (String suit : suits) {
            for (String rank : ranks) {
                cards.add(new Card(rank, suit));
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card deal() {
        return cards.isEmpty() ? null : cards.remove(cards.size() - 1);
    }
}

class Player {
    private List<Card> hand;
    private double balance;

    public Player(double balance) {
        this.balance = balance;
        hand = new ArrayList<>();
    }

    public void addCard(Card card) {
        if (card != null) {
            hand.add(card);
        }
    }

    public List<Card> getHand() {
        return hand;
    }

    public double getBalance() {
        return balance;
    }

    public void updateBalance(double amount) {
        this.balance += amount;
    }

    public void clearHand() {
        hand.clear();
    }

    public Hand determineBestHand(List<Card> communityCards) {
        return new Hand(HandRank.HIGH_CARD, new ArrayList<>(this.hand));
    }
}

enum HandRank {
    STRAIGHT_FLUSH, FOUR_OF_A_KIND, FULL_HOUSE, FLUSH, STRAIGHT,
    THREE_OF_A_KIND, TWO_PAIR, ONE_PAIR, HIGH_CARD
}

class Hand implements Comparable<Hand> {
    private HandRank rank;
    private List<Card> cards;

    public Hand(HandRank rank, List<Card> cards) {
        this.rank = rank;
        this.cards = cards;
    }

    public HandRank getRank() {
        return rank;
    }

    public List<Card> getCards() {
        return cards;
    }

    @Override
    public int compareTo(Hand other) {
        return this.rank.compareTo(other.rank);
    }
}
