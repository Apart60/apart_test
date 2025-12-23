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
		var crowdata = permissiondata.find([ "fvalue" ], [ "prodrequisitiondata:print" ]);
		if (crowdata.length > 0) {// 增加
			if (justep.Browser.isPC)
				$(this.getElementByXid("printbtn")).show();
		}

		crowdata = permissiondata.find([ "fvalue" ], [ "prodrequisitiondata:showprice" ]);
		if (crowdata.length > 0)
			this.showprice = true;// 单价显示

		// 新增2022-11-01
		crowdata = permissiondata.find([ "fvalue" ], [ "allroles:modifybill" ]);
		if (crowdata.length > 0) {
			this.modifybill = true;
		} else {
			this.modifybill = false;
		}
	};

	Model.prototype.modelParamsReceive = function(event) {

		var prodrequisitiondetail = localStorage.getItem("prodrequisitiondetail");
		this.showprint = false;
		if (prodrequisitiondetail !== null) {
			var params = JSON.parse(prodrequisitiondetail);
			var prodrequisitionData = this.comp("prodrequisitionData");
			var prodrequisitiondetailData = this.comp("prodrequisitiondetailData");

			this.furlname = params.furlname;
			this.mainid = params.mainid;

			prodrequisitionData.clear();
			prodrequisitiondetailData.clear();

				var mainid = params.mainid;
				prodrequisitionData.setFilter("filter1", "prodrequisitionid='" + mainid + "'");
				prodrequisitionData.refreshData();
				prodrequisitionData.first();

				// prodrequisitiondetailData.setFilter("filter1",
				// "prodrequisitionid='" + mainid + "'");
				prodrequisitiondetailData.filters.setVar("prodrequisitionid", mainid);
				prodrequisitiondetailData.refreshData();
				prodrequisitiondetailData.first();

				this.showprint = (params.showprint !== null && params.showprint !== undefined ? params.showprint : false);

			var status = prodrequisitionData.getValue("status");
			if (status === "0") {
				$(this.getElementByXid("statusspan")).html("<font style='color:blue;'>【未记帐】</font>");
				//$(this.getElementByXid("printbtn")).hide();
			} else if (status === "1") {
				$(this.getElementByXid("statusspan")).html("<font style='color:green;'>【已记帐】</font>");
			} else if (status === "2") {
				$(this.getElementByXid("statusspan")).html("<font style='color:red;'>【已作废】</font>");
				$(this.getElementByXid("printbtn")).hide();
			} else if (status === "3") {
				$(this.getElementByXid("statusspan")).html("<font style='color:orange;'>【待出库】</font>");
				//$(this.getElementByXid("printbtn")).hide();
			}

			// 新增2022-11-01
			if (this.modifybill && status !== "0") {
				$(this.getElementByXid("billbtn")).show();
				$(this.getElementByXid("mainremarkbtn")).show();
			}

			var grid2 = this.comp("grid2");
			grid2.refresh();

			var property = prodrequisitionData.getValue("iproperty");
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
			//pdacommon.showdatagridrow(prodrequisitiondetailData);

			var scount = prodrequisitiondetailData.count(function(ev) {
				return ev.source.getValue("worksheetid") !== "";
			});

			if (scount === 0 || prodrequisitionData.getValue("worksheetid") !== "") {
				grid2.hideCol("worksheetbillno");
				grid2.hideCol("order_id");
				grid2.hideCol("pcodeid");
			}
			
			  pdacommon.gridresize(grid2, 180);

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
		}  else if (event.colName === "remark" && event.row !== null && event.row.val("itemid") !== "") {// 修改出库状态
			event.html = "<div style='word-break:break-all;word-wrap:break-word; white-space:normal;'><button style='display:"
					+ ((this.modifybill === true && event.row.val("status") === "1") ? "inline" : "none")
					+ ";' class='btn btn-link btn-sm linkbtn'  onclick='justep.Util.getModel(this).modifybillBtnClick(event,\"" + event.rowID + "\")'><i  class='icon-edit'/> </button>" + event.colVal
					+ "</div>";
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

	// 新增2022-11-01
	Model.prototype.mainremarkbtnClick = function(event) {
		var rowdata = this.comp("prodrequisitionData").getCurrentRow();
		this.getParent().comp("modifyDialog").open({
			"data" : {
				"stype" : 1,
				"mainid" : rowdata.val("prodrequisitionid"),
				"tablename" : "prodrequisition",
				"title" : "修改备注：",
				"remark" : rowdata.val("remark"),
				"orderid" : rowdata.val("orderid"),
				"logid" : 23,
				"returnfunction" : "prodrequisition_modifybillrefresh"
			},
			"src" : require.toUrl("$UI/erpscan/pdamanage/modifyremark.w"),
			"status" : justep.Browser.isPC ? "normal" : "maximize"
		});
	};

	// 新增2022-11-01
	Model.prototype.billbtnClick = function(event) {
		var rowdata = this.comp("prodrequisitionData").getCurrentRow();
		this.getParent().comp("modifyDialog").open({
			"data" : {
				"stype" : 0,
				"mainid" : rowdata.val("prodrequisitionid"),
				"tablename" : "prodrequisition",
				"title" : "修改原单号：",
				"remark" : rowdata.val("originalbill"),
				"orderid" : rowdata.val("orderid"),
				"logid" : 23,
				"returnfunction" : "prodrequisition_modifybillrefresh"
			},
			"src" : require.toUrl("$UI/erpscan/pdamanage/modifyremark.w"),
			"status" : justep.Browser.isPC ? "normal" : "maximize"
		});
	};

	// 新增2022-11-01
	Model.prototype.modifybillBtnClick = function(event, rowid) {
		var rowdata = this.comp("prodrequisitiondetailData").getRowByID(rowid);
		this.getParent().comp("modifyDialog").open({
			"data" : {
				"stype" : 2,
				"mainid" : rowdata.val("prodrequisitionid"),
				"detailid" : rowid,
				"tablename" : "prodrequisition",
				"title" : "修改第" + rowdata.val("goods_number") + "行的备注：",
				"remark" : rowdata.val("remark"),
				"orderid" : rowdata.val("orderid"),
				"logid" : 23,
				"returnfunction" : "prodrequisition_modifybillrefresh"
			},
			"src" : require.toUrl("$UI/erpscan/pdamanage/modifyremark.w"),
			"status" : justep.Browser.isPC ? "normal" : "maximize"
		});
	};

	// 新增2022-11-01
	Model.prototype.modifybillrefreshClick = function(event) {
		var temparr = localStorage.getItem("prodrequisition_modifybillrefresh");
		var fpdata = JSON.parse(temparr);
		if (fpdata.stype === 0) {
			var mainrowdata = this.comp("prodrequisitionData").getCurrentRow();
			mainrowdata.val("originalbill", fpdata.remark);
		} else if (fpdata.stype === 1) {
			var mainrowdata = this.comp("prodrequisitionData").getCurrentRow();
			mainrowdata.val("remark", fpdata.remark);
		} else if (fpdata.stype === 2) {
			var rowdata = this.comp("prodrequisitiondetailData").getRowByID(fpdata.detailid);
			rowdata.val("remark", fpdata.remark);
		}

		$(document.getElementById("prodrequisitionrefreshBtn")).click();
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

	Model.prototype.grid2Reload = function(event) {
		var data = this.comp("prodrequisitiondetailData");
		var total = this.cutZero((data.sum("count")).toFixed(this.countbit));
		event.source.setFooterData({
			itemname : '合计',
			count : total
		});
	};

	// 打印
	Model.prototype.printbtnClick = function(event) {

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
				"mainid" : this.mainid,
				"allcount" : this.comp("prodrequisitiondetailData").count(),
				"showbillno" : showbillno,
				"dialogid" : this.getIDByXID("pringDialog")
			},
			"src" : require.toUrl("./prodrequisitionprint.w"),
			"status" : justep.Browser.isPC ? "normal" : "maximize"
		});
	};

	return Model;
});