package ru.gryaznov.nikolay.task2;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Реализуйте 2 метода для преобразования строки:
- один - в целочисленное значение элементарного типа (int)
- другой метод в число с плавающей запятой (double), так же элементарного типа.
* Необходимо реализовать собственные методы, без использования аналогичных методов из Java (если знаете аналогичные
* Java методы, пожалуйста, перечислите их).
 */
public class Task2 {
    /**
     * Метод, преобразующий строку в целочисленное значение типа int. Это аналог, метода Integer.parseInt() в Java.
     * Суть метода заключается в получении цифры через ASCII, при вычитание символа '0' получается абсолютное значение
     * этого символа в виде целого числа.
     * @param intNumber - Целое число, представленное в строчном виде.
     * @return - Целое число, преобразованное из строчного вида.
     */
    public static int stringToInt(String intNumber){
        /*
         * Проверка на правильность строчного представления целого числа (int)
         */
        Pattern pattern = Pattern.compile("^[-+]?(0|[0-9][0-9]{0,8}|[1][0-9]{1,9}|[-][2]([0][0-9]{8}|[1]([0-3][0-9]{7}|[4]([0-6][0-9]{6}|[7]([0-3][0-9]{5}|[4]([0-7][0-9]{4}|[8]([0-2][0-9]{3}|[3]([0-5][0-9]{2}|[6]([0-3][0-9]|[4][0-8]))))))))|(\\+)?[2]([0][0-9]{8}|[1]([0-3][0-9]{7}|[4]([0-6][0-9]{6}|[7]([0-3][0-9]{5}|[4]([0-7][0-9]{4}|[8]([0-2][0-9]{3}|[3]([0-5][0-9]{2}|[6]([0-3][0-9]|[4][0-7])))))))))");
        Matcher matcher = pattern.matcher(intNumber);
        if (!matcher.matches()) throw new NumberFormatException("For input string: " + intNumber);
        if (intNumber.charAt(0)=='+') {
            intNumber = intNumber.substring(1);
        }
        int resultNumber = 0;
        int position = 1;
        boolean isNegativeNumber = false;
        if (intNumber.charAt(0)=='-') {
            intNumber = intNumber.substring(1);
            isNegativeNumber = true;
        }
        for (int i = intNumber.length()-1; i>=0; i--) {
            resultNumber += (intNumber.charAt(i)-'0')*position;
            position*=10;
        }
        if (isNegativeNumber) resultNumber*=-1;
        return resultNumber;
    }

    /**
     * Метод, преобразующий строку в дробное значение типа double. Это аналог, метода Double.parseDouble() в Java.
     * Суть метода заключается в получении цифры через ASCII, при вычитание символа '0' получается абсолютное значение
     * этого символа в виде целого числа, с учётом дробной части и возможной экспоненциальной формы.
     * @param doubleNumber - Дробное число, представленное в строчном виде.
     * @return - Дробное число, преобразованное из строчного вида.
     */
    public static double stringToDouble(String doubleNumber) {
        //Проверка на правильность строчного представления дробного числа (double)
        Pattern pattern = Pattern.compile("^[-+]?[0-9]*?[.]?[0-9]+[.]?([eE][-+]?[0-9]+)?$");
        Matcher matcher = pattern.matcher(doubleNumber);
        if (!matcher.matches()) throw new NumberFormatException();
        if (doubleNumber.charAt(0)=='+') {
            doubleNumber = doubleNumber.substring(1);
        }
        double resultNumber = 0;

        long position = 1;
        long pointPosition = 0;
        int eDegree = 0;
        boolean isNegativeNumber = false;
        if (doubleNumber.charAt(0)=='-') {
            doubleNumber = doubleNumber.substring(1);
            isNegativeNumber = true;
        }
        for (int i = doubleNumber.length()-1; i>=0; i--) {
            if (doubleNumber.charAt(i)=='.'){
                if (resultNumber!=0){
                    if (i==0) pointPosition = position;
                    else pointPosition = i;
                }
                continue;
            }
            if (doubleNumber.charAt(i)=='-' || doubleNumber.charAt(i)=='+'){
                if (doubleNumber.charAt(i)=='-'){
                    eDegree = (int) -resultNumber;
                } else eDegree = (int) resultNumber;
                position = 1;
                resultNumber = 0;
                i-=1;
                continue;
            }
            if (doubleNumber.charAt(i)=='e' || doubleNumber.charAt(i)=='E'){
                eDegree = (int) resultNumber;
                position = 1;
                resultNumber = 0;
                continue;
            }
            resultNumber += (doubleNumber.charAt(i)-'0')*position;
            position*=10;
        }
        if (isNegativeNumber) resultNumber*=-1;
        if (pointPosition!=0 ) {
            if (pointPosition != position){
                position/=Math.pow(10, pointPosition);
            }
            resultNumber/=position;
        }
        if (eDegree!=0)  resultNumber*=Math.pow(10, eDegree);
        return resultNumber;
    }

    /**
     * Метод, преобразующий строку в дробное значение типа double.
     * Суть метода преобразование строки через класс BigDecimal.
     * @param doubleNumber - Дробное число, представленное в строчном виде.
     * @return - Дробное число, преобразованное из строчного вида.
     */
    public static double stringToDoubleWithJava(String doubleNumber) {
        return new BigDecimal(doubleNumber).doubleValue();
    }
}
