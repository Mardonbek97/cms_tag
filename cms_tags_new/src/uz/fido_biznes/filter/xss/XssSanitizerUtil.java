package uz.fido_biznes.filter.xss;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class XssSanitizerUtil {
   private static List<Pattern> XSS_INPUT_PATTERNS = new ArrayList();

   public static String stripXSS(String value) {
      if (value != null) {
         value = value.replaceAll("\u0000", "");

         for(Pattern xssInputPattern : XSS_INPUT_PATTERNS) {
            value = xssInputPattern.matcher(value).replaceAll("");
         }
      }

      return value;
   }

   static {
      XSS_INPUT_PATTERNS.add(Pattern.compile("<script>(.*?)</script>", 2));
      XSS_INPUT_PATTERNS.add(Pattern.compile("<iframe(.*?)>(.*?)</iframe>", 2));
      XSS_INPUT_PATTERNS.add(Pattern.compile("<input(.*?)>(.*?)</input>", 2));
      XSS_INPUT_PATTERNS.add(Pattern.compile("src[\r\n]*=[\r\n]*\\'(.*?)\\'", 42));
      XSS_INPUT_PATTERNS.add(Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", 42));
      XSS_INPUT_PATTERNS.add(Pattern.compile("src[\r\n]*=[\r\n]*([^>]+)", 42));
      XSS_INPUT_PATTERNS.add(Pattern.compile("</script>", 2));
      XSS_INPUT_PATTERNS.add(Pattern.compile("<\\s*script\\b[^>]", 2));
      XSS_INPUT_PATTERNS.add(Pattern.compile("<\\s*/\\s*script\\s*>", 2));
      XSS_INPUT_PATTERNS.add(Pattern.compile("<\\s*a\\b[^>]", 2));
      XSS_INPUT_PATTERNS.add(Pattern.compile("<\\s*/\\s*a\\s*>", 2));
      XSS_INPUT_PATTERNS.add(Pattern.compile("</script(.*?)>", 2));
      XSS_INPUT_PATTERNS.add(Pattern.compile("<script(.*?)>", 42));
      XSS_INPUT_PATTERNS.add(Pattern.compile("<input(.*?)>", 42));
      XSS_INPUT_PATTERNS.add(Pattern.compile("eval\\((.*?)\\)", 42));
      XSS_INPUT_PATTERNS.add(Pattern.compile("expression\\((.*?)\\)", 42));
      XSS_INPUT_PATTERNS.add(Pattern.compile("javascript:", 2));
      XSS_INPUT_PATTERNS.add(Pattern.compile("vbscript:", 2));
      XSS_INPUT_PATTERNS.add(Pattern.compile("<a(.*?)>", 2));
      XSS_INPUT_PATTERNS.add(Pattern.compile("onload(.*?)=", 42));
      XSS_INPUT_PATTERNS.add(Pattern.compile("onfocus(.*?)=", 42));
      XSS_INPUT_PATTERNS.add(Pattern.compile("<(.*?)form(.*?)>(.*?)</(.*?)form(.*?)>", 42));
      XSS_INPUT_PATTERNS.add(Pattern.compile("<(.*?)img(.*?)>", 42));
      XSS_INPUT_PATTERNS.add(Pattern.compile("<object(.*?)>", 42));
      XSS_INPUT_PATTERNS.add(Pattern.compile("<link(.*?)>", 42));
      XSS_INPUT_PATTERNS.add(Pattern.compile("<script(.*?)>", 42));
   }
}
