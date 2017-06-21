package com.metabiota.rs.csv.tranform;

import com.opencsv.CSVReader;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by nhchon on 6/21/2017 2:21 PM.
 */
public class CSVTransformation {
    public static final String CSV_EXTENSION = ".csv";
    public static final String A_WEEKLYFINALDAT_CSV_HEADER = "eventID,location_lvl,location_nm,State,day,infect,hosp,deaths";
    public static final String NEW_A_WEEKLYFINALDAT_CSV_HEADER = "eventID,location_lvl,location_nm,State,day,country_code,infect,hosp,deaths";
    public static final String A_WeeklyFinalDatFilePath = "D:\\Metabiota-Docs\\metabiota_modeling-res_deliverables-0628f5734f9c\\FLU\\V3\\A_WeeklyFinalDat";//D_LifeLossPopFinalDat-Test.csv

    public static void main(String[] args) throws IOException {

//        Path path = Paths.get(A_WeeklyFinalDatFilePath);
//        List<String> trimmedStrings = Files.lines(path).map(String::trim).filter(l -> !"".equals(l)).collect(Collectors.toList());
        //trimmedStrings.forEach(System.out::println);

        try (CSVReader reader = new CSVReader(new FileReader(A_WeeklyFinalDatFilePath + CSV_EXTENSION))) {
            // Read and check header line
            String[] nextLine = reader.readNext();
            System.out.println(toString(nextLine));
            if (!A_WEEKLYFINALDAT_CSV_HEADER.equalsIgnoreCase(toString(nextLine))) {
                System.out.println("This is not A_WEEKLYFINALDAT.csv file");
                System.exit(0);
            }

            //Read CSV line by line and use the string array as you want
            List<String> tranformedList = new ArrayList<>();
            while ((nextLine = reader.readNext()) != null) {
                if (nextLine != null) {

                    List<String> l = new ArrayList(Arrays.asList(nextLine));
                    // day in format yyyy-mm-dd
                    String day = StringUtils.trimToEmpty(l.get(4));
                    if (day.length() < 10) {
                        day = StringUtils.leftPad(day, 10, '0');
                    }
                    l.set(4, day);

                    // country code is location
                    String countryCode = l.get(2);
                    l.add(5, countryCode);

                    // state is "null" string if empty
                    String state = StringUtils.trimToEmpty(l.get(3));
                    if ("".equals(state)) {
                        state = "null";
                    }
                    l.set(3, state);

                    //Verifying the read data here
                    System.out.println(l.toString());

                    tranformedList.add(toString(l));
                }
            }

            // write to file
            writeFile(tranformedList, NEW_A_WEEKLYFINALDAT_CSV_HEADER,A_WeeklyFinalDatFilePath + "-tranformed" + CSV_EXTENSION);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <T extends Object> void writeFile(List<T> objs, String csvHeaderLine, String fileName) throws IOException {
        File fout = new File(fileName);
        try (
                FileOutputStream fos = new FileOutputStream(fout);
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        ) {
            bw.write(csvHeaderLine);
            bw.newLine();
            for(T r : objs) {
                bw.write(r.toString());
                bw.newLine();
            }
        }
    }

    public static String toString(List<String> ls) {
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < ls.size(); i++) {
            strBuilder.append(ls.get(i));
            if (i < (ls.size() - 1)) {
                strBuilder.append(',');
            }
        }
        return strBuilder.toString();
    }

    public static String toString(String[] strArr) {
        StringBuilder strBuilder = new StringBuilder();
        for (int i = 0; i < strArr.length; i++) {
            strBuilder.append(strArr[i]);
            if (i < (strArr.length - 1)) {
                strBuilder.append(',');
            }
        }
        return strBuilder.toString();
    }

    public static String[] splitLine(String l) {
        return l.split(",");
    }
}
