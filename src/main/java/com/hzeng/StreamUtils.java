package com.hzeng;

import java.io.*;
import java.util.*;

import static java.lang.Math.min;

public class StreamUtils {

    private static File createFile(String file_name) throws IOException {

        File file = new File(file_name);

        if (!file.exists()) {

            if (!file.getParentFile().exists())
                file.getParentFile().mkdirs();
            file.createNewFile();
        }

        return file;
    }

    public static OutputStream saveToFile(String file_name) throws IOException {

        return new FileOutputStream(createFile(file_name));
    }

    public static InputStream readFromFile(String file_name) throws IOException {

        return new FileInputStream(createFile(file_name));
    }

    /**
     * 序列化,List
     */
    public static <T> boolean writeObject(List<T> list, String file)
    {
        T[] array = (T[]) list.toArray();
        try(ObjectOutputStream out = new ObjectOutputStream(saveToFile(file)))
        {
            out.writeObject(array);
            out.flush();
            out.close();
            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 反序列化,List
     */
    public static <E> List<E> readObjectForList(String file)
    {
        E[] object;
        try(ObjectInputStream in = new ObjectInputStream(readFromFile(file)))
        {
            object = (E[]) in.readObject();
            in.close();
            return Arrays.asList(object);
        } catch (IOException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> ArrayList<T> rankHashMap(HashMap<T, Double> integerHashMap, int rank) {

        List<HashMap.Entry<T, Double>> list = new ArrayList<>();

        for (HashMap.Entry<T, Double> integerEntry : integerHashMap.entrySet()) {
            list.add(integerEntry);
        }

        list.sort(new Comparator<HashMap.Entry<T, Double>>() {
            @Override
            public int compare(Map.Entry<T, Double> o1, Map.Entry<T, Double> o2) {
                return o2.getValue() > o1.getValue() ? 1 : 0;
            }
        });

        ArrayList<T> arrayList = new ArrayList<>();
        list.subList(0, min(rank, list.size())).forEach((element -> {
            arrayList.add(element.getKey());
        }));

        return arrayList;
    }

}

