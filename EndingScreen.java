import java.util.List;

class EndingScreen extends Screen{
    // 플레이어 별 rank를 담은 배열 만드는 함수(n은 플레이어 수)
    static String[] makeRankArr(Player[] players,int n){
        String[] playersRank = new String[n];
        
        for (int i = 0; i < playersRank.length; ++i) {
        long rank = Rank.bestRank(players[i].hands);
            playersRank[i] = Rank.rankToString(rank);
        }
        return playersRank;
    }

    public static void printScreen(Player[] players) {
        List<Integer> winnersIndex = Rank.determineWinner(players);
        String[] playersRank = makeRankArr(players, Poker.numPlayers);

        // 우승자 출력
        if (winnersIndex.size() == 1) {
            // 우승자 처리 로직
            System.out.println("\n                                   WINNER IS PLAYER " + (winnersIndex.get(0) + 1) + "!(" + playersRank[winnersIndex.get(0)] + ")");
            MainGameScreen.printCardFrame(players[winnersIndex.get(0)], winnersIndex.get(0), 5);
        } 
        
        else {
            // 동점자 처리 로직
            System.out.print("\n                                   TIE WINNERS ARE: ");
                for (int i = 0; i < winnersIndex.size(); i++) {
                    System.out.print("PLAYER " + (winnersIndex.get(i) + 1) + "(" + playersRank[winnersIndex.get(0)] + ")" + ", ");
                }
                System.out.println("!\n");

                for (int i = 0; i < winnersIndex.size(); i++) {
                    MainGameScreen.printCardFrame(players[winnersIndex.get(i)], winnersIndex.get(i), 5);
                }
            
        }
    }
}