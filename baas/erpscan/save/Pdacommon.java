package erpscan.save;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.naming.NamingException;

import erpscan.Common;

import com.alibaba.fastjson.JSONObject;
import com.justep.baas.action.ActionContext;
import com.justep.baas.data.DataUtils;
import com.justep.baas.data.Table;
import com.justep.baas.data.Transform;

public class Pdacommon {
	private static final String DATASOURCE = Common.DATASOURCE;

	public static JSONObject getIDBytablename(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject rt = new JSONObject();
		String tablename = params.getString("tablename");
		String identifier = params.getString("identifier");
		String companyid = params.getString("companyid");

		// System.out.println("getIDBytablename tablename" + tablename);
		String codeid = "";
		try {
			codeid = getCodeByparams(companyid, tablename, identifier, conn);
			rt.put("codeid", codeid);
		} catch (Exception e) {
			rt.put("codeid", codeid);
		} finally {
			conn.close();
		}
		return rt;
	}

	// 获取单据前缀
	public static String getCodeByparams(String companyid, String tablename, String identifier, Connection conn) {
		String codeid = "";
		String prex = "";
		int maxcount = 99;
		String format = "%03d";
		int idminlength = 5;
		if (tablename.equals("staffinfo")) {
			prex = "YG";
			maxcount = 999;
			format = "%04d";
			idminlength = 6;
		} else if (tablename.equals("customer")) {
			prex = "DW";
			maxcount = 999;
			format = "%04d";
			idminlength = 6;
		} else if (tablename.equals("iteminfo")) {
			prex = "XP";
			maxcount = 9999;
			format = "%05d";
			idminlength = 7;
		} else if (tablename.equals("storehouse")) {
			prex = "CK";
			maxcount = 99;
			format = "%03d";
			idminlength = 5;
		} else if (tablename.equals("storetemplate")) {// 增加合同模板ID
			prex = "MB";
			maxcount = 999;
			format = "%03d";
			idminlength = 5;
		}
		if (!prex.equals("")) {

			String sql = "select max(cast(SUBSTRING(" + identifier + "," + (prex.length() + 1) + ",LENGTH(" + identifier + ")-2) AS SIGNED)) from " + tablename + " where  companyid='" + companyid
					+ "' and  SUBSTRING(" + identifier + ",1," + prex.length() + ")='" + prex + "'  and (SUBSTRING(" + identifier + "," + (prex.length() + 1) + ",LENGTH(" + identifier
					+ ")-2) REGEXP '[^0-9]') = 0  and length(" + identifier + ")>=" + idminlength;

			Object cobject = DataUtils.getValueBySQL(conn, sql, null);
			int count;
			if (cobject == null) {
				count = 1;
			} else {
				count = Integer.parseInt(cobject.toString()) + 1;
			}

			if (count > maxcount) {
				codeid = prex + count;
			} else {
				codeid = prex + String.format(format, count);
			}
		}

		return codeid;
	}

	// 更新默认值设置
	public static JSONObject updateDefaultValue(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject rt = new JSONObject();
		String tablename = params.getString("tablename");
		String id = params.getString("id");
		String companyid = params.getString("companyid");

		// System.out.println("updateDefaultValue tablename " + tablename);
		String state = "";
		try {
			String defaultcolname = "";
			String idname = "";
			if (tablename.equals("itemclass")) {
				defaultcolname = "defaultclass";
				idname = "classid";
			} else if (tablename.equals("storehouse")) {
				defaultcolname = "defaulthouse";
				idname = "houseid";
			}
			if (!defaultcolname.equals("")) {
				Statement ps = conn.createStatement();
				ps.addBatch("update " + tablename + " set " + defaultcolname + "=0 where companyid='" + companyid + "'");
				ps.addBatch("update " + tablename + " set " + defaultcolname + "=1 where " + idname + "='" + id + "'");
				ps.executeBatch();
				state = "1";
			} else {
				state = "0";
			}

		} catch (Exception e) {
			state = "0";
		} finally {
			conn.close();
		}
		rt.put("state", state);
		return rt;
	}

	// 2019-08-01 获取商品数量 暂无用
	public static JSONObject getselectallitem(JSONObject params, ActionContext context) throws SQLException, NamingException, java.sql.SQLException {
		String companyid = params.getString("companyid");
		String condition = params.getString("condition");

		JSONObject ret = new JSONObject();
		int offset = params.getInteger("offset");
		int limit = params.getInteger("limit");
		int type = params.getInteger("type");

		Table table = null;
		Connection conn = context.getConnection(DATASOURCE);
		String sql = "";
		try {

			if (type == 1) {
				sql = "select i.itemid,i.codeid,i.itemname,i.sformat,i.mcode,i.classid, ifnull(cs.classname,'') as classname, i.imgurl,i.inprice,i.barcode,i.property1,i.property2,i.property3,i.property4,i.property5,i.outprice,i.outprice1,i.outprice2,i.outprice3,i.outprice4,i.outprice5, sh.houseid,sh.housecode,sh.housename,k.batchno, sum(k.count) as count, sum(k.money) as money,sum(k.newcostprice) as newcostprice,sum(k.checkout_count) as checkout_count from ("
						+ " select s.itemid,s.houseid,s.batchno,s.count,s.money,s.newcostprice,s.checkout_count from stock s where s.companyid='"
						+ companyid
						+ "' "
						+ " union all "
						+ " select im.itemid,sh.houseid,'' as batchno,0 as count,0 as money,0 as newcostprice,0 as checkout_count from iteminfo im,storehouse sh where im.companyid=sh.companyid and im.companyid='"
						+ companyid
						+ "' "
						+ " ) k, iteminfo i left join itemclass cs on i.classid=cs.classid,storehouse sh where k.itemid=i.itemid and sh.houseid=k.houseid  "
						+ (condition.equals("") ? "" : " and " + condition) + " group by i.itemid,sh.houseid,k.batchno order by i.codeid asc,k.batchno asc limit " + offset + "," + limit + "";

				// System.out.println(sql);
				table = DataUtils.queryData(conn, sql, null, null, null, null);
				ret.put("data", Transform.tableToJson(table));
				if (offset == 0) {
					sql = "select k.itemid, k.houseid,k.batchno, sum(k.count) as count from ("
							+ " select s.itemid,s.houseid,s.batchno,s.count,s.money,s.newcostprice,s.checkout_count from stock s where s.companyid='"
							+ companyid
							+ "' "
							+ " union all "
							+ " select im.itemid,sh.houseid,'' as batchno,0 as count,0 as money,0 as newcostprice,0 as checkout_count from iteminfo im,storehouse sh where im.companyid=sh.companyid and im.companyid='"
							+ companyid + "' " + " ) k, iteminfo i,storehouse sh where k.itemid=i.itemid and sh.houseid=k.houseid  " + (condition.equals("") ? "" : " and " + condition)
							+ " group by k.itemid,k.houseid,k.batchno ";
					// System.out.println(sql);
					table = DataUtils.queryData(conn, sql, null, null, null, null);
					ret.put("rowsize", table.getRows().size());
				}

			} else {

				sql = "select i.*,ifnull(cs.classname,'') as classname ,sh.houseid ,sh.housename,s.batchno,ifnull(s.count,0) as count,ifnull(s.money,0) as money,ifnull(s.newcostprice,0) as newcostprice,ifnull(s.checkout_count,0) as checkout_count  from iteminfo i left join itemclass cs on i.classid=cs.classid ,stock s left join storehouse sh on "
						+ " s.houseid=sh.houseid where i.itemid = s.itemid and i.companyid=s.companyid and i.companyid='"
						+ companyid
						+ "' "
						+ (condition.equals("") ? "" : " and " + condition)
						+ "  order by i.codeid asc,s.batchno  limit " + offset + "," + limit + "";
				// System.out.println(sql);
				table = DataUtils.queryData(conn, sql, null, null, null, null);
				ret.put("data", Transform.tableToJson(table));

				if (offset == 0) {
					sql = "select count(i.itemid)  from iteminfo i left join itemclass cs on i.classid=cs.classid ,stock s left join storehouse sh on "
							+ " s.houseid=sh.houseid where i.itemid = s.itemid  and i.companyid=s.companyid and i.companyid='" + companyid + "' " + (condition.equals("") ? "" : " and " + condition);
					// System.out.println(sql);
					Object countObject = DataUtils.getValueBySQL(conn, sql, null);
					int count;
					if (countObject == null) {
						count = 0;
					} else {
						count = Integer.parseInt(countObject.toString());
					}
					ret.put("rowsize", count);
				}
			}

			return ret;
		} finally {
			conn.close();
		}

	}

	public static JSONObject getSellNotoutcount(JSONObject params, ActionContext context) throws SQLException, NamingException, java.sql.SQLException {
		String relationdetailid = params.getString("relationdetailid");
		String update_time = params.getString("update_time");
		int countbit = params.getInteger("countbit");
		JSONObject ret = new JSONObject();

		Connection conn = context.getConnection(DATASOURCE);
		double notoutcount = 0.0;
		String originalbill = "";

		String sql = "select   max(ifnull(sd.originalbill,'')) as s_originalbill, round(max(ifnull(sd.count,0))-ifnull(sum(if(s.stype='21',s.count,-s.count)),0)," + countbit
				+ ") as notoutcout from storeoutdetail s left join salesorderdetail sd on sd.detailid=s.relationdetailid where " + " ((s.relationdetailid='" + relationdetailid
				+ "' and s.stype='21') or ((select count(*) from storeoutdetail sd where sd.relationdetailid='" + relationdetailid
				+ "' and sd.detailid=s.returndetailid)>0 and s.stype='71' )) and s.status<>'2' and s.update_time<='" + update_time + "'";

		// System.out.println(sql);
		Table table = DataUtils.queryData(conn, sql, null, null, null, null);
		if (table.getRows().size() == 1) {
			notoutcount = table.getRows().get(0).getValue("notoutcout") == null ? 0 : Double.parseDouble(table.getRows().get(0).getValue("notoutcout").toString());
			originalbill = table.getRows().get(0).getString("s_originalbill") == null ? "" : table.getRows().get(0).getString("s_originalbill");
		}
		ret.put("originalbill", originalbill);
		ret.put("notoutcount", notoutcount);
		return ret;

	}

	public static JSONObject getSaleOriginalbill(JSONObject params, ActionContext context) throws SQLException, NamingException, java.sql.SQLException {
		String relationdetailid = params.getString("relationdetailid");
		JSONObject ret = new JSONObject();

		Connection conn = context.getConnection(DATASOURCE);
		String originalbill = "";

		String sql = "select  ifnull(sd.originalbill,'') as s_originalbill from salesorderdetail sd where sd.detailid='" + relationdetailid + "'";

		Table table = DataUtils.queryData(conn, sql, null, null, null, null);
		if (table.getRows().size() == 1) {
			originalbill = table.getRows().get(0).getString("s_originalbill") == null ? "" : table.getRows().get(0).getString("s_originalbill");
		}
		ret.put("originalbill", originalbill);
		return ret;

	}

	public static JSONObject getStaffname(JSONObject params, ActionContext context) throws SQLException, NamingException, java.sql.SQLException {
		String staffid = params.getString("staffid");
		JSONObject ret = new JSONObject();

		Connection conn = context.getConnection(DATASOURCE);
		String staffname = "";

		String sql = "select  ifnull(staffname,'') as staffname from staffinfo   where staffid='" + staffid + "'";

		Table table = DataUtils.queryData(conn, sql, null, null, null, null);
		if (table.getRows().size() == 1) {
			staffname = table.getRows().get(0).getString("staffname") == null ? "" : table.getRows().get(0).getString("staffname");
		}
		ret.put("staffname", staffname);
		return ret;

	}

	// 根据不同条件查询库存总数量
	public static JSONObject getitemstore(JSONObject params, ActionContext context) throws SQLException, NamingException, java.sql.SQLException {
		String companyid = params.getString("companyid");
		String itemid = params.getString("itemid");
		String houseid = params.getString("houseid");
		String batchno = (params.getString("batchno") == null ? null : erpscan.save.Pdainvalid.transformSpecialInfo(params.getString("batchno")));
		Integer countbit = params.getInteger("countbit");
		JSONObject ret = new JSONObject();

		String houselist = params.getString("houselist");

		Connection conn = context.getConnection(DATASOURCE);

		countbit = (countbit == null ? 2 : countbit);

		String sql = "select round(ifnull(sum(count-checkout_count),0)," + countbit + ") as storecount from stock where companyid='" + companyid + "' "
				+ ((houseid != null && !houseid.equals("")) ? " and houseid='" + houseid + "'" : "") + ((itemid != null && !itemid.equals("")) ? " and itemid='" + itemid + "'" : "")
				+ (batchno != null ? " and batchno='" + batchno + "'" : "") + ((houselist != null && !houselist.equals("")) ? " and FIND_IN_SET(houseid,'" + houselist + "')<=0 " : "");

		double storecount = 0.0;
		Table table = DataUtils.queryData(conn, sql, null, null, null, null);
		if (table.getRows().size() == 1) {
			storecount = table.getRows().get(0).getValue("storecount") == null ? 0 : Double.parseDouble(table.getRows().get(0).getValue("storecount").toString());
		}
		ret.put("storecount", storecount);
		return ret;

	}

	// 查询商品单价
	public static JSONObject getitemPrice(JSONObject params, ActionContext context) throws SQLException, NamingException, java.sql.SQLException {

		String itemid = params.getString("itemid");
		Integer pricebit = params.getInteger("pricebit");
		String stype = params.getString("stype"); // 1-成本单产 2-商品设置进货价
		JSONObject ret = new JSONObject();

		Connection conn = context.getConnection(DATASOURCE);

		double price = 0.0;
		Table table = null;

		String sql = "";
		if (stype.equals("1")) {
			sql = "select round(if(sum(count)>0,(sum(money)/sum(count)),0)," + pricebit + ") as price from stock where  itemid='" + itemid + "' ";

			table = DataUtils.queryData(conn, sql, null, null, null, null);
			if (table.getRows().size() == 1) {
				price = table.getRows().get(0).getValue("price") == null ? 0 : Double.parseDouble(table.getRows().get(0).getValue("price").toString());
			}
			if (price == 0) {
				sql = "select round( money / count," + pricebit + ") as price  from itemmonth where  itemid='" + itemid + "' and  count<>0 and  money<>0  order by  sdate desc limit 1";
				table = DataUtils.queryData(conn, sql, null, null, null, null);
				if (table.getRows().size() == 1) {
					price = table.getRows().get(0).getValue("price") == null ? 0 : Double.parseDouble(table.getRows().get(0).getValue("price").toString());
				}
			}

			if (price < 0)
				price = -price;

		} else if (stype.equals("2")) {
			sql = "select inprice as price from iteminfo where  itemid='" + itemid + "'";

			table = DataUtils.queryData(conn, sql, null, null, null, null);
			if (table.getRows().size() == 1) {
				price = table.getRows().get(0).getValue("price") == null ? 0 : Double.parseDouble(table.getRows().get(0).getValue("price").toString());
			}
		}
		// System.out.println(sql);

		ret.put("price", price);
		return ret;
	}

	public static String getselltype(String selltype) {
		switch (selltype) {
		case "0":
			return "零售单价";
		case "1":
			return "一级销售单价";
		case "2":
			return "二级销售单价";
		case "3":
			return "三级销售单价";
		case "4":
			return "四级销售单价";
		case "5":
			return "五级销售单价";
		default:
			return "";
		}
	};

	public static String getnature(String nature) {
		switch (nature) {
		case "1":
			return "国有企业";
		case "2":
			return "集体企业";
		case "3":
			return "联营企业";
		case "4":
			return "股份合作制企业";
		case "5":
			return "私营企业";
		case "6":
			return "个体户";
		case "7":
			return "合伙企业";
		case "8":
			return "有限责任公司";
		case "9":
			return "股份有限公司";
		case "10":
			return "其他性质";
		default:
			return "";
		}
	};

	public static String getrole(String role) {
		switch (role) {
		case "1":
			return "供应商";
		case "2":
			return "供应商_客户";
		case "3":
			return "客户";
		case "4":
			return "本单位部门";
		case "5":
			return "加工单位";
		case "6":
			return "供应_加工";
		case "7":
			return "供应_加工_客户";
		case "8":
			return "加工_客户";
		default:
			return "";
		}
	};

	public static String getSate(String state) {
		switch (state) {
		case "1":
			return "启用";
		case "2":
			return "停用";
		default:
			return "启用";
		}
	};

	public static String getCheckStype(String stype) {
		switch (stype) {
		case "0":
			return "无盈亏";
		case "1":
			return "有盈亏";
		default:
			return "";
		}
	};

	public static String getCheckDetailStype(String stype) {
		switch (stype) {
		case "51":
			return "盘盈";
		case "52":
			return "盘亏";
		case "53":
			return "无盈亏";
		default:
			return "";
		}
	};

	public static String getsplitsDetailStype(String stype) {
		switch (stype) {
		case "31":
			return "原商品清单";
		case "32":
			return "新商品清单";
		default:
			return "";
		}
	};

	public static String getStatus(String state) {
		switch (state) {
		case "0":
			return "已暂存";
		case "1":
			return "已记帐";
		case "2":
			return "已作废";
		case "3":
			return "待出库";
		default:
			return "";
		}
	};

	public static String getAuditStatus(String state) {
		switch (state) {
		case "3":
			return "审批中";
		case "0":
			return "未审核";
		case "1":
			return "已审核";
		case "2":
			return "已作废";
		default:
			return "未审核";
		}
	};

	public static String getT_OrderStatus(int state) {
		switch (state) {
		case 3:
			return "审批中";
		case 0:
			return "未审核";
		case 1:
			return "已审核";
		case 2:
			return "已作废";
		default:
			return "未审核";
		}
	};

	public static String getSaleStatus(String state) {
		switch (state) {
		case "0":
			return "未审核";
		case "1":
			return "已出货";
		case "3":
			return "已审核";
		case "2":
			return "已作废";
		default:
			return "未审核";
		}
	};

	public static String getprocessinstatus(String state) {
		switch (state) {
		case "0":
			return "暂存";
		case "1":
			return "已记账";
		case "2":
			return "已作废";
		default:
			return "暂存";
		}
	};

	public static String getDeliverStatus(String state) {
		switch (state) {
		case "0":
			return "暂存";
		case "1":
			return "待出货";
		case "2":
			return "已完结";
		case "3":
			return "已取消";
		default:
			return "暂存";
		}
	};

	public static String getDeliver2Status(String state) {
		switch (state) {
		case "0":
			return "待出货";
		case "1":
			return "已出货";
		case "2":
			return "取消出货";
		default:
			return "待出货";
		}
	};

	public static String getStockstatus(String state) {
		switch (state) {
		case "0":
			return "等待入库";
		case "-1":
			return "货未收全";
		case "1":
			return "完成入库";
		case "2":
			return "终止并完成";
		default:
			return "等待入库";
		}
	};

	public static String getQuotationstatus(String state) {
		switch (Integer.parseInt(state)) {
		case 0:
			return "报价中";
		case 1:
			return "报价成功";
		case 2:
			return "报价终止";
		default:
			return "报价中";
		}
	};

	public static String getorderstatus(String state) {
		switch (state) {
		case "0":
			return "等待下单";
		case "1":
			return "完成下单";
		case "2":
			return "终止并完成";
		default:
			return "";
		}
	};

	public static String getSalesStockstatus(String state) {
		switch (state) {
		case "0":
			return "等待出库";
		case "1":
			return "完成出库";
		case "2":
			return "终止并完成";
		default:
			return "等待出库";
		}
	};

	public static String getSalesorderSchedulstatus(String state) {
		switch (state) {
		case "0":
			return "等待出库";
		case "1":
			return "完成出库";
		case "2":
			return "终止并完成";
		default:
			return "等待出库";
		}
	};

	public static String getSchedulstatus(String state) {
		switch (state) {
		case "0":
			return "待处理";
		case "1":
			return "生产中";
		case "2":
			return "完成入库";
		case "3":
			return "终止并完成";
		default:
			return "待处理";
		}
	};

	public static String getDeliverstatus(String state) {
		switch (state) {
		case "0":
			return "暂存";
		case "1":
			return "待出货";
		case "2":
			return "已完结";
		default:
			return "暂存";
		}
	};

	public static String getDeliverdetailstatus(String state) {
		switch (state) {
		case "0":
			return "待出货";
		case "1":
			return "已出货";
		case "2":
			return "已取消";
		case "3":
			return "已作废";
		default:
			return "待出货";
		}
	};

	public static String getTState(String state) {
		switch (state) {
		case "1":
			return "正常";
		case "2":
			return "停用";
		default:
			return "";
		}
	}

	public static String getmbilltype(String bill_type) {
		// 1-采购单 2-销售单 3-组装拆卸单 4-调拨单 5-盘点单 9-报损记录单 6-采购退货单 7-销售退货单
		switch (bill_type) {
		case "1":
			return "采购入库单";
		case "2":
			return "销售出库单";
		case "3":
			return "组装拆卸单";
		case "4":
			return "调拨单";
		case "5":
			return "盘点单";
		case "6":
			return "采购退货单";
		case "7":
			return "销售退货单";
		case "8":
			return "期初库存单";
		case "9":
			return "报损记录单";
		case "10":
			return "生产领用单";
		case "11":
			return "产品入库单";
		case "12":
			return "其他入库单";
		case "13":
			return "其他出库单";
		case "14":
			return "采购订单";
		case "15":
			return "销售订单";
		case "16":
			return "排产单";
		case "17":
			return "送货单";
		case "22":
			return "委外加工单";
		case "23":
			return "加工出库单";
		case "24":
			return "加工退料单";
		case "25":
			return "加工入库单";
		case "26":
			return "生产退料单";
		case "27":
			return "加工退货单";
		case "28":
			return "采购申请单";
		case "29":
			return "报价单";
		default:
			return "";
		}
	}

	// 2020-12-1 增加采购申请类型
	public static String getdetailbilltype(String stype) {

		switch (stype) {
		case "11":
			return "采购-入库 ";
		case "21":
			return "销售-出库";
		case "31":
			return "组装拆卸单-出库";
		case "32":
			return "组装拆卸单-入库";
		case "41":
			return "调拨-出库";
		case "42":
			return "调拨-入库";
		case "51":
			return "盘点盘盈-入库";
		case "52":
			return "盘点盘亏-出库";
		case "53":
			return "库存盘点-无盈亏";
		case "81":
			return "期初库存-入库";
		case "61":
			return "采购退货-出库";
		case "71":
			return "销售退货-入库";
		case "91":
			return "报损记录";
		case "101":
			return "生产领用-出库";
		case "111":
			return "生产入库-入库";
		case "121":
			return "其他入库-入库";
		case "131":
			return "其他出库-出库";
		case "141":
			return "采购订单";
		case "151":
			return "销售订单";
		case "161":
			return "排产单";
		case "1611":
			return "生产工单";
		case "171":
			return "送货单";
		case "221":
		case "222":
			return "委外加工单";
		case "231":
			return "加工材料-出库";
		case "241":
			return "加工退料-入库";
		case "251":
			return "委外加工-入库";
		case "271":
			return "委外加工-退货";
		case "261":
			return "生产退料-入库";
		case "281":
			return "采购申请";
		case "311":
			return "工序外协发货单";
		case "321":
			return "工序外协收货单";
		case "291":
			return "报价单";
		default:
			return "";
		}
	}

	public static String getbilltype(String stype) {

		switch (stype) {
		case "11":
			return "采购-入库 ";
		case "21":
			return "销售-出库";
		case "31":
			return "组装拆卸单-出库";
		case "32":
			return "组装拆卸单-入库";
		case "41":
			return "调拨-出库";
		case "42":
			return "调拨-入库";
		case "51":
			return "盘点盘盈-入库";
		case "52":
			return "盘点盘亏-出库";
		case "53":
			return "库存盘点-无盈亏";
		case "81":
			return "期初库存-入库";
		case "61":
			return "采购退货-出库";
		case "71":
			return "销售退货-入库";
		case "91":
			return "报损记录";
		case "101":
			return "生产领用-出库";
		case "111":
			return "生产入库-入库";
		case "121":
			return "其他入库-入库";
		case "131":
			return "其他出库-出库";
		case "141":
			return "采购订单";
		case "151":
			return "销售订单";
		case "161":
			return "生产工单";
		case "171":
			return "送货单";
		case "221":
			return "委外加工单";
		case "231":
			return "加工材料-出库";
		case "241":
			return "加工退料-入库";
		case "251":
			return "委外加工-入库";
		case "271":
			return "委外加工-退货";
		case "261":
			return "生产退料-入库";
		case "311":
			return "工序外协发货单";
		case "321":
			return "工序外协收货单";
		case "291":
			return "报价单";
		default:
			return "";
		}
	}

	// 2019-09-09 增加委外加工加工状态
	public static String getOutsourcingbillstatus(String state) {
		switch (state) {
		case "0":
			return "未完成";
		case "1":
			return "已完成";
		case "2":
			return "终止并完成";
		default:
			return "未完成";
		}
	};

	public static String getdeliverdetailbilltype(String billtype) {
		switch (billtype) {
		case "2":
			return "销售出货";
		case "23":
			return "加工出货";
		default:
			return "销售出货";
		}
	};

	public static String getscheduletype(int scheduletype) {
		switch (scheduletype) {
		case 0:
			return "全部工单";
		case 1:
			return "订货工单";
		case 2:
			return "备货工单";
		case 3:
			return "返工工单";
		case 4:
			return "非返工工单";
		default:
			return "订货工单";
		}
	};

	public static int getDatalogBillChangefunc(String tablename, String billtype) {
		int stype = 0;
		switch (tablename) {
		case "storein":// 采购单
			if (billtype.equals("6")) {// 采购退货单
				stype = 16;
			} else {// 1或空采购单//if (billtype.equals("1"))
				stype = 11;
			}
			break;
		case "storeout":// 出库单
			if (billtype.equals("7")) {// 客户退货单
				stype = 17;
			} else {// 2或空 //销售单// (billtype.equals("2"))
				stype = 12;
			}
			break;
		case "storecheck":// 盘点单
			stype = 15;
			break;
		case "splits":// 组装拆卸
			stype = 13;
			break;
		case "storemove":// 调拨单
			stype = 14;
			break;
		case "reportloss":// 报损记录单
			stype = 19;
			break;
		case "itembegin":// 期初库存单
			stype = 18;
			break;
		case "prodrequisition":// 生产领用
			if (billtype.equals("10")) {// 生产领用
				stype = 23;
			} else {// 26或空 //生产退料
				stype = 47;
			}
			break;
		case "prodstorage":// 产品入库
			stype = 24;
			break;
		case "scheduleorder":// 排产单
			stype = 29;
			break;
		case "purchase":// 采购订单
			stype = 49;
			break;
		case "purchaseorder":// 采购订单
			stype = 27;
			break;
		case "salesorder":// 销售订单
			stype = 28;
			break;
		case "otherinout":// 其他出入库
			if (billtype.equals("12")) {// 其他入库单
				stype = 25;
			} else if (billtype.equals("13")) {// 其他出库单
				stype = 26;
			}
			break;
		case "deliver":// 送货订单
			stype = 32;
			break;
		case "t_order_detail":// 工单明细
			stype = 33;
			break;
		case "t_detail_code":// 工单细码
			stype = 34;
			break;
		case "storelocation":// 库位表
			stype = 35;
			break;
		case "locationitem":// 库位物料表
			stype = 36;
			break;
		case "account":// 结算账户
			stype = 37;
			break;
		case "accountbill":// 收款单
			if (billtype.equals("18")) {// 收款单
				stype = 38;
			} else if (billtype.equals("19")) {// 19或空 //付款单
				stype = 39;
			} else if (billtype.equals("38")) {// 收承兑
				stype = 81;
			} else if (billtype.equals("39")) {// 付承兑
				stype = 82;
			}
			break;
		case "inouttype":// 收支项目类型
			stype = 40;
			break;
		case "dayinout":// 收款单
			if (billtype.equals("20")) {// 日常收入
				stype = 41;
			} else {// 21或空 //日常支出
				stype = 42;
			}
			break;
		case "outsourcing":// 委外加工单
			stype = 43;
			break;
		case "processinout":// 加工材料出入库单
			if (billtype.equals("23")) {// 加工材料出库单
				stype = 44;
			} else {// 24-加工退料入库单
				stype = 45;
			}
			break;
		case "outsourcingin":// 委外加入库单
			if (billtype.equals("24")) {
				stype = 46;
			} else {
				stype = 73;
			}
			break;

		case "customerbill":// 往来单位调账
			stype = 74;
			break;
		case "transfer":// 结算账户转账
			stype = 75;
			break;
		// 23-生产领用 24-产品入库 25-其他入库 26-其他出库 27-采购订单 28-销售订单 29-排产单30-采购报表 31-销售报表
		// 32-送货单 33工单明细 34工单细码 [35-库位信息 36-库位物料信息 37-结算帐户 38-收款单 39-付款单
		// 40-收支项目类型 41-日常收入 42-日常支出 43-委外加工单 44-加工材料出库 45-加工退料入库 46-委外加工入库单

		case "stageoutsourcing":// 工序外协
			if (billtype.equals("31")) {
				stype = 76;
			} else {
				stype = 77;
			}
			break;
		case "quotation":// 报价单
			stype = 80;
			break;
		case "qualitymain":// 质检单
			stype = 90;
			break;
		case "apply_material":// 质检单
			stype = 440;
			break;
		case "apply_payment":// 付款申请单
			stype = 441;
			break;
		case "apply_invoice":// 开票申请单
			stype = 442;
			break;
		case "apply_leave":// 请假申请单
			stype = 443;
			break;
		case "apply_overtime":// 加班申请单
			stype = 444;
			break;
		case "apply_iteminfo":// 商品信息申请单
			stype = 445;
			break;
		case "apply_overtimetotal":// 加班汇总申请单
			stype = 446;
			break;
		case "invoicestorein":// 发票商品
			if (billtype.equals("51")) {// 发票商品入库单
				stype = 600;
			} else { // 52 发票商品出库单
				stype = 601;
			}
			break;
		case "storeoutapply":
			stype = 450;
			break;
		case "t_devicemaintenance":
			stype = 650;
			break;	
		case "t_devicemaintainapply":
			stype = 651;
			break;	
		case "t_devicemaintain":
			stype = 652;
			break;
		case "schedule_pick":
			stype = 653;
			break;
		}

		return stype;
	}

	public static int getUploadFilefunc(String tablename, String billtype) {
		int stype = 0;
		switch (tablename) {
		case "storein":// 采购单
			if (billtype.equals("6")) {// 采购退货单
				stype = 6;
			} else {// 1或空采购单//if (billtype.equals("1"))
				stype = 5;
			}
			break;
		case "storeout":// 出库单
			if (billtype.equals("7")) {// 客户退货单
				stype = 9;
			} else {// 2或空 //销售单// (billtype.equals("2"))
				stype = 8;
			}
			break;
		// case "storecheck":// 盘点单
		// stype = 15;
		// break;
		// case "splits":// 组装拆卸
		// stype = 13;
		// break;
		// case "storemove":// 调拨单
		// stype = 14;
		// break;
		// case "reportloss":// 报损记录单
		// stype = 19;
		// break;
		// case "itembegin":// 期初库存单
		// stype = 18;
		// break;
		// case "prodrequisition":// 生产领用
		// if (billtype.equals("10")) {// 生产领用
		// stype = 23;
		// } else {// 26或空 //生产退料
		// stype = 47;
		// }
		// break;
		// case "prodstorage":// 产品入库
		// stype = 24;
		// break;
		// case "scheduleorder":// 排产单
		// stype = 29;
		// break;
		// case "purchase":// 采购订单
		// stype = 4;
		// break;
		case "purchaseorder":// 采购订单
			stype = 4;
			break;
		case "salesorder":// 销售订单
			stype = 3;
			break;
		// case "otherinout":// 其他出入库
		// if (billtype.equals("12")) {// 其他入库单
		// stype = 25;
		// } else if (billtype.equals("13")) {// 其他出库单
		// stype = 26;
		// }
		// break;
		case "deliver":// 送货订单
			stype = 19;
			break;
		case "t_order_detail":// 工单明细
			stype = 1;
			break;
		// case "t_detail_code":// 工单细码
		// stype = 34;
		// break;
		// case "storelocation":// 库位表
		// stype = 35;
		// break;
		// case "locationitem":// 库位物料表
		// stype = 36;
		// break;
		// case "account":// 结算账户
		// stype = 37;
		// break;
		case "accountbill":// 收款单
			if (billtype.equals("18")) {// 收款单
				stype = 21;
			} else if (billtype.equals("19")) {// 19或空 //付款单
				stype = 21;
			} else if (billtype.equals("38")) {// 收承兑
				stype = 24;
			} else if (billtype.equals("39")) {// 付承兑
				stype = 24;
			}
			break;
		// case "inouttype":// 收支项目类型
		// stype = 40;
		// break;
		case "dayinout":// 收款单
			// if (billtype.equals("20")) {// 日常收入
			// stype = 41;
			// } else {// 21或空 //日常支出
			// stype = 42;
			// }
			stype = 20;
			break;
		case "outsourcing":// 委外加工单
			stype = 10;
			break;
		case "processinout":// 加工材料出入库单
			if (billtype.equals("23")) {// 加工材料出库单
				stype = 13;
			} else {// 24-加工退料入库单
				stype = 14;
			}
			break;
		case "outsourcingin":// 委外加入库单
			if (billtype.equals("24")) {
				stype = 11;
			} else {
				stype = 12;
			}
			break;

		case "customerbill":// 往来单位调账
			stype = 22;
			break;
		case "transfer":// 结算账户转账
			stype = 23;
			break;
		// 23-生产领用 24-产品入库 25-其他入库 26-其他出库 27-采购订单 28-销售订单 29-排产单30-采购报表 31-销售报表
		// 32-送货单 33工单明细 34工单细码 [35-库位信息 36-库位物料信息 37-结算帐户 38-收款单 39-付款单
		// 40-收支项目类型 41-日常收入 42-日常支出 43-委外加工单 44-加工材料出库 45-加工退料入库 46-委外加工入库单

		case "stageoutsourcing":// 工序外协
			if (billtype.equals("31")) {
				stype = 15;
			} else {
				stype = 16;
			}
			break;
		case "quotation":// 报价单
			stype = 18;
			break;
		case "qualitymain":// 质检单
			stype = 25;
			break;
		case "apply_material":// 质检单
			stype = 26;
			break;
		case "apply_payment":// 付款申请单
			stype = 27;
			break;
		case "apply_invoice":// 开票申请单
			stype = 28;
			break;
		case "apply_leave":// 请假申请单
			stype = 29;
			break;
		case "apply_overtime":// 加班申请单
			stype = 30;
			break;
		case "purchase":// 采购申请单
			stype = 31;
			break;
		case "invoicestorein": // 32-发票商品入库 33-发票商品出库
			if (billtype.equals("51")) {
				stype = 32;
			} else {
				stype = 33;
			}
			break;
		case "t_device":
			stype = 34;
			break;
		case "t_devicemaintenance":
			stype = 35;
			break;
		case "t_devicemaintainApply":
			stype = 36;
			break;
		case "t_devicemaintain":
			stype = 37;
			break;
		case "storeoutapply":
			stype = 40;
			break;
		case "seal_apply":
			stype = 41;
			break;
		case "schedule_pick":
			stype = 42;
			break;
		}

		return stype;
	}

	public static String getCustomerstatus(int state) {
		switch (state) {
		case 0:
			return "开放不需确认接收";
		case 1:
			return "开放需确认接收";
		case 2:
			return "不开放";
		default:
			return "";
		}
	};

	public static String getInouttype(int state) {
		switch (state) {
		case 1:
			return "加工入库";
		case 2:
			return "加工出库";
		case 3:
			return "加工退回";
		case 4:
			return "加工领出";
		case 5:
			return "材料入库";
		case 6:
			return "材料退回";
		case 7:
			return "加工领料";
		case 8:
			return "加工退料";
		default:
			return "";
		}
	};

	// 向上保留位数
	public static double formatDoubleUp(double dvalue, int digit) {
		return (new BigDecimal((dvalue)).setScale(digit, RoundingMode.HALF_UP)).doubleValue();
	}

	// 向下保留位数
	public static double formatDoubleDown(double dvalue, int digit) {
		return (new BigDecimal((dvalue)).setScale(digit, RoundingMode.HALF_DOWN)).doubleValue();
	}

	// 两个double相减
	public static double subtractdouble(double minuend, double subtrahend) {
		return new BigDecimal(Double.toString(minuend)).subtract(new BigDecimal(Double.toString(subtrahend))).doubleValue();
	}

	// 两个double相加
	public static double adddouble(double a, double b) {
		return new BigDecimal(Double.toString(a)).add(new BigDecimal(Double.toString(b))).doubleValue();
	}

	// 两个double相加
	public static double multiplydouble(double a, double b) {
		return new BigDecimal(Double.toString(a)).multiply(new BigDecimal(Double.toString(b))).doubleValue();
	}

	public static Double addDoubleArray(ArrayList<Double> arr) {
		Double sum = 0.0;
		for (Double obj : arr) {
			sum = adddouble(obj, sum);
		}
		return sum;
	}

	public static String checkSaveDate(String companyid, int t6days, String operate_time, boolean hasdays, Connection conn) {
		String message = "";
		try {
			if (hasdays == false) {
				List<Object> fparams = new ArrayList<>();
				fparams.add(companyid);
				Table companytalbe = DataUtils.queryData(conn, "select t6days from s_company_config where company_id=? ", fparams, null, null, null);
				if (companytalbe.getRows().size() > 0) {
					t6days = companytalbe.getRows().get(0).getInteger("t6days");
				}
			}
			// System.out.println("t6days:"+t6days);
			if (t6days > 0) {
				SimpleDateFormat checkFormat = new SimpleDateFormat("yyyy-MM");
				SimpleDateFormat checkFormat2 = new SimpleDateFormat("yyyy-MM-dd");
				Date checkcurdate = checkFormat2.parse(checkFormat2.format(new Date()));
				String t6daysstr = checkFormat.format(new Date());
				Date checkt6days = checkFormat2.parse(t6daysstr + "-" + String.format("%02d", t6days));
				// System.out.println("checkcurdate:"+checkcurdate+" "+checkt6days+" "+operate_time);
				Date checkoperate_time = checkFormat2.parse(operate_time);
				checkt6days = checkFormat2.parse(t6daysstr + "-01");

				Calendar calday = Calendar.getInstance();
				calday.setTime(checkt6days);
				calday.add(Calendar.MONTH, -1);
				 

				if (checkoperate_time.before(calday.getTime())) {
					message = "不能创建或保存" + (checkFormat2.format(calday.getTime())) + "之前的库存单据，请修改单据日期。";
				} else {
					if (checkcurdate.after(checkt6days)) {
						// System.out.println("checkoperate_time:"+checkoperate_time+" "+checkt6days);
						if (checkoperate_time.before(checkt6days)) {
							message = "每月" + t6days + "日之后不能创建或保存上月库存单据，请修改单据日期。";
						}
						// System.out.println("message:"+message);
					}
				}

			}
		} catch (Exception e) {
		}
		return message;
	}

	public static String checkInvalidDate(String mainid, String tablename, String colid, Connection conn) {
		String message = "";
		Date operate_time = null;
		try {
			int t6days = 0;
			int dockingOK = 0;
			List<Object> fparams = new ArrayList<>();
			fparams.add(mainid);// DATE_FORMAT(m.operate_time,'%Y-%m-%d')
			Table companytalbe = DataUtils.queryData(conn, "select m.operate_time, "+(tablename.equals("itembegin")?" 0 as dockingOK,":"m.dockingOK, ")+"sc.t6days from " + tablename + " m join s_company_config sc on m.companyid=sc.company_id where m."
					+ colid + "=? ", fparams, null, null, null);
			if (companytalbe.getRows().size() > 0) {
				dockingOK = companytalbe.getRows().get(0).getInteger("dockingOK");
				t6days = companytalbe.getRows().get(0).getInteger("t6days");
				operate_time = companytalbe.getRows().get(0).getDate("operate_time");
			}
			 

			if (dockingOK > 0) {
				message = "当前单据已对接到财务系统，不能作废或修改。";
			} else {
				if (t6days > 0) {
					SimpleDateFormat checkFormat = new SimpleDateFormat("yyyy-MM");
					SimpleDateFormat checkFormat2 = new SimpleDateFormat("yyyy-MM-dd");
					Date checkcurdate = checkFormat2.parse(checkFormat2.format(new Date()));
					String t6daysstr = checkFormat.format(new Date());
					Date checkt6days = checkFormat2.parse(t6daysstr + "-" + String.format("%02d", t6days));
					// System.out.println("checkcurdate:"+checkcurdate+" "+checkt6days+" "+operate_time);
					checkt6days = checkFormat2.parse(t6daysstr + "-01");

					Calendar calday = Calendar.getInstance();
					calday.setTime(checkt6days);
					calday.add(Calendar.MONTH, -1);
					 

					if (operate_time.before(calday.getTime())) {
						message = "不能作废或修改" + (checkFormat2.format(calday.getTime())) + "之前的库存单据。";
					} else {
						if (checkcurdate.after(checkt6days)) { 
							if (operate_time.before(checkt6days)) {
								message = "每月" + t6days + "日之后不能作废或修改上月库存单据。";
							}
							// System.out.println("message:"+message);
						}
					}
				}
			}
		} catch (Exception e) {
		}
		return message;
	}
	
	public static String getMaintainApplyStatus(int state) {
		switch (state) {
			case 0:
				return "未审核";
			case 1:
				return "已审核";
			case 2:
				return "已作废";
			default:
				return "未审核";
		}
	};

	public static String getMaintainApplyFStatus(int state) {
		switch (state) {
			case 0:
				return "未登记";
			case 1:
				return "已登记";
			default:
				return "未登记";
		}
	};
	
	 

	public static String getSealApplyStatus(int state) {
		switch (state) {
			case 0:
				return "未审核";
			case 1:
				return "已审核";
				case 2:
					return "已作废";
					case 3:
						return "审批中";
			default:
				return "未登记";
		}
	};

	public static String getAfterSalesMaintainStatus(int state) {
		switch (state) {
			case 0:
				return "未处理";
			case 1:
				return "已处理";
			case 2:
				return "不处理";
			default:
				return "未处理";
		}
	};

	public static String getAfterSalesMaintainFStatus(int state) {
		switch (state) {
			case 0:
				return "未支付";
			case 1:
				return "已支付";
			case 2:
				return "无需支付";
			default:
				return "未支付";
		}
	};
	
	public static String getProgressType(String state) {
		switch (state) {
			case "0":
				return "工单扫描";
			case "1":
				return "细码扫描";
			default:
				return "异常状态";
		}
	};

	public static String getReportType(String status) {
		switch (status) {
			case "1":
				return "后端报工";
			case "2":
				return "工序终止并报工";
			default:
				return "异常状态";
		}
	}

	public static String getWorkMode(String status) {
		switch (status) {
			case "1":
				return "计件";
			case "2":
				return "计时";
			default:
				return "计件";
		}
	}

	public static String getSchedulePickStatus(String status) {
		switch (status) {
			case "0":
				return "暂存";
			case "1":
				return "已记账";
			case "2":
				return "已作废";
			default:
				return "暂存";
		}
	}
}
