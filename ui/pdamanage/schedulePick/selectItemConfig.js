define(function(require){
	var $ = require("jquery");
	var justep = require("$UI/system/lib/justep");

	var Model = function(){
		this.callParent();
	};

	Model.prototype.modelLoad = function(event){

	};

	Model.prototype.modelParamsReceive = function(event){
		var data = event.params.data;
		var itemConfigData = this.comp("itemConfigData");
		var itemStockData = this.comp("itemStockData");
		var itemEditData = this.comp("itemEditData");
		var grid = this.comp("grid1");

		this.itemid = data.itemid;
		this.houseid = data.houseid;
		this.still_need = data.still_need;

		var keyConfig = data.itemConfig;
		var arrConfig = this.loadConfigData(keyConfig);
		itemConfigData.loadData(arrConfig);
		grid.refresh();

		itemStockData.refreshData();
	};

	Model.prototype.cancelbtnClick = function(event){
		this.comp("window").close();
	};

	Model.prototype.saveBtnClick = function(event){
		var data = {
				"config" : this.getConfigData(),
				"itemid" : this.itemid,
		};
		this.owner.send(JSON.stringify(data));
		this.comp("window").close();
	};

	Model.prototype.loadConfigData = function(data) {
		var unwantedKeys = ["value", "originalValue", "changed"];
		var configData = Object.keys(data)
		.filter(function(key) { 
			return unwantedKeys.indexOf(key) === -1;
		})
		.map(function(key) {
			return { 
				batchno: key === "" ? "无批号" : key, 
						count: data[key] 
			};
		});
		return configData;
	};

	Model.prototype.getConfigData = function() {
		var itemConfigData = this.comp("itemConfigData");
		var config = {};
		itemConfigData.each(function(params) {
			var batchno = params.row.val("batchno");
			var count = params.row.val("count");
			if (batchno === "无批号"){
				batchno = "";
			}
			config[batchno] = count;
		});
		return config;
	};

	Model.prototype.addBtnClick = function(event){
		var itemEditData = this.comp("itemEditData");
		var itemConfigData = this.comp("itemConfigData");
		var row = itemEditData.getCurrentRow(true);

		var batchno = row.val("batchno");
		if (batchno === "请先选择批号"){
			justep.Util.hint("请先选择批号", {
				"type" : "danger",
				"position" : "middle"
			});
			return;
		}
		
		var count = row.val("count");
		
		if (count === 0 && itemConfigData.getRowByID(batchno) !== undefined && itemConfigData.getRowByID(batchno) !== null){
			itemConfigData.remove(itemConfigData.getRowByID(batchno));
		} else if (itemConfigData.getRowByID(batchno) !== undefined && itemConfigData.getRowByID(batchno) !== null){
			itemConfigData.setValueByID("count", count, batchno);
		} else if (itemConfigData.getRowByID(batchno) === undefined || itemConfigData.getRowByID(batchno) === null){
			itemConfigData.add({"batchno" : batchno,"count" : count});
		}
		this.comp("grid1").refresh();
	};

	Model.prototype.grid1CellRender = function(event){

	};

	Model.prototype.itemStockDataBeforeRefresh = function(event){
		var sourcedata = this.comp("itemStockData");
		sourcedata.clear();
		justep.Baas.sendRequest({
			"url" : "/erpscan/base/schedulePick",
			"action" : "getItemStockById",
			"async" : false,
			"params" : {
				"itemid" : this.itemid,
				"houseid" : this.houseid,
			},
			"success" : function(data) {
				if (data.message === "") {
					sourcedata.loadData(data.table);
				} else {
					justep.Util.hint(data.message, {
						"type" : "danger",
						"position" : "middle"
					});
				}
			}
		});
	};

	Model.prototype.countInputBlur = function(event){
		var value = parseFloat($(this.getElementByXid("countInput")).val());
		var itemEditData = this.comp("itemEditData");

		if (isNaN(value) === true || value < 0) {
			this.comp("countInput").val(0);
			return;
		}
		
		var maxValue = itemEditData.getValue("stockCount");
		if (value > maxValue){
			this.comp("countInput").val(maxValue);
			justep.Util.hint("领料数不能超过库存总数", {
				"type" : "danger",
				"position" : "middle"
			});
		}
		
		var row = itemEditData.getCurrentRow(true);
		var batchno = row.val("batchno");
		var totalPick = this.getTotalPick(batchno,value);
		if (totalPick > this.still_need){
			this.comp("countInput").val(0);
			justep.Util.hint("排产领料单不支持超领，总领料数：" + this.still_need, {
				"type" : "danger",
				"position" : "middle"
			});
		}
	};

	Model.prototype.gridSelect1UpdateValue = function(event){
		var itemEditData = this.comp("itemEditData");
		var value = parseFloat($(this.getElementByXid("countInput")).val());
		
		if (isNaN(value) === true || value < 0) {
			this.comp("countInput").val(0);
			return;
		}
		
		var maxValue = itemEditData.getValue("stockCount");
		if (value > maxValue){
			this.comp("countInput").val(maxValue);
		}
		
	};

	Model.prototype.clearBtnClick = function(event){
		var itemConfigData = this.comp("itemConfigData");
		itemConfigData.clear();
		this.comp("grid1").refresh();
	};
	
	Model.prototype.getTotalPick = function(batchno,count){
		var itemConfigData = this.comp("itemConfigData");
		var totalPick = isNaN(count) ? 0 : count;
		itemConfigData.each(function(params){
			if (params.row.val("batchno") === batchno){
				return;
			}
			totalPick += params.row.val("count");
		});
		return totalPick;
	};

	return Model;
});