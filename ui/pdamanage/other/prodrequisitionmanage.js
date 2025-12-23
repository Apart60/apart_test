define(function(require) {
	var $ = require("jquery");
	var justep = require("$UI/system/lib/justep");
	var mlfcommon = require("./../../js/mlfcommon");
	var pdacommon = require("./../../js/pdacommon");

	var Model = function() {
		this.callParent();
		this.companyid = "";
		this.companyname = "";
		this.loginuserid = "";
		this.loginUser = "";
		this.detail = true;
		this.copynew = true;
		this.status = true; // 正常/作废
		this.datastr = "";
		// dtype 1-公共规则 2-私有规则 3-不能查看数据列
		this.dtype1 = "";// 1-公共规则
		this.curdtype1 = "";// 导出公共规则
		this.canprint = false;
		this.countbit = 0;
		this.moneybit = 2;
		this.edit = false;
	};

	Model.prototype.cutZero = function(value) {
		return pdacommon.cutZero(value);
	};

	Model.prototype.modelLoad = function(event) {
		var parent = this.getParentModel();
		if (parent === undefined) {
			window.open(require.toUrl("./../index.w"), "_self");
		}

		var loginUserData = this.getParentModel().comp("userinfoData");
		this.loginuserid = loginUserData.val("userid");
		this.loginUser = loginUserData.val('realname') + "[" + loginUserData.val('username') + "]";
		this.companyid = loginUserData.val("companyid");

		var companyData = this.getParentModel().comp("companyData");
		this.companyname = companyData.val("companyname");
		this.moneybit = companyData.getValue("moneybit");
		this.countbit = companyData.getValue("countbit");
		this.pricebit = companyData.getValue("pricebit");

		// 2020-12-19 获取辅助单位设置 ，约定-3个辅助运算列的列名需为：原列名+tounit1、2、3的形式
		this.companyData = companyData;
		var option = [ {
			grid : "grid2", // grid表格xid
			needconvertcol : [ "count" ],// 加入需要换算的数量列的列名
			needconvertcolname : [ "" ], // 加入需要换算的数量列的名称
		} ];
		this.unitsetdata = pdacommon.createUnitOption(this, this.companyData, option);
		// 获取辅助单位设置 ---end

		// 按钮权限过滤 begin
		var permissiondata = this.getParentModel().comp("permissiondata");
		var crowdata = permissiondata.find([ "fvalue" ], [ "prodrequisitiondata:new" ]);
		if (crowdata.length > 0) {// 增加
			$(this.getElementByXid("add")).show();
			this.edit = true;
			if (justep.Browser.isPC) {
				$(this.getElementByXid("infoexample")).show();
				$(this.getElementByXid("tolist")).show();
			}
		}
		
		crowdata = permissiondata.find([ "fvalue" ], [ "prodrequisitiondata:read" ]);
		if (crowdata.length === 0) {// 查看功能
			justep.Util.hint("没有权限操作此功能！", {
				"type" : "warning",
				"position" : "middle"
			});
			$(this.getElementByXid("window")).hide();
		}
		// crowdata = permissiondata.find([ "fvalue" ], [ "draftbilldata:read"
		// ]);
		// if (crowdata.length > 0) {// 暂存
		// $(this.getElementByXid("todraftbillBtn")).show();
		// }

		crowdata = permissiondata.find([ "fvalue" ], [ "prodrequisitiondata:exporttotal" ]);
		if (crowdata.length > 0) {// 导出汇总
			if (justep.Browser.isPC)
				$(this.getElementByXid("toexceltotal")).show();
		}

		crowdata = permissiondata.find([ "fvalue" ], [ "prodrequisitiondata:exportdetail" ]);
		if (crowdata.length > 0) {// 导出明细
			if (justep.Browser.isPC)
				$(this.getElementByXid("toexceldetail")).show();
		}

		crowdata = permissiondata.find([ "fvalue" ], [ "prodrequisitiondata:detail" ]);
		if (crowdata.length === 0)
			this.detail = false;// 详情

		crowdata = permissiondata.find([ "fvalue" ], [ "prodrequisitiondata:copynew" ]);
		if (crowdata.length === 0)
			this.copynew = false;// 复制

		crowdata = permissiondata.find([ "fvalue" ], [ "prodrequisitiondata:status" ]);
		if (crowdata.length === 0)
			this.status = false;// 正常作废

		crowdata = permissiondata.find([ "fvalue" ], [ "prodrequisitiondata:print" ]);
		if (crowdata.length > 0) {// 增加
			if (justep.Browser.isPC)
				this.canprint = true;// 打印
		}

		crowdata = permissiondata.find([ "fvalue" ], [ "prodrequisitiondata:showprice" ]);
		if (crowdata.length > 0)
			this.showprice = true;// 单价显示

		// 数据规则检验 begin
		var dataruledata = this.getParent().comp("dataruledata");
		var rulesrow = dataruledata.find([ "pfvalue" ], [ "prodrequisitiondata:read" ]);
		// dtype 1-公共规则 2-私有规则 3-不能查看数据列
		if (rulesrow !== null && rulesrow.length > 0) {
			var data = pdacommon.getDataRules(rulesrow, loginUserData, companyData, "md.create_id");
			this.dtype1 = data.dtype1;// 1-公共规则
			data = pdacommon.getDataRules(rulesrow, loginUserData, companyData, "pr.create_id");
			this.curdtype1 = data.dtype1;// 导出公共规则
		}
		// 数据规则检验 end
	};

	Model.prototype.modelParamsReceive = function(event) {
		var loginUserData = this.getParentModel().comp("userinfoData");
		this.loginuserid = loginUserData.val("userid");
		this.loginUser = loginUserData.val('realname') + "[" + loginUserData.val('username') + "]";
		this.companyid = loginUserData.val("companyid");

		this.companyname = this.getParentModel().comp("companyData").val("companyname");

		// 2020-12-08
		this.housefilter = localStorage.getItem(this.loginuserid + "housefilter");
		this.customerfilter = localStorage.getItem(this.loginuserid + "customerfilter");

		var prodrequisitionData = this.comp("prodrequisitionData");
		var storehouseData = this.comp("storehouseData");
		var customerData = this.comp("customerData");

		prodrequisitionData.clear();
		storehouseData.clear();
		customerData.clear();

		this.clearBtnClick();// 清除所有还原默认条件

		storehouseData.filters.setVar("companyid", this.companyid);
		storehouseData.setFilter("q_housefilter", this.housefilter); // 2020-12-08
		storehouseData.refreshData();

		customerData.filters.setVar("companyid", this.companyid);
		customerData.setFilter("q_customerfilter", this.customerfilter); // 2020-12-08
		customerData.refreshData();

		// 2020-12-08
		// prodrequisitionData.setFilter("q_housefilter", "prodrequisitionid in
		// (select d.prodrequisitionid from prodrequisitiondetail d where
		// d.houseid in (select houseid from storehouse where "+housefilter+"
		// ))");
		// prodrequisitionData.setFilter("q_customerfilter", "customerid in
		// (select customerid from customer where "+customerfilter+" )");

		if (this.dtype1 !== "") {// 1-公共规则 数据过滤 or 结构
			prodrequisitionData.setFilter("dtype1", this.dtype1);
		}

		// prodrequisitionData.refreshData();
		// prodrequisitionData.first();

		pdacommon.initdayselect2(this, this.comp("groupmenu"), this.comp("begininput"), this.comp("endinput"), null);// 加载日期选择框

		// house初始化变更操作
		var me = this;
		$($(this.getElementByXid("housegridSelect")).find("input")[0]).click(function() {
			$(this).select();
		});
		$($(this.getElementByXid("housegridSelect")).find("input")[0]).change(function() {
			var house = $(this).val().trim();
			me.comp("fconditiondata").setValue("housename", house === "" ? "所有仓库" : house);
			if (house === "")
				me.comp("fconditiondata").setValue("houseid", "");
			me.prodrequisitionDataBRefresh();
		});

		$($(this.getElementByXid("customgridSelect")).find("input")[0]).click(function() {
			$(this).select();

		});
		$($(this.getElementByXid("customgridSelect")).find("input")[0]).change(function() {
			var custom = $(this).val().trim();
			me.comp("fconditiondata").setValue("customname", custom === "" ? "所有领用部门" : custom);
			me.prodrequisitionDataBRefresh();
		});

		$(this.getElementByXid("searchcontent")).click(function(params) {
			$(this).select();
		});

		$(this.getElementByXid("iteminput")).click(function(params) {
			$(this).select();
		});

		$(this.getElementByXid("billinput")).click(function(params) {
			$(this).select();
		});
		
		$(this.getElementByXid("iteminput")).focus();

		// this.downdivClick();

	};

	Model.prototype.prodrequisitionDataBRefresh = function() {

		var prodrequisitionData = this.comp("prodrequisitionData");
		var fconditiondata = this.comp("fconditiondata");
		var custom = fconditiondata.getValue("customname");
		var house = fconditiondata.getValue("housename");

		prodrequisitionData.clear();

		if (custom === "所有领用部门") {
			if (this.customerfilter == "1=1") {
				prodrequisitionData.setFilter("customerfilter", "1=1");
			} else {
				prodrequisitionData.setFilter("customerfilter", " (select 1 from customer where  customerid=md.customerid and " + this.customerfilter + " limit 1)  ");
			}
		} else {
			if (fconditiondata.getValue("customid") === "") {
				prodrequisitionData.setFilter("customerfilter", "(c.customername like '%" + fconditiondata.getValue("customname") + "%' or c.customercode like '%"
						+ fconditiondata.getValue("customname") + "%')"
						+ (this.customerfilter == "1=1" ? "" : " and (select 1 from customer where  customerid=md.customerid and " + this.customerfilter + " limit 1)  "));
			} else {
				prodrequisitionData.setFilter("customerfilter", "md.customerid='" + fconditiondata.getValue("customid") + "'");
			}
		}

		if (house === "所有仓库") {// 2020-11-05 修改仓库是在明细表查询
			if (this.housefilter == "1=1") {
				prodrequisitionData.setFilter("housefilter", "1=1");
			} else {
				prodrequisitionData.setFilter("housefilter",
						"  (select 1 from prodrequisitiondetail d where  d.prodrequisitionid=md.prodrequisitionid  and d.stype='101' and   (select  1 from storehouse where houseid=d.houseid and "
								+ this.housefilter + " limit 1)  " + " limit 1)  ");
			}
		} else {
			if (fconditiondata.getValue("houseid") === "") {

				prodrequisitionData.setFilter("housefilter", " (select 1 from prodrequisitiondetail d,storehouse sh   where d.prodrequisitionid=md.prodrequisitionid "
						+ (this.housefilter == "1=1" ? "" : " and  (select  1 from storehouse where houseid=d.houseid and " + this.housefilter + " limit 1)  ")
						+ " and d.stype='101' and d.houseid=sh.houseid and (sh.housename like '%" + fconditiondata.getValue("housename") + "%' or sh.housecode like '%"
						+ fconditiondata.getValue("housename") + "%')  limit 1) ");
			} else {
				prodrequisitionData.setFilter("housefilter", "  (select 1 from prodrequisitiondetail d where d.prodrequisitionid=md.prodrequisitionid  and d.stype='101' and d.houseid='"
						+ fconditiondata.getValue("houseid") + "'" + " limit 1)  ");
			}
		}
		// prodrequisitionData.refreshData();

	};

	Model.prototype.customgridSelectUpdateValue = function(event) {
		this.prodrequisitionDataBRefresh();
	};

	Model.prototype.housegridSelectUpdateValue = function(event) {
		this.prodrequisitionDataBRefresh();
	};

	Model.prototype.grid1CellRender = function(event) {
		if (event.colName === "operate") {
			var schedule_pick_id = event.row.val("schedule_pick_id");
			var status = event.row.val("status");
			var showCopyNew = (this.copynew && schedule_pick_id === "");
			var showInvalid = (this.status && schedule_pick_id === "" && status === "1");
			event.html = ((status === "0" || status === "3") && this.edit ? "<button class='btn btn-link btn-sm linkbtn'  onclick='justep.Util.getModel(this).editBtnClick(event,\"" + event.rowID
					+ "\",\"" + event.row.val("orderid") + "\")'>编辑</button>" : "")
					+ (status === "0" && this.edit ? "<button class='btn btn-link btn-sm linkbtn'  id='" + event.rowID + "mainstatusbtn'   onclick='justep.Util.getModel(this).deleteBtnClick(event,\""
							+ event.rowID + "\")'>删除</button>" : "")
					+ (this.detail === false ? "" : "<button class='btn btn-link btn-sm linkbtn'  onclick='justep.Util.getModel(this).detailBtnClick(event,\"" + event.rowID + "\")'>详情</button>")
					+ (showCopyNew ? "<button class='btn btn-link btn-sm linkbtn'  onclick='justep.Util.getModel(this).copyBtnClick(event,\"" + event.rowID + "\")'>复制</button>" : "")
					+ (showInvalid ? "<button class='btn btn-link btn-sm linkbtn'  id='" + event.rowID
							+ "prodrequisitionstatusbtn'   onclick='justep.Util.getModel(this).statusBtnClick(event,\"" + event.rowID + "\")'>作废</button>" : "")
					+ (this.canprint === false ? "" : (status!=="2" ? "<button class='btn btn-link btn-sm linkbtn'  id='" + event.rowID
							+ "prodrequisitionprint'   onclick='justep.Util.getModel(this).printBtnClick(event,\"" + event.rowID + "\")'>打印</button>" : ""));
		}
		if (event.colName === "status") {
			event.html = "<font style='color:" + pdacommon.getstoreoutcolor(event.colVal) + ";'>" + pdacommon.getstoreoutstatus(event.colVal) + "</font>";
		}

		if (event.colName === "billno" && event.row !== null) {
			if (event.colVal !== "" && event.colVal !== undefined) {
				event.html = "<a  onclick='justep.Util.getModel(this).showt_orderClick(event,\"" + event.row.val("worksheetid") + "\")'><font style='text-decoration:underline;'>" + event.colVal
						+ "</font></a>";
			} else {
				event.html = "";
			}
		}
		// if(event.colName === "reworksheet"){
		// var reworksheet = event.colVal;
		// event.html = reworksheet==="0"||reworksheet===undefined ? "" : "<span
		// style='color:red;'>是</>";
		// }

		if ((",count,worksheetcount,total,").indexOf("," + event.colName + ",") > -1) {
			if (event.colVal === 0) {
				event.html = "";
			} else {
				if (event.colVal < 0) {
					event.html = "<font style='color:red;'>" + mlfcommon.formatNumber(event.colVal) + "</font>";
				} else {
					event.html = mlfcommon.formatNumber(event.colVal);
				}
			}
		}
		
		

		if (event.colName === 'total' && !this.showprice && event.colVal !== undefined) {
			event.html = "--";
		}

	};

	// 删除单据数据
	Model.prototype.deleteBtnClick = function(event, rowid) {
		var mainData = this.comp("prodrequisitionData");
		var row = mainData.getRowByID(rowid);
		var me = this;
		justep.Util.confirm("[" + pdacommon.getbilltype(row.val("bill_type")) + "]单据编号[" + row.val("orderid") + "]，一旦删除，不能恢复，确认删除吗？", function() {

			justep.Baas.sendRequest({
				"url" : "/erpscan/save/pdainvalid",
				"action" : "deleteDraftBillFunction",
				"async" : false,
				"params" : {
					"mainid" : rowid,
					"loginuserid" : me.loginuserid,
					"loginUser" : me.loginUser,
					"tname" : "prodrequisition",
					"orderid" : row.val("orderid"),
					"companyid" : row.val("companyid"),
					"billtype" : row.val("bill_type")
				},
				"success" : function(data) {
					if (data.state === "1") {

						if (mainData.getTotal() > 10) {
							mainData.remove(row);
						} else {
							mainData.refreshData();
						}
					} else if (data.state === "2") {
						me.comp("message").show({
							"title" : "删除操作提示",
							"message" : "单据[" + row.val("orderid") + "]" + data.message
						});
						if (mainData.getTotal() > 10) {
							mainData.remove(row);
						} else {
							mainData.refreshData();
						}
					} else {
						justep.Util.hint(data.message, {
							"type" : "danger",
							"position" : "middle"
						});
					}
				},
				"error" : function() {
					justep.Util.hint("删除操作失败，请稍后再试。", {
						"type" : "danger",
						"position" : "middle"
					});
				}
			});

		});

	};

	// 查看详情
	Model.prototype.showt_orderClick = function(event, rowid) {
		this.getParent().closePage("summaryorderdetail");

		var params = {
			"mainid" : rowid,
			"furlname" : "showdetailbyorderid"
		};
		var pdata = JSON.stringify(params);
		localStorage.setItem("summaryorderdetail", pdata);
		this.getParent().showPage(this.getParent()._cfg.summaryorderdetail);

	};

	// 打印
	Model.prototype.printBtnClick = function(event, rowid) {
		var row = this.comp("prodrequisitionData").getRowByID(rowid);
		this.getdetaildata(rowid, row);// 加载明细数据再操作

		var showbillno = false;
		var scount = this.comp("prodrequisitiondetailData").count(function(ev) {
			return ev.source.getValue("worksheetid") !== "";
		});

		if (scount === 0 || this.comp("prodrequisitionData").getValue("worksheetid") !== "") {
			showbillno = false;
		} else if (scount > 0) {
			showbillno = true;
		}

		this.comp("pringDialog").open({
			"data" : {
				"mainid" : rowid,
				"allcount" : this.comp("prodrequisitiondetailData").count(),
				"showbillno" : showbillno,
				"dialogid" : this.getIDByXID("pringDialog")
			},
			"src" : require.toUrl("./prodrequisitionprint.w"),
			"status" : justep.Browser.isPC ? "normal" : "maximize"
		});
	};

	// 点击主表行，加载数据
	Model.prototype.grid1RowClick = function(event) {

		this.getdetaildata(event.rowID, event.row);
	};

	Model.prototype.getdetaildata = function(rowid, row) {
		var prodrequisitiondetailData = this.comp("prodrequisitiondetailData");
		var dcount = prodrequisitiondetailData.count();
		// 已加载了明细不再加载，没有就加载
		if (rowid !== "" && rowid !== undefined && (dcount === 0 || (dcount > 0 && prodrequisitiondetailData.getValue("orderid") !== row.val("orderid")))) {
			prodrequisitiondetailData.clear();
			prodrequisitiondetailData.filters.setVar("prodrequisitionid", rowid);
			prodrequisitiondetailData.refreshData();
			var property = row.val("iproperty");
			var grid2 = this.comp("grid2");
			if (property !== undefined) {
				var propertyarr = property.split(";");
				grid2.hideCol("property1");
				grid2.hideCol("property2");
				grid2.hideCol("property3");
				grid2.hideCol("property4");
				grid2.hideCol("property5");
				for (var i = 0; i < propertyarr.length; i++) {
					var arr = propertyarr[i].split(",");
					if (arr[0] !== "") {
						grid2.showCol(arr[0]);
						grid2.setLabel(arr[0], arr[1]);
					}
				}
			}

			var scount = prodrequisitiondetailData.count(function(ev) {
				return ev.source.getValue("worksheetid") !== "";
			});

			if (scount === 0 || this.comp("prodrequisitionData").getCurrentRow().val("worksheetid") !== "") {
				grid2.hideCol("worksheetbillno");
				grid2.hideCol("order_id");
				grid2.hideCol("pcodeid");
			} else {
				grid2.showCol("worksheetbillno");
				grid2.showCol("order_id");
				grid2.showCol("pcodeid");
			}

		}
	};

	// 刷新 条件为默认值，刷新数据源
	Model.prototype.refreshBtnClick = function(event) {

		this.clearBtnClick();
		// var prodrequisitionData = this.comp("prodrequisitionData");
		// prodrequisitionData.refreshData();
		// prodrequisitionData.first();
		this.findBtnClick();

	};
	// 只清除查询条件
	Model.prototype.clearBtnClick = function() {
		this.comp("datetypeselect").val("operate_time");
		this.comp("searchcontent").val("");
		this.comp("iteminput").val("");
		this.comp("billinput").val("");
		this.comp("statusselect").val("-1");

		var curdate = justep.Date.toString(new Date(), justep.Date.STANDART_FORMAT_SHOT);
		var startdate = justep.Date.toString(justep.Date.decrease(justep.Date.increase(new Date(), 1, 'd'), 3, 'm'), justep.Date.STANDART_FORMAT_SHOT);
		this.comp("begininput").val(startdate);
		this.comp("endinput").val("");

		var fconditiondata = this.comp("fconditiondata");
		fconditiondata.setValue("houseid", "");
		fconditiondata.setValue("housename", "所有仓库");
		fconditiondata.setValue("customid", "");
		fconditiondata.setValue("customname", "所有领用部门");

		var prodrequisitionData = this.comp("prodrequisitionData");
		prodrequisitionData.filters.setVar("companyid", this.companyid);
		prodrequisitionData.setFilter("searchFilter", "md.bill_type='10' and false");
		prodrequisitionData.setFilter("statusfilter", "md.status<>'2'");
		prodrequisitionData.setFilter("housefilter", "1=1");
		prodrequisitionData.setFilter("customerfilter", "1=1");

		this.prodrequisitionDataBRefresh();
	};
	// 查找条件
	Model.prototype.findBtnClick = function(event) {
		this.comp("prodrequisitiondetailData").clear();

		var prodrequisitionData = this.comp("prodrequisitionData");
		var searchcontent = mlfcommon.transformSpecialInfo01(this.comp("searchcontent").val().trim());
		var datesql = "";

		var begininput = this.comp("begininput").val();
		var endinput = this.comp("endinput").val();
		if (begininput === "" || begininput === null || begininput === undefined) {
			justep.Util.hint("请选择开始日期", {
				type : "danger",
				position : "middle"
			});
			return;
		}

		// if (endinput === "" || endinput === null || endinput === undefined) {
		// justep.Util.hint("请选择结束日期", {
		// type : "danger",
		// position : "middle"
		// });
		// return;
		// }

		var datetypeselect = this.comp("datetypeselect").val();

		datesql = (begininput !== "" && begininput !== null ? " md." + datetypeselect + ">='" + begininput + "'" : "");
		datesql = datesql + (datesql === "" ? "" : " and ") + (endinput !== "" && endinput !== null ? "md." + datetypeselect + "<='" + endinput + " 23:59:59'" : "1=1");

		// 查找商品信息
		var itemsql = "";
		var iteminput = mlfcommon.transformSpecialInfo01(this.comp("iteminput").val().trim());
		if (iteminput !== "") {
			itemsql = " and (select 1 from prodrequisitiondetail s,iteminfo i where s.prodrequisitionid= md.prodrequisitionid and s.itemid=i.itemid " + " and  s.status='1' and (((i.codeid like '%"
					+ iteminput + "%' or i.barcode like '%" + iteminput + "%' or i.itemname like '%" + iteminput + "%' or i.sformat like '%" + iteminput + "%') or s.batchno like '%" + iteminput
					+ "%' or s.remark like '%" + iteminput + "%')  ) limit 1) ";
		}

		var billsql = "";
		var billinput = mlfcommon.transformSpecialInfo01(this.comp("billinput").val().trim());
		if (billinput !== "") {
			billsql = " and (select 1 from prodrequisitiondetail s,t_order tor left join iteminfo i on tor.itemid=i.itemid where  s.prodrequisitionid= md.prodrequisitionid and "
					+ " s.worksheetid=tor.id  and s.status='1'  and ( tor.billno like '%" + billinput + "%' or tor.originalbill like '%" + billinput + "%' or tor.order_id like '%" + billinput
					+ "%' or  i.codeid like '%" + billinput + "%' or i.itemname like '%" + billinput + "%' or i.sformat like '%" + billinput + "%') limit 1) ";
		}

		prodrequisitionData.setFilter("searchFilter", (datesql === "" ? "" : datesql + " and ")
				+ (searchcontent === "" ? " md.bill_type='10' " : "  md.bill_type='10' and (md.orderid like '%" + searchcontent + "%' or s.staffname like '%" + searchcontent
						+ "%' or md.remark like '%" + searchcontent + "%' or md.originalbill like '%" + searchcontent + "%' or md.create_by like '%" + searchcontent + "%' or md.update_by like '%"
						+ searchcontent + "%')") + itemsql + " " + billsql);

		// console.log((datesql === "" ? "" : datesql + " and ")
		// + (searchcontent === "" ? "1=1" : "(orderid like '%" + searchcontent
		// + "%' or staffname like '%" + searchcontent
		// + "%' or remark like '%" + searchcontent + "%' or originalbill like
		// '%" + searchcontent + "%' or create_by like '%" + searchcontent + "%'
		// or update_by like '%" + searchcontent
		// + "%')") + itemsql+" "+billsql);

		prodrequisitionData.refreshData();

	};
	// 查看详情
	Model.prototype.detailBtnClick = function(event, rowid) {
		var rowdata = this.comp("prodrequisitionData").getRowByID(rowid);
		this.getParent().closePage(this.getParent()._cfg.prodrequisitiondetail.xid);
			var params = {
				"mainid" : rowid,
				"furlname" : "prodrequisitionmanage"
			};
			var pdata = JSON.stringify(params);
			localStorage.setItem("prodrequisitiondetail", pdata);
			this.getParent().showPage(this.getParent()._cfg.prodrequisitiondetail);
	};

	// 新增生产领用单
	Model.prototype.addClick = function(event) {
		// 关闭打开生产领用单
		this.getParent().closePage(this.getParent()._cfg.prodrequisitionedit.xid);
		var params = {
			"operate" : "new",
			"furlname" : "prodrequisitionmanage"
		};
		var pdata = JSON.stringify(params);
		localStorage.setItem("prodrequisitionedit", pdata);
		// 打开新的生产领用单
		this.getParent().showPage(this.getParent()._cfg.prodrequisitionedit);
	};

	// 编辑详情
	Model.prototype.editBtnClick = function(event, rowid, orderid) {
		this.getParent().closePage(this.getParent()._cfg.prodrequisitionedit.xid);
		var params = {
			"mainid" : rowid,
			"oldorderid" : orderid,
			"furlname" : "prodrequisitionmanage",
			"operate" : "draftedit"
		};
		var pdata = JSON.stringify(params);
		localStorage.setItem("prodrequisitionedit", pdata);

		this.getParent().showPage(this.getParent()._cfg.prodrequisitionedit);

	};

	// 复制新增生产领用单
	Model.prototype.copyBtnClick = function(event, rowid) {
		var rowdata = this.comp("prodrequisitionData").getRowByID(rowid);

		this.getdetaildata(rowid, rowdata);// 加载明细数据再操作

		this.getParent().closePage(this.getParent()._cfg.prodrequisitionedit.xid);
		var prodrequisitiondetailData = this.comp("prodrequisitiondetailData");
		if (prodrequisitiondetailData.count() > 0) {
			var params = {
				"rowData" : rowdata.toJson(),
				"detailData" : prodrequisitiondetailData.toJson(),
				"operate" : "copynew",
				"furlname" : "prodrequisitionmanage"
			};
			var pdata = JSON.stringify(params);
			localStorage.setItem("prodrequisitionedit", pdata);
			this.getParent().showPage(this.getParent()._cfg.prodrequisitionedit);
		} else {
			justep.Util.hint("数据加载失败，请稍后再试。", {
				"type" : "danger",
				"position" : "middle"
			});
		}
	};
	// 作废数据
	Model.prototype.statusBtnClick = function(event, rowid) {
		var prodrequisitionData = this.comp("prodrequisitionData");
		var row = prodrequisitionData.getRowByID(rowid);
		
		if (row.val("schedule_pick_id") !== ""){
			justep.Util.hint("此单据由排产领料单【" + row.val("schedule_pick_id") + "】生成，若要作废请作废该排产领料单", {
				"type" : "danger",
				"position" : "middle"
			});
			return;
		}

		var me = this;
		if (row.val("status") === "1") {
			justep.Util.confirm("确定要作废单据[" + row.val("orderid") + "]吗？", function() {

				var operate_time = justep.Date.toString(row.val("operate_time"), justep.Date.STANDART_FORMAT_SHOT);

				justep.Baas.sendRequest({
					"url" : "/erpscan/erp/otheroperate",
					"action" : "invalidProdrequisitionFunction",
					"async" : false,
					"params" : {
						"mainid" : rowid,
						"loginuserid" : me.loginuserid,
						"loginUser" : me.loginUser,
						"worksheetcount" : row.val("worksheetcount"),
						"worksheetid" : row.val("worksheetid"),
						"operate_time" : operate_time,
						"pricebit" : me.getParent().comp("companyData").val("pricebit"),
						"moneybit" : me.getParent().comp("companyData").val("moneybit"),
						"orderid" : row.val("orderid"),
						"companyid" : row.val("companyid"),
						"reworksheet" : row.val("reworksheet")
					},
					"success" : function(data) {
						if (data.state === "1") {
							row.val("status", "2");
							justep.Util.hint("单据[" + row.val("orderid") + "]已作废", {
								"type" : "success",
								"position" : "middle"
							});
							document.getElementById(rowid + "prodrequisitionstatusbtn").style.display = "none";
							document.getElementById(rowid + "prodrequisitionprint").style.display = "none";
						} else if (data.state === "2") {
							me.comp("message").show({
								"title" : "作废操作提示",
								"message" : "单据[" + row.val("orderid") + "]" + data.message
							});
						} else if (data.state === "3") {
							justep.Util.hint("单据[" + row.val("orderid") + "]" + data.message, {
								"type" : "info",
								"position" : "middle"
							});
							prodrequisitionData.refreshData();
						} else {
							justep.Util.hint(data.message, {
								"type" : "danger",
								"position" : "middle"
							});
						}
					},
					"error" : function() {
						justep.Util.hint("作废操作失败，请稍后再试。", {
							"type" : "danger",
							"position" : "middle"
						});
					}
				});

			});
		} else if (row.val("status") === "2") {
			justep.Util.hint("单据[" + row.val("orderid") + "]已作废", {
				"type" : "danger",
				"position" : "middle"
			});
		}

	};

	// 选择状态
	Model.prototype.statusselectChange = function(event) {

		var svalue = event.value;
		var prodrequisitionData = this.comp("prodrequisitionData");
		prodrequisitionData.clear();
		prodrequisitionData.setFilter("statusfilter", (svalue === "" ? "1=1" : (svalue === "-1" ? "md.status!='2'" : "md.status='" + svalue + "'")));
		// prodrequisitionData.refreshData();

	};
	// // 选择日期条件
	// Model.prototype.datetypeselectChange = function(event) {
	// var svalue = event.value;
	// if (svalue !== "") {
	// this.comp("begininput").set({
	// "disabled" : false
	// });
	// this.comp("endinput").set({
	// "disabled" : false
	// });
	// $(this.getElementByXid("daydropdown")).show();
	// } else {
	// this.comp("begininput").set({
	// "disabled" : true
	// });
	// this.comp("endinput").set({
	// "disabled" : true
	// });
	// this.comp("begininput").val("");
	// this.comp("endinput").val("");
	// $(this.getElementByXid("daydropdown")).hide();
	// }
	// };
	// 明细图片显示
	Model.prototype.grid2CellRender = function(event) {
		if (event.colName === "imgurl" && event.rowID !== "" && event.rowID !== undefined) {
			event.html = " <img src='" + mlfcommon.getItemImgUrl(event.colVal) + "' style='border-radius:50%;width:30px;height:30px;' onclick='{window.open(\"" + mlfcommon.getItemImgUrl(event.colVal)
					+ "\");}" + "' />";
		} else if ((",count,price,total,").indexOf("," + event.colName + ",") > -1) {
			if (event.colVal === 0) {
				event.html = "";
			} else {
				if (event.colVal < 0) {
					event.html = "<font style='color:red;'>" + mlfcommon.formatNumber(event.colVal) + "</font>";
				} else {
					event.html = mlfcommon.formatNumber(event.colVal);
				}
			}
		} else if (event.colName === "worksheetbillno" && event.row !== null) {
			if (event.colVal !== "" && event.colVal !== undefined) {
				event.html = "<div style='word-break:break-all;word-wrap:break-word; white-space:normal;'><a  onclick='justep.Util.getModel(this).showt_orderClick(event,\""
						+ event.row.val("worksheetid") + "\")'><font style='text-decoration:underline;'>" + event.colVal + "</font></a></div>";
			} else {
				event.html = "";
			}
		} else if (event.colName === "order_id" && event.colVal !== undefined && (event.colVal !== "" || event.row.val("soriginalbill") !== "")) {
			var tempstr = event.row.val("soriginalbill") !== "" ? " (" + event.row.val("soriginalbill") + ")" : "";
			event.html = "<div style='word-break:break-all;word-wrap:break-word; white-space:normal;'>" + event.colVal + tempstr + "</div>";
		} else if (event.colName === "pcodeid" && event.colVal !== undefined && event.colVal !== "") {
			var tempinfor = event.row.val("pcodeid") + " " + event.row.val("pitemname") + " " + event.row.val("psformat")
					+ (event.row.val("worksheetbatchno") === undefined ? "" : " " + event.row.val("worksheetbatchno"));
			event.html = "<div style='word-break:break-all;word-wrap:break-word; white-space:normal;'>" + tempinfor + "</div>";

		} else if (event.colName.indexOf('tounit') > -1 && event.row !== null) {
			var row = event.row;
			var type = event.colName.charAt(event.colName.length - 1);
			var countcolname = event.colName.substring(0, event.colName.indexOf("tounit"));
			if (this.unitsetdata["unitstate" + type] === 1 && row.val("unitstate" + type) === 1) {
				event.html = pdacommon.getConvertCount(row.val(countcolname), row.val("unit"), row.val("unitset" + type), this.unitsetdata.countbit);
			}
		} else {
			if (event.colVal === undefined) {
				event.html = "";
			} else {
				event.html = "<div style='word-break:break-all;word-wrap:break-word; white-space:normal;'>" + event.colVal + "</div>";
			}
		}

		if ((event.colName === 'price' || event.colName === 'total') && !this.showprice && event.colVal !== undefined) {
			event.html = "--";
		}

	};

	// 展开隐藏更多功能
	Model.prototype.showimportbtnClick = function(event) {
		var filter = this.comp('showimportbtn');
		var fconditiondata = this.comp("fconditiondata");
		var hasmore = fconditiondata.getValue("hasmore");
		if (hasmore === 1) {
			fconditiondata.setValue("hasmore", 0);
			filter.set({
				label : "更多",
				icon : "icon-android-add"
			});
		} else {
			fconditiondata.setValue("hasmore", 1);
			filter.set({
				label : "隐藏",
				icon : "icon-android-remove"
			});
		}
	};

	// 显示明细隐藏主表表
	Model.prototype.updivClick = function(event) {
		var countData = this.comp("countData");
		countData.setValue("secondStatus", 0);
		countData.setValue("firstStatus", 1);
	};
	// 显示主表与明细表
	Model.prototype.alldivClick = function(event) {
		var countData = this.comp("countData");
		countData.setValue("secondStatus", 1);
		this.comp("grid1").setHeight(350);
		countData.setValue("firstStatus", 1);
	};
	// 显示主表隐藏明细表
	Model.prototype.downdivClick = function(event) {
		var windowHieight = $(window).height();
		var countData = this.comp("countData");
		countData.setValue("secondStatus", 1);
		this.comp("grid1").setHeight(windowHieight - 350);
		countData.setValue("firstStatus", 0);
	};

	// 获取导出数据的条件
	Model.prototype.getCurCondition = function(type) {
		var fconditiondata = this.comp("fconditiondata");
		var custom = fconditiondata.getValue("customname");
		var house = fconditiondata.getValue("housename");

		var customersql = "";
		if (custom === "所有领用部门") {
			if (this.customerfilter !== "1=1") {
				customersql = "  (select 1 from customer where  customerid=pr.customerid and " + this.customerfilter + " limit 1)  ";
			}
		} else {
			if (fconditiondata.getValue("customid") === "") {
				customersql = "(c.customername like '%" + fconditiondata.getValue("customname") + "%' or c.customercode like '%" + fconditiondata.getValue("customname") + "%')";
				if (this.customerfilter !== "1=1") {
					customersql = customersql + " and (select 1 from customer where  customerid=pr.customerid and " + this.customerfilter + " limit 1)  ";
				}
			} else {
				customersql = "pr.customerid='" + fconditiondata.getValue("customid") + "'";
			}
		}
		var housesql = "";
		if (house === "所有仓库") {
			if (this.housefilter !== "1=1") {
				if (type === "total") {
					housesql = "  (select 1 from prodrequisitiondetail d where d.prodrequisitionid=pr.prodrequisitionid and d.houseid in (select houseid from storehouse where " + this.housefilter
							+ " )  limit 1)  ";
				} else {
					housesql = " (select 1 from storehouse where houseid=pr.houseid and " + this.housefilter + " limit 1) ";
				}
			}
		} else { // 2020-11-05 修改仓库是在明细表查询

			if (fconditiondata.getValue("houseid") === "") {
				if (type === "total") {
					housesql = " (select 1 from prodrequisitiondetail d,storehouse sh where d.prodrequisitionid=pr.prodrequisitionid and d.houseid=sh.houseid and  (sh.housename like '%"
							+ fconditiondata.getValue("housename") + "%' or sh.housecode like '%" + fconditiondata.getValue("housename") + "%')"
							+ (this.housefilter == "1=1" ? "" : "  and  (select 1 from storehouse where houseid=d.houseid and " + this.housefilter + " limit 1)  ") + ")  ";
				} else {
					housesql = " (sh.housename like '%" + fconditiondata.getValue("housename") + "%' or sh.housecode like '%" + fconditiondata.getValue("housename") + "%') ";
				}
			} else {
				if (type === "total") {
					housesql = " (select 1 from prodrequisitiondetail d where d.prodrequisitionid=pr.prodrequisitionid and d.houseid='" + fconditiondata.getValue("houseid") + "' limit 1)  ";
				} else {
					housesql = " pr.houseid='" + fconditiondata.getValue("houseid") + "' ";
				}
			}
		}

		var sql = "pr.companyid='" + this.companyid + "'   and " + (type === "total" ? " pr.bill_type='10' " : " pr.stype='101' ") + (customersql === "" ? "" : " and " + customersql)
				+ (housesql === "" ? "" : " and " + housesql);

		var statusselect = this.comp("statusselect").val();

		sql = sql + (statusselect === "" ? "" : (statusselect === "-1" ? " and pr.status!='2' " : " and pr.status='" + statusselect + "' "));

		var datesql = "";// 日期查询字符串
		var datetypeselect = this.comp("datetypeselect").val();
		if (datetypeselect !== "") {
			var begininput = this.comp("begininput").val();
			var endinput = this.comp("endinput").val();
			datesql = (begininput !== "" && begininput !== null ? "pr." + datetypeselect + ">='" + begininput + "'" : "");
			datesql = datesql + (datesql === "" ? "" : " and ") + (endinput !== "" && endinput !== null ? "pr." + datetypeselect + "<='" + endinput + " 23:59:59'" : "1=1");

			this.datastr = this.comp("datetypedata").getRowByID(datetypeselect).val("slabel") + "：" + (begininput !== "" && begininput !== null ? begininput + " " : "初始") + "至 "
					+ (endinput !== "" && endinput !== null ? endinput : justep.Date.toString(new Date(), "yyyy-MM-dd"));

			sql = sql + (datesql === "" ? "" : " and " + datesql);
		}

		// 查找商品信息
		var itemsql = "";
		var iteminput = pdacommon.transformSpecialInfo01(this.comp("iteminput").val().trim());
		if (iteminput !== "") {
			if (type === "total") {
				itemsql = " and  (select 1 from prodrequisitiondetail s,iteminfo i where  s.prodrequisitionid=pr.prodrequisitionid and s.itemid=i.itemid and s.companyid='" + this.companyid
						+ "'  and s.status='1' and (  i.codeid like '%" + iteminput + "%' or i.barcode like '%" + iteminput + "%' or i.itemname like '%" + iteminput + "%' or i.sformat like '%"
						+ iteminput + "%' or s.batchno like '%" + iteminput + "%' or s.remark like '%" + iteminput + "%') limit 1)  ";
			} else {
				itemsql = " and ( i.codeid like '%" + iteminput + "%' or i.barcode like '%" + iteminput + "%' or i.itemname like '%" + iteminput + "%' or i.sformat like '%" + iteminput
						+ "%' or pr.batchno like '%" + iteminput + "%' or pr.remark like '%" + iteminput + "%' )";
			}
			sql = sql + itemsql;
		}

		var billsql = "";
		var billinput = pdacommon.transformSpecialInfo01(this.comp("billinput").val().trim());
		if (billinput !== "") {

			if (type === "total") {
				billsql = " and  (select 1 from prodrequisitiondetail s,t_order tor left join iteminfo i on tor.itemid=i.itemid where  s.prodrequisitionid=pr.prodrequisitionid and "
						+ " s.worksheetid=tor.id and s.stype='101' and s.status='1'  and ( tor.billno like '%" + billinput + "%' or tor.originalbill like '%" + billinput
						+ "%' or tor.order_id like '%" + billinput + "%' or  i.codeid like '%" + billinput + "%' or i.itemname like '%" + billinput + "%' or i.sformat like '%" + billinput
						+ "%') limit 1)  ";

			} else {
				itemsql = " and ( pr.worksheetbillno like '%" + billinput + "%' or tor.originalbill like '%" + billinput + "%' or tor.order_id like '%" + billinput + "%' or ibs.itemname like '%"
						+ iteminput + "%' or ibs.codeid like '%" + billinput + "%' or ibs.sformat like '%" + billinput + "%')";
			}
			sql = sql + itemsql;
		}

		var searchcontent = pdacommon.transformSpecialInfo01(this.comp("searchcontent").val().trim());
		if (searchcontent !== "") {
			if (type === "total") {
				sql = sql + " and (pr.orderid like '%" + searchcontent + "%' or s.staffname like '%" + searchcontent + "%' or pr.remark like '%" + searchcontent + "%' or pr.originalbill like '%"
						+ searchcontent + "%' or pr.create_by like '%" + searchcontent + "%' or pr.update_by like '%" + searchcontent + "%')";

			} else {
				sql = sql + " and (s.staffname like '%" + searchcontent + "%' or (select 1 from prodrequisition sm where sm.prodrequisitionid=pr.prodrequisitionid and ( sm.orderid like '%"
						+ searchcontent + "%' or sm.remark like '%" + searchcontent + "%' or sm.originalbill like '%" + searchcontent + "%' or sm.create_by like '%" + searchcontent
						+ "%' or sm.update_by like '%" + searchcontent + "%') limit 1))";

			}

		}

		var arr = this.comp('grid1').getCheckeds();
		if (arr.length !== 0) {
			var temp = "";
			for (var i = 0; i < arr.length; i++) {
				temp = temp + (temp === "" ? "" : ",") + "'" + arr[i] + "'";
			}

			sql = sql + " and pr.prodrequisitionid in (" + temp + ")";
		}

		if (this.curdtype1 !== "") {// 1-公共规则 数据过滤 or 结构
			sql = sql + " and (" + this.curdtype1 + ")";
		}
		return sql;
	};

	// 导出入库汇总
	Model.prototype.toexceltotalClick = function(event) {

		var begininput = this.comp("begininput").val();
		var endinput = this.comp("endinput").val();
		if (begininput === "" || begininput === null || begininput === undefined) {
			justep.Util.hint("请选择开始日期", {
				type : "danger",
				position : "middle"
			});
			return;
		}

		// if (endinput === "" || endinput === null || endinput === undefined) {
		// justep.Util.hint("请选择结束日期", {
		// type : "danger",
		// position : "middle"
		// });
		// return;
		// }

		event.source.set({
			disabled : true
		});
		setTimeout(function() {
			event.source.set({
				disabled : false
			});
		}, 4000);
		var arr = this.comp('grid1').getCheckeds();
		if (arr.length === 0) {

			var me = this;
			justep.Util.confirm("确定要导出符合条件的【全部】生产领用汇总数据吗？", function() {
				me.toexceltotalordetail("total");
			});
		} else {
			this.toexceltotalordetail("total");
		}

	};
	// 导出入库明细
	Model.prototype.toexceldetailClick = function(event) {
		event.source.set({
			disabled : true
		});
		setTimeout(function() {
			event.source.set({
				disabled : false
			});
		}, 4000);
		var arr = this.comp('grid1').getCheckeds();
		if (arr.length === 0) {

			var me = this;
			justep.Util.confirm("确定要导出符合条件的【全部】生产领用明细数据吗？", function() {
				me.toexceltotalordetail("detail");
			});
		} else {
			this.toexceltotalordetail("detail");
		}
	};
	// 根据类型type导出汇总或明细数据
	Model.prototype.toexceltotalordetail = function(type) {
		var filename = "";
		var url = "";
		var form = $('#dynamicFormoutexcel');
		if (type === "total") {
			filename = "生产领用单汇总列表";
			url = "/baas/erpscan/erp/tootherexcel/getprodrequisitiontotalexcel";

		} else if (type === "detail") {
			filename = "生产领用单明细列表";
			url = "/baas/erpscan/erp/tootherexcel/getprodrequisitiondetailexcel";

		}

		if (form.length <= 0) {
			form = $("<form>");
			form.attr('id', 'dynamicFormoutexcel');
			form.attr('style', 'display:none');
			form.attr('target', '');
			form.attr('method', 'post');

			$('body').append(form);

			var my_input = $('<input type="hidden" name="filname" />');
			my_input.attr('value', filename);
			$("#dynamicFormoutexcel").append(my_input);

			var sql = this.getCurCondition(type);
			if (sql !== "") {
				my_input = $('<input type="hidden" name="condition" />');
				my_input.attr('value', sql);
				$("#dynamicFormoutexcel").append(my_input);

				// 2020-12-19
				var unitsetdatajson = JSON.stringify(this.unitsetdata);
				my_input = $('<input type="hidden" name="unitsetdata" />');
				my_input.attr('value', unitsetdatajson);
				$("#dynamicFormoutexcel").append(my_input);
			}

			my_input = $('<input type="hidden" name="datastr" />');
			my_input.attr('value', this.datastr);
			$("#dynamicFormoutexcel").append(my_input);

			my_input = $('<input type="hidden" name="companyid" />');
			my_input.attr('value', this.companyid);
			$("#dynamicFormoutexcel").append(my_input);

			my_input = $('<input type="hidden" name="companyname" />');
			my_input.attr('value', this.companyname);
			$("#dynamicFormoutexcel").append(my_input);

			my_input = $('<input type="hidden" name="loginuserid" />');
			my_input.attr('value', this.loginuserid);
			$("#dynamicFormoutexcel").append(my_input);

			my_input = $('<input type="hidden" name="loginUser" />');
			my_input.attr('value', this.loginUser);
			$("#dynamicFormoutexcel").append(my_input);

			my_input = $('<input type="hidden" name="showprice" />');
			my_input.attr('value', this.showprice);
			$("#dynamicFormoutexcel").append(my_input);

		}

		form = $('#dynamicFormoutexcel');
		form.attr('action', url);

		form.submit();
		form.remove();
	};

	// 打开单据草稿
	Model.prototype.todraftbillBtnClick = function(event) {
		this.getParent().closePage("draftbillmanage");
		localStorage.setItem("draftbilltype", "10");
		// 打开新的生产领用单
		this.getParent().showPage("draftbillmanage");
	};
	// 刷新新数据源
	Model.prototype.refreshdatabtnClick = function(event) {
		var storehouseData = this.comp("storehouseData");
		storehouseData.refreshData();
		var newstorehouseData = this.comp("newstorehouseData");
		newstorehouseData.clear();
		newstorehouseData.loadData(storehouseData.toJson()); // 加载调入数据源
		justep.Util.hint("已刷新【查询仓库】的数据", {
			"type" : "success",
			"position" : "middle"
		});

	};

	// 数据刷新显示加载图
	Model.prototype.prodrequisitionDataBeforeRefresh = function(event) {
		this.getParentModel().showloading();// 显示加载图
	};

	// 主表刷新，副表加载数据
	Model.prototype.prodrequisitionDataAfterRefresh = function(event) {
		this.getParentModel().hideloading();// 隐藏加载图
		var prodrequisitiondetailData = this.comp("prodrequisitiondetailData");
		prodrequisitiondetailData.clear();
		event.source.first();
		// var grid2 = this.comp("grid2");
		// grid2.hideCol("property1");
		// grid2.hideCol("property2");
		// grid2.hideCol("property3");
		// grid2.hideCol("property4");
		// grid2.hideCol("property5");
		// var row = event.source.getCurrentRow();
		// if (row !== null) {
		// prodrequisitiondetailData.filters.setVar("companyid",
		// this.companyid);
		// prodrequisitiondetailData.filters.setVar("prodrequisitionid",
		// row.val("prodrequisitionid"));
		// prodrequisitiondetailData.refreshData();
		// var property = row.val("iproperty");
		// if (property !== undefined) {
		// var propertyarr = property.split(";");
		// for (var i = 0; i < propertyarr.length; i++) {
		// var arr = propertyarr[i].split(",");
		// if (arr[0] !== "") {
		// grid2.showCol(arr[0]);
		// grid2.setLabel(arr[0], arr[1]);
		// }
		// }
		// }
		// }
		if (event.source.count() === 0) {
			justep.Util.hint("没有符合条件记录", {
				type : "danger",
				position : "middle"
			});
		}
	};

	// 数据刷新显示加载图
	Model.prototype.prodrequisitiondetailDataBeforeRefresh = function(event) {
		this.getParentModel().showloading();// 显示加载图
	};
	// 数据刷新隐藏加载图
	Model.prototype.prodrequisitiondetailDataAfterRefresh = function(event) {
		this.getParentModel().hideloading();// 隐藏加载图
	};

	Model.prototype.controlGroup1Keypress = function(event) {
		if (event.keyCode == 13) {
			var arr = document.querySelectorAll('input');
			for (var i = 0; i < arr.length; i++) {
				arr[i].blur();
			}
			this.findBtnClick();
		}
	};

	// 导入数据
	Model.prototype.tolistClick = function(event) {
		event.source.set({
			disabled : true
		});
		setTimeout(function() {
			event.source.set({
				disabled : false
			});
		}, 4000);

		var fileload = document.getElementById("prload_xls");
		if (fileload === null) {
			$(this.getElementByXid('tolist')).after('<input type="file" id="prload_xls" name="file" style="display:none" onchange="justep.Util.getModel(this).exceltolist()" />');
		}
		document.getElementById("prload_xls").value = "";
		document.getElementById("prload_xls").click();

	};

	Model.prototype.exceltolist = function(event) {

		var filePath = document.getElementById("prload_xls").value;
		var fileExt = filePath.substring(filePath.lastIndexOf(".")).toLowerCase();
		if (!fileExt.match(/.xls/i)) {
			this.comp("message").show({
				title : "信息提示",
				message : "当前上传文件为:" + filePath + ",文件类型错误，请上传后缀为.xls的excel文件。"
			});
		} else {
			this.getParentModel().showloading("数据正在导入处理中...");// 显示加载图
			var myform = new FormData();
			myform.append("companyid", this.companyid);
			myform.append("create_id", this.loginuserid);
			myform.append("create_by", this.loginUser);
			myform.append("moneybit", this.moneybit);
			myform.append("pricebit", this.pricebit);
			myform.append('file', document.getElementById("prload_xls").files[0]);

			var me = this;

			$.ajax({
				url : "/baas/erpscan/erp/toimportexcel/prodrequisitionexceltolist",
				type : "POST",
				data : myform,
				contentType : false,
				processData : false,
				success : function(data) {
					me.getParentModel().hideloading();// 隐藏加载图
					var msdata = JSON.parse(data);
					me.findBtnClick();
					me.comp("message").show({
						title : "信息提示",
						message : msdata.message
					});

				}
			});

		}

	};

	Model.prototype.infoexampleClick = function(event) {
		event.source.set({
			disabled : true
		});
		setTimeout(function() {
			event.source.set({
				disabled : false
			});
		}, 4000);

		if (justep.Browser.isPC) {
			   
			  var url = require.toUrl("./../../file/生产领用导入模板.xls");
				var form = $('#dynamicFormoutexcel');

				form = $("<form>");
				form.attr('id', 'dynamicFormoutexcel');
				form.attr('style', 'display:none');
				form.attr('target', '');
				form.attr('method', 'GET');

				$('body').append(form);

				form = $('#dynamicFormoutexcel');
				form.attr('action', url);

				form.submit();
				form.remove();

		} else {
			justep.Util.hint("请在电脑端进行模板导出操作。", {
				type : "warning",
				"position" : "middle"
			});
		}
	};

	return Model;
});