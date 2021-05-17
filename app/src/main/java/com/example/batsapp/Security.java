package com.example.batsapp;

import java.util.Random;

public class Security {
    private static final Random r = new Random();

    private static String getE(int n) {
        String AlphaNumericString = "02468";
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            int index = (int) (AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString
                    .charAt(index));
        }
        return sb.toString();
    }

    private static String getO(int n) {
        String AlphaNumericString = "13579";
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            int index = (int) (AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString
                    .charAt(index));
        }
        return sb.toString();
    }

    private static String revString(String str) {
        char[] ch = str.toCharArray();
        StringBuilder rev = new StringBuilder();
        for (int i = ch.length - 1; i >= 0; i--) {
            rev.append(ch[i]);
        }
        return rev.toString();
    }


    public static String getToken() {
        int low = 2;
        int high = 6;
        int codec = r.nextInt(high - low) + low;
        return "t" + revString(codec + getO(codec) + getE(codec + 1) +
                getO(codec + 2) + getE(codec + 3) +
                System.currentTimeMillis());


    }


    public static String getVersion() {
        return MainActivity.APP_VERSION;
    }
}
