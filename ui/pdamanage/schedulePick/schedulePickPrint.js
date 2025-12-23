define(function(require) {
	var $ = require("jquery");
	var justep = require("$UI/system/lib/justep");
	var mlfcommon = require("./../../js/mlfcommon");
	var pdacommon = require("./../../js/pdacommon");

	var Model = function() {
		this.callParent();
		// 默认入库列名表
		this.columns = [ {
			"colid" : "originalbill",
			"colname" : "原单号",
			"colnewname" : "原单号",
			"type" : 1
		}, {
			"colid" : "create_by",
			"colname" : "制单人",
			"colnewname" : "制单人",
			"type" : 1
		}, {
			"colid" : "orderid",
			"colname" : "单据编号",
			"colnewname" : "单据编号",
			"type" : 1
		}, {
			"colid" : "housename",
			"colname" : "仓库",
			"colnewname" : "仓库",
			"type" : 1
		}, {
			"colid" : "staffname",
			"colname" : "经手人",
			"colnewname" : "经手人",
			"type" : 1
		}, {
			"colid" : "operate_time",
			"colname" : "出库日期",
			"colnewname" : "出库日期",
			"type" : 1
		}, {
			"colid" : "customername",
			"colname" : "单位部门",
			"colnewname" : "单位部门",
			"type" : 1
		}, {
			"colid" : "remark",
			"colname" : "单据备注",
			"colnewname" : "单据备注",
			"type" : 1
		}, {
			"colid" : "mcount",
			"colname" : "总数量",
			"colnewname" : "总数量",
			"type" : 1
		}, {
			"colid" : "mtotal",
			"colname" : "总金额",
			"colnewname" : "总金额",
			"type" : 1
		}, {// change20210808
			"colid" : "signname1",
			"colname" : "确认签名1",
			"colnewname" : "",
			"type" : 1
		}, {
			"colid" : "signname2",
			"colname" : "确认签名2",
			"colnewname" : "",
			"type" : 1
		}, {
			"colid" : "goods_number",
			"colname" : "序号",
			"colnewname" : "序号",
			"type" : 2
		}, {
			"colid" : "codeid",
			"colname" : "商品编号",
			"colnewname" : "商品编号",
			"type" : 2
		}, {
			"colid" : "itemname",
			"colname" : "商品名称",
			"colnewname" : "商品名称",
			"type" : 2
		}, {
			"colid" : "batchno",
			"colname" : "批号",
			"colnewname" : "批号",
			"type" : 2
		}, {
			"colid" : "sformat",
			"colname" : "商品规格",
			"colnewname" : "商品规格",
			"type" : 2
		}, {
			"colid" : "barcode",
			"colname" : "商品码",
			"colnewname" : "商品码",
			"type" : 2
		}, {
			"colid" : "classname",
			"colname" : "商品分类",
			"colnewname" : "商品分类",
			"type" : 2
		}, {
			"colid" : "unit",
			"colname" : "单位",
			"colnewname" : "单位",
			"type" : 2
		}, {
			"colid" : "count",
			"colname" : "数量",
			"colnewname" : "数量",
			"type" : 2
		}, {
			"colid" : "counttounit1",
			"colname" : "辅助单位1",
			"colnewname" : "辅助单位1",
			"type" : 2
		}, {
			"colid" : "counttounit2",
			"colname" : "辅助单位2",
			"colnewname" : "辅助单位2",
			"type" : 2
		}, {
			"colid" : "counttounit3",
			"colname" : "辅助单位3",
			"colnewname" : "辅助单位3",
			"type" : 2
		}, {
			"colid" : "dremark",
			"colname" : "备注",
			"colnewname" : "备注",
			"type" : 2
		} ];
		this.allcount = 0;// 记录总条数
		this.countbit = 0;
		this.moneybit = 2;
		this.showprice = false;
	};

	Model.prototype.cutZero = function(value) {
		return pdacommon.cutZero(value);
	};
	// 打印
	Model.prototype.printBtnClick = function(event) {
		this.comp("printHtml1").print();

		localStorage.setItem("schedule_pick_print_page", this.comp("select1").val());

		var schedulePickData = this.comp("schedulePickData");
		// 保存打印次数及数据记录，每进入一次当打印一次
		pdacommon.saveprintdatalog(this.getParent().getParentModel().comp("userinfoData"), this.companyid, "schedule_pick", schedulePickData.getValue("bill_type"), schedulePickData.getValue("schedule_pick_id"),
				schedulePickData.getValue("orderid"), "");// otheroutrefreshBtn

	};
	// 关闭
	Model.prototype.backBtnClick = function(event) {
		this.comp("window").close();
	};
	Model.prototype.modelParamsReceive = function(event) {
		var schedulePickData = this.comp("schedulePickData");
		var schedulePickDetailData = this.comp("schedulePickDetailData");

		var companydata = this.getParent().getParentModel().comp("companyData");
		var columndata = this.comp("columndata");
		columndata.clear();

		this.moneybit = companydata.getValue("moneybit");
		this.countbit = companydata.getValue("countbit");

		// 2020-12-15 获取辅助单位设置 ，约定-3个辅助运算列的列名需为：原列名+tounit1、2、3的形式
		this.companyData = companydata;
		var option = [ {
			grid : "grid1", // grid表格xid
			needconvertcol : [ "count" ], // 加入需要换算的数量列的列名
			needconvertcolname : [ "" ], // 加入需要换算的数量列的名称
			needconvertcolnum : [ 20 ], // 打印页面加入需转换列的columns的数组位置
		} ];
		this.unitsetdata = pdacommon.createUnitOptionPrint(this, this.companyData, option, this.columns);
		// 获取辅助单位设置 ---end

		this.companyid = companydata.getValue("id");
		this.blankline = companydata.getValue("blankline");// 0-显示空白行 1-不显示空白行
		var company_logo = companydata.getValue("company_logo");

		if (company_logo === undefined || company_logo === null) {
			$(".imageshow").hide();
		} else {
			$(this.getElementByXid("image1")).attr("src", mlfcommon.getHeadImgUrl2(company_logo));
			var imageleft = companydata.getValue("imageleft");
			if (imageleft > 0) {
				$(".imageshow").css({
					"position" : "absolute",
					"left" : imageleft + "px"
				});
			} else {
				$(".imageshow").css({
					"margin-right" : "10px"
				});
			}
		}

		// 20201109 增加二维码显示控制
		var showqrcode = companydata.getValue("showqrcode");
		if (showqrcode === 1) {
			$(this.getElementByXid("qrcodediv")).hide();
		} else {
			$(this.getElementByXid("qrcodediv")).show();
		}
		
		var schedulePickPrint2ColumnSet = localStorage.getItem("schedulePickPrint2ColumnSet");// 存表格显示的列
		if (schedulePickPrint2ColumnSet !== undefined && schedulePickPrint2ColumnSet !== null) {
			columndata.loadData(JSON.parse(schedulePickPrint2ColumnSet));
		} else {
			columndata.loadData(this.columns);
		}

		var schedulePickShowMoneyPrint = localStorage.getItem("schedulePickShowMoneyPrint");// 存表格显示的列
		if (schedulePickShowMoneyPrint !== undefined && schedulePickShowMoneyPrint !== null) {
			this.comp("showmoneyprintCKB").val(schedulePickShowMoneyPrint);
		} else {
			schedulePickShowMoneyPrint = 0;
			this.comp("showmoneyprintCKB").val(0);
		}

		if (parseInt(schedulePickShowMoneyPrint) === 1) {
			this.hidePrintPrice = true;
		} else {
			this.hidePrintPrice = false;
		}

//		this.changePagetype();


		this.mainid = event.params.data.schedule_pick_id;
		schedulePickData.refreshData();
		schedulePickDetailData.refreshData();
		
		this.allcount = schedulePickDetailData.getTotal();

		var pdata = this.comp("pdata");
		var schedulePickPrintPageSize = localStorage.getItem("schedulePickPrintPageSize");// 存表格显示的列
		if (schedulePickPrintPageSize !== undefined && schedulePickPrintPageSize !== null) {
			var fpdata = JSON.parse(schedulePickPrintPageSize);
			pdata.setValue("type", fpdata.type === undefined ? "1" : fpdata.type);
			pdata.setValue("title", fpdata.title === undefined ? companydata.val("companyname") + "（排产领料单）" : fpdata.title);
			pdata.setValue("address", fpdata.address === undefined ? ((companydata.val("address") !== "" ? "地址：" + companydata.val("address") : "") + (companydata.val("companyphone") !== "" ? "  电话："
					+ companydata.val("companyphone") : "")) : fpdata.address);
			pdata.setValue("titlesize", fpdata.titlesize === undefined ? 15 : fpdata.titlesize);
			pdata.setValue("othersize", fpdata.othersize === undefined ? 14 : fpdata.othersize);
			pdata.setValue("maintitlesize", fpdata.maintitlesize === undefined ? 24 : fpdata.maintitlesize);
			pdata.setValue("staffname", fpdata.staffname === undefined ? 1 : fpdata.staffname);
			pdata.setValue("create_by", fpdata.create_by === undefined ? 1 : fpdata.create_by);
		} else {
			pdata.setValue("type", "1");
			pdata.setValue("title", companydata.val("companyname") + "（排产领料单）");
			pdata.setValue("address", (companydata.val("address") !== "" ? "地址：" + companydata.val("address") : "")
					+ (companydata.val("companyphone") !== "" ? "  电话：" + companydata.val("companyphone") : ""));
			pdata.setValue("titlesize", 15);
			pdata.setValue("othersize", 14);
			pdata.setValue("maintitlesize", 24);
			pdata.setValue("staffname", 1);
			pdata.setValue("create_by", 1);
		}
		this.changePagetype(pdata.getValue("type"));

		if (!this.showprice || this.hidePrintPrice) {
			$(this.getElementByXid("fcol22")).hide();
		} else {
			$(this.getElementByXid("fcol22")).show();
		}

	};

	// 根据配置显示列名
	Model.prototype.getNewColumn = function() {
		var grid1 = this.comp("grid1");
		// 根据配置显示列名
		this.comp("columndata").eachAll(function(params) {
			if (params.row.val("type") === 2) {
				var colid = params.row.val("colid");// dremark 明细的备注列
				grid1.setLabel((colid === "dremark" ? "remark" : colid), params.row.val("colnewname"));
			}
		});
	};

	// 获取主表设置列名
	Model.prototype.getColunmname = function(colid) {
		if (colid !== undefined && this.comp("columndata").count() > 0) {
			var rowdata = this.comp("columndata").find([ "colid" ], [ colid ]);
			if (rowdata.length > 0) {
				return ((rowdata[0].val("colnewname")).trim() === "" ? "" : rowdata[0].val("colnewname") + "：");
			} else {
				return "";
			}
		} else {
			return "";
		}
	};
	// 明细表单元格渲染
	Model.prototype.grid1CellRender = function(event) {
		if (event.colName === "imgurl" && event.rowID !== "" && event.rowID !== undefined && event.colVal !== undefined) {
			event.html = " <img src='" + mlfcommon.getItemImgUrl(event.colVal) + "' style='border-radius:50%;width:30px;height:30px;' onclick='{window.open(\"" + mlfcommon.getItemImgUrl(event.colVal)
					+ "\");}" + "' />";
		} else {
			if (event.colVal === undefined) {
				event.html = "";
			} else {
				event.html = "<div style='word-break:break-all;word-wrap:break-word; white-space:normal;'>" + event.colVal + "</div>";
			}
		}

		if (event.colName === 'total'   && event.colVal !== undefined) {
			 if (!this.showprice || this.hidePrintPrice) {
					event.html = "--";
				} else if (event.colVal === 0) {
					event.html = "";
				} 
		}
		if (event.colName === 'price'   && event.colVal !== undefined) {
			 if (!this.showprice || this.hidePrintPrice) {
					event.html = "--";
				} else if (event.colVal === 0) {
					event.html = "";
				} 
		}

		// 2020-12-24
		if (event.colName.indexOf('tounit') > -1 && event.row !== null) {
			var row = event.row;
			var type = event.colName.charAt(event.colName.length - 1);
			var countcolname = event.colName.substring(0, event.colName.indexOf("tounit"));
			if (this.unitsetdata["unitstate" + type] === 1 && row.val("unitstate" + type) === 1) {
				event.html = pdacommon.getConvertCount(row.val(countcolname), row.val("unit"), row.val("unitset" + type), this.unitsetdata.countbit);
				event.html = "<div style='word-break:break-all;word-wrap:break-word; white-space:normal;'>" + event.html + "</div>";
			}
		}
	};
	// 显示设置列别名
	Model.prototype.setBtnClick = function(event) {
		this.comp("setpopOver").show();
	};
	// 关闭设置列别名
	Model.prototype.colsebtnClick = function(event) {
		this.comp("setpopOver").hide();
	};
	// 恢复默认列名
	Model.prototype.retsetBtnClick = function(event) {
		var columndata = this.comp("columndata");
		columndata.clear();
		columndata.loadData(this.columns);
		var grid1 = this.comp("grid1");
		grid1.refresh();
		var property = this.comp("schedulePickData").getValue("iproperty");
		if (property !== undefined) {
			var propertyarr = property.split(";");
			var i = 0;
			for (i = 1; i <= 5; i++) {
				if (property.indexOf("property" + i) <= -1) {
					grid1.hideCol("property" + i);
				}
			}
			for (i = 0; i < propertyarr.length; i++) {
				var arr = propertyarr[i].split(",");
				if (arr[0] !== "") {
					grid1.setLabel(arr[0], arr[1]);
					columndata.newData({
						"defaultValues" : [ {
							"colid" : arr[0],
							"colname" : arr[1],
							"colnewname" : arr[1],
							"type" : 2
						} ]
					});
				}
			}
		}
		this.getNewColumn();
		localStorage.removeItem("schedulePickPrint2ColumnSet");
		localStorage.removeItem("schedulePickPrintPageSize");

		// change20210808
		var companydata = this.getParent().getParentModel().comp("companyData");
		var pdata = this.comp("pdata");
		pdata.setValue("type", "1");
		pdata.setValue("title", companydata.val("companyname") + "（排产领料单）");
		pdata.setValue("address", (companydata.val("address") !== "" ? "地址：" + companydata.val("address") : "")
				+ (companydata.val("companyphone") !== "" ? "  电话：" + companydata.val("companyphone") : ""));
		pdata.setValue("titlesize", 15);
		pdata.setValue("othersize", 14);
		pdata.setValue("maintitlesize", 24);
		pdata.setValue("staffname", 1);
		pdata.setValue("create_by", 1);

		localStorage.removeItem("schedulePickPrintPageCount");
		this.changePagetype("1");

	};
	// 保存设置列名
	Model.prototype.savesetBtnClick = function(event) {
		localStorage.setItem("schedulePickPrint2ColumnSet", JSON.stringify(this.comp("columndata").toJson({
			format : "simple"
		})));

		// change20210808
		var pdata = this.comp("pdata");
		var data = {
			type : pdata.getValue("type"),
			title : pdata.getValue("title"),
			titlesize : pdata.getValue("titlesize"),
			othersize : pdata.getValue("othersize"),
			maintitlesize : pdata.getValue("maintitlesize"),
			address : pdata.getValue("address"),
			staffname : pdata.getValue("staffname"),
			create_by : pdata.getValue("create_by")
		};

		localStorage.setItem("schedulePickPrintPageSize", JSON.stringify(data));// change20210808
		justep.Util.hint("保存成功", {
			"type" : "success",
			"position" : "middle"
		});
	};
	// 更改明细列名变更明细列名
	Model.prototype.input1Change = function(event) {
		var row = event.bindingContext.$object;
		if (row.val("type") === 2) {
			this.comp("grid1").setLabel((row.val("colid") === "dremark" ? "remark" : row.val("colid")), row.val("colnewname"));
		}
	};

	Model.prototype.schedulePickDetailDataAfterRefresh = function(event) {
		this.changeschedulePickDetailData(event.source);
		this.grid1Reload();
	};

	Model.prototype.changeschedulePickDetailData = function(schedulePickDetailData) {
		var count = schedulePickDetailData.count();
		var allcount = this.allcount;
		var page = this.comp("select1").val();
		var k = count;
		if ((parseInt(page) === 6 || allcount < 6) && count < 6 && this.blankline === 0) {
			schedulePickDetailData.last();
			var goods_number = schedulePickDetailData.getValue("goods_number");
			for (k = count + 1; k <= 6; k++) {
				schedulePickDetailData.newData({
					"index" : goods_number + k - count,
					"defaultValues" : [ {
						"detailid" : justep.UUID.createUUID(),
						"goods_number" : goods_number + k - count
					} ]
				});
			}

		}
		var t = allcount / page;
		var f = parseInt(allcount / page);
		var total = t > f ? f + 1 : f;

		var curpage = this.comp("pagination1").currentIndex + 1;
		curpage = (isNaN(curpage) || curpage === 0) ? total : curpage;
		$(this.getElementByXid("colpage")).html(curpage + "/" + total);

		this.comp("titleBar1").set({
			title : "排产领料单打印【纸张尺寸：" + this.comp("pagedata").find([ "type" ], [ this.comp("pdata").getValue("type") ])[0].val("typename") + " " + page + "条】"
		});
		localStorage.setItem("schedulePickPrintPageCount", page);
	};

	Model.prototype.pageradioGroupChange = function(event) {
		var type = event.value;
		localStorage.removeItem("schedulePickPrintPageCount");// change20210808
		this.changePagetype(type);
	};

	Model.prototype.changePagetype = function(type) {
		var pagesize = "";
		if (type === "1") {
			$(this.getElementByXid("contendiv")).css("width", "24.1cm");
			$(this.getElementByXid("contendiv")).css("height", "13.95cm");
			pagesize = "6";
		} else if (type === "2") {
			$(this.getElementByXid("contendiv")).css("width", "24.1cm");
			$(this.getElementByXid("contendiv")).css("height", "27.95cm");
			pagesize = "20";
		} else if (type === "3") {
			$(this.getElementByXid("contendiv")).css("width", "21cm");
			$(this.getElementByXid("contendiv")).css("height", "29.65cm");
			pagesize = "22";
		} else if (type === "4") {
			$(this.getElementByXid("contendiv")).css("width", "29.7cm");
			$(this.getElementByXid("contendiv")).css("height", "20.95cm");
			pagesize = "13";
		}

		var pagecount = localStorage.getItem("schedulePickPrintPageCount");
		if (pagecount !== undefined && pagecount !== null)
			pagesize = pagecount;
		this.comp("pagerLimitSelect1").select.val(pagesize);
		$(this.getElementByXid("select1")).trigger("change"); // 触发分页，一定要把detail的查询放到custom里
		this.comp("grid1").refresh();
	};
	// 重新加载表格
	Model.prototype.grid1Reload = function(event) {
		var grid1 = this.comp("grid1");
		var columndata = this.comp("columndata");
		var property = this.comp("schedulePickData").getValue("iproperty");
		if (property !== undefined) {
			var i = 0;
			for (i = 1; i <= 5; i++) {
				if (property.indexOf("property" + i) <= -1) {
					grid1.hideCol("property" + i);
				}
			}
			var propertyarr = property.split(";");
			for (i = 0; i < propertyarr.length; i++) {
				var arr = propertyarr[i].split(",");
				if (arr[0] !== "") {
					var rowdata = columndata.find([ "colid" ], [ arr[0] ]);
					if (rowdata.length === 0) {// 初始不存在新增
						columndata.newData({
							"defaultValues" : [ {
								"colid" : arr[0],
								"colname" : arr[1],
								"colnewname" : arr[1],
								"type" : 2
							} ]
						});
						grid1.setLabel(arr[0], arr[1]);
					}
				}
			}
		}
		this.getNewColumn();
	};

	Model.prototype.modelLoad = function(event) {
		var parent = this.getParentModel();
		if (parent === undefined) {
			window.open(require.toUrl("./../../index.w"), "_self");
		}

		this.showprice = true;
//		var permissiondata = this.getParentModel().getParent().comp("permissiondata");
//		// 查看单价
//		var crowdata = permissiondata.find([ "fvalue" ], [ "otheroutdata:showprice" ]);
//		if (crowdata.length > 0) {
//			this.showprice = true;
//		} else {
//			this.showprice = false;
//		}

	};

	// change20210808
	Model.prototype.finput4Change = function(event) {
		var fsize = parseInt(event.source.val());
		if (isNaN(fsize) || fsize <= 13 || fsize > 17) {
			event.source.val(14);
			fsize = 14;
		}
		var oh = fsize + 1;
		$(".fontcss").css({
			"font-size" : fsize + "px",
			"line-height" : oh + "px",
			"color" : "#000",
			"display" : "flex",
			"justify-content" : "left",
			"align-items" : "left",
			"padding-left" : "10px",
			"text-align" : "left",
			"word-break" : "break-all",
			"word-wrap" : "break-word",
			"white-space" : "normal"
		});

		$(".totalBigcss").css({
			"font-size" : fsize + "px",
			"line-height" : oh + "px",
			"font-weight" : "bold",
			"color" : "#000",
			"display" : "flex",
			"justify-content" : "center",
			"align-items" : "baseline"
		});

		$(".totalcss").css({
			"font-size" : fsize + "px",
			"line-height" : oh + "px",
			"color" : "#000",
			"display" : "flex",
			"justify-content" : "center",
			"align-items" : "baseline"
		});

	};

	// change20210808
	Model.prototype.finput3Change = function(event) {
		var fsize = parseInt(event.source.val());
		if (isNaN(fsize) || fsize <= 10 || fsize > 17) {
			event.source.val(13);
			fsize = 13;
		}
		var oh = fsize + 1;

		var $root = $(this.getRootNode());
		var grid = $root.find(".x-grid");
		var ths = $(grid[0]).find("th").find("div");
		for (var k = 0; k < ths.size(); k++) {
			$(ths[k]).css({
				"font-size" : fsize + "px",
				"line-height" : oh + "px",
				"padding" : "0px",
				"margin" : "0px",
				"height" : "auto",
				"width" : "auto",
				"word-wrap" : "break-word",
				"white-space" : "normal",
				"display" : "flex",
				/* 实现垂直居中 */
				"align-items" : "center",
				/* 实现水平居中 */
				"justify-content" : "center"

			});
		}

		$(this.getElementByXid("grid1")).css({
			"font-size" : fsize + "px",
			"line-height" : oh + "px"
		});

		ths = $(grid[0]).find(".footrow").find("td");
		for (k = 0; k < ths.size(); k++) {
			$(ths[k]).css({
				"font-size" : fsize + "px",
				"line-height" : oh + "px",
				"padding" : "2px"
			});
		}

	};

	Model.prototype.titleinputChange = function(event) {
		var fsize = parseInt(event.source.val());
		if (isNaN(fsize) || fsize < 14 || fsize > 30) {
			event.source.val(24);
			fsize = 24;
		}

		$(".titlecss").css({
			"font-size" : fsize + "px",
			"color" : "#000",
			"display" : "flex",
			"justify-content" : "center",
			"align-items" : "center",
			"padding-bottom" : "0px",
			"margin-bottom" : "0px"
		});

	};

	Model.prototype.showmoneyprintCKBChange = function(event) {
		localStorage.setItem("schedulePickShowMoneyPrint", event.value);
		if (parseInt(event.value) === 1) {
			this.hidePrintPrice = true;
		} else {
			this.hidePrintPrice = false;
		}

		if (this.showprice) {
			$(this.getElementByXid("fcol22")).show();
			this.comp("grid1").setFooterData({
				"total" : parseFloat((this.comp("schedulePickDetailData").sum("total")).toFixed(this.moneybit))
			});
		}

		if (!this.showprice || this.hidePrintPrice)
			$(this.getElementByXid("fcol22")).hide();

		this.comp("grid1").refresh(false);
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

	Model.prototype.schedulePickDetailDataCustomRefresh = function(event){
		var sourcedata = this.comp("schedulePickDetailData");
		var grid = this.comp("grid1");
		justep.Baas.sendRequest({
			"url" : "/erpscan/base/schedulePick",
			"action" : "querySchedulePickDetailData",
			"async" : false,
			"params" : {
				"mainid" : this.mainid,
				"offset" : sourcedata.getOffset(),
				"limit" : sourcedata.limit,
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