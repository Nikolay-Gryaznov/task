package ru.gryaznov.nikolay.task3;

/*
 * Напишите простую программу, которая выводит на экран числа от 1 до 100 включительно. При этом вместо чисел,
 * кратных трем, программа должна выводить слово Fizz, а вместо чисел, кратных пяти — слово Buzz. Если число
 * кратно и трем, и пяти, то программа должна выводить слово FizzBuzz.
 */
public class Task3 {
    /**
     * Метод выводящий числа от 1 до 100. При числах кратным 3 выводит Fizz, при числах кратным 5 выводит Buzz,
     * если число кратно 3 и 5, то выводит FizzBuzz
     */
    public static void numberOutput(){
        String currentValue;
        for (int i = 1; i <=100 ; i++) {
            currentValue="";
            if (i % 3 == 0) currentValue+="Fizz";
            if (i % 5 == 0) currentValue+="Buzz";
            if (currentValue.equals("")) currentValue = String.valueOf(i);
            System.out.println(currentValue);
        }
    }
}
