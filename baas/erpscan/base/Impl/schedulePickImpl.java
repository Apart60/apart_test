package erpscan.base.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.justep.baas.action.ActionContext;
import com.justep.baas.data.DataUtils;
import com.justep.baas.data.Row;
import com.justep.baas.data.Table;
import com.justep.baas.data.Transform;
import erpscan.Common;
import erpscan.base.Basecommon;
import erpscan.base.baseUtils.baseUtil;
import erpscan.base.entity.itemStock;
import erpscan.excelopera;
import erpscan.save.Pdacommon;
import erpscan.save.Pdasave;
import erpscan.utils.ExcelExportUtil;
import erpscan.utils.PagedDataIterator;
import org.apache.poi.ss.usermodel.Cell;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;
import java.util.function.BiConsumer;

// 如果你是来维护这个的，那你有福了:)
public class schedulePickImpl {

    private static final String DATASOURCE = Common.DATASOURCE;

    public static JSONObject getSelectScheduleItem(JSONObject params, ActionContext context) throws SQLException, NamingException {
        Connection conn = context.getConnection(DATASOURCE);

        String houseid = params.getString("houseid");
        String scheduleid = params.getString("scheduleid");
        String schedule_pick_id = params.getString("schedule_pick_id");
        JSONArray itemList = params.getJSONArray("itemList");

        String searchcontent = params.getString("searchcontent");
        String nameinput = params.getString("nameinput");
        String sformatinput = params.getString("sformatinput");
        String classids = params.getString("classids");
        String searchClassChild = params.getString("searchClassChild");

        int offset = params.getInteger("offset");
        int limit = params.getInteger("limit");
        Integer countbit = params.getInteger("countbit");
        countbit = (countbit == null ? 2 : countbit);

        String orderBys = params.getString("orderBys");

        StringBuilder sqlBuilder = new StringBuilder();
        StringBuilder countSqlBuilder = new StringBuilder();
        StringBuilder sql = new StringBuilder();
        List<Object> fparams = new ArrayList<>();
        Table table = null;
        JSONObject rt = new JSONObject();

        String temporderBys = "i.create_time desc,i.codeid desc";
        if (orderBys.equals("")) {
            orderBys = temporderBys;
        } else if (orderBys.indexOf("classname") > -1) {
            orderBys = "cs." + orderBys + "," + temporderBys;
        } else if (orderBys.indexOf("count") > -1 || orderBys.indexOf("money") > -1 || orderBys.indexOf("checkout_count") > -1) {
            orderBys = "k." + orderBys;
        } else if (orderBys.indexOf("codeid") > -1) {
            orderBys = "i." + orderBys + ", batchno asc";
        } else {
            orderBys = orderBys + "," + temporderBys;
        }

        sqlBuilder.append(" SELECT i.*, ")
                .append(" ifnull(ic.classname,'') as classname, ")
                .append("")
                .append("")
                .append("")
                .append("")
                .append(" round(sum(s.checkout_count) , " + countbit + " ) AS checkout_count, ")
                .append(" round(sum(s.count) , " + countbit + " ) AS count ");

        countSqlBuilder.append(" SELECT count(*) ");

        sql.append(" FROM schedule_pick sp ")
                .append(" LEFT JOIN prodrequisition_work_total pwt ON sp.relation_schedule_id = pwt.scheduleid ")
                .append(" INNER JOIN iteminfo i ON pwt.itemid = i.itemid ")
                .append(" LEFT JOIN itemclass ic ON ic.classid = i.classid ")
                .append(" LEFT JOIN stock s ON s.itemid = i.itemid ");

        sql.append(" WHERE sp.relation_schedule_id = ? ")
                .append(" AND sp.schedule_pick_id = ? ")
                .append(" AND s.houseid = ? ");
        fparams.add(scheduleid);
        fparams.add(schedule_pick_id);
        fparams.add(houseid);

        if (itemList != null && itemList.size() > 0) {
            sql.append(" AND pwt.itemid NOT IN ( ");
            for (int i = 0; i < itemList.size(); i++) {
                sql.append(" ? ");
                if (i < itemList.size() - 1) {
                    sql.append(" , ");
                }
                fparams.add(itemList.getString(i));
            }
            sql.append(" ) ");
        }

        if (classids != null && !classids.equals("")) {
            if (searchClassChild.equals("1")) {
                sql.append(" AND i.classid = ? ");
                fparams.add(classids);
            } else {
                sql.append(" AND i.classid IN ( ");
                String[] classidarr = classids.replaceAll("'", "").split(",");
                for (int i = 0; i < classidarr.length; i++) {
                    sql.append(" ? ");
                    if (i < classidarr.length - 1) {
                        sql.append(" , ");
                    }
                    fparams.add(classidarr[i]);
                }
                sql.append(" ) ");
            }
        }

        if (searchcontent != null && !searchcontent.equals("")) {
            sql.append(" AND ( ")
                    .append(" lower(i.mcode) like ? or (i.barcode like ?)  or (i.remark like ?) ")
                    .append(" or ( i.property1<>'' and i.property1 like ? ) ")
                    .append(" or ( i.property2<>'' and i.property2 like ? ) ")
                    .append(" or ( i.property3<>'' and i.property3 like ? ) ")
                    .append(" or ( i.property4<>'' and i.property4 like ? ) ")
                    .append(" or ( i.property5<>'' and i.property5 like ? ) ")
                    .append(" ) ");
            fparams.add("%" + searchcontent.toLowerCase() + "%");
            String fuzzySearchcontent = "%" + searchcontent + "%";
            for (int i = 0; i < 7; i++) {
                fparams.add(fuzzySearchcontent);
            }
        }

        if (nameinput != null && !nameinput.equals("")) {
            sql.append(" AND ( i.codeid like ? or i.itemname like ? ) ");
            fparams.add("%" + nameinput + "%");
            fparams.add("%" + nameinput + "%");
        }

        if (sformatinput != null && !sformatinput.equals("")) {
            sql.append(" AND i.sformat like ? ");
            fparams.add("%" + sformatinput + "%");
        }

        sql.append(" GROUP BY i.itemid ")
                .append(" ORDER BY ").append(orderBys);

        sqlBuilder.append(sql);
        countSqlBuilder.append(sql);
        try {
            table = DataUtils.queryData(conn, sqlBuilder.toString(), fparams, null, offset, limit);
            int scount = 0;
            if (offset == 0) {
                Object countObject = DataUtils.getValueBySQL(conn, countSqlBuilder.toString(), fparams);

                if (countObject == null) {
                    scount = 0;
                } else {
                    scount = Integer.parseInt(countObject.toString());
                }
                table.setTotal(scount);
            }
            rt.put("table", Transform.tableToJson(table));
            rt.put("rowsize", scount);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rt;
    }

    public static JSONObject getSchedulePickDetailExcel(JSONObject params, ActionContext context)
            throws SQLException, NamingException, IOException {
        HttpServletResponse resp = (HttpServletResponse) context.get(ActionContext.RESPONSE);
        HttpServletRequest request = (HttpServletRequest) context.get(ActionContext.REQUEST);
        Connection conn = context.getConnection(DATASOURCE);

        // 解码参数
        String Tdata = new String(request.getParameter("data").getBytes("iso-8859-1"), "utf-8");
        String datastr = new String(request.getParameter("datastr").getBytes("iso-8859-1"), "utf-8");
        String filname = new String(request.getParameter("filname").getBytes("iso-8859-1"), "utf-8");

        JSONObject pdata = JSONObject.parseObject(Tdata);
        String mainids = pdata.getString("mainids");
        String companyid = pdata.getString("companyid");
        String userid = pdata.getString("userid");
        String user = pdata.getString("user");

        String datetypeselect = pdata.getString("datetypeselect");
        String begininput = pdata.getString("begininput");
        String endinput = pdata.getString("endinput");

        String customerid = pdata.getString("customerid");
        String companyname = pdata.getString("companyname");
        String houseid = pdata.getString("houseid");

        String searchcontent = pdata.getString("searchcontent");
        String iteminput = pdata.getString("iteminput");
        Integer status = pdata.getInteger("statusselect");

        String dtype1 = pdata.getString("dtype1");
        Integer cfilter = pdata.getInteger("cfilter");
        Integer hfilter = pdata.getInteger("hfilter");
        Integer countbit = pdata.getInteger("countbit");
        Integer moneybit = pdata.getInteger("moneybit");

        JSONArray rulesrowdata = pdata.getJSONArray("rulesrowdata");
        Integer titleSelect = pdata.getInteger("titleSelect");

        // === 1. 构建 SQL 和参数 ===
        int m = 0;
        StringBuilder sqlBuilder = new StringBuilder();
        List<Object> fparams = new ArrayList<>();

        sqlBuilder.append(" SELECT i.codeid,i.itemname,i.sformat,i.mcode,i.classid,i.unit,i.imgurl,i.barcode,i.property1,i.property2,i.property3,i.property4,i.property5,i.unitstate1,i.unitset1,i.unitstate2,i.unitset2,i.unitstate3,i.unitset3, ")
                .append(" ifnull(ic.classname,'') as classname, ")
                .append(" spd.schedule_pick_id,spd.goods_number,spd.companyid,spd.operate_time,spd.operate_by,spd.orderid,spd.itemid,spd.houseid,spd.customerid,spd.stype, ")
                .append(" spd.status,spd.remark,spd.create_id,spd.create_by,spd.create_time,spd.update_id,spd.update_by,spd.update_time,spd.originalbill,spd.batchno, ")
                .append(" round(spd.count, ").append(countbit).append(") as count, ")
                .append(" round(spd.price, ").append(moneybit).append(") as price, ")
                .append(" round(spd.total, ").append(moneybit).append(") as total, ")
                .append(" sp.iproperty AS iproperty,sp.orderid AS parentOrderId, ")
                .append(" sh.housename AS housename, ")
                .append(" c.customername AS customername, ")
                .append(" s.staffname AS staffname ");

        sqlBuilder.append(" FROM schedule_pick_detail spd ")
                .append(" INNER JOIN schedule_pick sp ON sp.schedule_pick_id = spd.schedule_pick_id ")
                .append(" INNER JOIN iteminfo i ON i.itemid = spd.itemid and i.companyid = spd.companyid ")
                .append(" LEFT JOIN itemclass ic on i.classid=ic.classid ")
                .append(" LEFT JOIN staffinfo s ON sp.operate_by = s.staffid ")
                .append(" LEFT JOIN customer c ON c.customerid = sp.customerid ")
                .append(" LEFT JOIN storehouse sh ON sh.houseid = sp.houseid ");

        sqlBuilder.append(" where spd.companyid = ? ");
        fparams.add(companyid);

        if (mainids != null && !mainids.isEmpty()) {
            sqlBuilder.append(" and spd.schedule_pick_id in ( ");
            String[] temp = mainids.split(",");
            for (m = 0; m < temp.length; m++) {
                if (m != 0) {
                    sqlBuilder.append(",");
                }
                sqlBuilder.append(" ? ");
                fparams.add(temp[m]);
            }
            sqlBuilder.append(" ) ");
        }

        if (customerid != null && !customerid.equals("")) {
            sqlBuilder.append(" and sp.customerid = ? ");
            fparams.add(customerid);
        }

        if (houseid != null && !houseid.equals("")) {
            sqlBuilder.append(" and sp.houseid = ? ");
            fparams.add(houseid);
        }

        if (status != null && status != -1) {
            sqlBuilder.append(" and sp.status = ? ");
            fparams.add(status);
        } else if (status != null && status == -1) {
            sqlBuilder.append(" and (sp.status in (0,1,3) ) ");
        }

        //拼接dtype1
        if (dtype1 != null && !dtype1.isEmpty()) {
            sqlBuilder.append(" and ? ");
            fparams.add(dtype1);
        }

        if (searchcontent != null && !searchcontent.equals("")) {
            String fuzzySearch = "%" + searchcontent + "%";
            sqlBuilder.append(" and ( ")
                    .append(" sp.orderid like ? ")
                    .append(" or s.staffname like ? ")
                    .append(" or sp.remark like ? ")
                    .append(" or sp.originalbill like ? ")
                    .append(" or sp.create_by like ? ")
                    .append(" or sp.update_by like ? ")
                    .append(" ) ");
            for (m = 0; m < 6; m++) {
                fparams.add(fuzzySearch);
            }
        }

        if (iteminput != null && !iteminput.equals("")) {
            String fuzzyItemInput = "%" + iteminput + "%";
            sqlBuilder.append(" and ( ")
                    .append(" spd.batchno like ? ")
                    .append(" or spd.remark like ? ")
                    .append(" or i.codeid like ? ")
                    .append(" or i.itemname like ? ")
                    .append(" or i.barcode like ? ")
                    .append(" or i.sformat like ? ")
                    .append(" or ( i.property1<>'' and i.property1 like ? ) ")
                    .append(" or ( i.property2<>'' and i.property2 like ? ) ")
                    .append(" or ( i.property3<>'' and i.property3 like ? ) ")
                    .append(" or ( i.property4<>'' and i.property4 like ? ) ")
                    .append(" or ( i.property5<>'' and i.property5 like ? ) ")
                    .append(" ) ");
            for (m = 0; m < 11; m++) {
                fparams.add(fuzzyItemInput);
            }
        }

        // 时间
        if (datetypeselect != null && !datetypeselect.equals("")) {
            if (begininput != null && !begininput.equals("")) {
                sqlBuilder.append(" and sp." + datetypeselect + ">= ? ");
                fparams.add(begininput);
            }

            if (endinput != null && !endinput.equals("")) {
                sqlBuilder.append(" and sp." + datetypeselect + "<= ? ");
                fparams.add(endinput + " 23:59:59");
            }
        }

        // cfilter
        if (cfilter != null && cfilter > 0 && userid != null) {
            sqlBuilder.append(" and ( c.usercount = 0 or (select 1 from t_customer_userid where customerid=c.customerid and userid = ? limit 1)  ) ");
            fparams.add(userid);
        }

        //hfilter
        if (hfilter != null && hfilter > 0 && userid != null) {
            sqlBuilder.append(" and ( sh.usercount = 0 or (select 1 from t_storehouse_userid where houseid=sh.houseid and userid = ? limit 1)  ) ");
            fparams.add(userid);
        }

        if (rulesrowdata != null && rulesrowdata.size() > 0) {// 数据规则
            String datarule = Basecommon.getDataruleSQl(rulesrowdata, " sp.create_id ", userid);
            if (!datarule.equals("")) {
                sqlBuilder.append(" and " + datarule);
            }
        }

        sqlBuilder.append(" order by ").append(" sp.orderid DESC,spd.goods_number ASC ");

        // === 2. 定义列配置 ===
        Map<String, String> columnConfig = new LinkedHashMap<>();
        columnConfig.put("number", "6#编号");
        columnConfig.put("operate_time", "15#领料日期");
        columnConfig.put("orderid", "15#单据编号");
        columnConfig.put("parentOrderId", "15#主单据编号");
        columnConfig.put("goods_number", "15#序号");
        columnConfig.put("status", "12#单据状态");
        columnConfig.put("housename", "15#出库仓库");
        columnConfig.put("customername", "15#单位部门");
        columnConfig.put("barcode", "15#商品码");
        columnConfig.put("codeid", "15#商品编号");
        columnConfig.put("itemname", "15#商品名称");
        columnConfig.put("sformat", "15#商品规格");
        columnConfig.put("batchno", "15#批号");

        // 设置商品属性
        Table itemproperty = excelopera.queryItemproperty(companyid, conn);
        for (int i = 0; i < itemproperty.getRows().size(); i++) {
            Row itempropertyrow = itemproperty.getRows().get(i);
            columnConfig.put(itempropertyrow.getValue("propertyname").toString(), "15#" + itempropertyrow.getValue("propertyshow"));
        }

        columnConfig.put("unit", "15#单位");
        columnConfig.put("count", "8#数量");
        columnConfig.put("price", "8#单价");
        columnConfig.put("total", "12#金额");
        columnConfig.put("classname", "15#商品分类");
        columnConfig.put("remark", "15#备注");
        columnConfig.put("originalbill", "15#原单号");
        columnConfig.put("staffname", "15#经手人");
        columnConfig.put("update_by", "15#修改人");
        columnConfig.put("update_time", "15#修改时间");

        List<String> sumFields = new ArrayList<>();
        sumFields.add("count");
        sumFields.add("total");


        // === 3. 定义自定义字段处理器 ===
        Map<String, BiConsumer<Cell, Map<String, Object>>> customSetters = new HashMap<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

//        customSetters.put("create_time", (cell, row) -> {
//            Timestamp timestamp = (Timestamp) row.get("create_time");
//            cell.setCellValue(timestamp != null ? timestamp.toLocalDateTime().format(formatter) : "");
//        });

        customSetters.put("update_time", (cell, row) -> {
            Timestamp timestamp = (Timestamp) row.get("update_time");
            cell.setCellValue(timestamp != null ? timestamp.toLocalDateTime().format(formatter) : "");
        });

        customSetters.put("operate_time", (cell, row) -> {
            Date date = (Date) row.get("operate_time");
            Timestamp timestamp = date != null ? new Timestamp(date.getTime()) : null;
            cell.setCellValue(timestamp != null ? timestamp.toLocalDateTime().format(dateFormatter) : "");
        });

        customSetters.put("status", (cell, row) -> {
            cell.setCellValue(Pdacommon.getSchedulePickStatus(row.get("status").toString()));
        });

        // 其他字段默认 toString 即可

        // === 4. 分页获取数据并包装为 Iterator ===
        Iterator<Map<String, Object>> dataIterator = new PagedDataIterator(conn, sqlBuilder.toString(), fparams, Common.Exportlimit);

        // === 5. 调用工具类导出 ===
        try {

            ExcelExportUtil.exportToResponse(
                    resp,
                    filname,
                    "排产领料明细",
                    companyname + "——" + "排产领料明细单",
                    datastr,
                    columnConfig,
                    dataIterator,
                    customSetters,
                    sumFields
            );

            // === 6. 记录日志/更新次数 ===
            conn.setAutoCommit(false);

            baseUtil.writeLog(conn, companyid, Pdacommon.getDatalogBillChangefunc("schedule_pick", "42"), "导出", "", "导出排产领料明细单", userid, user);

            StringBuilder updateOutExcelSql = new StringBuilder(" UPDATE schedule_pick SET outexcel = outexcel + 1 ");
            if (mainids != null && !mainids.isEmpty()) {
                updateOutExcelSql.append(" WHERE schedule_pick_id IN ( ");
                String[] temp = mainids.split(",");
                for (m = 0; m < temp.length; m++) {
                    updateOutExcelSql.append(" ?,");
                }
                updateOutExcelSql.deleteCharAt(updateOutExcelSql.length() - 1);
                updateOutExcelSql.append(" ) ");
                PreparedStatement pstmt = conn.prepareStatement(updateOutExcelSql.toString());
                for (m = 0; m < temp.length; m++) {
                    pstmt.setString(m + 1, temp[m]);
                }
                pstmt.executeUpdate();
            } else {
                updateOutExcelSql.append(" WHERE companyid = ? ");
                PreparedStatement pstmt = conn.prepareStatement(updateOutExcelSql.toString());
                pstmt.setString(1, companyid);
                pstmt.executeUpdate();
            }
            conn.commit();

        } catch (Exception e) {
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException ex) {
            }
        } finally {
            if (conn != null && !conn.isClosed()) conn.close();
        }

        return null;
    }

    public static JSONObject getSchedulePickTotalExcel(JSONObject params, ActionContext context)
            throws SQLException, NamingException, IOException {
        HttpServletResponse resp = (HttpServletResponse) context.get(ActionContext.RESPONSE);
        HttpServletRequest request = (HttpServletRequest) context.get(ActionContext.REQUEST);
        Connection conn = context.getConnection(DATASOURCE);

        // 解码参数
        String Tdata = new String(request.getParameter("data").getBytes("iso-8859-1"), "utf-8");
        String datastr = new String(request.getParameter("datastr").getBytes("iso-8859-1"), "utf-8");
        String filname = new String(request.getParameter("filname").getBytes("iso-8859-1"), "utf-8");

        JSONObject pdata = JSONObject.parseObject(Tdata);
        String mainids = pdata.getString("mainids");
        String companyid = pdata.getString("companyid");
        String userid = pdata.getString("userid");
        String user = pdata.getString("user");

        String datetypeselect = pdata.getString("datetypeselect");
        String begininput = pdata.getString("begininput");
        String endinput = pdata.getString("endinput");

        String customerid = pdata.getString("customerid");
        String companyname = pdata.getString("companyname");
        String houseid = pdata.getString("houseid");

        String searchcontent = pdata.getString("searchcontent");
        String iteminput = pdata.getString("iteminput");
        Integer status = pdata.getInteger("statusselect");

        String dtype1 = pdata.getString("dtype1");
        Integer cfilter = pdata.getInteger("cfilter");
        Integer hfilter = pdata.getInteger("hfilter");
        Integer countbit = pdata.getInteger("countbit");
        Integer moneybit = pdata.getInteger("moneybit");

        JSONArray rulesrowdata = pdata.getJSONArray("rulesrowdata");
        Integer titleSelect = pdata.getInteger("titleSelect");

        // === 1. 构建 SQL 和参数 ===
        int m = 0;
        StringBuilder sqlBuilder = new StringBuilder();
        List<Object> fparams = new ArrayList<>();

        sqlBuilder.append(" SELECT sp.schedule_pick_id,sp.bill_type,sp.companyid,sp.orderid,sp.operate_time,sp.operate_by,sp.originalbill,sp.iproperty,sp.status, ")
                .append(" sp.remark,sp.printing,sp.outexcel,sp.create_id,sp.create_by,sp.create_time,sp.update_id,sp.update_by,sp.update_time,sp.relation_schedule_id, ")
                .append(" round( sp.count, ").append(countbit).append(" ) AS count, ")
                .append(" round( sp.total, ").append(moneybit).append(" ) AS total, ")
                .append(" c.customercode,c.customername,c.customerphone, ")
                .append(" s.staffcode,s.staffname, ")
                .append(" sh.housecode,sh.housename ");

        sqlBuilder.append(" from schedule_pick sp ")
                .append(" left join customer c on sp.customerid=c.customerid ")
                .append(" left join storehouse sh on sp.houseid=sh.houseid ")
                .append(" left join staffinfo s on sp.operate_by=s.staffid ");

        sqlBuilder.append(" where sp.companyid = ? ");
        fparams.add(companyid);

        if (mainids != null && !mainids.isEmpty()) {
            sqlBuilder.append(" and sp.schedule_pick_id in ( ");
            String[] temp = mainids.split(",");
            for (m = 0; m < temp.length; m++) {
                if (m != 0) {
                    sqlBuilder.append(",");
                }
                sqlBuilder.append(" ? ");
                fparams.add(temp[m]);
            }
            sqlBuilder.append(" ) ");
        }

        if (customerid != null && !customerid.equals("")) {
            sqlBuilder.append(" and sp.customerid = ? ");
            fparams.add(customerid);
        }

        if (houseid != null && !houseid.equals("")) {
            sqlBuilder.append(" and sp.houseid = ? ");
            fparams.add(houseid);
        }

        if (status != null && status != -1) {
            sqlBuilder.append(" and sp.status = ? ");
            fparams.add(status);
        } else if (status != null && status == -1) {
            sqlBuilder.append(" and (sp.status in (0,1,3) ) ");
        }

        //拼接dtype1
        if (dtype1 != null && !dtype1.isEmpty()) {
            sqlBuilder.append(" and ? ");
            fparams.add(dtype1);
        }

        if (searchcontent != null && !searchcontent.equals("")) {
            String fuzzySearch = "%" + searchcontent + "%";
            sqlBuilder.append(" and ( ")
                    .append(" sp.orderid like ? ")
                    .append(" or s.staffname like ? ")
                    .append(" or sp.remark like ? ")
                    .append(" or sp.originalbill like ? ")
                    .append(" or sp.create_by like ? ")
                    .append(" or sp.update_by like ? ")
                    .append(" ) ");
            for (m = 0; m < 6; m++) {
                fparams.add(fuzzySearch);
            }
        }

        if (iteminput != null && !iteminput.equals("")) {
            String fuzzyItemInput = "%" + iteminput + "%";
            sqlBuilder.append(" and exists ( ")
                    .append(" select 1 from schedule_pick_detail spd inner join iteminfo i on spd.itemid = i.itemid where spd.schedule_pick_id=sp.schedule_pick_id ")
                    .append(" and ( ")
                    .append(" spd.batchno like ? ")
                    .append(" or spd.remark like ? ")
                    .append(" or i.codeid like ? ")
                    .append(" or i.itemname like ? ")
                    .append(" or i.barcode like ? ")
                    .append(" or i.sformat like ? ")
                    .append(" or ( i.property1<>'' and i.property1 like ? ) ")
                    .append(" or ( i.property2<>'' and i.property2 like ? ) ")
                    .append(" or ( i.property3<>'' and i.property3 like ? ) ")
                    .append(" or ( i.property4<>'' and i.property4 like ? ) ")
                    .append(" or ( i.property5<>'' and i.property5 like ? ) ")
                    .append(" ) ")
                    .append(" ) ");
            for (m = 0; m < 11; m++) {
                fparams.add(fuzzyItemInput);
            }
        }

        // 时间
        if (datetypeselect != null && !datetypeselect.equals("")) {
            if (begininput != null && !begininput.equals("")) {
                sqlBuilder.append(" and sp." + datetypeselect + ">= ? ");
                fparams.add(begininput);
            }

            if (endinput != null && !endinput.equals("")) {
                sqlBuilder.append(" and sp." + datetypeselect + "<= ? ");
                fparams.add(endinput + " 23:59:59");
            }
        }

        // cfilter
        if (cfilter != null && cfilter > 0 && userid != null) {
            sqlBuilder.append(" and ( c.usercount = 0 or (select 1 from t_customer_userid where customerid=c.customerid and userid = ? limit 1)  ) ");
            fparams.add(userid);
        }

        //hfilter
        if (hfilter != null && hfilter > 0 && userid != null) {
            sqlBuilder.append(" and ( sh.usercount = 0 or (select 1 from t_storehouse_userid where houseid=sh.houseid and userid = ? limit 1)  ) ");
            fparams.add(userid);
        }

        if (rulesrowdata != null && rulesrowdata.size() > 0) {// 数据规则
            String datarule = Basecommon.getDataruleSQl(rulesrowdata, " sp.create_id ", userid);
            if (!datarule.equals("")) {
                sqlBuilder.append(" and " + datarule);
            }
        }

        sqlBuilder.append(" order by ").append(" sp.operate_time DESC ");

        // === 2. 定义列配置 ===
        Map<String, String> columnConfig = new LinkedHashMap<>();
        columnConfig.put("number", "6#序号");
        columnConfig.put("orderid", "15#单号");
        columnConfig.put("status", "8#单据状态");
        columnConfig.put("operate_time", "15#领料时间");
        columnConfig.put("originalbill", "15#原单号");
        columnConfig.put("customername", "15#单位部门");
        columnConfig.put("housename", "15#出库仓库");
        columnConfig.put("count", "15#数量");
        columnConfig.put("total", "15#总额");
        columnConfig.put("staffname", "15#经手人");
        columnConfig.put("remark", "15#备注");
        columnConfig.put("printing", "15#打印次数");
        columnConfig.put("outexcel", "15#导出次数");
        columnConfig.put("create_by", "15#创建人");
        columnConfig.put("create_time", "15#创建时间");
        columnConfig.put("update_by", "15#更新人");
        columnConfig.put("update_time", "15#更新时间");

        List<String> sumFields = new ArrayList<>();
        sumFields.add("count");
        sumFields.add("total");


        // === 3. 定义自定义字段处理器 ===
        Map<String, BiConsumer<Cell, Map<String, Object>>> customSetters = new HashMap<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        customSetters.put("create_time", (cell, row) -> {
            Timestamp timestamp = (Timestamp) row.get("create_time");
            cell.setCellValue(timestamp != null ? timestamp.toLocalDateTime().format(formatter) : "");
        });

        customSetters.put("update_time", (cell, row) -> {
            Timestamp timestamp = (Timestamp) row.get("update_time");
            cell.setCellValue(timestamp != null ? timestamp.toLocalDateTime().format(formatter) : "");
        });

        customSetters.put("operate_time", (cell, row) -> {
            Date date = (Date) row.get("operate_time");
            Timestamp timestamp = date != null ? new Timestamp(date.getTime()) : null;
            cell.setCellValue(timestamp != null ? timestamp.toLocalDateTime().format(dateFormatter) : "");
        });

        customSetters.put("status", (cell, row) -> {
            cell.setCellValue(Pdacommon.getSchedulePickStatus(row.get("status").toString()));
        });

        // 其他字段默认 toString 即可

        // === 4. 分页获取数据并包装为 Iterator ===
        Iterator<Map<String, Object>> dataIterator = new PagedDataIterator(conn, sqlBuilder.toString(), fparams, Common.Exportlimit);

        // === 5. 调用工具类导出 ===
        try {

            ExcelExportUtil.exportToResponse(
                    resp,
                    filname,
                    "排产领料汇总",
                    companyname + "——" + "排产领料单",
                    datastr,
                    columnConfig,
                    dataIterator,
                    customSetters,
                    sumFields
            );

            // === 6. 记录日志/更新次数 ===
            conn.setAutoCommit(false);

            baseUtil.writeLog(conn, companyid, Pdacommon.getDatalogBillChangefunc("schedule_pick", "42"), "导出", "", "导出排产领料单", userid, user);

            StringBuilder updateOutExcelSql = new StringBuilder(" UPDATE schedule_pick SET outexcel = outexcel + 1 ");
            if (mainids != null && !mainids.isEmpty()) {
                updateOutExcelSql.append(" WHERE schedule_pick_id IN ( ");
                String[] temp = mainids.split(",");
                for (m = 0; m < temp.length; m++) {
                    updateOutExcelSql.append(" ?,");
                }
                updateOutExcelSql.deleteCharAt(updateOutExcelSql.length() - 1);
                updateOutExcelSql.append(" ) ");
                PreparedStatement pstmt = conn.prepareStatement(updateOutExcelSql.toString());
                for (m = 0; m < temp.length; m++) {
                    pstmt.setString(m + 1, temp[m]);
                }
                pstmt.executeUpdate();
            } else {
                updateOutExcelSql.append(" WHERE companyid = ? ");
                PreparedStatement pstmt = conn.prepareStatement(updateOutExcelSql.toString());
                pstmt.setString(1, companyid);
                pstmt.executeUpdate();
            }
            conn.commit();

        } catch (Exception e) {
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException ex) {
            }
        } finally {
            if (conn != null && !conn.isClosed()) conn.close();
        }

        return null;
    }


    public static JSONObject deleteSchedulePick(JSONObject params, ActionContext context)
            throws SQLException, NamingException {
        Connection conn = context.getConnection(DATASOURCE);
        JSONObject rt = new JSONObject();
        String message = "";
        int m = 0;

        try {
            String mainid = params.getString("mainid");
            String loginuserid = params.getString("loginuserid");
            String loginUser = params.getString("loginUser");
            String operate_time = params.getString("operate_time");
            String companyid = params.getString("companyid");

            conn.setAutoCommit(false);

            String deleteMainSql = " DELETE FROM schedule_pick WHERE schedule_pick_id = ? AND status = '0' AND companyid = ? ";
            String deleteDetailSql = " DELETE FROM schedule_pick_detail WHERE schedule_pick_id = ? AND status = '0' AND companyid = ? ";

            PreparedStatement deleteMainPs = conn.prepareStatement(deleteMainSql);
            deleteMainPs.setString(1, mainid);
            deleteMainPs.setString(2, companyid);
            m = deleteMainPs.executeUpdate();

            if (m == 0) {
                message = "单据不存在";
                rt.put("message", message);
                return rt;
            }

            PreparedStatement deleteDetailPs = conn.prepareStatement(deleteDetailSql);
            deleteDetailPs.setString(1, mainid);
            deleteDetailPs.setString(2, companyid);
            m = deleteDetailPs.executeUpdate();

            int changebilltype = Pdacommon.getDatalogBillChangefunc("schedule_pick", "42");
            baseUtil.writeLog(conn,
                    companyid,
                    changebilltype,
                    "删除",
                    mainid,
                    message,
                    loginuserid,
                    loginUser);

            conn.commit();

        } catch (Exception e) {
            e.printStackTrace();
            conn.rollback();
            conn.close();
            message = "操作失败！！请联系提供方！！";
        }
        rt.put("message", message);
        return rt;
    }

    /**
     * 以[{itemid : "123",config : {"batchno1" : 100.0,"batchno2" : 21.0}}]的形式返回
     */
    public static JSONObject queryItemBatchConfigData(JSONObject params, ActionContext context)
            throws SQLException, NamingException {
        Connection conn = context.getConnection(DATASOURCE);
        JSONObject rt = new JSONObject();
        String message = "";
        int m = 0;

        try {
            String mainid = params.getString("mainid");
            String houseid = params.getString("houseid");
            List<Object> fparams = new ArrayList<>();
            String itemBatchnoSql = " SELECT spd.itemid,spd.batchno,spd.count FROM schedule_pick_detail spd WHERE spd.schedule_pick_id = ? ";
            fparams.add(mainid);
            Table table = DataUtils.queryData(conn, itemBatchnoSql, fparams, null, null, null);

            JSONArray itemConfigList = new JSONArray();

            // 按itemid分组
            Map<String, List<Row>> itemBatchnoMap = new HashMap<>();

            // 构建查询
            StringBuilder itemStockSql = new StringBuilder();
            StringBuilder unionSql = new StringBuilder();
            fparams.clear();
            m = 0;
            itemStockSql.append(" SELECT s.itemid,s.batchno,s.count,s.checkout_count ")
                    .append(" FROM stock s ")
                    .append(" WHERE houseid = ? ");
            fparams.add(houseid);
            Map<String, Double> itemStockMap = new HashMap<>();

            for (Row row : table.getRows()) {
                String itemid = row.getString("itemid");
                List<Row> itemRowList = itemBatchnoMap.computeIfAbsent(itemid, k -> new ArrayList<>());
                itemRowList.add(row);

                // 以item+batchno为key，存储已有stock数据
                String batchno = row.getString("batchno");
                itemStockMap.put(itemid + "|" + batchno, 0.0);

                // 拼接批量多条件查询
                if (unionSql.length() > 0) {
                    unionSql.append(" UNION ALL ");
                }
                unionSql.append(" SELECT ? AS query_itemid, ? AS query_batchno ");
                fparams.add(itemid);
                fparams.add(batchno);
            }

            itemStockSql.append(" AND EXISTS ( SELECT 1 FROM ( ")
                    .append(unionSql)
                    .append(" ) AS t WHERE t.query_itemid = s.itemid AND t.query_batchno = s.batchno ")
                    .append(" ) ");

            Table itemStockTable = DataUtils.queryData(conn, itemStockSql.toString(), fparams, null, null, null);

            // 商品库存信息
            for (Row row : itemStockTable.getRows()) {
                String itemid = row.getString("itemid");
                String batchno = row.getString("batchno");
                double count = Double.parseDouble(row.getValue("count").toString());
                double checkout_count = Double.parseDouble(row.getValue("checkout_count").toString());
                // 其实用类面向对象更好
                itemStockMap.put(itemid + "|" + batchno, count - checkout_count);
            }

            // 生成itemconfig
            for (String key : itemBatchnoMap.keySet()) {
                JSONObject itemConfig = new JSONObject();
                itemConfig.put("itemid", key);

                List<Row> itemRowList = itemBatchnoMap.getOrDefault(key, new ArrayList<>());
                Map<String, Double> config = new HashMap<>();
                for (Row row : itemRowList) {
                    String batchno = row.getString("batchno");
                    // 订单库存与已有库存取两个中最小值，库存不足则不放入config
                    double count = Math.min(Double.parseDouble(row.getValue("count").toString()), itemStockMap.getOrDefault(key + "|" + batchno, 0.0));
                    if (count <= 0) {
                        continue;
                    }
                    config.put(batchno, count);
                }
                itemConfig.put("config", config);
                itemConfigList.add(itemConfig);
            }

            rt.put("table", itemConfigList);
        } catch (Exception e) {
            e.printStackTrace();
            message = "数据获取失败！！";
        }
        rt.put("message", message);
        return rt;
    }


    // 保存
    /**
     * 传参：
     * 1.maindata : {schedule_pick_id ：
     * 					bill_type : 42,
     * 					companyid :
     * 					operate_time : 单据时间
     * 					operate_by : staff的id
     * 					originalbill : 自定义字符串
     * 					houseid :
     * 					customerid :
     * 					remark : 自定义字符串
     * 					iproperty : 与其他单据类似
     * 					count : 所有商品总数
     * 					orderid : 单号
     * 				}
     * 2.detaildata : 数组，每项对应生成对应明细：{
     * 					detailid : 随机生成的uuid,
     * 					goods_number : 序号,
     * 					itemname : 商品名,
     * 					itemid : 商品id,
     * 					batchno : 批号,
     * 					count : 领料的商品数量,
     * 					price : 单价,
     * 					total : 总价,
     * 					remark : 备注,
     * 					create_id : ,
     * 					create_by : ,
     * 					create_time :
     *                  }
     * 3.checkdata : 数组，每项对应每条item的总领料数，每项如下： {
     * 					itemname : 用于提示,
     * 					itemid : ,
     * 					batchno : 批号,
     * 					pick_count : 领料数量
     *                  }
     * 4.type : 0=暂存，1=正式保存
     * 5.scheduleid :关联的排产单id。用于校验总需领料数，关联对应单据...
     * 6.pricebit,moneybit,loginUser,loginuserid,operate
     */
    public static JSONObject saveSchedulePick(JSONObject params, ActionContext context) throws SQLException, NamingException {
        Connection conn = context.getConnection(DATASOURCE);
        JSONObject rt = new JSONObject();

        Integer type = params.getInteger("type"); // 0=暂存，1=正式保存
        Integer pricebit = params.getInteger("pricebit");
        Integer moneybit = params.getInteger("moneybit");
        String loginUser = params.getString("loginUser");
        String loginuserid = params.getString("loginuserid");

        JSONObject maindata = JSONObject.parseObject(params.getString("maindata"));
        JSONArray detaildata = params.getJSONArray("detaildata");
        JSONArray checkdata = params.getJSONArray("checkdata");

        String operate = params.getString("operate");// draftedit 编辑单据
        String scheduleid = params.getString("scheduleid");
        String state = "0";

        String companyid = maindata.getString("companyid");
        String houseid = maindata.getString("houseid");
        String customerid = maindata.getString("customerid");
        String operate_time = maindata.getString("operate_time");
        double count = maindata.getDoubleValue("count");
        double total = 0;

        int status = type == 0 ? 0 : 1;
        String message = "";
        int i = 0;

        String[] sdatearr = operate_time.split("-");
        int syear = Integer.parseInt(sdatearr[0]);
        int smonth = Integer.parseInt(sdatearr[1]);
        String sdate = sdatearr[0] + "-" + sdatearr[1] + "-01";

        String billdate = sdatearr[0] + sdatearr[1] + sdatearr[2];

        JSONArray Bhouselimit = new JSONArray();
        StringBuffer condition = new StringBuffer();

        Timestamp now = new Timestamp(new Date().getTime());

        try {
            int countbit = 0;
            int t6days = 0;
            int overPick = 0;

            Table companytalbe = DataUtils.queryData(conn, "select pricebit,countbit,moneybit,t6days,repicking from s_company_config where company_id='" + companyid + "'", null, null, null, null);
            if (companytalbe.getRows().size() > 0) {
                countbit = companytalbe.getRows().get(0).getInteger("countbit");
                moneybit = companytalbe.getRows().get(0).getInteger("moneybit");
                t6days = companytalbe.getRows().get(0).getInteger("t6days");
                overPick = companytalbe.getRows().get(0).getInteger("repicking");
            }

            boolean save = true;
            if (t6days > 0) {
                message = Pdacommon.checkSaveDate(companyid, t6days, operate_time, true, conn);
                if (!message.equals("")) {
                    save = false;
                    state = "3";
                }
            }

            Statement ps = conn.createStatement();
            PreparedStatement pst = null;
            List<Object> fparams = new ArrayList<Object>();
            conn.setAutoCommit(false);
            HashMap<String, Row> stockMap = new HashMap<>();
            String orderid = operate.equals("draftedit") ? maindata.getString("orderid") : Pdasave.getOrderidByparams(companyid, "schedule_pick", "42", billdate, conn);
            String store = "update stock set ";

            String details = "";
            if (save && operate.equals("draftedit")) {// 草稿编辑 删除明细数据重新增加
                fparams.clear();
                String fsql = "select status from schedule_pick where schedule_pick_id = ? ";
                fparams.add(maindata.getString("schedule_pick_id"));
                Object statusObject = DataUtils.getValueBySQL(conn, fsql, fparams);
                if (statusObject == null) {
                    save = false;
                    message = "当前记录已删除，操作失败。";
                    state = "2";
                } else {
                    String fstatus = statusObject.toString();
                    if (fstatus.equals("0")) {
                        pst = conn.prepareStatement("delete from schedule_pick_detail where schedule_pick_id = ? ");
                        pst.setString(1, maindata.getString("schedule_pick_id"));
                        pst.executeUpdate();
                    } else if (fstatus.equals("1")) {
                        save = false;
                        message = "当前记录已记帐，操作失败。";
                        state = "2";
                    } else if (fstatus.equals("2")) {
                        save = false;
                        message = "当前记录已作废，操作失败。";
                        state = "2";
                    }
                }
            }

            if (detaildata.size() > 0) {
                if (save) {

                    StringBuilder stockSql = new StringBuilder();
                    StringBuilder unionSql = new StringBuilder();

                    fparams.clear();
                    stockSql.append(" SELECT itemid,batchno,count,money,round(count-checkout_count,2) as scount ")
                            .append(" FROM stock s ")
                            .append(" WHERE s.houseid = ? ");
                    fparams.add(houseid);

                    // 批量查询sql
                    for (i = 0; i < detaildata.size(); i++) {
                        JSONObject result = JSONObject.parseObject(detaildata.getString(i));
                        String batchno = result.getString("batchno");
                        String itemid = result.getString("itemid");
                        double dcount = result.getDoubleValue("count");

                        if (dcount <= 0) {
                            continue;
                        }

                        if (unionSql.length() > 0) {
                            unionSql.append(" UNION ALL ");
                        }
                        unionSql.append(" SELECT ? AS query_itemid, ? AS query_batchno ");
                        fparams.add(itemid);
                        fparams.add(batchno);
                    }

                    stockSql.append(" AND EXISTS ( ")
                            .append(" SELECT 1 FROM ( ").append(unionSql).append(" ) AS t ")
                            .append(" WHERE t.query_itemid = s.itemid AND t.query_batchno = s.batchno ) ");
                    Table stockTable = DataUtils.queryData(conn, stockSql.toString(), fparams, null, null, null);

                    // 转为item + batchno为key的hashmap
                    stockTable.getRows().forEach(row -> {
                        String key = row.getString("itemid") + "|" + row.getString("batchno");
                        stockMap.put(key, row);
                    });

                    String detailSql =
                            "INSERT INTO schedule_pick_detail (" +
                                    "orderid, detailid, schedule_pick_id, originalbill, goods_number, " +
                                    "companyid, operate_by, operate_time, itemid, houseid, " +
                                    "customerid, price, count, total, stype, " +
                                    "remark, status, create_id, create_by, create_time, " +
                                    "update_id, update_by, update_time, batchno" +
                                    ") VALUES (" +
                                    "?, ?, ?, ?, ?, " +
                                    "?, ?, ?, ?, ?, " +
                                    "?, ?, ?, ?, ?, " +
                                    "?, ?, ?, ?, ?, " +
                                    "?, ?, ?, ?" +
                                    ")";
                    PreparedStatement detailPs = conn.prepareStatement(detailSql);

                    String stockUpdateSql =
                            " UPDATE stock SET " +
                                    " count = round(count - ? , ? ), " +
                                    " money = round(money - ? , ? )," +
                                    " newcostprice = round(if(count=0,newcostprice,money/count), ? ) " +
                                    " WHERE companyid = ? AND houseid = ? AND itemid = ? AND batchno = ? ";
                    PreparedStatement stockUpdatePs = conn.prepareStatement(stockUpdateSql);

                    String deleteStockSql = " DELETE FROM stock WHERE itemid = ? AND houseid = ? AND batchno = ? AND count = 0 AND money = 0 ";
                    PreparedStatement deleteStockPs = conn.prepareStatement(deleteStockSql);

                    String itemMonthSql = "INSERT INTO itemmonth (" +
                            "monthid, companyid, itemid, houseid, sdate, " +
                            "syear, smonth, count, money, otherout_count, " +
                            "otherout_money, batchno" +
                            ") VALUES (" +
                            "?, ?, ?, ?, ?, " +
                            "?, ?, ?, ?, ?, " +
                            "?, ?" +
                            ") ON DUPLICATE KEY UPDATE " +
                            "count = ROUND(count - ?, ? ), " +
                            "money = ROUND(money - ?, ? ), " +
                            "otherout_count = ROUND(otherout_count + ?, ? ), " +
                            "otherout_money = ROUND(otherout_money + ?, ? )";
                    PreparedStatement itemMonthPs = conn.prepareStatement(itemMonthSql);

                    for (i = 0; i < detaildata.size(); i++) {
                        JSONObject result = JSONObject.parseObject(detaildata.getString(i));
                        String detailid = result.getString("detailid") == null ? "" : result.getString("detailid");
                        String batchno = result.getString("batchno");
                        String itemid = result.getString("itemid");
                        double pickCount = result.getDoubleValue("count");
                        double costPrice = pickCount == 0 ? 0 : result.getDoubleValue("price");
                        double dtotal = result.getDoubleValue("total");

                        String itemKey = itemid + "|" + batchno;
                        Row stockRow = stockMap.get(itemKey);
                        if (pickCount <= 0) {
                            continue;
                        }
                        if (stockRow == null) {
                            continue;
                        }

                        double stockCount = Double.parseDouble(stockRow.getValue("count").toString());
                        double canPickCount = Double.parseDouble(stockRow.getValue("scount").toString());
                        double stockMoney = Double.parseDouble(stockRow.getValue("money").toString());
                        costPrice = (stockCount == 0 ? 0 : Pdacommon.formatDoubleUp(stockMoney / stockCount, pricebit));
                        dtotal = (stockCount == pickCount ? Pdacommon.formatDoubleUp(stockMoney, moneybit) : Pdacommon.formatDoubleUp(pickCount * costPrice, moneybit));

                        total = Pdacommon.adddouble(total, dtotal);

                        detailPs.setString(1, orderid);
                        detailPs.setString(2, detailid.equals("") ? Common.getUpperUUIDString() : detailid);
                        detailPs.setString(3, maindata.getString("schedule_pick_id"));
                        detailPs.setString(4, maindata.getString("originalbill"));
                        detailPs.setInt(5, result.getInteger("goods_number"));
                        // 用goods_number有相同goods_number的itemconfig重复的问题
//                        detailPs.setInt(5, i + 1);

                        detailPs.setString(6, companyid);
                        detailPs.setString(7, maindata.getString("operate_by"));
                        detailPs.setString(8, maindata.getString("operate_time"));
                        detailPs.setString(9, itemid);
                        detailPs.setString(10, houseid);

                        detailPs.setString(11, customerid);
                        detailPs.setDouble(12, costPrice);
                        detailPs.setDouble(13, pickCount);
                        detailPs.setDouble(14, dtotal);
                        detailPs.setString(15, "42");

                        detailPs.setString(16, result.getString("remark"));
                        detailPs.setInt(17, status);
                        detailPs.setString(18, loginuserid);
                        detailPs.setString(19, loginUser);
                        detailPs.setTimestamp(20, now);

                        detailPs.setString(21, loginuserid);
                        detailPs.setString(22, loginUser);
                        detailPs.setTimestamp(23, now);
                        detailPs.setString(24, batchno);

                        detailPs.addBatch();

                        if (type > 0) {

                            stockUpdatePs.setDouble(1, pickCount);
                            stockUpdatePs.setDouble(2, countbit);
                            stockUpdatePs.setDouble(3, dtotal);
                            stockUpdatePs.setDouble(4, moneybit);
                            stockUpdatePs.setDouble(5, pricebit);
                            stockUpdatePs.setString(6, companyid);
                            stockUpdatePs.setString(7, houseid);
                            stockUpdatePs.setString(8, itemid);
                            stockUpdatePs.setString(9, batchno);
                            stockUpdatePs.addBatch();

                            deleteStockPs.setString(1, itemid);
                            deleteStockPs.setString(2, houseid);
                            deleteStockPs.setString(3, batchno);
                            deleteStockPs.addBatch();

                            itemMonthPs.setString(1, Common.getUpperUUIDString());
                            itemMonthPs.setString(2, companyid);
                            itemMonthPs.setString(3, itemid);
                            itemMonthPs.setString(4, houseid);
                            itemMonthPs.setString(5, sdate);
                            itemMonthPs.setInt(6, syear);
                            itemMonthPs.setInt(7, smonth);
                            itemMonthPs.setDouble(8, pickCount);
                            itemMonthPs.setDouble(9, dtotal);
                            itemMonthPs.setDouble(10, pickCount);
                            itemMonthPs.setDouble(11, dtotal);
                            itemMonthPs.setString(12, batchno);

                            itemMonthPs.setDouble(13, pickCount);
                            itemMonthPs.setDouble(14, countbit);
                            itemMonthPs.setDouble(15, dtotal);
                            itemMonthPs.setDouble(16, moneybit);
                            itemMonthPs.setDouble(17, pickCount);
                            itemMonthPs.setDouble(18, countbit);
                            itemMonthPs.setDouble(19, dtotal);
                            itemMonthPs.setDouble(20, moneybit);
                            itemMonthPs.addBatch();

                            String temp = "('" + itemid + "','" + houseid + "')";
                            if (condition.toString().equals("")) {
                                condition.append(temp);
                            } else {
                                if (condition.toString().indexOf(temp) == -1) {
                                    condition.append("," + temp);
                                }
                            }
                        }
                    }
                    stockUpdatePs.executeBatch();
                    deleteStockPs.executeBatch();
                    detailPs.executeBatch();
                    itemMonthPs.executeBatch();

                }

                StringBuilder checkSql = new StringBuilder();
                fparams.clear();
                checkSql.append(" SELECT round( sum( pwt.needcount - pwt.count ) , ? ) AS can_pick_count,i.itemname,i.itemid ")
                        .append(" FROM prodrequisition_work_total pwt ")
                        .append(" LEFT JOIN iteminfo i ON pwt.itemid = i.itemid ")
                        .append(" WHERE pwt.scheduleid = ? ")
                        .append(" AND pwt.itemid IN ( ");
                fparams.add(countbit);
                fparams.add(scheduleid);

                Map<String, Double> checkDataMap = new HashMap<String, Double>();
                for (i = 0; i < checkdata.size(); i++) {
                    JSONObject checkResult = JSONObject.parseObject(checkdata.getString(i));
                    double pick_count = checkResult.getDoubleValue("pick_count");
                    // 按itemid聚合
                    checkDataMap.compute(checkResult.getString("itemid"), (k, v) -> v == null ? pick_count : v + pick_count);

                    // 拼接itemid
                    checkSql.append(" ? ");
                    fparams.add(checkResult.getString("itemid"));
                    if (i < checkdata.size() - 1) {
                        checkSql.append(" , ");
                    }
                }
                checkSql.append(" ) ")
                        .append(" GROUP BY pwt.itemid ");
                Table checktable = DataUtils.queryData(conn, checkSql.toString(), fparams, null, null, null);

                // 转为item为key的hashmap
                // 其实没必要
                HashMap<String, Row> checkMap = new HashMap<>();
                checktable.getRows().forEach(row -> {
                    String key = row.getString("itemid");
                    checkMap.put(key, row);
                });

                // 检查库存
                for(String itemid : checkMap.keySet()){
                    Double can_pick_count = Double.parseDouble(checkMap.get(itemid).getValue("can_pick_count").toString());
                    if (can_pick_count < checkDataMap.get(itemid)) {
                        message += " ( " + checkMap.get(itemid).getString("itemname") + " 最大可领料数量超出 <" + can_pick_count + "> )";
                        save = false;
                        continue;
                    }
                }
//                for (i = 0; i < checkdata.size(); i++) {
//                    JSONObject checkresult = JSONObject.parseObject(checkdata.getString(i));
//                    double pick_count = checkresult.getDoubleValue("pick_count");
//                    String key = checkresult.getString("itemid") + "|" + checkresult.getString("batchno");
//                    Row checkrow = checkMap.get(key);
//
//                    if (checkrow == null || checkrow.getValue("can_pick_count") == null) {
//                        message += " ( " + checkresult.getString("codeid") + "、" + checkresult.getString("itemname") + "、" + checkresult.getString("housename") + " " + checkresult.getString("batchno") + " 未查询到库存 )";
//                        save = false;
//                        continue;
//                    }
//
//                    double can_pick_count = Double.parseDouble(checkrow.getValue("can_pick_count").toString());
//
//                    if (pick_count > can_pick_count) {
//                        message += "(" + checkresult.getString("codeid") + "、" + checkresult.getString("itemname") + "、" + checkresult.getString("housename") + " " + checkresult.getString("batchno") + "《总出库数量》："
//                                + checkresult.getDoubleValue("totalcount") + " 大于 《最大可出库的库存数量》：" + can_pick_count + ")";
//                        save = false;
//                        continue;
//                    }
//                }

            } else {
                save = false;
                message = message + "没有商品明细数据，保存失败";
            }

            if (save) {
                int changebilltype = Pdacommon.getDatalogBillChangefunc("schedule_pick", "42");
                if (operate.equals("draftedit")) {// 草稿编辑 更新

                    String updateSql = " UPDATE schedule_pick SET orderid = ?,originalbill = ?,operate_time = ?,operate_by = ?,houseid = ?," +
                            " customerid = ?, count = ?, total = ?, remark = ?,status = ?," +
                            " update_id = ?,update_by = ? ,update_time = ?, iproperty = ?,relation_schedule_id = ? " +
                            " WHERE schedule_pick_id = ? ";
                    PreparedStatement updatePs = conn.prepareStatement(updateSql);
                    updatePs.setString(1, orderid);
                    updatePs.setString(2, maindata.getString("originalbill"));
                    updatePs.setString(3, operate_time);
                    updatePs.setString(4, maindata.getString("operate_by"));
                    updatePs.setString(5, houseid);
                    updatePs.setString(6, customerid);
                    updatePs.setDouble(7, count);
                    updatePs.setDouble(8, total);
                    updatePs.setString(9, maindata.getString("remark"));
                    updatePs.setInt(10, status);
                    updatePs.setString(11, loginuserid);
                    updatePs.setString(12, loginUser);
                    updatePs.setTimestamp(13, now);
                    updatePs.setString(14, maindata.getString("iproperty"));
                    updatePs.setString(15, scheduleid);
                    updatePs.setString(16, maindata.getString("schedule_pick_id"));
                    updatePs.executeUpdate();

                    ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
                            + companyid + "'," + changebilltype + ",'修改','" + maindata.getString("schedule_pick_id") + "','单据编号：" + orderid + "','" + loginuserid + "','" + loginUser + "',now())");

                    String updateDetailSql = "update schedule_pick_detail d,schedule_pick s set d.create_id=s.create_id,d.create_by=s.create_by,d.create_time=s.create_time where d.schedule_pick_id = s.schedule_pick_id and s.schedule_pick_id= ? ";
                    PreparedStatement updateDetailPs = conn.prepareStatement(updateDetailSql);
                    updateDetailPs.setString(1, maindata.getString("schedule_pick_id"));
                    updateDetailPs.executeUpdate();

                } else {

                    String insertSql = "insert into schedule_pick (orderid, schedule_pick_id, bill_type, originalbill, companyid, " +
                            "operate_time, operate_by, houseid, customerid, count, " +
                            "total, remark, status, printing, outexcel, " +
                            "create_id, create_by, create_time, update_id, update_by, " +
                            "update_time, iproperty,relation_schedule_id) " +
                            "VALUES (?, ?, ?, ?, ?, " +
                            "?, ?, ?, ?, ?, " +
                            "?, ?, ?, ?, ?, " +
                            "?, ?, ?, ?, ?, " +
                            "?, ?, ?)";
                    PreparedStatement insertPs = conn.prepareStatement(insertSql);
                    insertPs.setString(1, orderid);
                    insertPs.setString(2, maindata.getString("schedule_pick_id"));
                    insertPs.setString(3, "42");
                    insertPs.setString(4, maindata.getString("originalbill"));
                    insertPs.setString(5, companyid);
                    insertPs.setString(6, operate_time);
                    insertPs.setString(7, maindata.getString("operate_by"));
                    insertPs.setString(8, houseid);
                    insertPs.setString(9, customerid);
                    insertPs.setDouble(10, count);
                    insertPs.setDouble(11, total);
                    insertPs.setString(12, maindata.getString("remark"));
                    insertPs.setInt(13, status);
                    insertPs.setInt(14, 0);
                    insertPs.setInt(15, 0);
                    insertPs.setString(16, loginuserid);
                    insertPs.setString(17, loginUser);
                    insertPs.setTimestamp(18, now);
                    insertPs.setString(19, loginuserid);
                    insertPs.setString(20, loginUser);
                    insertPs.setTimestamp(21, now);
                    insertPs.setString(22, maindata.getString("iproperty"));
                    insertPs.setString(23, scheduleid);
                    insertPs.executeUpdate();

                    ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
                            + companyid + "'," + changebilltype + ",'" + (status == 0 ? "新增暂存" : "新增保存") + "','" + maindata.getString("schedule_pick_id") + "','单据编号：" + orderid + "','" + loginuserid
                            + "','" + loginUser + "',now())");

                }

                // 记账生产领料单，库存相关
                if (status == 1) {
                    String requestOrderId = Pdasave.getOrderidByparams(companyid, "prodrequisition", "10", billdate, conn);
                    String prodrequisitionId = Common.getUpperUUIDString();
                    String requestSql = " INSERT INTO prodrequisition (prodrequisitionid,bill_type,companyid,orderid,operate_time," +
                            " operate_by,houseid,customerid,count,total," +
                            " status,create_id,create_by,create_time,update_id," +
                            " update_by,update_time,originalbill,iproperty,schedule_pick_id," +
                            " schedule_pick_billno) " +
                            " VALUES " +
                            "(?,?,?,?,?," +
                            " ?,?,?,?,?," +
                            " ?,?,?,?,?," +
                            " ?,?,?,?,?," +
                            " ?)";
                    PreparedStatement requestPs = conn.prepareStatement(requestSql);
                    requestPs.setString(1, prodrequisitionId);
                    requestPs.setString(2, "10");
                    requestPs.setString(3, companyid);
                    requestPs.setString(4, requestOrderId);
                    requestPs.setString(5, operate_time);
                    requestPs.setString(6, maindata.getString("operate_by"));
                    requestPs.setString(7, "");
                    requestPs.setString(8, customerid);
                    requestPs.setDouble(9, count);
                    requestPs.setDouble(10, total);
                    requestPs.setString(11, "1");
                    requestPs.setString(12, loginuserid);
                    requestPs.setString(13, loginUser);
                    requestPs.setTimestamp(14, now);
                    requestPs.setString(15, loginuserid);
                    requestPs.setString(16, loginUser);
                    requestPs.setTimestamp(17, now);
                    requestPs.setString(18, "");
                    requestPs.setString(19, maindata.getString("iproperty"));
                    requestPs.setString(20, maindata.getString("schedule_pick_id"));
                    requestPs.setString(21, orderid);
                    requestPs.executeUpdate();

//                    String requestDetailSql = " INSERT INTO prodrequisitiondetail (detailid,prodrequisitionid,goods_number,companyid,operate_time," +
//                            " operate_by,orderid,itemid,houseid,customerid," +
//                            " stype,count,price,total,status," +
//                            " create_id,create_by,create_time,update_id,update_by," +
//                            " update_time,batchno) " +
//                            " VALUES " +
//                            " (?,?,?,?,?," +
//                            " ?,?,?,?,?," +
//                            " ?,?,?,?,?," +
//                            " ?,?,?,?,?," +
//                            " ?,?) ";
//                    PreparedStatement requestDetailPs = conn.prepareStatement(requestDetailSql);
//
//                    for (i = 0; i < detaildata.size(); i++) {
//                        JSONObject result = JSONObject.parseObject(detaildata.getString(i));
//                        String batchno = result.getString("batchno");
//                        String itemid = result.getString("itemid");
//                        double pickCount = result.getDoubleValue("count");
//                        double costPrice = pickCount == 0 ? 0 : result.getDoubleValue("price");
//                        double dtotal = result.getDoubleValue("total");
//
//                        String itemKey = itemid + "|" + batchno;
//                        Row stockRow = stockMap.get(itemKey);
//                        if (pickCount <= 0) {
//                            continue;
//                        }
//                        if (stockRow == null) {
//                            continue;
//                        }
//
//                        requestDetailPs.setString(1, Common.getUpperUUIDString());
//                        requestDetailPs.setString(2, prodrequisitionId);
//                        requestDetailPs.setInt(3, i + 1);
//                        requestDetailPs.setString(4, companyid);
//                        requestDetailPs.setString(5, operate_time);
//                        requestDetailPs.setString(6, maindata.getString("operate_by"));
//                        requestDetailPs.setString(7, orderid);
//                        requestDetailPs.setString(8, itemid);
//                        requestDetailPs.setString(9, houseid);
//                        requestDetailPs.setString(10, customerid);
//                        requestDetailPs.setString(11, "101");
//                        requestDetailPs.setDouble(12, pickCount);
//                        requestDetailPs.setDouble(13, costPrice);
//                        requestDetailPs.setDouble(14, dtotal);
//                        requestDetailPs.setString(15, "1");
//                        requestDetailPs.setString(16, loginuserid);
//                        requestDetailPs.setString(17, loginUser);
//                        requestDetailPs.setTimestamp(18, now);
//                        requestDetailPs.setString(19, loginuserid);
//                        requestDetailPs.setString(20, loginUser);
//                        requestDetailPs.setTimestamp(21, now);
//                        requestDetailPs.setString(22, batchno);
//                        requestDetailPs.addBatch();
//
//                    }
//                    requestDetailPs.executeBatch();

                    // 分配给排产单
                    // 按itemid分类
//                    Map<String, Double> itemCountMap = new HashMap<>();
                    Map<String, List<itemStock>> itemCountMap = new HashMap<>();// 前端传入的总领料数
                    Map<String, List<Row>> itemRowMap = new HashMap<>();
                    for (i = 0; i < detaildata.size(); i++) {
                        JSONObject result = JSONObject.parseObject(detaildata.getString(i));
                        itemStock itemStock = new itemStock(result);
                        itemCountMap.computeIfAbsent(itemStock.getItemid(), k -> new ArrayList<>()).add(itemStock);
//                        String itemid = result.getString("itemid");
//                        double pickCount = result.getDoubleValue("count");
//                        itemCountMap.put(itemid, itemCountMap.get(itemid) == null ? pickCount : itemCountMap.get(itemid) + pickCount);
                    }

                    // 一次查询出所有所需item数量并分类
                    fparams.clear();
                    i = 0;
                    StringBuilder itemTotalSql = new StringBuilder();
                    itemTotalSql.append(" SELECT itemid,count,needcount,totalid,worksheetid,worksheetbillno FROM prodrequisition_work_total WHERE scheduleid = ? AND needcount - count > 0 AND itemid in ( ");
                    fparams.add(scheduleid);
                    for (String itemid : itemCountMap.keySet()) {
                        if (i > 0) {
                            itemTotalSql.append(",");
                        }
                        itemTotalSql.append("?");
                        fparams.add(itemid);
                        i++;
                    }
                    itemTotalSql.append(" ) ").append(" ORDER BY needcount ASC ");// 从小到大填充
                    Table itemTable = DataUtils.queryData(conn, itemTotalSql.toString(), fparams, null, null, null);
                    // 按itemid分类
                    for (Row row : itemTable.getRows()) {
                        String itemid = row.getString("itemid");
                        itemRowMap.computeIfAbsent(itemid, k -> new ArrayList<>()).add(row);
                    }

                    // 按item分配
                    int rowIndex = 0;
                    // 领料单序号
                    int requestIndex = 1;
                    double leftCount = 0;// row>itemStock，跳出循环后的剩余数量
                    // 领料的row
                    PreparedStatement itemRequestPs = conn.prepareStatement(" UPDATE prodrequisition_work_total SET count = ? WHERE totalid = ?  ");
                    // 领料单生成
                    String requestDetailSql = " INSERT INTO prodrequisitiondetail (detailid,prodrequisitionid,goods_number,companyid,operate_time," +
                            " operate_by,orderid,itemid,houseid,customerid," +
                            " stype,count,price,total,status," +
                            " create_id,create_by,create_time,update_id,update_by," +
                            " update_time,batchno,worksheetid,worksheetbillno,schedule_pick_billno) " +
                            " VALUES " +
                            " (?,?,?,?,?," +
                            " ?,?,?,?,?," +
                            " ?,?,?,?,?," +
                            " ?,?,?,?,?," +
                            " ?,?,?,?,?) ";
                    PreparedStatement requestDetailPs = conn.prepareStatement(requestDetailSql);
                    double pickCount = 0;
                    for (String itemid : itemCountMap.keySet()) {
                        rowIndex = 0;
                        double remaining = 0; // 重置为0，表示当前物料ID无上一条行剩余
                        List<Row> itemRows = itemRowMap.get(itemid);
                        List<itemStock> itemStocks = itemCountMap.get(itemid);

                        // 确保itemRows非空
                        if (itemRows == null || itemRows.isEmpty()) {
                            continue;
                        }


                        for (itemStock itemStock : itemStocks) {
                            pickCount = itemStock.getCount();
                            if (pickCount <= 0) {
                                continue;
                            }

                            // 按行分配当前物料的领料量
                            while (rowIndex < itemRows.size() && pickCount > 0) {
                                Row itemRow = itemRows.get(rowIndex);
                                // 计算当前行实际可领量（避免重复计算）
                                double currentRowCanPickCount = Double.parseDouble(itemRow.getValue("needcount").toString())
                                        - Double.parseDouble(itemRow.getValue("count").toString());

                                // 计算本次实际可领量（考虑上一行剩余）
                                double rowCanPickCount = remaining > 0
                                        ? Math.min(remaining, currentRowCanPickCount)
                                        : currentRowCanPickCount;

                                // 当前行已无剩余，跳过
                                if (rowCanPickCount <= 0) {
                                    rowIndex++;
                                    continue;
                                }

                                // 计算本次实际领料量（不超过当前行剩余和当前物料需求）
                                double toPick = Math.min(pickCount, rowCanPickCount);

                                // 更新行数据
                                itemRequestPs.setDouble(1, Double.parseDouble(itemRow.getValue("count").toString()) + toPick);
                                itemRequestPs.setString(2, itemRow.getString("totalid"));
                                itemRequestPs.addBatch();

                                // 生成领料单
                                requestDetailPs.setString(1, Common.getUpperUUIDString());
                                requestDetailPs.setString(2, prodrequisitionId);
                                requestDetailPs.setInt(3, requestIndex);
                                requestDetailPs.setString(4, companyid);
                                requestDetailPs.setString(5, operate_time);
                                requestDetailPs.setString(6, maindata.getString("operate_by"));
                                requestDetailPs.setString(7, requestOrderId);
                                requestDetailPs.setString(8, itemid);
                                requestDetailPs.setString(9, houseid);
                                requestDetailPs.setString(10, customerid);
                                requestDetailPs.setString(11, "101");
                                requestDetailPs.setDouble(12, toPick); // 使用toPick,本次实际领料量
                                requestDetailPs.setDouble(13, itemStock.getPrice());
                                requestDetailPs.setDouble(14, toPick * itemStock.getPrice());
                                requestDetailPs.setString(15, "1");
                                requestDetailPs.setString(16, loginuserid);
                                requestDetailPs.setString(17, loginUser);
                                requestDetailPs.setTimestamp(18, now);
                                requestDetailPs.setString(19, loginuserid);
                                requestDetailPs.setString(20, loginUser);
                                requestDetailPs.setTimestamp(21, now);
                                requestDetailPs.setString(22, itemStock.getBatchno());
                                requestDetailPs.setString(23, itemRow.getString("worksheetid"));
                                requestDetailPs.setString(24, itemRow.getString("worksheetbillno"));
                                requestDetailPs.setString(25, orderid);
                                requestDetailPs.addBatch();
                                requestIndex++;

                                // 更新剩余量
                                pickCount -= toPick;
                                remaining = currentRowCanPickCount - toPick; // 计算当前行剩余

                                // 当前行已领完，移动到下一行
                                if (remaining <= 0) {
                                    rowIndex++;
                                    remaining = 0; // 重置为0（避免影响下一条itemStock）
                                }
                            }
                        }
                        if (pickCount > 0) {
                            message = "保存失败,排产领料单不支持超领！";
                            state = "2";
                        }
                    }
                    requestDetailPs.executeBatch();
                    itemRequestPs.executeBatch();

                }

                if (state.equals("0")){
                    ps.executeBatch();
                    conn.commit();
                    conn.setAutoCommit(true);
                    state = "1";
                    if (type > 0) {
                        // 库存报警
                        Bhouselimit = Common.gethouselimitdata(conn, companyid, condition, Bhouselimit);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (Exception e1) {
                // e1.printStackTrace();
            }
            message = "保存失败," + e.getMessage().toString();
        } finally {
            condition.setLength(0);
            conn.close();
        }
        rt.put("state", state);
        rt.put("message", message);
        rt.put("warning", Bhouselimit);
        return rt;
    }

    public static JSONObject getItemStockById(JSONObject params, ActionContext context) throws SQLException, NamingException {
        Connection conn = context.getConnection(DATASOURCE);
        JSONObject rt = new JSONObject();
        String message = "";
        int m = 0;

        try {
            List<Object> fparams = new ArrayList<>();

            String itemid = params.getString("itemid");
            String houseid = params.getString("houseid");

            String sql = " SELECT if(batchno = '','无批号',batchno) as batchno,count from stock where itemid = ? and houseid = ? ";
            fparams.add(itemid);
            fparams.add(houseid);

            Table table = DataUtils.queryData(conn, sql, fparams, null, null, null);

            rt.put("table", Transform.tableToJson(table));

        } catch (Exception e) {
            e.printStackTrace();
            message = "数据获取失败！！";
        }
        rt.put("message", message);

        return rt;
    }

    public static JSONObject queryScheduleData(JSONObject params, ActionContext context) throws SQLException, NamingException {
        Connection conn = context.getConnection(DATASOURCE);
        JSONObject rt = new JSONObject();
        String message = "";
        int m = 0;

        try {
            List<Object> fparams = new ArrayList<>();

            String companyid = params.getString("companyid");
            String status = params.getString("status");
            String schedulestatus = params.getString("schedulestatus");
            String searchcontent = params.getString("searchcontent");

            Integer offset = params.getInteger("offset");
            Integer limit = params.getInteger("limit");

            StringBuilder sqlBuilder = new StringBuilder();
            StringBuilder countSqlBuilder = new StringBuilder();
            StringBuilder sql = new StringBuilder();

            sqlBuilder.append(" SELECT so.*,")
                    .append(" ifnull(c.customername,'') as customername, ")
                    .append(" s.staffcode,s.staffname ");

            countSqlBuilder.append(" select count(*) ");

            sql.append(" from scheduleorder so ")
                    .append(" left join customer c on so.customerid=c.customerid ")
                    .append(" left join staffinfo s on so.operate_by=s.staffid ");

            sql.append(" where so.companyid = ? ");
            fparams.add(companyid);

            if (status != null && !status.equals("")) {
                sql.append(" and so.status = ? ");
                fparams.add(status);
            }

            if (schedulestatus != null && !schedulestatus.equals("")) {
                sql.append(" and so.schedulestatus = ? ");
                fparams.add(schedulestatus);
            }

            if (searchcontent != null && !searchcontent.equals("")) {
                String fuzzySearch = "%" + searchcontent + "%";
                sql.append(" and ( ")
                        .append(" so.orderid like ? ")
                        .append(" ) ");
                for (int i = 0; i < 1; i++) {
                    fparams.add(fuzzySearch);
                }
            }

            sql.append(" order by so.orderid desc ");

            sqlBuilder.append(sql);
            countSqlBuilder.append(sql);

            Table table = DataUtils.queryData(conn, sqlBuilder.toString(), fparams, null, offset, limit);

            if (offset != null && offset.equals(0)) {
                Object total = DataUtils.getValueBySQL(conn, countSqlBuilder.toString(), fparams);
                table.setTotal(Integer.parseInt(total.toString()));
            }
            rt.put("table", Transform.tableToJson(table));

        } catch (Exception e) {
            e.printStackTrace();
            message = "数据获取失败！！";
        }
        rt.put("message", message);

        return rt;
    }

    public static JSONObject querySchedulePickData(JSONObject params, ActionContext context) throws SQLException, NamingException {
        Connection conn = context.getConnection(DATASOURCE);
        JSONObject rt = new JSONObject();
        String message = "";
        int m = 0;

        try {
            List<Object> fparams = new ArrayList<>();
            Table table = null;

            String mainid = params.getString("mainid");
            if (mainid != null && !mainid.equals("")) {
                String sql = "SELECT sp.*, "
                        + " c.customername,c.customercode, "
                        + " s.staffname,s.staffcode, "
                        + " sh.housename,sh.housecode "
                        + " from schedule_pick sp "
                        + " left join customer c on sp.customerid=c.customerid "
                        + " left join storehouse sh on sp.houseid=sh.houseid "
                        + " left join staffinfo s on sp.operate_by=s.staffid "
                        + " where schedule_pick_id = ? ";
                fparams.add(mainid);
                table = DataUtils.queryData(conn, sql, fparams, null, null, null);
                rt.put("table", Transform.tableToJson(table));
            } else {
                String companyid = params.getString("companyid");
                String userid = params.getString("userid");

                String datetypeselect = params.getString("datetypeselect");
                String begininput = params.getString("begininput");
                String endinput = params.getString("endinput");

                String customerid = params.getString("customerid");
                String houseid = params.getString("houseid");

                String searchcontent = params.getString("searchcontent");
                String iteminput = params.getString("iteminput");

                Integer status = params.getInteger("statusselect");
                String dtype1 = params.getString("dtype1");

                Integer cfilter = params.getInteger("cfilter");
                Integer hfilter = params.getInteger("hfilter");
                Integer countbit = params.getInteger("countbit");
                Integer moneybit = params.getInteger("moneybit");

                Integer offset = params.getInteger("offset");
                Integer limit = params.getInteger("limit");
                String orderBys = params.getString("orderBys");

                JSONArray rulesrowdata = params.getJSONArray("rulesrowdata");

                if (companyid.length() != 32 && userid.length() != 32) {
                    message = "含非法数据，获取失败！！";
                } else {
                    StringBuilder sqlBuilder = new StringBuilder();
                    StringBuilder countSqlBuilder = new StringBuilder();
                    StringBuilder sql = new StringBuilder();

                    sqlBuilder.append(" SELECT sp.schedule_pick_id,sp.bill_type,sp.companyid,sp.orderid,sp.operate_time,sp.operate_by,sp.originalbill,sp.iproperty,sp.status, ")
                            .append(" sp.remark,sp.printing,sp.outexcel,sp.create_id,sp.create_by,sp.create_time,sp.update_id,sp.update_by,sp.update_time,sp.relation_schedule_id, ")
                            .append(" round( count, ").append(countbit).append(" ) AS count, ")
                            .append(" round( total, ").append(moneybit).append(" ) AS total, ")
                            .append(" c.customercode,c.customername,c.customerphone, ")
                            .append(" s.staffcode,s.staffname, ")
                            .append(" sh.housecode,sh.housename ");
                    countSqlBuilder.append(" select count(*) ");

                    sql.append(" from schedule_pick sp ")
                            .append(" left join customer c on sp.customerid=c.customerid ")
                            .append(" left join storehouse sh on sp.houseid=sh.houseid ")
                            .append(" left join staffinfo s on sp.operate_by=s.staffid ");

                    if (orderBys == null || orderBys.equals("")) {
                        orderBys = " sp.update_time desc ";
                    } else if (orderBys.contains("customername")) {
                        orderBys = " c." + orderBys;
                    } else {
                        orderBys = " sp." + orderBys;
                    }

                    sql.append(" where sp.companyid = ? ");
                    fparams.add(companyid);

                    if (customerid != null && !customerid.equals("")) {
                        sql.append(" and sp.customerid = ? ");
                        fparams.add(customerid);
                    }

                    if (houseid != null && !houseid.equals("")) {
                        sql.append(" and sp.houseid = ? ");
                        fparams.add(houseid);
                    }

                    if (status != null && status != -1) {
                        sql.append(" and sp.status = ? ");
                        fparams.add(status);
                    } else if (status != null && status == -1) {
                        sql.append(" and (sp.status in (0,1,3) ) ");
                    }

                    //拼接dtype1
                    if (dtype1 != null && !dtype1.isEmpty()) {
                        sql.append(" and ? ");
                        fparams.add(dtype1);
                    }

                    if (searchcontent != null && !searchcontent.equals("")) {
                        String fuzzySearch = "%" + searchcontent + "%";
                        sql.append(" and ( ")
                                .append(" sp.orderid like ? ")
                                .append(" or s.staffname like ? ")
                                .append(" or sp.remark like ? ")
                                .append(" or sp.originalbill like ? ")
                                .append(" or sp.create_by like ? ")
                                .append(" or sp.update_by like ? ")
                                .append(" ) ");
                        for (m = 0; m < 6; m++) {
                            fparams.add(fuzzySearch);
                        }
                    }

                    if (iteminput != null && !iteminput.equals("")) {
                        String fuzzyItemInput = "%" + iteminput + "%";
                        sql.append(" and exists ( ")
                                .append(" select 1 from schedule_pick_detail spd inner join iteminfo i on spd.itemid = i.itemid where spd.schedule_pick_id=sp.schedule_pick_id ")
                                .append(" and ( ")
                                .append(" spd.batchno like ? ")
                                .append(" or spd.remark like ? ")
                                .append(" or i.codeid like ? ")
                                .append(" or i.itemname like ? ")
                                .append(" or i.barcode like ? ")
                                .append(" or i.sformat like ? ")
                                .append(" or ( i.property1<>'' and i.property1 like ? ) ")
                                .append(" or ( i.property2<>'' and i.property2 like ? ) ")
                                .append(" or ( i.property3<>'' and i.property3 like ? ) ")
                                .append(" or ( i.property4<>'' and i.property4 like ? ) ")
                                .append(" or ( i.property5<>'' and i.property5 like ? ) ")
                                .append(" ) ")
                                .append(" ) ");
                        for (m = 0; m < 11; m++) {
                            fparams.add(fuzzyItemInput);
                        }
                    }

                    // 时间
                    if (datetypeselect != null && !datetypeselect.equals("")) {
                        if (begininput != null && !begininput.equals("")) {
                            sql.append(" and sp." + datetypeselect + ">= ? ");
                            fparams.add(begininput);
                        }

                        if (endinput != null && !endinput.equals("")) {
                            sql.append(" and sp." + datetypeselect + "<= ? ");
                            fparams.add(endinput + " 23:59:59");
                        }
                    }

                    // cfilter
                    if (cfilter != null && cfilter > 0 && userid != null) {
                        sql.append(" and ( c.usercount = 0 or (select 1 from t_customer_userid where customerid=c.customerid and userid = ? limit 1)  ) ");
                        fparams.add(userid);
                    }

                    //hfilter
                    if (hfilter != null && hfilter > 0 && userid != null) {
                        sql.append(" and ( sh.usercount = 0 or (select 1 from t_storehouse_userid where houseid=sh.houseid and userid = ? limit 1)  ) ");
                        fparams.add(userid);
                    }

                    if (rulesrowdata != null && rulesrowdata.size() > 0) {// 数据规则
                        String datarule = Basecommon.getDataruleSQl(rulesrowdata, " sp.create_id ", userid);
                        if (!datarule.equals("")) {
                            sql.append(" and " + datarule);
                        }
                    }

                    sql.append(" order by ").append(orderBys);

                    sqlBuilder.append(sql);
                    countSqlBuilder.append(sql);
                    String presql = sqlBuilder.toString();
                    String countSql = countSqlBuilder.toString();

                    table = DataUtils.queryData(conn, presql, fparams, null, offset, limit);
                    if (offset != null && offset.equals(0)) {
                        Object total = DataUtils.getValueBySQL(conn, countSql, fparams);
                        table.setTotal(Integer.parseInt(total.toString()));
                    }
                    rt.put("table", Transform.tableToJson(table));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            message = "数据获取失败！！";
        }
        rt.put("message", message);

        return rt;
    }

    public static JSONObject querySchedulePickDetailData(JSONObject params, ActionContext context)
            throws SQLException, NamingException {
        Connection conn = context.getConnection(DATASOURCE);
        JSONObject rt = new JSONObject();
        String message = "";
        int m = 0;

        try {
            String mainid = params.getString("mainid");
            Integer countbit = params.getInteger("countbit") == null ? 2 : params.getInteger("countbit");
            Integer moneybit = params.getInteger("moneybit") == null ? 2 : params.getInteger("moneybit");
            Boolean getSchedule = params.getBoolean("getSchedule");
            Integer offset = params.getInteger("offset");
            Integer limit = params.getInteger("limit");
            String orderBys = params.getString("orderBys");

            //只用mainid查询
            if (mainid != null && !mainid.isEmpty()) {
                List<Object> fparams = new ArrayList<>();
                StringBuilder sqlBuilder = new StringBuilder();
                StringBuilder countSqlBuilder = new StringBuilder();
                StringBuilder sql = new StringBuilder();

                if (orderBys == null || orderBys.isEmpty()) {
                    orderBys = "spd.goods_number asc";
                } else if (orderBys.contains("classname")) {
                    orderBys = "ic." + orderBys;
                } else if (orderBys.contains("codeid") || orderBys.contains("barcode") || orderBys.contains("itemname") || orderBys.contains("sformat") || orderBys.contains("property") || orderBys.contains("unit")) {
                    orderBys = "i." + orderBys;
                } else {
                    orderBys = "spd." + orderBys;
                }

                sqlBuilder.append(" SELECT i.codeid,i.itemname,i.sformat,i.mcode,i.classid,i.unit,i.imgurl,i.barcode,i.property1,i.property2,i.property3,i.property4,i.property5,i.unitstate1,i.unitset1,i.unitstate2,i.unitset2,i.unitstate3,i.unitset3, ")
                        .append(" ifnull(ic.classname,'') as classname, ")
                        .append(" spd.detailid,spd.schedule_pick_id,spd.goods_number,spd.companyid,spd.operate_time,spd.operate_by,spd.orderid,spd.itemid,spd.houseid,spd.customerid,spd.stype, ")
                        .append(" spd.status,spd.remark,spd.create_id,spd.create_by,spd.create_time,spd.update_id,spd.update_by,spd.update_time,spd.originalbill,spd.batchno, ")
                        .append(" round(spd.count, ").append(countbit).append(") as count, ")
                        .append(" round(spd.price, ").append(moneybit).append(") as price, ")
                        .append(" round(spd.total, ").append(moneybit).append(") as total, ")
                        .append(" sp.relation_schedule_id as relation_schedule_id ");

                countSqlBuilder.append(" SELECT count(*) ");

                sql.append(" FROM schedule_pick_detail spd ")
                        .append(" INNER JOIN schedule_pick sp ON sp.schedule_pick_id = spd.schedule_pick_id ")
                        .append(" INNER JOIN iteminfo i ON i.itemid = spd.itemid and i.companyid = spd.companyid ")
                        .append(" LEFT JOIN itemclass ic on i.classid=ic.classid ");

                sql.append(" WHERE spd.schedule_pick_id = ? ");
                fparams.add(mainid);

                sqlBuilder.append(sql);
                countSqlBuilder.append(sql);

                if (orderBys != null && !orderBys.isEmpty() && !orderBys.contains(";")) {
                    sqlBuilder.append(" order by ").append(orderBys).append(" ");
                }
                //sql的String
                String presql = sqlBuilder.toString();

                if (limit != null && limit == -1) {
                    limit = null;
                }
                Table table = DataUtils.queryData(conn, presql, fparams, null, offset, limit);

                if (offset != null && offset.equals(0)) {
                    Object total = DataUtils.getValueBySQL(conn, countSqlBuilder.toString(), fparams);
                    table.setTotal(Integer.parseInt(total.toString()));
                }

                rt.put("table", Transform.tableToJson(table));

                if (getSchedule != null && getSchedule) {
                    String relationScheduleId = table.getRows().get(0).getString("relation_schedule_id");
                    fparams.clear();
                    String scheduleSql = " SELECT orderid FROM scheduleorder WHERE scheduleid = ? ";
                    fparams.add(relationScheduleId);
                    Object relationScheduleOrderId = DataUtils.getValueBySQL(conn, scheduleSql, fparams);
                    rt.put("relationScheduleOrderId", relationScheduleOrderId);
                }

            } else {

                // 暂时未做


//                Table table = DataUtils.queryData(conn, presql, fparams, null, offset, limit);
//
//                if (offset != null && offset.equals(0)) {
//                    Object total = DataUtils.getValueBySQL(conn, countSql, fparams);
//                    table.setTotal(Integer.parseInt(total.toString()));
//                }
//                rt.put("table", Transform.tableToJson(table));
            }
        } catch (Exception e) {
            e.printStackTrace();
            message = "数据获取失败！！";
        }
        rt.put("message", message);
        return rt;
    }


    public static JSONObject invalidSchedulePick(JSONObject params, ActionContext context) throws SQLException, NamingException {
        Connection conn = context.getConnection(DATASOURCE);
        JSONObject rt = new JSONObject();
        String orderid = params.getString("orderid");
        String companyid = params.getString("companyid");
        String mainid = params.getString("mainid");
        String loginuserid = params.getString("loginuserid");
        String loginUser = params.getString("loginUser");
        Integer pricebit = params.getInteger("pricebit");
        String houseid = params.getString("houseid");
        String operate_time = params.getString("operate_time");
        String[] arr = operate_time.split("-");
        String sdate = arr[0] + "-" + arr[1] + "-" + "01"; // 业务日期转成每个月1日来记录itemmonth数据

        String state = "0";
        String message = "";

        int countbit = params.getInteger("countbit");
        int moneybit = params.getInteger("moneybit");

        try {

            message = Pdacommon.checkInvalidDate(mainid, "schedule_pick", "schedule_pick_id", conn);
            if (!message.equals("")) {
                state = "2";
            } else {
                Statement ps = conn.createStatement();
                List<Object> fparams = new ArrayList<>();
                conn.setAutoCommit(false);
                String item = "";
                String findsql = "select itemid,count,total,batchno from schedule_pick_detail spd where spd.schedule_pick_id = ? order by spd.goods_number asc";
                fparams.add(mainid);
                Table table = DataUtils.queryData(conn, findsql, fparams, null, null, null);

                String scheduleTotalSql = " SELECT relation_schedule_id FROM schedule_pick WHERE schedule_pick_id = ? ";
                String relationScheduleId = DataUtils.getValueBySQL(conn, scheduleTotalSql, fparams).toString();

                String requestUpdateSql = " UPDATE prodrequisition_work_total SET count = count - ? WHERE scheduleid = ? AND itemid = ? ";
                PreparedStatement requestUpdatePs = conn.prepareStatement(requestUpdateSql);

                Iterator<Row> iteratordata = table.getRows().iterator();
                while (iteratordata.hasNext()) {
                    Row info = iteratordata.next();
                    double count = Double.parseDouble(info.getValue("count").toString());
                    double total = Double.parseDouble(info.getValue("total").toString());
                    String itemid = info.getString("itemid");
                    String batchno = erpscan.save.Pdainvalid.transformSpecialInfo(info.getString("batchno"));

                    ps.addBatch("insert into stock (stockid,companyid,itemid,houseid,count,money,newcostprice,batchno) VALUES ('" + Common.getUpperUUIDString() + "','" + companyid + "','" + itemid
                            + "','" + houseid + "',+" + count + "," + total + ",round(" + (count > 0 ? total / count : 0) + "," + pricebit + "),'" + batchno
                            + "') on duplicate key update count=round(count+" + count + "," + countbit + "),money=round(money+" + total + "," + moneybit
                            + "),newcostprice=round(if(count=0,newcostprice,money/count)," + pricebit + ") ");

                    ps.addBatch("delete from stock where itemid='" + itemid + "' and houseid='" + houseid + "' and batchno='" + batchno + "' and count=0 and money=0 ");

                    requestUpdatePs.setDouble(1, count);
                    requestUpdatePs.setString(2, relationScheduleId);
                    requestUpdatePs.setString(3, itemid);
                    requestUpdatePs.addBatch();

                    ps.addBatch("update itemmonth set count=round(count+" + count + "," + countbit + "), money=round(money+" + total + "," + moneybit + "),prodreq_count=round(prodreq_count-"
                            + count + "," + countbit + "), prodreq_money=round(prodreq_money-" + total + "," + moneybit + ")" + " where houseid='" + houseid + "' and itemid='" + itemid + "' and batchno='"
                            + batchno + "' and sdate='" + sdate + "'  and (select count(schedule_pick_id) from schedule_pick where schedule_pick_id='" + mainid + "' and status='1')=1");

                }
                requestUpdatePs.executeBatch();

                fparams.clear();
                String checstocksql = "select k.itemid,k.codeid,k.itemname,k.houseid,k.batchno,round(-k.scount-ifnull(s.count,0)-ifnull(s.checkout_count,0),?) as bcount " +
                        " from (select spd.itemid,im.codeid,im.itemname,spd.houseid,spd.batchno,round(sum(spd.count),?) as scount " +
                        " from schedule_pick_detail spd join iteminfo im on spd.itemid=im.itemid and spd.companyid=im.companyid " +
                        " where spd.schedule_pick_id = ? and spd.status='1' " +
                        " group by spd.itemid,spd.houseid,spd.batchno having scount<0) k " +
                        " left join stock s on k.itemid=s.itemid and k.houseid=s.houseid and k.batchno=s.batchno ";
                fparams.add(countbit);
                fparams.add(countbit);
                fparams.add(mainid);
                Table stable = DataUtils.queryData(conn, checstocksql, fparams, null, null, null);

                Iterator<Row> iteratordata2 = stable.getRows().iterator();
                while (iteratordata2.hasNext()) {
                    Row info = iteratordata2.next();
                    double bcount = Double.parseDouble(info.getValue("bcount").toString());
                    if (bcount > 0) {
                        item = item + (item.equals("") ? "" : "、") + info.getString("codeid") + " " + info.getString("itemname") + " " + info.getString("batchno");
                    }
                }

                if (item.equals("")) {

                    ps.addBatch("update schedule_pick_detail set status='2',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where schedule_pick_id ='" + mainid
                            + "' and status='1' ");
                    ps.addBatch("update schedule_pick set status='2',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where schedule_pick_id='" + mainid + "' and status='1'");

                    fparams.clear();
                    String relationSql = " SELECT prodrequisitionid FROM prodrequisition WHERE schedule_pick_id = ? ";
                    fparams.add(mainid);
                    Object result = DataUtils.getValueBySQL(conn, relationSql, fparams);
                    String prodrequisitionid = result == null ? "" : result.toString();

                    ps.addBatch("update prodrequisition set status='2',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where prodrequisitionid ='" + prodrequisitionid
                            + "' and status='1' ");
                    ps.addBatch("update prodrequisitiondetail set status='2',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where prodrequisitionid='" + prodrequisitionid + "' and status='1'");

                    ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
                            + companyid + "'," + Pdacommon.getDatalogBillChangefunc("schedule_pick", "43") + ",'作废','" + mainid + "','单据编号：" + orderid + "','" + loginuserid + "','" + loginUser
                            + "',now())");

                    ps.executeBatch();
                    conn.commit();
                    int count = ps.getUpdateCount();
                    if (count == 0) {
                        state = "3";
                        message = "已作废，不能重复作废。";
                    } else {
                        state = "1";
                    }
                }

                conn.setAutoCommit(true);
            }

        } catch (Exception e) {
            e.printStackTrace();
            message = "作废操作失败，请稍后再试。";
            try {
                conn.rollback();
            } catch (Exception e1) {
                // e1.printStackTrace();
            }
        } finally {
            conn.close();
        }
        rt.put("state", state);
        rt.put("message", message);
        return rt;
    }

    public static JSONObject getItemListByScheduleId(JSONObject params, ActionContext context) throws SQLException, NamingException {
        Connection conn = context.getConnection(DATASOURCE);
        JSONObject rt = new JSONObject();
        String message = "";
        int m = 0;

        try {
            String scheduleid = params.getString("scheduleid");

            List<Object> fparams = new ArrayList<Object>();

            // 获取总需物料
            StringBuilder sql = new StringBuilder();

            sql.append(" select pwt.itemid,pwt.goods_number as goods_number, ")
                    .append(" i.itemname as itemname,i.imgurl as imgurl,i.barcode as barcode,i.codeid as codeid,i.sformat as sformat,i.unit as unit, ")
                    .append(" i.property1 as property1,i.property2 as property2,i.property3 as property3,i.property4 as property4,i.property5 as property5, ")
                    .append(" i.unitstate1,i.unitset1,i.unitstate2,i.unitset2,i.unitstate3,i.unitset3, ")
                    .append(" ifnull(ic.classname,'')as classname, ")
                    .append(" sum(needcount) as original_need, ")
                    .append(" sum(count) as have_picked,  ")
                    .append(" sum(needcount-count) as still_need ");

            sql.append(" from prodrequisition_work_total pwt ")
                    .append(" left join iteminfo i on pwt.itemid=i.itemid ")
                    .append(" left join itemclass ic on i.classid=ic.classid ");

            sql.append(" where pwt.scheduleid= ? ")
                    .append(" group by pwt.itemid ")
                    .append(" having still_need > 0 ")
                    .append(" order by pwt.goods_number asc ");
            fparams.add(scheduleid);

            Table itemTable = DataUtils.queryData(conn, sql.toString(), fparams, null, null, null);

            if (itemTable == null || itemTable.getRows().size() == 0) {
                message = "没有需领的物料。";
                rt.put("message", message);
                return rt;
            }

            // 获取需领的物料id以及需领数量
            Map<String, Double> itemids = new HashMap<String, Double>();

            for (Row row : itemTable.getRows()) {
                itemids.put(row.getString("itemid"), Double.parseDouble(row.getValue("still_need").toString()));
            }

            // 获取物料领料配置
            List<Map<String, Object>> itemConfig = getItemConfig(itemids, params.getString("houseid"), 1, conn);

            rt.put("itemTable", Transform.tableToJson(itemTable));
            rt.put("itemConfig", JSON.toJSON(itemConfig));


        } catch (Exception e) {
            e.printStackTrace();
            message = "数据获取失败！！";
        }
        rt.put("message", message);

        return rt;
    }

    public static JSONObject getItemListByMainId(JSONObject params, ActionContext context)
            throws SQLException, NamingException {
        Connection conn = context.getConnection(DATASOURCE);
        JSONObject rt = new JSONObject();
        String message = "";
        int m = 0;

        try {
            String mainid = params.getString("mainid");

            List<Object> fparams = new ArrayList<>();
            StringBuilder sqlBuilder = new StringBuilder();
            StringBuilder sql = new StringBuilder();

            sqlBuilder.append(" SELECT i.codeid,i.itemname,i.sformat,i.mcode,i.classid,i.unit,i.imgurl,i.barcode,i.property1,i.property2,i.property3,i.property4,i.property5,i.unitstate1,i.unitset1,i.unitstate2,i.unitset2,i.unitstate3,i.unitset3, ")
                    .append(" ifnull(ic.classname,'') as classname, ")
                    .append(" sum(pwt.needcount) as original_need, ")
                    .append(" sum(pwt.count) as have_picked,  ")
                    .append(" sum(pwt.needcount-pwt.count) as still_need, ")
                    .append(" pwt.goods_number AS goods_number,pwt.itemid AS itemid ");

            sql.append(" FROM schedule_pick sp ")
                    .append(" LEFT JOIN prodrequisition_work_total pwt ON pwt.scheduleid = sp.relation_schedule_id ")
                    .append(" INNER JOIN iteminfo i ON i.itemid = pwt.itemid ")
                    .append(" LEFT JOIN itemclass ic on i.classid=ic.classid ");

            sql.append(" WHERE sp.schedule_pick_id = ? ")
                    .append(" AND pwt.itemid IN ( SELECT itemid FROM schedule_pick_detail spd WHERE spd.schedule_pick_id = sp.schedule_pick_id ) ")
                    .append(" GROUP BY pwt.itemid ")
                    .append(" HAVING still_need > 0 ")
                    .append(" ORDER BY pwt.goods_number ");
            fparams.add(mainid);

            sqlBuilder.append(sql);


            //sql的String
            String presql = sqlBuilder.toString();

            Table table = DataUtils.queryData(conn, presql, fparams, null, null, null);

            rt.put("table", Transform.tableToJson(table));

        } catch (Exception e) {
            e.printStackTrace();
            message = "数据获取失败！！";
        }
        rt.put("message", message);
        return rt;
    }

    public static JSONObject getItemListByItemList(JSONObject params, ActionContext context) throws SQLException, NamingException {
        Connection conn = context.getConnection(DATASOURCE);
        JSONObject rt = new JSONObject();
        String message = "";
        int m = 0;

        try {
            JSONArray itemList = params.getJSONArray("itemList");
            String scheduleid = params.getString("scheduleid");

            if (itemList == null || itemList.size() == 0) {
                message = "服务器未接收到商品";
                rt.put("message", message);
                return rt;
            }

            List<Object> fparams = new ArrayList<Object>();

            // 获取总需物料
            StringBuilder sql = new StringBuilder();

            sql.append(" select pwt.itemid,pwt.goods_number as goods_number, ")
                    .append(" i.itemname as itemname,i.imgurl as imgurl,i.barcode as barcode,i.codeid as codeid,i.sformat as sformat,i.unit as unit, ")
                    .append(" i.property1 as property1,i.property2 as property2,i.property3 as property3,i.property4 as property4,i.property5 as property5, ")
                    .append(" i.unitstate1,i.unitset1,i.unitstate2,i.unitset2,i.unitstate3,i.unitset3, ")
                    .append(" ifnull(ic.classname,'')as classname, ")
                    .append(" sum(needcount) as original_need, ")
                    .append(" sum(count) as have_picked,  ")
                    .append(" sum(needcount-count) as still_need ");

            sql.append(" from prodrequisition_work_total pwt ")
                    .append(" left join iteminfo i on pwt.itemid=i.itemid ")
                    .append(" left join itemclass ic on i.classid=ic.classid ");

            sql.append(" where pwt.scheduleid = ? ");
            fparams.add(scheduleid);

            sql.append(" and pwt.itemid in ( ");
            for (int i = 0; i < itemList.size(); i++) {
                sql.append(" ? ");
                if (i != itemList.size() - 1) {
                    sql.append(",");
                }
                fparams.add(itemList.getString(i));
            }
            sql.append(" ) ");

            sql.append(" group by pwt.itemid ")
                    .append(" having still_need > 0 ")
                    .append(" order by pwt.goods_number asc ");

            Table itemTable = DataUtils.queryData(conn, sql.toString(), fparams, null, null, null);

            // 获取需领的物料id以及需领数量
            Map<String, Double> itemids = new HashMap<String, Double>();

            for (Row row : itemTable.getRows()) {
                itemids.put(row.getString("itemid"), Double.parseDouble(row.getValue("still_need").toString()));
            }

            // 获取物料领料配置
            List<Map<String, Object>> itemConfig = getItemConfig(itemids, params.getString("houseid"), 1, conn);

            rt.put("itemTable", Transform.tableToJson(itemTable));
            rt.put("itemConfig", JSON.toJSON(itemConfig));


        } catch (Exception e) {
            e.printStackTrace();
            message = "数据获取失败！！";
        }
        rt.put("message", message);

        return rt;
    }

    /**
     * 以[{itemid : "123",config : {"batchno1" : 100.0,"batchno2" : 21.0}}]的形式返回
     *
     * @param itemids 需领的物料id以及数量（Map<String, Double>，key=物料ID，value=需领数量）
     * @param houseid 仓库id
     * @param mode    1:优先领取数量最多（其他模式暂不支持）
     */
    private static List<Map<String, Object>> getItemConfig(
            Map<String, Double> itemids,
            String houseid,
            int mode,
            Connection conn) {

        List<Map<String, Object>> itemConfig = new ArrayList<>();

        // 1. 构建SQL查询（无需SUM和GROUP BY）
        List<Object> fparams = new ArrayList<>();
        StringBuilder stockSql = new StringBuilder();

        stockSql.append(" SELECT s.itemid, s.batchno, s.count ")
                .append(" FROM stock s ")
                .append(" WHERE s.houseid = ? ")
                .append(" AND s.itemid IN ( ");

        fparams.add(houseid);
        for (String itemid : itemids.keySet()) {
            stockSql.append(" ?, ");
            fparams.add(itemid);
        }
        stockSql.delete(stockSql.length() - 2, stockSql.length()); // 移除末尾逗号

        if (mode == 1) {
            stockSql.append(" ) ")
                    .append(" ORDER BY s.itemid, s.count DESC"); // 按库存数量降序
        } else {
            stockSql.append(" ) ")
                    .append(" ORDER BY s.itemid, s.batchno "); // 默认按批次升序
        }


        Table stockTable = DataUtils.queryData(conn, stockSql.toString(), fparams, null, null, null);

        // 2. 按物料ID分组（保持批次顺序）
        Map<String, List<Row>> groupedStock = new HashMap<>();
        if (stockTable != null && !stockTable.getRows().isEmpty()) {
            for (Row row : stockTable.getRows()) {
                String itemid = row.getString("itemid");
                List<Row> batchList = groupedStock.computeIfAbsent(itemid, k -> new ArrayList<>());
                batchList.add(row);
            }
        }

        // 3. 为每个物料ID生成分配配置
        if (mode == 1) {
            for (String itemid : itemids.keySet()) {
                double need = itemids.get(itemid); // 需领数量
                Map<String, Double> config = new LinkedHashMap<>(); // 保持批次顺序

                List<Row> batchList = groupedStock.getOrDefault(itemid, new ArrayList<>());

                for (Row batch : batchList) {
                    if (need <= 0) break; // 分配完成

                    double available = Double.parseDouble(batch.getValue("count").toString()); // 库存数量
                    double allocated = Math.min(need, available); // 计算可分配量

                    if (allocated > 0) {
                        String batchno = batch.getString("batchno");
                        config.put(batchno, allocated); // 存储为double类型
                        need -= allocated;
                    }
                }

                // 构建结果Map
                Map<String, Object> result = new HashMap<>();
                result.put("itemid", itemid);
                result.put("config", config); // config是Map<String, Double>
                itemConfig.add(result);
            }
        }

        return itemConfig;
    }

}
