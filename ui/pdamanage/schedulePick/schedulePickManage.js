define(function(require){
	var $ = require("jquery");
	var justep = require("$UI/system/lib/justep");
	var mlfcommon = require("./../../js/mlfcommon");
	var pdacommon = require("./../../js/pdacommon");

	var Model = function(){
		this.callParent();
	};

	Model.prototype.modelLoad = function(event){
		var parent = this.getParentModel();
		if (parent === undefined) {
			window.open(require.toUrl("./../../index.w"), "_self");
		}

		var loginUserData = this.getParentModel().comp("userinfoData");
		this.loginuserid = loginUserData.val("userid");
		this.loginUser = loginUserData.val('realname') + "[" + loginUserData.val('username') + "]";
		this.companyid = loginUserData.val("companyid");

		var companyData = this.getParentModel().comp("companyData");
		this.companyname = companyData.val("companyname");
		this.moneybit = companyData.getValue("moneybit");
		this.countbit = companyData.getValue("countbit");

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
		var crowdata = permissiondata.find([ "fvalue" ], [ "schedulepickdata:new" ]);
		if (crowdata.length > 0) {// 增加
			$(this.getElementByXid("add")).show();
			if (justep.Browser.isPC) {
				$(this.getElementByXid("itembeginexample")).show();
			}
			this.edit = true;
		}

		crowdata = permissiondata.find([ "fvalue" ], [ "schedulepickdata:read" ]);
		if (crowdata.length === 0) {// 查看功能
			justep.Util.hint("没有权限操作此功能！", {
				"type" : "warning",
				"position" : "middle"
			});
			$(this.getElementByXid("window")).hide();
		}

		crowdata = permissiondata.find([ "fvalue" ], [ "schedulepickdata:exporttotal" ]);
		if (crowdata.length > 0) {// 导出汇总
			if (justep.Browser.isPC)
				$(this.getElementByXid("toexceltotal")).show();
		}

		crowdata = permissiondata.find([ "fvalue" ], [ "schedulepickdata:exportdetail" ]);
		if (crowdata.length > 0) {// 导出明细
			if (justep.Browser.isPC)
				$(this.getElementByXid("toexceldetail")).show();
		}

		crowdata = permissiondata.find([ "fvalue" ], [ "schedulepickdata:detail" ]);
		if (crowdata.length === 0)
			this.detail = false;// 详情

		crowdata = permissiondata.find([ "fvalue" ], [ "schedulepickdata:copynew" ]);
		if (crowdata.length === 0)
			this.copynew = false;// 复制

		crowdata = permissiondata.find([ "fvalue" ], [ "schedulepickdata:status" ]);
		if (crowdata.length === 0)
			this.status = false;// 正常作废

		crowdata = permissiondata.find([ "fvalue" ], [ "schedulepickdata:print" ]);
		if (crowdata.length > 0) {// 增加
			if (justep.Browser.isPC)
				this.canprint = true;// 打印
		}

		// 数据规则检验 begin
		var dataruledata = this.getParent().comp("dataruledata");
		var rulesrow = dataruledata.find([ "pfvalue" ], [ "schedulepickdata:read" ]);
		// dtype 1-公共规则 2-私有规则 3-不能查看数据列
		if (rulesrow !== null && rulesrow.length > 0) {
			var data = pdacommon.getDataRules(rulesrow, loginUserData, companyData, "sp.create_id");
			this.dtype1 = data.dtype1;// 1-公共规则
			data = pdacommon.getDataRules(rulesrow, loginUserData, companyData, "create_id");
			this.curdtype1 = data.curdtype1;// 导出公共规则
		}
		// 数据规则检验 end
	};

	Model.prototype.modelParamsReceive = function(event){
		this.housefilter = localStorage.getItem(this.loginuserid + "hfilter");
		this.customerfilter = localStorage.getItem(this.loginuserid + "cfilter");

		var schedulePickData = this.comp("schedulePickData");
		var storehouseData = this.comp("storehouseData");
		var customerData = this.comp("customerData");

		schedulePickData.clear();
		storehouseData.clear();
		customerData.clear();

		this.clearBtnClick();// 清除所有还原默认条件

		mlfcommon.getHouseData(storehouseData, this.companyid, this.housefilter, this.loginuserid, "", "", "");

		mlfcommon.getCustomerData(customerData, this.companyid, this.customerfilter, this.loginuserid, 4, "", "", "");

		// schedulePickData.refreshData();
		// schedulePickData.first();

		pdacommon.initdayselect2(this, this.comp("groupmenu"), this.comp("begininput"), this.comp("endinput"), null);// 加载日期选择框

		// house初始化变更操作
		var me = this;
		$($(this.getElementByXid("housegridSelect")).find("input")[0]).click(function() {
			var house = $(this).val().trim();
			$(this).val("");
			me.comp("fconditiondata").setValue("housename", house);
			me.comp("fconditiondata").setValue("houseid", "");

		});
		$($(this.getElementByXid("housegridSelect")).find("input")[0]).change(function() {
			var house = $(this).val().trim();
			me.comp("fconditiondata").setValue("housename", house === "" ? "所有仓库" : house);
			if (house === "")
				me.comp("fconditiondata").setValue("houseid", "");
		});

		$($(this.getElementByXid("customgridSelect")).find("input")[0]).click(function() {
			var custom = $(this).val().trim();
			$(this).val("");
			me.comp("fconditiondata").setValue("customname", custom);

		});
		$($(this.getElementByXid("customgridSelect")).find("input")[0]).change(function() {
			var custom = $(this).val().trim();
			me.comp("fconditiondata").setValue("customname", custom === "" ? "所有单位部门" : custom);
		});

		$(this.getElementByXid("searchcontent")).click(function(params) {
			$(this).select();
		});

		$(this.getElementByXid("iteminput")).click(function(params) {
			$(this).select();
		});

		$(this.getElementByXid("iteminput")).focus();

		// if (justep.Browser.isPC) this.downdivClick();
		this.findBtnClick();
	};

	// 只清除查询条件
	Model.prototype.clearBtnClick = function() {
		this.comp("searchcontent").val("");
		this.comp("iteminput").val("");
		this.comp("statusselect").val("-1");

		this.comp("datetypeselect").val("operate_time");
		var curdate = justep.Date.toString(new Date(), justep.Date.STANDART_FORMAT_SHOT);
		var startdate = justep.Date.toString(justep.Date.decrease(justep.Date.increase(new Date(), 1, 'd'), 1, 'm'), justep.Date.STANDART_FORMAT_SHOT);
		this.comp("begininput").val(startdate);
		this.comp("endinput").val(curdate);

		var fconditiondata = this.comp("fconditiondata");
		fconditiondata.setValue("houseid", "");
		fconditiondata.setValue("housename", "所有仓库");
		fconditiondata.setValue("customid", "");
		fconditiondata.setValue("customname", "所有单位部门");

		this.findBtnClick();
	};

	// 查找条件
	Model.prototype.findBtnClick = function(event) {
		this.comp("schedulePickDetailData").clear();
		var schedulePickData = this.comp("schedulePickData");
		schedulePickData.clear();
		schedulePickData.setOffset(0);
		if (this.getParamsdata(0) === 1) {
			schedulePickData.refreshData();
			schedulePickData.first();
		}
	};
	// 查看详情
	Model.prototype.detailBtnClick = function(event, rowid) {
		this.getParent().closePage(this.getParent()._cfg.schedulePickDetail.xid);
			var params = {
					"furlname" : "schedulePickManage",
					"mainid" : rowid,
					"modifybill" : true,
			};
			var pdata = JSON.stringify(params);
			localStorage.setItem("schedule_pick_detail", pdata);
			this.getParent().showPage(this.getParent()._cfg.schedulePickDetail);
	};

	// 新增入库单
	Model.prototype.addClick = function(event) {
		this.getParent().closePage(this.getParent()._cfg.schedulePickEdit.xid);
		var params = {
				"operate" : "new",
				"furlname" : "schedulePickManage"
		};
		var pdata = JSON.stringify(params);
		localStorage.setItem("schedule_pick_edit", pdata);
		this.getParent().showPage(this.getParent()._cfg.schedulePickEdit);
	};

	// 编辑详情
	Model.prototype.editBtnClick = function(event, rowid, orderid) {
		this.getParent().closePage(this.getParent()._cfg.schedulePickEdit.xid);
		var params = {
				"mainid" : rowid,
				"oldorderid" : orderid,
				"furlname" : "schedulePickManage",
				"operate" : "draftedit"
		};
		var pdata = JSON.stringify(params);
		localStorage.setItem("schedule_pick_edit", pdata);

		this.getParent().showPage(this.getParent()._cfg.schedulePickEdit);

	};

	// 复制新增入库单
	Model.prototype.copyBtnClick = function(event, rowid) {
		var rowdata = this.comp("schedulePickData").getRowByID(rowid);

		this.getdetaildata(rowid, rowdata);// 加载明细数据再操作

		this.getParent().closePage(this.getParent()._cfg.schedulePickEdit.xid);
			var params = {
					"mainid" : rowid,
					"operate" : "copynew",
					"furlname" : "schedulePickManage"
			};
			var pdata = JSON.stringify(params);
			localStorage.setItem("schedule_pick_edit", pdata);
			this.getParent().showPage(this.getParent()._cfg.schedulePickEdit);
	};
	// 作废数据
	Model.prototype.statusBtnClick = function(event, rowid) {
		var schedulePickData = this.comp("schedulePickData");
		var row = schedulePickData.getRowByID(rowid);

		var me = this;

		if (row.val("status") === "1") {
			justep.Util.confirm("确定要作废单据[" + row.val("orderid") + "]吗？", function() {

				var operate_time = justep.Date.toString(row.val("operate_time"), justep.Date.STANDART_FORMAT_SHOT);

				justep.Baas.sendRequest({
					"url" : "/erpscan/base/schedulePick",
					"action" : "invalidSchedulePick",
					"async" : false,
					"params" : {
						"mainid" : rowid,
						"loginuserid" : me.loginuserid,
						"loginUser" : me.loginUser,
						"houseid" : row.val("houseid"),
						"operate_time" : operate_time,
						"pricebit" : me.getParent().comp("companyData").val("pricebit"),
						"orderid" : row.val("orderid"),
						"companyid" : row.val("companyid"),
						"countbit" : me.getParent().comp("companyData").val("countbit"),
						"moneybit" : me.getParent().comp("companyData").val("moneybit")
					},
					"success" : function(data) {
						if (data.state === "1") {
							row.val("status", "2");
							justep.Util.hint("单据[" + row.val("orderid") + "]已作废", {
								"type" : "success",
								"position" : "middle"
							});
							document.getElementById(rowid + "grid1statusbtn").style.display = "none";
							document.getElementById(rowid + "grid1print").style.display = "none";
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
							schedulePickData.refreshData();
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
		this.statusselect = svalue;
	};
	// 选择日期条件
	Model.prototype.datetypeselectChange = function(event) {
		var svalue = event.value;
		if (svalue !== "") {
			this.comp("begininput").set({
				"disabled" : false
			});
			this.comp("endinput").set({
				"disabled" : false
			});
			$(this.getElementByXid("daydropdown")).show();
		} else {
			this.comp("begininput").set({
				"disabled" : true
			});
			this.comp("endinput").set({
				"disabled" : true
			});
			this.comp("begininput").val("");
			this.comp("endinput").val("");
			$(this.getElementByXid("daydropdown")).hide();
		}
	};
	// 明细图片显示
	Model.prototype.grid2CellRender = function(event) {
		if (event.colName === "imgurl" && event.rowID !== "" && event.rowID !== undefined) {
			event.html = " <img src='" + mlfcommon.getItemImgUrl(event.colVal) + "' style='border-radius:50%;width:30px;height:30px;' onclick='{window.open(\"" + mlfcommon.getItemImgUrl(event.colVal)
			+ "\");}" + "' />";
		} else if ((",count,total,price,").indexOf("," + event.colName + ",") > -1) {
			if (event.colVal === 0) {
				event.html = "";
			} else {
				event.html = mlfcommon.formatNumber(event.colVal);
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

		if (endinput === "" || endinput === null || endinput === undefined) {
			justep.Util.hint("请选择结束日期", {
				type : "danger",
				position : "middle"
			});
			return;
		}

		event.source.set({
			disabled : true
		});
		setTimeout(function() {
			event.source.set({
				disabled : false
			});
		}, 4000);
		var arr = this.comp('grid1').getCheckeds();
		this.mainids = "";// 存放勾选指定记录
			if (arr.length === 0) {

				var me = this;
				justep.Util.confirm("确定要导出符合条件的【全部】排产领料汇总数据吗？", function() {
					me.toexceltotalordetail("total");
				});
			} else {

				var temp = "";
				for (var i = 0; i < arr.length; i++) {
					temp = temp + (temp === "" ? "" : ",") + arr[i]; // 不要单引号
				}
				this.mainids = temp;

				// 获取勾选的记录
				this.toexceltotalordetail("total");

			}

	};
	// 导出入库明细
	Model.prototype.toexceldetailClick = function(event) {
		var begininput = this.comp("begininput").val();
		var endinput = this.comp("endinput").val();
		if (begininput === "" || begininput === null || begininput === undefined) {
			justep.Util.hint("请选择开始日期", {
				type : "danger",
				position : "middle"
			});
			return;
		}

		if (endinput === "" || endinput === null || endinput === undefined) {
			justep.Util.hint("请选择结束日期", {
				type : "danger",
				position : "middle"
			});
			return;
		}
		event.source.set({
			disabled : true
		});
		setTimeout(function() {
			event.source.set({
				disabled : false
			});
		}, 4000);
			var arr = this.comp('grid1').getCheckeds();
			this.mainids = "";// 存放勾选指定记录
			if (arr.length === 0) {
				var me = this;
				justep.Util.confirm("确定要导出符合条件的【全部】排产领料明细数据吗？", function() {
					me.toexceltotalordetail("detail");
				});
			} else {

				var temp = "";
				for (var i = 0; i < arr.length; i++) {
					temp = temp + (temp === "" ? "" : ",") + arr[i]; // 不要单引号
				}
				this.mainids = temp;

				this.toexceltotalordetail("detail");

			}
	};
	// 根据类型type导出汇总或明细数据
	Model.prototype.toexceltotalordetail = function(type) {
		var filename = "";
		var url = "";
		var form = $('#dynamicFormoutexcel');
		if (type === "total") {
			filename = "排产领料汇总列表";
			url = "/baas/erpscan/base/schedulePick/getSchedulePickTotalExcel";

		} else if (type === "detail") {
			filename = "排产领料明细列表";
			url = "/baas/erpscan/base/schedulePick/getSchedulePickDetailExcel";
		}

		form = $("<form>");
		form.attr('id', 'dynamicFormoutexcel');
		form.attr('style', 'display:none');
		form.attr('target', '');
		form.attr('method', 'post');

		$('body').append(form);	
		
		this.getParamsdata();
		var params = {
				"userid" : this.loginuserid,
				"user" : this.loginUser,
				"dtype1" : this.dtype1,
				"datetypeselect" : this.datetypeselect,
				"statusselect" : this.statusselect,
				"companyid" : this.companyid,
				"begininput" : this.begininput,
				"endinput" : this.endinput,
				"houseid" : this.houseid,
				"customerid" : this.customerid,
				"iteminput" : this.iteminput,
				"searchcontent" : this.searchcontent,
				"hfilter" : this.hfilter,
				"cfilter" : this.cfilter,
				"mainids" : this.mainids,
				
				"countbit" : this.countbit,
				"moneybit" : this.moneybit,
				
				"companyname" : this.companyname,
		};
		var data = JSON.stringify(params);

		var my_input = $('<input type="hidden" name="filname" />');
		my_input.attr('value', filename);
		$("#dynamicFormoutexcel").append(my_input);

		my_input = $('<input type="hidden" name="datastr" />');
		my_input.attr('value', this.datastr);
		$("#dynamicFormoutexcel").append(my_input);

		my_input = $('<input type="hidden" name="showprice" />');
		my_input.attr('value', this.showprice);
		$("#dynamicFormoutexcel").append(my_input);

		my_input = $('<input type="hidden" name="data" />');
		my_input.attr('value', data);
		$("#dynamicFormoutexcel").append(my_input);


		// 2020-12-19
		var unitsetdatajson = JSON.stringify(this.unitsetdata);
		my_input = $('<input type="hidden" name="unitsetdata" />');
		my_input.attr('value', unitsetdatajson);
		$("#dynamicFormoutexcel").append(my_input);

		form = $('#dynamicFormoutexcel');
		form.attr('action', url);

		form.submit();
		form.remove();
	};

	// 打开单据草稿
	Model.prototype.todraftbillBtnClick = function(event) {
		this.getParent().closePage("draftbillmanage");
		localStorage.setItem("draftbilltype", "12");
		// 打开其他入库草稿
		this.getParent().showPage("draftbillmanage");
	};
	// 刷新新数据源
	Model.prototype.refreshdatabtnClick = function(event) {
		mlfcommon.getHouseData(this.comp("storehouseData"), this.companyid, this.housefilter, this.loginuserid, "", "", "");

		mlfcommon.getCustomerData(this.comp("customerData"), this.companyid, this.customerfilter, this.loginuserid, 4, "", "", "");

		justep.Util.hint("已刷新【查询仓库、单位部门】的数据", {
			"type" : "success",
			"position" : "middle"
		});
	};

	// 主表刷新，副表加载数据
	Model.prototype.schedulePickDataAfterRefresh = function(event) {
		this.getParentModel().hideloading();// 隐藏加载图
		var schedulePickDetailData = this.comp("schedulePickDetailData");
		schedulePickDetailData.clear();
		event.source.first();
	};

	// 数据刷新显示加载图
	Model.prototype.schedulePickDetailDataBeforeRefresh = function(event) {
		var sourcedata = this.comp("schedulePickDetailData");
		var grid = this.comp("grid2");
		justep.Baas.sendRequest({
			"url" : "/erpscan/base/schedulePick",
			"action" : "querySchedulePickDetailData",
			"async" : false,
			"params" : {
				"mainid" : this.currentRowId,
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

	Model.prototype.toolBar1Keypress = function(event){
		if (event.keyCode == 13) {
			var arr = document.querySelectorAll('input');
			for(var i=0;i<arr.length;i++){
				arr[i].blur();
			}
			this.findBtnClick();
		}
	};

	Model.prototype.getParamsdata = function(hasstatus) {

		var fconditiondata = this.comp("fconditiondata");
		this.begininput = this.comp("begininput").val();
		this.endinput = this.comp("endinput").val();
		if (this.begininput === "" || this.begininput === null || this.begininput === undefined) {
			justep.Util.hint("请选择开始日期", {
				type : "danger",
				position : "middle"
			});
			return 0;
		}
		this.searchcontent = pdacommon.transformSpecialInfo01(this.comp("searchcontent").val().trim());
		this.iteminput = pdacommon.transformSpecialInfo01(this.comp("iteminput").val().trim());
		this.datetypeselect = this.comp("datetypeselect").val();
		this.houseid = fconditiondata.getValue("houseid");
		this.customerid = this.comp("fconditiondata").getValue("customid");
		if (hasstatus === 0) {// 0-读取最新值 1-取变动值
			this.statusselect = this.comp("statusselect").val();
		}
		return 1;

	};

	Model.prototype.schedulePickDataCustomRefresh = function(event){
		this.getParentModel().showloading();// 显示加载图
		var sourcedata = this.comp("schedulePickData");
		var grid = this.comp("grid1");
		var me = this;
		justep.Baas.sendRequest({
			"url" : "/erpscan/base/schedulePick",
			"action" : "querySchedulePickData",
			"async" : false,
			"params" : {
				"userid" : this.loginuserid,
				"dtype1" : this.dtype1,
				"datetypeselect" : this.datetypeselect,
				"statusselect" : this.statusselect,
				"companyid" : this.companyid,
				"begininput" : this.begininput,
				"endinput" : this.endinput,
				"houseid" : this.houseid,
				"customerid" : this.customerid,
				"iteminput" : this.iteminput,
				"searchcontent" : this.searchcontent,
				"hfilter" : this.hfilter,
				"cfilter" : this.cfilter,
				
				"countbit" : this.countbit,
				"moneybit" : this.moneybit,
				
				"offset" : sourcedata.getOffset(),
				"limit" : sourcedata.limit,
				"orderBys" : sourcedata.getOrderBys()
			},
			"success" : function(data) {
				if (data.message === "") {
					sourcedata.loadData(data.table);
					sourcedata.first();
					grid.refresh();
					me.getParentModel().hideloading();// 隐藏加载图
				} else {
					justep.Util.hint(data.message, {
						"type" : "danger",
						"position" : "middle"
					});
				}
			}
		});
	};

	Model.prototype.scheduleRefreshBtnClick = function(event){
		this.clearBtnClick();
		this.findBtnClick();
	};

	Model.prototype.grid1RowClick = function(event){
		if (this.currentRowId === undefined || this.currentRowId !== event.rowId){
			this.currentRowId = event.rowID;
			this.getdetaildata(event.rowID, event.row);
		} else {

		}

	};	

	Model.prototype.getdetaildata = function(rowid, row) {
		var schedulePickDetailData = this.comp("schedulePickDetailData");
		var dcount = schedulePickDetailData.count();
		// 已加载了明细不再加载，没有就加载
		if (rowid !== "" && rowid !== undefined && (dcount === 0 || (dcount > 0 && schedulePickDetailData.getValue("orderid") !== row.val("orderid")))) {
			schedulePickDetailData.clear();
			schedulePickDetailData.refreshData();
			var property = row.val("iproperty");
			if (property !== undefined) {
				var propertyarr = property.split(";");
				var grid2 = this.comp("grid2");
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
		}
	};

	Model.prototype.grid1CellRender = function(event){
		if (event.colName === "operate") {
			var status = event.row.val("status");
			event.html = (status === "0" && this.edit ? "<button class='btn btn-link btn-sm linkbtn'  onclick='justep.Util.getModel(this).editBtnClick(event,\"" + event.rowID + "\",\""
					+ event.row.val("orderid") + "\")'>编辑</button>" + "<button class='btn btn-link btn-sm linkbtn'  id='" + event.rowID
					+ "mainstatusbtn'   onclick='justep.Util.getModel(this).deleteBtnClick(event,\"" + event.rowID + "\")'>删除</button>" : "")
					+ (this.detail === false ? "" : "<button class='btn btn-link btn-sm linkbtn'  onclick='justep.Util.getModel(this).detailBtnClick(event,\"" + event.rowID + "\")'>详情</button>")
					+ (this.copynew === false ? "" : "<button class='btn btn-link btn-sm linkbtn'  onclick='justep.Util.getModel(this).copyBtnClick(event,\"" + event.rowID + "\")'>复制</button>")
					+ (this.status === false ? "" : (status === "1" ? "<button class='btn btn-link btn-sm linkbtn'  id='" + event.rowID
							+ "grid1statusbtn'   onclick='justep.Util.getModel(this).statusBtnClick(event,\"" + event.rowID + "\")'>作废</button>" : ""))
							+ (this.canprint === false ? "" : (status === "1" ? "<button class='btn btn-link btn-sm linkbtn'  id='" + event.rowID
									+ "grid1print'   onclick='justep.Util.getModel(this).printBtnClick(event,\"" + event.rowID + "\")'>打印</button>" : ""));
		}
		if (event.colName === "status") {
			event.html = "<font style='color:" + pdacommon.getstoreoutcolor(event.colVal) + ";'>" + pdacommon.getprocessinstatus(event.colVal) + "</font>";

		}
		if ((",count,total,").indexOf("," + event.colName + ",") > -1) {
			if (event.colVal === 0) {
				event.html = "";
			} else {
				event.html = mlfcommon.formatNumber(event.colVal);
			}
		}

		if (event.colName === 'total' && event.colVal === undefined) {
			event.html = "--";
		}
	};

	Model.prototype.printBtnClick = function(event, rowid) {
		this.comp("pringDialog").open({
			"data" : {
				"schedule_pick_id" : rowid,
				"allcount" : this.comp("schedulePickDetailData").count(),
				"dialogid" : this.getIDByXID("pringDialog")
			},
			"src" : require.toUrl("./schedulePickPrint.w"),
			"status" : justep.Browser.isPC ? "normal" : "maximize"
		});
	};

	Model.prototype.cutZero = function(value) {
		return pdacommon.cutZero(value);
	};
	
	// 作废数据
	Model.prototype.deleteBtnClick = function(event, rowid) {
		var schedulePickData = this.comp("schedulePickData");
		var row = schedulePickData.getRowByID(rowid);
		var grid = this.comp("grid1");

		var me = this;

		if (row.val("status") === "0") {
			justep.Util.confirm("确定要删除单据[" + row.val("orderid") + "]吗？", function() {

				var operate_time = justep.Date.toString(row.val("operate_time"), justep.Date.STANDART_FORMAT_SHOT);

				justep.Baas.sendRequest({
					"url" : "/erpscan/base/schedulePick",
					"action" : "deleteSchedulePick",
					"async" : false,
					"params" : {
						"mainid" : rowid,
						"loginuserid" : me.loginuserid,
						"loginUser" : me.loginUser,
						"operate_time" : operate_time,
						"companyid" : row.val("companyid"),
					},
					"success" : function(data) {
						if (data.message === "") {
							schedulePickData.remove(row);
							justep.Util.hint("单据[" + row.val("orderid") + "]已删除", {
								"type" : "success",
								"position" : "middle"
							});
							grid.refresh();
							
						} else {
							justep.Util.hint(data.message, {
								"type" : "danger",
								"position" : "middle"
							});
						}
					},
					"error" : function() {
						justep.Util.hint("操作失败，请稍后再试。", {
							"type" : "danger",
							"position" : "middle"
						});
					}
				});

			});
		} else if (row.val("status") !== "0") {
			justep.Util.hint("单据[" + row.val("orderid") + "]不可删除", {
				"type" : "danger",
				"position" : "middle"
			});
		}

	};

	return Model;
});