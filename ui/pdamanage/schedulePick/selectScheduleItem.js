define(function(require) {
	var $ = require("jquery");
	var justep = require("$UI/system/lib/justep");
	var mlfcommon = require("./../../js/mlfcommon");
	var pdacommon = require("./../../js/pdacommon");
	require("css!./../css/page").load();

	var Model = function() {
		this.callParent();
		this.companyid = "";
		this.loginuserid = "";
		this.propertycondition = "";

	};

	Model.prototype.cancelbtnClick = function(event) {
		this.comp("window").close();
	};

	Model.prototype.modelLoad = function(event) {
		var parent = this.getParentModel();
		if (parent === undefined) {
			window.open(require.toUrl("./../index.w"), "_self");
		}
	};

	Model.prototype.modelParamsReceive = function(event) {

		mlfcommon.setIOSTop($($(document.getElementById(event.params.data.dialogid)).find(".x-dialog")[0]));

		var houseid = event.params.data.houseid;
		var housename = event.params.data.housename;

		this.houseid = houseid;
		this.scheduleid = event.params.data.scheduleid;
		this.itemList = event.params.data.itemList;
		this.schedule_pick_id = event.params.data.schedule_pick_id;

		var loginUserData = this.getParentModel().getParentModel().comp("userinfoData");
		this.companyid = loginUserData.val("companyid");
		this.loginuserid = loginUserData.val("userid");

		// 2020-12-15 获取辅助单位设置 ，约定-3个辅助运算列的列名需为：原列名+tounit1、2、3的形式
		this.companyData = this.getParent().companyData;
		var option = [ {
			grid : "selectitemallgrid", // grid表格xid
			needconvertcol : [ "count" ],// 加入需要换算的数量列的列名
			needconvertcolname : [ "" ], // 加入需要换算的数量列的名称
		} ];
		this.unitsetdata = pdacommon.createUnitOption(this, this.companyData, option);
		// 获取辅助单位设置 ---end

		this.salesoutset = this.companyData.val("salesoutset");

		this.countbit = this.companyData.val("countbit");

		this.comp("titleBar1").set({
			"title" : "选择领料的商品【"+ housename + "】"
		});


		// var iteminfoData = this.comp("iteminfoData");
		this.companyid = this.getParent().companyid;

		var itemclassData = this.comp("itemclassData");
		itemclassData.clear();
		itemclassData.filters.setVar("companyid", this.companyid);
		itemclassData.refreshData();

		this.clearinputdata();

		this.classfilter = "";
		this.searchFilter = "";

		var me = this;
		var itempropertyData = this.getParent().comp("itempropertyData");
		var selectitemallgrid = this.comp("selectitemallgrid");
		if (itempropertyData.count() > 0) {// 从父页获取商品自定义属性

			itempropertyData.eachAll(function(params) {
				var colname = params.row.val("propertyname");
				if (colname !== undefined) {
					selectitemallgrid.showCol(colname);
					selectitemallgrid.setLabel(colname, params.row.val("propertyshow"));
					me.propertycondition = me.propertycondition + (me.propertycondition === "" ? "" : ",") + params.row.val("propertyname");
				}
			});
		}

		$(this.getElementByXid("nameinput")).click(function(params) {
			$(this).select();
		});

		$(this.getElementByXid("sformatinput")).click(function(params) {
			$(this).select();
		});

		$(this.getElementByXid("searchcontent")).click(function(params) {
			$(this).select();
		});


		$(this.getElementByXid("nameinput")).focus();

		// if(justep.Browser.isIOS)
		if (justep.Browser.isIOS) {
			$(this.getElementByXid("pagerBar1")).bind('DOMNodeInserted', function(e) {
				var heigh = $(me.getElementByXid("selectitemallgrid")).css("height").replace("px", "");
				var heightgrid2 = $(me.getElementByXid("grid2")).css("height").replace("px", "");
				var gridheight = parseInt(heigh) + parseInt(heightgrid2) + 750;
				if (me.cheight !== gridheight) {
					window.scrollTo(0, 0);
					$(me.getElementByXid("content1")).css({
						"height" : gridheight + "px"
					});
					me.cheight = gridheight;
				}
			});
		}
		
		this.loadRecord();

	};

	Model.prototype.itemclassDataAfterRefresh = function(event) {

		var classdata = [];// 获取树型数据源
		classdata.push({
			"classid" : '1',
			"companyid" : this.companyid,
			"classname" : "无分类",
			"parentid" : ''
		});
		event.source.eachAll(function(params) {
			var objdata = {
					"classid" : params.row.val("classid"),
					"companyid" : params.row.val("companyid"),
					"classname" : params.row.val("classname"),
					"parentid" : params.row.val("parentid")
			};
			classdata.push(objdata);
		});

		this.getTreeData(classdata);

		event.source.clear();
	};

	// 刷新商品数据源与分类数据源
	Model.prototype.refreshBtnClick = function(event) {
		this.clearinputdata();

		this.classfilter = "";
		this.searchFilter = "";

		var pagedata = this.comp("pagedata");
		pagedata.setValue("currentpage", 1);
		pagedata.setValue("offset", 0);
		this.pageInit(0);
		this.loadRecord();

	};

	Model.prototype.clearinputdata = function() {
		this.comp("searchcontent").val("");
		this.comp("nameinput").val("");
		this.comp("sformatinput").val("");
	};
	// 查找商品信息
	//here
	Model.prototype.findBtnClick = function(event) {
		// var iteminfoData = this.comp("iteminfoData");
		this.searchcontent = pdacommon.transformSpecialInfo01(this.comp("searchcontent").val().trim());
		this.nameinput = pdacommon.transformSpecialInfo01(this.comp("nameinput").val().trim());
		this.sformatinput = pdacommon.transformSpecialInfo01(this.comp("sformatinput").val().trim());

//		var countsql = "";
//		if (scount !== "") {
//			countsql = " k.count>=" + scount + "  ";
//		}
//		if (ecount !== "") {
//			countsql = countsql === "" ? " k.count<= " + ecount + "  " : countsql + " and  k.count<= " + ecount + "  ";
//		}
//
//		var sql = (batchnoselect === "" || storeselect === "2" ? "" : (batchnoselect === "1" ? " k.batchno='' and " : " k.batchno <>'' and "))
//		+ (nameinput === "" ? "" : " (i.codeid like '%" + nameinput + "%' or i.itemname like '%" + nameinput + "%') and ")
//		+ (sformatinput === "" ? "" : " (i.sformat like '%" + sformatinput + (storeselect === "2" ? "" : "%' or k.batchno like '%" + sformatinput) + "%' ) and ")
//		+ (housesql === "" ? "" : housesql + " and ");
//
//		// iteminfoData.setFilter("searchFilter", sql
//		// + (searchcontent === "" ? "1=1" : "(lower(i.mcode) like '%" +
//		// searchcontent.toLowerCase() + "%' or i.barcode like '%" +
//		// searchcontent + "%' or i.remark like '%" + searchcontent + "%'"
//		// + pcondition + ")"));
//		//
//		// iteminfoData.refreshData();
//
//		this.searchFilter = sql
//		+ (countsql === "" ? "" : countsql + " and ")
//		+ (searchcontent === "" ? "1=1" : "(lower(i.mcode) like '%" + searchcontent.toLowerCase() + "%' or i.barcode like '%" + searchcontent + "%' or i.remark like '%" + searchcontent + "%'"
//				+ pcondition + ")");

		var pagedata = this.comp("pagedata");
		pagedata.setValue("currentpage", 1);
		pagedata.setValue("offset", 0);

		this.loadRecord();
	};
	// type=1 选择并关闭
	Model.prototype.okcloseBtnClick = function(event) {
		event.source.set({
			disabled : true
		});
		this.selectItem(1);
		event.source.set({
			disabled : false
		});
	};
	Model.prototype.selectItem = function(type) {
		var rows = this.comp("selectitemallgrid").getCheckedRows();
		if (rows.length === 0) {
			justep.Util.hint("请选择商品", {
				"type" : "danger",
				"position" : "middle"
			});
			return;
		}
			var itemarr = [];
			for (var a = 0; a < rows.length; a++) {
				itemarr.push(rows[a].val("itemid"));
			}
			this.owner.send(itemarr);
			justep.Util.hint("已选择 " + rows.length + " 条商品", {
				"type" : "success",
				"position" : "middle",
				"delay" : 1000
			});
				this.comp("window").close();
	};

	Model.prototype.grid1RowChecked = function(event) {
		if (event.checked) {
			this.comp('selectitemallgrid').setRowCss(event.rowID, {
				background : '#FFFFB5'
			});
		} else {
			this.comp('selectitemallgrid').setRowCss(event.rowID, {
				background : 'white'
			});
		}

		var len = this.comp('selectitemallgrid').getCheckeds().length;
		this.comp("okcloseBtn").set({
			"label" : " 选择并关闭" + (len > 0 ? "（" + this.comp('selectitemallgrid').getCheckeds().length + "）" : "")
		});
	};

	Model.prototype.grid1CellRender = function(event) {
		if (event.colName === "imgurl") {
			event.html = " <img src='" + mlfcommon.getItemImgUrl(event.colVal) + "' style='border-radius:50%;width:30px;height:30px;' onclick='{window.open(\"" + mlfcommon.getItemImgUrl(event.colVal)
			+ "\");}" + "' />";

		} else if (event.colName === "classid") {
			var rowdata = this.comp("classData").find([ "classid" ], [ event.colVal ]);
			if (rowdata.length > 0) {
				event.html = "<div style='word-break:break-all;word-wrap:break-word; white-space:normal;'>" + rowdata[0].val("classname") + "</div>";

			} else {
				event.html = "";
			}
		} else if (event.colName === "count") {
			if (event.colVal > 0) {
				event.html = "<font style='color:blue;'>" + event.colVal + "</font>";
			} else if (event.colVal === 0) {
				event.html = "";
			} else {
				event.html = "<font style='color:red;'>" + event.colVal + "</font>";
			}
		} else if (event.colName === "checkout_count") {
			if (event.colVal !== 0) {
				event.html = "<font style='color:orange;'>" + event.colVal + "</font>";
			} else if (event.colVal === 0) {
				event.html = "";
			}
		} else if (("inprice,newcostprice,outprice,outprice1,outprice2,outprice3,outprice4,outprice5,").indexOf(event.colName + ",") > -1) {
			if (event.colVal === 0)
				event.html = "";
		} else if (event.colName.indexOf('tounit') > -1 && event.row !== null) { // 2020-12-21
			// 多单位功能
			var row = event.row;
			var type = event.colName.charAt(event.colName.length - 1);
			var countcolname = event.colName.substring(0, event.colName.indexOf("tounit"));
			if (this.unitsetdata["unitstate" + type] === 1 && row.val("unitstate" + type) === 1) {
				event.html = pdacommon.getConvertCount(row.val(countcolname), row.val("unit"), row.val("unitset" + type), this.unitsetdata.countbit);
				event.html = "<div style='word-break:break-all;word-wrap:break-word; white-space:normal;'>" + event.html + "</div>";
			}
		} else {

			event.html = "<div style='word-break:break-all;word-wrap:break-word; white-space:normal;'>" + event.colVal + "</div>";
		}

	};

	// 点击分类行更新商品数据
	Model.prototype.grid2RowClick = function(event) {
		this.classcheckboxChange();
	};
	// 是否包含下级 更新商品数据
	Model.prototype.classcheckboxChange = function() {
		var check = this.comp("classcheckbox").val();
		var row = this.comp("classData").getCurrentRow();
		if (row !== null && row !== undefined) {
			var svalue = row.val("classid");
			var classids = "";
			if (svalue !== "" && svalue !== "1") {
				classids = this.getClassChilds(svalue, check);
			}
			// var iteminfoData = this.comp("iteminfoData");
			// iteminfoData.setFilter("classfilter", (svalue === "" ? "1=1" :
			// (svalue === "1" ? "i.classid=''" : "i.classid in (" + classids +
			// ")")));
			// iteminfoData.refreshData();
			//			
			this.classids = (svalue === "" ? "" : (svalue === "1" ? "" : classids));
		} else {
			this.classids = "";
		}
		var pagedata = this.comp("pagedata");
		pagedata.setValue("currentpage", 1);
		pagedata.setValue("offset", 0);

		this.loadRecord();
	};

	// 读取当前分类的记录或含子级记录
	Model.prototype.getClassChilds = function(classid, showchild) {
		var itemclassData = this.comp("classData");
		var classids = "'" + classid + "'";
		function each(classid) {
			var rowdata = itemclassData.find([ 'parentid' ], [ classid ]);
			if (rowdata.length > 0) {
				for (var i = 0; i < rowdata.length; i++) {
					each(rowdata[i].val("classid"));
					classids = classids + (classids === "" ? "" : ",") + "'" + rowdata[i].val("classid") + "'";
				}
			}
		}
		if (showchild === "1") {// 显示子级执行
			each(classid);
		} else {
			classids = "'" + classid + "'";
		}
		return classids;
	};

	// 读取分类权型结构数据
	Model.prototype.getTreeData = function(classdata) {

		function fn(data, classid) {
			var result = [], temp;
			for (var i = 0; i < data.length; i++) {
				if (data[i].parentid === classid) {
					var obj = {
							"classid" : data[i].classid,
							"companyid" : data[i].companyid,
							"classname" : data[i].classname,
							"parentid" : data[i].parentid
					};
					temp = fn(data, data[i].classid);
					if (temp.length > 0) {
						obj.rows = temp;
					}
					result.push(obj);
				}
			}
			return result;
		}
		var arr = fn(classdata, "");
		var classjson = {
				"rows" : [ {
					"classid" : "",
					"companyid" : this.companyid,
					"classname" : "所有分类",
					"parentid" : {},
					"rows" : arr
				} ]
		};
		var classData = this.comp("classData");
		classData.clear();
		classData.loadData(classjson, true);

		this.comp("grid2").refresh(false);

		if (justep.Browser.isPC) {
			this.comp('grid2').expandRow("");
			this.comp("grid2").setSelection("");
		}
	};

	Model.prototype.showclassbtnClick = function(event) {
		var filterDetail = $(this.getElementByXid('col1'));
		if (filterDetail.is(':hidden') === false) {
			filterDetail.hide();
		} else {
			filterDetail.show();
		}
		this.comp("selectitemallgrid").refresh();
	};

	Model.prototype.grid2CellRender = function(event) {
		event.html = "<div style='word-break:break-all;word-wrap:break-word; white-space:normal;'>" + event.colVal + "</div>";
	};

	// 计算页数，创建按钮
	Model.prototype.pageInit = function(total) {
		var pagedata = this.comp("pagedata");
		var pagenumdata = this.comp("pagenumdata");

		var limit = pagedata.getValue("limit");
		var currentpage = pagedata.getValue("currentpage");
		var pagecount = Math.ceil(total / limit);

		var currentcount = 0;
		var start = 0;
		var end = 0;
		if (total > 0) {
			currentcount = (currentpage === pagecount ? (total % limit === 0 ? limit : total % limit) : limit);
			start = (currentpage - 1) * limit + 1;
			end = (currentpage - 1) * limit + currentcount;
		}
		pagedata.setValue("pagecount", pagecount);
		pagedata.setValue("total", total);
		pagedata.setValue("currentcount", currentcount);
		pagedata.setValue("start", start);
		pagedata.setValue("end", end);
		pagenumdata.clear();

		if (currentpage <= 7) {
			for (var i = 1; i <= pagecount; i++) {
				pagenumdata.newData({
					"defaultValues" : [ {
						"num" : i,
					} ]
				});
			}
		} else if (currentpage > 7) {
			start = currentpage - 3;
			end = currentpage + 3;
			if (currentpage - 4 <= 0) {
				start = 1;
				end = 7;
			} else if (currentpage + 3 > pagecount) {
				start = pagecount - 6;
				end = pagecount;
			} else {
				start = currentpage - 3;
				end = currentpage + 3;
			}
			for (var j = start; j <= end; j++) {
				pagenumdata.newData({
					"defaultValues" : [ {
						"num" : j,
					} ]
				});
			}
		}

	};

	// 分页数量修改
	Model.prototype.limitChange = function(event) {
		var pagedata = this.comp("pagedata");
		pagedata.setValue("limit", event.value);
		pagedata.setValue("offset", 0);
		pagedata.setValue("currentpage", 1);
		this.loadRecord();
	};

	// 上一页
	Model.prototype.preClick = function(event) {
		var pagedata = this.comp("pagedata");
		var currentpage = pagedata.getValue("currentpage") - 1;
		var limit = pagedata.getValue("limit");
		var offset = (currentpage - 1) * limit;
		pagedata.setValue("currentpage", currentpage);
		pagedata.setValue("offset", offset);
		this.loadRecord();
	};

	// 下一页
	Model.prototype.nextClick = function(event) {
		var pagedata = this.comp("pagedata");
		var currentpage = pagedata.getValue("currentpage") + 1;
		var limit = pagedata.getValue("limit");
		var offset = (currentpage - 1) * limit;
		pagedata.setValue("currentpage", currentpage);
		pagedata.setValue("offset", offset);
		this.loadRecord();
	};

	// 首页
	Model.prototype.firstpageClick = function(event) {
		var pagedata = this.comp("pagedata");
		pagedata.setValue("currentpage", 1);
		pagedata.setValue("offset", 0);
		this.loadRecord();
	};

	// 尾页
	Model.prototype.lastpageClick = function(event) {
		var pagedata = this.comp("pagedata");
		var currentpage = pagedata.getValue("pagecount");
		var limit = pagedata.getValue("limit");
		var offset = (currentpage - 1) * limit;
		pagedata.setValue("currentpage", currentpage);
		pagedata.setValue("offset", offset);
		this.loadRecord();
	};

	// 页面点击
	Model.prototype.pageBtnClick = function(event) {
		var row = event.bindingContext.$object;
		var pagedata = this.comp("pagedata");
		var currentpage = row.val("num");
		var limit = pagedata.getValue("limit");
		var offset = (currentpage - 1) * limit;
		pagedata.setValue("currentpage", currentpage);
		pagedata.setValue("offset", offset);
		this.loadRecord();
	};

	Model.prototype.loadRecord = function() {
		this.getParentModel().getParentModel().showloading();// 显示加载图
		var iteminfoData = this.comp("iteminfoData");
		// 获取分页参数
		var pagedata = this.comp("pagedata");
		var offset = pagedata.getValue("offset");
		var limit = pagedata.getValue("limit");

		var orderBys = this.comp("iteminfoData").getOrderBys();
		var me = this;
		justep.Baas.sendRequest({
			"url" : "/erpscan/base/schedulePick",
			"action" : "getSelectScheduleItem",
			"async" : true,
			"params" : {
				"houseid" : me.houseid,
				"scheduleid" : me.scheduleid,
				"itemList" : me.itemList,
				"schedule_pick_id" : me.schedule_pick_id,
				
				"searchcontent" : me.searchcontent,
				"nameinput" : me.nameinput,
				"sformatinput" : me.sformatinput,
				"classids" : me.classids,
				"searchClassChild" : me.comp("classcheckbox").val(), // "1"找
				
				"countbit" : me.countbit,
				"offset" : offset,
				"limit" : limit,
				"orderBys" : orderBys,
			},
			"success" : function(result) {

				iteminfoData.clear();
				iteminfoData.loadData(result.table);

				if (offset === 0) {
					me.pageInit(result.rowsize);
					iteminfoData.setTotal(result.rowsize);
				} else {
					me.pageInit(me.comp("pagedata").getValue("total"));
				}
				me.comp("selectitemallgrid").refresh();

				me.comp("okcloseBtn").set({
					"label" : " 选择并关闭"
				});

				if (iteminfoData.count() === 0) {
					justep.Util.hint("当前没有符合条件的记录", {
						"type" : "info",
						"position" : "middle"
					});
				}
				me.getParentModel().getParentModel().hideloading();// 隐藏加载图

			}
		});
	};

	Model.prototype.findKeypress = function(event) {
		if (event.keyCode == 13) {
			var arr = document.querySelectorAll('input');
			for (var i = 0; i < arr.length; i++) {
				arr[i].blur();
			}
			this.findBtnClick();
		}
	};

	return Model;
});