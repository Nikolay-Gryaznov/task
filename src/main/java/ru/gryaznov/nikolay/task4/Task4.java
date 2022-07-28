package ru.gryaznov.nikolay.task4;

import com.github.davidmoten.bigsorter.Serializer;
import com.github.davidmoten.bigsorter.Sorter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/*
 * Необходимо отсортировать csv-файл по первому полю, можно считать, что оно целочисленное. Первое поле (ключ)
 * может иметь не уникальное значение. Длинны каждой из строк могут отличаться.
 * Попробуйте привести два решения:
- одно для случаев, когда данные файла целиком помещаются в оперативную память
- и второе, когда размер файла на порядки превышает объем доступной оперативной памяти.
 */
public class Task4 {
    /**
     * Метод для сортировки csv файла в оперативной памяти.
     * Сложность данного решения O(n^2), из всех сложность берётся самая, здесь это метод split O(m * n) в цикле while 0(n).
     *
     * @param inpFilePath    - Путь к файлу, который нужно отсортировать.
     * @param separatingMark - Символ, разделяющий данные в строке cvs файла.
     * @param outFilePath    -  Путь к файлу, который нужен для вывода отсортированных данных.
     */
    public static void sortCsvFileInMemory(String inpFilePath, String separatingMark, String outFilePath) {
        BufferedReader bufferedReader = null;
        String firstCsvString = "";
        try {
            bufferedReader = new BufferedReader(new FileReader(inpFilePath));
        } catch (FileNotFoundException e) {
            System.out.println("File " + inpFilePath + " not found. " + e);
        }
        Map<String, List<String>> map = new TreeMap<>();
        try {
            firstCsvString = bufferedReader.readLine();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String key = line.split(separatingMark)[0];
                List<String> l = map.get(key);
                if (l == null) {
                    l = new LinkedList<>();
                    map.put(key, l);
                }
                l.add(line);
            }
            bufferedReader.close();
        } catch (IOException e) {
            System.out.println("An I/O exception occurred while reading a file. " + e);
        }

        try {
            FileWriter fileWriter = new FileWriter(outFilePath);
            fileWriter.write(firstCsvString + "\n");
            for (List<String> list : map.values()) {
                for (String str : list) {
                    fileWriter.write(str);
                    fileWriter.write("\n");
                }
            }
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("An I/O exception occurred while writing to a file. " + e);
        }
    }

    /**
     * Метод для сортировки большого csv файла c использованием класса LargeCsvFileSorter.
     *
     * @param inpFilePath    - Путь до файла, который нужно отсортировать.
     * @param outFilePath    - Путь до файла, в который запишутся отсортированные данные.
     * @param tempDirectory  - Путь до места, где будет исполняться алгоритм.
     * @param separatingMark - Символ, разделяющий данные в строке cvs файла.
     */
    public static void csvFileSorting(String inpFilePath, String outFilePath, String tempDirectory, String separatingMark) {
        LargeCsvFileSorter streamSorter = new LargeCsvFileSorter(separatingMark);
        streamSorter.setTempDirectory(tempDirectory);
        InputStream targetStream = null;
        try {
            targetStream = new FileInputStream(inpFilePath);
        } catch (FileNotFoundException e) {
            System.out.println("Ее не получилось перевести (начало)");
        }
        streamSorter.splitChunks(targetStream);
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(outFilePath);
        } catch (FileNotFoundException e) {
            System.out.println("Ее не получилось перевести (конец)");
        }
        streamSorter.mergeChunks(outputStream);
    }

    /**
     * Метод для сортировки csv файла.
     * Библиотека использует сортировку слиянием, что значить что сложность O(n log(n))
     *
     * @param inpFilePath    - Путь к файлу, который нужно отсортировать.
     * @param separatingMark - Символ, разделяющий данные в строке cvs файла.
     * @param outFilePath    -  Путь к файлу, который нужен для вывода отсортированных данных.
     */
    public static void sortCsvFile(String inpFilePath, char separatingMark, String outFilePath) {
        Serializer<CSVRecord> serializer = Serializer.csv(
                CSVFormat
                        .newFormat(separatingMark)
                        .withRecordSeparator("\n")
                        .withFirstRecordAsHeader(),
                StandardCharsets.UTF_8);
        Comparator<CSVRecord> comparator = (x, y) -> {
            int a = Integer.parseInt(x.get(0));
            int b = Integer.parseInt(y.get(0));
            return Integer.compare(a, b);
        };

        Sorter
                .serializer(serializer)
                .comparator(comparator)
                .input(new File(inpFilePath))
                .output(new File(outFilePath))
                .sort();
    }
}
