define(function(require){
	var $ = require("jquery");
	var justep = require("$UI/system/lib/justep");

	var Model = function(){
		this.callParent();
	};
	
	Model.prototype.modelLoad = function(event){
		var parent = this.getParentModel();
		if (parent === undefined) {
			window.open(require.toUrl("./../index.w"), "_self");
		}
	};

	Model.prototype.modelParamsReceive = function(event){
		this.companyid = event.params.data.companyid;
		this.loginuserid = this.getParent().loginuserid;
		
		this.findBtnClick();
	};

	Model.prototype.cancelbtnClick = function(event){
		this.comp("window").close();
	};

	Model.prototype.findBtnClick = function(event){
		this.searchcontent = this.comp("searchcontent").value;
		this.comp("scheduleData").refreshData();
	};

	Model.prototype.refreshBtnClick = function(event){

	};

	Model.prototype.grid1CellRender = function(event){
		if (event.colName === "operate") {
			event.html = "<button class='btn btn-link btn-sm linkbtn'   onclick='justep.Util.getModel(this).selectClick(event,\"" + event.rowID + "\")'>选择</button>";
		}
	};

	Model.prototype.selectClick = function(event, rowid) {
		var row = this.comp("scheduleData").getRowByID(rowid);
		if (row === null || row === undefined) {
			justep.Util.hint("没有数据可操作", {
				"type" : "danger",
				"position" : "middle"
			});
		} else {
			var data = {
					"scheduleid" : rowid,
					"orderid" : row.val("orderid")
			};

			this.owner.send(JSON.stringify(data));
			this.comp("window").close();
		}
	};

	

	Model.prototype.scheduleDataCustomRefresh = function(event){
		var sourcedata = this.comp("scheduleData");
		sourcedata.clear();
		var grid = this.comp("grid1");
		var me = this;
		justep.Baas.sendRequest({
			"url" : "/erpscan/base/schedulePick",
			"action" : "queryScheduleData",
			"async" : false,
			"params" : {
				"companyid" : this.companyid,
				"searchcontent" : this.searchcontent,
				"status" : "1",
				"schedulestatus" : "1",
				"offset" : sourcedata.getOffset(),
				"limit" : sourcedata.limit,
				"orderBys" : sourcedata.getOrderBys(),
			},
			"success" : function(data) {
				if (data.message === "") {
					sourcedata.loadData(data.table);
//					sourcedata.first();
					grid.refresh();

					if (sourcedata.count() === 0) {
						justep.Util.hint("没有符合条件的数据", {
							"type" : "info",
							"position" : "middle"
						});
					}

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