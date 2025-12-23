define(function(require) {
	// require('./mockPortalApi');

	var $ = require("jquery");
	var justep = require("$UI/system/lib/justep");
	var ShellImpl = require('$UI/system/lib/portal/shellImpl');
	var WindowDialog = require("$UI/system/components/justep/windowDialog/windowDialog");
	var menu = require("$UI/erpscan/menu/menu");

	var base = require('$UI/system/api/native/base');

	var selectors = {
		pages : '.x-portal-pages',
		username : '.x-portal-username',
		agent : '.x-portal-agent',
		reload : '.x-portal-reload',
		logout : '.x-portal-logout',
		setPassWord : '.x-portal-setPassWord',
		showMainPage : '.x-portal-showMain',
		datetime : '.x-portal-datetime',
		payserivce : '.x-portal-payservice', // 新增 2019-06-20
		mlfuseurl : '.x-portal-mlfuseurl',
		goappurl : '.x-portal-app',
		versioninfo : '.x-portal-versioninfo',
		mainauditinfo : '.x-portal-mainauditinfo'
	};
	var mainPageKey = 'main';
	var changePasswordPageKey = 'changePassword';
	var changeRoleSetKey = "changeRoleSet";// 角色权限管理--配置权限功能
	var changeRolePresetSetKey = "changeRolePresetSet";// 角色权限管理--配置权限功能
	var itemeditKey = "itemedit";
	var itemBomshowKey = "itemBomshow";
	var customereditKey = "customeredit";
	var staffeditKey = "staffedit";
	var stockturnKey = "stockturn";
	var splitseditKey = "splitsedit";
	var splitsdetailKey = "splitsdetail";
	var storeineditKey = "storeinedit";
	var storeinedit_01Key = "storeinedit_01";

	var storeindetailKey = "storeindetail";
	var storeouteditKey = "storeoutedit";
	var storeoutdetailKey = "storeoutdetail";
	var storemoveeditKey = "storemoveedit";
	var storemovedetailKey = "storemovedetail";
	var storecheckeditKey = "storecheckedit";
	var storecheckdetailKey = "storecheckdetail";
	var reportlosseditKey = "reportlossedit";
	var reportlossdetailKey = "reportlossdetail";
	var storeinouteditKey = "storeinoutedit";
	var storeinoutdetailKey = "storeinoutdetail";
	var storeoutineditKey = "storeoutinedit";
	var storeoutindetailKey = "storeoutindetail";
	var itembegineditKey = "itembeginedit";
	var itembegindetailKey = "itembegindetail";
	var storeinreportdetailKey = "storeinreportdetail";
	var storeoutreportdetailKey = "storeoutreportdetail";
	// 工单
	var ordereditKey = "orderedit";
	var ordernewKey = "ordernew";
	var orderdetailKey = "orderdetail";
	var summaryorderdetailKey = "summaryorderdetail";
	var summarystepdetailKey = "summarystepdetail";
	var stepeditKey = "stepedit";
	var hhstepeditKey = "hhstepedit";
	var stepnewKey = "stepnew";
	var hhstepnewKey = "hhstepnew";
	var staffauthKey = "staffauth";
	var printitemKey = "printitem";
	var roleauthKey = "roleauth";
	var returncontentKey = "returncontent"; // 返工报备

	// 新增
	var purchaseordereditKey = "purchaseorderedit";
	var purchaseorderedit_01Key = "purchaseorderedit_01";
	var purchaseorderdetailKey = "purchaseorderdetail";
	var purchaseedit_01Key = "purchaseedit_01";
	var purchaseeditKey = "purchaseedit";
	var purchasedetailKey = "purchasedetail";
	var salesordereditKey = "salesorderedit";
	var salesorderedit_01Key = "salesorderedit_01";
	var salesorderdetailKey = "salesorderderdetail";
	var prodrequisitioneditKey = "prodrequisitionedit";
	var prodrequisitiondetailKey = "prodrequisitiondetail";
	var otherineditKey = "otherinedit";
	var otherindetailKey = "otherindetail";
	var otherouteditKey = "otheroutedit";
	var otheroutdetailKey = "otheroutdetail";
	// 送货
	var delivereditKey = "deliveredit";
	var deliverdetailKey = "deliverdetail";

	// 2019/6/18排产
	var scheduleordernewKey = "scheduleordernew";
	var scheduleordereditKey = "scheduleorderedit";
	var scheduleorderdetailKey = "scheduleorderdetail";
	var productindetailKey = "productindetail";
	var productinnewKey = "productinnew";

	var dayinoutdetail = "dayinoutdetail";
	var dayinKey = "dayinedit";
	var dayoutKey = "dayoutedit";
	var accountbillineditKey = "accountbillinedit";
	var accountbillouteditKey = "accountbilloutedit";
	var accountbillindetailKey = "accountbillindetail";
	var accountbilloutdetailKey = "accountbilloutdetail";
	var recpaycustomerdetailKey = "recpaycustomerdetail";

	var honourbillineditKey = "honourbillinedit";
	var honourbillouteditKey = "honourbilloutedit";

	var honourbillindetailKey = "honourbillindetail";
	var honourbilloutdetailKey = "honourbilloutdetail";

	// 委外加工
	var processineditKey = "processinedit";
	var processindetailKey = "processindetail";
	var processouteditKey = "processoutedit";
	var processoutdetailKey = "processoutdetail";
	var outsourcingeditKey = "outsourcingedit";
	var outsourcingdetailKey = "outsourcingdetail";
	var outsourcingineditKey = "outsourcinginedit";
	var outsourcingindetailKey = "outsourcingindetail";
	var outsourcingouteditKey = "outsourcingoutedit";
	var outsourcingoutdetailKey = "outsourcingoutdetail";

	var prodrequisitionbackeditKey = "prodrequisitionbackedit";
	var prodrequisitionbackdetailKey = "prodrequisitionbackdetail";

	// 采购合同
	var storecontracteditKey = "storecontractedit";
	var contracttemplacteditKey = "contracttemplactedit";

	// 审批流程
	var auditeditKey = "auditedit";

	var customerbilleditKey = "customerbilledit";
	var customerbilldetailKey = "customerbilldetail";
	var transfereditKey = "transferedit";
	var transferdetailKey = "transferdetail";

	var stageoutsourcingeditKey = "stageoutsourcingedit";
	var stageoutsourcingdetailKey = "stageoutsourcingdetail";
	var stageoutsourcingineditKey = "stageoutsourcinginedit";
	var stageoutsourcingindetailKey = "stageoutsourcingindetail";

	var quotationeditKey = "quotationedit";
	var quotationdetailKey = "quotationdetail";

	var auditmanageKey = "auditmanage";

	var qualitydatalistKey = "qualitydatalist";

	var salesorderconfigKey = "salesorderconfig";

	var applymaterialeditKey = "applymaterialedit";
	var applymaterialdetailKey = "applymaterialdetail";

	var applypaymenteditKey = "applypaymentedit";
	var applypaymentdetailKey = "applypaymentdetail";

	var applyinvoiceeditKey = "applyinvoiceedit";
	var applyinvoicedetailKey = "applyinvoicedetail";

	var applyleaveeditKey = "applyleaveedit";
	var applyleavedetailKey = "applyleavedetail";

	var applyovertimeeditKey = "applyovertimeedit";
	var applyovertimedetailKey = "applyovertimedetail";

	var applyovertimetotaleditKey = "applyovertimetotaledit";
	var applyovertimetotaldetailKey = "applyovertimetotaldetail";

	var applyiteminfoeditKey = "applyiteminfoedit";
	var applyiteminfodetailKey = "applyiteminfodetail";

	var orderprofitdetailKey = "orderprofitdetail";

	// 发票商品出入
	var invoicestoreineditKey = "invoicestoreinedit";
	var invoicestoreindetailKey = "invoicestoreindetail";
	var invoicestoreinouteditKey = "invoicestoreinoutedit";
	var invoicestoreinoutdetailKey = "invoicestoreinoutdetail";
	var invoiceinshowdetailKey = "invoiceinshowdetail";

	var devicemaintenanceeditKey = "devicemaintenanceedit";
	var devicemaintenancedetailKey = "devicemaintenancedetail";
	var devicepropertyKey = "deviceproperty";

	var maintainApplyEditKey = "maintainApplyEdit";
	var maintainApplyDetailKey = "maintainApplyDetail";
	var maintainDetailKey = "maintainDetail";
	var maintainEditKey = "maintainEdit";

	var storeoutapplyeditKey = "storeoutapplyedit";
	var storeoutapplydetailKey = "storeoutapplydetail";
	
	var failureModeTotalKey ="failureModeTotal";
	 
	var sealEditKey = "sealEdit";
	var sealDetailKey = "sealDetail"; 
	
	var storeinshowdetailKey = "storeinshowdetail";
	
	var salesordercombineeditKey = "salesordercombineedit";
	var salesordercombinedetailKey = "salesordercombinedetail";
	
	var schedulePickEditKey = "schedulePickEdit";
	var schedulePickDetailKey = "schedulePickDetail";

	window.isPortalWindow = true;
	var Model = function() {
		this.callParent();

		this._cfg = {
			isDebugMode : true,
			needLogin : true,
			main : {
				show : true,
				xid : 'main',
				url : "$UI/erpscan/base/main/main.w",
				title : "首页"
			},
			changeRoleSet : {
				xid : "changeRoleSet",
				url : '$UI/erpscan/mlfcommon/permission/roleset.w',
				title : '配置角色权限'
			},
			changeRolePresetSet : {
				xid : "changeRolePresetSet",
				url : '$UI/erpscan/mlfbackend/permission/rolePresetSet.w',
				title : '预设角色权限'
			},
			changePassword : {
				xid : "changePassword",
				url : '$UI/erpscan/base/changePassword/changePassword.w',
				title : '修改密码'
			},
			itemedit : {
				xid : "itemedit",
				url : '$UI/erpscan/pdamanage/itemedit.w',
				title : '编辑商品'
			},
			itemBomshow : {
				xid : "itemBomshow",
				url : '$UI/erpscan/pdamanage/itemBomshow.w',
				title : '商品Bom详情'
			},
			customeredit : {
				xid : "customeredit",
				url : '$UI/erpscan/pdamanage/customeredit.w',
				title : '编辑往来单位'
			},
			staffedit : {
				xid : "staffedit",
				url : '$UI/erpscan/pdamanage/staffedit.w',
				title : '编辑员工信息'
			},
			stockturn : {
				xid : "stockturn",
				url : '$UI/erpscan/pdamanage/findreport/stockturn.w',
				title : '商品流水'
			},
			splitsedit : {
				xid : "splitsedit",
				url : '$UI/erpscan/pdamanage/splitsedit.w',
				title : '组装拆卸'
			},
			splitsdetail : {
				xid : "splitsdetail",
				url : '$UI/erpscan/pdamanage/splitsdetail.w',
				title : '组装拆卸详情'
			},
			storeinedit : {
				xid : "storeinedit",
				url : '$UI/erpscan/pdamanage/storeinedit.w',
				title : '采购入库单'
			},
			storeinedit_01 : {
				xid : "storeinedit_01",
				url : '$UI/erpscan/pdamanage/storeinedit_01.w',
				title : '采购入库单'
			},
			storeindetail : {
				xid : "storeindetail",
				url : '$UI/erpscan/pdamanage/storeindetail.w',
				title : '采购入库单详情'
			},
			storeoutedit : {
				xid : "storeoutedit",
				url : '$UI/erpscan/pdamanage/storeoutedit.w',
				title : '销售出库单'
			},
			storeoutdetail : {
				xid : "storeoutdetail",
				url : '$UI/erpscan/pdamanage/storeoutdetail.w',
				title : '销售出库单详情'
			},
			storeinoutedit : {
				xid : "storeinoutedit",
				url : '$UI/erpscan/pdamanage/storeinoutedit.w',
				title : '采购退货'
			},
			storeinoutdetail : {
				xid : "storeinoutdetail",
				url : '$UI/erpscan/pdamanage/storeinoutdetail.w',
				title : '采购退货详情'
			},
			storeoutinedit : {
				xid : "storeoutinedit",
				url : '$UI/erpscan/pdamanage/storeoutinedit.w',
				title : '销售退货'
			},
			storeoutindetail : {
				xid : "storeoutindetail",
				url : '$UI/erpscan/pdamanage/storeoutindetail.w',
				title : '销售退货详情'
			},
			storemoveedit : {
				xid : "storemoveedit",
				url : '$UI/erpscan/pdamanage/storemoveedit.w',
				title : '商品调拨'
			},
			storemovedetail : {
				xid : "storemovedetail",
				url : '$UI/erpscan/pdamanage/storemovedetail.w',
				title : '商品调拨详情'
			},
			storecheckedit : {
				xid : "storecheckedit",
				url : '$UI/erpscan/pdamanage/storecheckedit.w',
				title : '商品盘点'
			},
			storecheckdetail : {
				xid : "storecheckdetail",
				url : '$UI/erpscan/pdamanage/storecheckdetail.w',
				title : '商品盘点详情'
			},
			reportlossedit : {
				xid : "reportlossedit",
				url : '$UI/erpscan/pdamanage/reportlossedit.w',
				title : '报损记录'
			},
			reportlossdetail : {
				xid : "reportlossdetail",
				url : '$UI/erpscan/pdamanage/reportlossdetail.w',
				title : '报损记录详情'
			},
			itembeginedit : {
				xid : "itembeginedit",
				url : '$UI/erpscan/pdamanage/itembeginedit.w',
				title : '期初库存'
			},
			itembegindetail : {
				xid : "itembegindetail",
				url : '$UI/erpscan/pdamanage/itembegindetail.w',
				title : '期初库存详情'
			},
			storeinreportdetail : {
				xid : "storeinreportdetail",
				url : '$UI/erpscan/pdamanage/findreport/storeinreportdetail.w',
				title : '采购入库报表详情'
			},
			storeoutreportdetail : {
				xid : "storeoutreportdetail",
				url : '$UI/erpscan/pdamanage/findreport/storeoutreportdetail.w',
				title : '销售出库报表详情'
			},
			orderedit : {
				xid : "orderedit",
				url : '$UI/erpscan/mlfmanage/orderedit.w',
				title : '工单编辑'
			},
			ordernew : {
				xid : "ordernew",
				url : '$UI/erpscan/mlfmanage/orderedit.w',
				title : '工单新增'
			},
			orderdetail : {
				xid : "orderdetail",
				url : '$UI/erpscan/mlfmanage/orderdetail.w',
				title : '工单详情'
			},
			summaryorderdetail : {
				xid : "summaryorderdetail",
				url : '$UI/erpscan/mlfmanage/summaryorderdetail.w',
				title : '工单进度详情'
			},
			summarystepdetail : {
				xid : "summarystepdetail",
				url : '$UI/erpscan/mlfmanage/summarystepdetail.w',
				title : '工序生产详情'
			},
			stepedit : {
				xid : "stepedit",
				url : '$UI/erpscan/mlfmanage/stepedit.w',
				title : '工艺流程编辑'
			},
			hhstepedit : {
				xid : "hhstepedit",
				url : '$UI/erpscan/mlfmanage/stepedit2.w',
				title : '工艺流程编辑'
			},
			stepnew : {
				xid : "stepnew",
				url : '$UI/erpscan/mlfmanage/stepedit.w',
				title : '工艺流程新增'
			},
			hhstepnew : {
				xid : "hhstepnew",
				url : '$UI/erpscan/mlfmanage/stepedit2.w',
				title : '工艺流程新增'
			},
			returncontent : {
				xid : "returncontent",
				url : '$UI/erpscan/mlfmanage/return/returncontent.w',
				title : '返工报备新增'
			},
			staffauth : {
				xid : "staffauth",
				url : '$UI/erpscan/mlfmanage/staffauth.w',
				title : '员工工艺权限配置'
			},
			printitem : {
				xid : "printitem",
				url : '$UI/erpscan/mlfmanage/printitem.w',
				title : '物料细码打印'
			},
			roleauth : {
				xid : "roleauth",
				url : '$UI/erpscan/mlfmanage/roleauth.w',
				title : '角色工艺权限配置'
			},
			purchaseorderedit : {
				xid : "purchaseorderedit",
				url : '$UI/erpscan/pdamanage/order/purchaseorderedit.w',
				title : '采购订单'
			},
			purchaseorderedit_01 : {
				xid : "purchaseorderedit_01",
				url : '$UI/erpscan/pdamanage/order/purchaseorderedit_01.w',
				title : '采购订单'
			},
			purchaseorderdetail : {
				xid : "purchaseorderdetail",
				url : '$UI/erpscan/pdamanage/order/purchaseorderdetail.w',
				title : '采购订单详情'
			},
			purchaseedit_01 : {
				xid : "purchaseedit_01",
				url : '$UI/erpscan/pdamanage/application/purchaseappedit_01.w',
				title : '采购申请单'
			},
			purchaseedit : {
				xid : "purchaseedit",
				url : '$UI/erpscan/pdamanage/application/purchaseappedit.w',
				title : '采购申请单'
			},
			purchasedetail : {
				xid : "purchasedetail",
				url : '$UI/erpscan/pdamanage/application/purchaseappdetail.w',
				title : '采购申请单详情'
			},
			salesorderedit : {
				xid : "salesorderedit",
				url : '$UI/erpscan/pdamanage/order/salesorderedit.w',
				title : '销售订单'
			},
			salesorderedit_01 : {
				xid : "salesorderedit_01",
				url : '$UI/erpscan/pdamanage/order/salesorderedit_01.w',
				title : '销售订单'
			},
			salesorderdetail : {
				xid : "salesorderdetail",
				url : '$UI/erpscan/pdamanage/order/salesorderdetail.w',
				title : '销售订单详情'
			},
			prodrequisitionedit : {
				xid : "prodrequisitionedit",
				url : '$UI/erpscan/pdamanage/other/prodrequisitionedit.w',
				title : '生产领料'
			},
			prodrequisitiondetail : {
				xid : "prodrequisitiondetail",
				url : '$UI/erpscan/pdamanage/other/prodrequisitiondetail.w',
				title : '生产领料详情'
			},
			otherinedit : {
				xid : "otherinedit",
				url : '$UI/erpscan/pdamanage/other/otherinedit.w',
				title : '其他入库'
			},
			otherindetail : {
				xid : "otherindetail",
				url : '$UI/erpscan/pdamanage/other/otherindetail.w',
				title : '其他入库详情'
			},
			otheroutedit : {
				xid : "otheroutedit",
				url : '$UI/erpscan/pdamanage/other/otheroutedit.w',
				title : '其他出库'
			},
			otheroutdetail : {
				xid : "otheroutdetail",
				url : '$UI/erpscan/pdamanage/other/otheroutdetail.w',
				title : '其他出库详情'
			},
			deliveredit : {
				xid : "deliveredit",
				url : '$UI/erpscan/pdamanage/deliver/deliveredit.w',
				title : '送货计划'
			},
			deliverdetail : {
				xid : "deliverdetail",
				url : '$UI/erpscan/pdamanage/deliver/deliverdetail.w',
				title : '送货计划详情'
			},
			scheduleordernew : {
				xid : "scheduleordernew",
				url : "$UI/erpscan/mlfmanage/scheduling/scheduleorderedit.w",
				title : '排产单新增'
			},
			scheduleorderedit : {
				xid : "scheduleorderedit",
				url : "$UI/erpscan/mlfmanage/scheduling/scheduleorderedit.w",
				title : '排产单修改'
			},
			scheduleorderdetail : {
				xid : "scheduleorderdetail",
				url : "$UI/erpscan/mlfmanage/scheduling/scheduleorderdetail.w",
				title : "排产单详情",
			},
			productinnew : {
				xid : "productinnew",
				url : "$UI/erpscan/mlfmanage/scheduling/productinedit.w",
				title : '生产入库'
			},
			productindetail : {
				xid : "productindetail",
				url : "$UI/erpscan/mlfmanage/scheduling/prodstoragedetail.w",
				title : '生产入库详情'
			},
			dayinedit : {
				xid : "dayinoutedit",
				url : '$UI/erpscan/pdamanage/account/dayinoutedit.w',
				title : '新增日常收入单'
			},
			dayoutedit : {
				xid : "dayinoutedit",
				url : '$UI/erpscan/pdamanage/account/dayinoutedit.w',
				title : '新增日常支出单'
			},
			dayinoutdetail : {
				xid : "dayinoutdetail",
				url : '$UI/erpscan/pdamanage/account/dayinoutdetail.w',
				title : '日常收支详情'
			},
			accountbillinedit : {
				xid : "accountbillinedit",
				url : '$UI/erpscan/pdamanage/account/accountbilledit.w',
				title : '新增收款单'
			},
			accountbilloutedit : {
				xid : "accountbilloutedit",
				url : '$UI/erpscan/pdamanage/account/accountbilledit.w',
				title : '新增付款单'
			},
			accountbillindetail : {
				xid : "accountbillindetail",
				url : '$UI/erpscan/pdamanage/account/accountbilldetail.w',
				title : '收款单详情'
			},
			accountbilloutdetail : {
				xid : "accountbilloutdetail",
				url : '$UI/erpscan/pdamanage/account/accountbilldetail.w',
				title : '付款单详情'
			},

			honourbillinedit : {
				xid : "honourbillinedit",
				url : '$UI/erpscan/pdamanage/account/honourbilledit.w',
				title : '编辑收承兑'
			},
			honourbilloutedit : {
				xid : "honourbilloutedit",
				url : '$UI/erpscan/pdamanage/account/honourbilledit.w',
				title : '编辑支承兑'
			},

			honourbillindetail : {
				xid : "honourbillindetail",
				url : '$UI/erpscan/pdamanage/account/honourbilldetail.w',
				title : '收承兑详情'
			},
			honourbilloutdetail : {
				xid : "honourbilloutdetail",
				url : '$UI/erpscan/pdamanage/account/honourbilldetail.w',
				title : '支承兑详情'
			},
			recpaycustomerdetail : {
				xid : "recpaycustomerdetail",
				url : '$UI/erpscan/pdamanage/account/recpaycustomerdetail.w',
				title : '往来应收应付详情'
			},
			processinedit : {
				xid : "processinedit",
				url : '$UI/erpscan/pdamanage/outsourcing/processinedit.w',
				title : '加工退料'
			},
			processindetail : {
				xid : "processindetail",
				url : '$UI/erpscan/pdamanage/outsourcing/processindetail.w',
				title : '加工退料详情'
			},
			processoutedit : {
				xid : "processoutedit",
				url : '$UI/erpscan/pdamanage/outsourcing/processoutedit.w',
				title : '加工出库'
			},
			processoutdetail : {
				xid : "processoutdetail",
				url : '$UI/erpscan/pdamanage/outsourcing/processoutdetail.w',
				title : '加工出库详情'
			},
			prodrequisitionbackedit : {
				xid : "prodrequisitionbackedit",
				url : '$UI/erpscan/pdamanage/other/prodrequisitionbackedit.w',
				title : '生产退料'
			},
			prodrequisitionbackdetail : {
				xid : "prodrequisitionbackdetail",
				url : '$UI/erpscan/pdamanage/other/prodrequisitionbackdetail.w',
				title : '生产退料详情'
			},
			outsourcingedit : {
				xid : "outsourcingedit",
				url : '$UI/erpscan/pdamanage/outsourcing/outsourcingedit.w',
				title : '委外加工'
			},
			outsourcingdetail : {
				xid : "outsourcingdetail",
				url : '$UI/erpscan/pdamanage/outsourcing/outsourcingdetail.w',
				title : '委外加工详情'
			},
			outsourcinginedit : {
				xid : "outsourcinginedit",
				url : '$UI/erpscan/pdamanage/outsourcing/outsourcinginedit.w',
				title : '加工入库'
			},
			outsourcingindetail : {
				xid : "outsourcingindetail",
				url : '$UI/erpscan/pdamanage/outsourcing/outsourcingindetail.w',
				title : '加工入库详情'
			},
			outsourcingoutedit : {
				xid : "outsourcingoutedit",
				url : '$UI/erpscan/pdamanage/outsourcing/outsourcingoutedit.w',
				title : '加工退货'
			},
			outsourcingoutdetail : {
				xid : "outsourcingoutdetail",
				url : '$UI/erpscan/pdamanage/outsourcing/outsourcingoutdetail.w',
				title : '加工退货详情'
			},
			storecontractedit : {
				xid : "storecontractedit",
				url : '$UI/erpscan/pdamanage/contract/storecontractedit.w',
				title : '编辑合同'
			},
			contracttemplactedit : {
				xid : "contracttemplactedit",
				url : '$UI/erpscan/pdamanage/contract/contracttemplactedit.w',
				title : '编辑合同模板'
			},
			// 审批流程
			auditedit : {
				xid : "auditedit",
				url : '$UI/erpscan/mlfcommon/audit/auditedit.w',
				title : '编辑审批流程'
			},
			customerbilledit : {
				xid : "customerbilledit",
				url : '$UI/erpscan/pdamanage/account/customerbilledit.w',
				title : '编辑往来单位调账单'
			},
			customerbilldetail : {
				xid : "customerbilldetail",
				url : '$UI/erpscan/pdamanage/account/customerbilldetail.w',
				title : '往来单位调账单详情'
			},
			transferedit : {
				xid : "transferedit",
				url : '$UI/erpscan/pdamanage/account/transferedit.w',
				title : '编辑结算账户转账'
			},
			transferdetail : {
				xid : "transferdetail",
				url : '$UI/erpscan/pdamanage/account/transferdetail.w',
				title : '结算账户转账详情'
			},
			stageoutsourcingedit : {
				xid : "stageoutsourcingedit",
				url : '$UI/erpscan/mlfmanage/stageoutsourcing/stageoutsourcingedit.w',
				title : '工序外协发货单编辑'
			},
			stageoutsourcingdetail : {
				xid : "stageoutsourcingdetail",
				url : '$UI/erpscan/mlfmanage/stageoutsourcing/stageoutsourcingdetail.w',
				title : '工序外协发货单详情'
			},
			stageoutsourcinginedit : {
				xid : "stageoutsourcinginedit",
				url : '$UI/erpscan/mlfmanage/stageoutsourcing/stageoutsourcinginedit.w',
				title : '工序外协收货单编辑'
			},
			stageoutsourcingindetail : {
				xid : "stageoutsourcingindetail",
				url : '$UI/erpscan/mlfmanage/stageoutsourcing/stageoutsourcingindetail.w',
				title : '工序外协收货单详情'
			},
			auditmanage : {
				xid : "auditmanage",
				url : '$UI/erpscan/pdamanage/auditflow/auditmanage.w',
				title : '待审批'
			},

			qualitydatalist : {
				xid : "qualitydatalist",
				url : '$UI/erpscan/pdamanage/quality/qualitydatalist.w',
				title : '待质检'
			},
			quotationedit : {
				xid : "quotationedit",
				url : '$UI/erpscan/pdamanage/order/quotationedit.w',
				title : '报价单'
			},
			quotationdetail : {
				xid : "quotationdetail",
				url : '$UI/erpscan/pdamanage/order/quotationdetail.w',
				title : '报价单详情'
			},
			salesorderconfig : {
				xid : "salesorderconfig",
				url : '$UI/erpscan/mlfmanage/mrp/salesorderconfig.w',
				title : '物料需求配置下单'
			},
			applymaterialedit : {
				xid : "applymaterialedit",
				url : '$UI/erpscan/pdamanage/apply/applymaterialedit.w',
				title : '补料申请单'
			},
			applymaterialdetail : {
				xid : "applymaterialdetail",
				url : '$UI/erpscan/pdamanage/apply/applymaterialdetail.w',
				title : '补料申请单详情'
			},
			applypaymentedit : {
				xid : "applypaymentedit",
				url : '$UI/erpscan/pdamanage/apply/applypaymentedit.w',
				title : '付款申请单'
			},
			applypaymentdetail : {
				xid : "applypaymentdetail",
				url : '$UI/erpscan/pdamanage/apply/applypaymentdetail.w',
				title : '付款申请单详情'
			},
			applyinvoiceedit : {
				xid : "applyinvoiceedit",
				url : '$UI/erpscan/pdamanage/apply/applyinvoiceedit.w',
				title : '开票申请单'
			},
			applyinvoicedetail : {
				xid : "applyinvoicedetail",
				url : '$UI/erpscan/pdamanage/apply/applyinvoicedetail.w',
				title : '开票申请单详情'
			},
			applyleaveedit : {
				xid : "applyleaveedit",
				url : '$UI/erpscan/pdamanage/apply/applyleaveedit.w',
				title : '请假申请单'
			},
			applyleavedetail : {
				xid : "applyleavedetail",
				url : '$UI/erpscan/pdamanage/apply/applyleavedetail.w',
				title : '请假申请单详情'
			},
			applyovertimeedit : {
				xid : "applyovertimeedit",
				url : '$UI/erpscan/pdamanage/apply/applyovertimeedit.w',
				title : '加班申请单'
			},
			applyovertimedetail : {
				xid : "applyovertimedetail",
				url : '$UI/erpscan/pdamanage/apply/applyovertimedetail.w',
				title : '加班申请单详情'
			},

			applyovertimetotaledit : {
				xid : "applyovertimetotaledit",
				url : '$UI/erpscan/pdamanage/apply/applyovertimetotaledit.w',
				title : '加班汇总单'
			},
			applyovertimetotaldetail : {
				xid : "applyovertimetotaldetail",
				url : '$UI/erpscan/pdamanage/apply/applyovertimetotaldetail.w',
				title : '加班汇总单详情'
			},
			applyiteminfoedit : {
				xid : "applyiteminfoedit",
				url : '$UI/erpscan/pdamanage/apply/applyiteminfoedit.w',
				title : '商品信息申请单'
			},
			applyiteminfodetail : {
				xid : "applyiteminfodetail",
				url : '$UI/erpscan/pdamanage/apply/applyiteminfodetail.w',
				title : '商品信息单详情'
			},
			orderprofitdetail : {
				xid : "orderprofitdetail",
				url : '$UI/erpscan/pdamanage/order/orderprofitdetail.w',
				title : '销售订单利润评估详情'
			},
			invoicestoreinedit : {
				xid : "invoicestoreinedit",
				url : '$UI/erpscan/pdamanage/invoice/invoicestoreinedit.w',
				title : '发票商品入库单'
			},
			invoicestoreindetail : {
				xid : "invoicestoreindetail",
				url : '$UI/erpscan/pdamanage/invoice/invoicestoreindetail.w',
				title : '发票商品入库单详情'
			},
			invoicestoreinoutedit : {
				xid : "invoicestoreinoutedit",
				url : '$UI/erpscan/pdamanage/invoice/invoicestoreinoutedit.w',
				title : '发票商品出库单'
			},
			invoicestoreinoutdetail : {
				xid : "invoicestoreinoutdetail",
				url : '$UI/erpscan/pdamanage/invoice/invoicestoreinoutdetail.w',
				title : '发票商品出库详情'
			},
			invoiceinshowdetail : {
				xid : "invoiceinshowdetail",
				url : '$UI/erpscan/pdamanage/invoice/invoiceinshowdetail.w',
				title : '发票商品历史价详情'
			},
			devicemaintenanceedit : {
				xid : "devicemaintenanceedit",
				url : '$UI/erpscan/pdamanage/device/devicemaintenanceedit.w',
				title : '设备保养登记单'
			},
			devicemaintenancedetail : {
				xid : "devicemaintenancedetail",
				url : '$UI/erpscan/pdamanage/device/devicemaintenancedetail.w',
				title : '设备保养登记单详情'
			},
			deviceproperty : {
				xid : "deviceproperty",
				url : '$UI/erpscan/pdamanage/device/deviceproperty.w',
				title : '设备看板属性详情'
			},
			maintainApplyEdit : {
				xid : "maintainApplyEdit",
				url : '$UI/erpscan/pdamanage/device/maintainApplyEdit.w',
				title : '设备维修申请'
			},
			maintainApplyDetail : {
				xid : "maintainApplyDetail",
				url : '$UI/erpscan/pdamanage/device/maintainApplyDetail.w',
				title : '设备维修申请详情'
			},
			maintainDetail : {
				xid : "maintainDetail",
				url : '$UI/erpscan/pdamanage/device/maintainDetail.w',
				title : '设备维修登记详情'
			},
			maintainEdit : {
				xid : "maintainEdit",
				url : '$UI/erpscan/pdamanage/device/maintainEdit.w',
				title : '设备维修登记'
			},
			storeoutapplyedit : {
				xid : "storeoutapplyedit",
				url : '$UI/erpscan/pdamanage/storeoutapplyedit.w',
				title : '发货申请单'
			},
			storeoutapplydetail : {
				xid : "storeoutapplydetail",
				url : '$UI/erpscan/pdamanage/storeoutapplydetail.w',
				title : '发货申请单详情'
			},failureModeTotal : {
				xid : "failureModeTotal",
				url : '$UI/erpscan/pdamanage/order/failureModeTotal.w',
				title : '故障模式汇总'
			},
			sealEdit : {
				xid : "sealEdit",
				url : '$UI/erpscan/pdamanage/seal/sealEdit.w',
				title : '用章登记'
			},
			sealDetail : {
				xid : "sealDetail",
				url : '$UI/erpscan/pdamanage/seal/sealDetail.w',
				title : '用章详情'
			},storeinshowdetail : {
				xid : "storeinshowdetail",
				url : '$UI/erpscan/pdamanage/storein/storeinshowdetail.w',
				title : '采购入库商品历史价格'
			},
			salesordercombinedetail : {
				xid : "salesordercombinedetail",
				url : '$UI/erpscan/pdamanage/order/salesordercombinedetail.w',
				title : '销售合并订单详情'
			},
			salesordercombineedit : {
				xid : "salesordercombineedit",
				url : '$UI/erpscan/pdamanage/order/salesordercombineedit.w',
				title : '销售合并订单'
			},
			schedulePickEdit : {
				xid : "schedulePickEdit",
				url : '$UI/erpscan/pdamanage/schedulePick/schedulePickEdit.w',
				title : '排产领料编辑'
			},
			schedulePickDetail : {
				xid : "schedulePickDetail",
				url : '$UI/erpscan/pdamanage/schedulePick/schedulePickDetail.w',
				title : '排产领料详情'
			},

			loginURL : '$UI/erpscan/login/login.w',
			selectExecutorURL : '$UI/erpscan/base/dialog/selectExecutor.w',
			iframeFuncURL : '$UI/erpscan/base/plugin/iframeFunc.w',
			dateTimeFormat : 'yyyy-MM-dd hh:mm:ss'
		};
		this._cfg.binds = [ {
			selector : selectors.setPassWord,
			event : 'click',
			func : this.changePassword.bind(this)
		}, {
			selector : selectors.logout,
			event : 'click',
			func : this.logout.bind(this)
		}, {
			selector : selectors.reload,
			event : 'click',
			func : this.reload.bind(this)
		}, {
			selector : selectors.showMainPage,
			event : 'click',
			func : this.showMainPage.bind(this)
		}, {
			selector : selectors.payserivce,
			event : 'click',
			func : this.showPayserivce.bind(this)
		}, {
			selector : selectors.versioninfo,
			event : 'click',
			func : this.showVersioninfo.bind(this)
		}, {
			selector : selectors.mlfuseurl,
			event : 'click',
			func : this.showMlfuseurl.bind(this)
		}, {
			selector : selectors.goappurl,
			event : 'click',
			func : this.gotoApp.bind(this)
		}, {
			selector : selectors.mainauditinfo,
			event : 'click',
			func : this.showMainauditinfo.bind(this)
		} ];
		this._pages = null;
		this.userName = "demo";
		this.logid = "";
		this.current = null;
		this._loginDlg = null;
		this._selectExecutorDlg = null;
		this.openedPage = {};

		this.loginresult = null;
		this.manualLogin = false;

		this.urllogin = false;

		this.openers = [];
		if (location.hash !== "" && location.hash !== "#!login") {
			this.loginDtd = this.getLoadedDeferred();
		}
	};

	Model.prototype.bindUpdateDateTime = function() {
		var $root = $(this.getRootNode());
		var $dateTime = $root.find(selectors.datetime);
		if ($dateTime.size() > 0) {
			var fn = function() {
				var now = new Date();
				$dateTime.text(justep.Date.toString(now, this._cfg.dateTimeFormat || justep.Date.DEFAULT_FORMAT));
				now = null;
			};
			this._updateDateTimeHandle = window.setInterval(justep.Util.bindModelFn(this, fn, this), 1000);
		}
	};

	Model.prototype.showMainPage = function() {
		this.showPage(mainPageKey);
	};

	Model.prototype.showVersioninfo = function() {
		this.comp("qualityDialog").open({
			"src" : require.toUrl("./../mlfbackend/showversionchange.w"),
			"status" : "maximize"
		});
	};

	// 增加 2019-06-20
	Model.prototype.showPayserivce = function() {
		var companyData = this.comp("companyData");
		if (companyData.count() > 0) {
			this.comp("paypageDialog").open({
				"data" : {
					"companyid" : companyData.getValue("id"),
					"companyname" : companyData.getValue("companyname"),
					"onlyshowuser" : (companyData.getValue("lifelimit") === '2' && companyData.getValue("maxuser") > -1),
					"username" : this.userName,
					"source" : "main",
					"paytype" : this.comp("sysconfigureData").getValue("paytype")
				},
				"src" : require.toUrl("./../login/pay.w"),
				"status" : justep.Browser.isPC ? "normal" : "maximize"
			});
		} else {
			justep.Util.hint("没有使用支付功能权限", {
				type : "danger",
				position : "middle"
			});
		}
	};
	// 增加 2019-07-15
	Model.prototype.showMlfuseurl = function() {
		var sysconfigureData = this.comp("sysconfigureData");
		var url = sysconfigureData.getValue("useurl");
		if (url !== "" && url !== undefined) {
			window.open(url, "_blank");
		}
	};

	Model.prototype.gotoApp = function() {
		var userinfoData = this.comp("userinfoData");
		var message = this.comp("message");
		var username = userinfoData.getValue("username");
		var password = userinfoData.getValue("password");
		var me = this;
		justep.Baas.sendRequest({
			"url" : "/erpscan/appcheck/appcheck",
			"action" : "checkAppLogin",
			"async" : false,
			"params" : {
				"username" : username,
				"password" : password
			},
			"success" : function(msdata) {
				if (msdata.message !== "") {
					message.show({
						title : "错误提示",
						message : msdata.message
					});
				} else {
					if (me._updateDateTimeHandle)
						window.clearInterval(me._updateDateTimeHandle);

					if (me._updateGGHandle)
						window.clearInterval(me._updateGGHandle);

					if (me._updateAuditHandle)
						window.clearInterval(me._updateAuditHandle);

					var icode = msdata.code;
					var linfo = username + "&" + password + "&" + icode;
					// begin info 用户名密码验证码的加密
					var codestr = String.fromCharCode(linfo.charCodeAt(0) + linfo.length);
					var k = 0;
					for (k = 1; k < linfo.length; k++) {
						codestr += String.fromCharCode(linfo.charCodeAt(k) + linfo.charCodeAt(k - 1));
					}

					var code = escape(codestr);
					// end info 用户名密码验证码的加密
					window.location.href = require.toUrl("./../mlffrontend/index.w?code=" + code);
				}
			}
		});
	};

	Model.prototype.getPageId = function() {
		return this.current;
	};

	Model.prototype.reload = function() {
		this.isReloadMode = true;
		window.location.reload();
	};

	Model.prototype.changePassword = function() {
		return this.showPage(changePasswordPageKey);
	};

	Model.prototype._getTitle = function(options, container) {
		var title = (options && (options.title || (options.extra && options.extra.title))) || '';
		if (container && container.getInnerModel()) {
			var evtData = {
				type : 'getTitle',
				title : title
			};
			container.getInnerModel().postMessage(evtData);
			title = evtData.title;
		}
		return title;
	};

	Model.prototype._doShowPage = function(event) {

		var container = event.container, options = event.data.params, pageID = event.data.xid;
		var title = this._getTitle(options, container);
		this.current = pageID;

		if (container) {
			// 增加相关的class
			container.$domNode.addClass('x-portal-page-container').parent(".x-contents-content").addClass('x-portal-page-content');
		}
		var isMainPage = (options ? this.isMainPage(options.xid) : false);
		if (this.hasListener('onShowPage')) {
			var eData = {
				source : this,
				isMainPage : isMainPage,
				title : title,
				pageID : pageID,
				first : !this.openedPage[pageID],
				options : options
			};
			this.fireEvent('onShowPage', eData);

		}
		this.openedPage[pageID] = true;
		// 更新操作时间
		this.updateLoginLog();

		container
		null;
		options = null;
		pageID = null;
		title = null;
		isMainPage = null;
	};

	Model.prototype.selectExecutorDialogReceive = function(event) {
		this.showPageByExecutor(this._executor_pageKey_, event.data);
	};

	Model.prototype.showPageByExecutor = function(pageKey, executor) {
		var pageParam = this.shellImpl.pageMappings[pageKey];
		var title = this._getTitle(pageParam);
		var cfg = {
			title : title,
			url : pageParam.url
		};
		if (executor)
			cfg.executor = executor;

		pageParam
		null;
		title = null;
		return this.showPage(cfg);
	};

	Model.prototype.showPage = function(options) {
		this.loginCheck();
		return this.shellImpl.showPage(options);
	};

	Model.prototype._doClosePage = function(event) {
		var id = event.data.closePageXid;

		if (this.current === id)
			this.current = null;
		delete this.openedPage[id];
		if (this.hasListener('onClosePage')) {
			var eData = {
				source : this,
				pageID : id
			};
			this.fireEvent('onClosePage', eData);
		}
		id = null;
	};

	Model.prototype.closePage = function(pageID) {
		return this.shellImpl.closePage(pageID, true);
	};

	Model.prototype.logout = function(config) {
		if (this._updateDateTimeHandle)
			window.clearInterval(this._updateDateTimeHandle);

		if (this._updateGGHandle)
			window.clearInterval(this._updateGGHandle);

		if (this._updateAuditHandle)
			window.clearInterval(this._updateAuditHandle);

		if (this.urllogin) {
			// history.back();
			var len = history.length - 1;
			history.go(-len);
		} else {
			config = config || {};
			if (config.ignoreConfirm || confirm("请您注意，是否打开的功能都保存了，关闭系统将导致没有保存的数据丢失！\r\r您确定要退出吗？")) {
				if (!this.isAgent) {
					this.__logined = false;
					this.closeAllAgent();// 关闭所有代理
					// this.closeAllPage();
					this.closeMainPage();
					this._doLogout();
					this.manualLogin = true;// 注销时避免自动登录情况显示不了登录页面，转手动登录
					this.logid = "";

					localStorage.setItem('pdaautoLogin', false);

					this.showLoginDialog();

				} else {
					window.close();// 代理时关闭功能
				}
			} else {
				if ((typeof event !== "undefined") && event.type == "hashchange") {
					history.forward();
				}
			}
		}
	};

	Model.prototype._doLogout = function() {
		// 2019-03-02 增加
		this.logined = false;
		// this.closeAllPage();

		justep.Shell.closeAllOpendedPages();

		// 2019-01-05 22:22 李趣芸 退出登录 增加 清除相关缓存信息
		localStorage.removeItem("erpscanuserlogin");// 用户登录刷新的信息
		localStorage.removeItem("rolesetdata");// mlfcommon/permission/rolemanage.w
		// 使用的rolesetdata缓存
		// 更新操作时间
		this.updateLoginoutLog();

		this.comp("companyData").clear();
		this.comp("userinfoData").clear();

		if (this._updateDateTimeHandle)
			window.clearInterval(this._updateDateTimeHandle);

		if (this._updateGGHandle)
			window.clearInterval(this._updateGGHandle);

		if (this._updateAuditHandle)
			window.clearInterval(this._updateAuditHandle);

		this._updateDateTimeHandle = null;
		this._updateGGHandle = null;
		this._updateAuditHandle = null;

	};

	Model.prototype._doBind = function() {
		var $root = $(this.getRootNode());
		var binds = this._cfg.binds;
		if ($.isArray(binds)) {
			for (var i = 0; i < binds.length; i++) {
				$root.find(binds[i].selector).on(binds[i].event, binds[i].func);
			}
		}
		this.bindUpdateDateTime();
		binds = null;
	};

	Model.prototype.clearFunctionTree = function() {
		if (this.hasListener('onClearFunctionTree')) {
			var eData = {
				source : this
			};
			this.fireEvent('onClearFunctionTree', eData);
		}
	};

	Model.prototype.getFunctions = function() {
		return this.getContext().data.functionTree.menu;
	};

	var param2pageMapping = function(param) {
		var ret = $.extend({}, param);
		if (param.title) {
			delete ret.title;
			ret.extra = {};
			ret.extra.title = param.title;
		}
		return ret;
	};

	var isJ = function isJ(url) {
		var i = url.indexOf('?');
		if (i != -1)
			url = url.substring(0, i);
		return /\.j$/.test(url.toLowerCase());
	};

	var isIframeFunc = function(func) {
		var isUI2 = true;
		try {
			isUI2 = justep.URL.isUI2(func.url);
		} catch (err) {
		}
		return !isUI2 || func.type == "iframe" || isJ(func.url);
	};

	var isIframeUrl = function(url) {
		var isUI2 = true;
		try {
			isUI2 = justep.URL.isUI2(url);
		} catch (err) {
		}
		return !isUI2 || isJ(url);
	};

	var getIframeFuncUrl = function(url, iframeFuncURL) {
		return iframeFuncURL + ((iframeFuncURL.indexOf("?") > 0) ? "&" : "?") + "iframeFunc=" + encodeURIComponent(url);
	};

	var createPageKey = function(func, iframeFuncURL) {
		// 2019-03-02 增加 将关键字改为链接解决在tomcat下没有显示标题原因
		var keyurl = func.url;
		var index = keyurl.lastIndexOf("/");
		var key = keyurl.substring(index + 1, keyurl.length - 2);

		// if (isIframeFunc(func)) {
		// func.url = getIframeFuncUrl(func.url, iframeFuncURL);
		// func.type = "iframe";
		// }
		func['pageKey'] = key;
		return key;
	};
	// 二级菜单
	var func2pageMapping = function(func, iframeFuncURL) {
		var ret = {
			extra : {}
		};
		// 2019-03-02 增加 将关键字改为链接解决在tomcat下没有显示标题原因
		var keyurl = func.url;
		var index = keyurl.lastIndexOf("/");
		var key = keyurl.substring(index + 1, keyurl.length - 2);
		ret["xid"] = key;

		$.each(func, function(k, v) {
			if ("label" === k)
				ret.extra.title = v;
			else if ($.inArray(k, [ 'activity', 'process', 'url' ]) > -1)
				ret[k] = v;
			else if ("$children" != k)
				ret.extra[k] = v;
		});
		return ret;
	};

	Model.prototype.createAgent = function() {
		var agentList = this.getContext().data.agentList;
		if (agentList && agentList.value && agentList.value.length > 0) {
			$(selectors.agent).show();
			if (this.hasListener('onLoadAgent')) {
				var eData = {
					source : this,
					agents : agentList.value
				};
				this.fireEvent('onLoadAgent', eData);
			}
		} else
			$(selectors.agent).hide();
	};
	Model.prototype.getMenu = function() {
		return menu;
	};
	Model.prototype.createFunctionTree = function() {
		var menu = this.getMenu();
		// 功能增加到maaping
		var pageMappings = {};
		pageMappings[mainPageKey] = param2pageMapping(this._cfg.main);
		pageMappings[changePasswordPageKey] = param2pageMapping(this._cfg.changePassword);
		pageMappings[changeRoleSetKey] = param2pageMapping(this._cfg.changeRoleSet);
		pageMappings[changeRolePresetSetKey] = param2pageMapping(this._cfg.changeRolePresetSet);
		pageMappings[itemeditKey] = param2pageMapping(this._cfg.itemedit);
		pageMappings[itemBomshowKey] = param2pageMapping(this._cfg.itemBomshow);
		pageMappings[customereditKey] = param2pageMapping(this._cfg.customeredit);
		pageMappings[staffeditKey] = param2pageMapping(this._cfg.staffedit);
		pageMappings[stockturnKey] = param2pageMapping(this._cfg.stockturn);
		pageMappings[splitseditKey] = param2pageMapping(this._cfg.splitsedit);
		pageMappings[splitsdetailKey] = param2pageMapping(this._cfg.splitsdetail);
		pageMappings[storeineditKey] = param2pageMapping(this._cfg.storeinedit);

		pageMappings[storeinedit_01Key] = param2pageMapping(this._cfg.storeinedit_01);

		pageMappings[storeindetailKey] = param2pageMapping(this._cfg.storeindetail);
		pageMappings[storeouteditKey] = param2pageMapping(this._cfg.storeoutedit);
		pageMappings[storeoutdetailKey] = param2pageMapping(this._cfg.storeoutdetail);
		pageMappings[storeinouteditKey] = param2pageMapping(this._cfg.storeinoutedit);
		pageMappings[storeinoutdetailKey] = param2pageMapping(this._cfg.storeinoutdetail);
		pageMappings[storeoutineditKey] = param2pageMapping(this._cfg.storeoutinedit);
		pageMappings[storeoutindetailKey] = param2pageMapping(this._cfg.storeoutindetail);
		pageMappings[storemoveeditKey] = param2pageMapping(this._cfg.storemoveedit);
		pageMappings[storemovedetailKey] = param2pageMapping(this._cfg.storemovedetail);
		pageMappings[storecheckeditKey] = param2pageMapping(this._cfg.storecheckedit);
		pageMappings[storecheckdetailKey] = param2pageMapping(this._cfg.storecheckdetail);
		pageMappings[reportlosseditKey] = param2pageMapping(this._cfg.reportlossedit);
		pageMappings[reportlossdetailKey] = param2pageMapping(this._cfg.reportlossdetail);
		pageMappings[itembegineditKey] = param2pageMapping(this._cfg.itembeginedit);
		pageMappings[itembegindetailKey] = param2pageMapping(this._cfg.itembegindetail);
		pageMappings[storeinreportdetailKey] = param2pageMapping(this._cfg.storeinreportdetail);
		pageMappings[storeoutreportdetailKey] = param2pageMapping(this._cfg.storeoutreportdetail);

		// 工单
		pageMappings[ordereditKey] = param2pageMapping(this._cfg.orderedit);
		pageMappings[ordernewKey] = param2pageMapping(this._cfg.ordernew);
		pageMappings[orderdetailKey] = param2pageMapping(this._cfg.orderdetail);
		pageMappings[summaryorderdetailKey] = param2pageMapping(this._cfg.summaryorderdetail);
		pageMappings[summarystepdetailKey] = param2pageMapping(this._cfg.summarystepdetail);
		pageMappings[stepeditKey] = param2pageMapping(this._cfg.stepedit);
		pageMappings[stepnewKey] = param2pageMapping(this._cfg.stepnew);
		pageMappings[hhstepeditKey] = param2pageMapping(this._cfg.hhstepedit);
		pageMappings[hhstepnewKey] = param2pageMapping(this._cfg.hhstepnew);
		pageMappings[staffauthKey] = param2pageMapping(this._cfg.staffauth);
		pageMappings[printitemKey] = param2pageMapping(this._cfg.printitem);
		pageMappings[roleauthKey] = param2pageMapping(this._cfg.roleauth);
		pageMappings[returncontentKey] = param2pageMapping(this._cfg.returncontent);

		pageMappings[purchaseordereditKey] = param2pageMapping(this._cfg.purchaseorderedit);
		pageMappings[purchaseorderedit_01Key] = param2pageMapping(this._cfg.purchaseorderedit_01);
		pageMappings[purchaseorderdetailKey] = param2pageMapping(this._cfg.purchaseorderdetail);

		pageMappings[purchaseedit_01Key] = param2pageMapping(this._cfg.purchaseedit_01);

		pageMappings[purchaseeditKey] = param2pageMapping(this._cfg.purchaseedit);
		pageMappings[purchasedetailKey] = param2pageMapping(this._cfg.purchasedetail);
		pageMappings[salesordereditKey] = param2pageMapping(this._cfg.salesorderedit);
		pageMappings[salesorderedit_01Key] = param2pageMapping(this._cfg.salesorderedit_01);
		pageMappings[salesorderdetailKey] = param2pageMapping(this._cfg.salesorderdetail);
		pageMappings[prodrequisitioneditKey] = param2pageMapping(this._cfg.prodrequisitionedit);
		pageMappings[prodrequisitiondetailKey] = param2pageMapping(this._cfg.prodrequisitiondetail);
		pageMappings[otherineditKey] = param2pageMapping(this._cfg.otherinedit);
		pageMappings[otherindetailKey] = param2pageMapping(this._cfg.otherindetail);
		pageMappings[otherouteditKey] = param2pageMapping(this._cfg.otheroutedit);
		pageMappings[otheroutdetailKey] = param2pageMapping(this._cfg.otheroutdetail);

		pageMappings[delivereditKey] = param2pageMapping(this._cfg.deliveredit);
		pageMappings[deliverdetailKey] = param2pageMapping(this._cfg.deliverdetail);

		// 排产新增
		pageMappings[scheduleordernewKey] = param2pageMapping(this._cfg.scheduleordernew);
		pageMappings[scheduleordereditKey] = param2pageMapping(this._cfg.scheduleorderedit);
		pageMappings[scheduleorderdetailKey] = param2pageMapping(this._cfg.scheduleorderdetail);
		pageMappings[productinnewKey] = param2pageMapping(this._cfg.productinnew);
		pageMappings[productindetailKey] = param2pageMapping(this._cfg.productindetail);

		pageMappings[dayinKey] = param2pageMapping(this._cfg.dayinedit);
		pageMappings[dayoutKey] = param2pageMapping(this._cfg.dayoutedit);

		pageMappings[dayinoutdetail] = param2pageMapping(this._cfg.dayinoutdetail);
		pageMappings[accountbillineditKey] = param2pageMapping(this._cfg.accountbillinedit);
		pageMappings[accountbillouteditKey] = param2pageMapping(this._cfg.accountbilloutedit);
		pageMappings[accountbillindetailKey] = param2pageMapping(this._cfg.accountbillindetail);
		pageMappings[accountbilloutdetailKey] = param2pageMapping(this._cfg.accountbilloutdetail);
		pageMappings[recpaycustomerdetailKey] = param2pageMapping(this._cfg.recpaycustomerdetail);

		pageMappings[honourbillineditKey] = param2pageMapping(this._cfg.honourbillinedit);
		pageMappings[honourbillouteditKey] = param2pageMapping(this._cfg.honourbilloutedit);

		pageMappings[honourbillindetailKey] = param2pageMapping(this._cfg.honourbillindetail);
		pageMappings[honourbilloutdetailKey] = param2pageMapping(this._cfg.honourbilloutdetail);

		// 委外加工
		pageMappings[processineditKey] = param2pageMapping(this._cfg.processinedit);
		pageMappings[processindetailKey] = param2pageMapping(this._cfg.processindetail);
		pageMappings[processouteditKey] = param2pageMapping(this._cfg.processoutedit);
		pageMappings[processoutdetailKey] = param2pageMapping(this._cfg.processoutdetail);

		pageMappings[outsourcingeditKey] = param2pageMapping(this._cfg.outsourcingedit);
		pageMappings[outsourcingdetailKey] = param2pageMapping(this._cfg.outsourcingdetail);
		pageMappings[outsourcingineditKey] = param2pageMapping(this._cfg.outsourcinginedit);
		pageMappings[outsourcingindetailKey] = param2pageMapping(this._cfg.outsourcingindetail);
		pageMappings[outsourcingouteditKey] = param2pageMapping(this._cfg.outsourcingoutedit);
		pageMappings[outsourcingoutdetailKey] = param2pageMapping(this._cfg.outsourcingoutdetail);

		pageMappings[prodrequisitionbackeditKey] = param2pageMapping(this._cfg.prodrequisitionbackedit);
		pageMappings[prodrequisitionbackdetailKey] = param2pageMapping(this._cfg.prodrequisitionbackdetail);
		// 合同新增
		pageMappings[storecontracteditKey] = param2pageMapping(this._cfg.storecontractedit);
		pageMappings[contracttemplacteditKey] = param2pageMapping(this._cfg.contracttemplactedit);

		// 审批流程
		pageMappings[auditeditKey] = param2pageMapping(this._cfg.auditedit);

		pageMappings[customerbilleditKey] = param2pageMapping(this._cfg.customerbilledit);
		pageMappings[customerbilldetailKey] = param2pageMapping(this._cfg.customerbilldetail);

		pageMappings[transfereditKey] = param2pageMapping(this._cfg.transferedit);
		pageMappings[transferdetailKey] = param2pageMapping(this._cfg.transferdetail);

		pageMappings[stageoutsourcingeditKey] = param2pageMapping(this._cfg.stageoutsourcingedit);
		pageMappings[stageoutsourcingdetailKey] = param2pageMapping(this._cfg.stageoutsourcingdetail);
		pageMappings[stageoutsourcingineditKey] = param2pageMapping(this._cfg.stageoutsourcinginedit);
		pageMappings[stageoutsourcingindetailKey] = param2pageMapping(this._cfg.stageoutsourcingindetail);

		pageMappings[auditmanageKey] = param2pageMapping(this._cfg.auditmanage);

		pageMappings[qualitydatalistKey] = param2pageMapping(this._cfg.qualitydatalist);

		pageMappings[salesorderconfigKey] = param2pageMapping(this._cfg.salesorderconfig);

		pageMappings[quotationeditKey] = param2pageMapping(this._cfg.quotationedit);
		pageMappings[quotationdetailKey] = param2pageMapping(this._cfg.quotationdetail);

		pageMappings[applymaterialeditKey] = param2pageMapping(this._cfg.applymaterialedit);
		pageMappings[applymaterialdetailKey] = param2pageMapping(this._cfg.applymaterialdetail);

		pageMappings[applypaymenteditKey] = param2pageMapping(this._cfg.applypaymentedit);
		pageMappings[applypaymentdetailKey] = param2pageMapping(this._cfg.applypaymentdetail);

		pageMappings[applyinvoiceeditKey] = param2pageMapping(this._cfg.applyinvoiceedit);
		pageMappings[applyinvoicedetailKey] = param2pageMapping(this._cfg.applyinvoicedetail);

		pageMappings[applyleaveeditKey] = param2pageMapping(this._cfg.applyleaveedit);
		pageMappings[applyleavedetailKey] = param2pageMapping(this._cfg.applyleavedetail);

		pageMappings[applyovertimeeditKey] = param2pageMapping(this._cfg.applyovertimeedit);
		pageMappings[applyovertimedetailKey] = param2pageMapping(this._cfg.applyovertimedetail);

		pageMappings[applyovertimetotaleditKey] = param2pageMapping(this._cfg.applyovertimetotaledit);
		pageMappings[applyovertimetotaldetailKey] = param2pageMapping(this._cfg.applyovertimetotaldetail);

		pageMappings[applyiteminfoeditKey] = param2pageMapping(this._cfg.applyiteminfoedit);
		pageMappings[applyiteminfodetailKey] = param2pageMapping(this._cfg.applyiteminfodetail);

		pageMappings[orderprofitdetailKey] = param2pageMapping(this._cfg.orderprofitdetail);

		// 发票商品入出库
		pageMappings[invoicestoreineditKey] = param2pageMapping(this._cfg.invoicestoreinedit);
		pageMappings[invoicestoreindetailKey] = param2pageMapping(this._cfg.invoicestoreindetail);
		pageMappings[invoicestoreinouteditKey] = param2pageMapping(this._cfg.invoicestoreinoutedit);
		pageMappings[invoicestoreinoutdetailKey] = param2pageMapping(this._cfg.invoicestoreinoutdetail);
		pageMappings[invoiceinshowdetailKey] = param2pageMapping(this._cfg.invoiceinshowdetail);

		// 设备保养登记
		pageMappings[devicemaintenanceeditKey] = param2pageMapping(this._cfg.devicemaintenanceedit);
		pageMappings[devicemaintenancedetailKey] = param2pageMapping(this._cfg.devicemaintenancedetail);
		pageMappings[devicepropertyKey] = param2pageMapping(this._cfg.deviceproperty);

		pageMappings[maintainApplyEditKey] = param2pageMapping(this._cfg.maintainApplyEdit);
		pageMappings[maintainApplyDetailKey] = param2pageMapping(this._cfg.maintainApplyDetail);
		pageMappings[maintainDetailKey] = param2pageMapping(this._cfg.maintainDetail);
		pageMappings[maintainEditKey] = param2pageMapping(this._cfg.maintainEdit);

		pageMappings[storeoutapplyeditKey] = param2pageMapping(this._cfg.storeoutapplyedit);
		pageMappings[storeoutapplydetailKey] = param2pageMapping(this._cfg.storeoutapplydetail);
		
		pageMappings[failureModeTotalKey] = param2pageMapping(this._cfg.failureModeTotal);
		
		pageMappings[sealEditKey] = param2pageMapping(this._cfg.sealEdit);
		pageMappings[sealDetailKey] = param2pageMapping(this._cfg.sealDetail);
		
		pageMappings[storeinshowdetailKey] = param2pageMapping(this._cfg.storeinshowdetail);
		
		pageMappings[salesordercombineeditKey] = param2pageMapping(this._cfg.salesordercombineedit);
		pageMappings[salesordercombinedetailKey] = param2pageMapping(this._cfg.salesordercombinedetail);
		
		pageMappings[schedulePickEditKey] = param2pageMapping(this._cfg.schedulePickEdit);
		pageMappings[schedulePickDetailKey] = param2pageMapping(this._cfg.schedulePickDetail);

		if (this._cfg.waitTask && this._cfg.waitTask.enabled)
			pageMappings[this._cfg.waitTask.xid] = param2pageMapping(this._cfg.waitTask);
		// 遍历功能树生成mapping
		var model = this;
		this.eachFuncs(menu.$children, function(params) {

			if (model.isFunc(params.func)) {
				var func = params.func;
				var key = createPageKey(func, model._cfg.iframeFuncURL);
				pageMappings[key] = func2pageMapping(func, model._cfg.iframeFuncURL);
			}
		});
		this.shellImpl.addPageMappings(pageMappings);
		// 清理功能树
		this.clearFunctionTree();

		if (this.hasListener('onLoadFunctionTree')) {
			var eData = {
				source : this,
				funcs : menu.$children
			};
			this.fireEvent('onLoadFunctionTree', eData);
		}

		menu = null;
		pageMappings = null;
		model = null;
	};

	Model.prototype.eachFuncs = function(funcs, fn, layer, parent) {

		var iLayer = layer || 0;
		if (!$.isArray(funcs) || !$.isFunction(fn))
			return;
		for (var i = 0; i < funcs.length; i++) {
			var func = funcs[i];
			fn({
				layer : iLayer,
				parent : parent,
				func : funcs[i],
				index : i
			});
			this.eachFuncs(func.$children, fn, (iLayer + 1), func);
		}
	};

	Model.prototype.isMainPage = function(pid) {
		return pid === this._cfg.main.xid;
	};

	Model.prototype.showLoginDialog = function() {
		this.beforeLogin();
		if (this._cfg.needLogin) {
			var local = window.location.href;
			var index = local.indexOf("#");
			var newurl = local.substring(0, index);
			if (newurl !== "") {
				window.location.href = newurl;
			} else {
				this.getLoginDialog().open();

			}
			local = null;
			index = null;
			newurl = null;
		} else {
			this.afterLogin();
		}
	};

	Model.prototype.closeMainPage = function() {
		if (this.shellImpl) {
			this.closePage(mainPageKey);
			this.shellImpl.removeMainPage();
		}
	};

	Model.prototype.closeAllPage = function() {
		if (this.shellImpl) {
			this.shellImpl.closeAllOpendedPages();
		}
	};

	Model.prototype.openAgent = function(executor) {
		var config = justep.Util.clone(this._cfg.agent);
		config.executor = executor;
		config.agent = 'true';
		this.bsessionid && (config.bsessionid = this.bsessionid);
		var context = this.getContext();
		var skin = context.getSkin();
		skin && (config.$skin = skin);
		if ($.isFunction(context.getLanguage)) {
			var lang = context.getLanguage();
			lang && (config.language = lang);
			lang = null;
		}
		var url = new justep.URL(window.location.protocol + '//' + window.location.host + window.location.pathname);
		url.setParam(config);

		var opener = window.open(url.toString(), executor);
		this.openers.push(opener);

		config = null;
		context = null;
		skin = null;
		url = null;

		return opener;
	};

	Model.prototype.closeAllAgent = function() {
		for (var i = 0; i < this.openers.length; i++) {
			this.openers[i].close();
		}
		this.openers = [];
	};

	Model.prototype.beforeLogin = function() {
		this.clearFunctionTree();
		// this.closeAllPage();
		justep.Shell.closeAllOpendedPages();
		// 登录前
		if (this.hasListener('onBeforeLogin')) {
			this.fireEvent('onBeforeLogin', {
				source : this
			});
		}
	};

	Model.prototype.afterLogin = function() {
		this.__logined = true;
		var context = this.getContext();
		// this.isDebugMode = context.isDebug();
		var me = this;
		// 控制开发模式刷新
		// $(selectors.reload)[this.isDebugMode ? 'show' : 'hide']();
		// 显示登陆者名称
		$(selectors.username).html(this.comp("userinfoData").getValue("realname"));// 2020-11-11
		// this.userName
		// 登录后完成
		if (this.hasListener('onLogin')) {
			var eData = {
				source : this,
				context : context
			};
			this.fireEvent('onLogin', eData);
		}
		// 初始化代理
		this.createAgent();
		// 初始化功能树
		this.createFunctionTree();

		// 加载主页面
		var loginDtdResolve = function() {
			if (me.loginDtd)
				me.loginDtd.resolve();
		};
		if (this._cfg.main.show)
			this.showPage(mainPageKey).done(loginDtdResolve);
		else
			loginDtdResolve();

		this.bindUpdateGG();

		var auditflowData = this.comp("auditflowData");
		auditflowData.clear();
		auditflowData.filters.setVar("companyid", this.companyid);
		auditflowData.refreshData();
		if (auditflowData.count() > 0) {
			this.bindUpdateAudit();
		}

	};

	Model.prototype.isFunc = function(func) {
		return !!func.url;
	};

	Model.prototype.getPages = function() {
		if (!this._pages) {
			var node = this.getPagesNode();
			if (node)
				this._pages = this.comp(node);
		}
		return this._pages;
	};

	Model.prototype.getPagesNode = function() {
		var $root = $(this.getRootNode());
		var $pages = $root.find(selectors.pages);
		if ($pages.size() > 0) {
			return $pages[0];
		}
	};

	Model.prototype._doInit = function() {
		// 兼容UI功能
		justep.Portal.isPortal2 = true;

		if (this.hasListener('onInit')) {
			var eData = {
				source : this,
				config : this._cfg
			};
			this.fireEvent('onInit', eData);
		}

		this.shellImpl = this.createShellImpl();
		this.shellImpl.on('onCallPortal', this._doCallPortal, this);
		this.shellImpl.on('onPageActive', this._doShowPage, this);
		this.shellImpl.on('onAfterPageClose', this._doClosePage, this);
	};

	Model.prototype._doCallPortal = function(event) {
		var fn = event.fn, param = event.param;
		if ($.isFunction(this[fn])) {
			event.result = this[fn].call(this, param);
		}
		fn = null;
		param = null;
	};

	Model.prototype.createShellImpl = function() {
		return new ShellImpl(this, {
			contentsXid : this.getPagesNode()
		});
	};

	// 2019-01-05 22:16 李趣芸 modelLoad代码重新调整为页面可刷新
	Model.prototype.modelParamsReceive = function(event) {
		var me = this;
		// this.closeAllPage();
		justep.Shell.closeAllOpendedPages();

		window.onunload = function() {
			if (me.logined && !me._cfg.isDebugMode) {
				me._doLogout();
			}
		};

		// 如果不是IE9 , 可以设置onbeforeunload isDebugMode=用户刷新或关闭需要提示用户
		if (!justep.Browser.IE9) {
			window.onbeforeunload = function() {
				if (me.logined && !me._cfg.isDebugMode)
					return "刷新或关闭会导致未保存的数据丢失?";
			};
		}

		var userLocal = localStorage.getItem("erpscanuserlogin");

		// 获取代理商码
		var rcode = this.getContext().getRequestParameter("rcode");
		if (rcode !== "") {
			rcode = unescape(rcode);
			var rcodestr = String.fromCharCode(rcode.charCodeAt(0) - rcode.length);
			for (var i = 1; i < rcode.length; i++) {
				rcodestr += String.fromCharCode(rcode.charCodeAt(i) - rcodestr.charCodeAt(i - 1));
			}
			localStorage.setItem("recommed", rcodestr);
		}

		var code = this.getContext().getRequestParameter("code");// 统一登录app
		var uname = "";
		var pwd = "";
		var icode = "";

		if (code !== "") { // 统一登录app 信息读取
			code = unescape(code);

			this.hurl = this.getContext().getRequestParameter("hurl");// 2019-04-13

			var codestr = String.fromCharCode(code.charCodeAt(0) - code.length);
			for (var i = 1; i < code.length; i++) {
				codestr += String.fromCharCode(code.charCodeAt(i) - codestr.charCodeAt(i - 1));
			}
			var codearr = codestr.split("&");
			if (codearr.length === 3) {
				uname = codearr[0];
				pwd = codearr[1];
				icode = codearr[2];
				this.urllogin = true;
			}
			codestr = null;

		}
		if (this.urllogin) {// app登录
			var userinfoData = me.comp("userinfoData");
			userinfoData.clear();
			userinfoData.setFilter("filter1", "(username='" + uname + "' or phone='" + uname + "') and password='" + pwd + "'");
			userinfoData.refreshData();
			if (userinfoData.count() > 0) {
				var logincheckData = me.comp("logincheckData");
				logincheckData.clear();
				logincheckData.setFilter("lcfilter", "userid='" + userinfoData.getValue("userid") + "' and appcode='" + icode + "'");
				logincheckData.refreshData();

				if (logincheckData.count() > 0) {
					me.userName = userinfoData.getValue("username");
					me.logined = true;
					logincheckData.deleteData();

					justep.Baas.sendRequest({
						"url" : "/erpscan/userrelevant",
						"action" : "checklogin",
						"async" : true,
						"params" : {
							"username" : uname,
							"password" : pwd,
							"platform" : justep.Browser.deviceType,
							"addlog" : false
						// 不增加日志
						},
						"success" : function(result) {
							me.loginresult = result;
						}
					});

					var companyData = me.comp("companyData");
					companyData.clear();
					// 2019-01-05 修改增加条件
					if (parseInt(userinfoData.getValue("role")) > 2) {// 角色大于2为企业端用户，后台用户没有公司id
						companyData.setFilter("filter1", "id='" + userinfoData.getValue("companyid") + "'");
						companyData.refreshData();
						// 2019-06-20 增加
						$(document.getElementById("manpayserivceid")).show();
					} else {
						$(document.getElementById("manpayserivceid")).hide();
					}

					// me.loginresult = result;

					if (me.logid === "") {
						me.addLoginLog();
					} else {
						var loginlogData = me.comp("loginlogData");
						loginlogData.clear();
						loginlogData.setFilter("filter1", "id='" + me.logid + "'");
						loginlogData.refreshData();
						loginlogData = null;
					}
					me.beforeLogin();
					me.logined = true;
					me.getCurPermission();
					me.getCurDataRule();
					me.afterLogin();

					companyData = null;

				} else {
					// justep.Util.hint("登录信息已过期，请重新登录。", {
					// type : "danger",
					// position : "middle",
					// delay : 5000
					// });
					history.back();
					return;
				}
			} else {
				// justep.Util.hint("登录帐户与密码不正确，登录失败。", {
				// type : "danger",
				// position : "middle"
				// });
				history.back();
				return;
			}
			userinfoData = null;
		} else if (me._cfg.isDebugMode && userLocal !== null) {// 电脑端登录
			var userdata = JSON.parse(userLocal);
			var username = userdata.username;
			var password = userdata.password;
			this.logid = userdata.logid;

			justep.Baas.sendRequest({
				"url" : "/erpscan/userrelevant",
				"action" : "checklogin",
				"async" : false,
				"params" : {
					"username" : username,
					"password" : password,
					"platform" : justep.Browser.deviceType,
					"addlog" : false
				// 不增加日志
				},
				"success" : function(result) {
					if (result.message === "") {
						me.userName = username;
						me.loginresult = result;
						var lmessage = result.lmessage;

						var userinfoData = me.comp("userinfoData");
						userinfoData.clear();
						userinfoData.setFilter("filter1", "username='" + username + "'");
						userinfoData.refreshData();

						var companyData = me.comp("companyData");
						companyData.clear();
						// 2019-01-05 修改增加条件
						if (parseInt(userinfoData.getValue("role")) > 2) {// 角色大于2为企业端用户，后台用户没有公司id
							companyData.setFilter("filter1", "id='" + userinfoData.getValue("companyid") + "'");
							companyData.refreshData();
						}

						if (me.logid === "") {
							me.addLoginLog();
						} else {
							var loginlogData = me.comp("loginlogData");
							loginlogData.clear();
							loginlogData.setFilter("filter1", "id='" + me.logid + "'");
							loginlogData.refreshData();
							loginlogData = null;
						}
						me.beforeLogin();
						me.logined = true;
						me.getCurPermission();
						me.getCurDataRule();
						me.afterLogin();

						userinfoData = null;
						companyData = null;

					} else {
						localStorage.removeItem("erpscanuserlogin");
						me.showLoginDialog();
					}

					userdata = null;
					username = null;
					password = null;

				},
				"error" : function() {
					localStorage.removeItem("erpscanuserlogin");
					me.showLoginDialog();
				}
			});
		} else {
			this.showLoginDialog();
		}

	};

	// 用windowdialog调用登录页面
	Model.prototype.getLoginDialog = function() {
		if (!this._loginDlg) {
			this._loginDlg = new WindowDialog({
				src : this._cfg.loginURL,
				parentNode : this.getRootNode(),
				forceRefreshOnOpen : true
			});
			this._loginDlg.on('onReceive', this.loginDialogReceive, this);
		}
		return this._loginDlg;
	};
	// 登录成功接收数据
	Model.prototype.loginDialogReceive = function(event) {
		var username = event.data.username;
		var password = event.data.password;

		this.logid = event.data.logid;
		this.loginresult = event.data.loginresult;

		var userinfoData = this.comp("userinfoData");
		userinfoData.clear();
		userinfoData.setFilter("filter1", "(username='" + username + "' or phone='" + username + "') and password='" + password + "'");
		userinfoData.refreshData();

		this.userName = userinfoData.getValue("username");
		var companyData = this.comp("companyData");
		companyData.clear();
		// 2019-01-05 修改增加条件
		if (parseInt(userinfoData.getValue("role")) > 2) {// 角色大于2为企业端用户，后台用户没有公司id
			companyData.setFilter("filter1", "id='" + userinfoData.getValue("companyid") + "'");
			companyData.refreshData();
			// 2019-06-20 增加
			$(document.getElementById("manpayserivceid")).show();
		} else {
			$(document.getElementById("manpayserivceid")).hide();
		}

		if (this.logid === "") {
			this.addLoginLog();
		} else {
			var loginlogData = this.comp("loginlogData");
			loginlogData.clear();
			loginlogData.setFilter("filter1", "id='" + this.logid + "'");
			loginlogData.refreshData();
		}
		this.logined = true;

		this.beforeLogin();
		this.getCurPermission();
		this.getCurDataRule();
		this.afterLogin();

		username = null;
		password = null;

		userinfoData = null;
		companyData = null;
	};

	// 获取当前有户菜单按钮功能权限
	Model.prototype.getCurPermission = function() {

		var userinfoData = this.comp("userinfoData");

		var permissiondata = this.comp("permissiondata");

		permissiondata.clear();
		// 获取当前有户菜单按钮功能权限
		justep.Baas.sendRequest({
			"url" : "/erpscan/permission",
			"action" : "getMenuPermissionData",
			"async" : false,
			"params" : {
				"userid" : userinfoData.getValue("userid"),
				"companyid" : userinfoData.getValue("companyid"),
				"role" : userinfoData.getValue("role"),
				"roletype" : userinfoData.getValue("roletype")
			},
			"success" : function(param) {
				if (param !== null) {
					permissiondata.loadData(param);
				}
			}
		});

		if (userinfoData.getValue("companyid") !== undefined) {
			var me = this;
			var sysconfigureData = this.comp("sysconfigureData");
			var companyData = this.comp("companyData");

			// 2020-12-05
			var roletype = userinfoData.getValue("roletype");
			var userid = userinfoData.getValue("userid");
			var companyid = userinfoData.getValue("companyid");

			var cfilter = 0;
			var hfilter = 0;
			this.housefilter = "1=1";
			this.customerfilter = "1=1";

			var hasstore = true;
			var hascustomer = true;
			var crowdata = permissiondata.find([ "fvalue" ], [ "alldatas:hasstore" ]);
			if (crowdata.length === 0) {
				hasstore = false;
			}

			crowdata = permissiondata.find([ "fvalue" ], [ "alldatas:hascustomer" ]);
			if (crowdata.length === 0) {
				hascustomer = false;
			}

			this.billtypes = "";
			var tempquality1 = 0;
			for (var r = 1; r <= 8; r++) {
				crowdata = permissiondata.find([ "fvalue" ], [ "qualitieddata:show" + r ]);
				tempquality1 = companyData.getValue("quality" + r);
				if (tempquality1 == 1 && crowdata.length > 0) {
					this.billtypes = this.billtypes + r + ",";
				}
			}

			if (roletype === "2" && (hasstore === false || hascustomer === false)) {
				justep.Baas.sendRequest({
					"url" : "/erpscan/baseoperate/permissionlimit",
					"action" : "getPermissionLimit",
					"async" : false,
					"params" : {
						"companyid" : companyid,
					},
					"success" : function(ret) {
						cfilter = ret.cfilter;
						hfilter = ret.hfilter;
					}
				});

				if (cfilter > 0 && hascustomer === false) {
					this.customerfilter = "companyid = '" + companyid + "' and ( usercount = 0 or (select 1 from t_customer_userid where customerid=customer.customerid and userid = '" + userid
							+ "' limit 1) ) ";
				} else {
					cfilter = 0;// =0不受限制,=1受限制
				}
				if (hfilter > 0 && hasstore === false) {
					this.housefilter = "companyid = '" + companyid + "' and ( usercount = 0 or  (select 1 from t_storehouse_userid where houseid=storehouse.houseid and userid = '" + userid
							+ "' limit 1)  ) ";
				} else {
					hfilter = 0;// =0不受限制,=1受限制
				}
			}
			// 旧方式仓库与往来单位限条件方式
			localStorage.setItem(userid + "housefilter", this.housefilter);
			localStorage.setItem(userid + "customerfilter", this.customerfilter);

			// 2025-06-23 新方式以传参方式加条件
			localStorage.setItem(userid + "cfilter", cfilter);
			localStorage.setItem(userid + "hfilter", hfilter);

			if (justep.Browser.isWeChat) {// wechataccountData
				var wechataccountData = this.comp("wechataccountData");
				wechataccountData.clear();
				if (companyData.getValue("useweixin") === "2") {
					wechataccountData.setFilter("wfilter", "wechatid='" + companyData.getValue("wechatid") + "'");
					wechataccountData.refreshData();
				}
				if (companyData.getValue("useweixin") === "3") {
					wechataccountData.setFilter("wfilter", "companyid='" + userinfoData.getValue("companyid") + "'");
					wechataccountData.refreshData();
				}

				if (wechataccountData.count() === 0 && sysconfigureData.getValue("wechatid") !== undefined) {
					wechataccountData.setFilter("wfilter", "wechatid='" + sysconfigureData.getValue("wechatid") + "'");
					wechataccountData.refreshData();
				}
				if (wechataccountData.count() > 0) {
					base.config.weixinJSApiUrl = "/baas/erpscan/weixin/weixin/jsapi";
					base.config.wxUserInfoUrl = "/baas/erpscan/weixin/weixin/userinfo";
					base.config.wxAppId = wechataccountData.getValue("appId");

					justep.Baas.sendRequest({
						"url" : "/erpscan/weixin/weixin",
						"action" : "checkWechataccount",
						"async" : false,
						"params" : {
							"wechataccountData" : wechataccountData.getCurrentRow().toJson()
						},
						"success" : function(param) {
							if (param.state === "0") {
								base.init();
							} else {
								me.comp("message").show({
									message : "微信名称为：<" + wechataccountData.getValue("wechatname") + "> 的微信公众号的配置数据不正确，这将影响调用微信扫描，请修改配置。"
								});
							}
						}
					});
				}
			}

			me = null;
			sysconfigureData = null;
			companyData = null;
			roletype = null;
			userid = null;
			companyid = null;

			cfilter = null;
			hfilter = null;
			hasstore = null;
			hascustomer = null;
			crowdata = null;
		}

		userinfoData = null;
		permissiondata = null;

		if ($(document.getElementById("userauditcountupdate")) !== undefined)
			$(document.getElementById("userauditcountupdate")).click();

	};
	// 获取当前有户菜单按钮功能权限
	Model.prototype.getCurDataRule = function() {
		var userinfoData = this.comp("userinfoData");
		var dataruledata = this.comp("dataruledata");
		dataruledata.clear();
		// 获取当前有户菜单按钮功能权限
		justep.Baas.sendRequest({
			"url" : "/erpscan/permission",
			"action" : "getDataRuleData",
			"async" : false,
			"params" : {
				"userid" : userinfoData.getValue("userid"),
				"role" : userinfoData.getValue("role"),
				"roletype" : userinfoData.getValue("roletype")
			},
			"success" : function(param) {
				if (param !== null) {
					dataruledata.loadData(param);
				}
			}
		});

		userinfoData = null;
		dataruledata = null;
	};

	Model.prototype.getFuncIframe = function(tabId) {
		var pages = this.getPages();
		if (pages === null) {
			return null;
		}
		var content = pages.getContent(tabId);
		var $iframe = content && content.$domNode.find('iframe.portal-frame');

		pages = null;
		content = null;

		if ($iframe && $iframe.size() > 0)
			return $iframe.get(0);

	};

	// 当前登录如没有操作登录记录重新生成一条
	Model.prototype.addLoginLog = function() {
		var me = this;
		var loginlogData = this.comp("loginlogData");
		var userinfoData = this.comp("userinfoData");
		justep.Baas.sendRequest({
			"url" : "/erpscan/userrelevant",
			"action" : "recordLoginLog",
			"async" : false,
			"params" : {
				"userid" : userinfoData.getValue("userid"),
				"username" : userinfoData.getValue("username"),
				"companyid" : userinfoData.getValue("companyid"),
				"platform" : justep.Browser.deviceType
			},
			"success" : function(param) {
				if (param.logid !== "") {

					me.logid = param.logid;
					loginlogData.clear();
					loginlogData.setFilter("filter1", "id='" + me.logid + "'");
					loginlogData.refreshData();
					loginlogData.first();
				}
			}
		});

		loginlogData = null;
		userinfoData = null;
	};

	// 更新当前登录最后操作时间
	Model.prototype.updateLoginLog = function() {
		var loginlogData = this.comp("loginlogData");
		if (loginlogData.count() > 0) {
			loginlogData.setValue("operatetime", new Date());
			loginlogData.saveData();
		}
		loginlogData = null;
	};
	// 更新退出操作时间
	Model.prototype.updateLoginoutLog = function() {
		var loginlogData = this.comp("loginlogData");
		if (loginlogData.count() > 0) {
			loginlogData.setValue("operatetime", new Date());
			loginlogData.setValue("checktime", new Date());
			loginlogData.saveData();
		}
		loginlogData = null;
	};

	Model.prototype.modelModelConstruct = function(event) {
		this._doInit();
		this._doBind();
	};

	Model.prototype.modelUnLoad = function(event) {
		if (this._updateDateTimeHandle)
			window.clearInterval(this._updateDateTimeHandle);

		if (this._updateGGHandle)
			window.clearInterval(this._updateGGHandle);

		if (this._updateAuditHandle)
			window.clearInterval(this._updateAuditHandle);

		this._updateDateTimeHandle = null;
		this._updateGGHandle = null;
		this._updateAuditHandle = null;

	};

	Model.prototype.loginCheck = function(event) {
		var sysconfigureData = this.comp("sysconfigureData");
		if (this.comp("userinfoData").count() > 0 && this.logid !== "" && sysconfigureData.count() > 0 && sysconfigureData.getValue("checkLG") === 1) {

			var me = this;
			justep.Baas.sendRequest({
				"url" : "/erpscan/userrelevant",
				"action" : "checkLegalLogin",
				"async" : true,
				"params" : {
					"userid" : this.comp("userinfoData").getValue("userid"),
					"logid" : this.logid
				},
				"success" : function(msdata) {
					if (msdata.message !== "") {
						me.comp("messageReloginDialog").show({
							"message" : msdata.message
						});
					}
					sysconfigureData = null;
					me = null;
				}
			});
		}

	};
	Model.prototype.messageLonginDialogOK = function(event) {
		this.__logined = false;
		this.closeAllAgent();// 关闭所有代理
		// this.closeAllPage();
		this.closeMainPage();
		this._doLogout();
		this.manualLogin = true;// 注销时避免自动登录情况显示不了登录页面，转手动登录
		this.logid = "";
		localStorage.setItem('pdaautoLogin', false);
		this.showLoginDialog();
	};

	Model.prototype.bindUpdateGG = function() {
		this.advid = "";
		this.level = "1";
		var sysconfigureData = this.comp("sysconfigureData");
		var companyData = this.comp("companyData");

		if (this.comp("userinfoData").count() > 0 && companyData.count() > 0 && this.logid !== "" && sysconfigureData.count() > 0) {
			var firstshow = true;
			if (sysconfigureData.getValue("showadv") === "1" && companyData.getValue("abvlimitshow") > -1) {
				var diff = 0;
				if (companyData.getValue("abvlimitshow") === 1 && companyData.getValue("abvenddate") !== undefined) {
					diff = justep.Date.diff(justep.Date.fromString(justep.Date.toString(new Date(), justep.Date.STANDART_FORMAT_SHOT), justep.Date.STANDART_FORMAT_SHOT), justep.Date.fromString(
							justep.Date.toString(companyData.getValue("abvenddate"), justep.Date.STANDART_FORMAT_SHOT), justep.Date.STANDART_FORMAT_SHOT), 'd') + 1;
				}

				if (diff <= 0) {
					firstshow = false;
					var advtime = sysconfigureData.getValue("advtime");
					var interval = advtime * 60 * 1000;

					var fn = function() {
						var me = this;
						justep.Baas.sendRequest({
							"url" : "/erpscan/backdata",
							"action" : "getAdv",
							"async" : true,
							"params" : {
								"advid" : me.advid,
								"level" : me.level
							},
							"success" : function(msdata) {
								if (msdata.advid !== "") {
									me.comp("advwindowDialog").open({
										"data" : {
											"advid" : msdata.advid,
											"showpay" : true
										},
										"src" : require.toUrl("./../mlfbackend/permission/advshow.w"),
										"status" : justep.Browser.isPC ? "normal" : "maximize"
									});
									me.advid = msdata.advid;
									me.level = msdata.level;
									me = null;
								}
							}
						});

					};
					fn();
					this._updateGGHandle = window.setInterval(justep.Util.bindModelFn(this, fn, this), interval);
				}
			}

			if (firstshow === true && sysconfigureData.getValue("firstadv") === 1 && companyData.getValue("firstadv") === 1) {
				var me = this;
				justep.Baas.sendRequest({
					"url" : "/erpscan/backdata",
					"action" : "getAdv",
					"async" : true,
					"params" : {
						"advid" : me.advid,
						"level" : me.level
					},
					"success" : function(msdata) {
						if (msdata.advid !== "") {
							me.comp("advwindowDialog").open({
								"data" : {
									"advid" : msdata.advid,
									"showpay" : false
								},
								"src" : require.toUrl("./../mlfbackend/permission/advshow.w"),
								"status" : justep.Browser.isPC ? "normal" : "maximize"
							});
							me.advid = msdata.advid;
							me.level = msdata.level;
							me = null;
						}
					}
				});
			}
		}

		sysconfigureData = null;
		companyData = null;

	};

	Model.prototype.bindUpdateAudit = function() {
		var reportrefresh = this.comp("companyData").getValue("step_reportrefresh");
		var interval = (reportrefresh < 3 ? 3 * 20 * 1000 : reportrefresh * 20 * 1000);

		var bargedata = this.comp("bargedata");
		var userinfoData = this.comp("userinfoData");

		// console.log(interval);

		var fn = function() {
			$(document.getElementById("mainauditinfo")).hide();
			// console.log("bindUpdateAudit");
			justep.Baas.sendRequest({
				"url" : "/erpscan/auditflow/auditflow",
				"action" : "getUserFlowCount",
				"async" : true,
				"params" : {
					"companyid" : userinfoData.getValue("companyid"),
					"userid" : userinfoData.getValue("userid")
				},
				"success" : function(data) {
					bargedata.setValue("auditnum", data.count);
					if (data.count > 0) {
						$(document.getElementById("mainauditinfo")).show();
					}

				}
			});
		};
		fn();
		this._updateAuditHandle = window.setInterval(justep.Util.bindModelFn(this, fn, this), interval);
	};

	Model.prototype.qualityauditcountBtnClick = function(event) {
		var bargedata = this.comp("bargedata");
		var userinfoData = this.comp("userinfoData");
		$(document.getElementById("qualitylistinfo")).hide();
		if (this.comp("companyData").getValue("hasqualitymange") === 1) {
			justep.Baas.sendRequest({
				"url" : "/erpscan/quality/quality",
				"action" : "getSelectQualitysource",
				"async" : true,
				"params" : {
					"companyid" : userinfoData.getValue("companyid"),
					"getcount" : 1,
					"billtypes" : this.billtypes
				},
				"success" : function(data) {
					bargedata.setValue("qualitynum", data.count);
					if (data.count > 0) {
						$(document.getElementById("qualitylistinfo")).show();
					}
					bargedata = null;
					userinfoData = null;
				}
			});
		}
	};

	Model.prototype.showMainauditinfo = function() {
		this.showPage("auditmanage");
	};

	Model.prototype.boardrefrshbtnClick = function(event) {
		var svalue = JSON.parse(localStorage.getItem("boarddatareturn"));
		this.comp("boardDialog").open({
			"data" : {
				fullscreen : svalue.fullscreen
			},
			"src" : require.toUrl("$UI/erpscan/mlfcommon/board/" + svalue.url + ".w")
		});
	};

	Model.prototype.userauditcountBtnClick = function(event) {
		var bargedata = this.comp("bargedata");
		var userinfoData = this.comp("userinfoData");
		$(document.getElementById("mainauditinfo")).hide();
		if (userinfoData.count() > 0) {
			justep.Baas.sendRequest({
				"url" : "/erpscan/auditflow/auditflow",
				"action" : "getUserFlowCount",
				"async" : true,
				"params" : {
					"companyid" : userinfoData.getValue("companyid"),
					"userid" : userinfoData.getValue("userid")
				},
				"success" : function(data) {
					bargedata.setValue("auditnum", data.count);
					if (data.count > 0) {
						$(document.getElementById("mainauditinfo")).show();
					}
					bargedata = null;
					userinfoData = null;
				}
			});
		}
	};

	return Model;
});
