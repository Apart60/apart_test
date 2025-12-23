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
			"colid" : "billno",
			"colname" : "工单编号",
			"colnewname" : "工单编号",
			"type" : 1
		}, {
			"colid" : "mcoedid",
			"colname" : "商品编号",
			"colnewname" : "商品编号",
			"type" : 1
		}, {
			"colid" : "msformat",
			"colname" : "商品规格",
			"colnewname" : "商品规格",
			"type" : 1
		}, {
			"colid" : "mitemname",
			"colname" : "商品名称",
			"colnewname" : "商品名称",
			"type" : 1
		}, {
			"colid" : "staffname",
			"colname" : "经手人",
			"colnewname" : "经手人",
			"type" : 1
		}, {
			"colid" : "operate_time",
			"colname" : "领用日期",
			"colnewname" : "领用日期",
			"type" : 1
		}, {
			"colid" : "customername",
			"colname" : "领用部门",
			"colnewname" : "领用部门",
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
		}, {// change20210808
			"colid" : "signname",
			"colname" : "确认签名",
			"colnewname" : "",
			"type" : 1
		}, {
			"colid" : "schedule_pick_billno",
			"colname" : "关联排产领料单",
			"colnewname" : "关联排产领料单",
			"type" : 1
		}, {
			"colid" : "goods_number",
			"colname" : "序号",
			"colnewname" : "序号",
			"type" : 2
		}, {
			"colid" : "houseid",
			"colname" : "仓库",
			"colnewname" : "仓库",
			"type" : 2
		}, {
			"colid" : "worksheetbillno",
			"colname" : "关联工单号",
			"colnewname" : "关联工单号",
			"type" : 2
		}, {
			"colid" : "order_id",
			"colname" : "关联订单号",
			"colnewname" : "关联订单号",
			"type" : 2
		}, {
			"colid" : "pcodeid",
			"colname" : "产品信息",
			"colnewname" : "产品信息",
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
			"colid" : "price",
			"colname" : "单价",
			"colnewname" : "单价",
			"type" : 2
		}, {
			"colid" : "total",
			"colname" : "金额",
			"colnewname" : "金额",
			"type" : 2
		}, {
			"colid" : "dremark",
			"colname" : "备注",
			"colnewname" : "备注",
			"type" : 2
		}, {
			"colid" : "qcrode",
			"colname" : "二维码",
			"colnewname" : "二维码",
			"type" : 2
		} ];
		this.allcount = 0;// 记录总条数
		this.countbit = 0;
		this.moneybit = 2;
	};

	Model.prototype.cutZero = function(value) {
		return pdacommon.cutZero(value);
	};
	// 打印
	Model.prototype.printBtnClick = function(event) {
		this.comp("printHtml1").print();

		localStorage.setItem("prodrequisitionbackprintpagecount", this.comp("select1").val());

		var prodrequisitionData = this.comp("prodrequisitionData");

		// 保存打印次数及数据记录，每进入一次当打印一次
		pdacommon.saveprintdatalog(this.getParent().getParentModel().comp("userinfoData"), this.companyid, "prodrequisition", prodrequisitionData.getValue("bill_type"), prodrequisitionData
				.getValue("prodrequisitionid"), prodrequisitionData.getValue("orderid"), "");// prodrequisitionrefreshBtn

	};
	// 关闭
	Model.prototype.backBtnClick = function(event) {
		this.comp("window").close();
	};
	Model.prototype.modelParamsReceive = function(event) {
		var prodrequisitionData = this.comp("prodrequisitionData");
		var prodrequisitiondetailData = this.comp("prodrequisitiondetailData");

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
			needconvertcolnum : [ 25 ], // 打印页面加入需转换列的columns的数组位置
		} ];
		this.unitsetdata = pdacommon.createUnitOptionPrint(this, this.companyData, option, this.columns);
		// 获取辅助单位设置 ---end

		this.companyid = companydata.getValue("id");
		this.blankline = companydata.getValue("blankline");// 0-显示空白行 1-不显示空白行
		var company_logo = companydata.getValue("company_logo");

		if (company_logo === undefined || company_logo === null) {
			$(".imageshow").hide();
		} else {
			$(this.getElementByXid("image2")).attr("src", mlfcommon.getHeadImgUrl2(company_logo));
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

		var prodrequisitionprint3columnset = localStorage.getItem("prodrequisitionprint3columnset");// 存表格显示的列
		if (prodrequisitionprint3columnset !== undefined && prodrequisitionprint3columnset !== null) {
			columndata.loadData(JSON.parse(prodrequisitionprint3columnset));
		} else {
			columndata.loadData(this.columns);
		}

		var storeoutShowmoneyprint = localStorage.getItem("prodShowmoneyprint");// 存表格显示的列
		if (storeoutShowmoneyprint !== undefined && storeoutShowmoneyprint !== null) {
			this.comp("showmoneyprintCKB").val(storeoutShowmoneyprint);
		} else {
			storeoutShowmoneyprint = 0;
			this.comp("showmoneyprintCKB").val(0);
		}

		if (parseInt(storeoutShowmoneyprint) === 1) {
			this.hidePrintPrice = true;
		} else {
			this.hidePrintPrice = false;
		}

		this.changePagetype();

		this.allcount = event.params.data.allcount;
		this.mainid = event.params.data.mainid;
		console.log(this.comp("prodrequisitionData").count(), 111);
		prodrequisitionData.clear();
		prodrequisitionData.setFilter("filter1", "prodrequisitionid='" + this.mainid + "'");
		prodrequisitionData.refreshData();
		console.log(this.comp("prodrequisitionData").count(), 111);
		prodrequisitionData.first();
		// prodrequisitionData.setValue("companyname",
		// companydata.val("companyname"));
		// prodrequisitionData.setValue("companyaddress",
		// companydata.val("address"));
		// prodrequisitionData.setValue("compnayphone",
		// companydata.val("companyphone"));
		prodrequisitiondetailData.clear();
		prodrequisitiondetailData.filters.setVar("prodrequisitionid", this.mainid);

		var pdata = this.comp("pdata");
		var prodrequisitionprintpagesize = localStorage.getItem("prodrequisitionprintpagesize");// 存表格显示的列
		if (prodrequisitionprintpagesize !== undefined && prodrequisitionprintpagesize !== null) {
			var fpdata = JSON.parse(prodrequisitionprintpagesize);
			pdata.setValue("type", fpdata.type === undefined ? "1" : fpdata.type);
			pdata.setValue("title", fpdata.title === undefined ? companydata.val("companyname") + "（生产领用单）" : fpdata.title);
			pdata.setValue("address", fpdata.address === undefined ? ((companydata.val("address") !== "" ? "地址：" + companydata.val("address") : "") + (companydata.val("companyphone") !== "" ? "  电话："
					+ companydata.val("companyphone") : "")) : fpdata.address);
			pdata.setValue("titlesize", fpdata.titlesize === undefined ? 15 : fpdata.titlesize);
			pdata.setValue("othersize", fpdata.othersize === undefined ? 14 : fpdata.othersize);
			pdata.setValue("maintitlesize", fpdata.maintitlesize === undefined ? 24 : fpdata.maintitlesize);
			pdata.setValue("staffname", fpdata.staffname === undefined ? 1 : fpdata.staffname);
			pdata.setValue("create_by", fpdata.create_by === undefined ? 1 : fpdata.create_by);
			pdata.setValue("showqrcode", fpdata.showqrcode === undefined ? 0 : fpdata.showqrcode);
		} else {
			pdata.setValue("type", "1");
			pdata.setValue("title", companydata.val("companyname") + "（生产领用单）");
			pdata.setValue("address", (companydata.val("address") !== "" ? "地址：" + companydata.val("address") : "")
					+ (companydata.val("companyphone") !== "" ? "  电话：" + companydata.val("companyphone") : ""));
			pdata.setValue("titlesize", 15);
			pdata.setValue("othersize", 14);
			pdata.setValue("maintitlesize", 24);
			pdata.setValue("staffname", 1);
			pdata.setValue("create_by", 1);
			pdata.setValue("showqrcode", 0);
		}
		this.changePagetype(pdata.getValue("type"));
		this.getsize();

		var grid1 = this.comp('grid1');
		if (event.params.data.showbillno === false) {
			grid1.hideCol("worksheetbillno");
			grid1.hideCol("order_id");
			grid1.hideCol("pcodeid");
		}

		if (prodrequisitionData.getValue("worksheetid") !== "") {
			$(this.getElementByXid("row7")).show();
			$(this.getElementByXid("col38")).show();
		} else {
			$(this.getElementByXid("row7")).hide();
			$(this.getElementByXid("col38")).hide();
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
		} else if (event.colName === "worksheetbillno" && event.row !== null) {
			if (event.colVal !== "" && event.colVal !== undefined) {
				event.html = "<div style='word-break:break-all;word-wrap:break-word; white-space:normal;'> " + event.colVal + "</div>";
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

		} else if (event.colName === "qcrode" && event.row !== null) {

			event.html = "<div align='center' style='padding:4px 3px;' id='pd" + event.row.val("detailid") + "'></div>";

		} else {
			if (event.colVal === undefined) {
				event.html = "";
			} else {
				event.html = "<div style='word-break:break-all;word-wrap:break-word; white-space:normal;'>" + event.colVal + "</div>";
			}
		}

		if ((event.colName === "total" || event.colName === "price") && event.colVal !== undefined) {
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
		var property = this.comp("prodrequisitionData").getValue("iproperty");
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
		localStorage.removeItem("prodrequisitionprint3columnset");
		localStorage.removeItem("prodrequisitionprintpagesize");

		// change20210808
		var companydata = this.getParent().getParentModel().comp("companyData");
		var pdata = this.comp("pdata");
		pdata.setValue("type", "1");
		pdata.setValue("title", companydata.val("companyname") + "（生产领用单）");
		pdata.setValue("address", (companydata.val("address") !== "" ? "地址：" + companydata.val("address") : "")
				+ (companydata.val("companyphone") !== "" ? "  电话：" + companydata.val("companyphone") : ""));
		pdata.setValue("titlesize", 15);
		pdata.setValue("othersize", 14);
		pdata.setValue("maintitlesize", 24);
		pdata.setValue("staffname", 1);
		pdata.setValue("create_by", 1);
		pdata.setValue("showqrcode", 0);

		if (this.comp("prodrequisitionData").getValue("worksheetid") !== "") {
			$(this.getElementByXid("row7")).show();
			$(this.getElementByXid("col38")).show();
		} else {
			$(this.getElementByXid("row7")).hide();
			$(this.getElementByXid("col38")).hide();
		}

		localStorage.removeItem("prodrequisitionbackprintpagecount");
		this.changePagetype("1");
		this.getsize();
	};
	// 保存设置列名
	Model.prototype.savesetBtnClick = function(event) {
		localStorage.setItem("prodrequisitionprint3columnset", JSON.stringify(this.comp("columndata").toJson({
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
			create_by : pdata.getValue("create_by"),
			showqrcode : pdata.getValue("showqrcode")
		};

		localStorage.setItem("prodrequisitionprintpagesize", JSON.stringify(data));// change20210808
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

	Model.prototype.prodrequisitiondetailDataAfterRefresh = function(event) {
		this.changeprodrequisitiondetailData(event.source);
		this.grid1Reload();
		this.getQrodeData();
	};

	Model.prototype.getQrodeData = function(event) {
		var needData = this.comp("prodrequisitiondetailData");
		needData.first();
		needData.each(function(params) {
			if (params.row.val("barcode") !== "") {
				var parentNode = document.getElementById("pd" + params.row.val("detailid"));
				var flag = {
					"text" : params.row.val("barcode"),
					"colorDark" : "#000000",
					"colorLight" : "#ffffff",
					"width" : 40,
					"height" : 40,
					"correctLevel" : QRCode.CorrectLevel.M
				};
				new QRCode(parentNode, flag);
			}
		});
	};

	Model.prototype.changeprodrequisitiondetailData = function(prodrequisitiondetailData) {
		var count = prodrequisitiondetailData.count();
		var allcount = this.allcount;
		var page = this.comp("select1").val();
		var k = count;
		if ((parseInt(page) === 6 || allcount < 6) && count < 6 && this.blankline === 0) {
			prodrequisitiondetailData.last();
			var goods_number = prodrequisitiondetailData.getValue("goods_number");
			for (k = count + 1; k <= 6; k++) {
				prodrequisitiondetailData.newData({
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
		curpage = curpage === 0 ? 1 : curpage;
		$(this.getElementByXid("colpage")).html(curpage + "/" + total);

		// change20210808
		this.comp("titleBar1").set({
			title : "生产领用单打印【纸张尺寸：" + this.comp("pagedata").find([ "type" ], [ this.comp("pdata").getValue("type") ])[0].val("typename") + " " + page + "条】"
		});
		localStorage.setItem("prodrequisitionprintpagecount", page);
		// change20210808
	};

	Model.prototype.pageradioGroupChange = function(event) {
		var type = event.value;
		localStorage.removeItem("prodrequisitionprintpagecount");// change20210808
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

		var pagecount = localStorage.getItem("prodrequisitionprintpagecount");
		if (pagecount !== undefined && pagecount !== null)
			pagesize = pagecount;

		this.comp("pagerLimitSelect1").select.val(pagesize);
		$(this.getElementByXid("select1")).trigger("change"); // 触发分页

		this.comp("grid1").refresh();
	};
	// 重新加载表格
	Model.prototype.grid1Reload = function(event) {
		var grid1 = this.comp("grid1");
		var columndata = this.comp("columndata");
		var property = this.comp("prodrequisitionData").getValue("iproperty");
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
		var permissiondata = this.getParent().getParentModel().comp("permissiondata");
		var crowdata = permissiondata.find([ "fvalue" ], [ "prodrequisitiondata:showprice" ]);
		if (crowdata.length > 0)
			this.showprice = true;// 单价显示
	};

	Model.prototype.imgdivClick = function(event) {

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
			event.source.val(14);
			fsize = 14;
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
		localStorage.setItem("prodShowmoneyprint", event.value);
		if (parseInt(event.value) === 1) {
			this.hidePrintPrice = true;
		} else {
			this.hidePrintPrice = false;
		}

		if (this.showprice) {
			this.comp("grid1").setFooterData({
				"total" : parseFloat((this.comp("prodrequisitiondetailData").sum("total")).toFixed(this.moneybit))
			});
		}

		this.comp("grid1").refresh(false);
	};

	Model.prototype.checkbox3Change = function(event) {
		this.comp("pdata").setValue("showqrcode", event.checked ? 1 : 0);
		this.getsize();
	};

	Model.prototype.getsize = function() {
		var showqrcode = this.comp("pdata").getValue("showqrcode");
		if (showqrcode === 1) {
			this.comp("grid1").showCol("qcrode");
			
		} else {
			this.comp("grid1").hideCol("qcrode");
		}

	};

	return Model;
});