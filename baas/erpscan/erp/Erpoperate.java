package erpscan.erp;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.justep.baas.action.ActionContext;
import com.justep.baas.data.DataUtils;
import com.justep.baas.data.Row;
import com.justep.baas.data.Table;
import com.justep.baas.data.Transform;

import erpscan.Common;
import erpscan.save.Pdacommon;
import erpscan.save.Pdasave;

public class Erpoperate {
	private static final String DATASOURCE = Common.DATASOURCE;

	// 保存采购订单信息
	public static JSONObject savePurchaseorderFunction(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject rt = new JSONObject();
		JSONObject maindata = JSONObject.parseObject(params.getString("maindata"));
		Integer type = params.getInteger("type");
		String loginUser = params.getString("loginUser");
		String loginuserid = params.getString("loginuserid");
		JSONArray detaildata = params.getJSONArray("detaildata");
		String operate = params.getString("operate");// edit 编辑单据
		String state = "0";
		String message = "";

		String companyid = maindata.getString("companyid");
		String houseid = maindata.getString("houseid");
		String customerid = maindata.getString("customerid");
		String operate_time = maindata.getString("operate_time");
		double count = maindata.getDoubleValue("count");
		double total = maindata.getDoubleValue("total");
		// int status = type == 0 ? 1 : 0;// type=0 为保存并审核 ，其他为保存 未审核
		int status = (type == 3 ? 3 : (type == 0 ? 1 : 0));// type=0 为保存并审核
		// ，其他为保存 未审核

		JSONObject auditflowlist = JSONObject.parseObject(params.getString("auditflowlist"));

		// 合同保存功能 2019-12-09 begin
		String storecontractstatus = params.getString("storecontractstatus");
		JSONObject contractdata = storecontractstatus.equals("no") ? null : JSONObject.parseObject(params.getString("contractdata"));
		// System.out.println(storecontractstatus);
		String fcontractmsg = params.getString("fcontractmsg");
		// 合同保存功能 2019-12-09 end

		String[] sdatearr = operate_time.split("-");
		int syear = Integer.parseInt(sdatearr[0]);
		int smonth = Integer.parseInt(sdatearr[1]);
		String sdate = sdatearr[0] + "-" + sdatearr[1] + "-01";

		String billdate = sdatearr[0] + sdatearr[1] + sdatearr[2];

		try {
			int countbit = 0;
			int moneybit = 2;
			Table companytalbe = DataUtils.queryData(conn, "select pricebit,countbit,moneybit from s_company_config where company_id='" + companyid + "'", null, null, null, null);
			if (companytalbe.getRows().size() > 0) {
				countbit = companytalbe.getRows().get(0).getInteger("countbit");
				moneybit = companytalbe.getRows().get(0).getInteger("moneybit");
			}

			Statement ps = conn.createStatement();
			conn.setAutoCommit(false);
			String detail = "insert into purchaseorderdetail (orderid,detailid,purchaseorderid,originalbill,goods_number,companyid,operate_by,operate_time,plandate,itemid,customerid,houseid,price,count,total,tax,taxrate,taxprice,taxmoney,stype,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,batchno"
					+ (type == 0 ? ",audit_id,audit_by,audit_time" : "") + ",relationdetailid,relationorderid,relationmainid,relationtype) VALUES ('";
			String itemmonth = "insert into ordermonth (monthid,companyid,itemid,sdate,syear,smonth,purchasecount,purchasemoney,batchno) VALUES ('";

			String orderid = operate.equals("edit") ? maindata.getString("orderid") : Pdasave.getOrderidByparams(companyid, "purchaseorder", "", billdate, conn);
			int i = 0;
			String details = "";
			boolean save = true;
			Map<String, String> relationdetailMap = new HashMap();// 缓存原数据绑定id
			Map<String, String> relationdetailMainMap = new HashMap();// 缓存原数据绑定id

			Map<String, String> ArelationdetailMap = new HashMap();// 缓存原数据绑定id
			Map<String, String> ArelationdetailMainMap = new HashMap();// 缓存原数据绑定id

			String relationmainids = "";
			String relationdetailids = "";

			if (operate.equals("edit")) {// 编辑 删除明细数据重新增加
				String fsql = "select status from purchaseorder where purchaseorderid='" + maindata.getString("purchaseorderid") + "'";
				Object cobject = DataUtils.getValueBySQL(conn, fsql, null);
				if (cobject == null) {
					save = false;
					message = "当前记录已删除，操作失败。";
					state = "2";
				} else {
					String fstatus = cobject.toString();
					if (fstatus.equals("0")) {
						// 获取原明细数据
						String olddetailsql = "select relationdetailid,relationmainid,relationtype from purchaseorderdetail where purchaseorderid='" + maindata.getString("purchaseorderid")
								+ "' group by relationdetailid,relationmainid";
						Table olddetailtable = DataUtils.queryData(conn, olddetailsql, null, null, null, null);
						if (olddetailtable.getRows().size() > 0) {
							for (Row row : olddetailtable.getRows()) {
								int relationtype = row.getInteger("relationtype");
								String relationdetailid = row.getString("relationdetailid");
								String relationmainid = row.getString("relationmainid");

								if (relationtype == 1) {
									ArelationdetailMap.put(relationdetailid, "");
									if (!ArelationdetailMainMap.containsKey(relationmainid)) {
										ArelationdetailMainMap.put(relationmainid, "");
									}
								} else {
									relationdetailMap.put(relationdetailid, "");
									if (!relationdetailMainMap.containsKey(relationmainid)) {
										relationdetailMainMap.put(relationmainid, "");
									}
								}

							}
						}
						ps.addBatch("delete from purchaseorderdetail where purchaseorderid='" + maindata.getString("purchaseorderid") + "'");
					} else if (fstatus.equals("1")) {
						save = false;
						message = "当前记录已审核，操作失败。";
						state = "2";
					} else if (fstatus.equals("3")) {
						save = false;
						message = "当前记录已提交审批，操作失败。";
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
						detailid = (detailid.equals("") ? Common.getUpperUUIDString() : detailid);
						String batchno = result.getString("batchno");
						String itemid = result.getString("itemid");
						double price = result.getDoubleValue("price");
						double dcount = result.getDoubleValue("count");
						double dtotal = result.getDoubleValue("total");
						String plandate = result.getString("plandate");
						String relationdetailid = result.getString("relationdetailid");
						String relationorderid = result.getString("relationorderid");
						String relationmainid = result.getString("relationmainid");
						Integer relationtype = result.getInteger("relationtype");
						relationtype = (relationtype == null ? 0 : relationtype);

						if (relationtype == 1) {
							if (!relationdetailid.equals("") && !ArelationdetailMap.containsKey(relationdetailid)) {
								ArelationdetailMap.put(relationdetailid, "");
							}

							if (!relationmainid.equals("") && !ArelationdetailMainMap.containsKey(relationmainid)) {
								ArelationdetailMainMap.put(relationmainid, "");
							}

							String findsql = "select sd.relationdetailid,sd.relationorderid from apply_materialdetail sd where sd.detailid='" + relationdetailid + "' and sd.status='1' ";

							Table ftable = DataUtils.queryData(conn, findsql, null, null, null, null);
							if (ftable.getRows().size() > 0) {
								String tempstr = ftable.getRows().get(0).getString("relationdetailid");
								if (!tempstr.equals("") && !tempstr.equals(detailid)) {
									message = "序号为" + result.getInteger("goods_number") + "的补料申请记录已采购(订单号：" + ftable.getRows().get(0).getString("relationorderid") + ")，不能重复操作！";
									save = false;
									break;
								}
							} else {
								message = "序号为" + result.getInteger("goods_number") + "的补料申请记录已作废，不能保存！";
								save = false;
								break;
							}

						} else {

							if (!relationdetailid.equals("") && !relationdetailMap.containsKey(relationdetailid)) {
								relationdetailMap.put(relationdetailid, "");
							}

							if (!relationmainid.equals("") && !relationdetailMainMap.containsKey(relationmainid)) {
								relationdetailMainMap.put(relationmainid, "");
							}
						}

						if (type == 0) {// 审核

							details = itemmonth + Common.getUpperUUIDString() + "','" + companyid + "','" + itemid + "','" + sdate + "'," + syear + "," + smonth + "," + dcount + "," + dtotal + ",'"
									+ batchno + "') on duplicate key update purchasecount=round(purchasecount+" + dcount + "," + countbit + "),purchasemoney=round(purchasemoney+" + dtotal + ","
									+ moneybit + ")";
							ps.addBatch(details);
						}

						details = detail + orderid + "','" + detailid + "','" + maindata.getString("purchaseorderid") + "','" + maindata.getString("originalbill") + "',"
								+ result.getInteger("goods_number") + ",'" + companyid + "','" + maindata.getString("operate_by") + "','" + maindata.getString("operate_time") + "','" + plandate
								+ "','" + itemid + "','" + customerid + "','" + houseid + "'," + price + "," + dcount + "," + dtotal + "," + result.getDoubleValue("tax") + ","
								+ result.getDoubleValue("taxrate") + "," + result.getDoubleValue("taxprice") + "," + result.getDoubleValue("taxmoney") + ",'141','" + result.getString("remark")
								+ "','" + status + "','" + loginuserid + "','" + loginUser + "', now() ,'" + loginuserid + "','" + loginUser + "',now(),'" + batchno + "'"
								+ (type == 0 ? ",'" + loginuserid + "','" + loginUser + "',now()" : "") + ",'" + relationdetailid + "','" + relationorderid + "','" + relationmainid + "',"
								+ relationtype + ")";

						ps.addBatch(details);

					}
				}
			} else {
				message = message + "没有商品明细数据，操作失败";
				save = false;
			}
			if (save) {
				String contractmsg = "";
				String contractid = "";
				String contractorderid = "";
				if (storecontractstatus.equals("new")) {
					contractid = Common.getUpperUUIDString();

					String[] fsdatearr = contractdata.getString("contractdate").split("-");
					String fsdate = fsdatearr[0] + "-" + fsdatearr[1] + "-01";
					String fbilldate = fsdatearr[0] + fsdatearr[1] + fsdatearr[2];

					contractorderid = Pdasave.getOrderidByparams(companyid, "storecontract", "store", fbilldate, conn);
					ps.addBatch("INSERT INTO storecontract (id, contractname, templateid, purchaseorderid, orderid, contractdate, Bcustomerid, Bcompanyname, Blinkphone, Bbankname, Bbanknumber, Baddress, address, Acompanyname, Alinkphone, Abankname, Abanknumber, Aaddress, content1, content2, companyid, operate_by, create_id, create_by, create_time, update_id, update_by, update_time, Afax, Bfax, stype,partAB) VALUES ('"
							+ contractid
							+ "','"
							+ contractdata.getString("contractname")
							+ "','"
							+ contractdata.getString("templateid")
							+ "','"
							+ contractdata.getString("purchaseorderid")
							+ "','"
							+ contractorderid
							+ "','"
							+ contractdata.getString("contractdate")
							+ "','"
							+ contractdata.getString("Bcustomerid")
							+ "','"
							+ contractdata.getString("Bcompanyname")
							+ "','"
							+ contractdata.getString("Blinkphone")
							+ "','"
							+ contractdata.getString("Bbankname")
							+ "','"
							+ contractdata.getString("Bbanknumber")
							+ "','"
							+ contractdata.getString("Baddress")
							+ "','"
							+ contractdata.getString("address")
							+ "','"
							+ contractdata.getString("Acompanyname")
							+ "','"
							+ contractdata.getString("Alinkphone")
							+ "','"
							+ contractdata.getString("Abankname")
							+ "','"
							+ contractdata.getString("Abanknumber")
							+ "','"
							+ contractdata.getString("Aaddress")
							+ "','"
							+ contractdata.getString("content1").replaceAll("'", "''")
							+ "','"
							+ contractdata.getString("content2").replaceAll("'", "''")
							+ "','"
							+ contractdata.getString("companyid")
							+ "','"
							+ contractdata.getString("operate_by")
							+ "','"
							+ loginuserid
							+ "','"
							+ loginUser
							+ "',now(),'"
							+ loginuserid
							+ "','"
							+ loginUser + "',now() " + ",'" + contractdata.getString("Afax") + "','" + contractdata.getString("Bfax") + "',1," + contractdata.getInteger("partAB") + ")");
					contractmsg = " 增加采购合同 " + contractorderid;
				} else if (storecontractstatus.equals("update")) {
					contractid = contractdata.getString("id");
					contractorderid = contractdata.getString("orderid");
					contractmsg = " 修改采购合同 " + contractorderid;
					ps.addBatch("update storecontract set orderid='" + contractorderid + "',contractname='" + contractdata.getString("contractname") + "',templateid='"
							+ contractdata.getString("templateid") + "',contractdate='" + contractdata.getString("contractdate") + "', Bcustomerid='" + contractdata.getString("Bcustomerid")
							+ "', Bcompanyname='" + contractdata.getString("Bcompanyname") + "', Blinkphone='" + contractdata.getString("Blinkphone") + "', Bbankname='"
							+ contractdata.getString("Bbankname") + "', Bbanknumber='" + contractdata.getString("Bbanknumber") + "', Baddress='" + contractdata.getString("Baddress") + "', address='"
							+ contractdata.getString("address") + "', Acompanyname='" + contractdata.getString("Acompanyname") + "', Alinkphone='" + contractdata.getString("Alinkphone")
							+ "', Abankname='" + contractdata.getString("Abankname") + "', Abanknumber='" + contractdata.getString("Abanknumber") + "', Aaddress='"
							+ contractdata.getString("Aaddress") + "', content1='" + contractdata.getString("content1").replaceAll("'", "''") + "', content2='"
							+ contractdata.getString("content2").replaceAll("'", "''") + "', companyid='" + contractdata.getString("companyid") + "', operate_by='"
							+ contractdata.getString("operate_by") + "', update_id='" + loginuserid + "', update_by='" + loginUser + "', update_time=now(),Afax='" + contractdata.getString("Afax")
							+ "',Bfax='" + contractdata.getString("Bfax") + "',partAB=" + contractdata.getInteger("partAB") + " where id='" + contractdata.getString("id") + "'");
				} else if (storecontractstatus.equals("delete")) {
					ps.addBatch("delete from storecontract where id='" + contractdata.getString("id") + "'");
					contractmsg = " 删除采购合同 " + maindata.getString("contractorderid");
					contractid = "";
					contractorderid = "";
				} else if (storecontractstatus.equals("nochange")) {
					contractid = contractdata.getString("id");
					contractorderid = contractdata.getString("orderid");
				}
				contractmsg = contractmsg + fcontractmsg;

				int changebilltype = Pdacommon.getDatalogBillChangefunc("purchaseorder", "");
				if (operate.equals("edit")) {// 编辑 更新
					ps.addBatch("update purchaseorder set orderid='" + orderid + "', originalbill='" + maindata.getString("originalbill") + "',operate_time='" + operate_time + "',plandate=null"
							+ ",operate_by='" + maindata.getString("operate_by") + "',houseid='" + houseid + "',customerid='" + customerid + "',count=" + count + ",total=" + total + ",totaltax="
							+ maindata.getDouble("totaltax") + ",totalmoney=" + maindata.getDouble("totalmoney") + ",remark='" + maindata.getString("remark") + "',status='" + status + "'"
							+ (type == 0 ? ",audit_id='" + loginuserid + "',audit_by='" + loginUser + "',audit_time=now()" : "") + ",update_id='" + loginuserid + "',update_by='" + loginUser
							+ "',update_time=now(),iproperty='" + maindata.getString("iproperty") + "',contractid='" + contractid + "',contractorderid='" + contractorderid
							+ "' where purchaseorderid='" + maindata.getString("purchaseorderid") + "'");

					ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
							+ companyid + "'," + changebilltype + ",'修改','" + maindata.getString("purchaseorderid") + "','单据编号：" + orderid + contractmsg + "','" + loginuserid + "','" + loginUser
							+ "',now())");

					ps.addBatch("update purchaseorderdetail d,purchaseorder s set d.create_id=s.create_id,d.create_by=s.create_by,d.create_time=s.create_time where d.purchaseorderid = s.purchaseorderid and s.purchaseorderid='"
							+ maindata.getString("purchaseorderid") + "'");

				} else {
					String main = "insert into purchaseorder (orderid,purchaseorderid,bill_type,originalbill,companyid,operate_time,operate_by,houseid,customerid,count,total,totaltax,totalmoney,remark,status,stockstatus,printing,outexcel,create_id,create_by,create_time,update_id,update_by,update_time,iproperty,contractid,contractorderid"
							+ (type == 0 ? ",audit_id,audit_by,audit_time" : "")
							+ ") VALUES ('"
							+ orderid
							+ "','"
							+ maindata.getString("purchaseorderid")
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
							+ maindata.getDouble("totaltax")
							+ ","
							+ maindata.getDouble("totalmoney")
							+ ",'"
							+ maindata.getString("remark")
							+ "','"
							+ status
							+ "','0',0,0,'"
							+ loginuserid
							+ "','"
							+ loginUser
							+ "',now(),'"
							+ loginuserid
							+ "','"
							+ loginUser
							+ "',now(),'"
							+ maindata.getString("iproperty")
							+ "','"
							+ contractid
							+ "','" + contractorderid + "'" + (type == 0 ? ",'" + loginuserid + "','" + loginUser + "',now()" : "") + ")";
					ps.addBatch(main);

					ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
							+ companyid + "'," + changebilltype + ",'" + (status == 0 ? "新增保存并审核" : "新增保存") + "','" + maindata.getString("purchaseorderid") + "','订单编号：" + orderid + contractmsg
							+ "','" + loginuserid + "','" + loginUser + "',now())");

				}

				// 计算采购申请已下单数量
				if (relationdetailMap.size() > 0) {
					for (Map.Entry<String, String> entry : relationdetailMap.entrySet()) {
						String rid = entry.getKey(); // 终止并完成状态不变，根据下单数量变更下单状态
						ps.addBatch("update purchasedetail p set p.ordercount=round(ifnull((select sum(s.count) from purchaseorderdetail s where  s.relationdetailid=p.detailid and s.status<>'2'  group by s.relationdetailid),0),"
								+ countbit + "),p.orderstatus=if(p.orderstatus=2,orderstatus,if(p.ordercount>=p.count,1,0)) where  p.detailid='" + rid + "' and p.status='1' ");
						// 2020-11-20 p.orderstatus=if(p.ordercount=2 更正为
						// p.orderstatus=if(p.orderstatus=2
					}
				}

				if (relationdetailMainMap.size() > 0) {
					for (Map.Entry<String, String> entry : relationdetailMainMap.entrySet()) {
						String rid = entry.getKey(); // 终止并完成状态不变，根据明细状态变更下单状态
						ps.addBatch("update purchase p set p.ordercount=round(ifnull((select sum(s.count) from purchaseorderdetail s where  s.relationmainid=p.purchaseid and s.status<>'2'  group by s.relationmainid),0),"
								+ countbit
								+ "),p.orderstatus=if(p.orderstatus=2,orderstatus,if((select count(*) from purchasedetail k where k.purchaseid=p.purchaseid and k.orderstatus=0)>0,0,1)) where p.purchaseid='"
								+ rid + "' and p.status='1'");
					}
				}

				if (ArelationdetailMap.size() > 0) {
					for (Map.Entry<String, String> entry : ArelationdetailMap.entrySet()) {
						String rid = entry.getKey(); // 终止并完成状态不变，根据下单数量变更下单状态
						ps.addBatch("update apply_materialdetail p set p.relationdetailid='',p.relationorderid='',p.relationmainid='',p.processstatus=1   where p.detailid='" + rid + "' ");
						ps.addBatch("update apply_materialdetail p,purchaseorderdetail pd set p.relationdetailid=pd.detailid,p.relationorderid=pd.orderid,p.relationmainid=pd.purchaseorderid,p.processstatus=2  where p.detailid='"
								+ rid + "' and p.status='1' and p.detailid = pd.relationdetailid and pd.relationtype=1 and pd.status<>2 ");
					}
				}

				if (ArelationdetailMainMap.size() > 0) {
					for (Map.Entry<String, String> entry : ArelationdetailMainMap.entrySet()) {
						String rid = entry.getKey(); // 终止并完成状态不变，根据明细状态变更下单状态
						ps.addBatch("update apply_material p set p.processstatus1=if((select count(*) from apply_materialdetail k where k.mainid=p.mainid and k.stype=332 and k.processstatus=1)>0,1,2)   where p.mainid='"
								+ rid + "' ");
					}
				}

				if (type == 3) {
					if (auditflowlist != null) {
						if (auditflowlist.getString("backbillflowid") != null && !auditflowlist.getString("backbillflowid").equals("")) {
							ps.addBatch("update t_billflow set back_type=1,back_time=now() where id='" + auditflowlist.getString("backbillflowid") + "'");
						}
						ps.addBatch("INSERT INTO `t_billflow` (`id`, `companyid`, `billid`,`orderid`, `auditflow_id`, `auditflowmain_id`, `flowfstatus`, `flownum`, `flowname`, `preflow`, `preflownum`, `preflowname`, `pass_id`, `pass_by`, `pass_time`, `pass_remark`, `receive_id`, `receive_by`,`pass_urgent`) VALUES "
								+ "('"
								+ Common.getUpperUUIDString()
								+ "', '"
								+ companyid
								+ "', '"
								+ maindata.getString("purchaseorderid")
								+ "', '"
								+ maindata.getString("orderid")
								+ "', '"
								+ auditflowlist.getString("auditflow_id")
								+ "', '"
								+ auditflowlist.getString("auditflowmain_id")
								+ "', 0, "
								+ auditflowlist.getInteger("newflownum")
								+ ", '"
								+ auditflowlist.getString("newflowname")
								+ "', '', "
								+ auditflowlist.getInteger("oldflownum")
								+ ", '"
								+ auditflowlist.getString("oldflowname")
								+ "', '"
								+ auditflowlist.getString("loginuserid")
								+ "', '"
								+ auditflowlist.getString("loginUser")
								+ "', now(), '"
								+ auditflowlist.getString("remark")
								+ "', '"
								+ auditflowlist.getString("receive_id") + "', '" + auditflowlist.getString("receive_by") + "'," + auditflowlist.getInteger("urgentselect") + ")");
					}
				}

				ps.executeBatch();
				conn.commit();
				conn.setAutoCommit(true);
				state = "1";
			}

		} catch (Exception e) {
			message = message + "操作失败，请稍后再试。";
			e.printStackTrace();
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

	// 保存采购订单信息
	public static JSONObject savePurchaseorderFunction_01(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject rt = new JSONObject();
		JSONObject maindata = JSONObject.parseObject(params.getString("maindata"));
		Integer type = params.getInteger("type");
		String loginUser = params.getString("loginUser");
		String loginuserid = params.getString("loginuserid");
		JSONArray detaildata = params.getJSONArray("detaildata");
		String operate = params.getString("operate");// edit 编辑单据
		String state = "0";
		String message = "";

		JSONObject auditflowlist = JSONObject.parseObject(params.getString("auditflowlist"));

		String companyid = maindata.getString("companyid");
		String houseid = maindata.getString("houseid");
		String customerid = maindata.getString("customerid");
		String operate_time = maindata.getString("operate_time");
		double count = maindata.getDoubleValue("count");
		double total = maindata.getDoubleValue("total");
		// int status = type == 0 ? 1 : 0;// type=0 为保存并审核 ，其他为保存 未审核

		int status = (type == 3 && auditflowlist != null ? 3 : (type == 0 ? 1 : 0));// type=0
																					// 为保存并审核
		// ，其他为保存 未审核

		// 合同保存功能 2019-12-09 begin
		String storecontractstatus = params.getString("storecontractstatus");
		JSONObject contractdata = storecontractstatus.equals("no") ? null : JSONObject.parseObject(params.getString("contractdata"));
		// System.out.println(storecontractstatus);
		String fcontractmsg = params.getString("fcontractmsg");
		// 合同保存功能 2019-12-09 end

		String[] sdatearr = operate_time.split("-");
		int syear = Integer.parseInt(sdatearr[0]);
		int smonth = Integer.parseInt(sdatearr[1]);
		String sdate = sdatearr[0] + "-" + sdatearr[1] + "-01";

		String billdate = sdatearr[0] + sdatearr[1] + sdatearr[2];

		try {
			int countbit = 0;
			int moneybit = 2;
			Table companytalbe = DataUtils.queryData(conn, "select pricebit,countbit,moneybit from s_company_config where company_id='" + companyid + "'", null, null, null, null);
			if (companytalbe.getRows().size() > 0) {
				countbit = companytalbe.getRows().get(0).getInteger("countbit");
				moneybit = companytalbe.getRows().get(0).getInteger("moneybit");
			}

			Statement ps = conn.createStatement();
			Statement ps1 = conn.createStatement();// 新物料增加
			conn.setAutoCommit(false);
			String detail = "insert into purchaseorderdetail (orderid,detailid,purchaseorderid,originalbill,goods_number,companyid,operate_by,operate_time,plandate,itemid,customerid,houseid,price,count,total,tax,taxrate,taxprice,taxmoney,stype,remark,status,create_id,create_by,create_time,update_id,update_by,update_time,batchno"
					+ (type == 0 ? ",audit_id,audit_by,audit_time" : "") + ",relationdetailid,relationorderid,relationmainid,relationtype) VALUES ('";
			String itemmonth = "insert into ordermonth (monthid,companyid,itemid,sdate,syear,smonth,purchasecount,purchasemoney,batchno) VALUES ('";

			String orderid = operate.equals("edit") ? maindata.getString("orderid") : Pdasave.getOrderidByparams(companyid, "purchaseorder", "", billdate, conn);
			int i = 0;
			String details = "";
			Map<String, String> relationdetailMap = new HashMap();// 缓存原数据绑定id
			Map<String, String> relationdetailMainMap = new HashMap();// 缓存原数据绑定id

			Map<String, String> ArelationdetailMap = new HashMap();// 缓存原数据绑定id
			Map<String, String> ArelationdetailMainMap = new HashMap();// 缓存原数据绑定id

			boolean save = true;
			if (operate.equals("edit")) {// 编辑 删除明细数据重新增加
				String fsql = "select status from purchaseorder where purchaseorderid='" + maindata.getString("purchaseorderid") + "'";
				Object cobject = DataUtils.getValueBySQL(conn, fsql, null);
				if (cobject == null) {
					save = false;
					message = "当前记录已删除，操作失败。";
					state = "2";
				} else {
					String fstatus = cobject.toString();
					if (fstatus.equals("0")) {
						// 获取原明细数据
						String olddetailsql = "select relationdetailid,relationmainid,relationtype from purchaseorderdetail where purchaseorderid='" + maindata.getString("purchaseorderid")
								+ "' group by relationdetailid,relationmainid";
						Table olddetailtable = DataUtils.queryData(conn, olddetailsql, null, null, null, null);
						if (olddetailtable.getRows().size() > 0) {
							for (Row row : olddetailtable.getRows()) {
								String relationdetailid = row.getString("relationdetailid");
								String relationmainid = row.getString("relationmainid");
								int relationtype = row.getInteger("relationtype");

								if (relationtype == 1) {
									ArelationdetailMap.put(relationdetailid, "");
									if (!ArelationdetailMainMap.containsKey(relationmainid)) {
										ArelationdetailMainMap.put(relationmainid, "");
									}
								} else {
									relationdetailMap.put(relationdetailid, "");
									if (!relationdetailMainMap.containsKey(relationmainid)) {
										relationdetailMainMap.put(relationmainid, "");
									}
								}
							}
						}
						ps.addBatch("delete from purchaseorderdetail where purchaseorderid='" + maindata.getString("purchaseorderid") + "'");
					} else if (fstatus.equals("3")) {
						save = false;
						message = "当前记录已提交审批，操作失败。";
						state = "2";
					} else if (fstatus.equals("1")) {
						save = false;
						message = "当前记录已审核，操作失败。";
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
						detailid = (detailid.equals("") ? Common.getUpperUUIDString() : detailid);
						String batchno = result.getString("batchno");
						String itemid = result.getString("itemid");
						double price = result.getDoubleValue("price");
						double dcount = result.getDoubleValue("count");
						double dtotal = result.getDoubleValue("total");
						String plandate = result.getString("plandate");

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

						String relationorderid = result.getString("relationorderid");
						String relationdetailid = result.getString("relationdetailid");
						String relationmainid = result.getString("relationmainid");

						Integer relationtype = result.getInteger("relationtype");
						relationtype = (relationtype == null ? 0 : relationtype);

						if (relationtype == 1) {
							if (!relationdetailid.equals("") && !ArelationdetailMap.containsKey(relationdetailid)) {
								ArelationdetailMap.put(relationdetailid, "");
							}

							if (!relationmainid.equals("") && !ArelationdetailMainMap.containsKey(relationmainid)) {
								ArelationdetailMainMap.put(relationmainid, "");
							}

							String findsql = "select sd.relationdetailid,sd.relationorderid from apply_materialdetail sd where sd.detailid='" + relationdetailid + "' and sd.status='1' ";

							Table ftable = DataUtils.queryData(conn, findsql, null, null, null, null);
							if (ftable.getRows().size() > 0) {
								String tempstr = ftable.getRows().get(0).getString("relationdetailid");
								if (!tempstr.equals("") && !tempstr.equals(detailid)) {
									message = "序号为" + result.getInteger("goods_number") + "的补料申请记录已采购(订单号：" + ftable.getRows().get(0).getString("relationorderid") + ")，不能重复操作！";
									save = false;
									break;
								}
							} else {
								message = "序号为" + result.getInteger("goods_number") + "的补料申请记录已作废，不能保存！";
								save = false;
								break;
							}
						} else {

							if (!relationdetailid.equals("") && !relationdetailMap.containsKey(relationdetailid)) {
								relationdetailMap.put(relationdetailid, "");
							}

							if (!relationmainid.equals("") && !relationdetailMainMap.containsKey(relationmainid)) {
								relationdetailMainMap.put(relationmainid, "");
							}
						}

						// 验证新商品是否已存在
						if (newmark == true) {// count(itemid) as itemcount,
							String iteminfochecksql = "select itemid,codeid,barcode from iteminfo where companyid='" + companyid + "' and itemname='" + itemname.replaceAll("'", "''")
									+ "' and sformat='" + sformat.replaceAll("'", "''") + "' and unit='" + unit + "' and property1='" + property1.replaceAll("'", "''") + "'" + " and property2 = '"
									+ property2.replaceAll("'", "''") + "' and property3 = '" + property3.replaceAll("'", "''") + "' and property4 = '" + property4.replaceAll("'", "''")
									+ "' and property5 = '" + property5.replaceAll("'", "''") + "'";
							Table itemTable = DataUtils.queryData(conn, iteminfochecksql, null, null, null, null);
							if (itemTable.getRows().size() > 0) {
								boolean hasinfo = false;
								// System.out.println("oldcodeid:" + codeid);
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
									itemid = itemTable.getRows().get(0).getString("itemid");
									codeid = itemTable.getRows().get(0).getString("codeid");
									barcode = itemTable.getRows().get(0).getString("barcode");
								}
								// System.out.println("oldcodeid:" + codeid);

								// 验证商品分类（已存在不处理，不同时更新商品基础里面分类:以最后选择的classid为标准更新）
								if (!classid.equals("")) {
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
								newmark = false;
							} else {
								// 新商品使用新itemid
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

						if (type == 0) {// 审核

							details = itemmonth + Common.getUpperUUIDString() + "','" + companyid + "','" + itemid + "','" + sdate + "'," + syear + "," + smonth + "," + dcount + "," + dtotal + ",'"
									+ batchno + "') on duplicate key update purchasecount=round(purchasecount+" + dcount + "," + countbit + "),purchasemoney=round(purchasemoney+" + dtotal + ","
									+ moneybit + ")";
							ps.addBatch(details);
						}
						details = detail + orderid + "','" + detailid + "','" + maindata.getString("purchaseorderid") + "','" + maindata.getString("originalbill") + "',"
								+ result.getInteger("goods_number") + ",'" + companyid + "','" + maindata.getString("operate_by") + "','" + maindata.getString("operate_time") + "','" + plandate
								+ "','" + itemid + "','" + customerid + "','" + houseid + "'," + price + "," + dcount + "," + dtotal + "," + result.getDoubleValue("tax") + ","
								+ result.getDoubleValue("taxrate") + "," + result.getDoubleValue("taxprice") + "," + result.getDoubleValue("taxmoney") + ",'141','" + result.getString("remark")
								+ "','" + status + "','" + loginuserid + "','" + loginUser + "', now() ,'" + loginuserid + "','" + loginUser + "',now(),'" + batchno + "'"
								+ (type == 0 ? ",'" + loginuserid + "','" + loginUser + "',now()" : "") + ",'" + relationdetailid + "','" + relationorderid + "','" + relationmainid + "',"
								+ relationtype + ")";

						ps.addBatch(details);

					}

				}
			} else {
				message = message + "没有商品明细数据，操作失败";
				save = false;
			}
			if (save) {
				String contractmsg = "";
				String contractid = "";
				String contractorderid = "";
				if (storecontractstatus.equals("new")) {
					contractid = Common.getUpperUUIDString();

					String[] fsdatearr = contractdata.getString("contractdate").split("-");
					String fsdate = fsdatearr[0] + "-" + fsdatearr[1] + "-01";
					String fbilldate = fsdatearr[0] + fsdatearr[1] + fsdatearr[2];

					contractorderid = Pdasave.getOrderidByparams(companyid, "storecontract", "store", fbilldate, conn);
					ps.addBatch("INSERT INTO storecontract (id, contractname, templateid, purchaseorderid, orderid, contractdate, Bcustomerid, Bcompanyname, Blinkphone, Bbankname, Bbanknumber, Baddress, address, Acompanyname, Alinkphone, Abankname, Abanknumber, Aaddress, content1, content2, companyid, operate_by, create_id, create_by, create_time, update_id, update_by, update_time ,Afax, Bfax, stype,partAB) VALUES ('"
							+ contractid
							+ "','"
							+ contractdata.getString("contractname")
							+ "','"
							+ contractdata.getString("templateid")
							+ "','"
							+ contractdata.getString("purchaseorderid")
							+ "','"
							+ contractorderid
							+ "','"
							+ contractdata.getString("contractdate")
							+ "','"
							+ contractdata.getString("Bcustomerid")
							+ "','"
							+ contractdata.getString("Bcompanyname")
							+ "','"
							+ contractdata.getString("Blinkphone")
							+ "','"
							+ contractdata.getString("Bbankname")
							+ "','"
							+ contractdata.getString("Bbanknumber")
							+ "','"
							+ contractdata.getString("Baddress")
							+ "','"
							+ contractdata.getString("address")
							+ "','"
							+ contractdata.getString("Acompanyname")
							+ "','"
							+ contractdata.getString("Alinkphone")
							+ "','"
							+ contractdata.getString("Abankname")
							+ "','"
							+ contractdata.getString("Abanknumber")
							+ "','"
							+ contractdata.getString("Aaddress")
							+ "','"
							+ contractdata.getString("content1").replaceAll("'", "''")
							+ "','"
							+ contractdata.getString("content2").replaceAll("'", "''")
							+ "','"
							+ contractdata.getString("companyid")
							+ "','"
							+ contractdata.getString("operate_by")
							+ "','"
							+ loginuserid
							+ "','"
							+ loginUser
							+ "',now(),'"
							+ loginuserid
							+ "','"
							+ loginUser + "',now() " + ",'" + contractdata.getString("Afax") + "','" + contractdata.getString("Bfax") + "',1," + contractdata.getInteger("partAB") + ")");

					contractmsg = " 增加采购合同 " + contractorderid;
				} else if (storecontractstatus.equals("update")) {
					contractid = contractdata.getString("id");
					contractorderid = contractdata.getString("orderid");
					contractmsg = " 修改采购合同 " + contractorderid;
					ps.addBatch("update storecontract set orderid='" + contractorderid + "',contractname='" + contractdata.getString("contractname") + "',templateid='"
							+ contractdata.getString("templateid") + "',contractdate='" + contractdata.getString("contractdate") + "', Bcustomerid='" + contractdata.getString("Bcustomerid")
							+ "', Bcompanyname='" + contractdata.getString("Bcompanyname") + "', Blinkphone='" + contractdata.getString("Blinkphone") + "', Bbankname='"
							+ contractdata.getString("Bbankname") + "', Bbanknumber='" + contractdata.getString("Bbanknumber") + "', Baddress='" + contractdata.getString("Baddress") + "', address='"
							+ contractdata.getString("address") + "', Acompanyname='" + contractdata.getString("Acompanyname") + "', Alinkphone='" + contractdata.getString("Alinkphone")
							+ "', Abankname='" + contractdata.getString("Abankname") + "', Abanknumber='" + contractdata.getString("Abanknumber") + "', Aaddress='"
							+ contractdata.getString("Aaddress") + "', content1='" + contractdata.getString("content1").replaceAll("'", "''") + "', content2='"
							+ contractdata.getString("content2").replaceAll("'", "''") + "', companyid='" + contractdata.getString("companyid") + "', operate_by='"
							+ contractdata.getString("operate_by") + "', update_id='" + loginuserid + "', update_by='" + loginUser + "', update_time=now(),Afax='" + contractdata.getString("Afax")
							+ "',Bfax='" + contractdata.getString("Bfax") + "',partAB=" + contractdata.getInteger("partAB") + " where id='" + contractdata.getString("id") + "'");
				} else if (storecontractstatus.equals("delete")) {
					ps.addBatch("delete from storecontract where id='" + contractdata.getString("id") + "'");
					contractmsg = " 删除采购合同 " + maindata.getString("contractorderid");
					contractid = "";
					contractorderid = "";
				} else if (storecontractstatus.equals("nochange")) {
					contractid = contractdata.getString("id");
					contractorderid = contractdata.getString("orderid");
				}
				contractmsg = contractmsg + fcontractmsg;

				int changebilltype = Pdacommon.getDatalogBillChangefunc("purchaseorder", "");
				if (operate.equals("edit")) {// 编辑 更新
					ps.addBatch("update purchaseorder set orderid='" + orderid + "', originalbill='" + maindata.getString("originalbill") + "',operate_time='" + operate_time + "',plandate=null"
							+ ",operate_by='" + maindata.getString("operate_by") + "',houseid='" + houseid + "',customerid='" + customerid + "',count=" + count + ",total=" + total + ",totaltax="
							+ maindata.getDouble("totaltax") + ",totalmoney=" + maindata.getDouble("totalmoney") + ",remark='" + maindata.getString("remark") + "',status='" + status + "'"
							+ (type == 0 ? ",audit_id='" + loginuserid + "',audit_by='" + loginUser + "',audit_time=now()" : "") + ",update_id='" + loginuserid + "',update_by='" + loginUser
							+ "',update_time=now(),iproperty='" + maindata.getString("iproperty") + "',contractid='" + contractid + "',contractorderid='" + contractorderid
							+ "' where purchaseorderid='" + maindata.getString("purchaseorderid") + "'");

					ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
							+ companyid + "'," + changebilltype + ",'修改','" + maindata.getString("purchaseorderid") + "','单据编号：" + orderid + contractmsg + "','" + loginuserid + "','" + loginUser
							+ "',now())");

					ps.addBatch("update purchaseorderdetail d,purchaseorder s set d.create_id=s.create_id,d.create_by=s.create_by,d.create_time=s.create_time where d.purchaseorderid = s.purchaseorderid and s.purchaseorderid='"
							+ maindata.getString("purchaseorderid") + "'");

				} else {
					String main = "insert into purchaseorder (orderid,purchaseorderid,bill_type,originalbill,companyid,operate_time,operate_by,houseid,customerid,count,total,totaltax,totalmoney,remark,status,stockstatus,printing,outexcel,create_id,create_by,create_time,update_id,update_by,update_time,iproperty,contractid,contractorderid"
							+ (type == 0 ? ",audit_id,audit_by,audit_time" : "")
							+ ") VALUES ('"
							+ orderid
							+ "','"
							+ maindata.getString("purchaseorderid")
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
							+ maindata.getDouble("totaltax")
							+ ","
							+ maindata.getDouble("totalmoney")
							+ ",'"
							+ maindata.getString("remark")
							+ "','"
							+ status
							+ "','0',0,0,'"
							+ loginuserid
							+ "','"
							+ loginUser
							+ "',now(),'"
							+ loginuserid
							+ "','"
							+ loginUser
							+ "',now(),'"
							+ maindata.getString("iproperty")
							+ "','"
							+ contractid
							+ "','" + contractorderid + "'" + (type == 0 ? ",'" + loginuserid + "','" + loginUser + "',now()" : "") + ")";
					ps.addBatch(main);

					ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
							+ companyid + "'," + changebilltype + ",'" + (status == 0 ? "新增保存并审核" : "新增保存") + "','" + maindata.getString("purchaseorderid") + "','订单编号：" + orderid + contractmsg
							+ "','" + loginuserid + "','" + loginUser + "',now())");

				}

				// 计算采购申请已下单数量
				if (relationdetailMap.size() > 0) {
					for (Map.Entry<String, String> entry : relationdetailMap.entrySet()) {
						String rid = entry.getKey(); // 终止并完成状态不变，根据下单数量变更下单状态
						ps.addBatch("update purchasedetail p set p.ordercount=round(ifnull((select sum(s.count) from purchaseorderdetail s where  s.relationdetailid=p.detailid and s.status<>'2'  group by s.relationdetailid),0),"
								+ countbit + "),p.orderstatus=if(p.orderstatus=2,orderstatus,if(p.ordercount>=p.count,1,0)) where  p.detailid='" + rid + "' and p.status='1' ");

					}
				}

				if (relationdetailMainMap.size() > 0) {
					for (Map.Entry<String, String> entry : relationdetailMainMap.entrySet()) {
						String rid = entry.getKey(); // 终止并完成状态不变，根据明细状态变更下单状态
						ps.addBatch("update purchase p set p.ordercount=round(ifnull((select sum(s.count) from purchaseorderdetail s where  s.relationmainid=p.purchaseid and s.status<>'2'  group by s.relationmainid),0),"
								+ countbit
								+ "),p.orderstatus=if(p.orderstatus=2,orderstatus,if((select count(*) from purchasedetail k where k.purchaseid=p.purchaseid and k.orderstatus=0)>0,0,1)) where p.purchaseid='"
								+ rid + "' and p.status='1'");
					}
				}

				if (ArelationdetailMap.size() > 0) {
					for (Map.Entry<String, String> entry : ArelationdetailMap.entrySet()) {
						String rid = entry.getKey(); // 终止并完成状态不变，根据下单数量变更下单状态
						ps.addBatch("update apply_materialdetail p set p.relationdetailid='',p.relationorderid='',p.relationmainid='',p.processstatus=1   where p.detailid='" + rid + "' ");
						ps.addBatch("update apply_materialdetail p,purchaseorderdetail pd set p.relationdetailid=pd.detailid,p.relationorderid=pd.orderid,p.relationmainid=pd.purchaseorderid,p.processstatus=2  where p.detailid='"
								+ rid + "' and p.status='1' and p.detailid = pd.relationdetailid and pd.relationtype=1 and pd.status<>2 ");
					}
				}

				if (ArelationdetailMainMap.size() > 0) {
					for (Map.Entry<String, String> entry : ArelationdetailMainMap.entrySet()) {
						String rid = entry.getKey(); // 终止并完成状态不变，根据明细状态变更下单状态
						ps.addBatch("update apply_material p set p.processstatus1=if((select count(*) from apply_materialdetail k where k.mainid=p.mainid and k.stype=332 and k.processstatus=1)>0,1,2)   where p.mainid='"
								+ rid + "' ");
					}
				}

				if (type == 3 && auditflowlist != null) {
					if (!auditflowlist.getString("backbillflowid").equals("")) {
						ps.addBatch("update t_billflow set back_type=1,back_time=now() where id='" + auditflowlist.getString("backbillflowid") + "'");
					}
					ps.addBatch("INSERT INTO `t_billflow` (`id`, `companyid`, `billid`,`orderid`, `auditflow_id`,`auditflowmain_id`, `flowfstatus`, `flownum`, `flowname`, `preflow`, `preflownum`, `preflowname`, `pass_id`, `pass_by`, `pass_time`, `pass_remark`, `receive_id`, `receive_by`,`pass_urgent`) VALUES "
							+ "('"
							+ Common.getUpperUUIDString()
							+ "', '"
							+ companyid
							+ "', '"
							+ maindata.getString("purchaseorderid")
							+ "', '"
							+ maindata.getString("orderid")
							+ "', '"
							+ auditflowlist.getString("auditflow_id")
							+ "', '"
							+ auditflowlist.getString("auditflowmain_id")
							+ "', 0, "
							+ auditflowlist.getInteger("newflownum")
							+ ", '"
							+ auditflowlist.getString("newflowname")
							+ "', '', "
							+ auditflowlist.getInteger("oldflownum")
							+ ", '"
							+ auditflowlist.getString("oldflowname")
							+ "', '"
							+ auditflowlist.getString("loginuserid")
							+ "', '"
							+ auditflowlist.getString("loginUser")
							+ "', now(), '"
							+ auditflowlist.getString("remark")
							+ "', '"
							+ auditflowlist.getString("receive_id") + "', '" + auditflowlist.getString("receive_by") + "'," + auditflowlist.getInteger("urgentselect") + ")");
				}

				ps.executeBatch();
				conn.commit();
				conn.setAutoCommit(true);
				state = "1";
			}

		} catch (Exception e) {
			message = message + "操作失败，请稍后再试。";
			e.printStackTrace();
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

	// 采购订单审核
	public static JSONObject auditPurchaseorderFunction(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject rt = new JSONObject();
		String orderid = params.getString("orderid");
		String companyid = params.getString("companyid");
		String mainid = params.getString("mainid").replaceAll("'", "");
		String loginuserid = params.getString("loginuserid");
		String loginUser = params.getString("loginUser");
		String operate_time = params.getString("operate_time");
		String houseid = params.getString("houseid");
		String[] arr = operate_time.split("-");
		String sdate = arr[0] + "-" + arr[1] + "-" + "01"; // 业务日期转成每个月1日来记录itemmonth数据

		int syear = Integer.parseInt(arr[0]);
		int smonth = Integer.parseInt(arr[1]);

		int countbit = params.getInteger("countbit");
		int moneybit = params.getInteger("moneybit");

		int audittype = params.getInteger("audittype");
		int stype = params.getInteger("stype");

		JSONObject auditflowlist = JSONObject.parseObject(params.getString("auditflowlist"));

		String state = "0";
		String message = "";
		Statement ps = null;

		try {
			if (mainid.length() != 32) {
				message = "含非法数据，操作失败!";
			} else {
				ps = conn.createStatement();
				conn.setAutoCommit(false);
				String sql = "select status from purchaseorder where purchaseorderid='" + mainid + "' ";
				Object cobject = DataUtils.getValueBySQL(conn, sql, null);
				if (cobject == null) {
					message = "已被删除";
					state = "2";
				} else {
					String fstatus = cobject.toString();
					if (fstatus.equals("1")) {
						message = "已被审核";
						state = "2";
					} else if (fstatus.equals("2")) {
						message = "已作废，不能进行审核操作";
						state = "2";
					} else {

						if (fstatus.equals("3") && stype == 2) {
							int agreedZJ = auditflowlist.getInteger("agreedZJ");
							if (auditflowlist.getInteger("curflowfstatus") == 2) {
								ps.addBatch("update t_billflow set back_type=" + agreedZJ + ",back_remark='" + auditflowlist.getString("remark") + "',back_time=now() where id='"
										+ auditflowlist.getString("curbillflow") + "'");
							} else {
								if (agreedZJ == 2) {
									if (!auditflowlist.getString("curbillflow").equals(auditflowlist.getString("back_flow"))) {
										ps.addBatch("update t_billflow set flowfstatus=" + agreedZJ + ",back_type=1,receive_remark='" + auditflowlist.getString("remark")
												+ "',receive_time=now() where id='" + auditflowlist.getString("curbillflow") + "'");
										ps.addBatch("update t_billflow set flowfstatus=" + agreedZJ + ",receive_remark='" + auditflowlist.getString("remark")
												+ "',receive_time=now(),back_send_remark='" + auditflowlist.getString("remark") + "',back_send_time=now(),back_urgent="
												+ auditflowlist.getInteger("urgentselect") + ",back_id='" + auditflowlist.getString("back_id") + "',back_by='" + auditflowlist.getString("back_by")
												+ "',backflownum=" + auditflowlist.getInteger("back_preflownum") + ",backflowname='" + auditflowlist.getString("backflowname") + "' where id='"
												+ auditflowlist.getString("back_flow") + "'");
									} else {
										ps.addBatch("update t_billflow set flowfstatus=" + agreedZJ + ",receive_remark='" + auditflowlist.getString("remark")
												+ "',receive_time=now(),back_send_remark='" + auditflowlist.getString("remark") + "',back_send_time=now(),back_urgent="
												+ auditflowlist.getInteger("urgentselect") + ",back_id='" + auditflowlist.getString("back_id") + "',back_by='" + auditflowlist.getString("back_by")
												+ "',backflownum=" + auditflowlist.getInteger("back_preflownum") + ",backflowname='" + auditflowlist.getString("backflowname") + "' where id='"
												+ auditflowlist.getString("curbillflow") + "'");

									}
								} else {
									ps.addBatch("update t_billflow set flowfstatus=" + agreedZJ + ",receive_remark='" + auditflowlist.getString("remark") + "',receive_time=now() where id='"
											+ auditflowlist.getString("curbillflow") + "'");
								}
							}
							if (agreedZJ == 1 && audittype != 1) {

								ps.addBatch("INSERT INTO `t_billflow` (`id`, `companyid`, `billid`,`orderid`, `auditflow_id`,`auditflowmain_id`, `flowfstatus`, `flownum`, `flowname`, `preflow`, `preflownum`, `preflowname`, `pass_id`, `pass_by`, `pass_time`, `pass_remark`, `receive_id`, `receive_by`,`pass_urgent`) VALUES "
										+ "('"
										+ Common.getUpperUUIDString()
										+ "', '"
										+ companyid
										+ "', '"
										+ auditflowlist.getString("mainid")
										+ "', '"
										+ orderid
										+ "', '"
										+ auditflowlist.getString("auditflow_id")
										+ "', '"
										+ auditflowlist.getString("auditflowmain_id")
										+ "', 0,"
										+ auditflowlist.getInteger("newflownum")
										+ ", '"
										+ auditflowlist.getString("newflowname")
										+ "', '"
										+ auditflowlist.getString("curbillflow")
										+ "', "
										+ auditflowlist.getInteger("oldflownum")
										+ ", '"
										+ auditflowlist.getString("oldflowname")
										+ "', '"
										+ auditflowlist.getString("loginuserid")
										+ "', '"
										+ auditflowlist.getString("loginUser")
										+ "', now(), '"
										+ auditflowlist.getString("remark")
										+ "', '"
										+ auditflowlist.getString("receive_id")
										+ "', '"
										+ auditflowlist.getString("receive_by")
										+ "',"
										+ auditflowlist.getInteger("urgentselect") + ")");
							} else if (agreedZJ == 2 && auditflowlist.getInteger("curflowfstatus") == 2) {
								ps.addBatch("update t_billflow set flowfstatus=" + agreedZJ + ",back_send_remark='" + auditflowlist.getString("remark") + "',back_id='"
										+ auditflowlist.getString("back_id") + "',back_by='" + auditflowlist.getString("back_by") + "',backflownum=" + auditflowlist.getInteger("back_preflownum")
										+ ",backflowname='" + auditflowlist.getString("backflowname") + "',back_send_time=now(),back_urgent=" + auditflowlist.getInteger("urgentselect")
										+ " where id='" + auditflowlist.getString("back_flow") + "'");

							}

							if (audittype == 0) {
								ps.addBatch("update purchaseorderdetail set status='0' where purchaseorderid='" + mainid + "' ");
								ps.addBatch("update purchaseorder set status='0' where purchaseorderid='" + mainid + "' ");
							}
						}
						if (houseid != null && !houseid.equals("")) {
							if ((fstatus.equals("0") && stype == 1) || (fstatus.equals("3") && stype == 2 && audittype == 1)) {

								String ordermonth = "insert into ordermonth (monthid,companyid,itemid,sdate,syear,smonth,purchasecount,purchasemoney,batchno) VALUES ('";

								// 2020-12-28
								// 验证仓库是否填写
								String fsql = "select sd.detailid,sd.count,sd.total,sd.itemid,sd.batchno from purchaseorderdetail sd  where sd.purchaseorderid='" + mainid
										+ "'  order by sd.goods_number asc ";
								Table table = DataUtils.queryData(conn, fsql, null, null, null, null);
								Iterator<Row> iteratordata = table.getRows().iterator();

								while (iteratordata.hasNext()) {
									Row info = iteratordata.next();

									double count = Double.parseDouble(info.getValue("count").toString());
									double total = Double.parseDouble(info.getValue("total").toString());
									String itemid = info.getString("itemid");
									String batchno = erpscan.save.Pdainvalid.transformSpecialInfo(info.getString("batchno"));
									String details = ordermonth + Common.getUpperUUIDString() + "','" + companyid + "','" + itemid + "','" + sdate + "'," + syear + "," + smonth + "," + count + ","
											+ total + ",'" + batchno + "') on duplicate key update purchasecount=round(purchasecount+" + count + "," + countbit
											+ "),purchasemoney=round(purchasemoney+" + total + "," + moneybit + ")";
									ps.addBatch(details);
									// System.out.println(details);
								}

								ps.addBatch("update purchaseorderdetail set status='1',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now(),audit_id='" + loginuserid
										+ "',audit_by='" + loginUser + "',audit_time=now() where purchaseorderid='" + mainid + "'");
								ps.addBatch("update purchaseorder set status='1',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now(),audit_id='" + loginuserid
										+ "',audit_by='" + loginUser + "',audit_time=now() where purchaseorderid='" + mainid + "' ");

								ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('"
										+ Common.getUpperUUIDString() + "','" + companyid + "'," + Pdacommon.getDatalogBillChangefunc("purchaseorder", "") + ",'审核','" + mainid + "','单据编号：" + orderid
										+ "','" + loginuserid + "','" + loginUser + "',now())");
							}

							ps.executeBatch();

							conn.commit();
							int count = ps.getUpdateCount();
							if (count == 0) {
								state = "3";
								message = "单据已审核。";
							} else {
								state = "1";
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								String str = sdf.format(new Date());
								rt.put("audit_time", str);

							}

							conn.setAutoCommit(true);

						} else {
							message = message + "当前单据[" + orderid + "]未选择仓库，请先选择仓库后再审核！";
						}
					}
				}
			}

		} catch (Exception e) {
			// e.printStackTrace();
			message = "审核操作失败，请稍后再试。";
			try {
				conn.rollback();
			} catch (Exception e1) {
				// e1.printStackTrace();
			}
		} finally {
			conn.close();
			ps.close();
		}
		rt.put("state", state);
		rt.put("message", message);
		return rt;
	}

	// 采购订单作废
	public static JSONObject invalidPurchaseorderFunction(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject rt = new JSONObject();
		String orderid = params.getString("orderid");
		String companyid = params.getString("companyid");
		String mainid = params.getString("mainid").replaceAll("'", "");
		JSONArray detailarr = params.getJSONArray("detailarr");
		String loginuserid = params.getString("loginuserid");
		String loginUser = params.getString("loginUser");
		String operate_time = params.getString("operate_time");
		String[] arr = operate_time.split("-");
		String sdate = arr[0] + "-" + arr[1] + "-" + "01"; // 业务日期转成每个月1日来记录itemmonth数据

		String state = "0";
		String message = "";

		int countbit = params.getInteger("countbit");
		int moneybit = params.getInteger("moneybit");

		try {
			if (mainid.length() != 32) {
				message = "含非法数据，操作失败!";
			} else {

				Statement ps = conn.createStatement();
				conn.setAutoCommit(false);

				Map<String, String> relationdetailMap = new HashMap();// 缓存原数据绑定id
				Map<String, String> relationdetailMainMap = new HashMap();// 缓存原数据绑定id

				String relativeinfo = "";

				String sqlstr = " select i.itemname, pod.count,pod.total,pod.batchno,pod.detailid,pod.relationdetailid,pod.relationmainid,pod.relationtype from purchaseorderdetail pod ,iteminfo i where pod.purchaseorderid='"
						+ mainid + "' and pod.itemid=i.itemid order by pod.goods_number asc ";

				Table tabledata = DataUtils.queryData(conn, sqlstr, null, null, null, null);

				Iterator<Row> iteratordata = tabledata.getRows().iterator();

				while (iteratordata.hasNext()) {
					Row info = iteratordata.next();
					double count = Double.parseDouble(info.getValue("count").toString());
					double total = Double.parseDouble(info.getValue("total").toString());
					String itemid = info.getString("itemid");
					String batchno = erpscan.save.Pdainvalid.transformSpecialInfo(info.getString("batchno"));
					String itemname = info.getString("itemname");
					String detailid = info.getString("detailid");
					String relationdetailid = info.getString("relationdetailid");
					String relationmainid = info.getString("relationmainid");
					int relationtype = info.getInteger("relationtype");

					if (relationtype == 0) {
						if (!relationdetailid.equals("") && !relationdetailMap.containsKey(relationdetailid)) {
							relationdetailMap.put(relationdetailid, "");
						}

						if (!relationmainid.equals("") && !relationdetailMainMap.containsKey(relationmainid)) {
							relationdetailMainMap.put(relationmainid, "");
						}
					}

					ps.addBatch("update ordermonth  set purchasecount=round(purchasecount-" + count + "," + countbit + "), purchasemoney=round(purchasemoney-" + total + "," + moneybit + ") where "
							+ " itemid='" + itemid + "' and batchno='" + batchno + "' and sdate='" + sdate + "'  and (select count(purchaseorderid) from purchaseorder where purchaseorderid='"
							+ mainid + "' and status='1' and incount=0)=1");

					// 与有效采购单关联不能作废
					String sql = "select count(detailid) from  storeindetail where status<>'2' and relationdetailid='" + detailid + "'";
					int scount = Integer.parseInt(DataUtils.getValueBySQL(conn, sql, null).toString());
					if (scount > 0) {
						relativeinfo = relativeinfo + (relativeinfo.equals("") ? "" : "、") + itemname;
					}

				}

				if (relativeinfo.equals("")) {

					ps.addBatch("update purchaseorderdetail set status='2',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now(),audit_id='" + loginuserid + "',audit_by='"
							+ loginUser + "',audit_time=now() where purchaseorderid='" + mainid + "' and status='1' ");
					ps.addBatch("update purchaseorder set status='2',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now(),audit_id='" + loginuserid + "',audit_by='"
							+ loginUser + "',audit_time=now() where purchaseorderid='" + mainid + "' and status='1' ");

					ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
							+ companyid + "'," + Pdacommon.getDatalogBillChangefunc("purchaseorder", "") + ",'作废','" + mainid + "','单据编号：" + orderid + "','" + loginuserid + "','" + loginUser
							+ "',now())");

					if (relationdetailMap.size() > 0) {
						for (Map.Entry<String, String> entry : relationdetailMap.entrySet()) {
							String rid = entry.getKey(); // 终止并完成状态不变，根据下单数量变更下单状态
							ps.addBatch("update purchasedetail p set p.ordercount=round(ifnull((select sum(s.count) from purchaseorderdetail s where  s.relationdetailid=p.detailid and s.status<>'2'  group by s.relationdetailid),0),"
									+ countbit + "),p.orderstatus=if(p.orderstatus=2,orderstatus,if(p.ordercount>=p.count,1,0)) where  p.detailid='" + rid + "' and p.status='1' ");

						}
					}

					if (relationdetailMainMap.size() > 0) {
						for (Map.Entry<String, String> entry : relationdetailMainMap.entrySet()) {
							String rid = entry.getKey(); // 终止并完成状态不变，根据明细状态变更下单状态
							ps.addBatch("update purchase p set p.ordercount=round(ifnull((select sum(s.count) from purchaseorderdetail s where  s.relationmainid=p.purchaseid and s.status<>'2'  group by s.relationmainid),0),"
									+ countbit
									+ "),p.orderstatus=if(p.orderstatus=2,orderstatus,if((select count(*) from purchasedetail k where k.purchaseid=p.purchaseid and k.orderstatus=0)>0,0,1)) where p.purchaseid='"
									+ rid + "' and p.status='1'");

						}
					}

					ps.addBatch("update apply_material p set p.processstatus1=1 where (select count(*) from apply_materialdetail where p.mainid=mainid and relationmainid='" + mainid
							+ "' and stype=332)>0 ");
					ps.addBatch("update apply_materialdetail p set p.relationdetailid='',p.relationorderid='',p.relationmainid='',p.processstatus=1   where p.relationmainid='" + mainid
							+ "' and p.stype=332 ");

					ps.executeBatch();
					conn.commit();
					// int count = ps.getUpdateCount();
					// if (count == 0) {
					// state = "3";
					// message = "单据已作废，操作失败。";
					// } else {
					state = "1";
					// }
				} else {
					state = "2";
					message = (relativeinfo.equals("") ? "" : "商品【" + relativeinfo + "】已入库，即与有效采购入库单相关联") + "，不能进行作废操作。";

				}

				conn.setAutoCommit(true);
			}

		} catch (Exception e) {
			// e.printStackTrace();
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

	// 修改采购订单入库状态
	public static JSONObject changePurchaseorderStockStatus(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject rt = new JSONObject();
		String orderid = params.getString("orderid");
		String companyid = params.getString("companyid");
		String mainid = params.getString("mainid").replaceAll("'", "");
		String loginuserid = params.getString("loginuserid");
		String loginUser = params.getString("loginUser");
		String oldstatus = params.getString("oldstatus");
		String newstatus = params.getString("newstatus");

		String state = "1";

		try {
			if (mainid.length() != 32) {
				state = "0";
			} else {
				Statement ps = conn.createStatement();
				conn.setAutoCommit(false);
				ps.addBatch("update purchaseorder set stockstatus='" + newstatus + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where purchaseorderid='" + mainid
						+ "' ");
				ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
						+ companyid + "','27','修改','" + mainid + "','订单编号：" + orderid + "的入库状态原[" + Pdacommon.getStockstatus(oldstatus) + "]变更为[" + Pdacommon.getStockstatus(newstatus) + "]','"
						+ loginuserid + "','" + loginUser + "',now())");

				ps.executeBatch();
				conn.commit();
				conn.setAutoCommit(true);
			}

		} catch (Exception e) {
			// e.printStackTrace();
			state = "0";
			try {
				conn.rollback();
			} catch (Exception e1) {
				// e1.printStackTrace();
			}
		} finally {
			conn.close();
		}
		rt.put("state", state);
		return rt;
	}

	// 保存采购申请单信息
	public static JSONObject savePurchaseFunction(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject rt = new JSONObject();
		JSONObject maindata = JSONObject.parseObject(params.getString("maindata"));
		Integer type = params.getInteger("type");
		String loginUser = params.getString("loginUser");
		String loginuserid = params.getString("loginuserid");
		JSONArray detaildata = params.getJSONArray("detaildata");
		String operate = params.getString("operate");// edit 编辑单据
		String state = "0";
		String message = "";

		JSONObject auditflowlist = JSONObject.parseObject(params.getString("auditflowlist"));

		String companyid = maindata.getString("companyid");
		// String houseid = maindata.getString("houseid");
		// String customerid = maindata.getString("customerid");
		String operate_time = maindata.getString("operate_time");
		// String plandate = maindata.getString("plandate");
		double count = maindata.getDoubleValue("count");
		// double total = maindata.getDoubleValue("total");
		int status = (type == 3 && auditflowlist != null ? 3 : (type == 0 ? 1 : 0));// type=0
																					// 为保存并审核
		// ，其他为保存 未审核

		// 合同保存功能 2019-12-09 begin
		/*
		 * String storecontractstatus = params.getString("storecontractstatus");
		 * JSONObject contractdata = storecontractstatus.equals("no") ? null :
		 * JSONObject.parseObject(params.getString("contractdata")); //
		 * System.out.println(storecontractstatus); String fcontractmsg =
		 * params.getString("fcontractmsg");
		 */
		// 合同保存功能 2019-12-09 end

		String[] sdatearr = operate_time.split("-");
		int syear = Integer.parseInt(sdatearr[0]);
		int smonth = Integer.parseInt(sdatearr[1]);
		String sdate = sdatearr[0] + "-" + sdatearr[1] + "-01";

		String billdate = sdatearr[0] + sdatearr[1] + sdatearr[2];

		try {
			int countbit = 0;
			int moneybit = 2;
			Table companytalbe = DataUtils.queryData(conn, "select pricebit,countbit,moneybit from s_company_config where company_id='" + companyid + "'", null, null, null, null);
			if (companytalbe.getRows().size() > 0) {
				countbit = companytalbe.getRows().get(0).getInteger("countbit");
				moneybit = companytalbe.getRows().get(0).getInteger("moneybit");
			}

			Statement ps = conn.createStatement();
			conn.setAutoCommit(false);
			String detail = "insert into purchasedetail (orderid,detailid,purchaseid,originalbill,purchasetype,goods_number,companyid,operate_by,operate_time,plandate,itemid,count,stype,remark,status,orderstatus,create_id,create_by,create_time,update_id,update_by,update_time,batchno"
					+ (type == 0 ? ",audit_id,audit_by,audit_time" : "") + ") VALUES ('";
			// String itemmonth =
			// "insert into ordermonth (monthid,companyid,itemid,sdate,syear,smonth,purchasecount,purchasemoney,batchno) VALUES ('";

			String orderid = operate.equals("edit") ? maindata.getString("orderid") : Pdasave.getOrderidByparams(companyid, "purchase", "", billdate, conn);
			int i = 0;
			String details = "";
			boolean save = true;
			if (operate.equals("edit")) {// 编辑 删除明细数据重新增加
				String fsql = "select status from purchase where purchaseid='" + maindata.getString("purchaseid") + "'";
				Object cobject = DataUtils.getValueBySQL(conn, fsql, null);
				if (cobject == null) {
					save = false;
					message = "当前记录已删除，操作失败。";
					state = "2";
				} else {
					String fstatus = cobject.toString();
					if (fstatus.equals("0")) {
						ps.addBatch("delete from purchasedetail where purchaseid='" + maindata.getString("purchaseid") + "'");
					} else if (fstatus.equals("3")) {
						save = false;
						message = "当前记录已提交审批，操作失败。";
						state = "2";
					} else if (fstatus.equals("1")) {
						save = false;
						message = "当前记录已审核，操作失败。";
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
						String dplandate = result.getString("plandate");

						details = detail + orderid + "','" + (detailid.equals("") ? Common.getUpperUUIDString() : detailid) + "','" + maindata.getString("purchaseid") + "','"
								+ maindata.getString("originalbill") + "','" + maindata.getString("purchasetype") + "'," + result.getInteger("goods_number") + ",'" + companyid + "','"
								+ maindata.getString("operate_by") + "','" + maindata.getString("operate_time") + "','" + dplandate + "','" + itemid + "'," + dcount + ",'281','"
								+ result.getString("remark") + "','" + status + "','0','" + (detailid.equals("") ? loginuserid : result.getString("create_id")) + "','"
								+ (detailid.equals("") ? loginUser : result.getString("create_by")) + "'," + (detailid.equals("") ? "now()" : "'" + result.getString("create_time") + "'") + ",'"
								+ loginuserid + "','" + loginUser + "',now(),'" + batchno + "'" + (type == 0 ? ",'" + loginuserid + "','" + loginUser + "',now()" : "") + ")";

						ps.addBatch(details);

					}
				}
			} else {
				message = message + "没有商品明细数据，操作失败";
				save = false;
			}
			if (save) {

				int changebilltype = Pdacommon.getDatalogBillChangefunc("purchase", "");
				if (operate.equals("edit")) {// 编辑 更新
					ps.addBatch("update purchase set orderid='" + orderid + "', purchasetype='" + maindata.getString("purchasetype") + "', originalbill='" + maindata.getString("originalbill")
							+ "',operate_time='" + operate_time + "',plandate=null,operate_by='" + maindata.getString("operate_by") + "',count=" + count + ",remark='" + maindata.getString("remark")
							+ "',status='" + status + "'" + (type == 0 ? ",audit_id='" + loginuserid + "',audit_by='" + loginUser + "',audit_time=now()" : "") + ",update_id='" + loginuserid
							+ "',update_by='" + loginUser + "',update_time=now(),iproperty='" + maindata.getString("iproperty") + "'" + " where purchaseid='" + maindata.getString("purchaseid") + "'");

					ps.addBatch("update purchasedetail d,purchase s set d.create_id=s.create_id,d.create_by=s.create_by,d.create_time=s.create_time where d.purchaseid = s.purchaseid and s.purchaseid='"
							+ maindata.getString("purchaseid") + "'");

				} else {
					String main = "insert into purchase (orderid,purchaseid,bill_type,originalbill,purchasetype,companyid,operate_time,plandate,operate_by,count,remark,status,orderstatus,printing,outexcel,create_id,create_by,create_time,update_id,update_by,update_time,iproperty"
							+ (type == 0 ? ",audit_id,audit_by,audit_time" : "")
							+ ") VALUES ('"
							+ orderid
							+ "','"
							+ maindata.getString("purchaseid")
							+ "','"
							+ maindata.getString("bill_type")
							+ "','"
							+ maindata.getString("originalbill")
							+ "','"
							+ maindata.getString("purchasetype")
							+ "','"
							+ companyid
							+ "','"
							+ operate_time
							+ "',null,'"
							+ maindata.getString("operate_by")
							+ "',"
							+ count
							+ ",'"
							+ maindata.getString("remark")
							+ "','"
							+ status
							+ "','0',0,0,'"
							+ loginuserid
							+ "','"
							+ loginUser
							+ "',now(),'"
							+ loginuserid + "','" + loginUser + "',now(),'" + maindata.getString("iproperty") + "'" + (type == 0 ? ",'" + loginuserid + "','" + loginUser + "',now()" : "") + ")";
					ps.addBatch(main);

					ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
							+ companyid + "'," + changebilltype + ",'" + (status == -1 ? "提交审批" : (status == 0 ? "新增保存并审核" : "新增保存")) + "','" + maindata.getString("purchaseorderid") + "','申请单编号："
							+ orderid + "','" + loginuserid + "','" + loginUser + "',now())");

				}

				if (type == 3 && auditflowlist != null) {
					if (!auditflowlist.getString("backbillflowid").equals("")) {
						ps.addBatch("update t_billflow set back_type=1,back_time=now() where id='" + auditflowlist.getString("backbillflowid") + "'");
					}
					ps.addBatch("INSERT INTO `t_billflow` (`id`, `companyid`, `billid`,`orderid`, `auditflow_id`,`auditflowmain_id`, `flowfstatus`, `flownum`, `flowname`, `preflow`, `preflownum`, `preflowname`, `pass_id`, `pass_by`, `pass_time`, `pass_remark`, `receive_id`, `receive_by`,`pass_urgent`) VALUES "
							+ "('"
							+ Common.getUpperUUIDString()
							+ "', '"
							+ companyid
							+ "', '"
							+ maindata.getString("purchaseid")
							+ "', '"
							+ maindata.getString("orderid")
							+ "', '"
							+ auditflowlist.getString("auditflow_id")
							+ "', '"
							+ auditflowlist.getString("auditflowmain_id")
							+ "', 0, "
							+ auditflowlist.getInteger("newflownum")
							+ ", '"
							+ auditflowlist.getString("newflowname")
							+ "', '', "
							+ auditflowlist.getInteger("oldflownum")
							+ ", '"
							+ auditflowlist.getString("oldflowname")
							+ "', '"
							+ auditflowlist.getString("loginuserid")
							+ "', '"
							+ auditflowlist.getString("loginUser")
							+ "', now(), '"
							+ auditflowlist.getString("remark")
							+ "', '"
							+ auditflowlist.getString("receive_id") + "', '" + auditflowlist.getString("receive_by") + "'," + auditflowlist.getInteger("urgentselect") + ")");
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

	public static JSONObject savePurchaseFunction_01(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject rt = new JSONObject();
		JSONObject maindata = JSONObject.parseObject(params.getString("maindata"));
		Integer type = params.getInteger("type");
		String loginUser = params.getString("loginUser");
		String loginuserid = params.getString("loginuserid");
		JSONArray detaildata = params.getJSONArray("detaildata");
		String operate = params.getString("operate");// edit 编辑单据
		String state = "0";
		String message = "";

		JSONObject auditflowlist = JSONObject.parseObject(params.getString("auditflowlist"));

		String companyid = maindata.getString("companyid");
		// String houseid = maindata.getString("houseid");
		// String customerid = maindata.getString("customerid");
		String operate_time = maindata.getString("operate_time");
		// String plandate = maindata.getString("plandate");
		double count = maindata.getDoubleValue("count");
		// double total = maindata.getDoubleValue("total");
		int status = (type == 3 && auditflowlist != null ? 3 : (type == 0 ? 1 : 0));// type=0
																					// 为保存并审核
		// ，其他为保存 未审核

		// 合同保存功能 2019-12-09 begin
		/*
		 * String storecontractstatus = params.getString("storecontractstatus");
		 * JSONObject contractdata = storecontractstatus.equals("no") ? null :
		 * JSONObject.parseObject(params.getString("contractdata")); //
		 * System.out.println(storecontractstatus); String fcontractmsg =
		 * params.getString("fcontractmsg");
		 */
		// 合同保存功能 2019-12-09 end

		String[] sdatearr = operate_time.split("-");
		int syear = Integer.parseInt(sdatearr[0]);
		int smonth = Integer.parseInt(sdatearr[1]);
		String sdate = sdatearr[0] + "-" + sdatearr[1] + "-01";

		String billdate = sdatearr[0] + sdatearr[1] + sdatearr[2];

		try {
			int countbit = 0;
			int moneybit = 2;
			Table companytalbe = DataUtils.queryData(conn, "select pricebit,countbit,moneybit from s_company_config where company_id='" + companyid + "'", null, null, null, null);
			if (companytalbe.getRows().size() > 0) {
				countbit = companytalbe.getRows().get(0).getInteger("countbit");
				moneybit = companytalbe.getRows().get(0).getInteger("moneybit");
			}

			Statement ps = conn.createStatement();
			Statement ps1 = conn.createStatement();// 新物料增加
			conn.setAutoCommit(false);
			String detail = "insert into purchasedetail (orderid,detailid,purchaseid,originalbill,purchasetype,goods_number,companyid,operate_by,operate_time,plandate,itemid,count,stype,remark,status,orderstatus,create_id,create_by,create_time,update_id,update_by,update_time,batchno"
					+ (type == 0 ? ",audit_id,audit_by,audit_time" : "") + ") VALUES ('";
			// String itemmonth =
			// "insert into ordermonth (monthid,companyid,itemid,sdate,syear,smonth,purchasecount,purchasemoney,batchno) VALUES ('";

			String orderid = operate.equals("edit") ? maindata.getString("orderid") : Pdasave.getOrderidByparams(companyid, "purchase", "", billdate, conn);
			int i = 0;
			String details = "";
			boolean save = true;
			if (operate.equals("edit")) {// 编辑 删除明细数据重新增加
				String fsql = "select status from purchase where purchaseid='" + maindata.getString("purchaseid") + "'";
				Object cobject = DataUtils.getValueBySQL(conn, fsql, null);
				if (cobject == null) {
					save = false;
					message = "当前记录已删除，操作失败。";
					state = "2";
				} else {
					String fstatus = cobject.toString();
					if (fstatus.equals("0")) {
						ps.addBatch("delete from purchasedetail where purchaseid='" + maindata.getString("purchaseid") + "'");
					} else if (fstatus.equals("3")) {
						save = false;
						message = "当前记录已提交审批，操作失败。";
						state = "2";
					} else if (fstatus.equals("1")) {
						save = false;
						message = "当前记录已审核，操作失败。";
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
						String dplandate = result.getString("plandate");

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
								// System.out.println("oldcodeid:" + codeid);
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
									itemid = itemTable.getRows().get(0).getString("itemid");
									codeid = itemTable.getRows().get(0).getString("codeid");
									barcode = itemTable.getRows().get(0).getString("barcode");
								}
								// System.out.println("oldcodeid:" + codeid);

								// 验证商品分类（已存在不处理，不同时更新商品基础里面分类:以最后选择的classid为标准更新）
								if (!classid.equals("")) {
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
								newmark = false;
							} else {
								// 新商品使用新itemid
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

							ps1.addBatch(iteminfosql);
							ps1.executeBatch();
						}

						details = detail + orderid + "','" + (detailid.equals("") ? Common.getUpperUUIDString() : detailid) + "','" + maindata.getString("purchaseid") + "','"
								+ maindata.getString("originalbill") + "','" + maindata.getString("purchasetype") + "'," + result.getInteger("goods_number") + ",'" + companyid + "','"
								+ maindata.getString("operate_by") + "','" + maindata.getString("operate_time") + "','" + dplandate + "','" + itemid + "'," + dcount + ",'281','"
								+ result.getString("remark") + "','" + status + "','0','" + (detailid.equals("") ? loginuserid : result.getString("create_id")) + "','"
								+ (detailid.equals("") ? loginUser : result.getString("create_by")) + "'," + (detailid.equals("") ? "now()" : "'" + result.getString("create_time") + "'") + ",'"
								+ loginuserid + "','" + loginUser + "',now(),'" + batchno + "'" + (type == 0 ? ",'" + loginuserid + "','" + loginUser + "',now()" : "") + ")";

						ps.addBatch(details);

					}
				}
			} else {
				message = message + "没有商品明细数据，操作失败";
				save = false;
			}
			if (save) {

				int changebilltype = Pdacommon.getDatalogBillChangefunc("purchase", "");
				if (operate.equals("edit")) {// 编辑 更新
					ps.addBatch("update purchase set orderid='" + orderid + "', purchasetype='" + maindata.getString("purchasetype") + "', originalbill='" + maindata.getString("originalbill")
							+ "',operate_time='" + operate_time + "',plandate=null,operate_by='" + maindata.getString("operate_by") + "',count=" + count + ",remark='" + maindata.getString("remark")
							+ "',status='" + status + "'" + (type == 0 ? ",audit_id='" + loginuserid + "',audit_by='" + loginUser + "',audit_time=now()" : "") + ",update_id='" + loginuserid
							+ "',update_by='" + loginUser + "',update_time=now(),iproperty='" + maindata.getString("iproperty") + "'" + " where purchaseid='" + maindata.getString("purchaseid") + "'");

					ps.addBatch("update purchasedetail d,purchase s set d.create_id=s.create_id,d.create_by=s.create_by,d.create_time=s.create_time where d.purchaseid = s.purchaseid and s.purchaseid='"
							+ maindata.getString("purchaseid") + "'");

				} else {
					String main = "insert into purchase (orderid,purchaseid,bill_type,originalbill,purchasetype,companyid,operate_time,plandate,operate_by,count,remark,status,orderstatus,printing,outexcel,create_id,create_by,create_time,update_id,update_by,update_time,iproperty"
							+ (type == 0 ? ",audit_id,audit_by,audit_time" : "")
							+ ") VALUES ('"
							+ orderid
							+ "','"
							+ maindata.getString("purchaseid")
							+ "','"
							+ maindata.getString("bill_type")
							+ "','"
							+ maindata.getString("originalbill")
							+ "','"
							+ maindata.getString("purchasetype")
							+ "','"
							+ companyid
							+ "','"
							+ operate_time
							+ "',null,'"
							+ maindata.getString("operate_by")
							+ "',"
							+ count
							+ ",'"
							+ maindata.getString("remark")
							+ "','"
							+ status
							+ "','0',0,0,'"
							+ loginuserid
							+ "','"
							+ loginUser
							+ "',now(),'"
							+ loginuserid + "','" + loginUser + "',now(),'" + maindata.getString("iproperty") + "'" + (type == 0 ? ",'" + loginuserid + "','" + loginUser + "',now()" : "") + ")";
					ps.addBatch(main);

					ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
							+ companyid + "'," + changebilltype + ",'" + (status == -1 ? "提交审批" : (status == 0 ? "新增保存并审核" : "新增保存")) + "','" + maindata.getString("purchaseorderid") + "','申请单编号："
							+ orderid + "','" + loginuserid + "','" + loginUser + "',now())");

				}

				if (type == 3 && auditflowlist != null) {
					if (!auditflowlist.getString("backbillflowid").equals("")) {
						ps.addBatch("update t_billflow set back_type=1,back_time=now() where id='" + auditflowlist.getString("backbillflowid") + "'");
					}
					ps.addBatch("INSERT INTO `t_billflow` (`id`, `companyid`, `billid`,`orderid`, `auditflow_id`,`auditflowmain_id`, `flowfstatus`, `flownum`, `flowname`, `preflow`, `preflownum`, `preflowname`, `pass_id`, `pass_by`, `pass_time`, `pass_remark`, `receive_id`, `receive_by`,`pass_urgent`) VALUES "
							+ "('"
							+ Common.getUpperUUIDString()
							+ "', '"
							+ companyid
							+ "', '"
							+ maindata.getString("purchaseid")
							+ "', '"
							+ maindata.getString("orderid")
							+ "', '"
							+ auditflowlist.getString("auditflow_id")
							+ "', '"
							+ auditflowlist.getString("auditflowmain_id")
							+ "', 0, "
							+ auditflowlist.getInteger("newflownum")
							+ ", '"
							+ auditflowlist.getString("newflowname")
							+ "', '', "
							+ auditflowlist.getInteger("oldflownum")
							+ ", '"
							+ auditflowlist.getString("oldflowname")
							+ "', '"
							+ auditflowlist.getString("loginuserid")
							+ "', '"
							+ auditflowlist.getString("loginUser")
							+ "', now(), '"
							+ auditflowlist.getString("remark")
							+ "', '"
							+ auditflowlist.getString("receive_id") + "', '" + auditflowlist.getString("receive_by") + "'," + auditflowlist.getInteger("urgentselect") + ")");
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

	// 采购申请单审核
	public static JSONObject auditPurchaseFunction(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject rt = new JSONObject();
		String orderid = params.getString("orderid");
		String companyid = params.getString("companyid");
		String mainid = params.getString("mainid").replaceAll("'", "");
		String loginuserid = params.getString("loginuserid");
		String loginUser = params.getString("loginUser");
		String operate_time = params.getString("operate_time");
		String[] arr = operate_time.split("-");
		String sdate = arr[0] + "-" + arr[1] + "-" + "01"; // 业务日期转成每个月1日来记录itemmonth数据

		int syear = Integer.parseInt(arr[0]);
		int smonth = Integer.parseInt(arr[1]);

		int audittype = params.getInteger("audittype");
		int stype = params.getInteger("stype");

		JSONObject auditflowlist = JSONObject.parseObject(params.getString("auditflowlist"));

		String state = "0";
		String message = "";

		try {
			if (mainid.length() != 32) {
				message = "含非法数据，操作失败!";
				state = "2";
			} else {

				Statement ps = conn.createStatement();
				conn.setAutoCommit(false);
				String sql = "select status from purchase where purchaseid='" + mainid + "' ";

				Object cobject = DataUtils.getValueBySQL(conn, sql, null);
				if (cobject == null) {
					message = "已被删除";
					state = "2";
				} else {
					String fstatus = cobject.toString();
					if (fstatus.equals("1")) {
						message = "已被审核";
						state = "2";
					} else if (fstatus.equals("2")) {
						message = "已作废，不能进行审核操作";
						state = "2";
					} else {

						if (fstatus.equals("3") && stype == 2) {
							int agreedZJ = auditflowlist.getInteger("agreedZJ");
							if (auditflowlist.getInteger("curflowfstatus") == 2) {
								ps.addBatch("update t_billflow set back_type=" + agreedZJ + ",back_remark='" + auditflowlist.getString("remark") + "',back_time=now() where id='"
										+ auditflowlist.getString("curbillflow") + "'");
							} else {
								if (agreedZJ == 2) {
									if (!auditflowlist.getString("curbillflow").equals(auditflowlist.getString("back_flow"))) {
										ps.addBatch("update t_billflow set flowfstatus=" + agreedZJ + ",back_type=1,receive_remark='" + auditflowlist.getString("remark")
												+ "',receive_time=now() where id='" + auditflowlist.getString("curbillflow") + "'");
										ps.addBatch("update t_billflow set flowfstatus=" + agreedZJ + ",receive_remark='" + auditflowlist.getString("remark")
												+ "',receive_time=now(),back_send_remark='" + auditflowlist.getString("remark") + "',back_send_time=now(),back_urgent="
												+ auditflowlist.getInteger("urgentselect") + ",back_id='" + auditflowlist.getString("back_id") + "',back_by='" + auditflowlist.getString("back_by")
												+ "',backflownum=" + auditflowlist.getInteger("back_preflownum") + ",backflowname='" + auditflowlist.getString("backflowname") + "' where id='"
												+ auditflowlist.getString("back_flow") + "'");
									} else {
										ps.addBatch("update t_billflow set flowfstatus=" + agreedZJ + ",receive_remark='" + auditflowlist.getString("remark")
												+ "',receive_time=now(),back_send_remark='" + auditflowlist.getString("remark") + "',back_send_time=now(),back_urgent="
												+ auditflowlist.getInteger("urgentselect") + ",back_id='" + auditflowlist.getString("back_id") + "',back_by='" + auditflowlist.getString("back_by")
												+ "',backflownum=" + auditflowlist.getInteger("back_preflownum") + ",backflowname='" + auditflowlist.getString("backflowname") + "' where id='"
												+ auditflowlist.getString("curbillflow") + "'");

									}
								} else {
									ps.addBatch("update t_billflow set flowfstatus=" + agreedZJ + ",receive_remark='" + auditflowlist.getString("remark") + "',receive_time=now() where id='"
											+ auditflowlist.getString("curbillflow") + "'");
								}
							}
							if (agreedZJ == 1 && audittype != 1) {

								ps.addBatch("INSERT INTO `t_billflow` (`id`, `companyid`, `billid`,`orderid`, `auditflow_id`,`auditflowmain_id`, `flowfstatus`, `flownum`, `flowname`, `preflow`, `preflownum`, `preflowname`, `pass_id`, `pass_by`, `pass_time`, `pass_remark`, `receive_id`, `receive_by`,`pass_urgent`) VALUES "
										+ "('"
										+ Common.getUpperUUIDString()
										+ "', '"
										+ companyid
										+ "', '"
										+ auditflowlist.getString("mainid")
										+ "', '"
										+ orderid
										+ "', '"
										+ auditflowlist.getString("auditflow_id")
										+ "', '"
										+ auditflowlist.getString("auditflowmain_id")
										+ "', 0,"
										+ auditflowlist.getInteger("newflownum")
										+ ", '"
										+ auditflowlist.getString("newflowname")
										+ "', '"
										+ auditflowlist.getString("curbillflow")
										+ "', "
										+ auditflowlist.getInteger("oldflownum")
										+ ", '"
										+ auditflowlist.getString("oldflowname")
										+ "', '"
										+ auditflowlist.getString("loginuserid")
										+ "', '"
										+ auditflowlist.getString("loginUser")
										+ "', now(), '"
										+ auditflowlist.getString("remark")
										+ "', '"
										+ auditflowlist.getString("receive_id")
										+ "', '"
										+ auditflowlist.getString("receive_by")
										+ "',"
										+ auditflowlist.getInteger("urgentselect") + ")");
							} else if (agreedZJ == 2 && auditflowlist.getInteger("curflowfstatus") == 2) {
								ps.addBatch("update t_billflow set flowfstatus=" + agreedZJ + ",back_send_remark='" + auditflowlist.getString("remark") + "',back_id='"
										+ auditflowlist.getString("back_id") + "',back_by='" + auditflowlist.getString("back_by") + "',backflownum=" + auditflowlist.getInteger("back_preflownum")
										+ ",backflowname='" + auditflowlist.getString("backflowname") + "',back_send_time=now(),back_urgent=" + auditflowlist.getInteger("urgentselect")
										+ " where id='" + auditflowlist.getString("back_flow") + "'");

							}

							if (audittype == 0) {
								ps.addBatch("update purchasedetail set status='0' where purchaseid='" + mainid + "' ");
								ps.addBatch("update purchase set status='0' where purchaseid='" + mainid + "' ");
							}
						}

						if ((fstatus.equals("0") && stype == 1) || (fstatus.equals("3") && stype == 2 && audittype == 1)) {

							ps.addBatch("update purchasedetail set status='1',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now(),audit_id='" + loginuserid
									+ "',audit_by='" + loginUser + "',audit_time=now() where purchaseid='" + mainid + "' ");
							ps.addBatch("update purchase set status='1',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now(),audit_id='" + loginuserid + "',audit_by='"
									+ loginUser + "',audit_time=now() where purchaseid='" + mainid + "' ");

							ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString()
									+ "','" + companyid + "'," + Pdacommon.getDatalogBillChangefunc("purchase", "") + ",'审核','" + mainid + "','单据编号：" + orderid + "','" + loginuserid + "','"
									+ loginUser + "',now())");
						}
						ps.executeBatch();

						conn.commit();
						int count = ps.getUpdateCount();
						if (count == 0) {
							state = "3";
							message = "单据已审核。";
						} else {
							state = "1";
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							String str = sdf.format(new Date());
							rt.put("audit_time", str);

						}
					}
				}

				conn.setAutoCommit(true);
			}

		} catch (Exception e) {
			// e.printStackTrace();
			message = "审核操作失败，请稍后再试。";
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

	// 采购订单作废
	public static JSONObject invalidPurchaseFunction(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject rt = new JSONObject();
		String orderid = params.getString("orderid");
		String companyid = params.getString("companyid");
		String mainid = params.getString("mainid").replaceAll("'", "");
		JSONArray detailarr = params.getJSONArray("detailarr");
		String loginuserid = params.getString("loginuserid");
		String loginUser = params.getString("loginUser");
		String operate_time = params.getString("operate_time");
		String[] arr = operate_time.split("-");
		String sdate = arr[0] + "-" + arr[1] + "-" + "01"; // 业务日期转成每个月1日来记录itemmonth数据

		String state = "0";
		String message = "";

		try {

			if (mainid.length() != 32) {
				message = "含非法数据，操作失败!";
			} else {
				int countbit = 0;
				int moneybit = 2;
				Table companytalbe = DataUtils.queryData(conn, "select pricebit,countbit,moneybit from s_company_config where company_id='" + companyid + "'", null, null, null, null);
				if (companytalbe.getRows().size() > 0) {
					countbit = companytalbe.getRows().get(0).getInteger("countbit");
					moneybit = companytalbe.getRows().get(0).getInteger("moneybit");
				}

				Statement ps = conn.createStatement();
				conn.setAutoCommit(false);

				String relativeinfo = "";
				if (detailarr.size() > 0) {
					for (int i = 0; i < detailarr.size(); i++) {
						JSONObject darr = JSONObject.parseObject(detailarr.getString(i));
						double count = darr.getDouble("count");
						// double total = darr.getDouble("total");
						String itemid = darr.getString("itemid");
						String batchno = darr.getString("batchno");
						String itemname = darr.getString("itemname");
						String detailid = darr.getString("detailid");

						// ps.addBatch("update ordermonth  set purchasecount=round(purchasecount-"
						// + count + "," + countbit +
						// "), purchasemoney=round(purchasemoney-" + total + ","
						// +
						// moneybit + ") where "
						// + " itemid='" + itemid + "' and batchno='" + batchno
						// +
						// "' and sdate='" + sdate +
						// "'  and (select count(purchaseorderid) from purchaseorder where purchaseorderid='"
						// + mainid + "' and status='1' and incount=0)=1");

						// 与有效采购单关联不能作废
						String sql = "select count(detailid) from  purchaseorderdetail where status in ('0','1') and relationdetailid='" + detailid + "'";
						int scount = Integer.parseInt(DataUtils.getValueBySQL(conn, sql, null).toString());
						if (scount > 0) {
							relativeinfo = relativeinfo + (relativeinfo.equals("") ? "" : "、") + itemname;
						}
					}
				}

				if (relativeinfo.equals("")) {

					ps.addBatch("update purchasedetail set status='2',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now(),audit_id='" + loginuserid + "',audit_by='"
							+ loginUser + "',audit_time=now() where purchaseid='" + mainid + "' and status='1' ");
					ps.addBatch("update purchase set status='2',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now(),audit_id='" + loginuserid + "',audit_by='" + loginUser
							+ "',audit_time=now() where purchaseid='" + mainid + "' and status='1' ");
					// 2020-11-18 purchaseorder 改为 purchase

					ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
							+ companyid + "'," + Pdacommon.getDatalogBillChangefunc("purchase", "") + ",'作废','" + mainid + "','单据编号：" + orderid + "','" + loginuserid + "','" + loginUser + "',now())");

					ps.executeBatch();
					conn.commit();
					int count = ps.getUpdateCount();
					if (count == 0) {
						state = "3";
						message = "单据已作废，操作失败。";
					} else {
						state = "1";
					}
				} else {
					state = "2";
					message = (relativeinfo.equals("") ? "" : "商品【" + relativeinfo + "】已下单，即与有效采购订单相关联") + "，不能进行作废操作。";

				}

				conn.setAutoCommit(true);
			}

		} catch (Exception e) {
			// e.printStackTrace();
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

	// 修改采购申请单下单状态
	public static JSONObject changePurchaseorderStatus(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject rt = new JSONObject();
		String orderid = params.getString("orderid");
		String companyid = params.getString("companyid");
		String mainid = params.getString("mainid").replaceAll("'", "");
		String loginuserid = params.getString("loginuserid");
		String loginUser = params.getString("loginUser");
		String oldstatus = params.getString("oldstatus");
		String newstatus = params.getString("newstatus");

		String state = "1";

		try {
			if (mainid.length() != 32) {
				state = "0";
			} else {
				Statement ps = conn.createStatement();
				conn.setAutoCommit(false);
				ps.addBatch("update purchase set orderstatus='" + newstatus + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where purchaseid='" + mainid + "' ");
				// 明细
				ps.addBatch("update purchasedetail set orderstatus='" + newstatus + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where purchaseid='" + mainid
						+ "' ");
				ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
						+ companyid + "','49','修改','" + mainid + "','订单编号：" + orderid + "的下单状态原[" + Pdacommon.getorderstatus(oldstatus) + "]变更为[" + Pdacommon.getorderstatus(newstatus) + "]','"
						+ loginuserid + "','" + loginUser + "',now())");

				ps.executeBatch();
				conn.commit();
				conn.setAutoCommit(true);
			}

		} catch (Exception e) {
			// e.printStackTrace();
			state = "0";
			try {
				conn.rollback();
			} catch (Exception e1) {
				// e1.printStackTrace();
			}
		} finally {
			conn.close();
		}
		rt.put("state", state);
		return rt;
	}

	// 修改采购申请单(明细)下单状态
	public static JSONObject changePurchasedetailorderStatus(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject rt = new JSONObject();
		String orderid = params.getString("orderid");
		String companyid = params.getString("companyid");
		String mainid = params.getString("mainid").replaceAll("'", "");
		String detailid = params.getString("detailid");
		int goods_number = params.getIntValue("goods_number");
		String loginuserid = params.getString("loginuserid");
		String loginUser = params.getString("loginUser");
		String oldstatus = params.getString("oldstatus");
		String newstatus = params.getString("newstatus");

		String state = "1";

		try {
			if (mainid.length() != 32) {
				state = "0";
			} else {
				Statement ps = conn.createStatement();
				conn.setAutoCommit(false);
				// 明细
				ps.addBatch("update purchasedetail set orderstatus='" + newstatus + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where detailid='" + detailid
						+ "' ");
				// 主表

				ps.addBatch("update purchase p set p.orderstatus=if(p.orderstatus=2,orderstatus,if((select count(*) from purchasedetail k where k.purchaseid=p.purchaseid and k.orderstatus=0)>0,0,1)),update_id='"
						+ loginuserid + "',update_by='" + loginUser + "',update_time=now() where p.purchaseid='" + mainid + "' ");

				// ps.addBatch("update purchase set orderstatus='" + newstatus +
				// "',update_id='" + loginuserid + "',update_by='" + loginUser +
				// "',update_time=now() where purchaseid='" + mainid
				// +
				// "' and (select count(detailid) from purchasedetail where purchaseid='"
				// + mainid +
				// "' and status='1') = (select count(detailid) from purchasedetail where purchaseid='"
				// + mainid
				// + "' and orderstatus='" + newstatus +
				// "' and status='1') and status='1'");

				ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
						+ companyid + "','49','修改明细下单状态','" + mainid + "','订单编号：" + orderid + "序号为《" + goods_number + "》的明细下单状态原[" + Pdacommon.getorderstatus(oldstatus) + "]变更为["
						+ Pdacommon.getorderstatus(newstatus) + "]','" + loginuserid + "','" + loginUser + "',now())");

				ps.executeBatch();
				conn.commit();
				conn.setAutoCommit(true);
			}

		} catch (Exception e) {
			// e.printStackTrace();
			state = "0";
			try {
				conn.rollback();
			} catch (Exception e1) {
				// e1.printStackTrace();
			}
		} finally {
			conn.close();
		}
		rt.put("state", state);
		return rt;
	}

	// 保存销售订单信息
	public static JSONObject saveSalesorderFunction(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject rt = new JSONObject();
		JSONObject maindata = JSONObject.parseObject(params.getString("maindata"));
		Integer type = params.getInteger("type");
		String loginUser = params.getString("loginUser");
		String loginuserid = params.getString("loginuserid");
		JSONArray detaildata = params.getJSONArray("detaildata");
		String operate = params.getString("operate");// edit 编辑单据
		String state = "0";
		String message = "";

		JSONObject auditflowlist = JSONObject.parseObject(params.getString("auditflowlist"));

		String companyid = maindata.getString("companyid");
		String houseid = maindata.getString("houseid");
		String customerid = maindata.getString("customerid");
		String operate_time = maindata.getString("operate_time");
		double count = maindata.getDoubleValue("count");
		double total = maindata.getDoubleValue("total");
		int status = (type == 3 && auditflowlist != null ? 3 : (type == 0 ? 1 : 0));// type=0
																					// 为保存并审核
		// ，其他为保存 未审核

		// 合同保存功能 2020-11-10 begin
		String storecontractstatus = params.getString("storecontractstatus");
		JSONObject contractdata = storecontractstatus.equals("no") ? null : JSONObject.parseObject(params.getString("contractdata"));
		// System.out.println(storecontractstatus);
		String fcontractmsg = params.getString("fcontractmsg");
		// 合同保存功能 2020-11-10 end

		String[] sdatearr = operate_time.split("-");
		int syear = Integer.parseInt(sdatearr[0]);
		int smonth = Integer.parseInt(sdatearr[1]);
		String sdate = sdatearr[0] + "-" + sdatearr[1] + "-01";

		String billdate = sdatearr[0] + sdatearr[1] + sdatearr[2];

		String originalbill = maindata.getString("originalbill");

		try {
			int countbit = 0;
			int moneybit = 2;
			int salesordernameset = 0;
			String salesordernamerule = "";

			Table companytalbe = DataUtils.queryData(conn, "select pricebit,countbit,moneybit,salesordernameset,salesordernamerule from s_company_config where company_id='" + companyid + "'", null,
					null, null, null);
			if (companytalbe.getRows().size() > 0) {
				countbit = companytalbe.getRows().get(0).getInteger("countbit");
				moneybit = companytalbe.getRows().get(0).getInteger("moneybit");

				salesordernameset = companytalbe.getRows().get(0).getInteger("salesordernameset");
				salesordernamerule = companytalbe.getRows().get(0).getString("salesordernamerule");
			}

			Statement ps = conn.createStatement();
			conn.setAutoCommit(false);
			String detail = "insert into salesorderdetail (orderid,detailid,salesorderid,originalbill,goods_number,companyid,operate_by,operate_time,plandate,itemid,customerid,houseid,price,count,total,tax,taxrate,taxprice,taxmoney,stype,remark,status,schedulstatus,create_id,create_by,create_time,update_id,update_by,update_time,batchno,relationdetailid,relationorderid,relationmainid"
					+ (type == 0 ? ",audit_id,audit_by,audit_time,schedulcount" : "") + ") VALUES ('";
			String itemmonth = "insert into ordermonth (monthid,companyid,itemid,sdate,syear,smonth,salescount,salesmoney,batchno) VALUES ('";

			String orderid = operate.equals("edit") ? maindata.getString("orderid") : Pdasave.getOrderidByparams(companyid, "salesorder", "", billdate, conn);
			int i = 0;
			String details = "";
			boolean save = true;

			if (salesordernameset == 1) {
				if (originalbill.equals("")) {
					if (!operate.equals("edit")) {
						originalbill = getOriginalbillValue(companyid, conn, salesordernamerule);
					}
				} else {
					String kfsql = "select orderid from salesorder where companyid='" + companyid + "' and salesorderid<>'" + maindata.getString("salesorderid") + "' and originalbill='"
							+ originalbill + "' and status<>'2' ";
					Object kcobject = DataUtils.getValueBySQL(conn, kfsql, null);
					if (kcobject != null) {
						save = false;
						message = "当前原单号[" + originalbill + "]已在正常状态中的销售单号[" + kcobject.toString() + "]使用，需作废此单才可使用，当前操作失败。";
					}
				}
			}
			if (save) {
				if (operate.equals("edit")) {// 编辑 删除明细数据重新增加
					String fsql = "select status from salesorder where salesorderid='" + maindata.getString("salesorderid") + "'";
					Object cobject = DataUtils.getValueBySQL(conn, fsql, null);
					if (cobject == null) {
						save = false;
						message = "当前记录已删除，操作失败。";
						state = "2";
					} else {
						String fstatus = cobject.toString();
						if (fstatus.equals("0")) {
							ps.addBatch("delete from salesorderdetail where salesorderid='" + maindata.getString("salesorderid") + "'");
						} else if (fstatus.equals("3")) {
							save = false;
							message = "当前记录已提交审批，操作失败。";
							state = "2";
						} else if (fstatus.equals("1")) {
							save = false;
							message = "当前记录已审核，操作失败。";
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
							String newrecode = ((detailid.equals("") || result.getString("newrecode") == null) ? "yes" : result.getString("newrecode"));
							String batchno = result.getString("batchno");
							String itemid = result.getString("itemid");
							double price = result.getDoubleValue("price");
							double dcount = result.getDoubleValue("count");
							double dtotal = result.getDoubleValue("total");
							String plandate = result.getString("plandate");

							String relationdetailid = result.getString("relationdetailid");
							String relationorderid = result.getString("relationorderid");
							String relationmainid = result.getString("relationmainid");

							if (type == 0) {// 审核

								details = itemmonth + Common.getUpperUUIDString() + "','" + companyid + "','" + itemid + "','" + sdate + "'," + syear + "," + smonth + "," + dcount + "," + dtotal
										+ ",'" + batchno + "') on duplicate key update salescount=round(salescount+" + dcount + "," + countbit + "),salesmoney=round(salesmoney+" + dtotal + ","
										+ moneybit + ")";
								ps.addBatch(details);
							}

							details = detail + orderid + "','" + (detailid.equals("") ? Common.getUpperUUIDString() : detailid) + "','" + maindata.getString("salesorderid") + "','" + originalbill
									+ "'," + result.getInteger("goods_number") + ",'" + companyid + "','" + maindata.getString("operate_by") + "','" + maindata.getString("operate_time") + "','"
									+ plandate + "','" + itemid + "','" + customerid + "','" + houseid + "'," + price + "," + dcount + "," + dtotal + "," + result.getDoubleValue("tax") + ","
									+ result.getDoubleValue("taxrate") + "," + result.getDoubleValue("taxprice") + "," + result.getDoubleValue("taxmoney") + ",'151','" + result.getString("remark")
									+ "','" + status + "','0','" + (newrecode.equals("yes") ? loginuserid : result.getString("create_id")) + "','"
									+ (newrecode.equals("yes") ? loginUser : result.getString("create_by")) + "'," + (newrecode.equals("yes") ? "now()" : "'" + result.getString("create_time") + "'")
									+ ",'" + loginuserid + "','" + loginUser + "',now(),'" + batchno + "','" + relationdetailid + "','" + relationorderid + "','" + relationmainid + "'"
									+ (type == 0 ? ",'" + loginuserid + "','" + loginUser + "',now()," + dcount : "") + ")";

							ps.addBatch(details);

						}
					}
				} else {
					message = message + "没有商品明细数据，操作失败";
					save = false;
				}
				if (save) {
					String contractmsg = "";
					String contractid = "";
					String contractorderid = "";
					if (storecontractstatus.equals("new")) {
						contractid = Common.getUpperUUIDString();

						String[] fsdatearr = contractdata.getString("contractdate").split("-");
						String fsdate = fsdatearr[0] + "-" + fsdatearr[1] + "-01";
						String fbilldate = fsdatearr[0] + fsdatearr[1] + fsdatearr[2];

						contractorderid = Pdasave.getOrderidByparams(companyid, "storecontract", "salesorder", fbilldate, conn);
						ps.addBatch("INSERT INTO storecontract (id, contractname, templateid, purchaseorderid, orderid, contractdate, Bcustomerid, Bcompanyname, Blinkphone, Bbankname, Bbanknumber, Baddress, address, Acompanyname, Alinkphone, Abankname, Abanknumber, Aaddress, content1, content2, companyid, operate_by, create_id, create_by, create_time, update_id, update_by, update_time, Afax, Bfax, stype,partAB) VALUES ('"
								+ contractid
								+ "','"
								+ contractdata.getString("contractname")
								+ "','"
								+ contractdata.getString("templateid")
								+ "','"
								+ contractdata.getString("purchaseorderid")
								+ "','"
								+ contractorderid
								+ "','"
								+ contractdata.getString("contractdate")
								+ "','"
								+ contractdata.getString("Bcustomerid")
								+ "','"
								+ contractdata.getString("Bcompanyname")
								+ "','"
								+ contractdata.getString("Blinkphone")
								+ "','"
								+ contractdata.getString("Bbankname")
								+ "','"
								+ contractdata.getString("Bbanknumber")
								+ "','"
								+ contractdata.getString("Baddress")
								+ "','"
								+ contractdata.getString("address")
								+ "','"
								+ contractdata.getString("Acompanyname")
								+ "','"
								+ contractdata.getString("Alinkphone")
								+ "','"
								+ contractdata.getString("Abankname")
								+ "','"
								+ contractdata.getString("Abanknumber")
								+ "','"
								+ contractdata.getString("Aaddress")
								+ "','"
								+ contractdata.getString("content1").replaceAll("'", "''")
								+ "','"
								+ contractdata.getString("content2").replaceAll("'", "''")
								+ "','"
								+ contractdata.getString("companyid")
								+ "','"
								+ contractdata.getString("operate_by")
								+ "','"
								+ loginuserid
								+ "','"
								+ loginUser
								+ "',now(),'"
								+ loginuserid
								+ "','"
								+ loginUser
								+ "',now() "
								+ ",'"
								+ contractdata.getString("Afax")
								+ "','"
								+ contractdata.getString("Bfax") + "',3," + contractdata.getInteger("partAB") + ")");
						contractmsg = " 增加销售合同 " + contractorderid;
					} else if (storecontractstatus.equals("update")) {
						contractid = contractdata.getString("id");
						contractorderid = contractdata.getString("orderid");
						contractmsg = " 修改销售合同 " + contractorderid;
						ps.addBatch("update storecontract set orderid='" + contractorderid + "',contractname='" + contractdata.getString("contractname") + "',templateid='"
								+ contractdata.getString("templateid") + "',contractdate='" + contractdata.getString("contractdate") + "', Bcustomerid='" + contractdata.getString("Bcustomerid")
								+ "', Bcompanyname='" + contractdata.getString("Bcompanyname") + "', Blinkphone='" + contractdata.getString("Blinkphone") + "', Bbankname='"
								+ contractdata.getString("Bbankname") + "', Bbanknumber='" + contractdata.getString("Bbanknumber") + "', Baddress='" + contractdata.getString("Baddress")
								+ "', address='" + contractdata.getString("address") + "', Acompanyname='" + contractdata.getString("Acompanyname") + "', Alinkphone='"
								+ contractdata.getString("Alinkphone") + "', Abankname='" + contractdata.getString("Abankname") + "', Abanknumber='" + contractdata.getString("Abanknumber")
								+ "', Aaddress='" + contractdata.getString("Aaddress") + "', content1='" + contractdata.getString("content1").replaceAll("'", "''") + "', content2='"
								+ contractdata.getString("content2").replaceAll("'", "''") + "', companyid='" + contractdata.getString("companyid") + "', operate_by='"
								+ contractdata.getString("operate_by") + "', update_id='" + loginuserid + "', update_by='" + loginUser + "', update_time=now(),Afax='" + contractdata.getString("Afax")
								+ "',Bfax='" + contractdata.getString("Bfax") + "',partAB=" + contractdata.getInteger("partAB") + " where id='" + contractdata.getString("id") + "'");
					} else if (storecontractstatus.equals("delete")) {
						ps.addBatch("delete from storecontract where id='" + contractdata.getString("id") + "'");
						contractmsg = " 删除销售合同 " + maindata.getString("contractorderid");
						contractid = "";
						contractorderid = "";
					} else if (storecontractstatus.equals("nochange")) {
						contractid = contractdata.getString("id");
						contractorderid = contractdata.getString("orderid");
					}
					contractmsg = contractmsg + fcontractmsg;

					int changebilltype = Pdacommon.getDatalogBillChangefunc("salesorder", "");
					if (operate.equals("edit")) {// 编辑 更新
						ps.addBatch("update salesorder set orderid='" + orderid + "',originalbill='" + originalbill + "'" + ",operate_time='" + operate_time + "',selltype='"
								+ maindata.getString("selltype") + "',plandate=null,operate_by='" + maindata.getString("operate_by") + "',houseid='" + houseid + "',customerid='" + customerid
								+ "',count=" + count + ",total=" + total + ",totaltax=" + maindata.getDouble("totaltax") + ",totalmoney=" + maindata.getDouble("totalmoney") + ",currency='"
								+ maindata.getString("currency") + "',remark='" + maindata.getString("remark") + "',status='" + status + "'"
								+ (type == 0 ? ",audit_id='" + loginuserid + "',audit_by='" + loginUser + "',audit_time=now()" : "") + ",update_by='" + loginUser + "',update_time=now(),iproperty='"
								+ maindata.getString("iproperty") + "',contractid='" + contractid + "',contractorderid='" + contractorderid + "' where salesorderid='"
								+ maindata.getString("salesorderid") + "'");

						ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString()
								+ "','" + companyid + "'," + changebilltype + ",'修改','" + maindata.getString("salesorderid") + "','单据编号：" + orderid + "','" + loginuserid + "','" + loginUser
								+ "',now())");

						ps.addBatch("update salesorderdetail d,salesorder s set d.create_id=s.create_id,d.create_by=s.create_by,d.create_time=s.create_time where d.salesorderid = s.salesorderid and s.salesorderid='"
								+ maindata.getString("salesorderid") + "'");

					} else {
						String main = "insert into salesorder (orderid,salesorderid,bill_type,originalbill,companyid,operate_time,selltype,operate_by,houseid,customerid,currency,count,total,totaltax,totalmoney,remark,status,stockstatus,printing,outexcel,create_id,create_by,create_time,update_id,update_by,update_time,iproperty,contractid,contractorderid"
								+ (type == 0 ? ",audit_id,audit_by,audit_time" : "")
								+ ") VALUES ('"
								+ orderid
								+ "','"
								+ maindata.getString("salesorderid")
								+ "','"
								+ maindata.getString("bill_type")
								+ "','"
								+ originalbill
								+ "','"
								+ companyid
								+ "','"
								+ operate_time
								+ "','"
								+ maindata.getString("selltype")
								+ "','"
								+ maindata.getString("operate_by")
								+ "','"
								+ houseid
								+ "','"
								+ customerid
								+ "','"
								+ maindata.getString("currency")
								+ "',"
								+ count
								+ ","
								+ total
								+ ","
								+ maindata.getDouble("totaltax")
								+ ","
								+ maindata.getDouble("totalmoney")
								+ ",'"
								+ maindata.getString("remark")
								+ "','"
								+ status
								+ "','0',0,0,'"
								+ loginuserid
								+ "','"
								+ loginUser
								+ "',now(),'"
								+ loginuserid
								+ "','"
								+ loginUser
								+ "',now(),'"
								+ maindata.getString("iproperty")
								+ "','"
								+ contractid
								+ "','"
								+ contractorderid
								+ "'"
								+ (type == 0 ? ",'" + loginuserid + "','" + loginUser + "',now()" : "") + ")";

						// System.out.println(main);
						ps.addBatch(main);

						ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString()
								+ "','" + companyid + "'," + changebilltype + ",'" + (status == 0 ? "新增保存并审核" : "新增保存") + "','" + maindata.getString("purchaseorderid") + "','订单编号：" + orderid + "','"
								+ loginuserid + "','" + loginUser + "',now())");

					}

					if (type == 3) {
						if (auditflowlist != null) {
							if (!auditflowlist.getString("backbillflowid").equals("")) {
								ps.addBatch("update t_billflow set back_type=1,back_time=now() where id='" + auditflowlist.getString("backbillflowid") + "'");
							}
							ps.addBatch("INSERT INTO `t_billflow` (`id`, `companyid`, `billid`,`orderid`, `auditflow_id`,`auditflowmain_id`, `flowfstatus`, `flownum`, `flowname`, `preflow`, `preflownum`, `preflowname`, `pass_id`, `pass_by`, `pass_time`, `pass_remark`, `receive_id`, `receive_by`,`pass_urgent`) VALUES "
									+ "('"
									+ Common.getUpperUUIDString()
									+ "', '"
									+ companyid
									+ "', '"
									+ maindata.getString("salesorderid")
									+ "', '"
									+ maindata.getString("orderid")
									+ "', '"
									+ auditflowlist.getString("auditflow_id")
									+ "', '"
									+ auditflowlist.getString("auditflowmain_id")
									+ "', 0, "
									+ auditflowlist.getInteger("newflownum")
									+ ", '"
									+ auditflowlist.getString("newflowname")
									+ "', '', "
									+ auditflowlist.getInteger("oldflownum")
									+ ", '"
									+ auditflowlist.getString("oldflowname")
									+ "', '"
									+ auditflowlist.getString("loginuserid")
									+ "', '"
									+ auditflowlist.getString("loginUser")
									+ "', now(), '"
									+ auditflowlist.getString("remark")
									+ "', '"
									+ auditflowlist.getString("receive_id") + "', '" + auditflowlist.getString("receive_by") + "'," + auditflowlist.getInteger("urgentselect") + ")");
						}
					}

					ps.executeBatch();
					conn.commit();
					conn.setAutoCommit(true);
					state = "1";
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
			conn.close();
		}
		rt.put("state", state);
		rt.put("message", message);
		return rt;
	}

	// 保存销售订单信息
	public static JSONObject saveSalesorderFunction_01(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject rt = new JSONObject();
		JSONObject maindata = JSONObject.parseObject(params.getString("maindata"));
		Integer type = params.getInteger("type");
		String loginUser = params.getString("loginUser");
		String loginuserid = params.getString("loginuserid");
		JSONArray detaildata = params.getJSONArray("detaildata");
		String operate = params.getString("operate");// edit 编辑单据
		String state = "0";
		String message = "";

		JSONObject auditflowlist = JSONObject.parseObject(params.getString("auditflowlist"));

		String companyid = maindata.getString("companyid");
		String houseid = maindata.getString("houseid");
		String customerid = maindata.getString("customerid");
		String operate_time = maindata.getString("operate_time");
		double count = maindata.getDoubleValue("count");
		double total = maindata.getDoubleValue("total");
		int status = (type == 3 && auditflowlist != null ? 3 : (type == 0 ? 1 : 0));// type=0
																					// 为保存并审核
		// ，其他为保存 未审核

		// 合同保存功能 2020-11-10 begin
		String storecontractstatus = params.getString("storecontractstatus");
		JSONObject contractdata = storecontractstatus.equals("no") ? null : JSONObject.parseObject(params.getString("contractdata"));
		// System.out.println(storecontractstatus);
		String fcontractmsg = params.getString("fcontractmsg");
		// 合同保存功能 2020-11-10 end

		String[] sdatearr = operate_time.split("-");
		int syear = Integer.parseInt(sdatearr[0]);
		int smonth = Integer.parseInt(sdatearr[1]);
		String sdate = sdatearr[0] + "-" + sdatearr[1] + "-01";

		String billdate = sdatearr[0] + sdatearr[1] + sdatearr[2];

		String originalbill = maindata.getString("originalbill");

		try {
			int countbit = 0;
			int moneybit = 2;
			int salesordernameset = 0;
			String salesordernamerule = "";

			Table companytalbe = DataUtils.queryData(conn, "select pricebit,countbit,moneybit,salesordernameset,salesordernamerule from s_company_config where company_id='" + companyid + "'", null,
					null, null, null);
			if (companytalbe.getRows().size() > 0) {
				countbit = companytalbe.getRows().get(0).getInteger("countbit");
				moneybit = companytalbe.getRows().get(0).getInteger("moneybit");

				salesordernameset = companytalbe.getRows().get(0).getInteger("salesordernameset");
				salesordernamerule = companytalbe.getRows().get(0).getString("salesordernamerule");
			}

			Statement ps = conn.createStatement();
			Statement ps1 = conn.createStatement();
			conn.setAutoCommit(false);
			String detail = "insert into salesorderdetail (orderid,detailid,salesorderid,originalbill,goods_number,companyid,operate_by,operate_time,plandate,itemid,customerid,houseid,price,count,total,tax,taxrate,taxprice,taxmoney,stype,remark,status,schedulstatus,create_id,create_by,create_time,update_id,update_by,update_time,batchno,relationdetailid,relationorderid,relationmainid"
					+ (type == 0 ? ",audit_id,audit_by,audit_time,schedulcount" : "") + ") VALUES ('";
			String itemmonth = "insert into ordermonth (monthid,companyid,itemid,sdate,syear,smonth,salescount,salesmoney,batchno) VALUES ('";

			String orderid = operate.equals("edit") ? maindata.getString("orderid") : Pdasave.getOrderidByparams(companyid, "salesorder", "", billdate, conn);
			int i = 0;
			String details = "";
			boolean save = true;

			if (salesordernameset == 1) {
				if (originalbill.equals("")) {
					if (!operate.equals("edit")) {
						originalbill = getOriginalbillValue(companyid, conn, salesordernamerule);
					}
				} else {
					String kfsql = "select orderid from salesorder where companyid='" + companyid + "' and salesorderid<>'" + maindata.getString("salesorderid") + "' and originalbill='"
							+ originalbill + "' and status<>'2' ";
					Object kcobject = DataUtils.getValueBySQL(conn, kfsql, null);
					if (kcobject != null) {
						save = false;
						message = "当前原单号[" + originalbill + "]已在正常状态中的销售单号[" + kcobject.toString() + "]使用，需作废此单才可使用，当前操作失败。";
					}
				}
			}
			if (save) {
				if (operate.equals("edit")) {// 编辑 删除明细数据重新增加
					String fsql = "select status from salesorder where salesorderid='" + maindata.getString("salesorderid") + "'";
					Object cobject = DataUtils.getValueBySQL(conn, fsql, null);
					if (cobject == null) {
						save = false;
						message = "当前记录已删除，操作失败。";
						state = "2";
					} else {
						String fstatus = cobject.toString();
						if (fstatus.equals("0")) {
							ps.addBatch("delete from salesorderdetail where salesorderid='" + maindata.getString("salesorderid") + "'");
						} else if (fstatus.equals("1")) {
							save = false;
							message = "当前记录已审核，操作失败。";
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
							String newrecode = ((detailid.equals("") || result.getString("newrecode") == null) ? "yes" : result.getString("newrecode"));
							String batchno = result.getString("batchno");
							String itemid = result.getString("itemid");
							double price = result.getDoubleValue("price");
							double dcount = result.getDoubleValue("count");
							double dtotal = result.getDoubleValue("total");
							String plandate = result.getString("plandate");

							String relationdetailid = result.getString("relationdetailid");
							String relationorderid = result.getString("relationorderid");
							String relationmainid = result.getString("relationmainid");

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
										+ "' and sformat='" + sformat.replaceAll("'", "''") + "' and unit='" + unit + "' and property1='" + property1.replaceAll("'", "''") + "'"
										+ " and property2 = '" + property2.replaceAll("'", "''") + "' and property3 = '" + property3.replaceAll("'", "''") + "' and property4 = '"
										+ property4.replaceAll("'", "''") + "' and property5 = '" + property5.replaceAll("'", "''") + "'";
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

							if (type == 0) {// 审核

								details = itemmonth + Common.getUpperUUIDString() + "','" + companyid + "','" + itemid + "','" + sdate + "'," + syear + "," + smonth + "," + dcount + "," + dtotal
										+ ",'" + batchno + "') on duplicate key update salescount=round(salescount+" + dcount + "," + countbit + "),salesmoney=round(salesmoney+" + dtotal + ","
										+ moneybit + ")";
								ps.addBatch(details);
							}

							details = detail + orderid + "','" + (detailid.equals("") ? Common.getUpperUUIDString() : detailid) + "','" + maindata.getString("salesorderid") + "','" + originalbill
									+ "'," + result.getInteger("goods_number") + ",'" + companyid + "','" + maindata.getString("operate_by") + "','" + maindata.getString("operate_time") + "','"
									+ plandate + "','" + itemid + "','" + customerid + "','" + houseid + "'," + price + "," + dcount + "," + dtotal + "," + result.getDoubleValue("tax") + ","
									+ result.getDoubleValue("taxrate") + "," + result.getDoubleValue("taxprice") + "," + result.getDoubleValue("taxmoney") + ",'151','" + result.getString("remark")
									+ "','" + status + "','0','" + (newrecode.equals("yes") ? loginuserid : result.getString("create_id")) + "','"
									+ (newrecode.equals("yes") ? loginUser : result.getString("create_by")) + "'," + (newrecode.equals("yes") ? "now()" : "'" + result.getString("create_time") + "'")
									+ ",'" + loginuserid + "','" + loginUser + "',now(),'" + batchno + "','" + relationdetailid + "','" + relationorderid + "','" + relationmainid + "'"
									+ (type == 0 ? ",'" + loginuserid + "','" + loginUser + "',now()," + dcount : "") + ")";

							ps.addBatch(details);

						}
					}
				} else {
					message = message + "没有商品明细数据，操作失败";
					save = false;
				}

				if (save) {
					String contractmsg = "";
					String contractid = "";
					String contractorderid = "";
					if (storecontractstatus.equals("new")) {
						contractid = Common.getUpperUUIDString();

						String[] fsdatearr = contractdata.getString("contractdate").split("-");
						String fsdate = fsdatearr[0] + "-" + fsdatearr[1] + "-01";
						String fbilldate = fsdatearr[0] + fsdatearr[1] + fsdatearr[2];

						contractorderid = Pdasave.getOrderidByparams(companyid, "storecontract", "salesorder", fbilldate, conn);
						ps.addBatch("INSERT INTO storecontract (id, contractname, templateid, purchaseorderid, orderid, contractdate, Bcustomerid, Bcompanyname, Blinkphone, Bbankname, Bbanknumber, Baddress, address, Acompanyname, Alinkphone, Abankname, Abanknumber, Aaddress, content1, content2, companyid, operate_by, create_id, create_by, create_time, update_id, update_by, update_time, Afax, Bfax, stype,partAB) VALUES ('"
								+ contractid
								+ "','"
								+ contractdata.getString("contractname")
								+ "','"
								+ contractdata.getString("templateid")
								+ "','"
								+ contractdata.getString("purchaseorderid")
								+ "','"
								+ contractorderid
								+ "','"
								+ contractdata.getString("contractdate")
								+ "','"
								+ contractdata.getString("Bcustomerid")
								+ "','"
								+ contractdata.getString("Bcompanyname")
								+ "','"
								+ contractdata.getString("Blinkphone")
								+ "','"
								+ contractdata.getString("Bbankname")
								+ "','"
								+ contractdata.getString("Bbanknumber")
								+ "','"
								+ contractdata.getString("Baddress")
								+ "','"
								+ contractdata.getString("address")
								+ "','"
								+ contractdata.getString("Acompanyname")
								+ "','"
								+ contractdata.getString("Alinkphone")
								+ "','"
								+ contractdata.getString("Abankname")
								+ "','"
								+ contractdata.getString("Abanknumber")
								+ "','"
								+ contractdata.getString("Aaddress")
								+ "','"
								+ contractdata.getString("content1").replaceAll("'", "''")
								+ "','"
								+ contractdata.getString("content2").replaceAll("'", "''")
								+ "','"
								+ contractdata.getString("companyid")
								+ "','"
								+ contractdata.getString("operate_by")
								+ "','"
								+ loginuserid
								+ "','"
								+ loginUser
								+ "',now(),'"
								+ loginuserid
								+ "','"
								+ loginUser
								+ "',now() "
								+ ",'"
								+ contractdata.getString("Afax")
								+ "','"
								+ contractdata.getString("Bfax") + "',3," + contractdata.getInteger("partAB") + ")");
						contractmsg = " 增加销售合同 " + contractorderid;
					} else if (storecontractstatus.equals("update")) {
						contractid = contractdata.getString("id");
						contractorderid = contractdata.getString("orderid");
						contractmsg = " 修改销售合同 " + contractorderid;
						ps.addBatch("update storecontract set orderid='" + contractorderid + "',contractname='" + contractdata.getString("contractname") + "',templateid='"
								+ contractdata.getString("templateid") + "',contractdate='" + contractdata.getString("contractdate") + "', Bcustomerid='" + contractdata.getString("Bcustomerid")
								+ "', Bcompanyname='" + contractdata.getString("Bcompanyname") + "', Blinkphone='" + contractdata.getString("Blinkphone") + "', Bbankname='"
								+ contractdata.getString("Bbankname") + "', Bbanknumber='" + contractdata.getString("Bbanknumber") + "', Baddress='" + contractdata.getString("Baddress")
								+ "', address='" + contractdata.getString("address") + "', Acompanyname='" + contractdata.getString("Acompanyname") + "', Alinkphone='"
								+ contractdata.getString("Alinkphone") + "', Abankname='" + contractdata.getString("Abankname") + "', Abanknumber='" + contractdata.getString("Abanknumber")
								+ "', Aaddress='" + contractdata.getString("Aaddress") + "', content1='" + contractdata.getString("content1").replaceAll("'", "''") + "', content2='"
								+ contractdata.getString("content2").replaceAll("'", "''") + "', companyid='" + contractdata.getString("companyid") + "', operate_by='"
								+ contractdata.getString("operate_by") + "', update_id='" + loginuserid + "', update_by='" + loginUser + "', update_time=now(),Afax='" + contractdata.getString("Afax")
								+ "',Bfax='" + contractdata.getString("Bfax") + "',partAB=" + contractdata.getInteger("partAB") + " where id='" + contractdata.getString("id") + "'");
					} else if (storecontractstatus.equals("delete")) {
						ps.addBatch("delete from storecontract where id='" + contractdata.getString("id") + "'");
						contractmsg = " 删除销售合同 " + maindata.getString("contractorderid");
						contractid = "";
						contractorderid = "";
					} else if (storecontractstatus.equals("nochange")) {
						contractid = contractdata.getString("id");
						contractorderid = contractdata.getString("orderid");
					}
					contractmsg = contractmsg + fcontractmsg;

					int changebilltype = Pdacommon.getDatalogBillChangefunc("salesorder", "");
					if (operate.equals("edit")) {// 编辑 更新
						ps.addBatch("update salesorder set orderid='" + orderid + "',originalbill='" + originalbill + "',operate_time='" + operate_time + "',selltype='"
								+ maindata.getString("selltype") + "',plandate=null,operate_by='" + maindata.getString("operate_by") + "',houseid='" + houseid + "',customerid='" + customerid
								+ "',count=" + count + ",total=" + total + ",totaltax=" + maindata.getDouble("totaltax") + ",totalmoney=" + maindata.getDouble("totalmoney") + ",currency='"
								+ maindata.getString("currency") + "',remark='" + maindata.getString("remark") + "',status='" + status + "'"
								+ (type == 0 ? ",audit_id='" + loginuserid + "',audit_by='" + loginUser + "',audit_time=now()" : "") + ",update_by='" + loginUser + "',update_time=now(),iproperty='"
								+ maindata.getString("iproperty") + "',contractid='" + contractid + "',contractorderid='" + contractorderid + "' where salesorderid='"
								+ maindata.getString("salesorderid") + "'");

						ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString()
								+ "','" + companyid + "'," + changebilltype + ",'修改','" + maindata.getString("salesorderid") + "','单据编号：" + orderid + "','" + loginuserid + "','" + loginUser
								+ "',now())");

						ps.addBatch("update salesorderdetail d,salesorder s set d.create_id=s.create_id,d.create_by=s.create_by,d.create_time=s.create_time where d.salesorderid = s.salesorderid and s.salesorderid='"
								+ maindata.getString("salesorderid") + "'");

					} else {
						String main = "insert into salesorder (orderid,salesorderid,bill_type,originalbill,companyid,operate_time,selltype,operate_by,houseid,customerid,currency,count,total,totaltax,totalmoney,remark,status,stockstatus,printing,outexcel,create_id,create_by,create_time,update_id,update_by,update_time,iproperty,contractid,contractorderid"
								+ (type == 0 ? ",audit_id,audit_by,audit_time" : "")
								+ ") VALUES ('"
								+ orderid
								+ "','"
								+ maindata.getString("salesorderid")
								+ "','"
								+ maindata.getString("bill_type")
								+ "','"
								+ originalbill
								+ "','"
								+ companyid
								+ "','"
								+ operate_time
								+ "','"
								+ maindata.getString("selltype")
								+ "','"
								+ maindata.getString("operate_by")
								+ "','"
								+ houseid
								+ "','"
								+ customerid
								+ "','"
								+ maindata.getString("currency")
								+ "',"
								+ count
								+ ","
								+ total
								+ ","
								+ maindata.getDouble("totaltax")
								+ ","
								+ maindata.getDouble("totalmoney")
								+ ",'"
								+ maindata.getString("remark")
								+ "','"
								+ status
								+ "','0',0,0,'"
								+ loginuserid
								+ "','"
								+ loginUser
								+ "',now(),'"
								+ loginuserid
								+ "','"
								+ loginUser
								+ "',now(),'"
								+ maindata.getString("iproperty")
								+ "','"
								+ contractid
								+ "','"
								+ contractorderid
								+ "'"
								+ (type == 0 ? ",'" + loginuserid + "','" + loginUser + "',now()" : "") + ")";
						ps.addBatch(main);

						ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString()
								+ "','" + companyid + "'," + changebilltype + ",'" + (status == 0 ? "新增保存并审核" : "新增保存") + "','" + maindata.getString("purchaseorderid") + "','订单编号：" + orderid + "','"
								+ loginuserid + "','" + loginUser + "',now())");

					}

					if (type == 3 && auditflowlist != null) {
						if (!auditflowlist.getString("backbillflowid").equals("")) {
							ps.addBatch("update t_billflow set back_type=1,back_time=now() where id='" + auditflowlist.getString("backbillflowid") + "'");
						}
						ps.addBatch("INSERT INTO `t_billflow` (`id`, `companyid`, `billid`,`orderid`, `auditflow_id`,`auditflowmain_id`, `flowfstatus`, `flownum`, `flowname`, `preflow`, `preflownum`, `preflowname`, `pass_id`, `pass_by`, `pass_time`, `pass_remark`, `receive_id`, `receive_by`,`pass_urgent`) VALUES "
								+ "('"
								+ Common.getUpperUUIDString()
								+ "', '"
								+ companyid
								+ "', '"
								+ maindata.getString("salesorderid")
								+ "', '"
								+ maindata.getString("orderid")
								+ "', '"
								+ auditflowlist.getString("auditflow_id")
								+ "', '"
								+ auditflowlist.getString("auditflowmain_id")
								+ "', 0, "
								+ auditflowlist.getInteger("newflownum")
								+ ", '"
								+ auditflowlist.getString("newflowname")
								+ "', '', "
								+ auditflowlist.getInteger("oldflownum")
								+ ", '"
								+ auditflowlist.getString("oldflowname")
								+ "', '"
								+ auditflowlist.getString("loginuserid")
								+ "', '"
								+ auditflowlist.getString("loginUser")
								+ "', now(), '"
								+ auditflowlist.getString("remark")
								+ "', '"
								+ auditflowlist.getString("receive_id") + "', '" + auditflowlist.getString("receive_by") + "'," + auditflowlist.getInteger("urgentselect") + ")");
					}

					ps.executeBatch();
					conn.commit();
					conn.setAutoCommit(true);
					state = "1";
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
			conn.close();
		}
		rt.put("state", state);
		rt.put("message", message);
		return rt;
	}

	// 销售订单审核
	public static JSONObject auditSalesorderFunction(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject rt = new JSONObject();
		String orderid = params.getString("orderid");
		String companyid = params.getString("companyid");
		String mainid = params.getString("mainid").replaceAll("'", "");
		// JSONArray detailarr = params.getJSONArray("detailarr");
		String loginuserid = params.getString("loginuserid");
		String loginUser = params.getString("loginUser");
		String operate_time = params.getString("operate_time");
		String[] arr = operate_time.split("-");
		String sdate = arr[0] + "-" + arr[1] + "-" + "01"; // 业务日期转成每个月1日来记录itemmonth数据

		int syear = Integer.parseInt(arr[0]);
		int smonth = Integer.parseInt(arr[1]);

		int audittype = params.getInteger("audittype");
		int stype = params.getInteger("stype");

		JSONObject auditflowlist = JSONObject.parseObject(params.getString("auditflowlist"));

		String state = "0";
		String message = "";

		try {
			if (mainid.length() != 32) {
				message = "含非法数据，操作失败!";
				state = "2";
			} else {
				Statement ps = conn.createStatement();
				conn.setAutoCommit(false);
				String sql = "select status from salesorder where  salesorderid='" + mainid + "' ";
				Object cobject = DataUtils.getValueBySQL(conn, sql, null);
				if (cobject == null) {
					message = "已被删除";
					state = "2";
				} else {
					String fstatus = cobject.toString();
					if (fstatus.equals("1")) {
						message = "已被审核";
						state = "2";
					} else if (fstatus.equals("2")) {
						message = "已作废，不能进行审核操作";
						state = "2";
					} else {

						int countbit = 0;
						int moneybit = 2;
						Table companytalbe = DataUtils.queryData(conn, "select pricebit,countbit,moneybit from s_company_config where company_id='" + companyid + "'", null, null, null, null);
						if (companytalbe.getRows().size() > 0) {
							countbit = companytalbe.getRows().get(0).getInteger("countbit");
							moneybit = companytalbe.getRows().get(0).getInteger("moneybit");
						}

						if (fstatus.equals("3") && stype == 2) {
							int agreedZJ = auditflowlist.getInteger("agreedZJ");
							if (auditflowlist.getInteger("curflowfstatus") == 2) {
								ps.addBatch("update t_billflow set back_type=" + agreedZJ + ",back_remark='" + auditflowlist.getString("remark") + "',back_time=now() where id='"
										+ auditflowlist.getString("curbillflow") + "'");
							} else {
								if (agreedZJ == 2) {
									if (!auditflowlist.getString("curbillflow").equals(auditflowlist.getString("back_flow"))) {
										ps.addBatch("update t_billflow set flowfstatus=" + agreedZJ + ",back_type=1,receive_remark='" + auditflowlist.getString("remark")
												+ "',receive_time=now() where id='" + auditflowlist.getString("curbillflow") + "'");
										ps.addBatch("update t_billflow set flowfstatus=" + agreedZJ + ",receive_remark='" + auditflowlist.getString("remark")
												+ "',receive_time=now(),back_send_remark='" + auditflowlist.getString("remark") + "',back_send_time=now(),back_urgent="
												+ auditflowlist.getInteger("urgentselect") + ",back_id='" + auditflowlist.getString("back_id") + "',back_by='" + auditflowlist.getString("back_by")
												+ "',backflownum=" + auditflowlist.getInteger("back_preflownum") + ",backflowname='" + auditflowlist.getString("backflowname") + "' where id='"
												+ auditflowlist.getString("back_flow") + "'");
									} else {
										ps.addBatch("update t_billflow set flowfstatus=" + agreedZJ + ",receive_remark='" + auditflowlist.getString("remark")
												+ "',receive_time=now(),back_send_remark='" + auditflowlist.getString("remark") + "',back_send_time=now(),back_urgent="
												+ auditflowlist.getInteger("urgentselect") + ",back_id='" + auditflowlist.getString("back_id") + "',back_by='" + auditflowlist.getString("back_by")
												+ "',backflownum=" + auditflowlist.getInteger("back_preflownum") + ",backflowname='" + auditflowlist.getString("backflowname") + "' where id='"
												+ auditflowlist.getString("curbillflow") + "'");

									}
								} else {
									ps.addBatch("update t_billflow set flowfstatus=" + agreedZJ + ",receive_remark='" + auditflowlist.getString("remark") + "',receive_time=now() where id='"
											+ auditflowlist.getString("curbillflow") + "'");
								}
							}
							if (agreedZJ == 1 && audittype != 1) {

								ps.addBatch("INSERT INTO `t_billflow` (`id`, `companyid`, `billid`,`orderid`, `auditflow_id`,`auditflowmain_id`, `flowfstatus`, `flownum`, `flowname`, `preflow`, `preflownum`, `preflowname`, `pass_id`, `pass_by`, `pass_time`, `pass_remark`, `receive_id`, `receive_by`,`pass_urgent`) VALUES "
										+ "('"
										+ Common.getUpperUUIDString()
										+ "', '"
										+ companyid
										+ "', '"
										+ auditflowlist.getString("mainid")
										+ "', '"
										+ orderid
										+ "', '"
										+ auditflowlist.getString("auditflow_id")
										+ "', '"
										+ auditflowlist.getString("auditflowmain_id")
										+ "', 0,"
										+ auditflowlist.getInteger("newflownum")
										+ ", '"
										+ auditflowlist.getString("newflowname")
										+ "', '"
										+ auditflowlist.getString("curbillflow")
										+ "', "
										+ auditflowlist.getInteger("oldflownum")
										+ ", '"
										+ auditflowlist.getString("oldflowname")
										+ "', '"
										+ auditflowlist.getString("loginuserid")
										+ "', '"
										+ auditflowlist.getString("loginUser")
										+ "', now(), '"
										+ auditflowlist.getString("remark")
										+ "', '"
										+ auditflowlist.getString("receive_id")
										+ "', '"
										+ auditflowlist.getString("receive_by")
										+ "',"
										+ auditflowlist.getInteger("urgentselect") + ")");
							} else if (agreedZJ == 2 && auditflowlist.getInteger("curflowfstatus") == 2) {
								ps.addBatch("update t_billflow set flowfstatus=" + agreedZJ + ",back_send_remark='" + auditflowlist.getString("remark") + "',back_id='"
										+ auditflowlist.getString("back_id") + "',back_by='" + auditflowlist.getString("back_by") + "',backflownum=" + auditflowlist.getInteger("back_preflownum")
										+ ",backflowname='" + auditflowlist.getString("backflowname") + "',back_send_time=now(),back_urgent=" + auditflowlist.getInteger("urgentselect")
										+ " where id='" + auditflowlist.getString("back_flow") + "'");

							}

							if (audittype == 0) {
								ps.addBatch("update salesorderdetail set status='0' where salesorderid='" + mainid + "' ");
								ps.addBatch("update salesorder set status='0' where salesorderid='" + mainid + "' ");
							}
						}

						if ((fstatus.equals("0") && stype == 1) || (fstatus.equals("3") && stype == 2 && audittype == 1)) {

							String ordermonth = "insert into ordermonth (monthid,companyid,itemid,sdate,syear,smonth, salescount, salesmoney,batchno) VALUES ('";

							String fsql = "select sd.detailid,sd.count,sd.total,sd.itemid,sd.batchno from salesorderdetail sd  where sd.salesorderid='" + mainid + "'  order by sd.goods_number asc ";
							Table table = DataUtils.queryData(conn, fsql, null, null, null, null);
							Iterator<Row> iteratordata = table.getRows().iterator();

							while (iteratordata.hasNext()) {
								Row info = iteratordata.next();

								double count = Double.parseDouble(info.getValue("count").toString());
								double total = Double.parseDouble(info.getValue("total").toString());
								String itemid = info.getString("itemid");
								String batchno = erpscan.save.Pdainvalid.transformSpecialInfo(info.getString("batchno"));
								String details = ordermonth + Common.getUpperUUIDString() + "','" + companyid + "','" + itemid + "','" + sdate + "'," + syear + "," + smonth + "," + count + ","
										+ total + ",'" + batchno + "') on duplicate key update  salescount=round(salescount+" + count + "," + countbit + "), salesmoney=round(salesmoney+" + total
										+ "," + moneybit + ")";

								ps.addBatch(details);
								// System.out.println(details);
							}

							ps.addBatch("update  salesorderdetail set schedulcount=count,status='1',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now(),audit_id='"
									+ loginuserid + "',audit_by='" + loginUser + "',audit_time=now() where  salesorderid='" + mainid + "' ");
							ps.addBatch("update  salesorder set status='1',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now(),audit_id='" + loginuserid + "',audit_by='"
									+ loginUser + "',audit_time=now() where  salesorderid='" + mainid + "' ");

							ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString()
									+ "','" + companyid + "'," + Pdacommon.getDatalogBillChangefunc("salesorder", "") + ",'审核','" + mainid + "','单据编号：" + orderid + "','" + loginuserid + "','"
									+ loginUser + "',now())");
						}

						ps.executeBatch();
						conn.commit();
						int count = ps.getUpdateCount();
						if (count == 0) {
							state = "3";
							message = "单据已审核。";
						} else {
							state = "1";
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							String str = sdf.format(new Date());
							rt.put("audit_time", str);
						}

						conn.setAutoCommit(true);

					}
				}
			}

		} catch (Exception e) {
			// e.printStackTrace();
			message = "审核操作失败，请稍后再试。";
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

	// 销售订单作废
	public static JSONObject invalidSalesorderFunction(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject rt = new JSONObject();
		String orderid = params.getString("orderid");
		String companyid = params.getString("companyid");
		String mainid = params.getString("mainid").replaceAll("'", "");
		// JSONArray detailarr = params.getJSONArray("detailarr");
		String loginuserid = params.getString("loginuserid");
		String loginUser = params.getString("loginUser");
		String operate_time = params.getString("operate_time");
		String[] arr = operate_time.split("-");
		String sdate = arr[0] + "-" + arr[1] + "-" + "01"; // 业务日期转成每个月1日来记录itemmonth数据

		String state = "0";
		String message = "";

		try {
			if (mainid.length() != 32) {
				message = "含非法数据，操作失败!";
			} else {
				int countbit = 0;
				int moneybit = 2;
				Table companytalbe = DataUtils.queryData(conn, "select pricebit,countbit,moneybit from s_company_config where company_id='" + companyid + "'", null, null, null, null);
				if (companytalbe.getRows().size() > 0) {
					countbit = companytalbe.getRows().get(0).getInteger("countbit");
					moneybit = companytalbe.getRows().get(0).getInteger("moneybit");
				}

				Statement ps = conn.createStatement();
				conn.setAutoCommit(false);

				String relativeinfo = "";// 与销售出库单关联
				String scheduleinfo = "";// 与排产明细相关联

				String fsql = "select sd.detailid,sd.count,sd.total,sd.itemid,sd.batchno,i.itemname from salesorderdetail sd,iteminfo i where sd.salesorderid='" + mainid
						+ "' and sd.itemid=i.itemid order by sd.goods_number asc ";
				Table table = DataUtils.queryData(conn, fsql, null, null, null, null);
				Iterator<Row> iteratordata = table.getRows().iterator();

				while (iteratordata.hasNext()) {
					Row info = iteratordata.next();

					double count = Double.parseDouble(info.getValue("count").toString());
					double total = Double.parseDouble(info.getValue("total").toString());
					String itemid = info.getString("itemid");
					String batchno = erpscan.save.Pdainvalid.transformSpecialInfo(info.getString("batchno"));
					String itemname = info.getString("itemname");
					String detailid = info.getString("detailid");

					ps.addBatch("update ordermonth  set salescount=round(salescount-" + count + "," + countbit + "), salesmoney=round(salesmoney-" + total + "," + moneybit + ") where " + " itemid='"
							+ itemid + "' and batchno='" + batchno + "' and sdate='" + sdate + "'  and (select count(salesorderid) from salesorder where salesorderid='" + mainid
							+ "' and status='1' and outcount=0)=1");

					// 与有效排产明细单关联不能作废
					String sql = "select count(id) from  t_order where  salesorderdetailid='" + detailid + "' and order_status<>2 ";
					int scount = Integer.parseInt(DataUtils.getValueBySQL(conn, sql, null).toString());
					if (scount > 0) {
						scheduleinfo = scheduleinfo + (scheduleinfo.equals("") ? "" : "、") + itemname;
					}

					// 与有效出货单关联不能作废
					sql = "select count(detailid) from  storeoutdetail where relationdetailid='" + detailid + "' and status<>'2'  ";
					scount = Integer.parseInt(DataUtils.getValueBySQL(conn, sql, null).toString());
					if (scount > 0) {
						relativeinfo = relativeinfo + (relativeinfo.equals("") ? "" : "、") + itemname;
					}

				}

				if (scheduleinfo.equals("") && relativeinfo.equals("")) {

					ps.addBatch("update salesorderdetail set status='2',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now(),audit_id='" + loginuserid + "',audit_by='"
							+ loginUser + "',audit_time=now() where salesorderid='" + mainid + "' and status='1' ");
					ps.addBatch("update salesorder set status='2',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now(),audit_id='" + loginuserid + "',audit_by='"
							+ loginUser + "',audit_time=now() where salesorderid='" + mainid + "' and status='1' ");

					ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
							+ companyid + "'," + Pdacommon.getDatalogBillChangefunc("salesorder", "") + ",'作废','" + mainid + "','单据编号：" + orderid + "','" + loginuserid + "','" + loginUser
							+ "',now())");

					ps.executeBatch();
					conn.commit();
					int count = ps.getUpdateCount();
					if (count == 0) {
						state = "3";
						message = "单据已作废，操作失败。";
					} else {
						state = "1";
					}
				} else {
					state = "2";
					message = (scheduleinfo.equals("") ? "" : "商品【" + scheduleinfo + "】已进行排产，即与有效排产单相关联，") + (relativeinfo.equals("") ? "" : "商品【" + relativeinfo + "】已出库，即与有效销售单相关联，") + "不能进行作废操作。";

				}

				conn.setAutoCommit(true);
			}

		} catch (Exception e) {
			// e.printStackTrace();
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

	// 修改销售订单出库状态
	public static JSONObject changeSalesorderStockStatus(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject rt = new JSONObject();
		String orderid = params.getString("orderid");
		String companyid = params.getString("companyid");
		String mainid = params.getString("mainid").replaceAll("'", "");
		String loginuserid = params.getString("loginuserid");
		String loginUser = params.getString("loginUser");
		String oldstatus = params.getString("oldstatus");
		String newstatus = params.getString("newstatus");

		String state = "1";

		try {
			if (mainid.length() != 32) {
				state = "0";
			} else {
				Statement ps = conn.createStatement();
				conn.setAutoCommit(false);
				ps.addBatch("update salesorder set stockstatus='" + newstatus + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where salesorderid='" + mainid
						+ "' ");

				ps.addBatch("update salesorderdetail set schedulstatus='" + newstatus + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where salesorderid='"
						+ mainid + "'");

				ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
						+ companyid + "','28','修改','" + mainid + "','订单编号：" + orderid + "的出库状态原[" + Pdacommon.getSalesStockstatus(oldstatus) + "]变更为[" + Pdacommon.getSalesStockstatus(newstatus)
						+ "]','" + loginuserid + "','" + loginUser + "',now())");

				ps.executeBatch();
				conn.commit();
				conn.setAutoCommit(true);
			}

		} catch (Exception e) {
			 e.printStackTrace();
			state = "0";
			try {
				conn.rollback();
			} catch (Exception e1) {
				// e1.printStackTrace();
			}
		} finally {
			conn.close();
		}
		rt.put("state", state);
		return rt;
	}

	// 修改销售订单明细订单状态
	public static JSONObject changeSalesorderSchedulStatus(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject rt = new JSONObject();
		String orderid = params.getString("orderid");
		String companyid = params.getString("companyid");
		String mainid = params.getString("mainid").replaceAll("'", "");
		String detailid = params.getString("detailid");
		String loginuserid = params.getString("loginuserid");
		String loginUser = params.getString("loginUser");
		String oldstatus = params.getString("oldstatus");
		String newstatus = params.getString("newstatus");
		String iteminfo = params.getString("iteminfo");

		String state = "1";
		String mainstate = "-1";
		try {
			if (mainid.length() != 32) {
				state = "0";
			} else {
				Statement ps = conn.createStatement();
				conn.setAutoCommit(false);

				ps.addBatch("update salesorderdetail set schedulstatus='" + newstatus + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where detailid='" + detailid
						+ "' ");

				// if (newstatus.equals("0")) {
				// ps.addBatch("update salesorder set stockstatus='" + newstatus
				// +
				// "',update_id='" + loginuserid + "',update_by='" + loginUser +
				// "',update_time=now() where salesorderid='" + mainid + "'");
				// } else {
				// ps.addBatch("update salesorder set stockstatus='1',update_id='"
				// +
				// loginuserid + "',update_by='" + loginUser +
				// "',update_time=now() where salesorderid='" + mainid
				// +
				// "' and (select count(detailid) from salesorderdetail where salesorderid='"
				// + mainid +
				// "')=(select count(detailid) from salesorderdetail where salesorderid='"
				// + mainid
				// + "' and schedulstatus<>'0') ");
				//
				// if (newstatus.equals("2")) {
				// ps.addBatch("update salesorder set stockstatus='" + newstatus
				// +
				// "',update_id='" + loginuserid + "',update_by='" + loginUser +
				// "',update_time=now() where salesorderid='" + mainid
				// +
				// "' and (select count(detailid) from salesorderdetail where salesorderid='"
				// + mainid +
				// "')=(select count(detailid) from salesorderdetail where salesorderid='"
				// + mainid
				// + "' and schedulstatus='" + newstatus + "') ");
				// }
				// }

				ps.addBatch("update salesorder p set p.stockstatus=if(p.stockstatus=2,stockstatus,if((select count(*) from salesorderdetail k where k.salesorderid=p.salesorderid and k.schedulstatus=0)>0,0,1)),update_id='"
						+ loginuserid + "',update_by='" + loginUser + "',update_time=now() where p.salesorderid='" + mainid + "' ");

				ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
						+ companyid + "','28','修改','" + mainid + "','订单编号：" + orderid + "的商品[" + iteminfo + "]的订单状态原[" + Pdacommon.getSalesStockstatus(oldstatus) + "]变更为["
						+ Pdacommon.getSalesStockstatus(newstatus) + "]','" + loginuserid + "','" + loginUser + "',now())");

				ps.executeBatch();
				conn.commit();
				conn.setAutoCommit(true);

				String fsql = "select stockstatus from salesorder where salesorderid='" + mainid + "'";
				Object cobject = DataUtils.getValueBySQL(conn, fsql, null);
				if (cobject != null) {
					mainstate = cobject.toString();
				}
			}

		} catch (Exception e) {
			// e.printStackTrace();
			state = "0";
			try {
				conn.rollback();
			} catch (Exception e1) {
				// e1.printStackTrace();
			}
		} finally {
			conn.close();
		}
		rt.put("state", state);
		rt.put("mainstate", mainstate);
		return rt;
	}

	// 修改销售订单原单号
	public static JSONObject changeSalesorderOriginalbill(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject rt = new JSONObject();
		String orderid = params.getString("orderid");
		String companyid = params.getString("companyid");
		String mainid = params.getString("mainid").replaceAll("'", "");
		String loginuserid = params.getString("loginuserid");
		String loginUser = params.getString("loginUser");
		String oldoriginalbill = params.getString("oldoriginalbill").replaceAll("'", "''");
		String neworiginalbill = params.getString("neworiginalbill").replaceAll("'", "''");
		String state = "1";
		String stype = params.getString("stype");
		String detailid = params.getString("detailid");
		Integer moneybit = params.getInteger("moneybit");
		String customerid = params.getString("customerid");

		String goods_number = params.getString("goods_number");

		String info = params.getString("info");

		Boolean showprice = params.getBoolean("showprice");
		Integer countbit = params.getInteger("countbit");
		countbit = (countbit == null ? 2 : countbit);
		moneybit = (moneybit == null ? 2 : moneybit);

		try {
			if (mainid.length() != 32 || (detailid != null && !detailid.equals("") && detailid.replaceAll("'", "").length() != 32)) {
				state = "0";
			} else {
				Statement ps = conn.createStatement();
				conn.setAutoCommit(false);
				String log = "";
				String fbill = "28";
				if (stype != null && stype.equals("1")) {
					ps.addBatch("update salesorderdetail set plandate='" + neworiginalbill + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where detailid='"
							+ detailid + "' ");
					ps.addBatch("update t_order set plandate='" + neworiginalbill + "' where salesorderdetailid='" + detailid + "' ");

					log = "序号为" + goods_number + "的记录的原计划交货日期[";
				} else if (stype != null && stype.equals("2")) {
					ps.addBatch("update salesorderdetail set schedulcount=" + neworiginalbill + ",update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where detailid='"
							+ detailid + "' ");
					log = "序号为" + goods_number + "的记录原待排产数[";
				} else if (stype != null && stype.equals("3")) {
					ps.addBatch("update salesorderdetail set outsourcingcount=" + neworiginalbill + ",update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where detailid='"
							+ detailid + "' ");
					log = "序号为" + goods_number + "的记录原需加工数量[";
				} else if (stype != null && stype.equals("4")) {// 采购订单计划到货日期
					ps.addBatch("update purchaseorderdetail set plandate='" + neworiginalbill + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where detailid='"
							+ detailid + "' ");
					log = "序号为" + goods_number + "的记录原计划到货日期[";
					fbill = "27";
				} else if (stype != null && stype.equals("6")) {// 采购申请单期望到货日期
					ps.addBatch("update purchasedetail set plandate='" + neworiginalbill + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where detailid='"
							+ detailid + "' ");
					log = "序号为" + goods_number + "的记录原计划到货日期[";
					fbill = "49";
				} else if (stype != null && stype.equals("282")) {// 销售备注
					ps.addBatch("update salesorder set remark='" + neworiginalbill + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where salesorderid='" + mainid
							+ "' ");
					log = "的订单原备注[";
					fbill = "28";
				}  else if (stype != null && stype.equals("289")) {// 销售币种
					ps.addBatch("update salesorder set currency='" + neworiginalbill + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where salesorderid='" + mainid
							+ "' ");
					log = "的订单原币种[";
					fbill = "28";
				} else if (stype != null && stype.equals("283")) {// 销售记录备注
					ps.addBatch("update salesorderdetail set remark='" + neworiginalbill + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where detailid='"
							+ detailid + "' ");
					log = "序号为" + goods_number + "的记录原备注[";
					fbill = "28";
				} else if (stype != null && stype.equals("491")) {// 采购申请单原单号
					ps.addBatch("update purchasedetail set originalbill='" + neworiginalbill + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where purchaseid='"
							+ mainid + "' ");

					ps.addBatch("update purchase set originalbill='" + neworiginalbill + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where purchaseid='"
							+ mainid + "' ");

					log = "的原单号原[";
					fbill = "49";
				} else if (stype != null && stype.equals("492")) {// 采购申请单备注
					ps.addBatch("update purchase set remark='" + neworiginalbill + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where purchaseid='" + mainid
							+ "' ");
					log = "的订单原备注[";
					fbill = "49";
				} else if (stype != null && stype.equals("493")) {// 采购申请单明细备注
					ps.addBatch("update purchasedetail set remark='" + neworiginalbill + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where detailid='"
							+ detailid + "' ");
					log = "序号为" + goods_number + "的记录原备注[";
					fbill = "49";
				} else if (stype != null && stype.equals("495")) {// 采购申请单明细总数量修改
					ps.addBatch("update purchasedetail set count='" + neworiginalbill + "', "
							+ "orderstatus = if(orderstatus=2,orderstatus,if(ordercount>=" + neworiginalbill + ",1,0)), "
						    + "update_id='" + loginuserid + "', update_by='" + loginUser + "', update_time=now() "
						    + "where detailid = '" + detailid + "' ");
				
					ps.addBatch("update purchase p set "
						    + "count = round((select sum(count) from purchasedetail where purchaseid = '" + mainid + "'),"+countbit+"), "
						    + "orderstatus = if(orderstatus=2,orderstatus,if((select count(*) from purchasedetail k  where k.purchaseid=p.purchaseid and k.orderstatus=0)>0,0,1)), "
						    + "update_id = '" + loginuserid + "', update_by = '" + loginUser + "', update_time = now() "
						    + "where p.purchaseid = '" + mainid + "' ");
					log = "序号为" + goods_number + "的记录总数量[";
					fbill = "49";

				} else if (stype != null && stype.equals("271")) {// 采购原单号
					ps.addBatch("update purchaseorderdetail set originalbill='" + neworiginalbill + "',update_id='" + loginuserid + "',update_by='" + loginUser
							+ "',update_time=now() where purchaseorderid='" + mainid + "' ");

					ps.addBatch("update purchaseorder set originalbill='" + neworiginalbill + "',update_id='" + loginuserid + "',update_by='" + loginUser
							+ "',update_time=now() where purchaseorderid='" + mainid + "' ");

					log = "的原单号原[";
					fbill = "27";
				} else if (stype != null && stype.equals("272")) {// 采购备注
					ps.addBatch("update purchaseorder set remark='" + neworiginalbill + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where purchaseorderid='"
							+ mainid + "' ");
					log = "的订单原备注[";
					fbill = "27";
				} else if (stype != null && stype.equals("273")) {// 采购记录备注
					ps.addBatch("update purchaseorderdetail set remark='" + neworiginalbill + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where detailid='"
							+ detailid + "' ");
					log = "序号为" + goods_number + "的记录原备注[";
					fbill = "27";
				} else if (stype != null && stype.equals("275")) {// 采购订单金额修改
					moneybit = (moneybit == null) ? 2 : moneybit;
					String[] tempdata = neworiginalbill.split(",");

					// System.out.println(oldoriginalbill + " " +
					// neworiginalbill);

					ps.addBatch("update ordermonth om,purchaseorderdetail sd set "
							+ (tempdata.length > 2 ? " om.purchasecount=round(om.purchasecount-sd.count+" + tempdata[2] + "," + countbit + ")," : "")
							+ " om.purchasemoney=round(om.purchasemoney-sd.total+" + tempdata[1] + "," + moneybit + ")" + " where om.companyid=sd.companyid and sd.detailid='" + detailid
							+ "' and sd.itemid=om.itemid and om.syear=year(sd.operate_time) and om.smonth=month(sd.operate_time) and sd.batchno=om.batchno ");

					ps.addBatch("update purchaseorderdetail set price=" + tempdata[0] + ",total=" + tempdata[1] + ",count=" + tempdata[2] + ",taxrate=" + tempdata[3] + ",tax=" + tempdata[4]
							+ ",taxprice=" + tempdata[5] + ",taxmoney=" + tempdata[6] + ",update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where detailid='" + detailid
							+ "' ");

					ps.addBatch("update purchaseorder set  count=round(ifnull((select sum(sd.count) from purchaseorderdetail sd where sd.purchaseorderid='" + mainid + "' and sd.status='1'),0),"
							+ countbit + "),stockstatus=if(stockstatus='2','2',if((select 1 from purchaseorderdetail where purchaseorderid='" + mainid
							+ "' and status='1' and incount>0 and incount<count limit 1),'-1',if((select sum(incount) from purchaseorderdetail where purchaseorderid='" + mainid
							+ "' and status='1' )=0,'0','1')))," + " total=round(ifnull((select sum(sd.total) from purchaseorderdetail sd where sd.purchaseorderid='" + mainid
							+ "' and sd.status='1'),0)," + moneybit + ")," + " totaltax=round(ifnull((select sum(sd.tax) from purchaseorderdetail sd where sd.purchaseorderid='" + mainid
							+ "' and sd.status='1'),0)," + moneybit + ")," + " totalmoney=round(ifnull((select sum(sd.taxmoney) from purchaseorderdetail sd where sd.purchaseorderid='" + mainid
							+ "' and sd.status='1'),0)," + moneybit + ")" + ",update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where purchaseorderid='" + mainid + "' ");

					//采购申请单更新
					ps.addBatch("update purchasedetail p set p.ordercount=round(ifnull((select sum(s.count) from purchaseorderdetail s where  s.relationdetailid=p.detailid and s.status<>'2'  group by s.relationdetailid),0),"
							+ countbit + "),p.orderstatus=if(p.orderstatus=2,orderstatus,if(p.ordercount>=p.count,1,0)) where  p.detailid=(select relationdetailid from purchaseorderdetail where detailid='" + detailid+"' and relationtype=0) and p.status='1' ");

					ps.addBatch("update purchase p set p.ordercount=round(ifnull((select sum(s.count) from purchaseorderdetail s where  s.relationmainid=p.purchaseid and s.status<>'2'  group by s.relationmainid),0),"
							+ countbit
							+ "),p.orderstatus=if(p.orderstatus=2,orderstatus,if((select count(*) from purchasedetail k where k.purchaseid=p.purchaseid and k.orderstatus=0)>0,0,1)) where p.purchaseid=(select relationmainid from purchaseorderdetail where detailid='" + detailid+"' and relationtype=0) and p.status='1'");
					
					//补料申请单更新
					ps.addBatch("update apply_materialdetail p set p.relationdetailid='',p.relationorderid='',p.relationmainid='',p.processstatus=1   where p.detailid=(select relationdetailid from purchaseorderdetail where detailid='" + detailid+"' and relationtype=1) ");
					ps.addBatch("update apply_materialdetail p,purchaseorderdetail pd set p.relationdetailid=pd.detailid,p.relationorderid=pd.orderid,p.relationmainid=pd.purchaseorderid,p.processstatus=2  where p.detailid=(select relationdetailid from purchaseorderdetail where detailid='" + detailid+"' and relationtype=1) and p.status='1' and p.detailid = pd.relationdetailid and pd.relationtype=1 and pd.status<>2 ");

					ps.addBatch("update apply_material p set p.processstatus1=if((select count(*) from apply_materialdetail k where k.mainid=p.mainid and k.stype=332 and k.processstatus=1)>0,1,2)   where p.mainid=(select relationmainid from purchaseorderdetail where detailid='" + detailid+"' and relationtype=1) ");

					log = "序号为" + goods_number + "的记录原单价,原金额,原订单数量,原税率(%),原税额,原含税单价,原价税合计[";
					fbill = "27";

				} else if (stype != null && stype.equals("5")) {// 委外加工单计划交货日期
					ps.addBatch("update outsourcingdetail set plandate='" + neworiginalbill + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where detailid='"
							+ detailid + "' ");
					log = "序号为" + goods_number + "的记录原计划交货日期[";
					fbill = "43";
				} else if (stype != null && stype.equals("431")) {// 委外加工备注1
					ps.addBatch("update outsourcing set remark1='" + neworiginalbill + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where outsourcingid='"
							+ mainid + "' ");
					log = "的委外加工单原备注1[";
					fbill = "43";
				} else if (stype != null && stype.equals("432")) {// 委外加工备注2
					ps.addBatch("update outsourcing set remark2='" + neworiginalbill + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where outsourcingid='"
							+ mainid + "' ");
					log = "的委外加工单原备注2[";
					fbill = "43";
				} else if (stype != null && stype.equals("433")) {// 委外加工产品备注
					ps.addBatch("update outsourcingdetail set remark='" + neworiginalbill + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where detailid='"
							+ detailid + "' ");
					log = "序号为" + goods_number + "的产品记录原备注[";
					fbill = "43";
				} else if (stype != null && stype.equals("434")) {// 委外加工材料备注
					ps.addBatch("update outsourcingdetail set remark='" + neworiginalbill + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where detailid='"
							+ detailid + "' ");
					log = "序号为" + goods_number + "的产品材料原备注[";
					fbill = "43";
				} else if (stype != null && stype.equals("435")) {// 委外加工数量金额
					String[] newdata = neworiginalbill.split(",");
					ps.addBatch("update outsourcingdetail set count=" + newdata[0] + ",price=" + newdata[1] + ",total=" + newdata[2] + ",taxrate=" + newdata[3] + ",tax=" + newdata[4] + ",taxprice="
							+ newdata[5] + ",taxmoney=" + newdata[6] + ",billstatus = if(billstatus='0' and count<=incount,'1',if(billstatus='1' and count>incount,'0',billstatus)),update_id='"
							+ loginuserid + "',update_by='" + loginUser + "',update_time=now() where detailid='" + detailid + "' ");
					ps.addBatch("update outsourcing set count=round((select sum(count) from outsourcingdetail where outsourcingid='" + mainid + "'  and stype='221')," + countbit
							+ "),total=round((select sum(total) from outsourcingdetail where outsourcingid='" + mainid + "' and stype='221')," + moneybit
							+ "), totaltax=round((select sum(tax) from outsourcingdetail where outsourcingid='" + mainid + "' and stype='221')," + moneybit + "),"
							+ " totalmoney=round((select sum(taxmoney) from outsourcingdetail where outsourcingid='" + mainid + "' and stype='221')," + moneybit + ") ,update_id='" + loginuserid
							+ "',update_by='" + loginUser + "',update_time=now(),billstatus=if((select count(*) from outsourcingdetail where outsourcingid='" + mainid
							+ "' and status='1' and billstatus='0' and stype='221')>0,if(billstatus='2',billstatus,'0'),if(billstatus='0','1',billstatus)) where outsourcingid='" + mainid + "' ");
					
					
					//委外加工数量变动，对应销售订单也变动
					ps.addBatch("update salesorderdetail set hadoutsourcing=round((select sum(count) from outsourcingdetail where relationdetailid=salesorderdetail.detailid  and stype='221' and status<>'2')," + countbit
							+ ")  where detailid=(select relationdetailid from outsourcingdetail where detailid='" + detailid+"')" );
					
					
					if (showprice != null && showprice) {
						log = "序号为" + goods_number + "的产品记录原加工数量,加工单价,加工费,原税率(%),原税额,原含税单价,原价税合计[";
					} else {
						oldoriginalbill = oldoriginalbill.split(",")[0];
						neworiginalbill = newdata[0];
						log = "序号为" + goods_number + "的产品记录原加工数量[";
					}
					fbill = "43";
				} else if (stype != null && stype.equals("436")) {// 委外加工材料 数量
					ps.addBatch("update outsourcingdetail set count=" + neworiginalbill + ",update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where detailid='"
							+ detailid + "' ");
					ps.addBatch("update outsourcing set materiel=round((select sum(count) from outsourcingdetail where outsourcingid='" + mainid + "'  and stype='222')," + countbit + ") ,update_id='"
							+ loginuserid + "',update_by='" + loginUser + "',update_time=now() where outsourcingid='" + mainid + "'");

					log = "序号为" + goods_number + "的产品材料原材料数量[";
					fbill = "43";
				} else if (stype != null && stype.equals("437")) {// 委外加工产品备注
					ps.addBatch("update outsourcingdetail set myremark='" + neworiginalbill + "',myuserid='" + loginuserid + "',myusername='" + loginUser + "',mydate=now() where detailid='"
							+ detailid + "' ");
					if (!neworiginalbill.equals("")) {
						ps.addBatch("INSERT INTO `sendmessage` (`id`, `companyid`, `bill_type`, `customerid`, `billid`, `detailid`, `fid`, `billorderid`, `sendtype`, `cmname`, `myname`, `sendinfo`, `senddate`, `senduserid`, `sendusername`, `confirmdate`, `confirmuserid`, `confirmusername`, `confirminfo`, `status`) VALUES"
								+ "('"
								+ Common.getUpperUUIDString()
								+ "', '"
								+ companyid
								+ "', '221', '"
								+ customerid
								+ "', '"
								+ mainid
								+ "', '"
								+ detailid
								+ "', '', '"
								+ orderid
								+ "', 1,'','', '产品明细" + info + "<br/><strong>" + neworiginalbill + "</strong>', now(), '" + loginuserid + "', '" + loginUser + "', NULL, '', '', '', 1)");
					}
					log = "序号为" + goods_number + "的产品记录原我方备注[";
					fbill = "43";
				} else if (stype != null && stype.equals("438")) {// 委外加工材料我方备注
					ps.addBatch("update outsourcingdetail set myremark='" + neworiginalbill + "',myuserid='" + loginuserid + "',myusername='" + loginUser + "',mydate=now() where detailid='"
							+ detailid + "' ");

					if (!neworiginalbill.equals("")) {
						ps.addBatch("INSERT INTO `sendmessage` (`id`, `companyid`, `bill_type`, `customerid`, `billid`, `detailid`, `fid`, `billorderid`, `sendtype`, `cmname`, `myname`, `sendinfo`, `senddate`, `senduserid`, `sendusername`, `confirmdate`, `confirmuserid`, `confirmusername`, `confirminfo`, `status`) VALUES"
								+ "('"
								+ Common.getUpperUUIDString()
								+ "', '"
								+ companyid
								+ "', '222', '"
								+ customerid
								+ "', '"
								+ mainid
								+ "', '"
								+ detailid
								+ "', '', '"
								+ orderid
								+ "', 1,'','', '材料明细" + info + "<br/><strong>" + neworiginalbill + "</strong>', now(), '" + loginuserid + "', '" + loginUser + "',  NULL, '', '', '', 1)");
					}

					log = "序号为" + goods_number + "的产品材料原我方备注[";
					fbill = "43";
				} else if (stype != null && stype.equals("10")) {// 销售订单金额修改
					moneybit = (moneybit == null) ? 2 : moneybit;
					String[] tempdata = neworiginalbill.split(",");

					// System.out.println(oldoriginalbill + " " +
					// neworiginalbill);

					ps.addBatch("update ordermonth om,salesorderdetail sd set " + (tempdata.length > 2 ? " om.salescount=round(om.salescount-sd.count+" + tempdata[2] + "," + countbit + ")," : "")
							+ " om.salesmoney=round(om.salesmoney-sd.total+" + tempdata[1] + "," + moneybit + ")" + " where om.companyid=sd.companyid and sd.detailid='" + detailid
							+ "' and sd.itemid=om.itemid and om.syear=year(sd.operate_time) and om.smonth=month(sd.operate_time) and sd.batchno=om.batchno ");

					ps.addBatch("update salesorderdetail set price=" + tempdata[0] + ",total=" + tempdata[1] + ",count=" + tempdata[2] + ",taxrate=" + tempdata[3] + ",tax=" + tempdata[4]
							+ ",taxprice=" + tempdata[5] + ",taxmoney=" + tempdata[6] + ",schedulcount=if(count>scheduledcount,round(count-scheduledcount," + countbit
							+ "),0),schedulstatus=if(count<=outcount,'1',if(schedulstatus='2','2','0'))" + ",update_id='" + loginuserid + "',update_by='" + loginUser
							+ "',update_time=now() where detailid='" + detailid + "' ");

					ps.addBatch("update salesorder set count=round(ifnull((select sum(sd.count) from salesorderdetail sd where sd.salesorderid='" + mainid + "' and sd.status='1'),0)," + countbit
							+ "),stockstatus=if(stockstatus='2','2',if((select 1 from salesorderdetail where salesorderid='" + mainid + "' and schedulstatus='0' limit 1),'0','1')),"
							+ " total=round(ifnull((select sum(sd.total) from salesorderdetail sd where sd.salesorderid='" + mainid + "' and sd.status='1'),0)," + moneybit + "),"
							+ " totaltax=round(ifnull((select sum(sd.tax) from salesorderdetail sd where sd.salesorderid='" + mainid + "' and sd.status='1'),0)," + moneybit + "),"
							+ " totalmoney=round(ifnull((select sum(sd.taxmoney) from salesorderdetail sd where sd.salesorderid='" + mainid + "' and sd.status='1'),0)," + moneybit + "),"
							+ " update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where salesorderid='" + mainid + "' ");

				
					
					
					
					log = "序号为" + goods_number + "的记录原单价,原金额,原订单数量,原税率(%),原税额,原含税单价,原价税合计[";
					fbill = "28";
				} else if (stype != null && stype.equals("2902")) {// 报价单报价有效期
					ps.addBatch("update quotation set plandate='" + neworiginalbill + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where quotationid='" + mainid
							+ "' ");
					ps.addBatch("update quotationdetail set plandate='" + neworiginalbill + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where quotationid='"
							+ mainid + "' ");
					log = "的报价单原报价有效期[";
					fbill = "80";
				} else if (stype != null && stype.equals("2903")) {// 报价备注
					ps.addBatch("update quotation set remark='" + neworiginalbill + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where quotationid='" + mainid
							+ "' ");
					log = "的报价单原备注[";
					fbill = "80";
				} else if (stype != null && stype.equals("2904")) {// 报价币种
					ps.addBatch("update quotation set currency='" + neworiginalbill + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where quotationid='" + mainid
							+ "' ");
					log = "的报价单原币种[";
					fbill = "80";
				} else if (stype != null && stype.equals("2910")) {// 报价备注
					ps.addBatch("update quotationdetail set remark='" + neworiginalbill + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where detailid='"
							+ detailid + "' ");
					log = "序号为" + goods_number + "的记录原备注[";
					fbill = "80";
				} else if (stype != null && stype.equals("2911")) {// 报价备注
					ps.addBatch("update quotationdetail set remark1='" + neworiginalbill + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where detailid='"
							+ detailid + "' ");
					log = "序号为" + goods_number + "的记录原备注1[";
					fbill = "80";
				} else if (stype != null && stype.equals("2912")) {// 报价备注
					ps.addBatch("update quotationdetail set remark2='" + neworiginalbill + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where detailid='"
							+ detailid + "' ");
					log = "序号为" + goods_number + "的记录原备注2[";
					fbill = "80";
				} else if (stype != null && stype.equals("2913")) {// 报价备注
					ps.addBatch("update quotationdetail set remark3='" + neworiginalbill + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where detailid='"
							+ detailid + "' ");
					log = "序号为" + goods_number + "的记录原备注3[";
					fbill = "80";
				} else if (stype != null && stype.equals("2901")) {// 销售订单金额修改
					moneybit = (moneybit == null) ? 2 : moneybit;
					String[] tempdata = neworiginalbill.split(",");

					ps.addBatch("update quotationdetail set price=" + tempdata[0] + ",total=" + tempdata[1] + ",count=" + tempdata[2] + ",taxrate=" + tempdata[3] + ",tax=" + tempdata[4]
							+ ",taxprice=" + tempdata[5] + ",taxmoney=" + tempdata[6] + ",update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where detailid='" + detailid
							+ "' ");

					ps.addBatch("update quotation set count=round(ifnull((select sum(sd.count) from quotationdetail sd where sd.quotationid='" + mainid + "' and sd.status='1'),0)," + countbit + "),"
							+ " total=round(ifnull((select sum(sd.total) from quotationdetail sd where sd.quotationid='" + mainid + "' and sd.status='1'),0)," + moneybit + "),"
							+ " totaltax=round(ifnull((select sum(sd.tax) from quotationdetail sd where sd.quotationid='" + mainid + "' and sd.status='1'),0)," + moneybit + "),"
							+ " totalmoney=round(ifnull((select sum(sd.taxmoney) from quotationdetail sd where sd.quotationid='" + mainid + "' and sd.status='1'),0)," + moneybit + "),"
							+ " update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where quotationid='" + mainid + "' ");

					log = "序号为" + goods_number + "的记录原单价,原金额,原报价数量,原税率(%),原税额,原含税单价,原价税合计[";
					fbill = "80";
				} else {// stype=0或null
					ps.addBatch("update salesorderdetail set originalbill='" + neworiginalbill + "',update_id='" + loginuserid + "',update_by='" + loginUser
							+ "',update_time=now() where salesorderid='" + mainid + "' ");

					ps.addBatch("update salesorder set originalbill='" + neworiginalbill + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where salesorderid='"
							+ mainid + "' ");

					ps.addBatch("update t_order set originalbill='" + neworiginalbill + "' where salesorderid='" + mainid + "' ");
					log = "的原单号原[";
					fbill = "28";
				}

				ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
						+ companyid + "'," + fbill + ",'修改','" + mainid + "','单据编号：" + orderid + log + oldoriginalbill + "]变更为[" + neworiginalbill + "]','" + loginuserid + "','" + loginUser
						+ "',now())");

				ps.executeBatch();
				conn.commit();
				conn.setAutoCommit(true);
			}

		} catch (Exception e) {
			 e.printStackTrace();
			state = "0";
			try {
				conn.rollback();
			} catch (Exception e1) {
				// e1.printStackTrace();
			}
		} finally {
			conn.close();
		}
		rt.put("state", state);
		return rt;
	}

	// 销售订单批量审核
	public static JSONObject auditBatchSalesorderFunction(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject rt = new JSONObject();
		String companyid = params.getString("companyid");
		String mainids = params.getString("mainids");
		String loginuserid = params.getString("loginuserid");
		String loginUser = params.getString("loginUser");

		String state = "0";
		String message = "";
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String orderids = "";
		String salesorderids = "";
		int auditcount = 0;

		try {
			int countbit = 0;
			int moneybit = 2;
			Table companytalbe = DataUtils.queryData(conn, "select pricebit,countbit,moneybit from s_company_config where company_id='" + companyid + "'", null, null, null, null);
			if (companytalbe.getRows().size() > 0) {
				countbit = companytalbe.getRows().get(0).getInteger("countbit");
				moneybit = companytalbe.getRows().get(0).getInteger("moneybit");
			}

			Statement ps = conn.createStatement();
			conn.setAutoCommit(false);
			String ordermonth = "insert into ordermonth (monthid,companyid,itemid,sdate,syear,smonth, salescount, salesmoney,batchno) VALUES ('";

			List<Object> fparams = new ArrayList<>();

			int m = 0;
			String[] idssarr = mainids.replaceAll("'", "").split(",");
			String temp = "";
			for (m = 0; m < idssarr.length; m++) {
				temp = temp + (temp.equals("") ? "" : ",") + "?";
				fparams.add(idssarr[m]);
			}

			String sql = "select salesorderid,orderid,operate_time from salesorder where  salesorderid in (" + temp + ") and status='0'";

			System.out.println(sql);

			Table table = DataUtils.queryData(conn, sql, fparams, null, null, null);
			if (table.getRows().size() > 0) {
				Iterator<Row> iteratordata = table.getRows().iterator();

				while (iteratordata.hasNext()) {
					Row info = iteratordata.next();
					String salesorderid = info.getString("salesorderid");
					String orderid = info.getString("orderid");
					String operate_time = format.format(info.getDate("operate_time"));
					String[] arr = operate_time.split("-");
					String sdate = arr[0] + "-" + arr[1] + "-" + "01"; // 业务日期转成每个月1日来记录itemmonth数据

					int syear = Integer.parseInt(arr[0]);
					int smonth = Integer.parseInt(arr[1]);

					String subsql = "select count,total,itemid,batchno from salesorderdetail where  salesorderid = '" + salesorderid + "' and status='0'";
					Table subtable = DataUtils.queryData(conn, subsql, null, null, null, null);
					Iterator<Row> subiteratordata = subtable.getRows().iterator();
					if (subtable.getRows().size() > 0) {
						salesorderids = salesorderids + (salesorderids.equals("") ? "" : ",") + "'" + salesorderid + "'";
						orderids = orderids + (orderids.equals("") ? "" : ",") + orderid;
						auditcount++;
					}

					while (subiteratordata.hasNext()) {
						Row subinfo = subiteratordata.next();
						double count = Double.parseDouble(subinfo.getValue("count").toString());
						double total = Double.parseDouble(subinfo.getValue("total").toString());
						String itemid = subinfo.getString("itemid");
						String batchno = erpscan.save.Pdainvalid.transformSpecialInfo(subinfo.getString("batchno"));
						String details = ordermonth + Common.getUpperUUIDString() + "','" + companyid + "','" + itemid + "','" + sdate + "'," + syear + "," + smonth + "," + count + "," + total + ",'"
								+ batchno + "') on duplicate key update  salescount= round(salescount+" + count + "," + countbit + "), salesmoney= round(salesmoney+" + total + "," + moneybit + ")";

						ps.addBatch(details);
					}

				}
			}

			if (auditcount > 0) {
				ps.addBatch("update  salesorderdetail set schedulcount=count,status='1',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now(),audit_id='" + loginuserid
						+ "',audit_by='" + loginUser + "',audit_time=now() where  salesorderid in (" + salesorderids + ")");
				ps.addBatch("update  salesorder set status='1',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now(),audit_id='" + loginuserid + "',audit_by='" + loginUser
						+ "',audit_time=now() where  salesorderid in (" + salesorderids + ")");

				ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
						+ companyid + "',28,'批量审核','','单据编号：" + orderids + "共" + auditcount + "条销售订单','" + loginuserid + "','" + loginUser + "',now())");

				ps.executeBatch();
				conn.commit();
				state = "1";
				message = "已成功审核" + auditcount + "条销售订单";
			} else {
				message = "没有<未审核>状态的订单可操作";
			}

			conn.setAutoCommit(true);

		} catch (Exception e) {
			// e.printStackTrace();
			message = "批量审核操作失败，请稍后再试。";
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

	// 销售订单批量删除
	public static JSONObject delBatchSalesorderFunction(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject rt = new JSONObject();
		String companyid = params.getString("companyid");
		String mainids = params.getString("mainids");
		String loginuserid = params.getString("loginuserid");
		String loginUser = params.getString("loginUser");

		String state = "0";
		String message = "";
		String orderids = "";
		String salesorderids = "";
		int auditcount = 0;

		try {
			Statement ps = conn.createStatement();
			conn.setAutoCommit(false);
			List<Object> fparams = new ArrayList<>();

			int m = 0;
			String[] idssarr = mainids.replaceAll("'", "").split(",");
			String temp = "";
			for (m = 0; m < idssarr.length; m++) {
				temp = temp + (temp.equals("") ? "" : ",") + "?";
				fparams.add(idssarr[m]);
			}
			String sql = "select salesorderid,orderid from salesorder where  salesorderid in (" + temp + ") and status='0'";
			Table table = DataUtils.queryData(conn, sql, fparams, null, null, null);
			if (table.getRows().size() > 0) {
				Iterator<Row> iteratordata = table.getRows().iterator();

				while (iteratordata.hasNext()) {
					Row info = iteratordata.next();
					String salesorderid = info.getString("salesorderid");
					String orderid = info.getString("orderid");

					salesorderids = salesorderids + (salesorderids.equals("") ? "" : ",") + "'" + salesorderid + "'";
					orderids = orderids + (orderids.equals("") ? "" : ",") + orderid;
					auditcount++;

				}
			}

			if (auditcount > 0) {
				ps.addBatch("delete from t_file where fstatus=2 and detail_id in ( select detailid from salesorderdetail  where salesorderid in (" + salesorderids + "))");

				ps.addBatch("delete from salesorderdetail  where  salesorderid in (" + salesorderids + ")");
				ps.addBatch("delete from  salesorder  where  salesorderid in (" + salesorderids + ")");

				ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
						+ companyid + "',28,'批量删除','','单据编号：" + orderids + "共" + auditcount + "条销售订单','" + loginuserid + "','" + loginUser + "',now())");

				ps.executeBatch();
				conn.commit();
				state = "1";
				message = "已成功删除" + auditcount + "条销售订单";
			} else {
				message = "没有<未审核>状态的订单可操作";
			}

			conn.setAutoCommit(true);

		} catch (Exception e) {
			// e.printStackTrace();
			message = "批量删除操作失败，请稍后再试。";
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

	// 采购订单批量审核
	public static JSONObject auditBatchPurchaseorderFunction(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject rt = new JSONObject();
		String companyid = params.getString("companyid");
		String mainids = params.getString("mainids");
		String loginuserid = params.getString("loginuserid");
		String loginUser = params.getString("loginUser");

		String state = "0";
		String message = "";
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String orderids = "";
		String purchaseorderids = "";
		String relatinmainids = "";
		int auditcount = 0;

		try {
			int countbit = 0;
			int moneybit = 2;
			Table companytalbe = DataUtils.queryData(conn, "select pricebit,countbit,moneybit from s_company_config where company_id='" + companyid + "'", null, null, null, null);
			if (companytalbe.getRows().size() > 0) {
				countbit = companytalbe.getRows().get(0).getInteger("countbit");
				moneybit = companytalbe.getRows().get(0).getInteger("moneybit");
			}

			Statement ps = conn.createStatement();
			conn.setAutoCommit(false);
			String ordermonth = "insert into ordermonth (monthid,companyid,itemid,sdate,syear,smonth,purchasecount,purchasemoney,batchno) VALUES ('";
			// 2020-12-28 增加houseid填写验证

			List<Object> fparams = new ArrayList<>();

			int m = 0;
			String[] idssarr = mainids.replaceAll("'", "").split(",");
			String temp = "";
			for (m = 0; m < idssarr.length; m++) {
				temp = temp + (temp.equals("") ? "" : ",") + "?";
				fparams.add(idssarr[m]);
			}

			String sql = "select purchaseorderid,orderid,operate_time,houseid from purchaseorder where  purchaseorderid in (" + temp + ") and status='0'";
			Table table = DataUtils.queryData(conn, sql, fparams, null, null, null);
			// 2020-12-28 增加houseid填写验证
			int nothouse = 0;
			if (table.getRows().size() > 0) {
				Iterator<Row> iteratordata = table.getRows().iterator();

				while (iteratordata.hasNext()) {
					Row info = iteratordata.next();
					String purchaseorderid = info.getString("purchaseorderid");
					String orderid = info.getString("orderid");
					String operate_time = format.format(info.getDate("operate_time"));
					String[] arr = operate_time.split("-");
					String sdate = arr[0] + "-" + arr[1] + "-" + "01"; // 业务日期转成每个月1日来记录itemmonth数据

					int syear = Integer.parseInt(arr[0]);
					int smonth = Integer.parseInt(arr[1]);

					// 2020-12-28 增加houseid填写验证
					String houseid = info.getString("houseid");
					if (houseid == null || houseid.equals("")) {
						nothouse++;
					} else {

						String subsql = "select count,total,itemid,batchno,relationdetailid,relationmainid from purchaseorderdetail where  purchaseorderid = '" + purchaseorderid + "' and status='0'";
						Table subtable = DataUtils.queryData(conn, subsql, null, null, null, null);
						Iterator<Row> subiteratordata = subtable.getRows().iterator();
						if (subtable.getRows().size() > 0) {
							purchaseorderids = purchaseorderids + (purchaseorderids.equals("") ? "" : ",") + "'" + purchaseorderid + "'";
							orderids = orderids + (orderids.equals("") ? "" : ",") + orderid;
							auditcount++;
						}

						while (subiteratordata.hasNext()) {
							Row subinfo = subiteratordata.next();
							double count = Double.parseDouble(subinfo.getValue("count").toString());
							double total = Double.parseDouble(subinfo.getValue("total").toString());
							String itemid = subinfo.getString("itemid");
							String batchno = erpscan.save.Pdainvalid.transformSpecialInfo(subinfo.getString("batchno"));
							String relationdetailid = subinfo.getString("relationdetailid");
							String relationmainid = subinfo.getString("relationmainid");
							String details = ordermonth + Common.getUpperUUIDString() + "','" + companyid + "','" + itemid + "','" + sdate + "'," + syear + "," + smonth + "," + count + "," + total
									+ ",'" + batchno + "') on duplicate key update  purchasecount= round(purchasecount+" + count + "," + countbit + "), purchasemoney= round(purchasemoney+" + total
									+ "," + moneybit + ")";
							if (!relationmainid.equals("") && relatinmainids.indexOf(relationmainid) == -1) {
								purchaseorderids = purchaseorderids + (purchaseorderids.equals("") ? "" : ",") + "'" + purchaseorderid + "'";
							}

							ps.addBatch(details);
						}
					}
				}
			}

			if (auditcount > 0) {
				ps.addBatch("update  purchaseorderdetail set status='1',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now(),audit_id='" + loginuserid + "',audit_by='"
						+ loginUser + "',audit_time=now() where  purchaseorderid in (" + purchaseorderids + ")");
				ps.addBatch("update  purchaseorder set status='1',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now(),audit_id='" + loginuserid + "',audit_by='"
						+ loginUser + "',audit_time=now() where  purchaseorderid in (" + purchaseorderids + ")");

				ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
						+ companyid + "',27,'批量审核','','单据编号：" + orderids + "共" + auditcount + "条采购订单','" + loginuserid + "','" + loginUser + "',now())");

				ps.executeBatch();
				conn.commit();
				state = "1";
				// 2020-12-28 增加houseid填写验证
				message = "已成功审核" + auditcount + "条采购订单" + (nothouse > 0 ? "，有" + nothouse + "条采购订单的仓库没有值，不能审核。" : "");
			} else {
				// 2020-12-28 增加houseid填写验证
				message = (nothouse > 0 ? "有" + nothouse + "条采购订单的仓库没有值，不能审核。" : "没有<未审核>状态的订单可操作");
			}

			conn.setAutoCommit(true);

		} catch (Exception e) {
			// e.printStackTrace();
			message = "批量审核操作失败，请稍后再试。";
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

	// 采购订单批量删除
	public static JSONObject delBatchPurchaseorderFunction(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject rt = new JSONObject();
		String companyid = params.getString("companyid");
		String mainids = params.getString("mainids");
		String loginuserid = params.getString("loginuserid");
		String loginUser = params.getString("loginUser");
		double countbit = params.getDoubleValue("countbit");

		String state = "0";
		String message = "";
		String orderids = "";
		String purchaseorderids = "";
		int auditcount = 0;

		try {
			Statement ps = conn.createStatement();
			conn.setAutoCommit(false);

			List<Object> fparams = new ArrayList<>();

			int m = 0;
			String[] idssarr = mainids.replaceAll("'", "").split(",");
			String temp = "";
			for (m = 0; m < idssarr.length; m++) {
				temp = temp + (temp.equals("") ? "" : ",") + "?";
				fparams.add(idssarr[m]);
			}

			String sql = "select purchaseorderid,orderid from purchaseorder where  purchaseorderid in (" + temp + ") and status='0'";
			Table table = DataUtils.queryData(conn, sql, fparams, null, null, null);
			if (table.getRows().size() > 0) {
				Iterator<Row> iteratordata = table.getRows().iterator();

				while (iteratordata.hasNext()) {
					Row info = iteratordata.next();
					String purchaseorderid = info.getString("purchaseorderid");
					String orderid = info.getString("orderid");

					purchaseorderids = purchaseorderids + (purchaseorderids.equals("") ? "" : ",") + "'" + purchaseorderid + "'";
					orderids = orderids + (orderids.equals("") ? "" : ",") + orderid;
					auditcount++;

					String purchaseorderdetail = "select * from purchaseorderdetail where purchaseorderid='" + purchaseorderid + "'";
					Table purchaseorderdetailtable = DataUtils.queryData(conn, purchaseorderdetail, null, null, null, null);
					String relationmainids = "";
					if (purchaseorderdetailtable.getRows().size() > 0) {
						for (Row row : purchaseorderdetailtable.getRows()) {
							String relationdetailid = row.getString("relationdetailid");
							String relationmainid = row.getString("relationmainid");
							double count = Double.parseDouble((row.getValue("count").toString()));
							if (!relationmainid.equals("") && relationmainids.indexOf(relationmainid) == -1) {
								relationmainids = relationmainids + (relationmainids.equals("") ? "" : ",") + relationmainid;
							}

							ps.addBatch("update purchasedetail set ordercount=round(ordercount-" + count + "," + countbit
									+ "),orderstatus=if(ordercount<count&&orderstatus='1',0,orderstatus) where detailid='" + relationdetailid + "'");
							// ps.addBatch("update purchasedetail set orderstatus='0' where orderstatus='1' and detailid='"
							// + relationdetailid + "' and ordercount<count");
							ps.addBatch("update purchase set ordercount=round(ordercount-" + count + "," + countbit + ") where purchaseid ='" + relationmainid + "'");
						}

						if (!relationmainids.equals("")) {
							String[] farr = relationmainids.split(",");
							for (int j = 0; j < farr.length; j++) {
								ps.addBatch("update purchase set orderstatus='0' where purchaseid='" + farr[j] + "' and status='1' and (select count(detailid) from purchasedetail where purchaseid='"
										+ farr[j] + "' and count>ordercount)>0");
							}
						}
					}
				}
			}

			if (auditcount > 0) {
				ps.addBatch("delete from t_billflow  where  billid in (" + purchaseorderids + ") and (select 1 from t_auditflow t where t.id = t_billflow.auditflow_id and t.auditflownum=2 limit 1)");

				ps.addBatch("delete from purchaseorderdetail  where  purchaseorderid in (" + purchaseorderids + ")");
				ps.addBatch("delete from  purchaseorder  where  purchaseorderid in (" + purchaseorderids + ")");

				ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
						+ companyid + "',27,'批量删除','','单据编号：" + orderids + "共" + auditcount + "条采购订单','" + loginuserid + "','" + loginUser + "',now())");

				ps.executeBatch();
				conn.commit();
				state = "1";
				message = "已成功删除" + auditcount + "条采购订单";
			} else {
				message = "没有<未审核>状态的订单可操作";
			}

			conn.setAutoCommit(true);

		} catch (Exception e) {
			// e.printStackTrace();
			message = "批量删除操作失败，请稍后再试。";
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

	// 采购订单批量审核
	public static JSONObject auditBatchPurchaseFunction(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject rt = new JSONObject();
		String companyid = params.getString("companyid");
		String mainids = params.getString("mainids");
		String loginuserid = params.getString("loginuserid");
		String loginUser = params.getString("loginUser");

		String state = "0";
		String message = "";
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String orderids = "";
		String purchaseids = "";
		int auditcount = 0;

		try {
			int countbit = 0;
			int moneybit = 2;
			Table companytalbe = DataUtils.queryData(conn, "select pricebit,countbit,moneybit from s_company_config where company_id='" + companyid + "'", null, null, null, null);
			if (companytalbe.getRows().size() > 0) {
				countbit = companytalbe.getRows().get(0).getInteger("countbit");
				moneybit = companytalbe.getRows().get(0).getInteger("moneybit");
			}

			Statement ps = conn.createStatement();
			conn.setAutoCommit(false);
			// String ordermonth =
			// "insert into ordermonth (monthid,companyid,itemid,sdate,syear,smonth,purchasecount,purchasemoney,batchno) VALUES ('";

			List<Object> fparams = new ArrayList<>();

			int m = 0;
			String[] idssarr = mainids.replaceAll("'", "").split(",");
			String temp = "";
			for (m = 0; m < idssarr.length; m++) {
				temp = temp + (temp.equals("") ? "" : ",") + "?";
				fparams.add(idssarr[m]);
			}

			String sql = "select purchaseid,orderid,operate_time from purchase where  purchaseid in (" + temp + ") and status='0'";
			Table table = DataUtils.queryData(conn, sql, fparams, null, null, null);
			if (table.getRows().size() > 0) {
				Iterator<Row> iteratordata = table.getRows().iterator();

				while (iteratordata.hasNext()) {
					Row info = iteratordata.next();
					String purchaseid = info.getString("purchaseid");
					String orderid = info.getString("orderid");
					String operate_time = format.format(info.getDate("operate_time"));
					String[] arr = operate_time.split("-");
					String sdate = arr[0] + "-" + arr[1] + "-" + "01"; // 业务日期转成每个月1日来记录itemmonth数据

					int syear = Integer.parseInt(arr[0]);
					int smonth = Integer.parseInt(arr[1]);

					String subsql = "select count,itemid,batchno from purchasedetail where  purchaseid = '" + purchaseid + "' and status='0'";
					Table subtable = DataUtils.queryData(conn, subsql, null, null, null, null);
					Iterator<Row> subiteratordata = subtable.getRows().iterator();
					if (subtable.getRows().size() > 0) {
						purchaseids = purchaseids + (purchaseids.equals("") ? "" : ",") + "'" + purchaseid + "'";
						orderids = orderids + (orderids.equals("") ? "" : ",") + orderid;
						auditcount++;
					}

					// while (subiteratordata.hasNext()) {
					// Row subinfo = subiteratordata.next();
					// double count =
					// Double.parseDouble(subinfo.getValue("count").toString());
					// double total =
					// Double.parseDouble(subinfo.getValue("total").toString());
					// String itemid = subinfo.getString("itemid");
					// String batchno = subinfo.getString("batchno");
					// String details = ordermonth + Common.getUpperUUIDString()
					// + "','" + companyid + "','" + itemid + "','" + sdate +
					// "'," + syear + "," + smonth + "," + count + "," + total +
					// ",'"
					// + batchno +
					// "') on duplicate key update  purchasecount= round(purchasecount+"
					// + count + "," + countbit +
					// "), purchasemoney= round(purchasemoney+" + total + ","
					// + moneybit + ")";
					//
					// ps.addBatch(details);
					// }

				}
			}

			if (auditcount > 0) {
				ps.addBatch("update  purchasedetail set status='1',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now(),audit_id='" + loginuserid + "',audit_by='"
						+ loginUser + "',audit_time=now() where  purchaseid in (" + purchaseids + ")");
				ps.addBatch("update  purchase set status='1',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now(),audit_id='" + loginuserid + "',audit_by='" + loginUser
						+ "',audit_time=now() where  purchaseid in (" + purchaseids + ")");

				ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
						+ companyid + "',27,'批量审核','','单据编号：" + orderids + "共" + auditcount + "条采购申请单','" + loginuserid + "','" + loginUser + "',now())");

				ps.executeBatch();
				conn.commit();
				state = "1";
				message = "已成功审核" + auditcount + "条采购订单";
			} else {
				message = "没有<未审核>状态的订单可操作";
			}

			conn.setAutoCommit(true);

		} catch (Exception e) {
			// e.printStackTrace();
			message = "批量审核操作失败，请稍后再试。";
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

	// 采购订单批量删除
	public static JSONObject delBatchPurchaseFunction(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject rt = new JSONObject();
		String companyid = params.getString("companyid");
		String mainids = params.getString("mainids");
		String loginuserid = params.getString("loginuserid");
		String loginUser = params.getString("loginUser");

		String state = "0";
		String message = "";
		String orderids = "";
		String purchaseids = "";
		int auditcount = 0;

		try {
			Statement ps = conn.createStatement();
			conn.setAutoCommit(false);

			List<Object> fparams = new ArrayList<>();

			int m = 0;
			String[] idssarr = mainids.replaceAll("'", "").split(",");
			String temp = "";
			for (m = 0; m < idssarr.length; m++) {
				temp = temp + (temp.equals("") ? "" : ",") + "?";
				fparams.add(idssarr[m]);
			}

			String sql = "select purchaseid,orderid from purchase where  purchaseid in (" + temp + ") and status='0'";
			Table table = DataUtils.queryData(conn, sql, fparams, null, null, null);
			if (table.getRows().size() > 0) {
				Iterator<Row> iteratordata = table.getRows().iterator();

				while (iteratordata.hasNext()) {
					Row info = iteratordata.next();
					String purchaseid = info.getString("purchaseid");
					String orderid = info.getString("orderid");

					purchaseids = purchaseids + (purchaseids.equals("") ? "" : ",") + "'" + purchaseid + "'";
					orderids = orderids + (orderids.equals("") ? "" : ",") + orderid;
					auditcount++;

				}
			}

			if (auditcount > 0) {
				ps.addBatch("delete from t_billflow  where  billid in (" + purchaseids + ") and (select 1 from t_auditflow t where t.id = t_billflow.auditflow_id and t.auditflownum=1 limit 1)");

				ps.addBatch("delete from purchasedetail  where  purchaseid in (" + purchaseids + ")");
				ps.addBatch("delete from  purchase  where  purchaseid in (" + purchaseids + ")");

				ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
						+ companyid + "',27,'批量删除','','单据编号：" + orderids + "共" + auditcount + "条采购申请单','" + loginuserid + "','" + loginUser + "',now())");

				ps.executeBatch();
				conn.commit();
				state = "1";
				message = "已成功删除" + auditcount + "条采购申请单";
			} else {
				message = "没有<未审核>状态的订单可操作";
			}

			conn.setAutoCommit(true);

		} catch (Exception e) {
			// e.printStackTrace();
			message = "批量删除操作失败，请稍后再试。";
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

	public static JSONObject getSalesOriginalbill(JSONObject params, ActionContext context) throws SQLException, NamingException, java.sql.SQLException {
		String relationmainid = params.getString("relationmainid");
		JSONObject ret = new JSONObject();

		Connection conn = context.getConnection(DATASOURCE);
		String originalbill = "";

		String sql = "select sd.originalbill from salesorder sd where sd.salesorderid=? ";
		List<Object> fparams = new ArrayList<>();
		fparams.add(relationmainid);
		Table table = DataUtils.queryData(conn, sql, fparams, null, null, null);
		if (table.getRows().size() > 0) {
			originalbill = table.getRows().get(0).getString("originalbill") == null ? "" : table.getRows().get(0).getString("originalbill");
		}
		ret.put("originalbill", originalbill);
		return ret;
	}

	public static JSONObject getOldPrice(JSONObject params, ActionContext context) throws SQLException, NamingException, java.sql.SQLException {
		JSONObject rt = new JSONObject();

		String message = "";

		try {

			String customerid = params.getString("customerid").replaceAll("'", "");
			String itemid = params.getString("itemid").replaceAll("'", "");

			int stype = params.getIntValue("stype"); // 1-采购订单 2-销售订单 3-委外加工单
														// 4-其他出库
														// 5-生产领料 7-报价单 8-发票商品入库
			int hasbatchno = params.getIntValue("hasbatchno"); // 0-不计批号 1-计批号
			int limit = params.getIntValue("limit"); // 0-不计批号 1-计批号

			if (customerid.length() != 32 && itemid.length() != 32) {
				message = "含非法数据，获取失败！！";
			} else {

				List<Object> fparams = new ArrayList<>();
				int m = 0;
				Connection conn = context.getConnection(DATASOURCE);

				String sql = "";
				if (stype == 1) {
					if (hasbatchno == 1) {
						sql = "(select distinct price,taxrate,taxprice,batchno,concat('最近入货日期 ',DATE_FORMAT(max(operate_time),'%Y-%m-%d')) as remark from storeindetail where  customerid=? and itemid=? and stype='11' and status='1' group by price,taxrate,taxprice,batchno order by operate_time desc limit "
								+ limit + ") " + " union all  (select inprice as price,0 as taxrate, inprice as taxprice,'' as batchno, '商品进货单价' as remark  from iteminfo where itemid=?) ";

					} else {
						sql = "(select distinct price,taxrate,taxprice,concat('最近入货日期 ',DATE_FORMAT(max(operate_time),'%Y-%m-%d')) as remark from storeindetail where  customerid=? and itemid=? and stype='11' and status='1' group by price,taxrate,taxprice order by operate_time desc limit "
								+ limit + ") " + " union all (select inprice as price,0 as taxrate, inprice as taxprice,'商品进货单价' as remark  from iteminfo where itemid=? ) ";

					}
					fparams.add(customerid);
					fparams.add(itemid);
					fparams.add(itemid);
				} else if (stype == 2) {
					if (hasbatchno == 1) {
						sql = "(select distinct price,taxrate,taxprice,batchno,concat('最近出货日期 ',DATE_FORMAT(max(operate_time),'%Y-%m-%d')) as remark from storeoutdetail where  customerid=? and itemid=? and stype='21' and status='1' group by price,taxrate,taxprice,batchno order by operate_time desc limit "
								+ limit
								+ ") "
								+ " union all (select outprice as price,0 as taxrate, outprice as taxprice,'' as batchno, '商品零售单价' as remark  from iteminfo where itemid=?) "
								+ " union all (select outprice1 as price,0 as taxrate, outprice1 as taxprice,'' as batchno, '商品一级销售单价' as remark  from iteminfo where itemid=?) "
								+ " union all (select outprice2 as price,0 as taxrate, outprice2 as taxprice,'' as batchno, '商品二级销售单价' as remark  from iteminfo where itemid=?) "
								+ " union all (select outprice3 as price,0 as taxrate, outprice3 as taxprice,'' as batchno, '商品三级销售单价' as remark  from iteminfo where itemid=?) "
								+ " union all (select outprice4 as price,0 as taxrate, outprice4 as taxprice,'' as batchno, '商品四级销售单价' as remark  from iteminfo where itemid=?) "
								+ " union all (select outprice5 as price,0 as taxrate, outprice5 as taxprice,'' as batchno, '商品五级销售单价' as remark  from iteminfo where itemid=?) ";

						fparams.add(customerid);
						for (m = 1; m <= 7; m++) {
							fparams.add(itemid);
						}
					} else {
						sql = "(select distinct price,taxrate,taxprice,concat('最近出货日期 ',DATE_FORMAT(max(operate_time),'%Y-%m-%d')) as remark from storeoutdetail where  customerid=? and itemid=? and stype='21' and status='1' group by price,taxrate,taxprice order by operate_time desc limit "
								+ limit
								+ ") "
								+ " union all (select outprice as price,0 as taxrate, outprice as taxprice, '商品零售单价' as remark  from iteminfo where itemid=?) "
								+ " union all (select outprice1 as price,0 as taxrate, outprice1 as taxprice, '商品一级销售单价' as remark  from iteminfo where itemid=?) "
								+ " union all (select outprice2 as price,0 as taxrate, outprice2 as taxprice, '商品二级销售单价' as remark  from iteminfo where itemid=?) "
								+ " union all (select outprice3 as price,0 as taxrate, outprice3 as taxprice, '商品三级销售单价' as remark  from iteminfo where itemid=?) "
								+ " union all (select outprice4 as price,0 as taxrate, outprice4 as taxprice, '商品四级销售单价' as remark  from iteminfo where itemid=?) "
								+ " union all (select outprice5 as price,0 as taxrate, outprice5 as taxprice, '商品五级销售单价' as remark  from iteminfo where itemid=?) ";

						fparams.add(customerid);
						for (m = 1; m <= 7; m++) {
							fparams.add(itemid);
						}
					}
				} else if (stype == 3) {
					if (hasbatchno == 1) {
						sql = "select distinct processprice as price,taxrate,taxprice,batchno,concat('最近加工入库日期 ',DATE_FORMAT(max(operate_time),'%Y-%m-%d')) as remark from outsourcingindetail where  customerid=? and itemid=? and stype='251' and status='1' group by processprice,taxrate,taxprice,batchno order by operate_time desc limit "
								+ limit;

					} else {
						sql = "select distinct processprice as price,taxrate,taxprice,concat('最近加工入库日期 ',DATE_FORMAT(max(operate_time),'%Y-%m-%d')) as remark from outsourcingindetail where  customerid=? and itemid=? and stype='251' and status='1' group by processprice,taxrate,taxprice order by operate_time desc limit "
								+ limit;

					}
					fparams.add(customerid);
					fparams.add(itemid);
				} else if (stype == 4) {
					if (hasbatchno == 1) {
						sql = "select distinct  price,0 as taxrate,0 as taxprice,batchno,concat('最近出库日期 ',DATE_FORMAT(max(operate_time),'%Y-%m-%d')) as remark from otherinoutdetail where  customerid=? and itemid=? and stype='131' and status='1' group by price,batchno order by operate_time desc limit "
								+ limit;

					} else {
						sql = "select distinct   price,0 as taxrate,0 as taxprice,concat('最近出库日期 ',DATE_FORMAT(max(operate_time),'%Y-%m-%d')) as remark from otherinoutdetail where  customerid=? and itemid=? and stype='131' and status='1' group by price order by operate_time desc limit "
								+ limit;

					}
					fparams.add(customerid);
					fparams.add(itemid);
				} else if (stype == 5) {
					if (hasbatchno == 1) {
						sql = "select distinct  price,0 as taxrate,0 as taxprice,batchno,concat('最近领用日期 ',DATE_FORMAT(max(operate_time),'%Y-%m-%d')) as remark from prodrequisitiondetail where  customerid=? and itemid=? and stype='101' and status='1' group by price,batchno order by operate_time desc limit "
								+ limit;

					} else {
						sql = "select distinct   price,0 as taxrate,0 as taxprice,concat('最近领用日期 ',DATE_FORMAT(max(operate_time),'%Y-%m-%d')) as remark from prodrequisitiondetail where  customerid=? and itemid=? and stype='101' and status='1' group by price order by operate_time desc limit "
								+ limit;

					}
					fparams.add(customerid);
					fparams.add(itemid);
				} else if (stype == 6) {
					if (hasbatchno == 1) {
						sql = "select distinct cost_price as price,0 as taxrate,0 as taxprice,batchno,concat('最近销售日期 ',DATE_FORMAT(max(operate_time),'%Y-%m-%d')) as remark from storeoutdetail where  customerid=? and itemid=? and stype='21' and status='1' group by cost_price,batchno order by operate_time desc limit "
								+ limit;
					} else {
						sql = "select distinct  cost_price as price,0 as taxrate,0 as taxprice,concat('最近销售日期 ',DATE_FORMAT(max(operate_time),'%Y-%m-%d')) as remark from storeoutdetail where  customerid=? and itemid=? and stype='21' and status='1' group by cost_price order by operate_time desc limit "
								+ limit;
					}
					fparams.add(customerid);
					fparams.add(itemid);
				} else if (stype == 7) {
					if (hasbatchno == 1) {
						sql = "select distinct  price, taxrate, taxprice,'' as batchno,concat('最近报价日期 ',DATE_FORMAT(max(operate_time),'%Y-%m-%d')) as remark from quotationdetail where itemid=? and customername=? and  status='1' group by  price,taxrate,taxprice order by operate_time desc limit "
								+ limit;
					} else {
						sql = "select distinct price, taxrate, taxprice,concat('最近报价日期 ',DATE_FORMAT(max(operate_time),'%Y-%m-%d')) as remark from quotationdetail where itemid=? and customername=? and  status='1' group by price,taxrate,taxprice order by operate_time desc limit "
								+ limit;

					}
					fparams.add(itemid);
					fparams.add(customerid);

				} else if (stype == 8) {
					if (hasbatchno == 1) {
						sql = "(select distinct price,taxrate,taxprice,batchno,concat('最近入货日期 ',DATE_FORMAT(max(operate_time),'%Y-%m-%d')) as remark from invoicestoreindetail where  customerid=? and itemid=? and stype='511' and status='1' group by price,taxrate,taxprice,batchno order by operate_time desc limit "
								+ limit + ") " + " union all  (select inprice as price,0 as taxrate, inprice as taxprice,'' as batchno, '商品进货单价' as remark  from iteminfo where itemid=?) ";
					} else {
						sql = "(select distinct price,taxrate,taxprice,concat('最近入货日期 ',DATE_FORMAT(max(operate_time),'%Y-%m-%d')) as remark from invoicestoreindetail where  customerid=? and itemid=? and stype='511' and status='1' group by price,taxrate,taxprice order by operate_time desc limit "
								+ limit + ") " + " union all (select inprice as price,0 as taxrate, inprice as taxprice,'商品进货单价' as remark  from iteminfo where itemid=?) ";
					}
					fparams.add(customerid);
					fparams.add(itemid);
					fparams.add(itemid);
				}

				// System.out.println(sql);
				if (!sql.equals("")) {
					Table table = DataUtils.queryData(conn, sql, fparams, null, null, null);
					rt.put("table", Transform.tableToJson(table));
				}
			}
		} catch (Exception e) {
			message = "往来单位数据获取失败！！";
		}
		rt.put("message", message);

		return rt;
	}

	public static JSONObject salesorderProgress(JSONObject params, ActionContext context) throws SQLException, NamingException, ParseException {

		Connection conn = context.getConnection(DATASOURCE);

		Table table = null;

		String companyid = params.getString("companyid");
		String countbit = params.getString("countbit");
		String condition = params.getString("condition");

		int offset = params.getInteger("offset");
		int limit = params.getInteger("limit");

		String moneybit = params.getString("moneybit");
		int showstype = params.getInteger("showstype");
		String orderBys = params.getString("orderBys");

		String sql = "";
		String sql1 = "";
		if (showstype == 2) {
			orderBys = (orderBys.equals("") ? "" : orderBys + ",") + "sd.plandate asc ,sd.orderid asc,sd.goods_number asc";

			sql = "select sd.detailid,sd.salesorderid,sd.schedulstatus,sd.originalbill,sd.orderid,sd.goods_number,sd.operate_time,sd.count,sd.scheduledcount,sd.hadoutsourcing,sd.plandate,round(sd.incount+sd.outsourcingin,"
					+ countbit
					+ ") as incount,sd.outcount,sd.remark,sd.batchno,sd.audit_by,sd.audit_time,sd.create_by,sd.create_time,sd.update_by,sd.update_time,sd.relationdetailid,sd.relationorderid,sd.relationmainid,s.staffname,im.codeid,im.itemname,im.sformat,im.unit,c.customername,c.property4,CONCAT_WS(' ',im.property1,im.property2,im.property3,im.property4,im.property5) as property,(select concat(ifnull(sum(pk.count),0),',',ifnull(sum(pk.needcount),0)) as prod from prodrequisition_work_total pk inner join t_order tor on pk.worksheetid=tor.id and tor.order_status=1 where tor.salesorderdetailid=sd.detailid) as prodreqstate,(select concat(ifnull(sum(o.order_count),0),',',ifnull(sum(o.finishcount),0)) from t_order o where o.salesorderdetailid=sd.detailid and o.order_status=1) as productcount from salesorderdetail sd left join iteminfo im on sd.itemid=im.itemid left join  salesorder md on sd.salesorderid=md.salesorderid "
					+ " left join customer c on sd.customerid = c.customerid left join staffinfo s on s.staffid=sd.operate_by where sd.companyid='"
					+ companyid
					+ "' "
					+ condition
					+ " order by "
					+ orderBys + "  limit " + offset + "," + limit + "";

			sql1 = "select count(*) from salesorderdetail sd left join  salesorder md on sd.salesorderid=md.salesorderid left join iteminfo im on sd.itemid=im.itemid left join customer c on sd.customerid = c.customerid left join staffinfo s on s.staffid=sd.operate_by where sd.companyid='"
					+ companyid + "' " + condition;
		} else if (showstype == 3) {
			orderBys = (orderBys.equals("") ? "" : orderBys + ",") + "im.codeid asc ";

			sql = "select round(sum(sd.count),"
					+ countbit
					+ ") as count,round(sum(sd.checkout_count),"
					+ countbit
					+ ") as checkout_count,round(sum(sd.scheduledcount),"
					+ countbit
					+ ") as scheduledcount,round(sum(sd.schedulcount),"
					+ countbit
					+ ") as schedulcount, round(sum(sd.hadoutsourcing),"
					+ countbit
					+ ") as hadoutsourcing,round(sum(sd.incount+sd.outsourcingin),"
					+ countbit
					+ ") as allincount,round(sum(sd.incount),"
					+ countbit
					+ ") as incount,round(sum(sd.outsourcingin),"
					+ countbit
					+ ") as outsourcingin,round(sum(sd.outcount),"
					+ countbit
					+ ") as outcount,round(sum(sd.outsourcingcount),"
					+ countbit
					+ ") as outsourcingcount,round(sum(sd.outtotal),"
					+ moneybit
					+ ") as outtotal, round(sum(sd.total),"
					+ moneybit
					+ ") as total, im.codeid,im.itemname,im.sformat,im.unit,im.property1,im.property2,im.property3,im.property4,im.property5,"
					+ " ifnull((select round(sum(s.count-s.checkout_count),"
					+ countbit
					+ ") from stock s where s.itemid=sd.itemid),0) as storecount"
					+ " from salesorderdetail sd left join  salesorder md on sd.salesorderid=md.salesorderid left join iteminfo im on sd.itemid=im.itemid left join customer c on sd.customerid = c.customerid    "
					+ "  where sd.companyid='" + companyid + "' " + condition + " group by sd.itemid order by " + orderBys + "  limit " + offset + "," + limit + "";

			sql1 = "select count(*) from (select 1 from salesorderdetail sd left join  salesorder md on sd.salesorderid=md.salesorderid left join iteminfo im on sd.itemid=im.itemid left join customer c on sd.customerid = c.customerid   where sd.companyid='"
					+ companyid + "' " + condition + " group by sd.itemid ) k ";
		}
		table = DataUtils.queryData(conn, sql, null, null, null, null);
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

		return ret;
	}

	public static JSONObject getPurchaseOrderOriginalbill(JSONObject params, ActionContext context) throws SQLException, NamingException, java.sql.SQLException {
		String relationmainid = params.getString("relationmainid");
		String relationdetailid = params.getString("relationdetailid");
		JSONObject ret = new JSONObject();

		Connection conn = context.getConnection(DATASOURCE);
		String originalbill = "";
		double ordercount = 0;

		List<Object> fparams = new ArrayList<>();

		String sql = "";

		if (relationdetailid != null && !relationdetailid.equals("")) {

			sql = "select sd.originalbill,sd.count from purchaseorderdetail sd where sd.detailid=? ";
			fparams.add(relationdetailid);
		} else {
			sql = "select sd.originalbill,sd.count from purchaseorder sd where sd.purchaseorderid=? ";
			fparams.add(relationmainid);
		}

		Table table = DataUtils.queryData(conn, sql, fparams, null, null, null);
		if (table.getRows().size() > 0) {
			originalbill = table.getRows().get(0).getString("originalbill") == null ? "" : table.getRows().get(0).getString("originalbill");
			ordercount = Double.parseDouble(table.getRows().get(0).getValue("count").toString());
		}
		ret.put("originalbill", originalbill);
		ret.put("ordercount", ordercount);
		return ret;
	}

	public static JSONObject schedulesalesorderstatus(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(Common.DATASOURCE);

		JSONObject ret = new JSONObject();
		String salesorderid = params.getString("salesorderid");

		List<Object> fparams = new ArrayList<>();

		int status = -1;
		try {
			String checksql = "select count(*) as scount,sum(if(count>scheduledcount and scheduledcount>0,1,0)) as pcount,sum(if(count<=scheduledcount and count>0,1,0)) as fcount from salesorderdetail  where  salesorderid=?";
			fparams.add(salesorderid);
			Table table = DataUtils.queryData(conn, checksql, fparams, null, null, null);
			if (table.getRows().size() > 0) {
				double scount = Double.parseDouble(table.getRows().get(0).getValue("scount").toString());
				double pcount = Double.parseDouble(table.getRows().get(0).getValue("pcount").toString());
				double fcount = Double.parseDouble(table.getRows().get(0).getValue("fcount").toString());
				if (pcount == 0 && fcount == 0) { // 未排产
					status = 1;
				} else if (scount == fcount) { // 已排产
					status = 3;
				} else { // 排过产
					status = 2;
				}
			} else {
				status = 1;
			}

			ret.put("status", status);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return ret;
	}

	public static JSONObject auditQuotationFunction(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject rt = new JSONObject();
		String orderid = params.getString("orderid");
		String companyid = params.getString("companyid");
		String mainid = params.getString("mainid").replaceAll("'", "");
		String loginuserid = params.getString("loginuserid");
		String loginUser = params.getString("loginUser");
		String operate_time = params.getString("operate_time");
		String[] arr = operate_time.split("-");
		String sdate = arr[0] + "-" + arr[1] + "-" + "01"; // 业务日期转成每个月1日来记录itemmonth数据

		int syear = Integer.parseInt(arr[0]);
		int smonth = Integer.parseInt(arr[1]);

		int countbit = params.getInteger("countbit");
		int moneybit = params.getInteger("moneybit");

		int audittype = params.getInteger("audittype");
		int stype = params.getInteger("stype");

		JSONObject auditflowlist = JSONObject.parseObject(params.getString("auditflowlist"));

		String state = "0";
		String message = "";
		Statement ps = null;

		try {
			if (mainid.length() != 32) {
				state = "0";
				message = "含非法数据，操作失败!";
			} else {
				ps = conn.createStatement();
				conn.setAutoCommit(false);
				List<Object> fparams = new ArrayList<>();
				String sql = "select status from quotation where quotationid=?";
				fparams.add(mainid);
				Object cobject = DataUtils.getValueBySQL(conn, sql, fparams);
				if (cobject == null) {
					message = "已被删除";
					state = "2";
				} else {
					String fstatus = cobject.toString();
					if (fstatus.equals("1")) {
						message = "已被审核";
						state = "2";
					} else if (fstatus.equals("2")) {
						message = "已作废，不能进行审核操作";
						state = "2";
					} else {

						if (fstatus.equals("3") && stype == 2) {
							int agreedZJ = auditflowlist.getInteger("agreedZJ");
							if (auditflowlist.getInteger("curflowfstatus") == 2) {
								ps.addBatch("update t_billflow set back_type=" + agreedZJ + ",back_remark='" + auditflowlist.getString("remark") + "',back_time=now() where id='"
										+ auditflowlist.getString("curbillflow") + "'");
							} else {
								if (agreedZJ == 2) {
									if (!auditflowlist.getString("curbillflow").equals(auditflowlist.getString("back_flow"))) {
										ps.addBatch("update t_billflow set flowfstatus=" + agreedZJ + ",back_type=1,receive_remark='" + auditflowlist.getString("remark")
												+ "',receive_time=now() where id='" + auditflowlist.getString("curbillflow") + "'");
										ps.addBatch("update t_billflow set flowfstatus=" + agreedZJ + ",receive_remark='" + auditflowlist.getString("remark")
												+ "',receive_time=now(),back_send_remark='" + auditflowlist.getString("remark") + "',back_send_time=now(),back_urgent="
												+ auditflowlist.getInteger("urgentselect") + ",back_id='" + auditflowlist.getString("back_id") + "',back_by='" + auditflowlist.getString("back_by")
												+ "',backflownum=" + auditflowlist.getInteger("back_preflownum") + ",backflowname='" + auditflowlist.getString("backflowname") + "' where id='"
												+ auditflowlist.getString("back_flow") + "'");
									} else {
										ps.addBatch("update t_billflow set flowfstatus=" + agreedZJ + ",receive_remark='" + auditflowlist.getString("remark")
												+ "',receive_time=now(),back_send_remark='" + auditflowlist.getString("remark") + "',back_send_time=now(),back_urgent="
												+ auditflowlist.getInteger("urgentselect") + ",back_id='" + auditflowlist.getString("back_id") + "',back_by='" + auditflowlist.getString("back_by")
												+ "',backflownum=" + auditflowlist.getInteger("back_preflownum") + ",backflowname='" + auditflowlist.getString("backflowname") + "' where id='"
												+ auditflowlist.getString("curbillflow") + "'");

									}
								} else {
									ps.addBatch("update t_billflow set flowfstatus=" + agreedZJ + ",receive_remark='" + auditflowlist.getString("remark") + "',receive_time=now() where id='"
											+ auditflowlist.getString("curbillflow") + "'");
								}
							}
							if (agreedZJ == 1 && audittype != 1) {

								ps.addBatch("INSERT INTO `t_billflow` (`id`, `companyid`, `billid`,`orderid`, `auditflow_id`,`auditflowmain_id`, `flowfstatus`, `flownum`, `flowname`, `preflow`, `preflownum`, `preflowname`, `pass_id`, `pass_by`, `pass_time`, `pass_remark`, `receive_id`, `receive_by`,`pass_urgent`) VALUES "
										+ "('"
										+ Common.getUpperUUIDString()
										+ "', '"
										+ companyid
										+ "', '"
										+ auditflowlist.getString("mainid")
										+ "', '"
										+ orderid
										+ "', '"
										+ auditflowlist.getString("auditflow_id")
										+ "', '"
										+ auditflowlist.getString("auditflowmain_id")
										+ "', 0,"
										+ auditflowlist.getInteger("newflownum")
										+ ", '"
										+ auditflowlist.getString("newflowname")
										+ "', '"
										+ auditflowlist.getString("curbillflow")
										+ "', "
										+ auditflowlist.getInteger("oldflownum")
										+ ", '"
										+ auditflowlist.getString("oldflowname")
										+ "', '"
										+ auditflowlist.getString("loginuserid")
										+ "', '"
										+ auditflowlist.getString("loginUser")
										+ "', now(), '"
										+ auditflowlist.getString("remark")
										+ "', '"
										+ auditflowlist.getString("receive_id")
										+ "', '"
										+ auditflowlist.getString("receive_by")
										+ "',"
										+ auditflowlist.getInteger("urgentselect") + ")");
							} else if (agreedZJ == 2 && auditflowlist.getInteger("curflowfstatus") == 2) {
								ps.addBatch("update t_billflow set flowfstatus=" + agreedZJ + ",back_send_remark='" + auditflowlist.getString("remark") + "',back_id='"
										+ auditflowlist.getString("back_id") + "',back_by='" + auditflowlist.getString("back_by") + "',backflownum=" + auditflowlist.getInteger("back_preflownum")
										+ ",backflowname='" + auditflowlist.getString("backflowname") + "',back_send_time=now(),back_urgent=" + auditflowlist.getInteger("urgentselect")
										+ " where id='" + auditflowlist.getString("back_flow") + "'");

							}

							if (audittype == 0) {
								ps.addBatch("update quotationdetail set status='0' where quotationid='" + mainid + "' ");
								ps.addBatch("update quotation set status='0' where quotationid='" + mainid + "' ");
							}
						}

						if ((fstatus.equals("0") && stype == 1) || (fstatus.equals("3") && stype == 2 && audittype == 1)) {

							ps.addBatch("update quotationdetail set status='1',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now(),audit_id='" + loginuserid
									+ "',audit_by='" + loginUser + "',audit_time=now() where quotationid='" + mainid + "'");
							ps.addBatch("update quotation set status='1',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now(),audit_id='" + loginuserid + "',audit_by='"
									+ loginUser + "',audit_time=now() where quotationid='" + mainid + "' ");

							ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString()
									+ "','" + companyid + "'," + Pdacommon.getDatalogBillChangefunc("quotation", "") + ",'审核','" + mainid + "','单据编号：" + orderid + "','" + loginuserid + "','"
									+ loginUser + "',now())");
						}

						ps.executeBatch();

						conn.commit();
						int count = ps.getUpdateCount();
						if (count == 0) {
							state = "3";
							message = "单据已审核。";
						} else {
							state = "1";
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							String str = sdf.format(new Date());
							rt.put("audit_time", str);

						}

						conn.setAutoCommit(true);

					}
				}
			}

		} catch (Exception e) {
			// e.printStackTrace();
			message = "审核操作失败，请稍后再试。";
			try {
				conn.rollback();
			} catch (Exception e1) {
				// e1.printStackTrace();
			}
		} finally {
			conn.close();
			ps.close();
		}
		rt.put("state", state);
		rt.put("message", message);
		return rt;
	}

	// 采购订单作废
	public static JSONObject invalidQuotationFunction(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject rt = new JSONObject();
		String orderid = params.getString("orderid");
		String companyid = params.getString("companyid");
		String mainid = params.getString("mainid").replaceAll("'", "");
		JSONArray detailarr = params.getJSONArray("detailarr");
		String loginuserid = params.getString("loginuserid");
		String loginUser = params.getString("loginUser");
		String operate_time = params.getString("operate_time");
		String[] arr = operate_time.split("-");
		String sdate = arr[0] + "-" + arr[1] + "-" + "01"; // 业务日期转成每个月1日来记录itemmonth数据

		String state = "0";
		String message = "";

		int countbit = params.getInteger("countbit");
		int moneybit = params.getInteger("moneybit");

		try {

			if (mainid.length() != 32) {
				message = "含非法数据，操作失败!";
			} else {

				Statement ps = conn.createStatement();
				conn.setAutoCommit(false);

				Map<String, String> relationdetailMap = new HashMap();// 缓存原数据绑定id
				Map<String, String> relationdetailMainMap = new HashMap();// 缓存原数据绑定id

				String relativeinfo = "";

				List<Object> fparams = new ArrayList<>();

				String sqlstr = " select i.itemname, pod.count,pod.total,pod.detailid from quotationdetail pod ,iteminfo i where pod.quotationid=? and pod.itemid=i.itemid order by pod.goods_number asc ";
				fparams.add(mainid);

				Table tabledata = DataUtils.queryData(conn, sqlstr, fparams, null, null, null);

				Iterator<Row> iteratordata = tabledata.getRows().iterator();

				while (iteratordata.hasNext()) {
					Row info = iteratordata.next();
					double count = Double.parseDouble(info.getValue("count").toString());
					double total = Double.parseDouble(info.getValue("total").toString());
					String itemid = info.getString("itemid");
					String itemname = info.getString("itemname");
					String detailid = info.getString("detailid");

					// 与有效订单单关联不能作废
					String sql = "select count(detailid) from  salesorderdetail where relationdetailid='" + detailid + "' and status<>'2'";
					int scount = Integer.parseInt(DataUtils.getValueBySQL(conn, sql, null).toString());
					if (scount > 0) {
						relativeinfo = relativeinfo + (relativeinfo.equals("") ? "" : "、") + itemname;
					}

				}

				if (relativeinfo.equals("")) {

					ps.addBatch("update quotationdetail set status='2',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now(),audit_id='" + loginuserid + "',audit_by='"
							+ loginUser + "',audit_time=now() where quotationid='" + mainid + "' and status='1' ");
					ps.addBatch("update quotation set status='2',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now(),audit_id='" + loginuserid + "',audit_by='" + loginUser
							+ "',audit_time=now() where quotationid='" + mainid + "' and status='1' ");

					ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
							+ companyid + "'," + Pdacommon.getDatalogBillChangefunc("quotation", "") + ",'作废','" + mainid + "','单据编号：" + orderid + "','" + loginuserid + "','" + loginUser + "',now())");

					ps.executeBatch();
					conn.commit();
					int count = ps.getUpdateCount();
					if (count == 0) {
						state = "3";
						message = "单据已作废，操作失败。";
					} else {
						state = "1";
					}
				} else {
					state = "2";
					message = (relativeinfo.equals("") ? "" : "报价商品【" + relativeinfo + "】开具销售订单，即与有效销售订单相关联") + "，不能进行作废操作。";

				}

				conn.setAutoCommit(true);
			}

		} catch (Exception e) {
			// e.printStackTrace();
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

	// 修改报价单入库状态
	public static JSONObject changeQuotationdstatus(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject rt = new JSONObject();
		String orderid = params.getString("orderid");
		String companyid = params.getString("companyid");
		String mainid = params.getString("mainid").replaceAll("'", "");
		String loginuserid = params.getString("loginuserid");
		String loginUser = params.getString("loginUser");
		String oldstatus = params.getString("oldstatus");
		String newstatus = params.getString("newstatus");

		String state = "1";

		try {
			if (mainid.length() != 32) {
				state = "0";
			} else {
				Statement ps = conn.createStatement();
				conn.setAutoCommit(false);
				ps.addBatch("update quotation set qstatus='" + newstatus + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where quotationid='" + mainid + "' ");
				ps.addBatch("update quotationdetail set qstatus='" + newstatus + "',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now() where quotationid='" + mainid
						+ "' ");
				ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
						+ companyid + "','80','修改','" + mainid + "','报价单号：" + orderid + "的报价状态原[" + Pdacommon.getQuotationstatus(oldstatus) + "]变更为[" + Pdacommon.getQuotationstatus(newstatus)
						+ "]','" + loginuserid + "','" + loginUser + "',now())");

				ps.executeBatch();
				conn.commit();
				conn.setAutoCommit(true);
			}

		} catch (Exception e) {
			// e.printStackTrace();
			state = "0";
			try {
				conn.rollback();
			} catch (Exception e1) {
				// e1.printStackTrace();
			}
		} finally {
			conn.close();
		}
		rt.put("state", state);
		return rt;
	}

	// 保存采购订单信息
	public static JSONObject saveQuotationFunction(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject rt = new JSONObject();
		JSONObject maindata = JSONObject.parseObject(params.getString("maindata"));
		Integer type = params.getInteger("type");
		String loginUser = params.getString("loginUser");
		String loginuserid = params.getString("loginuserid");
		JSONArray detaildata = params.getJSONArray("detaildata");
		String operate = params.getString("operate");// edit 编辑单据
		String state = "0";
		String message = "";

		String companyid = maindata.getString("companyid");
		String customerid = maindata.getString("customerid");
		String customername = maindata.getString("customername");
		String operate_time = maindata.getString("operate_time");
		String plandate = maindata.getString("plandate");

		double count = maindata.getDoubleValue("count");
		double total = maindata.getDoubleValue("total");
		// int status = type == 0 ? 1 : 0;// type=0 为保存并审核 ，其他为保存 未审核
		int status = (type == 3 ? 3 : (type == 0 ? 1 : 0));// type=0 为保存并审核
		// ，其他为保存 未审核

		JSONObject auditflowlist = JSONObject.parseObject(params.getString("auditflowlist"));

		String[] sdatearr = operate_time.split("-");
		int syear = Integer.parseInt(sdatearr[0]);
		int smonth = Integer.parseInt(sdatearr[1]);
		String sdate = sdatearr[0] + "-" + sdatearr[1] + "-01";

		String billdate = sdatearr[0] + sdatearr[1] + sdatearr[2];

		try {
			int countbit = 0;
			int moneybit = 2;
			Table companytalbe = DataUtils.queryData(conn, "select pricebit,countbit,moneybit from s_company_config where company_id='" + companyid + "'", null, null, null, null);
			if (companytalbe.getRows().size() > 0) {
				countbit = companytalbe.getRows().get(0).getInteger("countbit");
				moneybit = companytalbe.getRows().get(0).getInteger("moneybit");
			}

			Statement ps = conn.createStatement();
			conn.setAutoCommit(false);
			String detail = "insert into quotationdetail (orderid,detailid,quotationid,goods_number,companyid,operate_by,operate_time,plandate,itemid,customerid,customername,price,count,total,tax,taxrate,taxprice,taxmoney,stype,remark,status,qstatus,create_id,create_by,create_time,update_id,update_by,update_time"
					+ (type == 0 ? ",audit_id,audit_by,audit_time" : "") + ",remark1,remark2,remark3) VALUES ('";

			String orderid = operate.equals("edit") ? maindata.getString("orderid") : Pdasave.getOrderidByparams(companyid, "quotation", "", billdate, conn);
			int i = 0;
			String details = "";
			boolean save = true;
			Map<String, String> relationdetailMap = new HashMap();// 缓存原数据绑定id
			Map<String, String> relationdetailMainMap = new HashMap();// 缓存原数据绑定id

			if (operate.equals("edit")) {// 编辑 删除明细数据重新增加
				String fsql = "select status from quotation where quotationid=? ";
				List<Object> fparams = new ArrayList<>();
				fparams.add(maindata.getString("quotationid"));
				Object cobject = DataUtils.getValueBySQL(conn, fsql, fparams);
				if (cobject == null) {
					save = false;
					message = "当前记录已删除，操作失败。";
					state = "2";
				} else {
					String fstatus = cobject.toString();
					if (fstatus.equals("0")) {

						ps.addBatch("delete from quotationdetail where quotationid='" + maindata.getString("quotationid") + "'");
					} else if (fstatus.equals("1")) {
						save = false;
						message = "当前记录已审核，操作失败。";
						state = "2";
					} else if (fstatus.equals("3")) {
						save = false;
						message = "当前记录已提交审批，操作失败。";
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
						String itemid = result.getString("itemid");
						double price = result.getDoubleValue("price");
						double dcount = result.getDoubleValue("count");
						double dtotal = result.getDoubleValue("total");

						details = detail + orderid + "','" + (detailid.equals("") ? Common.getUpperUUIDString() : detailid) + "','" + maindata.getString("quotationid") + "',"
								+ result.getInteger("goods_number") + ",'" + companyid + "','" + maindata.getString("operate_by") + "','" + maindata.getString("operate_time") + "','" + plandate
								+ "','" + itemid + "','" + customerid + "','" + customername + "'," + price + "," + dcount + "," + dtotal + "," + result.getDoubleValue("tax") + ","
								+ result.getDoubleValue("taxrate") + "," + result.getDoubleValue("taxprice") + "," + result.getDoubleValue("taxmoney") + ",'291','" + result.getString("remark")
								+ "','" + status + "',0,'" + (detailid.equals("") ? loginuserid : result.getString("create_id")) + "','"
								+ (detailid.equals("") ? loginUser : result.getString("create_by")) + "'," + (detailid.equals("") ? "now()" : "'" + result.getString("create_time") + "'") + ",'"
								+ loginuserid + "','" + loginUser + "',now()" + (type == 0 ? ",'" + loginuserid + "','" + loginUser + "',now()" : "") + ",'" + result.getString("remark1") + "','"
								+ result.getString("remark2") + "','" + result.getString("remark3") + "')";

						ps.addBatch(details);

					}
				}
			} else {
				message = message + "没有商品明细数据，操作失败";
				save = false;
			}
			if (save) {

				int changebilltype = Pdacommon.getDatalogBillChangefunc("quotation", "");
				if (operate.equals("edit")) {// 编辑 更新
					ps.addBatch("update quotation set orderid='" + orderid + "', selltype='" + maindata.getString("selltype") + "', currency='" + maindata.getString("currency") + "',operate_time='"
							+ operate_time + "',plandate='" + plandate + "',operate_by='" + maindata.getString("operate_by") + "',customername='" + customername + "',customerid='" + customerid
							+ "',count=" + count + ",total=" + total + ",totaltax=" + maindata.getDouble("totaltax") + ",totalmoney=" + maindata.getDouble("totalmoney") + ",remark='"
							+ maindata.getString("remark") + "',status='" + status + "'" + (type == 0 ? ",audit_id='" + loginuserid + "',audit_by='" + loginUser + "',audit_time=now()" : "")
							+ ",update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now()  where quotationid='" + maindata.getString("quotationid") + "'");

					ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
							+ companyid + "'," + changebilltype + ",'修改','" + maindata.getString("quotationid") + "','单据编号：" + orderid + "','" + loginuserid + "','" + loginUser + "',now())");

					ps.addBatch("update quotationdetail d,quotation s set d.create_id=s.create_id,d.create_by=s.create_by,d.create_time=s.create_time where d.quotationid = s.quotationid and s.quotationid='"
							+ maindata.getString("quotationid") + "'");

				} else {
					String main = "insert into quotation (orderid,quotationid,bill_type,customername,selltype,currency,companyid,operate_time,plandate,operate_by,customerid,count,total,totaltax,totalmoney,remark,status,qstatus,printing,outexcel,create_id,create_by,create_time,update_id,update_by,update_time"
							+ (type == 0 ? ",audit_id,audit_by,audit_time" : "")
							+ ") VALUES ('"
							+ orderid
							+ "','"
							+ maindata.getString("quotationid")
							+ "','29','"
							+ maindata.getString("customername")
							+ "','"
							+ maindata.getString("selltype")
							+ "','"
							+ maindata.getString("currency")
							+ "','"
							+ companyid
							+ "','"
							+ operate_time
							+ "','"
							+ plandate
							+ "','"
							+ maindata.getString("operate_by")
							+ "','"
							+ customerid
							+ "',"
							+ count
							+ ","
							+ total
							+ ","
							+ maindata.getDouble("totaltax")
							+ ","
							+ maindata.getDouble("totalmoney")
							+ ",'"
							+ maindata.getString("remark")
							+ "','"
							+ status
							+ "',0,0,0,'"
							+ loginuserid
							+ "','"
							+ loginUser
							+ "',now(),'"
							+ loginuserid
							+ "','" + loginUser + "',now() " + (type == 0 ? ",'" + loginuserid + "','" + loginUser + "',now()" : "") + ")";
					ps.addBatch(main);

					ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
							+ companyid + "'," + changebilltype + ",'" + (status == 0 ? "新增保存并审核" : "新增保存") + "','" + maindata.getString("quotationid") + "','报价单号：" + orderid + "','" + loginuserid
							+ "','" + loginUser + "',now())");

				}

				if (type == 3) {
					if (!auditflowlist.getString("backbillflowid").equals("")) {
						ps.addBatch("update t_billflow set back_type=1,back_time=now() where id='" + auditflowlist.getString("backbillflowid") + "'");
					}
					ps.addBatch("INSERT INTO `t_billflow` (`id`, `companyid`, `billid`,`orderid`, `auditflow_id`, `auditflowmain_id`, `flowfstatus`, `flownum`, `flowname`, `preflow`, `preflownum`, `preflowname`, `pass_id`, `pass_by`, `pass_time`, `pass_remark`, `receive_id`, `receive_by`,`pass_urgent`) VALUES "
							+ "('"
							+ Common.getUpperUUIDString()
							+ "', '"
							+ companyid
							+ "', '"
							+ maindata.getString("quotationid")
							+ "', '"
							+ maindata.getString("orderid")
							+ "', '"
							+ auditflowlist.getString("auditflow_id")
							+ "', '"
							+ auditflowlist.getString("auditflowmain_id")
							+ "', 0, "
							+ auditflowlist.getInteger("newflownum")
							+ ", '"
							+ auditflowlist.getString("newflowname")
							+ "', '', "
							+ auditflowlist.getInteger("oldflownum")
							+ ", '"
							+ auditflowlist.getString("oldflowname")
							+ "', '"
							+ auditflowlist.getString("loginuserid")
							+ "', '"
							+ auditflowlist.getString("loginUser")
							+ "', now(), '"
							+ auditflowlist.getString("remark")
							+ "', '"
							+ auditflowlist.getString("receive_id") + "', '" + auditflowlist.getString("receive_by") + "'," + auditflowlist.getInteger("urgentselect") + ")");
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

	public static JSONObject auditBatchQuotationFunction(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject rt = new JSONObject();
		String companyid = params.getString("companyid");
		String mainids = params.getString("mainids");
		String loginuserid = params.getString("loginuserid");
		String loginUser = params.getString("loginUser");

		String state = "0";
		String message = "";
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		try {
			int countbit = 0;
			int moneybit = 2;
			Table companytalbe = DataUtils.queryData(conn, "select pricebit,countbit,moneybit from s_company_config where company_id='" + companyid + "'", null, null, null, null);
			if (companytalbe.getRows().size() > 0) {
				countbit = companytalbe.getRows().get(0).getInteger("countbit");
				moneybit = companytalbe.getRows().get(0).getInteger("moneybit");
			}

			Statement ps = conn.createStatement();
			conn.setAutoCommit(false);

			List<Object> fparams = new ArrayList<>();
			int m = 0;
			String[] idssarr = mainids.replaceAll("'", "").split(",");
			String temp = "";
			for (m = 0; m < idssarr.length; m++) {
				temp = temp + (temp.equals("") ? "" : ",") + "?";
				fparams.add(idssarr[m]);
			}

			String sql = "select  ifnull(group_concat(orderid order by  operate_time asc separator ','),'') as orderids,count(*) as reconds from quotation where  quotationid in (" + temp
					+ ") and status='0'";
			Table table = DataUtils.queryData(conn, sql, fparams, null, null, null);
			// 2020-12-28 增加houseid填写验证
			String auditcount = "0";
			String orderids = "";
			if (table.getRows().size() > 0) {
				auditcount = table.getRows().get(0).getValue("reconds").toString();
				orderids = table.getRows().get(0).getString("orderids");
			}

			if (!auditcount.equals("0")) {

				ps.addBatch("update  quotationdetail set status='1',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now(),audit_id='" + loginuserid + "',audit_by='"
						+ loginUser + "',audit_time=now() where  quotationid in (" + mainids + ") and status='0'");
				ps.addBatch("update  quotation set status='1',update_id='" + loginuserid + "',update_by='" + loginUser + "',update_time=now(),audit_id='" + loginuserid + "',audit_by='" + loginUser
						+ "',audit_time=now() where  quotationid in (" + mainids + ") and status='0'");

				ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
						+ companyid + "',80,'批量审核','','单据编号：" + orderids + "共" + auditcount + "报价单','" + loginuserid + "','" + loginUser + "',now())");

				ps.executeBatch();
				conn.commit();
				state = "1";
				// 2020-12-28 增加houseid填写验证
				message = "已成功审核" + auditcount + "条报价单";
			} else {
				message = "没有<未审核>状态的报价单可操作";
			}

			conn.setAutoCommit(true);

		} catch (Exception e) {
			// e.printStackTrace();
			message = "批量审核操作失败，请稍后再试。";
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

	// 报价单批量删除
	public static JSONObject delBatchQuotationFunction(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		JSONObject rt = new JSONObject();
		String companyid = params.getString("companyid");
		String mainids = params.getString("mainids");
		String loginuserid = params.getString("loginuserid");
		String loginUser = params.getString("loginUser");
		double countbit = params.getDoubleValue("countbit");

		String state = "0";
		String message = "";

		try {
			Statement ps = conn.createStatement();
			conn.setAutoCommit(false);

			List<Object> fparams = new ArrayList<>();

			int m = 0;
			String[] idssarr = mainids.replaceAll("'", "").split(",");
			String temp = "";
			for (m = 0; m < idssarr.length; m++) {
				temp = temp + (temp.equals("") ? "" : ",") + "?";
				fparams.add(idssarr[m]);
			}

			String sql = "select  ifnull(group_concat(orderid order by  operate_time asc separator ','),'') as orderids,count(*) as reconds from quotation where  quotationid in (" + temp
					+ ") and status='0'";
			Table table = DataUtils.queryData(conn, sql, fparams, null, null, null);
			// 2020-12-28 增加houseid填写验证
			String auditcount = "0";
			String orderids = "";
			if (table.getRows().size() > 0) {
				auditcount = table.getRows().get(0).getValue("reconds").toString();
				orderids = table.getRows().get(0).getString("orderids");
			}

			if (!auditcount.equals("0")) {

				ps.addBatch("delete from t_billflow  where  billid in (select quotationid from quotation  where  quotationid in (" + mainids
						+ ") and status='0' ) and (select 1 from t_auditflow t where t.id = t_billflow.auditflow_id and t.auditflownum=5 limit 1)");

				ps.addBatch("delete from quotationdetail  where  quotationid in (" + mainids + ") and status='0'");
				ps.addBatch("delete from  quotation  where  quotationid in (" + mainids + ") and status='0'");

				ps.addBatch("insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
						+ companyid + "',80,'批量删除','','单据编号：" + orderids + "共" + auditcount + "报价单','" + loginuserid + "','" + loginUser + "',now())");

				ps.executeBatch();
				conn.commit();
				state = "1";
				// 2020-12-28 增加houseid填写验证
				message = "已成功删除" + auditcount + "条报价单";
			} else {
				message = "没有<未审核>状态的报价单可操作";
			}

			conn.setAutoCommit(true);

		} catch (Exception e) {
			// e.printStackTrace();
			message = "批量删除操作失败，请稍后再试。";
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

	public static String getOriginalbillValue(String companyid, Connection conn, String salesordernamerule) {
		String codeid = "";
		String[] arr = salesordernamerule.split(",");
		if (arr.length == 3) {
			Date t = new Date();
			SimpleDateFormat df = null;

			int syear = Integer.parseInt(arr[0]);
			String year = "";
			String stryear = "";
			switch (syear) {
			case 1:
				df = new SimpleDateFormat("yyyy");
				stryear = df.format(t).substring(2);
				break;
			case 2:
				df = new SimpleDateFormat("yyyy");
				stryear = df.format(t);
				break;
			case 3:
				df = new SimpleDateFormat("yyyyMM");
				stryear = df.format(t).substring(2);
				break;
			case 4:
				df = new SimpleDateFormat("yyyyMM");
				stryear = df.format(t);
				break;
			}
			String pre = stryear + arr[1] + "-";

			String sql = "select max(SUBSTRING(originalbill," + (pre.length() + 1) + ",LENGTH(originalbill)-" + pre.length() + ")) from salesorder where companyid='" + companyid
					+ "' and SUBSTRING(originalbill,1," + pre.length() + ")='" + pre + "' and (substring(originalbill," + (pre.length() + 1) + ",LENGTH(originalbill)-" + pre.length()
					+ ") REGEXP '[^0-9]') = 0  ";

			Object cobject = DataUtils.getValueBySQL(conn, sql, null);
			int count;
			if (cobject == null) {
				count = 1;
			} else {
				count = Integer.parseInt(cobject.toString()) + 1;
			}

			if (String.valueOf(count).length() > Integer.parseInt(arr[2])) {
				codeid = pre + count;
			} else {
				codeid = pre + String.format("%0" + arr[2] + "d", count);
			}
		}

		System.out.println("getOriginalbillValue:" + codeid);
		return codeid;
	};

	public static JSONObject getOrderprofitquery(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);

		String companyid = params.getString("companyid");
		String begintime = params.getString("begintime");
		String endtime = params.getString("endtime");

		String staff_id = params.getString("staff_id");
		String order_id = params.getString("order_id");
		String originalbill = params.getString("originalbill");
		String stockstatusselect = params.getString("stockstatusselect");
		Integer infotype = params.getInteger("infotype");
		Integer tojitimestype = params.getInteger("tojitimestypeselect");
		Integer findcheckbox = params.getInteger("findcheckbox");
		String customersql = params.getString("customersql");

		int offset = params.getInteger("offset");
		int limit = params.getInteger("limit");

		int moneybit = params.getInteger("moneybit");
		int countbit = params.getInteger("countbit");

		String datestr = "";

		List<Object> fparams = new ArrayList<>();
		int m = 0;

		String inputsql = " where od.companyid = ? and od.status='1' and od.originalbill<>'' and od.operate_time>=? and od.operate_time<=? " + customersql;

		fparams.add(companyid);
		fparams.add(begintime);
		fparams.add(endtime);

		if (tojitimestype == 0) {
			datestr = "''";
		} else if (tojitimestype == 1) {
			datestr = "DATE_FORMAT(od.operate_time,'%Y-%m')";
		} else if (tojitimestype == 2) {
			datestr = "DATE_FORMAT(od.operate_time,'%Y-%u')";
		} else if (tojitimestype == 3) {
			datestr = "DATE_FORMAT(od.operate_time,'%Y')";
		} else if (tojitimestype == 4) {
			datestr = "DATE_FORMAT(od.operate_time,'%Y-%m-%d')";
		}

		if (!order_id.equals("")) {
			inputsql = inputsql + " and od.orderid = ? ";
			fparams.add(order_id);
		}

		if (!originalbill.equals("")) {
			inputsql = inputsql + " and od.originalbill = ? ";
			fparams.add(originalbill);
		}

		if (!staff_id.equals("")) {

			String[] idssarr = staff_id.replaceAll("'", "").split(",");
			String temp = "";
			for (m = 0; m < idssarr.length; m++) {
				temp = temp + (temp.equals("") ? "" : ",") + "?";
				fparams.add(idssarr[m]);
			}
			inputsql = inputsql + " and od.operate_by in (" + temp + ") ";

		}

		if (!stockstatusselect.equals("")) {
			inputsql = inputsql + " and  od.stockstatus =?  ";
			fparams.add(stockstatusselect);
		}

		if (findcheckbox != null && findcheckbox == 1) {
			inputsql = inputsql + " and  od.total > 0 ";
		}

		String sql = "";
		String sql1 = "";

		if (infotype == 1) {

			sql = "select od.*,s.staffname,c.customername,round(ifnull((select sum(if(st.bill_type='1',st.total,-st.total)) from storein st where st.companyid=od.companyid and st.status='1' and st.originalbill=od.originalbill),0),"
					+ moneybit
					+ ") as storeintotal"
					+ ",round(ifnull((select sum(if(st.bill_type='25',st.processmoney,-st.processmoney)) from outsourcingin st where st.companyid=od.companyid and st.status='1' and st.originalbill=od.originalbill),0),"
					+ moneybit
					+ ") as outsourcingtotal "
					+ ",round(ifnull((select sum(st.pay_money) from dayinout st where st.companyid=od.companyid and st.bill_type='21' and st.status='1' and st.originalbill=od.originalbill),0),"
					+ moneybit
					+ ") as daytotal "
					+ ",round(ifnull((select sum((p.progress_count-p.incount)*p.step_price+p.incount*out_price) from t_order tor inner join t_progress p on tor.id=p.order_id  left join t_stepnew t on p.stepnewid=t.stepnewid where  tor.companyid=od.companyid and tor.order_status=1 and tor.originalbill=od.originalbill and t.s_work_mode=1),0),"
					+ moneybit
					+ ") as reporttotal "
					+ " from salesorder od left join  customer c  on od.customerid=c.customerid  left join staffinfo s on od.operate_by=s.staffid "
					+ inputsql
					+ " order by od.operate_time desc,od.originalbill desc limit " + offset + "," + limit;

			sql1 = "select count(*)  from salesorder od left join  customer c  on od.customerid=c.customerid  left join staffinfo s on od.operate_by=s.staffid " + inputsql;

		} else if (infotype == 2) {
			sql = "select  "
					+ datestr
					+ " as monthdate,round(sum(od.count),"
					+ countbit
					+ ") as count,round(sum(od.total),"
					+ moneybit
					+ ") as total,round(sum(od.otherfee),"
					+ moneybit
					+ ") as otherfee,round(sum(ifnull((select sum(if(st.bill_type='1',st.total,-st.total)) from storein st where st.companyid=od.companyid and st.status='1' and st.originalbill=od.originalbill),0)),"
					+ moneybit
					+ ") as storeintotal  "
					+ ",round(sum(ifnull((select sum(if(st.bill_type='25',st.processmoney,-st.processmoney)) from outsourcingin st where st.companyid=od.companyid and st.status='1' and st.originalbill=od.originalbill),0)), "
					+ moneybit
					+ ") as outsourcingtotal  "
					+ " ,round(sum(ifnull((select sum(st.pay_money) from dayinout st where st.companyid=od.companyid and st.bill_type='21' and st.status='1' and st.originalbill=od.originalbill),0)),"
					+ moneybit
					+ ") as daytotal  "
					+ ",round(sum(ifnull((select sum((p.progress_count-p.incount)*p.step_price+p.incount*out_price) from t_order tor inner join t_progress p on tor.id=p.order_id  left join t_stepnew t on p.stepnewid=t.stepnewid where  tor.companyid=od.companyid and tor.order_status=1 and tor.originalbill=od.originalbill and t.s_work_mode=1),0)),"
					+ moneybit + ") as reporttotal " + "	  from salesorder od  " + inputsql + " group by monthdate order by monthdate asc limit " + offset + "," + limit;

			sql1 = "select count(*)  from (select " + datestr + " as monthdate from salesorder od  " + inputsql + " group by monthdate) a ";
		} else if (infotype == 3) {
			sql = "select c.customername, "
					+ datestr
					+ " as monthdate,round(sum(od.count),"
					+ countbit
					+ ") as count,round(sum(od.total),"
					+ moneybit
					+ ") as total,round(sum(od.otherfee),"
					+ moneybit
					+ ") as otherfee,round(sum(ifnull((select sum(if(st.bill_type='1',st.total,-st.total)) from storein st where st.companyid=od.companyid and st.status='1' and st.originalbill=od.originalbill),0)),"
					+ moneybit
					+ ") as storeintotal  "
					+ ",round(sum(ifnull((select sum(if(st.bill_type='25',st.processmoney,-st.processmoney)) from outsourcingin st where st.companyid=od.companyid and st.status='1' and st.originalbill=od.originalbill),0)), "
					+ moneybit
					+ ") as outsourcingtotal   "
					+ " ,round(sum(ifnull((select sum(st.pay_money) from dayinout st where st.companyid=od.companyid and st.bill_type='21' and st.status='1' and st.originalbill=od.originalbill),0)),"
					+ moneybit
					+ ") as daytotal   "
					+ ",round(sum(ifnull((select sum((p.progress_count-p.incount)*p.step_price+p.incount*out_price) from t_order tor inner join t_progress p on tor.id=p.order_id  left join t_stepnew t on p.stepnewid=t.stepnewid where  tor.companyid=od.companyid and tor.order_status=1 and tor.originalbill=od.originalbill and t.s_work_mode=1),0)),"
					+ moneybit + ") as reporttotal " + "	  from salesorder od left join  customer c  on od.customerid=c.customerid  " + inputsql
					+ " group by c.customername,monthdate order by c.customername asc,monthdate asc limit " + offset + "," + limit;

			sql1 = "select count(*)  from (select c.customername," + datestr + " as monthdate from salesorder od left join  customer c  on od.customerid=c.customerid " + inputsql
					+ " group by c.customername,monthdate) a ";

		} else if (infotype == 4) {
			sql = "select s.staffname, "
					+ datestr
					+ " as monthdate,round(sum(od.count),"
					+ countbit
					+ ") as count,round(sum(od.total),"
					+ moneybit
					+ ") as total,round(sum(od.otherfee),"
					+ moneybit
					+ ") as otherfee,round(sum(ifnull((select sum(if(st.bill_type='1',st.total,-st.total)) from storein st where st.companyid=od.companyid and st.status='1' and st.originalbill=od.originalbill),0)),"
					+ moneybit
					+ ") as storeintotal  "
					+ ",round(sum(ifnull((select sum(if(st.bill_type='25',st.processmoney,-st.processmoney)) from outsourcingin st where st.companyid=od.companyid and st.status='1' and st.originalbill=od.originalbill),0)), "
					+ moneybit
					+ ") as outsourcingtotal   "
					+ " ,round(sum(ifnull((select sum(st.pay_money) from dayinout st where st.companyid=od.companyid and st.bill_type='21' and st.status='1' and st.originalbill=od.originalbill),0)),"
					+ moneybit
					+ ") as daytotal  "
					+ ",round(sum(ifnull((select sum((p.progress_count-p.incount)*p.step_price+p.incount*out_price) from t_order tor inner join t_progress p on tor.id=p.order_id  left join t_stepnew t on p.stepnewid=t.stepnewid where  tor.companyid=od.companyid and tor.order_status=1 and tor.originalbill=od.originalbill and t.s_work_mode=1),0)),"
					+ moneybit + ") as reporttotal " + "	  from salesorder od left join staffinfo s on od.operate_by=s.staffid  " + inputsql
					+ " group by s.staffname,monthdate order by s.staffname asc,monthdate asc limit " + offset + "," + limit;

			sql1 = "select count(*)  from (select s.staffname," + datestr + " as monthdate from salesorder od left join staffinfo s on od.operate_by=s.staffid " + inputsql
					+ " group by s.staffname,monthdate) a ";

		}

		// System.out.println(sql);
		// System.out.println(sql1);

		Table table = DataUtils.queryData(conn, sql, fparams, null, null, null);

		Object countObject = DataUtils.getValueBySQL(conn, sql1, fparams);
		int count;
		if (countObject == null) {
			count = 0;
		} else {
			count = Integer.parseInt(countObject.toString());
		}

		JSONObject ret = new JSONObject();
		ret.put("table", Transform.tableToJson(table));
		ret.put("rowsize", count);
		return ret;

	}

	public static JSONObject updateOrderProfit(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);
		String salesorderid = params.getString("salesorderid").replaceAll("'", "");
		String otherremark = params.getString("otherremark");
		String userid = params.getString("userid");
		String username = params.getString("username");
		String companyid = params.getString("companyid");
		Double otherfee = params.getDoubleValue("otherfee");
		String orderid = params.getString("orderid");
		int stype = params.getInteger("stype");// 更新1- otherfee,2-otherremark

		JSONObject rt = new JSONObject();
		String message = "";
		try {

			if (salesorderid.length() != 32) {
				message = "含非法数据，操作失败!";
			} else {
				Statement ps = conn.createStatement();
				String sql = "";
				if (stype == 1) {
					ps.addBatch("update salesorder set otherfee=" + otherfee + ",update_id='" + userid + "',update_by='" + username + "',update_time=now() where salesorderid='" + salesorderid + "'");
					sql = "insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
							+ companyid + "',28,'修改其他费用','" + salesorderid + "','" + ("【订单编号：" + orderid + " 】【其他费用：" + otherfee + "】 ") + "','" + userid + "','" + username + "',now())";

				} else if (stype == 2) {
					ps.addBatch("update salesorder set otherremark='" + otherremark + "',update_id='" + userid + "',update_by='" + username + "',update_time=now() where salesorderid='" + salesorderid
							+ "'");
					sql = "insert into datachange_log (id,companyid,changefunc,changeoperate,recordid,content,create_id,create_by,create_time) VALUES ('" + Common.getUpperUUIDString() + "','"
							+ companyid + "',28,'修改预估利润备注','" + salesorderid + "','" + ("【订单编号：" + orderid + " 】【备注：" + otherremark + "】 ") + "','" + userid + "','" + username + "',now())";

				}
				ps.addBatch(sql);

				ps.executeBatch();
				ps.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
				message = message + e.getMessage().toString();
			} catch (Exception e1) {
				e1.printStackTrace();
				message = message + e.getMessage().toString();
			}
		} finally {
			conn.close();
		}
		rt.put("message", message);
		return rt;
	}

	public static JSONObject getOrderprofitDetail(JSONObject params, ActionContext context) throws SQLException, NamingException {
		Connection conn = context.getConnection(DATASOURCE);

		String companyid = params.getString("companyid");
		String originalbill = params.getString("originalbill");
		int infotype = params.getInteger("infotype");

		int countbit = params.getInteger("countbit");
		int moneybit = params.getInteger("moneybit");

		int offset = params.getInteger("offset");
		int limit = params.getInteger("limit");

		String sql = "";
		String sql1 = "";

		List<Object> fparams = new ArrayList<>();

		String inputsql = " where od.companyid=? and od.originalbill=? and od.status='1' ";
		fparams.add(companyid);
		fparams.add(originalbill);
		if (infotype == 1) {

			sql = "select od.*,s.staffname,c.customername, if(od.bill_type='1',od.count,-od.count) as fcount, if(od.bill_type='1',od.total,-od.total) as ftotal  "
					+ " from storein od left join  customer c  on od.customerid=c.customerid  left join staffinfo s on od.operate_by=s.staffid " + inputsql
					+ " order by od.operate_time asc,od.originalbill asc limit " + offset + "," + limit;

			sql1 = "select count(*)  from storein od left join  customer c  on od.customerid=c.customerid  left join staffinfo s on od.operate_by=s.staffid " + inputsql;

		} else if (infotype == 2) {
			sql = "select od.*,s.staffname,c.customername,if(od.bill_type='1',od.count,-od.count) as fcount, if(od.bill_type='25',od.processmoney,-od.processmoney) as ftotal  "
					+ " from outsourcingin od left join  customer c  on od.customerid=c.customerid  left join staffinfo s on od.operate_by=s.staffid " + inputsql
					+ " order by od.operate_time asc,od.originalbill asc limit " + offset + "," + limit;

			sql1 = "select count(*)  from outsourcingin od left join  customer c  on od.customerid=c.customerid  left join staffinfo s on od.operate_by=s.staffid " + inputsql;

		} else if (infotype == 3) {

			sql = "select od.*,ifnull(s.staffname,'') as staffname, od.pay_money as ftotal  " + " from dayinout od left join staffinfo s on od.operate_by=s.staffid " + inputsql
					+ " and od.bill_type='21' order by od.operate_time asc,od.originalbill asc limit " + offset + "," + limit;

			sql1 = "select count(*) from dayinout od left join staffinfo s on od.operate_by=s.staffid " + inputsql + " and od.bill_type='21' ";

		} else if (infotype == 4) {

			sql = "select tor.billno,p.id,p.detail_id,p.class_id,p.step_id,p.order_id,p.progress_type,p.step_no,p.step_name,p.step_price,p.out_price,p.work_count,p.progress_count,round(p.progress_count-p.incount,"
					+ countbit
					+ ") as fcount ,p.incount,round((p.progress_count-p.incount)*p.step_price,"
					+ moneybit
					+ ") as reportmoney, round(p.incount*p.out_price,"
					+ moneybit
					+ ") as outmoney from t_order tor inner join t_progress p on tor.id=p.order_id  left join t_stepnew t on p.stepnewid=t.stepnewid where  tor.companyid=? and tor.order_status=1 and tor.originalbill=? and t.s_work_mode=1 and (p.progress_count>0 or p.incount>0) order by tor.billno asc,p.detail_id asc,p.step_no asc limit "
					+ offset + "," + limit;

			sql1 = "select count(*) from  t_order tor inner join t_progress p on tor.id=p.order_id  left join t_stepnew t on p.stepnewid=t.stepnewid where  tor.companyid=? and tor.order_status=1 and tor.originalbill=? and t.s_work_mode=1 and (p.progress_count>0 or p.incount>0) ";

		}
		Table table = DataUtils.queryData(conn, sql, fparams, null, null, null);

		Object countObject = DataUtils.getValueBySQL(conn, sql1, fparams);
		int count;
		if (countObject == null) {
			count = 0;
		} else {
			count = Integer.parseInt(countObject.toString());
		}

		JSONObject ret = new JSONObject();
		ret.put("table", Transform.tableToJson(table));
		ret.put("rowsize", count);
		return ret;

	}
}
