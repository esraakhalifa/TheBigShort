package com.example.BigShort.url_service.util;

public class Base62Encoder {

    private static final String BASE62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    // Convert a decimal number to Base62
    public static String encode(long number) {
        if (number == 0) return "0";

        StringBuilder result = new StringBuilder();

        while (number > 0) {
            int remainder = (int) (number % 62);
            result.append(BASE62.charAt(remainder)); // get the corresponding Base62 character
            number = number / 62;
        }

        return result.reverse().toString(); // reverse because we build it backwards
    }
    public static long decode(String base62) {
        long number = 0;
        for (char c : base62.toCharArray()) {
            number = number * 62 + BASE62.indexOf(c);
        }
        return number;
    }

}
