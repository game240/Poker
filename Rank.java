import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Rank {
    /** 
     * rank: 숫자가 클수록 높은 순위
     * 총 12자리:
     * 1~2: 족보
     * 3~4: 페어(1) / 카드(1)
     * 5~6: 페어(2) / 카드(2)
     * 7~8: 하이 카드(1) / 카드(3)
     * 9~10: 하이 카드(2) / 카드(4)
     * 11~12: 하이 카드(3) / 카드(5)
     * CATEGORY_BASE: 11자리
     */ 
    public static final long CATEGORY_BASE = 1_00_00_00_00_00L;

    public static String rankToString(long rank) {
        switch ((int) (rank / 1_00_00_00_00)) {
            case 10:
                return "Straight Flush";
            case 9:
                return "Four of a Kind";
            case 8:
                return "Full House";
            case 7:
                return "Flush";
            case 6:
                return "Straight";
            case 5:
                return "Three of a Kind";
            case 4:
                return "Two Pair";
            case 3:
                return "One Pair";
            default:
                return "High Card";
        }
    }

    public static long cardRank(LinkedList<Card> cards) {
        /*
         * 다섯 장을 판별하는 방법:
         * 1번째 카드: 2~5번째 카드와 비교
         * 2번째 카드: 3~5번째 카드와 비교
         * 3번째 카드: 4~5번째 카드와 비교
         * 4번째 카드: 5번째 카드와 비교
         * 
         * i == 0, j == 1~4
         * i == 1, j == 2~4
         * i == 2, j == 3~4
         * i == 3, j == 4
         */
        int pairCount = 0;
        for (int i = 0; i < 4; ++i) {
            for (int j = i + 1; j < 5; ++j) {
                if (cards.get(i).number == cards.get(j).number) {
                    pairCount++;
                    /*
                     * 원페어: 1
                     * 투페어: 2
                     * 트리플: 3
                     * 풀하우스: 4 ∵ 3 + 1
                     * 포카드: 6 ∵ 2s, 2h, 2d, 2c, 3c일 때 똑같은 숫자의 쌍: 2s-2h, 2s-2d, 2s-2c, 2h-2d, 2h-2c, 2d-2c
                     */
                }
            }
        }

        // 숫자별 정렬
        LinkedList<Integer> sortedNum = new LinkedList<>();
        for (int i = 0; i < 5; ++i) {
            sortedNum.add(cards.get(i).number); // sortedNum에 값 복사: cards의 number값
        }
        Collections.sort(sortedNum);        

        // straight 여부 판정
        boolean straight = false;
        if (pairCount == 0) {
            // [1, 2, 3, 4, 5], [2, 3, 4, 5, 6], …, [9, 10, 11, 12, 13]: 가장 큰 수와 가장 작은 수의 차가 4
            if (sortedNum.get(4) - sortedNum.get(0) == 4) {
                straight = true;
            }
            if (sortedNum.get(0) == 1 && sortedNum.get(1) == 10) { // [A, 10, J, Q, K]
                straight = true;
            }
        }

        // flush 여부 판정
        LinkedList<String> sortedSuit = new LinkedList<>(); // suit: 문양별 정렬
        for (int i = 0; i < 5; ++i) {
            sortedSuit.add(cards.get(i).suit); // sortedSuit에 값 복사: cards의 suit값
        }
        Collections.sort(sortedSuit);

        // 문양별로 정렬한 후 suit[0]과 suit[4]가 같을 경우, index 0~4까지 같은 문양
        boolean flush = false;
        if (sortedSuit.get(0).equals(sortedSuit.get(4))) {
            flush = true;
        }

        long rank = 2 * CATEGORY_BASE;
        if (straight && flush) {
            rank = 10 * CATEGORY_BASE;
        } else if (pairCount == 6) { // 포카드
            rank = 9 * CATEGORY_BASE;
        } else if (pairCount == 4) { // 풀하우스
            rank = 8 * CATEGORY_BASE;
        } else if (flush) {
            rank = 7 * CATEGORY_BASE;
        } else if (straight) {
            rank = 6 * CATEGORY_BASE;
        } else if (pairCount == 3) {
            rank = 5 * CATEGORY_BASE;
        } else if (pairCount == 2) {
            rank = 4 * CATEGORY_BASE;
        } else if (pairCount == 1) {
            rank = 3 * CATEGORY_BASE;
        } else {                     // 하이 카드(족보)
            rank = 2 * CATEGORY_BASE;
        }

        // highCard 정보를 덧셈 형식으로 추가, 형식 예시: rank = 700 + highCard;
        rank += highCard(sortedNum, rank);

        return rank;
    }

    // 각 족보의 하이 카드 return: 같은 rank일 때 족보를 따져보는 용도
    public static long highCard(LinkedList<Integer> orgSortedNum, long rank) {
        LinkedList<Integer> sortedNum = new LinkedList<>(orgSortedNum);
        
        // 계산의 편리를 위해 sortedNum의 1(A) -> 14로 변환
        for (int i = 0; i < sortedNum.size(); ++i) {
            if (sortedNum.get(i) == 1) {
                sortedNum.set(i, 14);
            }
        }
        Collections.sort(sortedNum);

        int tempRank = (int)(rank / CATEGORY_BASE); // 족보만 추출
        switch (tempRank) {
            // 스트레이트 플러시, 스트레이트
            case 10:
            case 6:
                // [A, 2, 3, 4, 5]의 경우 하이 카드: 5
                if (sortedNum.get(0) == 2 && 
                    sortedNum.get(1) == 3 && 
                    sortedNum.get(2) == 4 && 
                    sortedNum.get(3) == 5 && 
                    sortedNum.get(4) == 14) {
                    return 5;
                }
                // 이외의 경우 하이 카드: getLast()
                return sortedNum.getLast();
            // 플러시, 하이 카드: 5장 모두 비교 필요
            case 7:
            case 2:
                return (sortedNum.get(4) * 1_00_00_00_00L + 
                        sortedNum.get(3) * 1_00_00_00L + 
                        sortedNum.get(2) * 1_00_00L + 
                        sortedNum.get(1) * 1_00L + 
                        sortedNum.get(0));
            // 풀하우스
            case 8:
                /* 
                * 풀하우스: 트리플 + 투페어, 트리플의 족보 > 투페어의 족보
                * 2 2 2 9 9 < 3 3 4 4 4
                * -> 3~4: 트리플, 5~6: 페어
                * 
                * 어떤 수가 트리플을 이루는지 확인
                * arr1: 2 2 2 9 9 < arr2: 3 3 4 4 4,
                * arr1[0] == arr1[2], arr2[0] != arr2[2]
                */
                if (sortedNum.get(0) == sortedNum.get(2)) {
                    return (sortedNum.get(2) * 1_00_00_00_00L + sortedNum.get(4) * 1_00_00_00L);
                } else {
                    return (sortedNum.get(2) * 1_00_00_00_00L + sortedNum.get(0) * 1_00_00_00L);
                }
            // 포카드, 트리플, 페어 등 5장 내에서 판단하지 않을 경우
            // 포카드: 5 5 5 5 6 / 5 6 6 6 6
            case 9:
                // 5 5 5 5 6일 경우
                if (sortedNum.get(0) == sortedNum.get(3)) {
                    return (sortedNum.get(0) * 1_00_00_00_00L + sortedNum.get(4) * 1_00_00_00L); // 포카드 + 키커
                // 5 6 6 6 6일 경우
                } else { 
                    return (sortedNum.get(4) * 1_00_00_00_00L + sortedNum.get(0) * 1_00_00_00L); // 포카드 + 키커
                }
            // 트리플: 3 3 3 4 5 / 3 4 4 4 5 / 3 4 5 5 5
            case 5:
                // 3 3 3 4 5일 경우
                if (sortedNum.get(0) == sortedNum.get(2)) {
                    // 트리플 값 + 키커1 + 키커2
                    return (sortedNum.get(0) * 1_00_00_00_00L + 
                            sortedNum.get(4) * 1_00_00_00L + 
                            sortedNum.get(3) * 1_00_00L);
                } else if (sortedNum.get(1) == sortedNum.get(3)) {
                // 3 4 4 4 5일 경우 
                    return (sortedNum.get(1) * 1_00_00_00_00L + 
                            sortedNum.get(4) * 1_00_00_00L + 
                            sortedNum.get(0) * 1_00_00L);
                } else { // 3 4 5 5 5일 경우
                    return (sortedNum.get(2) * 1_00_00_00_00L + 
                            sortedNum.get(1) * 1_00_00_00L +
                            sortedNum.get(0) * 1_00_00L);
                }
            // 원 페어: 2 2 3 4 5 / 2 3 3 4 5 / 2 3 4 4 5 / 2 3 4 5 5
            case 3:
                List<Integer> kickers = new ArrayList<>();
                int pairIndex = -1;
                // 페어 찾기
                for (int i = 0; i < 4; i++) {
                    if (sortedNum.get(i) == sortedNum.get(i + 1)) {
                        pairIndex = sortedNum.get(i);
                        break;
                    }
                }
                // 킥커 수집
                for (int i = 0; i < 5; i++) {
                    if (sortedNum.get(i) != pairIndex) {
                        kickers.add(sortedNum.get(i));
                    }
                }
                Collections.sort(kickers);
                // 페어 + 3개 킥커        
                return (pairIndex * 1_00_00_00_00L + 
                        kickers.get(2) * 1_00_00_00L + 
                        kickers.get(1) * 1_00_00L + 
                        kickers.get(0) * 1_00L); 
            // 투 페어: 2 2 3 3 4 / 2 2 3 4 4 / 2 3 3 4 4
            case 4:              
                int firstPair, secondPair, kicker;
                // 2 2 3 3 4일 경우
                if (sortedNum.get(0) == sortedNum.get(1) && sortedNum.get(2) == sortedNum.get(3)) {
                    firstPair = sortedNum.get(2);
                    secondPair = sortedNum.get(0);
                    kicker = sortedNum.get(4);
                // 2 2 3 4 4일 경우
                } else if (sortedNum.get(0) == sortedNum.get(1) && 
                            sortedNum.get(3) == sortedNum.get(4)) { 
                    firstPair = sortedNum.get(3);
                    secondPair = sortedNum.get(0);
                    kicker = sortedNum.get(2);
                } else { // 2 3 3 4 4일 경웅
                    firstPair = sortedNum.get(4);
                    secondPair = sortedNum.get(2);
                    kicker = sortedNum.get(0);
                }
                return (firstPair * 1_00_00_00_00L + 
                        secondPair * 1_00_00_00L + 
                        kicker * 1_00_00L); // 페어1 > 페어2 > 키커
            }
            return 0;
        }
    
    public static long bestRank(LinkedList<Card> cards) {
        long bestRank = 2 * CATEGORY_BASE; // 가장 낮은 랭크로 초기화
        /*
         * 7장의 카드 중 5장을 선택하는 모든 조합을 확인
         * i = 0, j = 1 ~ 6까지 검사
         * i = 1, j = 2 ~ 6까지 검사
         * ... 
         * i = 5, j = 6까지 검사
         */ 
    
        for (int i = 0; i < cards.size(); ++i) {
            for (int j = i + 1; j < cards.size(); ++j) {
                LinkedList<Card> temp = new LinkedList<>(cards); // temp에 cards의 값 복사
                
                // i, j의 카드 제외: 뒤의 index부터 제거해야 오류 X
                temp.remove(j);
                temp.remove(i);

                long rank = cardRank(temp); // 선별된 5장의 카드 랭크 계산
                if (rank > bestRank) {
                    bestRank = rank; // 더 좋은 랭크가 나오면 bestRank에 업데이트
                }
            }
        }
        return bestRank;
    }
    
    public static List<Integer> determineWinner(Player[] players) {
        long maxRank = 0; // 최대 랭크값 초기화
        List<Integer> winnersIndex = new ArrayList<>(); // 우승자의 인덱스를 저장할 리스트
    
        // 각 플레이어의 최종 순위를 계산하여 최대 랭크를 찾음
        for (int i = 0; i < players.length; i++) {
            long rank = bestRank(players[i].hands); // 최종 랭크 계산
            if (rank > maxRank) {
                maxRank = rank; // 더 높은 순위 발견 시 최대 랭크 업데이트
                winnersIndex.clear(); // 이전 우승자 인덱스 초기화
                winnersIndex.add(i); // 현재 플레이어를 우승자로 설정
            } else if (rank == maxRank) {
                winnersIndex.add(i); // 최대 랭크를 가진 플레이어를 우승자로 추가
            }
        }
        
        return winnersIndex;
        
    }
}
