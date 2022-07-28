package ru.gryaznov.nikolay.task4;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * Класс для сортировки большого файла путем разбиения этого файла на несколько временных отсортированных файлов
 * и слияния этих файлов.
 */
public class LargeCsvFileSorter {

    private final Comparator<String> sorter = String::compareTo;
    private int maxChunkSize = 100000000;
    private List<File> outputs = new ArrayList<>();
    private String tempDirectory = "";
    private String separatingMark;


    public LargeCsvFileSorter(String separatingMark) {
        this.separatingMark = separatingMark;
    }

    /**
     * Метод для установки места для реализации алгоритма.
     *
     * @param directoryPath - Путь до места реализации алгоритма.
     */
    public void setTempDirectory(String directoryPath) {
        tempDirectory = directoryPath;
        File file = new File(tempDirectory);
        if (!file.exists() || !file.isDirectory()) {
            throw new IllegalArgumentException("Неверно указан путь до места реализации алгоритма");
        }
    }

    /**
     * Метод для установки размер фрагмента.
     *
     * @param size - максимальный размер фрагмента.
     */
    public void setMaximumChunkSize(int size) {
        this.maxChunkSize = size;
    }


    /**
     * Метод для прочтения входного IO потока и разбиения его на отсортированные фрагменты, которые записываются
     * во временные файлы.
     *
     * @param in - Входной поток.
     */
    public void splitChunks(InputStream in) {
        outputs.clear();
        BufferedReader br = null;
        List<String> lines = new ArrayList<>();
        try {
            br = new BufferedReader(new InputStreamReader(in));
            String line;
            int currChunkSize = 0;
            while ((line = br.readLine()) != null) {
                lines.add(line);
                currChunkSize += line.length() + 1;
                if (currChunkSize >= maxChunkSize) {
                    currChunkSize = 0;
                    lines.sort(sorter);
                    File file = new File(tempDirectory + "temp" + System.currentTimeMillis());
                    outputs.add(file);
                    writeOut(lines, new FileOutputStream(file));
                    lines.clear();
                }
            }
            lines.sort(sorter);
            File file = new File(tempDirectory + "temp" + System.currentTimeMillis());
            outputs.add(file);
            writeOut(lines, new FileOutputStream(file));
            lines.clear();
        } catch (IOException e) {
            System.out.println("Произошла ошибка при разделении файла " + e);
        } finally {
            if (br != null) try {
                br.close();
            } catch (IOException e) {
                System.out.println("Произошла ошибка при закрытии файла " + e);
            }
        }
    }

    /**
     * Метод для записи список строк в выходной поток.
     *
     * @param list - Список строк.
     * @param os   - Выходной поток.
     */
    private void writeOut(List<String> list, OutputStream os) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(os));
            for (String s : list) {
                writer.write(s);
                writer.write("\n");
            }
            writer.flush();
        } catch (IOException e) {
            System.out.println("Произошла ошибка при записи в файл " + e);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    System.out.println("Произошла ошибка при закрытии файла " + e);
                }
            }
        }
    }

    /**
     * Метод для считывания временных файлов, созданных методом splitChunks, и объединения их в сортированном виде
     * в выходной поток.
     *
     * @param os - Выходной поток.
     */
    public void mergeChunks(OutputStream os) {
        Map<StringWrapper, BufferedReader> map = new HashMap<>();
        List<BufferedReader> readers = new ArrayList<>();
        BufferedWriter writer = null;
        ComparatorDelegate delegate = new ComparatorDelegate();
        try {
            writer = new BufferedWriter(new OutputStreamWriter(os));
            for (int i = 0; i < outputs.size(); i++) {
                BufferedReader reader = new BufferedReader(new FileReader(outputs.get(i)));
                readers.add(reader);
                String line = reader.readLine();
                if (line != null) {
                    map.put(new StringWrapper(line), readers.get(i));
                }
            }
            List<StringWrapper> sorted = new LinkedList<>(map.keySet());
            while (map.size() > 0) {
                sorted.sort(delegate);
                StringWrapper line = sorted.remove(0);
                writer.write(line.string);
                writer.write("\n");
                BufferedReader reader = map.remove(line);
                String nextLine = reader.readLine();
                if (nextLine != null) {
                    StringWrapper sw = new StringWrapper(nextLine);
                    map.put(sw, reader);
                    sorted.add(sw);
                }
            }
        } catch (IOException e) {
            System.out.println("Произошла ошибка при соединении файлов " + e);
        } finally {
            for (BufferedReader reader : readers) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.out.println("Произошла ошибка при закрытии файла " + e);
                }
            }
            for (File output : outputs) {
                output.delete();
            }
            try {
                writer.close();
            } catch (IOException e) {
                System.out.println("Произошла ошибка при закрытии файла " + e);
            }
        }
    }


    /**
     * Делегирующий компаратор для возможности сортировки StringWrapper.
     */
    private class ComparatorDelegate implements Comparator<StringWrapper> {
        @Override
        public int compare(StringWrapper o1, StringWrapper o2) {
            return sorter.compare(o1.string, o2.string);
        }
    }

    /**
     * Класс обёртка для String, созданный для избежания конфликтов с дубликатами в HashMap.
     */
    private class StringWrapper implements Comparable<StringWrapper> {
        private final String string;

        public StringWrapper(String line) {
            this.string = line;
        }

        @Override
        public int compareTo(StringWrapper o) {
            int a = Integer.parseInt(string.split(separatingMark)[0]);
            int b = Integer.parseInt(o.string.split(separatingMark)[0]);
            return Integer.compare(a, b);
        }
    }

}


