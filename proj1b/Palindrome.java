public class Palindrome {
    public static Deque<Character> wordToDeque(String word) {
        ArrayDequeSolution<Character> charArr = new ArrayDequeSolution<>();
        for (int i = 0; i < word.length(); i++) {
            charArr.addLast((Character) word.charAt(i));
        }
        return charArr;
    }

    public static boolean isPalindrome(String word) {
        if (word == null || word.length() <= 1) {
            return true;
        }
        Deque<Character> wordDeque = wordToDeque(word);
        while (wordDeque.size() > 1) {
            if (wordDeque.removeFirst() != wordDeque.removeLast()) {
                return false;
            }
        }
        return true;
    }

    public static boolean isPalindrome(String word, CharacterComparator cc) {
        if (word == null || word.length() <= 1) {
            return true;
        }
        Deque<Character> wordDeque = wordToDeque(word);
        while (wordDeque.size() > 1) {
            if (!cc.equalChars(wordDeque.removeFirst(), wordDeque.removeLast())) {
                return false;
            }
        }
        return true;
    }
}
