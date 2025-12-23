define(function(require) {

	return {
		"$children" : [ { // 2020-11-20 增加看板管理
			"$children" : [ {
				"$name" : "item",
				"$text" : "boardsalesorder",
				"display" : "solid",
				"label" : "订单进度看板",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfcommon/board/boardsalesorder.w"
			}, {
				"$name" : "item",
				"$text" : "steporderboard",
				"display" : "solid",
				"label" : "工艺工单看板",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfcommon/board/boardsteporder.w"
			}, {
				"$name" : "item",
				"$text" : "daysheetboard",
				"display" : "solid",
				"label" : "生产日报看板",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfcommon/board/boardreporttoday.w"
			}, {
				"$name" : "item",
				"$text" : "orderprogressboard",
				"display" : "solid",
				"label" : "工单进度看板",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfcommon/board/boardorder.w"
			}, /*
				 * { "$name" : "item", "$text" : "stepprogressboard", "display" :
				 * "solid", "label" : "工艺进度看板", "psmCount" : "1", "url" :
				 * "$UI/erpscan/mlfcommon/board/boardstep.w" },
				 */{
				"$name" : "item",
				"$text" : "stepboard",
				"display" : "solid",
				"label" : "工序进度看板",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfcommon/board/step.w"
			}, {
				"$name" : "item",
				"$text" : "boardworkshoptj",
				"display" : "solid",
				"label" : "车间产能看板",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfcommon/board/boardworkshoptj.w"
			}, {
				"$name" : "item",
				"$text" : "boardschedule",
				"display" : "solid",
				"label" : "设备排产看板",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfcommon/board/boardschedule.w"
			}, {
				"$name" : "item",
				"$text" : "boarddevice",
				"display" : "solid",
				"label" : "设备产能看板",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfcommon/board/boarddevice.w"
			}, { 
				"$name" : "item",
				"$text" : "boardBI",
				"display" : "solid",
				"label" : "BI看板",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfcommon/board/totalSalesOrderShowBoard.w"
			}, {
				"$name" : "item",
				"$text" : "boarddeviceproperty",
				"display" : "solid",
				"label" : "设备看板",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfcommon/board/boarddeviceproperty.w"
			} ],
			"$name" : "item",
			"$text" : "boardset",
			"display" : "solid",
			"iconClass" : "fa fa-desktop",
			"label" : "看板管理"
		}, {
			"$children" : [ {
				"$name" : "item",
				"$text" : "applymaterialdata",
				"display" : "solid",
				"label" : "补料申请管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/apply/applymaterialmanage.w"
			}, {
				"$name" : "item",
				"$text" : "applypaymentdata",
				"display" : "solid",
				"label" : "付款申请管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/apply/applypaymentmanage.w"
			}, {
				"$name" : "item",
				"$text" : "applyinvoicedata",
				"display" : "solid",
				"label" : "开票申请管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/apply/applyinvoicemanage.w"
			}, {
				"$name" : "item",
				"$text" : "applyleavedata",
				"display" : "solid",
				"label" : "请假申请管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/apply/applyleavemanage.w"
			}, {
				"$name" : "item",
				"$text" : "applyovertimedata",
				"display" : "solid",
				"label" : "加班申请管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/apply/applyovertimemanage.w"
			}, {
				"$name" : "item",
				"$text" : "applyovertimetotaldata",
				"display" : "solid",
				"label" : "加班汇总管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/apply/applyovertimetotalmanage.w"
			}, {
				"$name" : "item",
				"$text" : "applyiteminfodata",
				"display" : "solid",
				"label" : "商品信息申请管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/apply/applyiteminfomanage.w"
			}, {
				"$name" : "item",
				"$text" : "applysetdata",
				"display" : "solid",
				"label" : "申请模块配置",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/apply/applyconfig.w"
			}, {
				"$name" : "item",
				"$text" : "applytjdata",
				"display" : "solid",
				"label" : "请假加班统计查询",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/apply/applytjomanage.w"
			} ],
			"$name" : "item",
			"$text" : "applymodel",
			"display" : "solid",
			"iconClass" : "fa fa-file-o",
			"label" : "申请模块"
		}, {
			"$children" : [ {
				"$name" : "item",
				"$text" : "invoicestoreindata",
				"display" : "solid",
				"label" : "发票商品入库管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/invoice/invoicestoreinmanage.w"
			}, {
				"$name" : "item",
				"$text" : "invoicestoreinoutdata",
				"display" : "solid",
				"label" : "发票商品出库管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/invoice/invoicestoreinoutmanage.w"
			}, {
				"$name" : "item",
				"$text" : "invoicestockdata",
				"display" : "solid",
				"label" : "发票商品库存查询",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/invoice/invoicestockmanage.w"
			}, {
				"$name" : "item",
				"$text" : "invoiceinoutreportdata",
				"display" : "solid",
				"label" : "发票库存进出明细",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/invoice/invoiceinoutreport.w"
			}, {
				"$name" : "item",
				"$text" : "invoiceinoutallreportdata",
				"display" : "solid",
				"label" : "发票库存进出汇总",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/invoice/invoiceinoutallreport.w"
			}, {
				"$name" : "item",
				"$text" : "invoicepricedata",
				"display" : "solid",
				"label" : "发票商品历史价查询",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/invoice/invoicepricemanage.w"
			} ],
			"$name" : "item",
			"$text" : "invoiceinoutmodel",
			"display" : "solid",
			"iconClass" : "fa fa-exchange",
			"label" : "发票商品"
		}, {
			"$children" : [ {
				"$name" : "item",
				"$text" : "purchasedata",
				"display" : "solid",
				"label" : "采购申请管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/application/purchaseappmanage.w"
			}, {
				"$name" : "item",
				"$text" : "purchaseorderdata",
				"display" : "solid",
				"label" : "采购订单管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/order/purchaseordermanage.w"
			}, {
				"$name" : "item",
				"$text" : "storeindata",
				"display" : "solid",
				"label" : "采购入库管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/storeinmanage.w"
			}, {
				"$name" : "item",
				"$text" : "storeinoutdata",
				"display" : "solid",
				"label" : "采购退货管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/storeinoutmanage.w"
			}, {
				"$name" : "item",
				"$text" : "storeinpricedata",
				"display" : "solid",
				"label" : "采购历史价格",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/storein/storeinpricemanage.w"
			} ],
			"$name" : "item",
			"$text" : "storeinmodel",
			"display" : "solid",
			"iconClass" : "fa fa-sign-in",
			"label" : "采购模块"
		}, {
			"$children" : [ {
				"$name" : "item",
				"$text" : "quotationdata",
				"display" : "solid",
				"label" : "报价管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/order/quotationmanage.w"
			}, {
				"$name" : "item",
				"$text" : "salesorderdata",
				"display" : "solid",
				"label" : "销售订单管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/order/salesordermanage.w"
			}, {
				"$name" : "item",
				"$text" : "salesordercombinedata",
				"display" : "solid",
				"label" : "销售合并订单管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/order/salesordercombinemanage.w"
			}, {
				"$name" : "item",
				"$text" : "storeoutapplydata",
				"display" : "solid",
				"label" : "发货申请管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/storeoutapplymanage.w"
			}, {
				"$name" : "item",
				"$text" : "storeoutdata",
				"display" : "solid",
				"label" : "销售出库管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/storeoutmanage.w"
			}, {
				"$name" : "item",
				"$text" : "storeoutindata",
				"display" : "solid",
				"label" : "销售退货管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/storeoutinmanage.w"
			}, {
				"$name" : "item",
				"$text" : "deliverdata",
				"display" : "solid",
				"label" : "送货管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/deliver/delivermanage.w"
			}, {
				"$name" : "item",
				"$text" : "orderprofitdata",
				"display" : "solid",
				"label" : "销售订单利润评估",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/order/orderprofitquery.w"
			}, {
				"$name" : "item",
				"$text" : "aftersalesmaintaindata",
				"display" : "solid",
				"label" : "商品售后维护",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/order/afterSalesMaintainManage.w"
			} ],
			"$name" : "item",
			"$text" : "storeoutmodel",
			"display" : "solid",
			"iconClass" : "fa fa-sign-out",
			"label" : "销售模块"
		}, {
			"$children" : [ {
				"$name" : "item",
				"$text" : "contractmanage",
				"display" : "solid",
				"label" : "合同模板管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/contract/storecontractmanage.w"
			} ],
			"$name" : "item",
			"$text" : "contractmodel",
			"display" : "solid",
			"iconClass" : "fa fa-file-text-o",
			"label" : "合同模块"
		}, {
			"$children" : [ {
				"$name" : "item",
				"$text" : "mrpmanage",
				"display" : "solid",
				"label" : "物料需求计算",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfmanage/mrp/mrpmanage.w"
			}, {
				"$name" : "item",
				"$text" : "scheduleorderdata",
				"display" : "solid",
				"label" : "排产单管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfmanage/scheduling/scheduleordermanage.w"
			}, {
				"$name" : "item",
				"$text" : "orderdata",
				"display" : "solid",
				"label" : "工单管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfmanage/scheduling/worksheet.w"
			}, {
				"$name" : "item",
				"$text" : "prodtataldata",
				"display" : "solid",
				"label" : "工单需领料查询",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfmanage/prodtataldataquery.w"
			}, {
				"$name" : "item",
				"$text" : "stageoutsourcingdata",
				"display" : "solid",
				"label" : "工序外协发货管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfmanage/stageoutsourcing/stageoutsourcingmanage.w"
			}, {
				"$name" : "item",
				"$text" : "stageoutsourcingindata",
				"display" : "solid",
				"label" : "工序外协收货管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfmanage/stageoutsourcing/stageoutsourcinginmanage.w"
			}, {
				"$name" : "item",
				"$text" : "staffjobdata",
				"display" : "solid",
				"label" : "派工管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfmanage/staffjobdistribution.w"
			}, {
				"$name" : "item",
				"$text" : "scheduledata",
				"display" : "solid",
				"label" : "设备排产管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfmanage/deviceschedule.w"
			}, {
				"$name" : "item",
				"$text" : "nonpiecesetdata",
				"display" : "solid",
				"label" : "不计件时段配置",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfmanage/nonpieceset.w"
			}, {
				"$name" : "item",
				"$text" : "reportdatmanagedata",
				"display" : "solid",
				"label" : "生产日报表管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfmanage/reportdaymanage.w"
			}, {
				"$name" : "item",
				"$text" : "returncontentdata",
				"display" : "solid",
				"label" : "返工报备记录",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfmanage/r_returncontent.w"
			} ],
			"$name" : "item",
			"$text" : "orderset",
			"display" : "solid",
			"iconClass" : "fa fa-wrench",
			"label" : "生产管理"
		}, {
			"$children" : [ {
				"$name" : "item",
				"$text" : "orderstageoutdata",
				"display" : "solid",
				"label" : "工序外协商品查询",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfmanage/orderdetailstep.w"
			}, {
				"$name" : "item",
				"$text" : "summaryorderdata",
				"display" : "solid",
				"label" : "工单进度汇总",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfmanage/summaryorder.w"
			}, {
				"$name" : "item",
				"$text" : "orderstepdata",
				"display" : "solid",
				"label" : "工序进度报表",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfmanage/orderstepdata.w"
			}, {
				"$name" : "item",
				"$text" : "orderstepoverdata",
				"display" : "solid",
				"label" : "工序超期报表",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfmanage/orderstepoverdata.w"
			}, {
				"$name" : "item",
				"$text" : "stepquality",
				"display" : "solid",
				"label" : "工序质检报表",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfmanage/produceTotal/stepQuality.w"
			},{
				"$name" : "item",
				"$text" : "summaryprintitemdata",
				"display" : "solid",
				"label" : "工单细码进度表",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfmanage/summaryprintitem.w"
			}, {
				"$name" : "item",
				"$text" : "summarysteporderdata",
				"display" : "solid",
				"label" : "工艺工单汇总",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfmanage/summarysteporder.w"
			}, {
				"$name" : "item",
				"$text" : "summaryordersteptimedata",
				"display" : "solid",
				"label" : "工艺工单效率",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfmanage/summaryordersteptime.w"
			},
			/*
			 * , { "$name" : "item", "$text" : "stepqualitydata", "display" :
			 * "solid", "label" : "工序质检项报表", "psmCount" : "1", "url" :
			 * "$UI/erpscan/mlfmanage/stepqualitydata.w" }
			 */{
				"$name" : "item",
				"$text" : "staffwagesummary",
				"display" : "solid",
				"label" : "员工工资汇总",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfmanage/staffwage/staffwagesummary.w"
			}, {
				"$name" : "item",
				"$text" : "reportbuildsheet",
				"display" : "solid",
				"label" : "生产汇总分析",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfmanage/reportbuildSheet.w"
			}, {
				"$name" : "item",
				"$text" : "workshopstatistics",
				"display" : "solid",
				"label" : "车间产能统计",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfmanage/workshopstatistics.w"
			} ],
			"$name" : "item",
			"$text" : "orderreportset",
			"display" : "solid",
			"iconClass" : "fa fa-industry",
			"label" : "生产报表"
		}, {
			"$children" : [ {
				"$name" : "item",
				"$text" : "outsourcingdata",
				"display" : "solid",
				"label" : "委外加工管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/outsourcing/outsourcingmanage.w"
			}, {
				"$name" : "item",
				"$text" : "processoutdata",
				"display" : "solid",
				"label" : "加工出库管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/outsourcing/processoutmanage.w"
			}, {
				"$name" : "item",
				"$text" : "processindata",
				"display" : "solid",
				"label" : "加工退料管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/outsourcing/processinmanage.w"
			}, {
				"$name" : "item",
				"$text" : "outsourcingindata",
				"display" : "solid",
				"label" : "加工入库管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/outsourcing/outsourcinginmanage.w"
			}, {
				"$name" : "item",
				"$text" : "outsourcingoutdata",
				"display" : "solid",
				"label" : "加工退货管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/outsourcing/outsourcingoutmanage.w"
			}, {
				"$name" : "item",
				"$text" : "outsourcingprogress",
				"display" : "solid",
				"label" : "委外加工进度",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/outsourcing/outsourcingprogress.w"
			} ],
			"$name" : "item",
			"$text" : "outsourcingmodel",
			"display" : "solid",
			"iconClass" : "fa fa-gg",
			"label" : "委外加工"
		}, {
			"$children" : [ {
				"$name" : "item",
				"$text" : "qualitieddata",
				"display" : "solid",
				"label" : "质检单管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/quality/qualitiedmanage.w"
			}, {
				"$name" : "item",
				"$text" : "qualitytjdata",
				"display" : "solid",
				"label" : "质量问题统计",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/quality/qualitytjmanage.w"
			}, {
				"$name" : "item",
				"$text" : "qualityiteminfodata",
				"display" : "solid",
				"label" : "商品质检配置",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/quality/qualityiteminfomanage.w"
			}, {
				"$name" : "item",
				"$text" : "qualityitemdata",
				"display" : "solid",
				"label" : "质检项管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/quality/qualityitemmanage.w"
			}, {
				"$name" : "item",
				"$text" : "qualitytypedata",
				"display" : "solid",
				"label" : "质检问题管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/quality/qualitytypemanage.w"
			} ],
			"$name" : "item",
			"$text" : "qualitiedmodel",
			"display" : "solid",
			"iconClass" : "fa fa-gavel",
			"label" : "质量模块"
		}, {
			"$children" : [ {
				"$name" : "item",
				"$text" : "storehousedata",
				"display" : "solid",
				"label" : "库位物料管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/location/storehouse.w"
			}, {
				"$name" : "item",
				"$text" : "schedulepickdata",
				"display" : "solid",
				"label" : "排产领料管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/schedulePick/schedulePickManage.w"
			}, {
				"$name" : "item",
				"$text" : "prodrequisitiondata",
				"display" : "solid",
				"label" : "生产领用管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/other/prodrequisitionmanage.w"
			}, {
				"$name" : "item",
				"$text" : "prodrequisitionbackdata",
				"display" : "solid",
				"label" : "生产退料管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/other/prodrequisitionbackmanage.w"
			}, {
				"$name" : "item",
				"$text" : "prodstoragedata",
				"display" : "solid",
				"label" : "生产入库管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfmanage/scheduling/productinmanage.w"
			}, {
				"$name" : "item",
				"$text" : "otherindata",
				"display" : "solid",
				"label" : "其他入库管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/other/otherinmanage.w"
			}, {
				"$name" : "item",
				"$text" : "otheroutdata",
				"display" : "solid",
				"label" : "其他出库管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/other/otheroutmanage.w"
			}, {
				"$name" : "item",
				"$text" : "storemovedata",
				"display" : "solid",
				"label" : "调拨管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/storemovemanage.w"
			}, {
				"$name" : "item",
				"$text" : "storecheckdata",
				"display" : "solid",
				"label" : "盘点管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/storecheckmanage.w"
			}, {
				"$name" : "item",
				"$text" : "splitdata",
				"display" : "solid",
				"label" : "组装拆卸管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/splitsmanage.w"
			}, {
				"$name" : "item",
				"$text" : "reportlossdata",
				"display" : "solid",
				"label" : "报损记录管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/reportlossmanage.w"
			}

			],
			"$name" : "item",
			"$text" : "storemodel",
			"display" : "solid",
			"iconClass" : "fa fa-edit",
			"label" : "库存模块"
		}, {
			"$children" : [ {
				"$name" : "inout",
				"$text" : "inoutmanage",
				"display" : "solid",
				"label" : "收支管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/account/inoutmanage.w"
			}, {
				"$name" : "honour",
				"$text" : "honourmanage",
				"display" : "solid",
				"label" : "承兑管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/account/honourmanage.w"
			}, {
				"$name" : "runningaccount",
				"$text" : "runningaccount",
				"display" : "solid",
				"label" : "收支流水表",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/account/runningaccount.w"
			}, {
				"$name" : "recpaycustomer",
				"$text" : "recpaycustomer",
				"display" : "solid",
				"label" : "往来应收应付表",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/account/recpaycustomersearch.w"
			}, {
				"$name" : "recpaytotal",
				"$text" : "recpaytotal",
				"display" : "solid",
				"label" : "往来应收应付汇总",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/account/recpaytotal.w"
			}, {
				"$name" : "customerbilldata",
				"$text" : "customerbilldata",
				"display" : "solid",
				"label" : "往来单位调账",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/account/customerbillmanage.w"
			}, {
				"$name" : "statement",
				"$text" : "statement",
				"display" : "solid",
				"label" : "对账管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/account/Statement.w"
			}, {
				"$name" : "inouttype",
				"$text" : "inouttypemanage",
				"display" : "solid",
				"label" : "收支项目类型管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/account/inouttypemanage.w"
			}, {
				"$name" : "account",
				"$text" : "accountmanage",
				"display" : "solid",
				"label" : "结算账户管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/account/accountmanage.w"
			}, {
				"$name" : "finance",
				"$text" : "financemanage",
				"display" : "solid",
				"label" : "总账管理",
				"psmCount" : "1",
				"url" : "http://scxt.molisoft.cn:1699/x5/UI2/v_/fms/login2.w"
			}, {
				"$name" : "t6canjoin",
				"$text" : "t6canjoindata",
				"display" : "solid",
				"label" : "对接财务系统数据",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/finance/t6canjoinmanage.w"
			} ],
			"$name" : "cashier",
			"$text" : "cashiermodel",
			"display" : "solid",
			"iconClass" : "fa fa-yen",
			"label" : "财务模块"
		}, {
			"$children" : [ { // 20201107
				"$name" : "item",
				"$text" : "scanseeorderdata",
				"display" : "solid",
				"label" : "扫码查单",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/scanseeorder.w"
			}, {
				"$name" : "item",
				"$text" : "draftbilldata",
				"display" : "solid",
				"label" : "暂存中心",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/draftbillmanage.w"
			}, {
				"$name" : "item",
				"$text" : "billdata",
				"display" : "solid",
				"label" : "单据中心",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/billmanage.w"
			}, {
				"$name" : "item",
				"$text" : "detailbilldata",
				"display" : "solid",
				"label" : "商品往来查询",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/detailbillmanage.w"
			}, {
				"$name" : "item",
				"$text" : "stockdata",
				"display" : "solid",
				"label" : "库存查询",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/findreport/stockmanage.w"
			}, {
				"$name" : "item",
				"$text" : "stockstatedata",
				"display" : "solid",
				"label" : "库存状况",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/findreport/stockstatemanage.w"
			}, {
				"$name" : "item",
				"$text" : "houselimitreportdata",
				"display" : "solid",
				"label" : "库存报警",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/findreport/houselimitreport.w"
			}, {
				"$name" : "item",
				"$text" : "storeinreportdata",
				"display" : "solid",
				"label" : "采购报表",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/findreport/storeinreport.w"
			}, {
				"$name" : "item",
				"$text" : "storeoutreportdata",
				"display" : "solid",
				"label" : "销售报表",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/findreport/storeoutreport.w"
			}, {
				"$name" : "item",
				"$text" : "storeinoutreportdata",
				"display" : "solid",
				"label" : "库存进出明细",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/findreport/storeinoutreport.w"
			}, {
				"$name" : "item",
				"$text" : "storeinoutallreportdata",
				"display" : "solid",
				"label" : "库存进出汇总",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/findreport/storeinoutallreport.w"
			}, {
				"$name" : "item",
				"$text" : "businessanalysisdata",
				"display" : "solid",
				"label" : "营业分析",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/findreport/businessanalysis.w"
			} ],
			"$name" : "item",
			"$text" : "reportmodel",
			"display" : "solid",
			"iconClass" : "fa fa-sitemap",
			"label" : "报表模块"
		}, {
			"$children" : [ { // 20201107
				"$name" : "item",
				"$text" : "devicenewdata",
				"display" : "solid",
				"label" : "设备台账",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/device/devicenewmanage.w"
			},{ // 20201107
				"$name" : "item",
				"$text" : "devicemaintenancedata",
				"display" : "solid",
				"label" : "设备保养登记",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/device/devicemaintenancemanage.w"
			},{ // 20201107
				"$name" : "item",
				"$text" : "devicemaintenancemessagedata",
				"display" : "solid",
				"label" : "设备保养提醒",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/device/devicemaintenancemessage.w"
			},{ 
				"$name" : "item",
				"$text" : "maintainapplydata",
				"display" : "solid",
				"label" : "设备维修申请",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/device/maintainApplymanage.w"
			},{ 
				"$name" : "item",
				"$text" : "maintaindata",
				"display" : "solid",
				"label" : "设备维修登记",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/device/maintainmanage.w"
			},{ 
				"$name" : "item",
				"$text" : "maintaindata",
				"display" : "solid",
				"label" : "设备OpcUa管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/device/deviceOPCUA.w"
			},{ // 20201107
				"$name" : "item",
				"$text" : "deviceelectricitydata",
				"display" : "solid",
				"label" : "设备电耗统计",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/device/deviceelectricitymanage.w"
			}],
			"$name" : "item",
			"$text" : "devicemodel",
			"display" : "solid",
			"iconClass" : "fa fa-server",
			"label" : "设备模块"
		}, { 
			"$children" : [ {
				"$name" : "item",
				"$text" : "sealdata",
				"display" : "solid",
				"label" : "用章管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/seal/sealManage.w"
			},{
				"$name" : "item",
				"$text" : "projectdata",
				"display" : "solid",
				"label" : "生产项目",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/projectmanage.w"
			}, {
				"$name" : "item",
				"$text" : "iteminfodata",
				"display" : "solid",
				"label" : "商品管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/itemmanage.w"
			}, {
				"$name" : "item",
				"$text" : "codedata",
				"display" : "solid",
				"label" : "商品码管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/codemanage.w"
			}, {
				"$name" : "item",
				"$text" : "itempropertydata",
				"display" : "solid",
				"label" : "商品属性",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/basicset/itempropertymanage.w"
			}, {
				"$name" : "item",
				"$text" : "itemunitdata",
				"display" : "solid",
				"label" : "商品单位",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/unitmanage.w"
			}, {
				"$name" : "item",
				"$text" : "housedata",
				"display" : "solid",
				"label" : "仓库管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/housemanage.w"
			}, {
				"$name" : "item",
				"$text" : "customerdata",
				"display" : "solid",
				"label" : "往来单位管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/customermanage.w"
			}, {
				"$name" : "item",
				"$text" : "itembegindata",
				"display" : "solid",
				"label" : "期初库存管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/itembeginmanage.w"
			}, {
				"$name" : "item",
				"$text" : "houselimitdata",
				"display" : "solid",
				"label" : "库存报警设置",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/houselimitmanage.w"
			}, {
				"$name" : "item",
				"$text" : "staffdata",
				"display" : "solid",
				"label" : "员工管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/staffmanage.w"
			}, {
				"$name" : "item",
				"$text" : "new_stepmanage",
				"display" : "solid",
				"label" : "工序管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfmanage/new_stepmanage/new_stepmanage.w"
			}, {
				"$name" : "item",
				"$text" : "stepdata",
				"display" : "solid",
				"label" : "自定义工艺",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfmanage/stepmanage.w"
			}, {
				"$name" : "item",
				"$text" : "devicedata",
				"display" : "solid",
				"label" : "设备管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfmanage/devicemanage.w"
			}, {
				"$name" : "item",
				"$text" : "workshopdata",
				"display" : "solid",
				"label" : "车间管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfmanage/workshopList.w"
			} ],
			"$name" : "item",
			"$text" : "basicset",
			"display" : "solid",
			"iconClass" : "fa fa-cogs",
			"label" : "基础模块"
		}, {
			"$children" : [ {
				"$name" : "item",
				"$text" : "versionchangedata",
				"display" : "solid",
				"label" : "版本更新记录管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfbackend/versionchangemanage.w"
			}, {
				"$name" : "item",
				"$text" : "permissiondata",
				"display" : "solid",
				"label" : "菜单功能管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfbackend/permission/permissionmanage.w"
			}, {
				"$name" : "item",
				"$text" : "permissiondata",
				"display" : "solid",
				"label" : "数据规则管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfbackend/permission/datarulemanage.w"
			}, {
				"$name" : "item",
				"$text" : "rolepresetdata",
				"display" : "solid",
				"label" : "角色预设管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfbackend/permission/rolePresetManage.w"
			}, {
				"$name" : "item",
				"$text" : "qiniuyundata",
				"display" : "solid",
				"label" : "七牛云管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfbackend/systembase/qiniuyunmanage.w"
			}, {
				"$name" : "item",
				"$text" : "dxwdata",
				"display" : "solid",
				"label" : "短信网管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfbackend/systembase/dxwmanage.w"
			}, {
				"$name" : "item",
				"$text" : "redisdata",
				"display" : "solid",
				"label" : "Redis配置",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfbackend/systembase/redismanage.w"
			}, {
				"$name" : "item",
				"$text" : "wxdata",
				"display" : "solid",
				"label" : "微信公众号管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfbackend/systembase/wxconfig.w"
			}, {
				"$name" : "item",
				"$text" : "advdata",
				"display" : "solid",
				"label" : "广告管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfbackend/permission/advmanage.w"
			}, {
				"$name" : "item",
				"$text" : "sysconfigureData",
				"display" : "solid",
				"label" : "系统参数配置",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfbackend/systembase/systemset.w"
			}, {
				"$name" : "item",
				"$text" : "wxpaysetdata",
				"display" : "solid",
				"label" : "微信支付参数配置",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfbackend/systembase/wxpayset.w"
			}, {
				"$name" : "item",
				"$text" : "alipaysetdata",
				"display" : "solid",
				"label" : "支付宝参数配置",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfbackend/systembase/alipayset.w"
			}, {
				"$name" : "item",
				"$text" : "feeparamdata",
				"display" : "solid",
				"label" : "收费设置",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfbackend/systembase/feeparamList.w"
			}, {
				"$name" : "item",
				"$text" : "systemconfigureData",
				"display" : "solid",
				"label" : "系统设置",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfcommon/systembase/systemconfig.w"
			}, {
				"$name" : "item",
				"$text" : "companysetdata",
				"display" : "solid",
				"label" : "参数设置",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/companyset.w"
			}, {
				"$name" : "item",
				"$text" : "auditflowdata",
				"display" : "solid",
				"label" : "审批流程配置",
				"psmCount" : "1",
				"url" : "$UI/erpscan/pdamanage/auditflow/auditflowmanage.w"
			}, {
				"$name" : "item",
				"$text" : "userdata",
				"display" : "solid",
				"label" : "用户管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfcommon/permission/usermanage.w"
			}, {
				"$name" : "item",
				"$text" : "scompanyadmindata",
				"display" : "solid",
				"label" : "公司管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfbackend/permission/companyadminmanage.w"
			}, {
				"$name" : "companystatemanage",
				"$text" : "companystatedata",
				"display" : "solid",
				"label" : "公司业务统计",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfbackend/permission/companystatemanage.w"
			}, {
				"$name" : "item",
				"$text" : "dlscompanyadmindata",
				"display" : "solid",
				"label" : "业务的公司帐套管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfbackend/permission/dlcompanyadminmanage.w"
			}, {
				"$name" : "item",
				"$text" : "dlpayrecorddata",
				"display" : "solid",
				"label" : "业务的支付记录",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfbackend/dlpayrecordmanage.w"
			}, {
				"$name" : "item",
				"$text" : "scompanydata",
				"display" : "solid",
				"label" : "组织机构管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfcommon/permission/companymanage.w"
			}, {
				"$name" : "item",
				"$text" : "roledata",
				"display" : "solid",
				"label" : "角色权限管理",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfcommon/permission/rolemanage.w"
			}, {
				"$name" : "item",
				"$text" : "qiniuyunsetdata",
				"display" : "solid",
				"label" : "七牛云配置",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfcommon/systembase/qiniuyunset.w"
			}, {
				"$name" : "item",
				"$text" : "dxwsetdata",
				"display" : "solid",
				"label" : "短信网配置",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfcommon/systembase/dxwset.w"
			}, {
				"$name" : "item",
				"$text" : "wxsetdata",
				"display" : "solid",
				"label" : "微信公众号配置",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfcommon/systembase/wxset.w"
			}, {
				"$name" : "item",
				"$text" : "payrecorddata",
				"display" : "solid",
				"label" : "支付记录",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfcommon/payrecordmanage.w"
			}, {
				"$name" : "item",
				"$text" : "agentpayrecorddata",
				"display" : "solid",
				"label" : "支付流水",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfcommon/agentpayrecordmanage.w"
			}, {
				"$name" : "item",
				"$text" : "loginlogdata",
				"display" : "solid",
				"label" : "登录日志",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfcommon/permission/loginlogmanage.w"
			}, {
				"$name" : "item",
				"$text" : "datachangelogdata",
				"display" : "solid",
				"label" : "操作日志",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfcommon/datalogmanage.w"
			}, {
				"$name" : "item",
				"$text" : "systeminfodata",
				"display" : "solid",
				"label" : "注册授权",
				"psmCount" : "1",
				"url" : "$UI/erpscan/mlfbackend/license.w"
			} ],
			"$name" : "item",
			"$text" : "systemset",
			"display" : "solid",
			"iconClass" : "fa fa-user-plus",
			"label" : "系统管理"
		} ],
		"$name" : "root",
		"$text" : ""
	};
});