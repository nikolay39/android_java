package com.example.android.sunshine.model.common.staticWrappers;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hash
{
    public static String MD5(String s)
    {
        MessageDigest m = null;
        try
        {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }

        m.update(s.getBytes(), 0, s.length());
        String hash = new BigInteger(1, m.digest()).toString(16);
        return hash;
    }
}
