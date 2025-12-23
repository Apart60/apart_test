package erpscan.save;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;

import javax.naming.NamingException;

import erpscan.Common;
import erpscan.save.Pdacommon;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.justep.baas.action.ActionContext;
import com.justep.baas.data.DataUtils;
import com.justep.baas.data.Row;
import com.justep.baas.data.Table;

import java.util.List;

import java.util.Date;
import java.sql.PreparedStatement;

public class Pdasave {
	private static final String DATASOURCE = Common.DATASOURCE;

	// 根据公司表名前端获取orderid
	public static JSONObject getOrderId(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		try {
			String companyid = params.getString("companyid");
			String tablename = params.getString("tablename");
			String billtype = params.getString("billtype");
			String billdate = params.getString("billdate");
			billtype = billtype == null ? "" : billtype;

			JSONObject ret = new JSONObject();
			ret.put("NewID", getOrderidByparams(companyid, tablename, billtype, billdate, conn));
			return ret;
		} finally {
			conn.close();
		}
	}

	// 获取单据前缀
	public static String getOrderidByparams(String companyid, String tablename, String billtype, String billdate, Connection conn) {
		// Date t = new Date();
		// SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String pre = "SS";
		// IS-yyyymmdd-0001
		switch (tablename) {
			case "storein":// 采购单
				if (billtype.equals("6")) {// 采购退货单
					pre = "CT";
				} else if (billtype.equals("1")) {// 1或空 入库单
					pre = "CG";
				}
				break;
			case "storeout":// 销售单
				if (billtype.equals("7")) {// 销售退货单
					pre = "XT";
				} else if (billtype.equals("2")) {// 2或空 //销售单
					pre = "XS";
				}
				break;
			case "otherinout":// 销售单
				if (billtype.equals("12")) {// 其他入库单
					pre = "QR";
				} else if (billtype.equals("13")) {// 其他出库单
					pre = "QC";
				}
				break;
			case "storecheck":// 盘点单
				pre = "PD";
				break;
			case "splits":// 组装拆卸
				pre = "ZC";
				break;
			case "storemove":// 调拨单
				pre = "DB";
				break;
			case "reportloss":// 报损记录单
				pre = "BS";
				break;
			case "itembegin":// 期初库存单
				pre = "QC";
				break;
			case "purchase":// 采购申请
				pre = "CS";
				break;
			case "purchaseorder":// 采购订单
				pre = "CD";
				break;
			case "storecontract":// 采购合同
				if (billtype.equals("store")) { // 2020-11-14
					pre = "HT";
				} else if (billtype.equals("outsourcing")) {
					pre = "WT";
				} else if (billtype.equals("salesorder")) {
					pre = "XH";
				}
				break;
			case "salesorder":// 销售订单
				pre = "XD";
				break;
			case "prodrequisition":// 生产领用
				if (billtype.equals("10")) {// 生产领用
					pre = "SL";
				} else if (billtype.equals("26")) {// 生产退料
					pre = "ST";
				}
				break;
			case "prodstorage":// 产品入库
				pre = "CR";
				break;
			case "scheduleorder":// 排产单
				pre = "PC";
				break;
			case "deliver":// 送货计划单
				pre = "SH";
				break;
			case "accountbill":// 收付款单
				if (billtype.equals("18")) {// 收款单
					pre = "SK";
				} else if (billtype.equals("19")) {// 付款单
					pre = "FK";
				} else if (billtype.equals("38")) {// 收承兑
					pre = "SC";
				} else if (billtype.equals("39")) {// 付承兑
					pre = "FC";
				}
				break;
			case "dayinout":// 收付款单
				if (billtype.equals("20")) {// 日常收入
					pre = "RS";
				} else if (billtype.equals("21")) {// 日常支出
					pre = "RZ";
				}
				break;
			case "outsourcing":// 委外加工单
				pre = "WJ";
				break;
			case "processinout":// 加工材料出入库表
				if (billtype.equals("23")) {// 加工材料出库单
					pre = "JC";
				} else if (billtype.equals("24")) {// 加工退料入库单
					pre = "JT";
				}
				break;
			case "outsourcingin":// 委外加工入库单
				if (billtype.equals("25")) {
					pre = "WR";
					break;
				} else if (billtype.equals("27")) {
					pre = "WT";
					break;
				}
			case "customerbill":// 往来单位调账
				pre = "TZ";
				break;
			case "stageoutsourcing":// 工序外协
				if (billtype.equals("31")) {
					pre = "GC";
					break;
				} else if (billtype.equals("32")) {
					pre = "GR";
					break;
				}
			case "quotation":// 报价单
				pre = "QT";
				break;
			case "qualitymain":// 报价单
				pre = "";
				break;
			case "apply_material":// 补料申请单
				pre = "BL";
				break;
			case "apply_payment":// 付款申请单
				pre = "FS";
				break;
			case "apply_invoice":// 开票申请单
				pre = "KS";
				break;
			case "apply_leave":// 请假申请单
				pre = "QS";
				break;
			case "apply_overtime":// 加班申请单
				pre = "JS";
				break;
			case "apply_overtimetotal":// 加班申请单
				pre = "HS";
				break;
			case "apply_iteminfo":// 加班申请单
				pre = "SS";
				break;

			case "invoicestorein":// 发票出入库表
				if (billtype.equals("51")) {
					pre = "FPR";
				} else if (billtype.equals("52")) {
					pre = "FPC";
				}
				break;
			case "t_devicemaintenance":// 保养登记单 61
				pre = "DM";
				break;
			case "t_devicemaintain":// 设备维修登记单 63
				pre = "DW";
				break;
			case "t_devicemaintainapply":// 设备维修申请 62
				pre = "DA";
				break;

			case "after_sales_maintain":// 销售后维护 64
				pre = "WH";
				break;

			case "storeoutapply":// 发货申请单 65
				pre = "FH";
				break;

			case "seal_apply":// 用章申请
				pre = "SA";
				break;
			case "salesordercombine"://销售合并 
				pre = "XB";
				break;

			case "schedule_pick"://排产领料
				pre = "PL";
				break;

		}

		// System.out.println(tablename + " " + billtype + " " + pre);

		// pre = pre + "-" + df.format(t) + "-";
		if (tablename.equals("apply_leave") || tablename.equals("apply_overtime") || tablename.equals("apply_overtimetotal") || tablename.equals("t_devicemaintenance")
				|| tablename.equals("t_devicemaintain") || tablename.equals("t_devicemaintainapply")) {
			SimpleDateFormat df = new SimpleDateFormat("yyyy");
			pre = pre + "-" + df.format(new Date());
		} else if (tablename.equals("qualitymain")) {
			pre = pre + billdate;
		} else if (tablename.equals("invoicestorein")) {
			pre = pre + billdate.substring(0, 6) + "-";
		} else {
			pre = pre + "-" + billdate + "-";
		}

		String codeid = "";
		String sql = "select max(SUBSTRING(orderid," + (pre.length() + 1) + ",LENGTH(orderid)-" + pre.length() + ")) from " + tablename + " where companyid=? and SUBSTRING(orderid,1," + pre.length()
				+ ")='" + pre + "' and (substring(orderid," + (pre.length() + 1) + ",LENGTH(orderid)-" + pre.length() + ") REGEXP '[^0-9]') = 0  ";

		List<Object> fparams = new ArrayList<>();
		fparams.add(companyid);

		Object cobject = DataUtils.getValueBySQL(conn, sql, fparams);
		int count;
		if (cobject == null) {
			count = 1;
		} else {
			count = Integer.parseInt(cobject.toString()) + 1;
		}
		if (tablename.equals("apply_leave") || tablename.equals("apply_overtime") || tablename.equals("apply_overtime") || tablename.equals("t_devicemaintenance")
				|| tablename.equals("t_devicemaintain") || tablename.equals("t_devicemaintainapply")) {
			if (count > 999999999) {
				codeid = pre + count;
			} else {
				codeid = pre + String.format("%09d", count);
			}
		} else if (tablename.equals("qualitymain")) {
			if (count > 999999) {
				codeid = pre + count;
			} else {
				codeid = pre + String.format("%06d", count);
			}
		} else if (tablename.equals("invoicestorein")) {
			if (count > 999999) {
				codeid = pre + count;
			} else {
				codeid = pre + String.format("%06d", count);
			}
		} else {

			if (count > 999) {
				codeid = pre + count;
			} else {
				codeid = pre + String.format("%04d", count);
			}
		}
		return codeid;
	};

	// 保存商品信息表
	public static JSONObject saveIteminfoFunction(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject rt = new JSONObject();
		String iteminfo = params.getString("iteminfo");
		String pcodeid = params.getString("codeid");
		String operate = params.getString("operate");
		String companyid = params.getString("companyid");
		String change = params.getString("change");
		String loginUser = params.getString("loginUser");
		String loginuserid = params.getString("loginuserid");
		String itemid = params.getString("itemid");
		String oldcodeid = params.getString("oldcodeid");
		String iteminfostr = params.getString("iteminfostr");

		JSONArray splitsdata = params.getJSONArray("splitsdata");// 组装拆分数据
		String delsplits = params.getString("delsplits");// 删除组装拆分数据
		String splitsinfo = params.getString("splitsinfo");// 组装拆分数据变更信息

		String parentid = params.getString("parentid");
		Integer bomtype = params.getInteger("bomtype");
		String furlname = params.getString("furlname");
		String curitemid = params.getString("curitemid");

		try {

			Statement ps = conn.createStatement();
			conn.setAutoCommit(false);
			String codeid = pcodeid.equals("") ? Pdacommon.getCodeByparams(companyid, "iteminfo", "codeid", conn) : pcodeid;

			if (operate.equals("edit")) {
				if (!iteminfo.equals("")) {
					ps.addBatch("update  iteminfo set " + iteminfo
							+ (!pcodeid.equals("") && oldcodeid.equals(pcodeid) ? "" : (iteminfo.equals("") ? "" : ",") + "codeid='" + codeid.replaceAll("'", "''") + "'")
							+ ",update_time=now(),update_id='" + loginuserid + "',update_by='" + loginUser + "' where itemid='" + itemid + "'");

					String content = "";
					if (!oldcodeid.equals(pcodeid)) {
						content = "【商品编号】原值《" + oldcodeid + "》" + (pcodeid.equals("") ? "因修改为空值，自动生成编号" : "") + "改变为《" + codeid + "》" + (change.equals("") ? "" : "；" + change);
					} else {
						content = change;
					}
					// 修改数据记录
					ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
							+ companyid + "',1,'修改','" + itemid + "','" + (iteminfostr + content).replaceAll("'", "''") + "','" + loginuserid + "','" + loginUser + "',now())");
				}
			} else if (operate.equals("new") || operate.equals("copynew")) {
				// 2020-12-24 增加 package_count
				ps.addBatch("insert into iteminfo (codeid,itemid,companyid,itemname,sformat,mcode,classid,unit,imgurl,inprice,outsourcingprice,outprice,barcode,remark,status,property1,property2,property3,property4,property5,create_id,create_by,create_time,update_id,update_by,update_time,outprice1,outprice2,outprice3,outprice4,outprice5,package_count,unitstate1,unitstate2,unitstate3,unitset1,unitset2,unitset3,demand) VALUES ('"
						+ (pcodeid.equals("") ? codeid : pcodeid) + iteminfo);
				// System.out.println(iteminfo);
				// 新增数据记录
				ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
						+ companyid + "',1,'新增','" + itemid + "','商品编号：" + (pcodeid.equals("") ? codeid : pcodeid).replaceAll("'", "''") + "','" + loginuserid + "','" + loginUser + "',now())");

			}

			if (operate.equals("copynew") && furlname != null && furlname.equals("itemBomshow")) {

				ps.addBatch("update itemsplits set itemid='" + itemid + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where combitemid='" + parentid
						+ "' and itemid='" + curitemid + "' and splittype=" + bomtype);
			}

			// 组装折分操作代码
			if (!delsplits.equals("")) {
				ps.addBatch("delete from itemsplits where id in (" + delsplits + ")");
			}
			if (splitsdata.size() > 0) {
				for (int i = 0; i < splitsdata.size(); i++) {
					JSONObject result = JSONObject.parseObject(splitsdata.getString(i));
					ps.addBatch("insert into itemsplits (id,companyid,number,combitemid,itemid,count,splittype,remark,assistformula,create_id,create_by,create_time,update_id,update_by,update_time) VALUES ('"
							+ result.getString("id")
							+ "','"
							+ companyid
							+ "',"
							+ result.getInteger("number")
							+ ",'"
							+ itemid
							+ "','"
							+ result.getString("itemid")
							+ "',"
							+ result.getDoubleValue("count")
							+ ","
							+ result.getString("splittype")
							+ ",'"
							+ result.getString("remark")
							+ "','"
							+ result.getString("assistformula")
							+ "','"
							+ loginuserid
							+ "','"
							+ loginUser
							+ "',now(),'"
							+ loginuserid
							+ "','"
							+ loginUser
							+ "',now()) on duplicate key update number="
							+ result.getInteger("number")
							+ ",count="
							+ result.getDoubleValue("count")
							+ ",itemid='"
							+ result.getString("itemid")
							+ "',remark='"
							+ result.getString("remark")
							+ "',assistformula='"
							+ result.getString("assistformula") + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now()");
				}
			}

			if (splitsdata.size() > 0 || !delsplits.equals("")) {
				ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
						+ companyid + "',1,'修改','" + itemid + "','" + splitsinfo.replaceAll("'", "''") + "','" + loginuserid + "','" + loginUser + "',now())");
				ps.addBatch("update iteminfo set splits = if((select 1 from itemsplits where combitemid='" + itemid + "' and splittype=1 limit 1),1,0) where itemid='" + itemid + "'");
				ps.addBatch("update iteminfo set msplits = if((select 1 from itemsplits where combitemid='" + itemid + "' and splittype=2 limit 1),1,0) where itemid='" + itemid + "'");
			}

			ps.executeBatch();
			conn.commit();
			conn.setAutoCommit(true);
			rt.put("state", "1");
		} catch (Exception e) {
			// e.printStackTrace();
			try {
				conn.rollback();
				rt.put("state", "0");
			} catch (Exception e1) {
				// e1.printStackTrace();
				rt.put("state", "0");
			}
		} finally {
			conn.close();
		}
		return rt;
	}

	// 自定义工序
	public static JSONObject updateitemstep(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject ret = new JSONObject();
		String itemid = params.getString("itemid");
		String companyid = params.getString("companyid");
		String class_id = params.getString("class_id");
		String finishstep = params.getString("finishstep");
		String isupdate = params.getString("isupdate");
		String changeprice = params.getString("changeprice");
		String changestr = params.getString("changestr");

		String loginuserid = params.getString("loginuserid");
		String loginUser = params.getString("loginUser");

		String changeremark = params.getString("changeremark");

		JSONArray itemstepdata = params.getJSONArray("itemstepdata");

		String message = "";
		try {
			Statement ps = conn.createStatement();
			conn.setAutoCommit(false);

			int countbit = 0;

			Table companytalbe = DataUtils.queryData(conn, "select countbit from s_company_config where company_id='" + companyid + "'", null, null, null, null);
			if (companytalbe.getRows().size() > 0) {
				countbit = companytalbe.getRows().get(0).getInteger("countbit");
			}

			ps.addBatch("update iteminfo set class_id='" + class_id + "',m_finishstep='" + finishstep + "' " + (itemstepdata.size() > 0 ? ",hasstep=1" : "") + " where itemid='" + itemid + "'");

			ps.addBatch("delete from itemstep where itemid='" + itemid + "'");

			for (int i = 0; i < itemstepdata.size(); i++) {
				JSONObject result = JSONObject.parseObject(itemstepdata.getString(i));

				String stepremark = result.getString("step_remark");
				stepremark = (stepremark == null || stepremark.equals("null") || stepremark.equals("undefined")) ? "" : stepremark;

				ps.addBatch("INSERT INTO `itemstep` (`id`, `companyid`, `step_id`, `step_name`, `itemid`, `class_id`, `stepnewid`,`step_workshop`,`progress_scale`,`step_no`, `step_remark`, `price`, `out_price`, `create_id`, `create_by`, `create_date`, `update_id`, `update_by`, `update_date`,`progress_type`) VALUES ('"
						+ result.getString("id")
						+ "','"
						+ companyid
						+ "','"
						+ result.getString("step_id")
						+ "','"
						+ result.getString("step_name")
						+ "','"
						+ itemid
						+ "','"
						+ class_id
						+ "','"
						+ result.getString("stepnewid")
						+ "','"
						+ result.getString("step_workshop")
						+ "',"
						+ result.getDouble("progress_scale")
						+ ","
						+ result.getInteger("step_no")
						+ ",'"
						+ stepremark
						+ "',"
						+ result.getDoubleValue("price")
						+ ","
						+ result.getDoubleValue("out_price")
						+ ",'"
						+ loginuserid
						+ "','"
						+ loginUser
						+ "',now(),'"
						+ loginuserid
						+ "','"
						+ loginUser + "',now()," + result.getIntValue("progress_type") + ")");
			}

			if (isupdate.equals("true")) {
				String bb = "update t_order_detail tod set tod.p_finishstep = "
						+ (finishstep.equals("all") ? " 'all' " : " ifnull((select p.id from t_progress p where p.detail_id=tod.id and (select 1 from itemstep t where  t.id='" + finishstep
						+ "' and t.step_id=p.step_id limit 1) limit 1),'all') ") + " where  tod.itemid = '" + itemid + "' and tod.class_id = '" + class_id
						+ "'  and tod.schedulestatus='1' and   tod.t_isfinish=0 ";
				ps.addBatch(bb);

				ps.addBatch("update t_order_detail td set td.t_finishcount=round(ifnull((select min(tp.finishcount/tp.progress_scale) from t_progress tp where tp.detail_id=td.id and if(td.p_finishstep='all',1=1,tp.id=td.p_finishstep)),0),"
						+ countbit
						+ "),td.t_invailcount=round(ifnull((select sum(tp.invalid_count/tp.progress_scale) from t_progress tp where tp.detail_id=td.id and if(td.p_finishstep='all',1=1,tp.id=td.p_finishstep)),0),"
						+ countbit
						+ "), td.t_isfinish=if(td.item_count>round(td.t_finishcount+td.t_invailcount,"
						+ countbit
						+ "),0,1) where td.itemid = '"
						+ itemid
						+ "' and td.class_id = '"
						+ class_id + "'  and td.schedulestatus='1' and   td.t_isfinish=0 ");
				ps.addBatch("update t_order tr set tr.finishcount=round(ifnull((select min(if(td.t_finishcount/td.must_item_count>1,1,(td.t_finishcount/td.must_item_count))) from t_order_detail td where td.order_id=tr.id),0)*tr.max_order_count,"
						+ countbit
						+ "),tr.canincount=if(tr.finishcount>tr.incount,round(tr.finishcount-tr.incount,"
						+ countbit
						+ "),0) where tr.companyid = '"
						+ companyid
						+ "' and tr.schedulestatus='1' and tr.finishcount<tr.order_count  and (select 1 from t_order_detail where order_id=tr.id and itemid='"
						+ itemid
						+ "' and class_id='"
						+ class_id
						+ "' limit 1)");

				ps.addBatch(bb);
			}

			if (changeprice.equals("true") || changeremark.equals("true")) {
				if (changeprice.equals("true")) {
					ps.addBatch("update t_progress t,itemstep ts,t_order_detail td set t.step_price=ts.price,t.out_price=ts.out_price"
							+ (changeremark.equals("true") ? ",t.step_remark=ts.step_remark" : "") + " where t.companyid = '" + companyid + "' and td.itemid = '" + itemid
							+ "' and ts.itemid=td.itemid and ts.class_id = '" + class_id
							+ "' and ts.class_id=td.class_id and t.step_id=ts.step_id and t.detail_id=td.id and td.fstatus=1 and td.schedulestatus='1'");
					ps.addBatch("update t_order_progress t,itemstep ts,t_order_detail td set t.price=ts.price  where t.companyid = '" + companyid + "' and td.itemid = '" + itemid
							+ "' and ts.itemid=td.itemid and ts.class_id = '" + class_id
							+ "' and ts.class_id=td.class_id and t.step_id=ts.step_id  and t.detail_id=td.id and td.fstatus=1 and td.schedulestatus='1'");

				} else {
					ps.addBatch("update t_progress t,itemstep ts,t_order_detail td set t.step_remark=ts.step_remark  where t.companyid = '" + companyid + "' and td.itemid = '" + itemid
							+ "' and ts.itemid=td.itemid and ts.class_id = '" + class_id
							+ "' and ts.class_id=td.class_id and t.step_id=ts.step_id and t.detail_id=td.id and td.fstatus=1 and td.schedulestatus='1'");

				}
			}

			// 修改数据记录
			ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
					+ companyid + "',1,'修改工艺参数配置','" + itemid + "','" + changestr.replaceAll("'", "''") + "','" + loginuserid + "','" + loginUser + "',now())");

			ps.executeBatch();
			conn.commit();
			conn.setAutoCommit(true);

			ps.close();
		} catch (Exception e) {
			// System.out.println(e.getMessage().toString());
			// 若出现异常，对数据库中所有已完成的操作全部撤销，则回滚到事务开始状态
			if (!conn.isClosed()) {
				conn.rollback();// 4,当异常发生执行catch中SQLException时，记得要rollback(回滚)；
				// System.out.println("保存数据日志失败，回滚！");
				conn.setAutoCommit(true);
				message = message + " 保存数据日志失败！";
			}
			// e.printStackTrace();
			message = message + " 错误信息：" + e.getMessage().toString();
		} finally {
			conn.close();
		}
		ret.put("message", message);
		return ret;
	}

	// 批量删除商品信息，有其他表关联或有图片不能删除 执行sql语句返加执行条数。
	public static JSONObject delAllitems(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject rt = new JSONObject();
		String itemids = params.getString("itemids");
		String companyid = params.getString("companyid");
		int rowcount = 0;
		if (itemids != null && companyid != null && !itemids.equals("")) {
			try {
				// String sql =
				// "delete from iteminfo where imgurl='item.png' and itemid in ("
				// + itemids +
				// ") and itemid not in ( select itemid from itemsplits where companyid='"
				// + companyid
				// +
				// "') and itemid not in ( select itemid from houselimit where companyid='"
				// + companyid +
				// "')  and itemid not in (select itemid from salesorderdetail where companyid='"
				// + companyid +
				// "') and itemid not in (select itemid from t_order_detail where companyid='"
				// + companyid
				// +
				// "') and itemid not in (select itemid from detail_view where companyid='"
				// + companyid +
				// "')  and itemid not in (select itemid from purchaseorderdetail where companyid='"
				// + companyid +
				// "') and itemid not in (select itemid from reportlossdetail where companyid='"
				// + companyid
				// +
				// "') and itemid not in (select itemid from locationitem where companyid='"
				// + companyid + "')";

				// String sql = "delete from iteminfo where  itemid in (" +
				// itemids + ") and imgurl='item.png'  "
				// +
				// " and  (select count(*) from itemsplits where itemid = iteminfo.itemid and itemid in ("
				// + itemids + "))=0 "
				// +
				// " and  (select count(*) from houselimit where itemid = iteminfo.itemid and itemid in ("
				// + itemids + "))=0 "
				// +
				// " and  (select count(*) from salesorderdetail where itemid = iteminfo.itemid and itemid in ("
				// + itemids + "))=0 "
				// +
				// " and  (select count(*) from purchasedetail where itemid = iteminfo.itemid and itemid in ("
				// + itemids + "))=0 "
				// +
				// " and  (select count(*) from purchaseorderdetail where itemid = iteminfo.itemid and itemid in ("
				// + itemids + "))=0 "
				// +
				// " and  (select count(*) from reportlossdetail where itemid = iteminfo.itemid and itemid in ("
				// + itemids + "))=0 "
				// +
				// " and  (select count(*) from locationitem where itemid = iteminfo.itemid and itemid in ("
				// + itemids + "))=0 "
				// +
				// " and  (select count(*) from outsourcingdetail where itemid = iteminfo.itemid and itemid in ("
				// + itemids + "))=0 "
				// +
				// " and  (select count(*) from itembegindetail where itemid = iteminfo.itemid and itemid in ("
				// + itemids + "))=0 "
				// +
				// " and  (select count(*) from storeindetail where itemid = iteminfo.itemid and itemid in ("
				// + itemids + "))=0 "
				// +
				// " and  (select count(*) from storeoutdetail where itemid = iteminfo.itemid and itemid in ("
				// + itemids + "))=0 "
				// +
				// " and  (select count(*) from storemovedetail where itemid = iteminfo.itemid and itemid in ("
				// + itemids + "))=0 "
				// +
				// " and  (select count(*) from splitsdetail where itemid = iteminfo.itemid and itemid in ("
				// + itemids + "))=0 "
				// +
				// " and  (select count(*) from prodrequisitiondetail where itemid = iteminfo.itemid and itemid in ("
				// + itemids + "))=0 "
				// +
				// " and  (select count(*) from otherinoutdetail where itemid = iteminfo.itemid and itemid in ("
				// + itemids + "))=0 "
				// +
				// " and  (select count(*) from prodstoragedetail where itemid = iteminfo.itemid and itemid in ("
				// + itemids + "))=0 "
				// +
				// " and  (select count(*) from processinoutdetail where itemid = iteminfo.itemid and itemid in ("
				// + itemids + "))=0 "
				// +
				// " and  (select count(*) from outsourcingindetail where itemid = iteminfo.itemid and itemid in ("
				// + itemids + "))=0 "
				// +
				// " and  (select count(*) from t_order where itemid = iteminfo.itemid and itemid in ("
				// + itemids + "))=0 "
				// +
				// " and  (select count(*) from t_order_detail where itemid = iteminfo.itemid and itemid in ("
				// + itemids + "))=0 ";

				int m = 0;

				String[] itemidsarr = itemids.replaceAll("'", "").split(",");
				String temp = "";
				for (m = 0; m < itemidsarr.length; m++) {
					temp = temp + (temp.equals("") ? "" : ",") + "?";
				}

				String sql = "delete from iteminfo where  itemid in ("
						+ temp
						+ ") and quality = 0 and splits=0 and msplits=0 "
						+" and  if((select 1 from itemsplits where itemid = iteminfo.itemid  limit 1),false,true)  "
						+ " and  if((select 1 from quotationdetail where itemid = iteminfo.itemid limit 1),false,true)  "
						+ " and  if((select 1 from houselimit where itemid = iteminfo.itemid limit 1),false,true)  "
						+ " and  if((select 1 from salesorderdetail where itemid = iteminfo.itemid limit 1),false,true)  "
						+ " and  if((select 1 from purchasedetail where itemid = iteminfo.itemid limit 1),false,true)  "
						+ " and  if((select 1 from purchaseorderdetail where itemid = iteminfo.itemid limit 1),false,true)  "
						+ " and  if((select 1 from reportlossdetail where itemid = iteminfo.itemid limit 1),false,true)  "
						+ " and  if((select 1 from locationitem where itemid = iteminfo.itemid limit 1),false,true)  "
						+ " and  if((select 1 from outsourcingdetail where itemid = iteminfo.itemid limit 1),false,true)  "
						+ " and  if((select 1 from itemmonth where itemid = iteminfo.itemid limit 1),false,true)  "
						+ " and  if((select 1 from storeoutapplydetail where itemid = iteminfo.itemid limit 1),false,true)  "
						//+ " and  if((select 1 from itembegindetail where itemid = iteminfo.itemid limit 1),false,true)  "
						//+ " and  if((select 1 from storeindetail where itemid = iteminfo.itemid limit 1),false,true)  "
						//+ " and  if((select 1 from storeoutdetail where itemid = iteminfo.itemid limit 1),false,true)  "
						//+ " and  if((select 1 from storemovedetail where itemid = iteminfo.itemid limit 1),false,true)  "
						//+ " and  if((select 1 from splitsdetail where itemid = iteminfo.itemid limit 1),false,true)  "
						//+ " and  if((select 1 from prodrequisitiondetail where itemid = iteminfo.itemid limit 1),false,true)  "
						+ " and  if((select 1 from prodrequisition_work_total where itemid = iteminfo.itemid limit 1),false,true) "
						//+ " and  if((select 1 from otherinoutdetail where itemid = iteminfo.itemid limit 1),false,true)  "
						//+ " and  if((select 1 from prodstoragedetail where itemid = iteminfo.itemid limit 1),false,true)  "
						//+ " and  if((select 1 from processinoutdetail where itemid = iteminfo.itemid limit 1),false,true)  "
						//+ " and  if((select 1 from outsourcingindetail where itemid = iteminfo.itemid limit 1),false,true)  "
						+ " and  if((select 1 from t_order where itemid = iteminfo.itemid limit 1),false,true) "
						+ " and  if((select 1 from t_order_detail where itemid = iteminfo.itemid limit 1),false,true)  "
						+ " and  if((select 1 from invoicestoreindetail where itemid = iteminfo.itemid limit 1),false,true)  ";

				PreparedStatement ps = conn.prepareStatement(sql);
				for (m = 0; m < itemidsarr.length; m++) {
					ps.setString(m + 1, itemidsarr[m]);
				}

				ps.execute();

				rowcount = ps.getUpdateCount();
				sql = "delete from itemsplits where companyid=? and if((select 1 from iteminfo where itemid = itemsplits.combitemid limit 1),false,true)  ";
				ps = conn.prepareStatement(sql);
				ps.setString(1, companyid);
				ps.execute();

				sql = "delete from itemstep where companyid=? and if((select 1 from iteminfo where itemid=itemstep.itemid limit 1),false,true) ";
				ps = conn.prepareStatement(sql);
				ps.setString(1, companyid);
				ps.execute();

				sql = "delete from t_file where companyid=? and fstatus=3  and if((select 1 from iteminfo where itemid=t_file.detail_id limit 1),false,true) ";
				ps = conn.prepareStatement(sql);
				ps.setString(1, companyid);
				ps.execute();

			} catch (Exception e) {
				rowcount = 0;
			} finally {
				conn.close();
			}
		}
		rt.put("rowcount", rowcount);
		return rt;
	}

	// 保存往来单位信息表
	public static JSONObject saveCustomerFunction(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject rt = new JSONObject();
		String customer = params.getString("customer");
		String pcustomercode = params.getString("customercode");
		String operate = params.getString("operate");
		String companyid = params.getString("companyid");
		String change = params.getString("change");
		String loginUser = params.getString("loginUser");
		String loginuserid = params.getString("loginuserid");
		String customerid = params.getString("customerid");
		String oldcustomercode = params.getString("oldcustomercode");
		String customerstr = params.getString("customerstr");

		try {

			Statement ps = conn.createStatement();
			conn.setAutoCommit(false);
			String customercode = pcustomercode.equals("") ? Pdacommon.getCodeByparams(companyid, "customer", "customercode", conn) : pcustomercode;

			if (operate.equals("edit")) {
				ps.addBatch("update  customer set " + customer
						+ (!pcustomercode.equals("") && oldcustomercode.equals(pcustomercode) ? "" : (customer.equals("") ? "" : ",") + "customercode='" + customercode + "'") + " where customerid='"
						+ customerid + "'");

				String content = "";
				if (!oldcustomercode.equals(pcustomercode)) {
					content = "【单位编号】原值《" + oldcustomercode + "》" + (pcustomercode.equals("") ? "因修改为空值，自动生成编号" : "") + "改变为《" + customercode + "》" + (change.equals("") ? "" : "；" + change);
				} else {
					content = change;
				}

				ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
						+ companyid + "',5,'修改','" + customerid + "','" + customerstr + content + "','" + loginuserid + "','" + loginUser + "',now())");

			} else {
				ps.addBatch("insert into customer (customercode,customerid,companyid,customername,mcode,typeid,customerlinkname,customerphone,customeremail,customeraddress,nature,bank,bankno,selltype,staff,property1,property2,property3,property4,property5,remark,role,status,beginreceivable,beginpayable,receivable,payable,T_beginreceivable,T_beginpayable,TN_beginreceivable,TN_beginpayable,T_receivable,T_payable,update_id_begin,update_by_begin,update_time_begin,create_id,create_by,create_time,update_id,update_by,update_time,c_rate) VALUES ('"
						+ (pcustomercode.equals("") ? customercode : pcustomercode) + customer);

				ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
						+ companyid + "',5,'新增','" + customerid + "','单位编号：" + (pcustomercode.equals("") ? customercode : pcustomercode) + "','" + loginuserid + "','" + loginUser + "',now())");

			}

			ps.executeBatch();
			conn.commit();
			conn.setAutoCommit(true);
			rt.put("state", "1");
		} catch (Exception e) {
			// e.printStackTrace();
			try {
				conn.rollback();
				rt.put("state", "0");
			} catch (Exception e1) {
				// e1.printStackTrace();
				rt.put("state", "0");
			}

		} finally {
			conn.close();
		}
		return rt;
	}

	// 批量删除往来单位信息，有其他表关联不能删除 执行sql语句返加执行条数。
	public static JSONObject delAllcustomers(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject rt = new JSONObject();
		String customerids = params.getString("customerids");
		String companyid = params.getString("companyid");
		int rowcount = 0;
		if (customerids != null && companyid != null) {
			try {
				String sql = "delete from customer where  customerid in ("
						+ customerids
						+ ") and if((select 1 from purchaseorder where customerid=customer.customerid limit 1),false,true)  and if((select 1 from salesorder where  customerid=customer.customerid limit 1),false,true)  and if((select 1 from accountbill where  customerid=customer.customerid limit 1),false,true)  and if((select 1 from main_view where  customerid=customer.customerid limit 1),false,true) ";

				PreparedStatement ps = conn.prepareStatement(sql);
				ps.execute();
				rowcount = ps.getUpdateCount();
			} catch (Exception e) {
				rowcount = 0;
			} finally {
				conn.close();
			}
		}
		rt.put("rowcount", rowcount);
		return rt;
	}

	// 保存入库单信息
	public static JSONObject saveStoreInFunction(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject rt = new JSONObject();
		JSONObject maindata = JSONObject.parseObject(params.getString("maindata"));
		Integer type = params.getInteger("type");
		Integer pricebit = params.getInteger("pricebit");
		String loginUser = params.getString("loginUser");
		String loginuserid = params.getString("loginuserid");
		JSONArray detaildata = params.getJSONArray("detaildata");
		String operate = params.getString("operate");// draftedit 编辑单据
		String state = "0";
		String message = "";

		String companyid = maindata.getString("companyid");
		String houseid = maindata.getString("houseid");
		String customerid = maindata.getString("customerid");
		String operate_time = maindata.getString("operate_time");
		double count = maindata.getDoubleValue("count");
		double total = maindata.getDoubleValue("total");
		double totaltax = maindata.getDoubleValue("totaltax");
		double totalmoney = maindata.getDoubleValue("totalmoney");
		int status = type == 0 ? 0 : 1;

		String[] sdatearr = operate_time.split("-");
		int syear = Integer.parseInt(sdatearr[0]);
		int smonth = Integer.parseInt(sdatearr[1]);
		String sdate = sdatearr[0] + "-" + sdatearr[1] + "-01";

		String billdate = sdatearr[0] + sdatearr[1] + sdatearr[2];

		JSONArray Bhouselimit = new JSONArray();
		StringBuffer condition = new StringBuffer();

		JSONArray checkdata = params.getJSONArray("checkdata");

		double totaltaxmoney = 0;

		try {
			int countbit = 0;
			int moneybit = 2;
			int t6days = 0;

			Table companytalbe = DataUtils.queryData(conn, "select pricebit,countbit,moneybit,t6days from s_company_config where company_id='" + companyid + "'", null, null, null, null);
			if (companytalbe.getRows().size() > 0) {
				countbit = companytalbe.getRows().get(0).getInteger("countbit");
				moneybit = companytalbe.getRows().get(0).getInteger("moneybit");
				t6days = companytalbe.getRows().get(0).getInteger("t6days");
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
			conn.setAutoCommit(false);
			String detail = "insert into storeindetail (orderid,detailid,storeinid,originalbill,goods_number,companyid,operate_by,operate_time,itemid,customerid,houseid,price,count,total,tax,taxrate,taxprice,taxmoney,stype,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,batchno,relationdetailid,relationorderid,relationmainid,isshowwarn) VALUES ('";
			String store = "insert into stock (stockid,companyid,itemid,houseid,count,money,newcostprice,batchno,stockremark) VALUES ('";
			String itemmonth = "insert into itemmonth (monthid,companyid,itemid,houseid,sdate,syear,smonth,count,money,totalcount,totalmoney,batchno) VALUES ('";
			// 订单月报表
			String ordermonth = "insert into ordermonth (monthid,companyid,itemid,sdate,syear,smonth,purchaseincount,purchaseinmoney,batchno) VALUES ('";

			String orderid = operate.equals("draftedit") ? maindata.getString("orderid") : getOrderidByparams(companyid, "storein", "1", billdate, conn);
			int i = 0;
			String details = "";

			if (save && operate.equals("draftedit")) {// 草稿编辑 删除明细数据重新增加
				String fsql = "select status from storein where storeinid='" + maindata.getString("storeinid") + "'";
				Object cobject = DataUtils.getValueBySQL(conn, fsql, null);
				if (cobject == null) {
					save = false;
					message = "当前记录已删除，操作失败。";
					state = "2";
				} else {
					String fstatus = cobject.toString();
					if (fstatus.equals("0")) {
						ps.addBatch("delete from storeindetail where storeinid='" + maindata.getString("storeinid") + "'");
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
			String relationmainids = "";// 采购订单主表
			if (detaildata.size() > 0) {
				if (save) {
					for (i = 0; i < detaildata.size(); i++) {
						JSONObject result = JSONObject.parseObject(detaildata.getString(i));
						String detailid = result.getString("detailid");
						String batchno = result.getString("batchno");
						String itemid = result.getString("itemid");

						double dcount = result.getDoubleValue("count");
						double price = (dcount == 0 ? 0 : result.getDoubleValue("price"));
						double dtotal = result.getDoubleValue("total");
						String relationdetailid = result.getString("relationdetailid");// 采购订单明细ID
						String relationorderid = result.getString("relationorderid");// 采购订单订单编号
						String relationmainid = result.getString("relationmainid");// 采购订单订单主表ID

						double taxprice = (price == 0 ? 0 : result.getDoubleValue("taxprice"));

						if (!relationmainid.equals("") && relationmainids.indexOf(relationmainid) == -1) {// 获取唯一订单主表ID
							relationmainids = relationmainids + (relationmainids.equals("") ? "" : ",") + relationmainid;
						}

						if (type > 0) {
							details = store + Common.getUpperUUIDString() + "','" + companyid + "','" + itemid + "','" + houseid + "'," + dcount + "," + dtotal + ",round("
									+ (dcount > 0 ? dtotal / dcount : 0) + "," + pricebit + "),'" + batchno + "','" + result.getString("remark") + "') on duplicate key update count=round(count+"
									+ dcount + "," + countbit + "),money=round(money+" + dtotal + "," + moneybit + ") ,newcostprice=round(if(count=0,newcostprice,money/count)," + pricebit + ")"
									+ (result.getString("remark").equals("") ? "" : ",stockremark='" + result.getString("remark") + "'");

							ps.addBatch(details);
							// System.out.println("saveStoreInFunction " +
							// details);

							details = itemmonth + Common.getUpperUUIDString() + "','" + companyid + "','" + itemid + "','" + houseid + "','" + sdate + "'," + syear + "," + smonth + "," + dcount + ","
									+ dtotal + "," + dcount + "," + dtotal + ",'" + batchno + "') on duplicate key update count=round(count+" + dcount + "," + countbit + "),money=round(money+"
									+ dtotal + "," + moneybit + "),totalcount=round(totalcount+" + dcount + "," + countbit + "),totalmoney=round(totalmoney+" + dtotal + "," + moneybit + ") ";
							ps.addBatch(details);

							String storeInPriceSql = " INSERT INTO `storeinprice` (" +
									"    `storeinpriceid`, `companyid`, `itemid`, `customerid`, `newprice`," +
									"    `newdate`, `minprice`, `mindate`, `maxprice`, `maxdate`" +
									"  )" +
									"  VALUES (" +
									"    ?, ?, ?, ?, ?," +
									"    ?, ?, ?, ?, ?" +
									"  ) " +
									" ON DUPLICATE KEY UPDATE " +
									" newprice = IF(STRCMP(?, newdate) >= 0 OR newdate IS NULL, ?, newprice), " +
									" newdate = IF(STRCMP(?, newdate) >= 0 OR newdate IS NULL, ?, newdate), " +
									" minprice = IF(minprice > ? OR minprice = -1, ?, minprice), " +
									" mindate = IF(minprice > ? OR mindate IS NULL, ?, mindate), " +
									" maxprice = IF(maxprice < ?, ?, maxprice), " +
									" maxdate = IF(maxprice < ?, ?, maxdate)";
							PreparedStatement preparedStatement = conn.prepareStatement(storeInPriceSql);
							preparedStatement.setString(1, Common.getUpperUUIDString());
							preparedStatement.setString(2, companyid);
							preparedStatement.setString(3, itemid);
							preparedStatement.setString(4, customerid);
							preparedStatement.setDouble(5, taxprice);
							preparedStatement.setString(6, billdate);
							preparedStatement.setDouble(7, taxprice);
							preparedStatement.setString(8, billdate);
							preparedStatement.setDouble(9, taxprice);
							preparedStatement.setString(10, billdate);
							// update
							preparedStatement.setString(11, billdate);
							preparedStatement.setDouble(12, taxprice);
							preparedStatement.setString(13, billdate);
							preparedStatement.setString(14, billdate);
							preparedStatement.setDouble(15, taxprice);
							preparedStatement.setDouble(16, taxprice);
							preparedStatement.setDouble(17, taxprice);
							preparedStatement.setString(18, billdate);
							preparedStatement.setDouble(19, taxprice);
							preparedStatement.setDouble(20, taxprice);
							preparedStatement.setDouble(21, taxprice);
							preparedStatement.setString(22, billdate);
							preparedStatement.executeUpdate();

							if (!relationdetailid.equals("")) {// 增加修改采购订单入库数量且订单月报表

								ps.addBatch("update purchaseorderdetail set incount=round(incount+" + dcount + "," + countbit + "),intotal=round(intotal+" + dtotal + "," + moneybit
										+ ") where detailid='" + relationdetailid + "'");
								ps.addBatch("update purchaseorder set incount=round(incount+" + dcount + "," + countbit + "),intotal=round(intotal+" + dtotal + "," + moneybit + ")"
										+ " where   (select 1 from purchaseorderdetail d where d.purchaseorderid=purchaseorder.purchaseorderid and  d.detailid='" + relationdetailid + "' limit 1)");
								details = ordermonth + Common.getUpperUUIDString() + "','" + companyid + "','" + itemid + "','" + sdate + "'," + syear + "," + smonth + "," + dcount + "," + dtotal
										+ ",'" + batchno + "') on duplicate key update purchaseincount=round(purchaseincount+" + dcount + "," + countbit + "),purchaseinmoney=round(purchaseinmoney+"
										+ dtotal + "," + moneybit + ")";
								ps.addBatch(details);
							}

							String temp = "('" + itemid + "','" + houseid + "')";
							if (condition.toString().equals("")) {
								condition.append(temp);
							} else {
								if (condition.toString().indexOf(temp) == -1) {
									condition.append("," + temp);
								}
							}

						}

						// 质检
						String isshowwarn = "0";
						if (!detailid.equals("")) {
							Object sObject = DataUtils.getValueBySQL(conn, "select isshowwarn from storeindetail where detailid='" + detailid + "'", null);
							if (sObject != null) {
								isshowwarn = sObject.toString();
							}
						}
						// System.out.println(detailid+" "+isshowwarn);

						details = detail + orderid + "','" + (detailid.equals("") ? Common.getUpperUUIDString() : detailid) + "','" + maindata.getString("storeinid") + "','"
								+ maindata.getString("originalbill") + "'," + result.getInteger("goods_number") + ",'" + companyid + "','" + maindata.getString("operate_by") + "','"
								+ maindata.getString("operate_time") + "','" + itemid + "','" + customerid + "','" + houseid + "'," + price + "," + dcount + "," + dtotal + ","
								+ result.getDoubleValue("tax") + "," + result.getDoubleValue("taxrate") + "," + (price == 0 ? 0 : result.getDoubleValue("taxprice")) + ","
								+ result.getDoubleValue("taxmoney") + ",'11','" + result.getString("remark") + "','" + status + "','"
								+ (detailid.equals("") ? loginuserid : result.getString("create_id")) + "','" + (detailid.equals("") ? loginUser : result.getString("create_by")) + "',"
								+ (detailid.equals("") ? "now()" : "'" + result.getString("create_time") + "'") + ",'" + loginuserid + "','" + loginUser + "',now(),'" + batchno + "','"
								+ relationdetailid + "','" + relationorderid + "','" + relationmainid + "'," + isshowwarn + ")";

						ps.addBatch(details);

						if (type > 0 && result.getDoubleValue("tax") != 0) {
							totaltaxmoney = Pdacommon.adddouble(totaltaxmoney, result.getDoubleValue("taxmoney"));
						}

					}

				}

				if (type > 0 && !relationmainids.equals("")) {// 更改等待入库的采购订单的入库状态，符合改为已完成入库
					String[] farr = relationmainids.split(",");
					for (int j = 0; j < farr.length; j++) {

						ps.addBatch("update purchaseorder set stockstatus = '1' where purchaseorderid='" + farr[j]
								+ "' and (stockstatus='0' or stockstatus='-1')  and status='1'  and if((select 1 from purchaseorderdetail where purchaseorderid='" + farr[j]
								+ "' and count>incount and status='1' limit 1),false,true)");

						ps.addBatch("update purchaseorder set stockstatus = '-1' where purchaseorderid='" + farr[j] + "' and stockstatus<>'3'  and status='1' and incount>0 and"
								+ "(select count(detailid) from purchaseorderdetail where purchaseorderid='" + farr[j]
								+ "' and incount>=count and status='1')<(select count(detailid) from purchaseorderdetail where purchaseorderid='" + farr[j] + "' and status='1') ");
						// ps.addBatch("update purchaseorder set stockstatus = '-1' where purchaseorderid='"
						// + farr[j] +
						// "' and stockstatus='0' and status='1' and "
						// +
						// "(select count(detailid) from purchaseorderdetail where purchaseorderid='"
						// + farr[j] +
						// "' and incount>0 and count>incount and status='1')>0 ");

					}
				}
				if (type > 0) {
					for (i = 0; i < checkdata.size(); i++) {
						JSONObject checkresult = JSONObject.parseObject(checkdata.getString(i));
						String fhouseid = checkresult.getString("houseid");
						String fbatchno = checkresult.getString("batchno");
						double ftotalcount = checkresult.getDoubleValue("totalcount");
						String fitemid = checkresult.getString("itemid");
						String fcodeid = checkresult.getString("codeid");

						// companyid='" + companyid + "' and
						String checksql = "select count,round(count-checkout_count," + countbit + ") as cancount from stock where  itemid='" + fitemid + "' and houseid='" + fhouseid + "' "
								+ " and batchno='" + fbatchno + "'";

						Table ftable = DataUtils.queryData(conn, checksql, null, null, null, null);
						// 2020-12-10 涉及到仓库没有数据问题。
						double fcancount = 0;
						if (ftable.getRows().size() > 0) {
							fcancount = Double.parseDouble(ftable.getRows().get(0).getValue("cancount").toString());
							if (fcancount < checkresult.getDoubleValue("totalcount")) {
								message = message + "(" + fcodeid + "、" + checkresult.getString("itemname") + "、" + checkresult.getString("housename") + " " + fbatchno + "《总需减的库存数量》："
										+ checkresult.getDoubleValue("totalcount") + " 大于 《最大可减的库存数量》：" + fcancount + ")";
								save = false;
							}
						} else {// 2020-12-10 增加
							message = message + "(" + fcodeid + "、" + checkresult.getString("itemname") + "、" + checkresult.getString("housename") + " " + fbatchno + "《总需减的库存数量》："
									+ checkresult.getDoubleValue("totalcount") + " 大于 《最大可减的库存数量》：" + fcancount + ")";
							save = false;
						}
					}

					if (!message.equals(""))
						message = message + "，保存失败";
				}

			} else {
				message = message + "没有商品明细数据，操作失败";
				save = false;
			}
			if (save) {

				if (type > 0 && totalmoney != 0) {
					// 更新客户应收应付款
					String customersql = "update customer set payable=round(payable+" + totalmoney + "," + moneybit + ")"
							+ (totaltaxmoney != 0 ? ",T_payable=round(T_payable+" + totaltaxmoney + "," + moneybit + ")" : "") + " where companyid='" + companyid + "' and customerid='" + customerid
							+ "'";
					ps.addBatch(customersql);

					// 增加往来单位月收支报表
					String customermonthsql = "insert into customermonth (monthid,companyid,customerid,sdate,syear,smonth,payable,pay_purchasein_money,pay_add_money"
							+ (totaltaxmoney != 0 ? ",T_payable,T_pay_purchasein_money,T_pay_add_money" : "")
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
							+ totalmoney
							+ ","
							+ totalmoney
							+ ","
							+ totalmoney
							+ (totaltaxmoney != 0 ? ("," + totaltaxmoney + "," + totaltaxmoney + "," + totaltaxmoney) : "")
							+ ") on duplicate key update payable=round(payable+"
							+ totalmoney
							+ ","
							+ moneybit
							+ "),pay_purchasein_money=round(pay_purchasein_money+"
							+ totalmoney
							+ ","
							+ moneybit
							+ "),pay_add_money=round(pay_add_money+"
							+ totalmoney
							+ ","
							+ moneybit
							+ ")"
							+ (totaltaxmoney != 0 ? ",T_payable=round(T_payable+" + totaltaxmoney + "," + moneybit + "),T_pay_purchasein_money=round(T_pay_purchasein_money+" + totaltaxmoney + ","
							+ moneybit + "),T_pay_add_money=round(T_pay_add_money+" + totaltaxmoney + "," + moneybit + ")" : "");
					ps.addBatch(customermonthsql);

					// 增加往来单位年报表
					String customeryearsql = "insert into customeryear (yearid,companyid,customerid,syear,payable,pay_purchasein_money,pay_add_money"
							+ (totaltaxmoney != 0 ? ",T_payable,T_pay_purchasein_money,T_pay_add_money" : "")
							+ ") values('"
							+ Common.getUpperUUIDString()
							+ "','"
							+ companyid
							+ "','"
							+ customerid
							+ "',"
							+ syear
							+ ","
							+ totalmoney
							+ ","
							+ totalmoney
							+ ","
							+ totalmoney
							+ (totaltaxmoney != 0 ? ("," + totaltaxmoney + "," + totaltaxmoney + "," + totaltaxmoney) : "")
							+ ") on duplicate key update payable=round(payable+"
							+ totalmoney
							+ ","
							+ moneybit
							+ ")"
							+ ",pay_purchasein_money=round(pay_purchasein_money+"
							+ totalmoney
							+ ","
							+ moneybit
							+ "),pay_add_money=round(pay_add_money+"
							+ totalmoney
							+ ","
							+ moneybit
							+ ")"
							+ (totaltaxmoney != 0 ? ",T_payable=round(T_payable+" + totaltaxmoney + "," + moneybit + "),T_pay_purchasein_money=round(T_pay_purchasein_money+" + totaltaxmoney + ","
							+ moneybit + "),T_pay_add_money=round(T_pay_add_money+" + totaltaxmoney + "," + moneybit + ")" : "");
					ps.addBatch(customeryearsql);
				}

				int changebilltype = Pdacommon.getDatalogBillChangefunc("storein", "");
				if (operate.equals("draftedit")) {// 草稿编辑 更新
					ps.addBatch("update storein set   orderid='" + orderid + "',originalbill='" + maindata.getString("originalbill") + "',operate_time='" + operate_time + "',operate_by='"
							+ maindata.getString("operate_by") + "',houseid='" + houseid + "',customerid='" + customerid + "',count=" + count + ",total=" + total + ",totaltax=" + totaltax
							+ ",totalmoney=" + totalmoney + ",remark='" + maindata.getString("remark") + "',status='" + status + "',update_id='" + loginuserid + "',update_by='" + loginUser
							+ "',update_time=now(),iproperty='" + maindata.getString("iproperty") + "' where storeinid='" + maindata.getString("storeinid") + "'");

					ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
							+ companyid + "'," + changebilltype + ",'修改','" + maindata.getString("storeinid") + "','单据编号：" + orderid + "','" + loginuserid + "','" + loginUser + "',now())");

					ps.addBatch("update storeindetail d,storein s set d.create_id=s.create_id,d.create_by=s.create_by,d.create_time=s.create_time where d.storeinid = s.storeinid and s.storeinid='"
							+ maindata.getString("storeinid") + "'");
				} else {
					String main = "insert into storein (orderid,storeinid,bill_type,originalbill,companyid,operate_time,operate_by,houseid,customerid,count,total,totaltax,totalmoney,remark,status,printing,outexcel,create_id,create_by,create_time,update_id,update_by,update_time,iproperty) VALUES ('"
							+ orderid
							+ "','"
							+ maindata.getString("storeinid")
							+ "','"
							+ maindata.getString("bill_type")
							+ "','"
							+ maindata.getString("originalbill")
							+ "','"
							+ companyid
							+ "','"
							+ operate_time
							+ "','"
							+ maindata.getString("operate_by")
							+ "','"
							+ houseid
							+ "','"
							+ customerid
							+ "',"
							+ count
							+ ","
							+ total
							+ ","
							+ totaltax
							+ ","
							+ totalmoney
							+ ",'"
							+ maindata.getString("remark")
							+ "','"
							+ status
							+ "',0,0,'"
							+ loginuserid
							+ "','"
							+ loginUser
							+ "',now(),'"
							+ loginuserid
							+ "','"
							+ loginUser
							+ "',now(),'"
							+ maindata.getString("iproperty") + "')";
					ps.addBatch(main);

					ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
							+ companyid + "'," + changebilltype + ",'" + (status == 0 ? "新增暂存" : "新增保存") + "','" + maindata.getString("storeinid") + "','单据编号：" + orderid + "','" + loginuserid + "','"
							+ loginUser + "',now())");

				}

				// 检验质检
				ps.addBatch("update storeindetail set isquality=ifnull((select count(*) from  qualitymain where relationdetailid=storeindetail.detailid and relationdetailtatble='storeindetail' and status<>2  ),0)  where storeinid='"
						+ maindata.getString("storeinid") + "'");

				ps.executeBatch();
				conn.commit();
				conn.setAutoCommit(true);
				state = "1";

				if (type > 0) {
					// 库存报警
					Bhouselimit = Common.gethouselimitdata(conn, companyid, condition, Bhouselimit);
				}
			}

		} catch (Exception e) {
			message = message + "操作失败，请稍后再试。";
			// e.printStackTrace();
			try {
				conn.rollback();
			} catch (Exception e1) {
				// e1.printStackTrace();
			}
		} finally {
			condition.setLength(0);
			conn.close();
		}
		rt.put("state", state);
		rt.put("message", message);
		rt.put("warning", Bhouselimit);
		return rt;
	}

	// 保存入库单信息
	public static JSONObject saveStoreInFunction_01(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject rt = new JSONObject();
		JSONObject maindata = JSONObject.parseObject(params.getString("maindata"));
		Integer type = params.getInteger("type");
		Integer pricebit = params.getInteger("pricebit");
		String loginUser = params.getString("loginUser");
		String loginuserid = params.getString("loginuserid");
		JSONArray detaildata = params.getJSONArray("detaildata");
		String operate = params.getString("operate");// draftedit 编辑单据
		String state = "0";
		String message = "";

		String companyid = maindata.getString("companyid");
		String houseid = maindata.getString("houseid");
		String customerid = maindata.getString("customerid");
		String operate_time = maindata.getString("operate_time");
		double count = maindata.getDoubleValue("count");
		double total = maindata.getDoubleValue("total");
		double totaltax = maindata.getDoubleValue("totaltax");
		double totalmoney = maindata.getDoubleValue("totalmoney");
		int status = type == 0 ? 0 : 1;

		String[] sdatearr = operate_time.split("-");
		int syear = Integer.parseInt(sdatearr[0]);
		int smonth = Integer.parseInt(sdatearr[1]);
		String sdate = sdatearr[0] + "-" + sdatearr[1] + "-01";

		String billdate = sdatearr[0] + sdatearr[1] + sdatearr[2];

		JSONArray Bhouselimit = new JSONArray();
		StringBuffer condition = new StringBuffer();

		JSONArray checkdata = params.getJSONArray("checkdata");

		double totaltaxmoney = 0;

		try {
			int countbit = 0;
			int moneybit = 2;
			int t6days = 0;

			Table companytalbe = DataUtils.queryData(conn, "select pricebit,countbit,moneybit,t6days from s_company_config where company_id='" + companyid + "'", null, null, null, null);
			if (companytalbe.getRows().size() > 0) {
				countbit = companytalbe.getRows().get(0).getInteger("countbit");
				moneybit = companytalbe.getRows().get(0).getInteger("moneybit");
				t6days = companytalbe.getRows().get(0).getInteger("t6days");
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
			Statement ps1 = conn.createStatement();
			conn.setAutoCommit(false);
			String detail = "insert into storeindetail (orderid,detailid,storeinid,originalbill,goods_number,companyid,operate_by,operate_time,itemid,customerid,houseid,price,count,total,tax,taxrate,taxprice,taxmoney,stype,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,batchno,relationdetailid,relationorderid,relationmainid) VALUES ('";
			String store = "insert into stock (stockid,companyid,itemid,houseid,count,money,newcostprice,batchno,stockremark) VALUES ('";
			String itemmonth = "insert into itemmonth (monthid,companyid,itemid,houseid,sdate,syear,smonth,count,money,totalcount,totalmoney,batchno) VALUES ('";
			// 订单月报表
			String ordermonth = "insert into ordermonth (monthid,companyid,itemid,sdate,syear,smonth,purchaseincount,purchaseinmoney,batchno) VALUES ('";

			String orderid = operate.equals("draftedit") ? maindata.getString("orderid") : getOrderidByparams(companyid, "storein", "1", billdate, conn);
			int i = 0;
			String details = "";
			if (save && operate.equals("draftedit")) {// 草稿编辑 删除明细数据重新增加
				String fsql = "select status from storein where storeinid='" + maindata.getString("storeinid") + "'";
				Object cobject = DataUtils.getValueBySQL(conn, fsql, null);
				if (cobject == null) {
					save = false;
					message = "当前记录已删除，操作失败。";
					state = "2";
				} else {
					String fstatus = cobject.toString();
					if (fstatus.equals("0")) {
						ps.addBatch("delete from storeindetail where storeinid='" + maindata.getString("storeinid") + "'");
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
			String relationmainids = "";// 采购订单主表
			if (detaildata.size() > 0) {
				if (save) {
					for (i = 0; i < detaildata.size(); i++) {
						JSONObject result = JSONObject.parseObject(detaildata.getString(i));
						String detailid = result.getString("detailid");
						String batchno = result.getString("batchno");
						String itemid = result.getString("itemid");

						double dcount = result.getDoubleValue("count");
						double price = (dcount == 0 ? 0 : result.getDoubleValue("price"));
						double dtotal = result.getDoubleValue("total");
						String relationdetailid = result.getString("relationdetailid");// 采购订单明细ID
						String relationorderid = result.getString("relationorderid");// 采购订单订单编号
						String relationmainid = result.getString("relationmainid");// 采购订单订单主表ID

						String newrecode = ((detailid.equals("") || result.getString("newrecode") == null) ? "yes" : result.getString("newrecode"));

						String mcode = result.getString("mcode");

						mcode = ((mcode == null || mcode.equals("null")) ? "" : mcode);

						String itemname = result.getString("itemname");
						String sformat = result.getString("sformat");
						String unit = result.getString("unit");
						String property1 = result.getString("property1");
						String property2 = result.getString("property2");
						String property3 = result.getString("property3");
						String property4 = result.getString("property4");
						String property5 = result.getString("property5");
						String codeid = result.getString("codeid");
						String barcode = (result.getString("barcode").trim()).replaceAll("\n", "");
						boolean newmark = result.getBooleanValue("newmark");
						String classid = result.getString("classid");

						// 验证新商品是否已存在
						if (newmark == true) {// count(itemid) as itemcount,
							String iteminfochecksql = "select itemid,codeid,barcode from iteminfo where companyid='" + companyid + "' and itemname='" + itemname.replaceAll("'", "''")
									+ "' and sformat='" + sformat.replaceAll("'", "''") + "' and unit='" + unit + "' and property1='" + property1.replaceAll("'", "''") + "'" + " and property2 = '"
									+ property2.replaceAll("'", "''") + "' and property3 = '" + property3.replaceAll("'", "''") + "' and property4 = '" + property4.replaceAll("'", "''")
									+ "' and property5 = '" + property5.replaceAll("'", "''") + "'";
							Table itemTable = DataUtils.queryData(conn, iteminfochecksql, null, null, null, null);

							if (itemTable.getRows().size() > 0) {
								boolean hasinfo = false;
								if (!codeid.equals("")) {
									// 查出重复的商品，使用原商品的itemid
									Iterator<Row> iteratordata = itemTable.getRows().iterator();
									while (iteratordata.hasNext()) {
										Row info = iteratordata.next();
										if (codeid.equals(info.getString("codeid"))) {
											itemid = info.getString("itemid");
											codeid = info.getString("codeid");
											barcode = info.getString("barcode");
											hasinfo = true;
											break;
										}
									}
								}
								if (hasinfo == false) {
									boolean hascodeid = false;
									String fcodeidchecksql = "select count(*) from iteminfo where companyid='" + companyid + "' and codeid='" + codeid + "'";
									Object fcodeidcheckcount = DataUtils.getValueBySQL(conn, fcodeidchecksql, null);
									if (fcodeidcheckcount != null) {
										Long fcodeidcount = (Long) fcodeidcheckcount;
										if (fcodeidcount > 0) {
											hascodeid = true;
										}
									}
									if (hascodeid) {
										itemid = itemTable.getRows().get(0).getString("itemid");
										codeid = itemTable.getRows().get(0).getString("codeid");
										barcode = itemTable.getRows().get(0).getString("barcode");
										newmark = false;
									} else {
										itemid = Common.getUpperUUIDString();
										// 验证商品码 (商品码重复需重新编码 ， 为空不处理)
										if (!barcode.equals("")) {
											String barcodechecksql = "select count(*) from iteminfo where companyid='" + companyid + "' and barcode='" + barcode + "'";
											Object barcodecheckcount = DataUtils.getValueBySQL(conn, barcodechecksql, null);
											if (barcodecheckcount != null) {
												Long barcodecount = (Long) barcodecheckcount;
												// 已存在商品码，重新编写一个
												if (barcodecount > 0) {
													String coderandom = "69";
													for (int c = 0; c < 11; c++) {
														String code = Math.floor(Math.random() * 10) + "";
														coderandom = coderandom + (code + "").substring(0, (code).indexOf("."));
													}
													barcode = coderandom;
												}
											}
										}
									}
								} else {
									newmark = false;
								}

								// 验证商品分类（已存在不处理，不同时更新商品基础里面分类:以最后选择的classid为标准更新）
								if (!classid.equals("")) {
									// System.out.println(classid);
									String classidchecksql = "select count(*) from iteminfo where itemid = '" + itemid + "' and classid='" + classid + "'";
									Object classidcheckcount = DataUtils.getValueBySQL(conn, classidchecksql, null);
									if (classidcheckcount != null) {
										Long classidcount = (Long) classidcheckcount;
										if (classidcount <= 0) {
											String itemclassupdate = "update iteminfo set classid= '" + classid + "' where itemid='" + itemid + "'";
											ps1.addBatch(itemclassupdate);
											ps1.executeBatch();
										}
									}
								}

							} else {
								itemid = Common.getUpperUUIDString();
								// 验证商品码 (商品码重复需重新编码 ， 为空不处理)
								if (!barcode.equals("")) {
									String barcodechecksql = "select count(*) from iteminfo where companyid='" + companyid + "' and barcode='" + barcode + "'";
									Object barcodecheckcount = DataUtils.getValueBySQL(conn, barcodechecksql, null);
									if (barcodecheckcount != null) {
										Long barcodecount = (Long) barcodecheckcount;
										// 已存在商品码，重新编写一个
										if (barcodecount > 0) {
											String coderandom = "69";
											for (int c = 0; c < 11; c++) {
												String code = Math.floor(Math.random() * 10) + "";
												coderandom = coderandom + (code + "").substring(0, (code).indexOf("."));
											}
											barcode = coderandom;
										}
									}
								}
								// 验证商品编码 (编码为空或者重复都重新编码)
								boolean rcodeid = false;

								if (!codeid.equals("")) {
									String codeidchecksql = "select count(*) from iteminfo where companyid='" + companyid + "' and codeid='" + codeid + "'";
									Object codeidcheckcount = DataUtils.getValueBySQL(conn, codeidchecksql, null);
									if (codeidcheckcount != null) {
										Long codeidcount = (Long) codeidcheckcount;
										if (codeidcount > 0) {
											rcodeid = true;
										}
									}
								}
								if (codeid.equals("") || rcodeid) {
									codeid = Pdacommon.getCodeByparams(companyid, "iteminfo", "codeid", conn);
								}
							}
						}
						// 新建新的商品信息
						if (newmark) {
							String iteminfosql = "insert into iteminfo (itemid,companyid,codeid,itemname,sformat,mcode,classid,unit,imgurl,inprice,outprice,barcode,remark,status,property1,property2,property3,property4,property5"
									+ ",create_id,create_by,create_time,update_id,update_by,update_time,outprice1,outprice2,outprice3,outprice4,outprice5,class_id,splits,msplits,customerid) values ('"
									+ itemid
									+ "','"
									+ companyid
									+ "','"
									+ codeid.replaceAll("'", "''")
									+ "','"
									+ itemname.replaceAll("'", "''")
									+ "','"
									+ sformat.replaceAll("'", "''")
									+ "','"
									+ mcode
									+ "','"
									+ classid
									+ "','"
									+ unit
									+ "','item.png',0,0,'"
									+ barcode
									+ "','','1','"
									+ property1.replaceAll("'", "''")
									+ "','"
									+ property2.replaceAll("'", "''")
									+ "','"
									+ property3.replaceAll("'", "''")
									+ "','"
									+ property4.replaceAll("'", "''")
									+ "','"
									+ property5.replaceAll("'", "''")
									+ "','" + loginuserid + "','" + loginUser + "',now(),'" + loginuserid + "','" + loginUser + "',now()," + "0,0,0,0,0,'',0,0,'')";
							// System.out.println(iteminfosql);
							ps1.addBatch(iteminfosql);
							ps1.executeBatch();
						}

						if (!relationmainid.equals("") && relationmainids.indexOf(relationmainid) == -1) {// 获取唯一订单主表ID
							relationmainids = relationmainids + (relationmainids.equals("") ? "" : ",") + relationmainid;
						}

						if (type > 0) {
							details = store + Common.getUpperUUIDString() + "','" + companyid + "','" + itemid + "','" + houseid + "'," + dcount + "," + dtotal + ",round("
									+ (dcount > 0 ? dtotal / dcount : 0) + "," + pricebit + "),'" + batchno + "','" + result.getString("remark") + "') on duplicate key update count=round(count+"
									+ dcount + "," + countbit + "),money=round(money+" + dtotal + "," + moneybit + ") ,newcostprice=round(if(count=0,newcostprice,money/count)," + pricebit + ")"
									+ (result.getString("remark").equals("") ? "" : ",stockremark='" + result.getString("remark") + "'");

							ps.addBatch(details);
							// System.out.println("saveStoreInFunction " +
							// details);

							details = itemmonth + Common.getUpperUUIDString() + "','" + companyid + "','" + itemid + "','" + houseid + "','" + sdate + "'," + syear + "," + smonth + "," + dcount + ","
									+ dtotal + "," + dcount + "," + dtotal + ",'" + batchno + "') on duplicate key update count=round(count+" + dcount + "," + countbit + "),money=round(money+"
									+ dtotal + "," + moneybit + "),totalcount=round(totalcount+" + dcount + "," + countbit + "),totalmoney=round(totalmoney+" + dtotal + "," + moneybit + ") ";
							ps.addBatch(details);

							if (!relationdetailid.equals("")) {// 增加修改采购订单入库数量且订单月报表

								ps.addBatch("update purchaseorderdetail set incount=round(incount+" + dcount + "," + countbit + "),intotal=round(intotal+" + dtotal + "," + moneybit
										+ ") where detailid='" + relationdetailid + "'");
								ps.addBatch("update purchaseorder set incount=round(incount+" + dcount + "," + countbit + "),intotal=round(intotal+" + dtotal + "," + moneybit + ")"
										+ " where   (select 1 from purchaseorderdetail d where d.purchaseorderid=purchaseorder.purchaseorderid and  d.detailid='" + relationdetailid + "' limit 1)");
								details = ordermonth + Common.getUpperUUIDString() + "','" + companyid + "','" + itemid + "','" + sdate + "'," + syear + "," + smonth + "," + dcount + "," + dtotal
										+ ",'" + batchno + "') on duplicate key update purchaseincount=round(purchaseincount+" + dcount + "," + countbit + "),purchaseinmoney=round(purchaseinmoney+"
										+ dtotal + "," + moneybit + ")";
								ps.addBatch(details);
							}

							String temp = "('" + itemid + "','" + houseid + "')";
							if (condition.toString().equals("")) {
								condition.append(temp);
							} else {
								if (condition.toString().indexOf(temp) == -1) {
									condition.append("," + temp);
								}
							}

						}

						details = detail + orderid + "','" + (detailid.equals("") ? Common.getUpperUUIDString() : detailid) + "','" + maindata.getString("storeinid") + "','"
								+ maindata.getString("originalbill") + "'," + result.getInteger("goods_number") + ",'" + companyid + "','" + maindata.getString("operate_by") + "','"
								+ maindata.getString("operate_time") + "','" + itemid + "','" + customerid + "','" + houseid + "'," + price + "," + dcount + "," + dtotal + ","
								+ result.getDoubleValue("tax") + "," + result.getDoubleValue("taxrate") + "," + (price == 0 ? 0 : result.getDoubleValue("taxprice")) + ","
								+ result.getDoubleValue("taxmoney") + ",'11','" + result.getString("remark") + "','" + status + "','"
								+ (newrecode.equals("yes") ? loginuserid : result.getString("create_id")) + "','" + (newrecode.equals("yes") ? loginUser : result.getString("create_by")) + "',"
								+ (newrecode.equals("yes") ? "now()" : "'" + result.getString("create_time") + "'") + ",'" + loginuserid + "','" + loginUser + "',now(),'" + batchno + "','"
								+ relationdetailid + "','" + relationorderid + "','" + relationmainid + "')";

						ps.addBatch(details);

						if (type > 0 && result.getDoubleValue("tax") != 0) {
							totaltaxmoney = Pdacommon.adddouble(totaltaxmoney, result.getDoubleValue("taxmoney"));
						}

					}

				}

				if (type > 0 && !relationmainids.equals("")) {// 更改等待入库的采购订单的入库状态，符合改为已完成入库
					String[] farr = relationmainids.split(",");
					for (int j = 0; j < farr.length; j++) {

						ps.addBatch("update purchaseorder set stockstatus = '1' where purchaseorderid='" + farr[j]
								+ "' and (stockstatus='0' or stockstatus='-1')  and status='1'  and if((select 1 from purchaseorderdetail where purchaseorderid='" + farr[j]
								+ "' and count>incount and status='1' limit 1),false,true)");

						ps.addBatch("update purchaseorder set stockstatus = '-1' where purchaseorderid='" + farr[j] + "' and stockstatus<>'3'  and status='1' and incount>0 and"
								+ "(select count(detailid) from purchaseorderdetail where purchaseorderid='" + farr[j]
								+ "' and incount>=count and status='1')<(select count(detailid) from purchaseorderdetail where purchaseorderid='" + farr[j] + "' and status='1') ");
						// ps.addBatch("update purchaseorder set stockstatus = '-1' where purchaseorderid='"
						// + farr[j] +
						// "' and stockstatus='0' and status='1' and "
						// +
						// "(select count(detailid) from purchaseorderdetail where purchaseorderid='"
						// + farr[j] +
						// "' and incount>0 and count>incount and status='1')>0 ");

					}
				}
				if (type > 0) {
					for (i = 0; i < checkdata.size(); i++) {
						JSONObject checkresult = JSONObject.parseObject(checkdata.getString(i));
						String fhouseid = checkresult.getString("houseid");
						String fbatchno = checkresult.getString("batchno");
						double ftotalcount = checkresult.getDoubleValue("totalcount");
						String fitemid = checkresult.getString("itemid");
						String fcodeid = checkresult.getString("codeid");

						// companyid='" + companyid + "' and
						String checksql = "select count,round(count-checkout_count," + countbit + ") as cancount from stock where  itemid='" + fitemid + "' and houseid='" + fhouseid + "' "
								+ " and batchno='" + fbatchno + "'";

						Table ftable = DataUtils.queryData(conn, checksql, null, null, null, null);
						// 2020-12-10 涉及到仓库没有数据问题。
						double fcancount = 0;
						if (ftable.getRows().size() > 0) {
							fcancount = Double.parseDouble(ftable.getRows().get(0).getValue("cancount").toString());
							if (fcancount < checkresult.getDoubleValue("totalcount")) {
								message = message + "(" + fcodeid + "、" + checkresult.getString("itemname") + "、" + checkresult.getString("housename") + " " + fbatchno + "《总需减的库存数量》："
										+ checkresult.getDoubleValue("totalcount") + " 大于 《最大可减的库存数量》：" + fcancount + ")";
								save = false;
							}
						} else {// 2020-12-10 增加
							message = message + "(" + fcodeid + "、" + checkresult.getString("itemname") + "、" + checkresult.getString("housename") + " " + fbatchno + "《总需减的库存数量》："
									+ checkresult.getDoubleValue("totalcount") + " 大于 《最大可减的库存数量》：" + fcancount + ")";
							save = false;
						}
					}

					if (!message.equals(""))
						message = message + "，保存失败";
				}

			} else {
				message = message + "没有商品明细数据，操作失败";
				save = false;
			}
			if (save) {

				if (type > 0 && totalmoney != 0) {
					// 更新客户应收应付款
					String customersql = "update customer set payable=round(payable+" + totalmoney + "," + moneybit + ")"
							+ (totaltaxmoney != 0 ? ",T_payable=round(T_payable+" + totaltaxmoney + "," + moneybit + ")" : "") + " where companyid='" + companyid + "' and customerid='" + customerid
							+ "'";
					ps.addBatch(customersql);

					// 增加往来单位月收支报表
					String customermonthsql = "insert into customermonth (monthid,companyid,customerid,sdate,syear,smonth,payable,pay_purchasein_money,pay_add_money"
							+ (totaltaxmoney != 0 ? ",T_payable,T_pay_purchasein_money,T_pay_add_money" : "")
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
							+ totalmoney
							+ ","
							+ totalmoney
							+ ","
							+ totalmoney
							+ (totaltaxmoney != 0 ? ("," + totaltaxmoney + "," + totaltaxmoney + "," + totaltaxmoney) : "")
							+ ") on duplicate key update payable=round(payable+"
							+ totalmoney
							+ ","
							+ moneybit
							+ "),pay_purchasein_money=round(pay_purchasein_money+"
							+ totalmoney
							+ ","
							+ moneybit
							+ "),pay_add_money=round(pay_add_money+"
							+ totalmoney
							+ ","
							+ moneybit
							+ ")"
							+ (totaltaxmoney != 0 ? ",T_payable=round(T_payable+" + totaltaxmoney + "," + moneybit + "),T_pay_purchasein_money=round(T_pay_purchasein_money+" + totaltaxmoney + ","
							+ moneybit + "),T_pay_add_money=round(T_pay_add_money+" + totaltaxmoney + "," + moneybit + ")" : "");
					ps.addBatch(customermonthsql);

					// 增加往来单位年报表
					String customeryearsql = "insert into customeryear (yearid,companyid,customerid,syear,payable,pay_purchasein_money,pay_add_money"
							+ (totaltaxmoney != 0 ? ",T_payable,T_pay_purchasein_money,T_pay_add_money" : "")
							+ ") values('"
							+ Common.getUpperUUIDString()
							+ "','"
							+ companyid
							+ "','"
							+ customerid
							+ "',"
							+ syear
							+ ","
							+ totalmoney
							+ ","
							+ totalmoney
							+ ","
							+ totalmoney
							+ (totaltaxmoney != 0 ? ("," + totaltaxmoney + "," + totaltaxmoney + "," + totaltaxmoney) : "")
							+ ") on duplicate key update payable=round(payable+"
							+ totalmoney
							+ ","
							+ moneybit
							+ ")"
							+ ",pay_purchasein_money=round(pay_purchasein_money+"
							+ totalmoney
							+ ","
							+ moneybit
							+ "),pay_add_money=round(pay_add_money+"
							+ totalmoney
							+ ","
							+ moneybit
							+ ")"
							+ (totaltaxmoney != 0 ? ",T_payable=round(T_payable+" + totaltaxmoney + "," + moneybit + "),T_pay_purchasein_money=round(T_pay_purchasein_money+" + totaltaxmoney + ","
							+ moneybit + "),T_pay_add_money=round(T_pay_add_money+" + totaltaxmoney + "," + moneybit + ")" : "");
					ps.addBatch(customeryearsql);
				}

				int changebilltype = Pdacommon.getDatalogBillChangefunc("storein", "");
				if (operate.equals("draftedit")) {// 草稿编辑 更新
					ps.addBatch("update storein set   orderid='" + orderid + "',originalbill='" + maindata.getString("originalbill") + "',operate_time='" + operate_time + "',operate_by='"
							+ maindata.getString("operate_by") + "',houseid='" + houseid + "',customerid='" + customerid + "',count=" + count + ",total=" + total + ",totaltax=" + totaltax
							+ ",totalmoney=" + totalmoney + ",remark='" + maindata.getString("remark") + "',status='" + status + "',update_id='" + loginuserid + "',update_by='" + loginUser
							+ "',update_time=now(),iproperty='" + maindata.getString("iproperty") + "' where storeinid='" + maindata.getString("storeinid") + "'");

					ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
							+ companyid + "'," + changebilltype + ",'修改','" + maindata.getString("storeinid") + "','单据编号：" + orderid + "','" + loginuserid + "','" + loginUser + "',now())");

					ps.addBatch("update storeindetail d,storein s set d.create_id=s.create_id,d.create_by=s.create_by,d.create_time=s.create_time where d.storeinid = s.storeinid and s.storeinid='"
							+ maindata.getString("storeinid") + "'");
				} else {
					String main = "insert into storein (orderid,storeinid,bill_type,originalbill,companyid,operate_time,operate_by,houseid,customerid,count,total,totaltax,totalmoney,remark,status,printing,outexcel,create_id,create_by,create_time,update_id,update_by,update_time,iproperty) VALUES ('"
							+ orderid
							+ "','"
							+ maindata.getString("storeinid")
							+ "','"
							+ maindata.getString("bill_type")
							+ "','"
							+ maindata.getString("originalbill")
							+ "','"
							+ companyid
							+ "','"
							+ operate_time
							+ "','"
							+ maindata.getString("operate_by")
							+ "','"
							+ houseid
							+ "','"
							+ customerid
							+ "',"
							+ count
							+ ","
							+ total
							+ ","
							+ totaltax
							+ ","
							+ totalmoney
							+ ",'"
							+ maindata.getString("remark")
							+ "','"
							+ status
							+ "',0,0,'"
							+ loginuserid
							+ "','"
							+ loginUser
							+ "',now(),'"
							+ loginuserid
							+ "','"
							+ loginUser
							+ "',now(),'"
							+ maindata.getString("iproperty") + "')";
					ps.addBatch(main);

					ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
							+ companyid + "'," + changebilltype + ",'" + (status == 0 ? "新增暂存" : "新增保存") + "','" + maindata.getString("storeinid") + "','单据编号：" + orderid + "','" + loginuserid + "','"
							+ loginUser + "',now())");

				}

				// 检验质检
				ps.addBatch("update storeindetail set isquality=ifnull((select count(*) from  qualitymain where relationdetailid=storeindetail.detailid and relationdetailtatble='storeindetail' and status<>2  ),0)  where storeinid='"
						+ maindata.getString("storeinid") + "'");

				ps.executeBatch();
				conn.commit();
				conn.setAutoCommit(true);
				state = "1";

				if (type > 0) {
					// 库存报警
					Bhouselimit = Common.gethouselimitdata(conn, companyid, condition, Bhouselimit);
				}
			}

		} catch (Exception e) {
			message = message + "操作失败，请稍后再试。";
			// e.printStackTrace();
			try {
				conn.rollback();
			} catch (Exception e1) {
				// e1.printStackTrace();
			}
		} finally {
			condition.setLength(0);
			conn.close();
		}
		rt.put("state", state);
		rt.put("message", message);
		rt.put("warning", Bhouselimit);
		return rt;
	}

	// 保存退货出库信息
	public static JSONObject saveStoreInOutFunction(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject rt = new JSONObject();
		JSONObject maindata = JSONObject.parseObject(params.getString("maindata"));
		Integer type = params.getInteger("type");
		Integer pricebit = params.getInteger("pricebit");
		String loginUser = params.getString("loginUser");
		String loginuserid = params.getString("loginuserid");
		JSONArray detaildata = params.getJSONArray("detaildata");
		String operate = params.getString("operate");// draftedit 编辑单据
		String state = "0";
		String message = "";

		JSONArray checkdata = params.getJSONArray("checkdata");

		String companyid = maindata.getString("companyid");
		String houseid = maindata.getString("houseid");
		String customerid = maindata.getString("customerid");
		String operate_time = maindata.getString("operate_time");
		double count = maindata.getDoubleValue("count");
		double total = maindata.getDoubleValue("total");

		double totaltax = maindata.getDoubleValue("totaltax");
		double totalmoney = maindata.getDoubleValue("totalmoney");

		int status = type == 0 ? 0 : 1;

		String[] sdatearr = operate_time.split("-");
		int syear = Integer.parseInt(sdatearr[0]);
		int smonth = Integer.parseInt(sdatearr[1]);
		String sdate = sdatearr[0] + "-" + sdatearr[1] + "-01";

		String billdate = sdatearr[0] + sdatearr[1] + sdatearr[2];

		JSONArray Bhouselimit = new JSONArray();
		StringBuffer condition = new StringBuffer();

		double totaltaxmoney = 0;

		try {
			int countbit = 0;
			int moneybit = 2;
			int t6days = 0;

			Table companytalbe = DataUtils.queryData(conn, "select pricebit,countbit,moneybit,t6days from s_company_config where company_id='" + companyid + "'", null, null, null, null);
			if (companytalbe.getRows().size() > 0) {
				countbit = companytalbe.getRows().get(0).getInteger("countbit");
				moneybit = companytalbe.getRows().get(0).getInteger("moneybit");
				t6days = companytalbe.getRows().get(0).getInteger("t6days");
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
			conn.setAutoCommit(false);
			String detail = "insert into storeindetail (orderid,detailid,storeinid,originalbill,goods_number,companyid,operate_by,operate_time,itemid,customerid,houseid,price,count,total,tax,taxrate,taxprice,taxmoney,stype,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,batchno,returndetailid,returnorderid) VALUES ('";
			String store = "update stock set ";
			String itemmonth = "insert into itemmonth (monthid,companyid,itemid,houseid,sdate,syear,smonth,count,money,t_totalcount,t_totalmoney,batchno) VALUES ('";

			// 订单月报表
			String ordermonth = "insert into ordermonth (monthid,companyid,itemid,sdate,syear,smonth,purchaseoutcount,purchaseoutmoney,batchno) VALUES ('";

			String orderid = operate.equals("draftedit") ? maindata.getString("orderid") : getOrderidByparams(companyid, "storein", "6", billdate, conn);
			int i = 0;
			String details = "";
			if (save && operate.equals("draftedit")) {// 草稿编辑 删除明细数据重新增加
				String fsql = "select status from storein where storeinid='" + maindata.getString("storeinid") + "'";
				Object cobject = DataUtils.getValueBySQL(conn, fsql, null);
				if (cobject == null) {
					save = false;
					message = "当前记录已删除，操作失败。";
					state = "2";
				} else {
					String fstatus = cobject.toString();
					if (fstatus.equals("0")) {
						ps.addBatch("delete from storeindetail where storeinid='" + maindata.getString("storeinid") + "'");
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
			String relationmainids = "";// 采购订单主表
			String sql = "";
			if (detaildata.size() > 0) {
				if (save) {

					for (i = 0; i < detaildata.size(); i++) {
						JSONObject result = JSONObject.parseObject(detaildata.getString(i));
						String detailid = result.getString("detailid");
						String batchno = result.getString("batchno");
						String itemid = result.getString("itemid");
						double price = result.getDoubleValue("price");
						double dcount = result.getDoubleValue("count");
						double dtotal = result.getDoubleValue("total");
						String returndetailid = result.getString("returndetailid");
						String returnorderid = result.getString("returnorderid");

						if (save) {
							if (type > 0) {
								if (!returndetailid.equals("")) {// 入库单可退数量
									sql = "select round(count-returncount," + countbit + ") as cancount,relationdetailid,relationmainid from storeindetail where detailid='" + returndetailid + "'";
									Table stable = DataUtils.queryData(conn, sql, null, null, null, null);

									double canreturncount = 0;
									String relationdetailid = "";
									if (stable.getRows().size() > 0) {
										canreturncount = Double.parseDouble(stable.getRows().get(0).getValue("cancount").toString());
										relationdetailid = stable.getRows().get(0).getString("relationdetailid");
										String relationmainid = stable.getRows().get(0).getString("relationmainid");
										if (!relationmainid.equals("") && relationmainids.indexOf(relationmainid) == -1) {// 获取唯一订单主表ID
											relationmainids = relationmainids + (relationmainids.equals("") ? "" : ",") + relationmainid;
										}
									}

									if (canreturncount < dcount) {
										message = message + (message.equals("") ? "" : "；") + "第" + result.getInteger("goods_number") + "行的商品《" + result.getString("itemname") + " "
												+ result.getString("batchno") + "》已超出入库单单号《" + returnorderid + "》可以退的数量" + canreturncount;
										save = false;
									} else {
										ps.addBatch("update storeindetail set returncount=round(returncount+" + dcount + "," + countbit + ") where  detailid='" + returndetailid + "'");

										// 退货关联入库单且入库单关联采购订单，修改其实际入库数，及统计期退货数
										if (!relationdetailid.equals("")) {
											ps.addBatch("update purchaseorderdetail set incount=round(incount-" + dcount + "," + countbit + "),intotal=round(intotal-" + dtotal + "," + moneybit
													+ ") where detailid='" + relationdetailid + "'");
											ps.addBatch("update purchaseorder set incount=round(incount-" + dcount + "," + countbit + "),intotal=round(intotal-" + dtotal + "," + moneybit + ")"
													+ " where  (select 1 from purchaseorderdetail d where d.purchaseorderid=purchaseorder.purchaseorderid and d.detailid='" + relationdetailid
													+ "' limit 1)");
											details = ordermonth + Common.getUpperUUIDString() + "','" + companyid + "','" + itemid + "','" + sdate + "'," + syear + "," + smonth + "," + dcount + ","
													+ dtotal + ",'" + batchno + "') on duplicate key update purchaseoutcount=round(purchaseoutcount+" + dcount + "," + countbit + ")"
													+ ",purchaseoutmoney=round(purchaseoutmoney+" + dtotal + "," + moneybit + ")";
											ps.addBatch(details);
										}
									}

								}

								details = store + "count=round(count-" + dcount + "," + countbit + "),money=round(money-" + dtotal + "," + moneybit
										+ ") ,newcostprice=round(if(count=0,newcostprice,money/count)," + pricebit + ") where companyid='" + companyid + "' and itemid='" + itemid + "' and houseid='"
										+ houseid + "' and batchno='" + batchno + "'";

								ps.addBatch("delete from stock where itemid='" + itemid + "' and houseid='" + houseid + "' and batchno='" + batchno + "' and count=0 and money=0 ");
								ps.addBatch(details);

								details = itemmonth + Common.getUpperUUIDString() + "','" + companyid + "','" + itemid + "','" + houseid + "','" + sdate + "'," + syear + "," + smonth + ",-" + dcount
										+ ",-" + dtotal + "," + dcount + "," + dtotal + ",'" + batchno + "') on duplicate key update count=round(count-" + dcount + "," + countbit
										+ "),money=round(money-" + dtotal + "," + moneybit + ")" + ",t_totalcount=round(t_totalcount+" + dcount + "," + countbit + "),t_totalmoney=round(t_totalmoney+"
										+ dtotal + "," + moneybit + ")";
								ps.addBatch(details);

								String temp = "('" + itemid + "','" + houseid + "')";
								if (condition.toString().equals("")) {
									condition.append(temp);
								} else {
									if (condition.toString().indexOf(temp) == -1) {
										condition.append("," + temp);
									}
								}
							}

							details = detail + orderid + "','" + (detailid.equals("") ? Common.getUpperUUIDString() : detailid) + "','" + maindata.getString("storeinid") + "','"
									+ maindata.getString("originalbill") + "'," + result.getInteger("goods_number") + ",'" + companyid + "','" + maindata.getString("operate_by") + "','"
									+ maindata.getString("operate_time") + "','" + itemid + "','" + customerid + "','" + houseid + "'," + price + "," + dcount + "," + dtotal + ","
									+ result.getDoubleValue("tax") + "," + result.getDoubleValue("taxrate") + "," + result.getDoubleValue("taxprice") + "," + result.getDoubleValue("taxmoney")
									+ ",'61','" + result.getString("remark") + "','" + status + "','" + (detailid.equals("") ? loginuserid : result.getString("create_id")) + "','"
									+ (detailid.equals("") ? loginUser : result.getString("create_by")) + "'," + (detailid.equals("") ? "now()" : "'" + result.getString("create_time") + "'") + ",'"
									+ loginuserid + "','" + loginUser + "',now(),'" + batchno + "','" + returndetailid + "','" + returnorderid + "')";

							ps.addBatch(details);

							if (type > 0 && result.getDoubleValue("tax") != 0) {
								totaltaxmoney = Pdacommon.adddouble(totaltaxmoney, result.getDoubleValue("taxmoney"));
							}

						}
					}

					if (type > 0 && !relationmainids.equals("")) {// 更改等待入库的采购订单的入库状态，符合改为已完成入库
						String[] farr = relationmainids.split(",");
						for (int j = 0; j < farr.length; j++) {

							ps.addBatch("update purchaseorder set stockstatus = '-1' where purchaseorderid='" + farr[j] + "' and stockstatus<>'3'  and status='1' and incount>0 and"
									+ "(select count(detailid) from purchaseorderdetail where purchaseorderid='" + farr[j]
									+ "' and incount>=count and status='1')<(select count(detailid) from purchaseorderdetail where purchaseorderid='" + farr[j] + "' and status='1') ");
							ps.addBatch("update purchaseorder set stockstatus = '0' where purchaseorderid='" + farr[j]
									+ "'  and  status='1' and (select count(detailid) from purchaseorderdetail where purchaseorderid='" + farr[j]
									+ "' and status='1')=(select count(detailid) from purchaseorderdetail where purchaseorderid='" + farr[j] + "' and incount=0 and status='1')");

						}
					}

					if (!message.equals("")) {
						message = message + "，保存失败。";
						save = false;
					} else {

						for (i = 0; i < checkdata.size(); i++) {
							JSONObject checkresult = JSONObject.parseObject(checkdata.getString(i));
							String fbatchno = checkresult.getString("batchno");
							double ftotalcount = checkresult.getDoubleValue("totalcount");
							String fitemid = checkresult.getString("itemid");
							String fcodeid = checkresult.getString("codeid");

							String checksql = "select count,round(count-checkout_count," + countbit + ") as cancount from stock where companyid='" + companyid + "' and itemid='" + fitemid
									+ "' and houseid='" + houseid + "' " + " and batchno='" + fbatchno + "'";

							// //System.out.println(checksql);

							Table ftable = DataUtils.queryData(conn, checksql, null, null, null, null);
							// 2020-12-10 涉及到仓库没有数据问题。
							double fcancount = 0;
							if (ftable.getRows().size() > 0) {
								fcancount = Double.parseDouble(ftable.getRows().get(0).getValue("cancount").toString());
								if (fcancount < checkresult.getDoubleValue("totalcount")) {
									message = message + "(" + fcodeid + "、" + checkresult.getString("itemname") + "、" + maindata.getString("housename") + " " + fbatchno + "《总退货数量》："
											+ checkresult.getDoubleValue("totalcount") + " 大于 《当前可出库的库存数量》：" + fcancount + ")";
									save = false;
								}
							} else {// 2020-12-10 增加
								message = message + "(" + fcodeid + "、" + checkresult.getString("itemname") + "、" + maindata.getString("housename") + " " + fbatchno + "《总退货数量》："
										+ checkresult.getDoubleValue("totalcount") + " 大于 《当前可出库的库存数量》：" + fcancount + ")";
								save = false;
							}
						}
					}
				}

			} else {
				message = message + "没有商品明细数据，操作失败";
				save = false;
			}
			if (save) {

				if (type > 0 && totalmoney != 0) {
					// 更新客户应收应付款
					String customersql = "update customer set payable=round(payable-" + totalmoney + "," + moneybit + ")"
							+ (totaltaxmoney != 0 ? ",T_payable=round(T_payable-" + totaltaxmoney + "," + moneybit + ")" : "") + " where companyid='" + companyid + "' and customerid='" + customerid
							+ "'";
					ps.addBatch(customersql);

					// 增加往来单位月收支报表
					String customermonthsql = "insert into customermonth (monthid,companyid,customerid,sdate,syear,smonth,payable,pay_purchaseout_money,pay_add_money"
							+ (totaltaxmoney != 0 ? ",T_payable,T_pay_purchaseout_money,T_pay_add_money" : "")
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
							+ totalmoney
							+ ","
							+ totalmoney
							+ ",-"
							+ totalmoney
							+ (totaltaxmoney != 0 ? (",-" + totaltaxmoney + "," + totaltaxmoney + ",-" + totaltaxmoney) : "")
							+ ") on duplicate key update payable=round(payable-"
							+ totalmoney
							+ ","
							+ moneybit
							+ "),pay_purchaseout_money=round(pay_purchaseout_money+"
							+ totalmoney
							+ ","
							+ moneybit
							+ "),pay_add_money=round(pay_add_money-"
							+ totalmoney
							+ ","
							+ moneybit
							+ ")"
							+ (totaltaxmoney != 0 ? ",T_payable=round(T_payable-" + totaltaxmoney + "," + moneybit + "),T_pay_purchaseout_money=round(T_pay_purchaseout_money+" + totaltaxmoney + ","
							+ moneybit + "),T_pay_add_money=round(T_pay_add_money-" + totaltaxmoney + "," + moneybit + ")" : "");
					ps.addBatch(customermonthsql);

					// 增加往来单位年报表
					String customeryearsql = "insert into customeryear (yearid,companyid,customerid,syear,payable,pay_purchaseout_money,pay_add_money"
							+ (totaltaxmoney != 0 ? ",T_payable,T_pay_purchaseout_money,T_pay_add_money" : "")
							+ ") values('"
							+ Common.getUpperUUIDString()
							+ "','"
							+ companyid
							+ "','"
							+ customerid
							+ "',"
							+ syear
							+ ",-"
							+ totalmoney
							+ ","
							+ totalmoney
							+ ",-"
							+ totalmoney
							+ (totaltaxmoney != 0 ? (",-" + totaltaxmoney + "," + totaltaxmoney + ",-" + totaltaxmoney) : "")
							+ ") on duplicate key update payable=round(payable-"
							+ totalmoney
							+ ","
							+ moneybit
							+ ")"
							+ ",pay_purchaseout_money=round(pay_purchaseout_money+"
							+ totalmoney
							+ ","
							+ moneybit
							+ "),pay_add_money=round(pay_add_money-"
							+ totalmoney
							+ ","
							+ moneybit
							+ ")"
							+ (totaltaxmoney != 0 ? ",T_payable=round(T_payable-" + totaltaxmoney + "," + moneybit + "),T_pay_purchaseout_money=round(T_pay_purchaseout_money+" + totaltaxmoney + ","
							+ moneybit + "),T_pay_add_money=round(T_pay_add_money-" + totaltaxmoney + "," + moneybit + ")" : "");
					ps.addBatch(customeryearsql);
				}

				int changebilltype = Pdacommon.getDatalogBillChangefunc("storein", "6");
				if (operate.equals("draftedit")) {// 草稿编辑 更新
					ps.addBatch("update storein set orderid='" + orderid + "', originalbill='" + maindata.getString("originalbill") + "',operate_time='" + operate_time + "',operate_by='"
							+ maindata.getString("operate_by") + "',houseid='" + houseid + "',customerid='" + customerid + "',count=" + count + ",total=" + total + ",totaltax=" + totaltax
							+ ",totalmoney=" + totalmoney + ",remark='" + maindata.getString("remark") + "',status='" + status + "',update_id='" + loginuserid + "',update_by='" + loginUser
							+ "',update_time=now(),iproperty='" + maindata.getString("iproperty") + "' where storeinid='" + maindata.getString("storeinid") + "'");

					ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
							+ companyid + "'," + changebilltype + ",'修改','" + maindata.getString("storeinid") + "','单据编号：" + orderid + "','" + loginuserid + "','" + loginUser + "',now())");

					ps.addBatch("update storeindetail d,storein s set d.create_id=s.create_id,d.create_by=s.create_by,d.create_time=s.create_time where d.storeinid = s.storeinid and s.storeinid='"
							+ maindata.getString("storeinid") + "'");

				} else {
					String main = "insert into storein (orderid,storeinid,bill_type,originalbill,companyid,operate_time,operate_by,houseid,customerid,count,total,totaltax,totalmoney,remark,status,printing,outexcel,create_id,create_by,create_time,update_id,update_by,update_time,iproperty) VALUES ('"
							+ orderid
							+ "','"
							+ maindata.getString("storeinid")
							+ "','"
							+ maindata.getString("bill_type")
							+ "','"
							+ maindata.getString("originalbill")
							+ "','"
							+ companyid
							+ "','"
							+ operate_time
							+ "','"
							+ maindata.getString("operate_by")
							+ "','"
							+ houseid
							+ "','"
							+ customerid
							+ "',"
							+ count
							+ ","
							+ total
							+ ","
							+ totaltax
							+ ","
							+ totalmoney
							+ ",'"
							+ maindata.getString("remark")
							+ "','"
							+ status
							+ "',0,0,'"
							+ loginuserid
							+ "','"
							+ loginUser
							+ "',now(),'"
							+ loginuserid
							+ "','"
							+ loginUser
							+ "',now(),'"
							+ maindata.getString("iproperty") + "')";
					ps.addBatch(main);

					ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
							+ companyid + "'," + changebilltype + ",'" + (status == 0 ? "新增暂存" : "新增保存") + "','" + maindata.getString("storeinid") + "','单据编号：" + orderid + "','" + loginuserid + "','"
							+ loginUser + "',now())");

				}

				ps.executeBatch();
				conn.commit();
				conn.setAutoCommit(true);
				state = "1";

				if (type > 0) {
					// 库存报警
					Bhouselimit = Common.gethouselimitdata(conn, companyid, condition, Bhouselimit);
				}
			} else {
				message = message + "，保存失败。";
			}

		} catch (Exception e) {
			message = message + "操作失败，请稍后再试。";
			// e.printStackTrace();
			try {
				conn.rollback();
			} catch (Exception e1) {
				// e1.printStackTrace();
			}
		} finally {
			condition.setLength(0);
			conn.close();
		}
		rt.put("state", state);
		rt.put("message", message);
		rt.put("warning", Bhouselimit);
		return rt;
	}

	// 保存出库单信息
	public static JSONObject saveStoreOutFunction(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject rt = new JSONObject();
		JSONObject maindata = JSONObject.parseObject(params.getString("maindata"));
		Integer type = params.getInteger("type");
		Integer pricebit = params.getInteger("pricebit");
		Integer moneybit = params.getInteger("moneybit");
		Integer countbit = params.getInteger("countbit");
		String loginUser = params.getString("loginUser");
		String loginuserid = params.getString("loginuserid");
		JSONArray detaildata = params.getJSONArray("detaildata");
		String operate = params.getString("operate");// draftedit 编辑单据
		Integer oldstatus = params.getInteger("oldstatus");
		String oldorderid = params.getString("oldorderid");
		String state = "0";

		JSONArray checkdata = params.getJSONArray("checkdata");

		String companyid = maindata.getString("companyid");
		String customerid = maindata.getString("customerid");
		// String contractno = maindata.getString("contractno");
		String customername = maindata.getString("customername");
		String operate_time = maindata.getString("operate_time");
		double count = maindata.getDoubleValue("count");
		double total = maindata.getDoubleValue("total");
		double totaltax = maindata.getDoubleValue("totaltax");
		double totalmoney = maindata.getDoubleValue("totalmoney");
		double cost_money = 0;
		double tprofit = 0;
		double newcount = 0;

		double totaltaxmoney = 0;

		String linkman = maindata.getString("linkman");
		String linkphone = maindata.getString("linkphone");
		String deliveryadrr = maindata.getString("deliveryadrr");
		String deliverer = maindata.getString("deliverer");
		String license = maindata.getString("license");
		String receiver = maindata.getString("receiver");

		Integer salesoutset = params.getInteger("salesoutset");

		int status = type;
		String message = "";
		int i = 0;

		String[] sdatearr = operate_time.split("-");
		int syear = Integer.parseInt(sdatearr[0]);
		int smonth = Integer.parseInt(sdatearr[1]);
		String sdate = sdatearr[0] + "-" + sdatearr[1] + "-01";

		String billdate = sdatearr[0] + sdatearr[1] + sdatearr[2];

		JSONArray Bhouselimit = new JSONArray();
		StringBuffer condition = new StringBuffer();

		try {

			boolean save = true;

			message = Pdacommon.checkSaveDate(companyid, 0, operate_time, false, conn);
			if (!message.equals("")) {
				save = false;
				state = "3";
			}

			Statement ps = conn.createStatement();
			conn.setAutoCommit(false);
			String detail = "insert into storeoutdetail (orderid,detailid,storeoutid,originalbill,goods_number,companyid,operate_by,operate_time,itemid,customerid,houseid,price,count,total,tax,taxrate,taxprice,taxmoney,stype,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,batchno,cost_price,cost_money,profit,profit_rate,relationdetailid,relationorderid,relationmainid,deliverer,license,oldcount,relationtype,applyrelationdetailid,applyrelationorderid,applyrelationmainid) VALUES ('";
			String store = "update stock set ";
			String itemmonth = "insert into itemmonth (monthid,companyid,itemid,houseid,sdate,syear,smonth,count,money,sellcount,sellmoney,sellcost,batchno) VALUES ('";

			// 订单月报表
			String ordermonth = "insert into ordermonth (monthid,companyid,itemid,sdate,syear,smonth,salesoutcount,salesoutmoney,salesoutcost,batchno) VALUES ('";

			String orderid = operate.equals("draftedit") && oldorderid.equals(maindata.getString("orderid")) ? maindata.getString("orderid") : getOrderidByparams(companyid, "storeout", "2", billdate,
					conn);

			String details = "";
			boolean okout = false;
			String deliverdetailid = "";
			String delivermainid = "";
			String deliverorderid = "";
			if (save && operate.equals("draftedit")) {// 草稿编辑 删除明细数据重新增加
				String fsql = "select status,relationdetailid,relationorderid,relationmainid from storeout where storeoutid='" + maindata.getString("storeoutid") + "'";

				Table ftable = DataUtils.queryData(conn, fsql, null, null, null, null);
				if (ftable.getRows().size() == 0) {
					save = false;
					message = "当前记录已删除，操作失败。";
					state = "2";
				} else {
					String fstatus = ftable.getRows().get(0).getString("status");
					oldstatus = Integer.parseInt(fstatus);
					deliverorderid = ftable.getRows().get(0).getString("relationorderid");
					if (fstatus.equals("1")) {
						save = false;
						message = "当前记录已出货，操作失败。";
						state = "2";
					} else if (fstatus.equals("2")) {
						save = false;
						message = "当前记录已作废，操作失败。";
						state = "2";
					} else if (fstatus.equals("0") || fstatus.equals("3")) {
						if (fstatus.equals("3") && !deliverorderid.equals("")) {
							if (status == 1) {
								okout = true;
								deliverdetailid = ftable.getRows().get(0).getString("relationdetailid");
								delivermainid = ftable.getRows().get(0).getString("relationmainid");
							} else {
								save = false;
								message = "当前销售出库单已关联有效的送货单" + deliverorderid + "，不能修改，操作失败。";
								state = "2";
							}
						} else {
							ps.addBatch("delete from storeoutdetail where storeoutid='" + maindata.getString("storeoutid") + "'");
						}
					}
				}
			}
			String relationmainids = "";// 销售订单主表
			if (detaildata.size() > 0) {

				if (save) {
					for (i = 0; i < detaildata.size(); i++) {
						JSONObject result = JSONObject.parseObject(detaildata.getString(i));
						String detailid = result.getString("detailid");
						String batchno = result.getString("batchno");
						String itemid = result.getString("itemid");
						double dcount = result.getDoubleValue("count");
						String relationdetailid = result.getString("relationdetailid");// 销售订单明细ID
						String relationorderid = result.getString("relationorderid");// 销售订单订单编号
						String relationmainid = result.getString("relationmainid");// 销售订单订单主表ID
						String houseid = result.getString("houseid");
						double oldcount = result.getDoubleValue("oldcount");
						Integer relationtype = result.getInteger("relationtype");

						String applyrelationdetailid = result.getString("applyrelationdetailid");// 发货申请明细ID
						String applyrelationorderid = result.getString("applyrelationorderid");// 发货申请编号
						String applyrelationmainid = result.getString("applyrelationmainid");// 发货申请主表ID

						if (!relationmainid.equals("") && relationmainids.indexOf(relationmainid) == -1 && relationtype == 0) {// 获取唯一订单主表ID
							relationmainids = relationmainids + (relationmainids.equals("") ? "" : ",") + relationmainid;
						}

						if (dcount > 0) {

							String sql = "select count,money,round(count-checkout_count+" + (oldstatus == 3 && relationtype == 0 ? oldcount : 0) + "," + countbit
									+ ") as cancount from stock where companyid='" + companyid + "' and itemid='" + itemid + "' and houseid='" + houseid + "' " + " and batchno='" + batchno + "'";

							Table table = DataUtils.queryData(conn, sql, null, null, null, null);
							if (table.getRows().size() > 0) {
								double scount = Double.parseDouble(table.getRows().get(0).getValue("count").toString());
								double cancount = Double.parseDouble(table.getRows().get(0).getValue("cancount").toString());

								if (cancount < dcount && relationtype == 0 && ((salesoutset != null && salesoutset == 1 && type > 0) || (salesoutset == null || salesoutset == 0))) {
									message = message + (message.equals("") ? "" : "；") + "第" + result.getInteger("goods_number") + "行的商品《" + result.getString("itemname") + " " + batchno + "》最新可出库存为"
											+ cancount;
									save = false;
								} else if (save) {
									double smoney = Double.parseDouble(table.getRows().get(0).getValue("money").toString());
									double cost_price = (scount == 0 ? 0 : Pdacommon.formatDoubleUp(smoney / scount, pricebit));
									double dcost_money = (scount == dcount ? Pdacommon.formatDoubleUp(smoney, moneybit) : Pdacommon.formatDoubleUp(dcount * cost_price, moneybit));
									double dtotal = result.getDoubleValue("total");
									double dprofit = Pdacommon.formatDoubleUp(Pdacommon.subtractdouble(dtotal, dcost_money), moneybit);
									double dprofit_rate = dprofit > 0 && dtotal > 0 ? Pdacommon.formatDoubleUp(dprofit / result.getDoubleValue("total"), 4) : 0;

									cost_money = Pdacommon.adddouble(cost_money, dcost_money);// 计算总的成本金额
									tprofit = Pdacommon.adddouble(tprofit, dprofit);
									if (okout) {// 关联送货单确认出货
										ps.addBatch("update storeoutdetail set price=" + result.getDoubleValue("price") + ",count=" + dcount + ",total=" + dtotal + ",tax="
												+ result.getDoubleValue("tax") + ",taxrate=" + result.getDoubleValue("taxrate") + ",taxprice=" + result.getDoubleValue("taxprice") + ",taxmoney="
												+ result.getDoubleValue("taxmoney") + ",status='" + status + "',deliverer='" + deliverer + "',license='" + license + "',originalbill='"
												+ maindata.getString("originalbill") + "',remark='" + result.getString("remark") + "',cost_price=" + cost_price + ",cost_money=" + dcost_money
												+ ",profit=" + dprofit + ",profit_rate=" + dprofit_rate + ",newcount=" + dcount + " where detailid='" + detailid + "'");
										newcount = Pdacommon.adddouble(newcount, dcount);
									} else {
										details = detail + orderid + "','" + (detailid.equals("") ? Common.getUpperUUIDString() : detailid) + "','" + maindata.getString("storeoutid") + "','"
												+ maindata.getString("originalbill") + "'," + result.getInteger("goods_number") + ",'" + companyid + "','" + maindata.getString("operate_by") + "','"
												+ maindata.getString("operate_time") + "','" + itemid + "','" + customerid + "','" + houseid + "'," + result.getDoubleValue("price") + "," + dcount
												+ "," + dtotal + "," + result.getDoubleValue("tax") + "," + result.getDoubleValue("taxrate") + "," + result.getDoubleValue("taxprice") + ","
												+ result.getDoubleValue("taxmoney") + ",'21','" + result.getString("remark") + "','" + status + "','"
												+ (detailid.equals("") ? loginuserid : result.getString("create_id")) + "','" + (detailid.equals("") ? loginUser : result.getString("create_by"))
												+ "'," + (detailid.equals("") ? "now()" : "'" + result.getString("create_time") + "'") + ",'" + loginuserid + "','" + loginUser + "',now(),'" + batchno
												+ "'," + cost_price + "," + dcost_money + "," + dprofit + "," + dprofit_rate + ",'" + relationdetailid + "','" + relationorderid + "','"
												+ relationmainid + "','" + deliverer + "','" + license + "'," + dcount + "," + relationtype + ",'" + applyrelationdetailid + "','"
												+ applyrelationorderid + "','" + applyrelationmainid + "')";
										ps.addBatch(details);
									}

									if (type == 1) {// 已出货
										// //原状态为待出货减原来的数据，不能减当前数量
										details = store
												+ ((oldstatus == 3 || relationtype == 1) ? " checkout_count=round(checkout_count-" + (relationtype == 1 ? dcount : oldcount) + "," + countbit + "),"
												: "") + "count=round(count-" + dcount + "," + countbit + "),money=round(money-" + dcost_money + "," + moneybit + ")"
												+ ",newcostprice=round(if(count=0,newcostprice,money/count)," + pricebit + ") where companyid='" + companyid + "' and itemid='" + itemid
												+ "' and houseid='" + houseid + "' and batchno='" + batchno + "'";

										ps.addBatch(details);

										ps.addBatch("delete from stock where itemid='" + itemid + "' and houseid='" + houseid + "' and batchno='" + batchno + "' and count=0 and money=0 ");

										details = itemmonth + Common.getUpperUUIDString() + "','" + companyid + "','" + itemid + "','" + houseid + "','" + sdate + "'," + syear + "," + smonth + ",-"
												+ dcount + ",-" + dcost_money + "," + dcount + "," + dtotal + "," + dcost_money + ",'" + batchno + "') on duplicate key update count=round(count-"
												+ dcount + "," + countbit + ")" + ",money=round(money-" + dcost_money + "," + moneybit + "),sellcount=round(sellcount+" + dcount + "," + countbit
												+ "),sellmoney=round(sellmoney+" + dtotal + "," + moneybit + "),sellcost=round(sellcost+" + dcost_money + "," + moneybit + ")";
										ps.addBatch(details);

										if (!relationdetailid.equals("")) {// 增加修改销售订单出库数量且订单月报表

											ps.addBatch("update salesorderdetail set "
													+ ((oldstatus == 3 || relationtype == 1) ? " checkout_count=round(checkout_count-" + (relationtype == 1 ? dcount : oldcount) + "," + countbit
													+ ")," : "") + "outcount=round(outcount+" + dcount + "," + countbit + ")" + ",outtotal=round(outtotal+" + dtotal + "," + moneybit
													+ ")  where detailid='" + relationdetailid + "'");

											ps.addBatch("update salesorderdetail set schedulstatus=if(schedulstatus='2','2',if(count<=outcount,'1','0')) where detailid='" + relationdetailid + "'");

											ps.addBatch("update salesorder set outcount=round(outcount+" + dcount + "," + countbit + "),outtotal=round(outtotal+" + dtotal + "," + moneybit + ")"
													+ " where (select 1 from salesorderdetail d where d.salesorderid=salesorder.salesorderid and d.detailid='" + relationdetailid + "' limit 1)");
											details = ordermonth + Common.getUpperUUIDString() + "','" + companyid + "','" + itemid + "','" + sdate + "'," + syear + "," + smonth + "," + dcount + ","
													+ dtotal + "," + dcost_money + ",'" + batchno + "') on duplicate key update salesoutcount=round(salesoutcount+" + dcount + "," + countbit
													+ "),salesoutmoney=round(salesoutmoney+" + dtotal + "," + moneybit + "),salesoutcost=round(salesoutcost+" + dcost_money + "," + moneybit + ")";
											ps.addBatch(details);
											// ps.addBatch("update salesorderdetail set schedulstatus='1' where  detailid='"
											// + relationdetailid +
											// "' and count<=outcount ");

										}

										if (result.getDoubleValue("tax") != 0) {
											totaltaxmoney = Pdacommon.adddouble(totaltaxmoney, result.getDoubleValue("taxmoney"));
										}
									}

									if (oldstatus == 0 && type == 3 && relationtype == 0) {// 原暂存变待出货
										details = store + " checkout_count=round(checkout_count+" + dcount + "," + countbit + ") where companyid='" + companyid + "' and itemid='" + itemid
												+ "' and houseid='" + houseid + "' and batchno='" + batchno + "'";
										ps.addBatch(details);
										if (!relationdetailid.equals("")) {

											String fsql = "select round(count-outcount-checkout_count," + countbit + ") as fcount from salesorderdetail where  detailid='" + relationdetailid + "'";

											Object cobject = DataUtils.getValueBySQL(conn, fsql, null);
											double fcount;
											if (cobject == null) {
												fcount = 0;
											} else {
												fcount = Double.parseDouble(cobject.toString());
											}
											if (fcount <= 0) {
												message = message + (message.equals("") ? "" : "；") + "第" + result.getInteger("goods_number") + "行的商品《" + result.getString("itemname") + " " + batchno
														+ "》关联的订单最新可出库小于或等于0，此行数据不能再进行出库操作，请删除。";
												save = false;
											}

											ps.addBatch("update salesorderdetail set  checkout_count=round(checkout_count+" + dcount + "," + countbit + ") where detailid='" + relationdetailid + "'");
										}
									}

									if (oldstatus == 3 && type == 0 && relationtype == 0) {// 原待出货变暂存
										details = store + " checkout_count=round(checkout_count-" + oldcount + "," + countbit + ") where companyid='" + companyid + "' and itemid='" + itemid
												+ "' and houseid='" + houseid + "' and batchno='" + batchno + "'";
										ps.addBatch(details);
										if (!relationdetailid.equals("")) {
											ps.addBatch("update salesorderdetail set  checkout_count=round(checkout_count-" + oldcount + "," + countbit + ") where detailid='" + relationdetailid + "'");
										}
									}
									if (oldstatus == 3 && type == 3 && relationtype == 0) {// 原待出货变待出货
										details = store + " checkout_count=round(checkout_count-" + oldcount + "+" + dcount + "," + countbit + ") where companyid='" + companyid + "' and itemid='"
												+ itemid + "' and houseid='" + houseid + "' and batchno='" + batchno + "'";
										ps.addBatch(details);
										if (!relationdetailid.equals("")) {
											ps.addBatch("update salesorderdetail set  checkout_count=round(checkout_count-" + oldcount + "+" + dcount + "," + countbit + ") where detailid='"
													+ relationdetailid + "'");
										}
									}
								}

							} else if (salesoutset != null && salesoutset == 1 && type == 0) {
								double smoney = 0;
								double cost_price = 0;
								double dcost_money = 0;
								double dtotal = result.getDoubleValue("total");
								double dprofit = Pdacommon.formatDoubleUp(Pdacommon.subtractdouble(dtotal, dcost_money), moneybit);
								double dprofit_rate = dprofit > 0 && dtotal > 0 ? Pdacommon.formatDoubleUp(dprofit / result.getDoubleValue("total"), 4) : 0;

								cost_money = Pdacommon.adddouble(cost_money, dcost_money);// 计算总的成本金额
								tprofit = Pdacommon.adddouble(tprofit, dprofit);

								details = detail + orderid + "','" + (detailid.equals("") ? Common.getUpperUUIDString() : detailid) + "','" + maindata.getString("storeoutid") + "','"
										+ maindata.getString("originalbill") + "'," + result.getInteger("goods_number") + ",'" + companyid + "','" + maindata.getString("operate_by") + "','"
										+ maindata.getString("operate_time") + "','" + itemid + "','" + customerid + "','" + houseid + "'," + result.getDoubleValue("price") + "," + dcount + ","
										+ dtotal + "," + result.getDoubleValue("tax") + "," + result.getDoubleValue("taxrate") + "," + result.getDoubleValue("taxprice") + ","
										+ result.getDoubleValue("taxmoney") + ",'21','" + result.getString("remark") + "','" + status + "','"
										+ (detailid.equals("") ? loginuserid : result.getString("create_id")) + "','" + (detailid.equals("") ? loginUser : result.getString("create_by")) + "',"
										+ (detailid.equals("") ? "now()" : "'" + result.getString("create_time") + "'") + ",'" + loginuserid + "','" + loginUser + "',now(),'" + batchno + "',"
										+ cost_price + "," + dcost_money + "," + dprofit + "," + dprofit_rate + ",'" + relationdetailid + "','" + relationorderid + "','" + relationmainid + "','"
										+ deliverer + "','" + license + "'," + dcount + "," + relationtype + ",'" + applyrelationdetailid + "','" + applyrelationorderid + "','" + applyrelationmainid
										+ "')";
								ps.addBatch(details);
							}

							if (type > 0) {
								String temp = "('" + itemid + "','" + houseid + "')";
								if (condition.toString().equals("")) {
									condition.append(temp);
								} else {
									if (condition.toString().indexOf(temp) == -1) {
										condition.append("," + temp);
									}
								}
							}
						} else {

							double cost_price = result.getDoubleValue("cost_price");
							double dcost_money = result.getDoubleValue("cost_money");
							double dtotal = result.getDoubleValue("total");
							double dprofit = Pdacommon.formatDoubleUp(Pdacommon.subtractdouble(dtotal, dcost_money), moneybit);
							double dprofit_rate = dprofit > 0 && dtotal > 0 ? Pdacommon.formatDoubleUp(dprofit / result.getDoubleValue("total"), 4) : 0;

							cost_money = Pdacommon.adddouble(cost_money, dcost_money);// 计算总的成本金额
							tprofit = Pdacommon.adddouble(tprofit, dprofit);
							if (okout) {// 关联送货单确认出货
								ps.addBatch("update storeoutdetail set price=" + result.getDoubleValue("price") + ",count=" + dcount + ",total=" + dtotal + ",tax=" + result.getDoubleValue("tax")
										+ ",taxrate=" + result.getDoubleValue("taxrate") + ",taxprice=" + result.getDoubleValue("taxprice") + ",taxmoney=" + result.getDoubleValue("taxmoney")
										+ ",status='" + status + "',deliverer='" + deliverer + "',license='" + license + "',originalbill='" + maindata.getString("originalbill") + "',remark='"
										+ result.getString("remark") + "',cost_price=" + cost_price + ",cost_money=" + dcost_money + ",profit=" + dprofit + ",profit_rate=" + dprofit_rate
										+ ",newcount=" + dcount + " where detailid='" + detailid + "'");
								newcount = Pdacommon.adddouble(newcount, dcount);
							} else {
								details = detail + orderid + "','" + (detailid.equals("") ? Common.getUpperUUIDString() : detailid) + "','" + maindata.getString("storeoutid") + "','"
										+ maindata.getString("originalbill") + "'," + result.getInteger("goods_number") + ",'" + companyid + "','" + maindata.getString("operate_by") + "','"
										+ maindata.getString("operate_time") + "','" + itemid + "','" + customerid + "','" + houseid + "'," + result.getDoubleValue("price") + "," + dcount + ","
										+ dtotal + "," + result.getDoubleValue("tax") + "," + result.getDoubleValue("taxrate") + "," + result.getDoubleValue("taxprice") + ","
										+ result.getDoubleValue("taxmoney") + ",'21','" + result.getString("remark") + "','" + status + "','"
										+ (detailid.equals("") ? loginuserid : result.getString("create_id")) + "','" + (detailid.equals("") ? loginUser : result.getString("create_by")) + "',"
										+ (detailid.equals("") ? "now()" : "'" + result.getString("create_time") + "'") + ",'" + loginuserid + "','" + loginUser + "',now(),'" + batchno + "',"
										+ cost_price + "," + dcost_money + "," + dprofit + "," + dprofit_rate + ",'" + relationdetailid + "','" + relationorderid + "','" + relationmainid + "','"
										+ deliverer + "','" + license + "'," + dcount + "," + relationtype + ",'" + applyrelationdetailid + "','" + applyrelationorderid + "','" + applyrelationmainid
										+ "')";

								ps.addBatch(details);
							}

							if (type == 1) {// 已出货
								// //原状态为待出货减原来的数据，不能减当前数量
								details = store
										+ ((oldstatus == 3 || relationtype == 1) ? " checkout_count=round(checkout_count-" + (relationtype == 1 ? dcount : oldcount) + "," + countbit + ")," : "")
										+ "count=round(count-" + dcount + "," + countbit + "),money=round(money-" + dcost_money + "," + moneybit + ")"
										+ ",newcostprice=round(if(count=0,newcostprice,money/count)," + pricebit + ") where companyid='" + companyid + "' and itemid='" + itemid + "' and houseid='"
										+ houseid + "' and batchno='" + batchno + "'";

								ps.addBatch(details);

								ps.addBatch("delete from stock where itemid='" + itemid + "' and houseid='" + houseid + "' and batchno='" + batchno + "' and count=0 and money=0 ");

								details = itemmonth + Common.getUpperUUIDString() + "','" + companyid + "','" + itemid + "','" + houseid + "','" + sdate + "'," + syear + "," + smonth + ",-" + dcount
										+ ",-" + dcost_money + "," + dcount + "," + dtotal + "," + dcost_money + ",'" + batchno + "') on duplicate key update count=round(count-" + dcount + ","
										+ countbit + ")" + ",money=round(money-" + dcost_money + "," + moneybit + "),sellcount=round(sellcount+" + dcount + "," + countbit
										+ "),sellmoney=round(sellmoney+" + dtotal + "," + moneybit + "),sellcost=round(sellcost+" + dcost_money + "," + moneybit + ")";
								ps.addBatch(details);

								if (!relationdetailid.equals("")) {// 增加修改销售订单出库数量且订单月报表

									ps.addBatch("update salesorderdetail set "
											+ ((oldstatus == 3 || relationtype == 1) ? " checkout_count=round(checkout_count-" + (relationtype == 1 ? dcount : oldcount) + "," + countbit + ")," : "")
											+ "outcount=round(outcount+" + dcount + "," + countbit + ")" + ",outtotal=round(outtotal+" + dtotal + "," + moneybit + ")  where detailid='"
											+ relationdetailid + "'");

									ps.addBatch("update salesorderdetail set schedulstatus=if(schedulstatus='2','2',if(count<=outcount,'1','0')) where detailid='" + relationdetailid + "'");

									ps.addBatch("update salesorder set outcount=round(outcount+" + dcount + "," + countbit + "),outtotal=round(outtotal+" + dtotal + "," + moneybit + ")"
											+ " where (select 1 from salesorderdetail d where d.salesorderid=salesorder.salesorderid and d.detailid='" + relationdetailid + "' limit 1)");
									details = ordermonth + Common.getUpperUUIDString() + "','" + companyid + "','" + itemid + "','" + sdate + "'," + syear + "," + smonth + "," + dcount + "," + dtotal
											+ "," + dcost_money + ",'" + batchno + "') on duplicate key update salesoutcount=round(salesoutcount+" + dcount + "," + countbit
											+ "),salesoutmoney=round(salesoutmoney+" + dtotal + "," + moneybit + "),salesoutcost=round(salesoutcost+" + dcost_money + "," + moneybit + ")";
									ps.addBatch(details);
									// ps.addBatch("update salesorderdetail set schedulstatus='1' where  detailid='"
									// + relationdetailid +
									// "' and count<=outcount ");

								}

								if (result.getDoubleValue("tax") != 0) {
									totaltaxmoney = Pdacommon.adddouble(totaltaxmoney, result.getDoubleValue("taxmoney"));
								}
							}

							if (oldstatus == 0 && type == 3 && relationtype == 0) {// 原暂存变待出货
								details = store + " checkout_count=round(checkout_count+" + dcount + "," + countbit + ") where companyid='" + companyid + "' and itemid='" + itemid + "' and houseid='"
										+ houseid + "' and batchno='" + batchno + "'";
								ps.addBatch(details);
								if (!relationdetailid.equals("")) {

									String fsql = "select round(count-outcount-checkout_count," + countbit + ") as fcount from salesorderdetail where  detailid='" + relationdetailid + "'";

									Object cobject = DataUtils.getValueBySQL(conn, fsql, null);
									double fcount;
									if (cobject == null) {
										fcount = 0;
									} else {
										fcount = Double.parseDouble(cobject.toString());
									}
									if (fcount <= 0) {
										message = message + (message.equals("") ? "" : "；") + "第" + result.getInteger("goods_number") + "行的商品《" + result.getString("itemname") + " " + batchno
												+ "》关联的订单最新可出库小于或等于0，此行数据不能再进行出库操作，请删除。";
										save = false;
									}

									ps.addBatch("update salesorderdetail set  checkout_count=round(checkout_count+" + dcount + "," + countbit + ") where detailid='" + relationdetailid + "'");
								}
							}

							if (oldstatus == 3 && type == 0 && relationtype == 0) {// 原待出货变暂存
								details = store + " checkout_count=round(checkout_count-" + oldcount + "," + countbit + ") where companyid='" + companyid + "' and itemid='" + itemid
										+ "' and houseid='" + houseid + "' and batchno='" + batchno + "'";
								ps.addBatch(details);
								if (!relationdetailid.equals("")) {
									ps.addBatch("update salesorderdetail set  checkout_count=round(checkout_count-" + oldcount + "," + countbit + ") where detailid='" + relationdetailid + "'");
								}
							}
							if (oldstatus == 3 && type == 3 && relationtype == 0) {// 原待出货变待出货
								details = store + " checkout_count=round(checkout_count-" + oldcount + "+" + dcount + "," + countbit + ") where companyid='" + companyid + "' and itemid='" + itemid
										+ "' and houseid='" + houseid + "' and batchno='" + batchno + "'";
								ps.addBatch(details);
								if (!relationdetailid.equals("")) {
									ps.addBatch("update salesorderdetail set  checkout_count=round(checkout_count-" + oldcount + "+" + dcount + "," + countbit + ") where detailid='"
											+ relationdetailid + "'");
								}
							}
						}

						if (type > 0) {
							String temp = "('" + itemid + "','" + houseid + "')";
							if (condition.toString().equals("")) {
								condition.append(temp);
							} else {
								if (condition.toString().indexOf(temp) == -1) {
									condition.append("," + temp);
								}
							}
						}
						if (oldstatus == 0 && (type == 0 || type == 3 || type == 1) && relationtype == 1) {

							if (!applyrelationdetailid.equals("")) {

								String kfsql = "select notoutcount from storeoutapplydetail where  detailid='" + applyrelationdetailid + "'";

								Object kcobject = DataUtils.getValueBySQL(conn, kfsql, null);
								double notoutcount;
								if (kcobject == null) {
									notoutcount = 0;
								} else {
									notoutcount = Double.parseDouble(kcobject.toString());
								}

								if (notoutcount < dcount) {
									message = message + (message.equals("") ? "" : "；") + "第" + result.getInteger("goods_number") + "行的商品《" + result.getString("itemname") + " " + batchno
											+ "》关联的发货申请单最新未出库数量" + notoutcount + "小于当前出库数量" + dcount + "，此行数据不能再进行出库操作，请删除。";
									save = false;
								}
								if (type == 3 || type == 1) {
									ps.addBatch("update storeoutapplydetail set  notoutcount=round(notoutcount-" + dcount + "," + countbit + "),outcount=round(outcount+" + dcount + "," + countbit
											+ ") where detailid='" + applyrelationdetailid + "'");
								}
							}
						}

						if (oldstatus == 3 && type == 0 && relationtype == 1) {// 原待出货变暂存

							ps.addBatch("update storeoutapplydetail set  notoutcount=round(notoutcount+" + oldcount + "," + countbit + "),outcount=round(outcount-" + oldcount + "," + countbit
									+ ") where detailid='" + applyrelationdetailid + "'");
						}
						if (oldstatus == 3 && type == 3 && relationtype == 1) {// 原待出货变待出货

							ps.addBatch("update storeoutapplydetail set  notoutcount=round(notoutcount+" + oldcount + "-" + dcount + "," + countbit + "),outcount=round(outcount-" + oldcount + "+"
									+ dcount + "," + countbit + ") where detailid='" + applyrelationdetailid + "'");
						}

					}

					if ((salesoutset != null && salesoutset == 1 && type > 0) || (salesoutset == null || salesoutset == 0)) {

						for (i = 0; i < checkdata.size(); i++) {
							JSONObject checkresult = JSONObject.parseObject(checkdata.getString(i));
							String fhouseid = checkresult.getString("houseid");
							String fbatchno = checkresult.getString("batchno");
							double ftotalcount = checkresult.getDoubleValue("totalcount");
							String fitemid = checkresult.getString("itemid");
							String fcodeid = checkresult.getString("codeid");

							String checksql = "select count,round(count-checkout_count+" + (oldstatus == 3 ? ftotalcount : 0) + "," + countbit + ") as cancount from stock where companyid='"
									+ companyid + "' and itemid='" + fitemid + "' and houseid='" + fhouseid + "' " + " and batchno='" + fbatchno + "'";

							Table ftable = DataUtils.queryData(conn, checksql, null, null, null, null);
							// 2020-12-10 涉及到仓库没有数据问题。
							double fcancount = 0;

							// System.out.println(checksql + " " +
							// ftable.getRows().size() + " " + oldstatus);

							if (ftable.getRows().size() > 0) {
								fcancount = Double.parseDouble(ftable.getRows().get(0).getValue("cancount").toString());
								if (fcancount < checkresult.getDoubleValue("totalcount")) {
									message = message + "(" + fcodeid + "、" + checkresult.getString("itemname") + "、" + checkresult.getString("housename") + " " + fbatchno + "《总出库数量》："
											+ checkresult.getDoubleValue("totalcount") + " 大于 《最大可出库的库存数量》：" + fcancount + ")";
									save = false;
								}
							} else {// 2020-12-10 增加
								message = message + "(" + fcodeid + "、" + checkresult.getString("itemname") + "、" + checkresult.getString("housename") + " " + fbatchno + "《总出库数量》："
										+ checkresult.getDoubleValue("totalcount") + " 大于 《最大可出库的库存数量》：" + fcancount + ")";
								save = false;
							}
						}
					}

					if (!message.equals("")) {
						message = message + "，保存失败。";
					} else {

						if (okout) {// 关联送货单确认出货
							ps.addBatch("update deliverdetail set newcount=" + newcount + ",d_customerid='" + customerid + "',d_customername='" + customername + "',d_linkman='" + linkman
									+ "',d_linkphone='" + linkphone + "',d_deliveryadrr='" + deliveryadrr + "',fstatus='1',update_id='" + loginuserid + "',update_by='" + loginUser
									+ "',update_time=now() where detailid='" + deliverdetailid + "' and status='1' and fstatus='0'");
							ps.addBatch("update deliver set newcount=" + newcount + ",update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now()  where deliverid='"
									+ delivermainid + "' and status='1'");

						}
					}
				}

				if (type == 1 && !relationmainids.equals("")) {// 更改销售订单的出库状态，符合改为已完成出库
					String[] farr = relationmainids.split(",");
					for (int j = 0; j < farr.length; j++) {
						ps.addBatch("update salesorder set stockstatus = '1' where salesorderid='" + farr[j]
								+ "'   and status='1' and stockstatus = '0' and if((select 1 from salesorderdetail where salesorderid='" + farr[j]
								+ "' and schedulstatus='0' and status='1' limit 1),false,true)");
					}
				}
			} else {
				save = false;
				message = message + "没有商品明细数据，保存失败";
			}

			if (save) {

				if (type == 1 && totalmoney != 0) {
					// 更新客户应收应付款
					String customersql = "update customer set receivable=round(receivable+" + totalmoney + "," + moneybit + ")"
							+ (totaltaxmoney != 0 ? ",T_receivable=round(T_receivable+" + totaltaxmoney + "," + moneybit + ")" : "") + " where companyid='" + companyid + "' and customerid='"
							+ customerid + "'";
					ps.addBatch(customersql);

					// 增加往来单位月收支报表
					String customermonthsql = "insert into customermonth (monthid,companyid,customerid,sdate,syear,smonth,receivable,rec_sellout_money,rec_add_money"
							+ (totaltaxmoney != 0 ? ",T_receivable,T_rec_sellout_money,T_rec_add_money" : "")
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
							+ totalmoney
							+ ","
							+ totalmoney
							+ ","
							+ totalmoney
							+ (totaltaxmoney != 0 ? "," + totalmoney + "," + totalmoney + "," + totalmoney : "")
							+ ") on duplicate key update receivable=round(receivable+"
							+ totalmoney
							+ ","
							+ moneybit
							+ "),rec_sellout_money=round(rec_sellout_money+"
							+ totalmoney
							+ ","
							+ moneybit
							+ "),rec_add_money=round(rec_add_money+"
							+ totalmoney
							+ ","
							+ moneybit
							+ ")"
							+ (totaltaxmoney != 0 ? ",T_receivable=round(T_receivable+" + totaltaxmoney + "," + moneybit + "),T_rec_sellout_money=round(T_rec_sellout_money+" + totaltaxmoney + ","
							+ moneybit + "),T_rec_add_money=round(T_rec_add_money+" + totaltaxmoney + "," + moneybit + ")" : "");
					ps.addBatch(customermonthsql);

					// 增加往来单位年报表
					String customeryearsql = "insert into customeryear (yearid,companyid,customerid,syear,receivable,rec_sellout_money,rec_add_money"
							+ (totaltaxmoney != 0 ? ",T_receivable,T_rec_sellout_money,T_rec_add_money" : "")
							+ ") values('"
							+ Common.getUpperUUIDString()
							+ "','"
							+ companyid
							+ "','"
							+ customerid
							+ "',"
							+ syear
							+ ","
							+ totalmoney
							+ ","
							+ totalmoney
							+ ","
							+ totalmoney
							+ (totaltaxmoney != 0 ? "," + totalmoney + "," + totalmoney + "," + totalmoney : "")
							+ ") on duplicate key update receivable=round(receivable+"
							+ totalmoney
							+ ","
							+ moneybit
							+ ")"
							+ ",rec_sellout_money=round(rec_sellout_money+"
							+ totalmoney
							+ ","
							+ moneybit
							+ "),rec_add_money=round(rec_add_money+"
							+ totalmoney
							+ ","
							+ moneybit
							+ ")"
							+ (totaltaxmoney != 0 ? ",T_receivable=round(T_receivable+" + totaltaxmoney + "," + moneybit + "),T_rec_sellout_money=round(T_rec_sellout_money+" + totaltaxmoney + ","
							+ moneybit + "),T_rec_add_money=round(T_rec_add_money+" + totaltaxmoney + "," + moneybit + ")" : "");
					ps.addBatch(customeryearsql);
				}
				int changebilltype = Pdacommon.getDatalogBillChangefunc("storeout", "");
				if (operate.equals("draftedit")) {// 草稿编辑 更新
					ps.addBatch("update storeout set orderid='" + orderid + "', originalbill='" + maindata.getString("originalbill") + "', currency='" + maindata.getString("currency")
							+ "',operate_time='" + operate_time + "',operate_by='" + maindata.getString("operate_by") + "',houseid='',customerid='" + customerid + "',count=" + count + ",total="
							+ total + ",totaltax=" + totaltax + ",totalmoney=" + totalmoney + ",remark='" + maindata.getString("remark") + "',status='" + status + "',update_id='" + loginuserid
							+ "',update_by='" + loginUser + "',update_time=now(),iproperty='" + maindata.getString("iproperty") + "',cost_money=" + cost_money + ",profit=" + tprofit
							+ ",profit_rate=round(" + (tprofit > 0 && total > 0 ? (tprofit / total) : 0) + ",4)," + (okout ? " newcount=" : "oldcount=") + count + ",linkman='" + linkman
							+ "',linkphone='" + linkphone + "',deliveryadrr='" + deliveryadrr + "',deliverer='" + deliverer + "',license='" + license + "',receiver='" + receiver
							+ "' where storeoutid='" + maindata.getString("storeoutid") + "'");

					ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
							+ companyid + "'," + changebilltype + ",'修改','" + maindata.getString("storeoutid") + "','单据编号：" + orderid + "','" + loginuserid + "','" + loginUser + "',now())");

					ps.addBatch("update storeoutdetail d,storeout s set d.create_id=s.create_id,d.create_by=s.create_by,d.create_time=s.create_time where d.storeoutid = s.storeoutid and s.storeoutid='"
							+ maindata.getString("storeoutid") + "'");

				} else {
					String main = "insert into storeout (orderid,storeoutid,bill_type,originalbill,companyid,operate_time,operate_by,houseid,customerid,currency,count,total,totaltax,totalmoney,remark,status,printing,outexcel,create_id,create_by,create_time,update_id,update_by,update_time,iproperty,cost_money,profit,profit_rate,oldcount,linkman,linkphone,deliveryadrr,deliverer,license,receiver) VALUES ('"
							+ orderid
							+ "','"
							+ maindata.getString("storeoutid")
							+ "','"
							+ maindata.getString("bill_type")
							+ "','"
							+ maindata.getString("originalbill")
							+ "','"
							+ companyid
							+ "','"
							+ operate_time
							+ "','"
							+ maindata.getString("operate_by")
							+ "','','"
							+ customerid
							+ "','"
							+ maindata.getString("currency")
							+ "',"
							+ count
							+ ","
							+ total
							+ ","
							+ totaltax
							+ ","
							+ totalmoney
							+ ",'"
							+ maindata.getString("remark")
							+ "','"
							+ status
							+ "',0,0,'"
							+ loginuserid
							+ "','"
							+ loginUser
							+ "',now(),'"
							+ loginuserid
							+ "','"
							+ loginUser
							+ "',now(),'"
							+ maindata.getString("iproperty")
							+ "',"
							+ cost_money
							+ ","
							+ tprofit
							+ ",round("
							+ (tprofit > 0 && total > 0 ? (tprofit / total) : 0)
							+ ",4)"
							+ ","
							+ count
							+ ",'" + linkman + "','" + linkphone + "','" + deliveryadrr + "','" + deliverer + "','" + license + "','" + receiver + "')";
					ps.addBatch(main);

					ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
							+ companyid + "'," + changebilltype + ",'" + (status == 0 ? "新增暂存" : "新增保存") + "','" + maindata.getString("storeoutid") + "','单据编号：" + orderid + "','" + loginuserid
							+ "','" + loginUser + "',now())");

				}

				if (okout) {// 更新总送货单状态
					String dsql = "select deliverer,license from deliver where deliverid='" + delivermainid + "'";
					Table dtable = DataUtils.queryData(conn, dsql, null, null, null, null);
					if (dtable.getRows().size() > 0) {
						String fdeliverer = dtable.getRows().get(0).getString("deliverer");
						String flicense = dtable.getRows().get(0).getString("license");

						ps.addBatch("update deliver set status='2'  where deliverid='" + delivermainid + "' and (select count(d.detailid) from deliverdetail d where d.deliverid='" + delivermainid
								+ "' and d.fstatus='0')=0");// 更新送货主表状态
						ps.addBatch("update deliverdetail set status='2'  where deliverid='" + delivermainid + "' and (select count(d.deliverid) from deliver d where d.deliverid='" + delivermainid
								+ "' and d.status='2')=1");// 更新送货明细状态

						// 送货单完结更新销售出库单的送货人，送货车号
						ps.addBatch("update storeoutdetail set deliverer='" + fdeliverer + "',license='" + flicense
								+ "' where storeoutid in (select count(f.detailid) from deliverdetail f where f.deliverid='" + delivermainid
								+ "' and f.fstatus='1') and (select count(d.detailid) from deliverdetail d where d.deliverid='" + delivermainid + "' and d.fstatus='0')=0");
						ps.addBatch("update storeout set deliverer='" + fdeliverer + "',license='" + flicense
								+ "' where storeoutid in (select count(f.detailid) from deliverdetail f where f.deliverid='" + delivermainid
								+ "' and f.fstatus='1') and (select count(d.detailid) from deliverdetail d where d.deliverid='" + delivermainid + "' and d.fstatus='0')=0");

						ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString()
								+ "','" + companyid + "',32,'确认出货','" + delivermainid + "','送货编号：" + deliverorderid + "，销售出货单号：" + orderid + "','" + loginuserid + "','" + loginUser + "',now())");
					}
				}
				ps.executeBatch();
				conn.commit();
				conn.setAutoCommit(true);
				state = "1";

				if (type > 0) {
					// 库存报警
					Bhouselimit = Common.gethouselimitdata(conn, companyid, condition, Bhouselimit);
				}
			}

		} catch (Exception e) {
			// e.printStackTrace();
			try {
				conn.rollback();
			} catch (Exception e1) {
				// e1.printStackTrace();
			}
			message = "保存失败!!";// + e.getMessage().toString();
		} finally {
			condition.setLength(0);
			conn.close();
		}
		rt.put("state", state);
		rt.put("message", message);
		rt.put("warning", Bhouselimit);
		return rt;
	}

	// 保存客户退货单信息
	public static JSONObject saveStoreOutInFunction(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject rt = new JSONObject();
		JSONObject maindata = JSONObject.parseObject(params.getString("maindata"));
		Integer type = params.getInteger("type");
		Integer pricebit = params.getInteger("pricebit");
		Integer moneybit = params.getInteger("moneybit");
		String loginUser = params.getString("loginUser");
		String loginuserid = params.getString("loginuserid");
		JSONArray detaildata = params.getJSONArray("detaildata");
		String operate = params.getString("operate");// draftedit 编辑单据
		String state = "0";

		String companyid = maindata.getString("companyid");
		String houseid = maindata.getString("houseid");
		String customerid = maindata.getString("customerid");
		String operate_time = maindata.getString("operate_time");
		double count = maindata.getDoubleValue("count");
		double total = maindata.getDoubleValue("total");
		double totaltax = maindata.getDoubleValue("totaltax");
		double totalmoney = maindata.getDoubleValue("totalmoney");

		double cost_money = 0;
		double tprofit = 0;

		double totaltaxmoney = 0;

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

		try {
			int countbit = 0;
			int t6days = 0;

			Table companytalbe = DataUtils.queryData(conn, "select pricebit,countbit,moneybit,t6days from s_company_config where company_id='" + companyid + "'", null, null, null, null);
			if (companytalbe.getRows().size() > 0) {
				countbit = companytalbe.getRows().get(0).getInteger("countbit");
				t6days = companytalbe.getRows().get(0).getInteger("t6days");
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
			conn.setAutoCommit(false);
			String detail = "insert into storeoutdetail (orderid,detailid,storeoutid,originalbill,goods_number,companyid,operate_by,operate_time,itemid,customerid,houseid,price,count,total,tax,taxrate,taxprice,taxmoney,stype,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,batchno,cost_price,cost_money,profit,profit_rate,returndetailid,returnorderid) VALUES ('";
			String store = "insert into stock (stockid,companyid,itemid,houseid,count,money,newcostprice,batchno,stockremark) VALUES ('";
			String itemmonth = "insert into itemmonth (monthid,companyid,itemid,houseid,sdate,syear,smonth,count,money,t_sellcount,t_sellmoney,t_sellcost,batchno) VALUES ('";
			// 订单月报表
			String ordermonth = "insert into ordermonth (monthid,companyid,itemid,sdate,syear,smonth,salesincount,salesinmoney,salesoutcost,batchno) VALUES ('";

			String orderid = operate.equals("draftedit") ? maindata.getString("orderid") : getOrderidByparams(companyid, "storeout", "7", billdate, conn);

			String details = "";
			if (save && operate.equals("draftedit")) {// 草稿编辑 删除明细数据重新增加
				String fsql = "select status from storeout where storeoutid='" + maindata.getString("storeoutid") + "'";
				Object cobject = DataUtils.getValueBySQL(conn, fsql, null);
				if (cobject == null) {
					save = false;
					message = "当前记录已删除，操作失败。";
					state = "2";
				} else {
					String fstatus = cobject.toString();
					if (fstatus.equals("0")) {
						ps.addBatch("delete from storeoutdetail where storeoutid='" + maindata.getString("storeoutid") + "'");
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
				String relationmainids = "";// 销售订单主表
				for (i = 0; i < detaildata.size(); i++) {
					if (save) {
						JSONObject result = JSONObject.parseObject(detaildata.getString(i));
						String detailid = result.getString("detailid");
						String batchno = result.getString("batchno");
						String itemid = result.getString("itemid");
						double dcount = result.getDoubleValue("count");
						double dtotal = result.getDoubleValue("total");
						String returndetailid = result.getString("returndetailid");
						String returnorderid = result.getString("returnorderid");
						double cost_price = 0;

						if (!returndetailid.equals("")) {
							cost_price = result.getDoubleValue("cost_price");
						} else {
							String sqlcost = "select newcostprice from stock where   itemid='" + itemid + "' and houseid='" + houseid + "' " + " and batchno='" + batchno + "'";

							Table table = DataUtils.queryData(conn, sqlcost, null, null, null, null);
							if (table.getRows().size() > 0) {// 当前仓库有记录
								cost_price = Double.parseDouble(table.getRows().get(0).getValue("newcostprice").toString());
							} else {// 当前仓库无记录
								sqlcost = "select  price  from storeoutdetail where  itemid='" + itemid + "'  and batchno='" + batchno
										+ "' and status='1' and stype='21' order by update_time desc limit 1";
								table = DataUtils.queryData(conn, sqlcost, null, null, null, null);
								if (table.getRows().size() > 0) {
									cost_price = Double.parseDouble(table.getRows().get(0).getValue("price").toString());
								} else {
									cost_price = result.getDoubleValue("cost_price");
								}
							}

						}

						double dcost_money = Pdacommon.formatDoubleUp(dcount * cost_price, moneybit);

						double canreturncount = 0;
						String relationdetailid = "";
						Integer relationtype = 0;
						String applyrelationdetailid = "";
						Table stable = null;
						if (!returndetailid.equals("")) {// 出库单可退数量
							String sql = "select round(count-returncount," + countbit + ") as cancount,round(cost_money-ifnull((select sum(cost_money) from storeoutdetail where relationdetailid='"
									+ returndetailid + "' and status='1') ,0)," + moneybit
									+ ") as cost_money,relationdetailid,relationmainid,relationtype,applyrelationdetailid from storeoutdetail where detailid='" + returndetailid + "'";
							stable = DataUtils.queryData(conn, sql, null, null, null, null);

							if (stable.getRows().size() > 0) {
								canreturncount = Double.parseDouble(stable.getRows().get(0).getValue("cancount").toString());
								relationdetailid = stable.getRows().get(0).getString("relationdetailid");
								String relationmainid = stable.getRows().get(0).getString("relationmainid");

								relationtype = stable.getRows().get(0).getInteger("relationtype");
								applyrelationdetailid = stable.getRows().get(0).getString("applyrelationdetailid");// 发货申请明细ID

								if (!relationmainid.equals("") && relationmainids.indexOf(relationmainid) == -1) {// 获取唯一订单主表ID
									relationmainids = relationmainids + (relationmainids.equals("") ? "" : ",") + relationmainid;
								}

								if (canreturncount == dcount) {
									dcost_money = Double.parseDouble(stable.getRows().get(0).getValue("cost_money").toString());
								}
							}

							if (canreturncount < dcount) {
								message = message + (message.equals("") ? "" : "；") + "第" + result.getInteger("goods_number") + "行的商品《" + result.getString("itemname") + " "
										+ result.getString("batchno") + "》已超出销售单单号《" + returnorderid + "》可以退回的数量" + canreturncount;
								save = false;
							}
						}

						double dprofit = Pdacommon.formatDoubleUp(Pdacommon.subtractdouble(dtotal, dcost_money), moneybit);
						double dprofit_rate = dprofit > 0 && result.getDoubleValue("total") > 0 ? Pdacommon.formatDoubleUp(dprofit / result.getDoubleValue("total"), 4) : 0;

						cost_money = Pdacommon.adddouble(cost_money, dcost_money);// 计算总的成本金额
						tprofit = Pdacommon.adddouble(tprofit, dprofit);
						if (type > 0) {

							if (!returndetailid.equals("") && save) {// 出库单可退数量

								ps.addBatch("update storeoutdetail set returncount=round(returncount+" + dcount + "," + countbit + ") where  detailid='" + returndetailid + "'");

								// 销售退货关联销售单且销售单关联销售订单，修改其实际出库数，及统计期销售退货数
								if (!relationdetailid.equals("")) {
									ps.addBatch("update salesorderdetail set outcount=round(outcount-" + dcount + "," + countbit + "),outtotal=round(outtotal-" + dtotal + "," + moneybit
											+ ")  where detailid='" + relationdetailid + "'");

									ps.addBatch("update salesorderdetail set  schedulstatus=if(count>outcount,'0',schedulstatus) where detailid='" + relationdetailid + "'");

									ps.addBatch("update salesorder set outcount=round(outcount-" + dcount + "," + countbit + "),outtotal=round(outtotal-" + dtotal + "," + moneybit + ")"
											+ " where salesorderid in (select d.salesorderid from salesorderdetail d where d.detailid='" + relationdetailid + "')");
									details = ordermonth + Common.getUpperUUIDString() + "','" + companyid + "','" + itemid + "','" + sdate + "'," + syear + "," + smonth + "," + dcount + "," + dtotal
											+ "," + dcost_money + ",'" + batchno + "') on duplicate key update salesincount=round(salesincount+" + dcount + "," + countbit
											+ "),salesinmoney=round(salesinmoney+" + dtotal + "," + moneybit + "),salesoutcost=round(salesoutcost-" + dcost_money + "," + moneybit + ")";
									// ps.addBatch("update salesorderdetail set schedulstatus='0' where  detailid='"
									// + relationdetailid +
									// "' and schedulstatus in ('1','2')");
									ps.addBatch(details);
								}
							}

							// details = store + Common.getUpperUUIDString() +
							// "','" + companyid + "','" + itemid + "','" +
							// houseid + "'," + dcount + "," + dtotal + ",-" +
							// dcount + ",-" + dtotal + ",-"
							// + dcost_money + ",round(" + (dcount > 0 ? dtotal
							// / dcount : 0) + "," + pricebit + "),'" + batchno
							// + "') on duplicate key update count=round(count+"
							// + dcount + ","
							// + countbit + ")" + ",money=round(money+" +
							// dcost_money + "," + moneybit +
							// "),t_sellcount=round(t_sellcount+" + dcount + ","
							// + countbit
							// + "),t_sellmoney=round(t_sellmoney+" + dtotal +
							// "," + moneybit + "),t_sellcost=round(t_sellcost+"
							// + dcost_money + "," + moneybit + ")"
							// +
							// ",newcostprice=round(if(count=0,newcostprice,money/count),"
							// + pricebit + ")";

							details = store + Common.getUpperUUIDString() + "','" + companyid + "','" + itemid + "','" + houseid + "'," + dcount + "," + dcost_money + ",round("
									+ (dcount > 0 ? dcost_money / dcount : 0) + "," + pricebit + "),'" + batchno + "','" + result.getString("remark") + "') on duplicate key update count=round(count+"
									+ dcount + "," + countbit + ")" + ",money=round(money+" + dcost_money + "," + moneybit + ") ,newcostprice=round(if(count=0,newcostprice,money/count)," + pricebit
									+ ")" + (result.getString("remark").equals("") ? "" : ",stockremark='" + result.getString("remark") + "'");

							ps.addBatch(details);

							details = itemmonth + Common.getUpperUUIDString() + "','" + companyid + "','" + itemid + "','" + houseid + "','" + sdate + "'," + syear + "," + smonth + "," + dcount + ","
									+ dcost_money + "," + dcount + "," + dtotal + "," + dcost_money + ",'" + batchno + "') on duplicate key update count=round(count+" + dcount + "," + countbit
									+ "),money=round(money+" + dcost_money + "," + moneybit + "),t_sellcount=round(t_sellcount+" + dcount + "," + countbit + "),t_sellmoney=round(t_sellmoney+"
									+ dtotal + "," + moneybit + "),t_sellcost=round(t_sellcost+" + dcost_money + "," + moneybit + ")";
							ps.addBatch(details);

							String temp = "('" + itemid + "','" + houseid + "')";
							if (condition.toString().equals("")) {
								condition.append(temp);
							} else {
								if (condition.toString().indexOf(temp) == -1) {
									condition.append("," + temp);
								}
							}
						}
						details = detail + orderid + "','" + (detailid.equals("") ? Common.getUpperUUIDString() : detailid) + "','" + maindata.getString("storeoutid") + "','"
								+ maindata.getString("originalbill") + "'," + result.getInteger("goods_number") + ",'" + companyid + "','" + maindata.getString("operate_by") + "','"
								+ maindata.getString("operate_time") + "','" + itemid + "','" + customerid + "','" + houseid + "'," + result.getDoubleValue("price") + "," + dcount + "," + dtotal
								+ "," + result.getDoubleValue("tax") + "," + result.getDoubleValue("taxrate") + "," + result.getDoubleValue("taxprice") + "," + result.getDoubleValue("taxmoney")
								+ ",'71','" + result.getString("remark") + "','" + status + "','" + (detailid.equals("") ? loginuserid : result.getString("create_id")) + "','"
								+ (detailid.equals("") ? loginUser : result.getString("create_by")) + "'," + (detailid.equals("") ? "now()" : "'" + result.getString("create_time") + "'") + ",'"
								+ loginuserid + "','" + loginUser + "',now(),'" + batchno + "'," + cost_price + "," + dcost_money + "," + dprofit + "," + dprofit_rate + ",'" + returndetailid + "','"
								+ returnorderid + "')";
						ps.addBatch(details);

						if (type > 0 && result.getDoubleValue("tax") != 0) {
							totaltaxmoney = Pdacommon.adddouble(totaltaxmoney, result.getDoubleValue("taxmoney"));
						}

						if (type > 0 && relationtype == 1) {

							ps.addBatch(" update stock set checkout_count=round(checkout_count+" + dcount + "," + countbit + ") where companyid='" + companyid + "' and itemid='" + itemid
									+ "' and houseid='" + houseid + "' and batchno='" + batchno + "'");
							if (!relationdetailid.equals("")) {
								ps.addBatch("update salesorderdetail set checkout_count=round(checkout_count+" + dcount + "," + countbit + ") where detailid='" + relationdetailid + "'");
							}
							ps.addBatch("update storeoutapplydetail set  notoutcount=round(notoutcount+" + dcount + "," + countbit + "),outcount=round(outcount-" + dcount + "," + countbit
									+ ") where detailid='" + applyrelationdetailid + "'");

						}

					}

				}
				if (type > 0 && !relationmainids.equals("")) {// 更改销售订单的出库状态，退货，如已完成出库更改为等待出库
					String[] farr = relationmainids.split(",");
					for (int j = 0; j < farr.length; j++) {
						ps.addBatch("update salesorder set stockstatus = '0' where salesorderid='" + farr[j] + "' and status='1' and (select 1 from salesorderdetail where salesorderid='" + farr[j]
								+ "' and schedulstatus='0' and status='1' limit 1) ");
					}
				}
			} else {
				save = false;
				message = message + "没有商品明细数据，保存失败";
			}

			if (save) {
				if (type > 0 && totalmoney != 0) {
					// 更新客户应收应付款
					String customersql = "update customer set receivable=round(receivable-" + totalmoney + "," + moneybit + ")"
							+ (totaltaxmoney != 0 ? ",T_receivable=round(T_receivable-" + totaltaxmoney + "," + moneybit + ")" : "") + "  where companyid='" + companyid + "' and customerid='"
							+ customerid + "'";
					ps.addBatch(customersql);

					// 增加往来单位月收支报表
					String customermonthsql = "insert into customermonth (monthid,companyid,customerid,sdate,syear,smonth,receivable,rec_sellin_money,rec_add_money"
							+ (totaltaxmoney != 0 ? ",T_receivable,T_rec_sellin_money,T_rec_add_money" : "")
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
							+ totalmoney
							+ ","
							+ totalmoney
							+ ",-"
							+ totalmoney
							+ (totaltaxmoney != 0 ? ",-" + totalmoney + "," + totalmoney + ",-" + totalmoney : "")
							+ ") on duplicate key update receivable=round(receivable-"
							+ totalmoney
							+ ","
							+ moneybit
							+ "),rec_sellin_money=round(rec_sellin_money+"
							+ totalmoney
							+ ","
							+ moneybit
							+ "),rec_add_money=round(rec_add_money-"
							+ totalmoney
							+ ","
							+ moneybit
							+ ")"
							+ (totaltaxmoney != 0 ? ",T_receivable=round(T_receivable-" + totaltaxmoney + "," + moneybit + "),T_rec_sellin_money=round(T_rec_sellin_money+" + totaltaxmoney + ","
							+ moneybit + "),T_rec_add_money=round(T_rec_add_money-" + totaltaxmoney + "," + moneybit + ")" : "");
					ps.addBatch(customermonthsql);

					// 增加往来单位年报表
					String customeryearsql = "insert into customeryear (yearid,companyid,customerid,syear,receivable,rec_sellin_money,rec_add_money"
							+ (totaltaxmoney != 0 ? ",T_receivable,T_rec_sellin_money,T_rec_add_money" : "")
							+ ") values('"
							+ Common.getUpperUUIDString()
							+ "','"
							+ companyid
							+ "','"
							+ customerid
							+ "',"
							+ syear
							+ ",-"
							+ totalmoney
							+ ","
							+ totalmoney
							+ ",-"
							+ totalmoney
							+ (totaltaxmoney != 0 ? ",-" + totalmoney + "," + totalmoney + ",-" + totalmoney : "")
							+ ") on duplicate key update receivable=round(receivable-"
							+ totalmoney
							+ ","
							+ moneybit
							+ ")"
							+ ",rec_sellin_money=round(rec_sellin_money+"
							+ totalmoney
							+ ","
							+ moneybit
							+ "),rec_add_money=round(rec_add_money-"
							+ totalmoney
							+ ","
							+ moneybit
							+ ")"
							+ (totaltaxmoney != 0 ? ",T_receivable=round(T_receivable-" + totaltaxmoney + "," + moneybit + "),T_rec_sellin_money=round(T_rec_sellin_money+" + totaltaxmoney + ","
							+ moneybit + "),T_rec_add_money=round(T_rec_add_money-" + totaltaxmoney + "," + moneybit + ")" : "");
					ps.addBatch(customeryearsql);
				}
				int changebilltype = Pdacommon.getDatalogBillChangefunc("storeout", "7");
				if (operate.equals("draftedit")) {// 草稿编辑 更新
					ps.addBatch("update storeout set orderid='" + orderid + "', originalbill='" + maindata.getString("originalbill") + "',operate_time='" + operate_time + "',operate_by='"
							+ maindata.getString("operate_by") + "',houseid='" + houseid + "',customerid='" + customerid + "',count=" + count + ",total=" + total + ",totaltax=" + totaltax
							+ ",totalmoney=" + totalmoney + ",remark='" + maindata.getString("remark") + "',status='" + status + "',update_id='" + loginuserid + "',update_by='" + loginUser
							+ "',update_time=now(),iproperty='" + maindata.getString("iproperty") + "',cost_money=" + cost_money + ",profit=" + tprofit + ",profit_rate=round("
							+ (tprofit > 0 ? (tprofit / total) : 0) + ",4) where storeoutid='" + maindata.getString("storeoutid") + "'");

					ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
							+ companyid + "'," + changebilltype + ",'修改','" + maindata.getString("storeoutid") + "','单据编号：" + orderid + "','" + loginuserid + "','" + loginUser + "',now())");

					ps.addBatch("update storeoutdetail d,storeout s set d.create_id=s.create_id,d.create_by=s.create_by,d.create_time=s.create_time where d.storeoutid = s.storeoutid and s.storeoutid='"
							+ maindata.getString("storeoutid") + "'");

				} else {
					String main = "insert into storeout (orderid,storeoutid,bill_type,originalbill,companyid,operate_time,operate_by,houseid,customerid,count,total,totaltax,totalmoney,remark,status,printing,outexcel,create_id,create_by,create_time,update_id,update_by,update_time,iproperty,cost_money,profit,profit_rate) VALUES ('"
							+ orderid
							+ "','"
							+ maindata.getString("storeoutid")
							+ "','"
							+ maindata.getString("bill_type")
							+ "','"
							+ maindata.getString("originalbill")
							+ "','"
							+ companyid
							+ "','"
							+ operate_time
							+ "','"
							+ maindata.getString("operate_by")
							+ "','"
							+ houseid
							+ "','"
							+ customerid
							+ "',"
							+ count
							+ ","
							+ total
							+ ","
							+ totaltax
							+ ","
							+ totalmoney
							+ ",'"
							+ maindata.getString("remark")
							+ "','"
							+ status
							+ "',0,0,'"
							+ loginuserid
							+ "','"
							+ loginUser
							+ "',now(),'"
							+ loginuserid
							+ "','"
							+ loginUser
							+ "',now(),'"
							+ maindata.getString("iproperty") + "'," + cost_money + "," + tprofit + ",round(" + (tprofit > 0 && total > 0 ? (tprofit / total) : 0) + ",4))";
					ps.addBatch(main);

					ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
							+ companyid + "'," + changebilltype + ",'" + (status == 0 ? "新增暂存" : "新增保存") + "','" + maindata.getString("storeoutid") + "','单据编号：" + orderid + "','" + loginuserid
							+ "','" + loginUser + "',now())");

				}
				ps.executeBatch();
				conn.commit();
				conn.setAutoCommit(true);
				state = "1";

				if (type > 0) {
					// 库存报警
					Bhouselimit = Common.gethouselimitdata(conn, companyid, condition, Bhouselimit);
				}
			}
		} catch (Exception e) {
			// e.printStackTrace();
			try {
				conn.rollback();
			} catch (Exception e1) {
				// e1.printStackTrace();
			}
			message = "保存失败!!";// + e.getMessage().toString();
		} finally {
			condition.setLength(0);
			conn.close();
		}
		rt.put("state", state);
		rt.put("message", message);
		rt.put("warning", Bhouselimit);
		return rt;
	}

	// 保存调拨单信息
	public static JSONObject saveStoreMoveFunction(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject rt = new JSONObject();
		JSONObject maindata = JSONObject.parseObject(params.getString("maindata"));
		Integer type = params.getInteger("type");
		Integer pricebit = params.getInteger("pricebit");
		Integer moneybit = params.getInteger("moneybit");
		String loginUser = params.getString("loginUser");
		String loginuserid = params.getString("loginuserid");
		JSONArray detaildata = params.getJSONArray("detaildata");
		String operate = params.getString("operate");// draftedit 编辑单据
		String state = "0";

		String companyid = maindata.getString("companyid");
		String houseid = maindata.getString("oldhouseid");
		String newhouseid = maindata.getString("newhouseid");
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

		try {
			int countbit = 0;
			int t6days = 0;

			Table companytalbe = DataUtils.queryData(conn, "select pricebit,countbit,moneybit,t6days from s_company_config where company_id='" + companyid + "'", null, null, null, null);
			if (companytalbe.getRows().size() > 0) {
				countbit = companytalbe.getRows().get(0).getInteger("countbit");
				t6days = companytalbe.getRows().get(0).getInteger("t6days");
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
			conn.setAutoCommit(false);
			String orderid = operate.equals("draftedit") ? maindata.getString("orderid") : getOrderidByparams(companyid, "storemove", "", billdate, conn);
			String detail = "insert into storemovedetail (orderid,detailid,storemoveid,originalbill,goods_number,companyid,operate_by,operate_time,itemid,oldhouseid,newhouseid,price,count,total,oldtype,newtype,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,batchno) VALUES ('";
			String store = "update stock set ";
			String newstore = "insert into stock (stockid,companyid,itemid,houseid,count,money,newcostprice,batchno,stockremark) VALUES ('";
			String itemmonth = "insert into itemmonth (monthid,companyid,itemid,houseid,sdate,syear,smonth,count,money,out_count,out_money,batchno) VALUES ('";
			String newitemmonth = "insert into itemmonth (monthid,companyid,itemid,houseid,sdate,syear,smonth,count,money,in_count,in_money,batchno) VALUES ('";

			String details = "";
			if (save && operate.equals("draftedit")) {// 草稿编辑 删除明细数据重新增加
				String fsql = "select status from storemove where storemoveid='" + maindata.getString("storemoveid") + "'";
				Object cobject = DataUtils.getValueBySQL(conn, fsql, null);
				if (cobject == null) {
					save = false;
					message = "当前记录已删除，操作失败。";
					state = "2";
				} else {
					String fstatus = cobject.toString();
					if (fstatus.equals("0")) {
						ps.addBatch("delete from storemovedetail where storemoveid='" + maindata.getString("storemoveid") + "'");
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
					for (i = 0; i < detaildata.size(); i++) {
						JSONObject result = JSONObject.parseObject(detaildata.getString(i));
						String detailid = result.getString("detailid");
						String batchno = result.getString("batchno");
						String itemid = result.getString("itemid");
						double dcount = result.getDoubleValue("count");

						String sql = "select count,money,round(count-checkout_count," + countbit + ") as scount from stock where companyid='" + companyid + "' and itemid='" + itemid
								+ "' and houseid='" + houseid + "' " + " and batchno='" + batchno + "'";

						Table table = DataUtils.queryData(conn, sql, null, null, null, null);
						if (table.getRows().size() > 0) {
							double scount = Double.parseDouble(table.getRows().get(0).getValue("count").toString());
							double dscount = Double.parseDouble(table.getRows().get(0).getValue("scount").toString());

							if (dscount < dcount) {
								message = message + (message.equals("") ? "" : "；") + "第" + result.getInteger("goods_number") + "行的商品《" + result.getString("itemname") + " " + batchno + "》最新可用库存为"
										+ dscount;
								save = false;
							} else if (save) {
								double smoney = Double.parseDouble(table.getRows().get(0).getValue("money").toString());
								double cost_price = (scount == 0 ? 0 : Pdacommon.formatDoubleUp(smoney / scount, pricebit));
								double dtotal = (dcount == scount ? Pdacommon.formatDoubleUp(smoney, moneybit) : Pdacommon.formatDoubleUp(dcount * cost_price, moneybit));

								total = Pdacommon.adddouble(total, dtotal);// 计算总的成本金额

								details = detail + orderid + "','" + (detailid.equals("") ? Common.getUpperUUIDString() : detailid) + "','" + maindata.getString("storemoveid") + "','"
										+ maindata.getString("originalbill") + "'," + result.getInteger("goods_number") + ",'" + companyid + "','" + maindata.getString("operate_by") + "','"
										+ maindata.getString("operate_time") + "','" + itemid + "','" + houseid + "','" + newhouseid + "'," + cost_price + "," + dcount + "," + dtotal + ",'41','42','"
										+ result.getString("remark") + "','" + status + "','" + (detailid.equals("") ? loginuserid : result.getString("create_id")) + "','"
										+ (detailid.equals("") ? loginUser : result.getString("create_by")) + "'," + (detailid.equals("") ? "now()" : "'" + result.getString("create_time") + "'")
										+ ",'" + loginuserid + "','" + loginUser + "',now(),'" + batchno + "')";
								ps.addBatch(details); // 明细记录

								if (type > 0) {
									details = store + "count=round(count-" + dcount + "," + countbit + "),money=round(money-" + dtotal + "," + moneybit
											+ ") ,newcostprice=round(if(count=0,newcostprice,money/count)," + pricebit + ") where companyid='" + companyid + "' and itemid='" + itemid
											+ "' and houseid='" + houseid + "' and batchno='" + batchno + "'";

									ps.addBatch(details);// 调出变更

									ps.addBatch("delete from stock where itemid='" + itemid + "' and houseid='" + houseid + "' and batchno='" + batchno + "' and count=0 and money=0 ");

									details = newstore + Common.getUpperUUIDString() + "','" + companyid + "','" + itemid + "','" + newhouseid + "'," + dcount + "," + dtotal + "," + cost_price + ",'"
											+ batchno + "','" + result.getString("remark") + "') on duplicate key update count=round(count+" + dcount + "," + countbit + "),money=round(money+"
											+ dtotal + "," + moneybit + ") ,newcostprice=round(if(count=0,newcostprice,money/count)," + pricebit + ")"
											+ (result.getString("remark").equals("") ? "" : ",stockremark='" + result.getString("remark") + "'");

									ps.addBatch(details); // 调入变更

									details = itemmonth + Common.getUpperUUIDString() + "','" + companyid + "','" + itemid + "','" + houseid + "','" + sdate + "'," + syear + "," + smonth + ",-"
											+ dcount + ",-" + dtotal + "," + dcount + "," + dtotal + ",'" + batchno + "') on duplicate key update count=round(count-" + dcount + "," + countbit
											+ "),money=round(money-" + dtotal + "," + moneybit + "),out_count=round(out_count+" + dcount + "," + countbit + "),out_money=round(out_money+" + dtotal
											+ "," + moneybit + ")";
									ps.addBatch(details); // 调出变更

									details = newitemmonth + Common.getUpperUUIDString() + "','" + companyid + "','" + itemid + "','" + newhouseid + "','" + sdate + "'," + syear + "," + smonth + ","
											+ dcount + "," + dtotal + "," + dcount + "," + dtotal + ",'" + batchno + "') on duplicate key update count=round(count+" + dcount + "," + countbit
											+ "),money=round(money+" + dtotal + "," + moneybit + "),in_count=round(in_count+" + dcount + "," + countbit + "),in_money=round(in_money+" + dtotal + ","
											+ moneybit + ")";
									ps.addBatch(details); // 调入变更

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
						} else {
							message = message + (message.equals("") ? "" : "；") + "第" + result.getInteger("goods_number") + "行的商品《" + result.getString("itemname") + " " + result.getString("batchno")
									+ "》最新可用库存为0";
							save = false;
						}
					}
					if (!message.equals(""))
						message = message + "这些商品库存不足，保存失败。";
				}
			} else {
				save = false;
				message = message + "没有商品明细数据，保存失败";
			}
			if (save) {
				int changebilltype = Pdacommon.getDatalogBillChangefunc("storemove", "");
				if (operate.equals("draftedit")) {// 草稿编辑 更新
					ps.addBatch("update storemove set orderid='" + orderid + "', originalbill='" + maindata.getString("originalbill") + "',operate_time='" + operate_time + "',operate_by='"
							+ maindata.getString("operate_by") + "',oldhouseid='" + houseid + "',newhouseid='" + newhouseid + "',count=" + count + ",total=" + total + ",remark='"
							+ maindata.getString("remark") + "',status='" + status + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now(),iproperty='"
							+ maindata.getString("iproperty") + "' where storemoveid='" + maindata.getString("storemoveid") + "'");

					ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
							+ companyid + "'," + changebilltype + ",'修改','" + maindata.getString("storemoveid") + "','单据编号：" + orderid + "','" + loginuserid + "','" + loginUser + "',now())");

					ps.addBatch("update storemovedetail d,storemove s set d.create_id=s.create_id,d.create_by=s.create_by,d.create_time=s.create_time where d.storemoveid = s.storemoveid and s.storemoveid='"
							+ maindata.getString("storemoveid") + "'");

				} else {
					String main = "insert into storemove (orderid,storemoveid,bill_type,originalbill,companyid,operate_time,operate_by,oldhouseid,newhouseid,count,total,remark,status,printing,outexcel,create_id,create_by,create_time,update_id,update_by,update_time,iproperty) VALUES ('"
							+ orderid
							+ "','"
							+ maindata.getString("storemoveid")
							+ "','"
							+ maindata.getString("bill_type")
							+ "','"
							+ maindata.getString("originalbill")
							+ "','"
							+ companyid
							+ "','"
							+ operate_time
							+ "','"
							+ maindata.getString("operate_by")
							+ "','"
							+ houseid
							+ "','"
							+ newhouseid
							+ "',"
							+ count
							+ ","
							+ total
							+ ",'"
							+ maindata.getString("remark")
							+ "','"
							+ status + "',0,0,'" + loginuserid + "','" + loginUser + "',now(),'" + loginuserid + "','" + loginUser + "',now(),'" + maindata.getString("iproperty") + "')";
					ps.addBatch(main); // 主记录

					ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
							+ companyid + "'," + changebilltype + ",'" + (status == 0 ? "新增暂存" : "新增保存") + "','" + maindata.getString("storemoveid") + "','单据编号：" + orderid + "','" + loginuserid
							+ "','" + loginUser + "',now())");

				}
				ps.executeBatch();
				conn.commit();
				conn.setAutoCommit(true);
				state = "1";

				if (type > 0) {
					// 库存报警
					Bhouselimit = Common.gethouselimitdata(conn, companyid, condition, Bhouselimit);
				}
			}

		} catch (Exception e) {
			// e.printStackTrace();
			try {
				conn.rollback();
			} catch (Exception e1) {
				// e1.printStackTrace();
			}
			message = "保存失败!!";// + e.getMessage().toString();
		} finally {
			condition.setLength(0);
			conn.close();
		}
		rt.put("state", state);
		rt.put("message", message);
		rt.put("warning", Bhouselimit);
		return rt;
	}

	// 保存盘点单信息
	public static JSONObject saveStoreCheckFunction(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject rt = new JSONObject();
		JSONObject maindata = JSONObject.parseObject(params.getString("maindata"));
		Integer type = params.getInteger("type");
		Integer pricebit = params.getInteger("pricebit");
		Integer moneybit = params.getInteger("moneybit");
		String loginUser = params.getString("loginUser");
		String loginuserid = params.getString("loginuserid");
		JSONArray detaildata = params.getJSONArray("detaildata");
		String operate = params.getString("operate");// draftedit 编辑单据
		String state = "0";

		String companyid = maindata.getString("companyid");
		String houseid = maindata.getString("houseid");
		String operate_time = maindata.getString("operate_time");
		double count = 0;
		double loss_count = 0;
		double total_profit = 0;
		double total_loss = 0;

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

		try {
			int countbit = 0;
			int t6days = 0;

			Table companytalbe = DataUtils.queryData(conn, "select pricebit,countbit,moneybit,t6days from s_company_config where company_id='" + companyid + "'", null, null, null, null);
			if (companytalbe.getRows().size() > 0) {
				countbit = companytalbe.getRows().get(0).getInteger("countbit");
				t6days = companytalbe.getRows().get(0).getInteger("t6days");
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
			conn.setAutoCommit(false);
			String orderid = operate.equals("draftedit") ? maindata.getString("orderid") : getOrderidByparams(companyid, "storecheck", "", billdate, conn);
			String detail = "insert into storecheckdetail (orderid,detailid,storecheckid,originalbill,goods_number,companyid,operate_by,operate_time,itemid,houseid,oldcount,newcount,price,count,total_profit,loss_count,total_loss,stype,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,batchno) VALUES ('";
			String store = "insert into stock (stockid,companyid,itemid,houseid,count,money,newcostprice,batchno,stockremark) VALUES ('";
			String itemmonth = "insert into itemmonth (monthid,companyid,itemid,houseid,sdate,syear,smonth,count,money,profitcount,profitmoney,losscount,lossmoney,batchno) VALUES ('";

			String details = "";
			if (save && operate.equals("draftedit")) {// 草稿编辑 删除明细数据重新增加
				String fsql = "select status from storecheck where storecheckid='" + maindata.getString("storecheckid") + "'";
				Object cobject = DataUtils.getValueBySQL(conn, fsql, null);
				if (cobject == null) {
					save = false;
					message = "当前记录已删除，操作失败。";
					state = "2";
				} else {
					String fstatus = cobject.toString();
					if (fstatus.equals("0")) {
						ps.addBatch("delete from storecheckdetail where storecheckid='" + maindata.getString("storecheckid") + "'");
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
					for (i = 0; i < detaildata.size(); i++) {
						JSONObject result = JSONObject.parseObject(detaildata.getString(i));
						String detailid = result.getString("detailid");
						String batchno = result.getString("batchno");
						String itemid = result.getString("itemid");

						double newcount = result.getDoubleValue("newcount");
						double inprice = result.getDoubleValue("inprice");
						double cost_price = 0;
						double oldcount = 0;
						double cost_money = 0;

						String sqlcost = "select count,newcostprice,money from stock where companyid='" + companyid + "' and itemid='" + itemid + "' and houseid='" + houseid + "' " + " and batchno='"
								+ batchno + "'";

						Table table = DataUtils.queryData(conn, sqlcost, null, null, null, null);
						if (table.getRows().size() > 0) {// 当前仓库有记录
							cost_price = Double.parseDouble(table.getRows().get(0).getValue("newcostprice").toString());
							oldcount = Double.parseDouble(table.getRows().get(0).getValue("count").toString());
							cost_money = Double.parseDouble(table.getRows().get(0).getValue("money").toString());
						} else {// 当前仓库无记录
							sqlcost = "select count(newcostprice) as recordcount,ifnull(avg(newcostprice),0) as newcostprice from stock where companyid='" + companyid + "' and itemid='" + itemid
									+ "' and batchno='" + batchno + "'";
							table = DataUtils.queryData(conn, sqlcost, null, null, null, null);
							if (table.getRows().size() > 0) {
								if (Integer.parseInt(table.getRows().get(0).getValue("recordcount").toString()) == 0) {// 没记录以进货价为单价
									cost_price = inprice;
								} else {// 有记录按平均单价核算
									cost_price = Double.parseDouble(table.getRows().get(0).getValue("newcostprice").toString());
								}

							}
						}

						String stype = "";// 无盈亏空值
						double dcount = 0;
						double dloss_count = 0;
						if (oldcount < newcount) {
							dcount = Pdacommon.subtractdouble(newcount, oldcount);
							stype = "51";// 库存盘点-入库(盘盈)
						} else if (oldcount > newcount) {
							dloss_count = Pdacommon.subtractdouble(oldcount, newcount);
							stype = "52";// 库存盘点-出库(盘亏)
						} else {
							stype = "53";// 库存盘点-入库(盘盈)
						}

						double dtotal_profit = Pdacommon.formatDoubleUp(dcount * cost_price, moneybit);
						double dtotal_loss = Pdacommon.formatDoubleUp(dloss_count * cost_price, moneybit);

						if (oldcount == 0 && newcount == 0) {
							dtotal_profit = 0;
							dtotal_loss = 0;
							if (cost_money > 0) {
								dtotal_loss = cost_money;
								stype = "52";// 库存盘点-出库(盘亏)
							} else if (cost_money < 0) {
								dtotal_profit = -cost_money;
								stype = "51";// 库存盘点-出库(盘亏)
							}
						}
						if (oldcount != 0 && newcount == 0) {
							if (dcount > 0) {
								dtotal_profit = -cost_money;
							}
							if (dtotal_loss > 0) {
								dtotal_loss = cost_money;
							}
						}

						count = Pdacommon.adddouble(count, dcount);
						loss_count = Pdacommon.adddouble(loss_count, dloss_count);
						total_profit = Pdacommon.adddouble(total_profit, dtotal_profit);
						total_loss = Pdacommon.adddouble(total_loss, dtotal_loss);

						details = detail + orderid + "','" + (detailid.equals("") ? Common.getUpperUUIDString() : detailid) + "','" + maindata.getString("storecheckid") + "','"
								+ maindata.getString("originalbill") + "'," + result.getInteger("goods_number") + ",'" + companyid + "','" + maindata.getString("operate_by") + "','"
								+ maindata.getString("operate_time") + "','" + itemid + "','" + houseid + "'," + oldcount + "," + newcount + "," + cost_price + "," + dcount + "," + dtotal_profit
								+ "," + dloss_count + "," + dtotal_loss + ",'" + stype + "','" + result.getString("remark") + "','" + status + "','"
								+ (detailid.equals("") ? loginuserid : result.getString("create_id")) + "','" + (detailid.equals("") ? loginUser : result.getString("create_by")) + "',"
								+ (detailid.equals("") ? "now()" : "'" + result.getString("create_time") + "'") + ",'" + loginuserid + "','" + loginUser + "',now(),'" + batchno + "')";
						ps.addBatch(details); // 明细记录

						if (type > 0) {

							if (stype.equals("51")) {// 库存盘点-入库(盘盈)

								details = store + Common.getUpperUUIDString() + "','" + companyid + "','" + itemid + "','" + houseid + "'," + dcount + "," + dtotal_profit + "," + cost_price + ",'"
										+ batchno + "','" + result.getString("remark") + "') on duplicate key update count=round(count+" + dcount + "," + countbit + "),money=round(money+"
										+ dtotal_profit + "," + moneybit + ") ,newcostprice=round(if(count=0,newcostprice,money/count)," + pricebit + ")"
										+ (result.getString("remark").equals("") ? "" : ",stockremark='" + result.getString("remark") + "'");

								ps.addBatch(details); // 调入变更

								ps.addBatch("delete from stock where itemid='" + itemid + "' and houseid='" + houseid + "' and batchno='" + batchno + "' and count=0 and money=0 ");

								details = itemmonth + Common.getUpperUUIDString() + "','" + companyid + "','" + itemid + "','" + houseid + "','" + sdate + "'," + syear + "," + smonth + "," + dcount
										+ "," + dtotal_profit + "," + dcount + "," + dtotal_profit + ",0,0,'" + batchno + "') on duplicate key update count=round(count+" + dcount + "," + countbit
										+ "),money=round(money+" + dtotal_profit + "," + moneybit + "),profitcount=round(profitcount+" + dcount + "," + countbit + "),profitmoney=round(profitmoney+"
										+ dtotal_profit + "," + moneybit + ")";
								ps.addBatch(details); // 调入变更
							} else if (stype.equals("52")) {// 库存盘点-出库(盘亏)

								// details =
								// "update stock set count=round(count-" +
								// dloss_count + "," + countbit +
								// "),money=round(money-" + dtotal_loss + "," +
								// moneybit
								// +
								// "),newcostprice=round(if(count=0,newcostprice,money/count),"
								// + pricebit + ") where companyid='" +
								// companyid + "' and itemid='" + itemid +
								// "' and houseid='"
								// + houseid + "' and batchno='" + batchno +
								// "'";

								details = store + Common.getUpperUUIDString() + "','" + companyid + "','" + itemid + "','" + houseid + "',-" + dloss_count + ",-" + dtotal_loss + "," + cost_price
										+ ",'" + batchno + "','" + result.getString("remark") + "') on duplicate key update count=round(count-" + dloss_count + "," + countbit + "),money=round(money-"
										+ dtotal_loss + "," + moneybit + ") ,newcostprice=round(if(count=0,newcostprice,money/count)," + pricebit + ")"
										+ (result.getString("remark").equals("") ? "" : ",stockremark='" + result.getString("remark") + "'");

								ps.addBatch(details); // 调出变更

								ps.addBatch("delete from stock where itemid='" + itemid + "' and houseid='" + houseid + "' and batchno='" + batchno + "' and count=0 and money=0 ");

								details = itemmonth + Common.getUpperUUIDString() + "','" + companyid + "','" + itemid + "','" + houseid + "','" + sdate + "'," + syear + "," + smonth + ",-"
										+ dloss_count + ",-" + dtotal_loss + ",0,0," + dloss_count + "," + dtotal_loss + ",'" + batchno + "') on duplicate key update count=round(count-" + dloss_count
										+ "," + countbit + "),money=round(money-" + dtotal_loss + "," + moneybit + "),losscount=round(losscount+" + dloss_count + "," + countbit
										+ "),lossmoney=round(lossmoney+" + dtotal_loss + "," + moneybit + ")";
								ps.addBatch(details); // 调出变更
							}

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
				}

			} else {
				save = false;
				message = message + "没有商品明细数据，保存失败";
			}
			if (save) {
				int changebilltype = Pdacommon.getDatalogBillChangefunc("storecheck", "");
				if (operate.equals("draftedit")) {// 草稿编辑 更新
					ps.addBatch("update storecheck set orderid='" + orderid + "', originalbill='" + maindata.getString("originalbill") + "',operate_time='" + operate_time + "',operate_by='"
							+ maindata.getString("operate_by") + "',houseid='" + houseid + "',stype='" + ((count > 0 || loss_count > 0 || total_profit > 0 || total_loss > 0) ? "1" : "0") + "',count="
							+ count + ",loss_count=" + loss_count + ",total_profit=" + total_profit + ",total_loss=" + total_loss + ",remark='" + maindata.getString("remark") + "',status='" + status
							+ "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now(),iproperty='" + maindata.getString("iproperty") + "' where storecheckid='"
							+ maindata.getString("storecheckid") + "'");

					ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
							+ companyid + "'," + changebilltype + ",'修改','" + maindata.getString("storecheckid") + "','单据编号：" + orderid + "','" + loginuserid + "','" + loginUser + "',now())");

					// 2020-12-1 原这个句用storemove写错了,更新为盘点的
					ps.addBatch("update storecheckdetail d,storecheck s set d.create_id=s.create_id,d.create_by=s.create_by,d.create_time=s.create_time where d.storecheckid = s.storecheckid and s.storecheckid='"
							+ maindata.getString("storecheckid") + "'");

				} else {
					String main = "insert into storecheck (orderid,storecheckid,bill_type,originalbill,companyid,operate_time,operate_by,houseid,stype,count,loss_count,total_profit,total_loss,remark,status,printing,outexcel,create_id,create_by,create_time,update_id,update_by,update_time,iproperty) VALUES ('"
							+ orderid
							+ "','"
							+ maindata.getString("storecheckid")
							+ "','"
							+ maindata.getString("bill_type")
							+ "','"
							+ maindata.getString("originalbill")
							+ "','"
							+ companyid
							+ "','"
							+ operate_time
							+ "','"
							+ maindata.getString("operate_by")
							+ "','"
							+ houseid
							+ "','"
							+ ((count > 0 || loss_count > 0 || total_profit > 0 || total_loss > 0) ? "1" : "0")// 0无盈亏
							// 1有盈亏
							+ "',"
							+ count
							+ ","
							+ loss_count
							+ ","
							+ total_profit
							+ ","
							+ total_loss
							+ ",'"
							+ maindata.getString("remark")
							+ "','"
							+ status
							+ "',0,0,'"
							+ loginuserid
							+ "','"
							+ loginUser + "',now(),'" + loginuserid + "','" + loginUser + "',now(),'" + maindata.getString("iproperty") + "')";
					ps.addBatch(main); // 主记录

					ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
							+ companyid + "'," + changebilltype + ",'" + (status == 0 ? "新增暂存" : "新增保存") + "','" + maindata.getString("storecheckid") + "','单据编号：" + orderid + "','" + loginuserid
							+ "','" + loginUser + "',now())");

				}
				ps.executeBatch();
				conn.commit();
				conn.setAutoCommit(true);
				state = "1";

				if (type > 0) {
					// 库存报警
					Bhouselimit = Common.gethouselimitdata(conn, companyid, condition, Bhouselimit);
				}
			}

		} catch (Exception e) {
			// e.printStackTrace();
			try {
				conn.rollback();
			} catch (Exception e1) {
				// e1.printStackTrace();
			}
			message = "保存失败!!";// + e.getMessage().toString();
		} finally {
			condition.setLength(0);
			conn.close();
		}
		rt.put("state", state);
		rt.put("message", message);
		rt.put("warning", Bhouselimit);
		return rt;
	}

	// 保存组装拆卸单信息
	public static JSONObject saveSplitsFunction(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject rt = new JSONObject();
		JSONObject maindata = JSONObject.parseObject(params.getString("maindata"));
		Integer type = params.getInteger("type");
		Integer pricebit = params.getInteger("pricebit");
		String loginUser = params.getString("loginUser");
		String loginuserid = params.getString("loginuserid");
		JSONArray detaildata = params.getJSONArray("detaildata");
		String operate = params.getString("operate");// draftedit 编辑单据
		String state = "0";

		String companyid = maindata.getString("companyid");
		// String houseid = maindata.getString("oldhouseid");
		// String newhouseid = maindata.getString("newhouseid");
		String operate_time = maindata.getString("operate_time");

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

		try {
			int countbit = 0;
			int moneybit = 2;
			int t6days = 0;

			Table companytalbe = DataUtils.queryData(conn, "select pricebit,countbit,moneybit,t6days from s_company_config where company_id='" + companyid + "'", null, null, null, null);
			if (companytalbe.getRows().size() > 0) {
				countbit = companytalbe.getRows().get(0).getInteger("countbit");
				moneybit = companytalbe.getRows().get(0).getInteger("moneybit");
				t6days = companytalbe.getRows().get(0).getInteger("t6days");
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
			conn.setAutoCommit(false);
			String orderid = operate.equals("draftedit") ? maindata.getString("orderid") : getOrderidByparams(companyid, "splits", "", billdate, conn);
			String detail = "insert into splitsdetail (orderid,detailid,splitsid,originalbill,goods_number,companyid,operate_by,operate_time,itemid,houseid,price,count,total,stype,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,batchno) VALUES ('";
			String store = "update stock set ";
			String newstore = "insert into stock (stockid,companyid,itemid,houseid,count,money,newcostprice,batchno,stockremark) VALUES ('";
			String itemmonth = "insert into itemmonth (monthid,companyid,itemid,houseid,sdate,syear,smonth,count,money,splitsout_count,splitsout_money,batchno) VALUES ('";
			String newitemmonth = "insert into itemmonth (monthid,companyid,itemid,houseid,sdate,syear,smonth,count,money,splitsin_count,splitsin_money,batchno) VALUES ('";

			String details = "";
			if (save && operate.equals("draftedit")) {// 草稿编辑 删除明细数据重新增加
				String fsql = "select status from splits where splitsid='" + maindata.getString("splitsid") + "'";
				Object cobject = DataUtils.getValueBySQL(conn, fsql, null);
				if (cobject == null) {
					save = false;
					message = "当前记录已删除，操作失败。";
					state = "2";
				} else {
					String fstatus = cobject.toString();
					if (fstatus.equals("0")) {
						ps.addBatch("delete from splitsdetail where splitsid='" + maindata.getString("splitsid") + "'");
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
					for (i = 0; i < detaildata.size(); i++) {
						JSONObject result = JSONObject.parseObject(detaildata.getString(i));
						String detailid = result.getString("detailid");
						String batchno = result.getString("batchno");
						String itemid = result.getString("itemid");
						String stype = result.getString("stype");
						String houseid = result.getString("houseid");
						double cost_price = result.getDoubleValue("price");
						double dcount = result.getDoubleValue("count");
						double dtotal = result.getDoubleValue("total");

						if (stype.equals("31")) {// 组装拆卸出库

							String sql = "select count,round(count-checkout_count," + countbit + ") as scount from stock where companyid='" + companyid + "' and itemid='" + itemid + "' and houseid='"
									+ houseid + "' " + " and batchno='" + batchno + "'";
							// System.out.println(sql);

							Table table = DataUtils.queryData(conn, sql, null, null, null, null);
							// System.out.println(table.getRows().size());
							if (table.getRows().size() > 0) {
								// double scount =
								// Double.parseDouble(table.getRows().get(0).getValue("count").toString());
								double dscount = Double.parseDouble(table.getRows().get(0).getValue("scount").toString());

								if (dscount < dcount) {
									message = message + (message.equals("") ? "" : "；") + "第" + result.getInteger("goods_number") + "行的商品《" + result.getString("itemname") + " " + batchno + "》最新可用库存为"
											+ dscount;
									save = false;
								} else if (save) {
									details = detail + orderid + "','" + (detailid.equals("") ? Common.getUpperUUIDString() : detailid) + "','" + maindata.getString("splitsid") + "','"
											+ maindata.getString("originalbill") + "'," + result.getInteger("goods_number") + ",'" + companyid + "','" + maindata.getString("operate_by") + "','"
											+ maindata.getString("operate_time") + "','" + itemid + "','" + houseid + "'," + cost_price + "," + dcount + "," + dtotal + ",'" + stype + "','"
											+ result.getString("remark") + "','" + status + "','" + (detailid.equals("") ? loginuserid : result.getString("create_id")) + "','"
											+ (detailid.equals("") ? loginUser : result.getString("create_by")) + "'," + (detailid.equals("") ? "now()" : "'" + result.getString("create_time") + "'")
											+ ",'" + loginuserid + "','" + loginUser + "',now(),'" + batchno + "')";
									ps.addBatch(details); // 明细记录
									if (type > 0) {
										details = store + "count=round(count-" + dcount + "," + countbit + "),money=round(money-" + dtotal + "," + moneybit
												+ ") ,newcostprice=round(if(count=0,newcostprice,money/count)," + pricebit + ") where companyid='" + companyid + "' and itemid='" + itemid
												+ "' and houseid='" + houseid + "' and batchno='" + batchno + "'";

										ps.addBatch(details);// 调出变更

										ps.addBatch("delete from stock where itemid='" + itemid + "' and houseid='" + houseid + "' and batchno='" + batchno + "' and count=0 and money=0 ");

										details = itemmonth + Common.getUpperUUIDString() + "','" + companyid + "','" + itemid + "','" + houseid + "','" + sdate + "'," + syear + "," + smonth + ",-"
												+ dcount + ",-" + dtotal + "," + dcount + "," + dtotal + ",'" + batchno + "') on duplicate key update count=round(count-" + dcount + "," + countbit
												+ "),money=round(money-" + dtotal + "," + moneybit + ")" + ",splitsout_count=round(splitsout_count+" + dcount + "," + countbit
												+ "),splitsout_money=round(splitsout_money+" + dtotal + "," + moneybit + ")";
										ps.addBatch(details); // 调出变更

									}
								}
							} else {
								message = message + (message.equals("") ? "" : "；") + "第" + result.getInteger("goods_number") + "行的商品《" + result.getString("itemname") + " "
										+ result.getString("batchno") + "》最新可用库存为0";
								save = false;
							}
						} else if (stype.equals("32")) {// 组装拆卸入库
							details = detail + orderid + "','" + Common.getUpperUUIDString() + "','" + maindata.getString("splitsid") + "','" + maindata.getString("originalbill") + "',"
									+ result.getInteger("goods_number") + ",'" + companyid + "','" + maindata.getString("operate_by") + "','" + maindata.getString("operate_time") + "','" + itemid
									+ "','" + houseid + "'," + cost_price + "," + dcount + "," + dtotal + ",'" + stype + "','" + result.getString("remark") + "','" + status + "','" + loginuserid
									+ "','" + loginUser + "',now(),'" + loginuserid + "','" + loginUser + "',now(),'" + batchno + "')";
							ps.addBatch(details); // 明细记录
							if (type > 0) {

								details = newstore + Common.getUpperUUIDString() + "','" + companyid + "','" + itemid + "','" + houseid + "'," + dcount + "," + dtotal + "," + cost_price + ",'"
										+ batchno + "','" + result.getString("remark") + "') on duplicate key update count=round(count+" + dcount + "," + countbit + "),money=round(money+" + dtotal
										+ "," + moneybit + "),newcostprice=round(if(count=0,newcostprice,money/count)," + pricebit + ")"
										+ (result.getString("remark").equals("") ? "" : ",stockremark='" + result.getString("remark") + "'");

								ps.addBatch(details); // 调入变更

								details = newitemmonth + Common.getUpperUUIDString() + "','" + companyid + "','" + itemid + "','" + houseid + "','" + sdate + "'," + syear + "," + smonth + ","
										+ dcount + "," + dtotal + "," + dcount + "," + dtotal + ",'" + batchno + "') on duplicate key update count=round(count+" + dcount + "," + countbit
										+ "),money=round(money+" + dtotal + "," + moneybit + ")" + ",splitsin_count=round(splitsin_count+" + dcount + "," + countbit
										+ "),splitsin_money=round(splitsin_money+" + dtotal + "," + moneybit + ")";
								ps.addBatch(details); // 调入变更
							}
						}

						if (type > 0) {
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

					if (!message.equals(""))
						message = message + "这些商品库存不足，保存失败。";
				}
			} else {
				save = false;
				message = message + "没有商品明细数据，保存失败";
			}
			if (save) {
				int changebilltype = Pdacommon.getDatalogBillChangefunc("splits", "");
				if (operate.equals("draftedit")) {// 草稿编辑 更新
					ps.addBatch("update splits set  orderid='" + orderid + "',originalbill='" + maindata.getString("originalbill") + "',operate_time='" + operate_time + "',operate_by='"
							+ maindata.getString("operate_by") + "',oldhouseid='',newhouseid='',oldcount=" + maindata.getString("oldcount") + ",newcount=" + maindata.getString("newcount")
							+ ",oldtotal=" + maindata.getString("oldtotal") + ",newtotal=" + maindata.getString("newtotal") + ",remark='" + maindata.getString("remark") + "',status='" + status
							+ "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now(),iproperty='" + maindata.getString("iproperty") + "' where splitsid='"
							+ maindata.getString("splitsid") + "'");

					ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
							+ companyid + "'," + changebilltype + ",'修改','" + maindata.getString("splitsid") + "','单据编号：" + orderid + "','" + loginuserid + "','" + loginUser + "',now())");

					ps.addBatch("update splitsdetail d,splits s set d.create_id=s.create_id,d.create_by=s.create_by,d.create_time=s.create_time where d.splitsid = s.splitsid and s.splitsid='"
							+ maindata.getString("splitsid") + "'");

				} else {
					String main = "insert into splits (orderid,splitsid,bill_type,originalbill,companyid,operate_time,operate_by,oldhouseid,newhouseid,oldcount,newcount,oldtotal,newtotal,remark,status,printing,outexcel,create_id,create_by,create_time,update_id,update_by,update_time,iproperty) VALUES ('"
							+ orderid
							+ "','"
							+ maindata.getString("splitsid")
							+ "','"
							+ maindata.getString("bill_type")
							+ "','"
							+ maindata.getString("originalbill")
							+ "','"
							+ companyid
							+ "','"
							+ operate_time
							+ "','"
							+ maindata.getString("operate_by")
							+ "','','',"
							+ maindata.getString("oldcount")
							+ ","
							+ maindata.getString("newcount")
							+ ","
							+ maindata.getString("oldtotal")
							+ ","
							+ maindata.getString("newtotal")
							+ ",'"
							+ maindata.getString("remark")
							+ "','"
							+ status
							+ "',0,0,'"
							+ loginuserid
							+ "','"
							+ loginUser + "',now(),'" + loginuserid + "','" + loginUser + "',now(),'" + maindata.getString("iproperty") + "')";
					ps.addBatch(main); // 主记录

					ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
							+ companyid + "'," + changebilltype + ",'" + (status == 0 ? "新增暂存" : "新增保存") + "','" + maindata.getString("splitsid") + "','单据编号：" + orderid + "','" + loginuserid + "','"
							+ loginUser + "',now())");

				}
				ps.executeBatch();
				conn.commit();
				conn.setAutoCommit(true);
				state = "1";

				if (type > 0) {
					// 库存报警
					Bhouselimit = Common.gethouselimitdata(conn, companyid, condition, Bhouselimit);
				}
			}

		} catch (Exception e) {
			// e.printStackTrace();
			try {
				conn.rollback();
			} catch (Exception e1) {
				// e1.printStackTrace();
			}
			message = "保存失败!!";// + e.getMessage().toString();
		} finally {
			condition.setLength(0);
			conn.close();
		}
		rt.put("state", state);
		rt.put("message", message);
		rt.put("warning", Bhouselimit);
		return rt;
	}

	// 保存报损记录单信息
	public static JSONObject saveReportLossFunction(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject rt = new JSONObject();
		JSONObject maindata = JSONObject.parseObject(params.getString("maindata"));
		Integer type = params.getInteger("type");
		String loginUser = params.getString("loginUser");
		String loginuserid = params.getString("loginuserid");
		JSONArray detaildata = params.getJSONArray("detaildata");
		String operate = params.getString("operate");// draftedit 编辑单据
		String state = "0";

		String companyid = maindata.getString("companyid");
		String houseid = maindata.getString("houseid");
		String operate_time = maindata.getString("operate_time");
		double count = maindata.getDoubleValue("count");
		double total = maindata.getDoubleValue("total");

		int status = type == 0 ? 0 : 1;
		String message = "";
		int i = 0;

		String[] sdatearr = operate_time.split("-");
		String billdate = sdatearr[0] + sdatearr[1] + sdatearr[2];

		try {

			Statement ps = conn.createStatement();
			conn.setAutoCommit(false);
			String orderid = operate.equals("draftedit") ? maindata.getString("orderid") : getOrderidByparams(companyid, "reportloss", "", billdate, conn);
			String detail = "insert into reportlossdetail (orderid,detailid,lossid,originalbill,goods_number,companyid,operate_by,operate_time,itemid,houseid,price,count,total,stype,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,batchno) VALUES ('";

			String details = "";
			boolean save = true;
			if (operate.equals("draftedit")) {// 草稿编辑 删除明细数据重新增加
				String fsql = "select status from reportloss where lossid='" + maindata.getString("lossid") + "'";
				Object cobject = DataUtils.getValueBySQL(conn, fsql, null);
				if (cobject == null) {
					save = false;
					message = "当前记录已删除，操作失败。";
					state = "2";
				} else {
					String fstatus = cobject.toString();
					if (fstatus.equals("0")) {
						ps.addBatch("delete from reportlossdetail where lossid='" + maindata.getString("lossid") + "'");
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
					for (i = 0; i < detaildata.size(); i++) {
						JSONObject result = JSONObject.parseObject(detaildata.getString(i));
						String detailid = result.getString("detailid");
						String batchno = result.getString("batchno");
						String itemid = result.getString("itemid");
						double cost_price = result.getDoubleValue("price");
						double dcount = result.getDoubleValue("count");
						double dtotal = result.getDoubleValue("total");

						details = detail + orderid + "','" + (detailid.equals("") ? Common.getUpperUUIDString() : detailid) + "','" + maindata.getString("lossid") + "','"
								+ maindata.getString("originalbill") + "'," + result.getInteger("goods_number") + ",'" + companyid + "','" + maindata.getString("operate_by") + "','"
								+ maindata.getString("operate_time") + "','" + itemid + "','" + houseid + "'," + cost_price + "," + dcount + "," + dtotal + ",'91','" + result.getString("remark")
								+ "','" + status + "','" + (detailid.equals("") ? loginuserid : result.getString("create_id")) + "','"
								+ (detailid.equals("") ? loginUser : result.getString("create_by")) + "'," + (detailid.equals("") ? "now()" : "'" + result.getString("create_time") + "'") + ",'"
								+ loginuserid + "','" + loginUser + "',now(),'" + batchno + "')";
						ps.addBatch(details); // 明细记录

					}
				}

			} else {
				save = false;
				message = message + "没有商品明细数据，保存失败";
			}
			if (save) {
				int changebilltype = Pdacommon.getDatalogBillChangefunc("reportloss", "");
				if (operate.equals("draftedit")) {// 草稿编辑 更新
					ps.addBatch("update reportloss set orderid='" + orderid + "', originalbill='" + maindata.getString("originalbill") + "',operate_time='" + operate_time + "',operate_by='"
							+ maindata.getString("operate_by") + "',houseid='" + houseid + "',count=" + maindata.getString("count") + ",total=" + maindata.getString("total") + ",remark='"
							+ maindata.getString("remark") + "',status='" + status + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now(),iproperty='"
							+ maindata.getString("iproperty") + "' where lossid='" + maindata.getString("lossid") + "'");

					ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
							+ companyid + "'," + changebilltype + ",'修改','" + maindata.getString("lossid") + "','单据编号：" + orderid + "','" + loginuserid + "','" + loginUser + "',now())");

					ps.addBatch("update reportlossdetail d,reportloss s set d.create_id=s.create_id,d.create_by=s.create_by,d.create_time=s.create_time where d.lossid = s.lossid and s.lossid='"
							+ maindata.getString("lossid") + "'");

				} else {
					String main = "insert into reportloss (orderid,lossid,bill_type,originalbill,companyid,operate_time,operate_by,houseid,count,total,remark,status,printing,outexcel,create_id,create_by,create_time,update_id,update_by,update_time,iproperty) VALUES ('"
							+ orderid
							+ "','"
							+ maindata.getString("lossid")
							+ "','"
							+ maindata.getString("bill_type")
							+ "','"
							+ maindata.getString("originalbill")
							+ "','"
							+ companyid
							+ "','"
							+ operate_time
							+ "','"
							+ maindata.getString("operate_by")
							+ "','"
							+ houseid
							+ "',"
							+ count
							+ ","
							+ total
							+ ",'"
							+ maindata.getString("remark")
							+ "','"
							+ status
							+ "',0,0,'"
							+ loginuserid + "','" + loginUser + "',now(),'" + loginuserid + "','" + loginUser + "',now(),'" + maindata.getString("iproperty") + "')";
					ps.addBatch(main); // 主记录

					ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
							+ companyid + "'," + changebilltype + ",'" + (status == 0 ? "新增暂存" : "新增保存") + "','" + maindata.getString("lossid") + "','单据编号：" + orderid + "','" + loginuserid + "','"
							+ loginUser + "',now())");

				}

				ps.executeBatch();
				conn.commit();
				conn.setAutoCommit(true);
				state = "1";
			}

		} catch (Exception e) {
			// e.printStackTrace();
			try {
				conn.rollback();
			} catch (Exception e1) {
				// e1.printStackTrace();
			}
			message = "保存失败!!";// + e.getMessage().toString();
		} finally {
			conn.close();
		}
		rt.put("state", state);
		rt.put("message", message);
		return rt;
	}

	// 2019-04-28 李趣芸 更新保存打印数据记录
	public static JSONObject savePrintInfo(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		String loginuserid = params.getString("loginuserid");
		String loginUser = params.getString("loginUser");
		String tablename = params.getString("tablename");
		String mainid = params.getString("mainid");
		String billtype = params.getString("billtype");
		String companyid = params.getString("companyid");
		String orderid = params.getString("orderid");
		JSONObject rt = new JSONObject();
		String message = "";
		String datachageinfo = "单据编号：" + orderid;
		int stype = Pdacommon.getDatalogBillChangefunc(tablename, billtype);
		// System.out.println(tablename + " " + mainid + " " + orderid);
		try {
			// 11-入库单 12-出库单 13-组装拆卸单 14-调拨单 15-盘点单 19-报损记录单 16-退货单 17-客户退回
			Statement ps = conn.createStatement();
			if (tablename.equals("reportloss")) {
				ps.addBatch("update reportloss set printing=ifnull(printing,0)+1 where lossid ='" + mainid + "'");
			} else if (tablename.equals("t_order")) {
				ps.addBatch("update t_order set printing=ifnull(printing,0)+1 where id ='" + mainid + "'");
			} else if (tablename.equals("qualitymain") || tablename.equals("apply_material") || tablename.equals("apply_invoice") || tablename.equals("apply_leave")
					|| tablename.equals("apply_overtime") || tablename.equals("apply_iteminfo")) {//
				ps.addBatch("update " + tablename + " set printing=ifnull(printing,0)+1 where mainid ='" + mainid + "'");
			} else if (tablename.equals("scheduleorder")) {
				ps.addBatch("update scheduleorder set printing=ifnull(printing,0)+1 where scheduleid ='" + mainid + "'");
			} else if (tablename.equals("t_order_detail") || tablename.equals("t_detail_code") || tablename.equals("apply_payment")) {
				ps.addBatch("update " + tablename + "  set printing=ifnull(printing,0)+1 where id in (" + mainid + ")");
			} else {
				ps.addBatch("update " + tablename + " set printing=ifnull(printing,0)+1 where " + tablename + "id ='" + mainid + "'");
			}

			ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
					+ companyid + "'," + stype + ",'打印','" + (mainid.indexOf("'") > -1 ? "" : mainid) + "','" + datachageinfo + "','" + loginuserid + "','" + loginUser + "',now())");

			ps.executeBatch();
			rt.put("state", "1");
		} catch (Exception e) {
			// e.printStackTrace();
			try {
				conn.rollback();
				rt.put("state", "0");
				// message = message + e.getMessage().toString();
			} catch (Exception e1) {
				// e1.printStackTrace();
				rt.put("state", "0");
				// message = message + e.getMessage().toString();
			}
		} finally {
			conn.close();
		}
		rt.put("message", message);
		return rt;
	}

	// 保存期初库存单信息
	public static JSONObject saveItemBeginFunction(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject rt = new JSONObject();
		JSONObject maindata = JSONObject.parseObject(params.getString("maindata"));
		Integer type = params.getInteger("type");
		Integer pricebit = params.getInteger("pricebit");
		String loginUser = params.getString("loginUser");
		String loginuserid = params.getString("loginuserid");
		JSONArray detaildata = params.getJSONArray("detaildata");
		String operate = params.getString("operate");// draftedit 编辑单据
		String state = "0";
		String message = "";

		String companyid = maindata.getString("companyid");
		String houseid = maindata.getString("houseid");
		String operate_time = maindata.getString("operate_time");
		double count = maindata.getDoubleValue("count");
		double total = maindata.getDoubleValue("total");
		int status = type == 0 ? 0 : 1;

		String[] sdatearr = operate_time.split("-");
		int syear = Integer.parseInt(sdatearr[0]);
		int smonth = Integer.parseInt(sdatearr[1]);
		String sdate = sdatearr[0] + "-" + sdatearr[1] + "-01";

		String billdate = sdatearr[0] + sdatearr[1] + sdatearr[2];

		JSONArray checkdata = params.getJSONArray("checkdata");

		try {
			int countbit = 0;
			int moneybit = 2;
			int t6days = 0;

			Table companytalbe = DataUtils.queryData(conn, "select pricebit,countbit,moneybit,t6days from s_company_config where company_id='" + companyid + "'", null, null, null, null);
			if (companytalbe.getRows().size() > 0) {
				countbit = companytalbe.getRows().get(0).getInteger("countbit");
				moneybit = companytalbe.getRows().get(0).getInteger("moneybit");
				t6days = companytalbe.getRows().get(0).getInteger("t6days");
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
			conn.setAutoCommit(false);
			String detail = "insert into itembegindetail (orderid,detailid,itembeginid,originalbill,goods_number,companyid,operate_by,operate_time,itemid,houseid,price,count,total,stype,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,batchno) VALUES ('";
			String store = "insert into stock (stockid,companyid,itemid,houseid,count,money,newcostprice,batchno,stockremark) VALUES ('";
			String itemmonth = "insert into itemmonth (monthid,companyid,itemid,houseid,sdate,syear,smonth,count,money,begincount,begintotal,batchno) VALUES ('";

			String orderid = operate.equals("draftedit") ? maindata.getString("orderid") : getOrderidByparams(companyid, "itembegin", "", billdate, conn);
			int i = 0;
			String details = "";
			if (save && operate.equals("draftedit")) {// 草稿编辑 删除明细数据重新增加
				String fsql = "select status from itembegin where itembeginid='" + maindata.getString("itembeginid") + "'";
				Object cobject = DataUtils.getValueBySQL(conn, fsql, null);
				if (cobject == null) {
					save = false;
					message = "当前记录已删除，操作失败。";
					state = "2";
				} else {
					String fstatus = cobject.toString();
					if (fstatus.equals("0")) {
						ps.addBatch("delete from itembegindetail where itembeginid='" + maindata.getString("itembeginid") + "'");
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
					for (i = 0; i < detaildata.size(); i++) {
						JSONObject result = JSONObject.parseObject(detaildata.getString(i));
						String detailid = result.getString("detailid");
						String batchno = result.getString("batchno");
						String itemid = result.getString("itemid");
						double price = result.getDoubleValue("price");
						double dcount = result.getDoubleValue("count");
						double dtotal = result.getDoubleValue("total");

						if (type > 0) {
							details = store + Common.getUpperUUIDString() + "','" + companyid + "','" + itemid + "','" + houseid + "'," + dcount + "," + dtotal + ",round("
									+ (dcount > 0 ? dtotal / dcount : 0) + "," + pricebit + "),'" + batchno + "','" + result.getString("remark") + "') on duplicate key update count=round(count+"
									+ dcount + "," + countbit + "),money=round(money+" + dtotal + "," + moneybit + "),newcostprice=round(if(count=0,newcostprice,money/count)," + pricebit + ")"
									+ (result.getString("remark").equals("") ? "" : ",stockremark='" + result.getString("remark") + "'");

							ps.addBatch(details);

							details = itemmonth + Common.getUpperUUIDString() + "','" + companyid + "','" + itemid + "','" + houseid + "','" + sdate + "'," + syear + "," + smonth + "," + dcount + ","
									+ dtotal + "," + dcount + "," + dtotal + ",'" + batchno + "') on duplicate key update count=round(count+" + dcount + "," + countbit + "),money=round(money+"
									+ dtotal + "," + moneybit + "),begincount=round(begincount+" + dcount + "," + countbit + "),begintotal=round(begintotal+" + dtotal + "," + moneybit + ")";
							ps.addBatch(details);
						}

						details = detail + orderid + "','" + (detailid.equals("") ? Common.getUpperUUIDString() : detailid) + "','" + maindata.getString("itembeginid") + "','"
								+ maindata.getString("originalbill") + "'," + result.getInteger("goods_number") + ",'" + companyid + "','" + maindata.getString("operate_by") + "','"
								+ maindata.getString("operate_time") + "','" + itemid + "','" + houseid + "'," + price + "," + dcount + "," + dtotal + ",'81','" + result.getString("remark") + "','"
								+ status + "','" + (detailid.equals("") ? loginuserid : result.getString("create_id")) + "','" + (detailid.equals("") ? loginUser : result.getString("create_by"))
								+ "'," + (detailid.equals("") ? "now()" : "'" + result.getString("create_time") + "'") + ",'" + loginuserid + "','" + loginUser + "',now(),'" + batchno + "')";

						ps.addBatch(details);

					}
				}

				if (type > 0) {
					for (i = 0; i < checkdata.size(); i++) {
						JSONObject checkresult = JSONObject.parseObject(checkdata.getString(i));
						String fhouseid = checkresult.getString("houseid");
						String fbatchno = checkresult.getString("batchno");
						double ftotalcount = checkresult.getDoubleValue("totalcount");
						String fitemid = checkresult.getString("itemid");
						String fcodeid = checkresult.getString("codeid");

						// companyid='" + companyid + "' and
						String checksql = "select count,round(count-checkout_count," + countbit + ") as cancount from stock where  itemid='" + fitemid + "' and houseid='" + fhouseid + "' "
								+ " and batchno='" + fbatchno + "'";

						Table ftable = DataUtils.queryData(conn, checksql, null, null, null, null);
						// 2020-12-10 涉及到仓库没有数据问题。
						double fcancount = 0;
						if (ftable.getRows().size() > 0) {
							fcancount = Double.parseDouble(ftable.getRows().get(0).getValue("cancount").toString());
							if (fcancount < checkresult.getDoubleValue("totalcount")) {
								message = message + "(" + fcodeid + "、" + checkresult.getString("itemname") + "、" + checkresult.getString("housename") + " " + fbatchno + "《总需减的库存数量》："
										+ checkresult.getDoubleValue("totalcount") + " 大于 《最大可减的库存数量》：" + fcancount + ")";
								save = false;
							}
						} else {// 2020-12-10 增加
							message = message + "(" + fcodeid + "、" + checkresult.getString("itemname") + "、" + checkresult.getString("housename") + " " + fbatchno + "《总需减的库存数量》："
									+ checkresult.getDoubleValue("totalcount") + " 大于 《最大可减的库存数量》：" + fcancount + ")";
							save = false;
						}
					}
				}
			} else {
				message = message + "没有商品明细数据，操作失败";
				save = false;
			}
			if (save) {
				int changebilltype = Pdacommon.getDatalogBillChangefunc("itembegin", "");
				if (operate.equals("draftedit")) {// 草稿编辑 更新
					ps.addBatch("update itembegin set orderid='" + orderid + "', originalbill='" + maindata.getString("originalbill") + "',operate_time='" + operate_time + "',operate_by='"
							+ maindata.getString("operate_by") + "',houseid='" + houseid + "',count=" + count + ",total=" + total + ",remark='" + maindata.getString("remark") + "',status='" + status
							+ "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now(),iproperty='" + maindata.getString("iproperty") + "' where itembeginid='"
							+ maindata.getString("itembeginid") + "'");

					ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
							+ companyid + "'," + changebilltype + ",'修改','" + maindata.getString("itembeginid") + "','单据编号：" + orderid + "','" + loginuserid + "','" + loginUser + "',now())");

					ps.addBatch("update itembegindetail d,itembegin s set d.create_id=s.create_id,d.create_by=s.create_by,d.create_time=s.create_time where d.itembeginid = s.itembeginid and s.itembeginid='"
							+ maindata.getString("itembeginid") + "'");

				} else {
					String main = "insert into itembegin (orderid,itembeginid,bill_type,originalbill,companyid,operate_time,operate_by,houseid,count,total,remark,status,printing,outexcel,create_id,create_by,create_time,update_id,update_by,update_time,iproperty) VALUES ('"
							+ orderid
							+ "','"
							+ maindata.getString("itembeginid")
							+ "','"
							+ maindata.getString("bill_type")
							+ "','"
							+ maindata.getString("originalbill")
							+ "','"
							+ companyid
							+ "','"
							+ operate_time
							+ "','"
							+ maindata.getString("operate_by")
							+ "','"
							+ houseid
							+ "',"
							+ count
							+ ","
							+ total
							+ ",'"
							+ maindata.getString("remark")
							+ "','"
							+ status
							+ "',0,0,'"
							+ loginuserid + "','" + loginUser + "',now(),'" + loginuserid + "','" + loginUser + "',now(),'" + maindata.getString("iproperty") + "')";
					ps.addBatch(main);

					ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
							+ companyid + "'," + changebilltype + ",'" + (status == 0 ? "新增暂存" : "新增保存") + "','" + maindata.getString("itembeginid") + "','单据编号：" + orderid + "','" + loginuserid
							+ "','" + loginUser + "',now())");

				}

				ps.executeBatch();
				conn.commit();
				conn.setAutoCommit(true);
				state = "1";
			}

		} catch (Exception e) {
			message = message + "操作失败，请稍后再试。";
			// e.printStackTrace();
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

	// 保存库存报警信息
	public static JSONObject saveHouseLimitFunction(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject rt = new JSONObject();
		JSONArray detaildata = params.getJSONArray("detaildata");
		String loginUser = params.getString("loginUser");
		String loginuserid = params.getString("loginuserid");
		String state = "0";

		String companyid = params.getString("companyid");
		String houseid = params.getString("houseid");
		String housename = params.getString("housename");

		String message = "";
		int i = 0;

		try {

			Statement ps = conn.createStatement();
			conn.setAutoCommit(false);

			if (detaildata.size() > 0) {
				for (i = 0; i < detaildata.size(); i++) {
					JSONObject result = JSONObject.parseObject(detaildata.getString(i));
					String limitid = result.getString("limitid");
					String itemid = result.getString("itemid");
					Double uplimit = result.getDoubleValue("uplimit");
					Double lowlimit = result.getDoubleValue("lowlimit");

					if (uplimit == 0 && lowlimit == 0) {
						ps.addBatch("delete from houselimit where limitid='" + limitid + "'");
					} else {

						ps.addBatch("insert into houselimit (limitid,companyid,itemid,houseid,uplimit,lowlimit,create_id,create_by,create_time,update_id,update_by,update_time) VALUES ('" + limitid
								+ "','" + companyid + "','" + itemid + "','" + houseid + "'," + uplimit + "," + lowlimit + ",'" + loginuserid + "','" + loginUser + "',now(),'" + loginuserid + "','"
								+ loginUser + "',now()) on duplicate key update "

								+ " uplimit=" + uplimit + ",lowlimit=" + lowlimit + ",update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now()"); // 明细记录

					}
				}
				ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
						+ companyid + "',6,'修改保存','" + houseid + "','库存报警仓库：" + housename + "','" + loginuserid + "','" + loginUser + "',now())");

				ps.executeBatch();
				conn.commit();
				conn.setAutoCommit(true);
				state = "1";
			} else {
				message = "没有数据可保存";
			}

		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (Exception e1) {
			}
			message = "保存失败!!";// + e.getMessage().toString();
		} finally {
			conn.close();
		}
		rt.put("message", message);
		rt.put("state", state);
		return rt;
	}

	// 员工信息启、停用
	public static JSONObject updateStaffinfoStatus(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		String status = params.getString("status");
		String loginuserid = params.getString("loginuserid");
		String loginUser = params.getString("loginUser");
		String companyid = params.getString("companyid");
		String staffid = params.getString("staffid");
		String datacontent = params.getString("datacontent");
		JSONObject rt = new JSONObject();
		String message = "";
		try {
			Statement ps = conn.createStatement();
			ps.addBatch("update staffinfo set status='" + status + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where staffid='" + staffid + "'");
			ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
					+ companyid + "',8,'启/停用','" + staffid + "','" + datacontent + "','" + loginuserid + "','" + loginUser + "',now())");

			ps.executeBatch();
			rt.put("state", "1");
		} catch (Exception e) {
			// e.printStackTrace();
			try {
				conn.rollback();
				rt.put("state", "0");
				// message = message + e.getMessage().toString();
			} catch (Exception e1) {
				// e1.printStackTrace();
				rt.put("state", "0");
				// message = message + e.getMessage().toString();
			}
		} finally {
			conn.close();
		}
		rt.put("message", message);
		return rt;
	}

	// 保存员工信息表
	public static JSONObject saveStaffinfoFunction(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject rt = new JSONObject();
		JSONObject maindata = JSONObject.parseObject(params.getString("maindata"));
		String staffcode = params.getString("staffcode");
		String operate = params.getString("operate");
		String companyid = params.getString("companyid");
		String change = params.getString("change");
		String loginUser = params.getString("loginUser");
		String loginuserid = params.getString("loginuserid");
		String oldstaffcode = params.getString("oldstaffcode");
		String updateinfo = params.getString("updateinfo");
		String staffinfostr = params.getString("staffinfostr");

		String staffid = maindata.getString("staffid");

		try {

			Statement ps = conn.createStatement();
			conn.setAutoCommit(false);

			String codeid = staffcode.equals("") ? Pdacommon.getCodeByparams(companyid, "staffinfo", "staffcode", conn) : staffcode;

			if (operate.equals("edit")) {
				ps.addBatch("update  staffinfo set " + updateinfo + (!staffcode.equals("") && oldstaffcode.equals(staffcode) ? "" : (updateinfo.equals("") ? "" : ",") + "staffcode='" + codeid + "'")
						+ ",update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now()  where staffid='" + staffid + "'");

				String content = "";
				if (!oldstaffcode.equals(staffcode)) {
					content = "【员工编号】原值《" + staffcode + "》" + (staffcode.equals("") ? "因修改为空值，自动生成编号" : "") + "改变为《" + codeid + "》" + (change.equals("") ? "" : "；" + change);
				} else {
					content = change;
				}
				// 修改数据记录
				ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
						+ companyid + "',8,'修改','" + staffid + "','" + staffinfostr + content + "','" + loginuserid + "','" + loginUser + "',now())");

			} else {
				ps.addBatch("insert into staffinfo (staffcode,staffid,companyid,staffname,staffsex,staffphone,staffemail,staffaddress,staffposition,orgid,userid,status,create_id,create_by,create_time,update_id,update_by,update_time) VALUES ('"
						+ (staffcode.equals("") ? codeid : staffcode)
						+ "','"
						+ staffid
						+ "','"
						+ companyid
						+ "','"
						+ maindata.getString("staffname")
						+ "','"
						+ maindata.getString("staffsex")
						+ "','"
						+ maindata.getString("staffphone")
						+ "','"
						+ maindata.getString("staffemail")
						+ "','"
						+ maindata.getString("staffaddress")
						+ "','"
						+ maindata.getString("staffposition")
						+ "','" + maindata.getString("orgid") + "','','1','" + loginuserid + "','" + loginUser + "',now(),'" + loginuserid + "','" + loginUser + "',now())");

				// 新增数据记录
				ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
						+ companyid + "',8,'新增','" + staffid + "','员工编号：" + (staffcode.equals("") ? codeid : staffcode) + "','" + loginuserid + "','" + loginUser + "',now())");

			}

			ps.executeBatch();
			conn.commit();
			conn.setAutoCommit(true);
			rt.put("state", "1");
		} catch (Exception e) {
			// e.printStackTrace();
			try {
				conn.rollback();
				rt.put("state", "0");
			} catch (Exception e1) {
				// e1.printStackTrace();
				rt.put("state", "0");
			}
		} finally {
			conn.close();
		}
		return rt;
	}

	// 批量删除员工信息，有其他表关联或有操作员关联的不能删除。
	public static JSONObject delAllstaffs(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject rt = new JSONObject();
		String staffids = params.getString("staffids");
		String companyid = params.getString("companyid");
		int rowcount = 0;
		if (staffids != null && companyid != null && !staffids.equals("")) {
			try {
				// String sql =
				// "delete from staffinfo where (userid='' or userid is null)  and staffid in ("
				// + staffids +
				// ") and staffid not in (select distinct staff from customer where companyid='"
				// + companyid + "' and staff in (" + staffids +
				// ")) and staffid not in (select distinct user_id from t_order_progress where companyid='"
				// + companyid + "' and user_id in ("
				// + staffids +
				// "))  and staffid not in (select distinct operate_by from main_view where companyid='"
				// + companyid + "' and operate_by in (" + staffids
				// +
				// ") union all select distinct operate_by from purchaseorder where companyid='"
				// + companyid + "'  and operate_by in (" + staffids
				// +
				// ") union all select distinct operate_by from salesorder where companyid='"
				// + companyid + "'  and operate_by in (" + staffids
				// +
				// ") union all select distinct operate_by from scheduleorder where companyid='"
				// + companyid + "'  and operate_by in (" + staffids
				// +
				// ") union all select distinct operate_by from outsourcing where companyid='"
				// + companyid + "')";
				int m = 0;
				String[] itemidsarr = staffids.replaceAll("'", "").split(",");
				String temp = "";
				for (m = 0; m < itemidsarr.length; m++) {
					temp = temp + (temp.equals("") ? "" : ",") + "?";
				}

				String sql = "delete from staffinfo where staffid in ("
						+ temp
						+ ") and (userid='' or userid is null) and if((select  1 from customer where staff=staffinfo.staffid limit 1),false,true)  and if((select  1 from t_order_progress where  user_id =staffinfo.staffid limit 1),false,true)    and if((select  1 from main_view where  operate_by =staffinfo.staffid limit 1),false,true)  "
						+ " and if((select  1 from purchaseorder where operate_by =staffinfo.staffid limit 1),false,true)  "
						+ " and if((select  1 from salesorder where operate_by =staffinfo.staffid limit 1),false,true)  "
						+ " and if((select  1 operate_by from scheduleorder where operate_by =staffinfo.staffid limit 1),false,true)  "
						+ " and if((select  1 from outsourcing where operate_by =staffinfo.staffid limit 1),false,true)  "
						+ " and if((select  1 from invoicestoreindetail where operate_by =staffinfo.staffid limit 1),false,true)  ";

				PreparedStatement ps = conn.prepareStatement(sql);
				for (m = 0; m < itemidsarr.length; m++) {
					ps.setString(m + 1, itemidsarr[m]);
				}
				ps.execute();
				rowcount = ps.getUpdateCount();
			} catch (Exception e) {
				rowcount = 0;
			} finally {
				conn.close();
			}
		}
		rt.put("rowcount", rowcount);
		return rt;
	}

}
