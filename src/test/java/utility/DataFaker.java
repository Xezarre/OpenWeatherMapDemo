package utility;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Random;

public class DataFaker {

    /**
     * @param email - The email that requests to add random string alias
     * @return return new email by adding random string alias
     * @ActionName: generateRandomEmail
     */
    public static String generateRandomEmail(String email) {

        if (email.contains("@")) {
            String[] parts = email.split("@");
            String part1 = parts[0];
            String part2 = parts[1];
            return part1 + "+" + Utility.getTestCaseID().replace("c", "") + generateTimeStampString("MMddHHmmssSSS") + "@" + part2;

        } else {
            throw new IllegalArgumentException("The String" + email + " does not contain @");
        }
    }

    /**
     * Generate a random email with specific length
     *
     * @param email
     * @param length
     * @return return new email with specific length
     */
    public static String generateRandomEmailWithLength(String email, int length) {
        String emailTemp = generateRandomEmail(email);
        String[] parts = emailTemp.split("@");
        String part1 = parts[0];
        String part2 = parts[1];
        int lengthTemp = length - emailTemp.length();
        String valueTemp = RandomStringUtils.randomNumeric(lengthTemp);
        return part1 + valueTemp + "@" + part2;
    }

    /**
     * @Action generateIMEI
     * @CreatedDate: 2018/03/21
     * @ModifyDate: 2018/03/21
     * @Owner: Vinh Ly
     * Translated from: https://lazyzhu.com/imei-generator/js/imei-generator.js
     */
    public static String generateIMEI() {
        int pos;
        int[] str = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        int sum = 0;
        int final_digit;
        int t;
        int len_offset;
        int len = 15;
        String imei = "";

        String[] rbi = new String[]{"01", "10", "30", "33", "35", "44", "45", "49", "50", "51", "52", "53", "54", "86", "91", "98", "99"};
        String[] arr = rbi[(int) Math.floor(Math.random() * rbi.length)].split("");
        str[0] = Integer.parseInt(arr[0]);
        str[1] = Integer.parseInt(arr[1]);
        pos = 2;

        while (pos < len - 1) {
            str[pos++] = (int) (Math.floor(Math.random() * 10) % 10);
        }

        len_offset = (len + 1) % 2;
        for (pos = 0; pos < len - 1; pos++) {
            if ((pos + len_offset) % 2 != 0) {
                t = str[pos] * 2;
                if (t > 9) {
                    t -= 9;
                }
                sum += t;
            } else {
                sum += str[pos];
            }
        }

        final_digit = (10 - (sum % 10)) % 10;
        str[len - 1] = final_digit;

        for (int d : str) {
            imei += String.valueOf(d);
        }

        return imei;
    }

    /***
     * Generate a 13-digits random number
     * @throws IOException e
     */
    public static long numbGenerator() {
        long min = 1000000000000L; //13 digits inclusive
        long max = 10000000000000L; //14 digits exclusive
        Random random = new Random();
        return min + ((long) (random.nextDouble() * (max - min)));
    }

    /**
     * @return randomStr
     * - random string like: 2016-12-23-01-46-16
     * @author: Quoc Le
     * @ActionName: generateTimeStampString
     * @CreatedDate: 12/23/2016
     * This method generates timestamp
     */
    public static String generateTimeStampString(String pattern) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }

    public static String generateTimeStampString(int length) {
        String timestampStr = null;
        if (length <= 14 && length > 0) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            LocalDateTime now = LocalDateTime.now();
            timestampStr = dtf.format(now);
        }
        return Utility.right(timestampStr, length);
    }

    public static String addDaysToCurrentDate(int days){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, days);
        String futureDate = sdf.format(cal.getTime());
        return futureDate;
    }
}
