package ru.gryaznov.nikolay.task1;

/*
 * Реализуйте метод для замены заданного символа в строке (простой аналог метода replace класса String в Java).
 * На вход метод получает строку и символы, возвращает строку.
 */
public class Task1 {
    /**
     * Метод с функционалом метода replace() Java.
     * Суть метода заключается во временном разбиении строки на массив символов для посимвольное сравнения.
     *
     * @param str     - Строка, в которой нужно произвести замену символа.
     * @param oldChar - Символ, которой нужно заменить.
     * @param newChar - Символ, на который заменяется текущий символ.
     * @return - Строка с заменёнными символами.
     */
    public static String replaceMethod(String str, char oldChar, char newChar) {
        StringBuilder result = new StringBuilder();
        for (char ch : str.toCharArray()) {
            if (ch == oldChar) result.append(newChar);
            else result.append(ch);
        }
        return result.toString();
    }
}
