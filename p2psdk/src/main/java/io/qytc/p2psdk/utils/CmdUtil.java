package io.qytc.p2psdk.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CmdUtil {
    public static String executeLinuxCmd(String cmd) {
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            InputStreamReader ir = new InputStreamReader(process.getInputStream());
            BufferedReader input = new BufferedReader(ir);
            String string;
            while ((string = input.readLine()) != null) {
                return string;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getCardNo() {
        String cardNo = executeLinuxCmd("getprop persist.sys.ca.card_id");
        if (cardNo == null) {
            cardNo = "";
        }
        //return "201912121700";
      return cardNo;
    }
}
