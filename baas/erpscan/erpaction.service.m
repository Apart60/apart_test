<?xml version="1.0" encoding="UTF-8"?>
<model xmlns="http://www.justep.com/model">
	<action xmlns="http://www.w3.org/1999/xhtml" name="queryItemsplits_view"
		impl="action:common/CRUD/query">
		<private name="condition" type="String"></private>
		<private name="db" type="String">erpscan</private>
		<private name="tableName" type="String">itemsplits_view</private>
		<public name="columns" type="Object"></public>
		<public name="filter" type="String"></public>
		<public name="limit" type="Integer"></public>
		<public name="offset" type="Integer"></public>
		<public name="orderBy" type="String"></public>
		<public name="variables" type="Object"></public>
	</action>
	
	<action xmlns="http://www.w3.org/1999/xhtml" name="queryItemsplits_view2"
		impl="action:common/CRUD/sqlQuery">
		<private name="countSql" type="String">select count(*) from
			 itemsplits ims,iteminfo im  where ims.combitemid=:combitemid and ims.itemid=im.itemid 
			and ::filter
		</private>
		<private name="db" type="String">erpscan</private>
		<private name="sql" type="String">SELECT im.inprice,im.barcode,im.unit,im.codeid,im.imgurl,im.itemname,im.sformat,im.classid,ifnull(ic.classname,'') as classname,im.status,im.property1,im.property2,im.property3,im.property4,im.property5,im.class_id,im.unitstate1,im.unitset1,im.unitstate2,im.unitset2,im.unitstate3,im.unitset3,ims.* from itemsplits ims,iteminfo im left join itemclass ic on im.classid=ic.classid where ims.combitemid=:combitemid and ims.itemid=im.itemid 
			and ::filter ::orderBy
		</private>
		<private name="tableName" type="String">itemsplits_view</private>
		<public name="columns" type="Object"></public>
		<public name="filter" type="String"></public>
		<public name="limit" type="Integer"></public>
		<public name="offset" type="Integer"></public>
		<public name="orderBy" type="String"></public>
		<public name="variables" type="Object"></public>
	</action>
	
	<action xmlns="http://www.w3.org/1999/xhtml" name="queryPurchaseorder_view"
		impl="action:common/CRUD/query">
		<private name="condition" type="String"></private>
		<private name="db" type="String">erpscan</private>
		<private name="tableName" type="String">purchaseorder_view</private>
		<public name="columns" type="Object"></public>
		<public name="filter" type="String"></public>
		<public name="limit" type="Integer"></public>
		<public name="offset" type="Integer"></public>
		<public name="orderBy" type="String"></public>
		<public name="variables" type="Object"></public>
	</action>
	<action xmlns="http://www.w3.org/1999/xhtml" name="queryPurchaseorderdetail_item_view"
		impl="action:common/CRUD/query">
		<private name="condition" type="String"></private>
		<private name="db" type="String">erpscan</private>
		<private name="tableName" type="String">purchaseorderdetail_item_view
		</private>
		<public name="columns" type="Object"></public>
		<public name="filter" type="String"></public>
		<public name="limit" type="Integer"></public>
		<public name="offset" type="Integer"></public>
		<public name="orderBy" type="String"></public>
		<public name="variables" type="Object"></public>
	</action>
	<action xmlns="http://www.w3.org/1999/xhtml" name="queryPurchasedetail_item_view"
		impl="action:common/CRUD/query">
		<private name="condition" type="String"></private>
		<private name="db" type="String">erpscan</private>
		<private name="tableName" type="String">purchasedetail_item_view
		</private>
		<public name="columns" type="Object"></public>
		<public name="filter" type="String"></public>
		<public name="limit" type="Integer"></public>
		<public name="offset" type="Integer"></public>
		<public name="orderBy" type="String"></public>
		<public name="variables" type="Object"></public>
	</action>
	<action xmlns="http://www.w3.org/1999/xhtml" name="queryPurchaseorder"
		impl="action:common/CRUD/query">
		<private name="condition" type="String"></private>
		<private name="db" type="String">erpscan</private>
		<private name="tableName" type="String">purchaseorder</private>
		<public name="columns" type="Object"></public>
		<public name="filter" type="String"></public>
		<public name="limit" type="Integer"></public>
		<public name="offset" type="Integer"></public>
		<public name="orderBy" type="String"></public>
		<public name="variables" type="Object"></public>
	</action>
	<action xmlns="http://www.w3.org/1999/xhtml" name="queryPurchase"
		impl="action:common/CRUD/query">
		<private name="condition" type="String"></private>
		<private name="db" type="String">erpscan</private>
		<private name="tableName" type="String">purchase</private>
		<public name="columns" type="Object"></public>
		<public name="filter" type="String"></public>
		<public name="limit" type="Integer"></public>
		<public name="offset" type="Integer"></public>
		<public name="orderBy" type="String"></public>
		<public name="variables" type="Object"></public>
	</action>
	<action xmlns="http://www.w3.org/1999/xhtml" name="queryPurchaseorderdetail_all_view"
		impl="action:common/CRUD/query">
		<private name="condition" type="String"></private>
		<private name="db" type="String">erpscan</private>
		<private name="tableName" type="String">purchaseorderdetail_all_view
		</private>
		<public name="columns" type="Object"></public>
		<public name="filter" type="String"></public>
		<public name="limit" type="Integer"></public>
		<public name="offset" type="Integer"></public>
		<public name="orderBy" type="String"></public>
		<public name="variables" type="Object"></public>
	</action>
	<action xmlns="http://www.w3.org/1999/xhtml" name="querySalesorder_view"
		impl="action:common/CRUD/query">
		<private name="condition" type="String"></private>
		<private name="db" type="String">erpscan</private>
		<private name="tableName" type="String">salesorder_view</private>
		<public name="columns" type="Object"></public>
		<public name="filter" type="String"></public>
		<public name="limit" type="Integer"></public>
		<public name="offset" type="Integer"></public>
		<public name="orderBy" type="String"></public>
		<public name="variables" type="Object"></public>
	</action>
	<action xmlns="http://www.w3.org/1999/xhtml" name="querySalesorderdetail_all_view"
		impl="action:common/CRUD/query">
		<private name="condition" type="String"></private>
		<private name="db" type="String">erpscan</private>
		<private name="tableName" type="String">salesorderdetail_all_view
		</private>
		<public name="columns" type="Object"></public>
		<public name="filter" type="String"></public>
		<public name="limit" type="Integer"></public>
		<public name="offset" type="Integer"></public>
		<public name="orderBy" type="String"></public>
		<public name="variables" type="Object"></public>
	</action>
	<action xmlns="http://www.w3.org/1999/xhtml" name="querySalesorderdetail_item_view"
		impl="action:common/CRUD/query">
		<private name="condition" type="String"></private>
		<private name="db" type="String">erpscan</private>
		<private name="tableName" type="String">salesorderdetail_item_view
		</private>
		<public name="columns" type="Object"></public>
		<public name="filter" type="String"></public>
		<public name="limit" type="Integer"></public>
		<public name="offset" type="Integer"></public>
		<public name="orderBy" type="String"></public>
		<public name="variables" type="Object"></public>
	</action>
	<action xmlns="http://www.w3.org/1999/xhtml" name="querySalesorder"
		impl="action:common/CRUD/query">
		<private name="condition" type="String"></private>
		<private name="db" type="String">erpscan</private>
		<private name="tableName" type="String">salesorder</private>
		<public name="columns" type="Object"></public>
		<public name="filter" type="String"></public>
		<public name="limit" type="Integer"></public>
		<public name="offset" type="Integer"></public>
		<public name="orderBy" type="String"></public>
		<public name="variables" type="Object"></public>
	</action>
	<action xmlns="http://www.w3.org/1999/xhtml" name="queryT_order_all_view"
		impl="action:common/CRUD/query">
		<private name="condition" type="String"></private>
		<private name="db" type="String">erpscan</private>
		<private name="tableName" type="String">t_order_all_view</private>
		<public name="columns" type="Object"></public>
		<public name="filter" type="String"></public>
		<public name="limit" type="Integer"></public>
		<public name="offset" type="Integer"></public>
		<public name="orderBy" type="String"></public>
		<public name="variables" type="Object"></public>
	</action>

	<action xmlns="http://www.w3.org/1999/xhtml" name="queryT_order_all_view2"
		impl="action:common/CRUD/sqlQuery">
		<private name="countSql" type="String">select count(*) from
			t_order sd
			left join salesorderdetail sl on
			sd.salesorderdetailid=sl.detailid
			left join customer c on
			sd.customer_id=c.customerid left join
			staffinfo s on
			sd.operate_by=s.staffid ,iteminfo im left join
			itemclass ic on
			im.classid=ic.classid where sd.companyid=:companyid
			and
			sd.itemid=im.itemid
			and ::filter
		</private>
		<private name="db" type="String">erpscan</private>
		<private name="sql" type="String">select
			sd.*,im.codeid,im.itemname,im.sformat,im.mcode,im.classid,im.unit,im.imgurl,im.barcode,im.property1,im.property2,im.property3,im.property4,im.property5,im.unitstate1,im.unitset1,im.unitstate2,im.unitset2,im.unitstate3,im.unitset3,ifnull(ic.classname,'')
			as
			classname,c.customercode,c.customername,s.staffcode,s.staffname,sl.scheduledcount,sl.schedulcount-sl.outsourcingcount as schedulcount,sl.schedulcount-sl.outsourcingcount as oldschedulcount,sl.count
			from t_order sd left join salesorderdetail sl on
			sd.salesorderdetailid=sl.detailid left join customer c on
			sd.customer_id=c.customerid left join staffinfo s on
			sd.operate_by=s.staffid ,iteminfo im left join itemclass ic on
			im.classid=ic.classid where sd.companyid=:companyid and
			sd.itemid=im.itemid
			and
			::filter ::orderBy
		</private>
		<private name="tableName" type="String">t_order_all_view
		</private>
		<public name="columns" type="Object"></public>
		<public name="filter" type="String"></public>
		<public name="limit" type="Integer"></public>
		<public name="offset" type="Integer"></public>
		<public name="orderBy" type="String"></public>
		<public name="variables" type="Object"></public>
	</action>

	<action xmlns="http://www.w3.org/1999/xhtml" name="queryProdrequisition"
		impl="action:common/CRUD/query">
		<private name="condition" type="String"></private>
		<private name="db" type="String">erpscan</private>
		<private name="tableName" type="String">prodrequisition</private>
		<public name="columns" type="Object"></public>
		<public name="filter" type="String"></public>
		<public name="limit" type="Integer"></public>
		<public name="offset" type="Integer"></public>
		<public name="orderBy" type="String"></public>
		<public name="variables" type="Object"></public>
	</action>
<action xmlns="http://www.w3.org/1999/xhtml" name="queryProdrequisition_view"
		impl="action:common/CRUD/sqlQuery">
		<private name="countSql" type="String">select count(*)
			from
			prodrequisition pr left join
			storehouse sh on pr.houseid=sh.houseid left join customer c on
			pr.customerid=c.customerid left join staffinfo s on
			pr.operate_by=s.staffid left join iteminfo o on
			pr.worksheetitemid=o.itemid
			where ::filter
		</private>
		<private name="db" type="String">erpscan</private>
		<private name="sql" type="String">select pr.prodrequisitionid,pr.bill_type,pr.companyid,pr.orderid,
			pr.operate_time,pr.operate_by,pr.houseid,pr.customerid,pr.count,pr.total,
			pr.status,pr.remark,pr.printing,pr.outexcel,pr.create_id,pr.create_by,
			pr.create_time,pr.update_id,pr.update_by,pr.update_time,pr.originalbill,
			pr.iproperty,pr.reworksheet,pr.worksheetid,pr.worksheetbillno,pr.worksheetitemid,
			pr.worksheetbatchno,sh.housecode,sh.housename,c.customercode,c.customername,
			s.staffcode,s.staffname,pr.worksheetbillno as billno,pr.worksheetitemid as itemid,
			o.itemname,o.sformat,o.codeid,pr.schedule_pick_id,pr.schedule_pick_billno
			from prodrequisition pr 
			left join storehouse sh on pr.houseid=sh.houseid 
			left join customer c on pr.customerid=c.customerid 
			left join staffinfo s on pr.operate_by=s.staffid 
			left join iteminfo o on pr.worksheetitemid=o.itemid
			where ::filter ::orderBy
		</private>
		<private name="tableName" type="String">prodrequisition</private>
		<public name="columns" type="Object"></public>
		<public name="filter" type="String"></public>
		<public name="limit" type="Integer"></public>
		<public name="offset" type="Integer"></public>
		<public name="orderBy" type="String"></public>
		<public name="variables" type="Object"></public>
	</action>

	<action xmlns="http://www.w3.org/1999/xhtml" name="queryProdrequisition_view2"
		impl="action:common/CRUD/sqlQuery">
		<private name="countSql" type="String">select count(*)
			from
			prodrequisition pr left join
			storehouse sh on pr.houseid=sh.houseid left join customer c on
			pr.customerid=c.customerid left join staffinfo s on
			pr.operate_by=s.staffid left join iteminfo o on
			pr.worksheetitemid=o.itemid where
			pr.prodrequisitionid=:prodrequisitionid and
			::filter
		</private>
		<private name="db" type="String">erpscan</private>
		<private name="sql" type="String">select pr.*,
			sh.housecode,sh.housename
			,c.customercode,c.customername,s.staffcode,s.staffname,pr.worksheetbillno
			as billno,pr.worksheetitemid as
			itemid,o.itemname,o.sformat,o.codeid,o.unit,if(pr.worksheetid!='',ifnull((select
			t.order_count from t_order t where t.id=pr.worksheetid),0),0) as
			order_count from prodrequisition pr left join
			storehouse sh on pr.houseid=sh.houseid left join customer c on
			pr.customerid=c.customerid left join staffinfo s on
			pr.operate_by=s.staffid left join iteminfo o on
			pr.worksheetitemid=o.itemid where
			pr.prodrequisitionid=:prodrequisitionid and
			::filter ::orderBy
		</private>
		<private name="tableName" type="String"></private>
		<public name="columns" type="Object"></public>
		<public name="filter" type="String"></public>
		<public name="limit" type="Integer"></public>
		<public name="offset" type="Integer"></public>
		<public name="orderBy" type="String"></public>
		<public name="variables" type="Object"></public>
	</action>

	<action xmlns="http://www.w3.org/1999/xhtml" name="queryProdrequisitiondetail_item_view"
		impl="action:common/CRUD/query">
		<private name="condition" type="String"></private>
		<private name="db" type="String">erpscan</private>
		<private name="tableName" type="String">prodrequisitiondetail_item_view
		</private>
		<public name="columns" type="Object"></public>
		<public name="filter" type="String"></public>
		<public name="limit" type="Integer"></public>
		<public name="offset" type="Integer"></public>
		<public name="orderBy" type="String"></public>
		<public name="variables" type="Object"></public>
	</action>
	<action xmlns="http://www.w3.org/1999/xhtml" name="queryProdrequisitiondetail_all_view"
		impl="action:common/CRUD/query">
		<private name="condition" type="String"></private>
		<private name="db" type="String">erpscan</private>
		<private name="tableName" type="String">prodrequisitiondetail_all_view
		</private>
		<public name="columns" type="Object"></public>
		<public name="filter" type="String"></public>
		<public name="limit" type="Integer"></public>
		<public name="offset" type="Integer"></public>
		<public name="orderBy" type="String"></public>
		<public name="variables" type="Object"></public>
	</action>
	<action xmlns="http://www.w3.org/1999/xhtml" name="queryOtherinout_view"
		impl="action:common/CRUD/query">
		<private name="condition" type="String"></private>
		<private name="db" type="String">erpscan</private>
		<private name="tableName" type="String">otherinout_view</private>
		<public name="columns" type="Object"></public>
		<public name="filter" type="String"></public>
		<public name="limit" type="Integer"></public>
		<public name="offset" type="Integer"></public>
		<public name="orderBy" type="String"></public>
		<public name="variables" type="Object"></public>
	</action>
	<action xmlns="http://www.w3.org/1999/xhtml" name="queryOtherinoutdetail_all_view"
		impl="action:common/CRUD/query">
		<private name="condition" type="String"></private>
		<private name="db" type="String">erpscan</private>
		<private name="tableName" type="String">otherinoutdetail_all_view
		</private>
		<public name="columns" type="Object"></public>
		<public name="filter" type="String"></public>
		<public name="limit" type="Integer"></public>
		<public name="offset" type="Integer"></public>
		<public name="orderBy" type="String"></public>
		<public name="variables" type="Object"></public>
	</action>
	<action xmlns="http://www.w3.org/1999/xhtml" name="queryOtherinoutdetail_item_view"
		impl="action:common/CRUD/query">
		<private name="condition" type="String"></private>
		<private name="db" type="String">erpscan</private>
		<private name="tableName" type="String">otherinoutdetail_item_view
		</private>
		<public name="columns" type="Object"></public>
		<public name="filter" type="String"></public>
		<public name="limit" type="Integer"></public>
		<public name="offset" type="Integer"></public>
		<public name="orderBy" type="String"></public>
		<public name="variables" type="Object"></public>
	</action>
	<action xmlns="http://www.w3.org/1999/xhtml" name="queryOtherinout"
		impl="action:common/CRUD/query">
		<private name="condition" type="String"></private>
		<private name="db" type="String">erpscan</private>
		<private name="tableName" type="String">otherinout</private>
		<public name="columns" type="Object"></public>
		<public name="filter" type="String"></public>
		<public name="limit" type="Integer"></public>
		<public name="offset" type="Integer"></public>
		<public name="orderBy" type="String"></public>
		<public name="variables" type="Object"></public>
	</action>
	<action xmlns="http://www.w3.org/1999/xhtml" name="queryprodrequisition_total"
		impl="action:common/CRUD/sqlQuery">
		<private name="countSql" type="String">select count(*)
			from
			prodrequisition_work_total pwt left join iteminfo im on
			pwt.itemid=im.itemid
			where pwt.worksheetid=:worksheetid and ::filter
		</private>
		<private name="db" type="String">erpscan</private>
		<private name="sql" type="String">select im.itemid,
			im.codeid,im.itemname,im.sformat,im.mcode,im.classid,im.unit,im.imgurl,im.barcode,im.property1,im.property2,im.property3,im.property4,im.property5,im.unitstate1,im.unitset1,im.unitstate2,im.unitset2,im.unitstate3,im.unitset3,ifnull(ic.classname,'')
			as classname,pwt.goods_number,pwt.totalid,pwt.companyid as
			pcompanyid,pwt.worksheetid,pwt.worksheetitemid, pwt.unitcount,pwt.worksheetbillno,pwt.worksheetbatchno,pwt.assistformula,
			pwt.count as pcount, pwt.needcount,pwt.total, ifnull((select sum(sd.count) from prodrequisitiondetail sd where sd.worksheetid=pwt.worksheetid and sd.itemid=pwt.itemid and sd.status='3'),0) as fcheckcount  
			from
			prodrequisition_work_total pwt
			left join iteminfo im on
			pwt.itemid=im.itemid left join itemclass ic
			on
			im.classid=ic.classid
			where pwt.worksheetid=:worksheetid and
			::filter ::orderBy
		</private>
		<private name="tableName" type="String">prodrequisition_work_total
		</private>
		<public name="columns" type="Object"></public>
		<public name="filter" type="String"></public>
		<public name="limit" type="Integer"></public>
		<public name="offset" type="Integer"></public>
		<public name="orderBy" type="String"></public>
		<public name="variables" type="Object"></public>
	</action>
	
	<action xmlns="http://www.w3.org/1999/xhtml" name="queryprodrequisition_total2"
		impl="action:common/CRUD/sqlQuery">
		<private name="countSql" type="String">select count(*)
			from
			prodrequisition_work_total pwt left join iteminfo im on
			pwt.itemid=im.itemid
			where pwt.companyid=:companyid and ::filter
		</private>
		<private name="db" type="String">erpscan</private>
		<private name="sql" type="String">select im.itemid,
			im.codeid,im.itemname,im.sformat,im.mcode,im.classid,im.unit,im.imgurl,im.barcode,im.property1,im.property2,im.property3,im.property4,im.property5,im.unitstate1,im.unitset1,im.unitstate2,im.unitset2,im.unitstate3,im.unitset3,ifnull(ic.classname,'')
			as classname,pwt.totalid,pwt.companyid as
			pcompanyid,pwt.worksheetid,pwt.worksheetitemid, pwt.unitcount,pwt.worksheetbillno,pwt.worksheetbatchno,pwt.assistformula,
			pwt.count as pcount, pwt.needcount,pwt.total, ifnull((select sum(sd.count) from prodrequisitiondetail sd where sd.worksheetid=pwt.worksheetid and sd.itemid=pwt.itemid and sd.status='3'),0) as fcheckcount  
			from
			prodrequisition_work_total pwt
			left join iteminfo im on
			pwt.itemid=im.itemid left join itemclass ic
			on
			im.classid=ic.classid
			where pwt.companyid=:companyid and
			::filter ::orderBy
		</private>
		<private name="tableName" type="String">prodrequisition_work_total
		</private>
		<public name="columns" type="Object"></public>
		<public name="filter" type="String"></public>
		<public name="limit" type="Integer"></public>
		<public name="offset" type="Integer"></public>
		<public name="orderBy" type="String"></public>
		<public name="variables" type="Object"></public>
	</action>
	<action xmlns="http://www.w3.org/1999/xhtml" name="queryPurchase_view"
		impl="action:common/CRUD/query">
		<private name="condition" type="String"></private>
		<private name="db" type="String">erpscan</private>
		<private name="tableName" type="String">purchase_view</private>
		<public name="columns" type="Object"></public>
		<public name="filter" type="String"></public>
		<public name="limit" type="Integer"></public>
		<public name="offset" type="Integer"></public>
		<public name="orderBy" type="String"></public>
		<public name="variables" type="Object"></public>
	</action>
	<action xmlns="http://www.w3.org/1999/xhtml" name="queryOutsourcing"
		impl="action:common/CRUD/query">
		<private name="condition" type="String"></private>
		<private name="db" type="String">erpscan</private>
		<private name="tableName" type="String">outsourcing</private>
		<public name="columns" type="Object"></public>
		<public name="filter" type="String"></public>
		<public name="limit" type="Integer"></public>
		<public name="offset" type="Integer"></public>
		<public name="orderBy" type="String"></public>
		<public name="variables" type="Object"></public>
	</action>
	<action xmlns="http://www.w3.org/1999/xhtml" name="queryOutsourcingdetail_item_view"
		impl="action:common/CRUD/query">
		<private name="condition" type="String"></private>
		<private name="db" type="String">erpscan</private>
		<private name="tableName" type="String">outsourcingdetail_item_view
		</private>
		<public name="columns" type="Object"></public>
		<public name="filter" type="String"></public>
		<public name="limit" type="Integer"></public>
		<public name="offset" type="Integer"></public>
		<public name="orderBy" type="String"></public>
		<public name="variables" type="Object"></public>
	</action>
	<action xmlns="http://www.w3.org/1999/xhtml" name="queryProdrequisition_work_total"
		impl="action:common/CRUD/query">
		<private name="condition" type="String"></private>
		<private name="db" type="String">erpscan</private>
		<private name="tableName" type="String">prodrequisition_work_total
		</private>
		<public name="columns" type="Object"></public>
		<public name="filter" type="String"></public>
		<public name="limit" type="Integer"></public>
		<public name="offset" type="Integer"></public>
		<public name="orderBy" type="String"></public>
		<public name="variables" type="Object"></public>
	</action>
	<action xmlns="http://www.w3.org/1999/xhtml" name="queryItemsplits"
		impl="action:common/CRUD/query">
		<private name="condition" type="String"></private>
		<private name="db" type="String">erpscan</private>
		<private name="tableName" type="String">itemsplits</private>
		<public name="columns" type="Object"></public>
		<public name="filter" type="String"></public>
		<public name="limit" type="Integer"></public>
		<public name="offset" type="Integer"></public>
		<public name="orderBy" type="String"></public>
		<public name="variables" type="Object"></public>
	</action>
	
		<action xmlns="http://www.w3.org/1999/xhtml" name="queryProdrequisitiondetail"
		impl="action:common/CRUD/query">
		<private name="condition" type="String"></private>
		<private name="db" type="String">erpscan</private>
		<private name="tableName" type="String">prodrequisitiondetail
		</private>
		<public name="columns" type="Object"></public>
		<public name="filter" type="String"></public>
		<public name="limit" type="Integer"></public>
		<public name="offset" type="Integer"></public>
		<public name="orderBy" type="String"></public>
		<public name="variables" type="Object"></public>
	</action>
</model>