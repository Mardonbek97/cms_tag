package uz.fido_biznes.cms.tags.table;

import java.util.Hashtable;
import java.util.TreeMap;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;

import uz.fido_biznes.cms.*;
import uz.fido_biznes.cms.tags.FormTag;
import uz.fido_biznes.cms.tags.ReferenceTag;
import uz.fido_biznes.sql.StoredObject;

public class TableTag extends BodyTagSupport {
   private static final long serialVersionUID = 1L;
   private static final String STR_SESSION_TABLE_PREFIX = "TP_";
   private static final String STR_SESSION_TABLE_REF_PREFIX = "TR_";
   private static final String STR_RECREATE = "_recreateValue";
   private HttpServletRequest request;
   private HttpSession session;
   private StoredObject stored;
   private Language lang;
   private Hashtable userCache;
   private boolean debug;
   private Value value;
   private String from;
   private String where;
   private String function;
   private String sessionName;
   private TreeMap sorts;
   private User user;
   private String autoRefresh = "0";

   public void setFrom(String from) {
      this.from = from;
   }

   public void setFunction(String function) {
      this.function = function;
   }

   public void setSessionName(String sessionName) {
      this.sessionName = sessionName;
   }

   public void setWhere(String where) {
      this.where = where;
   }

   public void setAutoRefresh(String autoRefresh) {
      this.autoRefresh = autoRefresh;
   }

   public int doStartTag() throws JspException {
      this.request = (HttpServletRequest)this.pageContext.getRequest();
      this.session = this.pageContext.getSession();
      this.stored = (StoredObject)this.session.getValue("stored");
      this.lang = (Language)this.pageContext.getAttribute("lang");
      this.userCache = (Hashtable)this.session.getValue("user_cache");
      this.user = (User)this.pageContext.getAttribute("user", 3);
      this.debug = this.stored.isDebug();
      if (this.getParent() instanceof ReferenceTag && this.sessionName == null) {
         this.sessionName = "TR_" + this.request.getRequestURI() + "_" + ((ReferenceTag)this.getParent()).getName();
      }

      if (this.sessionName == null) {
         this.sessionName = "TP_" + this.request.getRequestURI();
      }

      this.value = (Value)this.userCache.get(this.sessionName);
      if ("true".equals(this.request.getParameter("clearSort"))) {
         this.value.clearSort();
      }

      if (this.value == null || this.debug && this.request.getParameter("_recreateValue") != null) {
         if (this.autoRefresh != "0") {
            int ar = 0;

            try {
               if (this.autoRefresh != null && !"".equals(this.autoRefresh)) {
                  ar = Integer.parseInt(this.autoRefresh);
               }
            } catch (NumberFormatException var3) {
               throw new NumberFormatException("Tableni autoRefresh atributiga faqat son kiritilishi kerak!");
            }

            this.value = new Value(this.user.getLanguageIndex(), ar);
         } else {
            this.value = new Value(this.user.getLanguageIndex());
         }

         this.userCache.put(this.sessionName, this.value);
         return 2;
      } else {
         return 0;
      }
   }

   public boolean isDebug() {
      return this.debug;
   }

   public Language getLang() {
      return this.lang;
   }

   public HttpServletRequest getRequest() {
      return this.request;
   }

   public HttpSession getSession() {
      return this.session;
   }

   public StoredObject getStored() {
      return this.stored;
   }

   public Hashtable getUserCache() {
      return this.userCache;
   }

   public Value getValue() {
      return this.value;
   }

   public void putSort(String orderKey, Sort sort) {
      if (this.sorts == null) {
         this.sorts = new TreeMap();
      }

      this.sorts.put(Integer.valueOf(orderKey), sort);
   }

   public int doEndTag() throws JspException {
      try {
         String userAgent = ((HttpServletRequest)this.pageContext.getRequest()).getHeader("User-Agent").toLowerCase();
         this.value.setSorts(this.sorts);
         this.value.setFrom(this.from);
         this.value.setWhere(this.where);
         this.value.setFunction(this.function);
         if (this.bodyContent != null) {
            this.bodyContent.clearBody();
         }

         this.readRequest();
         JspWriter out = this.pageContext.getOut();
         this.writeData(((FormTag)this.getParent()).getEnclosingWriter());
         out.write(this.value.getTableDefinition());
         out.print(Resource.getTableJS(this.user, userAgent, this.stored));
      } catch (Exception ex) {
         this.userCache.remove(this.sessionName);
         throw new JspException(Util.getStackTrace(ex));
      } finally {
         this.sorts = null;
         this.from = null;
         this.sessionName = null;
         this.function = null;
         this.lang = null;
      }

      return 6;
   }

   private void readRequest() throws Exception {
      this.value.setRecordsPerPage(this.request.getParameter("_rpp"));
      this.value.setPageNubmer(this.request.getParameter("page"));
      if (this.request.getParameter("clearFilter") != null) {
         this.value.clearFilter();
      }

      Vector fields = this.value.getFields();
      int len = fields.size();

      for(int i = 0; i < len; ++i) {
         Field field = (Field)fields.elementAt(i);
         if (field.getFilterType() > 0) {
            String[] values = this.request.getParameterValues("f" + field.getId());
            if (values != null) {
               field.setFilterValue(Util.encodeISO(values[0]));
               if (values.length > 1) {
                  field.setFilterValue2(Util.encodeISO(values[1]));
               }
            }

            if (field.getFilterType() == 3) {
               String operator = this.request.getParameter("fo" + field.getId());
               field.setFilterOperator(operator);
            }
         }
      }

      TreeMap tempSorts = null;

      for(int i = 0; i < len; ++i) {
         Field field = (Field)fields.elementAt(i);
         String[] values = this.request.getParameterValues("s" + field.getId());
         if (values != null) {
            if (tempSorts == null) {
               tempSorts = new TreeMap();
            }

            tempSorts.put(values[0], new Sort(field.getId(), field.getName(), values[1]));
         }
      }

      this.value.setSorts(tempSorts);
   }

   private void writeData(JspWriter out) throws Exception {
      String sqlFields = this.value.getSQLFields();
      String sqlFilter = this.value.getSQLFilter();
      String sqlCenter = "from " + this.from;
      String sqlTemp = " where ";
      String sqlOrderByClause = this.value.getSQLOrderByClause();
      if (this.where != null && this.where.length() > 0) {
         sqlCenter = sqlCenter + sqlTemp + this.where;
         sqlTemp = " and ";
      }

      if (sqlFilter != null) {
         sqlCenter = sqlCenter + sqlTemp + sqlFilter;
      }

      out.write("<script>tdd={SN:'" + this.sessionName + "',f:");
      out.print(this.value.getFilterDefinition());
      String filterValues = this.value.getFilterValues();
      if (filterValues != null) {
         out.write(",");
         out.write(filterValues);
      }

      String sortValues = this.value.getSortValues();
      if (sortValues != null) {
         out.write(",");
         out.write(sortValues);
      }

      if (this.function != null) {
         String functionResult = this.stored.execFunction(this.function);
         if (functionResult != null) {
            out.write(",");
            out.write("fr:");
            out.write("'" + functionResult + "'");
         }
      }

      String sum = this.value.getSQLSumFields();
      if (sum != null) {
         out.write(",sum:" + this.stored.execSelect("select " + sum + sqlCenter));
      }

      if (this.value.getRecordsPerPage() > 0) {
         String query = "select count(1)" + sqlCenter;
         int totalRecords = Integer.parseInt(this.stored.execSelect(query));
         int maxPage = totalRecords / this.value.getRecordsPerPage() + (totalRecords % this.value.getRecordsPerPage() != 0 ? 1 : 0);
         if (this.value.getPageNumber() < 1 || this.value.getPageNumber() > maxPage) {
            this.value.setPageNumber(1);
         }

         out.write(",p:{PN:" + this.value.getPageNumber() + ",MP:" + maxPage + ",RPP:" + this.value.getRecordsPerPage() + ",TR:" + totalRecords + "},d:[");
         query = "select " + sqlFields + sqlCenter + sqlOrderByClause;
         if (this.debug) {
            System.out.println("======================================");
            System.out.println(query);
         }

         out.write(this.stored.execSelect(query, this.value.getPageNumber(), this.value.getRecordsPerPage(), ","));
         out.write("]");
      } else {
         out.write(",d:[");
         String query = "select " + sqlFields + sqlCenter + sqlOrderByClause;
         if (this.debug) {
            System.out.println("======================================");
            System.out.println(query);
         }

         out.write(this.stored.execSelect(query, ","));
         out.write("]");
      }

      out.write("}</script>");
   }

   public static String getRefreshedTDD(PageContext pageContext, String sessionName, int pageNumber) throws NumberFormatException, Exception {
//      CmsLogUtil.log("Session name:" + sessionName);
      HttpSession session = pageContext.getSession();
//      CmsLogUtil.log("Session: "+session);
      StoredObject stored = (StoredObject)session.getValue("stored");
//      CmsLogUtil.log("Stored: " +stored);
      Hashtable userCache = (Hashtable)session.getValue("user_cache");
//      CmsLogUtil.log("userCache: " +userCache);


      Value value = (Value)userCache.get(sessionName);
      if (value == null && !sessionName.contains("/ibs/")) {
         value = (Value) userCache.get(sessionName.replace("TP_/", "TP_/ibs/"));
      }

      value.setPageNumber(pageNumber);
      String sqlFields = value.getSQLFields();
      String sqlFilter = value.getSQLFilter();
      String sqlCenter = "from " + value.getFrom();
      String sqlTemp = " where ";
      String sqlOrderByClause = value.getSQLOrderByClause();
      String function = value.getFunction();
      if (value.getWhere() != null && value.getWhere().length() > 0) {
         sqlCenter = sqlCenter + sqlTemp + value.getWhere();
         sqlTemp = " and ";
      }

      if (sqlFilter != null) {
         sqlCenter = sqlCenter + sqlTemp + sqlFilter;
      }

      StringBuffer sb = new StringBuffer(1000);
      sb.append("{SN:'" + sessionName + "',f:");
      sb.append(value.getFilterDefinition());
      String filterValues = value.getFilterValues();
      if (filterValues != null) {
         sb.append(",");
         sb.append(filterValues);
      }

      String sortValues = value.getSortValues();
      if (sortValues != null) {
         sb.append(",");
         sb.append(sortValues);
      }

      if (function != null) {
         String functionResult = stored.execFunction(function);
         if (functionResult != null) {
            sb.append(",");
            sb.append("fr:");
            sb.append("'").append(functionResult).append("'");
         }
      }

      String sum = value.getSQLSumFields();
      if (sum != null) {
         sb.append(",sum:").append(stored.execSelect("select " + sum + sqlCenter));
      }

      if (value.getRecordsPerPage() > 0) {
         String query = "select count(1)" + sqlCenter;
         int totalRecords = Integer.parseInt(stored.execSelect(query));
         int maxPage = totalRecords / value.getRecordsPerPage() + (totalRecords % value.getRecordsPerPage() != 0 ? 1 : 0);
         if (value.getPageNumber() < 1 || value.getPageNumber() > maxPage) {
            value.setPageNumber(1);
         }

         sb.append(",p:{PN:" + value.getPageNumber() + ",MP:" + maxPage + ",RPP:" + value.getRecordsPerPage() + ",TR:" + totalRecords + "},d:[");
         query = "select " + sqlFields + sqlCenter + sqlOrderByClause;
         sb.append(stored.execSelect(query, value.getPageNumber(), value.getRecordsPerPage(), ","));
         sb.append("]");
      } else {
         sb.append(",d:[");
         String query = "select " + sqlFields + sqlCenter + sqlOrderByClause;
         sb.append(stored.execSelect(query, ","));
         sb.append("]");
      }

      sb.append("}");
      return sb.toString();
   }
}
