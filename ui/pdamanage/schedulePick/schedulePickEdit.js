define(function(require) {
	var $ = require("jquery");
	var justep = require("$UI/system/lib/justep");
	var mlfcommon = require("./../../js/mlfcommon");
	var pdacommon = require("./../../js/pdacommon");

	var scan = require('$UI/system/api/native/scan');

	var Model = function() {
		this.callParent();
		this.companyid = "";
		this.storehouse2 = "";
		this.orderid = "";
		this.operate = "";
		this.furlname = "";// 父页名
		this.loginuserid = "";
		this.loginUser = "";
		this.countbit = 0;
		this.pricebit = 2;
		this.moneybit = 2;
		this.rowReadonly = justep.Bind.observable(true);// 数据源只读
	};

	Model.prototype.modelLoad = function(event) {
		var parent = this.getParentModel();
		if (parent === undefined) {
			window.open(require.toUrl("./../../index.w"), "_self");
		}

		var companyData = this.getParentModel().comp("companyData");
		this.countbit = companyData.val("countbit");
		this.pricebit = companyData.val("pricebit");
		this.moneybit = companyData.val("moneybit");

		this.showprinttip = companyData.val("showprinttip");

		var loginUserData = this.getParentModel().comp("userinfoData");
		this.loginuserid = loginUserData.val('userid');
		this.loginUser = loginUserData.val('realname') + "[" + loginUserData.val('username') + "]";
		this.companyid = loginUserData.val("companyid");

		// 2020-12-22 获取辅助单位设置 ，约定-3个辅助运算列的列名需为：原列名+tounit1、2、3的形式
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
		var crowdata = permissiondata.find([ "fvalue" ], [ "iteminfodata:new" ]);
		if (crowdata.length > 0) {// 增加商品
			$(this.getElementByXid("itemaddbutton")).show();
		}

		crowdata = permissiondata.find([ "fvalue" ], [ "staffdata:new" ]);
		if (crowdata.length > 0) {// 增加员工
			$(this.getElementByXid("staffdropdown")).show();
		}

		crowdata = permissiondata.find([ "fvalue" ], [ "housedata:new" ]);
		if (crowdata.length > 0) {// 增加仓库
			$(this.getElementByXid("addhousebutton")).show();
		}

		crowdata = permissiondata.find([ "fvalue" ], [ "customerdata:new" ]);
		if (crowdata.length > 0) {// 增加往来单位
			$(this.getElementByXid("addcustomerbutton")).show();
		}

		// 查看单价
//		crowdata = permissiondata.find([ "fvalue" ], [ "otheroutdata:showprice" ]);
//		if (crowdata.length > 0) {
			this.showprice = true;
//		} else {
//			this.showprice = false;
//		}

		crowdata = permissiondata.find([ "fvalue" ], [ "schedulepickdata:save" ]);
		if (crowdata.length > 0) {// 保存功能
			$(this.getElementByXid("submitbtn")).show();
//			$(this.getElementByXid("actsavelistbtn")).show();
		} else {
			$(this.getElementByXid("submitbtn")).hide();
//			$(this.getElementByXid("actsavelistbtn")).hide();
		}

		this.showprint = false;
		crowdata = permissiondata.find([ "fvalue" ], [ "schedulepickdata:print" ]);
		if (crowdata.length > 0) {// 增加
			if (justep.Browser.isPC && this.showprinttip === 1)
				this.showprint = true;
		}

	};

	Model.prototype.pricereadonly = function() {
		return !this.showprice;
	};

	Model.prototype.modelParamsReceive = function(event) {
		// 2020-12-08
		this.hfilter = localStorage.getItem(this.loginuserid + "hfilter");

		var schedule_pick_edit = localStorage.getItem("schedule_pick_edit");
		if (schedule_pick_edit !== null) {
			var params = JSON.parse(schedule_pick_edit);

			this.operate = params.operate;
			this.furlname = params.furlname;
			var repeat = params.repeat;

			if (justep.Browser.isX5App || justep.Browser.isWeChat) {
				$(this.getElementByXid("scanbtn")).show();
				$(this.getElementByXid("scaninbtn")).show();
			}

			var schedulePickData = this.comp("schedulePickData");
			var storehouseData = this.comp("storehouseData");
			var staffinfoData = this.comp("staffinfoData");
			var data2 = this.comp("data2");

			schedulePickData.clear();
			data2.clear();

			if (repeat !== true) {

				mlfcommon.getHouseData(storehouseData, this.companyid, this.hfilter, this.loginuserid, "", "", "");

				mlfcommon.getStaffinfoData(staffinfoData, this.companyid, "1", "", "convert(staffname using gbk) asc");
			}

			var orderid = "";
			var nocountstr = "";
			var me = this;
			var oldschedulePickData = this.comp("oldschedulePickData");
			oldschedulePickData.clear();

			if (this.operate === "draftedit") {// 编辑生产领料单
				this.mainid = params.mainid;
				var outmessage = "";
				orderid = params.oldorderid;
				oldschedulePickData.refreshData();
				oldschedulePickData.first();
				if (oldschedulePickData.count() > 0) {
					if (oldschedulePickData.getValue("status") === "1") {
						outmessage = "排产领料单[" + orderid + "]已记帐了，操作失败。";
					} else if (oldschedulePickData.getValue("status") === "2") {
						outmessage = "排产领料单[" + orderid + "]已作废了，操作失败。";
					}
				} else {
					outmessage = "排产领料单[" + orderid + "]已删除了，操作失败。";
				}

				if (outmessage !== "") {
					this.comp("message").show({
						message : outmessage
					});
					this.getParent().showPage(me.furlname);
					this.close();
					localStorage.removeItem("schedule_pick_edit");
					return;
				}
			}

			if (this.operate === "new") {

				schedulePickData.newData({
					"defaultValues" : [ {
						"schedule_pick_id" : justep.UUID.createUUID(),
						"bill_type" : "42",
						"companyid" : this.companyid,
						"orderid" : orderid,
						"operate_time" : new Date(),
						"operate_by" : "",
						"houseid" : "",
						"customerid" : "",
						"customername" : "",
						"count" : 0,
						"total" : 0,
						"status" : "1",
						"remark" : "",
						"printing" : 0,
						"outexcel" : 0,
						"barcode" : "",
						"originalbill" : "",
						"iproperty" : "",
						"staffname" : this.loginUser,
					} ]
				});

				this.comp("gridSelect2").set({
					"label" : ""
				});
				if (staffinfoData.count() > 0) {
					var staffrow = staffinfoData.find([ "userid" ], [ this.loginuserid ]);
					schedulePickData.setValue("operate_by", staffrow.length > 0 ? staffrow[0].val("staffid") : staffinfoData.getValue("staffid"));

				}

			} else {
				if (this.operate === "draftedit" || this.operate === "copynew" || this.operate === "draftcopynew" || this.operate === "billcopynew") {

					if (this.operate === "draftcopynew" || this.operate === "billcopynew" || this.operate === "copynew") {
						this.mainid = params.mainid;
						oldschedulePickData.refreshData();
					}
					oldschedulePickData.first();

					schedulePickData.newData({
						"defaultValues" : [ {
							"schedule_pick_id" : this.operate === "draftedit" ? oldschedulePickData.getValue("schedule_pick_id") : justep.UUID.createUUID(),
									"bill_type" : oldschedulePickData.getValue("bill_type"),
									"companyid" : this.companyid,
									"orderid" : orderid,
									"operate_time" : this.operate === "draftedit" ? oldschedulePickData.getValue("operate_time") : new Date(),
											"operate_by" : oldschedulePickData.getValue("operate_by"),
											"houseid" : oldschedulePickData.getValue("houseid"),
											"housename" : oldschedulePickData.getValue("housename"),
											"customerid" : oldschedulePickData.getValue("customerid"),
											"customername" : oldschedulePickData.getValue("customername"),
											"count" : 0,
											"total" : 0,
											"status" : "1",
											"remark" : oldschedulePickData.getValue("remark"),
											"printing" : 0,
											"outexcel" : 0,
											"barcode" : "",
											"originalbill" : oldschedulePickData.getValue("originalbill"),
											"iproperty" : oldschedulePickData.getValue("iproperty"),
											"staffname" : oldschedulePickData.getValue("staffname"),
						} ]
					});
				}
				var schedulePickDetailData = this.comp("schedulePickDetailData");
				var itemBatchConfigData = this.comp("itemBatchConfigData");
				if (this.operate === "draftedit" || this.operate === "draftcopynew" || this.operate === "billcopynew" || this.operate === "copynew") {
					this.houseid = oldschedulePickData.getValue("houseid");
					this.scheduleid = oldschedulePickData.getValue("relation_schedule_id");
					schedulePickDetailData.refreshData();
					itemBatchConfigData.refreshData();
				} else {
					return;
				}
				schedulePickDetailData.first();

				// 加载grid
				this.loadGrid2("ByMainId");

			}
			var property = "";
			var itempropertyData = this.comp("itempropertyData");
			if (repeat !== true) {
				mlfcommon.getItempropertyData(itempropertyData, this.companyid, "", "", "");
			}
			var grid2 = this.comp("grid2");

			if (itempropertyData.count() > 0) {
				itempropertyData.eachAll(function(params) {
					var colname = params.row.val("propertyname");
					grid2.showCol(colname);
					grid2.setLabel(colname, params.row.val("propertyshow"));
					property = property + (property === "" ? "" : ";") + colname + "," + params.row.val("propertyshow");
				});
			}

			schedulePickData.setValue("iproperty", property);// 保存当前属性显示列

//			if (nocountstr !== "") {
//			this.comp("message").show({
//			"message" : "【" + nocountstr + "】这些商品在当前仓库库存为0，不能进行出库，因此没有增加到当前生产领料单的明细记录中。"
//			});
//			}

//			pdacommon.showdatagridrow(data2);
			this.grid2Reload();

		} else {
			this.getParent().shellImpl.closePage();
		}
	};

	Model.prototype.customerdivClick = function(event) {
		var me = this;
		me.comp("customerDialog").open({
			"data" : {
				"type" : 3,
				"dialogid" : me.getIDByXID("customerDialog")
				// 选择本单位部门
			},
			"src" : require.toUrl("./../selectcustomer.w"),
			"status" : justep.Browser.isPC ? "normal" : "maximize"
		});
	};

	Model.prototype.customerDialogReceived = function(event) {
		var customer = JSON.parse(event.data);
		var schedulePickData = this.comp("schedulePickData");
		schedulePickData.setValue("customerid", customer.customerid);
		schedulePickData.setValue("customername", customer.customername);
		if (customer.staff !== "") {
			schedulePickData.setValue("operate_by", customer.staff);
		}
	};

	// 新增单位部门
	Model.prototype.addcustomerbuttonClick = function(event) {
		this.getParent().showPage("customermanage");
	};

	// 调取商品选择器
	Model.prototype.itemselectbtn = function(event) {
		var schedulePickData = this.comp("schedulePickData");
		var houseid = schedulePickData.getValue("houseid");
		var customerid = schedulePickData.getValue("customerid");
		var operate_by = schedulePickData.getValue("operate_by");
		if (houseid === undefined || houseid === "" || houseid === null) {
			this.comp("message").show({
				message : "请先选择【出库仓库】，再选择商品。"
			});
		} else if (customerid === undefined || customerid === "" || customerid === null) {
			this.comp("message").show({
				message : "请先选择【单位部门】，再选择商品。"
			});
		} else if (operate_by === undefined || operate_by === "" || operate_by === null) {
			this.comp("message").show({
				message : "请先选择【经手人】，再选择商品。"
			});
		} else if (this.scheduleid === undefined || this.scheduleid === "" || this.scheduleid === null) {
			this.comp("message").show({
				message : "请先选择【排产单】，再选择商品。"
			});
		} else {
			var itemList = this.getConfigItemList();
			this.comp("itemDialog").open({
				"data" : {
					"houseid" : houseid,
					"scheduleid" : this.scheduleid,
					"itemList" : itemList,
					"housename" : this.comp("storehouseData").getValueByID("housename", houseid),
					"schedule_pick_id" : this.mainid,
					"dialogid" : this.getIDByXID("itemDialog")
				},
				"src" : require.toUrl("./selectScheduleItem.w"),
				"status" : justep.Browser.isPC ? "normal" : "maximize"
			});
		}
	};

	Model.prototype.getConfigItemList = function() {
		var data2 = this.comp("data2");
		var itemList = [];
		data2.each(function(params){
			var itemid = params.row.val("itemid");
			if (itemid !== "")
				itemList.push(itemid);
		});
		return itemList;
	};


	Model.prototype.data2ValueChanged = function(event) {
		var rowdata = event.row;

		if (event.col === "count") {
			var count = 0;
			if (isNaN(rowdata.val("count")) === false) {
				count = rowdata.val("count").toFixed(this.countbit);
				rowdata.val("count", count);

			} else {
				count = 0;
				rowdata.val("count", 0);
			}

			if (count < 0) {
				rowdata.val("total", (count * rowdata.val("price")).toFixed(this.moneybit));
			} else if (count >= 0) {
				rowdata.val("price", 0);
				rowdata.val("total", 0);
			}

			// 多单位功能
			for (var a = 1; a <= 3; a++) {
				if (rowdata.val("unitstate" + a) === 1) {
					rowdata.val("counttounit" + a, pdacommon.getConvertCount(count, rowdata.val("unit"), rowdata.val("unitset" + a), this.unitsetdata.countbit));
				}
			}
		}

		if (event.col === "price") {
			if (isNaN(rowdata.val("price")) === false && rowdata.val("price") > 0 && rowdata.val("count") < 0) {
				var price = rowdata.val("price").toFixed(this.pricebit);
				rowdata.val("price", price);
				rowdata.val("total", (rowdata.val("count") * price).toFixed(this.moneybit));
			} else {
				rowdata.val("price", 0);
				if (rowdata.val("count") > 0)
					rowdata.val("total", 0);
			}
		}
		if (event.col === "total" && isNaN(rowdata.val("count")) === false && rowdata.val("count") <= 0) {
			if (isNaN(rowdata.val("total")) === false) {
				if (rowdata.val("count") !== 0) {
					var total = rowdata.val("total").toFixed(this.moneybit);
					rowdata.val("total", total);
					rowdata.val("price", (total / rowdata.val("count")).toFixed(this.pricebit));
				}
			} else {
				rowdata.val("total", 0);
				rowdata.val("price", 0);
			}
		}

	};

	Model.prototype.grid2CellRender = function(event) {

		var itemid = event.row !== null ? event.row.val("itemid") : "";
		if (event.colName === "itemid" && itemid !== "") {
			event.html = "<button class='btn btn-link btn-sm linkbtn gridbtn'  onclick='justep.Util.getModel(this).removebtn2Click(event,\"" + event.rowID + "\")'>删除</button>" 
			+ "<button class='btn btn-link btn-sm linkbtn gridbtn'  onclick='justep.Util.getModel(this).configBtnClick(event,\"" + event.rowID + "\")'>领料配置</button>";
		} else if ((",count,price,total,").indexOf("," + event.colName + ",") > -1 && (itemid !== "" || event.row === null)) {
			if (event.colVal === 0 || event.colVal === undefined) {
				event.html = "";
			} else {
				if (event.colVal < 0) {
					event.html = "<font style='color:red;'>" + mlfcommon.formatNumber(event.colVal) + "</font>";
				} else {
					event.html = mlfcommon.formatNumber(event.colVal);
				}
			}
		} else if (event.colName === "imgurl" && itemid !== "") {
			event.html = " <img src='" + mlfcommon.getItemImgUrl(event.colVal) + "' style='border-radius:50%;width:30px;height:30px;' onclick='{window.open(\"" + mlfcommon.getItemImgUrl(event.colVal)
			+ "\");}" + "' />";

		} else if (event.colName === "classid" && itemid !== "") {
			var rowdata = this.comp("itemclassData").find([ "classid" ], [ event.colVal ]);
			if (rowdata.length > 0) {
				event.html = rowdata[0].val("classname");
			} else {
				event.html = "";
			}
		} else if (event.colName.indexOf('tounit') > -1 && event.row !== null) {
			var row = event.row;
			var type = event.colName.charAt(event.colName.length - 1);
			var countcolname = event.colName.substring(0, event.colName.indexOf("tounit"));
			if (this.unitsetdata["unitstate" + type] === 1 && row.val("unitstate" + type) === 1) {
				event.html = pdacommon.getConvertCount(row.val(countcolname), row.val("unit"), row.val("unitset" + type), this.unitsetdata.countbit);
			}
		} else if (event.colName === "needString" && itemid !== "") {
			var row = event.row;
			event.html = this.getNeedString(row, 1);
		} else {
			if (event.colVal === undefined) {
				event.html = "";
			} else {
				event.html = "<div style='word-break:break-all;word-wrap:break-word; white-space:normal;'>" + event.colVal + "</div>";
			}
		}

		if ((event.colName === 'price' || event.colName === 'total') && !this.showprice && (itemid !== "" || event.row === null)) {
			event.html = "--";
		}

	};

	Model.prototype.getNeedString = function(row, stype) {
		var str = "";
		if (row !== undefined && row !== null) {
			str = "<div style='word-break:break-all;word-wrap:break-word; white-space:normal;'>" + row.val("still_need") + "/" + row.val("have_picked") + "/" + row.val("original_need")
			+ (row.val("still_need") === row.val("count") ? " <font style='color:red;'> 够料</font>" : "")
			+ (row.val("still_need") < row.val("count") ? " <font style='color:orange;'> 超领</font>" : "") + "</div>";
		}

		if (stype === 1) {
			return str;
		} else if (row !== undefined && row !== null) {
			row.val("needString", str);
		}
	};

	// 录入数量
	Model.prototype.countChange = function(fvalue, rowid) {
		var rowdata = this.comp("data2").getRowByID(rowid);
		var value = parseFloat(fvalue);
		if (isNaN(value) === true) {
			rowdata.val("count", 1);
			document.getElementById(rowid + "otheroutcountinput").value = 1;
		} else if (value <= 0) {
			rowdata.val("count", rowdata.val("count"));
			document.getElementById(rowid + "otheroutcountinput").value = rowdata.val("count");
		} else if (value <= rowdata.val("maxcount")) {
			rowdata.val("count", value.toFixed(this.countbit));
			document.getElementById(rowid + "otheroutcountinput").value = value.toFixed(this.countbit);
		} else {
			rowdata.val("count", rowdata.val("maxcount"));
			document.getElementById(rowid + "otheroutcountinput").value = rowdata.val("maxcount");
			justep.Util.hint("当前序号为" + rowid + "的商品《" + rowdata.val("itemname") + "》可出库数量为" + rowdata.val("maxcount") + "，不能超出此数量", {
				type : "danger",
				position : "middle",
				delay : 5000
			});
		}
	};
	// 增加数量
	Model.prototype.addRowBtnClick = function(event, rowid) {
		var rowdata = this.comp("data2").getRowByID(rowid);
		var count = parseFloat(rowdata.val("count") + 1);
		if (count <= 0) {
			rowdata.val("count", rowdata.val("count"));
		} else if (count <= rowdata.val("maxcount")) {
			rowdata.val("count", count.toFixed(this.countbit));
		} else {
			rowdata.val("count", rowdata.val("maxcount"));
			document.getElementById(rowid + "otheroutcountinput").value = rowdata.val("maxcount");
			justep.Util.hint("当前序号为" + rowid + "的商品《" + rowdata.val("itemname") + "》可出库数量为" + rowdata.val("maxcount") + "，不能超出此数量", {
				type : "danger",
				position : "middle",
				delay : 5000
			});
		}
	};
	// 减少数量
	Model.prototype.reduceRowBtnClick = function(event, rowid) {
		var rowdata = this.comp("data2").getRowByID(rowid);
		var count = parseFloat(rowdata.val("count") - 1);
		if (count <= 0) {
			rowdata.val("count", rowdata.val("count"));
		} else if (count <= rowdata.val("maxcount")) {
			rowdata.val("count", count.toFixed(this.countbit));
		} else {
			rowdata.val("count", rowdata.val("maxcount"));
			document.getElementById(rowid + "otheroutcountinput").value = rowdata.val("maxcount");
			justep.Util.hint("当前序号为" + rowid + "的商品《" + rowdata.val("itemname") + "》可出库数量为" + rowdata.val("maxcount") + "，不能超出此数量", {
				type : "danger",
				position : "middle",
				delay : 5000
			});
		}
	};

	// 删除后要重新编序号
	Model.prototype.changegoodsnumber = function() {
		var data2 = this.comp("data2");
		var i = 1;
		data2.eachAll(function(params) {
			params.row.val("goods_number", i);
			i++;
		});
		this.comp("grid2").refresh();
	};

	// 批量删除商品
	Model.prototype.deletebtnClick = function(event) {
		var data2 = this.comp("data2");
		var itemBatchConfigData = this.comp("itemBatchConfigData");
		var rows = this.comp("grid2").getCheckedRows();
		if (rows.length === 0) {
			justep.Util.hint("请选择要删除的商品", {
				"type" : "danger",
				"position" : "middle"
			});
		} else {
			var me = this;
			var str = "";
			var i = 0;
			var len = rows.length;
			for (i = 0; i < len; i++) {
				str = str + (str === "" ? "" : "、") + rows[i].val("goods_number") + "：" + rows[i].val("codeid");
			}
			justep.Util.confirm("确定要删除[" + str + "]这" + len + "条商品记录吗？", function() {
				for (i = 0; i < len; i++) {
					data2.remove(rows[i]);
					itemBatchConfigData.remove(itemBatchConfigData.getRowByID(rows[i].val("itemid")));
				}
				pdacommon.changegoodsnumber(data2, me.comp("grid2"));// 删除后重新编序号
				justep.Util.hint("已删除成功", {
					"type" : "success",
					"position" : "middle"
				});
			});
		}
	};

	// 删除记录
	Model.prototype.removebtn2Click = function(event, rowid) {
		var data2 = this.comp("data2");
		var itemBatchConfigData = this.comp("itemBatchConfigData");
		var row = data2.getRowByID(rowid);
		var me = this;
		justep.Util.confirm("确定要删除[" + row.val("goods_number") + "：" + row.val("codeid") + "]这条商品记录吗？", function() {
			data2.remove(row);
			itemBatchConfigData.remove(itemBatchConfigData.getRowByID(rowid));
			pdacommon.changegoodsnumber2(data2, me.comp("grid2"));// 删除后重新编序号
		});
	};

	// 商品码录入聚焦清空内容
	Model.prototype.barcodeinputFocus = function(event) {
		$(this.getElementByXid("barcodeinput")).val("");
		this.comp("schedulePickData").setValue("barcode", "");
	};

	// type = 0 暂存
	Model.prototype.tempstorageBtnClick = function(event) {
		event.source.set({
			disabled : true
		});
		this.saveoperate(0);
		event.source.set({
			disabled : false
		});
	};

	// type = 1 保存
	Model.prototype.submitbtnClick = function(event) {
		event.source.set({
			disabled : true
		});
		this.saveoperate(1);
		event.source.set({
			disabled : false
		});
	};
	// type=2保存并新增
	Model.prototype.saveaddBtnClick = function(event) {
		event.source.set({
			disabled : true
		});
		this.saveoperate(2);
		event.source.set({
			disabled : false
		});
	};

	// type=3保存并复制
	Model.prototype.savecopyBtnClick = function(event) {
		event.source.set({
			disabled : true
		});
		this.saveoperate(3);
		event.source.set({
			disabled : false
		});
	};

	// type=-2保存并新增
	Model.prototype.actsaveaddBtnClick = function(event) {
		event.source.set({
			disabled : true
		});
		this.saveoperate(-2);
		event.source.set({
			disabled : false
		});
	};

	// type=-3保存并复制
	Model.prototype.actsavecopyBtnClick = function(event) {
		event.source.set({
			disabled : true
		});
		this.saveoperate(-3);
		event.source.set({
			disabled : false
		});
	};

	// 保存操作
	// type = 1 保存
	// type=2保存并新增
	// type=3保存并复制
	Model.prototype.saveoperate = function(type) {
		var data2 = this.comp("data2");
		var schedulePickData = this.comp("schedulePickData");

		var operate_time = schedulePickData.getValue("operate_time");
		var operate_by = schedulePickData.getValue("operate_by");
		var houseid = schedulePickData.getValue("houseid");
		var customerid = schedulePickData.getValue("customerid");

		if (operate_time === undefined || operate_time === "") {
			justep.Util.hint("请选择领用日期", {
				type : "danger",
				position : "middle",
				delay : 5000
			});
			return;
		}
		if (houseid === undefined || justep.String.trim(houseid) === "") {
			justep.Util.hint("请选择出库仓库", {
				type : "danger",
				position : "middle",
				delay : 5000
			});
			return;
		}
		if (customerid === undefined || justep.String.trim(customerid) === "") {
			justep.Util.hint("请选择单位部门", {
				type : "danger",
				position : "middle",
				delay : 5000
			});
			return;
		}
		if (operate_by === undefined || justep.String.trim(operate_by) === "") {
			justep.Util.hint("请选择或填写经手人", {
				type : "danger",
				position : "middle",
				delay : 5000
			});
			return;
		}
		if (this.scheduleid === undefined || this.scheduleid === "") {
			justep.Util.hint("请选择排产单", {
				type : "danger",
				position : "middle",
				delay : 5000
			});
			return;
		}

		var me = this;
		var detaildata = [];

		var countmessage = "";
		var moneymessage = "";

		var itemBatchConfigData = this.comp("itemBatchConfigData");
		var checkdata = [];
		var housename = me.comp("storehouseData").getRowByID(schedulePickData.getValue("houseid")).val("housename");

		var goods_number = 1;

		data2.eachAll(function(params) {
			if (params.row.val("itemid") === "") {
				return;
			}
			if (params.row.val("count") === 0 && params.row.val("total") === 0) {
				countmessage = countmessage + (countmessage === "" ? "序号【" + params.row.val("goods_number") + "】" : ",序号【" + params.row.val("goods_number") + "】");
				return;
			}  if ((params.row.val("count") < 0 && params.row.val("total") > 0)) {
				moneymessage = moneymessage + (moneymessage === "" ? "序号【" + params.row.val("goods_number") + "】" : ",序号【" + params.row.val("goods_number") + "】");
				return;
			} 

			var itemid = params.row.val("itemid");

			var config = itemBatchConfigData.getRowByID(itemid).row.config;

			var configKeyArr = me.filtKey(config);

			// 遍历 批号：数量的配置文件
			for(var i = 0;i < configKeyArr.length; i++){
				var batchno = configKeyArr[i];
				var pick_count = config[configKeyArr[i]].toFixed(me.countbit);

				checkdata.push({
					itemname : params.row.val("itemname"),
					itemid : itemid,
					batchno : batchno,
					pick_count : pick_count,// 领料数量
				});

				detaildata.push({
					detailid : params.row.val("detailid"),
//					goods_number : params.row.val("goods_number"),
					goods_number : goods_number,
					itemname : params.row.val("itemname"),
					itemid : params.row.val("itemid"),
					batchno : batchno,
					count : pick_count,
					price : params.row.val("price"),
					total : params.row.val("total"),
					remark : pdacommon.transformSpecialInfo(params.row.val("remark")),
					create_id : me.operate === "draftedit" ? params.row.val("create_id") : "",
							create_by : me.operate === "draftedit" ? params.row.val("create_by") : "",
									create_time : me.operate === "draftedit" ? "" + params.row.val("create_time") : ""
				});
				goods_number++;
			}


		});

		if (countmessage !== "") {
			this.comp("message").show({
				message : countmessage + "数量与成本金额不能同时为0，请检查！"
			});
			return;
		}

		if (moneymessage !== "") {
			this.comp("message").show({
				message : "数量小于0时，" + moneymessage + "的金额只能填写小于等于0的数值，请检查！"
			});
			return;
		}

		if (detaildata.length === 0) {
			justep.Util.hint("无商品数据记录，不能进行保存操作", {
				"type" : "danger",
				position : "middle",
				delay : 5000
			});
			return;
		}

		var message = (type === 0 || type > 1 ? "确定要暂存当前单据吗？" : "一旦保存不能修改单据只能作废单据，确定要保存吗？");

		justep.Util.confirm(message, function() {

			var maindata = {
					schedule_pick_id : schedulePickData.getValue("schedule_pick_id"),
					bill_type : schedulePickData.getValue("bill_type"),
					companyid : schedulePickData.getValue("companyid"),
					operate_time : "" + justep.Date.toString(schedulePickData.getValue("operate_time"), justep.Date.STANDART_FORMAT_SHOT),
					operate_by : schedulePickData.getValue("operate_by"),
					originalbill : pdacommon.transformSpecialInfo(schedulePickData.getValue("originalbill")),
					houseid : schedulePickData.getValue("houseid"),
					customerid : schedulePickData.getValue("customerid"),
					remark : pdacommon.transformSpecialInfo(schedulePickData.getValue("remark")),
					iproperty : schedulePickData.getValue("iproperty"),
					count : parseFloat((data2.sum("count")).toFixed(me.countbit)),
					orderid : schedulePickData.getValue("orderid")
			};


			justep.Baas.sendRequest({
				"url" : "/erpscan/base/schedulePick",
				"action" : "saveSchedulePick",
				"async" : false,
				"params" : {
					"maindata" : maindata,
					"detaildata" : detaildata,
					"type" : (type === 1 || type < 0 ? 1 : 0),
					"pricebit" : me.pricebit,
					"moneybit" : me.moneybit,
					"loginUser" : me.loginUser,
					"loginuserid" : me.loginuserid,
					"operate" : me.operate,
					"checkdata" : checkdata,
					"scheduleid" : me.scheduleid,
				},
				"success" : function(data) {
					if (data.state === "1") {
						if (type === 0) {
							justep.Util.hint("已暂存成功", {
								"type" : "success",
								"position" : "middle"
							});

							// if (me.operate === "draftedit" || me.operate ===
							// "draftcopynew") {
							// me.getParent().showPage("draftbillmanage");
							// } else {
							// me.getParent().showPage(me.furlname);
							// }
							me.comp("window").close();
							// here
							localStorage.removeItem("schedule_pick_edit");

							$(document.getElementById("scheduleRefreshBtn")).click();

						} else {
//							if (me.operate === "draftedit" || me.operate === "draftcopynew") {
//							$(document.getElementById("draftbillrefreshBtn")).click();
//							} else if (me.operate === "billcopynew") {
//							$(document.getElementById("billrefreshBtn")).click();
//							}
							$(document.getElementById("scheduleRefreshBtn")).click();
							if (type === 1) {
								if (me.showprint && (type === 1 || type < 0)) {
									me.comp("printMessage").show();
									me.warning = data.warning;
								} else {
									justep.Util.hint("已保存成功", {
										"type" : "success",
										"position" : "middle"
									});

									if (data.warning.length > 0) {
										pdacommon.showhouselimit(data.warning, me.getParent(), "prodrequisitionedit");
									} else {
										me.comp("window").close();
										// me.getParent().showPage(me.furlname);
										localStorage.removeItem("schedule_pick_edit");
									}
								}

							} else {
								var params = {};
								if (type === 2 || type === -2) {
									params = {
											"operate" : "new",
											"furlname" : me.furlname,
											"repeat" : true
									};
									justep.Util.hint((type > 0 ? "暂存" : "保存") + "成功，并且已【新增】一张排产领料单", {
										"type" : "success",
										"position" : "middle"
									});
								} else if (type === 3 || type === -3) {
									params = {
											"rowData" : schedulePickData.getCurrentRow().toJson(),
											"detailData" : data2.toJson(),
											"operate" : "copynew",
											"furlname" : me.furlname,
											"repeat" : true
									};
									justep.Util.hint((type > 0 ? "暂存" : "保存") + "成功，并且已【复制新增】一张排产领料单", {
										"type" : "success",
										"position" : "middle"
									});
								}
								var pdata = JSON.stringify(params);
								localStorage.setItem("schedule_pick_edit", pdata);
								me.modelParamsReceive(event);

								if (data.warning.length > 0) {
									pdacommon.showhouselimit(data.warning, me.getParent(), "");
								}
							}
						}

						if ((type === 0 || type > 1) && $(document.getElementById("qualityauditcountupdate")) !== undefined)
							$(document.getElementById("qualityauditcountupdate")).click();
					} else if (data.state === "2") {
						if (me.operate === "draftedit" || me.operate === "draftcopynew") {
							$(document.getElementById("draftbillrefreshBtn")).click();
						} else if (me.operate === "billcopynew") {
							$(document.getElementById("billrefreshBtn")).click();
						}
						$(document.getElementById("scheduleRefreshBtn")).click();
						// me.comp("message").show({
						// "message" : data.message
						// });
						// me.getParent().showPage(me.furlname);
						justep.Util.hint(data.message, {
							type : "danger",
							"position" : "middle",
							delay : 5000
						});
						me.comp("window").close();
						localStorage.removeItem("storeoutedit");
					} else if (data.message !== "") {
						me.comp("message").show({
							"message" : data.message
						});
					} else {
						justep.Util.hint("操作失败，请重试", {
							type : "danger",
							"position" : "middle",
							delay : 5000
						});
					}
				},
				"error" : function() {
					justep.Util.hint("操作失败，请重试", {
						type : "danger",
						"position" : "middle",
						delay : 5000
					});
				}
			});
		});

	};

	Model.prototype.printMessageYes = function(event) {
		var rowdata = this.comp("schedulePickData").getCurrentRow();
		var editpage = "otheroutdetail";

		this.getParent().closePage(editpage);
		var params = {
				"mainid" : rowdata.val("otherinoutid"),
				"furlname" : "showdetailbyorderid",
				"showprint" : true
		};
		var pdata = JSON.stringify(params);

		localStorage.setItem(editpage, pdata);

		var me = this;
		localStorage.removeItem("schedule_pick_edit");

		if (this.warning.length > 0) {
			pdacommon.showhouselimit2(me.warning, me.getParent(), editpage, this);
		} else {
			justep.Shell.showPage(editpage).done(function() {
				me.close();
			});
		}

	};

	Model.prototype.printMessageNo = function(event) {
		localStorage.removeItem("schedule_pick_edit");

		if (this.warning.length > 0) {
			pdacommon.showhouselimit(this.warning, this.getParent(), "schedule_pick_edit");

		} else {
			this.comp("window").close();
		}

	};

	// 录入商品码回车后进行商品添加
	Model.prototype.barcodeinputKeypress = function(event) {
		if (event.keyCode == 13) {
			var input = $(this.getElementByXid("barcodeinput"));
			input.blur();
			var fvalue = input.val();
			this.inputBarcode(fvalue);
			input.val("");
			input.focus();
		}
	};

	// 增加商品码的记录
	// 没做
	Model.prototype.inputBarcode = function(fvalue) {
//		var schedulePickData = this.comp("schedulePickData");
//		var houseid = schedulePickData.getValue("houseid");
//
//		if (houseid === undefined || houseid === "" || houseid === null) {
//			this.comp("message").show({
//				message : "请先选择【仓库】，再扫商品码。"
//			});
//			return;
//		}
//		if (fvalue !== "" && fvalue !== undefined && fvalue !== null) {
//			var data2 = this.comp("data2");
//
//			var rowdata = data2.find(["barcode"], [fvalue]);
//			if (rowdata.length > 0) {
//				rowdata[0].val("count", rowdata[0].val("count") + 1);
//			} else {
//
//				var iteminfoData = this.comp("iteminfoData");
//
//				iteminfoData.clear();
//				iteminfoData.setFilter("itemfilter", "companyid='" + this.companyid + "' and houseid='" + houseid + "' and barcode='" + fvalue + "'  and count-checkout_count>0");
//				iteminfoData.refreshData();
//
//				var message = this.comp("message");
//				if (iteminfoData.count() === 0) {
//					message.show({
//						message : "商品码为【" + fvalue + "】的商品记录不存在或库存不足"
//					});
//					// }
//					// else if (iteminfoData.count() > 1) {
//					// message.show({
//					// message : "商品码重复，有" + iteminfoData.count() + "商品码都为【"
//					// +
//					// fvalue + "】，请核查修改商品资料的商品码数据。"
//					// });
//					// return;
//				} else {
//					rowdata = data2.find([ "barcode", "batchno" ], [ fvalue, iteminfoData.getValue("batchno") ]);
//					if (rowdata.length > 0) {
//						rowdata[0].val("count", rowdata[0].val("count") + 1);
//
//						if (this.inscan !== undefined && this.inscan !== null && this.inscan === true) {
//							this.scanbtnClick();
//						}
//					} else {
//
//						var datacount = data2.count();// 有几多条数据
//						var curindex = data2.getRowIndex(data2.getCurrentRow()) + 1;// 当前行
//						var notdatacount = (data2.find([ "itemid" ], [ "" ])).length;// 有几多条无数据
//						var hasdatacount = datacount - notdatacount;
//						var index = curindex === -1 || hasdatacount < curindex ? hasdatacount : curindex;
//
//						var maxcount = parseFloat((iteminfoData.getValue("count") - iteminfoData.getValue("checkout_count")).toFixed(this.countbit));
//						data2.newData({
//							"index" : index,
//							"defaultValues" : [ {
//								"detailid" : "",
//								"itemid" : iteminfoData.getValue("itemid"),
//								"goods_number" : index + 1,
//								"imgurl" : iteminfoData.getValue("imgurl"),
//								"codeid" : iteminfoData.getValue("codeid"),
//								"itemname" : iteminfoData.getValue("itemname"),
//								"sformat" : iteminfoData.getValue("sformat"),
//								"classid" : iteminfoData.getValue("classid"),
//								"classname" : iteminfoData.getValue("classname"),
//								"unit" : iteminfoData.getValue("unit"),
//								"barcode" : iteminfoData.getValue("barcode"),
//								"count" : maxcount,
//								"price" : 0,
//								"total" : 0,
//								"remark" : "",
//								"property1" : iteminfoData.getValue("property1"),
//								"property2" : iteminfoData.getValue("property2"),
//								"property3" : iteminfoData.getValue("property3"),
//								"property4" : iteminfoData.getValue("property4"),
//								"property5" : iteminfoData.getValue("property5"),
//								"batchno" : iteminfoData.getValue("batchno"),
//								"maxcount" : maxcount,
//								"unitstate1" : iteminfoData.getValue("unitstate1"), // 2020-12-17
//								// 多单位字段
//								"unitstate2" : iteminfoData.getValue("unitstate2"),
//								"unitstate3" : iteminfoData.getValue("unitstate3"),
//								"unitset1" : iteminfoData.getValue("unitset1"),
//								"unitset2" : iteminfoData.getValue("unitset2"),
//								"unitset3" : iteminfoData.getValue("unitset3"),
//							} ]
//						});
//						pdacommon.changegoodsnumber(data2, this.comp("grid2"));
//
//						if (this.inscan !== undefined && this.inscan !== null && this.inscan === true) {
//							this.scanbtnClick();
//						}
//					}
//				}
//			}
//		}
	};

	// 根据日期获取取单号
	Model.prototype.input1Change = function(event) {
		var schedulePickData = this.comp("schedulePickData");
		var fvalue = schedulePickData.getValue("operate_time");
		var billdate = "";
		if (fvalue !== undefined) {
			billdate = justep.Date.toString(fvalue, "yyyyMMdd");
		}
		var orderid = schedulePickData.getValue("orderid");
		var orderdate = "";
		if (orderid !== undefined && orderid !== "") {
			orderdate = orderid.split("-")[1];
		}
		if (this.operate !== "draftedit" || (this.operate === "draftedit" && billdate !== orderdate)) {
			justep.Baas.sendRequest({
				"url" : "/erpscan/save/pdasave",
				"action" : "getOrderId",
				"async" : false,
				"params" : {
					"companyid" : this.companyid,
					"tablename" : "schedule_pick",
					"billdate" : billdate,
					"billtype" : "42"
				},
				"success" : function(param) {
					orderid = param.NewID;
					schedulePickData.setValue("orderid", orderid);
				}
			});
		}
	};

	// 新增员工
	Model.prototype.addbuttonClick = function(event) {
		this.getParent().closePage(this.getParent()._cfg.staffedit.xid);
		var params = {
				"operate" : "new",
				"furlname" : "schedule_pick_edit"
		};
		var pdata = JSON.stringify(params);
		localStorage.setItem("staffedit", pdata);

		this.getParent().showPage(this.getParent()._cfg.staffedit);
	};

	// 刷新员工
	Model.prototype.refreshbuttonClick = function(event) {
		this.comp("staffinfoData").refreshData();
	};

	// 新增仓库
	Model.prototype.addhousebuttonClick = function(event) {
		this.comp("windowDialog").open({
			"title" : "新增仓库信息",
			"data" : {
				"operate" : "new",
				"dialogid" : this.getIDByXID("windowDialog")
			},
			"src" : require.toUrl("./../houseedit.w"),
			"status" : justep.Browser.isPC ? "normal" : "maximize"
		});
	};

	// 新增商品
	Model.prototype.itemaddbuttonClick = function(event) {
		this.getParent().showPage("itemmanage");
	};

	// 商品扫码功能
	Model.prototype.scanbtnClick = function(event) {

		var me = this;
		if (justep.Browser.isX5App) {

			function onSuccess(result) {
				var showaddress = result.text; // 读取二维码内容;
				// 编写取后处理的代码

				if (showaddress !== null && showaddress !== undefined && showaddress !== "") {
					me.inscan = true;
					me.inputBarcode(showaddress);
				} else {
					justep.Util.hint("商品码信息获取失败！", {
						type : "danger",
						"position" : "middle"
					});
				}
			}
			function onError(error) {
				justep.Util.hint("扫码失败: " + error, {
					type : "danger",
					"position" : "middle",
					delay : 6000
				});
			}

			if (cordova.plugins !== undefined && cordova.plugins.barcodeScanner !== undefined) {
				cordova.plugins.barcodeScanner.scan(onSuccess, onError);
			} else if (justep.barcodeScanner !== undefined) {
				justep.barcodeScanner.scan(onSuccess, onError);
			} else {
				justep.Util.hint("请在App环境下进行商品码扫码操作。", {
					type : "danger",
					"position" : "middle"
				});
			}

		} else if (justep.Browser.isWeChat) {// 是否微信状态下

			scan.scanQRCode({
				needResult : 1, // 默认为0，扫描结果由微信处理，1则直接返回扫描结果，
				scanType : [ "qrCode", "barCode" ], // 可以指定扫二维码还是一维码，默认二者都有
				success : function(res) {
					var showaddress = res.resultStr; // 读取二维码内容;
					if (showaddress !== null && showaddress !== undefined && showaddress !== "") {
						var strs = showaddress.split(",");
						if (strs.length > 0) {
							me.inscan = true;
							showaddress = strs[0];
							me.inputBarcode(showaddress);
						} else {
							justep.Util.hint("商品码格式不正确！", {
								type : "danger"
							});
						}

					} else {
						justep.Util.hint("商品码信息获取失败！", {
							type : "danger"
						});
					}
				},
				fail : function(res) {

					justep.Util.hint("扫码失败，请核查，错误提示: " + res.resultStr, {
						type : "danger",
						delay : 6000
					});
				}
			});
		} else {
			justep.Util.hint("请在移动端微信环境下或App才能进行商品码扫码操作。", {
				type : "danger",
				"position" : "middle",
				delay : 6000
			});
		}
	};

	Model.prototype.data2IndexChanged = function(event) {
		var row = event.row;// 获取行
		if (row !== undefined && row !== null && row.val("itemid") !== "")
			this.rowReadonly.set(false);
		else {
			this.rowReadonly.set(true);
		}
	};

	/**
	 * 2020-12-18 添加换算列填写转化 设置单元格可编辑，编辑器选择组件模式 数据源设置只读规则
	 * 约定：单元格编辑器input的xid设置为：转换主列（count）+input+数据源+辅助单位序号（1/2/3），如countinputdata21，与换算列counttounit1类似
	 */
	Model.prototype.unitinputFocus = function(event) {
		var inputnode = $(event.source);
		inputnode.val("");

		var xid = inputnode.attr("xid");
		var type = xid.substring(xid.length - 1);
		var countname = xid.substring(0, xid.indexOf("input"));
		var dataname = xid.substring(xid.indexOf("input") + 5, xid.length - 1);
		var row = this.comp(dataname).getCurrentRow();
		this.tempvalue = row.val(countname);
		row.val(countname + "tounit" + type, "");

	};

	Model.prototype.unitinputBlur = function(event) {
		var inputnode = $(event.source);
		var inputcount = event.source.value;
		var xid = inputnode.attr("xid");
		var type = xid.substring(xid.length - 1);
		var countname = xid.substring(0, xid.indexOf("input"));
		var dataname = xid.substring(xid.indexOf("input") + 5, xid.length - 1);
		var row = this.comp(dataname).getCurrentRow();
		if (inputcount !== "0" && inputcount > 0 && pdacommon.isNumber(inputcount)) {
			var unitset = row.val("unitset" + type);
			var unitsetarr = unitset.split(",");
			var bili = unitsetarr[1];

			var count = (inputcount / bili).toFixed(this.unitsetdata.countbit);
			if (countname == "count") {
				this.countChange(count, this.comp(dataname).getCurrentRowID());
			} else {
				if (count <= 0) {
					row.val(countname, 1);
				} else {
					row.val(countname, count);
				}
			}
		} else {
			row.val(countname, this.tempvalue);
		}
	};

	// 增加商品码的记录
	Model.prototype.scaninBarcode = function(fvalue) {
		if (fvalue !== "" && fvalue !== undefined && fvalue !== null) {
			var data2 = this.comp("data2");

			var datacount = data2.count();// 有几多条数据
			var notdatacount = (data2.find([ "itemid" ], [ "" ])).length;// 有几多条无数据
			var hasdatacount = datacount - notdatacount;

			var houseid = this.comp("schedulePickData").getValue("houseid");

			var prodstoragedetailData = this.comp("prodstoragedetailData");
			prodstoragedetailData.clear();
			prodstoragedetailData.setFilter("filter1", "psd.companyid='" + this.companyid + "' and psd.orderid='" + fvalue + "'");
			prodstoragedetailData.refreshData();
			prodstoragedetailData.first();

			var message = this.comp("message");

			if (prodstoragedetailData.count() === 0) {

				message.show({
					message : "没有找到生产入库单号为" + fvalue + "的记录数据。"
				});

				return;
			}

			if (prodstoragedetailData.getValue("houseid") !== houseid) {
				if (hasdatacount === 0) {
					this.comp("schedulePickData").setValue("houseid", prodstoragedetailData.getValue("houseid"));
					houseid = prodstoragedetailData.getValue("houseid");
				} else {

					message.show({
						message : "单号为" + fvalue + "的生产入库的仓库与当前所选的出库仓库不一致，不能加载商品数据。"
					});

					return;
				}
			}

			var allcount = prodstoragedetailData.count();
			var iteminfoData = this.comp("iteminfoData");
			var me = this;
			var mss = "";
			var scount = 0;

			prodstoragedetailData.eachAll(function(params) {
				var rowdata = data2.find([ "itemid", "batchno" ], [ params.row.val("itemid"), params.row.val("batchno") ]);
				if (rowdata.length > 0) {
					rowdata[0].val("count", parseFloat((rowdata[0].val("count") + params.row.val("count")).toFixed(me.countbit)));
				} else {
					iteminfoData.clear();
					iteminfoData.setFilter("itemfilter", "itemid='" + params.row.val("itemid") + "' and houseid='" + houseid + "' and batchno='"
							+ pdacommon.transformSpecialInfo2(params.row.val("batchno")) + "' and count-checkout_count>0");
					iteminfoData.refreshData();

					if (iteminfoData.count() === 0) {
						mss = mss + "【" + params.row.val("itemname") + "," + params.row.val("batchno") + "】";
						scount++;
					} else {
						var datacount = data2.count();// 有几多条数据
						notdatacount = (data2.find([ "itemid" ], [ "" ])).length;// 有几多条无数据
						hasdatacount = datacount - notdatacount;
						var curindex = data2.getRowIndex(data2.getCurrentRow()) + 1;// 当前行
						var index = curindex === -1 || hasdatacount < curindex ? hasdatacount : curindex;

						var pcount = params.row.val("count");
						var maxcount = parseFloat((iteminfoData.getValue("count") - iteminfoData.getValue("checkout_count")).toFixed(this.countbit));
						pcount = pcount > maxcount ? maxcount : pcount;

						data2.newData({
							"index" : index,
							"defaultValues" : [ {
								"detailid" : "",
								"itemid" : iteminfoData.getValue("itemid"),
								"goods_number" : index + 1,
								"imgurl" : iteminfoData.getValue("imgurl"),
								"codeid" : iteminfoData.getValue("codeid"),
								"itemname" : iteminfoData.getValue("itemname"),
								"sformat" : iteminfoData.getValue("sformat"),
								"classid" : iteminfoData.getValue("classid"),
								"classname" : iteminfoData.getValue("classname"),
								"unit" : iteminfoData.getValue("unit"),
								"barcode" : params.row.val("barcode"),
								"count" : pcount,
								"price" : 0,
								"total" : 0,
								"remark" : params.row.val("remark"),
								"property1" : iteminfoData.getValue("property1"),
								"property2" : iteminfoData.getValue("property2"),
								"property3" : iteminfoData.getValue("property3"),
								"property4" : iteminfoData.getValue("property4"),
								"property5" : iteminfoData.getValue("property5"),
								"batchno" : params.row.val("batchno"),
								"maxcount" : maxcount,
								"unitstate1" : iteminfoData.getValue("unitstate1"), // 2020-12-17
								// 多单位字段
								"unitstate2" : iteminfoData.getValue("unitstate2"),
								"unitstate3" : iteminfoData.getValue("unitstate3"),
								"unitset1" : iteminfoData.getValue("unitset1"),
								"unitset2" : iteminfoData.getValue("unitset2"),
								"unitset3" : iteminfoData.getValue("unitset3"),
							} ]
						});
						pdacommon.changegoodsnumber(data2, me.comp("grid2"));
					}

					if (mss !== "") {
						message.show({
							message : "共" + allcount + "条生产入库产品，其中有" + scount + "条【商品名称,批号】为" + mss + "的商品记录的没有库存,没有加载到当前表单中。"
						});
					}
				}
			});
		}
	};

	Model.prototype.upClick = function(event) {
		var data2 = this.comp("data2");
		var rowdata = data2.getCurrentRow();
		var row_number = rowdata.val("goods_number");
		
		var uprow = null;

		data2.each(function(params){
			if (uprow === null && params.row.val("goods_number") === row_number -1)
				uprow = params.row;
		});
		
		if (uprow !== null && row_number !== 1) {
			rowdata.val("goods_number", row_number - 1);
			uprow.val("goods_number", row_number);
			data2.exchangeRow(rowdata, uprow);
			data2.to(rowdata);
		}

	};

	Model.prototype.downClick = function(event) {
		var data2 = this.comp("data2");
		var rowdata = data2.getCurrentRow();
		var row_number = rowdata.val("goods_number");
		
		var downrow = null;

		data2.each(function(params){
			if (downrow === null && params.row.val("goods_number") === row_number + 1)
				downrow = params.row;
		});
		
		if (downrow !== null && row_number !== data2.count()) {
			rowdata.val("goods_number", row_number + 1);
			downrow.val("goods_number", row_number);
			data2.exchangeRow(rowdata, downrow);
			data2.to(rowdata);
		}

	};

	Model.prototype.grid2Reload = function(event) {
		var grid = this.comp("grid2");
		var me = this;
		this.comp("data2").each(function(params) {

			if (params.row.val("itemid") !== "") {
				grid.setCell(params.row.getID(), "count", {
					"background" : "#FFFFDF"
				});
				grid.setCell(params.row.getID(), "remark", {
					"background" : "#FFFFDF"
				});

				grid.setCell(params.row.getID(), "batchno", {
					"background" : "#FFFFDF"
				});

			}
		});
	};

	Model.prototype.pricebuttonClick = function(event) {
		var row = this.comp("data2").getCurrentRow();
		var customerid = this.comp("schedulePickData").getValue("customerid");

		if (customerid === undefined || justep.String.trim(customerid) === "") {
			justep.Util.hint("请选择单位部门，再进行历史单价查询选择。", {
				type : "danger",
				position : "middle",
				delay : 5000
			});
			return;
		}

		this.getParent().comp("windowDialog").open(
				{
					data : {
						"rowid" : row.val("rowid"),
						"customerid" : customerid,
						"itemid" : row.val("itemid"),
						"returnclickbtn" : "selectoldpricereturn4",
						"title" : "单位部门：<font style='font-weight:bold;margin-right:10px;'>" + this.comp("customerinput").val() + "</font>  商品信息：" + row.val("codeid") + " " + row.val("itemname") + " "
						+ row.val("sformat") + (row.val("property1") === "" ? "" : " " + row.val("property1")) + (row.val("property2") === "" ? "" : " " + row.val("property2"))
						+ (row.val("property3") === "" ? "" : " " + row.val("property3")) + (row.val("property4") === "" ? "" : " " + row.val("property4"))
						+ (row.val("property5") === "" ? "" : " " + row.val("property5")) + (row.val("bacthno") === undefined ? "" : " " + row.val("bacthno"))
						+ " <font style='margin-left:10px;color:red;'>当前单价：" + row.val("price") + "</font>",
						"stype" : 4,
						"pricebit" : this.pricebit,
						"price" : row.val("price")
					},
					src : require.toUrl("./../selectoldprice.w"),
					"status" : justep.Browser.isPC ? "normal" : "maximize"
				});
	};

	Model.prototype.selectoldpricereturnbtnClick = function(event) {
		var temparr = localStorage.getItem("selectoldpricereturn4");
		var fpdata = JSON.parse(temparr);
		var rowdata = this.comp("data2").getRowByID(fpdata.rowid);
		var price = fpdata.price;

		rowdata.val("price", price);
		rowdata.val("total", parseFloat((rowdata.val("count") * price).toFixed(this.moneybit)));

	};

	Model.prototype.oldschedulePickDataBeforeRefresh = function(event){
		var sourcedata = this.comp("oldschedulePickData");
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

	Model.prototype.schedulePickDetailDataBeforeRefresh = function(event) {
		var sourcedata = this.comp("schedulePickDetailData");
		var me = this;
		var grid = this.comp("grid2");
		justep.Baas.sendRequest({
			"url" : "/erpscan/base/schedulePick",
			"action" : "querySchedulePickDetailData",
			"async" : false,
			"params" : {
				"mainid" : this.mainid,
				"getSchedule" : true,
				"orderBys" : sourcedata.getOrderBy(),
			},
			"success" : function(data) {
				if (data.message === "") {
					sourcedata.loadData(data.table);
					sourcedata.first();
					// 排产单id
					me.comp("input5").val(data.relationScheduleOrderId);
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

	// 选排产单
	Model.prototype.div14Click = function(event){
		if (this.comp("schedulePickData").getValue("houseid") === undefined || this.comp("schedulePickData").getValue("houseid") === ""){
			justep.Util.hint("请先选择仓库", {
				"type" : "danger",
				"position" : "middle"
			});
			return;
		}

		var me = this;
		me.comp("scheduleDialog").open({
			"data" : {
				"dialogid" : me.getIDByXID("scheduleDialog"),
				"companyid" : this.companyid,
			},
			"src" : require.toUrl("./selectSchedule.w"),
			"status" : justep.Browser.isPC ? "normal" : "maximize"
		});
	};

	Model.prototype.scheduleDialogReceive = function(event){
		var receivedData = JSON.parse(event.data);
		this.scheduleid = receivedData.scheduleid;
		this.comp("input5").val(receivedData.orderid);
		this.loadGrid2("BySchedule");
	};

	Model.prototype.loadGrid2 = function(type){
		if (type === undefined || type === ""){
			return;
		}
		var schedulePickData = this.comp("schedulePickData");
		var data2 = this.comp("data2");
		var itemBatchConfigData = this.comp("itemBatchConfigData");
		var houseid = schedulePickData.getValue("houseid");
		var grid = this.comp("grid2");
		var me = this;

//		grid.refresh();
		if(type === "BySchedule"){
			justep.Baas.sendRequest({
				"url" : "/erpscan/base/schedulePick",
				"action" : "getItemListByScheduleId",
				"async" : false,
				"params" : {
					"scheduleid" : this.scheduleid,
					"houseid" : houseid,
				},
				"success" : function(data) {
					if (data.message === "") {
						data2.clear();
						data2.loadData(data.itemTable);
						itemBatchConfigData.loadData(data.itemConfig);

						me.setData2count();

						grid.refresh();

					} else {
						justep.Util.hint(data.message, {
							"type" : "danger",
							"position" : "middle"
						});
					}
				}
			});
		} else if(type === "ByMainId"){
			justep.Baas.sendRequest({
				"url" : "/erpscan/base/schedulePick",
				"action" : "getItemListByMainId",
				"async" : false,
				"params" : {
					"mainid" : this.mainid,
				},
				"success" : function(data) {
					if (data.message === "") {
						data2.clear();
						data2.loadData(data.table);

						me.setData2count();

						grid.refresh();

					} else {
						justep.Util.hint(data.message, {
							"type" : "danger",
							"position" : "middle"
						});
					}
				}
			});
		} else if(type === "ByItemList"){
			justep.Baas.sendRequest({
				"url" : "/erpscan/base/schedulePick",
				"action" : "getItemListByItemList",
				"async" : false,
				"params" : {
					"itemList" : this.itemList,
					"houseid" : this.houseid,
					"scheduleid" : this.scheduleid,
				},
				"success" : function(data) {
					if (data.message === "") {
						data2.loadData(data.itemTable,true);
						itemBatchConfigData.loadData(data.itemConfig,true);
						pdacommon.changegoodsnumber2(data2, me.comp("grid2"));

						me.setData2count();

						grid.refresh();

					} else {
						justep.Util.hint(data.message, {
							"type" : "danger",
							"position" : "middle"
						});
					}
				}
			});
		}
	};

	Model.prototype.configBtnClick = function(event,rowid){
		var me = this;
		var itemBatchConfigData = this.comp("itemBatchConfigData");
		var schedulePickData = this.comp("schedulePickData");
		var data2 = this.comp("data2");
		me.comp("itemConfigDialog").open({
			"data" : {
				"itemid" : rowid,
				"still_need" : data2.getRowByID(rowid).val("still_need"),
				"itemConfig" : itemBatchConfigData.getRowByID(rowid).row.config,
				"houseid" : schedulePickData.getValue("houseid"),
			},
			"src" : require.toUrl("./selectItemConfig.w"),
			"status" : justep.Browser.isPC ? "normal" : "maximize"
		});
	};

	Model.prototype.setData2count = function(itemid,config){
		var data2 = this.comp("data2");
		var itemBatchConfigData = this.comp("itemBatchConfigData");
		var me = this;

		// 按config获取count
		if (itemid === undefined || itemid === null ){
			data2.each(function(params) {
				var itemid = params.row.val("itemid");
				var config = itemBatchConfigData.getRowByID(itemid).row.config;

				var result = me.filtKey(config);
				var count = 0;
				for (var i = 0; i < result.length; i++){
					count += config[result[i]];
				}
				params.row.val("count",count);
			});
		} else if (itemBatchConfigData.getRowByID(itemid) !== null){
			var result = me.filtKey(config);
			var count = 0;
			for (var i = 0; i < result.length; i++) {
				count += config[result[i]];
			}
			data2.setValueByID("count", count, itemid);
		}
	};

	Model.prototype.filtKey = function(config){
		var unwantedKeys = ["value", "originalValue", "changed"];
		var result = Object.keys(config).filter(function(key) {
			return unwantedKeys.indexOf(key) === -1;
		});
		return result;
	};

	Model.prototype.itemConfigDialogReceive = function(event){
		var receivedData = JSON.parse(event.data);
		var config = receivedData.config;
		var itemid = receivedData.itemid;

		var itemBatchConfigData = this.comp("itemBatchConfigData");

		itemBatchConfigData.remove(itemBatchConfigData.getRowByID(itemid));
		itemBatchConfigData.add({"itemid" : itemid,"config" : config});

		this.setData2count(itemid,config);

		this.comp("grid2").refresh();

	};

	Model.prototype.itemBatchConfigDataBeforeRefresh = function(event){
		var sourcedata = this.comp("itemBatchConfigData");
		justep.Baas.sendRequest({
			"url" : "/erpscan/base/schedulePick",
			"action" : "queryItemBatchConfigData",
			"async" : false,
			"params" : {
				"mainid" : this.mainid,
				"houseid" : this.houseid,
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

	Model.prototype.itemDialogReceive = function(event){
		this.itemList = event.data;
		this.loadGrid2("ByItemList");
	};

	return Model;
});