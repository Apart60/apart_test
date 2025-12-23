package erpscan.save;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Iterator;

import javax.naming.NamingException;

import com.alibaba.fastjson.JSONObject;
import com.justep.baas.action.ActionContext;
import com.justep.baas.data.DataUtils;
import com.justep.baas.data.Row;
import com.justep.baas.data.Table;
import com.justep.baas.data.Transform;
import java.lang.StringBuffer;

import erpscan.Common;
import erpscan.save.Pdacommon;

public class Moreview {
	private static final String DATASOURCE = Common.DATASOURCE;

	public static JSONObject getStockstateInfo(JSONObject params, ActionContext context) throws SQLException, NamingException {

		Connection conn = context.getConnection(DATASOURCE);

		String companyid = params.getString("companyid");
		String condition = params.getString("condition");
		String storeselect = params.getString("storeselect");

		int offset = params.getInteger("offset");
		int limit = params.getInteger("limit");

		int countbit = params.getInteger("countbit");
		int moneybit = params.getInteger("moneybit");

		String orderBys = params.getString("orderBys");
		if (orderBys.equals("")) {
			orderBys = " codeid asc ";
		}

		String sql = "";
		String sql1 = "";
		if (storeselect.equals("")) {
			// sql = " select k.*,round(ifnull(sum(s.count),0),"
			// + countbit
			// + ") as count,round(ifnull(sum(s.money),0),"
			// + moneybit
			// +
			// ") as money,ifnull(sk.checkout_count,0) as checkout_count from itemmonth s left join iteminfo k on s.itemid=k.itemid left join stock sk on s.itemid=sk.itemid and s.houseid=sk.houseid and s.batchno=sk.batchno where "
			// + condition + "   order by " + orderBys + "  limit " + offset +
			// "," + limit + "";
			//
			// sql1 =
			// "select count(*)  from (select round(ifnull(sum(s.count),0)," +
			// countbit + ") as count,round(ifnull(sum(s.money),0)," + moneybit
			// +
			// ") as money,0 as checkout_count from itemmonth s left join iteminfo k on s.itemid=k.itemid where "
			// + condition + " ) a ";

			sql = " select k.itemid,k.companyid,k.codeid,k.itemname,k.sformat,k.unit,k.classid,k.imgurl,k.property1,k.property2,k.property3,k.property4,k.property5,k.barcode,k.mcode,k.remark,k.status,k.inprice,k.outsourcingprice,k.outprice,k.outprice1,k.outprice2,k.outprice3,k.outprice4,k.outprice5,k.unitstate1,k.unitstate2,k.unitstate3,k.unitset1,k.unitset2,k.unitset3,round(ifnull(sum(s.count),0),"
					+ countbit
					+ ") as count,round(ifnull(sum(s.money),0),"
					+ moneybit
					+ ") as money, round(ifnull(sum(s.checkout_count),0),"
					+ moneybit
					+ ") as checkout_count from itemtotal s left join iteminfo k on s.itemid=k.itemid   where " + condition + "   order by " + orderBys + "  limit " + offset + "," + limit + "";

			sql1 = "select count(*)  from (select round(ifnull(sum(s.count),0)," + countbit + ") as count from itemtotal s left join iteminfo k on s.itemid=k.itemid where " + condition + " ) a ";

		} else if (storeselect.equals("3")) {
			sql = " select  k.itemid,k.companyid,k.codeid,k.itemname,k.sformat,k.unit,k.classid,k.imgurl,k.property1,k.property2,k.property3,k.property4,k.property5,k.barcode,k.mcode,k.remark,k.status,k.inprice,k.outsourcingprice,k.outprice,k.outprice1,k.outprice2,k.outprice3,k.outprice4,k.outprice5,k.unitstate1,k.unitstate2,k.unitstate3,k.unitset1,k.unitset2,k.unitset3,round(ifnull(sum(s.count),0),"
					+ countbit
					+ ") as count,round(ifnull(sum(s.money),0),"
					+ moneybit
					+ ") as money, round(ifnull(sum(s.checkout_count),0),"
					+ moneybit
					+ ") as checkout_count from itemtotal s left join iteminfo k on s.itemid=k.itemid  where "
					+ condition
					+ " having count=0 and money=0  order by "
					+ orderBys
					+ "  limit "
					+ offset
					+ "," + limit + "";

			sql1 = "select count(*)  from (select round(ifnull(sum(s.count),0)," + countbit + ") as count,round(ifnull(sum(s.money),0)," + moneybit
					+ ") as money  from itemtotal s left join iteminfo k on s.itemid=k.itemid where " + condition + "  having count=0 and money=0 ) a ";
		} else {

			sql = " select  k.itemid,k.companyid,k.codeid,k.itemname,k.sformat,k.unit,k.classid,k.imgurl,k.property1,k.property2,k.property3,k.property4,k.property5,k.barcode,k.mcode,k.remark,k.status,k.inprice,k.outsourcingprice,k.outprice,k.outprice1,k.outprice2,k.outprice3,k.outprice4,k.outprice5,k.unitstate1,k.unitstate2,k.unitstate3,k.unitset1,k.unitset2,k.unitset3,round(ifnull(sum(s.count),0),"
					+ countbit
					+ ") as count,round(ifnull(sum(s.money),0),"
					+ moneybit
					+ ") as money, round(ifnull(sum(s.checkout_count),0),"
					+ moneybit
					+ ") as checkout_count from stock s left join iteminfo k on s.itemid=k.itemid where "
					+ condition
					+ (storeselect.equals("1") ? " having count>0 " : " having count<=0 or (count=0 and money<>0) ") + " order by " + orderBys + "  limit " + offset + "," + limit + "";

			sql1 = "select count(*)  from (select round(ifnull(sum(s.count),0)," + countbit + ") as count,round(ifnull(sum(s.money),0)," + moneybit
					+ ") as money from stock s left join iteminfo k on s.itemid=k.itemid where " + condition
					+ (storeselect.equals("1") ? " having count>0 " : " having count<=0 or (count=0 and money<>0) ") + ") a ";
		}

		// long s = System.currentTimeMillis();

		Table table = DataUtils.queryData(conn, sql, null, null, null, null);

		// long a = System.currentTimeMillis();
		// System.out.println("a:"+((a-s)/1000));

		Object countObject = DataUtils.getValueBySQL(conn, sql1, null);
		int count;
		if (countObject == null) {
			count = 0;
		} else {
			count = Integer.parseInt(countObject.toString());
		}

		JSONObject ret = new JSONObject();
		ret.put("table", Transform.tableToJson(table));
		ret.put("rowsize", count);

		// System.out.println("b:"+((System.currentTimeMillis()-s)/1000));

		return ret;
	}

	public static JSONObject getStockturnView(JSONObject params, ActionContext context) throws SQLException, NamingException {

		Connection conn = context.getConnection(DATASOURCE);

		String companyid = params.getString("companyid");
		String incondition = params.getString("incondition");
		String outcondition = params.getString("outcondition");

		int offset = params.getInteger("offset");
		int limit = params.getInteger("limit");

		String orderBys = params.getString("orderBys");
		// String temporderBys =
		// "d.operate_time desc,d.update_time desc,d.stype desc,d.orderid desc,d.goods_number asc";
		orderBys = "operate_time desc,update_time desc,stype desc,orderid desc,goods_number asc";

		// if (orderBys.indexOf("customercode") > -1 ||
		// orderBys.indexOf("customername") > -1) {
		// orderBys = "c." + orderBys + "," + temporderBys;
		// } else if (orderBys.indexOf("housecode") > -1 ||
		// orderBys.indexOf("housename") > -1) {
		// orderBys = "sh." + orderBys + "," + temporderBys;
		// } else if (orderBys.indexOf("staffcode") > -1 ||
		// orderBys.indexOf("staffname") > -1) {
		// orderBys = "s." + orderBys + "," + temporderBys;
		// } else if (orderBys.equals("") || orderBys.indexOf("operate_time") >
		// -1) {
		// orderBys = temporderBys;
		// } else if (orderBys.indexOf("orderid") > -1) {
		// orderBys = "d." + orderBys +
		// ",d.operate_time desc,d.update_time desc,d.goods_number asc";
		// } else if (orderBys.indexOf("stype") > -1) {
		// orderBys = "d." + orderBys +
		// ",d.operate_time desc,d.update_time desc,d.orderid desc,d.goods_number asc";
		// } else {
		// orderBys = "d." + orderBys + "," + temporderBys;
		// }

		String inorderby = "";// " order by " + orderBys + " limit 0," + (limit
								// + offset) + " ";

		StringBuffer manbuffer = new StringBuffer();
		StringBuffer allbuffer = new StringBuffer();
		StringBuffer buffer = detail_view(incondition, inorderby);

		manbuffer
				.append("select  d.*,ifnull(c.customercode,'') as customercode,ifnull(c.customername,'') as customername,ifnull(sh.housecode,'') as housecode,ifnull(sh.housename,'') as housename,ifnull(s.staffcode,'') as staffcode,ifnull(s.staffname,'') as staffname from ( ");
		manbuffer.append(buffer.toString());
		manbuffer.append(") d left join customer c on d.customerid=c.customerid left join storehouse sh on d.houseid=sh.houseid left join staffinfo s on d.operate_by=s.staffid "
				+ (outcondition.trim().equals("") ? "" : " where " + outcondition) + " order by " + orderBys + " limit " + offset + "," + limit);

		// System.out.println(manbuffer.toString());
		Table table = DataUtils.queryData(conn, manbuffer.toString(), null, null, null, null);

		allbuffer.append("select count(*) from ( ");
		allbuffer.append(detail_view(incondition, "").toString());
		allbuffer.append(") d left join customer c on d.customerid=c.customerid left join storehouse sh on d.houseid=sh.houseid left join staffinfo s on d.operate_by=s.staffid "
				+ (outcondition.trim().equals("") ? "" : " where " + outcondition));

		Object countObject = DataUtils.getValueBySQL(conn, allbuffer.toString(), null);
		long count;
		if (countObject == null) {
			count = 0;
		} else {
			count = Long.parseLong(countObject.toString());
		}

		manbuffer.setLength(0);
		allbuffer.setLength(0);
		buffer.setLength(0);

		JSONObject ret = new JSONObject();
		ret.put("table", Transform.tableToJson(table));
		ret.put("rowsize", count);

		return ret;
	}

	public static StringBuffer detail_view(String incondition, String orderlimit) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(" (select detailid,goods_number,orderid,itembeginid as mainid,stype,originalbill,companyid,operate_time,operate_by,itemid,batchno,houseid,'' as customerid,count as incount,total as intotal, 0 as outcount, 0 as outtotal,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,'itembegindetail' as tname from itembegindetail k "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append(" select detailid,goods_number,orderid,storeinid as mainid,stype,originalbill,companyid,operate_time,operate_by,itemid,batchno,houseid,customerid,if(stype='11',count,0) as incount,if(stype='11',total,0) as intotal,if(stype='61',count,0) as outcount, if(stype='61',total,0) as outtotal,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,'storeindetail' as tname from storeindetail k "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append(" select detailid,goods_number,orderid,storeoutid as mainid,stype,originalbill,companyid,operate_time,operate_by,itemid,batchno,houseid,customerid,if(stype='71',count,0) as incount,if(stype='71',cost_money,0) as intotal, if(stype='21',count,0) as outcount, if(stype='21',cost_money,0) as outtotal,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,'storeoutdetail' as tname from storeoutdetail k "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append(" select detailid,goods_number,orderid,storemoveid as mainid,oldtype as stype,originalbill,companyid,operate_time,operate_by,itemid,batchno,oldhouseid as houseid,'' as customerid,0 as incount,0 as intotal, count as outcount, total as outtotal,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,'storemovedetail' as tname from storemovedetail k "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append(" select detailid,goods_number,orderid,storemoveid as mainid,newtype as stype,originalbill,companyid,operate_time,operate_by,itemid,batchno,newhouseid as houseid, '' as customerid,count as incount,total as intotal, 0 as outcount, 0 as outtotal,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,'storemovedetail' as tname from storemovedetail k "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append(" select detailid,goods_number,orderid,splitsid as mainid,stype,originalbill,companyid,operate_time,operate_by,itemid,batchno, houseid,'' as customerid,if(stype='32',count,0) as incount,if(stype='32',total,0) as intotal, if(stype='31',count,0) as outcount, if(stype='31',total,0) as outtotal,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,'splitsdetail' as tname from splitsdetail k "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append(" select detailid,goods_number,orderid,storecheckid as mainid,stype,originalbill,companyid,operate_time,operate_by,itemid,batchno, houseid,'' as customerid,if(stype='51',count,0) as incount,if(stype='51',total_profit,0) as intotal, if(stype='52',loss_count,0) as outcount, if(stype='52',total_loss,0) as outtotal,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,'storecheckdetail' as tname from storecheckdetail k "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append(" select detailid,goods_number,orderid,prodrequisitionid as mainid,stype,originalbill,companyid,operate_time,operate_by,itemid,batchno,houseid,customerid,if(stype='261',count,0) as incount,if(stype='261',total,0) as intotal, if(stype='101',count,0) as outcount, if(stype='101',total,0) as outtotal,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,'prodrequisitiondetail' as tname from prodrequisitiondetail k "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append(" select detailid,goods_number,orderid,otherinoutid as mainid,stype,originalbill,companyid,operate_time,operate_by,itemid,batchno,houseid,customerid,if(stype='121',count,0) as incount,if(stype='121',total,0) as intotal, if(stype='131',count,0) as outcount, if(stype='131',total,0) as outtotal,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,'otherinoutdetail' as tname from otherinoutdetail  k "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append(" select detailid,goods_number,orderid,prodstorageid as mainid,stype,originalbill,companyid,operate_time,operate_by,itemid,batchno,houseid,'' as customerid,count as incount,total as intotal, 0 as outcount, 0 as outtotal,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,'prodstoragedetail' as tname from prodstoragedetail k "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append(" select detailid,goods_number,orderid,processinoutid as mainid,stype,originalbill,companyid,operate_time,operate_by,itemid,batchno,houseid,customerid,if(stype='241',count,0) as incount,if(stype='241',total,0) as intotal,if(stype='231',count,0) as outcount, if(stype='231',total,0) as outtotal,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,'otherinoutdetail' as tname from processinoutdetail k "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append(" select detailid,goods_number,orderid,outsourcinginid as mainid,stype,originalbill,companyid,operate_time,operate_by,itemid,batchno,houseid,customerid,if(stype='251' ,count,0) as incount,if(stype='251',total,0) as intotal,if(stype='271',count,0) as outcount, if(stype='271',total,0) as outtotal,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,'outsourcingin' as tname from outsourcingindetail k "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit + ") ");

		return buffer;
	}

	public static StringBuffer billdetail_view(String incondition, String orderlimit) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(" (select detailid,goods_number,orderid,itembeginid as mainid,stype,originalbill,companyid,operate_time,operate_by,itemid,batchno,houseid,'' as customerid,count as incount,total as intotal, 0 as outcount, 0 as outtotal,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,'itembegindetail' as tname from itembegindetail k "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append(" select detailid,goods_number,orderid,storeinid as mainid,stype,originalbill,companyid,operate_time,operate_by,itemid,batchno,houseid,customerid,if(stype='11',count,0) as incount,if(stype='11',total,0) as intotal,if(stype='61',count,0) as outcount, if(stype='61',total,0) as outtotal,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,'storeindetail' as tname from storeindetail k "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append(" select detailid,goods_number,orderid,storeoutid as mainid,stype,originalbill,companyid,operate_time,operate_by,itemid,batchno,houseid,customerid,if(stype='71',count,0) as incount,if(stype='71',cost_money,0) as intotal, if(stype='21',count,0) as outcount, if(stype='21',cost_money,0) as outtotal,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,'storeoutdetail' as tname from storeoutdetail k "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append(" select detailid,goods_number,orderid,storemoveid as mainid,oldtype as stype,originalbill,companyid,operate_time,operate_by,itemid,batchno,oldhouseid as houseid,'' as customerid,0 as incount,0 as intotal, count as outcount, total as outtotal,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,'storemovedetail' as tname from storemovedetail k "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append(" select detailid,goods_number,orderid,storemoveid as mainid,newtype as stype,originalbill,companyid,operate_time,operate_by,itemid,batchno,newhouseid as houseid, '' as customerid,count as incount,total as intotal, 0 as outcount, 0 as outtotal,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,'storemovedetail' as tname from storemovedetail k "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append(" select detailid,goods_number,orderid,splitsid as mainid,stype,originalbill,companyid,operate_time,operate_by,itemid,batchno, houseid,'' as customerid,if(stype='32',count,0) as incount,if(stype='32',total,0) as intotal, if(stype='31',count,0) as outcount, if(stype='31',total,0) as outtotal,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,'splitsdetail' as tname from splitsdetail k "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append(" select detailid,goods_number,orderid,storecheckid as mainid,stype,originalbill,companyid,operate_time,operate_by,itemid,batchno, houseid,'' as customerid,if(stype='51',count,0) as incount,if(stype='51',total_profit,0) as intotal, if(stype='52',loss_count,0) as outcount, if(stype='52',total_loss,0) as outtotal,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,'storecheckdetail' as tname from storecheckdetail k "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append(" select detailid,goods_number,orderid,prodrequisitionid as mainid,stype,originalbill,companyid,operate_time,operate_by,itemid,batchno,houseid,customerid,if(stype='261',count,0) as incount,if(stype='261',total,0) as intotal, if(stype='101',count,0) as outcount, if(stype='101',total,0) as outtotal,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,'prodrequisitiondetail' as tname from prodrequisitiondetail k "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append(" select detailid,goods_number,orderid,otherinoutid as mainid,stype,originalbill,companyid,operate_time,operate_by,itemid,batchno,houseid,customerid,if(stype='121',count,0) as incount,if(stype='121',total,0) as intotal, if(stype='131',count,0) as outcount, if(stype='131',total,0) as outtotal,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,'otherinoutdetail' as tname from otherinoutdetail k "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append(" select detailid,goods_number,orderid,prodstorageid as mainid,stype,originalbill,companyid,operate_time,operate_by,itemid,batchno,houseid,'' as customerid,count as incount,total as intotal, 0 as outcount, 0 as outtotal,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,'prodstoragedetail' as tname from prodstoragedetail k "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit);
		buffer.append(") union all (");

		buffer.append(" select detailid,goods_number,orderid,lossid as mainid,stype,originalbill,companyid,operate_time,operate_by,itemid,batchno,houseid,'' as customerid,count as incount,total as intotal, 0 as outcount, 0 as outtotal,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,'prodstoragedetail' as tname from reportlossdetail k "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit);
		buffer.append(") union all (");

		buffer.append(" select detailid,goods_number,orderid,processinoutid as mainid,stype,originalbill,companyid,operate_time,operate_by,itemid,batchno,houseid,customerid,if(stype='241',count,0) as incount,if(stype='241',total,0) as intotal,if(stype='231',count,0) as outcount, if(stype='231',total,0) as outtotal,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,'otherinoutdetail' as tname from processinoutdetail k "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append(" select detailid,goods_number,orderid,outsourcinginid as mainid,stype,originalbill,companyid,operate_time,operate_by,itemid,batchno,houseid,customerid,if(stype='251' ,count,0) as incount,if(stype='251',total,0) as intotal,if(stype='271',count,0) as outcount, if(stype='271',total,0) as outtotal,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,'outsourcingin' as tname from outsourcingindetail k "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit + ") ");

		return buffer;
	}

	// 2020-12-1 调整(incondition.equals("") ? "" : " where " + incondition ));
	public static StringBuffer main_view(String incondition, String orderlimit) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(" (select orderid,itembeginid as mainid,bill_type,originalbill,companyid,operate_time,operate_by,houseid as inhouseid,'' as outhouseid,'' as customerid,count,total,remark,status,printing,outexcel,create_id,create_by,create_time,update_id,update_by,update_time,iproperty,'itembegin' as tname from itembegin "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append(" select orderid,storeinid as mainid,bill_type,originalbill,companyid,operate_time,operate_by,if(bill_type='1',houseid,'') as inhouseid,if(bill_type='6',houseid,'') as outhouseid,customerid,count,total,remark,status,printing,outexcel,create_id,create_by,create_time,update_id,update_by,update_time,iproperty,'storein' as tname from storein "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append(" select orderid,storeoutid as mainid,bill_type,originalbill,companyid,operate_time,operate_by,if(bill_type='7',houseid,'') as inhouseid,if(bill_type='2',houseid,'') as outhouseid,customerid,count,total,remark,status,printing,outexcel,create_id,create_by,create_time,update_id,update_by,update_time,iproperty,'storeout' as tname from storeout "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append(" select orderid,storemoveid as mainid,bill_type,originalbill,companyid,operate_time,operate_by,newhouseid as inhouseid,oldhouseid as outhouseid,'' as customerid,count,total,remark,status,printing,outexcel,create_id,create_by,create_time,update_id,update_by,update_time,iproperty ,'storemove' as tname from storemove "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append(" select orderid,storecheckid as mainid,bill_type,originalbill,companyid,operate_time,operate_by,houseid as inhouseid,'' as outhouseid,'' as customerid,count+loss_count as count,total_profit+total_loss as total ,remark,status,printing,outexcel,create_id,create_by,create_time,update_id,update_by,update_time,iproperty,'storecheck' as tname  from storecheck "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append(" select orderid,splitsid as mainid,bill_type,originalbill,companyid,operate_time,operate_by,newhouseid as inhouseid,oldhouseid as outhouseid,'' as customerid,oldcount as count,oldtotal as total,remark,status,printing,outexcel,create_id,create_by,create_time,update_id,update_by,update_time,iproperty,'splits' as tname   from splits "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append(" select orderid,lossid as mainid,bill_type,originalbill,companyid,operate_time,operate_by,houseid as inhouseid,'' as outhouseid,'' as customerid,count,total,remark,status,printing,outexcel,create_id,create_by,create_time,update_id,update_by,update_time,iproperty,'reportloss' as tname from reportloss "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append(" select orderid,prodrequisitionid as mainid,bill_type,originalbill,companyid,operate_time,operate_by,if(bill_type='26',houseid,'') as inhouseid,if(bill_type='10',houseid,'') as outhouseid, customerid,count,total,remark,status,printing,outexcel,create_id,create_by,create_time,update_id,update_by,update_time,iproperty,'prodrequisition' as tname from prodrequisition "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append(" select orderid,otherinoutid as mainid,bill_type,originalbill,companyid,operate_time,operate_by,if(bill_type='12',houseid,'') as inhouseid,if(bill_type='13',houseid,'') as outhouseid,customerid,count,total,remark,status,printing,outexcel,create_id,create_by,create_time,update_id,update_by,update_time,iproperty,'otherinout' as tname from otherinout "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append(" select orderid,prodstorageid as mainid,bill_type,originalbill,companyid,operate_time,operate_by,houseid as inhouseid,'' as outhouseid,'' as customerid,count,total,remark,status,printing,outexcel,create_id,create_by,create_time,update_id,update_by,update_time,iproperty,'prodstorage' as tname from prodstorage "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append(" select orderid,processinoutid as mainid,bill_type,originalbill,companyid,operate_time,operate_by,if(bill_type='24',houseid,'') as inhouseid,if(bill_type='23',houseid,'') as outhouseid,customerid,count,total,remark,status,printing,outexcel,create_id,create_by,create_time,update_id,update_by,update_time,iproperty,'processinout' as tname from processinout  "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append(" select orderid,outsourcinginid as mainid,bill_type,originalbill,companyid,operate_time,operate_by,if(bill_type='25',houseid,'') as inhouseid,if(bill_type='27',houseid,'') as outhouseid, customerid,count,total,remark,status,printing,outexcel,create_id,create_by,create_time,update_id,update_by,update_time,iproperty,'outsourcingin' as tname from outsourcingin "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit + ") ");

		return buffer;

	}

	// 2020-12-09
	public static StringBuffer main_detailhouse_view(String incondition, String detailhouse, String companysql, String orderlimit) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(" (select orderid,itembeginid as mainid,bill_type,originalbill,companyid,operate_time,operate_by,houseid as inhouseid,'' as outhouseid,'' as customerid,count,total,remark,status,printing,outexcel,create_id,create_by,create_time,update_id,update_by,update_time,iproperty,'itembegin' as tname from itembegin "
				+ (detailhouse.equals("") ? "where 1=1 " : " where (itembeginid in (select detail.itembeginid from itembegindetail detail left join storehouse sh on detail.houseid=sh.houseid where "
						+ companysql + " and " + detailhouse + "))") + (incondition.equals("") ? "" : " and " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append(" select orderid,storeinid as mainid,bill_type,originalbill,companyid,operate_time,operate_by,if(bill_type='1',houseid,'') as inhouseid,if(bill_type='6',houseid,'') as outhouseid,customerid,count,total,remark,status,printing,outexcel,create_id,create_by,create_time,update_id,update_by,update_time,iproperty,'storein' as tname from storein "
				+ (detailhouse.equals("") ? "where 1=1 " : " where (storeinid in (select detail.storeinid from storeindetail detail left join storehouse sh on detail.houseid=sh.houseid where "
						+ companysql + " and " + detailhouse + "))") + (incondition.equals("") ? "" : " and " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append(" select orderid,storeoutid as mainid,bill_type,originalbill,companyid,operate_time,operate_by,if(bill_type='7',houseid,'') as inhouseid,if(bill_type='2',houseid,'') as outhouseid,customerid,count,total,remark,status,printing,outexcel,create_id,create_by,create_time,update_id,update_by,update_time,iproperty,'storeout' as tname from storeout "
				+ (detailhouse.equals("") ? "where 1=1 " : " where (storeoutid in (select detail.storeoutid from storeoutdetail detail left join storehouse sh on detail.houseid=sh.houseid where "
						+ companysql + " and " + detailhouse + "))") + (incondition.equals("") ? "" : " and " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append(" select orderid,storemoveid as mainid,bill_type,originalbill,companyid,operate_time,operate_by,newhouseid as inhouseid,oldhouseid as outhouseid,'' as customerid,count,total,remark,status,printing,outexcel,create_id,create_by,create_time,update_id,update_by,update_time,iproperty ,'storemove' as tname from storemove "
				+ (detailhouse.equals("") ? "where 1=1 "
						: " where (storemoveid in (select detail.storemoveid from (select storemoveid,oldhouseid as houseid,oldtype as stype from storemovedetail where "
								+ companysql.replaceAll("detail.", "") + " union all select storemoveid,newhouseid as houseid,newtype as stype from storemovedetail where "
								+ companysql.replaceAll("detail.", "") + ") detail left join storehouse sh on detail.houseid=sh.houseid where " + detailhouse + "))")
				+ (incondition.equals("") ? "" : " and " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append(" select orderid,storecheckid as mainid,bill_type,originalbill,companyid,operate_time,operate_by,houseid as inhouseid,'' as outhouseid,'' as customerid,count+loss_count as count,total_profit+total_loss as total ,remark,status,printing,outexcel,create_id,create_by,create_time,update_id,update_by,update_time,iproperty,'storecheck' as tname  from storecheck "
				+ (detailhouse.equals("") ? "where 1=1 "
						: " where (storecheckid in (select detail.storecheckid from storecheckdetail detail left join storehouse sh on detail.houseid=sh.houseid where " + companysql + " and "
								+ detailhouse + "))") + (incondition.equals("") ? "" : " and " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append(" select orderid,splitsid as mainid,bill_type,originalbill,companyid,operate_time,operate_by,newhouseid as inhouseid,oldhouseid as outhouseid,'' as customerid,oldcount as count,oldtotal as total,remark,status,printing,outexcel,create_id,create_by,create_time,update_id,update_by,update_time,iproperty,'splits' as tname   from splits "
				+ (detailhouse.equals("") ? "where 1=1 " : " where (splitsid in (select detail.splitsid from splitsdetail detail left join storehouse sh on detail.houseid=sh.houseid where "
						+ companysql + " and " + detailhouse + "))") + (incondition.equals("") ? "" : " and " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append(" select orderid,lossid as mainid,bill_type,originalbill,companyid,operate_time,operate_by,houseid as inhouseid,'' as outhouseid,'' as customerid,count,total,remark,status,printing,outexcel,create_id,create_by,create_time,update_id,update_by,update_time,iproperty,'reportloss' as tname from reportloss "
				+ (detailhouse.equals("") ? "where 1=1 " : " where (lossid in (select detail.lossid from reportlossdetail detail left join storehouse sh on detail.houseid=sh.houseid where "
						+ companysql + " and " + detailhouse + "))") + (incondition.equals("") ? "" : " and " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append(" select orderid,prodrequisitionid as mainid,bill_type,originalbill,companyid,operate_time,operate_by,if(bill_type='26',houseid,'') as inhouseid,if(bill_type='10',houseid,'') as outhouseid, customerid,count,total,remark,status,printing,outexcel,create_id,create_by,create_time,update_id,update_by,update_time,iproperty,'prodrequisition' as tname from prodrequisition "
				+ (detailhouse.equals("") ? "where 1=1 "
						: " where (prodrequisitionid in (select detail.prodrequisitionid from prodrequisitiondetail detail left join storehouse sh on detail.houseid=sh.houseid where " + companysql
								+ " and " + detailhouse + "))") + (incondition.equals("") ? "" : " and " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append(" select orderid,otherinoutid as mainid,bill_type,originalbill,companyid,operate_time,operate_by,if(bill_type='12',houseid,'') as inhouseid,if(bill_type='13',houseid,'') as outhouseid,customerid,count,total,remark,status,printing,outexcel,create_id,create_by,create_time,update_id,update_by,update_time,iproperty,'otherinout' as tname from otherinout "
				+ (detailhouse.equals("") ? "where 1=1 "
						: " where (otherinoutid in (select detail.otherinoutid from otherinoutdetail detail left join storehouse sh on detail.houseid=sh.houseid where " + companysql + " and "
								+ detailhouse + "))") + (incondition.equals("") ? "" : " and " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append(" select orderid,prodstorageid as mainid,bill_type,originalbill,companyid,operate_time,operate_by,houseid as inhouseid,'' as outhouseid,'' as customerid,count,total,remark,status,printing,outexcel,create_id,create_by,create_time,update_id,update_by,update_time,iproperty,'prodstorage' as tname from prodstorage "
				+ (detailhouse.equals("") ? "where 1=1 "
						: " where (prodstorageid in (select detail.prodstorageid from prodstoragedetail detail left join storehouse sh on detail.houseid=sh.houseid where " + companysql + " and "
								+ detailhouse + "))") + (incondition.equals("") ? "" : " and " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append(" select orderid,processinoutid as mainid,bill_type,originalbill,companyid,operate_time,operate_by,if(bill_type='24',houseid,'') as inhouseid,if(bill_type='23',houseid,'') as outhouseid,customerid,count,total,remark,status,printing,outexcel,create_id,create_by,create_time,update_id,update_by,update_time,iproperty,'processinout' as tname from processinout  "
				+ (detailhouse.equals("") ? "where 1=1 "
						: " where (processinoutid in (select detail.processinoutid from processinoutdetail detail left join storehouse sh on detail.houseid=sh.houseid where " + companysql + " and "
								+ detailhouse + "))") + (incondition.equals("") ? "" : " and " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append(" select orderid,outsourcinginid as mainid,bill_type,originalbill,companyid,operate_time,operate_by,if(bill_type='25',houseid,'') as inhouseid,if(bill_type='27',houseid,'') as outhouseid, customerid,count,total,remark,status,printing,outexcel,create_id,create_by,create_time,update_id,update_by,update_time,iproperty,'outsourcingin' as tname from outsourcingin "
				+ (detailhouse.equals("") ? "where 1=1 "
						: " where (outsourcinginid in (select detail.outsourcinginid from outsourcingindetail detail left join storehouse sh on detail.houseid=sh.houseid where " + companysql
								+ " and " + detailhouse + "))") + (incondition.equals("") ? "" : " and " + incondition) + orderlimit + ")");

		return buffer;

	}

	// 2020-12-1 调整(incondition.equals("") ? "" : " where " + incondition ));
	// 增加调拔入库
	public static StringBuffer itemdetail_view(String incondition, String incondition2, String orderlimit) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(" (select detailid,goods_number,orderid,itembeginid as mainid,stype,originalbill,companyid,operate_time,operate_by,itemid,batchno,houseid,'' as customerid,count,total,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,'itembegindetail' as tname from itembegindetail k "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append("select detailid,goods_number,orderid,storeinid as mainid,stype,originalbill,companyid,operate_time,operate_by,itemid,batchno,houseid,customerid,count,total,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,'storeindetail' as tname from storeindetail k "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append("select detailid,goods_number,orderid,storeoutid as mainid,stype,originalbill,companyid,operate_time,operate_by,itemid,batchno,houseid,customerid,count , cost_money as total,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,'storeoutdetail' as tname from storeoutdetail k "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append("select detailid,goods_number,orderid,storemoveid as mainid,oldtype as stype,originalbill,companyid,operate_time,operate_by,itemid,batchno,oldhouseid as houseid,'' as customerid, count,total,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,'storemovedetail' as tname from storemovedetail k "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append("select detailid,goods_number,orderid,storemoveid as mainid,newtype as stype,originalbill,companyid,operate_time,operate_by,itemid,batchno,newhouseid as houseid,'' as customerid, count,total,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,'storemovedetail' as tname from storemovedetail k "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append("select detailid,goods_number,orderid,splitsid as mainid,stype,originalbill,companyid,operate_time,operate_by,itemid,batchno,houseid,'' as customerid, count,total,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,'splitsdetail' as tname from splitsdetail k "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append("select detailid,goods_number,orderid,storecheckid as mainid,stype,originalbill,companyid,operate_time,operate_by,itemid,batchno, houseid,'' as customerid, if(stype='52',loss_count,count) as count,if(stype='52',total_loss,total_profit) as total,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,'storecheckdetail' as tname from storecheckdetail k "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append("select detailid,goods_number,orderid,prodrequisitionid as mainid,stype,originalbill,companyid,operate_time,operate_by,itemid,batchno,houseid,customerid, count , total,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,'prodrequisitiondetail' as tname from prodrequisitiondetail k "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append("select detailid,goods_number,orderid,otherinoutid as mainid,stype,originalbill,companyid,operate_time,operate_by,itemid,batchno,houseid,customerid,count,total ,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,'otherinoutdetail' as tname from otherinoutdetail k "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append("select detailid,goods_number,orderid,prodstorageid as mainid,stype,originalbill,companyid,operate_time,operate_by,itemid,batchno,houseid,'' as customerid,count ,total,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,'prodstoragedetail' as tname from prodstoragedetail k "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append("select detailid,goods_number,orderid,processinoutid as mainid,stype,originalbill,companyid,operate_time,operate_by,itemid,batchno,houseid,customerid,count , total,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,'processinoutdetail' as tname from processinoutdetail k "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append("select detailid,goods_number,orderid,outsourcinginid as mainid,stype,originalbill,companyid,operate_time,operate_by,itemid,batchno,houseid,customerid,count,total,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,'outsourcingin' as tname from outsourcingindetail k "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append("select detailid,goods_number,orderid,salesorderid as mainid,stype,originalbill,companyid,operate_time,operate_by,itemid,batchno,houseid,customerid,count,total,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,'saleorderdetail' as tname from salesorderdetail k "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append("select detailid,goods_number,orderid,purchaseid as mainid,stype,originalbill,companyid,operate_time,operate_by,itemid,batchno,'' as houseid,'' as customerid,count,0 as total,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,'purchasedetail' as tname from purchasedetail k "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append("select detailid,goods_number,orderid,purchaseorderid as mainid,stype,originalbill,companyid,operate_time,operate_by,itemid,batchno,houseid,customerid,count,total,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,'purchaseorderdetail' as tname from purchaseorderdetail k "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append("select k.* from ( select id as detailid,goods_number,orderid,scheduleid as mainid,stype,originalbill,companyid,order_time as operate_time,operate_by,itemid,batchno,'' as houseid, '' as customerid,order_count as count,0 as total,order_remark as remark,order_status as status,create_id,create_by,create_date as create_time,update_id,update_by,update_date as update_time,'scheduleorderdetail' as tname from t_order  k "
				+ (incondition2.equals("") ? "" : " where " + incondition2) + orderlimit + ") k " + (incondition.equals("") ? "" : " where " + incondition));
		buffer.append(") union all (");
		buffer.append("select k.* from ( select id as detailid,goods_number,billno as orderid,order_id as mainid,'1611' as stype,'' as originalbill,companyid,(select order_time from t_order tor where k.order_id=tor.id) as operate_time,create_id as operate_by,itemid,batchno,'' as houseid, '' as customerid,item_count as count,0 as total,item_remark as remark,fstatus as status,create_id,create_by,create_date as create_time,update_id,update_by,update_date as update_time,'t_order_detail' as tname from t_order_detail k "
				+ (incondition2.equals("") ? "" : " where " + incondition2) + orderlimit + ") k " + (incondition.equals("") ? "" : " where " + incondition));
		buffer.append(") union all (");
		buffer.append("select detailid,goods_number,orderid,stageoutsourcingid as mainid,stype,'' as originalbill,companyid,operate_time,operate_by,itemid,'' as batchno,'' as houseid,customerid,dstagecount as count,0 as total,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,'stageoutsourcingdetail' as tname from stageoutsourcingdetail k "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append("select detailid,goods_number,orderid,outsourcingid as mainid,stype,'' as originalbill,companyid,operate_time,operate_by,itemid,batchno,'' as houseid,customerid,count,total,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,'outsourcingdetail' as tname from outsourcingdetail k "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit);
		buffer.append(") union all (");
		buffer.append("select detailid,goods_number,orderid,lossid as mainid,stype,originalbill,companyid,operate_time,operate_by,itemid,batchno,houseid,'' as customerid,count,total,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,'reportlossdetail' as tname from reportlossdetail k "
				+ (incondition.equals("") ? "" : " where " + incondition) + orderlimit + ") ");

		return buffer;
	};

	public static JSONObject getDetailbill_all_view(JSONObject params, ActionContext context) throws SQLException, NamingException {

		Connection conn = context.getConnection(DATASOURCE);

		String companyid = params.getString("companyid");
		String incondition = params.getString("incondition");
		String outcondition = params.getString("outcondition");

		int offset = params.getInteger("offset");
		int limit = params.getInteger("limit");

		String orderBys = params.getString("orderBys");
		// String temporderBys =
		// "d.operate_time desc,d.update_time desc,d.stype desc,d.orderid desc,d.goods_number asc";
		orderBys = "operate_time desc,update_time desc,stype desc,orderid desc,goods_number asc";

		// if (orderBys.indexOf("codeid") > -1 || orderBys.indexOf("itemname") >
		// -1 || orderBys.indexOf("sformat") > -1 || orderBys.indexOf("unit") >
		// -1 || orderBys.indexOf("barcode") > -1
		// || orderBys.indexOf("property1") > -1 ||
		// orderBys.indexOf("property2") > -1 || orderBys.indexOf("property3") >
		// -1 || orderBys.indexOf("property4") > -1
		// || orderBys.indexOf("property5") > -1) {
		// orderBys = "im." + orderBys + "," + temporderBys;
		// } else if (orderBys.indexOf("classname") > -1) {
		// orderBys = "ic." + orderBys + "," + temporderBys;
		// } else if (orderBys.indexOf("customercode") > -1 ||
		// orderBys.indexOf("customername") > -1) {
		// orderBys = "c." + orderBys + "," + temporderBys;
		// } else if (orderBys.indexOf("housecode") > -1 ||
		// orderBys.indexOf("housename") > -1) {
		// orderBys = "sh." + orderBys + "," + temporderBys;
		// } else if (orderBys.indexOf("staffcode") > -1 ||
		// orderBys.indexOf("staffname") > -1) {
		// orderBys = "s." + orderBys + "," + temporderBys;
		// } else if (orderBys.indexOf("operate_time") > -1) {
		// orderBys = temporderBys;
		// } else if (orderBys.indexOf("orderid") > -1) {
		// orderBys =
		// "d.orderid desc,d.operate_time desc,d.update_time desc,d.goods_number asc";
		// } else if (orderBys.indexOf("stype") > -1) {
		// orderBys =
		// "d.stype desc,d.operate_time desc,d.update_time desc,d.orderid desc,d.goods_number asc";
		// } else if (orderBys.equals("")) {
		// orderBys =
		// "d.operate_time desc,d.stype desc,d.update_time desc,d.orderid desc,d.goods_number asc";
		// } else {
		// orderBys = "d." + orderBys + "," + temporderBys;
		// }

		String inorderby = "";// " order by " + orderBys + " limit 0," + (limit
								// + offset) + " ";

		StringBuffer manbuffer = new StringBuffer();
		StringBuffer buffer = detail_view(incondition, inorderby);

		manbuffer
				.append("select  d.*,im.codeid,im.itemname,im.sformat,im.mcode,im.classid,im.unit,im.imgurl,im.barcode,im.property1,im.property2,im.property3,im.property4,im.property5,im.unitstate1,im.unitset1,im.unitstate2,im.unitset2,im.unitstate3,im.unitset3,ifnull(ic.classname,'') as classname ,ifnull(c.customercode,'') as customercode,ifnull(c.customername,'') as customername,ifnull(sh.housecode,'') as housecode,ifnull(sh.housename,'') as housename,ifnull(s.staffcode,'') as staffcode,ifnull(s.staffname,'') as staffname from ( ");
		manbuffer.append(buffer.toString());
		manbuffer
				.append(") d left join customer c on d.customerid=c.customerid left join storehouse sh on d.houseid=sh.houseid left join staffinfo s on d.operate_by=s.staffid left join iteminfo im on d.itemid=im.itemid left join itemclass ic on im.classid=ic.classid "
						+ (outcondition.trim().equals("") ? "" : " where " + outcondition) + " order by " + orderBys + " limit " + offset + "," + limit);

		Table table = DataUtils.queryData(conn, manbuffer.toString(), null, null, null, null);
		// System.out.println("getDetailbill_all_view:" + manbuffer.toString());
		StringBuffer allbuffer = new StringBuffer();

		allbuffer.append("select count(*) from ( ");
		allbuffer.append(detail_view(incondition, "").toString());
		allbuffer
				.append(") d left join customer c on d.customerid=c.customerid left join storehouse sh on d.houseid=sh.houseid left join staffinfo s on d.operate_by=s.staffid left join iteminfo im on d.itemid=im.itemid left join itemclass ic on im.classid=ic.classid "
						+ (outcondition.trim().equals("") ? "" : " where " + outcondition));

		Object countObject = DataUtils.getValueBySQL(conn, allbuffer.toString(), null);
		long count;
		if (countObject == null) {
			count = 0;
		} else {
			count = Long.parseLong(countObject.toString());
		}

		// System.out.println("getDetailbill_all_view:" + count);

		manbuffer.setLength(0);
		buffer.setLength(0);
		allbuffer.setLength(0);

		JSONObject ret = new JSONObject();
		ret.put("table", Transform.tableToJson(table));
		ret.put("rowsize", count);

		return ret;
	}

	public static JSONObject getMainbillView(JSONObject params, ActionContext context) throws SQLException, NamingException {

		Connection conn = context.getConnection(DATASOURCE);

		String companyid = params.getString("companyid");
		String incondition = params.getString("incondition");
		String outcondition = params.getString("outcondition");
		String detailhouse = params.getString("detailhouse"); // 2020-12-09

		int offset = params.getInteger("offset");
		int limit = params.getInteger("limit");

		String orderBys = params.getString("orderBys");
		// String temporderBys =
		// "d.operate_time desc,d.update_time desc,d.bill_type desc,d.orderid desc";
		orderBys = "operate_time desc,update_time desc,bill_type desc,orderid desc";

		// if (orderBys.indexOf("customercode") > -1 ||
		// orderBys.indexOf("customername") > -1) {
		// orderBys = "c." + orderBys + "," + temporderBys;
		// } else if (orderBys.indexOf("inhousecode") > -1 ||
		// orderBys.indexOf("inhousename") > -1) {
		// orderBys = "osh." + orderBys.replaceAll("inh", "h") + "," +
		// temporderBys;
		// } else if (orderBys.indexOf("outhousecode") > -1 ||
		// orderBys.indexOf("outhousename") > -1) {
		// orderBys = "nsh." + orderBys.replaceAll("outh", "h") + "," +
		// temporderBys;
		// } else if (orderBys.indexOf("staffcode") > -1 ||
		// orderBys.indexOf("staffname") > -1) {
		// orderBys = "s." + orderBys + "," + temporderBys;
		// } else if (orderBys.equals("") || orderBys.indexOf("operate_time") >
		// -1) {
		// orderBys = temporderBys;
		// } else if (orderBys.indexOf("orderid") > -1) {
		// orderBys = "d." + orderBys + ",d.operate_time desc";
		// } else if (orderBys.indexOf("bill_type") > -1) {
		// orderBys = "d." + orderBys + ",d.operate_time desc,d.orderid desc";
		// } else {
		// orderBys = "d." + orderBys + "," + temporderBys;
		// }

		String inorderby = "";// " order by " + orderBys + " limit 0," + (limit
								// + offset) + " ";

		StringBuffer manbuffer = new StringBuffer();
		// StringBuffer buffer = main_view(incondition);
		String companysql = " detail.companyid ='" + companyid + "'";
		StringBuffer buffer = main_detailhouse_view(incondition, detailhouse, companysql, inorderby); // 2020-12-09

		manbuffer
				.append("select  d.*,ifnull(c.customercode,'') as customercode,ifnull(c.customername,'') as customername,ifnull(osh.housecode,'') as inhousecode,ifnull(osh.housename,'') as inhousename,ifnull(nsh.housecode,'') as outhousecode,ifnull(nsh.housename,'') as outhousename,ifnull(s.staffcode,'') as staffcode,ifnull(s.staffname,'') as staffname from ( ");
		manbuffer.append(buffer.toString());
		// 2020-12-1 与商品改为left join
		manbuffer
				.append(") d left join customer c on d.customerid=c.customerid left join storehouse osh on d.inhouseid=osh.houseid left join storehouse nsh on d.outhouseid=nsh.houseid left join staffinfo s on d.operate_by=s.staffid "
						+ (outcondition.trim().equals("") ? "" : " where " + outcondition) + " order by " + orderBys + " limit " + offset + "," + limit);

		// System.out.println(manbuffer.toString());
		Table table = DataUtils.queryData(conn, manbuffer.toString(), null, null, null, null);

		StringBuffer allbuffer = new StringBuffer();

		allbuffer.append("select count(*) from ( ");
		allbuffer.append(main_detailhouse_view(incondition, detailhouse, companysql, "").toString());
		// 2020-12-1 与商品改为left join
		allbuffer
				.append(") d left join customer c on d.customerid=c.customerid left join storehouse osh on d.inhouseid=osh.houseid left join storehouse nsh on d.outhouseid=nsh.houseid left join staffinfo s on d.operate_by=s.staffid "
						+ (outcondition.trim().equals("") ? "" : " where " + outcondition));

		Object countObject = DataUtils.getValueBySQL(conn, allbuffer.toString(), null);
		long count;
		if (countObject == null) {
			count = 0;
		} else {
			count = Long.parseLong(countObject.toString());
		}

		manbuffer.setLength(0);
		allbuffer.setLength(0);

		JSONObject ret = new JSONObject();
		ret.put("table", Transform.tableToJson(table));
		ret.put("rowsize", count);

		return ret;
	}

	public static JSONObject getitemdetail_all_view(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);

		String companyid = params.getString("companyid");
		String incondition = params.getString("incondition");
		String outcondition = params.getString("outcondition");
		String incondition2 = params.getString("incondition2");

		incondition2 = incondition2 == null ? " companyid='" + companyid + "' " : incondition2;

		int offset = params.getInteger("offset");
		int limit = params.getInteger("limit");

		String orderBys = params.getString("orderBys");
		// String temporderBys =
		// "detail.operate_time desc,detail.update_time desc,detail.stype desc,detail.orderid desc";
		orderBys = "operate_time desc,update_time desc,stype desc,orderid desc";

		// if (orderBys.indexOf("customercode") > -1 ||
		// orderBys.indexOf("customername") > -1) {
		// orderBys = "c." + orderBys + "," + temporderBys;
		// } else if (orderBys.indexOf("housecode") > -1 ||
		// orderBys.indexOf("housename") > -1) {
		// orderBys = "sh." + orderBys + "," + temporderBys;
		// } else if (orderBys.indexOf("staffcode") > -1 ||
		// orderBys.indexOf("staffname") > -1) {
		// orderBys = "s." + orderBys + "," + temporderBys;
		// } else if (orderBys.equals("") || orderBys.indexOf("operate_time") >
		// -1) {
		// orderBys = temporderBys;
		// } else if (orderBys.indexOf("orderid") > -1) {
		// orderBys = "detail." + orderBys +
		// ",d.operate_time desc,detail.update_time desc";
		// } else if (orderBys.indexOf("stype") > -1) {
		// orderBys = "detail." + orderBys +
		// ",d.operate_time desc,detail.update_time desc,d.orderid desc";
		// } else {
		// orderBys = "detail." + orderBys + "," + temporderBys;
		// }

		String inorderby = "";// " order by " + orderBys + " limit 0," + (limit
								// + offset) + " ";

		if (incondition.indexOf("companyid") <= -1)
			outcondition = " detail.companyid='" + companyid + "' and " + outcondition;

		StringBuffer manbuffer = new StringBuffer();
		StringBuffer buffer = itemdetail_view(incondition, incondition2, inorderby);

		manbuffer
				.append("select detail.*,im.codeid,im.itemname,im.sformat,im.mcode,im.classid,im.unit,im.imgurl,im.barcode,im.property1,im.property2,im.property3,im.property4,im.property5,im.unitstate1,im.unitset1,im.unitstate2,im.unitset2,im.unitstate3,im.unitset3,ifnull(ic.classname,'') as classname ,ifnull(c.customercode,'') as customercode,ifnull(c.customername,'') as customername,ifnull(sh.housecode,'') as housecode,ifnull(sh.housename,'') as housename from (");
		manbuffer.append(buffer.toString());
		manbuffer
				.append(") detail left join customer c on detail.customerid=c.customerid left join storehouse sh on detail.houseid=sh.houseid left join iteminfo im on im.itemid=detail.itemid  left join itemclass ic on im.classid=ic.classid  "
						+ (outcondition.trim().equals("") ? "" : " where " + outcondition) + " order by " + orderBys + " limit " + offset + "," + limit);

		// System.out.println(manbuffer.toString());
		Table table = DataUtils.queryData(conn, manbuffer.toString(), null, null, null, null);

		StringBuffer allbuffer = new StringBuffer();

		allbuffer.append("select count(*) from ( ");
		allbuffer.append(itemdetail_view(incondition, incondition2, "").toString());
		allbuffer
				.append(") detail left join customer c on detail.customerid=c.customerid left join storehouse sh on detail.houseid=sh.houseid left join iteminfo im  on im.itemid=detail.itemid left join itemclass ic on im.classid=ic.classid  "
						+ (outcondition.trim().equals("") ? "" : " where " + outcondition));

		Object countObject = DataUtils.getValueBySQL(conn, allbuffer.toString(), null);
		long count;
		if (countObject == null) {
			count = 0;
		} else {
			count = Long.parseLong(countObject.toString());
		}

		manbuffer.setLength(0);
		allbuffer.setLength(0);

		JSONObject ret = new JSONObject();
		ret.put("table", Transform.tableToJson(table));
		ret.put("rowsize", count);
		return ret;
	};

	// 修改单据详情值
	public static JSONObject changeBillDetails(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject rt = new JSONObject();
		String orderid = params.getString("orderid");
		String stype = params.getString("stype");
		String mainid = params.getString("mainid");
		String detailid = params.getString("detailid");
		String loginuserid = params.getString("loginuserid");
		String loginUser = params.getString("loginUser");
		String companyid = params.getString("companyid");
		String tablename = params.getString("tablename");
		String oldremark = params.getString("oldremark").replaceAll("'", "''");
		String newremark = params.getString("newremark").replaceAll("'", "''");
		String title = params.getString("title");
		String logid = params.getString("logid");
		String moneybit = params.getString("moneybit");
		String price = params.getString("price");
		String total = params.getString("total");
		String taxrate = params.getString("taxrate");
		double tax = params.getDoubleValue("tax");
		String taxprice = params.getString("taxprice");
		String taxmoney = params.getString("taxmoney");

		String cost = params.getString("cost");
		String money = params.getString("money");

		String countbit = params.getString("countbit");
		String pricebit = params.getString("pricebit");

		String detailname = tablename + "detail";

		String modifyreason = params.getString("modifyreason").replaceAll("'", "''");

		String message = "";

		if (moneybit == null) {
			moneybit = "2";
		}

		String state = "1";
		double yprice = 0;
		double ytotal = 0;
		double ytaxrate = 0;
		double ytax = 0;
		double ytaxprice = 0;
		double ytaxmoney = 0;

		double ycost = 0;
		double ymoney = 0;
		try {
			if (mainid.length() != 32 || (detailid != null && !detailid.equals("") && detailid.length() != 32)) {
				state = "0";
			} else {

				Statement ps = conn.createStatement();
				conn.setAutoCommit(false);

				String mainnameid = tablename + "id";

				switch (tablename){
					case "reportloss" :
						mainnameid = "lossid";
						break;
					case "schedule_pick" :
						mainnameid = "schedule_pick_id";
						detailname = "schedule_pick_detail";
						break;
					default:
						mainnameid = tablename + "id";
						detailname = tablename + "detail";
						break;
				}

				if (stype.equals("0") && (logid.equals("38") || logid.equals("39"))) {
					ps.addBatch("update " + tablename + " set originalbill='" + newremark + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where accountbillid='"
							+ mainid + "' ");
				} else if (stype.equals("1") && (logid.equals("38") || logid.equals("39"))) {
					ps.addBatch("update " + tablename + " set remark='" + newremark + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where accountbillid='"
							+ mainid + "' ");
				} else if (stype.equals("0") && (logid.equals("41") || logid.equals("42"))) {
					ps.addBatch("update " + tablename + " set originalbill='" + newremark + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where dayinoutid='"
							+ mainid + "' ");
				} else if (stype.equals("1") && (logid.equals("41") || logid.equals("42"))) {
					ps.addBatch("update " + tablename + " set remark='" + newremark + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where dayinoutid='" + mainid
							+ "' ");
				} else if (stype.equals("0") && logid.equals("74")) {
					ps.addBatch("update " + tablename + " set originalbill='" + newremark + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where customerbillid='"
							+ mainid + "' ");
				} else if (stype.equals("1") && logid.equals("74")) {
					ps.addBatch("update " + tablename + " set remark='" + newremark + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where customerbillid='"
							+ mainid + "' ");
				} else if (stype.equals("0") && logid.equals("75")) {
					ps.addBatch("update " + tablename + " set originalbill='" + newremark + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where transferid='"
							+ mainid + "' ");
				} else if (stype.equals("1") && logid.equals("75")) {
					ps.addBatch("update " + tablename + " set remark='" + newremark + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where transferid='" + mainid
							+ "' ");
				} else if (stype.equals("0")) {
					ps.addBatch("update " + tablename + " set originalbill='" + newremark + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where " + mainnameid
							+ "='" + mainid + "' ");
					ps.addBatch("update " + detailname + " set originalbill='" + newremark + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now()  where "
							+ mainnameid + "='" + mainid + "' ");

				} else if (stype.equals("1")) {
					ps.addBatch("update " + tablename + " set remark='" + newremark + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where " + mainnameid + "='"
							+ mainid + "' ");

				} else if (stype.equals("2")) {
					ps.addBatch("update " + detailname + " set remark='" + newremark + "'  where detailid='" + detailid + "' ");
					ps.addBatch("update " + tablename + " set update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where " + mainnameid + "='" + mainid + "' ");

				} else if (stype.equals("3")) {// storeout
					ps.addBatch("update " + tablename + " set currency='" + newremark + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where " + mainnameid + "='"
							+ mainid + "' ");

				} else if (stype.equals("4")) {// storeoutdetail
					ps.addBatch("update " + tablename + "detail set invoicedate=" + (newremark.equals("") ? null : "'" + newremark + "'") + "  where detailid='" + detailid + "' ");

				} else if (stype.equals("5")) {// storeoutdetail
					ps.addBatch("update " + tablename + "detail set invoicedate=" + (newremark.equals("") ? null : "'" + newremark + "'") + "  where "
							+ (detailid.equals("all") ? mainnameid + "='" + mainid + "'" : " detailid in (" + detailid + ")"));

				} else if (stype.equals("12") || stype.equals("13")) {
					String fsql = "";
					if (tablename.equals("stageoutsourcing")) {
						fsql = "select taxrate,tax,taxprice,taxmoney,dprice as price,dprocessmoney as total,itemid,'' as batchno,operate_time,customerid,'' as houseid,relationmainid,relationdetailid  from "
								+ detailname + " where detailid='" + detailid + "' and status='1'";

					} else if (tablename.equals("outsourcingin")) {
						fsql = "select price as cost,total as money,taxrate,tax,taxprice,taxmoney,processprice as price,processmoney as total,itemid,batchno,houseid,operate_time,customerid,relationmainid,relationdetailid  from "
								+ detailname + " where detailid='" + detailid + "' and status='1'";  

					} else if (tablename.equals("storeoutapply")){
						fsql = "select price,total,taxrate,tax,taxprice,taxmoney,itemid,batchno,houseid,operate_time,customerid,relationmainid,relationdetailid  from " + detailname
								+ " where detailid='" + detailid + "' "; 
					}else {
						fsql = "select price,total,taxrate,tax,taxprice,taxmoney,itemid,batchno,houseid,operate_time,customerid,relationmainid,relationdetailid  from " + detailname
								+ " where detailid='" + detailid + "' and status='1'"; 
					}
					//System.out.println("fsql:"+fsql);
					message = Pdacommon.checkInvalidDate(mainid, tablename, tablename + "id", conn);
					if (message.equals("")) {

						Table ftable = DataUtils.queryData(conn, fsql, null, null, null, null);
						if (ftable.getRows().size() > 0) {
							SimpleDateFormat sdfdate = new SimpleDateFormat("yyyy-MM-dd");

							String customerid = ftable.getRows().get(0).getString("customerid");
							String itemid = ftable.getRows().get(0).getString("itemid");
							String batchno = erpscan.save.Pdainvalid.transformSpecialInfo(ftable.getRows().get(0).getString("batchno"));
							String houseid = ftable.getRows().get(0).getString("houseid");
							yprice = Double.parseDouble(ftable.getRows().get(0).getValue("price").toString());
							ytotal = Double.parseDouble(ftable.getRows().get(0).getValue("total").toString());
							ytaxrate = Double.parseDouble(ftable.getRows().get(0).getValue("taxrate").toString());
							ytax = Double.parseDouble(ftable.getRows().get(0).getValue("tax").toString());
							ytaxprice = Double.parseDouble(ftable.getRows().get(0).getValue("taxprice").toString());
							ytaxmoney = Double.parseDouble(ftable.getRows().get(0).getValue("taxmoney").toString());
							String operate_time = sdfdate.format(ftable.getRows().get(0).getDate("operate_time"));
							String[] sdatearr = operate_time.split("-");
							int syear = Integer.parseInt(sdatearr[0]);
							int smonth = Integer.parseInt(sdatearr[1]);

							String relationdetailid = ftable.getRows().get(0).getString("relationdetailid");
							String relationmainid = ftable.getRows().get(0).getString("relationmainid");

							String sdate = sdatearr[0] + "-" + sdatearr[1] + "-01";
							if (tablename.equals("storeoutapply")) {
								//System.out.println("AAA:"+tablename);
								ps.addBatch("update storeoutapplydetail set price=" + price + ",total=" + total + ",taxrate=" + taxrate + ",tax=" + tax + ",taxprice=" + taxprice + ",taxmoney=" + taxmoney
										+ "  where detailid='" + detailid + "' ");
								ps.addBatch("update storeoutapply set total=round((select sum(sd.total) from storeoutapplydetail sd where sd.storeoutapplyid=storeoutapply.storeoutapplyid)," + moneybit
										+ "),totaltax=round((select sum(sd.tax) from storeoutapplydetail sd where sd.storeoutapplyid=storeoutapply.storeoutapplyid)," + moneybit
										+ "),totalmoney=round((select sum(sd.taxmoney) from storeoutapplydetail sd where sd.storeoutapplyid=storeoutapply.storeoutapplyid)," + moneybit
										+ ") , update_id='" + loginuserid
										+ "',update_by='" + loginUser + "',update_time=now() where storeoutapplyid='" + mainid + "' ");
							}else if (tablename.equals("stageoutsourcing")) {
								if (stype.equals("13")) {

									// 更新客户应收应付款
									ps.addBatch("update customer set payable=round(payable-"
											+ ytaxmoney
											+ "+"
											+ taxmoney
											+ ","
											+ moneybit
											+ ")"
											+ (ytax > 0 ? (tax > 0 ? ",T_payable=round(T_payable-" + ytaxmoney + "+" + taxmoney + "," + moneybit + ")" : ",T_payable=round(T_payable-" + ytaxmoney
													+ "," + moneybit + ")") : (tax > 0 ? ",T_payable=round(T_payable+" + taxmoney + "," + moneybit + ")" : "")) + " where companyid='" + companyid
											+ "' and customerid='" + customerid + "'");

									// 增加往来单位月收支报表
									ps.addBatch("insert into customermonth (monthid,companyid,customerid,sdate,syear,smonth,payable,pay_outsourcing_money,pay_add_money"
											+ (tax > 0 ? ",T_payable,T_pay_outsourcing_money,T_pay_add_money" : "")
											+ ") values('"
											+ Common.getUpperUUIDString()
											+ "','"
											+ companyid
											+ "','"
											+ customerid
											+ "','"
											+ sdate
											+ "',"
											+ syear
											+ ","
											+ smonth
											+ ","
											+ taxmoney
											+ ","
											+ taxmoney
											+ ","
											+ taxmoney
											+ (tax > 0 ? ("," + taxmoney + "," + taxmoney + "," + taxmoney) : "")
											+ ") on duplicate key update payable=round(payable-"
											+ ytaxmoney
											+ "+"
											+ taxmoney
											+ ","
											+ moneybit
											+ "),pay_outsourcing_money=round(pay_outsourcing_money-"
											+ ytaxmoney
											+ "+"
											+ taxmoney
											+ ","
											+ moneybit
											+ "),pay_add_money=round(pay_add_money-"
											+ ytaxmoney
											+ "+"
											+ taxmoney
											+ ","
											+ moneybit
											+ ")"
											+ (ytax > 0 ? (tax > 0 ? ",T_payable=round(T_payable-" + ytaxmoney + "+" + taxmoney + "," + moneybit
													+ "),T_pay_outsourcing_money=round(T_pay_outsourcing_money-" + ytaxmoney + "+" + taxmoney + "," + moneybit
													+ "),T_pay_add_money=round(T_pay_add_money-" + ytaxmoney + "+" + taxmoney + "," + moneybit + ")" : ",T_payable=round(T_payable-" + ytaxmoney + ","
													+ moneybit + "),T_pay_outsourcing_money=round(T_pay_outsourcing_money-" + ytaxmoney + "," + moneybit + "),T_pay_add_money=round(T_pay_add_money-"
													+ ytaxmoney + "," + moneybit + ")") : (tax > 0 ? ",T_payable=round(T_payable+" + taxmoney + "," + moneybit
													+ "),T_pay_outsourcing_money=round(T_pay_outsourcing_money+" + taxmoney + "," + moneybit + "),T_pay_add_money=round(T_pay_add_money+" + taxmoney
													+ "," + moneybit + ")" : "")));

									// 增加往来单位年报表
									ps.addBatch("insert into customeryear (yearid,companyid,customerid,syear,payable,pay_outsourcing_money,pay_add_money"
											+ (tax > 0 ? ",T_payable,T_pay_outsourcing_money,T_pay_add_money" : "")
											+ ") values('"
											+ Common.getUpperUUIDString()
											+ "','"
											+ companyid
											+ "','"
											+ customerid
											+ "',"
											+ syear
											+ ","
											+ taxmoney
											+ ","
											+ taxmoney
											+ ","
											+ taxmoney
											+ (tax > 0 ? ("," + taxmoney + "," + taxmoney + "," + taxmoney) : "")
											+ ") on duplicate key update payable=round(payable-"
											+ ytaxmoney
											+ "+"
											+ taxmoney
											+ ","
											+ moneybit
											+ "),pay_outsourcing_money=round(pay_outsourcing_money-"
											+ ytaxmoney
											+ "+"
											+ taxmoney
											+ ","
											+ moneybit
											+ "),pay_add_money=round(pay_add_money-"
											+ ytaxmoney
											+ "+"
											+ taxmoney
											+ ","
											+ moneybit
											+ ")"
											+ (ytax > 0 ? (tax > 0 ? ",T_payable=round(T_payable-" + ytaxmoney + "+" + taxmoney + "," + moneybit
													+ "),T_pay_outsourcing_money=round(T_pay_outsourcing_money-" + ytaxmoney + "+" + taxmoney + "," + moneybit
													+ "),T_pay_add_money=round(T_pay_add_money-" + ytaxmoney + "+" + taxmoney + "," + moneybit + ")" : ",T_payable=round(T_payable-" + ytaxmoney + ","
													+ moneybit + "),T_pay_outsourcing_money=round(T_pay_outsourcing_money-" + ytaxmoney + "," + moneybit + "),T_pay_add_money=round(T_pay_add_money-"
													+ ytaxmoney + "," + moneybit + ")") : (tax > 0 ? ",T_payable=round(T_payable+" + taxmoney + "," + moneybit
													+ "),T_pay_outsourcing_money=round(T_pay_outsourcing_money+" + taxmoney + "," + moneybit + "),T_pay_add_money=round(T_pay_add_money+" + taxmoney
													+ "," + moneybit + ")" : "")));

									if (!relationdetailid.equals("")) {
										ps.addBatch("update stageoutsourcingdetail o set o.dprocessmoneyin=round(o.dprocessmoneyin-" + ytotal + "+" + total + "," + moneybit + ")  where o.detailid='"
												+ relationdetailid + "' ");

										ps.addBatch("update stageoutsourcing set  processmoney=round(processmoney-" + ytotal + "+" + total + "," + moneybit + ") where stageoutsourcingid='"
												+ relationmainid + "'  ");
									}

								}
								ps.addBatch("update " + detailname + " set dprice=" + price + ",dprocessmoney=" + total + ",taxrate=" + taxrate + ",tax=" + tax + ",taxprice=" + taxprice
										+ ",taxmoney=" + taxmoney + " where detailid='" + detailid + "' ");
								ps.addBatch("update " + tablename + " set processmoney=round((select sum(sd.dprocessmoney) from " + detailname
										+ " sd where sd.stageoutsourcingid=stageoutsourcing.stageoutsourcingid)," + moneybit + "),totaltax=round((select sum(sd.tax) from " + detailname
										+ " sd where sd.stageoutsourcingid=stageoutsourcing.stageoutsourcingid)," + moneybit + "),totalmoney=round((select sum(sd.taxmoney) from " + detailname
										+ " sd where sd.stageoutsourcingid=stageoutsourcing.stageoutsourcingid)," + moneybit + "), update_id='" + loginuserid + "',update_by='" + loginUser
										+ "',update_time=now() where stageoutsourcingid='" + mainid + "' ");

							} else if (tablename.equals("outsourcingin")) {
								ycost = Double.parseDouble(ftable.getRows().get(0).getValue("cost").toString());
								ymoney = Double.parseDouble(ftable.getRows().get(0).getValue("money").toString());
								if (stype.equals("12")) {

									ps.addBatch("insert into stock (stockid,companyid,itemid,houseid,count,money,newcostprice,batchno) VALUES ('" + Common.getUpperUUIDString() + "','" + companyid
											+ "','" + itemid + "','" + houseid + "',0,round(-" + ymoney + "+" + money + "," + moneybit + "),0,'" + batchno
											+ "') on duplicate key update money=round(money-" + ymoney + "+" + money + "," + moneybit + "),newcostprice=round(if(count=0,newcostprice,money/count),"
											+ pricebit + ") ");

									ps.addBatch("delete from stock where itemid='" + itemid + "' and houseid='" + houseid + "' and batchno='" + batchno + "' and count=0 and money=0 ");

									ps.addBatch("update itemmonth  set money=round(money-" + ymoney + "+" + money + "," + moneybit + "),outsourcing_cost=round(outsourcing_cost-" + ymoney + "+"
											+ money + "," + moneybit + "),outsourcing_money=round(outsourcing_money-" + ytotal + "+" + total + "," + moneybit + ")  where houseid='" + houseid
											+ "' and itemid='" + itemid + "' and batchno='" + batchno + "' and sdate='" + sdate + "'");

									ps.addBatch("update ordermonth  set   outsourcingmoney=round(outsourcingmoney-" + ytotal + "+" + total + "," + moneybit + ") where companyid='" + companyid
											+ "' and itemid='" + itemid + "' and batchno='" + batchno + "' and sdate='" + sdate + "' ");

									// 更新客户应收应付款
									ps.addBatch("update customer set payable=round(payable-"
											+ ytaxmoney
											+ "+"
											+ taxmoney
											+ ","
											+ moneybit
											+ ")"
											+ (ytax > 0 ? (tax > 0 ? ",T_payable=round(T_payable-" + ytaxmoney + "+" + taxmoney + "," + moneybit + ")" : ",T_payable=round(T_payable-" + ytaxmoney
													+ "," + moneybit + ")") : (tax > 0 ? ",T_payable=round(T_payable+" + taxmoney + "," + moneybit + ")" : "")) + " where companyid='" + companyid
											+ "' and customerid='" + customerid + "'");

									// 增加往来单位月收支报表
									ps.addBatch("insert into customermonth (monthid,companyid,customerid,sdate,syear,smonth,payable,pay_outsourcing_money,pay_add_money"
											+ (tax > 0 ? ",T_payable,T_pay_outsourcing_money,T_pay_add_money" : "")
											+ ") values('"
											+ Common.getUpperUUIDString()
											+ "','"
											+ companyid
											+ "','"
											+ customerid
											+ "','"
											+ sdate
											+ "',"
											+ syear
											+ ","
											+ smonth
											+ ","
											+ taxmoney
											+ ","
											+ taxmoney
											+ ","
											+ taxmoney
											+ (tax > 0 ? ("," + taxmoney + "," + taxmoney + "," + taxmoney) : "")
											+ ") on duplicate key update payable=round(payable-"
											+ ytaxmoney
											+ "+"
											+ taxmoney
											+ ","
											+ moneybit
											+ "),pay_outsourcing_money=round(pay_outsourcing_money-"
											+ ytaxmoney
											+ "+"
											+ taxmoney
											+ ","
											+ moneybit
											+ "),pay_add_money=round(pay_add_money-"
											+ ytaxmoney
											+ "+"
											+ taxmoney
											+ ","
											+ moneybit
											+ ")"
											+ (ytax > 0 ? (tax > 0 ? ",T_payable=round(T_payable-" + ytaxmoney + "+" + taxmoney + "," + moneybit
													+ "),T_pay_outsourcing_money=round(T_pay_outsourcing_money-" + ytaxmoney + "+" + taxmoney + "," + moneybit
													+ "),T_pay_add_money=round(T_pay_add_money-" + ytaxmoney + "+" + taxmoney + "," + moneybit + ")" : ",T_payable=round(T_payable-" + ytaxmoney + ","
													+ moneybit + "),T_pay_outsourcing_money=round(T_pay_outsourcing_money-" + ytaxmoney + "," + moneybit + "),T_pay_add_money=round(T_pay_add_money-"
													+ ytaxmoney + "," + moneybit + ")") : (tax > 0 ? ",T_payable=round(T_payable+" + taxmoney + "," + moneybit
													+ "),T_pay_outsourcing_money=round(T_pay_outsourcing_money+" + taxmoney + "," + moneybit + "),T_pay_add_money=round(T_pay_add_money+" + taxmoney
													+ "," + moneybit + ")" : "")));

									// 增加往来单位年报表
									ps.addBatch("insert into customeryear (yearid,companyid,customerid,syear,payable,pay_outsourcing_money,pay_add_money"
											+ (tax > 0 ? ",T_payable,T_pay_outsourcing_money,T_pay_add_money" : "")
											+ ") values('"
											+ Common.getUpperUUIDString()
											+ "','"
											+ companyid
											+ "','"
											+ customerid
											+ "',"
											+ syear
											+ ","
											+ taxmoney
											+ ","
											+ taxmoney
											+ ","
											+ taxmoney
											+ (tax > 0 ? ("," + taxmoney + "," + taxmoney + "," + taxmoney) : "")
											+ ") on duplicate key update payable=round(payable-"
											+ ytaxmoney
											+ "+"
											+ taxmoney
											+ ","
											+ moneybit
											+ "),pay_outsourcing_money=round(pay_outsourcing_money-"
											+ ytaxmoney
											+ "+"
											+ taxmoney
											+ ","
											+ moneybit
											+ "),pay_add_money=round(pay_add_money-"
											+ ytaxmoney
											+ "+"
											+ taxmoney
											+ ","
											+ moneybit
											+ ")"
											+ (ytax > 0 ? (tax > 0 ? ",T_payable=round(T_payable-" + ytaxmoney + "+" + taxmoney + "," + moneybit
													+ "),T_pay_outsourcing_money=round(T_pay_outsourcing_money-" + ytaxmoney + "+" + taxmoney + "," + moneybit
													+ "),T_pay_add_money=round(T_pay_add_money-" + ytaxmoney + "+" + taxmoney + "," + moneybit + ")" : ",T_payable=round(T_payable-" + ytaxmoney + ","
													+ moneybit + "),T_pay_outsourcing_money=round(T_pay_outsourcing_money-" + ytaxmoney + "," + moneybit + "),T_pay_add_money=round(T_pay_add_money-"
													+ ytaxmoney + "," + moneybit + ")") : (tax > 0 ? ",T_payable=round(T_payable+" + taxmoney + "," + moneybit
													+ "),T_pay_outsourcing_money=round(T_pay_outsourcing_money+" + taxmoney + "," + moneybit + "),T_pay_add_money=round(T_pay_add_money+" + taxmoney
													+ "," + moneybit + ")" : "")));

									if (!relationdetailid.equals("")) {
										ps.addBatch("update outsourcingdetail o set o.actualtotal=round(o.actualtotal-" + ytotal + "+" + total + "," + moneybit + ") where o.detailid='"
												+ relationdetailid + "' and status='1'");

										ps.addBatch("update outsourcing set  actualtotal=round(actualtotal-" + ytotal + "+" + total + "," + moneybit + ") where outsourcingid='" + relationmainid
												+ "' and status='1'");
									}

									ps.addBatch("update " + detailname + " set price=" + cost + ",total=" + money + ",processprice=" + price + ",processmoney=" + total + ",taxrate=" + taxrate
											+ ",tax=" + tax + ",taxprice=" + taxprice + ",taxmoney=" + taxmoney + " where detailid='" + detailid + "' ");
									ps.addBatch("update " + tablename + " set total=round((select sum(sd.total) from " + detailname + " sd where sd.outsourcinginid=outsourcingin.outsourcinginid),"
											+ moneybit + "),processmoney=round((select sum(sd.processmoney) from " + detailname + " sd where sd.outsourcinginid=outsourcingin.outsourcinginid),"
											+ moneybit + "),totaltax=round((select sum(sd.tax) from " + detailname + " sd where sd.outsourcinginid=outsourcingin.outsourcinginid)," + moneybit
											+ "),totalmoney=round((select sum(sd.taxmoney) from " + detailname + " sd where sd.outsourcinginid=outsourcingin.outsourcinginid)," + moneybit
											+ "), update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where outsourcinginid='" + mainid + "' ");

								} else if (stype.equals("13")) {

									ps.addBatch("update itemmonth  set  outsourcing_money=round(outsourcing_money-" + ytotal + "+" + total + "," + moneybit + ") where houseid='" + houseid
											+ "' and itemid='" + itemid + "' and batchno='" + batchno + "' and sdate='" + sdate + "' ");

									ps.addBatch("update customer set payable=round(payable+"
											+ ytaxmoney
											+ "-"
											+ taxmoney
											+ ","
											+ moneybit
											+ ")"
											+ (ytax > 0 ? (tax > 0 ? ",T_payable=round(T_payable+" + ytaxmoney + "-" + taxmoney + "," + moneybit + ")" : ",T_payable=round(T_payable+" + ytaxmoney
													+ "," + moneybit + ")") : (tax > 0 ? ",T_payable=round(T_payable-" + taxmoney + "," + moneybit + ")" : "")) + " where companyid='" + companyid
											+ "' and customerid='" + customerid + "'");

									// 增加往来单位月收支报表
									ps.addBatch("insert into customermonth (monthid,companyid,customerid,sdate,syear,smonth,payable,pay_outsourcing_money,pay_add_money"
											+ (tax > 0 ? ",T_payable,T_pay_outsourcing_money,T_pay_add_money" : "")
											+ ") values('"
											+ Common.getUpperUUIDString()
											+ "','"
											+ companyid
											+ "','"
											+ customerid
											+ "','"
											+ sdate
											+ "',"
											+ syear
											+ ","
											+ smonth
											+ ",-"
											+ taxmoney
											+ ",-"
											+ taxmoney
											+ ",-"
											+ taxmoney
											+ (tax > 0 ? (",-" + taxmoney + ",-" + taxmoney + ",-" + taxmoney) : "")
											+ ") on duplicate key update  payable=round(payable+"
											+ ytaxmoney
											+ "-"
											+ taxmoney
											+ ","
											+ moneybit
											+ "),pay_outsourcing_money=round(pay_outsourcing_money+"
											+ ytaxmoney
											+ "-"
											+ taxmoney
											+ ","
											+ moneybit
											+ "),pay_add_money=round(pay_add_money+"
											+ ytaxmoney
											+ "-"
											+ taxmoney
											+ ","
											+ moneybit
											+ ")"
											+ (ytax > 0 ? (tax > 0 ? ",T_payable=round(T_payable+" + ytaxmoney + "-" + taxmoney + "," + moneybit
													+ "),T_pay_outsourcing_money=round(T_pay_outsourcing_money+" + ytaxmoney + "-" + taxmoney + "," + moneybit
													+ "),T_pay_add_money=round(T_pay_add_money+" + ytaxmoney + "-" + taxmoney + "," + moneybit + ")" : ",T_payable=round(T_payable+" + ytaxmoney + ","
													+ moneybit + "),T_pay_outsourcing_money=round(T_pay_outsourcing_money+" + ytaxmoney + "," + moneybit + "),T_pay_add_money=round(T_pay_add_money+"
													+ ytaxmoney + "," + moneybit + ")") : (tax > 0 ? ",T_payable=round(T_payable-" + taxmoney + "," + moneybit
													+ "),T_pay_outsourcing_money=round(T_pay_outsourcing_money-" + taxmoney + "," + moneybit + "),T_pay_add_money=round(T_pay_add_money-" + taxmoney
													+ "," + moneybit + ")" : "")));

									// 增加往来单位年报表
									ps.addBatch("insert into customeryear (yearid,companyid,customerid,syear,payable,pay_outsourcing_money,pay_add_money"
											+ (tax > 0 ? ",T_payable,T_pay_outsourcing_money,T_pay_add_money" : "")
											+ ") values('"
											+ Common.getUpperUUIDString()
											+ "','"
											+ companyid
											+ "','"
											+ customerid
											+ "',"
											+ syear
											+ ",-"
											+ taxmoney
											+ ",-"
											+ taxmoney
											+ ",-"
											+ taxmoney
											+ (tax > 0 ? (",-" + taxmoney + ",-" + taxmoney + ",-" + taxmoney) : "")
											+ ") on duplicate key update  payable=round(payable+"
											+ ytaxmoney
											+ "-"
											+ taxmoney
											+ ","
											+ moneybit
											+ "),pay_outsourcing_money=round(pay_outsourcing_money+"
											+ ytaxmoney
											+ "-"
											+ taxmoney
											+ ","
											+ moneybit
											+ "),pay_add_money=round(pay_add_money+"
											+ ytaxmoney
											+ "-"
											+ taxmoney
											+ ","
											+ moneybit
											+ ")"
											+ (ytax > 0 ? (tax > 0 ? ",T_payable=round(T_payable+" + ytaxmoney + "-" + taxmoney + "," + moneybit
													+ "),T_pay_outsourcing_money=round(T_pay_outsourcing_money+" + ytaxmoney + "-" + taxmoney + "," + moneybit
													+ "),T_pay_add_money=round(T_pay_add_money+" + ytaxmoney + "-" + taxmoney + "," + moneybit + ")" : ",T_payable=round(T_payable+" + ytaxmoney + ","
													+ moneybit + "),T_pay_outsourcing_money=round(T_pay_outsourcing_money+" + ytaxmoney + "," + moneybit + "),T_pay_add_money=round(T_pay_add_money+"
													+ ytaxmoney + "," + moneybit + ")") : (tax > 0 ? ",T_payable=round(T_payable-" + taxmoney + "," + moneybit
													+ "),T_pay_outsourcing_money=round(T_pay_outsourcing_money-" + taxmoney + "," + moneybit + "),T_pay_add_money=round(T_pay_add_money-" + taxmoney
													+ "," + moneybit + ")" : "")));

									ps.addBatch("update ordermonth  set   outsourcingmoney=round(outsourcingmoney+" + ytotal + "-" + total + "," + moneybit + ") where companyid='" + companyid
											+ "' and itemid='" + itemid + "' and batchno='" + batchno + "' and sdate='" + sdate + "' ");

									if (!relationdetailid.equals("")) {
										ps.addBatch("update outsourcingdetail o set o.actualtotal=round(o.actualtotal+" + ytotal + "-" + total + "," + moneybit + ")  where o.detailid='"
												+ relationdetailid + "' and status='1'");

										ps.addBatch("update outsourcing set  actualtotal=round(actualtotal+" + ytotal + "-" + total + "," + moneybit + ") where outsourcingid='" + relationmainid
												+ "' and status='1'");

									}

									ps.addBatch("update " + detailname + " set processprice=" + price + ",processmoney=" + total + ",taxrate=" + taxrate + ",tax=" + tax + ",taxprice=" + taxprice
											+ ",taxmoney=" + taxmoney + " where detailid='" + detailid + "' ");
									ps.addBatch("update " + tablename + " set processmoney=round((select sum(sd.processmoney) from " + detailname
											+ " sd where sd.outsourcinginid=outsourcingin.outsourcinginid)," + moneybit + "),totaltax=round((select sum(sd.tax) from " + detailname
											+ " sd where sd.outsourcinginid=outsourcingin.outsourcinginid)," + moneybit + "),totalmoney=round((select sum(sd.taxmoney) from " + detailname
											+ " sd where sd.outsourcinginid=outsourcingin.outsourcinginid)," + moneybit + "), update_id='" + loginuserid + "',update_by='" + loginUser
											+ "',update_time=now() where outsourcinginid='" + mainid + "' ");

								}
							} else if (detailname.equals("storeoutdetail")) {
								if (stype.equals("12")) {

									ps.addBatch("update itemmonth set sellmoney=round(sellmoney-" + ytotal + "+" + total + "," + moneybit + ")  where itemid='" + itemid + "' and houseid='" + houseid
											+ "' and batchno='" + batchno + "' and sdate='" + sdate + "'");

									ps.addBatch("update ordermonth set salesoutmoney=round(salesoutmoney-" + ytotal + "+" + total + "," + moneybit + ")  where itemid='" + itemid + "' and  batchno='"
											+ batchno + "'  and sdate='" + sdate + "'");

									ps.addBatch("update customer set receivable=round(receivable-"
											+ ytaxmoney
											+ "+"
											+ taxmoney
											+ ","
											+ moneybit
											+ ")"
											+ (ytax > 0 ? (tax > 0 ? ",T_receivable=round(T_receivable-" + ytaxmoney + "+" + taxmoney + "," + moneybit + ")" : ",T_receivable=round(T_receivable-"
													+ ytaxmoney + "," + moneybit + ")") : (tax > 0 ? ",T_receivable=round(T_receivable+" + taxmoney + "," + moneybit + ")" : ""))
											+ " where  customerid='" + customerid + "'");
									ps.addBatch("insert into customermonth (monthid,companyid,customerid,sdate,syear,smonth,receivable,rec_sellout_money,rec_add_money"
											+ (tax > 0 ? ",T_receivable,T_rec_sellout_money,T_rec_add_money" : "")
											+ ") values('"
											+ Common.getUpperUUIDString()
											+ "','"
											+ companyid
											+ "','"
											+ customerid
											+ "','"
											+ sdate
											+ "',"
											+ syear
											+ ","
											+ smonth
											+ ","
											+ taxmoney
											+ ","
											+ taxmoney
											+ ","
											+ taxmoney
											+ (tax > 0 ? "," + taxmoney + "," + taxmoney + "," + taxmoney : "")
											+ ") on duplicate key update receivable=round(receivable-"
											+ ytaxmoney
											+ "+"
											+ taxmoney
											+ ","
											+ moneybit
											+ "),rec_sellout_money=round(rec_sellout_money-"
											+ ytaxmoney
											+ "+"
											+ taxmoney
											+ ","
											+ moneybit
											+ "),rec_add_money=round(rec_add_money-"
											+ ytaxmoney
											+ "+"
											+ taxmoney
											+ ","
											+ moneybit
											+ ")"
											+ (ytax > 0 ? (tax > 0 ? ",T_receivable=round(T_receivable-" + ytaxmoney + "+" + taxmoney + "," + moneybit
													+ "),T_rec_sellout_money=round(T_rec_sellout_money-" + ytaxmoney + "+" + taxmoney + "," + moneybit + "),T_rec_add_money=round(T_rec_add_money-"
													+ ytaxmoney + "+" + taxmoney + "," + moneybit + ")" : ",T_receivable=round(T_receivable-" + ytaxmoney + "," + moneybit
													+ "),T_rec_sellout_money=round(T_rec_sellout_money-" + ytaxmoney + "," + moneybit + "),T_rec_add_money=round(T_rec_add_money-" + ytaxmoney + ","
													+ moneybit + ")") : (tax > 0 ? ",T_receivable=round(T_receivable+" + taxmoney + "," + moneybit + "),T_rec_sellout_money=round(T_rec_sellout_money+"
													+ taxmoney + "," + moneybit + "),T_rec_add_money=round(T_rec_add_money+" + taxmoney + "," + moneybit + ")" : "")));
									ps.addBatch("insert into customeryear (yearid,companyid,customerid,syear,receivable,rec_sellout_money,rec_add_money"
											+ (tax > 0 ? ",T_receivable,T_rec_sellout_money,T_rec_add_money" : "")
											+ ") values('"
											+ Common.getUpperUUIDString()
											+ "','"
											+ companyid
											+ "','"
											+ customerid
											+ "',"
											+ syear
											+ ","
											+ taxmoney
											+ ","
											+ taxmoney
											+ ","
											+ taxmoney
											+ (tax > 0 ? "," + taxmoney + "," + taxmoney + "," + taxmoney : "")
											+ ") on duplicate key update receivable=round(receivable-"
											+ ytaxmoney
											+ "+"
											+ taxmoney
											+ ","
											+ moneybit
											+ "),rec_sellout_money=round(rec_sellout_money-"
											+ ytaxmoney
											+ "+"
											+ taxmoney
											+ ","
											+ moneybit
											+ "),rec_add_money=round(rec_add_money-"
											+ ytaxmoney
											+ "+"
											+ taxmoney
											+ ","
											+ moneybit
											+ ")"
											+ (ytax > 0 ? (tax > 0 ? ",T_receivable=round(T_receivable-" + ytaxmoney + "+" + taxmoney + "," + moneybit
													+ "),T_rec_sellout_money=round(T_rec_sellout_money-" + ytaxmoney + "+" + taxmoney + "," + moneybit + "),T_rec_add_money=round(T_rec_add_money-"
													+ ytaxmoney + "+" + taxmoney + "," + moneybit + ")" : ",T_receivable=round(T_receivable-" + ytaxmoney + "," + moneybit
													+ "),T_rec_sellout_money=round(T_rec_sellout_money-" + ytaxmoney + "," + moneybit + "),T_rec_add_money=round(T_rec_add_money-" + ytaxmoney + ","
													+ moneybit + ")") : (tax > 0 ? ",T_receivable=round(T_receivable+" + taxmoney + "," + moneybit + "),T_rec_sellout_money=round(T_rec_sellout_money+"
													+ taxmoney + "," + moneybit + "),T_rec_add_money=round(T_rec_add_money+" + taxmoney + "," + moneybit + ")" : "")));

									ps.addBatch("update ordermonth  set  salesoutmoney=round(salesoutmoney-" + ytotal + "+" + total + "," + moneybit + ") where  itemid='" + itemid + "' and batchno='"
											+ batchno + "' and sdate='" + sdate + "' ");

									if (!relationdetailid.equals("")) {
										ps.addBatch("update salesorderdetail set outtotal=round(outtotal-" + ytotal + "+" + total + "," + moneybit + ") where detailid='" + relationdetailid + "' ");
										ps.addBatch("update salesorder set  outtotal=round(outtotal-" + ytotal + "+" + total + "," + moneybit + ")" + " where salesorderid='" + relationmainid + "'");

									}

								} else if (stype.equals("13")) {

									ps.addBatch("update itemmonth set t_sellmoney=round(t_sellmoney-" + ytotal + "+" + total + "," + moneybit + ")" + "  where houseid='" + houseid + "' and itemid='"
											+ itemid + "' and batchno='" + batchno + "' and sdate='" + sdate + "'  ");

									ps.addBatch("update customer set receivable=round(receivable+"
											+ ytaxmoney
											+ "-"
											+ taxmoney
											+ ","
											+ moneybit
											+ ")"
											+ (ytax > 0 ? (tax > 0 ? ",T_receivable=round(T_receivable+" + ytaxmoney + "-" + taxmoney + "," + moneybit + ")" : ",T_receivable=round(T_receivable+"
													+ ytaxmoney + "," + moneybit + ")") : (tax > 0 ? ",T_receivable=round(T_receivable-" + taxmoney + "," + moneybit + ")" : ""))
											+ " where companyid='" + companyid + "' and customerid='" + customerid + "'");

									// 增加往来单位月收支报表
									ps.addBatch("insert into customermonth (monthid,companyid,customerid,sdate,syear,smonth,receivable,rec_sellin_money,rec_add_money"
											+ (tax > 0 ? ",T_receivable,T_rec_sellin_money,T_rec_add_money" : "")
											+ ") values('"
											+ Common.getUpperUUIDString()
											+ "','"
											+ companyid
											+ "','"
											+ customerid
											+ "','"
											+ sdate
											+ "',"
											+ syear
											+ ","
											+ smonth
											+ ",-"
											+ taxmoney
											+ ","
											+ taxmoney
											+ ",-"
											+ taxmoney
											+ (tax > 0 ? ",-" + taxmoney + "," + taxmoney + ",-" + taxmoney : "")
											+ ") on duplicate key update receivable=round(receivable+"
											+ ytaxmoney
											+ "-"
											+ taxmoney
											+ ","
											+ moneybit
											+ "),rec_sellin_money=round(rec_sellin_money-"
											+ ytaxmoney
											+ "+"
											+ taxmoney
											+ ","
											+ moneybit
											+ "),rec_add_money=round(rec_add_money+"
											+ ytaxmoney
											+ "-"
											+ taxmoney
											+ ","
											+ moneybit
											+ ")"
											+ (ytax > 0 ? (tax > 0 ? ",T_receivable=round(T_receivable+" + ytaxmoney + "-" + taxmoney + "," + moneybit
													+ "),T_rec_sellin_money=round(T_rec_sellin_money-" + ytaxmoney + "+" + taxmoney + "," + moneybit + "),T_rec_add_money=round(T_rec_add_money+"
													+ ytaxmoney + "-" + taxmoney + "," + moneybit + ")" : ",T_receivable=round(T_receivable+" + ytaxmoney + "," + moneybit
													+ "),T_rec_sellin_money=round(T_rec_sellin_money-" + ytaxmoney + "," + moneybit + "),T_rec_add_money=round(T_rec_add_money+" + ytaxmoney + ","
													+ moneybit + ")") : (tax > 0 ? ",T_receivable=round(T_receivable-" + taxmoney + "," + moneybit + "),T_rec_sellin_money=round(T_rec_sellin_money+"
													+ taxmoney + "," + moneybit + "),T_rec_add_money=round(T_rec_add_money-" + taxmoney + "," + moneybit + ")" : "")));

									ps.addBatch("insert into customeryear (yearid,companyid,customerid,syear,receivable,rec_sellin_money,rec_add_money"
											+ (tax > 0 ? ",T_receivable,T_rec_sellin_money,T_rec_add_money" : "")
											+ ") values('"
											+ Common.getUpperUUIDString()
											+ "','"
											+ companyid
											+ "','"
											+ customerid
											+ "',"
											+ syear
											+ ",-"
											+ taxmoney
											+ ","
											+ taxmoney
											+ ",-"
											+ taxmoney
											+ (tax > 0 ? ",-" + taxmoney + "," + taxmoney + ",-" + taxmoney : "")
											+ ") on duplicate key update receivable=round(receivable+"
											+ ytaxmoney
											+ "-"
											+ taxmoney
											+ ","
											+ moneybit
											+ "),rec_sellin_money=round(rec_sellin_money-"
											+ ytaxmoney
											+ "+"
											+ taxmoney
											+ ","
											+ moneybit
											+ "),rec_add_money=round(rec_add_money+"
											+ ytaxmoney
											+ "-"
											+ taxmoney
											+ ","
											+ moneybit
											+ ")"
											+ (ytax > 0 ? (tax > 0 ? ",T_receivable=round(T_receivable+" + ytaxmoney + "-" + taxmoney + "," + moneybit
													+ "),T_rec_sellin_money=round(T_rec_sellin_money-" + ytaxmoney + "+" + taxmoney + "," + moneybit + "),T_rec_add_money=round(T_rec_add_money+"
													+ ytaxmoney + "-" + taxmoney + "," + moneybit + ")" : ",T_receivable=round(T_receivable+" + ytaxmoney + "," + moneybit
													+ "),T_rec_sellin_money=round(T_rec_sellin_money-" + ytaxmoney + "," + moneybit + "),T_rec_add_money=round(T_rec_add_money+" + ytaxmoney + ","
													+ moneybit + ")") : (tax > 0 ? ",T_receivable=round(T_receivable-" + taxmoney + "," + moneybit + "),T_rec_sellin_money=round(T_rec_sellin_money+"
													+ taxmoney + "," + moneybit + "),T_rec_add_money=round(T_rec_add_money-" + taxmoney + "," + moneybit + ")" : "")));

									ps.addBatch("update ordermonth  set  salesinmoney=round(salesinmoney-" + ytotal + "+" + total + "," + moneybit + ") where  itemid='" + itemid + "' and batchno='"
											+ batchno + "' and sdate='" + sdate + "' ");

									if (!relationdetailid.equals("")) {
										ps.addBatch("update salesorderdetail set outtotal=round(outtotal+" + ytotal + "-" + total + "," + moneybit + ") where detailid='" + relationdetailid + "' ");
										ps.addBatch("update salesorder set  outtotal=round(outtotal+" + ytotal + "-" + total + "," + moneybit + ")" + " where salesorderid='" + relationmainid + "'");

									}

								}

								ps.addBatch("update storeoutdetail set price=" + price + ",total=" + total + ",taxrate=" + taxrate + ",tax=" + tax + ",taxprice=" + taxprice + ",taxmoney=" + taxmoney
										+ ",profit=round(total-cost_money," + moneybit + "),profit_rate=if(profit>0 and total>0,round(profit / total,4),0) where detailid='" + detailid + "' ");
								ps.addBatch("update storeout set total=round((select sum(sd.total) from storeoutdetail sd where sd.storeoutid=storeout.storeoutid)," + moneybit
										+ "),totaltax=round((select sum(sd.tax) from storeoutdetail sd where sd.storeoutid=storeout.storeoutid)," + moneybit
										+ "),totalmoney=round((select sum(sd.taxmoney) from storeoutdetail sd where sd.storeoutid=storeout.storeoutid)," + moneybit
										+ "),profit=round(total-cost_money," + moneybit + "),profit_rate=if(profit>0 and total>0,round(profit / total,4),0), update_id='" + loginuserid
										+ "',update_by='" + loginUser + "',update_time=now() where storeoutid='" + mainid + "' ");
							} else if (detailname.equals("storeindetail")) {
								if (stype.equals("12")) {

									ps.addBatch("insert into stock (stockid,companyid,itemid,houseid,count,money,newcostprice,batchno) VALUES ('" + Common.getUpperUUIDString() + "','" + companyid
											+ "','" + itemid + "','" + houseid + "',0,round(-" + ytotal + "+" + total + "," + moneybit + ") ,0,'" + batchno
											+ "') on duplicate key update money=round(money-" + ytotal + "+" + total + "," + moneybit + "),newcostprice=round(if(count=0,newcostprice,money/count),"
											+ pricebit + ") ");

									ps.addBatch("delete from stock where itemid='" + itemid + "' and houseid='" + houseid + "' and batchno='" + batchno + "' and count=0 and money=0 ");

									// ps.addBatch("update stock set money=round(money-"
									// + ytotal + "+" + total + "," + moneybit +
									// ")"
									// +
									// ",newcostprice=round(if(count=0,newcostprice,money/count),"
									// + pricebit
									// + ") where itemid='" + itemid +
									// "' and houseid='"
									// + houseid + "' and batchno='" + batchno +
									// "'");

									ps.addBatch("update itemmonth  set money=round(money-" + ytotal + "+" + total + "," + moneybit + "),totalmoney=round(totalmoney-" + ytotal + "+" + total + ","
											+ moneybit + ")  where houseid='" + houseid + "' and itemid='" + itemid + "' and batchno='" + batchno + "' and sdate='" + sdate + "'");

									// 更新客户应收应付款
									ps.addBatch("update customer set payable=round(payable-"
											+ ytaxmoney
											+ "+"
											+ taxmoney
											+ ","
											+ moneybit
											+ ")"
											+ (ytax > 0 ? (tax > 0 ? ",T_payable=round(T_payable-" + ytaxmoney + "+" + taxmoney + "," + moneybit + ")" : ",T_payable=round(T_payable-" + ytaxmoney
													+ "," + moneybit + ")") : (tax > 0 ? ",T_payable=round(T_payable+" + taxmoney + "," + moneybit + ")" : "")) + " where companyid='" + companyid
											+ "' and customerid='" + customerid + "'");

									// 增加往来单位月收支报表
									ps.addBatch("insert into customermonth (monthid,companyid,customerid,sdate,syear,smonth,payable,pay_purchasein_money,pay_add_money"
											+ (tax > 0 ? ",T_payable,T_pay_purchasein_money,T_pay_add_money" : "")
											+ ") values('"
											+ Common.getUpperUUIDString()
											+ "','"
											+ companyid
											+ "','"
											+ customerid
											+ "','"
											+ sdate
											+ "',"
											+ syear
											+ ","
											+ smonth
											+ ","
											+ taxmoney
											+ ","
											+ taxmoney
											+ ","
											+ taxmoney
											+ (tax > 0 ? ("," + taxmoney + "," + taxmoney + "," + taxmoney) : "")
											+ ") on duplicate key update payable=round(payable-"
											+ ytaxmoney
											+ "+"
											+ taxmoney
											+ ","
											+ moneybit
											+ "),pay_purchasein_money=round(pay_purchasein_money-"
											+ ytaxmoney
											+ "+"
											+ taxmoney
											+ ","
											+ moneybit
											+ "),pay_add_money=round(pay_add_money-"
											+ ytaxmoney
											+ "+"
											+ taxmoney
											+ ","
											+ moneybit
											+ ")"
											+ (ytax > 0 ? (tax > 0 ? ",T_payable=round(T_payable-" + ytaxmoney + "+" + taxmoney + "," + moneybit
													+ "),T_pay_purchasein_money=round(T_pay_purchasein_money-" + ytaxmoney + "+" + taxmoney + "," + moneybit
													+ "),T_pay_add_money=round(T_pay_add_money-" + ytaxmoney + "+" + taxmoney + "," + moneybit + ")" : ",T_payable=round(T_payable-" + ytaxmoney + ","
													+ moneybit + "),T_pay_purchasein_money=round(T_pay_purchasein_money-" + ytaxmoney + "," + moneybit + "),T_pay_add_money=round(T_pay_add_money-"
													+ ytaxmoney + "," + moneybit + ")") : (tax > 0 ? ",T_payable=round(T_payable+" + taxmoney + "," + moneybit
													+ "),T_pay_purchasein_money=round(T_pay_purchasein_money+" + taxmoney + "," + moneybit + "),T_pay_add_money=round(T_pay_add_money+" + taxmoney
													+ "," + moneybit + ")" : "")));

									// 增加往来单位年报表
									ps.addBatch("insert into customeryear (yearid,companyid,customerid,syear,payable,pay_purchasein_money,pay_add_money"
											+ (tax > 0 ? ",T_payable,T_pay_purchasein_money,T_pay_add_money" : "")
											+ ") values('"
											+ Common.getUpperUUIDString()
											+ "','"
											+ companyid
											+ "','"
											+ customerid
											+ "',"
											+ syear
											+ ","
											+ taxmoney
											+ ","
											+ taxmoney
											+ ","
											+ taxmoney
											+ (tax > 0 ? ("," + taxmoney + "," + taxmoney + "," + taxmoney) : "")
											+ ") on duplicate key update payable=round(payable-"
											+ ytaxmoney
											+ "+"
											+ taxmoney
											+ ","
											+ moneybit
											+ "),pay_purchasein_money=round(pay_purchasein_money-"
											+ ytaxmoney
											+ "+"
											+ taxmoney
											+ ","
											+ moneybit
											+ "),pay_add_money=round(pay_add_money-"
											+ ytaxmoney
											+ "+"
											+ taxmoney
											+ ","
											+ moneybit
											+ ")"
											+ (ytax > 0 ? (tax > 0 ? ",T_payable=round(T_payable-" + ytaxmoney + "+" + taxmoney + "," + moneybit
													+ "),T_pay_purchasein_money=round(T_pay_purchasein_money-" + ytaxmoney + "+" + taxmoney + "," + moneybit
													+ "),T_pay_add_money=round(T_pay_add_money-" + ytaxmoney + "+" + taxmoney + "," + moneybit + ")" : ",T_payable=round(T_payable-" + ytaxmoney + ","
													+ moneybit + "),T_pay_purchasein_money=round(T_pay_purchasein_money-" + ytaxmoney + "," + moneybit + "),T_pay_add_money=round(T_pay_add_money-"
													+ ytaxmoney + "," + moneybit + ")") : (tax > 0 ? ",T_payable=round(T_payable+" + taxmoney + "," + moneybit
													+ "),T_pay_purchasein_money=round(T_pay_purchasein_money+" + taxmoney + "," + moneybit + "),T_pay_add_money=round(T_pay_add_money+" + taxmoney
													+ "," + moneybit + ")" : "")));

									ps.addBatch("update ordermonth  set  purchaseinmoney=round(purchaseinmoney-" + ytotal + "+" + total + "," + moneybit + ") where  itemid='" + itemid
											+ "' and batchno='" + batchno + "' and sdate='" + sdate + "' ");

									if (!relationdetailid.equals("")) {
										ps.addBatch("update purchaseorderdetail set intotal=round(intotal-" + ytotal + "+" + total + "," + moneybit + ") where detailid='" + relationdetailid + "' ");
										ps.addBatch("update purchaseorder set  intotal=round(intotal-" + ytotal + "+" + total + "," + moneybit + ")" + " where purchaseorderid='" + relationmainid
												+ "'");

									}

								} else if (stype.equals("13")) {
									// ps.addBatch("update stock set  money=round(money+"
									// + ytotal + "-" + total + "," + moneybit +
									// ") ,newcostprice=round(if(count=0,newcostprice,money/count),"
									// + pricebit
									// + ") where itemid='" + itemid +
									// "' and houseid='"
									// + houseid + "' and batchno='" + batchno +
									// "' ");

									ps.addBatch("insert into stock (stockid,companyid,itemid,houseid,count,money,newcostprice,batchno) VALUES ('" + Common.getUpperUUIDString() + "','" + companyid
											+ "','" + itemid + "','" + houseid + "',0, round(" + ytotal + "-" + total + "," + moneybit + "),0,'" + batchno
											+ "') on duplicate key update money=round(money+" + ytotal + "-" + total + "," + moneybit + "),newcostprice=round(if(count=0,newcostprice,money/count),"
											+ pricebit + ") ");

									ps.addBatch("delete from stock where itemid='" + itemid + "' and houseid='" + houseid + "' and batchno='" + batchno + "' and count=0 and money=0 ");

									ps.addBatch("update itemmonth  set  money=round(money+" + ytotal + "-" + total + "," + moneybit + "),  t_totalmoney=round(t_totalmoney-" + ytotal + "+" + total
											+ "," + moneybit + ")" + " where houseid='" + houseid + "' and itemid='" + itemid + "' and batchno='" + batchno + "' and sdate='" + sdate + "' ");

									// 更新客户应收应付款
									ps.addBatch("update customer set payable=round(payable+"
											+ ytaxmoney
											+ "-"
											+ taxmoney
											+ ","
											+ moneybit
											+ ")"
											+ (ytax > 0 ? (tax > 0 ? ",T_payable=round(T_payable+" + ytaxmoney + "-" + taxmoney + "," + moneybit + ")" : ",T_payable=round(T_payable+" + ytaxmoney
													+ "," + moneybit + ")") : (tax > 0 ? ",T_payable=round(T_payable-" + taxmoney + "," + moneybit + ")" : "")) + " where companyid='" + companyid
											+ "' and customerid='" + customerid + "'");

									// 增加往来单位月收支报表
									ps.addBatch("insert into customermonth (monthid,companyid,customerid,sdate,syear,smonth,payable,pay_purchaseout_money,pay_add_money"
											+ (tax > 0 ? ",T_payable,T_pay_purchaseout_money,T_pay_add_money" : "")
											+ ") values('"
											+ Common.getUpperUUIDString()
											+ "','"
											+ companyid
											+ "','"
											+ customerid
											+ "','"
											+ sdate
											+ "',"
											+ syear
											+ ","
											+ smonth
											+ ",-"
											+ taxmoney
											+ ","
											+ taxmoney
											+ ",-"
											+ taxmoney
											+ (tax > 0 ? (",-" + taxmoney + "," + taxmoney + ",-" + taxmoney) : "")
											+ ") on duplicate key update payable=round(payable+"
											+ ytaxmoney
											+ "-"
											+ taxmoney
											+ ","
											+ moneybit
											+ "),pay_purchaseout_money=round(pay_purchaseout_money-"
											+ ytaxmoney
											+ "+"
											+ taxmoney
											+ ","
											+ moneybit
											+ "),pay_add_money=round(pay_add_money+"
											+ ytaxmoney
											+ "-"
											+ taxmoney
											+ ","
											+ moneybit
											+ ")"
											+ (ytax > 0 ? (tax > 0 ? ",T_payable=round(T_payable+" + ytaxmoney + "-" + taxmoney + "," + moneybit
													+ "),T_pay_purchaseout_money=round(T_pay_purchaseout_money-" + ytaxmoney + "+" + taxmoney + "," + moneybit
													+ "),T_pay_add_money=round(T_pay_add_money+" + ytaxmoney + "-" + taxmoney + "," + moneybit + ")" : ",T_payable=round(T_payable+" + ytaxmoney + ","
													+ moneybit + "),T_pay_purchaseout_money=round(T_pay_purchaseout_money-" + ytaxmoney + "," + moneybit + "),T_pay_add_money=round(T_pay_add_money+"
													+ ytaxmoney + "," + moneybit + ")") : (tax > 0 ? ",T_payable=round(T_payable-" + taxmoney + "," + moneybit
													+ "),T_pay_purchaseout_money=round(T_pay_purchaseout_money+" + taxmoney + "," + moneybit + "),T_pay_add_money=round(T_pay_add_money-" + taxmoney
													+ "," + moneybit + ")" : "")));

									// 增加往来单位年报表
									ps.addBatch("insert into customeryear (yearid,companyid,customerid,syear,payable,pay_purchaseout_money,pay_add_money"
											+ (tax > 0 ? ",T_payable,T_pay_purchaseout_money,T_pay_add_money" : "")
											+ ") values('"
											+ Common.getUpperUUIDString()
											+ "','"
											+ companyid
											+ "','"
											+ customerid
											+ "',"
											+ syear
											+ ",-"
											+ taxmoney
											+ ","
											+ taxmoney
											+ ",-"
											+ taxmoney
											+ (tax > 0 ? (",-" + taxmoney + "," + taxmoney + ",-" + taxmoney) : "")
											+ ") on duplicate key update payable=round(payable+"
											+ ytaxmoney
											+ "-"
											+ taxmoney
											+ ","
											+ moneybit
											+ "),pay_purchaseout_money=round(pay_purchaseout_money-"
											+ ytaxmoney
											+ "+"
											+ taxmoney
											+ ","
											+ moneybit
											+ "),pay_add_money=round(pay_add_money+"
											+ ytaxmoney
											+ "-"
											+ taxmoney
											+ ","
											+ moneybit
											+ ")"
											+ (ytax > 0 ? (tax > 0 ? ",T_payable=round(T_payable+" + ytaxmoney + "-" + taxmoney + "," + moneybit
													+ "),T_pay_purchaseout_money=round(T_pay_purchaseout_money-" + ytaxmoney + "+" + taxmoney + "," + moneybit
													+ "),T_pay_add_money=round(T_pay_add_money+" + ytaxmoney + "-" + taxmoney + "," + moneybit + ")" : ",T_payable=round(T_payable+" + ytaxmoney + ","
													+ moneybit + "),T_pay_purchaseout_money=round(T_pay_purchaseout_money-" + ytaxmoney + "," + moneybit + "),T_pay_add_money=round(T_pay_add_money+"
													+ ytaxmoney + "," + moneybit + ")") : (tax > 0 ? ",T_payable=round(T_payable-" + taxmoney + "," + moneybit
													+ "),T_pay_purchaseout_money=round(T_pay_purchaseout_money+" + taxmoney + "," + moneybit + "),T_pay_add_money=round(T_pay_add_money-" + taxmoney
													+ "," + moneybit + ")" : "")));

									ps.addBatch("update ordermonth  set  purchaseoutmoney=round(purchaseoutmoney-" + ytotal + "+" + total + "," + moneybit + ") where  itemid='" + itemid
											+ "' and batchno='" + batchno + "' and sdate='" + sdate + "' ");

									if (!relationdetailid.equals("")) {
										ps.addBatch("update purchaseorderdetail set intotal=round(intotal+" + ytotal + "-" + total + "," + moneybit + ") where detailid='" + relationdetailid + "' ");
										ps.addBatch("update purchaseorder set  intotal=round(intotal+" + ytotal + "-" + total + "," + moneybit + ")" + " where purchaseorderid='" + relationmainid
												+ "'");

									}

								}

								ps.addBatch("update " + detailname + " set price=" + price + ",total=" + total + ",taxrate=" + taxrate + ",tax=" + tax + ",taxprice=" + taxprice + ",taxmoney="
										+ taxmoney + " where detailid='" + detailid + "' ");
								ps.addBatch("update " + tablename + " set total=round((select sum(sd.total) from " + detailname + " sd where sd.storeinid=storein.storeinid)," + moneybit
										+ "),totaltax=round((select sum(sd.tax) from " + detailname + " sd where sd.storeinid=storein.storeinid)," + moneybit
										+ "),totalmoney=round((select sum(sd.taxmoney) from " + detailname + " sd where sd.storeinid=storein.storeinid)," + moneybit + "), update_id='" + loginuserid
										+ "',update_by='" + loginUser + "',update_time=now() where storeinid='" + mainid + "' ");

							}

						} else {
							state = "0";
						}
					} else {
						state = "0";
					}
				}
				//System.out.println("state:"+state);
				if (state.equals("1")) {
					if (stype.equals("12") || stype.equals("13")) {
						if (logid.equals("46")) {
							ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString()
									+ "','" + companyid + "','" + logid + "','修改','" + mainid + "','单据编号：" + orderid + " " + title + "原[加工单价：" + yprice + " 加工费:" + ytotal + " 税率(%):" + ytaxrate
									+ " 税额:" + ytax + " 含税单价:" + ytaxprice + " 价税合计:" + ytaxmoney + " 成本单价：" + ycost + " 成本金额:" + ymoney + "]变更为[加工单价：" + price + " 加工费:" + total + " 税率(%):" + taxrate
									+ " 税额:" + tax + " 含税单价:" + taxprice + " 价税合计:" + taxmoney + " 成本单价：" + cost + " 成本金额:" + money + "]修改原因：" + modifyreason + "','" + loginuserid + "','" + loginUser
									+ "',now())");

						} else if (logid.equals("75") || logid.equals("76") || logid.equals("77")) {
							ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString()
									+ "','" + companyid + "','" + logid + "','修改','" + mainid + "','单据编号：" + orderid + " " + title + "原[加工单价：" + yprice + " 加工费:" + ytotal + " 税率(%):" + ytaxrate
									+ " 税额:" + ytax + " 含税单价:" + ytaxprice + " 价税合计:" + ytaxmoney + "]变更为[加工单价：" + price + " 加工费:" + total + " 税率(%):" + taxrate + " 税额:" + tax + " 含税单价:" + taxprice
									+ " 价税合计:" + taxmoney + "]修改原因：" + modifyreason + "','" + loginuserid + "','" + loginUser + "',now())");

						} else {
							ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString()
									+ "','" + companyid + "','" + logid + "','修改','" + mainid + "','单据编号：" + orderid + " " + title + "原[单价：" + yprice + " 金额:" + ytotal + " 税率(%):" + ytaxrate + " 税额:"
									+ ytax + " 含税单价:" + ytaxprice + " 价税合计:" + ytaxmoney + "]变更为[单价：" + price + " 金额:" + total + " 税率(%):" + taxrate + " 税额:" + tax + " 含税单价:" + taxprice + " 价税合计:"
									+ taxmoney + "]修改原因：" + modifyreason + "','" + loginuserid + "','" + loginUser + "',now())");
						}
					} else if (stype.equals("5")) {
						ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString()
								+ "','" + companyid + "','" + logid + "','修改','" + mainid + "','单据编号：" + orderid + " " + title + oldremark + "批量变更为[" + newremark + "]','" + loginuserid + "','"
								+ loginUser + "',now())");
					} else {
						ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString()
								+ "','" + companyid + "','" + logid + "','修改','" + mainid + "','单据编号：" + orderid + " " + title + "原[" + oldremark + "]变更为[" + newremark + "]','" + loginuserid + "','"
								+ loginUser + "',now())");
					}

					ps.executeBatch();
					conn.commit();
					conn.setAutoCommit(true);

					if (stype.equals("2")) {
						if (",11,17,25,47,46,24,13,15,14,".indexOf("," + logid + ",") > -1) {
							String usql = getstoreremarkSql(conn, logid, detailid, tablename);
							if (!usql.equals("")) {
								ps.execute(usql);
							}
						}
					}
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
			state = "0";
			try {
				conn.rollback();
			} catch (Exception e1) {
				 e1.printStackTrace();
			}
		} finally {
			conn.close();
		}
		rt.put("message", message);
		rt.put("state", state);
		return rt;
	}

	public static JSONObject getStoreItemRemark(JSONObject params, ActionContext context) throws SQLException, NamingException {

		Connection conn = context.getConnection(DATASOURCE);

		String houseid = params.getString("houseid");
		String itemid = params.getString("itemid");
		String batchno = params.getString("batchno");

		String remark = getstoreremark(conn, houseid, itemid, batchno);

		JSONObject ret = new JSONObject();
		ret.put("remark", remark);

		return ret;
	}

	public static String getstoreremarkSql(Connection conn, String logid, String detailid, String tablename) {
		String updatesql = "";

		String psql = "";

		if (tablename.equals("storemove")) {
			psql = "select sc.* from " + tablename + "detail d,stock sc where d.detailid='" + detailid + "' and d.itemid=sc.itemid and  d.newhouseid=sc.houseid and  d.batchno=sc.batchno ";

		} else {
			psql = "select sc.* from " + tablename + "detail d,stock sc where d.detailid='" + detailid + "' and d.itemid=sc.itemid and  d.houseid=sc.houseid and  d.batchno=sc.batchno ";
		}

		Table table = DataUtils.queryData(conn, psql, null, null, null, null);
		if (table.getRows().size() > 0) {
			Row info = table.getRows().get(0);
			String stockid = info.getString("stockid");
			String itemid = info.getString("itemid");
			String houseid = info.getString("houseid");
			String batchno = erpscan.save.Pdainvalid.transformSpecialInfo(info.getString("batchno"));

			String incondition = " itemid='" + itemid + "' and houseid='" + houseid + "' and batchno='" + batchno + "' and status='1' and remark<>'' ";
			String movecondition = " itemid='" + itemid + "' and newhouseid='" + houseid + "' and batchno='" + batchno + "' and status='1' and remark<>'' ";
			String orderby = " order by create_time desc limit 1 ";

			StringBuffer buffer = new StringBuffer();
			buffer.append("select  d.remark, d.create_time,tname from (");

			buffer.append(" (select  remark,create_time,'storeindetail' as tname from storeindetail" + (incondition.equals("") ? "" : " where " + incondition) + " and stype='11' " + orderby);

			buffer.append(") union all ");
			buffer.append(" (select   remark, create_time ,'itembegindetail' as tname from itembegindetail" + (incondition.equals("") ? "" : " where " + incondition) + orderby);

			buffer.append(") union all ");
			buffer.append(" (select  remark,create_time, 'storeoutdetail' as tname from storeoutdetail" + (incondition.equals("") ? "" : " where " + incondition) + " and stype='71' " + orderby);

			buffer.append(") union all ");
			buffer.append(" (select  remark,create_time,'storemovedetail' as tname from storemovedetail" + (movecondition.equals("") ? "" : " where " + movecondition) + orderby);

			buffer.append(") union all ");
			buffer.append(" (select  remark,create_time,'splitsdetail' as tname from splitsdetail" + (incondition.equals("") ? "" : " where " + incondition) + " and stype='32' " + orderby);

			buffer.append(") union all ");
			buffer.append(" (select  remark,create_time,'storecheckdetail' as tname from storecheckdetail" + (incondition.equals("") ? "" : " where " + incondition) + orderby);

			buffer.append(") union all ");

			buffer.append(" (select  remark, create_time, 'prodrequisitiondetail' as tname from prodrequisitiondetail" + (incondition.equals("") ? "" : " where " + incondition) + " and stype='261' "
					+ orderby);

			buffer.append(") union all ");
			buffer.append(" (select  remark, create_time,'otherinoutdetail' as tname from otherinoutdetail" + (incondition.equals("") ? "" : " where " + incondition) + " and stype='121' " + orderby);

			buffer.append(") union all ");
			buffer.append(" (select  remark, create_time,'prodstoragedetail' as tname from prodstoragedetail" + (incondition.equals("") ? "" : " where " + incondition) + orderby);

			buffer.append(") union all ");
			buffer.append(" (select  remark, create_time,'processinoutdetail' as tname from processinoutdetail" + (incondition.equals("") ? "" : " where " + incondition) + " and stype='241' "
					+ orderby);

			buffer.append(") union all ");
			buffer.append(" (select  remark, create_time,'outsourcingin' as tname from outsourcingindetail" + (incondition.equals("") ? "" : " where " + incondition) + " and stype='251' " + orderby);

			buffer.append(")) d  order by d.create_time desc limit 1");

			Table ftable = DataUtils.queryData(conn, buffer.toString(), null, null, null, null);
			String remark = "";
			if (ftable.getRows().size() > 0) {
				remark = erpscan.save.Pdainvalid.transformSpecialInfo(ftable.getRows().get(0).getString("remark"));

				updatesql = "update stock set stockremark ='" + remark + "' where stockid='" + stockid + "'";
			}
			buffer.setLength(0);
			buffer = null;

		}

		return updatesql;
	};

	public static String getstoreremark(Connection conn, String houseid, String itemid, String batchno) {
		String incondition = " itemid='" + itemid + "' and houseid='" + houseid + "' and batchno='" + batchno + "' and status='1' and remark<>'' ";
		String movecondition = " itemid='" + itemid + "' and newhouseid='" + houseid + "' and batchno='" + batchno + "' and status='1' and remark<>'' ";
		String orderby = " order by create_time desc limit 1 ";

		StringBuffer buffer = new StringBuffer();
		buffer.append("select  d.remark, d.create_time from (");
		buffer.append(" (select   remark, create_time ,'itembegindetail' as tname from itembegindetail" + (incondition.equals("") ? "" : " where " + incondition) + orderby);
		buffer.append(") union all ");
		buffer.append(" (select  remark,create_time,'storeindetail' as tname from storeindetail" + (incondition.equals("") ? "" : " where " + incondition) + " and stype='11' " + orderby);
		buffer.append(") union all ");
		buffer.append(" (select  remark,create_time, 'storeoutdetail' as tname from storeoutdetail" + (incondition.equals("") ? "" : " where " + incondition) + " and stype='71' " + orderby);
		buffer.append(") union all ");
		buffer.append(" (select  remark,create_time,'storemovedetail' as tname from storemovedetail" + (movecondition.equals("") ? "" : " where " + movecondition) + orderby);
		buffer.append(") union all ");
		buffer.append(" (select  remark,create_time,'splitsdetail' as tname from splitsdetail" + (incondition.equals("") ? "" : " where " + incondition) + " and stype='32' " + orderby);
		buffer.append(") union all ");
		buffer.append(" (select  remark,create_time,'storecheckdetail' as tname from storecheckdetail" + (incondition.equals("") ? "" : " where " + incondition) + orderby);
		buffer.append(") union all ");
		buffer.append(" (select  remark, create_time, 'prodrequisitiondetail' as tname from prodrequisitiondetail" + (incondition.equals("") ? "" : " where " + incondition) + " and stype='261' "
				+ orderby);
		buffer.append(") union all ");
		buffer.append(" (select  remark, create_time,'otherinoutdetail' as tname from otherinoutdetail" + (incondition.equals("") ? "" : " where " + incondition) + " and stype='121' " + orderby);
		buffer.append(") union all ");
		buffer.append(" (select  remark, create_time,'prodstoragedetail' as tname from prodstoragedetail" + (incondition.equals("") ? "" : " where " + incondition) + orderby);
		buffer.append(") union all ");
		buffer.append(" (select  remark, create_time,'processinoutdetail' as tname from processinoutdetail" + (incondition.equals("") ? "" : " where " + incondition) + " and stype='241' " + orderby);
		buffer.append(") union all ");
		buffer.append(" (select  remark, create_time,'outsourcingin' as tname from outsourcingindetail" + (incondition.equals("") ? "" : " where " + incondition) + " and stype='251' " + orderby);
		buffer.append(")) d  order by d.create_time desc limit 1");

		// System.out.println(buffer.toString());

		Table table = DataUtils.queryData(conn, buffer.toString(), null, null, null, null);
		String remark = "";
		if (table.getRows().size() > 0) {
			remark = table.getRows().get(0).getString("remark");
		}

		buffer.setLength(0);
		buffer = null;

		return remark;
	};
}