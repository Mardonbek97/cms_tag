/*     */ package uz.fido_biznes.cms.tags.table;
/*     */
/*     */ import java.lang.reflect.Method;
/*     */ import java.sql.PreparedStatement;
/*     */ import java.sql.ResultSet;
/*     */ import java.util.Hashtable;
/*     */ import java.util.TreeMap;
/*     */ import java.util.Vector;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpSession;
/*     */ import javax.servlet.jsp.JspException;
/*     */ import javax.servlet.jsp.JspWriter;
/*     */ import javax.servlet.jsp.PageContext;
/*     */ import javax.servlet.jsp.tagext.BodyTagSupport;
/*     */ import uz.fido_biznes.cms.Language;
/*     */ import uz.fido_biznes.cms.Resource;
/*     */ import uz.fido_biznes.cms.User;
/*     */ import uz.fido_biznes.cms.Util;
/*     */ import uz.fido_biznes.cms.tags.FormTag;
/*     */ import uz.fido_biznes.cms.tags.ReferenceTag;
/*     */ import uz.fido_biznes.sql.StoredObject;
/*     */
/*     */
/*     */
/*     */ public class DynamicGridTag
/*     */   extends BodyTagSupport
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private static final String STR_SESSION_TABLE_PREFIX = "TP_";
/*     */   private static final String STR_SESSION_TABLE_REF_PREFIX = "TR_";
/*     */   private static final String STR_RECREATE = "_recreateValue";
/*     */   private HttpServletRequest request;
/*     */   private HttpSession session;
/*     */   private StoredObject stored;
/*     */   private Language lang;
/*     */   private Hashtable userCache;
/*     */   private boolean debug;
/*     */   private Value value;
/*     */   private Integer gridId;
/*     */   private String where;
/*     */   private String function;
/*     */   private String sessionName;
/*     */   private TreeMap sorts;
/*     */   private User user;
/*     */   private String tableDefinitionScript;
/*  42 */   private String autoRefresh = "0";
/*     */
/*     */   public void setGridId(Integer gridId) {
/*  45 */     this.gridId = gridId;
/*     */   }
/*     */
/*     */   public void setFunction(String function) {
/*  49 */     this.function = function;
/*     */   }
/*     */
/*     */   public void setSessionName(String sessionName) {
/*  53 */     this.sessionName = sessionName;
/*     */   }
/*     */
/*     */   public void setWhere(String where) {
/*  57 */     this.where = where;
/*     */   }
/*     */
/*     */   public void setAutoRefresh(String autoRefresh) {
/*  61 */     this.autoRefresh = autoRefresh;
/*     */   }
/*     */
/*     */   public int doStartTag() throws JspException {
/*  65 */     this.request = (HttpServletRequest)this.pageContext.getRequest();
/*  66 */     this.session = this.pageContext.getSession();
/*  67 */     this.stored = (StoredObject)this.session.getAttribute("stored");
/*  68 */     this.lang = (Language)this.pageContext.getAttribute("lang");
/*  69 */     //this.userCache = (Hashtable)this.session.getValue("user_cache");
              this.userCache = (Hashtable)this.session.getAttribute("user_cache");
/*  70 */     this.user = (User)this.pageContext.getAttribute("user", 3);
/*     */
/*  72 */     this.debug = this.stored.isDebug();
/*     */
/*  74 */     if (getParent() instanceof ReferenceTag && this.sessionName == null) {
/*  75 */       this
/*     */
/*  77 */         .sessionName = "TR_" + this.request.getRequestURI() + "_" + ((ReferenceTag)getParent()).getName();
/*     */     }
/*  79 */     if (this.sessionName == null) {
/*  80 */       this.sessionName = "TP_" + this.request.getRequestURI() + "_G" + this.gridId;
/*     */     }
/*     */
/*  83 */     if ("true".equals(this.request.getParameter("clearSort")) && this.value != null) {
/*  84 */       this.value.clearSort();
/*     */     }
/*     */
/*  87 */     this.value = (Value)this.userCache.get(this.sessionName);
/*     */
/*  89 */     if (this.value == null || this.request.getParameter("_recreateValue") != null) {
/*     */
/*  92 */       if (this.autoRefresh != "0") {
/*  93 */         int ar = 0;
/*     */         try {
/*  95 */           if (this.autoRefresh != null && !"".equals(this.autoRefresh)) {
/*  96 */             ar = Integer.parseInt(this.autoRefresh);
/*     */           }
/*  98 */         } catch (NumberFormatException e) {
/*  99 */           throw new NumberFormatException("Tableni autoRefresh atributiga faqat son kiritilishi kerak!");
/*     */         }
/*     */
/* 102 */         this.value = new Value(this.user.getLanguageIndex(), ar);
/*     */       } else {
/* 104 */         this.value = new Value(this.user.getLanguageIndex());
/*     */       }
/*     */
/* 107 */       this.userCache.put(this.sessionName, this.value);
/* 108 */       return 2;
/*     */     }
/* 110 */     return 0;
/*     */   }
/*     */
/*     */   public boolean isDebug() {
/* 114 */     return this.debug;
/*     */   }
/*     */
/*     */   public Language getLang() {
/* 118 */     return this.lang;
/*     */   }
/*     */
/*     */   public HttpServletRequest getRequest() {
/* 122 */     return this.request;
/*     */   }
/*     */
/*     */   public HttpSession getSession() {
/* 126 */     return this.session;
/*     */   }
/*     */
/*     */   public StoredObject getStored() {
/* 130 */     return this.stored;
/*     */   }
/*     */
/*     */   public Hashtable getUserCache() {
/* 134 */     return this.userCache;
/*     */   }
/*     */
/*     */   public Value getValue() {
/* 138 */     return this.value;
/*     */   }
/*     */
/*     */   public void putSort(String orderKey, Sort sort) {
/* 142 */     if (this.sorts == null) {
/* 143 */       this.sorts = new TreeMap<>();
/*     */     }
/* 145 */     this.sorts.put(Integer.valueOf(orderKey), sort);
/*     */   }
/*     */
/*     */
/*     */   public int doEndTag() throws JspException {
/*     */     try {
/* 151 */       String userAgent = ((HttpServletRequest)this.pageContext.getRequest()).getHeader("User-Agent").toLowerCase();
/* 152 */       loadGridMetadata();
/* 153 */       this.value.setSorts(this.sorts);

/* 154 */       this.value.setWhere(this.where);
/* 155 */       this.value.setFunction(this.function);
/*     */
/* 157 */       if (this.bodyContent != null) {
/* 158 */         this.bodyContent.clearBody();
/*     */       }
/*     */
/* 161 */       readRequest();
/* 162 */       JspWriter out = this.pageContext.getOut();
/* 163 */       writeData(((FormTag)getParent()).getEnclosingWriter());
/* 164 */       out.write(this.tableDefinitionScript);
/* 165 */       out.print(Resource.getTableJS((iabs.User)this.user, userAgent, this.stored));
/* 166 */     } catch (Exception ex) {
/* 167 */       this.userCache.remove(this.sessionName);
/*     */       writeFriendlyError(ex);
/*     */       return 6;
/*     */     } finally {
/* 170 */       this.sorts = null;
/* 171 */       this.gridId = null;
/* 172 */       this.sessionName = null;
/* 173 */       this.function = null;
/* 174 */       this.lang = null;
/*     */       this.tableDefinitionScript = null;
/*     */     }
/* 176 */     return 6;
/*     */   }
/*     */
/*     */   private void writeFriendlyError(Exception ex) throws JspException {
/*     */     try {
/*     */       JspWriter out = this.pageContext.getOut();
/*     */       String message = getFriendlyErrorMessage(ex);
/*     */       out.write("<div style=\"margin:10px 0;padding:10px 12px;border:1px solid #c95f5f;background:#fff4f4;color:#7a1f1f;font:13px Arial,sans-serif;line-height:1.4;\">");
/*     */       out.write("<b>Dynamic grid xatosi</b><br>");
/*     */       out.write(escapeHtml(message));
/*     */       out.write("</div>");
/*     */     } catch (Exception writeEx) {
/*     */       throw new JspException(getFriendlyErrorMessage(ex));
/*     */     }
/*     */   }
/*     */
/*     */   private String getFriendlyErrorMessage(Exception ex) {
/*     */     String text = Util.getStackTrace(ex);
/*     */     if (text == null) {
/*     */       return "Noma'lum xatolik yuz berdi.";
/*     */     }
/*     */     String oracleMessage = getOracleMessage(text);
/*     */     return oracleMessage == null ? firstUsefulLine(text) : oracleMessage;
/*     */   }
/*     */
/*     */   private String getOracleMessage(String text) {
/*     */     String[] lines = text.split("\\r?\\n");
/*     */     StringBuffer result = null;
/*     */     for (int i = 0; i < lines.length; i++) {
/*     */       String line = lines[i].trim();
/*     */       if (line.indexOf("ORA-") >= 0) {
/*     */         if (result == null) {
/*     */           result = new StringBuffer();
/*     */         } else {
/*     */           result.append("<br>");
/*     */         }
/*     */         result.append(line);
/*     */       }
/*     */     }
/*     */     return result == null ? null : result.toString();
/*     */   }
/*     */
/*     */   private String firstUsefulLine(String text) {
/*     */     String[] lines = text.split("\\r?\\n");
/*     */     for (int i = 0; i < lines.length; i++) {
/*     */       String line = lines[i].trim();
/*     */       if (line.length() > 0 && (line.indexOf("ORA-") >= 0 || line.indexOf("Exception") >= 0)) {
/*     */         return line;
/*     */       }
/*     */     }
/*     */     return lines.length == 0 ? text : lines[0];
/*     */   }
/*     */
/*     */   private String escapeHtml(String value) {
/*     */     return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
/*     */   }
/*     */
/*     */   private void loadGridMetadata() throws Exception {
/*     */     PreparedStatement ps = null;
/*     */     ResultSet rs = null;
/*     */     boolean loadFields = this.value.getFields().size() == 0;
/*     */     String numbering = null;
/*     */     String withoutFocus = null;
/*     */     String withoutCursor = null;
/*     */     String withoutSortButton = null;
/*     */     String withoutRefreshButton = null;
/*     */     String resetCursor = null;
/*     */     String hideFilterButton = null;
/*     */     String hideExcelButton = null;
/*     */     String enterDirection = null;
/*     */     String rowColor = null;
/*     */     try {
/*     */       ps = this.stored.getConnection().prepareStatement(
/*     */         "select view_name, filter_clause, page_count, reset_cursor, numbering, without_focus, without_cursor, " +
/*     */         "without_sort_button, without_refresh_button, hide_filter_button, hide_excel_button, enter_direction, row_color " +
/*     */         "from core_grids where grid_id = ?");
/*     */       ps.setInt(1, this.gridId.intValue());
/*     */       rs = ps.executeQuery();
/*     */       if (!rs.next()) {
/*     */         throw new JspException("core_grids jadvalida grid_id=" + this.gridId + " topilmadi");
/*     */       }
/*     */       this.value.setFrom(rs.getString("view_name"));
/*     */       this.value.setWhere(mergeWhere(rs.getString("filter_clause"), this.where));
/*     */       this.value.setRecordsPerPage(rs.getString("page_count"));
/*     */       this.value.setResetCursor("Y".equals(rs.getString("reset_cursor")));
/*     */       numbering = rs.getString("numbering");
/*     */       withoutFocus = rs.getString("without_focus");
/*     */       withoutCursor = rs.getString("without_cursor");
/*     */       withoutSortButton = rs.getString("without_sort_button");
/*     */       withoutRefreshButton = rs.getString("without_refresh_button");
/*     */       resetCursor = rs.getString("reset_cursor");
/*     */       hideFilterButton = rs.getString("hide_filter_button");
/*     */       hideExcelButton = rs.getString("hide_excel_button");
/*     */       enterDirection = rs.getString("enter_direction");
/*     */       rowColor = normalizeColorExpression(rs.getString("row_color"));
/*     */     } finally {
/*     */       if (rs != null) {
/*     */         try { rs.close(); } catch (Exception ignored) {}
/*     */       }
/*     */       if (ps != null) {
/*     */         try { ps.close(); } catch (Exception ignored) {}
/*     */       }
/*     */     }
/*     */
/*     */     ps = null;
/*     */     rs = null;
/*     */     String secureUser = getSecureUserCode();
/*     */     int labelLanguageIndex = getUserLanguageIndex();
/*     */     try {
/*     */       ps = this.stored.getConnection().prepareStatement(
/*     */         "select field_name, field_order, field_type, encrypted, entityname, sort_order_key, sort_direction, " +
/*     */         "is_sum, sum_type, is_filter, filter_operator, filter_mask, filter_show_in_grid, filter_value, filter_value2, " +
/*     */         "filter_size, reference_name, reference_url, request_name, request_url, font_color, cell_color, " +
/*     */         "nvl(Mll_core_api.Get_Label_By_Id(i_Label_Id => column_label_id, i_Lang_Index => ?), field_name) column_label, " +
/*     */         "nvl(Mll_core_api.Get_Label_By_Id(i_Label_Id => filter_label_id, i_Lang_Index => ?), " +
/*     */         "nvl(Mll_core_api.Get_Label_By_Id(i_Label_Id => column_label_id, i_Lang_Index => ?), field_name)) filter_label " +
/*     */         "from core_grid_fields where grid_id = ? order by field_order");
/*     */       ps.setInt(1, labelLanguageIndex);
/*     */       ps.setInt(2, labelLanguageIndex);
/*     */       ps.setInt(3, labelLanguageIndex);
/*     */       ps.setInt(4, this.gridId.intValue());
/*     */       rs = ps.executeQuery();
/*     */       while (rs.next()) {
/*     */         String fieldOrder = rs.getString("field_order");
/*     */         String fieldName = rs.getString("field_name");
/*     */         if (loadFields) {
/*     */           Field field = new Field();
/*     */           field.setId(fieldOrder);
/*     */           field.setName(fieldName);
/*     */           field.setLabel(rs.getString("column_label"));
/*     */           field.setType(rs.getString("field_type"));
/*     */           field.setEncrypted(rs.getString("encrypted"));
/*     */           field.setEntityName(rs.getString("entityname"));
/*     */           field.setSecureUser(secureUser);
/*     */           field.setColor(normalizeColorExpression(rs.getString("font_color")));
/*     */           field.setBgColor(normalizeColorExpression(rs.getString("cell_color")));
/*     */           if ("Y".equals(rs.getString("is_sum"))) {
/*     */             field.setSumTegExist(true);
/*     */             field.setSumType(rs.getString("sum_type"));
/*     */           }
/*     */           if ("Y".equals(rs.getString("is_filter"))) {
/*     */             field.setFilterType(1);
/*     */             field.setFilterLabel(rs.getString("filter_label"));
/*     */             field.setFilterOperator(rs.getString("filter_operator") == null ? "=" : rs.getString("filter_operator"));
/*     */             field.setFilterMask(rs.getString("filter_mask"));
/*     */             field.setFilterInGrid(rs.getString("filter_show_in_grid"));
/*     */             field.setFilterValue(rs.getString("filter_value"));
/*     */             field.setFilterValue2(rs.getString("filter_value2"));
/*     */             field.setFilterSize(rs.getString("filter_size"));
/*     */             field.setFilterReferenceName(rs.getString("reference_name"));
/*     */             field.setFilterReferenceURL(rs.getString("reference_url"));
/*     */             field.setFilterRequestName(rs.getString("request_name"));
/*     */             field.setFilterRequestURL(rs.getString("request_url"));
/*     */           }
/*     */           this.value.addField(field);
/*     */         }
/*     */         if (rs.getString("sort_order_key") != null) {
/*     */           putSort(rs.getString("sort_order_key"), new Sort(fieldOrder, fieldName, rs.getString("sort_direction")));
/*     */         }
/*     */       }
/*     */     } finally {
/*     */       if (rs != null) {
/*     */         try { rs.close(); } catch (Exception ignored) {}
/*     */       }
/*     */       if (ps != null) {
/*     */         try { ps.close(); } catch (Exception ignored) {}
/*     */       }
/*     */     }
/*     */     loadGridColumns(numbering, withoutFocus, withoutCursor, withoutSortButton, withoutRefreshButton,
/*     */       resetCursor, hideFilterButton, hideExcelButton, enterDirection, rowColor);
/*     */   }
/*     */
/*     */   private void loadGridColumns(String numbering, String withoutFocus, String withoutCursor,
/*     */       String withoutSortButton, String withoutRefreshButton, String resetCursor,
/*     */       String hideFilterButton, String hideExcelButton, String enterDirection, String rowColor) throws Exception {
/*     */     Vector columns = new Vector();
/*     */     Hashtable fieldOrders = getFieldOrdersByName();
/*     */     PreparedStatement ps = null;
/*     */     ResultSet rs = null;
/*     */     try {
/*     */       ps = this.stored.getConnection().prepareStatement(
/*     */         "select field_order, column_align, column_nowrap, component_type, component_name, component_size, " +
/*     */         "component_mask, component_readonly, component_enable, component_required, component_checked_by, rowspan, colspan " +
/*     */         ", column_width, column_height, label_height " +
/*     */         "from core_grid_fields where grid_id = ? and is_column = 'Y' " +
/*     */         "order by nvl(column_order, field_order), field_order");
/*     */       ps.setInt(1, this.gridId.intValue());
/*     */       rs = ps.executeQuery();
/*     */       while (rs.next()) {
/*     */         Column column = new Column();
/*     */         column.setRef(rs.getString("field_order"));
/*     */         column.setType(rs.getString("component_type"));
/*     */         column.setName(rs.getString("component_name"));
/*     */         column.setMask(rs.getString("component_mask"));
/*     */         column.setSize(rs.getString("component_size"));
/*     */         column.setAlign(rs.getString("column_align"));
/*     */         column.setNowrap("Y".equals(rs.getString("column_nowrap")));
/*     */         column.setRowspan(rs.getString("rowspan"));
/*     */         column.setColspan(rs.getString("colspan"));
/*     */         column.setWidth(rs.getString("column_width"));
/*     */         column.setHeight(rs.getString("column_height"));
/*     */         column.setLabelHeight(rs.getString("label_height"));
/*     */         if (isEnabledAttribute(rs.getString("component_readonly"))) {
/*     */           column.setReadonly(toTagAttribute(rs.getString("component_readonly")));
/*     */         }
/*     */         if (isEnabledAttribute(rs.getString("component_required"))) {
/*     */           column.setRequired(toTagAttribute(rs.getString("component_required")));
/*     */         }
/*     */         if (hasText(rs.getString("component_enable"))) {
/*     */           column.setEnable(rs.getString("component_enable"));
/*     */         }
/*     */         String checkedBy = resolveFieldRef(rs.getString("component_checked_by"), fieldOrders);
/*     */         if (hasText(checkedBy)) {
/*     */           column.setChecked(checkedBy);
/*     */         }
/*     */         columns.addElement(column);
/*     */       }
/*     */     } finally {
/*     */       if (rs != null) {
/*     */         try { rs.close(); } catch (Exception ignored) {}
/*     */       }
/*     */       if (ps != null) {
/*     */         try { ps.close(); } catch (Exception ignored) {}
/*     */       }
/*     */     }
/*     */     if (columns.size() == 0) {
/*     */       throw new JspException("core_grid_fields jadvalida grid_id=" + this.gridId + " uchun IS_COLUMN='Y' column topilmadi");
/*     */     }
/*     */     Vector feet = loadGridFooter();
/*     */     applyGridDefinition(columns, feet, numbering, withoutFocus, withoutCursor, withoutSortButton,
/*     */       withoutRefreshButton, resetCursor, hideFilterButton, hideExcelButton, enterDirection, rowColor);
/*     */     loadSqlUtilTableDefinition();
/*     */   }
/*     */
/*     */   private void loadSqlUtilTableDefinition() throws Exception {
/*     */     String definition = this.stored.execFunction("Sql_Util.Get_Grid_Definition(" + this.gridId + ")");
/*     */     if (!hasText(definition)) {
/*     */       throw new JspException("Sql_Util.Get_Grid_Definition(" + this.gridId + ") bo'sh natija qaytardi.");
/*     */     }
/*     */     this.tableDefinitionScript = "<script>tdf=" + definition + "</script>";
/*     */   }
/*     */
/*     */   private Vector loadGridFooter() throws Exception {
/*     */     Vector feet = new Vector();
/*     */     Vector row = null;
/*     */     int lastRowOrder = -1;
/*     */     PreparedStatement ps = null;
/*     */     ResultSet rs = null;
/*     */     try {
/*     */       ps = this.stored.getConnection().prepareStatement(
/*     */         "select field_order, row_order, cell_order, footer_type, rows_count, footer_component_size, " +
/*     */         "footer_align, rowspan, colspan from core_grid_fields " +
/*     */         "where grid_id = ? and is_footer = 'Y' " +
/*     */         "order by nvl(row_order, 1), nvl(cell_order, field_order), field_order");
/*     */       ps.setInt(1, this.gridId.intValue());
/*     */       rs = ps.executeQuery();
/*     */       while (rs.next()) {
/*     */         int rowOrder = rs.getString("row_order") == null ? 1 : rs.getInt("row_order");
/*     */         if (row == null || rowOrder != lastRowOrder) {
/*     */           row = new Vector();
/*     */           feet.addElement(row);
/*     */           lastRowOrder = rowOrder;
/*     */         }
/*     */         FootCell cell = new FootCell();
/*     */         cell.setRef(rs.getString("field_order"));
/*     */         cell.setType(rs.getString("footer_type"));
/*     */         cell.setRows(rs.getString("rows_count"));
/*     */         cell.setSize(rs.getString("footer_component_size"));
/*     */         cell.setAlign(rs.getString("footer_align"));
/*     */         cell.setRowspan(rs.getString("rowspan"));
/*     */         cell.setColspan(rs.getString("colspan"));
/*     */         row.addElement(cell);
/*     */       }
/*     */     } finally {
/*     */       if (rs != null) {
/*     */         try { rs.close(); } catch (Exception ignored) {}
/*     */       }
/*     */       if (ps != null) {
/*     */         try { ps.close(); } catch (Exception ignored) {}
/*     */       }
/*     */     }
/*     */     return feet.size() == 0 ? null : feet;
/*     */   }
/*     */
/*     */   private Hashtable getFieldOrdersByName() {
/*     */     Hashtable fieldOrders = new Hashtable();
/*     */     Vector fields = this.value.getFields();
/*     */     for (int i = 0; i < fields.size(); i++) {
/*     */       Field field = (Field)fields.elementAt(i);
/*     */       fieldOrders.put(field.getId(), field.getId());
/*     */       if (field.getName() != null) {
/*     */         fieldOrders.put(field.getName().toUpperCase(), field.getId());
/*     */       }
/*     */     }
/*     */     return fieldOrders;
/*     */   }
/*     */
/*     */   private String resolveFieldRef(String fieldRef, Hashtable fieldOrders) {
/*     */     if (!hasText(fieldRef)) {
/*     */       return fieldRef;
/*     */     }
/*     */     String ref = fieldRef.trim();
/*     */     String byName = (String)fieldOrders.get(ref.toUpperCase());
/*     */     return byName == null ? ref : byName;
/*     */   }
/*     */
/*     */   private void applyGridDefinition(Vector columns, Vector feet, String numbering, String withoutFocus, String withoutCursor,
/*     */       String withoutSortButton, String withoutRefreshButton, String resetCursor,
/*     */       String hideFilterButton, String hideExcelButton, String enterDirection, String rowColor) {
/*     */     StringBuffer buf = new StringBuffer(",c:");
/*     */     buf.append(columns.toString());
/*     */     if (feet != null) {
/*     */       buf.append(",t:").append(feet.toString());
/*     */     }
/*     */     int flag = getGridFlag(numbering, withoutFocus, withoutCursor, withoutSortButton,
/*     */       withoutRefreshButton, resetCursor, hideFilterButton, hideExcelButton);
/*     */     if (flag != 0) {
/*     */       buf.append(",f:").append(flag);
/*     */     }
/*     */     if (hasText(enterDirection)) {
/*     */       buf.append(",e:").append(getEnterDirection(enterDirection));
/*     */     }
/*     */     if (hasText(rowColor)) {
/*     */       buf.append(",rc:'").append(Util.quotesEsc(rowColor)).append("'");
/*     */     }
/*     */     this.value.setGridDefinition(buf.toString());
/*     */     this.value.setColumns(columns);
/*     */     this.value.setFeet(feet);
/*     */     this.value.setResetCursor((flag & 0x80) != 0);
/*     */   }
/*     */
/*     */   private int getGridFlag(String numbering, String withoutFocus, String withoutCursor,
/*     */       String withoutSortButton, String withoutRefreshButton, String resetCursor,
/*     */       String hideFilterButton, String hideExcelButton) {
/*     */     int flag = 0;
/*     */     if ("Y".equals(numbering)) flag |= 0x1;
/*     */     if ("Y".equals(withoutFocus)) flag |= 0x2;
/*     */     if ("Y".equals(withoutCursor)) flag |= 0x4;
/*     */     if ("Y".equals(withoutSortButton)) flag |= 0x8;
/*     */     if ("Y".equals(withoutRefreshButton)) flag |= 0x10;
/*     */     if (this.debug) flag |= 0x20;
/*     */     if ("Y".equals(hideFilterButton)) flag |= 0x40;
/*     */     if ("Y".equals(resetCursor)) flag |= 0x80;
/*     */     if ("Y".equals(hideExcelButton)) flag |= 0x100;
/*     */     return flag;
/*     */   }
/*     */
/*     */   private int getEnterDirection(String enterDirection) {
/*     */     if ("up".equals(enterDirection)) return 0;
/*     */     if ("right".equals(enterDirection)) return 1;
/*     */     if ("left".equals(enterDirection)) return 3;
/*     */     return 2;
/*     */   }
/*     */
/*     */   private boolean isEnabledAttribute(String value) {
/*     */     return hasText(value) && !"N".equals(value);
/*     */   }
/*     */
/*     */   private String toTagAttribute(String value) {
/*     */     return "Y".equals(value) ? "" : value;
/*     */   }
/*     */
/*     */   private String normalizeColorExpression(String color) {
/*     */     if (!hasText(color)) {
/*     */       return color;
/*     */     }
/*     */     String value = color.trim();
/*     */     if (value.startsWith("'") || value.startsWith("\"") || value.indexOf("d(") >= 0 ||
/*     */         value.indexOf("?") >= 0 || value.indexOf("(") >= 0) {
/*     */       return value;
/*     */     }
/*     */     return "'" + value + "'";
/*     */   }
/*     */
/*     */   private boolean hasText(String value) {
/*     */     return value != null && value.trim().length() > 0;
/*     */   }
/*     */   private int getUserLanguageIndex() {
/*     */     return this.user == null || this.user.getLanguageIndex() <= 0 ? 1 : this.user.getLanguageIndex();
/*     */   }
/*     */   private String getSecureUserCode() {
/*     */     Object employee = null;
/*     */     if (this.session != null) {
/*     */       employee = this.session.getAttribute("employee");
/*     */       if (employee == null) {
/*     */         try {
/*     */           employee = this.session.getValue("employee");
/*     */         } catch (Exception ignored) {}
/*     */       }
/*     */     }
/*     */     if (employee != null && hasText(employee.toString())) {
/*     */       return employee.toString();
/*     */     }
/*     */     String userCode = getUserMethodValue("getUserCode");
/*     */     if (hasText(userCode)) {
/*     */       return userCode;
/*     */     }
/*     */     return this.user == null ? "" : this.user.toString();
/*     */   }
/*     */
/*     */   private String getUserMethodValue(String methodName) {
/*     */     if (this.user == null) {
/*     */       return null;
/*     */     }
/*     */     try {
/*     */       Method method = this.user.getClass().getMethod(methodName);
/*     */       Object value = method.invoke(this.user);
/*     */       return value == null ? null : value.toString();
/*     */     } catch (Exception ignored) {
/*     */       return null;
/*     */     }
/*     */   }
/*     */
/*     */   private String mergeWhere(String gridWhere, String tagWhere) {
/*     */     if (gridWhere == null || gridWhere.trim().length() == 0) {
/*     */       return tagWhere;
/*     */     }
/*     */     if (tagWhere == null || tagWhere.trim().length() == 0) {
/*     */       return gridWhere;
/*     */     }
/*     */     return "(" + gridWhere + ") and (" + tagWhere + ")";
/*     */   }
/*     */
/*     */   private void readRequest() throws Exception {
/* 180 */     this.value.setRecordsPerPage(this.request.getParameter("_rpp"));
/* 181 */     this.value.setPageNubmer(this.request.getParameter("page"));
/*     */
/*     */
/*     */
/* 185 */     if (this.request.getParameter("clearFilter") != null) {
/* 186 */       this.value.clearFilter();
/*     */     }
/* 188 */     Vector<Field> fields = this.value.getFields();
/*     */
/* 190 */     int len = fields.size();
/* 191 */     for (int i = 0; i < len; i++) {
/* 192 */       Field field = fields.elementAt(i);
/* 193 */       if (field.getFilterType() > 0) {
/* 194 */         String[] values = this.request.getParameterValues("f" + field
/* 195 */             .getId());
/* 196 */         if (values != null) {
/* 197 */           field.setFilterValue(Util.encodeISO(values[0]));
/* 198 */           if (values.length > 1) {
/* 199 */             field.setFilterValue2(Util.encodeISO(values[1]));
/*     */           }
/*     */         }
/* 202 */         if (field.getFilterType() == 3) {
/*     */
/* 204 */           String operator = this.request.getParameter("fo" + field.getId());
/* 205 */           field.setFilterOperator(operator);
/*     */         }
/*     */       }
/*     */     }
/*     */
/* 210 */     TreeMap<Object, Object> tempSorts = null;
/* 211 */     for (int j = 0; j < len; j++) {
/* 212 */       Field field = fields.elementAt(j);
/* 213 */       String[] values = this.request.getParameterValues("s" + field.getId());
/* 214 */       if (values != null) {
/* 215 */         if (tempSorts == null) {
/* 216 */           tempSorts = new TreeMap<>();
/*     */         }
/* 218 */         tempSorts.put(values[0], new Sort(field
/* 219 */               .getId(), field.getName(), values[1]));
/*     */       }
/*     */     }
/* 222 */     this.value.setSorts(tempSorts);
/*     */   }
/*     */
/*     */   private void writeData(JspWriter out) throws Exception {
/* 226 */     String sqlFields = this.value.getSQLFields();
/* 227 */     String sqlFilter = this.value.getSQLFilter();
/* 228 */     String sqlCenter = "from " + this.value.getFrom();
/* 229 */     String sqlTemp = " where ";
/* 230 */     String sqlOrderByClause = this.value.getSQLOrderByClause();
/* 231 */     if (this.where != null && this.where.length() > 0) {
/* 232 */       sqlCenter = sqlCenter + sqlTemp + this.where;
/* 233 */       sqlTemp = " and ";
/*     */     }
/* 235 */     if (sqlFilter != null) {
/* 236 */       sqlCenter = sqlCenter + sqlTemp + sqlFilter;
/*     */     }
/* 238 */     out.write("<script>tdd={SN:'" + this.sessionName + "',f:");
/* 239 */     out.print(this.value.getFilterDefinition());
/*     */
/* 241 */     String filterValues = this.value.getFilterValues();
/* 242 */     if (filterValues != null) {
/* 243 */       out.write(",");
/* 244 */       out.write(filterValues);
/*     */     }
/* 246 */     String sortValues = this.value.getSortValues();
/* 247 */     if (sortValues != null) {
/* 248 */       out.write(",");
/* 249 */       out.write(sortValues);
/*     */     }
/* 251 */     if (this.function != null) {
/* 252 */       String functionResult = this.stored.execFunction(this.function);
/* 253 */       if (functionResult != null) {
/* 254 */         out.write(",");
/* 255 */         out.write("fr:");
/* 256 */         out.write("'" + functionResult + "'");
/*     */       }
/*     */     }
/*     */
/* 260 */     String sum = this.value.getSQLSumFields();
/*     */
/*     */
/*     */
/*     */
/*     */
/* 266 */     if (this.value.getRecordsPerPage() > 0) {
/*     */       int totalRecords;
/*     */
/* 269 */       if (sum != null) {
/* 270 */         String query = "select count(1)||','||" + sum + sqlCenter;
/* 271 */         String s = this.stored.execSelect(query);
/* 272 */         if (s == null) {
/* 273 */           throw new JspException("Total count va sum topilmadi.");
/*     */         }
/* 275 */         totalRecords = Integer.parseInt(s.substring(0, s.indexOf(',')));
/* 276 */         out.write(",sum:" + s.substring(s.indexOf(',') + 1));
/*     */       } else {
/*     */
/* 279 */         String query = "select count(1)" + sqlCenter;
/* 280 */         totalRecords = Integer.parseInt(this.stored.execSelect(query));
/*     */       }
/*     */
/*     */
/*     */
/*     */
/*     */
/* 287 */       int maxPage = totalRecords / this.value.getRecordsPerPage() + ((totalRecords % this.value.getRecordsPerPage() != 0) ? 1 : 0);
/* 288 */       if (this.value.getPageNumber() < 1 || this.value.getPageNumber() > maxPage) {
/* 289 */         this.value.setPageNumber(1);
/*     */       }
/* 291 */       out.write(",p:{PN:" + this.value.getPageNumber() + ",MP:" + maxPage + ",RPP:" + this.value
/* 292 */           .getRecordsPerPage() + ",TR:" + totalRecords + "},d:[");
/*     */
/*     */
/*     */
/*     */
/* 297 */       String str = "select " + sqlFields + sqlCenter + sqlOrderByClause;
/* 302 */       if (totalRecords > 0) {
/* 303 */         out.write(this.stored.execSelect(str, this.value.getPageNumber(), this.value
/* 304 */               .getRecordsPerPage(), ","));
/*     */       }
/* 306 */       out.write("]");
/*     */     } else {
/* 308 */       if (sum != null) {
/* 309 */         out.write(",sum:" + this.stored.execSelect("select " + sum + sqlCenter));
/*     */       }
/* 311 */       out.write(",d:[");
/* 312 */       String query = "select " + sqlFields + sqlCenter + sqlOrderByClause;
/* 317 */       out.write(this.stored.execSelect(query, ","));
/* 318 */       out.write("]");
/*     */     }
/* 320 */     out.write("}</script>");
/*     */   }
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */   public static String getRefreshedTDD(PageContext pageContext, String sessionName, int pageNumber) throws NumberFormatException, Exception {
/* 383 */     HttpSession session = pageContext.getSession();
/* 384 */     StoredObject stored = null;
/* 385 */     Hashtable userCache = null;
/*     */
/* 387 */     if (session != null) {
/* 388 */       stored = (StoredObject)session.getValue("stored");
/* 389 */       userCache = (Hashtable)session.getValue("user_cache");
/*     */     }
/*     */
/* 392 */     Value value = null;
/* 393 */     if (userCache != null) {
/* 394 */       value = (Value)userCache.get(sessionName);
/*     */     }
/* 396 */     value.setPageNumber(pageNumber);
/*     */
/* 398 */     String sqlFields = value.getSQLFields();
/* 399 */     String sqlFilter = value.getSQLFilter();
/* 400 */     String sqlCenter = "from " + value.getFrom();
/* 401 */     String sqlTemp = " where ";
/* 402 */     String sqlOrderByClause = value.getSQLOrderByClause();
/* 403 */     String function = value.getFunction();
/* 404 */     if (value.getWhere() != null && value.getWhere().length() > 0) {
/* 405 */       sqlCenter = sqlCenter + sqlTemp + value.getWhere();
/* 406 */       sqlTemp = " and ";
/*     */     }
/* 408 */     if (sqlFilter != null) {
/* 409 */       sqlCenter = sqlCenter + sqlTemp + sqlFilter;
/*     */     }
/*     */
/* 412 */     StringBuffer sb = new StringBuffer(1000);
/*     */
/* 414 */     sb.append("{SN:'" + sessionName + "',f:");
/* 415 */     sb.append(value.getFilterDefinition());
/*     */
/* 417 */     String filterValues = value.getFilterValues();
/* 418 */     if (filterValues != null) {
/* 419 */       sb.append(",");
/* 420 */       sb.append(filterValues);
/*     */     }
/* 422 */     String sortValues = value.getSortValues();
/* 423 */     if (sortValues != null) {
/* 424 */       sb.append(",");
/* 425 */       sb.append(sortValues);
/*     */     }
/* 427 */     if (function != null) {
/* 428 */       String functionResult = stored.execFunction(function);
/* 429 */       if (functionResult != null) {
/* 430 */         sb.append(",");
/* 431 */         sb.append("fr:");
/* 432 */         sb.append("'").append(functionResult).append("'");
/*     */       }
/*     */     }
/* 435 */     String sum = value.getSQLSumFields();
/*     */
/* 437 */     if (value.getRecordsPerPage() > 0) {
/*     */       int totalRecords;
/*     */
/* 440 */       if (sum != null) {
/* 441 */         String query = "select count(1)||','||" + sum + sqlCenter;
/* 442 */         String s = stored.execSelect(query);
/* 443 */         if (s == null) {
/* 444 */           throw new JspException("Total count va sum topilmadi.");
/*     */         }
/* 446 */         totalRecords = Integer.parseInt(s.substring(0, s.indexOf(',')));
/* 447 */         sb.append(",sum:" + s.substring(s.indexOf(',') + 1));
/*     */       } else {
/*     */
/* 450 */         String query = "select count(1)" + sqlCenter;
/* 451 */         totalRecords = Integer.parseInt(stored.execSelect(query));
/*     */       }
/*     */
/*     */
/*     */
/*     */
/* 457 */       int maxPage = totalRecords / value.getRecordsPerPage() + ((totalRecords % value.getRecordsPerPage() != 0) ? 1 : 0);
/* 458 */       if (value.getPageNumber() < 1 || value.getPageNumber() > maxPage) {
/* 459 */         value.setPageNumber(1);
/*     */       }
/* 461 */       sb.append(",p:{PN:" + value.getPageNumber() + ",MP:" + maxPage + ",RPP:" + value
/* 462 */           .getRecordsPerPage() + ",TR:" + totalRecords + "},d:[");
/*     */
/* 464 */       String str = "select " + sqlFields + sqlCenter + sqlOrderByClause;
/* 465 */       sb.append(stored.execSelect(str, value.getPageNumber(), value
/* 466 */             .getRecordsPerPage(), ","));
/* 467 */       sb.append("]");
/*     */     } else {
/* 469 */       if (sum != null) {
/* 470 */         sb.append(",sum:").append(stored
/* 471 */             .execSelect("select " + sum + sqlCenter));
/*     */       }
/* 473 */       sb.append(",d:[");
/* 474 */       String query = "select " + sqlFields + sqlCenter + sqlOrderByClause;
/* 475 */       sb.append(stored.execSelect(query, ","));
/* 476 */       sb.append("]");
/*     */     }
/* 478 */     sb.append("}");
/*     */
/* 480 */     return sb.toString();
/*     */   }
/*     */ }


/* Location:              c:\Users\azizbek\Oracle\Projects\Java\fido\cms\cms_tags.jar\\uz\fido_biznes\cms\tags\table\DynamicGridTag.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */
