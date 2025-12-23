define(function(require) {
	var $ = require("jquery");
	var justep = require("$UI/system/lib/justep");
	var mlfcommon = require("./../../js/mlfcommon");
	var pdacommon = require("./../../js/pdacommon");

	var Model = function() {
		this.callParent();
		this.furlname = "";// 父页名
		this.countbit = 0;
		this.moneybit = 2;
	};

	Model.prototype.cutZero = function(value) {
		return pdacommon.cutZero(value);
	};

	Model.prototype.modelLoad = function(event) {
		var parent = this.getParentModel();
		if (parent === undefined) {
			window.open(require.toUrl("./../../index.w"), "_self");
		}

		var companydata = this.getParentModel().comp("companyData");
		this.moneybit = companydata.getValue("moneybit");
		this.countbit = companydata.getValue("countbit");

		// 2020-12-19 获取辅助单位设置 ，约定-3个辅助运算列的列名需为：原列名+tounit1、2、3的形式
		this.companyData = companydata;
		var option = [ {
			grid : "grid2", // grid表格xid
			needconvertcol : [ "count" ],// 加入需要换算的数量列的列名
			needconvertcolname : [ "" ], // 加入需要换算的数量列的名称
		} ];
		this.unitsetdata = pdacommon.createUnitOption(this, this.companyData, option);
		// 获取辅助单位设置 ---end

		var permissiondata = this.getParentModel().comp("permissiondata");
		var crowdata = permissiondata.find([ "fvalue" ], [ "schedulepickdata:print" ]);
		if (crowdata.length > 0) {
			if (justep.Browser.isPC)
				$(this.getElementByXid("printbtn")).show();
		}
		
		crowdata = permissiondata.find([ "fvalue" ], [ "schedulepickdata:read" ]);
		if (crowdata.length === 0) {// 查看功能
			justep.Util.hint("没有权限操作此功能！", {
				"type" : "warning",
				"position" : "middle"
			});
			$(this.getElementByXid("window")).hide();
		}
	};

	Model.prototype.modelParamsReceive = function(event) {
		var schedule_pick_detail = localStorage.getItem("schedule_pick_detail");
		this.showprint = false;
		if (schedule_pick_detail !== null) {
			var params = JSON.parse(schedule_pick_detail);
			var schedulePickData = this.comp("schedulePickData");
			var schedulePickDetailData = this.comp("schedulePickDetailData");

			this.furlname = params.furlname;
			this.mainid = params.mainid;
			this.modifybill = params.modifybill;

			schedulePickData.clear();
			schedulePickDetailData.clear();

			if (this.furlname === "showdetailbyorderid") {// 通过点订单编号orderid查看记录
				schedulePickData.refreshData();

				schedulePickDetailData.refreshData();

				this.showprint = (params.showprint !== null && params.showprint !== undefined ? params.showprint : false);

			} else { // 操作功能查看详情

				schedulePickData.refreshData();

				schedulePickDetailData.refreshData();
			}

			var status = schedulePickData.getValue("status");
			if (status === "0") {
				$(this.getElementByXid("statusspan")).html("<font style='color:blue;'>【未记帐】</font>");
				$(this.getElementByXid("printbtn")).hide();
			} else if (status === "1") {
				$(this.getElementByXid("statusspan")).html("<font style='color:green;'>【已记帐】</font>");
			} else if (status === "2") {
				$(this.getElementByXid("statusspan")).html("<font style='color:red;'>【已作废】</font>");
				$(this.getElementByXid("printbtn")).hide();
			}

			console.log(this.modifybill , status !== "0");
			if (this.modifybill && status !== "0") {
				$(this.getElementByXid("billbtn")).show();
				$(this.getElementByXid("mainremarkbtn")).show();
			}

			var grid2 = this.comp("grid2");
			grid2.refresh();

			var property = schedulePickData.getValue("iproperty");
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

			pdacommon.showdatagridrow(schedulePickDetailData);

			if (this.showprint) {
				this.printbtnClick(event);
			}

		} else {
			this.getParent().shellImpl.closePage();
		}
	};

	Model.prototype.grid2CellRender = function(event) {
		if (event.colName === "imgurl" && event.row !== null && event.row.val("itemid") !== "") {
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
		} else {
			if (event.colVal === undefined) {
				event.html = "";
			} else {
				event.html = "<div style='word-break:break-all;word-wrap:break-word; white-space:normal;'>" + event.colVal + "</div>";
			}
		}
		if (event.colName === 'total' && !this.showprice && event.colVal !== undefined) {
			event.html = "--";
		}
		if (event.colName === 'price' && !this.showprice && event.colVal !== undefined) {
			event.html = "--";
		}

		// 新增2022-11-01
		if (event.colName === "remark" && event.row !== null && event.row.val("itemid") !== "") {// 修改出库状态
			event.html = "<div style='word-break:break-all;word-wrap:break-word; white-space:normal;'><button style='display:"
					+ ((this.modifybill === true && event.row.val("status") === "1") ? "inline" : "none")
					+ ";' class='btn btn-link btn-sm linkbtn'  onclick='justep.Util.getModel(this).modifybillBtnClick(event,\"" + event.rowID + "\")'><i  class='icon-edit'/> </button>" + event.colVal
					+ "</div>";
		}

		// 2020-12-19
		if (event.colName.indexOf('tounit') > -1 && event.row !== null) {
			var row = event.row;
			var type = event.colName.charAt(event.colName.length - 1);
			var countcolname = event.colName.substring(0, event.colName.indexOf("tounit"));
			if (this.unitsetdata["unitstate" + type] === 1 && row.val("unitstate" + type) === 1) {
				event.html = pdacommon.getConvertCount(row.val(countcolname), row.val("unit"), row.val("unitset" + type), this.unitsetdata.countbit);
			}
		}
	};

	Model.prototype.mainremarkbtnClick = function(event) {
		var rowdata = this.comp("schedulePickData").getCurrentRow();
		this.getParent().comp("modifyDialog").open({
			"data" : {
				"stype" : 1,
				"mainid" : rowdata.val("schedule_pick_id"),
				"tablename" : "schedule_pick",
				"title" : "修改备注：",
				"remark" : rowdata.val("remark"),
				"orderid" : rowdata.val("orderid"),
				"logid" : 653,
				"returnfunction" : "schedule_pick_modifybillrefresh"
			},
			"src" : require.toUrl("$UI/erpscan/pdamanage/modifyremark.w"),
			"status" : justep.Browser.isPC ? "normal" : "maximize"
		});
	};

	Model.prototype.billbtnClick = function(event) {
		var rowdata = this.comp("schedulePickData").getCurrentRow();
		this.getParent().comp("modifyDialog").open({
			"data" : {
				"stype" : 0,
				"mainid" : rowdata.val("schedule_pick_id"),
				"tablename" : "schedule_pick",
				"title" : "修改原单号：",
				"remark" : rowdata.val("originalbill"),
				"orderid" : rowdata.val("orderid"),
				"logid" : 653,
				"returnfunction" : "schedule_pick_modifybillrefresh"
			},
			"src" : require.toUrl("$UI/erpscan/pdamanage/modifyremark.w"),
			"status" : justep.Browser.isPC ? "normal" : "maximize"
		});
	};

	Model.prototype.modifybillBtnClick = function(event, rowid) {
		var rowdata = this.comp("schedulePickDetailData").getRowByID(rowid);
		this.getParent().comp("modifyDialog").open({
			"data" : {
				"stype" : 2,
				"mainid" : rowdata.val("schedule_pick_id"),
				"detailid" : rowid,
				"tablename" : "schedule_pick",
				"title" : "修改第" + rowdata.val("goods_number") + "行的备注：",
				"remark" : rowdata.val("remark"),
				"orderid" : rowdata.val("orderid"),
				"logid" : 653,
				"returnfunction" : "schedule_pick_modifybillrefresh"
			},
			"src" : require.toUrl("$UI/erpscan/pdamanage/modifyremark.w"),
			"status" : justep.Browser.isPC ? "normal" : "maximize"
		});
	};

	Model.prototype.modifybillrefreshClick = function(event) {
		var temparr = localStorage.getItem("schedule_pick_modifybillrefresh");
		var fpdata = JSON.parse(temparr);
		if (fpdata.stype === 0) {
			var mainrowdata = this.comp("schedulePickData").getCurrentRow();
			mainrowdata.val("originalbill", fpdata.remark);
		} else if (fpdata.stype === 1) {
			var mainrowdata = this.comp("schedulePickData").getCurrentRow();
			mainrowdata.val("remark", fpdata.remark);
		} else if (fpdata.stype === 2) {
			var rowdata = this.comp("schedulePickDetailData").getRowByID(fpdata.detailid);
			rowdata.val("remark", fpdata.remark);
		}

		$(document.getElementById("schedulePickRefreshBtn")).click();
	};

	Model.prototype.grid2Reload = function(event) {
		var data = this.comp("schedulePickDetailData");
		var count = this.cutZero((data.sum("count")).toFixed(this.countbit));
		var total = this.cutZero((data.sum("total")).toFixed(this.moneybit));
		event.source.setFooterData({
			itemname : '合计',
			count : count,
			total : total
		});
	};

	Model.prototype.printbtnClick = function(event) {
		this.comp("pringDialog").open({
			"data" : {
				"schedule_pick_id" : this.mainid,
				"allcount" : this.comp("schedulePickDetailData").count(),
				"dialogid" : this.getIDByXID("pringDialog")
			},
			"src" : require.toUrl("./schedulePickPrint.w"),
			"status" : justep.Browser.isPC ? "normal" : "maximize"
		});
	};

	Model.prototype.schedulePickDataBeforeRefresh = function(event){
		var sourcedata = this.comp("schedulePickData");
		sourcedata.clear();
		justep.Baas.sendRequest({
			"url" : "/erpscan/base/schedulePick",
			"action" : "querySchedulePickData",
			"async" : false,
			"params" : {
				"mainid" : this.mainid,
			},
			"success" : function(data) {
				if (data.message === "") {
					sourcedata.loadData(data.table);
					sourcedata.first();
				} else {
					justep.Util.hint(data.message, {
						"type" : "danger",
						"position" : "middle"
					});
				}
			}
		});
	};

	Model.prototype.schedulePickDetailDataBeforeRefresh = function(event){
		var sourcedata = this.comp("schedulePickDetailData");
		var grid = this.comp("grid2");
		justep.Baas.sendRequest({
			"url" : "/erpscan/base/schedulePick",
			"action" : "querySchedulePickDetailData",
			"async" : false,
			"params" : {
				"mainid" : this.mainid,
				"orderBys" : sourcedata.getOrderBy(),
			},
			"success" : function(data) {
				if (data.message === "") {
					sourcedata.loadData(data.table);
					sourcedata.first();

					grid.refresh();
				} else {
					justep.Util.hint(data.message, {
						"type" : "danger",
						"position" : "middle"
					});
				}
			}
		});
	};

	return Model;
});