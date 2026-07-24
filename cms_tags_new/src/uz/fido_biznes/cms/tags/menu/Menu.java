package uz.fido_biznes.cms.tags.menu;

import java.util.Vector;
import uz.fido_biznes.cms.JArray;
import uz.fido_biznes.cms.JHash;

public class Menu {
   private String label;
   private String action;
   private Object items;

   public String getAction() {
      return this.action;
   }

   public void setAction(String action) {
      this.action = action;
   }

   public String getLabel() {
      return this.label;
   }

   public void setLabel(String label) {
      this.label = label;
   }

   public void addMenu(Menu m) {
      if (this.items == null) {
         this.items = new Vector();
      }

      ((Vector)this.items).addElement(m);
   }

   public void setItems(Object items) {
      this.items = items;
   }

   public String toString() {
      JHash result = new JHash();
      if (this.label != null) {
         result.put("label", this.label);
      }

      if (this.action != null) {
         result.putScript("action", "function(){" + this.action + "}");
      }

      if (this.items != null) {
         if (this.items instanceof Vector) {
            JArray menus = new JArray();
            Vector it = (Vector)this.items;

            for(int i = 0; i < it.size(); ++i) {
               Menu menu = (Menu)it.elementAt(i);
               menus.pushScript(menu.toString());
            }

            result.put("items", menus);
         } else {
            result.putScript("items", (String)this.items);
         }
      }

      return result.toString();
   }
}
