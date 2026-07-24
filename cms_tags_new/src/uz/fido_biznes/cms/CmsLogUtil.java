package uz.fido_biznes.cms;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CmsLogUtil {

//    private static final String LOG_FILE = "D:/temp/excel.log";
    // Linux server bo'lsa:
     private static final String LOG_FILE = "/tmp/eeeee.log";

    public static void log(String message) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
            pw.println(time + " - " + message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void log(Throwable t) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
            pw.println("\n========== " + time + " ==========");
            t.printStackTrace(pw);
            pw.println("==================================");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}